package es.lavanda.filebot.service.impl;

import es.lavanda.filebot.amqp.ProducerService;
import es.lavanda.filebot.model.FilebotExecution;
import es.lavanda.filebot.model.FilebotExecution.FileExecutor;
import es.lavanda.filebot.model.FilebotExecution.FilebotJobStatus;
import es.lavanda.filebot.model.FilebotExecution.FilebotStatus;
import es.lavanda.filebot.repository.FilebotExecutionRepository;
import es.lavanda.filebot.service.FilebotService;
import es.lavanda.filebot.util.FilebotUtils;
import es.lavanda.lib.common.model.FilebotExecutionIDTO;
import es.lavanda.lib.common.model.FilebotExecutionTestIDTO;
import es.lavanda.lib.common.model.filebot.FilebotAction;
import io.kubernetes.client.PodLogs;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.BatchV1Api;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.*;
import io.kubernetes.client.util.Config;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor

public class FilebotServiceImpl implements FilebotService {

    private final ProducerService producerService;

    //private final FilebotAMCExecutor filebotAMCExecutor; //FIXME: REMOVE THIS WHEN CHANGE TO KUBERNETES JOB EXECUTION

    private final FilebotUtils filebotUtils;

    private final FilebotExecutionRepository filebotExecutionRepository;

    @Value("${filebot.namespace}")
    private String KUBERNETES_NAMESPACE;

    @Value("${filebot.volumes.config.claimname}")
    private String CONFIG_CLAIM_NAME;

    @Value("${filebot.volumes.data.path}")
    private String HOST_PATH_DATA_VOLUME;

    @Value("${filebot.user}")
    private String USER;


    private static final Pattern PATTERN_PROCESSED_FILE = Pattern.compile("Processed \\d+ file");

    private static final Pattern PATTERN_FILES_NOT_FOUND = Pattern.compile("Exit status: 100");

    private static final Pattern PATTERN_NOT_FILE_SELECTED = Pattern.compile("No files selected for processing");

    private static final Pattern PATTERN_MOVED_CONTENT = Pattern.compile("\\[(.*)\\] from \\[(.*)\\] to \\[(.*)\\]");

    private static final Pattern PATTERN_SKIPPED_CONTENT = Pattern
            .compile("\\[(.*)\\] Skipped \\[(.*)\\] because \\[(.*)\\] already exists");

    private static final Pattern PATTERN_SELECT_CONTENT = Pattern.compile("Group:.*=> \\[(.*)\\]");
    private static final Pattern PATTERN_FILE_EXIST = Pattern
            .compile("Skipped \\[(.*)\\] because \\[(.*)\\] already exists");


    @Override
    public FilebotExecution executeByKubernetes(FilebotExecution filebotExecution) {
        log.info("On Execution ByKubernetes ");
        try {
            return executeKubernetesJob(filebotExecution);
        } catch (Exception e) {
            log.error("Error executing filebot", e);
            throw new RuntimeException("Error executing filebot", e);
        }
    }

    private FilebotExecution executeKubernetesJob(FilebotExecution filebotExecution) {
        try {
            filebotExecution.setStatus(FilebotStatus.ON_FILEBOT_EXECUTION);
            filebotExecution.setJobStatus(FilebotJobStatus.STARTED);
            filebotExecution.setLog("");
            filebotExecutionRepository.save(filebotExecution);
            ApiClient client = Config.defaultClient();
            Configuration.setDefaultApiClient(client);
            CoreV1Api coreApi = new CoreV1Api(client);
            BatchV1Api api = new BatchV1Api();
            String lowercase = filebotExecution.getName().toLowerCase();
            String sanitized = lowercase.replaceAll("[^a-z0-9]+", "-").replaceAll("^-|-$", "");
            String baseName = sanitized.length() > 44 ? sanitized.substring(0, 44) : sanitized;
            String nameForContainerAndJob = baseName + "-" + RandomStringUtils.randomAlphanumeric(6).toLowerCase();
            api.createNamespacedJob(KUBERNETES_NAMESPACE, createFilebotJob(nameForContainerAndJob, nameForContainerAndJob, filebotExecution.getCommand())).execute();
            boolean isJobActive = true;
            while (isJobActive) {
                V1JobStatus jobStatus = api.readNamespacedJobStatus(nameForContainerAndJob, KUBERNETES_NAMESPACE).execute().getStatus();
                if (Objects.nonNull(jobStatus) && ((Objects.nonNull(jobStatus.getSucceeded()) && jobStatus.getSucceeded() > 0) || (Objects.nonNull(jobStatus.getFailed()) && jobStatus.getFailed() > 0))) {
                    log.info("Job finished");
                    filebotExecution.setLog(getLogOfContainer(coreApi, KUBERNETES_NAMESPACE, nameForContainerAndJob));
                    filebotExecution.setJobStatus(getJobStatusFromLog(filebotExecution.getLog(), jobStatus.getFailed() == null ? 0 : 1));
                    filebotExecutionRepository.save(filebotExecution);
                    if (FilebotJobStatus.PROCESSED.equals(filebotExecution.getJobStatus()) || FilebotJobStatus.TEST_PHASE.equals(filebotExecution.getJobStatus())) {
                        log.info("Completed FilebotExecution");
                        completedFilebotExecution(filebotExecution);
                    } else {
                        log.info("Handling Exception");
                        handleException(filebotExecution);
                    }
                    isJobActive = false;
                }
                Thread.sleep(1000);
            }
            return filebotExecution;
        } catch (ApiException | IOException | InterruptedException e) {
            log.error("Exception: ", e);
            throw new RuntimeException(e);
        }
    }

    private FilebotJobStatus getJobStatusFromLog(String log, Integer jobStatusFailed) {
        if (isNotLicensed(log)) {
            return FilebotJobStatus.ERROR_NO_LICENSE;
        } else if (isNonStrictOrQuery(log)) {
            return FilebotJobStatus.ERROR_NEED_STRICT_OR_QUERY;
        } else if (isNoFilesSelectedOrSkipped(log)) {
            return FilebotJobStatus.ERROR_FILES_NOT_FOUND;
        } else if (isFileExists(log)) {
            return FilebotJobStatus.ERROR_FILES_EXISTED_IN_DESTINATION;
        } else if (isTestMovedContent(log)) {
            return FilebotJobStatus.TEST_PHASE;
        } else if (jobStatusFailed > 0) {
            return FilebotJobStatus.ERROR_GENERIC;
        } else {
            return FilebotJobStatus.PROCESSED;
        }
    }

    private boolean isTestMovedContent(String log) {
        Matcher matcherMovedContent = PATTERN_MOVED_CONTENT.matcher(log);
        return matcherMovedContent.find();
    }

    private boolean isNotLicensed(String logExecution) {
        log.debug("Checking if is licensed");
        return logExecution.contains("License Error: UNREGISTERED");
    }

    private boolean isNoFilesSelectedOrSkipped(String logExecution) {
        Matcher matcherNoFilesSelected = PATTERN_NOT_FILE_SELECTED.matcher(logExecution);
        Matcher matcherFilesNotFound = PATTERN_FILES_NOT_FOUND.matcher(logExecution);
        Matcher matcherMovedContent = PATTERN_MOVED_CONTENT.matcher(logExecution);
        Matcher matcherSkippedContent = PATTERN_SKIPPED_CONTENT.matcher(logExecution);
        return (matcherNoFilesSelected.find() || matcherFilesNotFound.find())
                && Boolean.FALSE.equals(matcherMovedContent.find())
                && Boolean.FALSE.equals(matcherSkippedContent.find());
    }

    private boolean isNonStrictOrQuery(String logExecution) {
        return logExecution.contains("Consider using -non-strict to enable opportunistic matching")
                || logExecution.contains("No episode data found:")
                || (notContainsProcessedFile(logExecution)
                && logExecution.contains("Finished without processing any files"));
    }

    private boolean notContainsProcessedFile(String logExecution) {
        Matcher matcherMovedContent = PATTERN_PROCESSED_FILE.matcher(logExecution);
        return !matcherMovedContent.find();
    }

    private boolean isFileExists(String logExecution) {
        Matcher matcherMovedContent = PATTERN_SKIPPED_CONTENT.matcher(logExecution);
        return matcherMovedContent.find();
    }

    private String getLogOfContainer(CoreV1Api coreApi, String namespace, String containerName) throws ApiException, IOException {
        V1PodList podList = coreApi.listNamespacedPod(namespace).execute();
        if (podList.getItems().isEmpty()) {
            throw new RuntimeException("No pod was found for the job.");
        }
        V1Pod podSelected = null;
        for (V1Pod pod : podList.getItems()) {
            if (pod.getSpec().getContainers().get(0).getName().equals(containerName))
                podSelected = pod;
        }
        String podLog = "";
        PodLogs podsLogs = new PodLogs();
        try (InputStream is = podsLogs.streamNamespacedPodLog(podSelected)) {
            podLog = IOUtils.toString(is, StandardCharsets.UTF_8);
        }
        log.info("Finished on execution ByKubernetes: {}", podLog);
        return podLog;
    }


    private V1Job createFilebotJob(String jobName, String containerName, String command) {
        return new V1Job()
                .apiVersion("batch/v1")
                .kind("Job")
                .metadata(new V1ObjectMeta().name(jobName))
                .spec(new V1JobSpec()
                        .backoffLimit(0)
                        .ttlSecondsAfterFinished(100)
                        .template(new V1PodTemplateSpec()
                                .spec(new V1PodSpec()
                                        .containers(Collections.singletonList(new V1Container()
                                                .name(containerName)
                                                .image(filebotUtils.getImageTag())
                                                .command(Arrays.asList("/bin/sh", "-c"))
                                                .args(Collections.singletonList(command))
                                                .env(getEnvVars())
                                                .volumeMounts(getVolumeMounts())
                                                .securityContext(new V1SecurityContext().privileged(true).runAsUser(Long.valueOf(USER)))))
                                        .volumes(getVolumes())
                                        .restartPolicy("Never"))));
    }

    private List<V1EnvVar> getEnvVars() {
        V1EnvVar puid = new V1EnvVar().name("PUID").value(USER);
        V1EnvVar pgid = new V1EnvVar().name("PGID").value(USER);
        return Arrays.asList(puid, pgid);
    }

    private List<V1VolumeMount> getVolumeMounts() {
        V1VolumeMount filebotConfigVolumeMount = new V1VolumeMount()
                .name("filebot-config")
                .mountPath(filebotUtils.getFilebotPathData());
        V1VolumeMount mediaVolumeMount = new V1VolumeMount()
                .name("data")
                .mountPath(filebotUtils.getFilebotPathInput())
                .mountPropagation("Bidirectional");
        return Arrays.asList(mediaVolumeMount, filebotConfigVolumeMount);
    }

    private List<V1Volume> getVolumes() {
        V1Volume filebotConfigVolume = new V1Volume()
                .name("filebot-config")
                .persistentVolumeClaim(new V1PersistentVolumeClaimVolumeSource()
                        .claimName(CONFIG_CLAIM_NAME));
        V1Volume dataVolume = new V1Volume()
                .name("data")
                .hostPath(new V1HostPathVolumeSource()
                        .path(HOST_PATH_DATA_VOLUME)
                        .type("Directory"));
        return Arrays.asList(dataVolume, filebotConfigVolume);
    }

    private void handleException(FilebotExecution filebotExecution) {
        switch (filebotExecution.getJobStatus()) {
            case ERROR_NEED_STRICT_OR_QUERY:
                log.info("Handling STRICT_QUERY");
                strictOrQuery(filebotExecution);
                break;
            case ERROR_NO_LICENSE:
                log.error("Error No License - Full Log: {}", filebotExecution.getLog());
                filebotExecution.setStatus(FilebotStatus.ERROR);
                filebotExecutionRepository.save(filebotExecution);
                //tryLicensed();// Send notification;
                //FIXME: SENDMESSAGE TO TELEGRAM TO INFORM
                break;
            case ERROR_FILES_EXISTED_IN_DESTINATION:
                log.error("Exception file exists");
                fileExist(filebotExecution);
                //FIXME: SENDMESSAGE TO TELEGRAM TO INFORM
                break;
            case ERROR_FILES_NOT_FOUND:
                log.error("Files not found");
                filebotExecution.setStatus(FilebotStatus.FILES_NOT_FOUND);
                filebotExecutionRepository.save(filebotExecution);
                //FIXME: SENDMESSAGE TO TELEGRAM TO INFORM
                break;
            case ERROR_GENERIC:
                log.error("Error generic - Full Log: {}", filebotExecution.getLog());
                filebotExecution.setStatus(FilebotStatus.ERROR);
                //FIXME: SENDMESSAGE TO TELEGRAM TO INFORM
                filebotExecutionRepository.save(filebotExecution);
                break;
            default:
                break;
        }
    }

    private void fileExist(FilebotExecution filebotExecution) {
        Matcher matcherMovedContent = PATTERN_FILE_EXIST.matcher(filebotExecution.getLog());
        List<FileExecutor> filesExecutor = new ArrayList<>();
        while (matcherMovedContent.find()) {
            String fromContent = matcherMovedContent.group(1);
            String toContent = matcherMovedContent.group(2);
            log.info("From Content {}", fromContent);
            log.info("To Content {}", toContent);
            FileExecutor fileExecutor = new FileExecutor();
            fileExecutor.setFile(fromContent);
            fileExecutor.setNewFile(toContent);
            filesExecutor.add(fileExecutor);
        }
        // filebotExecution.setNewParentPath(getFolderPathOfFiles(newFilesname));
        filebotExecution.setFiles(filesExecutor);
        filebotExecution.setStatus(FilebotStatus.FILES_EXISTED_IN_DESTINATION);
        filebotExecutionRepository.save(filebotExecution);
    }
//FIXME: On a future
//    private void tryLicensed(FilebotExecution filebotExecution) {
//        log.info("Try to register license");
//        try {
//            // notificationService.sendNotification(SnsTopic.FILEBOT_LICENSE_ERROR,
//            // "Filebot License Error",
//            // "Filebot License Error");
//            filebotAMCExecutor.execute(filebotUtils.getRegisterCommand());
//            log.info("Licensed");
//        } catch (FilebotAMCException e) {
//            log.info("Not Licensed");
//            throw e;
//        }
//    }

    private void strictOrQuery(FilebotExecution filebotExecution) {
        Matcher matcherGroupContent = PATTERN_SELECT_CONTENT.matcher(filebotExecution.getLog());
        List<String> groupContentList = new ArrayList<>();
        while (matcherGroupContent.find()) {
            String groupContent = matcherGroupContent.group(1);
            String[] groupContentSplit = groupContent.split(",");
            groupContentList.addAll(Arrays.asList(groupContentSplit));
        }
        List<FileExecutor> filesExecutor = new ArrayList<>();
        for (String content : groupContentList) {
            FileExecutor fileExecutor = new FileExecutor();
            fileExecutor.setFile(content);
            filesExecutor.add(fileExecutor);
        }
        filebotExecution.setFiles(filesExecutor);
        FilebotExecutionIDTO filebotExecutionIDTO = new FilebotExecutionIDTO();
        filebotExecutionIDTO.setId(filebotExecution.getId());
        filebotExecutionIDTO.setFiles(filesExecutor.stream().map(FileExecutor::getFile).collect(Collectors.toList()));
        filebotExecutionIDTO.setPath(filebotExecution.getPath());
        filebotExecutionIDTO.setName(filebotExecution.getName());
        producerService.sendFilebotExecutionToTelegram(filebotExecutionIDTO);
        filebotExecution.setStatus(FilebotStatus.ON_TELEGRAM);
        filebotExecution.setLog(filebotExecution.getLog());
        filebotExecutionRepository.save(filebotExecution);
    }

    private void completedFilebotExecution(FilebotExecution filebotExecution) {
        log.info("CompletedFilebotExecution {}", filebotExecution);
        if (filebotExecution.isOnTestPhase()) {
            completedExecutionTest(filebotExecution);
        } else {
            completedExecution(filebotExecution);
        }
    }

    private void completedExecutionTest(FilebotExecution filebotExecution) {
        Matcher matcherMovedContent = PATTERN_MOVED_CONTENT.matcher(filebotExecution.getLog());
        List<String> oldFilesName = new ArrayList<>();
        List<String> newFilesname = new ArrayList<>();
        while (matcherMovedContent.find()) {
            String fromContent = matcherMovedContent.group(2);
            String toContent = matcherMovedContent.group(3);
            log.info("From Content {}", fromContent);
            log.info("To Content {}", toContent);
            oldFilesName.add(Path.of(fromContent).getFileName().toString());
            newFilesname.add(Path.of(toContent).getFileName().toString());
        }
        FilebotExecutionTestIDTO filebotExecutionTestIDTO = new FilebotExecutionTestIDTO();
        filebotExecutionTestIDTO.setId(filebotExecution.getId());
        filebotExecutionTestIDTO.setFiles(oldFilesName.stream().map(fe -> fe).collect(Collectors.toList()));
        filebotExecutionTestIDTO.setPossibilities(newFilesname.stream().map(fe -> fe).collect(Collectors.toList()));
        filebotExecutionTestIDTO.setPath(filebotExecution.getPath());
        filebotExecutionTestIDTO.setName(filebotExecution.getName());
        filebotExecution.setLog(filebotExecution.getLog());
        filebotExecution.setStatus(FilebotStatus.ON_TELEGRAM_TEST);
        filebotExecutionRepository.save(filebotExecution);
        producerService.sendFilebotToConfirmTest(filebotExecutionTestIDTO);
    }

    private void completedExecution(FilebotExecution filebotExecution) {
        Matcher matcherMovedContent = PATTERN_MOVED_CONTENT.matcher(filebotExecution.getLog());
        List<String> oldFilesName = new ArrayList<>();
        List<String> newFilesname = new ArrayList<>();
        String newParentFolderPath = null;
        while (matcherMovedContent.find()) {
            String fromContent = matcherMovedContent.group(2);
            String toContent = matcherMovedContent.group(3);
            log.info("From Content {}", fromContent);
            log.info("To Content {}", toContent);
            oldFilesName.add(Path.of(fromContent).getFileName().toString());
            newFilesname.add(Path.of(toContent).getFileName().toString());
            newParentFolderPath = Path.of(toContent).getParent().getFileName().toString();
        }
        List<FileExecutor> filesExecutor = new ArrayList<>();
        for (int i = 0; i < oldFilesName.size(); i++) {
            FileExecutor fileExecutor = new FileExecutor();
            fileExecutor.setFile(oldFilesName.get(i));
            fileExecutor.setNewFile(newFilesname.get(i));
            filesExecutor.add(fileExecutor);
        }
        filebotExecution.setFiles(filesExecutor);
        filebotExecution.setNewPath(newParentFolderPath);
        filebotExecution.setStatus(FilebotStatus.PROCESSED);
        if (FilebotAction.MOVE.equals(filebotExecution.getAction())) {
            // TODO: SEND MESSAGE TO TELEGRAM TO CONFIRM DELETE WITH LS TO SEE THE FILES TO
            // DELETE
            // fileService.rmdir(filebotExecution.getPath().toString());
        }
        filebotExecutionRepository.save(filebotExecution);
    }

}

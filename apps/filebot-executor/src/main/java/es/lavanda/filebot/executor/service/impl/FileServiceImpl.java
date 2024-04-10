package es.lavanda.filebot.executor.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

import es.lavanda.filebot.executor.model.FilebotExecution;
import io.kubernetes.client.PodLogs;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.BatchV1Api;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.*;
import io.kubernetes.client.util.Config;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import es.lavanda.filebot.executor.exception.FilebotExecutorException;
import es.lavanda.filebot.executor.model.FilebotExecution.FileExecutor;
import es.lavanda.filebot.executor.service.FileService;
import es.lavanda.filebot.executor.util.FilebotUtils;
import es.lavanda.filebot.executor.util.StreamGobbler;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {


    private final FilebotUtils filebotUtils;

    @Value("${lavanda.namespace}")
    private String KUBERNETES_NAMESPACE;

    @Value("${lavanda.volumes.data.path}")
    private String HOST_PATH_DATA_VOLUME;

    @Value("${lavanda.user}")
    private String USER;

    @Override
    public List<String> ls(String path) {
        String returnedLog = executeJob("ls " + "\"" + path + "\"");
        return Arrays.asList(returnedLog.split("\\r?\\n"));
    }

    private String executeJob(String command) {
        try {
            ApiClient client = Config.defaultClient();
            Configuration.setDefaultApiClient(client);
            CoreV1Api coreApi = new CoreV1Api(client);
            BatchV1Api api = new BatchV1Api();
            String lowercase = command.toLowerCase();
            String sanitized = lowercase.replaceAll("[^a-z0-9]+", "-");
            String jobName = sanitized.replaceAll("^-|-$", "").substring(0, 50) + "-" + RandomStringUtils.randomAlphanumeric(10).toLowerCase();
            String containerName = jobName.substring(0, 44) + "-" + RandomStringUtils.randomAlphanumeric(6).toLowerCase();
            api.createNamespacedJob(KUBERNETES_NAMESPACE, createJob(jobName, containerName, command)).execute();
            boolean isJobActive = true;
            while (isJobActive) {
                V1JobStatus jobStatus = api.readNamespacedJobStatus(jobName, KUBERNETES_NAMESPACE).execute().getStatus();
                if (Objects.nonNull(jobStatus) && ((Objects.nonNull(jobStatus.getSucceeded()) && jobStatus.getSucceeded() > 0) || (Objects.nonNull(jobStatus.getFailed()) && jobStatus.getFailed() > 0))) {
                    log.info("Job finished");
                    return getLogOfContainer(coreApi, KUBERNETES_NAMESPACE, containerName);
                }
                Thread.sleep(1000);
            }
        } catch (ApiException | IOException | InterruptedException e) {
            log.error("Exception: ", e);
            throw new RuntimeException(e);
        }
        log.error("Returning empty");
        return "";
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

//    @Override
//    public void rmdir(String path) {
//        int status;
//        List<String> lsResult = new ArrayList<>();
//        try {
//            Process process = new ProcessBuilder("bash", "-c", "rmdir -r\"" + path + "\"").redirectErrorStream(true)
//                    .start();
//            StreamGobbler streamGobbler = new StreamGobbler(process.getInputStream(), line -> {
//                log.debug("BASH commandline: {}", line);
//                lsResult.add(line);
//            });
//            executorService.submit(streamGobbler);
//            status = process.waitFor();
//            if (status != 0) {
//                log.error("RMDIR command result on fail {}", lsResult.stream()
//                        .map(n -> String.valueOf(n))
//                        .collect(Collectors.joining("\n", "IN-", "-OUT")));
//            } else {
//                log.debug("RMDIR command result on success {}", lsResult.stream()
//                        .map(n -> String.valueOf(n))
//                        .collect(Collectors.joining("\n", "IN-", "-OUT")));
//            }
//        } catch (InterruptedException | IOException e) {
//            log.error("Exception on command 'RMDIR'", e);
//            Thread.currentThread().interrupt();
//            throw new FilebotExecutorException("Exception on command 'RMDIR'", e);
//        }
//    }

    private boolean isValidExtension(String extension) {
        System.out.println("La extensión del archivo es: " + extension);
        if (extension.equalsIgnoreCase("mkv") || extension.equalsIgnoreCase("mp4")
                || extension.equalsIgnoreCase("avi")) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isValidForFilebot(String fileOrDirectory) {
        File file = new File(fileOrDirectory);
        if (!file.exists()) {
            throw new FilebotExecutorException(String.format("File not found %s", fileOrDirectory));
        }
        if (file.isFile()) {
            log.info("Is a file {}", fileOrDirectory);
            return isValidExtension(getFileExtension(file));
            // boolean valid = isValidExtension(getFileExtension(file));
            // if (valid) {
            //     return valid;
            // } else {
            //     return isRarFile(getFileExtension(file));
            // }
        } else if (file.isDirectory()) {
            log.info("Is a directory {}", fileOrDirectory);
            return true;
        } else {
            return false;
        }
    }

    public static String getFileExtension(File file) {
        String fileName = file.getName();
        if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        } else {
            return "";
        }
    }

    @Override
    public List<FileExecutor> getFilesExecutor(String path) {
        List<FileExecutor> files = new ArrayList<>();
        List<String> ls = ls(path);
        for (String file : ls) {
            if (isValidForFilebot(path + "/" + file)) {
                FileExecutor fileExecutor = new FileExecutor();
                fileExecutor.setFile(file);
                fileExecutor.setNewFile(null);
                files.add(fileExecutor);
            }
        }
        return files;
    }

    @Override
    public boolean isDirectory(String fileOrDirectory) {
        File file = new File(fileOrDirectory);
        return file.isDirectory();
    }

    private V1Job createJob(String jobName, String containerName, String command) {
        return new V1Job()
                .apiVersion("batch/v1")
                .kind("Job")
                .metadata(new V1ObjectMeta().name(jobName))
                .spec(new V1JobSpec()
                        .backoffLimit(0)
                        .template(new V1PodTemplateSpec()
                                .spec(new V1PodSpec()
                                        .containers(Collections.singletonList(new V1Container()
                                                .name(containerName)
                                                .image("busybox")
                                                .command(Arrays.asList("/bin/sh", "-c"))
                                                .args(Collections.singletonList(command))
                                                .env(getEnvVars())
                                                .volumeMounts(getVolumeMounts())
                                                .securityContext(new V1SecurityContext().privileged(true).runAsUser(Long.valueOf(USER)))))
                                        .volumes(getVolumes())
                                        .restartPolicy("Never"))));
    }

    private List<V1VolumeMount> getVolumeMounts() {
        V1VolumeMount mediaVolumeMount = new V1VolumeMount()
                .name("data")
                .mountPath("/media")
                .mountPropagation("Bidirectional");
        return Collections.singletonList(mediaVolumeMount);
    }

    private List<V1EnvVar> getEnvVars() {
        V1EnvVar puid = new V1EnvVar().name("PUID").value(USER);
        V1EnvVar pgid = new V1EnvVar().name("PGID").value(USER);
        return Arrays.asList(puid, pgid);
    }

    private List<V1Volume> getVolumes() {
        V1Volume dataVolume = new V1Volume()
                .name("data")
                .hostPath(new V1HostPathVolumeSource()
                        .path(HOST_PATH_DATA_VOLUME)
                        .type("Directory"));
        return Collections.singletonList(dataVolume);
    }
}

package es.lavanda.lib.common.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.condition.ProducesRequestCondition;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.lavanda.lib.common.dto.ffmpeg.FfprobeDTO;
import es.lavanda.lib.common.dto.rclone.RcloneSync;
import es.lavanda.lib.common.exception.HandlerException;
import es.lavanda.lib.common.model.media.MediaInfoDTO;
import es.lavanda.lib.common.service.CommandService;
import es.lavanda.lib.common.service.ProducerServiceLibrary;
import es.lavanda.lib.common.util.StreamGobbler;
import es.lavanda.lib.common.util.ffmpeg.FfmpegStreamLine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommandServiceImpl implements CommandService {

    private final ProducerServiceLibrary producerService;

    private final Pattern durationVideoPattern = Pattern.compile("(?<=Duration: )[^,]*");
    private final Pattern streamPattern = Pattern
            .compile("Stream #0:(\\d+)[(](\\w+?)[)]: (\\w+): (\\w+) [(*](\\w+?)[)*] ");
    private final Pattern resolutionPattern = Pattern.compile("\\), (\\d+x\\d+) \\[");
    private final Pattern bitratePattern = Pattern.compile("(?<=bitrate: )[^\n]*");
    private final Pattern progressRcloneSyncPattern = Pattern.compile("(\\d{1,3})%,");

    private final String QUEUE_SYNC_RCLONE = "sync-rclone-progress";

    @Override
    public FfprobeDTO getMediaInfo(String path) throws HandlerException {
        // MediaInfoDTO mediaInfo = new MediaInfoDTO();
        // mediaInfo.setFilesize((getSizeMB(new File(path)) + " MB").replace(",", "."));
        // mediaInfo.setContainer(getContainerName(path));
        String command = String.join(" ", "ffprobe -v quiet -print_format json -show_format -show_streams", path);
        FfprobeDTO ffprobeOutut = new FfprobeDTO();
        StringBuilder output = new StringBuilder();
        try {
            ProcessBuilder builder = new ProcessBuilder();
            builder.command("bash", "-c", command);
            builder.redirectErrorStream(true);
            Process process = builder.start();

            StreamGobbler streamGobbler = new StreamGobbler(process.getInputStream(), line -> {
                output.append(line);

            });
            Executors.newSingleThreadExecutor().submit(streamGobbler);
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new HandlerException("Media info not retrivied corrected for this file " + path);
            }
            // mediaInfo.setRawMetadata(output.toString());
        } catch (IOException | InterruptedException e) {
            log.error("IOException or interrupt getMediaInfo ", e);
            Thread.currentThread().interrupt();
            throw new HandlerException("IOException or interruptException getMediaInfo ", e);
        }
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            ffprobeOutut = objectMapper.readValue(output.toString(), FfprobeDTO.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return ffprobeOutut;
    }

    @Override
    public void syncFile(String inputPath, String outputPath, int id) throws HandlerException {
        RcloneSync rcloneSync = new RcloneSync();
        rcloneSync.setInputPath(inputPath);
        rcloneSync.setId(id);
        rcloneSync.setOutputPath(outputPath);
        rcloneSync.setCommand(createRcloneSyncCommand(inputPath, outputPath));
        log.info("Rclone copyTo command: {} ", rcloneSync.getCommand());
        try {
            ProcessBuilder builder = new ProcessBuilder();
            builder.command("bash", "-c", rcloneSync.getCommand());
            builder.redirectErrorStream(true);
            Process process = builder.start();
            StreamGobbler streamGobbler = new StreamGobbler(process.getInputStream(), line -> {
                log.info("Rclone copyTo output: {} ", line);
                Matcher progressMatcher = progressRcloneSyncPattern.matcher(line);
                if (progressMatcher.find()) {
                    log.info("Rclone progressMatcher finded", line);
                    rcloneSync.setProcessed(Integer.parseInt(progressMatcher.group(1)));
                    log.info("Rclone progressMatcher {}", rcloneSync.getProcessed());
                    producerService.sendMessageToQueue(QUEUE_SYNC_RCLONE, rcloneSync);
                    log.info("Rclone producerService sended");
                }
            });
            Executors.newSingleThreadExecutor().submit(streamGobbler);
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                log.error("Rclone sync error {} ", inputPath);
                throw new HandlerException("Rclone sync error " + inputPath);
            } else {
                rcloneSync.setSuccess(true);
                rcloneSync.setProcessed(100);
                producerService.sendMessageToQueue(QUEUE_SYNC_RCLONE, rcloneSync);
            }
        } catch (IOException | InterruptedException e) {
            log.error("IOException or interrupt getMediaInfo ", e);
            Thread.currentThread().interrupt();
            rcloneSync.setError(true);
            producerService.sendMessageToQueue(QUEUE_SYNC_RCLONE, rcloneSync);
            throw new HandlerException("IOException or interruptException getMediaInfo ", e);
        }
    }

    private String getContainerName(String path) {
        return FilenameUtils.getExtension(path).toUpperCase();
    }

    private String getBitrate(String line) {
        Matcher matcher = bitratePattern.matcher(line);
        if (matcher.find()) {
            return matcher.group(0);
        }
        return "";
    }

    /**
     * The duration of the video
     * 
     * @param group
     * @return
     */
    private double getDuration(String group) {
        String[] hms = group.split(":");
        return Integer.parseInt(hms[0]) * 3600 + Integer.parseInt(hms[1]) * 60 + Double.parseDouble(hms[2]);
    }

    private String getSizeMB(File f) {
        double fileSizeInBytes = f.length();
        double fileSizeInKB = fileSizeInBytes / 1024;
        double fileSizeInMB = fileSizeInKB / 1024;
        return String.format("%.2f", fileSizeInMB);
    }

    private String createRcloneSyncCommand(String inputPath, String outputPath) {
        return String.format("rclone copyto -P \"%s\" \"%s\"", inputPath, outputPath);

    }

}
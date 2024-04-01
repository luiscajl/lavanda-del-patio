package es.lavanda.lib.common.service;

import es.lavanda.lib.common.dto.ffmpeg.FfprobeDTO;
import es.lavanda.lib.common.dto.rclone.RcloneSync;
import es.lavanda.lib.common.exception.HandlerException;

public interface CommandService {

    /**
     * Using FFPROBE
     * 
     * @param path
     * @return
     * @throws HandlerException
     */
    FfprobeDTO getMediaInfo(String path) throws HandlerException;

    void syncFile(String inputPath, String outputPath, int id) throws HandlerException;

}
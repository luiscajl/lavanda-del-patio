package es.lavanda.filebot.executor.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import es.lavanda.lib.common.model.filebot.FilebotAction;
import es.lavanda.lib.common.model.filebot.FilebotCategory;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Data
@Document("filebot_execution")
@ToString
public class FilebotExecution implements Serializable {

    @Id
    private String id;

    @Field("files")
    private List<FileExecutor> files = new ArrayList<>();

    private String name;

    @Field("path")
    private String path;

    @Field("new_path")
    private String newPath;

    @Field("command")
    private String command;

    @Field("english")
    private boolean english;

    @Field("force_strict")
    private boolean forceStrict;

    @Field("on_test_phase")
    private boolean onTestPhase = true;

    @Field("query")
    private String query;

    @Field("category")
    private FilebotCategory category;

    @Field("action")
    private FilebotAction action = FilebotAction.COPY;

    @Field("manual")
    private boolean manual;

    @Field("status")
    private FilebotStatus status = FilebotStatus.UNPROCESSED;

    @Field("log")
    private String log;

    @CreatedDate
    @Field("created_at")
    private Date createdAt;

    @LastModifiedDate
    @Field("last_modified_at")
    private Date lastModifiedAt;

    @Indexed(expireAfter = "P14D")
    private String expireAfterFourteenDays;

    public enum FilebotStatus {
        UNPROCESSED, ON_TELEGRAM, PENDING, ON_FILEBOT_EXECUTION, ON_TELEGRAM_TEST, PROCESSED,
        FILES_EXISTED_IN_DESTINATION,
        ERROR,
        FILES_NOT_FOUND;
    }

    @Data
    @RequiredArgsConstructor
    public static class FileExecutor {
        private String file;
        private String newFile;
    }
}

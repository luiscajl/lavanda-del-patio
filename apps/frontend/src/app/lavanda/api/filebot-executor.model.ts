export interface FilebotExecutor {
    id?: string;
    files?: string[];
    newFiles?: string[];
    path?: string;
    newPath?: string
    category?: FilebotExecutorCategory;
    command?: string | null;
    english?: boolean;
    expireAfterFourteenDays?: string;
    status?: FilebotExecutorStatus;
    action?: FilebotExecutorAction;
    log?: string;
}

export enum FilebotExecutorStatus {
    UNPROCESSED = "UNPROCESSED",
    ON_TELEGRAM = "ON_TELEGRAM",
    PENDING = "PENDING",
    ON_FILEBOT_EXECUTION = "ON_FILEBOT_EXECUTION",
    PROCESSED = "PROCESSED",
    FILES_EXISTED_IN_DESTINATION = "FILES_EXISTED_IN_DESTINATION",
    ERROR = "ERROR",
    FILES_NOT_FOUND = "FILES_NOT_FOUND"
}

export enum FilebotExecutorAction {
    COPY = "COPY",
    MOVE = "MOVE",
}

export enum FilebotExecutorCategory {
    TV = "TV",
    TV_EN = "TV_EN",
    FILM = "FILM"
}

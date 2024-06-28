package es.lavanda.lib.common.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Provider {

    MEGA("MEGA"),

    NAS("NAS"),

    DROPBOX("DROPBOX");

    private final String value;

}

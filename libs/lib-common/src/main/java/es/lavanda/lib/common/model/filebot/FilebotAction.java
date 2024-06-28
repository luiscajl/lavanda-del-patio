package es.lavanda.lib.common.model.filebot;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum FilebotAction {

    COPY("Copiar"), MOVE("Mover");

    private String value;

    FilebotAction(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @JsonCreator
    public static FilebotAction fromValue(String value) {
        for (FilebotAction b : FilebotAction.values()) {
            if (b.value.equals(value)) {
                return b;
            }
            if (b.name().equals(value)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }

    public static List<String> getAllValues() {
        FilebotAction[] categories = values();
        List<String> categoryValues = new ArrayList<>(categories.length);

        for (int i = 0; i < categories.length; i++) {
            categoryValues.add(categories[i].getValue());
        }

        return categoryValues;
    }
}

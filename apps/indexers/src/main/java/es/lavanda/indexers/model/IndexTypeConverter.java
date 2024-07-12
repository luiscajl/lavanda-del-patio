package es.lavanda.indexers.model;

import java.beans.PropertyEditorSupport;

public class IndexTypeConverter extends PropertyEditorSupport {
    public void setAsText(final String text) throws IllegalArgumentException {
        setValue(Index.Type.valueOf(text));
    }
}

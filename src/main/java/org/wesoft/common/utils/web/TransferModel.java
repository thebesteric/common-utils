package org.wesoft.common.utils.web;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashMap;

/**
 * TransferModel
 *
 * @author Eric Joe
 * @version Ver 1.0
 * @build 2020-03-01 17:43
 */
@NoArgsConstructor
public class TransferModel extends HashMap<String, Object> implements Serializable {

    // TransferModel.Entry
    private static final String ENTRY = "entry";

    // Object
    private static final String OBJECT = "object";

    public <T> TransferModel(Entry<String, T> entry) {
        setEntry(entry);
    }

    public <T> TransferModel(T object) {
        setObject(object);
    }

    public <T> void setEntry(Entry<String, T> entry) {
        this.put(ENTRY, entry);
    }

    @SuppressWarnings("unchecked")
    public <T> Entry<String, T> getEntry(Class<T> clazz) {
        return (Entry<String, T>) this.get(ENTRY);
    }

    public <T> void setObject(T object) {
        this.put(OBJECT, object);
    }

    @SuppressWarnings("unchecked")
    public <T> T getObject(Class<T> clazz) {
        return (T) this.get(OBJECT);
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Entry<String, Object> implements Serializable {
        private String key;
        private Object value;
    }
}

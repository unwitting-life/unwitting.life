package life.unwitting.http4j.model;

import java.util.Map;

public class KeyValuePair implements Map.Entry<String, String> {
    public String key;
    public String value;

    public KeyValuePair(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return null;
    }

    public String getValue() {
        return null;
    }

    public String setValue(String value) {
        return null;
    }
}

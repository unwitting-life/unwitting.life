package life.unwitting;

import org.json.JSONObject;
import org.json.XML;

@SuppressWarnings("unused")
public class xml4j extends lib<String> {
    public static final String ValueField = "value";
    public static final String Colon = ":";

    public xml4j(String obj) {
        super(obj);
    }

    public <T> T toObject(Class<T> clazz) {
        T obj = null;
        try {
            json4j json = this.toJson();
            if (json != null) {
                obj = json.toObject(clazz);
            }
        } catch (Exception e) {
            log4j.err(e);
        }
        return obj;
    }

    @Override
    public json4j toJson() {
        json4j json = null;
        try {
            if (this.isJsonString()) {
                JSONObject jObject = XML.toJSONObject(this.m_instance);
                String j = jObject.toString();
                if (lib.notNullOrEmpty(j)) {
                    json = new json4j(j);
                }
            }
        } catch (Exception e) {
            log4j.err(e);
        }
        return json;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}


package life.unwitting;

import life.unwitting.httpd4j.httpd4j;
import jp.co.fujixerox.xcp.plugin.repository.PluginDescriptor;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;

@SuppressWarnings({"unused", "rawtypes"})
public class lib<T> {
    protected T m_instance;

    public lib(T obj) {
        this.m_instance = obj;
    }

    public static httpd4j init(PluginDescriptor descriptor, Class<?> ownerClass) {
        file4j.pluginDescriptor = descriptor;
        if (lib.notNull(descriptor)) {
            log4j.logFileDirectory = descriptor.getWorkFile(
                    httpd4j.class.getSimpleName()).getParent() + File.separator + log4j.DirectoryName;
        }
        return httpd4j.newInstance(descriptor)
                .prototype(ownerClass)
                .disableProxy()
                .launch();
    }

    public T instance() {
        return this.m_instance;
    }

    public boolean isNull() {
        return this.m_instance == null;
    }

    public boolean isJsonString() {
        return lib.notNull(this.m_instance);
    }

    public json4j toJson() {
        return new json4j(this.m_instance);
    }

    public file4j ToFile() throws CloneNotSupportedException {
        file4j file;
        if (this.m_instance instanceof String) {
            file = new file4j(new File((String) this.m_instance));
        } else if (this.m_instance instanceof File) {
            file = new file4j((File) this.m_instance);
        } else if (this.m_instance instanceof file4j) {
            file = (file4j) this.m_instance;
        } else {
            throw new CloneNotSupportedException();
        }
        return file;
    }

    public reflection4j ToReflection() {
        return new reflection4j(this.m_instance);
    }

    public port4j ToPort() {
        port4j port = new port4j(0);
        if (this.m_instance instanceof Integer) {
            port = new port4j((Integer) this.m_instance);
        }
        return port;
    }

    public array4j ToArray() {
        return new array4j(this.m_instance);
    }

    public static boolean isNull(Object obj) {
        boolean b = false;
        if (obj instanceof String) {
            if (obj == "") {
                b = true;
            }
        } else if (obj == null) {
            b = true;
        }
        return b;
    }

    public static boolean notNull(Object obj) {
        return !lib.isNull(obj);
    }

    public static boolean notNull(Object... objects) {
        boolean b = true;
        for (Object o : objects) {
            if (lib.isNull(o)) {
                b = false;
                break;
            }
        }
        return b;
    }

    public static boolean isNullOrEmpty(String obj) {
        return obj == null || obj.equals("");
    }

    public static boolean isNullOrZeroLength(Object obj) {
        return obj == null ||
                !obj.getClass().isArray() ||
                Array.getLength(obj) == 0;
    }

    public static boolean notNullOrEmpty(String obj) {
        return obj != null && !obj.equals("");
    }

    public static boolean notNullOrZeroLength(Object obj) {
        return obj != null &&
                obj.getClass().isArray() &&
                Array.getLength(obj) > 0;
    }

    public static String nm(String obj) {
        return obj == null ? string4j.empty : obj;
    }

    public static <T> T nm(T obj, T substObj) {
        return obj == null ? substObj : obj;
    }

    public static int parseInt(Integer obj) {
        return obj == null ? 0 : obj;
    }

    public static int parseInt(String obj) {
        return lib.of(obj).toInt();
    }

    public static lib of(Object obj) {
        return new lib<Object>(obj);
    }

    public static string4j of(String obj) {
        return new string4j(obj);
    }

    public static arrayList4j of(ArrayList obj) {
        return new arrayList4j(obj);
    }

    public static file4j of(File obj) {
        return new file4j(obj);
    }

    public static int4j of(Integer obj) {
        return new int4j(obj);
    }

    public static array4j of(Array array) {
        return new array4j(array);
    }

    public static inputStream4j of(InputStream input) {
        return new inputStream4j(input);
    }

    public static arrayTemplate4j<Byte> of(byte[] bytes) {
        Byte[] byteObjects = null;
        if (bytes != null) {
            byteObjects = new Byte[bytes.length];
            int i = 0;
            for (byte b : bytes) {
                byteObjects[i++] = b;
            }
        }
        return new arrayTemplate4j<Byte>(byteObjects);
    }

    public static byte4j of(Byte obj) {
        return new byte4j(obj);
    }
}

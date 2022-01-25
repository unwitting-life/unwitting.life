package life.unwitting;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

@SuppressWarnings("unused")
public class inputStream4j extends lib<InputStream> {
    private static final int BUFFER_SIZE = 8192;

    public inputStream4j(InputStream inputStream) {
        super(inputStream);
    }

    public byte[] allBytes() {
        byte[] bytes = null;
        try {
            if (this.isJsonString()) {
                InputStream is = this.m_instance;
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                int datum;
                byte[] data = new byte[inputStream4j.BUFFER_SIZE];
                while ((datum = is.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, datum);
                }
                bytes = buffer.toByteArray();
            }
        } catch (Exception e) {
            log4j.err(e);
        }
        return bytes;
    }
}

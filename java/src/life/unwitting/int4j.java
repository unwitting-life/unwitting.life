package life.unwitting;

public class int4j extends lib<Integer> {

    public int4j(Integer n) {
        super(n);
    }

    public byte[] ToBytes(boolean isBigEndian) {
        byte[] bytes = new byte[4];
        if (this.isJsonString()) {
            if (isBigEndian) {
                bytes[0] = (byte) (this.m_instance >> 24 & 0xff);
                bytes[1] = (byte) (this.m_instance >> 16 & 0xff);
                bytes[2] = (byte) (this.m_instance >> 8 & 0xff);
                bytes[3] = (byte) (this.m_instance & 0xff);
            } else {
                bytes[0] = (byte) (this.m_instance & 0xff);
                bytes[1] = (byte) (this.m_instance >> 8 & 0xff);
                bytes[2] = (byte) (this.m_instance >> 16 & 0xff);
                bytes[3] = (byte) (this.m_instance >> 24 & 0xff);
            }
        }
        return bytes;
    }
}

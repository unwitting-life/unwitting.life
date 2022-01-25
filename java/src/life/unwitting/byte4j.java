package life.unwitting;

public class byte4j extends lib<Byte> {

    public byte4j(Byte obj) {
        super(obj);
    }

    public char toChar() {
        return (char) ((int) this.m_instance & 0xFF);
    }
}

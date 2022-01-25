package life.unwitting.http4j.model;

@SuppressWarnings("unused")
public abstract class DataArrival {
    public Object parameter;

    public DataArrival() {
    }

    public DataArrival(Object param) {
        this.parameter = param;
    }

    public abstract boolean arrived(byte[] data);

    public abstract void closed();
}
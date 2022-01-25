package life.unwitting.Serializable;

@SuppressWarnings("unused")
public abstract class SerializableNode {
    public SerializableNode() {
    }

    public String serializeName(String fieldName) {
        return fieldName;
    }
}

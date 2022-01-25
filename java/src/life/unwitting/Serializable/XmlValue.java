package life.unwitting.Serializable;

import life.unwitting.string4j;

@SuppressWarnings("unused")
public abstract class XmlValue extends SerializableNode {
    public Object content;

    public Object getContent() {
        return this.content;
    }

    public void setContent(Object content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return this.content == null ? string4j.empty : this.content.toString();
    }
}

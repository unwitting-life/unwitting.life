package life.unwitting;

import java.util.ArrayList;

@SuppressWarnings({"rawtypes", "unused"})
public
class arrayList4j extends lib<ArrayList> {
    public arrayList4j(ArrayList obj) {
        super(obj);
    }

    @SuppressWarnings("unchecked")
    public <T> T[] toArray(Class<T> type) {
        T[] array = null;
        if (this.isJsonString() && type != null) {
            array = (T[]) array4j.newInstance(type, this.m_instance.size());
            for (int i = 0; i < this.m_instance.size(); i++) {
                Object o = this.m_instance.get(i);
                if (o == null || type.isAssignableFrom(o.getClass())) {
                    array[i] = (T) o;
                }
            }
        }
        return array;
    }

    public byte[] allBytes() {
        byte[] bytes = null;
        if (this.isJsonString()) {
            bytes = new byte[this.m_instance.size()];
            for (int i = 0; i < this.m_instance.size(); i++) {
                Object o = this.m_instance.get(i);
                if (o instanceof Byte) {
                    bytes[i] = (Byte) o;
                }
            }
        }
        return bytes;
    }

    @Override
    public boolean isJsonString() {
        boolean b = false;
        if (super.isJsonString()) {
            if (this.m_instance != null) {
                ArrayList list = this.m_instance;
                b = list.size() > 0;
            }
        }
        return b;
    }
}

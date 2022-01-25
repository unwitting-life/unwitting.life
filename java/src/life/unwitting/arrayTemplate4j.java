package life.unwitting;

import java.util.ArrayList;
import java.util.Arrays;

public class arrayTemplate4j<T> extends lib<T[]> {
    public arrayTemplate4j(T[] obj) {
        super(obj);
    }

    @Override
    public boolean isJsonString() {
        return super.isJsonString() && this.m_instance.length > 0;
    }

    public ArrayList<T> toList() {
        ArrayList<T> list = null;
        if (this.isJsonString()) {
            list = new ArrayList<T>(Arrays.asList(this.m_instance));
        }
        return list;
    }

    public char[] toCharArray() {
        char[] raw = null;
        if (super.isJsonString() && this.m_instance.getClass().getComponentType() == Byte.class) {
            if (this.m_instance.length == 0) {
                raw = new char[]{};
            } else {
                raw = new char[this.m_instance.length];
                for (int i = 0; i < this.m_instance.length; i++) {
                    raw[i] = of((Byte) this.m_instance[i]).toChar();
                }
            }
        }
        return raw;
    }

    public long toLong(boolean isBigEndian) {
        long value = 0;
        if (this.isJsonString() && this.m_instance.getClass().getComponentType() == Byte.class) {
            if (isBigEndian) {
                for (T e : this.m_instance) {
                    value = (value << 8) | (Byte) e & 0xFF;
                }
            } else {
                int shift = 0;
                for (T e : this.m_instance) {
                    value = (Byte) e & 0xFF | (value << shift);
                    shift += 8;
                }
            }
        }
        return value;
    }
}
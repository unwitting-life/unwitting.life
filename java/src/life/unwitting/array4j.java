package life.unwitting;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class array4j extends lib<Object> {
    public array4j(Object obj) {
        super(obj);
    }

    @Override
    public boolean isJsonString() {
        return super.isJsonString() && this.m_instance.getClass().isArray();
    }

    public static byte[] copyOf(byte[] original, int newLength) {
        byte[] copy = new byte[newLength];
        System.arraycopy(original, 0, copy, 0,
                Math.min(original.length, newLength));
        return copy;
    }

    public static byte[] resize(byte[] old, int newSize) {
        byte[] arr = new byte[newSize];
        if (old != null) {
            arr = array4j.copyOf(old, Math.min(old.length, arr.length));
        }
        return arr;
    }

    public static Object newInstance(Class<?> type) {
        return Array.newInstance(type, 0);
    }

    public static Object newInstance(Class<?> type, int size) {
        return Array.newInstance(type, size);
    }

    public static Object newInstanceWithElement(Class<?> type, Object element) {
        Object obj = Array.newInstance(type, 1);
        Array.set(obj, Array.getLength(obj) - 1, element);
        return obj;
    }

    @SuppressWarnings("SuspiciousSystemArraycopy")
    public static Object resize(Object obj, int newSize) {
        Object array = obj;
        if (obj != null && obj.getClass().isArray()) {
            int size = array4j.sizeof(obj);
            array = Array.newInstance(obj.getClass().getComponentType(), newSize);
            if (size > 0) {
                System.arraycopy(obj, 0, array, 0, size);
            }
        }
        return array;
    }

    public static Object increase(Object obj) {
        Object array = obj;
        if (obj != null && obj.getClass().isArray()) {
            try {
                array = array4j.resize(obj, array4j.sizeof(obj) + 1);
            } catch (Exception e) {
                log4j.err(e);
            }
        }
        return array;
    }

    public static Object increaseNewInstance(Object obj) {
        Object array = obj;
        if (obj != null && obj.getClass().isArray()) {
            try {
                array = array4j.increaseWith(obj, obj.getClass().getComponentType().newInstance());
            } catch (Exception e) {
                log4j.err(e);
            }
        }
        return array;
    }

    public static Object increaseWith(Object obj, Object element) {
        Object array = obj;
        if (obj != null && obj.getClass().isArray()) {
            array = array4j.resize(obj, Array.getLength(obj) + 1);
            try {
                Array.set(array, Array.getLength(array) - 1, element);
            } catch (Exception e) {
                log4j.err(e);
            }
        }
        return array;
    }

    @SuppressWarnings("SuspiciousSystemArraycopy")
    public static Object addTail(Object array, Object element) {
        Object obj = null;
        if (array != null && array.getClass().isArray()) {
            obj = Array.newInstance(array.getClass().getComponentType(), Array.getLength(array) + 1);
            System.arraycopy(array, 0, obj, 0, Array.getLength(array));
            Array.set(obj, Array.getLength(obj) - 1, element);
        }
        return obj;
    }

    public static Object last(Object expr) {
        Object element = null;
        if (expr != null && expr.getClass().isArray()) {
            int length = Array.getLength(expr);
            if (length > 0) {
                element = Array.get(expr, length - 1);
            }
        }
        return element;
    }

    public static int sizeof(Object array) {
        int size = 0;
        try {
            if (array != null && array.getClass().isArray()) {
                size = Array.getLength(array);
            }
        } catch (Exception e) {
            log4j.err(e);
        }
        return size;
    }

    public static Object get(Object array, int i) {
        Object element = null;
        try {
            if (array != null && array.getClass().isArray()) {
                element = Array.get(array, i);
            }
        } catch (Exception e) {
            log4j.err(e);
        }
        return element;
    }

    public static void set(Object array, int i, Object element) {
        try {
            if (array != null && array.getClass().isArray()) {
                Array.set(array, i, element);
            }
        } catch (Exception e) {
            log4j.err(e);
        }
    }

    @SuppressWarnings({"rawtypes", "SingleStatementInBlock", "UnusedReturnValue", "unchecked"})
    public List toArrayList() {
        ArrayList list = null;
        if (this.isJsonString()) {
            list = new ArrayList();
            int length = Array.getLength(this.m_instance);
            for (int i = 0; i < length; i++) {
                list.add(Array.get(this.m_instance, i));
            }
        }
        return list;
    }

    @SuppressWarnings({"SingleStatementInBlock", "unchecked"})
    public <T> ArrayList<T> toArrayList(Class<T> type) {
        ArrayList<T> list = null;
        if (this.isJsonString()) {
            list = new ArrayList<T>();
            int length = Array.getLength(this.m_instance);
            for (int i = 0; i < length; i++) {
                Object obj = Array.get(this.m_instance, i);
                if (obj == null || obj.getClass() == type) {
                    list.add((T) obj);
                }
            }
        }
        return list;
    }
}

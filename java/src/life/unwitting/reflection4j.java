package life.unwitting;

import java.lang.reflect.Field;
import java.sql.Connection;

@SuppressWarnings("unused")
public class reflection4j extends lib<Object> {
    public reflection4j(Object any) {
        super(any);
    }

    public Object instance() {
        return this.m_instance;
    }

    public static instanceType GetType(Class<?> obj) {
        instanceType type = instanceType.unknown;
        if (obj != null) {
            if (obj.isEnum()) {
                type = instanceType.Enum;
            } else if ((String.class.isAssignableFrom(obj) ||
                    Character.class.isAssignableFrom(obj) ||
                    Character.class.isAssignableFrom(obj) ||
                    char.class.isAssignableFrom(obj))) {
                type = instanceType.String;
            } else if ((Byte.class.isAssignableFrom(obj) ||
                    Short.class.isAssignableFrom(obj) ||
                    Integer.class.isAssignableFrom(obj) ||
                    Number.class.isAssignableFrom(obj) ||
                    int.class.isAssignableFrom(obj) ||
                    byte.class.isAssignableFrom(obj) ||
                    short.class.isAssignableFrom(obj))) {
                type = instanceType.Integer;
            } else if ((Long.class.isAssignableFrom(obj) ||
                    long.class.isAssignableFrom(obj))) {
                type = instanceType.Long;
            } else if ((Float.class.isAssignableFrom(obj) || float.class.isAssignableFrom(obj))) {
                type = instanceType.Float;
            } else if ((Double.class.isAssignableFrom(obj) || double.class.isAssignableFrom(obj))) {
                type = instanceType.Double;
            } else if ((Boolean.class.isAssignableFrom(obj) ||
                    Boolean.class.isAssignableFrom(obj) ||
                    boolean.class.isAssignableFrom(obj))) {
                type = instanceType.Boolean;
            } else if (obj.isArray()) {
                type = instanceType.Array;
            } else if (Connection.class.isAssignableFrom(obj)) {
                type = instanceType.Connection;
            } else if (obj == Object.class) {
                type = instanceType.Object;
            }
        }
        return type;
    }

    public static String GetMethodName() {
        String methodName = string4j.Unknown;
        try {
            throw new Exception();
        } catch (Exception e) {
            try {
                StackTraceElement[] stacks = e.getStackTrace();
                if (stacks.length >= 2) {
                    methodName = stacks[1].getMethodName();
                }
            } catch (Exception err) {
                log4j.err(err);
            }
        }
        return methodName;
    }

    public static String GetUpstairsFullMethodName() {
        String methodName = string4j.Unknown;
        try {
            throw new Exception();
        } catch (Exception e) {
            try {
                StackTraceElement[] stacks = e.getStackTrace();
                if (stacks.length >= 3) {
                    methodName = stacks[2].getClassName() + "." + stacks[2].getMethodName();
                }
            } catch (Exception err) {
                log4j.err(err);
            }
        }
        return methodName;
    }

    public static String GetUpstairsMethodName() {
        String methodName = string4j.Unknown;
        try {
            throw new Exception();
        } catch (Exception e) {
            try {
                StackTraceElement[] stacks = e.getStackTrace();
                if (stacks.length >= 3) {
                    methodName = stacks[2].getMethodName();
                }
            } catch (Exception err) {
                log4j.err(err);
            }
        }
        return methodName;
    }

    @SuppressWarnings("StringConcatenationInsideStringBufferAppend")
    public static void setValue(Field field, Object obj, Object value) {
        try {
            if (field != null && obj != null) {
                try {
                    field.setAccessible(true);
                } catch (Exception ignore) {

                }
                if (field.getType() == String.class && value != null) {
                    if (value.getClass() == String.class) {
                        field.set(obj, value);
                    } else {
                        field.set(obj, value.toString());
                    }
                } else {
                    field.set(obj, value);
                }
            }
        } catch (Exception e) {
            StringBuilder sb = new StringBuilder();
            sb.append("obj: " + obj.getClass().getSimpleName() + ", field: " + field.getName());
            if (value != null) {
                sb.append(", value: " + value + ", valueType: " + value.getClass().getSimpleName());
            }
            log4j.info(sb.toString());
            log4j.err(e);
        }
    }

    @SuppressWarnings("unused")
    public enum instanceType {
        unknown(-1),
        Object(0),
        Byte(1),
        Integer(2),
        Long(3),
        Float(4),
        Double(5),
        Boolean(6),
        byteType(7),
        intType(8),
        longType(9),
        floatType(10),
        doubleType(11),
        booleanType(12),
        String(13),
        Array(14),
        Enum(15),
        Connection(16),
        NotSpecified(17);
        private final int instanceType;

        instanceType(int type) {
            this.instanceType = type;
        }

        public int value() {
            return this.instanceType;
        }

        public boolean isPrimitive() {
            return this == reflection4j.instanceType.Object ||
                    this == reflection4j.instanceType.Integer ||
                    this == reflection4j.instanceType.Long ||
                    this == reflection4j.instanceType.Float ||
                    this == reflection4j.instanceType.Double ||
                    this == reflection4j.instanceType.Boolean ||
                    this == reflection4j.instanceType.Enum ||
                    this == reflection4j.instanceType.String;
        }

        public static instanceType parse(int type) {
            instanceType value = reflection4j.instanceType.unknown;
            for (instanceType e : reflection4j.instanceType.values()) {
                if (e.instanceType == type) {
                    value = e;
                    break;
                }
            }
            return value;
        }
    }
}

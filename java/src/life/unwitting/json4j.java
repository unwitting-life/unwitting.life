package life.unwitting;

import life.unwitting.Serializable.SerializableNode;
import life.unwitting.Serializable.XmlValue;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
public class json4j extends lib<Object> {
    public int indentFactor = 4;

    public json4j(Object obj) {
        super(obj);
    }

    public boolean isJsonString() {
        boolean result = false;
        try {
            if (super.isJsonString() && this.m_instance instanceof String) {
                new JSONObject((String) this.m_instance);
                result = true;
            }
        } catch (Exception e) {
            log4j.err(e);
        }
        return result;
    }

    public String toString() {
        return this.isJsonString() ? (String) this.m_instance : this.toString(true);
    }

    public String toString(boolean ignoreNull) {
        String json = "{}";
        try {
            if (this.m_instance instanceof String) {
                assert (false);
            } else if (notNull(this.m_instance)) {
                Object inst = this.m_instance;
                if (this.m_instance.getClass().isAssignableFrom(List.class) ||
                        this.m_instance.getClass().isAssignableFrom(ArrayList.class)) {
                    inst = ((List<?>) this.m_instance).toArray();
                }
                if (inst.getClass().isArray()){
                    int length = Array.getLength(inst);
                    JSONArray jArray = new JSONArray();
                    for (int i = 0; i < length; i++) {
                        jArray.put(i, this.jObjectFromObject(Array.get(inst, i), ignoreNull));
                    }
                    json = jArray.toString(this.indentFactor);
                } else {
                    json = this.jObjectFromObject(inst, ignoreNull).toString(this.indentFactor);
                }
            }
        } catch (Exception e) {
            log4j.err(e);
        }
        return json;
    }

    protected JSONObject jObjectFromObject(Object obj, boolean ignoreNull) {
        JSONObject j = null;
        try {
            if (obj != null) {
                j = new JSONObject();
                if (obj.getClass().isPrimitive() || reflection4j.GetType(obj.getClass()).isPrimitive()) {
                    j.put(obj.getClass().getSimpleName(), obj);
                } else {
                    List<Field> declaredFields = new ArrayList<Field>();
                    List<Field> superFields = new ArrayList<Field>();
                    try {
                        declaredFields.addAll(Arrays.asList(obj.getClass().getDeclaredFields()));
                    } catch (Exception ignored) {
                    }
                    try {
                        superFields.addAll(Arrays.asList(obj.getClass().getSuperclass().getDeclaredFields()));
                    } catch (Exception ignored) {
                    }
                    ArrayList<Field> fields = new ArrayList<Field>();
                    if (runtime4j.isXCPJvm()) {
                        fields.addAll(superFields);
                        fields.addAll(declaredFields);
                    } else {
                        fields.addAll(declaredFields);
                        fields.addAll(superFields);
                    }
                    for (Field field : fields) {
                        try {
                            if (!Modifier.isStatic(field.getModifiers())) {
                                try {
                                    field.setAccessible(true);
                                } catch (Exception ignored) {

                                }
                                Object value = field.get(obj);
                                if (!ignoreNull || value != null) {
                                    reflection4j.instanceType type = reflection4j.GetType(field.getType());
                                    if (type.isPrimitive() || field.getType().isPrimitive()) {
                                        Object jValue = field.get(obj);
                                        if (jValue == null) {
                                            jValue = string4j.nil;
                                        }
                                        j.put(field.getName(), jValue);
                                    } else if (field.getType().isArray()) {
                                        if (value == null) {
                                            j.put(field.getName(), array4j.newInstance(field.getType()));
                                        } else {
                                            j.put(field.getName(), this.jArrayFromArray(value, ignoreNull));
                                        }
                                    } else {
                                        Object jValue = this.jObjectFromObject(field.get(obj), ignoreNull);
                                        if (jValue == null) {
                                            jValue = string4j.nil;
                                        }
                                        j.put(field.getName(), jValue);
                                    }
                                }
                            }
                        } catch (Exception e) {
                            log4j.err(e);
                        }
                    }
                }
            }
        } catch (Exception e) {
            log4j.err(e);
        }
        return j;
    }

    public Object arrayFromJObject(JSONObject jObject, Class<?> arrayElementType) {
        Object array = null;
        try {
            if (jObject != null) {
                Object obj = this.readJSONAndReflectToNewObject(jObject.toString(this.indentFactor), arrayElementType);
                array = array4j.newInstanceWithElement(arrayElementType, obj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return array;
    }

    public Object arrayFromJArray(JSONArray jsonArray, Class<?> arrayElementType) {
        Object array = array4j.newInstance(arrayElementType);
        if (jsonArray != null) {
            int length = jsonArray.length();
            for (int i = 0; i < length; i++) {
                try {
                    array = array4j.increaseNewInstance(array);
                    Object element = jsonArray.get(i);
                    if (element != null) {
                        if (element instanceof JSONObject) {
                            array4j.set(array, Array.getLength(array) - 1,
                                    this.readJSONAndReflectToNewObject(((JSONObject) element).toString(this.indentFactor), arrayElementType));
                        } else {
                            array4j.set(array, Array.getLength(array) - 1, element);
                        }
                    }
                } catch (Exception e) {
                    log4j.err(e);
                }
            }
        }
        return array;
    }

    public JSONArray jArrayFromArray(Object array, boolean ignoreNull) {
        JSONArray jArray = new JSONArray();
        if (array != null && array.getClass().isArray()) {
            int length = array4j.sizeof(array);
            for (int i = 0; i < length; i++) {
                Object element = array4j.get(array, i);
                if (!ignoreNull || element != null) {
                    if (element == null) {
                        jArray.put((Object) null);
                    } else if (element.getClass().isPrimitive() || reflection4j.GetType(element.getClass()).isPrimitive()) {
                        jArray.put(element);
                    } else {
                        jArray.put(this.jObjectFromObject(element, ignoreNull));
                    }
                }
            }
        }
        return jArray;
    }

    @SuppressWarnings("unchecked")
    public <T> T toObject(Class<T> type) {
        T obj = null;
        try {
            if (this.m_instance instanceof String) {
                obj = (T) this.readJSONAndReflectToNewObject((String) this.m_instance, type);
            }
        } catch (Exception e) {
            log4j.err(e);
        }
        return obj;
    }

    public static <T> T parse(String json, Class<T> type) {
        return new json4j(json).toObject(type);
    }

    public Object readJSONAndReflectToNewObject(String json, Class<?> type) {
        Object obj = null;
        try {
            if (notNullOrEmpty(json) && type != null) {
                if (type.isArray()) {
                    obj = this.arrayFromJArray(new JSONArray(json), type.getComponentType());
                } else {
                    obj = type.newInstance();
                    Object jToken = new JSONTokener(json).nextValue();
                    if (jToken != null) {
                        if (jToken instanceof JSONObject) {
                            JSONObject jObject = (JSONObject) new JSONTokener(json).nextValue();
                            if (jObject != null) {
                                List<Field> fields = new ArrayList<Field>();
                                for (Field field : type.getFields()) {
                                    if (!Modifier.isStatic(field.getModifiers())) {
                                        fields.add(field);
                                    }
                                }
                                for (Field field : type.getDeclaredFields()) {
                                    if (!Modifier.isStatic(field.getModifiers())) {
                                        fields.add(field);
                                    }
                                }
                                for (Field field : fields) {
                                    JSONObject jValue = this.findJValueFromJObject(jObject, field);
                                    if (jValue == null) {
                                        Object value = this.findValueFromJObject(jObject, field);
                                        if (XmlValue.class.isAssignableFrom(field.getType())) {
                                            XmlValue e = (XmlValue) field.getType().newInstance();
                                            e.content = value;
                                            reflection4j.setValue(field, obj, e);
                                        } else {
                                            if (value instanceof String && value.equals(string4j.nil)) {
                                                value = null;
                                            }
                                            reflection4j.setValue(field, obj, value);
                                        }
                                    } else {
                                        try {
                                            Object value = this.readJSONAndReflectToNewObject(jValue.toString(this.indentFactor), field.getType());
                                            reflection4j.setValue(field, obj, value);
                                        } catch (Exception e) {
                                            log4j.err(e);
                                        }
                                    }
                                }
                            }
                        } else if (jToken instanceof String && type == String.class) {
                            obj = jToken;
                        } else {
                            obj = null;
                        }
                    }
                }
            }
        } catch (Exception e) {
            log4j.err(e);
        }
        return obj;
    }

    public JSONObject findJValueFromJObject(JSONObject obj, Field reflectedFieldInfoOfNewObject) {
        JSONObject jValue = null;
        Object value = this.findValueFromJObject(obj, reflectedFieldInfoOfNewObject);
        if (value instanceof JSONObject) {
            jValue = (JSONObject) value;
        }
        return jValue;
    }

    public Object findValueFromJObject(JSONObject obj, Field reflectedFieldInfoOfNewObject) {
        Object value = null;
        try {
            String jsonKeyNameForReflectedFieldInfo = reflectedFieldInfoOfNewObject.getName();
            try {
                boolean isAssignableFromSerializableNodeClass = false;
                if (SerializableNode.class.isAssignableFrom(reflectedFieldInfoOfNewObject.getType())) {
                    isAssignableFromSerializableNodeClass = true;
                } else {
                    Class<?> declaringClass = reflectedFieldInfoOfNewObject.getDeclaringClass();
                    while (declaringClass != null && !isAssignableFromSerializableNodeClass) {
                        if (SerializableNode.class.isAssignableFrom(reflectedFieldInfoOfNewObject.getType())) {
                            isAssignableFromSerializableNodeClass = true;
                        } else {
                            declaringClass = declaringClass.getDeclaringClass();
                        }
                    }
                }
                if (isAssignableFromSerializableNodeClass && !Modifier.isAbstract(reflectedFieldInfoOfNewObject.getType().getModifiers())) {
                    jsonKeyNameForReflectedFieldInfo = ((SerializableNode) reflectedFieldInfoOfNewObject.getType().newInstance()).serializeName(jsonKeyNameForReflectedFieldInfo);
                }
            } catch (Exception e) {
                log4j.err(e);
            }
            if (obj.has(jsonKeyNameForReflectedFieldInfo)) {
                Class<?> fieldType = reflectedFieldInfoOfNewObject.getType();
                if (int.class.isAssignableFrom(fieldType)) {
                    value = obj.optInt(jsonKeyNameForReflectedFieldInfo);
                } else if (long.class.isAssignableFrom(fieldType)) {
                    value = obj.optLong(jsonKeyNameForReflectedFieldInfo);
                } else if (float.class.isAssignableFrom(fieldType) || double.class.isAssignableFrom(fieldType)) {
                    value = obj.optDouble(jsonKeyNameForReflectedFieldInfo);
                } else if (boolean.class.isAssignableFrom(fieldType)) {
                    value = obj.optBoolean(jsonKeyNameForReflectedFieldInfo);
                } else if (fieldType.isArray()) {
                    if (obj.optJSONArray(jsonKeyNameForReflectedFieldInfo) == null) {
                        Object jObject = obj.get(jsonKeyNameForReflectedFieldInfo);
                        assert (jObject instanceof JSONObject);
                        value = this.arrayFromJObject((JSONObject) jObject,
                                reflectedFieldInfoOfNewObject.getType().getComponentType());
                    } else {
                        value = this.arrayFromJArray(obj.optJSONArray(jsonKeyNameForReflectedFieldInfo),
                                reflectedFieldInfoOfNewObject.getType().getComponentType());
                    }
                } else {
                    value = obj.get(jsonKeyNameForReflectedFieldInfo);
                    if (value instanceof JSONArray) {
                        JSONArray array = (JSONArray) value;
                        if (array.length() > 0) {
                            value = array.get(0);
                        } else {
                            value = null;
                        }
                    }
                }
            }
        } catch (Exception e) {
            log4j.err(e);
        }
        return value;
    }
}

package life.unwitting;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
public class string4j extends lib<String> {
    public static final String empty = "";
    public static final String nil = "null";
    public static final String Colon = ":";
    public static final String EqualsSign = "=";
    public static final String ColonSpace = ": ";
    public static final String Cr = "\r";
    public static final String Lf = "\n";
    public static final String CrLf = "\r\n";
    public static final String Space = " ";
    public static final String Splash = "/";
    public static final String DoubleQuotationMarks = "\"";
    public static final String QuestionMark = "?";
    public static final String Comma = ",";
    public static final String lBrace = "{";
    public static final String rBrace = "}";
    public static final String Semicolon = ";";
    public static final String Dot = ".";
    public static final String Unknown = "unknown";
    private static final Pattern LTRIM = Pattern.compile("^\\s+");
    private static final Pattern RTRIM = Pattern.compile("\\s+$");

    public static class encoding {
        public static final String unknown = "unknown";
        public static final String ascii = "US-ASCII";
        public static final String utf8 = "UTF-8";
    }

    public static String random() {
        return string4j.random(16, true);
    }

    @SuppressWarnings("SpellCheckingInspection")
    public static String random(int length, boolean mixed) {
        String str = mixed ? "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789" : "0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(str.length());
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }

    public string4j(String obj) {
        super(obj);
    }

    public static String nil(String expr) {
        return expr == null ? string4j.nil : expr;
    }

    protected boolean isString() {
        return super.isJsonString() && !this.m_instance.equals(string4j.empty);
    }

    public boolean isNullOrEmpty() {
        return !this.isString();
    }

    public boolean notNullOrEmpty() {
        return this.isString();
    }

    public boolean isJsonString() {
        return this.isString();
    }

    public boolean equals(String expr) {
        boolean b = false;
        if (super.isJsonString()) {
            b = this.m_instance.equals(expr);
        }
        return b;
    }

    public boolean notEquals(String expr) {
        return !this.equals(expr);
    }

    public boolean equalsIgnoreCase(String expr) {
        boolean b = false;
        if (this.isString()) {
            b = this.m_instance.equalsIgnoreCase(expr);
        }
        return b;
    }

    public boolean notEqualsIgnoreCase(String expr) {
        return !this.equalsIgnoreCase(expr);
    }

    public byte[] getBytes() {
        return this.getBytes(encoding.utf8);
    }

    public byte[] getBytes(String encoding) {
        byte[] encoded = null;
        try {
            if (this.isString() &&
                    of(encoding).isJsonString()) {
                encoded = this.m_instance.getBytes(encoding);
            }
        } catch (Exception e) {
            log4j.err(e);
        }
        return encoded;
    }

    public int toInt() {
        int i = 0;
        try {
            if (this.isJsonString()) {
                i = Integer.parseInt(this.m_instance);
            }
        } catch (Exception e) {
            log4j.err(e);
        }
        return i;
    }

    public byte toByte() {
        byte i = 0;
        try {
            if (this.isJsonString()) {
                i = Byte.parseByte(this.m_instance);
            }
        } catch (Exception e) {
            log4j.err(e);
        }
        return i;
    }

    public boolean toBool() {
        boolean b = false;
        try {
            if (this.isJsonString()) {
                if (this.notEquals("0") || this.equalsIgnoreCase("true")) {
                    b = true;
                }
            }
        } catch (Exception e) {
            log4j.err(e);
        }
        return b;
    }

    public String ltrim() {
        String ltrim = this.m_instance;
        if (this.isJsonString()) {
            ltrim = LTRIM.matcher(this.m_instance).replaceAll(string4j.empty);
        }
        return ltrim;
    }

    public String rtrim() {
        String rtrim = this.m_instance;
        if (this.isJsonString()) {
            rtrim = RTRIM.matcher(this.m_instance).replaceAll(string4j.empty);
        }
        return rtrim;
    }

    public String trimBothEnd() {
        String trim = this.m_instance;
        if (this.isJsonString()) {
            trim = RTRIM.matcher(LTRIM.matcher(this.m_instance).replaceAll(string4j.empty)).replaceAll(string4j.empty);
        }
        return trim;
    }

    public String toBase64() {
        String encode = null;
        if (this.isJsonString()) {
            encode = base64j.encodeString(this.m_instance);
        }
        return encode;
    }

    public String asBase64ToString() {
        String decode = null;
        if (this.isJsonString()) {
            decode = base64j.decodeString(this.m_instance);
        }
        return decode;
    }

    public Date toDate() {
        return this.toDate(date4j.format);
    }

    public Date toDate(String format) {
        Date date = null;
        try {
            if (this.isJsonString()) {
                date = new SimpleDateFormat(format).parse(this.m_instance);
            }
        } catch (Exception e) {
            log4j.err(e);
        }
        return date;
    }
}

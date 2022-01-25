package life.unwitting;

@SuppressWarnings("unused")
public enum sampleEnum4j {
    unknown(""),
    ascii("US-ASCII"),
    utf8("UTF-8");
    private final String name;

    sampleEnum4j(String name) {
        this.name = name;
    }

    public String value() {
        return this.name;
    }

    public static sampleEnum4j parse(String name) {
        sampleEnum4j value = unknown;
        for (sampleEnum4j e : sampleEnum4j.values()) {
            if (lib.of(e.name).equals(name)) {
                value = e;
                break;
            }
        }
        return value;
    }
}
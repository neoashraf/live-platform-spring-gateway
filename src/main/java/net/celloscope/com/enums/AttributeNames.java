package net.celloscope.com.enums;

public enum AttributeNames {
    HEADER("header"),
    CACHED_REQUEST_BODY_OBJECT("cachedRequestBodyObject"),
    CACHED_RESPONSE_BODY_OBJECT("cachedRequestBodyObject"),
    ;

    private String value;

    AttributeNames(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

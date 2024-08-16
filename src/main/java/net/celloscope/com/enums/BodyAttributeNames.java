package net.celloscope.com.enums;

public enum BodyAttributeNames {
    Client_IP_Address("clientIpAddress"),
    Client_ID("clientId"),
    ;

    private String value;

    BodyAttributeNames(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

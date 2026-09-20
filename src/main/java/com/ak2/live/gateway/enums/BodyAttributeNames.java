package com.ak2.live.gateway.enums;

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

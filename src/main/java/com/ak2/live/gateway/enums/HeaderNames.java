package com.ak2.live.gateway.enums;

public enum HeaderNames {
    Client_IP_Address("Client-IP-Address"),
    Authorization("Authorization"),
    Client_ID("Client-ID"),
    ;

    private String value;

    HeaderNames(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

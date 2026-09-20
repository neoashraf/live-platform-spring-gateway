package com.ak2.live.gateway.enums;

import lombok.Getter;

@Getter
public enum Constants {
    STATUS_YES("Yes"),
    STATUS_NO("No"),
    UPDATE_STATUS_ACTIVE("Active"),
    UPDATE_STATUS_INACTIVE("Inactive"),
    HOST_TYPE_AUDIO("Audio"),
    HOST_TYPE_VIDEO("Video"),
    RESOURCE_TYPE_IMAGE("IMAGE"),
    RESOURCE_TYPE_SVGA("SVGA"),
    RESOURCE_TYPE_ANIMATION("GIF"),
    USER_TYPE_RECEIVER("RECEIVER"),
    SOURCE_PURCHASE("PURCHASE"),
    SOURCE_GIFT("GIFT"),

    RIDE_ENTRY("RIDE_ENTRY"),
    FRAME("FRAME"),
    ENTRY_CARD("ENTRY_CARD"),

    VIDEO_HOST_INCREMENT_PER_LAC("10"),
    AUDIO_HOST_INCREMENT_PER_LAC("7"),
    DIVIDER("100000"),
    ;
    private final String value;

    Constants(String value) {
        this.value = value;
    }
}

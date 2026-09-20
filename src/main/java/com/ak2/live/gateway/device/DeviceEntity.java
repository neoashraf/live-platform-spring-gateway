package com.ak2.live.gateway.device;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Document(collection = "device")
public class DeviceEntity implements Persistable<String> {
    @Id
    private String id;
    private String deviceId;
    private String deviceType;
    private String deviceName;
    private String active;
    private String remarks;
    private String createdBy;
    private String updatedBy;
    private Instant createdOn;
    private Instant updatedOn;
    private Instant lastBannedOn;
    private String lastBannedBy;
    private Instant lastUnbannedOn;
    private String lastUnbannedBy;

    private String status;


    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public boolean isNew() {
        boolean isNull = Objects.isNull(this.id);
        this.id = isNull ? UUID.randomUUID().toString() : this.id;
        return isNull;
    }

}

package com.ak2.live.gateway.permission.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Permission {
    private String method;
    private String url;
    private String permissionName;

}

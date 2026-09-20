package com.ak2.live.gateway.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomPrincipal {
    public String mfiId;
    public String instituteOid;
}

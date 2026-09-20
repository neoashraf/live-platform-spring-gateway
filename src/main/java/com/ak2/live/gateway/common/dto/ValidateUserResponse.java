package com.ak2.live.gateway.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidateUserResponse {
    //    List<Authority> authorities;
//    Authority userAuthentication;
//    private String principal;
    @JsonProperty("name")
    private String name;

    @JsonProperty("preferred_username")
    private String preferredUsername;

    @JsonProperty("sub")
    private String sub;

    @JsonProperty("email")
    private String email;

    @JsonProperty("azp")
    private String azp;

    @JsonProperty("typ")
    private String typ;

    @JsonProperty("sid")
    private String sid;
 }

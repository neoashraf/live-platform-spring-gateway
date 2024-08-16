package net.celloscope.com.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidateUserResponse {
    List<Authority> authorities;
//    Authority userAuthentication;
//    private String principal;
    private String name;
    public CustomPrincipal principal;
 }

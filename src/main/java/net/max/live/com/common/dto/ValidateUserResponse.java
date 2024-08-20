package net.max.live.com.common.dto;

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
    private String name;
    private String preferred_username;
    public CustomPrincipal principal;

    public String sub;
    public String email;
 }

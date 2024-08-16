package net.max.live.com.common.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
@Setter
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//@Entity
@ToString
//@Table(name="login")
public class CurrentUser extends User {

    //This constructor is a must
    public CurrentUser(String username, String password, boolean enabled, boolean accountNonExpired,
                       boolean credentialsNonExpired, boolean accountNonLocked,
                       Collection<? extends GrantedAuthority> authorities, String mfiId, String instituteOid) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.instituteOid= instituteOid;
        this.mfiId= mfiId;
    }
    //Setter and getters are required
    private String mfiId="Tuhin";
    private String instituteOid="Mohsem";

}
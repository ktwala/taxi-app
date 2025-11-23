package com.taxiservice.security;

import com.taxiservice.entity.User;
import com.taxiservice.entity.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Data
@AllArgsConstructor
public class CustomUserDetails implements UserDetails {

    private Long userId;
    private String username;
    private String password;
    private String email;
    private String fullName;
    private Boolean active;
    private UserRole userRole;

    public static CustomUserDetails build(User user) {
        return new CustomUserDetails(
                user.getUserId(),
                user.getUsername(),
                user.getPasswordHash(),
                user.getContactEmail(),
                user.getFullName(),
                user.getActive(),
                user.getUserRole()
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (userRole != null && userRole.getRoleName() != null) {
            // Prefix role name with "ROLE_" as per Spring Security convention
            return Collections.singletonList(
                    new SimpleGrantedAuthority("ROLE_" + userRole.getRoleName())
            );
        }
        return Collections.emptyList();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return active != null && active;
    }

    public String getRoleName() {
        return userRole != null ? userRole.getRoleName() : null;
    }
}

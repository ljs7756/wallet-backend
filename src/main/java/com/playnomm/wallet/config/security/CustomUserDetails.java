package com.playnomm.wallet.config.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * packageName :  com.playnomm.wallet.config.security
 * fileName : CustomUserDetails
 * author :  ljs77
 * date : 2022-11-28
 * description :
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2022-11-28                ljs77             최초 생성
 */
@Getter
public class CustomUserDetails implements UserDetails {
    private String id;

    private Integer userSn;
    private String email;

    private Integer userCmmnSn;

    private Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(Integer userSn, Integer userCmmnSn) {
        this.userSn = userSn;
        this.userCmmnSn = userCmmnSn;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return null;
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
        return true;
    }


}

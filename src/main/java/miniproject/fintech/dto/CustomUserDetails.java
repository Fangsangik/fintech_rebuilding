package miniproject.fintech.dto;

import miniproject.fintech.domain.Admin;
import miniproject.fintech.domain.BankMember;
import org.apache.catalina.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    private final BankMember bankMember;
    private final Admin admin;

    // BankMember와 Admin 중 하나만 필요에 따라 설정 가능
    public CustomUserDetails(BankMember bankMember, Admin admin) {
        this.bankMember = bankMember;
        this.admin = admin;
    }

    //role에대한 권한 설정
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (bankMember != null) {
            return List.of(new SimpleGrantedAuthority("ROLE_" + bankMember.getRoles()));
        } else if (admin != null) {
            return List.of(new SimpleGrantedAuthority("ROLE_" + admin.getRoles()));
        }
        return List.of();
    }

    @Override
    public String getPassword() {
        if (bankMember != null) {
            return bankMember.getPassword();
        } else if (admin != null) {
            return admin.getPassword();
        }
        return null;
    }

    @Override
    public String getUsername() {
        if (bankMember != null) {
            return bankMember.getUserId();
        } else if (admin != null) {
            return admin.getAdminId();
        }
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
        if (bankMember != null) {
            return bankMember.isActive();
        } else if (admin != null) {
            return true;  // Admin은 활성화 상태가 있다고 가정
        }
        return false;
    }
}

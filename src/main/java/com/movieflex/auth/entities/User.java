package com.movieflex.auth.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;

    @NotBlank(message = "The field name can't be blank")
    private String name;

    @NotBlank(message = "The field username can't be blank")
    @Column(unique = true)
    private String username;

    @NotBlank(message = "The field email can't be blank")
    @Column(unique = true)
    @Email(message = "Please enter email in proper format!")
    private String email;

    @NotBlank(message = "The field password can't be blank")
    @Size(min = 4, message = "The password must have at least 5 characters")
    private String password;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @OneToOne(mappedBy = "user")
    private RefreshToken refereshToken;

    @OneToOne(mappedBy = "user")
    private ForgotPassword forgotPassword;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
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

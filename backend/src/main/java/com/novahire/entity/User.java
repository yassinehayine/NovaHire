package com.novahire.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users", uniqueConstraints = {
    @UniqueConstraint(columnNames = "email")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 2, max = 50)
    @Column(nullable = false, length = 50)
    private String firstName;

    @NotBlank
    @Size(min = 2, max = 50)
    @Column(nullable = false, length = 50)
    private String lastName;

    @NotBlank
    @Email
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @NotBlank
    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private Role role = Role.USER;

    // ── Sprint 2 additions ─────────────────────────────────────────────────────

    /** e.g. "Software Engineer", "Data Scientist", "Product Manager" */
    @Column(length = 100)
    private String targetRole;

    /** e.g. "JUNIOR", "MID", "SENIOR", "LEAD" */
    @Column(length = 20)
    private String experienceLevel;

    /** Years of total professional experience */
    @Column
    private Integer yearsOfExperience;

    /** e.g. "ENGLISH", "FRENCH", "GERMAN" */
    @Column(length = 20)
    @Builder.Default
    private String preferredLanguage = "ENGLISH";

    /** e.g. "GERMANY", "FRANCE", "CANADA", "MOROCCO" */
    @Column(length = 50)
    private String targetCountry;

    /**
     * Avatar stored as Base64 data-URL.
     * Sprint 2: upload-and-store-in-DB approach (simple, no file-server needed).
     * Sprint 7 can migrate to S3/object-storage if needed.
     */
    @Column(columnDefinition = "TEXT")
    private String avatarBase64;

    /** Raw CV text (extracted from PDF upload) — used by AI in Sprint 3+ */
    @Column(columnDefinition = "TEXT")
    private String cvText;

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // --- UserDetails implementation ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }

    public enum Role {
        USER, ADMIN
    }
}

package com.vaultspring.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@EntityListeners(AuditingEntityListener.class)
public class User implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    private Long id;

    @NotBlank
    @Column(name = "user_name", nullable = false)
    private String name;

    @Email
    @NotBlank
    @Column(name = "user_email", nullable = false, unique = true)
    private String email;

    @Setter
    @Getter(AccessLevel.PACKAGE)
    @NotBlank
    @Column(name = "user_password", nullable = false)
    private String password;

    @CreatedDate
    @Column(name = "user_created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "user_updated_at")
    private LocalDateTime updatedAt;
}

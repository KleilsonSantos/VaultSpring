package com.vaultspring.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.EqualsAndHashCode;
import lombok.AccessLevel;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Represents a User entity mapped to the "users" table in the database.
 * Includes fields for user details and auditing information.
 */
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

    /**
     * The unique identifier for the User entity.
     * Generated automatically using the IDENTITY strategy.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    private Long id;

    /**
     * The name of the user.
     * Cannot be blank and is mapped to the "user_name" column.
     */
    @NotBlank
    @Column(name = "user_name", nullable = false)
    private String name;

    /**
     * The email of the user.
     * Must be a valid email format, cannot be blank, and is unique.
     * Mapped to the "user_email" column.
     */
    @Email
    @NotBlank
    @Column(name = "user_email", nullable = false, unique = true)
    private String email;

    /**
     * The password of the user.
     * Cannot be blank and is mapped to the "user_password" column.
     * Getter has package-level access for security purposes.
     */
    @Setter
    @Getter(AccessLevel.PACKAGE)
    @NotBlank
    @Column(name = "user_password", nullable = false)
    private String password;

    /**
     * The timestamp when the user was created.
     * Automatically populated and cannot be updated.
     * Mapped to the "user_created_at" column.
     */
    @CreatedDate
    @Column(name = "user_created_at", updatable = false)
    private LocalDateTime created_at;

    /**
     * The timestamp when the user was last updated.
     * Automatically populated during updates.
     * Mapped to the "user_updated_at" column.
     */
    @LastModifiedDate
    @Column(name = "user_updated_at")
    private LocalDateTime updated_at;
}

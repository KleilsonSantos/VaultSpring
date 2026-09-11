package io.github.kleilsonsantos.security.vaultspring.entity;

/**
 * Application roles for JWT-backed authorization.
 */
public enum UserRole {

    /**
     * Standard user — self profile only.
     */
    USER,

    /**
     * Administrator — list and create users.
     */
    ADMIN;

    /**
     * @return Spring Security authority (e.g. {@code ROLE_ADMIN})
     */
    public String toAuthority() {
        return "ROLE_" + name();
    }
}

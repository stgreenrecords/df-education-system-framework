package com.darkfactory.education.identityaccess.auth;

import java.util.EnumSet;
import java.util.Locale;
import java.util.Set;

public enum IdentityRoleCode {
    COUNTRY_ADMIN,
    REGION_ADMIN,
    CITY_ADMIN,
    INSTITUTION_ADMIN,
    TEACHER,
    STUDENT,
    PARENT;

    public static IdentityRoleCode parse(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Role code is required.");
        }

        try {
            return IdentityRoleCode.valueOf(value.trim().toUpperCase(Locale.ROOT).replace('-', '_'));
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Unsupported role code: " + value, exception);
        }
    }

    public String apiValue() {
        return name().toLowerCase(Locale.ROOT).replace('_', '-');
    }

    public Set<IdentityPermission> permissions() {
        return switch (this) {
            case COUNTRY_ADMIN -> EnumSet.of(
                    IdentityPermission.ASSIGN_ROLE,
                    IdentityPermission.MANAGE_INSTITUTION,
                    IdentityPermission.VIEW_INSTITUTION,
                    IdentityPermission.VIEW_STUDENT
            );
            case REGION_ADMIN, CITY_ADMIN, INSTITUTION_ADMIN -> EnumSet.of(
                    IdentityPermission.MANAGE_INSTITUTION,
                    IdentityPermission.VIEW_INSTITUTION,
                    IdentityPermission.VIEW_STUDENT
            );
            case TEACHER -> EnumSet.of(
                    IdentityPermission.VIEW_INSTITUTION,
                    IdentityPermission.VIEW_STUDENT
            );
            case STUDENT, PARENT -> EnumSet.of(IdentityPermission.VIEW_STUDENT);
        };
    }
}


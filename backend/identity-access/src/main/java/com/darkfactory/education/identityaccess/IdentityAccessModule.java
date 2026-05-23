package com.darkfactory.education.identityaccess;

import com.darkfactory.education.common.CommonModule;

public final class IdentityAccessModule {

    public static final String NAME = "identity-access";

    private IdentityAccessModule() {
    }

    public static String dependsOn() {
        return CommonModule.NAME;
    }
}

package com.darkfactory.education.organization;

import com.darkfactory.education.common.CommonModule;

public final class OrganizationModule {

    public static final String NAME = "organization";

    private OrganizationModule() {
    }

    public static String dependsOn() {
        return CommonModule.NAME;
    }
}

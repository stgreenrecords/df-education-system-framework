package com.darkfactory.education.schoolpack;

import com.darkfactory.education.common.CommonModule;

public final class SchoolPackModule {

    public static final String NAME = "school-pack";

    private SchoolPackModule() {
    }

    public static String dependsOn() {
        return CommonModule.NAME;
    }
}

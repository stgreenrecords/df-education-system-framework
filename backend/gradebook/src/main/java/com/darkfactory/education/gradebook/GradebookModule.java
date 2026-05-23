package com.darkfactory.education.gradebook;

import com.darkfactory.education.common.CommonModule;

public final class GradebookModule {

    public static final String NAME = "gradebook";

    private GradebookModule() {
    }

    public static String dependsOn() {
        return CommonModule.NAME;
    }
}

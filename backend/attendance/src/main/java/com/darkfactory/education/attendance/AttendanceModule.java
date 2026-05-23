package com.darkfactory.education.attendance;

import com.darkfactory.education.common.CommonModule;

public final class AttendanceModule {

    public static final String NAME = "attendance";

    private AttendanceModule() {
    }

    public static String dependsOn() {
        return CommonModule.NAME;
    }
}

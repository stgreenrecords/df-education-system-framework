package com.darkfactory.education.mealcatering;

import com.darkfactory.education.common.CommonModule;

public final class MealCateringModule {

    public static final String NAME = "meal-catering";

    private MealCateringModule() {
    }

    public static String dependsOn() {
        return CommonModule.NAME;
    }
}

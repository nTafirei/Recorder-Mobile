package com.marotech.recording.gson;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.marotech.recording.model.GsonExcludeField;

public class CustomExclusionStrategy implements ExclusionStrategy {
    public CustomExclusionStrategy() {
    }

    public boolean shouldSkipField(FieldAttributes fieldAttributes) {
        return fieldAttributes.getAnnotation(GsonExcludeField.class) != null;
    }

    public boolean shouldSkipClass(Class<?> clazz) {
        return false;
    }
}
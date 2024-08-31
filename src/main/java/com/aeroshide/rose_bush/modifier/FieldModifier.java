package com.aeroshide.rose_bush.modifier;

import com.aeroshide.rose_bush.api.FieldModifierService;

import java.lang.reflect.Field;

public class FieldModifier implements FieldModifierService {
    @Override
    public void modifyFinalField(Object obj, String fieldName, Object newValue) throws Exception {
        Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(obj, newValue);
    }
}

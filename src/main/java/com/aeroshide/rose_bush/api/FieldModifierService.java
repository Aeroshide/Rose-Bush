package com.aeroshide.rose_bush.api;

public interface FieldModifierService {
    void modifyFinalField(Object obj, String fieldName, Object newValue) throws Exception;
}

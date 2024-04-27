package com.aeroshide.rose_bush.api;

public interface VersionService {
    int convertVersionToInt(String version);
    int getVersionAsInt(String modId);
    String convertIntToVersion(int versionInt);
}

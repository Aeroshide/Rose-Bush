package com.aeroshide.rose_bush.api;

public interface HttpService {
    String fetchDataFromUrl(String url);
    String getGistRawUrl(String gistId, String fileToLookFor);
    boolean downloadFile(String url, String localPath);
}

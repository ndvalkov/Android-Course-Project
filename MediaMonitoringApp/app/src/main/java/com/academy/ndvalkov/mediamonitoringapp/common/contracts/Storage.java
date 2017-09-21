package com.academy.ndvalkov.mediamonitoringapp.common.contracts;

public interface Storage {
    String getExternalStorageDirectory();
    String getInternalFilesDirectory();
    void createDirectory(String path);
    void appendFile(String path, String content);
    String readFileContent(String path);
}
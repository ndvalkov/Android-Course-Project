package com.academy.ndvalkov.mediamonitoringapp.common;

import android.content.Context;

import com.snatik.storage.Storage;

import java.io.File;

public class FileUtils {
    private static final String TAG = FileUtils.class.getSimpleName();

    // application public folder
    public static final String USER_MAIN_DIR = "MediaMonitor";
    // application private folder
    public static final String APP_MAIN_DIR = "Config";

    // application config files
    public static final String EMAILS_INPUT_FILENAME = "emails_input";

    private static FileUtils instance;

    private Context mContext;

    private final Storage mStorage;

    private final String EXTERNAL_STORAGE_PATH;
    private final String INTERNAL_STORAGE_PATH;
    private String EXTERNAL_MAIN_PATH;
    private final String INTERNAL_MAIN_PATH;

    public Storage getMainStorage() {
        return mStorage;
    }

    public String getExternalStoragePath() {
        return EXTERNAL_STORAGE_PATH;
    }

    public String getInternalStoragePath() {
        return INTERNAL_STORAGE_PATH;
    }

    public String getExternalMainPath() {
        return EXTERNAL_MAIN_PATH;
    }

    public String getInternalMainPath() {
        return INTERNAL_MAIN_PATH;
    }

    private FileUtils(Context context, Storage storage) {
        mContext = context;
        mStorage = storage;
        EXTERNAL_STORAGE_PATH = storage.getExternalStorageDirectory();
        INTERNAL_STORAGE_PATH = storage.getInternalFilesDirectory();
        EXTERNAL_MAIN_PATH = EXTERNAL_STORAGE_PATH +
                File.pathSeparator +
                USER_MAIN_DIR;
        INTERNAL_MAIN_PATH = INTERNAL_STORAGE_PATH +
                File.pathSeparator +
                APP_MAIN_DIR;

        createApplicationDirectories();
        createApplicationFiles();
    }

    public static FileUtils getInstance(Context context, Storage storage) {
        if (instance == null) {
            instance = new FileUtils(context, storage);
        }
        return instance;
    }

    private void createApplicationDirectories() {
        if (!Storage.isExternalWritable()) {
            EXTERNAL_MAIN_PATH = INTERNAL_STORAGE_PATH + File.pathSeparator + USER_MAIN_DIR;
        }

        mStorage.createDirectory(EXTERNAL_MAIN_PATH);
        mStorage.createDirectory(INTERNAL_MAIN_PATH);
    }

    private void createApplicationFiles() {
        mStorage.createFile(INTERNAL_MAIN_PATH + File.pathSeparator + EMAILS_INPUT_FILENAME, "");
    }

    public void appendToFile(String path, String content) {
        mStorage.appendFile(path, content);
    }

    public String readFileContent(String path) {
        return mStorage.readTextFile(path);
    }
}

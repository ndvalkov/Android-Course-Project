package com.academy.ndvalkov.mediamonitoringapp.data.db;

import com.academy.ndvalkov.mediamonitoringapp.models.MonitoringConfig;
import com.orm.SugarRecord;

import java.util.List;

public class DbProvider {
    private static final DbProvider dbInstance = new DbProvider();

    public static DbProvider getInstance() {
        return dbInstance;
    }

    private DbProvider() {
    }

    public void saveConfig(MonitoringConfig config) {
        SugarRecord.save(config);
    }

    public List<MonitoringConfig> getAllConfigs() {
        return SugarRecord.listAll(MonitoringConfig.class);
    }
}

package com.academy.ndvalkov.mediamonitoringapp.data.db;

import android.content.Context;

import com.academy.ndvalkov.mediamonitoringapp.models.MonitoringConfig;
import com.orm.SchemaGenerator;
import com.orm.SugarContext;
import com.orm.SugarDb;
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

    public void recreateDb(Context context) {
        SugarContext.terminate();
        SchemaGenerator schemaGenerator = new SchemaGenerator(context.getApplicationContext());
        schemaGenerator.deleteTables(new SugarDb(context.getApplicationContext()).getDB());
        SugarContext.init(context.getApplicationContext());
        schemaGenerator.createDatabase(new SugarDb(context.getApplicationContext()).getDB());
    }
}

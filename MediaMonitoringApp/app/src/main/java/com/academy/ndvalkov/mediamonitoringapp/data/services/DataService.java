package com.academy.ndvalkov.mediamonitoringapp.data.services;

import com.academy.ndvalkov.mediamonitoringapp.data.tasks.HttpTask;

public interface DataService<T> {
    void getAll(HttpTask.OnHttpTaskResult<T[]> onHttpTaskResult);
    void getById(Object id, HttpTask.OnHttpTaskResult<T> onHttpTaskResult);
    void add(T obj, HttpTask.OnHttpTaskResult<T> onHttpTaskResult);
}
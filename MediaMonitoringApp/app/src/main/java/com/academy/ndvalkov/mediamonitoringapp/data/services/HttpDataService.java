package com.academy.ndvalkov.mediamonitoringapp.data.services;

import com.academy.ndvalkov.mediamonitoringapp.data.tasks.HttpTask;

public class HttpDataService<T> implements DataService<T> {

    private final String baseUrl;
    private final Class<T> klassSingle;
    private final Class<T[]> klassArray;

    public HttpDataService(String baseUrl, Class<T> klassSingle, Class<T[]> klassArray) {
        this.baseUrl = baseUrl;
        this.klassSingle = klassSingle;
        this.klassArray = klassArray;
    }

    public void getAll(HttpTask.OnHttpTaskResult<T[]> callback) {
        new HttpTask<T[]>(this.klassArray, callback)
                .execute(this.baseUrl);
    }

    public void getById(Object id, HttpTask.OnHttpTaskResult<T> callback) {
        new HttpTask<T>(this.klassSingle, callback)
                .execute(this.baseUrl + "/" + id);
    }

    @Override
    public void add(T obj, HttpTask.OnHttpTaskResult<T> onHttpTaskResult) {

    }
}

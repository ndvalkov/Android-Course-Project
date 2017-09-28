package com.academy.ndvalkov.mediamonitoringapp.data.services;

import com.academy.ndvalkov.mediamonitoringapp.data.tasks.HttpTask;

public class HttpDataService<T> implements DataService<T> {

    private final String baseUrl;
    private final String key;
    private final Class<T> klassSingle;
    private final Class<T[]> klassArray;

    public HttpDataService(String baseUrl, String key, Class<T> klassSingle, Class<T[]> klassArray) {
        this.baseUrl = baseUrl;
        this.key = key;
        this.klassSingle = klassSingle;
        this.klassArray = klassArray;
    }

    public void getAll(HttpTask.OnHttpTaskResult<T[]> callback) {
        new HttpTask<T[]>(this.klassArray, callback)
                .execute(this.baseUrl, this.key);
    }

//    public void getById(Object id, HttpTask.OnHttpTaskResult<T> callback) {
//        new HttpTask<T>(this.klassSingle, callback)
//                .execute(this.baseUrl + "/" + id);
//    }
//
//    @Override
//    public void add(T obj, HttpTask.OnHttpTaskResult<T> onHttpTaskResult) {
//
//    }
}

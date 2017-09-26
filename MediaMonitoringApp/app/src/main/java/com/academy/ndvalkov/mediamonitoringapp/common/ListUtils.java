package com.academy.ndvalkov.mediamonitoringapp.common;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ListUtils {
    public interface Filter<T>{
        boolean keepItem(T item);
    }

    public interface Map<T1, T2>{
        T2 mapItem(T1 item);
    }

    public static <T> void filter(@NonNull List<T> items, @NonNull Filter<T> filter) {
        for (Iterator<T> iterator = items.iterator(); iterator.hasNext();){
            if(!filter.keepItem(iterator.next())){
                iterator.remove();
            }
        }
    }

    public static <T1, T2> List<T2> map(@NonNull List<T1> items, @NonNull Map<T1, T2> map) {
        List<T2> result = new ArrayList<>();
        for (Iterator<T1> iterator = items.iterator(); iterator.hasNext();){
                result.add(map.mapItem(iterator.next()));
        }
        return result;
    }
}

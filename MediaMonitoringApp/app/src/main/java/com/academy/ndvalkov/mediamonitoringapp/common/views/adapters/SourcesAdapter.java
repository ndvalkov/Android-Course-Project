package com.academy.ndvalkov.mediamonitoringapp.common.views.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.academy.ndvalkov.mediamonitoringapp.R;
import com.academy.ndvalkov.mediamonitoringapp.models.NewsSource;

import java.util.ArrayList;

public class SourcesAdapter extends ArrayAdapter<NewsSource> {
    public SourcesAdapter(Context context, ArrayList<NewsSource> sources) {
        super(context, 0, sources);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        NewsSource newsSource = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_news_source, parent, false);
        }

        TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
        TextView tvDescription = (TextView) convertView.findViewById(R.id.tvDescription);
        TextView tvCategory = (TextView) convertView.findViewById(R.id.tvCategory);
        TextView tvUrl = (TextView) convertView.findViewById(R.id.tvUrl);

        tvName.setText(newsSource.getName());
        tvDescription.setText(newsSource.getDescription());
        tvCategory.setText(newsSource.getCategory());
        tvUrl.setText(newsSource.getUrl());

        return convertView;
    }
}

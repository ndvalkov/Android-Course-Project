package com.academy.ndvalkov.mediamonitoringapp.common.views.adapters;

import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.academy.ndvalkov.mediamonitoringapp.R;
import com.academy.ndvalkov.mediamonitoringapp.models.NewsSource;

import java.util.List;

public class SourcesRVAdapter extends
        RecyclerView.Adapter<SourcesRVAdapter.MyViewHolder> {

    private List<NewsSource> sourcesList;

    /**
     * View holder class
     */
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName;
        public TextView tvCategory;
        public TextView tvDescription;
        public TextView tvUrl;

        public MyViewHolder(View view) {
            super(view);
            tvName = (TextView) view.findViewById(R.id.tvName);
            tvCategory = (TextView) view.findViewById(R.id.tvCategory);
            tvDescription = (TextView) view.findViewById(R.id.tvDescription);
            tvUrl = (TextView) view.findViewById(R.id.tvUrl);
        }
    }

    public SourcesRVAdapter(List<NewsSource> countryList) {
        this.sourcesList = countryList;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        NewsSource ns = sourcesList.get(position);
        holder.tvName.setText(ns.getName());
        holder.tvCategory.setText(ns.getCategory());
        holder.tvDescription.setText(ns.getDescription());
        holder.tvUrl.setText(fromHtml(ns.getUrl()));
    }

    @Override
    public int getItemCount() {
        return sourcesList.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_news_source, parent, false);
        return new MyViewHolder(v);
    }

    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String html) {
        Spanned result;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(html);
        }
        return result;
    }
}

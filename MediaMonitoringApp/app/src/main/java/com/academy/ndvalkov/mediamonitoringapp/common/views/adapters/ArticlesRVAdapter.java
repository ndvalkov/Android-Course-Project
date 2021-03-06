package com.academy.ndvalkov.mediamonitoringapp.common.views.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.academy.ndvalkov.mediamonitoringapp.R;
import com.academy.ndvalkov.mediamonitoringapp.models.Article;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ArticlesRVAdapter extends
        RecyclerView.Adapter<ArticlesRVAdapter.MyViewHolder> {

    private List<Article> articlesList;

    /**
     * View holder class
     */
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvTitle;
        public TextView tvAuthor;
        public TextView tvDescription;
        public TextView tvUrl;
        public TextView tvDate;
        public ImageView ivFeatured;
        public ProgressBar progress;

        public MyViewHolder(View view) {
            super(view);
            tvTitle = (TextView) view.findViewById(R.id.tvTitle);
            tvAuthor = (TextView) view.findViewById(R.id.tvAuthor);
            tvDescription = (TextView) view.findViewById(R.id.tvDescription);
            tvUrl = (TextView) view.findViewById(R.id.tvUrl);
            tvDate = (TextView) view.findViewById(R.id.tvDate);
            ivFeatured = (ImageView) view.findViewById(R.id.ivFeatured);
            progress = (ProgressBar) view.findViewById(R.id.progress);
        }
    }

    public ArticlesRVAdapter(List<Article> countryList) {
        this.articlesList = countryList;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
       final Article article = articlesList.get(position);
        holder.tvTitle.setText(article.getTitle());
        holder.tvAuthor.setText(article.getAuthor());
        holder.tvDescription.setText(article.getDescription());
        holder.tvDate.setText(article.getPublishedAt());
//        String format = "<a href=\"%s\">Link</a>";
//        String htmlString = String.format(format, article.getUrl());
//        holder.tvUrl.setText(fromHtml(htmlString));
        final Context context = holder.tvDate.getContext();

        holder.tvUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = article.getUrl();
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                context.startActivity(i);
            }
        });

        holder.progress.setVisibility(View.VISIBLE);
        Picasso.with(context)
                .load(article.getUrlToImage())
                .error(R.drawable.ic_notify)
                .into(holder.ivFeatured, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        //Success image already loaded into the view
                        holder.progress.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
                        //Error placeholder image already loaded into the view, do further handling of this situation here
                    }
                });
    }

    @Override
    public int getItemCount() {
        return articlesList.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_article, parent, false);
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


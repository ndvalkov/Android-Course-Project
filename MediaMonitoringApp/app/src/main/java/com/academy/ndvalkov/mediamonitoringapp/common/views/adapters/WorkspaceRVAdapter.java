package com.academy.ndvalkov.mediamonitoringapp.common.views.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.academy.ndvalkov.mediamonitoringapp.R;
import com.academy.ndvalkov.mediamonitoringapp.models.Article;

import java.util.List;

public class WorkspaceRVAdapter extends
        RecyclerView.Adapter<WorkspaceRVAdapter.MyViewHolder> {

    private List<Article> articlesList;

    /**
     * View holder class
     */
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvTitle;
        public TextView tvDescription;

        public MyViewHolder(View view) {
            super(view);
            tvTitle = (TextView) view.findViewById(R.id.tvTitle);
            tvDescription = (TextView) view.findViewById(R.id.tvDescription);
        }
    }

    public WorkspaceRVAdapter(List<Article> countryList) {
        this.articlesList = countryList;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final Article article = articlesList.get(position);
        holder.tvTitle.setText(article.getTitle());
        holder.tvDescription.setText(article.getDescription());
    }

    @Override
    public int getItemCount() {
        return articlesList.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_workspace, parent, false);
        return new MyViewHolder(v);
    }
}


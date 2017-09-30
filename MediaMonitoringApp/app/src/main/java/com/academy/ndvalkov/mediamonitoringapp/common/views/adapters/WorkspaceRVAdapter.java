package com.academy.ndvalkov.mediamonitoringapp.common.views.adapters;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ReplacementSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.academy.ndvalkov.mediamonitoringapp.R;
import com.academy.ndvalkov.mediamonitoringapp.models.Article;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WorkspaceRVAdapter extends
        RecyclerView.Adapter<WorkspaceRVAdapter.MyViewHolder> {

    private List<Article> articlesList;
    private Set<String> primaryKeywords = new HashSet<>();
    private Set<String> secondaryKeywords = new HashSet<>();
    private boolean isProcessed;

    public List<Article> getArticlesList() {
        return articlesList;
    }

    public void setArticlesList(List<Article> articlesList) {
        this.articlesList = articlesList;
    }

    public Set<String> getPrimaryKeywords() {
        return primaryKeywords;
    }

    public void setPrimaryKeywords(Set<String> primaryKeywords) {
        this.primaryKeywords = primaryKeywords;
    }

    public Set<String> getSecondaryKeywords() {
        return secondaryKeywords;
    }

    public void setSecondaryKeywords(Set<String> secondaryKeywords) {
        this.secondaryKeywords = secondaryKeywords;
    }

    public boolean isProcessed() {
        return isProcessed;
    }

    public void setProcessed(boolean processed) {
        isProcessed = processed;
    }

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
        Context context = holder.tvDescription.getContext();

        if (isProcessed) {
            String title = article.getTitle();
            SpannableString sTitle = new SpannableString(title);
            for (String word: primaryKeywords) {
                String lower = word.toLowerCase();
                String upperFirst = capitalizeFirstLetter(lower);

                decorateSpannableKeyword(sTitle, lower, context);
                decorateSpannableKeyword(sTitle, upperFirst, context);
            }

            holder.tvTitle.setMovementMethod(LinkMovementMethod.getInstance());
            holder.tvTitle.setText(sTitle);





        } else {
            holder.tvTitle.setText(article.getTitle());
            holder.tvDescription.setText(article.getDescription());
        }
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

    private SpannableString decorateSpannableKeyword(SpannableString target, final String keyword, final Context context) {
        String str = target.toString();
        int index = str.indexOf(keyword);
        while(index >= 0) {
            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(View textView) {
                    Toast.makeText(context, keyword, Toast.LENGTH_SHORT).show();
                }
            };


            target.setSpan(clickableSpan, index, index + keyword.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            target.setSpan(new RoundedBackgroundSpan(context), index, index + keyword.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            index = str.indexOf(keyword, index + 1);
        }

        return target;
    }

    private String capitalizeFirstLetter(String str) {
        return str.substring(0,1).toUpperCase() + str.substring(1).toLowerCase();
    }

    public static class RoundedBackgroundSpan extends ReplacementSpan {

        private static int CORNER_RADIUS = 8;
        private int backgroundColor = 0;
        private int textColor = 0;

        public RoundedBackgroundSpan(Context context) {
            super();
            backgroundColor = context.getResources().getColor(R.color.orange);
            textColor = context.getResources().getColor(R.color.white);
        }

        @Override
        public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
            RectF rect = new RectF(x, top, x + measureText(paint, text, start, end), bottom);
            paint.setColor(backgroundColor);
            canvas.drawRoundRect(rect, CORNER_RADIUS, CORNER_RADIUS, paint);
            paint.setColor(textColor);
            canvas.drawText(text, start, end, x, y, paint);
        }

        @Override
        public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
            return Math.round(paint.measureText(text, start, end));
        }

        private float measureText(Paint paint, CharSequence text, int start, int end) {
            return paint.measureText(text, start, end);
        }
    }
}


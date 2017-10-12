package com.academy.ndvalkov.mediamonitoringapp.common.views.adapters;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ReplacementSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.academy.ndvalkov.mediamonitoringapp.R;
import com.academy.ndvalkov.mediamonitoringapp.models.Article;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class WorkspaceRVAdapter extends
        RecyclerView.Adapter<WorkspaceRVAdapter.MyViewHolder> {

    private List<Article> articlesList;
    private Set<String> primaryKeywords = new HashSet<>();
    private Set<String> secondaryKeywords = new HashSet<>();
    private Map<String, HashMap<String, Integer>> primaryResults = new HashMap<>();
    private Map<String, HashMap<String, Integer>> secondaryResults = new HashMap<>();
    private boolean isProcessed;
    private boolean isDecorated;

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

    public Map<String, HashMap<String, Integer>> getPrimaryResults() {
        return new HashMap<>(primaryResults);
    }

    public Map<String, HashMap<String, Integer>> getSecondaryResults() {
        return new HashMap<>(secondaryResults);
    }

    public boolean isProcessed() {
        return isProcessed;
    }

    public void startProcessing() {
        isProcessed = false;
        isDecorated = true;
    }

    public void clearResults() {
        primaryResults.clear();
        secondaryResults.clear();
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

        if (isDecorated) {
            decorateTitleView(holder, article, context);
            decorateDescriptionView(holder, article, context);
        } else {
            holder.tvTitle.setText(article.getTitle());
            holder.tvDescription.setText(article.getDescription());
        }

        if (position == this.articlesList.size() - 1) {
            isProcessed = true;
        }

        Log.d("RV", "Processed");
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

    private SpannableString processKeyword(final SpannableString target,
                                           final String keyword,
                                           final Context context,
                                           final int color,
                                           String title,
                                           boolean isPrimary) {
        String str = target.toString();
        int index = str.indexOf(keyword);
        final int closureIndex = index;
        while (index >= 0) {
            decorateSpannableWordAtPosition(target,
                    keyword,
                    context,
                    color,
                    index,
                    closureIndex,
                    title,
                    isPrimary);
            generateResults(title, keyword, isPrimary);
            index = str.indexOf(keyword, index + 1);
        }

        return target;
    }

    private void generateResults(String title, String keyword, boolean isPrimary) {
        if (!isProcessed) {
            if (isPrimary) {
                addToResultMap(title, keyword, primaryResults);
            } else {
                addToResultMap(title, keyword, secondaryResults);
            }
        }
    }

    private void addToResultMap(String title, String keyword, Map<String, HashMap<String, Integer>> resultMap) {
        if (!resultMap.containsKey(title)) {
            resultMap.put(title, new HashMap<String, Integer>());
        }

        HashMap<String, Integer> keywordOccurrences = resultMap.get(title);
        String keyToLower = keyword.toLowerCase();
        if (!keywordOccurrences.containsKey(keyToLower)) {
            keywordOccurrences.put(keyToLower, 1);
        } else {
            keywordOccurrences.put(keyToLower, keywordOccurrences.get(keyToLower) + 1);
        }
    }

    private void updateResultMap(String title, String keyword, Map<String, HashMap<String, Integer>> resultMap) {
        int occurrences = resultMap.get(title).get(keyword.toLowerCase());
        resultMap.get(title).put(keyword.toLowerCase(), --occurrences);
    }

    private void decorateSpannableWordAtPosition(final SpannableString target,
                                                 final String keyword,
                                                 final Context context,
                                                 int color,
                                                 int index,
                                                 final int closureIndex,
                                                 final String title,
                                                 final boolean isPrimary) {
        final ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                if (isPrimary) {
                    updateResultMap(title, keyword, primaryResults);
                } else {
                    updateResultMap(title, keyword, secondaryResults);
                }

                // keyword skipped, remove span
                target.removeSpan(this);

                showSkipPopup(textView, context, keyword);
                target.setSpan(new RoundedBackgroundSpan(context,
                                ContextCompat.getColor(context, R.color.red)),
                        closureIndex,
                        closureIndex + keyword.length(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                ((TextView) textView).setText(target);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
            }
        };

        target.setSpan(clickableSpan, index, index + keyword.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        target.setSpan(new RoundedBackgroundSpan(context, color),
                index,
                index + keyword.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private void showSkipPopup(View parent, Context context, String keyword) {
        final PopupWindow popupWindow = new PopupWindow(context);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupContent = inflater.inflate(R.layout.popup, null);

        TextView tvSkipped = (TextView) popupContent.findViewById(R.id.tvSkipped);
        tvSkipped.setText(String.format("Keyword \"%s\" skipped", keyword));

        popupWindow.setContentView(popupContent);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(ContextCompat.getDrawable(context,
                android.R.drawable.checkbox_off_background));
        popupWindow.setAnimationStyle(android.R.style.Animation_Translucent);
        if (Build.VERSION.SDK_INT >= 21) {
            popupWindow.setElevation(5.0f);
        }

        popupWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                popupWindow.dismiss();
            }
        }, 1000);
    }

    private void decorateDescriptionView(MyViewHolder holder, Article article, Context context) {
        String description = article.getDescription();
        SpannableString sDescription = new SpannableString(description);

        processPrimary(context, sDescription, article.getTitle());
        processSecondary(context, sDescription, article.getTitle());

        holder.tvDescription.setMovementMethod(LinkMovementMethod.getInstance());
        holder.tvDescription.setText(sDescription);
    }

    private void decorateTitleView(MyViewHolder holder, Article article, Context context) {
        String title = article.getTitle();
        SpannableString spanTitle = new SpannableString(title);

        processPrimary(context, spanTitle, title);
        processSecondary(context, spanTitle, title);

        holder.tvTitle.setMovementMethod(LinkMovementMethod.getInstance());
        holder.tvTitle.setText(spanTitle);
    }

    private void processPrimary(Context context, SpannableString target, String title) {
        for (String word : primaryKeywords) {
            String lower = word.toLowerCase();
            String upperFirst = capitalizeFirstLetter(lower);

            processKeyword(target, lower, context, ContextCompat.getColor(context, R.color.orange),
                    title, true);
            processKeyword(target, upperFirst, context, ContextCompat.getColor(context, R.color.orange),
                    title, true);
        }
    }

    private void processSecondary(Context context, SpannableString target, String title) {
        for (String word : secondaryKeywords) {
            String lower = word.toLowerCase();
            String upperFirst = capitalizeFirstLetter(lower);

            processKeyword(target, lower, context, ContextCompat.getColor(context, R.color.green),
                    title, false);
            processKeyword(target, upperFirst, context, ContextCompat.getColor(context, R.color.green),
                    title, false);
        }
    }

    private String capitalizeFirstLetter(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    public static class RoundedBackgroundSpan extends ReplacementSpan {

        private static int CORNER_RADIUS = 8;
        private int backgroundColor = 0;
        private int textColor = 0;

        public RoundedBackgroundSpan(Context context, int backgroundColor) {
            super();
            this.backgroundColor = backgroundColor;
            this.textColor = context.getResources().getColor(R.color.white);
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


package com.academy.ndvalkov.mediamonitoringapp.common.views.adapters;

import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.TextView;

import com.academy.ndvalkov.mediamonitoringapp.R;
import com.academy.ndvalkov.mediamonitoringapp.models.NewsSource;

import java.util.List;

public class SourcesRVAdapter extends
        RecyclerView.Adapter<SourcesRVAdapter.MyViewHolder> {

    private List<NewsSource> sourcesList;
    private int selected_position = -1;

    public int getSelectedPosition() {
        return selected_position;
    }

    /**
     * View holder class
     */
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName;
        public TextView tvCategory;
        public TextView tvDescription;
        public TextView tvUrl;
        public Button btnSelect;

        public MyViewHolder(View view) {
            super(view);
            tvName = (TextView) view.findViewById(R.id.tvName);
            tvCategory = (TextView) view.findViewById(R.id.tvCategory);
            tvDescription = (TextView) view.findViewById(R.id.tvDescription);
            tvUrl = (TextView) view.findViewById(R.id.tvUrl);
            btnSelect = (Button) view.findViewById(R.id.btnSelect);
        }
    }

    public SourcesRVAdapter(List<NewsSource> countryList) {
        this.sourcesList = countryList;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
       // holder.itemView.setBackgroundColor(selected_position == position ? Color.GREEN : Color.TRANSPARENT);

        if (selected_position == position) {
            ScaleAnimation anim = new ScaleAnimation(0.0f,
                    1.0f,
                    0.0f,
                    1.0f,
                    Animation.RELATIVE_TO_SELF,
                    0.5f,
                    Animation.RELATIVE_TO_SELF,
                    0.5f);
            anim.setDuration(500);
            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    holder.itemView.setBackgroundTintMode(PorterDuff.Mode.SRC);
                    holder.itemView.setBackgroundTintList(ContextCompat
                            .getColorStateList(holder.itemView.getContext(), R.color.secondaryLightColor));
//                    holder.tvCategory.setTextColor(ContextCompat.getColor(holder.itemView.getContext(),
//                            R.color.white));
//                    holder.tvDescription.setTextColor(ContextCompat.getColor(holder.itemView.getContext(),
//                            R.color.white));
//                    holder.tvUrl.setTextColor(ContextCompat.getColor(holder.itemView.getContext(),
//                            R.color.green));
                    holder.btnSelect.setText("SELECTED");
                    holder.btnSelect.setEnabled(false);
                    holder.tvCategory.setVisibility(View.INVISIBLE);
                    holder.tvDescription.setVisibility(View.INVISIBLE);
                    holder.tvUrl.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
//                    holder.itemView.setBackgroundTintList(ContextCompat
//                            .getColorStateList(holder.itemView.getContext(), R.color.secondaryLightColor));
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            holder.itemView.startAnimation(anim);
        } else {
            holder.itemView.setBackgroundTintList(ContextCompat
                    .getColorStateList(holder.itemView.getContext(), android.R.color.transparent));
            holder.itemView.setBackgroundTintMode(PorterDuff.Mode.CLEAR);
//            holder.tvCategory.setTextColor(ContextCompat.getColor(holder.itemView.getContext(),
//                    R.color.common_google_signin_btn_text_light));
//            holder.tvDescription.setTextColor(ContextCompat.getColor(holder.itemView.getContext(),
//                    R.color.common_google_signin_btn_text_light));
//            holder.tvUrl.setTextColor(ContextCompat.getColor(holder.itemView.getContext(),
//                    R.color.common_google_signin_btn_text_light));
            holder.btnSelect.setText("SELECT");
            holder.btnSelect.setEnabled(true);
            holder.tvCategory.setVisibility(View.VISIBLE);
            holder.tvDescription.setVisibility(View.VISIBLE);
            holder.tvUrl.setVisibility(View.VISIBLE);
        }

        NewsSource ns = sourcesList.get(position);
        holder.tvName.setText(ns.getName());
        holder.tvCategory.setText(ns.getCategory());
        holder.tvDescription.setText(ns.getDescription());
        holder.tvUrl.setText(fromHtml(ns.getUrl()));

        holder.btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.getAdapterPosition() == RecyclerView.NO_POSITION) return;
                notifyItemChanged(selected_position);
                selected_position = holder.getAdapterPosition();
                notifyItemChanged(selected_position);
            }
        });
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

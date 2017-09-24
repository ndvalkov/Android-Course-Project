package com.academy.ndvalkov.mediamonitoringapp.common;

/**
 * Utility class for the creation of different types of Dialogs.
 * Sample usage --> (apply Otto annotations to pass data between the Dialog and Activity/worker threads):
 * -> {@code
 * DialogFactory.DialogParams dlgParams = new DialogFactory.DialogParams();
 * dlgParams.setTitle("Information")
 * .setIcon(getResources().getDrawable(R.drawable.fire_smoke))
 * .setContent("Some content")
 * .setIgnoreButton(true)
 * .setCancelButton(true);
 * final Dialog dlg = DialogFactory.getInstance(this).createDialog(dlgParams);
 * dlg.findViewById(R.id.okButton).setOnClickListener(new View.OnClickListener() {
 *
 * @Override
 * public void onClick(View v) {
 * dlg.dismiss();
 * }
 * });
 * } <-
 */

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.academy.ndvalkov.mediamonitoringapp.R;

public class DialogFactory {
    private static final String TAG = DialogFactory.class.getSimpleName();
    private Context mContext;

    public DialogFactory(Context context) {
        mContext = context;
    }

    public void init(Context context) {
        mContext = context;
    }

    public Dialog createDialog(DialogParams params) {
        if (mContext == null) {
            return null;
        }
        AlertDialog.Builder b = new AlertDialog.Builder(mContext);

        /**
         * Inflate the custom title from xml.
         */
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View titleView = inflater.inflate(R.layout.dialog_title, null);

        /**
         * Set Dialog icon.
         */
        ImageView iconView = (ImageView) titleView.findViewById(R.id.dialogIcon);
        Drawable icon = params.getIcon();
        if (icon != null) {
            iconView.setImageDrawable(icon);
        }

        /**
         * Set Dialog title.
         */
        TextView titleText = (TextView) titleView.findViewById(R.id.dialogTitle);
        String title = params.getTitle();
        if (title != null) {
            titleText.setText(title);
        }

        // add the title View to the builder
        b.setCustomTitle(titleView);

        /**
         * Inflate the custom content, with new action Buttons and message TextView.
         */
        View contentView = inflater.inflate(R.layout.dialog_content, null);

        /**
         * Set the Dialog message.
         */
        TextView messageView = (TextView) contentView.findViewById(R.id.dialogContent);

        String content = params.getContent();
        if (content == null || TextUtils.isEmpty(content)) {
            messageView.setVisibility(View.GONE);
        } else {
            messageView.setText(content);
        }

        // if a Widget/View is present among the parameters, replace the msg TextView with it
        View contentWidget = params.getContentWidget();
        if (contentWidget != null) {
            RelativeLayout contView = ((RelativeLayout) contentView);
            int msgId = messageView.getId();
            contView.removeView(messageView);
            contentWidget.setId(msgId);
            contView.addView(contentWidget, 0);
        }

        // add the content View container to the builder
        b.setView(contentView);

        final AlertDialog ad = b.create();

        ad.show();

        if (params.hasOKButton()) {
            Button okButton = (Button) ad.findViewById(R.id.okButton);
            assert okButton != null;
            okButton.setVisibility(View.VISIBLE);
        }
        if (params.hasCancelButton()) {
            Button cancelButton = (Button) ad.findViewById(R.id.cancelButton);
            assert cancelButton != null;
            cancelButton.setVisibility(View.VISIBLE);
        }

        if (params.hasYesButton()) {
            Button yesButton = (Button) ad.findViewById(R.id.yesButton);
            assert yesButton != null;
            yesButton.setVisibility(View.VISIBLE);
        }
        if (params.hasNoButton()) {
            Button noButton = (Button) ad.findViewById(R.id.noButton);
            assert noButton != null;
            noButton.setVisibility(View.VISIBLE);
        }

        /**
         * Returns a reference to the Dialog, that needs to be stored in whichever
         * object will pass a Listener callback.
         */
        return ad;
    }


    public static class DialogParams {
        private String title;
        private String content;
        private View contentWidget;
        private Drawable icon;
        private boolean hasOKButton;
        private boolean hasCancelButton;
        private boolean hasYesButton;
        private boolean hasNoButton;

        public String getTitle() {
            return title;
        }

        public DialogParams setTitle(String title) {
            this.title = title;
            return this;
        }

        public String getContent() {
            return content;
        }

        public DialogParams setContent(String content) {
            this.content = content;
            return this;
        }

        public View getContentWidget() {
            return contentWidget;
        }

        public DialogParams setContentWidget(View contentWidget) {
            this.contentWidget = contentWidget;
            return this;
        }

        public Drawable getIcon() {
            return icon;
        }

        public DialogParams setIcon(Drawable icon) {
            this.icon = icon;
            return this;
        }

        public boolean hasOKButton() {
            return hasOKButton;
        }

        public DialogParams setOKButton(boolean hasOKButton) {
            this.hasOKButton = hasOKButton;
            return this;
        }

        public boolean hasCancelButton() {
            return hasCancelButton;
        }

        public DialogParams setCancelButton(boolean hasCancelButton) {
            this.hasCancelButton = hasCancelButton;
            return this;
        }

        public boolean hasYesButton() {
            return hasYesButton;
        }

        public DialogParams setYesButton(boolean hasYesButton) {
            this.hasYesButton = hasYesButton;
            return this;
        }

        public boolean hasNoButton() {
            return hasNoButton;
        }

        public DialogParams setNoButton(boolean hasNoButton) {
            this.hasNoButton = hasNoButton;
            return this;
        }
    }
}

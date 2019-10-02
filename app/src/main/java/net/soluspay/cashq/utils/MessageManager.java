package net.soluspay.cashq.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

public class MessageManager {
    private TextView msgView;
    private Context  mContext;

    public MessageManager(Context context, TextView textview) {
        msgView = textview;
        mContext = context;
    }

    public void InfoMessage(String msg) {
        InfoMessage(msg, Color.BLACK);
    }

    public void InfoMessage(String msg, int color) {
        msgView.setText(msg, TextView.BufferType.SPANNABLE);
        Spannable style = (Spannable) msgView.getText();
        int start = 0;
        int end = start + msg.length();
        ForegroundColorSpan colors = new ForegroundColorSpan(color);
        style.setSpan(colors, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    public void AppendInfoMessage(String msg, int color) {
        int start = msgView.getText().length();
        msgView.append(msg + "\n");
        Spannable style = (Spannable) msgView.getText();
        int end = start + msg.length();
        ForegroundColorSpan colors = new ForegroundColorSpan(color);
        style.setSpan(colors, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    public void AppendInfoMessage(int id) {
        AppendInfoMessage(mContext.getString(id));
    }

    public void AppendInfoMessage(int id, int color) {
        AppendInfoMessage(mContext.getString(id), color);
    }

    public void AppendInfoMessageInUiThread(final String msg, final int color) {
        final Activity activity = (Activity) mContext;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (activity.isFinishing() == false)
                    AppendInfoMessage(msg, color);

            }
        });
    }

    public void AppendInfoMessageInUiThread(final String msg) {
        final Activity activity = (Activity) mContext;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (activity.isFinishing() == false)
                    AppendInfoMessage(msg);

            }
        });
    }

    public void AppendInfoMessageInUiThread(final int id, final int color) {
        final Activity activity = (Activity) mContext;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (activity.isFinishing() == false)
                    AppendInfoMessage(mContext.getString(id), color);

            }
        });

    }

    public void AppendInfoMessageInUiThread(final int id) {
        final Activity activity = (Activity) mContext;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (activity.isFinishing() == false)
                    AppendInfoMessage(mContext.getString(id));

            }
        });
    }

    public void AppendInfoMessage(String msg) {
        AppendInfoMessage(msg, Color.BLACK);
    }

    public void clear() {
        msgView.setText("");

    }
}

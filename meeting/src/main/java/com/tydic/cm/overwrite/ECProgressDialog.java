package com.tydic.cm.overwrite;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.tydic.cm.R;

/**
 * Created by like on 2017-09-18
 */

public class ECProgressDialog extends Dialog {
    private TextView mTextView;
    private View mImageView;

    /**
     * @param context
     */
    public ECProgressDialog(Context context) {
        super(context, R.style.Theme_Light_CustomDialog_Blue);
        setCancelable(true);
        setContentView(R.layout.request_loading_diloag);
        mTextView = (TextView) findViewById(R.id.textview);
        mTextView.setText(R.string.loading_press);
        mImageView = findViewById(R.id.imageview);
    }

    /**
     * @param context
     * @param resid
     */
    public ECProgressDialog(Context context, int resid) {
        this(context);
        mTextView.setText(resid);
    }

    public ECProgressDialog(Context context, CharSequence text) {
        this(context);
        mTextView.setText(text);
    }

    public ECProgressDialog(Context context, AsyncTask asyncTask) {
        this(context);
    }

    public ECProgressDialog(Context context, CharSequence text, AsyncTask asyncTask) {
        this(context, text);
    }

    /**
     * 设置对话框显示文本
     *
     * @param text
     */
    public final void setPressText(CharSequence text) {
        mTextView.setText(text);
    }

    public final void dismiss() {
        super.dismiss();
        mImageView.clearAnimation();
    }

    public final void show() {
        super.show();
        Animation loadAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.loading);
        mImageView.startAnimation(loadAnimation);
    }
}

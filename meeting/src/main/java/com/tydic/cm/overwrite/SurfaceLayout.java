package com.tydic.cm.overwrite;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.widget.RelativeLayout;

import com.tydic.cm.R;

/**
 * 自定义视频显示控件,包含提示界面
 * Created by like on 2017-09-26
 */

public class SurfaceLayout extends RelativeLayout {

    private RelativeLayout hint;

    public SurfaceView mSurfaceView;

    public SurfaceLayout(Context context) {
        this(context, null);
    }

    public SurfaceLayout(Context context, AttributeSet attrs) {
        this(context, null, 0);
    }

    public SurfaceLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {

        View view = LayoutInflater.from(context).inflate(R.layout.layout_surface_view, null, false);
        hint = (RelativeLayout) view.findViewById(R.id.hint_image);
        mSurfaceView = (SurfaceView) view.findViewById(R.id.surface_item);
        addView(view);
    }
}

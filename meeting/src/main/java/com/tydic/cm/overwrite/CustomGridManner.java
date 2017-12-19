package com.tydic.cm.overwrite;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.util.AttributeSet;

/**
 * 定义列表是否可以滚动
 * Created by li on 2017/11/30.
 */

public class CustomGridManner extends GridLayoutManager {

    private boolean isScrollEnabled = true;

    public CustomGridManner(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public CustomGridManner(Context context, int spanCount) {
        super(context, spanCount);
    }

    public CustomGridManner(Context context, int spanCount, int orientation, boolean reverseLayout) {
        super(context, spanCount, orientation, reverseLayout);
    }

    public void setScrollEnabled(boolean flag) {
        this.isScrollEnabled = flag;
    }

    @Override
    public boolean canScrollVertically() {
        return isScrollEnabled && super.canScrollVertically();
    }
}

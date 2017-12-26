package com.tydic.cm.bean;

import android.os.Parcel;
import android.support.annotation.NonNull;

import com.felipecsl.asymmetricgridview.AsymmetricItem;

/**
 * 决定每一个视频的位置显示
 * Created by like on 2017/12/26.
 */

public class LocalBean implements AsymmetricItem {
    /**
     * item所占列
     */
    private int columnSpan;
    /**
     * item所占行
     */
    private int rowSpan;
    /**
     * item的下标
     */
    private int position;
    /**
     * item的宽度
     */
    private int width;
    /**
     * item的高度
     */
    private int height;

    public LocalBean() {
        this(1, 1, 0);
    }

    public LocalBean(int columnSpan, int rowSpan, int position) {
        this.columnSpan = columnSpan;
        this.rowSpan = rowSpan;
        this.position = position;
    }


    public LocalBean(Parcel in) {
        readFromParcel(in);
    }

    @Override
    public int getColumnSpan() {
        return columnSpan;
    }

    @Override
    public int getRowSpan() {
        return rowSpan;
    }

    public int getPosition() {
        return position;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public String toString() {
        return String.format("%s: %sx%s", position, rowSpan, columnSpan);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    private void readFromParcel(Parcel in) {
        columnSpan = in.readInt();
        rowSpan = in.readInt();
        position = in.readInt();
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(columnSpan);
        dest.writeInt(rowSpan);
        dest.writeInt(position);
    }

    /* Parcelable interface implementation */
    public static final Creator<LocalBean> CREATOR = new Creator<LocalBean>() {
        @Override
        public LocalBean createFromParcel(@NonNull Parcel in) {
            return new LocalBean(in);
        }

        @Override
        @NonNull
        public LocalBean[] newArray(int size) {
            return new LocalBean[size];
        }
    };
}

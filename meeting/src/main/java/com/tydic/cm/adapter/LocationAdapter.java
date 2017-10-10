package com.tydic.cm.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tydic.cm.R;
import com.tydic.cm.bean.UsersBean;
import com.tydic.cm.model.inf.OnItemClickListener;
import com.tydic.cm.util.ScreenUtil;

import java.util.List;

/**
 * 弹窗切换位置适配器
 * Created by like on 2017-10-10
 */

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.LocationViewHolder> {

    private List<String> mList;

    private Context mContext;

    private OnItemClickListener onItemClickListener;

    public LocationAdapter(List<String> mList, Context mContext, OnItemClickListener onItemClickListener) {
        this.mList = mList;
        this.mContext = mContext;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public LocationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new LocationViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_location, parent, false));
    }

    @Override
    public void onBindViewHolder(LocationViewHolder holder, final int position) {
        final String text = mList.get(position);
        holder.loactionText.setText(text);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.loactionText.getLayoutParams();
        params.height = ScreenUtil.getScreenHeight(mContext) / 4;
        holder.loactionText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(position, new UsersBean());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    class LocationViewHolder extends RecyclerView.ViewHolder {

        TextView loactionText;

        public LocationViewHolder(View itemView) {
            super(itemView);
            loactionText = itemView.findViewById(R.id.location_text);
        }
    }

}

package com.tydic.cm.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.tydic.cm.R;
import com.tydic.cm.bean.UsersBean;
import com.tydic.cm.constant.Key;
import com.tydic.cm.util.CacheUtil;
import com.tydic.cm.util.CollectionsUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yufeng on 2017/11/30.
 */

public class CarouselAdapter extends BaseAdapter {
    private Context mContext;
    private List<UsersBean> usersBeanList;
    private List<UsersBean> chooseBean = new ArrayList<>();
    private CacheUtil cacheUtil;

    /**
     * 传入部门和人员数据列表
     *
     * @param context
     * @param usersBeanList
     */
    public CarouselAdapter(Context context, List<UsersBean> usersBeanList) {
        super();
        this.mContext = context;
        this.usersBeanList = usersBeanList;
        cacheUtil = CacheUtil.get(mContext);
    }

    public void notifyDataSetChanged(List<UsersBean> usersBeanList) {
        this.usersBeanList = usersBeanList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return usersBeanList == null ? 0 : usersBeanList.size();
    }

    @Override
    public Object getItem(int position) {
        return usersBeanList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        CarouselViewHolder carouselViewHolder;
        if (convertView == null) {
            carouselViewHolder = new CarouselViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.carousel_item_layout, null);
            carouselViewHolder.mUserName = (TextView) convertView.findViewById(R.id.carousel_user_name);
            carouselViewHolder.mCheckbox = (CheckBox) convertView.findViewById(R.id.carousel_user_checkbox);
            convertView.setTag(carouselViewHolder);
        } else {
            carouselViewHolder = (CarouselViewHolder) convertView.getTag();
        }
        carouselViewHolder.mUserName.setText(usersBeanList.get(position).getNickName());
        carouselViewHolder.mCheckbox.setChecked(usersBeanList.get(position).isChecked());
        carouselViewHolder.mCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                usersBeanList.get(position).setChecked(b);
                if (b) {
                    chooseBean.add(usersBeanList.get(position));
                } else {
                    chooseBean.remove(usersBeanList.get(position));
                }
            }
        });
        return convertView;
    }

    public List<UsersBean> getChooseBean() {
        for (int i = 0; i < chooseBean.size(); i++) {
            if (cacheUtil.getAsString(Key.ANYCHAT_USER_ID).equals(chooseBean.get(i).getUserId())) {
                CollectionsUtil.swap3(chooseBean, i, 0);
//                chooseBean.remove(i);
            }
        }
        for (int i = 0; i < 11; i++) {
            UsersBean bean = new UsersBean();
            bean.setNickName(i + "");
            bean.setUserId(12344 + i + "");
            bean.setVideoStatus("1");
            chooseBean.add(bean);
        }
        return chooseBean;
    }

    /**
     * 清除已经选择需求轮播的集合
     */
    public void clear() {
        if (chooseBean != null) {
            chooseBean.clear();
        }
    }

    private class CarouselViewHolder {
        TextView mUserName;
        CheckBox mCheckbox;
    }
}

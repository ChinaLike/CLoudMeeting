package com.tydic.cm.model;

import android.content.Context;
import android.graphics.Color;

import com.tydic.cm.bean.LocalBean;
import com.tydic.cm.bean.SurfaceConfig;
import com.tydic.cm.util.ScreenUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by like on 2017/12/26.
 */

public class LayoutConfig {

    private SurfaceConfig surfaceConfig;
    /**
     * 默认使用的布局文件id
     */
    private int defaultLayoutID;
    /**
     * 需要使用配置文件
     */
    private SurfaceConfig.LayoutConfigListBean config;
    /**
     * 总列数
     */
    private int columnCount;
    /**
     * 总行数
     */
    private int rowCount;
    /**
     * 布局Item
     */
    private List<SurfaceConfig.LayoutConfigListBean.CellInfoListBean> cellInfoList;
    /**
     * 屏幕的宽
     */
    private int screenWidth;
    /**
     * 屏幕的高
     */
    private int screenHeight;

    public LayoutConfig(SurfaceConfig surfaceConfig, Context context) {
        this.surfaceConfig = surfaceConfig;
        defaultLayoutID = surfaceConfig.getDefaultLayoutID();
        screenWidth = ScreenUtil.getScreenWidth(context);
        screenHeight = ScreenUtil.getScreenHeight(context);
        getParentLayout();
        initLayoutGrid();
    }

    /**
     * 获取布局文件
     */
    private void getParentLayout() {
        //所有布局文件
        List<SurfaceConfig.LayoutConfigListBean> layoutConfigList = surfaceConfig.getLayoutConfigList();
        for (SurfaceConfig.LayoutConfigListBean bean : layoutConfigList) {
            if (bean.getId() == defaultLayoutID) {
                config = bean;
            }
        }
        if (config == null) {
            throw new IndexOutOfBoundsException("配置文件不正确，请检查后使用！");
        }
        getLayoutView();
    }

    /**
     * 获取列表显示的行数和列数
     */
    private void initLayoutGrid() {
        columnCount = config.getColumnCount();
        rowCount = config.getRowCount();
    }

    /**
     * 获取分割线宽度
     * @return
     */
    public int getDeviderWidth(){
        return config.getDividerWidth();
    }

    /**
     * 获取分割线颜色
     * @return
     */
    public int getDevideColor(){
        String colorStr = config.getDividerColor();
        if (colorStr == null || "".equals(colorStr)){
            return Color.WHITE;
        }else {
            // TODO: 2017/12/26 此处简单处理 ，如有需要请进行判断
            return Color.parseColor(colorStr);
        }
    }

    /**
     * 获取布局的每一个视图
     */
    private void getLayoutView() {
        cellInfoList = config.getCellInfoList();
    }

    public List<LocalBean> layout() {
        if (cellInfoList == null) {
            return null;
        } else {
            List<LocalBean> list = new ArrayList<>();
            for (SurfaceConfig.LayoutConfigListBean.CellInfoListBean bean : cellInfoList) {
                int position = bean.getRowIndex() * columnCount + bean.getColumnIndex();
                LocalBean localBean = new LocalBean(bean.getRowSpan(), bean.getColumnSpan(), position);
                initSize(localBean, bean);
                list.add(localBean);
            }
            return list;
        }
    }

    /**
     * 初始化item的宽高
     * @param localBean
     * @param bean
     */
    private void initSize(LocalBean localBean, SurfaceConfig.LayoutConfigListBean.CellInfoListBean bean) {
        localBean.setWidth((int) (screenWidth * bean.getWidth() + 0.5));
        localBean.setHeight((int) (screenHeight * bean.getHeight() + 0.5));
    }

    /**
     * 获取列数
     *
     * @return
     */
    public int getColumnCount() {
        return columnCount;
    }
}

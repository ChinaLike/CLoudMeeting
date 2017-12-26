package com.tydic.cm.model;

import com.tydic.cm.bean.LocalBean;
import com.tydic.cm.bean.SurfaceConfig;

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

    public LayoutConfig(SurfaceConfig surfaceConfig) {
        this.surfaceConfig = surfaceConfig;
        defaultLayoutID = surfaceConfig.getDefaultLayoutID();
        getParentLayout();
        initLayoutGrid();
    }

    /**
     * 获取布局文件
     */
    private void getParentLayout() {
        //所有布局文件
        List<SurfaceConfig.LayoutConfigListBean> layoutConfigList = surfaceConfig.getLayoutConfigList();
        for (SurfaceConfig.LayoutConfigListBean bean :layoutConfigList) {
            if (bean.getId() == defaultLayoutID){
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
                list.add(localBean);
            }
            return list;
        }
    }

    /**
     * 获取列数
     * @return
     */
    public int getColumnCount() {
        return columnCount;
    }
}

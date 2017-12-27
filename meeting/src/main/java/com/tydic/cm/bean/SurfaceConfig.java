package com.tydic.cm.bean;

import java.util.List;

/**
 * 界面配置文件
 * Created by like on 2017/12/26.
 */

public class SurfaceConfig {

    /**
     * id : 1
     * name : 默认视频配置
     * defaultLayoutID : 2
     * layoutConfigList : [{"id":1,"name":"全屏式布局","displayCount":1,"rowCount":1,"columnCount":1,"cellInfoList":[{"id":1,"top":0,"left":0,"width":1,"height":1,"fullscreenLayoutID":0,"rowIndex":0,"columnIndex":0,"rowSpan":1,"columnSpan":1}]},{"id":2,"name":"6格式布局","displayCount":6,"rowCount":3,"columnCount":3,"cellInfoList":[{"id":1,"top":0,"left":0,"width":0.67,"height":0.67,"fullscreenLayoutID":1,"rowIndex":0,"columnIndex":0,"rowSpan":2,"columnSpan":2},{"id":2,"top":0,"left":0.67,"width":0.33,"height":0.33,"fullscreenLayoutID":0,"rowIndex":0,"columnIndex":2,"rowSpan":1,"columnSpan":1},{"id":3,"top":0.33,"left":0.67,"width":0.33,"height":0.34,"fullscreenLayoutID":0,"rowIndex":1,"columnIndex":2,"rowSpan":1,"columnSpan":1},{"id":4,"top":0.67,"left":0,"width":0.34,"height":0.33,"fullscreenLayoutID":0,"rowIndex":2,"columnIndex":0,"rowSpan":1,"columnSpan":1},{"id":5,"top":0.67,"left":0.34,"width":0.33,"height":0.33,"fullscreenLayoutID":0,"rowIndex":2,"columnIndex":1,"rowSpan":1,"columnSpan":1},{"id":6,"top":0.67,"left":0.67,"width":0.33,"height":0.33,"fullscreenLayoutID":0,"rowIndex":2,"columnIndex":2,"rowSpan":1,"columnSpan":1}]}]
     */

    private int id;
    private String name;
    private int defaultLayoutID;
    private List<LayoutConfigListBean> layoutConfigList;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDefaultLayoutID() {
        return defaultLayoutID;
    }

    public void setDefaultLayoutID(int defaultLayoutID) {
        this.defaultLayoutID = defaultLayoutID;
    }

    public List<LayoutConfigListBean> getLayoutConfigList() {
        return layoutConfigList;
    }

    public void setLayoutConfigList(List<LayoutConfigListBean> layoutConfigList) {
        this.layoutConfigList = layoutConfigList;
    }

    public static class LayoutConfigListBean {
        /**
         * id : 1
         * name : 全屏式布局
         * displayCount : 1
         * rowCount : 1
         * columnCount : 1
         * cellInfoList : [{"id":1,"top":0,"left":0,"width":1,"height":1,"fullscreenLayoutID":0,"rowIndex":0,"columnIndex":0,"rowSpan":1,"columnSpan":1}]
         */

        private int id;
        private String name;
        private int displayCount;
        private int rowCount;
        private int columnCount;

        private int dividerWidth;//分割线宽度

        private String dividerColor;//分割线颜色

        private List<CellInfoListBean> cellInfoList;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getDisplayCount() {
            return displayCount;
        }

        public void setDisplayCount(int displayCount) {
            this.displayCount = displayCount;
        }

        public int getRowCount() {
            return rowCount;
        }

        public void setRowCount(int rowCount) {
            this.rowCount = rowCount;
        }

        public int getColumnCount() {
            return columnCount;
        }

        public void setColumnCount(int columnCount) {
            this.columnCount = columnCount;
        }

        public List<CellInfoListBean> getCellInfoList() {
            return cellInfoList;
        }

        public void setCellInfoList(List<CellInfoListBean> cellInfoList) {
            this.cellInfoList = cellInfoList;
        }

        public int getDividerWidth() {
            if (dividerWidth == 0){
                return 1;
            }
            return dividerWidth;
        }

        public void setDividerWidth(int dividerWidth) {
            this.dividerWidth = dividerWidth;
        }

        public String getDividerColor() {
            if (dividerColor == null || "".equals(dividerColor)){
                return "#FFFFFFFF";
            }
            return dividerColor;
        }

        public void setDividerColor(String dividerColor) {
            this.dividerColor = dividerColor;
        }

        public static class CellInfoListBean {
            /**
             * id : 1
             * top : 0.0
             * left : 0.0
             * width : 1.0
             * height : 1.0
             * fullscreenLayoutID : 0
             * rowIndex : 0
             * columnIndex : 0
             * rowSpan : 1
             * columnSpan : 1
             */

            private int id;
            private double top;
            private double left;
            private double width;
            private double height;
            private int fullscreenLayoutID;
            private int rowIndex;
            private int columnIndex;
            private int rowSpan;
            private int columnSpan;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public double getTop() {
                return top;
            }

            public void setTop(double top) {
                this.top = top;
            }

            public double getLeft() {
                return left;
            }

            public void setLeft(double left) {
                this.left = left;
            }

            public double getWidth() {
                return width;
            }

            public void setWidth(double width) {
                this.width = width;
            }

            public double getHeight() {
                return height;
            }

            public void setHeight(double height) {
                this.height = height;
            }

            public int getFullscreenLayoutID() {
                return fullscreenLayoutID;
            }

            public void setFullscreenLayoutID(int fullscreenLayoutID) {
                this.fullscreenLayoutID = fullscreenLayoutID;
            }

            public int getRowIndex() {
                return rowIndex;
            }

            public void setRowIndex(int rowIndex) {
                this.rowIndex = rowIndex;
            }

            public int getColumnIndex() {
                return columnIndex;
            }

            public void setColumnIndex(int columnIndex) {
                this.columnIndex = columnIndex;
            }

            public int getRowSpan() {
                return rowSpan;
            }

            public void setRowSpan(int rowSpan) {
                this.rowSpan = rowSpan;
            }

            public int getColumnSpan() {
                return columnSpan;
            }

            public void setColumnSpan(int columnSpan) {
                this.columnSpan = columnSpan;
            }
        }
    }
}

package com.tydic.cm.bean;

import java.util.List;

/**
 * Created by yufeng on 2017/12/20.
 */

public class VideoLayoutParamsBean {

    /**
     * id : 1
     * name : 默认视频配置
     * defaultLayoutID : 2
     * layoutConfigList : [{"id":1,"name":"全屏式布局","displayCount":1,"cellInfoList":[{"id":1,"top":0,"left":0,"width":1,"height":1,"fullscreenLayoutID":0}]},{"id":2,"name":"6格式布局","displayCount":6,"cellInfoList":[{"id":1,"top":0,"left":0,"width":0.66,"height":0.66,"fullscreenLayoutID":1},{"id":2,"top":0,"left":0.67,"width":0.34,"height":0.33,"fullscreenLayoutID":0},{"id":3,"top":0.33,"left":0.67,"width":0.34,"height":0.33,"fullscreenLayoutID":0},{"id":4,"top":0.67,"left":0,"width":0.33,"height":0.33,"fullscreenLayoutID":0},{"id":5,"top":0.67,"left":0.34,"width":0.33,"height":0.33,"fullscreenLayoutID":0},{"id":6,"top":0.67,"left":0.67,"width":0.33,"height":0.33,"fullscreenLayoutID":0}]}]
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
         * cellInfoList : [{"id":1,"top":0,"left":0,"width":1,"height":1,"fullscreenLayoutID":0}]
         */

        private int id;
        private String name;
        private int displayCount;
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

        public List<CellInfoListBean> getCellInfoList() {
            return cellInfoList;
        }

        public void setCellInfoList(List<CellInfoListBean> cellInfoList) {
            this.cellInfoList = cellInfoList;
        }

        public static class CellInfoListBean {
            /**
             * id : 1
             * top : 0.0
             * left : 0.0
             * width : 1.0
             * height : 1.0
             * fullscreenLayoutID : 0
             */

            private int id;
            private double top;
            private double left;
            private double width;
            private double height;
            private int fullscreenLayoutID;

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
        }
    }
}

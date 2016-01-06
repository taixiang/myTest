package com.overtake.objectlist;

import com.handmark.pulltorefresh.library.internal.FlipLoadingLayout;
import com.handmark.pulltorefresh.library.internal.LoadingLayout;
import com.handmark.pulltorefresh.library.internal.RotateLoadingLayout;

import cn.ikinder.androidlibs.R;

/**
 * Created by kevinhoo on 14-7-4.
 */
public class ListAbstractCustomizer {

    public static int CONTAINER_FOOTER_LAYOUT_ID = R.layout.ptr_listview_footer;

    /**
     * 设置容器尾部layout id
     * 默认为 cn.ikinder.androidlibs.R.layout.ptr_listview_footer
     *
     * @param id
     */
    public static void setContainerFooterLayoutId(int id) {
        CONTAINER_FOOTER_LAYOUT_ID = id;
    }

    /**
     * 设置横向滚动时loading layout id
     * 默认设置 cn.ikinder.androidlibs.R.layout.pull_to_refresh_header_horizontal
     *
     * @param id
     */

    public static void setPTRHorizontalLayoutId(int id) {
        LoadingLayout.PULL_TO_REFRESH_HORIZONTAL_LAYOUT_ID = id;
    }

    /**
     * 设置纵向滚动时loading layout id
     * 默认设置 cn.ikinder.androidlibs.R.layout.pull_to_refresh_header_vertical
     *
     * @param id
     */
    public static void setPTRVerticalLayoutId(int id) {
        LoadingLayout.PULL_TO_REFRESH_VERTICAL_LAYOUT_ID = id;
    }

    /**
     * 设置flip动画的默认drawable id
     * 默认设置为 cn.ikinder.androidlibs.R.drawable.global_loading_default
     *
     * @param id
     */
    public static void setPTRFlipDefaultDrawableId(int id) {
        FlipLoadingLayout.PULL_TO_REFRESH_FLIP_DEFAULT_ID = id;
    }

    /**
     * 设置rotation动画的默认drawable id
     * 默认设置为 cn.ikinder.androidlibs.R.drawable.ptr_default_rotate
     *
     * @param id
     */
    public static void setPTRRotationDefaultDrawableId(int id) {
        RotateLoadingLayout.PULL_TO_REFRESH_ROTATE_DEFAULT_ID = id;
    }


}

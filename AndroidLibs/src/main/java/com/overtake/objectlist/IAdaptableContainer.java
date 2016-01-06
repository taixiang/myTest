package com.overtake.objectlist;

public interface IAdaptableContainer {

    void setAdapter(AdaptableViewManager adapter);

    void setLoadMoreHandler(OnLoadMoreListener handler);

    void showLoadingData();

    void onLoadingDataComplete(boolean hasMore);

    Boolean contentIsEmptyOrFirstChildInView();
}

package com.overtake.objectlist;

import com.overtake.objectlist.ListDataProvider.RequestType;

public interface ObjectListViewController<T> {

	public ListViewItemViewHolderBase<?> createItemHolder();

	public ListViewItemDataWrapperBase<T> createItemWrapper(int position, T data);

	public void onRequestData(RequestType mRequestType);

	public void onRequestDataSucceed();
}
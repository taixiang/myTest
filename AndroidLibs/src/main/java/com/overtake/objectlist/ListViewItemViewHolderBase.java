package com.overtake.objectlist;

import android.view.LayoutInflater;
import android.view.View;

public abstract class ListViewItemViewHolderBase<HoldingItemWrapper> {

	protected HoldingItemWrapper mHoldingItemWrapper;
	protected HoldingItemWrapper mLastHoldingItemWrapper;

	public abstract View createView(LayoutInflater inflater, HoldingItemWrapper wrapper);

	public void setHoldingItemWrapper(HoldingItemWrapper wrapper) {
		mLastHoldingItemWrapper = mHoldingItemWrapper;
		mHoldingItemWrapper = (HoldingItemWrapper) wrapper;
	}

    public abstract void showHoldingItemWrapper();

	public HoldingItemWrapper getHoldingItemWrapper() {
		return mHoldingItemWrapper;
	}
}
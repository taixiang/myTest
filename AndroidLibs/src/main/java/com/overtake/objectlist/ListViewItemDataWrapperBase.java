package com.overtake.objectlist;

public abstract class ListViewItemDataWrapperBase<T> {

	public int total;
	public int position;
	public T rawData;

	public T getRawData() {
		return rawData;
	}

	public ListViewItemDataWrapperBase(int position, T data) {
		this.position = position;
		this.rawData = data;
	}

	public abstract void preProcess();
}
package com.overtake.data;

public interface OTRequestObserver {

	void requestSuccessForTask(OTDataTask task);
	void requestFailedForTask(OTDataTask task, Throwable error);
	
	void requestDataModifyForTask(OTDataTask task);
	void taskAddedToRequestManager(OTDataTask task);
}
package com.overtake.data;

import com.loopj.android.http.RequestProgressHandler;

public class OTUploadQueueItem implements RequestProgressHandler {

	public static interface UploadQueueItemListener {
		public void onProgress(OTUploadQueueItem item, long current, long total);

		public void onSucceed(OTUploadQueueItem item);

		public void onFailed(OTUploadQueueItem item);

	}

	public enum UploadQueueItemStatus {
		UploadQueueItemStatusRunning, UploadQueueItemStatusFailed, 
		UploadQueueItemStatusWaiting, UploadQueueItemStatusFinished, UploadQueueItemStatusCancel;
	}

	public String mItemId;
	public OTUploadQueueItemDetailInfo mDetailInfo;
	public long mProgress;
	public UploadQueueItemStatus mStatus;
	public UploadQueueItemListener itemListener;

	public OTUploadQueueItem(String itemId, UploadQueueItemStatus status) {

		this.mStatus = status;
		this.mItemId = itemId;
	}

	@Override
	public void updateProgress(long current, long total) {
		if (mProgress != current) {
			this.mProgress = current;

			if (itemListener != null) {
				itemListener.onProgress(this, current, total);
			}
		}
	}

	public void onSucceed() {
		if (itemListener != null) {
			itemListener.onSucceed(this);
		}
	}

	public void onFailed() {
		if (itemListener != null) {
			itemListener.onFailed(this);
		}
	}

}

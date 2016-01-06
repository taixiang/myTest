package com.overtake.data;

import java.util.ArrayList;

public class OTUploadQueueItemDetailInfo {
	
	public String mDescription;
	public String mIcon;
	public long mUploadTime;
	public ArrayList<?> mRichDescription;
	public boolean mIsSilence;
	public int mDataRequestType;
	public String mItemId;
	public boolean mIsFailed;
	
	public OTUploadQueueItemDetailInfo()
	{
		mRichDescription = new ArrayList<Object>();
	}

}

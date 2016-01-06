package com.overtake.data;

import java.util.Hashtable;


public final class OTDataStatus {
	
	public OTDataOnlineStatus onlineStatus;
	public int total;
	public boolean hasMore;
	public Hashtable<String, Object> userInfo;
	public long timestamp;
	
	public OTDataStatus() {
		userInfo = new Hashtable<String, Object>();
	}
}

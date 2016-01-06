package com.overtake.request;

import com.overtake.base.OTJson;

public interface AfterRequestHandler {
	public void afterRequest(Boolean succeed, OTJson retRawData);
}

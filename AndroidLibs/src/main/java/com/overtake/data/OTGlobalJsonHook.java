package com.overtake.data;

import com.overtake.base.OTJson;

public interface OTGlobalJsonHook {

    /**
     * global
     * @param json
     * @param task
     */
	void globalJsonHook(OTJson json, OTDataTask task);
}

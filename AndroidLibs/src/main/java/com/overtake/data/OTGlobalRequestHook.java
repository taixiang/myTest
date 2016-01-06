package com.overtake.data;

import com.overtake.base.OTJson;

/**
 * Created by kevinhoo on 14-3-19.
 */
public interface OTGlobalRequestHook {

    /**
     * global hook for request
     *
     * @param json
     * @param task
     * @return false means **skip** data process, usually to use to to something before all
     */
    boolean globalRequestHook(OTJson json, OTDataTask task);
}

package com.overtake.data;

import java.util.HashMap;

public class OTDataRequest {

    public String url;
    public HashMap<String, String> requestParams;
    public HashMap<String, Object> dataParams;
    public HashMap<String, Object> imageParams;
    public OTDataTask task;
    public HttpMethod httpMethod;

    public OTDataRequest() {

        this.requestParams = new HashMap<String, String>();
        this.dataParams = new HashMap<String, Object>();
        this.imageParams = new HashMap<String, Object>();
    }

    public OTDataRequest(String url) {

        this.url = url;
    }

    public boolean equals(OTDataRequest request) {

        if (request == this)
            return true;
        if (request == null)
            return false;

        boolean same = true;
        if (!this.task.dataCategory.equals(request.task.dataCategory) ||
                this.task.dataRequestType != request.task.dataRequestType ||
                this.task.dataId != request.task.dataId) {

            same = false;
        }

        if (same) {

            same = this.task.args.equals(request.task.args);
        }

        return same;
    }
}

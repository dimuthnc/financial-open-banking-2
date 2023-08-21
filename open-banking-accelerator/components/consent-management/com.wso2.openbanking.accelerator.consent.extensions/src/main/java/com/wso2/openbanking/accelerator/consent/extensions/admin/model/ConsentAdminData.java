/**
 * Copyright (c) 2023, WSO2 LLC. (https://www.wso2.com).
 *
 * WSO2 LLC. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.wso2.openbanking.accelerator.consent.extensions.admin.model;

import com.wso2.openbanking.accelerator.consent.extensions.common.ResponseStatus;
import net.minidev.json.JSONObject;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Data wrapper for consent admin data.
 */
public class ConsentAdminData {

    private Map<String, String> headers;
    private JSONObject payload;
    private Map queryParams;
    private String absolutePath;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private ResponseStatus responseStatus;
    private JSONObject responsePayload;

    public ConsentAdminData(Map<String, String> headers, JSONObject payload, Map queryParams,
                            String absolutePath, HttpServletRequest request, HttpServletResponse response) {
        this.headers = headers;
        this.payload = payload;
        this.queryParams = queryParams;
        this.absolutePath = absolutePath;
        this.request = request;
        this.response = response;
    }

    public ConsentAdminData(Map<String, String> headers, Map queryParams, String absolutePath,
                            HttpServletRequest request, HttpServletResponse response) {
        this.headers = headers;
        payload = null;
        this.queryParams = queryParams;
        this.absolutePath = absolutePath;
        this.request = request;
        this.response = response;
    }


    public HttpServletRequest getRequest() {
        return request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public Map getQueryParams() {
        return queryParams;
    }

    public JSONObject getPayload() {
        return payload;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setResponseStatus(ResponseStatus responseStatus) {
        this.responseStatus = responseStatus;
    }

    public void setResponsePayload(JSONObject responsePayload) {
        this.responsePayload = responsePayload;
    }

    public JSONObject getResponsePayload() {
        return responsePayload;
    }

    public ResponseStatus getResponseStatus() {
        return responseStatus;
    }

    public void setResponseHeader(String key, String value) {
        response.setHeader(key, value);
    }

    public void setResponseHeaders(Map<String, String> headers) {
        for (Map.Entry<String, String> header : headers.entrySet()) {
            setResponseHeader(header.getKey(), header.getValue());
        }
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public void setAbsolutePath(String absolutePath) {
        this.absolutePath = absolutePath;
    }
}

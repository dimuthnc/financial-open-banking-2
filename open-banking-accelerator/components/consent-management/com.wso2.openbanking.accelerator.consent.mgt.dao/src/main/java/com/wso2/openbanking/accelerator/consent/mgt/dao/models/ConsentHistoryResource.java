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

package com.wso2.openbanking.accelerator.consent.mgt.dao.models;

import com.wso2.openbanking.accelerator.common.util.Generated;

import java.util.HashMap;
import java.util.Map;

/**
 * Model for the consent history resource.
 */
public class ConsentHistoryResource {

    private String historyID;
    private String consentID;
    private long timestamp;
    private String reason;
    private DetailedConsentResource detailedConsentResource;
    private Map<String, Object> changedAttributesJsonDataMap;

    public ConsentHistoryResource() {

    }

    @Generated(message = "Excluding constructor because setter methods are explicitly called")
    public ConsentHistoryResource(String consentID, String historyID) {

        this.consentID = consentID;
        this.historyID = historyID;
        this.changedAttributesJsonDataMap = new HashMap<>();
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public DetailedConsentResource getDetailedConsentResource() {
        return detailedConsentResource;
    }

    public void setDetailedConsentResource(DetailedConsentResource detailedConsentResource) {
        this.detailedConsentResource = detailedConsentResource;
    }

    public Map<String, Object> getChangedAttributesJsonDataMap() {
        return changedAttributesJsonDataMap;
    }

    public void setChangedAttributesJsonDataMap(Map<String, Object> changedAttributesJsonDataMap) {
        this.changedAttributesJsonDataMap = changedAttributesJsonDataMap;
    }

    public String getConsentID() {
        return consentID;
    }

    public void setConsentID(String consentID) {
        this.consentID = consentID;
    }

    public String getHistoryID() {
        return historyID;
    }

    public void setHistoryID(String historyID) {
        this.historyID = historyID;
    }
}

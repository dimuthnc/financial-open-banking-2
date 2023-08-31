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
package com.wso2.openbanking.accelerator.consent.extensions.manage.impl;

import com.wso2.openbanking.accelerator.consent.extensions.manage.model.ConsentManageData;

/**
 * Abstract Consent Manage Request Handler class.
 */
public interface ConsentManageRequestHandler {

    /**
     * Method to handle Account Consent Manage Post Request.
     *
     * @param consentManageData   Object containing request details
     */
    void handleConsentManagePost(ConsentManageData consentManageData);

    /**
     * Method to handle Consent Manage GET Request.
     *
     * @param consentManageData  Object containing request details
     */
    void handleConsentManageGet(ConsentManageData consentManageData);

    /**
     * Method to handle Account Consent Manage Post Request.
     *
     * @param consentManageData   Object containing request details
     */
    void handleConsentManageDelete(ConsentManageData consentManageData);
}

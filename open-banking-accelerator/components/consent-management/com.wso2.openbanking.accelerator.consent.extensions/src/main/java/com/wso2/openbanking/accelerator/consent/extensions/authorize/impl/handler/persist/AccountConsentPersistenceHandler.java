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


package com.wso2.openbanking.accelerator.consent.extensions.authorize.impl.handler.persist;

import com.wso2.openbanking.accelerator.common.exception.ConsentManagementException;
import com.wso2.openbanking.accelerator.consent.extensions.authorize.model.ConsentData;
import com.wso2.openbanking.accelerator.consent.extensions.authorize.model.ConsentPersistData;
import com.wso2.openbanking.accelerator.consent.extensions.common.ConsentException;
import com.wso2.openbanking.accelerator.consent.extensions.common.ConsentExtensionConstants;
import com.wso2.openbanking.accelerator.consent.extensions.common.ResponseStatus;
import com.wso2.openbanking.accelerator.consent.extensions.internal.ConsentExtensionsDataHolder;
import com.wso2.openbanking.accelerator.consent.mgt.dao.models.ConsentResource;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;

/**
 * Class to handle Accounts Consent data persistence for Authorize.
 */
public class AccountConsentPersistenceHandler implements ConsentPersistenceHandler {

    private static final Log log = LogFactory.getLog(AccountConsentPersistenceHandler.class);

    /**
     * Abstract method defined to handle consent persistence based on the consent type.
     *
     * @param consentPersistData    Consent Persist Data Object
     * @param consentResource       Consent Resource Object
     * @throws ConsentManagementException
     */
    @Override
    public void consentPersist(ConsentPersistData consentPersistData, ConsentResource consentResource)
            throws ConsentManagementException {

        ConsentData consentData = consentPersistData.getConsentData();

        JSONObject payload = consentPersistData.getPayload();

        if (payload.get("accountIds") == null || !(payload.get("accountIds") instanceof JSONArray)) {
            log.error("Account IDs not available in persist request");
            throw new ConsentException(ResponseStatus.BAD_REQUEST,
                    "Account IDs not available in persist request");
        }

        JSONArray accountIds = (JSONArray) payload.get("accountIds");
        ArrayList<String> accountIdsString = new ArrayList<>();
        for (Object account : accountIds) {
            if (!(account instanceof String)) {
                log.error("Account IDs format error in persist request");
                throw new ConsentException(ResponseStatus.BAD_REQUEST,
                        "Account IDs format error in persist request");
            }
            accountIdsString.add((String) account);
        }
        String consentStatus;
        String authStatus;

        if (consentPersistData.getApproval()) {
            consentStatus = ConsentExtensionConstants.AUTHORIZED_STATUS;
            authStatus = ConsentExtensionConstants.AUTHORIZED_STATUS;
        } else {
            consentStatus = ConsentExtensionConstants.REJECTED_STATUS;
            authStatus = ConsentExtensionConstants.REJECTED_STATUS;
        }

        ConsentExtensionsDataHolder.getInstance().getConsentCoreService()
                .bindUserAccountsToConsent(consentResource, consentData.getUserId(),
                        consentData.getAuthResource().getAuthorizationID(), accountIdsString, authStatus,
                        consentStatus);

    }

}

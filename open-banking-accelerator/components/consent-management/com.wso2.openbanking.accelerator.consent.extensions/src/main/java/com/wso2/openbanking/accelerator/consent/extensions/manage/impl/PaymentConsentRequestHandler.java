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

import com.wso2.openbanking.accelerator.common.exception.ConsentManagementException;
import com.wso2.openbanking.accelerator.common.util.ErrorConstants;
import com.wso2.openbanking.accelerator.consent.extensions.common.ConsentException;
import com.wso2.openbanking.accelerator.consent.extensions.common.ConsentExtensionConstants;
import com.wso2.openbanking.accelerator.consent.extensions.common.ConsentExtensionUtils;
import com.wso2.openbanking.accelerator.consent.extensions.common.ConsentServiceUtil;
import com.wso2.openbanking.accelerator.consent.extensions.common.ResponseStatus;
import com.wso2.openbanking.accelerator.consent.extensions.internal.ConsentExtensionsDataHolder;
import com.wso2.openbanking.accelerator.consent.extensions.manage.model.ConsentManageData;
import com.wso2.openbanking.accelerator.consent.extensions.manage.validator.PaymentsConsentRequestValidator;
import com.wso2.openbanking.accelerator.consent.extensions.util.ConsentManageUtil;
import com.wso2.openbanking.accelerator.consent.mgt.dao.models.ConsentResource;
import com.wso2.openbanking.accelerator.consent.mgt.dao.models.DetailedConsentResource;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.Map;


/**
 * Consent Manage request handler class for Payment Request Validation.
 */
public class PaymentConsentRequestHandler implements ConsentManageRequestHandler {
    private static final Log log = LogFactory.getLog(PaymentConsentRequestHandler.class);
    private static final String CREATED_STATUS = "created";
    private static final String AUTH_TYPE_AUTHORIZATION = "authorization";

    @Override
    public void handleConsentManagePost(ConsentManageData consentManageData) {

        try {
            //Validate cutoff datetime
            if (ConsentExtensionUtils.shouldInitiationRequestBeRejected()) {
                log.error(ErrorConstants.MSG_ELAPSED_CUT_OFF_DATE_TIME);
                throw new ConsentException(ResponseStatus.BAD_REQUEST, ErrorConstants.PAYMENT_INITIATION_HANDLE_ERROR);
            }

            //Get the request payload from the ConsentManageData
            Object request = consentManageData.getPayload();
            if (!(request instanceof JSONObject)) {
                log.error(ErrorConstants.PAYLOAD_FORMAT_ERROR);
                throw new ConsentException(ResponseStatus.BAD_REQUEST, ErrorConstants.INVALID_REQ_PAYLOAD);
            }

            JSONObject requestObject = (JSONObject) request;

            //Set request object to the response
            JSONObject response = requestObject;

            //Check Idempotency key exists
            if (StringUtils.isEmpty(consentManageData.getHeaders()
                    .get(ConsentExtensionConstants.X_IDEMPOTENCY_KEY))) {
                throw new ConsentException(ResponseStatus.BAD_REQUEST, ErrorConstants.IDEMPOTENCY_KEY_NOT_FOUND);
            }

            //Handle payment initiation flows
            handlePaymentPost(consentManageData, requestObject, response);

        } catch (ConsentManagementException e) {
            log.error(e.getMessage());
            throw new ConsentException(ResponseStatus.INTERNAL_SERVER_ERROR,
                    ErrorConstants.PAYMENT_INITIATION_HANDLE_ERROR);
        }

    }

    @Override
    public void handleConsentManageGet(ConsentManageData consentManageData) {
        String consentId = consentManageData.getRequestPath().split("/")[1];
        if (ConsentManageUtil.isConsentIdValid(consentId)) {
            try {
                ConsentResource consent = ConsentServiceUtil.getConsentService().getConsent(consentId,
                        false);
                if (consent == null) {
                    throw new ConsentException(ResponseStatus.BAD_REQUEST, ErrorConstants.RESOURCE_CONSENT_MISMATCH);
                }
                // Check whether the client id is matching
                if (!consent.getClientID().equals(consentManageData.getClientId())) {
                    //Throwing same error as null scenario since client will not be able to identify if consent
                    // exists if consent does not belong to them
                    throw new ConsentException(ResponseStatus.BAD_REQUEST,
                            ErrorConstants.NO_CONSENT_FOR_CLIENT_ERROR);
                }
                JSONObject receiptJSON = (JSONObject) new JSONParser(JSONParser.MODE_PERMISSIVE).
                        parse(consent.getReceipt());
                consentManageData.setResponsePayload(ConsentManageUtil
                        .getInitiationRetrievalResponse(receiptJSON, consent, consentManageData,
                                ConsentExtensionConstants.PAYMENTS));
                consentManageData.setResponseStatus(ResponseStatus.OK);
            } catch (ConsentManagementException | ParseException e) {
                throw new ConsentException(ResponseStatus.INTERNAL_SERVER_ERROR,
                        ErrorConstants.ACC_INITIATION_RETRIEVAL_ERROR);
            }
        } else {
            throw new ConsentException(ResponseStatus.BAD_REQUEST, ErrorConstants.INVALID_CONSENT_ID);
        }
    }

    @Override
    public void handleConsentManageDelete(ConsentManageData consentManageData) {

    }
    private void handlePaymentPost(ConsentManageData consentManageData, JSONObject requestObject, JSONObject response)
            throws ConsentManagementException {

        DetailedConsentResource createdConsent;

        //Validate Payment Initiation request
        JSONObject validationResponse = PaymentsConsentRequestValidator
                .validatePaymentInitiation(consentManageData.getRequestPath(), requestObject);
        if (validationResponse.containsKey(ConsentExtensionConstants.IS_VALID) &&
                !((boolean) validationResponse.get(ConsentExtensionConstants.IS_VALID))) {
            log.error(ErrorConstants.PAYLOAD_INVALID);
            throw new ConsentException((ResponseStatus) validationResponse
                    .get(ConsentExtensionConstants.HTTP_CODE),
                    (JSONObject) validationResponse.get(ConsentExtensionConstants.ERRORS));
        }

        ConsentResource requestedConsent = new ConsentResource(consentManageData.getClientId(),
                requestObject.toJSONString(), ConsentExtensionConstants.PAYMENTS,
                ConsentExtensionConstants.AWAITING_AUTH_STATUS);

        createdConsent = ConsentExtensionsDataHolder.getInstance().getConsentCoreService()
                .createAuthorizableConsent(requestedConsent, null,
                        CREATED_STATUS, AUTH_TYPE_AUTHORIZATION, true);

        Map<String, String> consentAttributes = new HashMap();
        consentAttributes.put(ConsentExtensionConstants.IDEMPOTENCY_KEY, consentManageData.getHeaders()
                .get(ConsentExtensionConstants.X_IDEMPOTENCY_KEY));
        ConsentServiceUtil.getConsentService().storeConsentAttributes(createdConsent.getConsentID(),
                consentAttributes);
        consentManageData.setResponsePayload(ConsentManageUtil.getInitiationResponse(response, createdConsent,
                consentManageData, ConsentExtensionConstants.PAYMENTS));

        Map<String, String> headers = consentManageData.getHeaders();
        //Setting response headers
        //Setting created time and idempotency to headers to handle idempotency in Gateway
        consentManageData.setResponseHeader(ConsentExtensionConstants.X_IDEMPOTENCY_KEY,
                headers.get(ConsentExtensionConstants.X_IDEMPOTENCY_KEY));
        consentManageData.setResponseStatus(ResponseStatus.CREATED);
    }

}

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

package com.wso2.openbanking.accelerator.gateway.executor.core;

import com.wso2.openbanking.accelerator.gateway.executor.model.OBAPIRequestContext;
import com.wso2.openbanking.accelerator.gateway.executor.model.OBAPIResponseContext;
import com.wso2.openbanking.accelerator.gateway.util.GatewayConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * Open Banking Default Request Router.
 */
public class DefaultRequestRouter extends AbstractRequestRouter {

    private static final List<OpenBankingGatewayExecutor> EMPTY_LIST = new ArrayList<>();

    public List<OpenBankingGatewayExecutor> getExecutorsForRequest(OBAPIRequestContext requestContext) {
        if (GatewayConstants.API_TYPE_NON_REGULATORY
                .equals(requestContext.getOpenAPI().getExtensions().get(GatewayConstants.API_TYPE_CUSTOM_PROP))) {
            requestContext.addContextProperty(GatewayConstants.API_TYPE_CUSTOM_PROP,
                    GatewayConstants.API_TYPE_NON_REGULATORY);
            return EMPTY_LIST;
        } else if (GatewayConstants.API_TYPE_CONSENT
                .equals(requestContext.getOpenAPI().getExtensions().get(GatewayConstants.API_TYPE_CUSTOM_PROP))) {
            requestContext.addContextProperty(GatewayConstants.API_TYPE_CUSTOM_PROP,
                    GatewayConstants.API_TYPE_CONSENT);
            return this.getExecutorMap().get("Consent");
        } else if (requestContext.getMsgInfo().getResource().contains("/register")) {
            return this.getExecutorMap().get("DCR");
        } else {
            return this.getExecutorMap().get("Default");
        }
    }

    public List<OpenBankingGatewayExecutor> getExecutorsForResponse(OBAPIResponseContext responseContext) {

        if (responseContext.getContextProps().containsKey(GatewayConstants.API_TYPE_CUSTOM_PROP)) {
            if (GatewayConstants.API_TYPE_NON_REGULATORY
                    .equals(responseContext.getContextProps().get(GatewayConstants.API_TYPE_CUSTOM_PROP))) {
                return EMPTY_LIST;
            } else if (GatewayConstants.API_TYPE_CONSENT
                    .equals(responseContext.getContextProps().get(GatewayConstants.API_TYPE_CUSTOM_PROP))) {
                return this.getExecutorMap().get("Consent");
            }
        }

        if (responseContext.getMsgInfo().getResource().contains("/register")) {
            return this.getExecutorMap().get("DCR");
        } else {
            return this.getExecutorMap().get("Default");
        }
    }

}

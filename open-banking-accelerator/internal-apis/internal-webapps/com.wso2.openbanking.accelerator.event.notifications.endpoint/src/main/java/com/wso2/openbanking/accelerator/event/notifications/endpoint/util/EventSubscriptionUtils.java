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

package com.wso2.openbanking.accelerator.event.notifications.endpoint.util;

import com.wso2.openbanking.accelerator.common.config.OpenBankingConfigParser;
import com.wso2.openbanking.accelerator.common.constant.OpenBankingConstants;
import com.wso2.openbanking.accelerator.common.util.OpenBankingUtils;
import com.wso2.openbanking.accelerator.event.notifications.endpoint.constants.EventNotificationEndPointConstants;
import com.wso2.openbanking.accelerator.event.notifications.service.constants.EventNotificationConstants;
import com.wso2.openbanking.accelerator.event.notifications.service.handler.EventSubscriptionServiceHandler;
import com.wso2.openbanking.accelerator.event.notifications.service.response.EventSubscriptionResponse;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.apache.commons.io.IOUtils;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;


/**
 * Events Notification Subscription API Utils.
 */
public class EventSubscriptionUtils {

    /**
     * Extract string payload from request object.
     */
    public static EventSubscriptionServiceHandler getEventSubscriptionServiceHandler() {

        EventSubscriptionServiceHandler eventSubscriptionServiceHandler = (EventSubscriptionServiceHandler)
                OpenBankingUtils.getClassInstanceFromFQN(OpenBankingConfigParser.getInstance().getConfiguration().
                        get(OpenBankingConstants.EVENT_SUBSCRIPTION_HANDLER).toString());
        return eventSubscriptionServiceHandler;
    }

    /**
     * Extract string payload from request object.
     *
     * @param request The request object
     * @return String payload
     */
    public static JSONObject getJSONObjectPayload(HttpServletRequest request) throws IOException, ParseException {
        Object payload = new JSONParser(JSONParser.MODE_PERMISSIVE).parse(IOUtils.
                toString(request.getInputStream()));
        if (payload == null || !(payload instanceof JSONObject)) {
            return null;
        }
        return (JSONObject) payload;
    }

    /**
     * Method to map the Event Creation Service Response to API response.
     *
     * @param eventSubscriptionResponse
     * @return Response
     */
    public static Response mapEventSubscriptionServiceResponse(EventSubscriptionResponse eventSubscriptionResponse) {
        String status = eventSubscriptionResponse.getStatus();
        if (eventSubscriptionResponse.getErrorResponse() == null) {
            switch (status) {
                case EventNotificationConstants.CREATED:
                    return Response.status(Response.Status.CREATED)
                            .entity(eventSubscriptionResponse.getResponseBody())
                            .build();
                case EventNotificationConstants.OK:
                    return Response.status(Response.Status.OK)
                            .entity(eventSubscriptionResponse.getResponseBody())
                            .build();
                case EventNotificationConstants.NO_CONTENT:
                    return Response.status(Response.Status.NO_CONTENT)
                            .entity(eventSubscriptionResponse.getResponseBody())
                            .build();
            }
        } else if (status.equals(EventNotificationConstants.BAD_REQUEST)) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(EventNotificationUtils.getErrorDTO(
                            EventNotificationEndPointConstants.INVALID_REQUEST,
                            eventSubscriptionResponse.getErrorResponse()))
                    .build();
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(EventNotificationUtils.getErrorDTO(
                        "Internal Server Error",
                        eventSubscriptionResponse.getErrorResponse()))
                .build();

    }
}

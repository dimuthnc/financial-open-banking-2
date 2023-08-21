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

package com.wso2.openbanking.accelerator.consent.mgt.dao.queries;

import com.wso2.openbanking.accelerator.consent.mgt.dao.constants.ConsentMgtDAOConstants;
import org.apache.commons.lang.StringUtils;

/**
 * The Oracle database queries used by the consent management DAO layer.
 */
public class ConsentMgtOracleDBQueries extends ConsentMgtCommonDBQueries {

    public String getSearchConsentsPreparedStatement(String whereClause, boolean shouldLimit, boolean shouldOffset,
                                                     String userIdFilterClause) {

        String selectClause = "OB_CONSENT ";
        String joinType = "LEFT";

        if (StringUtils.isNotEmpty(userIdFilterClause)) {
            joinType = "INNER";
            userIdFilterClause = "AND " + userIdFilterClause;
        }

        if (whereClause.trim().isEmpty()) {
            whereClause = " WHERE ";
        } else {
            whereClause = whereClause + " AND ";
        }

        StringBuilder query = new StringBuilder("SELECT OBC.CONSENT_ID, " +
                " ( SELECT receipt FROM OB_CONSENT WHERE consent_id = obc.consent_id " +
                "               FETCH first 1 rows only ) AS RECEIPT, " +
                " (SELECT client_id FROM OB_CONSENT WHERE consent_id = obc.consent_id " +
                "               FETCH FIRST 1 rows only ) AS CLIENT_ID, " +
                " (SELECT consent_type FROM OB_CONSENT WHERE consent_id = obc.consent_id  " +
                "               FETCH FIRST 1  rows only ) AS CONSENT_TYPE, " +
                " (SELECT current_status FROM OB_CONSENT WHERE consent_id = obc.consent_id  " +
                "               FETCH FIRST 1  rows only ) AS current_status, " +
                " (SELECT consent_frequency FROM OB_CONSENT WHERE consent_id = obc.consent_id  " +
                "               FETCH FIRST 1  rows only ) AS CONSENT_FREQUENCY, " +
                " (SELECT validity_time FROM OB_CONSENT WHERE consent_id = obc.consent_id  " +
                "               FETCH FIRST 1  rows only ) AS VALIDITY_TIME, " +
                " (SELECT recurring_indicator FROM OB_CONSENT WHERE consent_id = obc.consent_id  " +
                "               FETCH FIRST 1  rows only ) AS RECURRING_INDICATOR, " +
                " (SELECT created_time FROM OB_CONSENT WHERE consent_id = obc.consent_id  " +
                "               FETCH FIRST 1  rows only ) AS consent_created_time,       " +
                " (SELECT updated_time FROM OB_CONSENT WHERE consent_id = obc.consent_id  " +
                "               FETCH FIRST 1  rows only ) AS consent_updated_time, " +

                "          ( SELECT   listagg(att_key || '||') within GROUP (ORDER BY att_key) " +
                "                   FROM     ob_consent_attribute " +
                "                   WHERE    consent_id = obc.consent_id " +
                "                   GROUP BY consent_id ) AS ATT_KEY, " +

                "          ( SELECT   listagg(att_value || '||') within GROUP (ORDER BY att_key)" +
                "                   FROM     ob_consent_attribute" +
                "                   WHERE    consent_id = obc.consent_id" +
                "                   GROUP BY consent_id ) AS ATT_VALUE, " +

                "          ( SELECT   listagg(auth_id || '||') within GROUP (ORDER BY auth_id)" +
                "                   FROM     ob_consent_auth_resource" +
                "                   WHERE    consent_id = obc.consent_id" +
                "                   GROUP BY consent_id ) AS AUTH_ID, " +

                "          ( SELECT   listagg(auth_status || '||') within GROUP (ORDER BY auth_id)" +
                "                   FROM     ob_consent_auth_resource" +
                "                   WHERE    consent_id = obc.consent_id" +
                "                   GROUP BY consent_id ) AS AUTH_STATUS, " +

                "          ( SELECT   listagg(auth_type || '||') within GROUP (ORDER BY auth_id) " +
                "                   FROM     ob_consent_auth_resource " +
                "                   WHERE    consent_id = obc.consent_id " +
                "                   GROUP BY consent_id ) AS AUTH_TYPE, " +

                "          ( SELECT   listagg(updated_time || '||') within GROUP (ORDER BY auth_id) " +
                "                   FROM     ob_consent_auth_resource " +
                "                   WHERE    consent_id = obc.consent_id " +
                "                   GROUP BY consent_id ) AS UPDATED_TIME, " +

                "          ( SELECT   listagg(user_id || '||') within GROUP (ORDER BY auth_id) " +
                "                   FROM     ob_consent_auth_resource " +
                "                   WHERE    consent_id = obc.consent_id " +
                "                   GROUP BY consent_id ) AS USER_ID, " +


                "           ( SELECT   listagg(ocm2.auth_id || '||') within GROUP (ORDER BY ocm2.mapping_id) " +
                "                   FROM     ob_consent_mapping ocm2 " +
                "                   JOIN     ob_consent_auth_resource ocar2 " +
                "                   ON       ocar2.auth_id = ocm2.auth_id " +
                "                   WHERE    ocar2.consent_id = obc.consent_id) AS AUTH_MAPPING_ID , " +

                "           ( SELECT   listagg(ocm2.account_id || '||') within GROUP (ORDER BY ocm2.mapping_id) " +
                "                   FROM     ob_consent_mapping ocm2 " +
                "                   JOIN     ob_consent_auth_resource ocar2 " +
                "                   ON       ocar2.auth_id = ocm2.auth_id " +
                "                   WHERE    ocar2.consent_id = obc.consent_id) AS ACCOUNT_ID , " +

                "          ( SELECT   listagg(ocm2.mapping_id || '||') within GROUP (ORDER BY ocm2.mapping_id) " +
                "                   FROM     ob_consent_mapping ocm2 " +
                "                   JOIN     ob_consent_auth_resource ocar2 " +
                "                   ON       ocar2.auth_id = ocm2.auth_id " +
                "                   WHERE    ocar2.consent_id = obc.consent_id) AS MAPPING_ID , " +

                "          ( SELECT   listagg(ocm2.mapping_status || '||') within GROUP (ORDER BY ocm2.mapping_id) " +
                "                   FROM     ob_consent_mapping ocm2 " +
                "                   JOIN     ob_consent_auth_resource ocar2 " +
                "                   ON       ocar2.auth_id = ocm2.auth_id " +
                "                   WHERE    ocar2.consent_id = obc.consent_id) AS MAPPING_STATUS , " +

                "          ( SELECT   listagg(ocm2.permission || '||') within GROUP (ORDER BY ocm2.mapping_id) " +
                "                   FROM     ob_consent_mapping ocm2 " +
                "                   JOIN     ob_consent_auth_resource ocar2 " +
                "                   ON       ocar2.auth_id = ocm2.auth_id " +
                "                   WHERE    ocar2.consent_id = obc.consent_id) AS PERMISSION " +

                "FROM " + selectClause + " OBC " +
                "LEFT JOIN OB_CONSENT_ATTRIBUTE CA ON OBC.CONSENT_ID=CA.CONSENT_ID " +
                joinType + " JOIN OB_CONSENT_AUTH_RESOURCE OCAR ON OBC.CONSENT_ID=OCAR.CONSENT_ID "
                + userIdFilterClause +
                "LEFT JOIN OB_CONSENT_MAPPING OCM ON OCAR.AUTH_ID=OCM.AUTH_ID " + whereClause +
                " (OBC.UPDATED_TIME >= COALESCE(?, OBC.UPDATED_TIME) " +
                " AND OBC.UPDATED_TIME <= COALESCE(?, OBC.UPDATED_TIME)) GROUP BY obc.consent_id " +
                "ORDER BY UPDATED_TIME DESC ");

        if (shouldLimit && shouldOffset) {
            query.append("OFFSET ? ROWS FETCH NEXT ? ROWS ONLY ");
        } else if (shouldLimit) {
            query.append("FETCH NEXT ? ROWS ONLY");
        }
        return query.toString();
    }

    /**
     * SQL query for delete consent mapping by auth id.
     * @param executeOnRetentionTables
     * @return
     */
    public String getDeleteConsentMappingByAuthIdPreparedStatement(boolean executeOnRetentionTables) {

        // table prefix is to execute on consent retention data (purged data) tables. (if enabled)
        String tablePrefix = "";
        if (executeOnRetentionTables) {
            tablePrefix = ConsentMgtDAOConstants.RETENTION_TABLE_NAME_PREFIX;
        }
        return "DELETE FROM " + tablePrefix + "OB_CONSENT_MAPPING where MAPPING_ID in (SELECT MAPPING_ID FROM " +
                tablePrefix + "OB_CONSENT_MAPPING OBCM INNER JOIN " + tablePrefix + "OB_CONSENT_AUTH_RESOURCE OBAR " +
                "ON OBCM.AUTH_ID = OBAR.AUTH_ID WHERE OBAR.CONSENT_ID = ?)";
    }

    /**
     * SQL query for get consent status audit records by consentIds.
     * @param whereClause conditions
     * @param shouldLimit
     * @param shouldOffset
     * @param fetchFromRetentionTables
     * @return
     */
    public String getConsentStatusAuditRecordsByConsentIdsPreparedStatement(String whereClause, boolean shouldLimit,
                                                                            boolean shouldOffset,
                                                                            boolean fetchFromRetentionTables) {

        // table prefix is to fetch from the consent retention data (purged data) tables. (if enabled)
        String tablePrefix = "";
        if (fetchFromRetentionTables) {
            tablePrefix = ConsentMgtDAOConstants.RETENTION_TABLE_NAME_PREFIX;
        }
        StringBuilder query =
                new StringBuilder("SELECT * FROM " + tablePrefix + "OB_CONSENT_STATUS_AUDIT " + whereClause);

        if (shouldLimit && shouldOffset) {
            query.append("OFFSET ? ROWS FETCH NEXT ? ROWS ONLY ");
        } else if (shouldLimit) {
            query.append("FETCH NEXT ? ROWS ONLY");
        }
        return query.toString();
    }

    /**
     * Util method to get the limit offset order for differentiate oracle and mssql pagination.
     * @return is limit is before in prepared statement than offset
     */
    public boolean isLimitBeforeThanOffset() {

        return false;
    }
}

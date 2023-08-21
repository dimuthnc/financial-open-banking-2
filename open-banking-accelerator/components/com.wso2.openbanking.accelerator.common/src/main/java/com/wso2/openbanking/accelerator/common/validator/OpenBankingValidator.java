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

package com.wso2.openbanking.accelerator.common.validator;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;


/**
 * Common Validator to validate objects based on annotation.
 */
public class OpenBankingValidator {

    public static final Validator FAIL_FAST_VALIDATOR = Validation
            .byDefaultProvider().providerResolver(new OsgiServiceDiscoverer())
            .configure().addProperty("hibernate.validator.fail_fast", "true")
            .buildValidatorFactory()
            .getValidator();

    private static volatile OpenBankingValidator instance;

    private OpenBankingValidator() {
    }

    public static OpenBankingValidator getInstance() {

        if (instance == null) {
            synchronized (OpenBankingValidator.class) {
                if (instance == null) {
                    instance = new OpenBankingValidator();
                }
            }
        }
        return instance;
    }


    /**
     * Check for violations on request object. Stop at the first violation and return error.
     * Validations are executed based on annotation in model of the class.
     */
    public String getFirstViolation(Object object) {

        Set<ConstraintViolation<Object>> violations = FAIL_FAST_VALIDATOR.validate(object);
        return violations.stream().findFirst().map(ConstraintViolation::getMessage).orElse(null);
    }

    public String getFirstViolation(Object object, Class validationGroup) {

        Set<ConstraintViolation<Object>> violations = FAIL_FAST_VALIDATOR.validate(object, validationGroup);
        return violations.stream().findFirst().map(ConstraintViolation::getMessage).orElse(null);
    }
}

package org.sagebionetworks.bridge.validators;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.sagebionetworks.bridge.validators.Validate.CANNOT_BE_BLANK;

import org.sagebionetworks.bridge.models.accounts.Demographic;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class DemographicValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return Demographic.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object o, Errors errors) {
        Demographic demographic = (Demographic) o;

        if (isBlank(demographic.getAppId())) {
            errors.rejectValue("appId", CANNOT_BE_BLANK);
        }
        if (isBlank(demographic.getUserId())) {
            errors.rejectValue("userId", CANNOT_BE_BLANK);
        }
        if (demographic.getCategory() == null) {
            errors.rejectValue("category", CANNOT_BE_BLANK);
        }
    }
}

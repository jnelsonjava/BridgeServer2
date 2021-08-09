package org.sagebionetworks.bridge.services;

import static com.google.common.base.Preconditions.checkNotNull;

import org.sagebionetworks.bridge.dao.DemographicDao;
import org.sagebionetworks.bridge.models.accounts.Demographic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DemographicService {

    private DemographicDao demographicDao;

    @Autowired
    final void setDemographicDao(DemographicDao demographicDao) {
        this.demographicDao = demographicDao;
    }

    public void createDemographic(Demographic demographic) {
        checkNotNull(demographic);

        System.out.println(demographic.getCategory());

        demographicDao.createDemographic(demographic);
    }
}

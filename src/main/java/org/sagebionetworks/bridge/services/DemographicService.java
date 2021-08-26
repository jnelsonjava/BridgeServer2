package org.sagebionetworks.bridge.services;

import static com.google.common.base.Preconditions.checkNotNull;

import org.sagebionetworks.bridge.dao.DemographicDao;
import org.sagebionetworks.bridge.models.accounts.Demographic;
import org.sagebionetworks.bridge.models.accounts.DemographicId;
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
        // TODO: Maybe check access permissions here? Or just in Controller?

        System.out.println(demographic.getCategory());

        // TODO: Validate demographic
        // TODO: Check that it's not attempting to overwrite? Or make it create/update?

        demographicDao.createDemographic(demographic);
    }

    public void updateDemographic(Demographic demographic) {
        checkNotNull(demographic);

        demographicDao.updateDemographic(demographic);
    }

    public void deleteDemographic(DemographicId demographicId) {
        checkNotNull(demographicId);

        demographicDao.deleteDemographic(demographicId);
    }

    public Demographic getDemographic(DemographicId demographicId) {
        checkNotNull(demographicId);

        return demographicDao.getDemographic(demographicId);
    }

    // TODO: Make a getDemographicSummary method
    // TODO: Make a queryDemographic method
}

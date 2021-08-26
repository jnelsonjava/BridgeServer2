package org.sagebionetworks.bridge.dao;

import org.sagebionetworks.bridge.models.accounts.Demographic;
import org.sagebionetworks.bridge.models.accounts.DemographicId;

public interface DemographicDao {

    void createDemographic(Demographic demographic);

    void updateDemographic(Demographic demographic);

    void deleteDemographic(DemographicId demographicId);

    Demographic getDemographic(DemographicId demographicId);
}

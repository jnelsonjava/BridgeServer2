package org.sagebionetworks.bridge.dao;

import org.sagebionetworks.bridge.models.accounts.Demographic;
import org.sagebionetworks.bridge.models.accounts.DemographicId;
import org.sagebionetworks.bridge.models.accounts.DemographicList;

public interface DemographicDao {

    void saveDemographic(Demographic demographic);

    void deleteDemographic(DemographicId demographicId);

    Demographic getDemographic(DemographicId demographicId);

    DemographicList getParticipantDemographicList(String userId);
}

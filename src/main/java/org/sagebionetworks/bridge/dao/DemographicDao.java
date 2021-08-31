package org.sagebionetworks.bridge.dao;

import org.sagebionetworks.bridge.models.accounts.Demographic;
import org.sagebionetworks.bridge.models.accounts.DemographicId;
import org.sagebionetworks.bridge.models.accounts.DemographicList;
import org.sagebionetworks.bridge.models.accounts.ParticipantDemographicSummary;

public interface DemographicDao {

    void createDemographic(Demographic demographic);

    void saveDemographic(Demographic demographic);

    void deleteDemographic(DemographicId demographicId);

    Demographic getDemographic(DemographicId demographicId);

    DemographicList getParticipantDemographicList(String userId);
}

package org.sagebionetworks.bridge.services;

import static com.google.common.base.Preconditions.checkNotNull;

import org.sagebionetworks.bridge.dao.DemographicDao;
import org.sagebionetworks.bridge.models.accounts.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class DemographicService {

    private DemographicDao demographicDao;

    @Autowired
    final void setDemographicDao(DemographicDao demographicDao) {
        this.demographicDao = demographicDao;
    }

    public void createDemographics(DemographicList demographicList) {
        checkNotNull(demographicList);
        // TODO: Maybe check access permissions here? Or just in Controller?

        // TODO: Validate demographic

        for (Demographic demographic : demographicList.getDemographics()) {
            saveDemographic(demographic);
        }
    }

    public void saveDemographic(Demographic demographic) {
        checkNotNull(demographic);

        demographicDao.saveDemographic(demographic);
    }

    public void deleteDemographic(DemographicId demographicId) {
        checkNotNull(demographicId);

        demographicDao.deleteDemographic(demographicId);
    }

    public Demographic getDemographic(DemographicId demographicId) {
        checkNotNull(demographicId);

        return demographicDao.getDemographic(demographicId);
    }

    public ParticipantDemographics getParticipantDemographics(String userId) {
        // TODO: Validate userId
        DemographicList demographicList = demographicDao.getParticipantDemographicList(userId);

        Map<DemographicCategory, String> demographicMap = new HashMap<>();

        for (Demographic demographic : demographicList.getDemographics()) {
            demographicMap.put(demographic.getCategory(), demographic.getAnswerValue());
        }

        return new ParticipantDemographics(demographicMap);
    }

    // TODO: Make a getDemographicSummary method


    // TODO: Make a queryDemographic method
}

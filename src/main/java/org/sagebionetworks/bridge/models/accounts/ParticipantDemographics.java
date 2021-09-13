package org.sagebionetworks.bridge.models.accounts;

import java.util.Map;

public class ParticipantDemographics {
    private final Map<DemographicCategory, String> demographicMap;

    public ParticipantDemographics(Map<DemographicCategory, String> demographicMap) {
        this.demographicMap = demographicMap;
    }

    public Map<DemographicCategory, String> getDemographicMap() {
        return demographicMap;
    }

    public void addDemographic(DemographicCategory category, String value) {
        demographicMap.put(category, value);
    }
}

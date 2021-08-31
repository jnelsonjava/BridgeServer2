package org.sagebionetworks.bridge.models.accounts;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.sagebionetworks.bridge.models.BridgeEntity;

import java.util.List;

public class DemographicList implements BridgeEntity {

    private final List<Demographic> demographics;

    @JsonCreator
    public DemographicList(@JsonProperty("demographics") List<Demographic> demographics) {
        this.demographics = demographics;
    }

    public List<Demographic> getDemographics() {
        return demographics;
    }
}

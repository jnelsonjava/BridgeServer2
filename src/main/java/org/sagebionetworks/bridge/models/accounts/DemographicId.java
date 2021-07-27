package org.sagebionetworks.bridge.models.accounts;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public final class DemographicId implements Serializable {

    private String userId;
    private String category;

    public DemographicId() {
    }

    public DemographicId(String userId, String category) {
        this.userId = userId;
        this.category = category;
    }
}

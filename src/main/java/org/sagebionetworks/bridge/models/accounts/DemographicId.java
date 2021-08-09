package org.sagebionetworks.bridge.models.accounts;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public final class DemographicId implements Serializable {

    private String userId;
    private DemographicCategory category;

    public DemographicId() {
    }

    public DemographicId(String userId, DemographicCategory category) {
        this.userId = userId;
        this.category = category;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public DemographicCategory getCategory() {
        return category;
    }

    public void setCategory(DemographicCategory category) {
        this.category = category;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DemographicId that = (DemographicId) o;
        return Objects.equals(userId, that.userId) && Objects.equals(category, that.category);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, category);
    }
}

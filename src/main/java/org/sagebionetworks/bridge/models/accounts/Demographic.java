package org.sagebionetworks.bridge.models.accounts;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.sagebionetworks.bridge.models.BridgeEntity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Entity
@Table(name = "Demographics")
@IdClass(DemographicId.class)
public class Demographic implements BridgeEntity {

    @JsonIgnore
    private String appId;
    @Id
    private String userId;
    @Id
    @Enumerated(EnumType.STRING)
    private DemographicCategory category;
    private String answerValue;

    public Demographic() {
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
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

    public String getAnswerValue() {
        return answerValue;
    }

    public void setAnswerValue(String answerValue) {
        this.answerValue = answerValue;
    }
}

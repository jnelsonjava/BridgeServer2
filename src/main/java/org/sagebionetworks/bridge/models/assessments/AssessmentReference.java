package org.sagebionetworks.bridge.models.assessments;

import static org.sagebionetworks.bridge.models.appconfig.ConfigResolver.INSTANCE;
import static org.sagebionetworks.bridge.BridgeConstants.SHARED_APP_ID;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.sagebionetworks.bridge.models.appconfig.ConfigResolver;

public final class AssessmentReference {
    
    private final ConfigResolver resolver;
    private final String appId;
    private final String guid;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private final String id;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private final String originSharedId;

    @JsonCreator
    public AssessmentReference(@JsonProperty("appId") String appId,
                               @JsonProperty("guid") String guid) {
        this(INSTANCE, appId, guid, null, null);
    }

    public AssessmentReference(ConfigResolver resolver, String appId, String guid, String id, String originSharedId) {
        this.resolver = resolver;
        this.appId = appId;
        this.guid = guid;
        this.id = id;
        this.originSharedId = originSharedId;
    }
    
    public String getAppId() {
        return appId;
    }
    public String getGuid() {
        return guid;
    }
    public String getId() {
        return id;
    }
    public String getConfigHref() {
        if (guid == null) {
            return null;
        }
        String path = (appId != null && appId.equals(SHARED_APP_ID)) ? "/v1/sharedassessments/" : "/v1/assessments/";
        return resolver.url("ws", path + guid + "/config");
    }
    /**
     * If this assessment was derived from a shared assessment, the shared assessment's
     * identifier may help to find it in config. We do not provide a GUID or revision
     * because we don't anticipate clients will want to retrieve the original shared
     * assessment.
     */
    public String getOriginSharedId() {
        return originSharedId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(guid);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        AssessmentReference other = (AssessmentReference) obj;
        return Objects.equals(guid, other.guid);
    }

    @Override
    public String toString() {
        return "AssessmentReference [id=" + id + ", originSharedId=" + originSharedId + ", guid=" + guid + ", appId=" + appId
                + ", configHref=" + getConfigHref() + "]";
    }
}

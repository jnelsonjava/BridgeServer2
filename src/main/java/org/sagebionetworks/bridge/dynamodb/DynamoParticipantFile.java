package org.sagebionetworks.bridge.dynamodb;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import org.joda.time.DateTime;
import org.sagebionetworks.bridge.models.files.ParticipantFile;

@DynamoDBTable(tableName = "ParticipantFiles")
public class DynamoParticipantFile implements ParticipantFile {
    /**
     * the file Id for this ParticipantFile.
     * Unique in the scope of the user (StudyParticipant).
     */
    private String fileId;

    /**
     * The StudyParticipant who owns this file.
     */
    private String userId;

    /**
     * The time when this file is created.
     */
    private DateTime createdOn;

    /**
     * The media type of this file.
     */
    private String mimeType;

    /**
     * The App ID of this file.
     */
    private String appId;

    public DynamoParticipantFile(String userId, String fileId) {
        this.userId = userId;
        this.fileId = fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setCreatedOn(DateTime createdOn) {
        this.createdOn = createdOn;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    @Override
    @DynamoDBRangeKey(attributeName = "fileId")
    public String getFileId() {
        return this.fileId;
    }

    @Override
    @DynamoDBHashKey(attributeName = "userId")
    public String getUserId() {
        return this.userId;
    }

    @Override
    @DynamoDBAttribute(attributeName = "mimeType")
    public String getMimeType() {
        return this.mimeType;
    }

    @Override
    @DynamoDBAttribute(attributeName = "createdOn")
    public DateTime getCreatedOn() {
        return this.createdOn;
    }

    @Override
    @DynamoDBAttribute(attributeName = "appId")
    public String getAppId() { return this.appId; }
}

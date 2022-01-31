package org.sagebionetworks.bridge.models.upload;

import java.util.List;

import com.fasterxml.jackson.databind.node.ObjectNode;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import org.sagebionetworks.bridge.dynamodb.DynamoUpload2;

/** Metadata for Bridge uploads. */
public interface Upload {
    /** Instantiates an Upload. */
    static Upload create() {
        return new DynamoUpload2();
    }

    /**
     * If the upload is in a state where calling uploadComplete() and kicking off validation is a valid thing to do,
     * then this method will return true. Otherwise, this method will return false.
     */
    boolean canBeValidated();

    /** Upload content length in bytes. */
    long getContentLength();
    void setContentLength(long contentLength);

    /** The base64-encoded, 128-bit MD5 digest of the object body. */
    String getContentMd5();
    void setContentMd5(String contentMd5);

    /** MIME content type. */
    String getContentType();
    void setContentType(String contentType);

    /** The original ID that this upload is a duplicate of, or null if this upload is not a duplicate. */
    String getDuplicateUploadId();

    /** True if the upload is encrypted. False if it is not encrypted. */
    boolean isEncrypted();
    void setEncrypted(boolean encrypted);

    /** Name of the file to upload. */
    String getFilename();
    void setFilename(String filename);

    /** Health code of the user from which this upload originates from. */
    String getHealthCode();

    /** @see #getHealthCode */
    void setHealthCode(String healthCode);

    /**
     * Metadata fields for this upload, as submitted by the app. This corresponds with the
     * uploadMetadataFieldDefinitions configured in the app.
     */
    ObjectNode getMetadata();
    void setMetadata(ObjectNode metadata);

    /**
     * S3 object ID (key name). This is generated by Bridge to ensure no filename collisions and to ensure that we
     * don't get S3 hotspots from poorly distributed names.
     */
    String getObjectId();

    /**
     * Record ID of the corresponding health data record. This is generally null until upload validation is complete
     * and creates the corresponding record.
     */
    String getRecordId();

    /** Represents upload status, such as requested, validation in progress, validation failed, or succeeded. */
    UploadStatus getStatus();
    void setStatus(UploadStatus status);

    /**
     * <p>
     * Calendar date the file was uploaded (specifically, the uploadComplete() call.
     * </p>
     * Date is determined using Pacific local time. Pacific local time was chosen because currently, all apps are
     * done in the US, so if we partitioned based on date using UTC, we'd get a cut-off in the middle of the afternoon,
     * likely in the middle of peak uploads. In the future, if we have apps outside of the US, the upload date
     * timezone will be configurable per app.
     * <p>
     */
    LocalDate getUploadDate();
    
    /**
     * <p>The UTC timestamp of the time when the server creates the initial REQUESTED upload record.</p>
     */
    long getRequestedOn();
    void setRequestedOn(long requestedOn);
    
    /**
     * <p>The UTC timestamp of the time when the upload record is updated based on a completed call by any external 
     * client (either the S3 event listener or the mobile client). </p>
     */
    long getCompletedOn();
    void setCompletedOn(long completedOn);
    
    /**
     * <p>A string indicating the client that completed the upload. The two current clients are "s3 listener" and 
     * "mobile client". </p>
     */
    UploadCompletionClient getCompletedBy();
    void setCompletedBy(UploadCompletionClient completedBy);
    
    /**
     * <p>The app ID for this upload.</p>
     */
    String getAppId();
    void setAppId(String appId);

    /** Upload ID. This is the key in the Dynamo DB table that uniquely identifies this upload. */
    String getUploadId();

    /** @see #getUploadId */
    void setUploadId(String uploadId);

    /** True if the upload is zipped. False if it is a single file. */
    boolean isZipped();
    void setZipped(boolean zipped);

    /**
     * List of validation messages, generally contains error messages. Since a single upload file may fail validation
     * in multiple ways, Bridge server will attempt to return all messages to the user. For example, the upload file
     * might be unencrypted, uncompressed, and it might not fit any of the expected schemas for the app.
     */
    List<String> getValidationMessageList();
    
    /**
     * If this upload is based on an assessment specified by the v2 scheduler, this optional field should
     * be the instance GUID of the assessment or session in the participant’s Timeline. This will be used 
     * to associate the upload with scheduling context information. 
     */
    String getInstanceGuid();
    void setInstanceGuid(String instanceGuid);
    
    /**
     * If this upload is based on an assessment specified by the v2 scheduler, this optional field should
     * be the timestamp of the event that triggered this performance of the assessment or session in the 
     * participant’s Timeline. This will be used to associate the upload with scheduling context information. 
     */
    DateTime getEventTimestamp();
    void setEventTimestamp(DateTime eventTimestamp);
}

package org.sagebionetworks.bridge.services;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.Boolean.TRUE;
import static java.nio.charset.Charset.defaultCharset;

import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.sagebionetworks.bridge.BridgeUtils;
import org.sagebionetworks.bridge.exceptions.BridgeServiceException;
import org.sagebionetworks.bridge.models.accounts.SharingScope;
import org.sagebionetworks.bridge.models.accounts.StudyParticipant;
import org.sagebionetworks.bridge.models.apps.App;
import org.sagebionetworks.bridge.models.subpopulations.ConsentSignature;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.fasterxml.jackson.core.util.ByteArrayBuilder;
import com.google.common.net.MediaType;
import com.lowagie.text.DocumentException;

/**
 * A class to construct a signed consent agreement for delivery to the user. The bytes can be embedded as 
 * an email attachment, or uploaded to S3 for download by the user. 
 */
public final class ConsentPdf {

    public static final DateTimeFormatter FORMATTER = DateTimeFormat.forPattern("MMMM d, yyyy");
    
    private final App app;
    private final StudyParticipant signer;
    private final ConsentSignature consentSignature;
    private final SharingScope sharingScope;
    private final String studyConsentAgreement;
    private final String xmlTemplateWithSignatureBlock;
    private String formattedConsentDocument;

    public ConsentPdf(App app, StudyParticipant signer, ConsentSignature consentSignature,
            SharingScope sharingScope, String studyConsentAgreement, String xmlTemplateWithSignatureBlock) {
        this.app = checkNotNull(app);
        this.signer = signer;
        this.consentSignature = checkNotNull(consentSignature);
        this.sharingScope = checkNotNull(sharingScope);
        this.studyConsentAgreement = checkNotNull(studyConsentAgreement);
        this.xmlTemplateWithSignatureBlock = checkNotNull(xmlTemplateWithSignatureBlock);
    }
    
    /**
     * Get the final, formatted string we supply to the PDF renderer. For unit tests. 
     */
    protected String getFormattedConsentDocument() { 
        if (formattedConsentDocument == null) {
            getBytes();
        }
        return formattedConsentDocument;
    }

    public byte[] getBytes() {
        final String consentDoc = createSignedDocument();

        String imageMimeType = consentSignature.getImageMimeType();
        String imageData = consentSignature.getImageData();
        boolean validConsentSigImage = isImageMimeType(imageMimeType) && isBase64(imageData);

        // Save this final non-binary output so we can test it is correct.
        formattedConsentDocument = consentDoc;
        if (validConsentSigImage) {
            // Embed the signature image
            formattedConsentDocument = consentDoc.replace("cid:consentSignature",
                    "data:" + imageMimeType + ";base64," + imageData);
        }

        return createPdf(formattedConsentDocument);
    }

    private byte[] createPdf(final String consentDoc) {
        try (ByteArrayBuilder byteArrayBuilder = new ByteArrayBuilder()) {
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(consentDoc);
            renderer.layout();
            renderer.createPDF(byteArrayBuilder);
            byteArrayBuilder.flush();
            return byteArrayBuilder.toByteArray();
        } catch (DocumentException e) {
            throw new BridgeServiceException(e);
        }
    }

    /**
     * Consent documents were originally whole XHTML documents (they must be valid XML because PDF support from Java
     * libraries is limited and bad, and the tool we're using only works with XML as an input). In order to edit these
     * documents, we are moving to a system where only the content portion of the consent document, excluding the
     * signature block at the end, is available to researchers to edit. We then assemble the complete HTML document at
     * runtime. Here we branch based on the detection of a complete HTML document and do either one or the other.
     */
    private String createSignedDocument() {
        DateTime localSignedOn = new DateTime(consentSignature.getSignedOn());
        String signingDate = FORMATTER.print(localSignedOn) + " (GMT)";
        String sharingLabel = (sharingScope == null) ? "" : sharingScope.getLabel();

        // User's name may contain HTML. Clean it up
        String username = Jsoup.clean(consentSignature.getName(), Safelist.none());
        
        // A prior format using '@@' as a delimiter is no longer used by any app in production.
        
        String contactInfo = "";
        String contactLabel = "";
        if (signer.getEmail() != null && TRUE.equals(signer.getEmailVerified())) {
            contactInfo = signer.getEmail();
            contactLabel = "Email Address";
        } else if (signer.getPhone() != null && TRUE.equals(signer.getPhoneVerified())) {
            contactInfo = signer.getPhone().getNationalFormat();
            contactLabel = "Phone Number";
        } else if (signer.getExternalId() != null) {
            contactInfo = signer.getExternalId();
            contactLabel = "ID";
        }
        
        // This is now a fragment, assemble accordingly
        Map<String,String> map = BridgeUtils.appTemplateVariables(app);
        map.put("participant.name", username);
        map.put("participant.signing.date", signingDate);
        map.put("participant.contactInfo", contactInfo);
        map.put("participant.contactLabel", contactLabel);
        map.put("participant.sharing", sharingLabel);
        String resolvedStudyConsentAgreement = BridgeUtils.resolveTemplate(studyConsentAgreement, map);
        
        map.put("consent.body", resolvedStudyConsentAgreement);
        return BridgeUtils.resolveTemplate(xmlTemplateWithSignatureBlock, map);
    }

    // Helper method to check if the given string is a valid Base64 string. Returns false for null or blank strings.
    private static boolean isBase64(String base64Str) {
        //noinspection SimplifiableIfStatement
        if (StringUtils.isBlank(base64Str)) {
            return false;
        } else {
            return Base64.isBase64(base64Str.getBytes(defaultCharset()));
        }
    }

    // Helper method to check if a given MIME type is an image MIME type. Returns false for null or blank strings, or
    // for strings that are not valid MIME types.
    private static boolean isImageMimeType(String mimeTypeStr) {
        if (StringUtils.isBlank(mimeTypeStr)) {
            return false;
        }

        MediaType mimeType;
        try {
            mimeType = MediaType.parse(mimeTypeStr);
        } catch (IllegalArgumentException ex) {
            return false;
        }

        return mimeType.is(MediaType.ANY_IMAGE_TYPE);
    }}

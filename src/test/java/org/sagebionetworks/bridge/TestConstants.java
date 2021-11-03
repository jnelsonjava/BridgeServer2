package org.sagebionetworks.bridge;

import static org.sagebionetworks.bridge.Roles.DEVELOPER;
import static org.sagebionetworks.bridge.Roles.STUDY_COORDINATOR;
import static org.sagebionetworks.bridge.models.accounts.AccountStatus.DISABLED;
import static org.sagebionetworks.bridge.models.accounts.AccountStatus.ENABLED;
import static org.sagebionetworks.bridge.models.assessments.ResourceCategory.LICENSE;
import static org.sagebionetworks.bridge.models.assessments.ResourceCategory.PUBLICATION;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import org.sagebionetworks.bridge.config.BridgeConfigFactory;
import org.sagebionetworks.bridge.models.CriteriaContext;
import org.sagebionetworks.bridge.models.Label;
import org.sagebionetworks.bridge.models.Tag;
import org.sagebionetworks.bridge.models.TagUtils;
import org.sagebionetworks.bridge.models.accounts.AccountId;
import org.sagebionetworks.bridge.models.accounts.AccountSummary;
import org.sagebionetworks.bridge.models.accounts.ConsentStatus;
import org.sagebionetworks.bridge.models.accounts.Phone;
import org.sagebionetworks.bridge.models.accounts.Withdrawal;
import org.sagebionetworks.bridge.models.apps.AndroidAppLink;
import org.sagebionetworks.bridge.models.apps.AppleAppLink;
import org.sagebionetworks.bridge.models.assessments.ColorScheme;
import org.sagebionetworks.bridge.models.assessments.ResourceCategory;
import org.sagebionetworks.bridge.models.assessments.config.PropertyInfo;
import org.sagebionetworks.bridge.models.notifications.NotificationMessage;
import org.sagebionetworks.bridge.models.schedules.Activity;
import org.sagebionetworks.bridge.models.subpopulations.ConsentSignature;
import org.sagebionetworks.bridge.models.subpopulations.SubpopulationGuid;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

public class TestConstants {
    public static final String TEST_APP_ID = "test-app";
    public static final String TEST_STUDY_ID = "test-study";
    public static final String TEST_ORG_ID = "test-org-id";
    public static final String TEST_EXTERNAL_ID = "test-external-id";

    public static final NotificationMessage NOTIFICATION_MESSAGE = new NotificationMessage.Builder()
            .withSubject("a subject").withMessage("a message").build();
    public static final DateTime TIMESTAMP = DateTime.parse("2015-01-27T00:38:32.486Z");
    public static final String REQUEST_ID = "request-id";
    public static final String UA = "Asthma/26 (Unknown iPhone; iPhone OS/9.1) BridgeSDK/4";
    public static final String IP_ADDRESS = "2.3.4.5";
    public static final String TEST_USER_ID = "userId";
    public static final String SYNAPSE_USER_ID = "12345";
    public static final DateTimeZone TIMEZONE_MSK = DateTimeZone.forOffsetHours(3);
    public static final String TEST_NOTE = "test note";
    public static final String TEST_CLIENT_TIME_ZONE = "America/Los_Angeles";

    public static final String HEALTH_CODE = "oneHealthCode";
    public static final String ENCRYPTED_HEALTH_CODE = "TFMkaVFKPD48WissX0bgcD3esBMEshxb3MVgKxHnkXLSEPN4FQMKc01tDbBAVcXx94kMX6ckXVYUZ8wx4iICl08uE+oQr9gorE1hlgAyLAM=";
    public static final String UNENCRYPTED_HEALTH_CODE = "5a2192ee-f55d-4d01-a385-2d19f15a0880";
    
    public static final String DUMMY_IMAGE_DATA = "VGhpcyBpc24ndCBhIHJlYWwgaW1hZ2Uu";

    public static final byte[] MOCK_MD5 = { -104, 10, -30, -37, 25, -113, 92, -9, 69, -118, -46, -87, 11, -14, 38, -61 };
    public static final String MOCK_MD5_HEX_ENCODED = "980ae2db198f5cf7458ad2a90bf226c3";

    public static final AccountId ACCOUNT_ID = AccountId.forId(TEST_APP_ID, TEST_USER_ID);
    public static final AccountId ACCOUNT_ID_WITH_HEALTHCODE = AccountId.forHealthCode(TEST_APP_ID, HEALTH_CODE);
    public static final CriteriaContext TEST_CONTEXT = new CriteriaContext.Builder()
            .withUserId("user-id").withAppId(TEST_APP_ID).build();

    public static final int TIMEOUT = 10000;
    public static final String TEST_BASE_URL = "http://localhost:3333";
    public static final String API_URL = "/v3";
    public static final String SIGN_OUT_URL = API_URL + "/auth/signOut";
    public static final String SIGN_IN_URL = API_URL + "/auth/signIn";
    public static final String SCHEDULES_API = API_URL + "/schedules";
    public static final String SCHEDULED_ACTIVITIES_API = API_URL + "/activities";

    public static final String APPLICATION_JSON = "application/json";
    public static final String EMAIL = "email@email.com";
    public static final String PASSWORD = "password";
    public static final String SESSION_TOKEN = "sessionToken";

    public static final String ATTACHMENT_BUCKET = BridgeConfigFactory.getConfig().getProperty("attachment.bucket");
    public static final String UPLOAD_BUCKET = BridgeConfigFactory.getConfig().getProperty("upload.bucket");
    
    public static final DateTime ENROLLMENT = DateTime.parse("2015-04-10T10:40:34.000-07:00");
    
    /**
     * During tests, must sometimes pause because the underlying query uses a DynamoDB global 
     * secondary index, and this does not currently support consistent reads.
     */
    public static final int GSI_WAIT_DURATION = 2000;

    public static final ConsentStatus REQUIRED_SIGNED_CURRENT = new ConsentStatus.Builder().withName("Name1")
            .withGuid(SubpopulationGuid.create("foo1")).withRequired(true).withConsented(true)
            .withSignedMostRecentConsent(true).build();
    public static final ConsentStatus REQUIRED_SIGNED_OBSOLETE = new ConsentStatus.Builder().withName("Name1")
            .withGuid(SubpopulationGuid.create("foo2")).withRequired(true).withConsented(true)
            .withSignedMostRecentConsent(false).build();
    public static final ConsentStatus OPTIONAL_SIGNED_CURRENT = new ConsentStatus.Builder().withName("Name1")
            .withGuid(SubpopulationGuid.create("foo3")).withRequired(false).withConsented(true)
            .withSignedMostRecentConsent(true).build();
    public static final ConsentStatus OPTIONAL_SIGNED_OBSOLETE = new ConsentStatus.Builder().withName("Name1")
            .withGuid(SubpopulationGuid.create("foo4")).withRequired(false).withConsented(true)
            .withSignedMostRecentConsent(false).build();
    public static final ConsentStatus REQUIRED_UNSIGNED = new ConsentStatus.Builder().withName("Name1")
            .withGuid(SubpopulationGuid.create("foo5")).withRequired(true).withConsented(false)
            .withSignedMostRecentConsent(false).build();
    public static final ConsentStatus OPTIONAL_UNSIGNED = new ConsentStatus.Builder().withName("Name1")
            .withGuid(SubpopulationGuid.create("foo6")).withRequired(false).withConsented(false)
            .withSignedMostRecentConsent(false).build();
    
    public static final Map<SubpopulationGuid, ConsentStatus> CONSENTED_STATUS_MAP = new ImmutableMap.Builder<SubpopulationGuid, ConsentStatus>()
            .put(SubpopulationGuid.create(REQUIRED_SIGNED_CURRENT.getSubpopulationGuid()), REQUIRED_SIGNED_CURRENT)
            .build();
    public static final Map<SubpopulationGuid, ConsentStatus> UNCONSENTED_STATUS_MAP = new ImmutableMap.Builder<SubpopulationGuid, ConsentStatus>()
            .put(SubpopulationGuid.create(REQUIRED_UNSIGNED.getSubpopulationGuid()), REQUIRED_UNSIGNED).build();
    
    public static final ConsentSignature SIGNATURE = new ConsentSignature.Builder().withName("Jack Aubrey")
            .withBirthdate("1970-10-10").withImageData("data:asdf").withImageMimeType("image/png")
            .withSignedOn(TIMESTAMP.getMillis()).build();
    
    public static final SubpopulationGuid SUBPOP_GUID = SubpopulationGuid.create(REQUIRED_UNSIGNED.getSubpopulationGuid());
    
    public static final String GUID = "oneGuid";

    public static final String ASSESSMENT_ID = "oneAssessmentId";
    
    public static final Set<String> USER_DATA_GROUPS = ImmutableSet.of("group1","group2");

    public static final Set<String> USER_STUDY_IDS = ImmutableSet.of("studyA","studyB");
    
    public static final List<String> LANGUAGES = ImmutableList.of("en","fr");
    
    public static final Set<ResourceCategory> RESOURCE_CATEGORIES = ImmutableSet.of(LICENSE, PUBLICATION);
    
    public static final Phone PHONE = new Phone("9712486796", "US");
    
    public static final Withdrawal WITHDRAWAL = new Withdrawal("reasons");
    
    public static final AndroidAppLink ANDROID_APP_LINK = new AndroidAppLink("namespace", "package_name",
            Lists.newArrayList("sha256_cert_fingerprints"));
    public static final AndroidAppLink ANDROID_APP_LINK_2 = new AndroidAppLink("namespace2", "package_name2",
            Lists.newArrayList("sha256_cert_fingerprints2"));
    public static final AndroidAppLink ANDROID_APP_LINK_3 = new AndroidAppLink("namespace3", "package_name3",
            Lists.newArrayList("sha256_cert_fingerprints3"));
    public static final AndroidAppLink ANDROID_APP_LINK_4 = new AndroidAppLink("namespace4", "package_name4",
            Lists.newArrayList("sha256_cert_fingerprints4"));
    public static final AppleAppLink APPLE_APP_LINK = new AppleAppLink("appId",
            Lists.newArrayList("/appId/", "/appId/*"));
    public static final AppleAppLink APPLE_APP_LINK_2 = new AppleAppLink("appId2",
            Lists.newArrayList("/appId2/", "/appId2/*"));
    public static final AppleAppLink APPLE_APP_LINK_3 = new AppleAppLink("appId3",
            Lists.newArrayList("/appId3/", "/appId3/*"));
    public static final AppleAppLink APPLE_APP_LINK_4 = new AppleAppLink("appId4",
            Lists.newArrayList("/appId4/", "/appId4/*"));
    
    public static final Activity ACTIVITY_1 = new Activity.Builder().withGuid("activity1guid").withLabel("Activity1")
            .withPublishedSurvey("identifier1", "AAA").build();
    
    public static final Activity ACTIVITY_2 = new Activity.Builder().withGuid("activity2guid").withLabel("Activity2")
                .withPublishedSurvey("identifier2", "BBB").build();
    
    public static final Activity ACTIVITY_3 = new Activity.Builder().withLabel("Activity3").withGuid("AAA")
            .withTask("tapTest").build();

    public static final String TEST_OWNER_ID = "oneOwnerId";
    public static final String IDENTIFIER = "oneIdentifier";
    public static final Set<String> STRING_TAGS = ImmutableSet.of("tag1", "tag2");
    public static final Set<Tag> TAGS = TagUtils.toTagSet(STRING_TAGS);
    public static final DateTime CREATED_ON = TIMESTAMP.minusHours(1);
    public static final DateTime EXPORTED_ON = CREATED_ON.plusMinutes(1);
    public static final DateTime MODIFIED_ON = TIMESTAMP.plusHours(1);

    public static final PropertyInfo INFO1 = new PropertyInfo.Builder().withPropName("foo").withLabel("foo label")
            .withDescription("a description").withPropType("string").build();
    public static final PropertyInfo INFO2 = new PropertyInfo.Builder().withPropName("bar").withLabel("bar label")
            .withDescription("a description").withPropType("string").build();
    public static final PropertyInfo INFO3 = new PropertyInfo.Builder().withPropName("baz").withLabel("baz label")
            .withDescription("a description").withPropType("string").build();
    public static final PropertyInfo INFO4 = new PropertyInfo.Builder().withPropName("bop").withLabel("bop label")
            .withDescription("a description").withPropType("string").build();
    
    public static final Map<String, Set<PropertyInfo>> CUSTOMIZATION_FIELDS = ImmutableMap.of(
            "guid1", ImmutableSet.of(INFO1, INFO2), 
            "guid2", ImmutableSet.of(INFO3, INFO4)); 
    
    public static final ImmutableList<String> PUBLISHERS = ImmutableList.of("pub1", "pub2");
    public static final ImmutableList<String> CREATORS = ImmutableList.of("creator1", "creator2");
    public static final ImmutableList<String> CONTRIBUTORS = ImmutableList.of("contrib1", "contrib2");
    
    public static final AccountSummary SUMMARY1 = new AccountSummary.Builder().withFirstName("firstName1")
            .withLastName("lastName1").withEmail(EMAIL).withSynapseUserId(SYNAPSE_USER_ID).withPhone(PHONE)
            .withExternalIds(ImmutableMap.of("study1", "externalId1")).withId("id")
            .withStudyIds(ImmutableSet.of("study1", "study2")).withCreatedOn(TIMESTAMP).withStatus(DISABLED)
            .withAppId(TEST_APP_ID).withOrgMembership(TEST_ORG_ID).withNote("note1")
            .withClientTimeZone(TEST_CLIENT_TIME_ZONE).withDataGroups(USER_DATA_GROUPS)
            .withRoles(ImmutableSet.of(DEVELOPER, STUDY_COORDINATOR)).build();
    public static final AccountSummary SUMMARY2 = new AccountSummary.Builder().withFirstName("firstName2")
            .withLastName("lastName2").withEmail(EMAIL).withSynapseUserId(SYNAPSE_USER_ID).withPhone(PHONE)
            .withExternalIds(ImmutableMap.of("study2", "externalId2")).withId("id2")
            .withStudyIds(ImmutableSet.of("study1", "study2")).withCreatedOn(TIMESTAMP).withStatus(ENABLED)
            .withAppId(TEST_APP_ID).withOrgMembership(TEST_ORG_ID).withNote("note2").build();

    public static final ColorScheme COLOR_SCHEME = new ColorScheme("#000000", "#FFFFFF", "#CCEECC", "#CCCCCC");
    public static final List<Label> LABELS = ImmutableList.of(new Label("en", "English"), new Label("fr", "French"));
    public static final List<NotificationMessage> MESSAGES = ImmutableList.of(
            new NotificationMessage.Builder().withLang("en").withSubject("English").withMessage("Body").build(),
            new NotificationMessage.Builder().withLang("fr").withSubject("French").withMessage("Body").build());
    
    public static final String SCHEDULE_GUID = "AAAAAAAAAAAAAAAAAAAAAAAA";

    public static final String SESSION_GUID_1 = "BBBBBBBBBBBBBBBBBBBBBBBB";
    public static final String SESSION_GUID_2 = "CCCCCCCCCCCCCCCCCCCCCCCC";
    public static final String SESSION_GUID_3 = "DDDDDDDDDDDDDDDDDDDDDDDD";
    public static final String SESSION_GUID_4 = "EEEEEEEEEEEEEEEEEEEEEEEE";
    
    public static final String SESSION_WINDOW_GUID_1 = "FFFFFFFFFFFFFFFFFFFFFFFF";
    public static final String SESSION_WINDOW_GUID_2 = "GGGGGGGGGGGGGGGGGGGGGGGG";
    public static final String SESSION_WINDOW_GUID_3 = "HHHHHHHHHHHHHHHHHHHHHHHH";
    public static final String SESSION_WINDOW_GUID_4 = "IIIIIIIIIIIIIIIIIIIIIIII";
    
    public static final String ASSESSMENT_1_GUID = "111111111111111111111111";
    public static final String ASSESSMENT_2_GUID = "222222222222222222222222";
    public static final String ASSESSMENT_3_GUID = "333333333333333333333333";
    public static final String ASSESSMENT_4_GUID = "444444444444444444444444";
}

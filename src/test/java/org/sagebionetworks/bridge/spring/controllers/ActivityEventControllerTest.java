package org.sagebionetworks.bridge.spring.controllers;

import static org.sagebionetworks.bridge.TestConstants.ACCOUNT_ID;
import static org.sagebionetworks.bridge.TestConstants.CREATED_ON;
import static org.sagebionetworks.bridge.TestConstants.HEALTH_CODE;
import static org.sagebionetworks.bridge.TestConstants.TEST_APP_ID;
import static org.sagebionetworks.bridge.TestConstants.TEST_STUDY_ID;
import static org.sagebionetworks.bridge.TestConstants.TEST_USER_ID;
import static org.sagebionetworks.bridge.TestConstants.TIMESTAMP;
import static org.sagebionetworks.bridge.TestUtils.assertCreate;
import static org.sagebionetworks.bridge.TestUtils.assertCrossOrigin;
import static org.sagebionetworks.bridge.TestUtils.assertDelete;
import static org.sagebionetworks.bridge.TestUtils.assertGet;
import static org.sagebionetworks.bridge.TestUtils.assertPost;
import static org.sagebionetworks.bridge.TestUtils.createJson;
import static org.sagebionetworks.bridge.models.activities.ActivityEventUpdateType.IMMUTABLE;
import static org.sagebionetworks.bridge.models.activities.ActivityEventUpdateType.MUTABLE;
import static org.sagebionetworks.bridge.spring.controllers.ActivityEventController.EVENT_DELETED_MSG;
import static org.sagebionetworks.bridge.spring.controllers.ActivityEventController.EVENT_RECORDED_MSG;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import org.sagebionetworks.bridge.TestUtils;
import org.sagebionetworks.bridge.dynamodb.DynamoActivityEvent;
import org.sagebionetworks.bridge.exceptions.EntityNotFoundException;
import org.sagebionetworks.bridge.models.PagedResourceList;
import org.sagebionetworks.bridge.models.RequestInfo;
import org.sagebionetworks.bridge.models.ResourceList;
import org.sagebionetworks.bridge.models.StatusMessage;
import org.sagebionetworks.bridge.models.accounts.StudyParticipant;
import org.sagebionetworks.bridge.models.accounts.UserSession;
import org.sagebionetworks.bridge.models.activities.ActivityEvent;
import org.sagebionetworks.bridge.models.activities.StudyActivityEvent;
import org.sagebionetworks.bridge.models.activities.StudyActivityEventIdsMap;
import org.sagebionetworks.bridge.models.apps.App;
import org.sagebionetworks.bridge.models.studies.StudyCustomEvent;
import org.sagebionetworks.bridge.services.ActivityEventService;
import org.sagebionetworks.bridge.services.AppService;
import org.sagebionetworks.bridge.services.RequestInfoService;
import org.sagebionetworks.bridge.services.StudyActivityEventService;
import org.sagebionetworks.bridge.services.StudyService;

public class ActivityEventControllerTest extends Mockito {

    @Mock
    private AppService appService;
    
    @Mock
    private ActivityEventService mockActivityEventService;
    
    @Mock
    private StudyActivityEventService mockStudyActivityEventService;
    
    @Mock
    private StudyService mockStudyService;
    
    @Mock
    private RequestInfoService mockRequestInfoService;
    
    @Mock
    private HttpServletRequest mockRequest;
    
    @Mock
    private HttpServletResponse mockResponse;
    
    @InjectMocks
    @Spy
    private ActivityEventController controller = new ActivityEventController();
    
    @Captor
    ArgumentCaptor<StudyActivityEvent> eventCaptor;
    
    @Captor
    ArgumentCaptor<RequestInfo> requestInfoCaptor;

    private App app;
    
    private UserSession session;
    
    @BeforeMethod
    private void before() {
        MockitoAnnotations.initMocks(this);
        
        session = new UserSession();
        session.setAppId(TEST_APP_ID);
        session.setParticipant(new StudyParticipant.Builder()
                .withId(TEST_USER_ID)
                .withHealthCode(HEALTH_CODE)
                .build());
        
        doReturn(session).when(controller).getAuthenticatedAndConsentedSession();
        doReturn(mockRequest).when(controller).request();
        doReturn(mockResponse).when(controller).response();
        
        app = App.create();
        when(appService.getApp(TEST_APP_ID)).thenReturn(app);
    }
    
    @Test
    public void verifyAnnotations() throws Exception {
        assertCrossOrigin(ActivityEventController.class);
        assertCreate(ActivityEventController.class, "createCustomActivityEvent");
        assertGet(ActivityEventController.class, "getSelfActivityEvents");        
        assertGet(ActivityEventController.class, "getSelfActivityEvents");
        assertGet(ActivityEventController.class, "getRecentActivityEventsForSelf");
        assertGet(ActivityEventController.class, "getActivityEventHistoryForSelf");
        assertPost(ActivityEventController.class, "publishActivityEventForSelf");
        assertDelete(ActivityEventController.class, "deleteActivityEventForSelf");
    }
    
    @Test
    public void createGlobalCustomActivityEvent() throws Exception {
        String json = TestUtils.createJson("{'eventKey':'foo','timestamp':'%s'}", TIMESTAMP.toString());
        doReturn(TestUtils.toInputStream(json)).when(mockRequest).getInputStream();
        
        StatusMessage message = controller.createCustomActivityEvent();
        assertEquals(message, EVENT_RECORDED_MSG);
        
        verify(mockActivityEventService).publishCustomEvent(app, HEALTH_CODE, "foo", TIMESTAMP);
    }
    
    @Test
    public void deleteCustomActivityEvent() {
        doReturn(session).when(controller).getAuthenticatedAndConsentedSession();
        
        StatusMessage retValue = controller.deleteCustomActivityEvent("eventKey");
        assertSame(retValue, EVENT_DELETED_MSG);
        
        verify(mockActivityEventService).deleteCustomEvent(app, HEALTH_CODE, "eventKey");
    }
    
    @Test
    public void getSelfActivityEvents() throws Exception {
        DynamoActivityEvent event = new DynamoActivityEvent();
        event.setEventId("foo");
        event.setHealthCode(HEALTH_CODE);
        event.setTimestamp(TIMESTAMP);
        
        List<ActivityEvent> activityEvents = ImmutableList.of(event);
        when(mockActivityEventService.getActivityEventList(TEST_APP_ID, null, HEALTH_CODE)).thenReturn(activityEvents);
        
        ResourceList<ActivityEvent> list = controller.getSelfActivityEvents();
        ActivityEvent returnedEvent = list.getItems().get(0);
        assertEquals(returnedEvent.getEventId(), "foo");
        assertEquals(returnedEvent.getTimestamp(), TIMESTAMP);
        
        verify(mockActivityEventService).getActivityEventList(TEST_APP_ID, null, HEALTH_CODE);
    }
    
    @Test
    public void getSelfActivityEventsInStudy() throws Exception {
        session.setParticipant(new StudyParticipant.Builder()
                .withStudyIds(ImmutableSet.of(TEST_STUDY_ID))
                .withId(TEST_USER_ID).build());
        
        doReturn(session).when(controller).getAuthenticatedAndConsentedSession();
        doReturn(CREATED_ON).when(controller).getDateTime();

        ResourceList<StudyActivityEvent> page = new ResourceList<>(
                ImmutableList.of(new StudyActivityEvent.Builder().build()), true);
        when(mockStudyActivityEventService.getRecentStudyActivityEvents(
                TEST_APP_ID, TEST_USER_ID, TEST_STUDY_ID)).thenReturn(page);
        
        ResourceList<StudyActivityEvent> retList = controller.getRecentActivityEventsForSelf(TEST_STUDY_ID);
        assertEquals(retList.getItems().size(), 1);

        verify(mockStudyActivityEventService).publishEvent(eventCaptor.capture(), eq(false));
        StudyActivityEvent event = eventCaptor.getValue();
        assertEquals(event.getAppId(), TEST_APP_ID);
        assertEquals(event.getStudyId(), TEST_STUDY_ID);
        assertEquals(event.getUserId(), TEST_USER_ID);
        assertEquals(event.getEventId(), "timeline_retrieved");
        assertEquals(event.getTimestamp(), CREATED_ON);
        
        verify(mockRequestInfoService).updateRequestInfo(requestInfoCaptor.capture());
        assertEquals(requestInfoCaptor.getValue().getTimelineAccessedOn(), CREATED_ON);
    }

    @Test
    public void publishActivityEventForSelf() throws Exception {
        session.setParticipant(new StudyParticipant.Builder()
                .withStudyIds(ImmutableSet.of(TEST_STUDY_ID))
                .withId(TEST_USER_ID).build());
        
        doReturn(session).when(controller).getAuthenticatedAndConsentedSession();

        TestUtils.mockRequestBody(mockRequest, createJson(
                "{'eventKey':'eventKey','timestamp':'"+CREATED_ON+"'}"));

        StudyActivityEventIdsMap eventMap = new StudyActivityEventIdsMap();
        eventMap.addCustomEvents(ImmutableList.of(new StudyCustomEvent("eventKey", IMMUTABLE)));
        when(mockStudyService.getStudyActivityEventIdsMap(TEST_APP_ID, TEST_STUDY_ID)).thenReturn(eventMap);
        
        StatusMessage retValue = controller.publishActivityEventForSelf(TEST_STUDY_ID, null);
        assertEquals(retValue, EVENT_RECORDED_MSG);
        
        verify(mockStudyActivityEventService).publishEvent(eventCaptor.capture(), eq(false));
        StudyActivityEvent event = eventCaptor.getValue();
        assertEquals(event.getAppId(), TEST_APP_ID);
        assertEquals(event.getStudyId(), TEST_STUDY_ID);
        assertEquals(event.getUserId(), TEST_USER_ID);
        assertEquals(event.getEventId(), "custom:eventKey");
        assertEquals(event.getTimestamp(), CREATED_ON);
    }
    
    @Test
    public void deleteSelfActivityEventInStudy() throws Exception {
        session.setParticipant(new StudyParticipant.Builder()
                .withStudyIds(ImmutableSet.of(TEST_STUDY_ID))
                .withId(TEST_USER_ID).build());
        
        doReturn(session).when(controller).getAuthenticatedAndConsentedSession();
        
        StudyActivityEventIdsMap map = new StudyActivityEventIdsMap();
        map.addCustomEvents(ImmutableList.of(new StudyCustomEvent("eventKey", MUTABLE)));
        when(mockStudyService.getStudyActivityEventIdsMap(TEST_APP_ID, TEST_STUDY_ID))
            .thenReturn(map);

        StatusMessage retValue = controller.deleteActivityEventForSelf(TEST_STUDY_ID, "eventKey", null);
        assertEquals(retValue, EVENT_DELETED_MSG);
        
        verify(mockStudyActivityEventService).deleteEvent(eventCaptor.capture(), eq(false));
        StudyActivityEvent event = eventCaptor.getValue();
        assertEquals(event.getAppId(), TEST_APP_ID);
        assertEquals(event.getStudyId(), TEST_STUDY_ID);
        assertEquals(event.getUserId(), TEST_USER_ID);
        assertEquals(event.getEventId(), "custom:eventKey");
    }
    
    @Test
    public void getActivityEventHistoryForSelf() throws Exception {
        doReturn(session).when(controller).getAuthenticatedAndConsentedSession();
        session.setParticipant(new StudyParticipant.Builder()
                .withStudyIds(ImmutableSet.of(TEST_STUDY_ID))
                .withId(TEST_USER_ID).build());
        
        StudyActivityEvent event = new StudyActivityEvent.Builder().build();
        
        List<StudyActivityEvent> list = ImmutableList.of(event, event);
        PagedResourceList<StudyActivityEvent> page = new PagedResourceList<StudyActivityEvent>(list, 100, true);
        when(mockStudyActivityEventService.getStudyActivityEventHistory(any(), any(), any(), any(), any()))
            .thenReturn(page);

        ResourceList<StudyActivityEvent> retValue = controller.getActivityEventHistoryForSelf(
                TEST_STUDY_ID, "eventKey", "100", "200");
        assertSame(retValue, page);
        
        verify(mockStudyActivityEventService).getStudyActivityEventHistory(
                ACCOUNT_ID, TEST_STUDY_ID, "eventKey", Integer.valueOf(100), Integer.valueOf(200));
    }
    
    @Test(expectedExceptions = EntityNotFoundException.class,
            expectedExceptionsMessageRegExp = "Account not found.")
    public void getSelfActivityEventsInStudy_notInStudy() throws Exception {
        doReturn(session).when(controller).getAuthenticatedAndConsentedSession();

        controller.getRecentActivityEventsForSelf(TEST_STUDY_ID);
    }

    @Test(expectedExceptions = EntityNotFoundException.class,
            expectedExceptionsMessageRegExp = "Account not found.")
    public void getActivityEventHistoryForSelfInStudy_notFound() throws Exception {
        doReturn(session).when(controller).getAuthenticatedAndConsentedSession();
        
        controller.getActivityEventHistoryForSelf(TEST_STUDY_ID, "eventKey", "100", "200");
    }
   
    @Test
    public void getActivityEventHistoryForSelfInStudy_withDefaults() throws Exception {
        doReturn(session).when(controller).getAuthenticatedAndConsentedSession();
        session.setParticipant(new StudyParticipant.Builder()
                .withStudyIds(ImmutableSet.of(TEST_STUDY_ID))
                .withId(TEST_USER_ID).build());
        
        StudyActivityEvent event = new StudyActivityEvent.Builder().build();
        
        List<StudyActivityEvent> list = ImmutableList.of(event, event);
        PagedResourceList<StudyActivityEvent> page = new PagedResourceList<StudyActivityEvent>(list, 100, true);
        when(mockStudyActivityEventService.getStudyActivityEventHistory(any(), any(), any(), any(), any()))
            .thenReturn(page);

        ResourceList<StudyActivityEvent> retValue = controller.getActivityEventHistoryForSelf(
                TEST_STUDY_ID, "eventKey", null, null);
        assertSame(retValue, page);
        
        verify(mockStudyActivityEventService).getStudyActivityEventHistory( 
                ACCOUNT_ID, TEST_STUDY_ID, "eventKey", Integer.valueOf(0), Integer.valueOf(50));
    }
    
    @Test(expectedExceptions = EntityNotFoundException.class,
            expectedExceptionsMessageRegExp = "Account not found.")
    public void publishActivityEventForSelf_notInStudy() throws Exception {
        doReturn(session).when(controller).getAuthenticatedAndConsentedSession();

        TestUtils.mockRequestBody(mockRequest, createJson(
                "{'eventKey':'eventKey','timestamp':'"+CREATED_ON+"'}"));
        
        controller.publishActivityEventForSelf(TEST_STUDY_ID, null);
    }
    
    @Test
    public void publishActivityEventForSelf_showError() throws Exception {
        session.setParticipant(new StudyParticipant.Builder()
                .withStudyIds(ImmutableSet.of(TEST_STUDY_ID))
                .withId(TEST_USER_ID).build());
        
        doReturn(session).when(controller).getAuthenticatedAndConsentedSession();

        TestUtils.mockRequestBody(mockRequest, createJson(
                "{'eventKey':'eventKey','timestamp':'"+CREATED_ON+"'}"));

        StudyActivityEventIdsMap eventMap = new StudyActivityEventIdsMap();
        eventMap.addCustomEvents(ImmutableList.of(new StudyCustomEvent("eventKey", IMMUTABLE)));
        when(mockStudyService.getStudyActivityEventIdsMap(TEST_APP_ID, TEST_STUDY_ID)).thenReturn(eventMap);
        
        controller.publishActivityEventForSelf(TEST_STUDY_ID, "true");
        
        verify(mockStudyActivityEventService).publishEvent(eventCaptor.capture(), eq(true));
    }
    
    @Test(expectedExceptions = EntityNotFoundException.class,
            expectedExceptionsMessageRegExp = "Account not found.")
    public void deleteSelfActivityEventInStudy_notInStudy() throws Exception {
        doReturn(session).when(controller).getAuthenticatedAndConsentedSession();
        
        controller.deleteActivityEventForSelf(TEST_STUDY_ID, "eventKey", null);
    }
    
    @Test
    public void deleteSelfActivityEventInStudy_throwError() throws Exception {
        doReturn(session).when(controller).getAuthenticatedAndConsentedSession();
        
        session.setParticipant(new StudyParticipant.Builder()
                .withId(TEST_USER_ID)
                .withStudyIds(ImmutableSet.of(TEST_STUDY_ID))
                .withHealthCode(HEALTH_CODE)
                .build());
        
        StudyActivityEventIdsMap map = new StudyActivityEventIdsMap();
        when(mockStudyService.getStudyActivityEventIdsMap(any(), any())).thenReturn(map);
        
        controller.deleteActivityEventForSelf(TEST_STUDY_ID, "eventKey", "true");
        
        verify(mockStudyActivityEventService).deleteEvent(any(), eq(true));
    }
    
}

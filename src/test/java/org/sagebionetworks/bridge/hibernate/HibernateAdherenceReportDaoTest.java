package org.sagebionetworks.bridge.hibernate;

import static org.sagebionetworks.bridge.TestConstants.TEST_APP_ID;
import static org.sagebionetworks.bridge.TestConstants.TEST_STUDY_ID;
import static org.sagebionetworks.bridge.hibernate.HibernateAdherenceReportDao.COMPLIANCE_UNDER_FIELD;
import static org.sagebionetworks.bridge.hibernate.HibernateAdherenceReportDao.LABEL_FILTER_FIELD;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertSame;

import java.util.List;
import java.util.Map;

import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.newrelic.agent.deps.com.google.common.collect.ImmutableList;

import org.mockito.MockitoAnnotations;
import org.sagebionetworks.bridge.models.PagedResourceList;
import org.sagebionetworks.bridge.models.schedules2.adherence.weekly.WeeklyAdherenceReport;

public class HibernateAdherenceReportDaoTest extends Mockito {
    
    private static String FULL_COUNT_SQL = "SELECT COUNT(*) FROM WeeklyAdherenceReport h "
            +"JOIN h.labels label WHERE h.appId = :appId AND h.studyId = :studyId AND "
            +"weeklyAdherencePercent < :complianceUnder AND label LIKE :labelFilter ORDER BY weeklyAdherencePercent";
    
    private static String FULL_QUERY_SQL = "SELECT h FROM WeeklyAdherenceReport h JOIN h.labels "
            +"label WHERE h.appId = :appId AND h.studyId = :studyId AND weeklyAdherencePercent < "
            +":complianceUnder AND label LIKE :labelFilter ORDER BY weeklyAdherencePercent";

    private static String NO_LABEL_FILTER_COUNT_SQL = "SELECT COUNT(*) FROM WeeklyAdherenceReport h "
            +"WHERE h.appId = :appId AND h.studyId = :studyId AND weeklyAdherencePercent < :complianceUnder "
            +"ORDER BY weeklyAdherencePercent";
    
    private static String NO_LABEL_FILTER_QUERY_SQL = "SELECT h FROM WeeklyAdherenceReport h WHERE "
            +"h.appId = :appId AND h.studyId = :studyId AND weeklyAdherencePercent < :complianceUnder "
            +"ORDER BY weeklyAdherencePercent";
    
    private static String NO_ADHERENCE_COUNT_SQL = "SELECT COUNT(*) FROM WeeklyAdherenceReport h "
            +"JOIN h.labels label WHERE h.appId = :appId AND h.studyId = :studyId AND label LIKE "
            +":labelFilter ORDER BY weeklyAdherencePercent";
    
    private static String NO_ADHERENCE_QUERY_SQL = "SELECT h FROM WeeklyAdherenceReport h JOIN h.labels "
            +"label WHERE h.appId = :appId AND h.studyId = :studyId AND label LIKE :labelFilter ORDER BY "
            +"weeklyAdherencePercent";

    @Mock
    HibernateHelper mockHelper;
    
    @Captor
    ArgumentCaptor<String> stringCaptor;
    
    @Captor
    ArgumentCaptor<Map<String,Object>> paramsCaptor;
    
    @InjectMocks
    HibernateAdherenceReportDao dao;
    
    @BeforeMethod
    public void beforeMethod() {
        MockitoAnnotations.initMocks(this);
    }
    
    @Test
    public void saveWeeklyAdherenceReport() {
        WeeklyAdherenceReport report = new WeeklyAdherenceReport();
        dao.saveWeeklyAdherenceReport(report);
        verify(mockHelper).saveOrUpdate(report);
    }
    
    @Test
    public void getWeeklyAdherenceReports() {
        List<WeeklyAdherenceReport> reports = ImmutableList.of();
        when(mockHelper.queryCount(any(), any())).thenReturn(1000);
        when(mockHelper.queryGet(any(), any(), eq(50), eq(100), eq(WeeklyAdherenceReport.class))).thenReturn(reports);

        PagedResourceList<WeeklyAdherenceReport> retValue = dao.getWeeklyAdherenceReports(
                TEST_APP_ID, TEST_STUDY_ID, "label", 75, 50, 100);
        
        assertEquals(retValue.getTotal(), Integer.valueOf(1000));
        assertSame(retValue.getItems(), reports);

        verify(mockHelper).queryCount(stringCaptor.capture(), paramsCaptor.capture());
        verify(mockHelper).queryGet(stringCaptor.capture(), paramsCaptor.capture(), eq(50), eq(100),
                eq(WeeklyAdherenceReport.class));
        
        assertEquals(stringCaptor.getAllValues().get(0), FULL_COUNT_SQL);
        assertEquals(stringCaptor.getAllValues().get(1), FULL_QUERY_SQL);
        assertEquals(paramsCaptor.getValue().get(LABEL_FILTER_FIELD), "%label%");
        assertEquals(paramsCaptor.getValue().get(COMPLIANCE_UNDER_FIELD), 75);
    }
    
    @Test
    public void getWeeklyAdherenceReports_withoutLabelFilter() {
        dao.getWeeklyAdherenceReports(TEST_APP_ID, TEST_STUDY_ID, null, 75, 50, 100);
        
        verify(mockHelper).queryCount(stringCaptor.capture(), paramsCaptor.capture());
        verify(mockHelper).queryGet(stringCaptor.capture(), paramsCaptor.capture(), eq(50), eq(100),
                eq(WeeklyAdherenceReport.class));
        
        assertEquals(stringCaptor.getAllValues().get(0), NO_LABEL_FILTER_COUNT_SQL);
        assertEquals(stringCaptor.getAllValues().get(1), NO_LABEL_FILTER_QUERY_SQL);
        assertNull(paramsCaptor.getValue().get(LABEL_FILTER_FIELD));
    }

    @Test
    public void getWeeklyAdherenceReports_withoutAdherenceUnder() {
        dao.getWeeklyAdherenceReports(TEST_APP_ID, TEST_STUDY_ID, "label", null, null, null);
        
        verify(mockHelper).queryCount(stringCaptor.capture(), paramsCaptor.capture());
        verify(mockHelper).queryGet(stringCaptor.capture(), paramsCaptor.capture(), eq(null), eq(null),
                eq(WeeklyAdherenceReport.class));
        
        assertEquals(stringCaptor.getAllValues().get(0), NO_ADHERENCE_COUNT_SQL);
        assertEquals(stringCaptor.getAllValues().get(1), NO_ADHERENCE_QUERY_SQL);
        assertNull(paramsCaptor.getValue().get(COMPLIANCE_UNDER_FIELD));
    }
}

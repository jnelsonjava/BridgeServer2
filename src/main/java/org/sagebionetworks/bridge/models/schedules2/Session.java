package org.sagebionetworks.bridge.models.schedules2;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OrderColumn;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.joda.time.Period;

import org.sagebionetworks.bridge.hibernate.LabelListConverter;
import org.sagebionetworks.bridge.hibernate.PeriodToStringConverter;
import org.sagebionetworks.bridge.hibernate.StringListConverter;
import org.sagebionetworks.bridge.json.BridgeTypeName;
import org.sagebionetworks.bridge.models.BridgeEntity;
import org.sagebionetworks.bridge.models.Label;

@Entity
@Table(name = "Sessions")
@BridgeTypeName("Session")
public class Session implements BridgeEntity, HasGuid {
    
    @ManyToOne
    @JoinColumn(name = "scheduleGuid", nullable = false)
    @JsonIgnore
    private Schedule2 schedule;
    
    @Id
    private String guid;
    @JsonIgnore
    private int position;
    private String name;
    private String symbol;
    @Convert(converter = PeriodToStringConverter.class)
    @Column(name = "delayPeriod")
    private Period delay;
    private Integer occurrences;
    @Convert(converter = PeriodToStringConverter.class)
    @Column(name = "intervalPeriod")
    private Period interval;
    @Enumerated(EnumType.STRING)
    private PerformanceOrder performanceOrder;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "SessionAssessments", 
        joinColumns = @JoinColumn(name = "sessionGuid", nullable = false))
    @OrderColumn(name = "position")
    private List<AssessmentReference> assessments;
    
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "SessionTimeWindows", 
        joinColumns = @JoinColumn(name = "sessionGuid", nullable = false))
    @OrderColumn(name = "position")
    private List<TimeWindow> timeWindows;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "SessionNotifications", 
        joinColumns = @JoinColumn(name = "sessionGuid", nullable = false))
    @OrderColumn(name = "position")
    private List<Notification> notifications;

    @Convert(converter = StringListConverter.class)
    private List<String> studyBurstIds;
    
    @Column(columnDefinition = "text", name = "labels", nullable = true)
    @Convert(converter = LabelListConverter.class)
    private List<Label> labels;
    
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "SessionStartEvents", 
        joinColumns = @JoinColumn(name = "sessionGuid", nullable = false))
    @OrderColumn(name = "position")
    @Column(name = "eventId")
    private List<String> startEventIds;

    public Schedule2 getSchedule() {
        return schedule;
    }
    public void setSchedule(Schedule2 schedule) {
        this.schedule = schedule;
    }
    public String getGuid() {
        return guid;
    }
    public void setGuid(String guid) {
        this.guid = guid;
    }
    public int getPosition() {
        return position;
    }
    public void setPosition(int position) {
        this.position = position;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getSymbol() {
        return symbol;
    }
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
    public List<Label> getLabels() {
        if (labels == null) {
            labels = new ArrayList<>();
        }
        return labels;
    }
    public void setLabels(List<Label> labels) {
        this.labels = labels;
    }
    public List<String> getStartEventIds() {
        if (startEventIds == null) {
            startEventIds = new ArrayList<>();
        }
        return startEventIds;
    }
    public void setStartEventIds(List<String> startEventIds) {
        this.startEventIds = startEventIds;
    }
    public List<String> getStudyBurstIds() { 
        if (studyBurstIds == null) { 
            studyBurstIds = new ArrayList<>();
        }
        return studyBurstIds;
    }
    public void setStudyBurstIds(List<String> studyBurstIds) {
       this.studyBurstIds = studyBurstIds;
    }
    public Period getDelay() {
        return delay;
    }
    public void setDelay(Period delay) {
        this.delay = delay;
    }
    public Integer getOccurrences() {
        return occurrences;
    }
    public void setOccurrences(Integer occurrences) {
        this.occurrences = occurrences;
    }
    public Period getInterval() {
        return interval;
    }
    public void setInterval(Period interval) {
        this.interval = interval;
    }
    public PerformanceOrder getPerformanceOrder() {
        return performanceOrder;
    }
    public void setPerformanceOrder(PerformanceOrder performanceOrder) {
        this.performanceOrder = performanceOrder;
    }
    public List<AssessmentReference> getAssessments() {
        if (assessments == null) {
            assessments = new ArrayList<>();
        }
        return assessments;
    }
    public void setAssessments(List<AssessmentReference> assessments) {
        this.assessments = assessments;
    }
    public List<TimeWindow> getTimeWindows() {
        if (timeWindows == null) {
            timeWindows = new ArrayList<>();
        }
        return timeWindows;
    }
    public void setTimeWindows(List<TimeWindow> timeWindows) {
        this.timeWindows = timeWindows;
    }
    public List<Notification> getNotifications() {
        if (notifications == null) {
            notifications = new ArrayList<>();
        }
        return notifications;
    }
    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
    }
}

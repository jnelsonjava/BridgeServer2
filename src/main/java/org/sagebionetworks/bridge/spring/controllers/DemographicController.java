package org.sagebionetworks.bridge.spring.controllers;

import static org.sagebionetworks.bridge.Roles.RESEARCHER;
import static org.sagebionetworks.bridge.Roles.STUDY_COORDINATOR;

import org.sagebionetworks.bridge.models.StatusMessage;
import org.sagebionetworks.bridge.models.accounts.UserSession;
import org.sagebionetworks.bridge.models.accounts.DemographicList;
import org.sagebionetworks.bridge.models.accounts.Demographic;
import org.sagebionetworks.bridge.models.accounts.DemographicId;
import org.sagebionetworks.bridge.models.accounts.DemographicCategory;
import org.sagebionetworks.bridge.models.accounts.ParticipantDemographics;
import org.sagebionetworks.bridge.services.DemographicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
public class DemographicController extends BaseController {

    private DemographicService demographicService;

    @Autowired
    final void setDemographicService(DemographicService demographicService) {
        this.demographicService = demographicService;
    }

    @PostMapping("/v3/participant/{userId}/demographics")
    public StatusMessage createDemographic(@PathVariable String userId) {
        // TODO: Verify what permissions should be required
        UserSession session = getAuthenticatedSession(RESEARCHER, STUDY_COORDINATOR);

//        Demographic demographic = parseJson(Demographic.class);

        DemographicList demographicList = parseJson(DemographicList.class);

//        Demographic demographic = demographicList.getDemographics().get(0);

        for (Demographic demographic : demographicList.getDemographics()) {
            demographic.setAppId(session.getAppId());
            demographic.setUserId(userId);
        }

        demographicService.createDemographics(demographicList);

        return new StatusMessage("Demographic created.");
    }

//    @PostMapping("/v3/participant/{userId}/demographics/update")
//    public StatusMessage updateDemographic(@PathVariable String userId) {
//        UserSession session = getAuthenticatedSession(RESEARCHER, STUDY_COORDINATOR);
//
//        Demographic demographic = parseJson(Demographic.class);
//        demographic.setAppId(session.getAppId());
//        demographic.setUserId(userId);
//        demographicService.saveDemographic(demographic);
//
//        return new StatusMessage("Demographic updated.");
//    }

    @DeleteMapping("/v3/participant/{userId}/demographics/{category}")
    public StatusMessage deleteDemographic(@PathVariable String userId, @PathVariable String category) {
        UserSession session = getAuthenticatedSession(RESEARCHER, STUDY_COORDINATOR);

        DemographicId demographicId = new DemographicId(userId, DemographicCategory.valueOf(category));

        demographicService.deleteDemographic(demographicId);

        return new StatusMessage("Demographic deleted.");
    }

    @GetMapping("/v3/participant/{userId}/demographics/{category}")
    public Demographic getParticipantDemographic(@PathVariable String userId, @PathVariable String category) {
        UserSession session = getAuthenticatedSession(RESEARCHER, STUDY_COORDINATOR);

        DemographicId demographicId = new DemographicId(userId, DemographicCategory.valueOf(category));

        return demographicService.getDemographic(demographicId);
    }

    @GetMapping("/v3/participant/{userId}/demographics")
    public ParticipantDemographics getParticipantDemographicSummary(@PathVariable String userId) {
        UserSession session = getAuthenticatedSession(RESEARCHER, STUDY_COORDINATOR);

        // TODO: Validate userId, can it be checked without hitting the database?

        return demographicService.getParticipantDemographics(userId);
    }
}

package org.sagebionetworks.bridge.spring.controllers;

import static org.sagebionetworks.bridge.Roles.RESEARCHER;
import static org.sagebionetworks.bridge.Roles.STUDY_COORDINATOR;

import org.sagebionetworks.bridge.models.StatusMessage;
import org.sagebionetworks.bridge.models.accounts.Demographic;
import org.sagebionetworks.bridge.models.accounts.UserSession;
import org.sagebionetworks.bridge.services.DemographicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

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
        UserSession session = getAuthenticatedSession(RESEARCHER, STUDY_COORDINATOR);

        Demographic demographic = parseJson(Demographic.class);
        demographic.setAppId(session.getAppId());
        demographic.setUserId(userId);
        System.out.println("-------CONTROLLER---------");
        System.out.println(demographic.getAppId());
        System.out.println(demographic.getUserId());
        System.out.println(demographic.getCategory());
        System.out.println(demographic.getAnswerValue());
        demographicService.createDemographic(demographic);

        return new StatusMessage("Demographic created.");
    }
}

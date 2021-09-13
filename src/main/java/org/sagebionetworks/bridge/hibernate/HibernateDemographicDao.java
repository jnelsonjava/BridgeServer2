package org.sagebionetworks.bridge.hibernate;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableMap;
import org.sagebionetworks.bridge.dao.DemographicDao;
import org.sagebionetworks.bridge.models.accounts.Demographic;
import org.sagebionetworks.bridge.models.accounts.DemographicId;
import org.sagebionetworks.bridge.models.accounts.DemographicList;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import java.util.List;
import java.util.Map;

@Component
public class HibernateDemographicDao implements DemographicDao {

    private HibernateHelper hibernateHelper;

    @Resource(name = "mysqlHibernateHelper")
    final void setHibernateHelper(HibernateHelper hibernateHelper) {
        this.hibernateHelper = hibernateHelper;
    }

    @Override
    public void saveDemographic(Demographic demographic) {
        checkNotNull(demographic);

        hibernateHelper.saveOrUpdate(demographic);
    }

    @Override
    public void deleteDemographic(DemographicId demographicId) {
        checkNotNull(demographicId);

        hibernateHelper.deleteById(Demographic.class, demographicId);
    }

    @Override
    public Demographic getDemographic(DemographicId demographicId) {
        checkNotNull(demographicId);

        return hibernateHelper.getById(Demographic.class, demographicId);
    }

    @Override
    public DemographicList getParticipantDemographicList(String userId) {

        Map<String, Object> parameters = ImmutableMap.of("userId", userId);
        List<Demographic> demographicList = hibernateHelper.queryGet("select demo from Demographic demo where userId=:userId",
                parameters,
                null,
                null,
                Demographic.class);

        return new DemographicList(demographicList);
    }
}

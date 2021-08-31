package org.sagebionetworks.bridge.hibernate;

import static com.google.common.base.Preconditions.checkNotNull;

import org.sagebionetworks.bridge.dao.DemographicDao;
import org.sagebionetworks.bridge.models.accounts.Demographic;
import org.sagebionetworks.bridge.models.accounts.DemographicId;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class HibernateDemographicDao implements DemographicDao {

    private HibernateHelper hibernateHelper;

    @Resource(name = "mysqlHibernateHelper")
    final void setHibernateHelper(HibernateHelper hibernateHelper) {
        this.hibernateHelper = hibernateHelper;
    }

    @Override
    public void createDemographic(Demographic demographic) {
        checkNotNull(demographic);

        System.out.println(demographic.getCategory());

        hibernateHelper.create(demographic);
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
}

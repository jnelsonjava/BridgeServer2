package org.sagebionetworks.bridge.hibernate;

import static com.google.common.base.Preconditions.checkNotNull;

import org.sagebionetworks.bridge.dao.DemographicDao;
import org.sagebionetworks.bridge.models.accounts.Demographic;
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

        hibernateHelper.create(demographic);
    }
}

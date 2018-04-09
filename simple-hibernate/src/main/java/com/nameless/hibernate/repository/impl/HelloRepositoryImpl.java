package com.nameless.hibernate.repository.impl;

import com.nameless.hibernate.base.BaseHibernateRepository;
import com.nameless.hibernate.entity.CCPDataSource;
import com.nameless.hibernate.repository.HelloRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Thinkpad on 2018/4/8.
 */
@Repository
public class HelloRepositoryImpl extends BaseHibernateRepository<CCPDataSource> implements HelloRepository {

    @Override
    public void save(CCPDataSource entity) {
        super.save(entity);
    }

    @Override
    public void delete(CCPDataSource entity) {
        super.delete(entity);
    }
}

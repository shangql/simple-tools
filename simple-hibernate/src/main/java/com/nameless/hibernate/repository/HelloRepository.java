package com.nameless.hibernate.repository;

import com.nameless.hibernate.entity.CCPDataSource;

/**
 * Created by Thinkpad on 2018/4/8.
 */
public interface HelloRepository {

    void save(CCPDataSource entity);

    void delete(CCPDataSource entity);
}

/**
 * Copyright (c) 2015-2018 ehsure
 */
package com.nameless.hibernate.base;

import com.nameless.hibernate.dto.PageReq;
import com.nameless.hibernate.dto.PageResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.persistence.PersistenceException;
import javax.persistence.TransactionRequiredException;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 仓储接口
 */
public interface BaseCrudRepository<E extends Serializable> {
    /**
     * Synchronize the persistence context to the
     * underlying database.
     *
     * @throws TransactionRequiredException if there is
     *                                      no transaction
     * @throws PersistenceException         if the flush fails
     */
    void flush();

    /**
     * Clear the persistence context, causing all managed
     * entities to become detached. Changes made to entities that
     * have not been flushed to the database will not be
     * persisted.
     */
    void clear();

    /**
     * Remove the given entity from the persistence context, causing
     * a managed entity to become detached.  Unflushed changes made
     * to the entity if any (including removal of the entity),
     * will not be synchronized to the database.  Entities which
     * previously referenced the detached entity will continue to
     * reference it.
     *
     * @param entity entity instance
     * @throws IllegalArgumentException if the instance is not an
     *                                  entity
     * @since Java Persistence 2.0
     */
    void detach(E entity);

    /**
     * 将entity从Session中脱离
     *
     * @param entities 实体集合
     */
    void detachAll(Collection<E> entities);

    /**
     * 不确定entity的状态时候保存
     *
     * @param entity 实体
     */
    void save(E entity);

    /**
     * 不确定entity的状态时保存，
     *
     * @param entities 实体集合
     */
    void save(Collection<E> entities);

    /**
     * 持久化新对象
     *
     * @param entity 实体
     */
    void insert(E entity);

    /**
     * 持久化一组新对象
     *
     * @param entities 实体集合
     */
    void insert(Collection<E> entities);

    /**
     * 删除实体对象
     *
     * @param entity 实体
     */
    void delete(E entity);

    /**
     * 根据ID删除实体对象，内部还是根据id去load对象，然后删除
     *
     * @param id 实体ID
     */
    void delete(Object id);

    /**
     * 根据ID从数据库获取对象
     *
     * @param id 实体ID
     * @return 实体
     */
    E load(Object id);

    /**
     * 分页获取对象集合
     *
     * @param hql          查询语句
     * @param conditionMap 查询条件键值对
     * @param pageRequest  分页请求参数对象
     * @return 分页对象
     */
    Page<Object[]> findResultObjects(final String hql, final Map<String, Object> conditionMap, final Pageable pageRequest);

    /**
     * 分页获取数据集合，元素为Map
     *
     * @param hql          查询语句
     * @param conditionMap 条件键值对
     * @param pageRequest  分页参数
     * @return 分页结果
     */
    Page<Map> findResultMapsSql(final String hql, final Map<String, Object> conditionMap, final Pageable pageRequest);

    /**
     * 分页获取数据集合，元素为Map
     *
     * @param hql         查询语句
     * @param values      条件值数组
     * @param pageRequest 分页参数
     * @return 分页结果
     */
    Page<Map> findResultMapsSql(final String hql, final Object[] values, final Pageable pageRequest);

    /**
     * 获取数据集合，元素为map
     *
     * @param sql          查询语句
     * @param conditionMap 条件键值对
     * @return 数据集合
     */
    List<Map> findAllResutlMapsBySql(final String sql, final Map<String, Object> conditionMap);

    /**
     * 通用分页获取数据集合，元素为map
     *
     * @param pageReq 分页请求参数对象
     * @param sql     查询语句
     * @return 分页结果
     */
    PageResult<Map> getSimplePageList(PageReq pageReq, String sql);

    /**
     * 通用分页获取数据集合，元素为map
     *
     * @param pageReq 分页请求参数对象
     * @return 分页结果
     */
    PageResult<Map> getSimplePageList(PageReq pageReq);

    /**
     * 获取前N个对象
     *
     * @param hql          hql
     * @param conditionMap 参数值map
     * @param size         前多少个
     * @return 数据集合
     */
    List<E> findTopNEntityObjects(final String hql, final Map<String, Object> conditionMap, int size);
}
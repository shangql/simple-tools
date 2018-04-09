package com.nameless.hibernate.base;

import com.nameless.hibernate.dto.ConditionItem;
import com.nameless.hibernate.dto.PageReq;
import com.nameless.hibernate.dto.PageResult;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.dom4j.tree.AbstractEntity;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.hql.internal.ast.QueryTranslatorImpl;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.lang.reflect.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 基于Hibernate的仓储基类
 *
 * @param <E> 实体类型
 */
public abstract class BaseHibernateRepository<E extends Serializable> implements BaseCrudRepository<E> {
    @PersistenceContext
    protected EntityManager em;
    private static SessionFactory sf;
    protected Class<E> entityClass = null;
    private static Logger logger = LoggerFactory.getLogger(BaseHibernateRepository.class);

    @SuppressWarnings("unchecked")
    public BaseHibernateRepository() {
        if (getClass().getGenericSuperclass() instanceof ParameterizedType) {
            if (!(((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0] instanceof TypeVariable)) {
                entityClass = (Class<E>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
            }
        }
    }

    @Override
    public void flush() {
        em.flush();
    }

    @Override
    public void clear() {
        em.clear();
    }

    @Override
    public void detach(E entity) {
        em.detach(entity);
    }

    /**
     * 将entity从Session中脱离
     *
     * @param entities 实体集合
     */
    @Override
    public void detachAll(Collection<E> entities) {
        entities.forEach(this::detach);
    }

    /**
     * 不确定entity的状态时候保存
     *
     * @param entity 实体
     */
    @Override
    public void save(E entity) {
        if (!em.contains(entity)) {
            em.merge(entity);
        }
    }

    /**
     * 不确定entity的状态时保存，
     *
     * @param entities 实体集合
     */
    @Override
    public void save(Collection<E> entities) {
        entities.forEach(this::save);
    }

    /**
     * 已确定entity为New状态，直接插入记录到数据库
     *
     * @param entity 实体
     */
    @Override
    public void insert(E entity) {
        em.persist(entity);
    }

    /**
     * 已确定entity为New状态，直接插入记录到数据库
     *
     * @param entities 实体集合
     */
    @Override
    public void insert(Collection<E> entities) {
        entities.forEach(this::insert);
    }

    /**
     * 删除
     *
     * @param entity 实体
     */
    @Override
    public void delete(E entity) {
        boolean bl = em.contains(entity);
        em.remove(em.contains(entity) ? entity : em.merge(entity));
    }

    /**
     * 根据ID删除
     *
     * @param id 实体ID
     */
    @Override
    public void delete(Object id) {
        delete(load(id));
    }

    /**
     * 查询一个实体
     *
     * @param id 实体ID
     * @return 实体
     */
    @Override
    public E load(Object id) {
        return em.find(entityClass, id);
    }

    /**
     * HQL查询语句返回单个Entity对象，使用位置参数（positional parameter）
     *
     * @param hql    hql语句
     * @param values 参数值数组
     * @return 实体
     */
    protected E findOneEntityObject(final String hql, final Object[] values) {
        return findOneEntityObject(hql, null, values);
    }

    /**
     * 根据HQL查询语句返回单个Entity对象，使用命名参数（named parameter）
     *
     * @param hql          hql语句
     * @param conditionMap 参数值Map
     * @return 实体
     */
    protected E findOneEntityObject(final String hql, final Map<String, Object> conditionMap) {
        return findOneEntityObject(hql, conditionMap, null);
    }

    /**
     * 根据HQL查询语句返回单个Entity对象
     *
     * @param hql    hql语句
     * @param map    参数值map，和values二选一
     * @param values 参数值数组，和map二选一
     * @return 实体
     */
    @SuppressWarnings("unchecked")
    private E findOneEntityObject(final String hql, final Map<String, Object> map, final Object[] values) {
        if (hql != null) {
            Query query = em.createQuery(hql, entityClass);
            setParameters(query, map, values);
            try {
                return (E) query.getSingleResult();
            } catch (NoResultException noResultException) {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * HQL查询语句返回Entity对象列表，使用位置参数（positional parameter）
     *
     * @param hql    hql语句
     * @param values 参数值数组
     * @return 实体集合
     */
    protected List<E> findEntityObjects(final String hql, final Object[] values) {
        return findAllEntityObjects(hql, null, values);
    }

    /**
     * HQL查询语句返回Entity对象列表，使用命名参数（named parameter）
     *
     * @param hql          hql语句
     * @param conditionMap 参数值map
     * @return 实体集合
     */
    protected List<E> findEntityObjects(final String hql, final Map<String, Object> conditionMap) {
        return findAllEntityObjects(hql, conditionMap, null);
    }

    /**
     * HQL查询语句返回Entity对象列表
     *
     * @param hql          hql语句
     * @param conditionMap 参数值map和values二选一
     * @param values       参数值数组，和map二选一
     * @return 实体集合
     */
    @SuppressWarnings("unchecked")
    private List<E> findAllEntityObjects(final String hql, final Map<String, Object> conditionMap, final Object[] values) {
        if (hql != null) {
            Query query = em.createQuery(hql, entityClass);
            setParameters(query, conditionMap, values);
            return query.getResultList();
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * HQL查询语句返回查询结果对象，对象为非Entity对象，若多个字段的情况下，返回Object[]，使用位置参数（positional parameter）
     *
     * @param hql    hql语句
     * @param values 参数值数组
     * @return 实体
     */
    protected Object findOneResultObject(final String hql, final Object[] values) {
        return findOneResultObject(hql, null, values);
    }

    /**
     * HQL查询语句返回查询结果对象，对象为非Entity对象，若多个字段的情况下，返回Object[]，使用命名参数（named parameter）
     *
     * @param hql          hql语句
     * @param conditionMap 参数值map
     * @return 实体
     */
    protected Object findOneResultObject(final String hql, final Map<String, Object> conditionMap) {
        return findOneResultObject(hql, conditionMap, null);
    }

    /**
     * 查询单个实体
     *
     * @param hql          hql语句
     * @param conditionMap 参数值map，和values二选一
     * @param values       参数值数组，和map二选一
     * @return 实体
     */
    private Object findOneResultObject(final String hql,
                                       final Map<String, Object> conditionMap, final Object[] values) {
        if (hql != null) {
            Query query = em.createQuery(hql);
            setParameters(query, conditionMap, values);
            return query.getSingleResult();
        } else {
            return null;
        }
    }

    /**
     * HQL查询语句返回查询结果对象列表，列表中的对象为非Entity对象，若多个字段的情况下，返回Object[]的列表，使用位置参数（positional parameter）
     *
     * @param hql    hql语句
     * @param values 参数值数组
     * @return 实体集合
     */
    protected List<?> findResultObjects(final String hql, final Object[] values) {
        return findAllResultObjects(hql, null, values);
    }

    /**
     * HQL查询语句返回查询结果对象列表，列表中的对象为非Entity对象，若多个字段的情况下，返回Object[]的列表，使用命名参数（named parameter）
     *
     * @param hql          hql语句
     * @param conditionMap 参数值map
     * @return 实体集合
     */
    protected List<?> findResultObjects(final String hql, final Map<String, Object> conditionMap) {
        return findAllResultObjects(hql, conditionMap, null);
    }

    /**
     * 查询满足条件的所有实体
     *
     * @param hql          hql语句
     * @param conditionMap 参数值map
     * @param values       参数值数组
     * @return 实体集合
     */
    private List<?> findAllResultObjects(final String hql, final Map<String, Object> conditionMap, final Object[] values) {
        if (hql != null) {
            Query query = em.createQuery(hql);
            setParameters(query, conditionMap, values);
            return query.getResultList();
        } else {
            return null;
        }
    }

    /**
     * HQL查询语句返回分页的Entity对象列表， 使用位置参数（positional parameter）
     *
     * @param hql         hql语句
     * @param values      参数数组
     * @param pageRequest 分页参数
     * @return 实体分页结果
     */
    protected Page<E> findEntityObjects(final String hql, final Object[] values, final Pageable pageRequest) {
        return findEntityObjects(hql, null, values, pageRequest);
    }

    /**
     * HQL查询语句返回分页的Entity对象列表， 使用命名参数（named parameter）
     *
     * @param hql          hql语句
     * @param conditionMap 参数值map
     * @param pageRequest  分页参数
     * @return 实体分页结果
     */
    protected Page<E> findEntityObjects(final String hql, final Map<String, Object> conditionMap, final Pageable pageRequest) {
        return findEntityObjects(hql, conditionMap, null, pageRequest);
    }

    /**
     * HQL查询语句返回分页的Entity对象列表
     *
     * @param hql          hql语句
     * @param conditionMap 参数值map
     * @param values       参数数组
     * @param pageRequest  分页参数
     * @return 分页结果
     */
    private Page<E> findEntityObjects(final String hql, final Map<String, Object> conditionMap, final Object[] values, final Pageable pageRequest) {
        if (hql != null && pageRequest.getOffset() >= 0 && pageRequest.getPageSize() > 0) {
            Query query = em.createQuery(hql, entityClass);
            setParameters(query, conditionMap, values);
            query.setFirstResult(pageRequest.getOffset());
            query.setMaxResults(pageRequest.getPageSize());
            @SuppressWarnings("unchecked")
            List<E> content = query.getResultList();
            long total;
            //如果每页数量是最大值，那么就不要统计数量了，默认是查询全部，返回的列表数量就是total
            if (pageRequest.getPageSize() != Integer.MAX_VALUE) {
                total = findCount(hql, conditionMap, values);
            } else {
                total = content.size();
            }
            return new PageImpl<>(content, pageRequest, total);
        } else {
            return null;
        }
    }

    /**
     * 获取前N个对象
     *
     * @param hql          hql语句
     * @param conditionMap 参数值map
     * @param size         数量
     * @return 实体集合
     */
    public List<E> findTopNEntityObjects(final String hql, final Map<String, Object> conditionMap, int size) {
        if (hql != null && size > 0) {
            Query query = em.createQuery(hql, entityClass);
            setParameters(query, conditionMap, null);
            query.setMaxResults(size);
            @SuppressWarnings("unchecked")
            List<E> list = (List<E>) query.getResultList();
            return list;
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * HQL查询语句返回分页的查询结果对象列表，使用位置参数（positional parameter）
     *
     * @param hql         hql语句
     * @param values      参数值数组
     * @param pageRequest 分页参数
     * @return 分页结果
     */
    protected Page<Object[]> findResultObjects(final String hql, final Object[] values, final Pageable pageRequest) {
        return findResultObjects(hql, null, values, pageRequest);
    }

    /**
     * HQL查询语句返回分页的查询结果对象列表，使用命名参数（named parameter）
     *
     * @param hql          查询语句
     * @param conditionMap 查询条件键值对
     * @param pageRequest  分页请求参数对象
     * @return 分页结果
     */
    public Page<Object[]> findResultObjects(final String hql, final Map<String, Object> conditionMap, final Pageable pageRequest) {
        return findResultObjects(hql, conditionMap, null, pageRequest);
    }

    /**
     * HQL查询语句返回分页的查询结果对象列表
     *
     * @param hql          查询语句
     * @param conditionMap 查询条件键值对
     * @param values       分页参数
     * @param pageRequest  分页请求参数对象
     * @return 分页结果
     */
    private Page<Object[]> findResultObjects(final String hql, final Map<String, Object> conditionMap, final Object[] values, final Pageable pageRequest) {
        if (hql != null && pageRequest.getOffset() >= 0 && pageRequest.getPageSize() > 0) {
            Query query = em.createQuery(hql);
            setParameters(query, conditionMap, values);
            query.setFirstResult(pageRequest.getOffset());
            query.setMaxResults(pageRequest.getPageSize());
            @SuppressWarnings("unchecked")
            List<Object[]> content = query.getResultList();
            long total;
            //如果每页数量是最大值，那么就不要统计数量了，默认是查询全部，返回的列表数量就是total
            if (pageRequest.getPageSize() != Integer.MAX_VALUE) {
                total = findCount(hql, conditionMap, values);
            } else {
                total = content.size();
            }
            return new PageImpl<>(content, pageRequest, total);
        } else {
            return null;
        }
    }

    /**
     * 分页获取键值对集合
     *
     * @param sql          查询语句
     * @param conditionMap 条件键值对
     * @param pageRequest  分页请求对象
     * @return 分页结果对象
     */
    public Page<Map> findResultMapsSql(final String sql, final Map<String, Object> conditionMap, final Pageable pageRequest) {
        return findResultMapsSql(sql, conditionMap, null, pageRequest);
    }

    /**
     * 分页获取键值对集合
     *
     * @param sql         查询语句
     * @param values      条件数组
     * @param pageRequest 分页请求参数对象
     * @return 分页结果对象
     */
    public Page<Map> findResultMapsSql(final String sql, final Object[] values, final Pageable pageRequest) {
        return findResultMapsSql(sql, null, values, pageRequest);
    }

    /**
     * 分页获取键值对结果
     *
     * @param sql          sql语句
     * @param conditionMap 条件键值对（和values二选一）
     * @param values       条件值数组（和conditionMap二选一）
     * @param pageRequest  分页参数
     * @return 分页结果
     */
    private Page<Map> findResultMapsSql(final String sql, final Map<String, Object> conditionMap, final Object[] values, final Pageable pageRequest) {
        if (sql != null && pageRequest.getOffset() >= 0 && pageRequest.getPageSize() > 0) {
            Query query = em.createNativeQuery(sql);
            setParameters(query, conditionMap, values);
            query.setFirstResult(pageRequest.getOffset());
            query.setMaxResults(pageRequest.getPageSize());
            query.unwrap(SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
            @SuppressWarnings("unchecked")
            List<Map> content = query.getResultList();
            long total;
            //如果每页数量是最大值，那么就不要统计数量了，默认是查询全部，返回的列表数量就是total
            if (pageRequest.getPageSize() != Integer.MAX_VALUE) {
                total = findCountBySql(sql, conditionMap, values);
            } else {
                total = content.size();
            }
            return new PageImpl<>(content, pageRequest, total);
        } else {
            return null;
        }
    }


    /**
     * 分页获取Bean集合
     *
     * @param sql          查询语句
     * @param conditionMap 条件键值对
     * @param pageRequest  分页请求对象
     * @return 分页结果对象
     */
    public <B> Page<B> findResultBeansSql(final String sql, Class<B> beanClass, final Map<String, Object> conditionMap, final Pageable pageRequest) {
        return findResultBeansSql(sql, beanClass, conditionMap, null, pageRequest);
    }

    /**
     * 分页获取Bean集合
     *
     * @param sql         查询语句
     * @param values      条件数组
     * @param pageRequest 分页请求参数对象
     * @return 分页结果对象
     */
    public <B> Page<B> findResultBeansSql(final String sql, Class<B> beanClass, final Object[] values, final Pageable pageRequest) {
        return findResultBeansSql(sql, beanClass, null, values, pageRequest);
    }

    /**
     * 分页获取Bean集合
     *
     * @param sql          sql语句
     * @param conditionMap 条件键值对（和 values 二选一）
     * @param values       条件值数组（和 conditionMap 二选一）
     * @param pageRequest  分页参数
     * @return 分页结果
     */
    private <B> Page<B> findResultBeansSql(final String sql, Class<B> beanClass, final Map<String, Object> conditionMap, final Object[] values, final Pageable pageRequest) {
        if (sql != null && pageRequest.getOffset() >= 0 && pageRequest.getPageSize() > 0) {
            Query query = em.createNativeQuery(sql);
            setParameters(query, conditionMap, values);
            query.setFirstResult(pageRequest.getOffset());
            query.setMaxResults(pageRequest.getPageSize());

            query.unwrap(SQLQuery.class).setResultTransformer(new BeanTransformerAdapter<>(beanClass));
            @SuppressWarnings("unchecked")
            List<B> content = query.getResultList();
            long total;
            //如果每页数量是最大值，那么就不要统计数量了，默认是查询全部，返回的列表数量就是total
            if (pageRequest.getPageSize() != Integer.MAX_VALUE) {
                total = findCountBySql(sql, conditionMap, values);
            } else {
                total = content.size();
            }
            return new PageImpl<>(content, pageRequest, total);
        } else {
            return null;
        }
    }

    /**
     * 查询符合条件的所有数据
     *
     * @param sql          查询语句
     * @param conditionMap 条件键值对
     * @return 结果集合
     */
    public List<Map> findAllResutlMapsBySql(final String sql, final Map<String, Object> conditionMap) {
        if (sql != null) {
            Query query = em.createNativeQuery(sql);
            setParameters(query, conditionMap, null);
            query.unwrap(SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
            @SuppressWarnings("unchecked")
            List<Map> list = (List<Map>) query.getResultList();
            return list;
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * 查询符合条件的所有bean集合
     *
     * @param sql          sql语句
     * @param conditionMap 参数值map
     * @param beanClass    结果数据类型
     * @param <B>          结果数据类型
     * @return 结果集合
     */
    public <B> List<B> findAllResutlBeansBySql(final String sql, final Map<String, Object> conditionMap, Class<B> beanClass) {
        if (sql != null) {
            Query query = em.createNativeQuery(sql);
            setParameters(query, conditionMap, null);
            query.unwrap(SQLQuery.class).setResultTransformer(new BeanTransformerAdapter<>(beanClass));
            @SuppressWarnings("unchecked")
            List<B> list = query.getResultList();
            return list;
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * 通用的hql查询总行数的方法
     *
     * @param hql          hql语句
     * @param conditionMap 参数值map
     * @param values       参数值数组
     * @return 结果总行数
     */
    private long findCount(final String hql, Map<String, Object> conditionMap, final Object[] values) {
        String sql = hqlToSql(hql);
        //hqlToSql(String)方法在转换SQL时，会把HQL中的命名参数转成位置参数，所以该地方调用时传入的是位置参数
        if (conditionMap != null && !conditionMap.isEmpty()) {
            Object[] paras = mapToArray(hql, conditionMap);
            sql = improveSql(sql);
            return findCountBySql(sql, null, paras);
        } else {
            return findCountBySql(sql, null, values);
        }
    }

    /**
     * 通用的sql查询语句返回单个Entity对象，使用位置参数（positional parameter）
     *
     * @param sql    sql语句
     * @param values 参数数组
     * @return 实体
     */
    protected E findOneEntityObjectBySql(final String sql, final Object[] values) {
        return findOneEntityObjectBySql(sql, null, values);
    }

    /**
     * 通用的sql查询语句返回单个Entity对象，使用命名参数（named parameter）
     *
     * @param sql          sql语句
     * @param conditionMap 参数值map
     * @return 实体
     */
    protected E findOneEntityObjectBySql(final String sql, final Map<String, Object> conditionMap) {
        return findOneEntityObjectBySql(sql, conditionMap, null);
    }

    /**
     * 根据原生sql查询单个实体
     *
     * @param sql          sql语句
     * @param conditionMap 参数值map
     * @param values       参数值数组
     * @return 实体
     */
    @SuppressWarnings("unchecked")
    private E findOneEntityObjectBySql(final String sql, final Map<String, Object> conditionMap, final Object[] values) {
        if (sql != null) {
            Query query = em.createNativeQuery(sql, entityClass);
            setParameters(query, conditionMap, values);
            return (E) query.getSingleResult();
        } else {
            return null;
        }
    }

    /**
     * 通用的原生sql查询语句返回Entity对象列表，使用位置参数（positional parameter）
     *
     * @param sql    sql语句
     * @param values 参数值数组
     * @return 实体集合
     */
    protected List<E> findEntityObjectsBySql(final String sql, final Object[] values) {
        return findAllEntityObjectsBySql(sql, null, values);
    }

    /**
     * 通用的原生sql查询语句返回Entity对象列表，使用命名参数（named parameter）
     *
     * @param sql          sql语句
     * @param conditionMap 参数值map
     * @return 实体集合
     */
    protected List<E> findEntityObjectsBySql(final String sql, final Map<String, Object> conditionMap) {
        return findAllEntityObjectsBySql(sql, conditionMap, null);
    }

    /**
     * 根据原生sql查询所有符合条件的实体
     *
     * @param sql          sql语句
     * @param conditionMap 参数值map
     * @param values       参数值数组
     * @return 实体集合
     */
    @SuppressWarnings("unchecked")
    private List<E> findAllEntityObjectsBySql(final String sql, final Map<String, Object> conditionMap, final Object[] values) {
        if (sql != null) {
            Query query = em.createNativeQuery(sql, entityClass);
            setParameters(query, conditionMap, values);
            return query.getResultList();
        } else {
            return null;
        }
    }

    /**
     * 通用的sql查询语句返回单个查询结果，使用位置参数（positional parameter）
     *
     * @param sql    sql语句
     * @param values 参数值数组
     * @return 对象
     */
    protected Object findOneResutlObjectBySql(final String sql, final Object[] values) {
        return findOneResutlObjectBySql(sql, null, values);
    }

    /**
     * 通用的sql查询语句返回单个查询结果，使用命名参数（named parameter）
     *
     * @param sql          sql语句
     * @param conditionMap 参数值map
     * @return 对象
     */
    protected Object findOneResutlObjectBySql(final String sql, final Map<String, Object> conditionMap) {
        return findOneResutlObjectBySql(sql, conditionMap, null);
    }

    /**
     * 通用的sql查询语句返回单个查询结果，使用命名参数（named parameter）
     *
     * @param sql          sql语句
     * @param conditionMap 参数值map
     * @param values       参数值数组
     * @return 对象
     */
    private Object findOneResutlObjectBySql(final String sql, final Map<String, Object> conditionMap, final Object[] values) {
        if (sql != null) {
            Query query = em.createNativeQuery(sql);
            setParameters(query, conditionMap, values);
            return query.getSingleResult();
        } else {
            return null;
        }
    }

    /**
     * 通用的sql查询语句返回查询结果对象列表，使用位置参数（positional parameter）
     *
     * @param sql    sql语句
     * @param values 参数值数组
     * @return 结果集合
     */
    protected List<?> findResutlObjectsBySql(final String sql, final Object[] values) {
        return findAllResutlObjectsBySql(sql, null, values);
    }

    /**
     * 通用的sql查询语句返回查询结果对象列表，使用命名参数（named parameter）
     *
     * @param sql          sql语句
     * @param conditionMap 参数值map
     * @return 结果集合
     */
    protected List<?> findResutlObjectsBySql(final String sql, final Map<String, Object> conditionMap) {
        return findAllResutlObjectsBySql(sql, conditionMap, null);
    }

    /**
     * 查询符合条件的所有数据
     *
     * @param sql          sql语句
     * @param conditionMap 参数值map
     * @param values       参数值数组
     * @return 数据集合
     */
    private List<?> findAllResutlObjectsBySql(final String sql, final Map<String, Object> conditionMap, final Object[] values) {
        if (sql != null) {
            Query query = em.createNativeQuery(sql);
            setParameters(query, conditionMap, values);
            return query.getResultList();
        } else {
            return null;
        }
    }

    /**
     * 通用的sql查询语句返回分页Entity对象列表，使用位置参数（positional parameter）
     *
     * @param sql         sql语句
     * @param values      参数值数组
     * @param pageRequest 分页参数
     * @return 分页结果
     */
    protected Page<E> findEntityObjectsBySql(final String sql, final Object[] values, final Pageable pageRequest) {
        return findEntityObjectsBySql(sql, null, values, pageRequest);
    }

    /**
     * 通用的sql查询语句返回分页Entity对象列表，使用命名参数（named parameter）
     *
     * @param sql          sql语句
     * @param conditionMap 参数值map
     * @param pageRequest  分页参数
     * @return 分页结果
     */
    protected Page<E> findEntityObjectsBySql(final String sql, final Map<String, Object> conditionMap, final Pageable pageRequest) {
        return findEntityObjectsBySql(sql, conditionMap, null, pageRequest);
    }

    /**
     * 通用的sql查询语句返回分页Entity对象列表
     *
     * @param sql          sql语句
     * @param conditionMap 参数值 map
     * @param values       参数值数组
     * @param pageRequest  分页参数
     * @return 分页结果
     */
    private Page<E> findEntityObjectsBySql(final String sql, final Map<String, Object> conditionMap, final Object[] values, final Pageable pageRequest) {
        if (pageRequest.getOffset() >= 0 && pageRequest.getPageSize() > 0 && sql != null) {
            Query query = em.createNativeQuery(sql, entityClass);
            setParameters(query, conditionMap, values);
            query.setFirstResult(pageRequest.getOffset());
            query.setMaxResults(pageRequest.getPageSize());
            @SuppressWarnings("unchecked")
            List<E> content = query.getResultList();
            //如果每页数量是最大值，那么就不要统计数量了，默认是查询全部，返回的列表数量就是total
            long total;
            if (pageRequest.getPageSize() != Integer.MAX_VALUE) {
                total = findCountBySql(sql, conditionMap, values);
            } else {
                total = content.size();
            }
            return new PageImpl<>(content, pageRequest, total);

        } else {
            return null;
        }
    }

    /**
     * 通用的sql查询语句返回分页查询结果列表，使用位置参数（positional parameter）
     *
     * @param sql         sql语句
     * @param values      参数值数组
     * @param pageRequest 分页参数
     * @return 分页结果
     */
    protected Page<Object[]> findResutlObjectsBySql(final String sql, final Object[] values, final Pageable pageRequest) {
        return findResutlObjectsBySql(sql, null, values, pageRequest);
    }

    /**
     * 通用的sql查询语句返回分页查询结果列表，使用命名参数（named parameter）
     *
     * @param sql          sql语句
     * @param conditionMap 参数值map
     * @param pageRequest  分页参数
     * @return 分页结果
     */
    protected Page<Object[]> findResutlObjectsBySql(final String sql, final Map<String, Object> conditionMap, final Pageable pageRequest) {
        return findResutlObjectsBySql(sql, conditionMap, null, pageRequest);
    }

    /**
     * 通用的sql查询语句返回分页查询结果列表
     *
     * @param sql          sql语句
     * @param conditionMap 参数值map
     * @param values       参数值数组
     * @param pageRequest  分页参数
     * @return 分页结果
     */
    private Page<Object[]> findResutlObjectsBySql(final String sql, final Map<String, Object> conditionMap, final Object[] values, final Pageable pageRequest) {
        if (pageRequest.getOffset() >= 0 && pageRequest.getPageSize() > 0 && sql != null) {
            long total;

            Query query = em.createNativeQuery(sql);
            setParameters(query, conditionMap, values);
            query.setFirstResult(pageRequest.getOffset());
            query.setMaxResults(pageRequest.getPageSize());
            @SuppressWarnings("unchecked")
            List<Object[]> content = query.getResultList();
            //如果每页数量是最大值，那么就不要统计数量了，默认是查询全部，返回的列表数量就是total
            if (pageRequest.getPageSize() != Integer.MAX_VALUE) {
                total = findCountBySql(sql, conditionMap, values);
            } else {
                total = content.size();
            }
            return new PageImpl<>(content, pageRequest, total);
        } else {
            return null;
        }
    }

    /**
     * 将count语句中的order by子句删除
     *
     * @param sql 带count的sql语句
     * @return 处理后的sql
     */
    private String prepareCountSql(String sql) {
        Pattern pattern = Pattern.compile("\\s*order\\s+by\\s*", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(sql);
        if (matcher.find()) {
            int orderByPos = matcher.start();
            sql = StringUtils.substring(sql, 0, orderByPos);
        }
        return "select count(*) from (" + sql + ")result_0_1";
    }

    /**
     * 查询数量
     *
     * @param sql          sql
     * @param conditionMap 参数值map
     * @param values       参数值数组
     * @return 数量
     */
    private long findCountBySql(final String sql, final Map<String, Object> conditionMap, final Object[] values) {
        if (sql != null) {
            String countSql = prepareCountSql(sql);
            Object o = findOneResutlObjectBySql(countSql, conditionMap, values);
            return ((Number) o).longValue();// ((BigDecimal)o).longValue();
        } else {
            return 0;
        }
    }

    /**
     * 更新或者删除语句hql 使用位置参数（positional parameter）
     *
     * @param hql    hql
     * @param values 参数值数组
     * @return 影响的行数
     */
    protected int bulkUpdate(final String hql, final Object[] values) {
        return bulkUpdate(hql, null, values);
    }

    /**
     * 更新或者删除语句hql 使用命名参数（named parameter）
     *
     * @param hql          hql
     * @param conditionMap 参数值map
     * @return 影响的行数
     */
    protected int bulkUpdate(final String hql, final Map<String, Object> conditionMap) {
        return bulkUpdate(hql, conditionMap, null);
    }

    /**
     * 更新或者删除语句hql
     *
     * @param hql          hql
     * @param conditionMap 参数值map
     * @param values       参数值数组
     * @return 影响的行数
     */
    private int bulkUpdate(final String hql, final Map<String, Object> conditionMap, final Object[] values) {
        if (hql != null) {
            Query query = em.createQuery(hql);
            setParameters(query, conditionMap, values);
            return query.executeUpdate();
        } else {
            return 0;
        }
    }

    /**
     * 更新或者删除语句sql，使用命名参数（named parameter）使用位置参数（positional parameter）
     *
     * @param sql    sql
     * @param values 参数值数组
     * @return 影响的行数
     */
    protected int bulkUpdateSql(final String sql, final Object[] values) {
        return bulkUpdateSql(sql, null, values);
    }

    /**
     * 更新或者删除语句sql，使用命名参数（named parameter）使用位置参数（positional parameter）
     *
     * @param sql          sql
     * @param conditionMap 参数值map
     * @return 影响的行数
     */
    protected int bulkUpdateSql(final String sql, final Map<String, Object> conditionMap) {
        return bulkUpdateSql(sql, conditionMap, null);
    }

    /**
     * 更新或者删除语句sql
     *
     * @param sql          sql
     * @param conditionMap 参数值map
     * @param values       参数值数组
     * @return 影响的行数
     */
    private int bulkUpdateSql(final String sql, final Map<String, Object> conditionMap, final Object[] values) {
        if (sql != null) {
            Query query = em.createNativeQuery(sql);
            setParameters(query, conditionMap, values);
            return query.executeUpdate();
        } else {
            return 0;
        }
    }

    /**
     * 设置参数
     *
     * @param query  查询对象
     * @param map    参数map
     * @param values 参数数组
     */
    private void setParameters(Query query, Map<String, Object> map,
                               Object[] values) {
        if (map != null && map.size() > 0) {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                query.setParameter(entry.getKey(), entry.getValue());
            }
        } else if (null != values && values.length > 0) {
            for (int i = 0; i < values.length; ++i) {
                query.setParameter(i + 1, values[i]);
            }
        }
    }

    /**
     * hql转为sql 该方法不支持JPA2.0规范，依赖于Hibernate实现， 并且使用了Hibernate内部API，Hibernate后继版本不保证兼容
     *
     * @param hql hql
     * @return sql
     */
    private String hqlToSql(final String hql) {
        if (StringUtils.isEmpty(hql)) {
            return hql;
        }
        SessionFactory sf = getSessionFactory();
        QueryTranslatorImpl queryTranslator = new QueryTranslatorImpl(hql, hql, Collections.EMPTY_MAP, (SessionFactoryImplementor) sf);
        queryTranslator.compile(Collections.EMPTY_MAP, true);
        return queryTranslator.getSQLString();
    }

    private SessionFactory getSessionFactory() {
        Session session = em.unwrap(Session.class);
        if (sf == null)
            sf = session.getSessionFactory();
        return sf;
    }

    /**
     * 截取分析HQL，把conditionMap的参数值，按照命名参数出现的顺序转换成Object[]
     *
     * @param hql          hql
     * @param conditionMap 参数值map
     * @return 参数数组
     */
    private Object[] mapToArray(final String hql, final Map<String, Object> conditionMap) {
        String tmp = " " + hql + " ";
        Object[] param = new Object[conditionMap.size()];
        int i = 0;
        while (true) {
            int start = tmp.indexOf(':');
            if (start < 0)
                break;
            tmp = tmp.substring(start + 1);
            int end = tmp.indexOf(' ');
            String key;
            if (end < 0) {
                key = tmp.substring(0, tmp.length());
            } else {
                key = tmp.substring(0, end);
            }
            int m = key.indexOf(')');
            if (m > 0) {
                key = key.substring(0, m);
            }
            param[i++] = conditionMap.get(key);
        }
        return param;
    }

    /**
     * 对SQL中的"?"的后面加上序号,比如?1,?2,?3
     *
     * @param sql sql
     * @return 处理后的sql
     */
    private String improveSql(String sql) {
        sql = " " + sql + " ";
        String[] split = sql.split("[?]");
        for (int i = 0; i < split.length; i++) {
            if (i != split.length - 1)
                split[i] += " ?" + (i + 1) + " ";
        }
        return toStr(split);
    }

    /**
     * 数组转成String，数组中的元素仅仅进行平铺，不进行任何分割
     *
     * @param param 参数
     * @return 处理后参数字符串
     */
    private String toStr(Object[] param) {
        StringBuilder sb = new StringBuilder();
        for (Object aParam : param) {
            sb.append(aParam.toString());
        }
        return sb.toString();
    }

    /**
     * 构造sql
     *
     * @param kvList         参数值键值对
     * @param conditionItems 查询条件集合
     * @param sql            sql语句
     * @return sql
     */
    protected String buildSql(Map<String, Object> kvList, List<ConditionItem> conditionItems, String sql) {
        if (conditionItems == null)
            return sql;
        PageConditionSqlBuilder sqlBuilder = new PageConditionSqlBuilder(conditionItems, sql, kvList);
        sqlBuilder.build();
        sql = sqlBuilder.getSql();
        return sql;
    }

    /**
     * 通用分页获取数据集合，元素为map
     *
     * @param pageReq 分页参数
     * @param sql     查询语句
     * @return 分页结果
     */
    public PageResult<Map> getSimplePageList(PageReq pageReq, String sql) {
        Pageable pageable = new PageRequest(pageReq.getPage() - 1, pageReq.getRows());
        HashMap<String, Object> kvList = new HashMap<>();
        sql = buildSql(kvList, pageReq.getConditionItems(), sql);
        Page<Map> page = this.findResultMapsSql(sql, kvList, pageable);
        PageResult<Map> pageResult = new PageResult<>();
        pageResult.setTotal(page.getTotalElements());
        pageResult.setRows(page.getContent());
        return pageResult;
    }

    /**
     * 通用分页获取数据集合，元素为map
     *
     * @param pageReq 分页参数
     * @return 分页结果
     */
    public PageResult<Map> getSimplePageList(PageReq pageReq) {
        //子类根据需要实现该方法
//        String sql="";
//        return getSimplePageList(pageReq,sql);
        return null;
    }

    /**
     * 原生sql批量插入
     *
     * @param list      数据集合
     * @param beanClass 数据元素类型
     * @param tableName 表名
     * @param <T>       数据元素类型
     */
    protected <T> void bulkInsert(List<T> list, Class<T> beanClass, String tableName) {
        Session session = em.unwrap(Session.class);
        session.doWork(connection -> {
            try {
                bulkInsert(list, connection, beanClass, tableName, new ArrayList<>());
            } catch (InvocationTargetException | ParseException | IllegalAccessException e) {
                e.printStackTrace();
                logger.error(e.getMessage(), e);
                throw new RuntimeException(e.getMessage());
            }
        });
    }

    /**
     * 批量插入
     *
     * @param list         数据集合
     * @param beanClass    数据元素类型
     * @param tableName    表名
     * @param ignoreFields 忽略的字段集合
     * @param <T>          数据元素类型
     */
    protected <T> void bulkInsert(List<T> list, Class<T> beanClass, String tableName, List<String> ignoreFields) {
        Session session = em.unwrap(Session.class);
        session.doWork(connection -> {
            try {
                bulkInsert(list, connection, beanClass, tableName, ignoreFields);
            } catch (InvocationTargetException | IllegalAccessException | ParseException e) {
                e.printStackTrace();
                logger.error(e.getMessage(), e);
                throw new RuntimeException(e.getMessage());
            }
        });
    }

    /**
     * 字段是否需要忽略
     *
     * @param field        字段名称
     * @param ignoreFields 字段忽略集合
     * @return 是否需要忽略
     */
    private boolean inIgnoreField(String field, List<String> ignoreFields) {
        return ignoreFields != null && ignoreFields.size() > 0 && ignoreFields.contains(field);
    }

    /**
     * 批量插入
     *
     * @param list         数据集合
     * @param con          数据库链接对象
     * @param beanClass    数据元素类型
     * @param tableName    表名
     * @param ignoreFields 要忽略的字段集合
     * @param <T>          数据元素类型
     * @throws SQLException              sql异常
     * @throws InvocationTargetException InvocationTargetException
     * @throws IllegalAccessException    IllegalAccessException
     * @throws ParseException            ParseException
     */
    private <T> void bulkInsert(List<T> list, Connection con, Class<T> beanClass, String tableName, List<String> ignoreFields) throws SQLException, InvocationTargetException, IllegalAccessException, ParseException {
        Class clazz = beanClass;
        List<String> fieldList = new ArrayList<>();

        for (Field field : clazz.getDeclaredFields()) {
            if (field.getType().isArray() || inIgnoreField(field.getName(), ignoreFields)) {
                continue;
            }
            fieldList.add(field.getName());
        }

        while (clazz.getSuperclass() != null && !clazz.getSuperclass().getName().equals(AbstractEntity.class.getName())) {
            for (Field field : clazz.getSuperclass().getDeclaredFields()) {
                if (field.getType().isArray() || inIgnoreField(field.getName(), ignoreFields)) {
                    continue;
                }
                fieldList.add(field.getName());
            }
            clazz = clazz.getSuperclass();
        }

        StringBuilder sql = new StringBuilder();
        sql.append("insert into ").append(tableName).append(" (");
        for (String idx : fieldList) {
            if (fieldList.indexOf(idx) > 0) {
                sql.append(",");
            }
            sql.append(idx);
        }
        sql.append(") values(");
        for (int i = 0; i < fieldList.size(); i++) {
            if (i > 0) {
                sql.append(",");
            }
            sql.append("?");
        }
        sql.append(")");

        if (logger.isDebugEnabled()) {
            logger.debug(sql.toString());
        }
        // 关闭事务自动提交
        //con.setAutoCommit(false);

        PreparedStatement pst = con.prepareStatement(sql.toString());
        StopWatch stopWatch2 = new StopWatch();
        stopWatch2.start();
        for (int i = 0; i < list.size(); i++) {
            String filedName;
            for (Method method : list.get(i).getClass().getMethods()) {
                if (method.getName().startsWith("get") || method.getName().startsWith("is")) {
                    if (method.getName().startsWith("get")) {
                        filedName = method.getName().substring(3);
                    } else {
                        filedName = method.getName().substring(2);
                    }

                    filedName = filedName.substring(0, 1).toLowerCase() + filedName.substring(1);
                    if (fieldList.indexOf(filedName) != -1) {
                        if (method.getReturnType() == Date.class) {
                            Date t = (Date) method.invoke(list.get(i));
                            if (t != null) {
                                pst.setObject(fieldList.indexOf(filedName) + 1, new java.sql.Timestamp(t.getTime()));
                            } else {
                                pst.setObject(fieldList.indexOf(filedName) + 1, null);
                            }

                        } else {
                            pst.setObject(fieldList.indexOf(filedName) + 1, method.invoke(list.get(i)));
                        }
                    }
                }
            }
            // 把一个SQL命令加入命令列表
            pst.addBatch();
            if (i + 1 == list.size() || (i + 1) % 5000 == 0) {
                StopWatch stopWatch = new StopWatch();
                stopWatch.start();
                // 执行批量更新
                pst.executeBatch();
                logger.info(tableName + " batch insert used:" + stopWatch.getTime() * 1.0 / 1000);
                stopWatch.stop();
            }
        }
        logger.info(tableName + " batch insert total used:" + stopWatch2.getTime() * 1.0 / 1000);
        stopWatch2.stop();

    }

    /**
     * 批量根据主键删除
     *
     * @param list        主键值集合
     * @param idFieldName 主键字段名
     * @param tableName   表名
     */
    protected void bulkDelete(List list, String idFieldName, String tableName) {
        Session session = em.unwrap(Session.class);
        session.doWork(connection -> {
            try {
                bulkDeleteSql(list, connection, idFieldName, tableName);
            } catch (InvocationTargetException | ParseException | IllegalAccessException e) {
                e.printStackTrace();
                logger.error(e.getMessage(), e);
                throw new RuntimeException(e.getMessage());
            }
        });
    }

    /**
     * 批量根据主键删除
     *
     * @param list        主键值集合
     * @param con         数据库链接对象
     * @param idFieldName 主键字段名称
     * @param tableName   表名
     * @throws SQLException              SQLException
     * @throws InvocationTargetException InvocationTargetException
     * @throws IllegalAccessException    IllegalAccessException
     * @throws ParseException            ParseException
     */
    private void bulkDeleteSql(List list, Connection con, String idFieldName, String tableName) throws SQLException, InvocationTargetException, IllegalAccessException, ParseException {
        StringBuilder sql = new StringBuilder();
        sql.append("delete from ").append(tableName).append(" where ").append(idFieldName).append("=?");
        if (logger.isDebugEnabled()) {
            logger.debug(sql.toString());
        }
        // 关闭事务自动提交
        //con.setAutoCommit(false);

        PreparedStatement pst = con.prepareStatement(sql.toString());
        StopWatch stopWatch2 = new StopWatch();
        stopWatch2.start();
        for (int i = 0; i < list.size(); i++) {
            pst.setObject(1, list.get(i));
            // 把一个SQL命令加入命令列表
            pst.addBatch();
            if (i == list.size() - 1 || (i + 1) % 5000 == 0) {
                StopWatch stopWatch = new StopWatch();
                stopWatch.start();
                // 执行批量操作
                pst.executeBatch();
                logger.info("batch delete used:" + stopWatch.getTime() * 1.0 / 1000);
                stopWatch.stop();
            }
        }
        logger.info("batch delete total used:" + stopWatch2.getTime() * 1.0 / 1000);
        stopWatch2.stop();
    }
}

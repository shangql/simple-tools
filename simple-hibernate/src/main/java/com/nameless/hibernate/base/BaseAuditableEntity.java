package com.nameless.hibernate.base;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;

/**
 * 通用可审核实体父类
 */
@MappedSuperclass
public abstract class BaseAuditableEntity<TId extends Serializable> extends AbstractAuditableEntity<TId> {
    @Id
    private TId id;

    @Override
    public TId getId() {
        return id;
    }

    @Override
    public void setId(TId id) {
        this.id = id;
    }
}

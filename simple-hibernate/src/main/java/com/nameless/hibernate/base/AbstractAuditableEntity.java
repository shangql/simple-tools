/**
 * Copyright (c) 2015-2018 ehsure
 */
package com.nameless.hibernate.base;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by jason on 2015/8/19.
 * 抽象可审核实体父类
 */
@MappedSuperclass
public abstract class AbstractAuditableEntity<TId extends Serializable> extends AbstractEntity<TId> {

    public AbstractAuditableEntity() {
        this.addTime = new Date();
        this.editTime = new Date();
    }

    private Date addTime;
    @Column(length = 50)
    private String addBy;
    private Date editTime;
    @Column(length = 50)
    private String editBy;
    @Column(length = 2000)
    private String remark;

    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }

    public String getAddBy() {
        return addBy;
    }

    public void setAddBy(String addBy) {
        this.addBy = addBy;
    }

    public Date getEditTime() {
        return editTime;
    }

    public void setEditTime(Date editTime) {
        this.editTime = editTime;
    }

    public String getEditBy() {
        return editBy;
    }

    public void setEditBy(String editBy) {
        this.editBy = editBy;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}

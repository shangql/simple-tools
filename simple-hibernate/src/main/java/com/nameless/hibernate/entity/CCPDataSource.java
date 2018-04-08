package com.nameless.hibernate.entity;

import com.nameless.hibernate.base.BaseAuditableEntity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bill on 2017/5/19.
 */
@Entity
@Table(name = "fmsCCPDataSource")
public class CCPDataSource extends BaseAuditableEntity<String> {

    @Column(nullable = false, length = 50)
    private String code;
    @Column(nullable = false, length = 200)
    private String name;
    @Column(nullable = false)
    private boolean inUse;
    @ManyToOne(
            cascade = {CascadeType.PERSIST}, fetch = FetchType.LAZY
    )
    @JoinColumn(
            name = "categoryId"
    )
    private CCPDataSource parentCCPDataSource;
    @OneToMany(
            cascade = {CascadeType.PERSIST},
            mappedBy = "parentCCPDataSource", fetch = FetchType.LAZY
    )
    private List<CCPDataSource> childList = new ArrayList<>();


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isInUse() {
        return inUse;
    }

    public void setInUse(boolean inUse) {
        this.inUse = inUse;
    }

    public CCPDataSource getParentCCPDataSource() {
        return parentCCPDataSource;
    }

    public void setParentCCPDataSource(CCPDataSource parentCCPDataSource) {
        this.parentCCPDataSource = parentCCPDataSource;
    }

    public List<CCPDataSource> getChildList() {
        return childList;
    }

    public void setChildList(List<CCPDataSource> childList) {
        this.childList = childList;
    }
}

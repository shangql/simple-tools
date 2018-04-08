package com.nameless.hibernate.base;

import javax.persistence.MappedSuperclass;
import java.io.Serializable;

/**
 * Created by jason on 2015/8/10.
 * 抽象实体父类
 */
@MappedSuperclass
public abstract class AbstractEntity<TId extends Serializable> implements Serializable {

    public abstract TId getId();

    public abstract void setId(final TId id);

    @Override
    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }

        if (this == obj) {
            return true;
        }

        if (!getClass().equals(obj.getClass())) {
            return false;
        }

        AbstractEntity<?> that = (AbstractEntity<?>) obj;

        return null != this.getId() && this.getId().equals(that.getId());
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {

        int hashCode = 17;

        hashCode += null == getId() ? 0 : getId().hashCode() * 31;

        return hashCode;
    }
}

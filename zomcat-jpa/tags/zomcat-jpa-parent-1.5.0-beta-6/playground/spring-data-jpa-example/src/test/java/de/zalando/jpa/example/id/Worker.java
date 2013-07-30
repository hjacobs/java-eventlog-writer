package de.zalando.jpa.example.id;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.springframework.data.jpa.domain.AbstractPersistable;

import com.google.common.base.Objects;

/**
 * We only extend from {@link AbstractPersistable} to not implement 'equals' and 'hashcode'.
 *
 * @author  jbellmann
 */
@Entity
public class Worker extends AbstractPersistable<Long> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    // not needed for this example
    long x;
    long y;
    long z;

    public Long getId() {
        return id;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("id", getId()).toString();
    }

}

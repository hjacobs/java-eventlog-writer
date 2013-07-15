package de.zalando.jpa.example.article;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Partitioned;

import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "attribute", schema = "zzj_data")
@Partitioned(value = "PartitionByShardKey") // lost... keine shard-key reference möglich
public class Attribute extends AbstractPersistable<Long> {

    private static final long serialVersionUID = 1L;

    private String name;

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }
}

package de.zalando.jpa.example.article;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Partitioned;

@Entity
@Table(name = "attribute", schema = "zzj_data")
@Partitioned(value = "PartitionByShardKey") // lost... keine shard-key reference möglich
public class Attribute {
    @Column
    private String name;

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }
}

package de.zalando.jpa.example.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "users2", schema = GlobalIdentifier.SCHEME_ZTEST_SHARD1)
public class UserCustomerIdGenerator {

    @SequenceGenerator(name = "SEQ_GEN", sequenceName = "seq_user")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "SEQ_GEN")
    @Id
    private Long x;

    @Id
    private String key;

    private Long id2;
}

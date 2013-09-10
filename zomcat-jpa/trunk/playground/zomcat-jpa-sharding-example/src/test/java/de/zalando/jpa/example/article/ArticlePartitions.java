package de.zalando.jpa.example.article;

/**
 * Avoid {@link String}s in Annotations. Makes refactorings easier.
 *
 * @author  jbellmann
 */
public interface ArticlePartitions {

    String REPLICATE = "Replicate";
    String HASH_PARTIONING_BY_SKU = "hashPartitioningBySku";
// String VALUE_PARTITIONING_BY_SKU = "valuePartitioningBySku";
    String SHARDED_OBJECT_PARTITIONING = "shardedObjectPartitioning";

    interface Pools {

        String DEFAULT = "default";
        String NODE_2 = "node2";
    }

}

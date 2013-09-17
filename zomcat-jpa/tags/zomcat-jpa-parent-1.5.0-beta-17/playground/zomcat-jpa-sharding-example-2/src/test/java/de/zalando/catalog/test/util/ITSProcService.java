package de.zalando.catalog.test.util;

// @SProcService(namespace = "test")
public interface ITSProcService {
    void truncateAllTables();

    void executeOnAllShards(final String sql);
}

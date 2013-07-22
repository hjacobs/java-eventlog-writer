package de.zalando.catalog.test.util;

import de.zalando.sprocwrapper.SProcService;

@SProcService(namespace = "test")
public interface ITSProcService {
    void truncateAllTables();

    void executeOnAllShards(final String sql);
}

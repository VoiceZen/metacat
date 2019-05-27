package com.netflix.metacat.connector.file;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Scopes;
import com.google.inject.name.Names;
import com.netflix.metacat.common.server.connectors.ConnectorDatabaseService;
import com.netflix.metacat.common.server.connectors.ConnectorPartitionService;
import com.netflix.metacat.common.server.connectors.ConnectorTableService;

import java.util.Map;

/**
 * Standard guice module.
 */
public class FileStoreModule implements Module {
    private final Map<String, String> configuration;
    private final String catalogName;

    public FileStoreModule(final String catalogName, final Map<String, String> configuration) {
        this.catalogName = catalogName;
        this.configuration = configuration;
    }

    @Override
    public void configure(final Binder binder) {
        final String rootLocation = this.configuration.get(FileStoreConfig.Keys.ROOT_LOCATION);
        final StoreSource dataSource = new StoreSource(rootLocation);
        binder.bind(String.class).annotatedWith(Names.named("catalogName")).toInstance(catalogName);
        binder.bind(String.class).annotatedWith(Names.named("rootLocation")).toInstance(rootLocation);
        binder.bind(StoreSource.class).toInstance(dataSource);
        binder.bind(ConnectorDatabaseService.class).to(FileConnectorDatabaseService.class).in(Scopes.SINGLETON);
        binder.bind(ConnectorTableService.class).to(FileConnectorTableService.class).in(Scopes.SINGLETON);
        binder.bind(ConnectorPartitionService.class).to(FileConnectorPartitionService.class).in(Scopes.SINGLETON);

    }
}

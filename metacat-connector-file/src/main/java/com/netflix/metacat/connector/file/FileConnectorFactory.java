/*
 *
 *  Copyright 2017 Netflix, Inc.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 */
package com.netflix.metacat.connector.file;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.netflix.metacat.common.server.connectors.ConnectorDatabaseService;
import com.netflix.metacat.common.server.connectors.ConnectorFactory;
import com.netflix.metacat.common.server.connectors.ConnectorPartitionService;
import com.netflix.metacat.common.server.connectors.ConnectorTableService;
import lombok.NonNull;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * File based implementation of a connector factory.
 *
 * @author tgianos
 * @since 1.0.0
 */
class FileConnectorFactory implements ConnectorFactory {

    private final String catalogName;
    private final String catalogShardName;
    private final Map<String, String> configuration;


    private ConnectorDatabaseService databaseService;
    private ConnectorTableService tableService;
    private ConnectorPartitionService partitionService;

    FileConnectorFactory(
        @Nonnull @NonNull final String catalogName,
        @Nonnull @NonNull final String catalogShardName,
        @Nonnull @NonNull final Map<String, String> configuration
    ) {
        this.catalogName = catalogName;
        this.catalogShardName = catalogShardName;
        this.configuration = configuration;
        init();
    }

    private void init() {
        final FileStoreModule module = new FileStoreModule(this.catalogName, configuration);
        final Injector injector = Guice.createInjector(module);
        this.databaseService = injector.getInstance(ConnectorDatabaseService.class);
        this.tableService = injector.getInstance(ConnectorTableService.class);
        this.partitionService = injector.getInstance(ConnectorPartitionService.class);
    }

    @Override
    public String getCatalogName() {
        return catalogName;
    }

    @Override
    public String getCatalogShardName() {
        return catalogShardName;
    }

    @Override
    public void stop() {

    }

    @Override
    public ConnectorDatabaseService getDatabaseService() {
        return databaseService;
    }

    @Override
    public ConnectorTableService getTableService() {
        return tableService;
    }

    @Override
    public ConnectorPartitionService getPartitionService() {
        return partitionService;
    }
}

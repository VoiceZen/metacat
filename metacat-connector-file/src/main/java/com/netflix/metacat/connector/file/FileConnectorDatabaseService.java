package com.netflix.metacat.connector.file;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.netflix.metacat.common.QualifiedName;
import com.netflix.metacat.common.dto.Pageable;
import com.netflix.metacat.common.dto.Sort;
import com.netflix.metacat.common.server.connectors.ConnectorDatabaseService;
import com.netflix.metacat.common.server.connectors.ConnectorRequestContext;
import com.netflix.metacat.common.server.connectors.ConnectorUtils;
import com.netflix.metacat.common.server.connectors.exception.ConnectorException;
import com.netflix.metacat.common.server.connectors.model.DatabaseInfo;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * File based Database Service.
 *
 * @author tgianos
 * @since 1.0.0
 */
@Slf4j
public class FileConnectorDatabaseService implements ConnectorDatabaseService {


    private final String catalogName;
    private final StoreSource storeSource;

    @Inject
    public FileConnectorDatabaseService(
        @Named("catalogName") final String catalogName,
        final StoreSource storeSource) {
        this.catalogName = catalogName;
        this.storeSource = storeSource;
    }

    @Override
    public List<QualifiedName> listNames(
        @Nonnull final ConnectorRequestContext context,
        @Nonnull final QualifiedName name,
        @Nullable final QualifiedName prefix,
        @Nullable final Sort sort,
        @Nullable final Pageable pageable
    ) {
        try {
            final String rCatalogName = name.getCatalogName();
            log.debug("Beginning to list folders for catalog {} for request {}", rCatalogName, context);
            final List<QualifiedName> names = Lists.newArrayList();
            final Path root = storeSource.getRootPath();
            final DirectoryStream<Path> paths = Files.newDirectoryStream(root, entry -> Files.isDirectory(entry));
            paths.forEach(path -> names.add(
                QualifiedName.ofDatabase(rCatalogName, path.getFileName().toString())
            ));
            final List<QualifiedName> results = ConnectorUtils.paginate(names, pageable);
            return results;
        } catch (Exception exc) {
            throw new ConnectorException(name.toString(), exc);
        }
    }

    public DatabaseInfo get(@Nonnull final ConnectorRequestContext context, @Nonnull final QualifiedName name) {
        final String databaseName = name.getDatabaseName();
        log.debug("Beginning to get database metadata for {} for request {}", databaseName, context);
        return DatabaseInfo.builder().name(name).build();
    }

}

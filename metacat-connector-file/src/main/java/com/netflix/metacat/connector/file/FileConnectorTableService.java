package com.netflix.metacat.connector.file;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.netflix.metacat.common.QualifiedName;
import com.netflix.metacat.common.dto.Pageable;
import com.netflix.metacat.common.dto.Sort;
import com.netflix.metacat.common.server.connectors.ConnectorRequestContext;
import com.netflix.metacat.common.server.connectors.ConnectorTableService;
import com.netflix.metacat.common.server.connectors.ConnectorUtils;
import com.netflix.metacat.common.server.connectors.model.TableInfo;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nullable;
import java.nio.file.DirectoryStream;
import java.nio.file.Path;
import java.util.List;

/**
 * Mysql table service implementation.
 *
 * @author amajumdar
 * @since 1.2.0
 */
@Slf4j
public class FileConnectorTableService implements ConnectorTableService {
    private final StoreSource storeSource;

    @Inject
    public FileConnectorTableService(final StoreSource storeSource) {
        this.storeSource = storeSource;
    }

    public List<QualifiedName> listNames(
        final ConnectorRequestContext context,
        final QualifiedName name,
        @Nullable final QualifiedName prefix,
        @Nullable final Sort sort,
        @Nullable final Pageable pageable
    ) {
        final List<QualifiedName> names = Lists.newArrayList();

        final DirectoryStream<Path> paths = storeSource.getChildren(name.getDatabaseName());
        paths.forEach(path -> names.add(
            QualifiedName.ofTable(name.getCatalogName(), name.getDatabaseName(), path.getFileName().toString())
        ));
        final List<QualifiedName> results = ConnectorUtils.paginate(names, pageable);
        return results;
    }

    public TableInfo get(final ConnectorRequestContext context, final QualifiedName name) {
        return storeSource.getTableInfo(name);
    }
}

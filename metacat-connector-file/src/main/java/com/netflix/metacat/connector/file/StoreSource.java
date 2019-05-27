package com.netflix.metacat.connector.file;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.netflix.metacat.common.QualifiedName;
import com.netflix.metacat.common.server.connectors.exception.ConnectorException;
import com.netflix.metacat.common.server.connectors.model.TableInfo;
import lombok.val;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class StoreSource {

    private final String rootLocation;

    @Inject
    public StoreSource(@Named("rootLocation") final String rootLocation) {
        this.rootLocation = rootLocation;
    }

    public String getRootLocation() {
        return rootLocation;
    }

    public Path getRootPath() {
        return Paths.get(this.rootLocation);
    }

    public DirectoryStream<Path> getChildren(final String folderName) {
        try {
            final Path path = createPath(folderName);
            return Files.newDirectoryStream(path, entry -> !Files.isDirectory(entry));
        } catch (Exception exc) {
            throw new ConnectorException(folderName, exc);
        }
    }

    public TableInfo getTableInfo(final QualifiedName name) {
        val simpleName = name.toString().toLowerCase();
        val builder = TableInfo.builder();
        val resourcePath = createPath(name.getDatabaseName(), name.getTableName());
        if (simpleName.endsWith("csv")) {
            val reader = new CSVReader();
            reader.decorate(builder, resourcePath);
        } else if (simpleName.endsWith("xlsx")) {
            val reader = new ExcelReader();
            reader.decorate(builder, resourcePath);
        } else {
            throw new UnsupportedOperationException("Does not handle files of type " + simpleName);
        }
        return builder.name(name).build();
    }

    private Path createPath(final String... pathElements) {
        return Paths.get(this.rootLocation, pathElements);
    }

}

package com.netflix.metacat.connector.file;

import com.netflix.metacat.common.server.connectors.model.TableInfo;

import java.nio.file.Path;

public interface Reader {
    void decorate(TableInfo.TableInfoBuilder builder, Path filePath);
}

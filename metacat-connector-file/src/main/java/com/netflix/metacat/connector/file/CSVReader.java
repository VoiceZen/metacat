package com.netflix.metacat.connector.file;

import com.google.common.base.Charsets;
import com.netflix.metacat.common.server.connectors.model.FieldInfo;
import com.netflix.metacat.common.server.connectors.model.TableInfo;
import com.netflix.metacat.common.server.converter.DefaultTypeConverter;
import lombok.val;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

public class CSVReader implements Reader {

    private static final DefaultTypeConverter CONVERTER = new DefaultTypeConverter();

    private static final int MAX_LINES = 100_000;

    public void decorate(final TableInfo.TableInfoBuilder builder, final Path filePath) {
        int lineCount = 0;
        try (val reader = Files.newBufferedReader(filePath, Charsets.UTF_8)) {
            val metadata = new HashMap<String, String>();
            String line = reader.readLine();
            while (line != null) {
                if (lineCount == 0) {
                    val fieldInfo = new ArrayList<FieldInfo>();
                    val delimiter = findDelimiter(line).orElseThrow(
                        () -> new UnsupportedOperationException("no delimiter for " + filePath)
                    );

                    metadata.put("delimiter", delimiter);
                    for (String split : line.split(delimiter)) {
                        fieldInfo.add(FieldInfo.builder()
                            .name(split.trim())
                            .sourceType("string")
                            .type(CONVERTER.toMetacatType("string"))
                            .build());
                    }
                    builder.fields(fieldInfo).metadata(metadata);
                }
                line = reader.readLine();
                if (lineCount++ > MAX_LINES) {
                    metadata.putIfAbsent("rows", "gt > " + MAX_LINES);
                    break;
                }
            }
            metadata.put("path", filePath.toString());
            metadata.putIfAbsent("rows", String.valueOf(lineCount));
        } catch (IOException exc) {
            throw new RuntimeException(exc);
        }
    }

    private Optional<String> findDelimiter(final String line) {
        final String[] knownDelimiters = new String[]{",", "\t", "\\$"};
        for (String delimiter : knownDelimiters) {
            if (line.split(delimiter).length > 1) {
                return Optional.of(delimiter);
            }
        }
        return Optional.empty();
    }

}

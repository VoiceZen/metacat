package com.netflix.metacat.connector.file;

import com.netflix.metacat.common.server.connectors.model.FieldInfo;
import com.netflix.metacat.common.server.connectors.model.TableInfo;
import com.netflix.metacat.common.server.converter.DefaultTypeConverter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Slf4j
public class ExcelReader implements Reader {

    private static final DefaultTypeConverter CONVERTER = new DefaultTypeConverter();
    private static final int MAX_LINES = 10_000;

    /**
     * Mutates the builder.
     *
     * @param builder
     * @param filePath
     */
    public void decorate(final TableInfo.TableInfoBuilder builder, final Path filePath) {
        try {
            final Workbook workbook = new XSSFWorkbook(filePath.toFile());
            final int sheetCount = workbook.getNumberOfSheets();
            if (sheetCount > 1) {
                throw new UnsupportedOperationException("Still not planned for multi sheet cases");
            } else {
                final Sheet firstSheet = workbook.getSheetAt(0);
                final Iterator<Row> iterator = firstSheet.iterator();
                final List<FieldInfo> fieldInfo = new ArrayList<>();
                final Map<String, String> metadata = new HashMap<>();

                int totalCount = 0;
                boolean readTableHeader = false;
                while (iterator.hasNext()) {
                    final Row current = iterator.next();
                    val cellIterator = current.cellIterator();
                    while (cellIterator.hasNext()) {
                        val currentCell = cellIterator.next();
                        if (!readTableHeader) {
                            // this is fail fast
                            // cell type other than string bomb, cell empty bomb
                            if (currentCell.getCellType() == CellType.STRING) {
                                val value = currentCell.getStringCellValue();
                                if (StringUtils.isNotBlank(value)) {
                                    fieldInfo.add(FieldInfo.builder()
                                        .name(value)
                                        .sourceType("string")
                                        .type(CONVERTER.toMetacatType("string"))
                                        .build());

                                } else {
                                    throw new UnsupportedOperationException("Header cell value can not be null");
                                }
                            } else {
                                log.info("Check cell data type " + currentCell);
                            }
                        }
                    }
                    readTableHeader = !fieldInfo.isEmpty();
                    totalCount++;
                    if (totalCount++ > MAX_LINES) {
                        metadata.putIfAbsent("rows", "gt > " + MAX_LINES);
                        break;
                    }

                }
                metadata.put("size", String.valueOf(totalCount));
                metadata.put("path", filePath.toString());
                builder.fields(fieldInfo).metadata(metadata);
            }
        } catch (IOException | InvalidFormatException exc) {
            throw new RuntimeException(exc);
        }
    }

}

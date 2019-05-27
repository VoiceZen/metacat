package com.netflix.metacat.connector.file

import com.netflix.metacat.common.server.connectors.model.TableInfo
import spock.lang.Specification

import java.nio.file.Paths

class ReaderSpec extends Specification {

    def "Can get list of columns from a xls file"() {
        when:
        def fileName = "/Volumes/bigdata/clients/indiamart/data/Others/MCAT mapping of Rejected Offers.xlsx"
        def builder = TableInfo.builder();
        def reader = new ExcelReader()
        reader.decorate(builder, Paths.get(fileName))
        def tableInfo = builder.build();
        then:
        tableInfo.getFields().size() > 0
        tableInfo.fields.each{ field ->
            println field.name
        }

    }

    def "Can get list of columns from a csv file"() {
        when:
        def fileName = "/Volumes/bigdata/clients/indiamart/data/Others/Seller Products.csv";
        def builder = TableInfo.builder();
        def reader = new CSVReader()
        reader.decorate(builder, Paths.get(fileName))
        def tableInfo = builder.build();
        then:
        tableInfo.getFields().size() > 0
        tableInfo.fields.each{ field ->
            println field.name
        }
    }

}

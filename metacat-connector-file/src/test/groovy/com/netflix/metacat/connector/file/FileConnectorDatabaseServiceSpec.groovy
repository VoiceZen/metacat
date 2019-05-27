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
package com.netflix.metacat.connector.file

import com.netflix.metacat.common.QualifiedName
import com.netflix.metacat.common.server.connectors.ConnectorRequestContext
import spock.lang.Specification

/**
 * Tests for the FileConnectorDatabaseService.
 *
 * @author vz
 * @since 1.0.0
 */
class FileConnectorDatabaseServiceSpec extends Specification {

    def context = Mock(ConnectorRequestContext)

    def testCatalog = "voicezen"

    def service = new FileConnectorDatabaseService(testCatalog)

    def "Can get list of files at a particular folder"() {
        def qName = QualifiedName.ofCatalog(UUID.randomUUID().toString())

        when:
        def databases = this.service.listNames(this.context, qName, null, null, null)

        then:
        databases.size() == 0
    }

}

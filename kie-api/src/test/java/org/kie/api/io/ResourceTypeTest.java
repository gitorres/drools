/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.api.io;

import java.util.List;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ResourceTypeTest {

    @Test
    public void testBPMN2Extension() {
        final ResourceType BPMN2 = ResourceType.BPMN2;

        assertTrue(BPMN2.matchesExtension("abc.bpmn"));
        assertTrue(BPMN2.matchesExtension("abc.bpmn2"));
        assertTrue(BPMN2.matchesExtension("abc.bpmn-cm"));
        assertFalse(BPMN2.matchesExtension("abc.bpmn2-cm"));
    }

    @Test
    public void testGetAllExtensions() throws Exception {
        final ResourceType BPMN2 = ResourceType.BPMN2;
        final List<String> extensionsBPMN2 = BPMN2.getAllExtensions();

        assertEquals(3, extensionsBPMN2.size());
        assertTrue(extensionsBPMN2.contains("bpmn"));
        assertTrue(extensionsBPMN2.contains("bpmn2"));
        assertTrue(extensionsBPMN2.contains("bpmn-cm"));
        assertFalse(extensionsBPMN2.contains("bpmn2-cm"));

        final ResourceType DRL = ResourceType.DRL;
        final List<String> extensionsDRL = DRL.getAllExtensions();

        assertEquals(1, extensionsDRL.size());
        assertTrue(extensionsDRL.contains("drl"));
    }

    @Test
    public void testDetermineResourceType() {
        assertEquals(ResourceType.DTABLE, ResourceType.determineResourceType("test.drl.xls"));
        assertNull(ResourceType.determineResourceType("test.xls"));
    }
}
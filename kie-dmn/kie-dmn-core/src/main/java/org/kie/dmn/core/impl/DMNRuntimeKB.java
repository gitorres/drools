/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.dmn.core.impl;

import java.util.List;

import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.kie.api.runtime.KieRuntimeFactory;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.event.DMNRuntimeEventListener;
import org.kie.dmn.core.compiler.DMNProfile;

public interface DMNRuntimeKB {

    List<DMNModel> getModels();

    DMNModel getModel(String namespace, String modelName);

    DMNModel getModelById(String namespace, String modelId);

    List<DMNProfile> getProfiles();

    List<DMNRuntimeEventListener> getListeners();

    /**
     * @throws UnsupportedOperationException if not supported on this platform.
     */
    ClassLoader getRootClassLoader();

    /**
     * @throws UnsupportedOperationException if not supported on this platform.
     */
    InternalKnowledgeBase getInternalKnowledgeBase();

    /**
     *
     * @param kieBaseName
     * @return
     * @throws UnsupportedOperationException if not supported on this platform.
     */
    KieRuntimeFactory getKieRuntimeFactory(String kieBaseName);


}

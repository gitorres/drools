/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
package org.kie.pmml.compiler.commons.factories;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.dmg.pmml.DefineFunction;
import org.dmg.pmml.DerivedField;
import org.dmg.pmml.TransformationDictionary;
import org.junit.Test;
import org.kie.pmml.commons.transformations.KiePMMLDefineFunction;
import org.kie.pmml.commons.transformations.KiePMMLDerivedField;
import org.kie.pmml.commons.transformations.KiePMMLTransformationDictionary;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.kie.pmml.compiler.api.testutils.PMMLModelTestUtils.getRandomTransformationDictionary;
import static org.kie.pmml.compiler.commons.factories.InstanceFactoriesTestCommon.commonVerifyKiePMMLDefineFunction;
import static org.kie.pmml.compiler.commons.factories.InstanceFactoriesTestCommon.commonVerifyKiePMMLDerivedField;

public class KiePMMLTransformationDictionaryInstanceFactoryTest {

    @Test
    public void getKiePMMLTransformationDictionary() {
        final TransformationDictionary toConvert = getRandomTransformationDictionary();
        KiePMMLTransformationDictionary retrieved =
                KiePMMLTransformationDictionaryInstanceFactory.getKiePMMLTransformationDictionary(toConvert,
                                                                                                                                      Collections.emptyList());
        assertNotNull(retrieved);

        List<DerivedField> derivedFields = toConvert.getDerivedFields();
        List<KiePMMLDerivedField> derivedFieldsToVerify = retrieved.getDerivedFields();
        assertEquals(derivedFields.size(), derivedFieldsToVerify.size());
        derivedFields.forEach(derivedFieldSource -> {
            Optional<KiePMMLDerivedField> derivedFieldToVerify =
                    derivedFieldsToVerify.stream().filter(param -> param.getName().equals(derivedFieldSource.getName().getValue()))
                            .findFirst();
            assertTrue(derivedFieldToVerify.isPresent());
            commonVerifyKiePMMLDerivedField(derivedFieldToVerify.get(), derivedFieldSource);
        });

        List<DefineFunction> defineFunctions = toConvert.getDefineFunctions();
        List<KiePMMLDefineFunction> defineFunctionsToVerify = retrieved.getDefineFunctions();
        assertEquals(defineFunctions.size(), defineFunctionsToVerify.size());
        defineFunctions.forEach(defineFunctionSource -> {
            Optional<KiePMMLDefineFunction> defineFunctionToVerify =
                    defineFunctionsToVerify.stream().filter(param -> param.getName().equals(defineFunctionSource.getName()))
                            .findFirst();
            assertTrue(defineFunctionToVerify.isPresent());
            commonVerifyKiePMMLDefineFunction(defineFunctionToVerify.get(), defineFunctionSource);
        });
    }
}
/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.drools.core.phreak;

import java.util.Collection;

import org.drools.core.common.BetaConstraints;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.ReteEvaluator;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.LeftTupleSinkNode;
import org.drools.core.reteoo.ReactiveFromNode;
import org.drools.core.reteoo.ReactiveFromNodeLeftTuple;
import org.drools.core.reteoo.RightTupleImpl;
import org.drools.core.rule.ContextEntry;
import org.drools.core.spi.PropagationContext;
import org.drools.core.spi.Tuple;

import static org.drools.core.phreak.PhreakFromNode.deleteChildLeftTuple;
import static org.drools.core.phreak.PhreakFromNode.isAllowed;
import static org.drools.core.phreak.PhreakFromNode.propagate;

public class ReactiveObjectUtil {

    public enum ModificationType {
        NONE, MODIFY, ADD, REMOVE
    }

    public static void notifyModification(ReactiveObject reactiveObject) {
        notifyModification( reactiveObject, reactiveObject.getLeftTuples(), ModificationType.MODIFY);
    }

    public static void notifyModification( Object object, Collection<Tuple> leftTuples, ModificationType type ) {
        for (Tuple leftTuple : leftTuples) {
            if (!( (ReactiveFromNodeLeftTuple) leftTuple ).updateModificationState( object, type )) {
                continue;
            }
            PropagationContext propagationContext = leftTuple.getPropagationContext();
            ReactiveFromNode node = leftTuple.getTupleSink();

            LeftTupleSinkNode sink = node.getSinkPropagator().getFirstLeftTupleSink();
            ReteEvaluator reteEvaluator = propagationContext.getFactHandle().getReteEvaluator();

            reteEvaluator.addPropagation(new ReactivePropagation(object, (ReactiveFromNodeLeftTuple)leftTuple, propagationContext, node, sink, type));
        }
    }

    static class ReactivePropagation extends PropagationEntry.AbstractPropagationEntry {

        private final Object object;
        private final ReactiveFromNodeLeftTuple leftTuple;
        private final PropagationContext propagationContext;
        private final ReactiveFromNode node;
        private final LeftTupleSinkNode sink;
        private final ModificationType type;

        ReactivePropagation( Object object, ReactiveFromNodeLeftTuple leftTuple, PropagationContext propagationContext, ReactiveFromNode node, LeftTupleSinkNode sink, ModificationType type ) {
            this.object = object;
            this.leftTuple = leftTuple;
            this.propagationContext = propagationContext;
            this.node = node;
            this.sink = sink;
            this.type = type;
        }

        @Override
        public void execute( ReteEvaluator reteEvaluator ) {
            if ( leftTuple.resetModificationState( object ) == ModificationType.NONE ) {
                return;
            }

            ReactiveFromNode.ReactiveFromMemory mem = reteEvaluator.getNodeMemory(node);
            InternalFactHandle factHandle = node.createFactHandle( reteEvaluator, object );

            if ( type != ModificationType.REMOVE && isAllowed( factHandle, node.getAlphaConstraints(), reteEvaluator, mem ) ) {
                ContextEntry[] context = mem.getBetaMemory().getContext();
                BetaConstraints betaConstraints = node.getBetaConstraints();
                betaConstraints.updateFromTuple( context, reteEvaluator, leftTuple );

                propagate( sink,
                           leftTuple,
                           new RightTupleImpl( factHandle ),
                           betaConstraints,
                           propagationContext,
                           context,
                           RuleNetworkEvaluator.useLeftMemory( node, leftTuple ),
                           mem.getStagedLeftTuples(),
                           null );
            } else {
                LeftTuple childLeftTuple = ((LeftTuple)leftTuple).getFirstChild();
                while (childLeftTuple != null) {
                    LeftTuple next = childLeftTuple.getHandleNext();
                    if ( object == childLeftTuple.getFactHandle().getObject() ) {
                        deleteChildLeftTuple( propagationContext, mem.getStagedLeftTuples(), null, childLeftTuple );
                    }
                    childLeftTuple = next;
                }
            }

            mem.getBetaMemory().setNodeDirty(node, reteEvaluator);
        }
    }
}

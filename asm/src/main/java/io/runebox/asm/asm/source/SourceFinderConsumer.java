/*
 * Copyright 2018 Sam Sun <github-contact@samczsun.com>
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

package io.runebox.asm.asm.source;

import java.util.List;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.tree.analysis.*;

public abstract class SourceFinderConsumer implements Opcodes {
    protected final SourceFinderConsumer parent;

    public SourceFinderConsumer(SourceFinderConsumer parent) {
        this.parent = parent;
    }

    public abstract SourceResult findSource(MethodNode methodNode, Frame<SourceValue>[] frames, List<AbstractInsnNode> instructions, AbstractInsnNode source, SourceValue want, AbstractInsnNode now);
}

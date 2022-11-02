/*
 * Copyright 2016 Sam Sun <me@samczsun.com>
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package io.runebox.asm.analyzer.frame;

import org.objectweb.asm.Opcodes;

public class NewFrame extends Frame {
    private String ntype;

    public NewFrame(String type) {
        super(Opcodes.NEW);
        this.ntype = type;
    }

    public String getType() {
        return this.ntype;
    }
}
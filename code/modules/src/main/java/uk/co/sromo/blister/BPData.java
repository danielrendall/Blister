/*
 * Copyright 2011 Daniel Rendall
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

package uk.co.sromo.blister;

import uk.co.sromo.blister.BPItem;

/**
 * Represents a block of arbitrary byte data.
 *
 * TODO: Document how to deal with the signed / unsigned byte confusion!
 */
public class BPData extends BPItem {
    private final byte[] data;

    public BPData(byte[] data) {
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }

    @Override
    public String toString() {
        return "BPData{" +
                "data=" + data +
                '}';
    }

    @Override
    public Type getType() {
        return Type.Data;
    }

    @Override
    public void accept(BPVisitor visitor) {
        visitor.visit(this);
    }
}

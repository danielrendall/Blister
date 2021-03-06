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

/**
 * No-op implementation of BPVisitor - convenient base class for making useful
 * Visitors.
 */
public class BPVisitorSupport implements BPVisitor {
    public void visit(BPArray item) {
    }

    public void visit(BPBoolean item) {
    }

    public void visit(BPData item) {
    }

    public void visit(BPDate item) {
    }

    public void visit(BPDict item) {
    }

    public void visit(BPInt item) {
    }

    public void visit(BPNull item) {
    }

    public void visit(BPReal item) {
    }

    public void visit(BPSet item) {
    }

    public void visit(BPString item) {
    }

    public void visit(BPUid item) {
    }
}

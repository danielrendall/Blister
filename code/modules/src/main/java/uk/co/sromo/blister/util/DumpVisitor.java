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

package uk.co.sromo.blister.util;

import org.apache.commons.lang.StringUtils;
import uk.co.sromo.blister.*;

import java.util.logging.Logger;

/**
 * Very simple example of a visitor, used to turn a binary plist into XML.
 * TODO: Write a proper method for indenting with spaces!
 */
public class DumpVisitor implements BPVisitor {

    int depth = 0;
    String spaces = "                                         ";
    StringBuilder sb = new StringBuilder();

    private void print(String aString) {
        sb.append(spaces.substring(0, depth)).append(aString).append("\n");
    }

    public String getXml() {
        return sb.toString();
    }

    public void visit(BPArray item) {
        print("<array>");
        depth++;
        for(BPItem child : item) {
            child.accept(this);
        }
        depth--;
        print("</array>");
    }

    public void visit(BPBoolean item) {
        print(item.getValue() ? "<true/>" : "<false/>");
    }

    public void visit(BPData item) {
        print("<data>" + item + "</data>");
    }

    public void visit(BPDate item) {
        print("<date>" + item + "</date>");
    }

    public void visit(BPDict item) {
        print("<dict>");
        depth++;
        for(BPString child : item.keySet()) {
            print("<key>" + child + "</key>");
            item.get(child).accept(this);
        }
        depth--;
        print("</dict>");
    }

    public void visit(BPInt item) {
        print("<int>" + Long.toString(item.getLongValue()) + "</int>");
    }

    public void visit(BPNull item) {
        print("<null/>");
    }

    public void visit(BPReal item) {
        print("<real>" + item + "</real>");
    }

    public void visit(BPSet item) {
        print("<set>");
        depth++;
        for(BPItem child : item) {
            child.accept(this);
        }
        depth--;
        print("</set>");
    }

    public void visit(BPString item) {
        print("<string>" + item + "</string>");
    }

    public void visit(BPUid item) {
        print("<uid>" + item + "</uid>");
    }
}

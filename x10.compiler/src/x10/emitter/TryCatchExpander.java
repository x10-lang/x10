/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */
package x10.emitter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import polyglot.ast.Block;
import polyglot.ast.Catch;
import polyglot.types.Type;
import polyglot.util.CodeWriter;
import polyglot.visit.Translator;

public class TryCatchExpander extends Expander {
    
    private class CatchBlock {
        private final String exClass;
        private final String exInstName;
        private final Expander bodyExpander;
        private final Catch catchBlock;

        private CatchBlock(String exClass, String exInstName, Expander bodyExpander) {
            this.exClass = exClass;
            this.exInstName = exInstName;
            this.bodyExpander = bodyExpander;
            this.catchBlock = null;
        }

        public CatchBlock(Catch catchBlock) {
            this.exClass = null;
            this.exInstName = null;
            this.bodyExpander = null;
            this.catchBlock = catchBlock;
        }

        void prettyPrint(Translator tr) {
            if (catchBlock != null) {
                er.prettyPrint(catchBlock, tr);
            } else {
                w.write("catch (");
                w.write(exClass);
                w.write(" ");
                w.write(exInstName);
                w.write(") {");
                bodyExpander.expand(tr);
                w.write("}");
            }
        }
    }

    private final CodeWriter w;
    private final Block block;
    private final Expander child;
    private final List<CatchBlock> catches = new ArrayList<CatchBlock>();
    private final Block finalBlock;

    public TryCatchExpander(CodeWriter w, Emitter er, Block block, Block finalBlock) {
        super(er);
        this.w = w;
        this.block = block;
        this.child = null;
        this.finalBlock = finalBlock;
    }

    public TryCatchExpander(CodeWriter w, Emitter er, TryCatchExpander expander, Block finalBlock) {
        super(er);
        this.w = w;
        this.block = null;
        this.child = expander;
        this.finalBlock = finalBlock;
    }

    public void addCatchBlock(String exClass, String exInstName, Expander expander) {
        catches.add(new CatchBlock(exClass, exInstName, expander));
    }

    public void addCatchBlock(Catch catchBlock) {
        catches.add(new CatchBlock(catchBlock));
    }

    @Override
    public void expand(Translator tr) {
        w.write("try {");

        if (block != null) {
            er.prettyPrint(block, tr);
        } else if (child != null) {
            child.expand(tr);
        }

        w.write("}");

        for (CatchBlock catchBlock : catches) {
            catchBlock.prettyPrint(tr);
        }

        if (finalBlock != null) {
            w.begin(0);
            w.write("finally {");
            er.prettyPrint(finalBlock, tr);
            w.write("}");
        }
    }

}

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

package x10cuda.types;

import java.util.ArrayList;
import java.util.Iterator;

import polyglot.ast.Expr;
import polyglot.ast.LocalDecl;
import polyglot.types.Name;
import polyglot.types.Type;
import polyglot.visit.Translator;
import polyglot.types.TypeSystem;
import x10.util.ClassifiedStream;
import x10.util.StreamWrapper;

public class ConstMem {
    
    ArrayList<Decl> decls = new ArrayList<Decl>();
    
    private abstract static class Decl {
        public final LocalDecl ast;
        public Decl (LocalDecl ast) { this.ast = ast; }
        abstract public void generateDef(StreamWrapper out, String offset, Translator tr);
        abstract public void generateInit(StreamWrapper out, String offset, Translator tr);
        abstract public void generateSize(StreamWrapper inc, Translator tr);
    }
    
    private static class Rail extends Decl {
        public final Expr numElements;
        public final Expr init;
        public Rail (LocalDecl ast, Expr numElements, Expr init) {
            super(ast);
            this.numElements = numElements;
            this.init = init;
        }
        public void generateDef(StreamWrapper out, String offset, Translator tr) {
            String name = ast.name().id().toString();
            // FIXME: x10_float is baked in here
            out.write("x10_float *"+name+" = (x10_float*) &__shm["+offset+"];"); out.newline();
        }
        public void generateInit(StreamWrapper out, String offset, Translator tr) {
            out.write("{"); out.newline(4); out.begin(0);
            out.write("x10_int __len = ");
            tr.print(null, numElements, out);
            out.write(";"); out.newline();
            out.write("for (int i=0 ; i<__len ; ++i) {"); out.newline(4); out.begin(0);
            // TODO: assumes rail initialised with another rail -- closure version also possible
            out.write(ast.name().id()+"[i] = ");
            tr.print(null, init, out);
            out.write("[i];"); out.newline();
            out.end(); out.newline();
            out.write("}");
            out.end(); out.newline();
            out.write("}");
        }
        public void generateSize(StreamWrapper inc, Translator tr) {
            tr.print(null, numElements, inc);
            // FIXME: x10_float is baked in here
            inc.write("*sizeof(x10_float)");

        }
    }
    private static class Var extends Decl {
        public Var (LocalDecl ast) { super(ast); }
        public void generateDef(StreamWrapper out, String offset, Translator tr) {
            // TODO: not implemented
            assert false: "not implemented";
        }
        public void generateInit(StreamWrapper out, String offset, Translator tr) {
            // TODO: not implemented
            assert false: "not implemented";
        }
        public void generateSize(StreamWrapper inc, Translator tr) {
            // TODO: not implemented
            assert false: "not implemented";
        }
    }
    
    public void addRail(LocalDecl ast, Expr numElements, Expr init) {
        decls.add(new Rail(ast,numElements,init));
    }

    public void addVar(LocalDecl ast) {
        decls.add(new Var(ast));
    }
    
    public boolean has(Name n) {
        for (Decl d : decls) {
            if (d.ast.name().id() == n) {
                return true;
            }
        }
        return false;
    }

    public void generateCode(StreamWrapper out, Translator tr) {
        if (decls.size()==0) return;

        out.write("// shm");
        out.newline();
        
        for (ConstMem.Decl d : decls) {
                // FIXME: offset is broken when more than one shm definition
                String offset = "0";
                d.generateDef(out, offset, tr);
                out.write("if (threadIdx.x == 0) {"); out.newline(4); out.begin(0);
                d.generateInit(out, offset, tr);
                out.end(); out.newline();
                out.write("}"); out.newline();
        }
        
        out.write("__syncthreads();"); out.newline();        

        out.forceNewline();                
    }

    public void generateSize(StreamWrapper inc, Translator tr) {
        // TODO Auto-generated method stub
        String prefix = "";
        for (ConstMem.Decl d : decls) {
            inc.write(prefix);
            d.generateSize(inc, tr);
            prefix = " + ";
        }
        if (prefix.equals("")) inc.write("0");
    }
}

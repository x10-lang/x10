/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created on Oct 7, 2004
 */
package x10cuda.ast;

import polyglot.util.CodeWriter;
import polyglot.visit.Translator;
import x10.visit.X10DelegatingVisitor;
import x10.util.StreamWrapper;
import x10cpp.ast.X10CPPDelFactory_c;
import x10cuda.visit.CUDACodeGenerator;

/**
 * @author Dave Cunningham
 */
public class X10CUDADelFactory_c extends X10CPPDelFactory_c {

    protected X10DelegatingVisitor makeCodeGenerator(CodeWriter w, Translator tr) {
        return new CUDACodeGenerator((StreamWrapper)w,tr);
    }

}


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
package polyglot.ext.x10cuda.ast;

import polyglot.ext.x10.visit.X10DelegatingVisitor;
import polyglot.ext.x10cpp.ast.X10CPPDelFactory_c;
import polyglot.ext.x10cuda.visit.CUDACodeGenerator;
import polyglot.util.CodeWriter;
import polyglot.visit.Translator;
import x10c.util.StreamWrapper;

/**
 * @author Dave Cunningham
 */
public class X10CUDADelFactory_c extends X10CPPDelFactory_c {

    protected X10DelegatingVisitor makeCodeGenerator(CodeWriter w, Translator tr) {
        return new CUDACodeGenerator((StreamWrapper)w,tr);
    }

}


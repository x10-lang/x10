/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2010.
 */
package x10.optimizations.inlining;

import polyglot.ast.Node;
import polyglot.frontend.Job;
import polyglot.types.QName;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.InternalCompilerError;
import x10.extension.X10Ext;
import x10.types.X10Def;
import x10.util.AnnotationUtils;

/**
 * @author Bowen Alpern
 *
 */
public class InlineUtils extends AnnotationUtils {

    /**
     * Annotation types.
     */
    Type InlineType;
    Type InlineOnlyType;
    Type NoInlineType;
    Type NativeType;
    Type NativeRepType;
    Type NativeClassType;
    Type ConstantType;
    Type EmbedType;

    private Job job;

    /**
     * @param j 
     * @throws InternalCompilerError
     */
    InlineUtils(Job job) throws InternalCompilerError {
        this.job = job;
        TypeSystem ts = job.extensionInfo().typeSystem(); 
        ConstantType = ts.CompileTimeConstant();
        InlineType = ts.Inline();
        InlineOnlyType = ts.InlineOnly();
        NoInlineType = ts.NoInline();
        NativeType = ts.NativeType();
        NativeRepType = ts.NativeRep();
        NativeClassType = ts.NativeClass();
        EmbedType = ts.Embed();
    }

    /**
     * @param node
     * @return
     */
    boolean inliningRequired(Node node) {
        return hasAnnotation(node, InlineType);
    }

    /**
     * @param node
     * @return
     */
    boolean inliningProhibited(Node node) {
        return hasAnnotation(node, NoInlineType) || hasNativeAnnotations(node, job);
    }

    /**
     * @param def
     * @return
     */
    boolean inliningRequired(X10Def def) {
        return hasAnnotation(def, InlineType);
    }

    /**
     * @param def
     * @return
     */
    boolean inliningProhibited(X10Def def) {
        return hasAnnotation(def, NoInlineType)  || 
               hasAnnotation(def, NativeRepType) || 
               hasAnnotation(def, NativeClassType);
        
    }

    /**
     * @param n
     * @return
     */
    boolean isNativeCode(Node n) {
        return hasNativeAnnotations(n, job);
    }

    /**
     * @return
     */
    public boolean hasEmbedAnnotation(Node n) {
        return hasAnnotation(n, EmbedType);
    }

}

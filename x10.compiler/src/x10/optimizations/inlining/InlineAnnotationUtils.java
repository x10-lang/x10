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

/**
 * @author Bowen Alpern
 *
 */
public class InlineAnnotationUtils extends AnnotationUtils {

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

    /**
     * Names of the annotation types that pertain to inlining.
     */
    private static final QName CONSTANT_ANNOTATION     = QName.make("x10.compiler.CompileTimeConstant");
    private static final QName INLINE_ANNOTATION       = QName.make("x10.compiler.Inline");
    private static final QName INLINE_ONLY_ANNOTATION  = QName.make("x10.compiler.InlineOnly");
    private static final QName NO_INLINE_ANNOTATION    = QName.make("x10.compiler.NoInline");
    private static final QName NATIVE_CLASS_ANNOTATION = QName.make("x10.compiler.NativeClass");

    private Job job;

    /**
     * @param j 
     * @throws InternalCompilerError
     */
    InlineAnnotationUtils(Job job) throws InternalCompilerError {
        this.job = job;
        TypeSystem ts = job.extensionInfo().typeSystem(); 
        try {
            ConstantType = ts.systemResolver().findOne(CONSTANT_ANNOTATION);
            InlineType = ts.systemResolver().findOne(INLINE_ANNOTATION);
            InlineOnlyType = ts.systemResolver().findOne(INLINE_ONLY_ANNOTATION);
            NoInlineType = ts.systemResolver().findOne(NO_INLINE_ANNOTATION);
            NativeType = ts.NativeType();
            NativeRepType = ts.NativeRep();
            NativeClassType = ts.systemResolver().findOne(NATIVE_CLASS_ANNOTATION);
        } catch (SemanticException e) {
            InternalCompilerError ice = new InternalCompilerError("Unable to find required Annotation Type", e);
            throw ice; // annotation types are required!
        }
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
        return hasAnnotation(node, NoInlineType);
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

}

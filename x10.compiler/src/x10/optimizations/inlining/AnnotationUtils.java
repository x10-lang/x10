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

import static x10cpp.visit.ASTQuery.assertNumberOfInitializers;
import static x10cpp.visit.ASTQuery.getStringPropertyInit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import polyglot.ast.Node;
import polyglot.frontend.Job;
import polyglot.types.QName;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.InternalCompilerError;
import x10.extension.X10Ext;
import x10.types.X10ClassType;
import x10.types.X10Def;
import x10.types.X10ProcedureDef;
import x10.visit.ExpressionFlattener;

/**
 * @author Bowen Alpern
 *
 */
public class AnnotationUtils {

    /**
     * @param memberDef
     * @param type
     * @return
     */
    public static boolean hasAnnotation(X10Def def, Type type) {
        return !getAnnotations(def, type).isEmpty();
    }

    /**
     * @param def
     * @param type
     * @return
     */
    public static List<Type> getAnnotations(X10Def def, Type type) {
        return def.annotationsMatching(type);
    }

    /**
     * @param node
     * @param type
     * @return
     */
    public static boolean hasAnnotation(Node node, Type type) {
        return !getAnnotations(node, type).isEmpty();
    }

    /**
     * @param node
     * @param type
     * @return
     */
    public static List<X10ClassType> getAnnotations(Node node, Type type) {
        assert node.ext() instanceof X10Ext;
        return ((X10Ext) node.ext()).annotationMatching(type);
    }

    private static final List<String> javaNativeStrings = Arrays.asList("java");
    private static final List<String> cppNativeStrings  = Arrays.asList("c++", "cuda");

    public static List<X10ClassType> getNativeAnnotations (Node node, Job job) {
        List<X10ClassType> result = new ArrayList<X10ClassType>();
        X10ClassType type = job.extensionInfo().typeSystem().NativeType();
        List<X10ClassType> annotations = getAnnotations(node, type);
        for (X10ClassType annotation : annotations) {
            assertNumberOfInitializers(annotation, 2);
            String platform = getStringPropertyInit(annotation, 0);
            List<String> nativeStrings = (ExpressionFlattener.javaBackend(job) ? javaNativeStrings : cppNativeStrings);
            for (String ns : nativeStrings) {
                if (platform != null && platform.equals(ns)) {
                    result.add(annotation);
                }
            }
        }
        return result;
    }

    /**
     * Annotation types.
     */
    static Type InlineType;
    static Type InlineOnlyType;
    static Type NoInlineType;
    static Type NativeType;
    static Type NativeRepType;
    static Type NativeClassType;
    static Type ConstantType;

    /**
     * Names of the annotation types that pertain to inlining.
     */
    private static final QName CONSTANT_ANNOTATION     = QName.make("x10.compiler.CompileTimeConstant");
    private static final QName INLINE_ANNOTATION       = QName.make("x10.compiler.Inline");
    private static final QName INLINE_ONLY_ANNOTATION  = QName.make("x10.compiler.InlineOnly");
    private static final QName NO_INLINE_ANNOTATION    = QName.make("x10.compiler.NoInline");
    private static final QName NATIVE_CLASS_ANNOTATION = QName.make("x10.compiler.NativeClass");

    private static boolean initialized = false;
    /**
     * @throws InternalCompilerError
     */
    static void initialize(TypeSystem ts) throws InternalCompilerError {
        if (initialized) return;
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
        initialized = true;
    }

    /**
     * @param node
     * @return
     */
    public static boolean inliningRequired(Node node) {
        return !((X10Ext) node.ext()).annotationMatching(InlineType).isEmpty();
    }

    /**
     * @param node
     * @return
     */
    static boolean inliningProhibited(Node node) {
        return !((X10Ext) node.ext()).annotationMatching(NoInlineType).isEmpty();
    }

    /**
     * @param def
     * @return
     */
    static boolean inliningRequired(X10Def def) {
        return hasAnnotation(def, InlineType);
    }

    /**
     * @param def
     * @return
     */
    static boolean inliningProhibited(X10Def def) {
        return hasAnnotation(def, NoInlineType)  || 
               hasAnnotation(def, NativeRepType) || 
               hasAnnotation(def, NativeClassType);
        
    }

}

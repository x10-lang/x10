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
package x10.util;

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
     * @return
     */
    public static List<Type> getAnnotations(X10Def def) {
        return def.annotations();
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

    /**
     * @param node
     * @return
     */
    public List<X10ClassType> getAnnotations(Node node) {
        assert node.ext() instanceof X10Ext;
        return ((X10Ext) node.ext()).annotationTypes();
    }

    private static final List<String> javaNativeStrings = Arrays.asList("java");
    private static final List<String> cudaNativeStrings = Arrays.asList("c++", "cuda");
    private static final List<String> cppNativeStrings  = Arrays.asList("c++");

    // XTENLANG-2824: chose between cudaNativeStrings and cppNativeStrings based on whether or not
    // we are within a CUDAKernel
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

    public static boolean hasNativeAnnotations (Node node, Job job) {
        return !getNativeAnnotations(node, job).isEmpty();
    }
}

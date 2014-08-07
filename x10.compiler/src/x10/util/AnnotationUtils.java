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
import java.util.Collections;
import java.util.List;

import polyglot.ast.Node;
import polyglot.ast.ProcedureDecl;
import polyglot.frontend.Job;
import polyglot.types.NoClassException;
import polyglot.types.ProcedureDef;
import polyglot.types.QName;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.InternalCompilerError;
import x10.ast.AnnotationNode;
import x10.extension.X10Ext;
import x10.types.X10ClassType;
import x10.types.X10Def;
import x10.types.X10ProcedureDef;
import x10.visit.ExpressionFlattener;

/**
 * @author Bowen Alpern
 *
 */
public abstract class AnnotationUtils {

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
     * TODO [IP]: same as annotationsMatching() below. 
     */
    public static List<X10ClassType> getAnnotations(Node node, Type type) {
        assert node.ext() instanceof X10Ext;
        return ((X10Ext) node.ext()).annotationMatching(type);
    }

    /**
     * @param node
     * @return
     */
    public static List<X10ClassType> getAnnotations(Node node) {
        assert node.ext() instanceof X10Ext;
        return ((X10Ext) node.ext()).annotationTypes();
    }

    /**
     * @param ts
     * @param o
     * @param name
     * @return
     * @throws SemanticException
     */
    public static X10ClassType annotationNamed(TypeSystem ts, Node o, QName name)
            throws SemanticException {
        // Nate's code. This one.
        if (o.ext() instanceof X10Ext) {
            X10Ext ext = (X10Ext) o.ext();
            Type baseType = ts.systemResolver().findOne(name);
            List<X10ClassType> ats = ext.annotationMatching(baseType);
            if (ats.size() > 1) {
                throw new SemanticException("Expression has more than one "+ name + " annotation.", o.position());
            }
            if (!ats.isEmpty()) {
                X10ClassType at = ats.get(0);
                return at;
            }
        }
        return null;
    }

    /**
     * @param o
     * @param name
     * @return
     */
    public static List<AnnotationNode> annotationNodesNamed(Node o, QName name) {
        if (o != null && o.ext() instanceof X10Ext) {
            X10Ext ext = (X10Ext) o.ext();
            List<AnnotationNode> l = new ArrayList<AnnotationNode>();
            for (AnnotationNode an : ext.annotations()) {
                X10ClassType ct = an.annotationInterface();
                if (name.equals(ct.fullName())) {
                    l.add(an);
                }
            }
            return l;
        }
        return null;
    }
    
    /**
     * @param o
     * @param annotationType
     * @return
     */
    public static List<X10ClassType> annotationsMatching(Node o, Type annotationType) {
        if (o != null && o.ext() instanceof X10Ext) {
            X10Ext ext = (X10Ext) o.ext();
            return ext.annotationMatching(annotationType);
        }
        return null;
    }

    /**
     * @param o
     * @param annotationType
     * @return
     */
    public static List<AnnotationNode> annotationNodesMatching(Node o, Type annotationType) {
        if (o != null && o.ext() instanceof X10Ext) {
            X10Ext ext = (X10Ext) o.ext();
            List<AnnotationNode> l = new ArrayList<AnnotationNode>();
            for (AnnotationNode an : ext.annotations()) {
                X10ClassType ct = an.annotationInterface();
                if (ct.isSubtype(annotationType, annotationType.typeSystem().emptyContext())) {
                    l.add(an);
                }
            }
            return l;
        }
        return null;
    }
    
    /**
     * @param ts
     * @param dec
     * @param name
     * @return
     */
    public static boolean hasAnnotation(TypeSystem ts, Node dec, QName name) {
        try {
            if (AnnotationUtils.annotationNamed(ts, dec, name) != null)
                return true;
        } catch (NoClassException e) {
            if (!e.getClassName().equals(name.toString()))
                throw new InternalCompilerError(
                        "Something went terribly wrong", e);
        } catch (SemanticException e) {
            throw new InternalCompilerError("Something is terribly wrong", e);
        }
        return false;
    }

    /**
     * @param o
     * @param fullName
     * @return
     */
    public static List<X10ClassType> annotationsNamed(Node o, QName fullName) {
        if (o != null && o.ext() instanceof X10Ext) {
            X10Ext ext = (X10Ext) o.ext();
            return ext.annotationNamed(fullName);
        }
        return null;
    }

    private static final List<String> javaNativeStrings = Arrays.asList("java");
    private static final List<String> cudaNativeStrings = Arrays.asList("c++", "cuda");
    private static final List<String> cppNativeStrings  = Arrays.asList("c++");

    // XTENLANG-2824: chose between cudaNativeStrings and cppNativeStrings based on whether or not
    // we are within a CUDAKernel
    public static List<X10ClassType> getNativeAnnotations (Node node, Job job) {
        List<X10ClassType> result = new ArrayList<X10ClassType>();
        X10ClassType type = job.extensionInfo().typeSystem().NativeType();
        boolean isManaged = ((x10.ExtensionInfo) job.extensionInfo()).isManagedX10();
        List<X10ClassType> annotations = getAnnotations(node, type);
        for (X10ClassType annotation : annotations) {
            assertNumberOfInitializers(annotation, 2);
            String platform = getStringPropertyInit(annotation, 0);
            List<String> nativeStrings = (isManaged ? javaNativeStrings : cppNativeStrings);
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

/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

package x10doc.doc;

import com.sun.javadoc.AnnotationTypeDoc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.ParameterizedType;
import com.sun.javadoc.Type;
import com.sun.javadoc.TypeVariable;
import com.sun.javadoc.WildcardType;

public class X10Type implements Type {
    protected polyglot.types.Type pType;

    public X10Type(polyglot.types.Type t) {
        pType = t;
    }

    public boolean isX10Specific() {
        return false;
    }

    public static boolean isX10Specific(Type t) {
        if ((t == null) || !(t instanceof X10Type)) {
            // meant to handle the case when t is of type X10ClassDoc
            return false;
        }
        return ((X10Type) t).isX10Specific();
    }

    public static String descriptor(Type t) {
        if ((t == null) || !(t instanceof X10Type)) {
            // meant to handle the case when t is of type X10ClassDoc
            return "";
        }
        return ((X10Type) t).descriptor();
    }

    public String descriptor() {
        return pType.toString();
    }

    public static String toString(Type t) {
        if (t instanceof X10ClassDoc) {
            return ((X10ClassDoc) t).qualifiedName();
        }
        return ((X10Type) t).pType.toString();
    }

    public AnnotationTypeDoc asAnnotationTypeDoc() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @since 1.8
     * @return
     */
    public Type getElementType() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @since 1.8
     * @return
     */
    public com.sun.javadoc.AnnotatedType asAnnotatedType() {
        // TODO Auto-generated method stub
        return null;        
    }

    public ClassDoc asClassDoc() {
        return null;
    }

    public ParameterizedType asParameterizedType() {
        // TODO Auto-generated method stub
        return null;
    }

    public TypeVariable asTypeVariable() {
        // TODO Auto-generated method stub
        return null;
    }

    public WildcardType asWildcardType() {
        // TODO Auto-generated method stub
        return null;
    }

    public String dimension() {
        return (pType.isArray() ? String.valueOf(pType.toClass().toArray().dims()) : "");
    }

    public boolean isPrimitive() {
        return pType.isVoid();
    }

    public String qualifiedTypeName() {
        // return pType.toString();
        return "!!QTYPENAME!!";
    }

    public String simpleTypeName() {
        // return pType.toString();
        return "!!SIMPLETYPENAME!!";
    }

    public String typeName() {
        return pType.toString();
        // return "!!TYPENAME!!";
    }
}

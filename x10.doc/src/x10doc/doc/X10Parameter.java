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

import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.Parameter;
import com.sun.javadoc.Type;

public class X10Parameter implements Parameter {
    private String name;
    private Type type;

    public X10Parameter(String name, Type type) {
        this.name = name;
        this.type = type;
    }

    public boolean isX10Specific() {
        if (type instanceof X10ClassDoc) {
            return false;
        }
        return ((X10Type) type).isX10Specific();
    }

    public AnnotationDesc[] annotations() {
        // TODO Auto-generated method stub
        return new AnnotationDesc[0];
    }

    public String name() {
        return name;
    }

    public String toString() {
        return name();
    }

    public Type type() {
        return type;
    }

    public String typeName() {
        return type.qualifiedTypeName();
    }

}

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

package x10.serialization;

import java.lang.reflect.Method;

import x10.runtime.impl.java.Runtime;
import x10.runtime.impl.java.Runtime.OSGI_MODES;

class SerializationUtils {

    /* cached classes and methods for OSGi bundle based classloading */
    static Class<?> FrameworkUtilClass;
    static Method getBundleMethod;
    static Class<?> BundleClass;
    static Method getBundleContextMethod;
    static Method getSymbolicNameMethod;
    static Method getVersionMethod;
    static Method loadClassMethod;
    static Class<?> BundleContextClass;
    static Method getBundlesMethod;
    static Class<?> VersionClass;
    static Method compareVersionMethod;
    static {
        if (Runtime.OSGI != OSGI_MODES.DISABLED) {
            try {
                FrameworkUtilClass = Class.forName("org.osgi.framework.FrameworkUtil");
                getBundleMethod = FrameworkUtilClass.getDeclaredMethod("getBundle", Class.class);
                getBundleMethod.setAccessible(true);
                BundleClass = Class.forName("org.osgi.framework.Bundle");
                getBundleContextMethod = BundleClass.getDeclaredMethod("getBundleContext");
                getBundleContextMethod.setAccessible(true);
                getSymbolicNameMethod = BundleClass.getDeclaredMethod("getSymbolicName");
                getSymbolicNameMethod.setAccessible(true);
                getVersionMethod = BundleClass.getDeclaredMethod("getVersion");
                getVersionMethod.setAccessible(true);
                loadClassMethod = BundleClass.getDeclaredMethod("loadClass", String.class);
                loadClassMethod.setAccessible(true);
                BundleContextClass = Class.forName("org.osgi.framework.BundleContext");
                getBundlesMethod = BundleContextClass.getDeclaredMethod("getBundles");
                getBundlesMethod.setAccessible(true);
                VersionClass = Class.forName("org.osgi.framework.Version");
                compareVersionMethod = VersionClass.getDeclaredMethod("compareTo", VersionClass);
                compareVersionMethod.setAccessible(true);
            } catch (Throwable e) {
                String msg = "FATAL ERROR! Cannot load OSGi framework.";
                System.out.println(msg);
                e.printStackTrace();
            }
        }
    }

    static boolean useX10SerializationProtocol(Class<?> clazz) {
        if (!x10.serialization.X10JavaSerializable.class.isAssignableFrom(clazz)) return false;
        
        // Now look for the serialization/deserialization methods to be sure it
        // really implements the protocol.
        try {
            clazz.getDeclaredMethod("$_deserialize_body", clazz, X10JavaDeserializer.class);
            clazz.getDeclaredMethod("$_deserializer", X10JavaDeserializer.class);
            clazz.getMethod("$_serialize", X10JavaSerializer.class);
        } catch (Exception e) {
            return false;
        }

        return true;
    }

}

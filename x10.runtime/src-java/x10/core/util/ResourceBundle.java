/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

package x10.core.util;

import java.io.IOException;
import java.util.Locale;

import x10.core.Ref;
import x10.rtt.NamedType;
import x10.rtt.RuntimeType;
import x10.rtt.Type;
import x10.rtt.Types;
import x10.serialization.X10JavaDeserializer;
import x10.serialization.X10JavaSerializable;
import x10.serialization.X10JavaSerializer;

@x10.runtime.impl.java.X10Generated
final public class ResourceBundle extends Ref implements X10JavaSerializable {

    @SuppressWarnings("unchecked")
    public static final RuntimeType<ResourceBundle> $RTT = NamedType.<ResourceBundle> make("x10.util.ResourceBundle", ResourceBundle.class);
    public RuntimeType<?> $getRTT() { return $RTT; }
    public Type<?> $getParam(int i) { return null; }

    private Object writeReplace() throws java.io.ObjectStreamException {
        return new x10.serialization.SerializationProxy(this);
    }

    public static X10JavaSerializable $_deserialize_body(ResourceBundle $_obj, X10JavaDeserializer $deserializer) throws IOException {
        $_obj.bundle = (java.util.ResourceBundle) $deserializer.readObject();
        return $_obj;
    }

    public static X10JavaSerializable $_deserializer(X10JavaDeserializer $deserializer) throws IOException {
        ResourceBundle $_obj = new ResourceBundle((System[]) null);
        $deserializer.record_reference($_obj);
        return $_deserialize_body($_obj, $deserializer);
    }

    public void $_serialize(X10JavaSerializer $serializer) throws IOException {
        $serializer.write(this.bundle);
    }

    // constructor just for allocation
    public ResourceBundle(System[] $dummy) { 
    }

    public java.util.ResourceBundle bundle;

    // creation method for java code (1-phase java constructor)
    public ResourceBundle(String baseName) {
        this((System[]) null);
        x10$util$ResourceBundle$$init$S(baseName);
    }

    // constructor for non-virtual call
    final public ResourceBundle x10$util$ResourceBundle$$init$S(String baseName) {
        this.bundle = java.util.ResourceBundle.getBundle(baseName);
        return this;
    }

    public static ResourceBundle getBundle(String baseName) {
        ResourceBundle rb = new ResourceBundle((System[]) null);
        rb.x10$util$ResourceBundle$$init$S(baseName);
        return rb;
    }

    // creation method for java code (1-phase java constructor)
    public ResourceBundle(String baseName, Object loaderOrLoaded) {
        this((System[]) null);
        x10$util$ResourceBundle$$init$S(baseName, loaderOrLoaded);
    }

    // constructor for non-virtual call
    final public ResourceBundle x10$util$ResourceBundle$$init$S(String baseName, Object loaderOrLoaded) {
        if (loaderOrLoaded == null) {
            this.bundle = java.util.ResourceBundle.getBundle(baseName);
        } else {
            Locale locale = Locale.getDefault();
            ClassLoader loader;
            if (loaderOrLoaded instanceof ClassLoader) {
                loader = (ClassLoader) loaderOrLoaded;
            } else if (loaderOrLoaded instanceof Class<?>) {
                loader = ((Class<?>) loaderOrLoaded).getClassLoader();
            } else {
                loader = loaderOrLoaded.getClass().getClassLoader();
            }
            this.bundle = java.util.ResourceBundle.getBundle(baseName, locale, loader);
        }
        return this;
    }

    public static ResourceBundle getBundle(String baseName, Object loaderOrLoaded) {
        ResourceBundle rb = new ResourceBundle((System[]) null);
        rb.x10$util$ResourceBundle$$init$S(baseName, loaderOrLoaded);
        return rb;
    }

    final public boolean containsKey$O(String key) {
        return bundle.containsKey(key);
    }

    final public String getString(String key) {
        return bundle.getString(key);
    }

    @SuppressWarnings("unchecked")
    final public x10.util.Set<String> keySet() {
        java.util.Set<String> js = bundle.keySet();
        x10.util.HashSet<String> xs = new x10.util.HashSet<String>((System[]) null, Types.STRING);
        xs.x10$util$HashSet$$init$S(js.size());
        x10.util.MapSet<String> mxs = (x10.util.MapSet<String>) xs;
        java.util.Iterator<String> ji = js.iterator();
        while (ji.hasNext()) {
            mxs.add__0x10$util$MapSet$$T$O(ji.next());
        }
        return xs;
    }


    final public ResourceBundle x10$util$ResourceBundle$$this$x10$util$ResourceBundle() {
        return ResourceBundle.this;
    }

    final public void __fieldInitializers_x10_util_ResourceBundle() {
    }

}

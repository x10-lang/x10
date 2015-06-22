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

package x10.serialization;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import x10.rtt.RuntimeType;
import x10.runtime.impl.java.Runtime;
import x10.runtime.impl.java.Runtime.OSGI_MODES;
import static x10.serialization.SerializationUtils.getBundleMethod;
import static x10.serialization.SerializationUtils.getSymbolicNameMethod;
import static x10.serialization.SerializationUtils.getVersionMethod;

/**
 * Used during serialization to maintain a mapping from Class to id.
 */
abstract class SerializationDictionary implements SerializationConstants {

    protected final Map<Class<?>,Short> dict;

    public SerializationDictionary(Map<Class<?>,Short> myMap) {
        dict = myMap;
    }

    short getSerializationId(Class<?> clazz, Object obj, DataOutputStream unused) throws IOException {
        if (obj instanceof RuntimeType<?>) {
            short sid = ((RuntimeType<?>)obj).$_get_serialization_id();
            if (sid <= MAX_HARDCODED_ID) {
                return sid;
            }
        }
        Short id = dict.get(clazz);
        return null == id ? NO_PREASSIGNED_ID : id.shortValue();
    }

    void serializeIdAssignment(DataOutputStream dos, short id, Class<?> clazz) throws IOException {
        dos.writeShort(DYNAMIC_ID_ID);
        dos.writeShort(id);
        String name = clazz.getName();
        dos.writeInt(name.length());
        dos.write(name.getBytes());
        if (Runtime.OSGI != OSGI_MODES.DISABLED) {
            // Standard version
//          org.osgi.framework.Bundle bundle = org.osgi.framework.FrameworkUtil.getBundle(es.getKey());
//          String bundleName, bundleVersion;
//          if (bundle != null) {
//              bundleName = bundle.getSymbolicName();
//              bundleVersion = bundle.getVersion().toString();
//          } else {
//              bundleName = bundleVersion = "";
//          }
//          dos.writeInt(bundleName.length());
//          dos.write(bundleName.getBytes());
//          dos.writeInt(bundleVersion.length());
//          dos.write(bundleVersion.getBytes());
            // Reflection version
            try {
                Object/*Bundle*/ bundle = getBundleMethod.invoke(null, clazz);
                String bundleName, bundleVersion;
                if (bundle != null) {
                    bundleName = (String) getSymbolicNameMethod.invoke(bundle);
                    bundleVersion = getVersionMethod.invoke(bundle).toString();
                } else {
                    bundleName = bundleVersion = "";
                }
                dos.writeInt(bundleName.length());
                dos.write(bundleName.getBytes());
                dos.writeInt(bundleVersion.length());
                dos.write(bundleVersion.getBytes());
            } catch (RuntimeException e) {
                e.printStackTrace();
                throw e;
            } catch (IOException e) {
                e.printStackTrace();
                throw e;
            } catch (Exception e) {
                e.printStackTrace();
                throw new IOException(e.getMessage(), e);
            }
        }
    }

    @Override
    public String toString() {
        return dict.toString();
    }

    /**
     * A SerializationDictionary that is used to maintain the set of globally known
     * serialization ids.  It uses a ConcurrentHashMap as its backing storage because
     * multiple threads may be concurrently reading/writing the dictionary.
     */
    static final class MasterSerializationDictionary extends SerializationDictionary {

        public MasterSerializationDictionary() {
            super(new ConcurrentHashMap<Class<?>, Short>());
        }

        void addEntry(Class<?> klazz, short id) {
            assert !dict.containsKey(klazz) : "MasterSerializationDictionary.addEntry: duplicate key assignment";
            assert !dict.containsValue(id) :  "MasterSerializationDictionary.addEntry: duplicate id assignment";
            dict.put(klazz, id);
        }
    }

    /**
     * A SerializationDictionary that is used to maintain the set of serialization ids
     * for a single message.  It uses a simple HashMap as its local store since it 
     * will only be accessed by a single thread and also internally delegates
     * to a parent dictionary (most likely a MasterSerializationDictionary) when asked for
     * an id before assigning an id itself.
     */
    static final class LocalSerializationDictionary extends SerializationDictionary {
        final SerializationDictionary parent;

        protected short nextId;

        public LocalSerializationDictionary(SerializationDictionary parent, short firstId) {
            super(new HashMap<Class<?>,Short>());
            this.parent = parent;
            this.nextId = firstId;
        }

        short getSerializationId(Class<?> clazz, Object obj, DataOutputStream dos) throws IOException {
            if (parent != null) {
                short sid = parent.getSerializationId(clazz, obj, dos);
                if (sid != NO_PREASSIGNED_ID) return sid;
            }
            short sid = super.getSerializationId(clazz, obj, dos);
            if (sid == NO_PREASSIGNED_ID) {
                sid = Short.valueOf(nextId++);
                serializeIdAssignment(dos, sid, clazz);
                dict.put(clazz, sid);
            }
            return sid;
        }
    }
}

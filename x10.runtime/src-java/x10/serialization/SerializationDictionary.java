/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2012.
 */

package x10.serialization;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import x10.rtt.RuntimeType;
import x10.runtime.impl.java.Runtime;

/**
 * Used during serialization to maintain a mapping from Class to id.
 */
abstract class SerializationDictionary implements SerializationConstants {

    protected final Map<Class<?>,Short> dict;

    public SerializationDictionary(Map<Class<?>,Short> myMap) {
        dict = myMap;
    }

    short getSerializationId(Class<?> clazz, Object obj) {
        if (obj instanceof RuntimeType<?>) {
            short sid = ((RuntimeType<?>)obj).$_get_serialization_id();
            if (sid <= MAX_HARDCODED_ID) {
                return sid;
            }
        }
        Short id = dict.get(clazz);
        return null == id ? NO_PREASSIGNED_ID : id.shortValue();
    }

    byte[] encode() throws IOException {
        if (dict.size() == 0) {
            return new byte[2]; // zero initialized, so 2 bytes of 0 is the short 0.
        }
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeShort(dict.size());
        for (Entry<Class<?>, Short> es  : dict.entrySet()) {
            dos.writeShort(es.getValue());
            String name = es.getKey().getName();
            dos.writeInt(name.length());
            dos.write(name.getBytes());
            if (Runtime.OSGI) {
            	// Standard version
//            	org.osgi.framework.Bundle bundle = org.osgi.framework.FrameworkUtil.getBundle(es.getKey());
//            	assert bundle != null;
//            	String bundleName = bundle.getSymbolicName();
//            	dos.writeInt(bundleName.length());
//            	dos.write(bundleName.getBytes());
//            	String bundleVersion = bundle.getVersion().toString();
//            	dos.writeInt(bundleVersion.length());
//            	dos.write(bundleVersion.getBytes());
            	// Reflection version
        		try {
					Class<?> FrameworkUtilClass = Class.forName("org.osgi.framework.FrameworkUtil");
					Method getBundleMethod = FrameworkUtilClass.getDeclaredMethod("getBundle", Class.class);
					Object/*Bundle*/ bundle = getBundleMethod.invoke(null, es.getKey());
	            	assert bundle != null;
					Class<?> BundleClass = Class.forName("org.osgi.framework.Bundle");
					
					Method getSymbolicNameMethod = BundleClass.getDeclaredMethod("getSymbolicName");
					String bundleName = (String) getSymbolicNameMethod.invoke(bundle);
					dos.writeInt(bundleName.length());
					dos.write(bundleName.getBytes());
					
					Method getVersionMethod = BundleClass.getDeclaredMethod("getVersion");
					String bundleVersion = getVersionMethod.invoke(bundle).toString();
					dos.writeInt(bundleVersion.length());
					dos.write(bundleVersion.getBytes());
				} catch (Exception e) {
					if (e instanceof IOException) throw (IOException) e;
					e.printStackTrace();
					throw new IOException(e);
				}
            }
        }
        dos.close();

        return baos.toByteArray();
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

        short getSerializationId(Class<?> clazz, Object obj) {
            if (parent != null) {
                short sid = parent.getSerializationId(clazz, obj);
                if (sid != NO_PREASSIGNED_ID) return sid;
            }
            short sid = super.getSerializationId(clazz, obj);
            if (sid == NO_PREASSIGNED_ID) {
                sid = Short.valueOf(nextId++);
                dict.put(clazz, sid);           
            }
            return sid;
        }
    }
}

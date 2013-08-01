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

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import x10.runtime.impl.java.Runtime;

abstract class DeserializationDictionary implements SerializationConstants {
    protected final Map<Short,Method> idsToMethod;
    protected final Map<Short,Class<?>> idsToClass;

    DeserializationDictionary(Map<Short,Method> mMap, Map<Short,Class<?>> cMap) {
        this.idsToMethod = mMap;
        this.idsToClass = cMap;
    }

    // non OSGI environment
    Class<?> loadClass(String name) throws ClassNotFoundException {
    	assert !Runtime.OSGI;
        if (Runtime.TRACE_SER) Runtime.printTraceMessage("DeserializationDictionary.loadClass: loading "+name);
        return Class.forName(name);
    }

    void addEntry(short id, String name) {
        Class<?> clazz;
        try {
            clazz = loadClass(name);
        } catch (ClassNotFoundException e) {
            String msg = "DeserializationDictionary.addEntry: failed to load class "+name;
            if (Runtime.TRACE_SER) Runtime.printTraceMessage(msg);
            throw new RuntimeException(msg, e);
        }
        addEntry(Short.valueOf(id), clazz);
    }

    // OSGI environment
    Class<?> loadClass(String name, String bundleName, String bundleVersion) throws ClassNotFoundException {
    	assert Runtime.OSGI;
    	
    	if (bundleName.equals("")) {
			if (Runtime.TRACE_SER) Runtime.printTraceMessage("DeserializationDictionary.loadClass: loading "+name+" without bundle");
    		return Class.forName(name);
    	}
    	
    	// Standard version
//    	org.osgi.framework.Bundle _bundle = org.osgi.framework.FrameworkUtil.getBundle(this.getClass());
//    	assert _bundle != null;
//    	org.osgi.framework.Bundle[] bundles = _bundle.getBundleContext().getBundles();
//        
//    	for (org.osgi.framework.Bundle bundle : bundles) {
//    		if (bundleName.equals(bundle.getSymbolicName()) && bundleVersion.equals(bundle.getVersion().toString())) {
//                if (Runtime.TRACE_SER) Runtime.printTraceMessage("DeserializationDictionary.loadClass: loading "+name+" with bundle "+bundle);
//    			return bundle.loadClass(name);
//    		}
//    	}
    	// Reflection version
		try {
			Class<?> FrameworkUtilClass = Class.forName("org.osgi.framework.FrameworkUtil");
			Method getBundleMethod = FrameworkUtilClass.getDeclaredMethod("getBundle", Class.class);
			getBundleMethod.setAccessible(true);
			Object/*Bundle*/ _bundle = getBundleMethod.invoke(null, this.getClass());
			assert _bundle != null;
			Class<?> BundleClass = Class.forName("org.osgi.framework.Bundle");
			Method getBundleContextMethod = BundleClass.getDeclaredMethod("getBundleContext");
			getBundleContextMethod.setAccessible(true);
			Object/*BundleContext*/ bundleContext = getBundleContextMethod.invoke(_bundle);
			Class<?> BundleContextClass = Class.forName("org.osgi.framework.BundleContext");
			Method getBundlesMethod = BundleContextClass.getDeclaredMethod("getBundles");
			getBundlesMethod.setAccessible(true);
			Object/*Bundle*/[] bundles = (Object[]) getBundlesMethod.invoke(bundleContext);
			
			Method getSymbolicNameMethod = BundleClass.getDeclaredMethod("getSymbolicName");
			getSymbolicNameMethod.setAccessible(true);
			Method getVersionMethod = BundleClass.getDeclaredMethod("getVersion");
			getVersionMethod.setAccessible(true);
			for (Object/*Bundle*/ bundle : bundles) {
				String bundleName_ = (String) getSymbolicNameMethod.invoke(bundle);
				String bundleVersion_ = getVersionMethod.invoke(bundle).toString();
				if (bundleName.equals(bundleName_) && bundleVersion.equals(bundleVersion_)) {
					if (Runtime.TRACE_SER) Runtime.printTraceMessage("DeserializationDictionary.loadClass: loading "+name+" with bundle "+bundle);
					Method loadClassMethod = BundleClass.getDeclaredMethod("loadClass", String.class);
					loadClassMethod.setAccessible(true);
					return (Class<?>) loadClassMethod.invoke(bundle, name);
				}
			}
		} catch (RuntimeException e) {
			throw e;
		} catch (ClassNotFoundException e) {
			throw e;
		} catch (Exception e) {
//			e.printStackTrace();
			throw new ClassNotFoundException(e.getMessage(), e);
		}
		
    	String msg = "Cannot find bundle "+bundleName+" "+bundleVersion+" for loading class "+name;
    	throw new ClassNotFoundException(msg);
//    	return Class.forName(name);
    }

    void addEntry(short id, String name, String bundleName, String bundleVersion) {
        Class<?> clazz;
        try {
            clazz = loadClass(name, bundleName, bundleVersion);
        } catch (ClassNotFoundException e) {
            String msg = "DeserializationDictionary.addEntry: failed to load class "+name;
            if (Runtime.TRACE_SER) Runtime.printTraceMessage(msg);
            throw new RuntimeException(msg, e);
        }
        addEntry(Short.valueOf(id), clazz);
    }

    void addEntry(Short id, Class<?> clazz) {
        idsToClass.put(id, clazz);
        if (!clazz.isInterface() && SerializationUtils.useX10SerializationProtocol(clazz)) {
            Method m;
            try {
                m = clazz.getDeclaredMethod("$_deserializer", X10JavaDeserializer.class);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("DeserializationDictionary: class "+clazz+
                                           " directly implements X10JavaSerializable but does not have a $_deserializer method", e);
            }
            m.setAccessible(true);
            idsToMethod.put(id, m);
        }
    }

    Class<?> getClassForID(short sid) {
        return idsToClass.get(Short.valueOf(sid));
    }

    Method getMethod(short sid) {
        return idsToMethod.get(Short.valueOf(sid));
    }

    static final class MasterDeserializationDictionary extends DeserializationDictionary {

        MasterDeserializationDictionary() {
            super(new ConcurrentHashMap<Short, Method>(), new ConcurrentHashMap<Short, Class<?>>());
        }

        @Override
        Class<?> getClassForID(short sid) {
            Class<?> clazz = super.getClassForID(sid);
            assert clazz != null || sid >= FIRST_DYNAMIC_ID : "master dictionary does not contain id for supposedly shared sid " + sid;
            return clazz;
        }

//        @Override
//        Method getMethod(short sid) {
//            Method m = super.getMethod(sid);
//            // Note: it is valid for m to be null when sid doesn't implement X10JavaSerializable. So no assert here.
//            return m;
//        }

        @Override
        void addEntry(short id, String name) {
            assert id >= FIRST_SHARED_ID && id < FIRST_DYNAMIC_ID : "invalid id in addEntry of master dictionary" + id;
            super.addEntry(id, name);
        }

        @Override
        void addEntry(short id, String name, String bundleName, String bundleVersion) {
            assert id >= FIRST_SHARED_ID && id < FIRST_DYNAMIC_ID : "invalid id in addEntry of master dictionary" + id;
            super.addEntry(id, name, bundleName, bundleVersion);
        }
    }

    static final class LocalDeserializationDictionary extends DeserializationDictionary {
        final private DeserializationDictionary shared;

        LocalDeserializationDictionary(DeserializationDictionary parent) {
            super(new HashMap<Short, Method>(), new HashMap<Short, Class<?>>());
            this.shared = parent;
        }

        LocalDeserializationDictionary(X10JavaDeserializer jds, DeserializationDictionary parent) {
            this(parent);

            try {
                short numEntries = jds.readShort();
                if (Runtime.TRACE_SER) {
                    Runtime.printTraceMessage("\tReceiving "+numEntries+" serialization ids");                
                }
                for (short i=0; i<numEntries; i++) {
                    short id = jds.readShort();
                    String name = jds.readStringValue();
                    if (Runtime.OSGI) {
                    	String bundleName = jds.readStringValue();
                    	String bundleVersion = jds.readStringValue();
                    	if (Runtime.TRACE_SER) {
                    		Runtime.printTraceMessage("\tserialization id: "+id+" = "+name+", bundle = "+bundleName+" "+bundleVersion);                
                    	}
                    	addEntry(id, name, bundleName, bundleVersion);
                    } else {
                    	if (Runtime.TRACE_SER) {
                    		Runtime.printTraceMessage("\tserialization id: "+id+" = "+name);                
                    	}
                    	addEntry(id, name);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException("Failure while reading message dictionary", e);
            }
        }
        
        public LocalDeserializationDictionary(SerializationDictionary js, DeserializationDictionary parent) {
            this(parent);
            
            if (Runtime.TRACE_SER) {
                Runtime.printTraceMessage("\tLocal copy of "+js.dict.size()+" serialization ids");                
            }
            
            for (Map.Entry<Class<?>,Short> entry : js.dict.entrySet()) {
                Short id = entry.getValue();
                Class<?> clazz = entry.getKey();
                if (Runtime.TRACE_SER) {
                    Runtime.printTraceMessage("\tserialization id: "+id+" = "+clazz.getName());                
                }
                addEntry(id, clazz);
            }
        
        }

		@Override
		Class<?> getClassForID(short sid) {
            if (sid < FIRST_DYNAMIC_ID) {
                return shared.getClassForID(sid);
            }
            Class<?> clazz = super.getClassForID(sid);
            assert clazz != null : "DeserializationDictionary: id "+sid+" is not mapped to a class!";
            return clazz;
        }

        @Override
        Method getMethod(short sid) {
            if (sid < FIRST_DYNAMIC_ID) {
                return shared.getMethod(sid);
            } else {
                return super.getMethod(sid);
            }
        }
    }
}
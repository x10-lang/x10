/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2011.
 */

package x10.x10rt;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import sun.misc.Unsafe;
import x10.core.GlobalRef;
import x10.core.IndexedMemoryChunk;
// TODO CHECKED_THROWABLE stop converting Java exception types that are mapped (i.e. not wrapped) to x10 exception types. 
//import x10.core.X10Throwable;
import x10.io.SerialData;
import x10.rtt.NamedStructType;
import x10.rtt.NamedType;
import x10.rtt.RuntimeType;
import x10.runtime.impl.java.Runtime;

public class X10JavaDeserializer {
	
	private static ConcurrentHashMap<Class<?>, DeserializerThunk> thunks = new ConcurrentHashMap<Class<?>, DeserializerThunk>(50);


    // When a Object is deserialized record its position
    private List<Object> objectList;
    private DataInputStream in;
    private int counter = 0;
    private static Unsafe unsafe = getUnsafe();
    private static final String CONSTRUCTOR_METHOD_NAME_FOR_REFLECTION = "$init_for_reflection";

    public X10JavaDeserializer(DataInputStream in) {
        this.in = in;
        objectList = new ArrayList<Object>();
    }

    public DataInput getInpForHadoop() {
    	return in;
    }
    
    public int record_reference(Object obj) {
        if (Runtime.TRACE_SER) {
            String className = obj == null ? "null" : obj.getClass().getName();
            Runtime.printTraceMessage("\tRecorded new reference of type " + Runtime.ANSI_CYAN + Runtime.ANSI_BOLD + className + Runtime.ANSI_RESET + " at " + counter + "  (absolute) in map");
        }
        objectList.add(counter, obj);
        counter++;
        return counter - 1;
    }

    public void update_reference(int pos, Object obj) {
        objectList.set(pos, obj);
    }


    public Object getObjectAtPosition(int pos) {
        Object o = objectList.get(pos);
        if (Runtime.TRACE_SER) {
            Runtime.printTraceMessage("\t\tRetrieving repeated reference  of type " + Runtime.ANSI_CYAN + Runtime.ANSI_BOLD + o.getClass().getName() + Runtime.ANSI_RESET + " at " + pos + "  (absolute) in map");
        }
        return o;
    }

    public Object readRef() throws IOException {
        short serializationID = readShort();
        return readRef(serializationID);
    }

    public Object readRef(short serializationID) throws IOException {
        if (Runtime.TRACE_SER) {
            Runtime.printTraceMessage("Dispatching deserialisation using id " + serializationID);
        }
        if (serializationID == DeserializationDispatcher.refValue) {
            return getObjectAtPosition(readInt());
        }
        if (serializationID == DeserializationDispatcher.javaClassID) {
            return deserializeRefUsingReflection(serializationID);
        }
        return DeserializationDispatcher.getInstanceForId(serializationID, this);
    }

    @SuppressWarnings("unchecked")
	public <T> void readArray(T[] array) throws IOException {
        int length = array.length;
        for (int i = 0; i < length; i++) {
            array[i] = (T) readRef();
        }
    }

    public int readInt() throws IOException {
        int v = in.readInt();
        if (Runtime.TRACE_SER) {
            Runtime.printTraceMessage("Deserializing [****] a " + Runtime.ANSI_CYAN + "int" + Runtime.ANSI_RESET + ": " + v);
        }
        return v;
    }

    public int[] readIntArray() throws IOException {
        int length = in.readInt();
        int[] v = new int[length];
        for (int i = 0; i < length; i++) {
            v[i] = in.readInt();
        }
        return v;
    }

    public boolean readBoolean() throws IOException {
        boolean v = in.readBoolean();
        if (Runtime.TRACE_SER) {
            Runtime.printTraceMessage("Deserializing [*] a " + Runtime.ANSI_CYAN + "boolean" + Runtime.ANSI_RESET + ": " + v);
        }
        return v;
    }

    public boolean[] readBooleanArray() throws IOException {
        int length = in.readInt();
        boolean[] v = new boolean[length];
        for (int i = 0; i < length; i++) {
            v[i] = in.readBoolean();
        }
        return v;
    }

    public char readChar() throws IOException {
        char v = in.readChar();
        if (Runtime.TRACE_SER) {
            Runtime.printTraceMessage("Deserializing [**] a " + Runtime.ANSI_CYAN + "char" + Runtime.ANSI_RESET + ": " + v);
        }
        return v;
    }

    public char[] readCharArray() throws IOException {
        int length = in.readInt();
        char[] v = new char[length];
        for (int i = 0; i < length; i++) {
            v[i] = in.readChar();
        }
        return v;
    }

    public byte readByte() throws IOException {
        byte v = in.readByte();
        if (Runtime.TRACE_SER) {
            Runtime.printTraceMessage("Deserializing [****] a " + Runtime.ANSI_CYAN + "byte" + Runtime.ANSI_RESET + ": " + v);
        }
        return v;
    }

    public byte[] readByteArray() throws IOException {
        int length = in.readInt();
        byte[] v = new byte[length];
        _readByteArray(length, v);
        return v;
    }

    public void _readByteArray(int length, byte[] v) throws IOException {
        int read = 0;
        while (read < length) {
                read += in.read(v, read, length-read);
        }
    }

    public short readShort() throws IOException {
        short v = in.readShort();
        if (Runtime.TRACE_SER) {
            Runtime.printTraceMessage("Deserializing [**] a " + Runtime.ANSI_CYAN + "short" + Runtime.ANSI_RESET + ": " + v);
        }
        return v;
    }

    public short[] readShortArray() throws IOException {
        int length = in.readInt();
        short[] v = new short[length];
        for (int i = 0; i < length; i++) {
            v[i] = in.readShort();
        }
        return v;
    }

    public long readLong() throws IOException {
        long v = in.readLong();
        if (Runtime.TRACE_SER) {
            Runtime.printTraceMessage("Deserializing [********] a " + Runtime.ANSI_CYAN + "long" + Runtime.ANSI_RESET + ": " + v);
        }
        return v;
    }

    public long[] readLongArray() throws IOException {
        int length = in.readInt();
        long[] v = new long[length];
        for (int i = 0; i < length; i++) {
            v[i] = in.readLong();
        }
        return v;
    }

    public double readDouble() throws IOException {
        double v = in.readDouble();
        if (Runtime.TRACE_SER) {
            Runtime.printTraceMessage("Deserializing [********] a " + Runtime.ANSI_CYAN + "double" + Runtime.ANSI_RESET + ": " + v);
        }
        return v;
    }

    public double[] readDoubleArray() throws IOException {
        int length = in.readInt();
        double[] v = new double[length];
        for (int i = 0; i < length; i++) {
            v[i] = in.readDouble();
        }
        return v;
    }

    public float readFloat() throws IOException {
        float v = in.readFloat();
        if (Runtime.TRACE_SER) {
            Runtime.printTraceMessage("Deserializing [********] a " + Runtime.ANSI_CYAN + "float" + Runtime.ANSI_RESET + ": " + v);
        }
        return v;
    }

    public float[] readFloatArray() throws IOException {
        int length = in.readInt();
        float[] v = new float[length];
        for (int i = 0; i < length; i++) {
            v[i] = in.readFloat();
        }
        return v;
    }

    public String readString() throws IOException {
        short classID = in.readShort();
        if (classID == DeserializationDispatcher.refValue) {
            return (String) getObjectAtPosition(readInt());
        } else if (classID == DeserializationDispatcher.NULL_ID) {
            if (Runtime.TRACE_SER) {
                Runtime.printTraceMessage("Deserializing a null reference");
            }
            return null;
        }
        String str = readStringValue();
        if (Runtime.TRACE_SER) {
            Runtime.printTraceMessage("Deserializing a " + Runtime.ANSI_CYAN + "String" + Runtime.ANSI_RESET + ": " + str);
        }
        record_reference(str);
        return str;
    }

    public String readStringValue() throws IOException {
        int length = readInt();
        byte[] bytes = new byte[length];
        _readByteArray(length, bytes);
        return new String(bytes);
    }

    public String[] readStringArray() throws IOException {
        int length = in.readInt();
        String[] v = new String[length];
        for (int i = 0; i < length; i++) {
            v[i] = readString();
        }
        return v;
    }

    public Object readRefUsingReflection() throws IOException {
        short serializationID = readShort();
        if (serializationID == DeserializationDispatcher.refValue) {
            return getObjectAtPosition(readInt());
        } else if (serializationID == DeserializationDispatcher.NULL_ID) {
            if (Runtime.TRACE_SER) {
                Runtime.printTraceMessage("Deserialized a null reference");
            }
            return null;
        } else if (serializationID <= DeserializationDispatcher.MAX_ID_FOR_PRIMITIVE) {
            return DeserializationDispatcher.deserializePrimitive(serializationID, this);
        }

        if (Runtime.TRACE_SER) {
            Runtime.printTraceMessage("Deserializing non-null value with id " + serializationID);
        }
        return deserializeRefUsingReflection(serializationID);
    }

    private Object deserializeRefUsingReflection(short serializationID) throws IOException {
            Class<?> clazz = DeserializationDispatcher.getClassForID(serializationID, this);
            String className = clazz.getName();

            if (className.startsWith("x10.rtt.") &&
            		("x10.rtt.FloatType".equals(className) 
            				|| "x10.rtt.IntType".equals(className)
            				|| "x10.rtt.DoubleType".equals(className)
            				|| "x10.rtt.LongType".equals(className)
            				|| "x10.rtt.BooleanType".equals(className)
            				|| "x10.rtt.StringType".equals(className)
            				|| "x10.rtt.CharType".equals(className)
            				|| "x10.rtt.ByteType".equals(className)
            				|| "x10.rtt.ShortType".equals(className)
            				|| "x10.rtt.ObjectType".equals(className)
            				|| "x10.rtt.UByteType".equals(className)
            				|| "x10.rtt.UIntType".equals(className)
            				|| "x10.rtt.ULongType".equals(className)
            				|| "x10.rtt.UShortType".equals(className)
            		)) {
                // These classes don't implement the serialization/deserialization routines, hence we deserialize the superclass
                readShort();
                clazz = clazz.getSuperclass();
            }
            
			try {
				DeserializerThunk thunk = getDeserializerThunk(clazz);
				return thunk.deserializeObject(clazz, this);
			} catch (SecurityException e) {
				throw new RuntimeException(e);
			} catch (NoSuchFieldException e) {
				throw new RuntimeException(e);
			} catch (NoSuchMethodException e) {
				throw new RuntimeException(e);
			} catch (IllegalArgumentException e) {
				throw new RuntimeException(e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			} catch (InvocationTargetException e) {
				throw new RuntimeException(e);
			} catch (InstantiationException e) {
				throw new RuntimeException(e);
			}
    }
    
    // This method is called from generated code when an X10 class has a Java superclass.
    public <T> Object deserializeClassUsingReflection(Class<? extends Object> clazz, T obj, int i) throws IOException {
		try {
			DeserializerThunk thunk = getDeserializerThunk(clazz);
			return thunk.deserializeObject(clazz, this);
		} catch (SecurityException e) {
			throw new RuntimeException(e);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		}
    }
    
    private static final Class<?>[] EMPTY_ARRAY = new Class[]{};
    
    private DeserializerThunk getDeserializerThunk(Class<? extends Object> clazz) throws SecurityException, NoSuchFieldException, NoSuchMethodException {
    	DeserializerThunk ans = thunks.get(clazz);
    	if (ans == null) {
    		ans = getDeserializerThunkHelper(clazz);
    		thunks.put(clazz, ans);
    		if (Runtime.TRACE_SER) {
    			Runtime.printTraceMessage("Creating deserialization thunk "+ans.getClass()+" for "+clazz);
    		}
    	}
    	return ans;
    }

    private DeserializerThunk getDeserializerThunkHelper(Class<?> clazz) throws SecurityException, NoSuchFieldException, NoSuchMethodException {

        // We need to handle these classes in a special way cause there implementation of serialization/deserialization is
        // not straight forward. Hence we just call into the custom serialization of these classes.
        if ("java.lang.String".equals(clazz.getName())) {
        	return new SpecialCaseDeserializerThunk(null);
        } else if ("x10.rtt.NamedType".equals(clazz.getName())) {
           	return new SpecialCaseDeserializerThunk(null);
        } else if ("x10.rtt.NamedStructType".equals(clazz.getName())) {
            return new SpecialCaseDeserializerThunk(null);  
        } else if ("x10.rtt.RuntimeType".equals(clazz.getName())) {
           	return new SpecialCaseDeserializerThunk(null);	
        } else if ("x10.core.IndexedMemoryChunk".equals(clazz.getName())) {
           	return new SpecialCaseDeserializerThunk(null);
        } else if ("x10.core.IndexedMemoryChunk$$Closure$0".equals(clazz.getName())) {
           	return new SpecialCaseDeserializerThunk(null);
        } else if ("x10.core.IndexedMemoryChunk$$Closure$1".equals(clazz.getName())) {
           	return new SpecialCaseDeserializerThunk(null);
        } else if (GlobalRef.class.getName().equals(clazz.getName())) {
           	return new SpecialCaseDeserializerThunk(null);
            // TODO CHECKED_THROWABLE stop converting Java exception types that are mapped (i.e. not wrapped) to x10 exception types. 
//        } else if (X10Throwable.class.getName().equals(clazz.getName())) {
//           	return new SpecialCaseDeserializerThunk(null);
        } else if ("java.lang.Class".equals(clazz.getName())) {
           	return new SpecialCaseDeserializerThunk(null);
        } else if ("java.lang.Object".equals(clazz.getName())) {
            return new SpecialCaseDeserializerThunk(null);
        }

        Class<?>[] interfaces = clazz.getInterfaces();
        boolean isCustomSerializable = false;
        boolean isHadoopSerializable = false;
        for (Class<?> aInterface : interfaces) {
            if ("x10.io.CustomSerialization".equals(aInterface.getName())) {
                isCustomSerializable = true;
                break;
            }
        }

        if (Runtime.implementsHadoopWritable(clazz)) {
        	isHadoopSerializable = true;
        }
        
        if (isCustomSerializable && isHadoopSerializable) {
        	throw new RuntimeException("deserializer: " + clazz + " implements both x10.io.CustomSerialization and org.apache.hadoop.io.Writable.");
        }
        
        if (isCustomSerializable) {
        	return new CustomDeserializerThunk(clazz);
        }
        
        if (isHadoopSerializable) {
        	return new HadoopDeserializerThunk(clazz);
        }

        Class<?> superclass = clazz.getSuperclass();
        DeserializerThunk superclassThunk = null;
        if (!("java.lang.Object".equals(superclass.getName()) || "x10.core.Ref".equals(superclass.getName()) || "x10.core.Struct".equals(superclass.getName()))) {
        	superclassThunk = getDeserializerThunkHelper(superclass);
        }

        return new FieldBasedDeserializerThunk(clazz, superclassThunk);
    }

    private <T> void readPrimitiveUsingReflection(Field field, T obj) throws IOException, IllegalAccessException {
        Class<?> type = field.getType();
        if ("int".equals(type.getName())) {
            field.setInt(obj, readInt());
        } else if ("double".equals(type.getName())) {
            field.setDouble(obj, readDouble());
        } else if ("float".equals(type.getName())) {
            field.setFloat(obj, readFloat());
        } else if ("boolean".equals(type.getName())) {
            field.setBoolean(obj, readBoolean());
        } else if ("byte".equals(type.getName())) {
            field.setByte(obj, readByte());
        } else if ("short".equals(type.getName())) {
            field.setShort(obj, readShort());
        } else if ("long".equals(type.getName())) {
            field.setLong(obj, readLong());
        } else if ("char".equals(type.getName())) {
            field.setChar(obj, readChar());
        }
    }

    public Object readArrayUsingReflection(Class<?> componentType) throws IOException {
        short serializationID = readShort();
        if (serializationID == DeserializationDispatcher.NULL_ID) {
            if (Runtime.TRACE_SER) {
                Runtime.printTraceMessage("Deserialized a null array");
            }
            return null;
        }
        if (componentType.isPrimitive()) {
            if ("int".equals(componentType.getName())) {
                return readIntArray();
            } else if ("double".equals(componentType.getName())) {
                return readDoubleArray();
            } else if ("float".equals(componentType.getName())) {
                return readFloatArray();
            } else if ("boolean".equals(componentType.getName())) {
                return readBooleanArray();
            } else if ("byte".equals(componentType.getName())) {
                return readByteArray();
            } else if ("short".equals(componentType.getName())) {
                return readShortArray();
            } else if ("long".equals(componentType.getName())) {
                return readLongArray();
            } else if ("char".equals(componentType.getName())) {
                return readCharArray();
            }
        } else if ("java.lang.String".equals(componentType.getName())) {
            return readStringArray();
        } else if (componentType.isArray()) {
            int length = readInt();
            Object o = Array.newInstance(componentType, length);
            for (int i = 0; i < length; i++) {
                 Array.set(o, i, readArrayUsingReflection(componentType));
            }
            return o;
        } else {
            int length = readInt();
            Object o = Array.newInstance(componentType, length);
            for (int i = 0; i < length; i++) {
                Array.set(o, i, readRefUsingReflection());
            }
            return o;
        }
        return null;
    }

    private String readStringUsingReflection() throws IOException {
           return readString();
    }

    public static Unsafe getUnsafe() {
        Unsafe unsafe = null;
        try {
            Class<Unsafe> uc = Unsafe.class;
            Field[] fields = uc.getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                if (fields[i].getName().equals("theUnsafe")) {
                    fields[i].setAccessible(true);
                    unsafe = (Unsafe) fields[i].get(uc);
                    break;
                }
            }
        } catch (Exception ignore) {
        }
        return unsafe;
    }

    // Read the object using java serialization. This is used by IMC to read primitive arrays
    public Object readObject() throws IOException {
        ObjectInputStream ois = new ObjectInputStream(this.in);
        try {
            return ois.readObject();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    
    private abstract static class DeserializerThunk {
    	protected final DeserializerThunk superclassThunk;
    	
    	DeserializerThunk(DeserializerThunk st) {
    		superclassThunk = st;
		}
    	
    	@SuppressWarnings("unchecked")
		<T> T deserializeObject(Class<?> clazz, X10JavaDeserializer jds) throws IOException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, InstantiationException {
            T obj = null;

            // If the class is java.lang.Class we cannot create an instance in the following manner so we just skip it
            if (!"java.lang.Class".equals(clazz.getName())) {
                try {
					obj = (T)unsafe.allocateInstance(clazz);
				} catch (InstantiationException e) {
					throw new RuntimeException(e);
				}
            }
            int i = jds.record_reference(obj);
            return deserializeObject(clazz, obj, i, jds);
    	}
    	
    	<T> T deserializeObject(Class<?> clazz, T obj, int i, X10JavaDeserializer jds) throws IOException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, InstantiationException {
    		if (superclassThunk != null) {
    			obj = superclassThunk.deserializeObject(clazz, obj, i, jds);
    		}
    		return deserializeBody(clazz, obj, i, jds);
    	}
    	
    	protected abstract <T> T deserializeBody(Class<?> clazz, T obj, int i, X10JavaDeserializer jds) throws IOException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, InstantiationException;
    }
    
    private static class FieldBasedDeserializerThunk extends DeserializerThunk {
    	protected final Field[] fields;
    	
    	FieldBasedDeserializerThunk(Class<? extends Object> clazz, DeserializerThunk st) {
    		super(st);

                // XTENLANG-2982,2983 transient fields may be initialized with readObject method. 
                Method readObjectMethod = null;
                try {
                    readObjectMethod = clazz.getDeclaredMethod("readObject", java.io.ObjectInputStream.class);
                } catch (Exception e) {}

    		// Sort the fields to get JVM-independent ordering.
    		Set<Field> flds = new TreeSet<Field>(new FieldComparator());
    		Field[] declaredFields = clazz.getDeclaredFields();
    		for (Field field : declaredFields) {
    			int modifiers = field.getModifiers();
    			if (Modifier.isStatic(modifiers) || (Modifier.isTransient(modifiers) && readObjectMethod == null)) {
    				continue;
    			}
    			field.setAccessible(true);
    			flds.add(field);
    		}
     		fields = flds.toArray(new Field[flds.size()]);
     	}

		protected <T> T deserializeBody(Class<?> clazz, T obj, int i, X10JavaDeserializer jds) throws IOException, IllegalAccessException {		
	        for (Field field : fields) {
	            Class<?> type = field.getType();
	            if (type.isPrimitive()) {
	                jds.readPrimitiveUsingReflection(field, obj);
	            } else if (type.isArray()) {
	                field.set(obj, jds.readArrayUsingReflection(type.getComponentType()));
	            } else if ("java.lang.String".equals(type.getName())) {
	                field.set(obj, jds.readStringUsingReflection());
	            } else {
	                Object value = jds.readRefUsingReflection();
	                field.set(obj, value);
	            }
	        }
			return obj;
		}
    }

    private static class HadoopDeserializerThunk extends DeserializerThunk {
    	protected final Constructor<?> constructor;
    	protected final Method readMethod;

    	HadoopDeserializerThunk(Class<? extends Object> clazz) throws SecurityException, NoSuchMethodException {
    		super(null);
        	constructor = clazz.getDeclaredConstructor(EMPTY_ARRAY);
            constructor.setAccessible(true);

    		readMethod = clazz.getMethod("readFields", java.io.DataInput.class);
    		readMethod.setAccessible(true);
    	}

       	@SuppressWarnings("unchecked")
       	@Override
		<T> T deserializeObject(Class<?> clazz, X10JavaDeserializer jds) throws IOException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, InstantiationException {
           	if (Runtime.TRACE_SER) {
                Runtime.printTraceMessage("Calling hadoop deserializer with object of type " + clazz);
            }
        	
       		// Hadoop assumes that the default constructor will be used to create the instance.
       		// The default constructor will execute field initialization expressions.  
       		// So we have to mimic that behavior here.
            T obj = (T)constructor.newInstance();
            int i = jds.record_reference(obj);

            return deserializeObject(clazz, obj, i, jds);
    	}
    	
		@Override
		protected <T> T deserializeBody(Class<?> clazz, T obj, int i, X10JavaDeserializer jds) throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
    		readMethod.invoke(obj, jds.in);
    		return obj;
		}
    	
    }
    
    private static class CustomDeserializerThunk extends DeserializerThunk {
    	protected final Field[] fields;
    	protected final Method makeMethod;

    	CustomDeserializerThunk(Class<? extends Object> clazz) throws SecurityException, NoSuchFieldException, NoSuchMethodException {
    		super(null);

    		// Sort the fields to get JVM-independent ordering.
    		// Need to serialize the fields related to RTT's since they
    		// are specific to the Java backend.
    		Set<Field> flds = new TreeSet<Field>(new FieldComparator());
    		TypeVariable<? extends Class<? extends Object>>[] typeParameters = clazz.getTypeParameters();
    		for (TypeVariable<? extends Class<? extends Object>> typeParameter: typeParameters) {
    			Field field = clazz.getDeclaredField(typeParameter.getName());
    			field.setAccessible(true);
    			flds.add(field);
    		}
    		fields = flds.toArray(new Field[flds.size()]);      
    		
            // We can't use the same method name in all classes cause it creates an endless loop cause when super.init is called it calls back to this method
    		makeMethod = clazz.getMethod(clazz.getName().replace(".", "$") + CONSTRUCTOR_METHOD_NAME_FOR_REFLECTION, SerialData.class);
            makeMethod.setAccessible(true);
    	}

		@Override
		protected <T> T deserializeBody(Class<?> clazz, T obj, int i, X10JavaDeserializer jds) throws IOException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
			for (Field field : fields) {
				Object value = jds.readRefUsingReflection();
				field.set(obj, value);
			}

			SerialData serialData = (SerialData) jds.readRefUsingReflection();
            makeMethod.invoke(obj, serialData);
            return obj;
		}
    }
    
    private static class SpecialCaseDeserializerThunk extends DeserializerThunk {

    	SpecialCaseDeserializerThunk(Class <? extends Object> clazz) {
    		super(null);
    	}

    	SpecialCaseDeserializerThunk(Class <? extends Object> clazz, DeserializerThunk st) {
    		super(st);
    	}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		protected <T> T deserializeBody(Class<?> clazz, T obj, int i, X10JavaDeserializer jds) throws IOException {
	        if ("java.lang.String".equals(clazz.getName())) {
	            obj = (T) jds.readStringValue();
	            return obj;
                } else if ("x10.rtt.NamedType".equals(clazz.getName())) {
	            NamedType.$_deserialize_body((NamedType) obj, jds);
	            return obj;
                } else if ("x10.rtt.NamedStructType".equals(clazz.getName())) {
                    NamedStructType.$_deserialize_body((NamedStructType) obj, jds);
                    return obj;
	        } else if ("x10.rtt.RuntimeType".equals(clazz.getName())) {
	            X10JavaSerializable x10JavaSerializable = RuntimeType.$_deserialize_body((RuntimeType) obj, jds);
	            if (obj != x10JavaSerializable) {
	                jds.update_reference(i, x10JavaSerializable);
	                obj = (T) x10JavaSerializable;
	            }
	            return obj;
	        } else if ("x10.core.IndexedMemoryChunk".equals(clazz.getName())) {
	            IndexedMemoryChunk imc = (IndexedMemoryChunk) obj;
	            IndexedMemoryChunk._deSerialize_body(imc, jds);
	            return (T) imc;
	        } else if ("x10.core.IndexedMemoryChunk$$Closure$0".equals(clazz.getName())) {
	            return (T) IndexedMemoryChunk.$Closure$0.$_deserialize_body((IndexedMemoryChunk.$Closure$0) obj, jds);
	        } else if ("x10.core.IndexedMemoryChunk$$Closure$1".equals(clazz.getName())) {
	            return (T) IndexedMemoryChunk.$Closure$1.$_deserialize_body((IndexedMemoryChunk.$Closure$1) obj, jds);
	        } else if (GlobalRef.class.getName().equals(clazz.getName())) {
	            return (T) GlobalRef.$_deserialize_body((GlobalRef) obj, jds);
	            // TODO CHECKED_THROWABLE stop converting Java exception types that are mapped (i.e. not wrapped) to x10 exception types. 
//	        } else if (X10Throwable.class.getName().equals(clazz.getName())) {
//	            return (T) X10Throwable.$_deserialize_body((X10Throwable) obj, jds);
	        } else if ("java.lang.Class".equals(clazz.getName())) {
	            String className = jds.readString();
	            try {
	                T t = (T) Class.forName(className);
	                jds.update_reference(i, t);
	                return t;
	            } catch (ClassNotFoundException e) {
	                throw new RuntimeException(e);
	            }
	        } else if ("java.lang.Object".equals(clazz.getName())) {
	            return obj;
	        }
	        throw new RuntimeException("Unhandled type in special case thunk: "+obj.getClass());
		}
    }

}

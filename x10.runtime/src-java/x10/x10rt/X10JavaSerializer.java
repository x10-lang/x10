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

import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.TypeVariable;
import java.util.IdentityHashMap;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import x10.core.GlobalRef;
import x10.core.IndexedMemoryChunk;
import x10.core.X10Throwable;
import x10.io.CustomSerialization;
import x10.io.SerialData;
import x10.runtime.impl.java.Runtime;

public class X10JavaSerializer {
	
	private static ConcurrentHashMap<Class<?>, SerializerThunk> thunks = new ConcurrentHashMap<Class<?>, X10JavaSerializer.SerializerThunk>(50);

    // When a Object is serialized record its position
    // N.B. set initial size to 19 as a workaround for frequent hash collision with J9
    IdentityHashMap<Object, Integer> objectMap = new IdentityHashMap<Object, Integer>(19);
    DataOutputStream out;
    int counter = 0;

    public X10JavaSerializer(DataOutputStream out) {
        this.out = out;
    }

    public DataOutput getOutForHadoop() {
    	return out;
    }
    
    public void write(X10JavaSerializable obj) throws IOException {
        if (obj == null) {
            writeNull();
            return;
        }

        if (obj.getClass().toString().equals("java.lang.Object")) {
            return;
        }
        Integer pos = previous_position(obj, true);
        if (pos !=null) {
            return;
        }
        short i = obj.$_get_serialization_id();
        if (Runtime.TRACE_SER) {
            Runtime.printTraceMessage("Serializing [**] a " + Runtime.ANSI_CYAN + "serialization_id_t" + Runtime.ANSI_RESET + ": " + i);
        }
        out.writeShort(i);
        if (Runtime.TRACE_SER) {
            Runtime.printTraceMessage("Serializing a " + Runtime.ANSI_CYAN + Runtime.ANSI_BOLD + obj.getClass().getName() + Runtime.ANSI_RESET);
        }
        obj.$_serialize(this);
    }

    private void writeNull() throws IOException {
        write(DeserializationDispatcher.NULL_ID);
        if (Runtime.TRACE_SER) {
            Runtime.printTraceMessage("Serializing a null reference");
        }
    }

    public void write(CustomSerialization obj) throws IOException {
        write((X10JavaSerializable) obj);
    }

    public void write(X10JavaSerializable obj[]) throws IOException {

        write(obj.length);
        for (X10JavaSerializable o : obj) {
            write(o);
        }
    }
    
    public void write(Object obj[]) throws IOException {
        write(obj.length);
        for (Object o : obj) {
            write(o);
        }
    }

    public void write(int i) throws IOException {
        if (Runtime.TRACE_SER) {
            Runtime.printTraceMessage("Serializing [****] a " + Runtime.ANSI_CYAN + " int" + Runtime.ANSI_RESET + ": " + i);
        }
        out.writeInt(i);
    }

    public void write(Integer p) throws IOException {
        if (p == null) {
            writeNull();
            return;
        }
        Integer pos = previous_position(p, true);
        if (pos != null) {
            return;
        }
        if (Runtime.TRACE_SER) {
            Runtime.printTraceMessage("Serializing a " + Runtime.ANSI_CYAN + "integer" + Runtime.ANSI_RESET + ": " + p);
        }
        out.writeShort(DeserializationDispatcher.INTEGER_ID);
        out.writeInt(p.intValue());
    }

    public void write(int[] i) throws IOException {
        out.writeInt(i.length);
        for (int j : i) {
            out.writeInt(j);
        }
    }

    public void write(boolean b) throws IOException {
        if (Runtime.TRACE_SER) {
            Runtime.printTraceMessage("Serializing [*] a " + Runtime.ANSI_CYAN + "boolean" + Runtime.ANSI_RESET + ": " + b);
        }
        out.writeBoolean(b);
    }

    public void write(Boolean p) throws IOException {
        if (p == null) {
            writeNull();
            return;
        }
        Integer pos = previous_position(p, true);
        if (pos != null) {
            return;
        }
        if (Runtime.TRACE_SER) {
            Runtime.printTraceMessage("Serializing a " + Runtime.ANSI_CYAN + "Boolean" + Runtime.ANSI_RESET + ": " + p);
        }
        out.writeShort(DeserializationDispatcher.BOOLEAN_ID);
        out.writeBoolean(p.booleanValue());
    }

    public void write(boolean v[]) throws IOException {
        out.writeInt(v.length);
        for (boolean b : v) {
            out.writeBoolean(b);
        }
    }

    public void write(char c) throws IOException {
        if (Runtime.TRACE_SER) {
            Runtime.printTraceMessage("Serializing [**] a " + Runtime.ANSI_CYAN + "char" + Runtime.ANSI_RESET + ": " + c);
        }
        out.writeChar(c);
    }

    public void write(Character p) throws IOException {
        if (p == null) {
            writeNull();
            return;
        }
        Integer pos = previous_position(p, true);
        if (pos != null) {
            return;
        }
        if (Runtime.TRACE_SER) {
            Runtime.printTraceMessage("Serializing a " + Runtime.ANSI_CYAN + "Character" + Runtime.ANSI_RESET + ": " + p);
        }
        out.writeShort(DeserializationDispatcher.CHARACTER_ID);
        out.writeChar(p.charValue());
    }

    public void write(char[] v) throws IOException {
        out.writeInt(v.length);
        for (char c : v) {
            out.writeChar(c);
        }
    }

    public void write(byte b) throws IOException {
        if (Runtime.TRACE_SER) {
            Runtime.printTraceMessage("Serializing [****] a " + Runtime.ANSI_CYAN + "byte" + Runtime.ANSI_RESET + ": " + b);
        }
        out.writeByte(b);
    }

    public void write(Byte p) throws IOException {
        if (p == null) {
            writeNull();
            return;
        }
        Integer pos = previous_position(p, true);
        if (pos != null) {
            return;
        }
        if (Runtime.TRACE_SER) {
            Runtime.printTraceMessage("Serializing a " + Runtime.ANSI_CYAN + "Byte" + Runtime.ANSI_RESET + ": " + p);
        }
        out.writeShort(DeserializationDispatcher.BYTE_ID);
        out.writeByte(p.byteValue());
    }

    public void write(byte[] b) throws IOException {
        out.writeInt(b.length);
        _write(b);
    }

    public void _write(byte[] b) throws IOException {
        out.write(b);
    }

    public void write(short s) throws IOException {
        if (Runtime.TRACE_SER) {
            Runtime.printTraceMessage("Serializing [**] a " + Runtime.ANSI_CYAN + "short" + Runtime.ANSI_RESET + ": " + s);
        }
        out.writeShort(s);
    }

    public void write(Short p) throws IOException {
        if (p == null) {
            writeNull();
            return;
        }
        Integer pos = previous_position(p, true);
        if (pos != null) {
            return;
        }
        if (Runtime.TRACE_SER) {
            Runtime.printTraceMessage("Serializing a " + Runtime.ANSI_CYAN + "Short" + Runtime.ANSI_RESET + ": " + p);
        }
        out.writeShort(DeserializationDispatcher.SHORT_ID);
        out.writeShort(p.shortValue());
    }

    public void write(short[] v) throws IOException {
        out.writeInt(v.length);
        for (short s : v) {
            out.writeShort(s);
        }
    }

    public void write(long l) throws IOException {
        if (Runtime.TRACE_SER) {
            Runtime.printTraceMessage("Serializing [********] a " + Runtime.ANSI_CYAN + "long" + Runtime.ANSI_RESET + ": " + l);
        }
        out.writeLong(l);
    }

    public void write(Long p) throws IOException {
        if (p == null) {
            writeNull();
            return;
        }
        Integer pos = previous_position(p, true);
        if (pos != null) {
            return;
        }
        if (Runtime.TRACE_SER) {
            Runtime.printTraceMessage("Serializing a " + Runtime.ANSI_CYAN + "Long" + Runtime.ANSI_RESET + ": " + p);
        }
        out.writeShort(DeserializationDispatcher.LONG_ID);
        out.writeLong(p.longValue());
    }

    public void write(long[] v) throws IOException {
        out.writeInt(v.length);
        for (long l : v) {
            out.writeLong(l);
        }
    }

    public void write(double d) throws IOException {
        if (Runtime.TRACE_SER) {
            Runtime.printTraceMessage("Serializing [********] a " + Runtime.ANSI_CYAN + "double" + Runtime.ANSI_RESET + ": " + d);
        }
        out.writeDouble(d);
    }

    public void write(Double p) throws IOException {
        if (p == null) {
            writeNull();
            return;
        }
        Integer pos = previous_position(p, true);
        if (pos != null) {
            return;
        }
        if (Runtime.TRACE_SER) {
            Runtime.printTraceMessage("Serializing a " + Runtime.ANSI_CYAN + "Double" + Runtime.ANSI_RESET + ": " + p);
        }
        out.writeShort(DeserializationDispatcher.DOUBLE_ID);
        out.writeDouble(p.doubleValue());
    }

    public void write(double[] v) throws IOException {
        out.writeInt(v.length);
        for (double d : v) {
            out.writeDouble(d);
        }
    }

    public void write(float f) throws IOException {
        if (Runtime.TRACE_SER) {           
            Runtime.printTraceMessage("Serializing [********] a " + Runtime.ANSI_CYAN + "float" + Runtime.ANSI_RESET + ": " + f);
        }
        out.writeFloat(f);
    }

    public void write(Float p) throws IOException {
        if (p == null) {
            writeNull();
            return;
        }
        Integer pos = previous_position(p, true);
        if (pos != null) {
            return;
        }
        if (Runtime.TRACE_SER) {
            Runtime.printTraceMessage("Serializing a " + Runtime.ANSI_CYAN + "Float" + Runtime.ANSI_RESET + ": " + p);
        }
        out.writeShort(DeserializationDispatcher.FLOAT_ID);
        out.writeFloat(p.floatValue());
    }

    public void write(float[] v) throws IOException {
        out.writeInt(v.length);
        for (float f : v) {
            out.writeFloat(f);
        }
    }

    public void write(Object v) throws IOException {
        writeObjectUsingReflection(v);
    }

    public void write(String str) throws IOException {
        if (str == null) {
            writeNull();
            return;
        }

        if (Runtime.TRACE_SER) {
            Runtime.printTraceMessage("Serializing a " + Runtime.ANSI_CYAN + "String" + Runtime.ANSI_RESET + ": " + str);
        }

        Integer pos = previous_position(str, true);
        if (pos != null) {
            return;
        }
        write(DeserializationDispatcher.STRING_ID);
        writeStringValue(str);
    }

    private void writeStringValue(String str) throws IOException {
        write(str.length());
        out.write(str.getBytes());
    }

    public void write(String[] v) throws IOException {
        out.writeInt(v.length);
        for (String str : v) {
            write(str);
        }
    }

    public void recordReference(Object obj) throws IOException {
        previous_position(obj, false);
    }

    private Integer previous_position(Object obj, boolean writeRef) throws IOException {
        Integer pos = objectMap.get(obj);
        if (pos != null) {
            if (Runtime.TRACE_SER) {
                Runtime.printTraceMessage("\t\tFound repeated reference of type " + Runtime.ANSI_CYAN + Runtime.ANSI_BOLD + obj.getClass().getName() + Runtime.ANSI_RESET+ " at " + pos + " (absolute) in map");
            }
            // We have serialized this object before hence no need to do it again
            if (writeRef) {
                write(DeserializationDispatcher.refValue);
                out.writeInt(pos);
            }
        } else {
            objectMap.put(obj, counter);
            if (Runtime.TRACE_SER) {
                Runtime.printTraceMessage("\t\tRecorded new reference of type " + Runtime.ANSI_CYAN + Runtime.ANSI_BOLD + obj.getClass().getName() + Runtime.ANSI_RESET + " at " + counter + " (absolute) in map");
            }
            counter++;
        }
        return pos;
    }

    public <T> void writeObjectUsingReflection(T body) throws IOException {
    	if (body == null) {
    		writeNull();
    		return;
    	}
    	Integer pos = previous_position(body, true);
    	if (pos != null) {
    		return;
    	}
    	Class<? extends Object> bodyClass = body.getClass();
    	String className = bodyClass.getName();

    	if (className.startsWith("x10.rtt.") &&
    			("x10.rtt.FloatType".equals(bodyClass.getName()) 
    					|| "x10.rtt.IntType".equals(bodyClass.getName())
    					|| "x10.rtt.DoubleType".equals(bodyClass.getName())
    					|| "x10.rtt.LongType".equals(bodyClass.getName())
    					|| "x10.rtt.BooleanType".equals(bodyClass.getName())
    					|| "x10.rtt.StringType".equals(bodyClass.getName())
    					|| "x10.rtt.CharType".equals(bodyClass.getName())
    					|| "x10.rtt.ByteType".equals(bodyClass.getName())
    					|| "x10.rtt.ShortType".equals(bodyClass.getName())
    					|| "x10.rtt.ObjectType".equals(bodyClass.getName())
    					|| "x10.rtt.UByteType".equals(bodyClass.getName())
    					|| "x10.rtt.UIntType".equals(bodyClass.getName())
    					|| "x10.rtt.ULongType".equals(bodyClass.getName())
    					|| "x10.rtt.UShortType".equals(bodyClass.getName()))) {
    		// These classes don't implement the serialization/deserialization routines, hence we serialize the superclass
    		bodyClass = bodyClass.getSuperclass();
    	}

    	try {
    		SerializerThunk st = getSerializerThunk(bodyClass);
    		writeClassID(bodyClass.getName());
    		st.serializeObj(body, this);
    	} catch (SecurityException e) {
    		e.printStackTrace();
    		throw new RuntimeException(e);
    	} catch (IllegalArgumentException e) {
    		e.printStackTrace();
    		throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
    		e.printStackTrace();
    		throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
    		e.printStackTrace();
    		throw new RuntimeException(e);
		} catch (NoSuchFieldException e) {
    		e.printStackTrace();
    		throw new RuntimeException(e);
		} catch (NoSuchMethodException e) {
    		e.printStackTrace();
    		throw new RuntimeException(e);
		}
    }

    // This method is called from generated code when an X10 class has a Java superclass
    public <T> void serializeClassUsingReflection(T obj, Class<T> clazz) throws IOException {
    	try {
    		SerializerThunk st = getSerializerThunk(clazz);
    		writeClassID(clazz.getName());
    		st.serializeObj(obj, this);
    	} catch (SecurityException e) {
    		e.printStackTrace();
    		throw new RuntimeException(e);
    	} catch (IllegalArgumentException e) {
    		e.printStackTrace();
    		throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
    		e.printStackTrace();
    		throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
    		e.printStackTrace();
    		throw new RuntimeException(e);
		} catch (NoSuchFieldException e) {
    		e.printStackTrace();
    		throw new RuntimeException(e);
		} catch (NoSuchMethodException e) {
    		e.printStackTrace();
    		throw new RuntimeException(e);
		}    	
    }
    
    public void writeClassID(String className) throws IOException {
    	short id = DeserializationDispatcher.getIDForClassName(className);
    	if (id < 0) {
    		write(DeserializationDispatcher.javaClassID);
    		writeStringValue(className);
    	} else {
    		write(id);
    	}
    }

    private SerializerThunk getSerializerThunk(Class<? extends Object> clazz) throws SecurityException, NoSuchFieldException, NoSuchMethodException {
    	SerializerThunk ans = thunks.get(clazz);
    	if (ans == null) {
    		ans = getSerializerThunkHelper(clazz);
    		thunks.put(clazz, ans);
    		if (Runtime.TRACE_SER) {
    			Runtime.printTraceMessage("Creating serialization thunk "+ans.getClass()+" for "+clazz);
    		}
    	}
    	return ans;
    }
    	
    private SerializerThunk getSerializerThunkHelper(Class<? extends Object> clazz) throws SecurityException, NoSuchFieldException, NoSuchMethodException {
    	
    	// We need to handle these classes in a special way because their 
    	// implementation of serialization/deserialization is not straight forward.
    	if ("java.lang.String".equals(clazz.getName())) {
    		return new SpecialCaseSerializerThunk(clazz);
    	} else if ("x10.rtt.NamedType".equals(clazz.getName())) {
    		SerializerThunk superThunk = getSerializerThunk(clazz.getSuperclass());
    		return new SpecialCaseSerializerThunk(clazz, superThunk);
    	} else if ("x10.rtt.RuntimeType".equals(clazz.getName())) {
    		return new SpecialCaseSerializerThunk(clazz);
    	} else if ("x10.core.IndexedMemoryChunk".equals(clazz.getName())) {
    		return new SpecialCaseSerializerThunk(clazz);
    	} else if ("x10.core.IndexedMemoryChunk$$Closure$0".equals(clazz.getName())) {
    		return new SpecialCaseSerializerThunk(clazz);
    	} else if ("x10.core.IndexedMemoryChunk$$Closure$1".equals(clazz.getName())) {
    		return new SpecialCaseSerializerThunk(clazz);
    	} else if (GlobalRef.class.getName().equals(clazz.getName())) {
    		return new SpecialCaseSerializerThunk(clazz);
    	} else if (X10Throwable.class.getName().equals(clazz.getName())) {
    		return new SpecialCaseSerializerThunk(clazz);
    	} else if ("java.lang.Class".equals(clazz.getName())) {
    		return new SpecialCaseSerializerThunk(clazz);
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

    	if(isCustomSerializable && isHadoopSerializable) {
    		throw new RuntimeException("serializer: " + clazz + " implements both x10.io.CustomSerialization and org.apache.hadoop.io.Writable.");
    	}

    	if (isCustomSerializable) {
    		return new CustomSerializerThunk(clazz);
    	}
    	
    	if (isHadoopSerializable) {
    		return new HadoopSerializerThunk(clazz);
    	}
    	
    	Class<?> superclass = clazz.getSuperclass();
    	SerializerThunk superThunk = null;
    	if (!("java.lang.Object".equals(superclass.getName()) || "x10.core.Ref".equals(superclass.getName()) || "x10.core.Struct".equals(superclass.getName()))) {
    		superThunk = getSerializerThunk(clazz.getSuperclass());
    	}

    	return new FieldBasedSerializerThunk(clazz, superThunk);
    }

    public void writeArrayUsingReflection(Object obj) throws IOException {

    	Class<?> componentType = obj.getClass().getComponentType();
    	int length = Array.getLength(obj);
    	write(length);
    	if (componentType.isPrimitive()) {
    		if ("int".equals(componentType.getName())) {
    			for (int i = 0; i < length; i++) {
    				write(Array.getInt(obj, i));
    			}
    		} else if ("double".equals(componentType.getName())) {
    			for (int i = 0; i < length; i++) {
    				write(Array.getDouble(obj, i));
    			}
    		} else if ("float".equals(componentType.getName())) {
    			for (int i = 0; i < length; i++) {
    				write(Array.getFloat(obj, i));
    			}
    		} else if ("boolean".equals(componentType.getName())) {
    			for (int i = 0; i < length; i++) {
    				write(Array.getBoolean(obj, i));
    			}
    		} else if ("byte".equals(componentType.getName())) {
    			for (int i = 0; i < length; i++) {
    				write(Array.getByte(obj, i));
    			}
    		} else if ("short".equals(componentType.getName())) {
    			for (int i = 0; i < length; i++) {
    				write(Array.getShort(obj, i));
    			}
    		} else if ("long".equals(componentType.getName())) {
    			for (int i = 0; i < length; i++) {
    				write(Array.getLong(obj, i));
    			}
    		} else if ("char".equals(componentType.getName())) {
    			for (int i = 0; i < length; i++) {
    				write(Array.getChar(obj, i));
    			}
    		}
    	} else if ("java.lang.String".equals(componentType.getName())) {
    		for (int i = 0; i < length; i++) {
    			String str = (String) Array.get(obj, i);
    			write(str);
    		}
    	} else if (componentType.isArray()) {
    		for (int i = 0; i < length; i++) {
    			Object o = Array.get(obj, i);
    			writeArrayUsingReflection(o);
    		}
    	} else {
    		for (int i = 0; i < length; i++) {
    			Object o = Array.get(obj, i);
    			writeObjectUsingReflection(o);
    		}
    	}
    }

    private <T> void writeStringUsingReflection(Field field, T obj) throws IllegalAccessException, IOException {
        String str = (String) field.get(obj);
        write(str);
    }

    private <T> void writePrimitiveUsingReflection(Field field, T obj) throws IllegalAccessException, IOException {
        Class<?> type = field.getType();
        if ("int".equals(type.getName())) {
            write(field.getInt(obj));
        } else if ("double".equals(type.getName())) {
            write(field.getDouble(obj));
        } else if ("float".equals(type.getName())) {
            write(field.getFloat(obj));
        } else if ("boolean".equals(type.getName())) {
            write(field.getBoolean(obj));
        } else if ("byte".equals(type.getName())) {
            write(field.getByte(obj));
        } else if ("short".equals(type.getName())) {
            write(field.getShort(obj));
        } else if ("long".equals(type.getName())) {
            write(field.getLong(obj));
        } else if ("char".equals(type.getName())) {
            write(field.getChar(obj));
        }
    }

    // Write the object using java serialization. This is used by IMC to write primitive arrays
    public void writeObject(Object obj) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(this.out);
        oos.writeObject(obj);
    }
    
    private static abstract class SerializerThunk {
    	protected final SerializerThunk superThunk;
    	
    	SerializerThunk(SerializerThunk st) {
    		superThunk = st;
    	}
    	
    	<T> void serializeObj(T obj, X10JavaSerializer xjs) throws IllegalAccessException, IOException, IllegalArgumentException, InvocationTargetException, SecurityException, NoSuchFieldException {
    		if (superThunk != null) {
    			superThunk.serializeObj(obj, xjs);
    		}
    		serializeBody(obj, xjs);
    	}
    	
    	abstract <T> void serializeBody(T obj, X10JavaSerializer xjs) throws IllegalAccessException, IOException, IllegalArgumentException, InvocationTargetException, SecurityException, NoSuchFieldException;
    }
    
    private static class FieldBasedSerializerThunk extends SerializerThunk {
    	protected final Field[] fields;
    	
    	FieldBasedSerializerThunk(Class<? extends Object> clazz, SerializerThunk st) {
    		super(st);

    		// Sort the fields to get JVM-independent ordering.
    		Set<Field> flds = new TreeSet<Field>(new FieldComparator());
    		Field[] declaredFields = clazz.getDeclaredFields();
    		for (Field field : declaredFields) {
    			if (field.isSynthetic())
    				continue;
    			int modifiers = field.getModifiers();
    			if (Modifier.isStatic(modifiers) || Modifier.isTransient(modifiers)) {
    				continue;
    			}
    			field.setAccessible(true);
    			flds.add(field);
    		}
     		fields = flds.toArray(new Field[flds.size()]);
     	}
    	
    	<T> void serializeBody(T obj, X10JavaSerializer xjs) throws IllegalAccessException, IOException {
    		for (int i=0; i<fields.length; i++) {
    			Field field = fields[i];
    			Class<?> type = field.getType();
                if (type.isPrimitive()) {
                    xjs.writePrimitiveUsingReflection(field, obj);
                } else if (type.isArray()) {
                    xjs.writeArrayUsingReflection(field.get(obj));
                } else if ("java.lang.String".equals(type.getName())) {
                    xjs.writeStringUsingReflection(field, obj);
                } else {
                    xjs.writeObjectUsingReflection(field.get(obj));
                }
    		}
    	}
    }
    	
    private static class HadoopSerializerThunk extends SerializerThunk {
    	protected final Method writeMethod;

    	HadoopSerializerThunk(Class<? extends Object> clazz) throws SecurityException, NoSuchMethodException {
    		super(null);
    		writeMethod = clazz.getMethod("write", java.io.DataOutput.class);
    		writeMethod.setAccessible(true);
    	}

    	<T> void serializeBody(T obj, X10JavaSerializer xjs) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
    		if (Runtime.TRACE_SER) {
                Runtime.printTraceMessage("\tInvoking "+writeMethod);
    		}
    		writeMethod.invoke(obj, xjs.out);
    	}
    }

    private static class CustomSerializerThunk extends SerializerThunk {
    	protected final Field[] fields;

    	CustomSerializerThunk(Class<? extends Object> clazz) throws SecurityException, NoSuchFieldException {
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
    	}

    	<T> void serializeBody(T obj, X10JavaSerializer xjs) throws IllegalArgumentException, IOException, IllegalAccessException {
    		for (Field field: fields) {
    			xjs.writeObjectUsingReflection(field.get(obj));
    		}
    		CustomSerialization cs = (CustomSerialization)obj;
    		SerialData serialData = cs.serialize();
    		xjs.writeObjectUsingReflection(serialData);
    	}
    }
    
    private static class SpecialCaseSerializerThunk extends SerializerThunk {

    	SpecialCaseSerializerThunk(Class <? extends Object> clazz) {
    		super(null);
    	}

    	SpecialCaseSerializerThunk(Class <? extends Object> clazz, SerializerThunk st) {
    		super(st);
    	}

    	@SuppressWarnings("rawtypes")
		<T> void serializeBody(T obj, X10JavaSerializer xjs) throws IOException, SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
    		Class<? extends Object> clazz = obj.getClass();
    		if ("java.lang.String".equals(clazz.getName())) {
    			xjs.writeStringValue((String) obj);
    		} else if ("x10.rtt.NamedType".equals(clazz.getName())) {
    			Field typeNameField = clazz.getDeclaredField("typeName");
    			String typeName = (String) typeNameField.get(obj);
    			xjs.writeClassID(typeName);
    		} else if ("x10.rtt.RuntimeType".equals(clazz.getName())) {
    			Field implField = clazz.getDeclaredField("impl");
    			Class<?> impl = (Class<?>) implField.get(obj);
    			xjs.writeClassID(impl.getName());
    		} else if ("x10.core.IndexedMemoryChunk".equals(clazz.getName())) {
    			((IndexedMemoryChunk) obj).$_serialize(xjs);
    		} else if ("x10.core.IndexedMemoryChunk$$Closure$0".equals(clazz.getName())) {
    			((IndexedMemoryChunk.$Closure$0) obj).$_serialize(xjs);
    		} else if ("x10.core.IndexedMemoryChunk$$Closure$1".equals(clazz.getName())) {
    			((IndexedMemoryChunk.$Closure$1) obj).$_serialize(xjs);
    		} else if (GlobalRef.class.getName().equals(clazz.getName())) {
    			((GlobalRef) obj).$_serialize(xjs);
    		} else if (X10Throwable.class.getName().equals(clazz.getName())) {
    			((X10Throwable) obj).$_serialize(xjs);
    		} else if ("java.lang.Class".equals(clazz.getName())) {
    			xjs.write(((Class)obj).getName());
    		}
    	}
    }
}

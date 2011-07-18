/*******************************************************************************
 * Copyright (c) 2002,2006 IBM Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package x10.sncode;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import x10.util.CollectionFactory;

/**
 * This class formats and writes class data into JVM format.
 */
public final class ConstantPool implements SnConstants {
    /** Inverted CP. Map from objects to index into the CP. */
    Map<Object, Integer> inverseMap = CollectionFactory.newHashMap(1);

    /** List of new CP entries added since last clearing of the list. */
    List<Object> cpItems = new ArrayList<Object>(1);

    /**
     * Create a blank Sn editor with no members.
     */
    public ConstantPool() {
        inverseMap = CollectionFactory.newHashMap();
        cpItems = new ArrayList<Object>();
        cpItems.add(null);
    }

    /**
     * Copy a constant pool from some ClassReader into this class. This must be
     * done before any entries are allocated in this ClassWriter's constant
     * pool, and it can only be done once. If and only if this is done, it is
     * safe to copy "raw" fields, methods and attributes from the ClassReader
     * into this class, because the constant pool references in those fields,
     * methods and attributes are guaranteed to point to the same constant pool
     * items in this new class.
     */
    public void setRawCP(ConstantPoolParser cp) throws InvalidClassFileException, IllegalArgumentException {
        if (cp == null) {
            throw new IllegalArgumentException();
        }

        cpItems.clear();
        
        // item 0
        cpItems.add(null);

        int nextCPIndex = cp.getItemCount();

        for (int i = 1; i < nextCPIndex; i++) {
            byte t = cp.getItemType(i);
            switch (t) {
            case CONSTANT_Array:
                addCPEntry(cp.getCPArray(i));
                break;
            case CONSTANT_String:
                addCPString(cp.getCPString(i));
                break;
            case CONSTANT_Class:
                addCPClass(cp.getCPClass(i));
                break;
            case CONSTANT_FieldRef:
                addCPFieldRef(cp.getCPRefClass(i), cp.getCPRefName(i), cp.getCPRefType(i));
                break;
            case CONSTANT_MethodRef:
                addCPMethodRef(cp.getCPRefClass(i), cp.getCPRefName(i), cp.getCPRefType(i));
                break;
            case CONSTANT_NameAndType:
                addCPNameAndType(cp.getCPNATName(i), cp.getCPNATType(i));
                break;
            case CONSTANT_Integer:
                addCPInt(cp.getCPInt(i));
                break;
            case CONSTANT_Boolean:
            	addCPBoolean(cp.getCPBoolean(i));
            	break;
            case CONSTANT_Float:
                addCPFloat(cp.getCPFloat(i));
                break;
            case CONSTANT_Long:
                addCPLong(cp.getCPLong(i));
                break;
            case CONSTANT_Double:
                addCPDouble(cp.getCPDouble(i));
                break;
            case CONSTANT_Utf8:
                addCPUtf8(cp.getCPUtf8(i));
                break;
            case CONSTANT_Constraint:
                addCPConstraint(cp.getCPConstraint(i));
                break;
            case CONSTANT_Type:
                addCPType(cp.getCPType(i));
                break;
            case CONSTANT_ByteArray:
                addCPBytes(cp.getCPBytes(i));
                break;
            }
        }
    }

    public int addCPEntry(Object o) {
        if (inverseMap == null) {
            throw new IllegalArgumentException("Cannot add a new constant pool entry during makeBytes() processing!");
        }
        
        if (o == null)
            return 0;

        Integer i = inverseMap.get(o);
        if (i != null) {
            return i.intValue();
        }
        else {
            int index = cpItems.size();
            i = new Integer(index);
            inverseMap.put(o, i);
            cpItems.add(o);
            
            boolean asserts = false;
            assert asserts = true;
            if (asserts)
            	copyInto(new ByteBuffer());
            
            return index;
        }
    }
    
    /**
     * Add a Utf8 string to the constant pool if necessary.
     * 
     * @return the index of a constant pool item with the right value
     */
    public int addCPUtf8(String s) {
        return addCPEntry(s);
    }

    /**
     * Add a Utf8 string to the constant pool if necessary.
     * 
     * @return the index of a constant pool item with the right value
     */
    public int addCPConstraint(Constraint s) {
        return addCPEntry(s);
    }

    /**
     * Add an Integer to the constant pool if necessary.
     * 
     * @return the index of a constant pool item with the right value
     */
    public int addCPBoolean(boolean b) {
        return addCPEntry(new Boolean(b));
    }
    
    /**
     * Add an Integer to the constant pool if necessary.
     * 
     * @return the index of a constant pool item with the right value
     */
    public int addCPInt(int i) {
    	return addCPEntry(new Integer(i));
    }

    /**
     * Add a Float to the constant pool if necessary.
     * 
     * @return the index of a constant pool item with the right value
     */
    public int addCPFloat(float f) {
        return addCPEntry(new Float(f));
    }
    
    /**
     * Add a Float to the constant pool if necessary.
     * 
     * @return the index of a constant pool item with the right value
     */
    public int addCPBytes(byte[] b) {
        return addCPEntry(b);
    }

    /**
     * Add a Long to the constant pool if necessary.
     * 
     * @return the index of a constant pool item with the right value
     */
    public int addCPLong(long l) {
        return addCPEntry(new Long(l));
    }

    /**
     * Add a Double to the constant pool if necessary.
     * 
     * @return the index of a constant pool item with the right value
     */
    public int addCPDouble(double d) {
        return addCPEntry(new Double(d));
    }

    /**
     * Add a String to the constant pool if necessary.
     * 
     * @return the index of a constant pool item with the right value
     */
    public int addCPString(String s) {
        return addCPEntry(new CWString(s));
    }

    /**
     * Add a Class to the constant pool if necessary.
     * 
     * @param s
     *            the class name, in JVM format (e.g., java/lang/Object)
     * @return the index of a constant pool item with the right value
     */
    public int addCPClass(String s) {
        if (s == null) {
            throw new IllegalArgumentException("null s: " + s);
        }
        return addCPEntry(new CWClass(s));
    }

    /**
     * Add a Type to the constant pool if necessary.
     * 
     * @param s
     *            the class name, in JVM format (e.g., java/lang/Object)
     * @return the index of a constant pool item with the right value
     */
    public int addCPType(String s) {
        if (s == null) {
            throw new IllegalArgumentException("null s: " + s);
        }
        try {
            return addCPEntry(Type.parse(s));
        }
        catch (InvalidClassFileException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Add a Type to the constant pool if necessary.
     * 
     * @param s
     *            the class name, in JVM format (e.g., java/lang/Object)
     * @return the index of a constant pool item with the right value
     */
    public int addCPType(Type s) {
        if (s == null) {
            throw new IllegalArgumentException("null s: " + s);
        }
        return addCPEntry(s);
    }

    /**
     * Add a FieldRef to the constant pool if necessary.
     * 
     * @param c
     *            the class name, in JVM format (e.g., java/lang/Object)
     * @param n
     *            the field name
     * @param t
     *            the field type, in JVM format (e.g., I, Z, or
     *            Ljava/lang/Object;)
     * @return the index of a constant pool item with the right value
     */
    public int addCPFieldRef(String c, String n, String t) {
        return addCPEntry(new CWRef(CONSTANT_FieldRef, c, n, t));
    }

    /**
     * Add a MethodRef to the constant pool if necessary.
     * 
     * @param c
     *            the class name, in JVM format (e.g., java/lang/Object)
     * @param n
     *            the method name
     * @param t
     *            the method type, in JVM format (e.g., V(ILjava/lang/Object;) )
     * @return the index of a constant pool item with the right value
     */
    public int addCPMethodRef(String c, String n, String t) {
        return addCPEntry(new CWRef(CONSTANT_MethodRef, c, n, t));
    }

    /**
     * Add a NameAndType to the constant pool if necessary.
     * 
     * @param n
     *            the name
     * @param t
     *            the type, in JVM format
     * @return the index of a constant pool item with the right value
     */
    public int addCPNameAndType(String n, String t) {
        return addCPEntry(new CWNameAndType(n, t));
    }

    void copyInto(ByteBuffer w) {
        int countOffset = w.offset();

        // Reserve a place for the number of entries.
        w.addInt(0);

        // BE CAREFUL: the newCPEntries array grows during this loop.
        for (int i = 1; i < cpItems.size(); i++) {
            Object o = getCPEntry(i);
            
            w.addInt(0xdeadbeef);

            if (o instanceof CWItem) {
                CWItem item = (CWItem) o;
                byte t = item.getType();
                switch (t) {
                case CONSTANT_Class:
                    w.addByte(t);
                    w.addInt(addCPUtf8(((CWClass) item).name));
                    break;
                case CONSTANT_String:
                    w.addByte(t);
                    w.addInt(addCPUtf8(((CWString) item).str));
                    break;
                case CONSTANT_NameAndType: {
                    CWNameAndType nat = (CWNameAndType) item;
                    w.addByte(t);
                    w.addInt(addCPUtf8(nat.name));
                    w.addInt(addCPType(nat.type));
                    break;
                }
                case CONSTANT_MethodRef:
                case CONSTANT_FieldRef: {
                    CWRef ref = (CWRef) item;
                    w.addByte(t);
                    w.addInt(addCPClass(ref.container));
                    w.addInt(addCPNameAndType(ref.name, ref.type));
                    break;
                }
                default:
                    throw new Error("Invalid type: " + t);
                }
            }
            else if (o instanceof String) {
                String s = (String) o;

                w.addByte(CONSTANT_Utf8);
                int lenOffset = w.offset();
                w.addInt(0);
                
                int startOffset = w.offset();

                for (int j = 0; j < s.length(); j++) {
                    char ch = s.charAt(j);
                    if (ch == 0) {
                        w.addUShort(0xC080);
                    }
                    else if (ch < 0x80) {
                        w.addByte((byte) ch);
                    }
                    else if (ch < 0x800) {
                        w.addByte((byte) ((ch >> 6) | 0xC0));
                        w.addByte((byte) ((ch & 0x3F) | 0x80));
                    }
                    else {
                        w.addByte((byte) ((ch >> 12) | 0xE0));
                        w.addByte((byte) (((ch >> 6) & 0x3F) | 0x80));
                        w.addByte((byte) ((ch & 0x3F) | 0x80));
                    }
                }
                int bytes = w.offset() - startOffset;
                w.setInt(lenOffset, bytes);
            }
            else if (o instanceof Boolean) {
            	w.addByte(CONSTANT_Boolean);
            	w.addByte(((Boolean) o).booleanValue() ? 1 : 0);
            }
            else if (o instanceof Integer) {
                w.addByte(CONSTANT_Integer);
                w.addInt(((Integer) o).intValue());
            }
            else if (o instanceof Long) {
                w.addByte(CONSTANT_Long);
                w.addLong(((Long) o).longValue());
            }
            else if (o instanceof Float) {
                w.addByte(CONSTANT_Float);
                w.addFloat(((Float) o).floatValue());
            }
            else if (o instanceof Double) {
                w.addByte(CONSTANT_Double);
                w.addDouble(((Double) o).doubleValue());
            }
            else if (o instanceof byte[]) {
                w.addByte(CONSTANT_ByteArray);
                w.addLength(((byte[]) o).length);
                w.addBytes((byte[]) o);
            }
            else if (o instanceof Object[]) {
                w.addByte(CONSTANT_Array);
                Object[] a = (Object[]) o;
                w.addLength(a.length*CP_INDEX_SIZE);
                for (int k = 0; k < a.length; k++) {
                    w.addCPIndex(addCPEntry(a[k]));
                }
            }
            else if (o instanceof int[]) {
                w.addByte(CONSTANT_Array);
                int[] a = (int[]) o;
                w.addLength(a.length*CP_INDEX_SIZE);
                for (int k = 0; k < a.length; k++) {
                    w.addCPIndex(addCPEntry(a[k]));
                }
            }
            else if (o instanceof long[]) {
                w.addByte(CONSTANT_Array);
                long[] a = (long[]) o;
                w.addLength(a.length*CP_INDEX_SIZE);
                for (int k = 0; k < a.length; k++) {
                    w.addCPIndex(addCPEntry(a[k]));
                }
            }
            else if (o instanceof float[]) {
                w.addByte(CONSTANT_Array);
                float[] a = (float[]) o;
                w.addLength(a.length*CP_INDEX_SIZE);
                for (int k = 0; k < a.length; k++) {
                    w.addCPIndex(addCPEntry(a[k]));
                }
            }
            else if (o instanceof double[]) {
                w.addByte(CONSTANT_Array);
                double[] a = (double[]) o;
                w.addLength(a.length*CP_INDEX_SIZE);
                for (int k = 0; k < a.length; k++) {
                    w.addCPIndex(addCPEntry(a[k]));
                }
            }
            else if (o instanceof Constraint) {
                Constraint c = (Constraint) o;
                w.addByte(CONSTANT_Constraint);
                c.write(w);
            }
            else if (o instanceof Type) {
                Type c = (Type) o;
                w.addByte(CONSTANT_Type);
                c.writeInto(this, w);
            }
            else {
                throw new IllegalArgumentException("bad cp item " + o + ": " + o.getClass().getName() + " at " + i);
            }
        }

        // go back and write the count
        w.setInt(countOffset, cpItems.size());
    }

    /**
     * @return the number of constant pool items (maximum item index plus one)
     */
    public int getItemCount() {
        return cpItems.size();
    }

    public Object getCPEntry(int i) {
        return cpItems.get(i);
    }

    /**
     * @return the type of constant pool item i, or 0 if i is an unused constant
     *         pool item
     */
    public byte getItemType(int i) throws IllegalArgumentException {
        Object o = getCPEntry(i);
        if (o == null)
            return CONSTANT_Null;
        if (o instanceof String)
            return CONSTANT_Utf8;
        if (o instanceof Boolean)
        	return CONSTANT_Boolean;
        if (o instanceof Integer)
            return CONSTANT_Integer;
        if (o instanceof Long)
            return CONSTANT_Long;
        if (o instanceof Float)
            return CONSTANT_Float;
        if (o instanceof Double)
            return CONSTANT_Double;
        if (o instanceof byte[])
            return CONSTANT_ByteArray;
        if (o instanceof Object[])
            return CONSTANT_Array;
        if (o instanceof CWItem)
            return ((CWItem) o).getType();
        throw new IllegalArgumentException("bad constant at " + i);
    }

    /**
     * @return the name of the Class at constant pool item i, in JVM format
     *         (e.g., java/lang/Object)
     */
    public String getCPClass(int i) {
        return ((CWClass) getCPEntry(i)).name;
    }

    /**
     * @return the name of the class part of the FieldRef, MethodRef, or
     *         InterfaceMethodRef at constant pool item i
     */
    public Type getCPType(int i) {
        return (Type) getCPEntry(i);
    }

    /**
     * @return the String at constant pool item i
     */
    public String getCPString(int i) {
        return ((CWString) getCPEntry(i)).str;
    }


    private static boolean isRef(byte b) {
        switch (b) {
        case CONSTANT_MethodRef:
        case CONSTANT_FieldRef:
            return true;
        default:
            return false;
        }
    }

    /**
     * @return the name of the class part of the FieldRef, MethodRef, or
     *         InterfaceMethodRef at constant pool item i
     */
    public String getCPRefClass(int i) {
        return ((CWRef) getCPEntry(i)).container;
    }

    /**
     * @return the name part of the FieldRef, MethodRef, or InterfaceMethodRef
     *         at constant pool item i
     */
    public String getCPRefName(int i) {
        return ((CWRef) getCPEntry(i)).name;
    }

    /**
     * @return the type part of the FieldRef, MethodRef, or InterfaceMethodRef
     *         at constant pool item i, in JVM format (e.g., I, Z, or
     *         Ljava/lang/Object;)
     */
    public String getCPRefType(int i) {
        return ((CWRef) getCPEntry(i)).type;
    }

    /**
     * @return the name part of the NameAndType at constant pool item i
     */
    public String getCPNATName(int i) {
        return ((CWNameAndType) getCPEntry(i)).name;
    }

    /**
     * @return the type part of the NameAndType at constant pool item i, in JVM
     *         format (e.g., I, Z, or Ljava/lang/Object;)
     */
    public String getCPNATType(int i) {
        return ((CWNameAndType) getCPEntry(i)).type;
    }

    /**
     * @return the value of the Integer at constant pool item i
     */
    public int getCPInt(int i) {
        return (Integer) getCPEntry(i);
    }

    /**
     * @return the value of the Float at constant pool item i
     */
    public float getCPFloat(int i) {
        return (Float) getCPEntry(i);
    }

    /**
     * @return the value of the Long at constant pool item i
     */
    public long getCPLong(int i) {
        return (Long) getCPEntry(i);
    }

    /**
     * @return the value of the Double at constant pool item i
     */
    public double getCPDouble(int i) {
        return (Double) getCPEntry(i);
    }

    /**
     * @return the value of the byte array at constant pool item i
     */
    public byte[] getCPBytes(int i) {
        return (byte[]) getCPEntry(i);
    }

    public String getCPUtf8(int i) {
        return (String) getCPEntry(i);
    }
    
    public <T> T[] getCPArray(int i) {
        return (T[]) getCPEntry(i);
    }

    /**
     * @return the value of the Constraint at constant pool item i
     */
    public Constraint getCPConstraint(int i) {
        return (Constraint) getCPEntry(i);
    }

    static abstract class CWItem {
        abstract byte getType();
    }

    static class CWString extends CWItem {
        final String str;

        CWString(String s) {
            this.str = s;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof CWString && ((CWString) o).str.equals(str);
        }

        @Override
        public int hashCode() {
            return str.hashCode() + 3901;
        }

        @Override
        byte getType() {
            return CONSTANT_String;
        }
    }

    static class CWClass extends CWItem {
        final String name;

        CWClass(String c) {
            this.name = c;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof CWClass && ((CWClass) o).name.equals(name);
        }

        @Override
        public int hashCode() {
            return name.hashCode() + 1431;
        }

        @Override
        byte getType() {
            return CONSTANT_Class;
        }
    }

    static class CWRef extends CWItem {
        final String container;
        final String name;
        final String type;

        final byte kind;

        CWRef(byte k, String c, String n, String t) {
            this.kind = k;
            this.container = c;
            this.name = n;
            this.type = t;
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof CWRef) {
                CWRef r = (CWRef) o;
                return r.kind == kind && r.container.equals(container) && r.name.equals(name) && r.type.equals(type);
            }
            else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            return kind + (container.hashCode() << 5) + (name.hashCode() << 3) + type.hashCode();
        }

        @Override
        byte getType() {
            return kind;
        }
    }

    static class CWNameAndType extends CWItem {
        final String name;
        final String type;

        CWNameAndType(String n, String t) {
            this.name = n;
            this.type = t;
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof CWNameAndType) {
                CWNameAndType r = (CWNameAndType) o;
                return r.name.equals(name) && r.type.equals(type);
            }
            else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            return (name.hashCode() << 3) + type.hashCode();
        }

        @Override
        byte getType() {
            return CONSTANT_NameAndType;
        }
    }

	public void dump(PrintStream out) {
		for (int i = 0; i < cpItems.size(); i++) {
			out.print(i + ": ");
			Object item = cpItems.get(i);
			if (item instanceof Object[]) {
				out.print("array ");
				item = Arrays.asList((Object[]) item);
			}
			if (item instanceof Integer)
				out.print("int ");
			if (item instanceof Long)
				out.print("long ");
			if (item instanceof Float)
				out.print("float ");
			if (item instanceof Double)
				out.print("double ");
			if (item instanceof Type)
				out.print("type ");
			if (item instanceof String)
				out.print("utf8 ");
			if (item instanceof CWNameAndType)
				out.print("nat ");
			if (item instanceof CWString)
				out.print("string ");
			out.print(item);
			out.println();
		}
	}

}
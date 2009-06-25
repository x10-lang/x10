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

/**
 * A ConstantPoolParser provides read-only access to the constant pool of a
 * class file.
 */
public final class ConstantPoolParser implements SnConstants {
    ByteBuffer bytes;
    private int[] cpOffsets;
    private Object[] cpItems;

    // TODO: use JVM spec limit here?
    private final static int MAX_CP_ITEMS = Integer.MAX_VALUE / 4;

    /**
     * @param bytes
     *            the raw class file data
     * @param offset
     *            the start of the constant pool data
     * @param itemCount
     *            the number of items in the pool
     */
    public ConstantPoolParser(ByteBuffer bytes, int itemCount) throws InvalidClassFileException {
        this.bytes = bytes;
        if (itemCount < 0 || itemCount > MAX_CP_ITEMS) {
            throw new IllegalArgumentException("invalid itemCount: " + itemCount);
        }
        parseConstantPool(itemCount);
    }

    /**
     * @return the number of constant pool items (maximum item index plus one)
     */
    public int getItemCount() {
        return cpOffsets.length - 1;
    }

    private void checkLength(int offset, int required) throws InvalidClassFileException {
        if (bytes.getBytes().length < offset + required) {
            throw new InvalidClassFileException(offset, "file truncated, expected " + required + " bytes, saw only " + (bytes.getBytes().length - offset));
        }
    }

    /**
     * @return the type of constant pool item i, or 0 if i is an unused constant
     *         pool item
     */
    public byte getItemType(int i) throws InvalidClassFileException {
        if (i < 1 || i >= cpItems.length) {
            throw new IllegalArgumentException("Constant pool item #" + i + " out of range");
        }
        int offset = cpOffsets[i];
        if (offset == 0) {
            return 0;
        }
        else {
            return getByte(offset);
        }
    }

    /**
     * @return the name of the Class at constant pool item i, in JVM format
     *         (e.g., java/lang/Object)
     */
    public String getCPClass(int i) throws InvalidClassFileException {
        if (i < 1 || i >= cpItems.length) {
            throw new IllegalArgumentException("Constant pool item #" + i + " out of range");
        }
        int offset = cpOffsets[i];
        if (offset == 0 || getByte(offset) != CONSTANT_Class) {
            throw new IllegalArgumentException("Constant pool item #" + i + " is not a Class");
        }
        Object s = cpItems[i];
        if (s == null) {
            try {
                s = getCPUtf8(getInt(offset + 1));
                cpItems[i] = s;
            }
            catch (IllegalArgumentException ex) {
                throw new InvalidClassFileException(offset, "Invalid class name at constant pool item #" + i + ": " + ex.getMessage());
            }
        }
        return (String) s;
    }

    /**
     * @return the name of the class part of the FieldRef, MethodRef, or
     *         InterfaceMethodRef at constant pool item i
     */
    public Type getCPType(int i) throws InvalidClassFileException {
        if (i < 1 || i >= cpItems.length) {
            throw new IllegalArgumentException("Constant pool item #" + i + " out of range");
        }
        int offset = cpOffsets[i];
        if (offset == 0)
            return null;
        byte kind = getByte(offset);
        if (kind != CONSTANT_Type)
            throw new IllegalArgumentException("Constant pool item #" + i + " is not a Type");
        Object t = cpItems[i];
        if (t == null) {
            int b = bytes.offset();
            try {
                bytes.seek(offset+1);
                t = Type.readFrom(this, bytes);
                cpItems[i] = t;
            }
            catch (IllegalArgumentException ex) {
                throw new InvalidClassFileException(offset, "Invalid Type at constant pool item #" + i + ": " + ex.getMessage());
            }
            finally {
                bytes.seek(b);
            }
        }
        return (Type) t;
    }
    
    static Object transform(Object[] a) {
        char kind = 0;
        for (int i = 0; i < a.length; i++) {
            char k = 0;
            if (a[i] instanceof Double)
                k = 'D';
            if (a[i] instanceof Float)
                k = 'F';
            if (a[i] instanceof String)
                k = 'S';
            if (a[i] instanceof Integer)
                k = 'I';
            if (a[i] instanceof Type)
                k = 'T';
            if (a[i] instanceof Long)
                k = 'L';
            if (a[i] instanceof Constraint)
                k = 'C';
            
            if (kind == 0)
                kind = k;
            else if (kind != k)
                kind = 'O';
        }
        
        switch (kind) {
        case 'D': {
            double[] b = new double[a.length];
            for (int i = 0; i < a.length; i++) b[i] = (Double) a[i];
            return b;
        }
        case 'F': {
            float[] b = new float[a.length];
            for (int i = 0; i < a.length; i++) b[i] = (Float) a[i];
            return b;
        }
        case 'I': {
            int[] b = new int[a.length];
            for (int i = 0; i < a.length; i++) b[i] = (Integer) a[i];
            return b;
        }
        case 'L': {
            long[] b = new long[a.length];
            for (int i = 0; i < a.length; i++) b[i] = (Long) a[i];
            return b;
        }
        case 'T': {
            Type[] b = new Type[a.length];
            for (int i = 0; i < a.length; i++) b[i] = (Type) a[i];
            return b;
        }
        case 'C': {
            Constraint[] b = new Constraint[a.length];
            for (int i = 0; i < a.length; i++) b[i] = (Constraint) a[i];
            return b;
        }
        case 'S': {
            String[] b = new String[a.length];
            for (int i = 0; i < a.length; i++) b[i] = (String) a[i];
            return b;
        }
        }
        
        return a;
    }
    
    public Object getCPArray(int i) throws InvalidClassFileException {
        if (i < 1 || i >= cpItems.length) {
            throw new IllegalArgumentException("Constant pool item #" + i + " out of range");
        }
        int offset = cpOffsets[i];
        if (offset == 0 || getByte(offset) != CONSTANT_Array) {
            throw new IllegalArgumentException("Constant pool item #" + i + " is not a Array");
        }
        Object s = cpItems[i];
        if (s == null) {
            try {
                int len = getInt(offset+1);
                int count = len / CP_INDEX_SIZE;
                offset += 5;
                Object[] a = new Object[count];
                for (int k = 0; k < count; k++) {
                    int index = getInt(offset);
                    offset += CP_INDEX_SIZE;

                    if (index == 0)
                        a[k] = null;
                    else {
                        int kind = getByte(cpOffsets[index]);
                        switch (kind) {
                        case CONSTANT_Array:
                            a[k] = getCPArray(index);
                            break;
                        case CONSTANT_Constraint:
                            a[k] = getCPConstraint(index);
                            break;
                        case CONSTANT_Double:
                            a[k] = getCPDouble(index);
                            break;
                        case CONSTANT_Float:
                            a[k] = getCPDouble(index);
                            break;
                        case CONSTANT_Boolean:
                        	a[k] = getCPBoolean(index);
                        	break;
                        case CONSTANT_Integer:
                            a[k] = getCPInt(index);
                            break;
                        case CONSTANT_Long:
                            a[k] = getCPLong(index);
                            break;
                        case CONSTANT_String:
                            a[k] = getCPString(index);
                            break;
                        case CONSTANT_Type:
                            a[k] = getCPType(index);
                            break;
                        case CONSTANT_Utf8:
                        	a[k] = getCPUtf8(index);
                        	break;
                        default:
                        	throw new InvalidClassFileException(offset, "cp entry #" + index + " is not an array element.");
                        }
                    }
                }
                s = transform(a);
            }
            catch (IllegalArgumentException ex) {
                throw new InvalidClassFileException(offset, "Invalid array at constant pool item #" + i + ": " + ex.getMessage());
            }
            cpItems[i] = s;
        }
        return s;
    }

    /**
     * @return the String at constant pool item i
     */
    public String getCPString(int i) throws InvalidClassFileException {
        if (i < 1 || i >= cpItems.length) {
            throw new IllegalArgumentException("Constant pool item #" + i + " out of range");
        }
        int offset = cpOffsets[i];
        if (offset == 0 || getByte(offset) != CONSTANT_String) {
            throw new IllegalArgumentException("Constant pool item #" + i + " is not a String");
        }
        Object s = cpItems[i];
        if (s == null) {
            try {
                s = getCPUtf8(getInt(offset + 1));
            }
            catch (IllegalArgumentException ex) {
                throw new InvalidClassFileException(offset, "Invalid string at constant pool item #" + i + ": " + ex.getMessage());
            }
            cpItems[i] = s;
        }
        return (String) s;
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
    public String getCPRefClass(int i) throws InvalidClassFileException {
        if (i < 1 || i >= cpItems.length) {
            throw new IllegalArgumentException("Constant pool item #" + i + " out of range");
        }
        int offset = cpOffsets[i];
        if (offset == 0 || !isRef(getByte(offset))) {
            throw new IllegalArgumentException("Constant pool item #" + i + " is not a Ref");
        }
        try {
            return getCPClass(getUShort(offset + 1));
        }
        catch (IllegalArgumentException ex) {
            throw new InvalidClassFileException(offset, "Invalid Ref class at constant pool item #" + i + ": " + ex.getMessage());
        }
    }

    /**
     * @return the name part of the FieldRef, MethodRef, or InterfaceMethodRef
     *         at constant pool item i
     */
    public String getCPRefName(int i) throws InvalidClassFileException {
        if (i < 1 || i >= cpItems.length) {
            throw new IllegalArgumentException("Constant pool item #" + i + " out of range");
        }
        int offset = cpOffsets[i];
        if (offset == 0 || !isRef(getByte(offset))) {
            throw new IllegalArgumentException("Constant pool item #" + i + " is not a Ref");
        }
        try {
            return getCPNATName(getInt(offset + 5));
        }
        catch (IllegalArgumentException ex) {
            throw new InvalidClassFileException(offset, "Invalid Ref NameAndType at constant pool item #" + i + ": " + ex.getMessage());
        }
    }

    /**
     * @return the type part of the FieldRef, MethodRef, or InterfaceMethodRef
     *         at constant pool item i, in JVM format (e.g., I, Z, or
     *         Ljava/lang/Object;)
     */
    public String getCPRefType(int i) throws InvalidClassFileException {
        if (i < 1 || i >= cpItems.length) {
            throw new IllegalArgumentException("Constant pool item #" + i + " out of range");
        }
        int offset = cpOffsets[i];
        if (offset == 0 || !isRef(getByte(offset))) {
            throw new IllegalArgumentException("Constant pool item #" + i + " is not a Ref");
        }
        try {
            return getCPNATType(getInt(offset + 5));
        }
        catch (IllegalArgumentException ex) {
            throw new InvalidClassFileException(offset, "Invalid Ref NameAndType at constant pool item #" + i + ": " + ex.getMessage());
        }
    }

    /**
     * @return the name part of the NameAndType at constant pool item i
     */
    public String getCPNATName(int i) throws InvalidClassFileException {
        if (i < 1 || i >= cpItems.length) {
            throw new IllegalArgumentException("Constant pool item #" + i + " out of range");
        }
        int offset = cpOffsets[i];
        if (offset == 0 || getByte(offset) != CONSTANT_NameAndType) {
            throw new IllegalArgumentException("Constant pool item #" + i + " is not a NameAndType");
        }
        try {
            return getCPUtf8(getInt(offset + 1));
        }
        catch (IllegalArgumentException ex) {
            throw new InvalidClassFileException(offset, "Invalid NameAndType name at constant pool item #" + i + ": " + ex.getMessage());
        }
    }

    /**
     * @return the type part of the NameAndType at constant pool item i, in JVM
     *         format (e.g., I, Z, or Ljava/lang/Object;)
     */
    public String getCPNATType(int i) throws InvalidClassFileException {
        if (i < 1 || i >= cpItems.length) {
            throw new IllegalArgumentException("Constant pool item #" + i + " out of range");
        }
        int offset = cpOffsets[i];
        if (offset == 0 || getByte(offset) != CONSTANT_NameAndType) {
            throw new IllegalArgumentException("Constant pool item #" + i + " is not a NameAndType");
        }
        try {
            return getCPUtf8(getInt(offset + 5));
        }
        catch (IllegalArgumentException ex) {
            throw new InvalidClassFileException(offset, "Invalid NameAndType type at constant pool item #" + i + ": " + ex.getMessage());
        }
    }

    /**
     * @return the value of the Integer at constant pool item i
     */
    public boolean getCPBoolean(int i) throws InvalidClassFileException {
        if (i < 1 || i >= cpItems.length) {
            throw new IllegalArgumentException("Constant pool item #" + i + " out of range");
        }
        int offset = cpOffsets[i];
        if (offset == 0 || getByte(offset) != CONSTANT_Boolean) {
            throw new IllegalArgumentException("Constant pool item #" + i + " is not a Boolean");
        }
        return getByte(offset + 1) != 0;
    }
    
    /**
     * @return the value of the Integer at constant pool item i
     */
    public int getCPInt(int i) throws InvalidClassFileException {
    	if (i < 1 || i >= cpItems.length) {
    		throw new IllegalArgumentException("Constant pool item #" + i + " out of range");
    	}
    	int offset = cpOffsets[i];
    	if (offset == 0 || getByte(offset) != CONSTANT_Integer) {
    		throw new IllegalArgumentException("Constant pool item #" + i + " is not an Integer");
    	}
    	return getInt(offset + 1);
    }

    /**
     * @return the value of the Float at constant pool item i
     */
    public float getCPFloat(int i) throws InvalidClassFileException {
        if (i < 1 || i >= cpItems.length) {
            throw new IllegalArgumentException("Constant pool item #" + i + " out of range");
        }
        int offset = cpOffsets[i];
        if (offset == 0 || getByte(offset) != CONSTANT_Float) {
            throw new IllegalArgumentException("Constant pool item #" + i + " is not a Float");
        }
        return getFloat(offset + 1);
    }

    /**
     * @return the value of the Long at constant pool item i
     */
    public long getCPLong(int i) throws InvalidClassFileException {
        if (i < 1 || i >= cpItems.length) {
            throw new IllegalArgumentException("Constant pool item #" + i + " out of range");
        }
        int offset = cpOffsets[i];
        if (offset == 0 || getByte(offset) != CONSTANT_Long) {
            throw new IllegalArgumentException("Constant pool item #" + i + " is not a Long");
        }
        return getLong(offset + 1);
    }

    /**
     * @return the value of the Double at constant pool item i
     */
    public double getCPDouble(int i) throws InvalidClassFileException {
        if (i < 1 || i >= cpItems.length) {
            throw new IllegalArgumentException("Constant pool item #" + i + " out of range");
        }
        int offset = cpOffsets[i];
        if (offset == 0 || getByte(offset) != CONSTANT_Double) {
            throw new IllegalArgumentException("Constant pool item #" + i + " is not a Double");
        }
        return getDouble(offset + 1);
    }

    /**
     * @return the value of the byte array at constant pool item i
     */
    public byte[] getCPBytes(int i) throws InvalidClassFileException {
        if (i < 1 || i >= cpItems.length) {
            throw new IllegalArgumentException("Constant pool item #" + i + " out of range");
        }
        int offset = cpOffsets[i];
        if (offset == 0 || getByte(offset) != CONSTANT_ByteArray) {
            throw new IllegalArgumentException("Constant pool item #" + i + " is not a byte array");
        }
        int len = getInt(offset + 1);
        return bytes.getBytes(offset + 1 + LENGTH_SIZE, len);
    }

    /**
     * @return the value of the Constraint at constant pool item i
     */
    public Constraint getCPConstraint(int i) throws InvalidClassFileException {
        if (i < 1 || i >= cpItems.length) {
            throw new IllegalArgumentException("Constant pool item #" + i + " out of range");
        }
        int offset = cpOffsets[i];
        if (offset == 0)
            return null;
        if (getByte(offset) != CONSTANT_Constraint) {
            throw new IllegalArgumentException("Constant pool item #" + i + " is not a Constraint");
        }
        Object r = cpItems[i];
        if (r == null) {
            int len = getInt(offset + 1);
            r = new Constraint(bytes, offset + 1 + LENGTH_SIZE, len);
            cpItems[i] = r;
        }
        return (Constraint) r;
    }

    private InvalidClassFileException invalidUtf8(int item, int offset) {
        try {
            return new InvalidClassFileException(offset, "Constant pool item #" + item + " starting at " + cpOffsets[item]
                    + ", is an invalid Java Utf8 string (byte is " + getByte(offset) + ")");
        }
        catch (InvalidClassFileException e) {
            return e;
        }
    }

    /**
     * @return the value of the Utf8 string at constant pool item i
     */
    public String getCPUtf8(int i) throws InvalidClassFileException {
        if (i < 1 || i >= cpItems.length) {
            throw new IllegalArgumentException("Constant pool item #" + i + " out of range");
        }
        int offset = cpOffsets[i];
        if (offset == 0 || getByte(offset) != CONSTANT_Utf8) {
            throw new IllegalArgumentException("Constant pool item #" + i + " is not a Utf8");
        }
        Object s = cpItems[i];
        if (s == null) {
            int count = getInt(offset + 1);
            int end = count + offset + 5;
            StringBuffer buf = new StringBuffer(count);
            offset += 5;
            while (offset < end) {
                byte x = getByte(offset);
                if ((x & 0x80) == 0) {
                    if (x == 0) {
                        throw invalidUtf8(i, offset);
                    }
                    buf.append((char) x);
                    offset++;
                }
                else if ((x & 0xE0) == 0xC0) {
                    if (offset + 1 >= end) {
                        throw invalidUtf8(i, offset);
                    }
                    byte y = getByte(offset + 1);
                    if ((y & 0xC0) != 0x80) {
                        throw invalidUtf8(i, offset);
                    }
                    buf.append((char) (((x & 0x1F) << 6) + (y & 0x3F)));
                    offset += 2;
                }
                else if ((x & 0xF0) == 0xE0) {
                    if (offset + 2 >= end) {
                        throw invalidUtf8(i, offset);
                    }
                    byte y = getByte(offset + 1);
                    byte z = getByte(offset + 2);
                    if ((y & 0xC0) != 0x80 || (z & 0xC0) != 0x80) {
                        throw invalidUtf8(i, offset);
                    }
                    buf.append((char) (((x & 0x0F) << 12) + ((y & 0x3F) << 6) + (z & 0x3F)));
                    offset += 3;
                }
                else {
                    throw invalidUtf8(i, offset);
                }
            }
            // s = buf.toString().intern(); // removed intern() call --MS
            s = buf.toString();
            cpItems[i] = s;
        }
        return (String) s;
    }

    private void parseConstantPool(int itemCount) throws InvalidClassFileException {
        int len;
        
        ByteBuffer buf = this.bytes;

        cpOffsets = new int[itemCount + 1];
        cpItems = new Object[itemCount];
        for (int i = 1; i < itemCount; i++) {
            int magic = bytes.getInt();
            assert magic == 0xdeadbeef;
            cpOffsets[i] = bytes.offset();
            byte tag = bytes.getByte();
            switch (tag) {
            case CONSTANT_String:
            case CONSTANT_Class:
                bytes.getCPIndex();
                break;
            case CONSTANT_Type:
                bytes.skip(Type.size(this, bytes));
                break;
            case CONSTANT_NameAndType:
            case CONSTANT_MethodRef:
            case CONSTANT_FieldRef:
                bytes.getCPIndex();
                bytes.getCPIndex();
                break;
            case CONSTANT_Boolean:
            	bytes.getByte();
            	break;
            case CONSTANT_Integer:
                bytes.getInt();
                break;
            case CONSTANT_Float:
                bytes.getFloat();
                break;
            case CONSTANT_Long:
                bytes.getLong();
                break;
            case CONSTANT_Double:
                bytes.getDouble();
                break;
            case CONSTANT_Utf8:
                len = bytes.getLength();
                bytes.skip(len);
                break;
            case CONSTANT_Constraint:
                len = bytes.getLength();
                bytes.skip(len);
                break;
            case CONSTANT_ByteArray:
                len = bytes.getLength();
                bytes.skip(len);
                break;
            case CONSTANT_Array:
                len = bytes.getLength();
                bytes.skip(len);
                break;
            default:
                throw new InvalidClassFileException(bytes.offset(), "unknown constant pool entry type " + tag);
            }
        }
        cpOffsets[itemCount] = bytes.offset();
    }

    private byte getByte(int i) throws InvalidClassFileException {
        return bytes.getByte(i);
    }

    private int getUShort(int i) throws InvalidClassFileException {
        return bytes.getUShort(i);
    }

    private short getShort(int i) throws InvalidClassFileException {
        return bytes.getShort(i);
    }

    private int getInt(int i) throws InvalidClassFileException {
        return bytes.getInt(i);
    }

    private long getLong(int i) throws InvalidClassFileException {
        return bytes.getLong(i);
    }

    private float getFloat(int i) throws InvalidClassFileException {
        return bytes.getFloat(i);
    }

    private double getDouble(int i) throws InvalidClassFileException {
        return bytes.getDouble(i);
    }
}
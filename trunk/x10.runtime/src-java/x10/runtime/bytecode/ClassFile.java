/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

package x10.runtime.bytecode;

import static x10.runtime.bytecode.ByteArrayUtil.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClassFile implements ClassFileConstants {
	public int magic;
	public short minor;
	public short major;
	public List<byte[]> constant_pool;
	public short access_flags;
	public short this_class;
	public short super_class;
	public short interfaces[];
	public Field fields[];
	public Method methods[];
	public Attribute attributes[];

	public short constantPoolSize() {
		return (short)constant_pool.size();
	}
	public short getNameIndex(int val_idx) {
		byte[] entry = constant_pool.get(val_idx);
		assert (entry[0] == CONSTANT_Class || entry[0] == CONSTANT_NameAndType || entry[0] == CONSTANT_String);
		return getShort(entry, 1);
	}
	public short getDescriptorIndex(int val_idx) {
		byte[] entry = constant_pool.get(val_idx);
		assert (entry[0] == CONSTANT_NameAndType);
		return getShort(entry, 3);
	}
	public short getClassIndex(int val_idx) {
		byte[] entry = constant_pool.get(val_idx);
		assert (entry[0] == CONSTANT_Fieldref || entry[0] == CONSTANT_Methodref ||
				entry[0] == CONSTANT_InterfaceMethodref);
		return getShort(entry, 1);
	}
	public short addClassRef(short name_index) {
		byte[] entry = new byte[3];
		entry[0] = CONSTANT_Class;
		putShort(entry, 1, name_index);
		short class_index = (short)constant_pool.size();
		constant_pool.add(entry);
		return class_index;
	}
	public short getNameAndTypeIndex(int val_idx) {
		byte[] entry = constant_pool.get(val_idx);
		assert (entry[0] == CONSTANT_Fieldref || entry[0] == CONSTANT_Methodref ||
				entry[0] == CONSTANT_InterfaceMethodref);
		return getShort(entry, 3);
	}
	public short addNameAndType(short name_index, short descriptor_index) {
		byte[] entry = new byte[5];
		entry[0] = CONSTANT_NameAndType;
		putShort(entry, 1, name_index);
		putShort(entry, 3, descriptor_index);
		short name_and_type_index = (short)constant_pool.size();
		constant_pool.add(entry);
		return name_and_type_index;
	}
	public short addMethodRef(short class_index, short name_and_type_index, boolean isInterface) {
		byte[] entry = new byte[5];
		if (isInterface)
			entry[0] = CONSTANT_InterfaceMethodref;
		else
			entry[0] = CONSTANT_Methodref;
		putShort(entry, 1, class_index);
		putShort(entry, 3, name_and_type_index);
		short method_ref_index = (short)constant_pool.size();
		constant_pool.add(entry);
		return method_ref_index;
	}
	public short addFieldRef(short class_index, short descriptor_index) {
		byte[] entry = new byte[5];
		entry[0] = CONSTANT_Fieldref;
		putShort(entry, 1, class_index);
		putShort(entry, 3, descriptor_index);
		short method_ref_index = (short)constant_pool.size();
		constant_pool.add(entry);
		return method_ref_index;
	}
	private static final String UTF8 = "UTF-8";
	public String getString(int name_idx) {
		try {
			byte[] entry = constant_pool.get(name_idx);
			assert (entry[0] == CONSTANT_Utf8);
			int name_len = getShort(entry, 1);
			return new String(entry, 3, name_len, UTF8).intern();
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}
	public void setString(int name_idx, String val) {
		try {
			byte[] old = constant_pool.get(name_idx);
			assert (old[0] == CONSTANT_Utf8);
			byte[] utf8 = val.getBytes(UTF8);
			byte[] entry = new byte[utf8.length + 3];
			entry[0] = CONSTANT_Utf8;
			putShort(entry, 1, (short)utf8.length);
			System.arraycopy(utf8, 0, entry, 3, utf8.length);
			constant_pool.set(name_idx, entry);
		} catch (UnsupportedEncodingException e) { /* Ignore */ }
	}
	public short addString(String val) {
		try {
			byte[] utf8 = val.getBytes(UTF8);
			byte[] entry = new byte[utf8.length + 3];
			entry[0] = CONSTANT_Utf8;
			putShort(entry, 1, (short)utf8.length);
			System.arraycopy(utf8, 0, entry, 3, utf8.length);
			short name_index = (short)constant_pool.size();
			constant_pool.add(entry);
			return name_index;
		} catch (UnsupportedEncodingException e) {
			return -1;
		}
	}
	public short findEntry(int tag) {
		return findEntry(tag, 0);
	}
	public short findEntry(int tag, int start) {
		for (int i = start; i < constant_pool.size(); i++)
			if (constant_pool.get(i)[0] == tag)
				return (short)i;
		return -1;
	}

	private short[] instances = new short[1]; // usually 1 other instance
	private int numInstances = 0;
	/**
	 * Is the class reference at a given index an instance of this class.
	 */
	public boolean isInstance(int index) {
		if (index == this_class)
			return true;
		for (int i = 0; i < numInstances; i++)
			if (instances[i] == index)
				return true;
		return false;
	}
	/**
	 * Register a class reference at a given index as an instance of this class.
	 */
	public void addInstance(int index) {
		if (isInstance(index)) return;
		if (numInstances == instances.length)
			System.arraycopy(instances, 0, instances = new short[numInstances*2], 0, numInstances);
		instances[numInstances++] = (short) index;
	}

	private static final String CODE = "Code";
	public static final int MethodCodeOffset = 8; // skip max_stack, max_locals, and code length
	public byte[] getMethodCode(Method m) {
		Attribute code = findAttribute(CODE, m.attributes);
		if (code == null) return null;
		return code.info;
	}
	// Can be static, but that causes a warning...
	public int getMethodStack(byte[] b) {
		return getShort(b, 0);
	}
	// Can be static, but that causes a warning...
	public void setMethodStack(byte[] b, short newVal) {
		putShort(b, 0, newVal);
	}
	// Can be static, but that causes a warning...
	public int getMethodLocals(byte[] b) {
		return getShort(b, 2); // skip max_stack
	}
	// Can be static, but that causes a warning...
	public void setMethodLocals(byte[] b, short newVal) {
		putShort(b, 2, newVal); // skip max_stack
	}
	// Can be static, but that causes a warning...
	public int getMethodCodeOffset(byte[] b) {
		return MethodCodeOffset;
	}
	// Can be static, but that causes a warning...
	public int getMethodCodeLength(byte[] b) {
		return getInt(b, 4); // skip max_stack and max_locals
	}
	// Can be static, but that causes a warning...
	public int getMethodExceptionTableOffset(byte[] b) {
		int len = getInt(b, 4); // skip max_stack and max_locals
		return 8 + len + 2; // also skip code length and bytecodes and ET length
	}
	// Can be static, but that causes a warning...
	public int getMethodExceptionTableLength(byte[] b) {
		int len = getInt(b, 4); // skip max_stack and max_locals
		return getShort(b, 8 + len) * 8; // skip code length and bytecodes; each entry is 8 bytes
	}

	public Attribute findAttribute(String name, Attribute[] attributes) {
		for (int i = 0; i < attributes.length; i++) {
			Attribute a = attributes[i];
			if (getString(a.attribute_name_index).equals(name))
				return a;
		}
		return null;
	}

	public static class Attribute {
		public short attribute_name_index;
		public byte info[];
		public String toString() {
			StringBuilder b = new StringBuilder();
			b.append(attribute_name_index).append(" [");
			for (int i = 0; i < info.length; i++)
				b.append(Integer.toHexString(info[i])).append(" ");
			b.append("]");
			return b.toString();
		}
	}
	public static class Member {
		public short access_flags;
		public short name_index;
		public short descriptor_index;
		public Attribute attributes[];
		public String toString() {
			StringBuilder b = new StringBuilder();
			b.append(access_flags).append(" ").append(name_index).append(":").append(descriptor_index);
			if (attributes != null) {
				b.append(" [ ");
				for (int i = 0; i < attributes.length; i++)
					b.append(attributes[i].toString()).append(" ");
				b.append("]");
			}
			return b.toString();
		}
	}
	// FIXME: Field and Method are essentially the same.  Combine?
	public static class Field extends Member {
		public String toString() { return "Field "+super.toString(); }
	}
	// FIXME: Field and Method are essentially the same.  Combine?
	public static class Method extends Member {
		public String toString() { return "Method "+super.toString(); }
	}
	public Field findField(String name, String descriptor) {
		for (int i = 0; i < fields.length; i++) {
			Field f = fields[i];
			String n = getString(f.name_index);
			String d = getString(f.descriptor_index);
			if (n.equals(name) && d.equals(descriptor))
				return f;
		}
		return null;
	}
	public Method findMethod(String name, String signature) {
		for (int i = 0; i < methods.length; i++) {
			Method m = methods[i];
			String n = getString(m.name_index);
			String s = getString(m.descriptor_index);
			if (n.equals(name) && s.equals(signature))
				return m;
		}
		return null;
	}

	public static class Annotation {
		public short type_index;
		public ElementValuePair[] element_value_pairs;

		public static class ElementValuePair {
			public short element_name_index;
			public ElementValue value;
		}
		public static final byte TAG_byte = 'B';
		public static final byte TAG_char = 'C';
		public static final byte TAG_double = 'D';
		public static final byte TAG_float = 'F';
		public static final byte TAG_int = 'I';
		public static final byte TAG_long = 'J';
		public static final byte TAG_short = 'S';
		public static final byte TAG_boolean = 'Z';
		public static final byte TAG_String = 's';
		public static final byte TAG_Enum = 'e';
		public static final byte TAG_Class = 'c';
		public static final byte TAG_Annotation = '@';
		public static final byte TAG_array = '[';
		public static abstract class ElementValue {
			public byte tag;
		}
		public static class ConstValue extends ElementValue {
			public short const_value_index;
		}
		public static class EnumValue extends ElementValue {
			public short type_name_index;
			public short const_name_index;
		}
		public static class ClassInfo extends ElementValue {
			public short class_info_index;
		}
		public static class AnnotationValue extends ElementValue {
			public Annotation annotation_value;
		}
		public static class ArrayValue extends Annotation.ElementValue {
			public Annotation.ElementValue[] values;
		}
	}
	public Annotation[] parseAnnotations() {
		return parseAnnotations(attributes);
	}
	public Annotation[] parseAnnotations(Field f) {
		return parseAnnotations(f.attributes);
	}
	public Annotation[] parseAnnotations(Method m) {
		return parseAnnotations(m.attributes);
	}
	public Annotation[][] parseParameterAnnotations(Method m) {
		return parseParameterAnnotations(m.attributes);
	}
	public Annotation findAnnotation(String name) {
		Annotation[] annot = parseAnnotations();
		return findAnnotation(annot, name);
	}
	public Annotation findAnnotation(Field f, String name) {
		return findAnnotation(parseAnnotations(f), name);
	}
	public Annotation findAnnotation(Method m, String name) {
		return findAnnotation(parseAnnotations(m), name);
	}
	public Annotation findAnnotation(Method m, String name, int p) {
		Annotation[][] paramAnnot = parseParameterAnnotations(m);
		if (paramAnnot == null)
			return null;
		return findAnnotation(paramAnnot[p], name);
	}
	public Annotation findAnnotation(Annotation[] annotations, String name) {
		for (int i = 0; i < annotations.length; i++) {
			Annotation a = annotations[i];
			String n = getString(a.type_index);
			if (n.equals(name))
				return a;
		}
		return null;
	}

	private static final String RUNTIME_VISIBLE_ANNOTATIONS = "RuntimeVisibleAnnotations";
	private static final String RUNTIME_INVISIBLE_ANNOTATIONS = "RuntimeInvisibleAnnotations";
	private static final String[] ANNOTATION_ATTRIBUTES = {
		RUNTIME_VISIBLE_ANNOTATIONS,
		RUNTIME_INVISIBLE_ANNOTATIONS,
	};
	private static final String RUNTIME_VISIBLE_PARAMETER_ANNOTATIONS = "RuntimeVisibleParameterAnnotations";
	private static final String RUNTIME_INVISIBLE_PARAMETER_ANNOTATIONS = "RuntimeInvisibleParameterAnnotations";
	private static final String[] PARAMETER_ANNOTATION_ATTRIBUTES = {
		RUNTIME_VISIBLE_PARAMETER_ANNOTATIONS,
		RUNTIME_INVISIBLE_PARAMETER_ANNOTATIONS,
	};
	private Annotation[] parseAnnotations(Attribute[] attributes) {
		int num_annotations = 0;
		Attribute[] annotAttrs = new Attribute[ANNOTATION_ATTRIBUTES.length];
		for (int i = 0; i < ANNOTATION_ATTRIBUTES.length; i++) {
			Attribute a = findAttribute(ANNOTATION_ATTRIBUTES[i], attributes);
			if (a != null)
				num_annotations += getShort(a.info, 0);
			annotAttrs[i] = a;
		}
		Annotation[] annotations = new Annotation[num_annotations];
		int total = 0;
		for (int i = 0; i < annotAttrs.length; i++) {
			Attribute a = annotAttrs[i];
			if (a != null) {
				short num = getShort(a.info, 0);
				int o = 2;
				for (int j = 0; j < num; j++)
					o = extractAnnotation(a.info, o, annotations, total + j);
				assert (o == a.info.length);
				total = total + num;
			}
		}
		assert (total == num_annotations);
		return annotations;
	}
	private Annotation[][] parseParameterAnnotations(Attribute[] attributes) {
		int num_parameters = 0;
		Attribute[] annotAttrs = new Attribute[PARAMETER_ANNOTATION_ATTRIBUTES.length];
		for (int i = 0; i < PARAMETER_ANNOTATION_ATTRIBUTES.length; i++) {
			Attribute a = findAttribute(PARAMETER_ANNOTATION_ATTRIBUTES[i], attributes);
			if (a != null) {
				short n = getByte(a.info, 0);
				assert (num_parameters == 0 || num_parameters == n);
				num_parameters = n;
			}
			annotAttrs[i] = a;
		}
		int[] num_annotations = new int[num_parameters];
		for (int i = 0; i < annotAttrs.length; i++) {
			Attribute a = annotAttrs[i];
			if (a != null) {
				int off = 1;
				for (int j = 0; j < num_parameters; j++) {
					short n = getShort(a.info, off);
					num_annotations[j] += n;
					off += 2;
					for (int k = 0; k < n; k++)
						off += getInt(a.info, off);
				}
			}
		}
		if (num_parameters == 0)
			return null;
		Annotation[][] annotations = new Annotation[num_parameters][];
		for (int i = 0; i < annotations.length; i++)
			annotations[i] = new Annotation[num_annotations[i]];
		int[] total = new int[num_parameters];
		for (int i = 0; i < annotAttrs.length; i++) {
			Attribute a = annotAttrs[i];
			if (a != null) {
				int off = 1;
				for (int j = 0; j < num_parameters; j++) {
					short n = getShort(a.info, off);
					off += 2;
					int t = total[j];
					for (int k = 0; k < n; k++)
						off = extractAnnotation(a.info, off, annotations[j], t+k);
					total[j] = t + n;
				}
				assert (off == a.info.length);
			}
		}
		for (int j = 0; j < num_parameters; j++)
			assert (total[j] == num_annotations[j]);
		return annotations;
	}
	private int extractAnnotation(byte[] b, int o, Annotation[] a, int j) {
		Annotation r = new Annotation();
		r.type_index = getShort(b, o);
		o += 2;
		short num_pairs = getShort(b, o);
		o += 2;
		Annotation.ElementValuePair[] evp = new Annotation.ElementValuePair[num_pairs];
		r.element_value_pairs = evp;
		for (int k = 0; k < num_pairs; k++) {
			evp[k] = new Annotation.ElementValuePair();
			evp[k].element_name_index = getShort(b, o);
			o += 2;
			Annotation.ElementValue[] v = new Annotation.ElementValue[1];
			int l = 0;
			o = extractElementValue(b, o, v, l);
			evp[k].value = v[0];
		}
		a[j] = r;
		return o;
	}
	private int extractElementValue(byte[] b, int o, Annotation.ElementValue[] v, int k) {
		byte tag = getByte(b, o);
		o += 1;
		switch (tag) {
		case Annotation.TAG_byte:
		case Annotation.TAG_char:
		case Annotation.TAG_double:
		case Annotation.TAG_float:
		case Annotation.TAG_int:
		case Annotation.TAG_long:
		case Annotation.TAG_short:
		case Annotation.TAG_boolean:
		case Annotation.TAG_String:
			Annotation.ConstValue constVal = new Annotation.ConstValue();
			constVal.tag = tag;
			constVal.const_value_index = getShort(b, o);
			o += 2;
			v[k] = constVal;
			break;
		case Annotation.TAG_Enum:
			Annotation.EnumValue enumVal = new Annotation.EnumValue();
			enumVal.tag = tag;
			enumVal.type_name_index = getShort(b, o);
			o += 2;
			enumVal.const_name_index = getShort(b, o);
			o += 2;
			v[k] = enumVal;
			break;
		case Annotation.TAG_Class:
			Annotation.ClassInfo classVal = new Annotation.ClassInfo();
			classVal.tag = tag;
			classVal.class_info_index = getShort(b, o);
			o += 2;
			v[k] = classVal;
			break;
		case Annotation.TAG_Annotation:
			Annotation.AnnotationValue annotVal = new Annotation.AnnotationValue();
			annotVal.tag = tag;
			Annotation[] h = new Annotation[1];
			o = extractAnnotation(b, o, h, 0);
			annotVal.annotation_value = h[0];
			v[k] = annotVal;
			break;
		case Annotation.TAG_array:
			Annotation.ArrayValue arrayVal = new Annotation.ArrayValue();
			arrayVal.tag = tag;
			short num_values = getShort(b, o);
			o += 2;
			arrayVal.values = new Annotation.ElementValue[num_values];
			for (int i = 0; i < num_values; i++)
				o = extractElementValue(b, o, arrayVal.values, i);
			v[k] = arrayVal;
			break;
		default:
			assert (false);
		}
		return o;
	}

	private int computeLength() {
		return 4 /* magic */
		     + 2 /* minor */
		     + 2 /* major */
		     + computeConstantPoolLength(constant_pool)
		     + 2 /* access_flags */
		     + 2 /* this_class */
		     + 2 /* super_class */
		     + 2 + 2 * interfaces.length /* interfaces */
		     + computeFieldsLength(fields)
		     + computeMethodsLength(methods)
		     + computeAttributesLength(attributes);
	}
	private static int computeConstantPoolLength(List<byte[]> cp) {
		int total = 2;
		boolean skip = true; // Skip first entry
		for (byte[] e : cp) {
			if (skip) { skip = false; continue; }
			total += e.length;
			if (e[0] == CONSTANT_Long || e[0] == CONSTANT_Double) skip = true; // Skip next entry
		}
		return total;
	}
	private static int computeFieldsLength(Field[] f) {
		int total = 2;
		for (int i = 0; i < f.length; i++) {
			total += 2 /* access_flags */
			       + 2 /* name_index */
			       + 2 /* descriptor_index */
			       + computeAttributesLength(f[i].attributes);
		}
		return total;
	}
	private static int computeMethodsLength(Method[] m) {
		int total = 2;
		for (int i = 0; i < m.length; i++) {
			total += 2 /* access_flags */
			       + 2 /* name_index */
			       + 2 /* descriptor_index */
			       + computeAttributesLength(m[i].attributes);
		}
		return total;
	}
	private static int computeAttributesLength(Attribute[] a) {
		int total = 2;
		for (int i = 0; i < a.length; i++) {
			total += 2 /* attribute_name_index */
			       + 4 + a[i].info.length;
		}
		return total;
	}
	private static int dumpConstantPool(byte[] b, int o, List<byte[]> cp) {
		o = putShort(b, o, (short)cp.size());
		boolean skip = true; // Skip first entry
		for (byte[] e : cp) {
			if (skip) { skip = false; continue; }
			System.arraycopy(e, 0, b, o, e.length);
			o += e.length;
			if (e[0] == CONSTANT_Long || e[0] == CONSTANT_Double) skip = true; // Skip next entry
		}
		return o;
	}
	private static int dumpFields(byte[] b, int o, Field[] f) {
		o = putShort(b, o, (short)f.length);
		for (int i = 0; i < f.length; i++) {
			o = putShort(b, o, f[i].access_flags);
			o = putShort(b, o, f[i].name_index);
			o = putShort(b, o, f[i].descriptor_index);
			o = dumpAttributes(b, o, f[i].attributes);
		}
		return o;
	}
	private static int dumpMethods(byte[] b, int o, Method[] m) {
		o = putShort(b, o, (short)m.length);
		for (int i = 0; i < m.length; i++) {
			o = putShort(b, o, m[i].access_flags);
			o = putShort(b, o, m[i].name_index);
			o = putShort(b, o, m[i].descriptor_index);
			o = dumpAttributes(b, o, m[i].attributes);
		}
		return o;
	}
	private static int dumpAttributes(byte[] b, int o, Attribute[] a) {
		o = putShort(b, o, (short)a.length);
		for (int i = 0; i < a.length; i++) {
			o = putShort(b, o, a[i].attribute_name_index);
			o = putInt(b, o, a[i].info.length);
			System.arraycopy(a[i].info, 0, b, o, a[i].info.length);
			o += a[i].info.length;
		}
		return o;
	}
	public byte[] toByteArray() {
		byte[] b = new byte[computeLength()];
		int o = 0;
		o = putInt(b, o, magic);
		o = putShort(b, o, major);
		o = putShort(b, o, minor);
		o = dumpConstantPool(b, o, constant_pool);
		o = putShort(b, o, access_flags);
		o = putShort(b, o, this_class);
		o = putShort(b, o, super_class);
		o = putShort(b, o, (short)interfaces.length);
		for (int i = 0; i < interfaces.length; i++) {
			o = putShort(b, o, interfaces[i]);
		}
		o = dumpFields(b, o, fields);
		o = dumpMethods(b, o, methods);
		o = dumpAttributes(b, o, attributes);
		assert (o == b.length);
		return b;
	}
	public static ClassFile parseClass(String name, InputStream in) throws IOException {
		ClassFile cf = new ClassFile();
		cf.magic = readInt(in);
		cf.major = readShort(in);
		cf.minor = readShort(in);
		cf.constant_pool = readConstantPool(name, in);
		cf.access_flags = readShort(in);
		cf.this_class = readShort(in);
		cf.super_class = readShort(in);
		short interfaces_count = readShort(in);
		cf.interfaces = new short[interfaces_count];
		for (int i = 0; i < interfaces_count; i++)
			cf.interfaces[i] = readShort(in);
		short fields_count = readShort(in);
		cf.fields = new Field[fields_count];
		for (int i = 0; i < fields_count; i++)
			cf.fields[i] = readField(in);
		short methods_count = readShort(in);
		cf.methods = new Method[methods_count];
		for (int i = 0; i < methods_count; i++)
			cf.methods[i] = readMethod(in);
		short attributes_count = readShort(in);
		cf.attributes = new Attribute[attributes_count];
		for (int i = 0; i < attributes_count; i++)
			cf.attributes[i] = readAttribute(in);
		return cf;
	}
	private static Field readField(InputStream in) throws IOException {
		Field f = new Field();
		f.access_flags = readShort(in);
		f.name_index = readShort(in);
		f.descriptor_index = readShort(in);
		short attributes_count = readShort(in);
		f.attributes = new Attribute[attributes_count];
		for (int i = 0; i < attributes_count; i++)
			f.attributes[i] = readAttribute(in);
		return f;
	}
	private static Method readMethod(InputStream in) throws IOException {
		Method m = new Method();
		m.access_flags = readShort(in);
		m.name_index = readShort(in);
		m.descriptor_index = readShort(in);
		short attributes_count = readShort(in);
		m.attributes = new Attribute[attributes_count];
		for (int i = 0; i < attributes_count; i++)
			m.attributes[i] = readAttribute(in);
		return m;
	}
	private static Attribute readAttribute(InputStream in) throws IOException {
		Attribute a = new Attribute();
		a.attribute_name_index = readShort(in);
		int attribute_length = readInt(in);
		a.info = new byte[attribute_length];
		in.read(a.info);
		return a;
	}
	private static final int CONSTANT_Empty = 0;
	private static final byte[] EMPTY = { CONSTANT_Empty };
	private static List<byte[]> readConstantPool(String name, InputStream in) throws IOException {
		int cp_size = ((in.read() & 0xFF) << 8 | (in.read() & 0xFF));
		byte[][] cp = new byte[cp_size][];
		cp[0] = EMPTY;
		for (int i = 1; i < cp_size; i++) {
			byte tag = (byte)in.read();
			switch (tag) {
				case CONSTANT_Class:
				case CONSTANT_String:
					cp[i] = new byte[] { tag, (byte)in.read(), (byte)in.read() };
					break;
				case CONSTANT_Fieldref:
				case CONSTANT_Methodref:
				case CONSTANT_InterfaceMethodref:
					cp[i] = new byte[] { tag, (byte)in.read(), (byte)in.read(),
											  (byte)in.read(), (byte)in.read() };
					break;
				case CONSTANT_Integer:
				case CONSTANT_Float:
					cp[i] = new byte[] { tag, (byte)in.read(), (byte)in.read(),
											  (byte)in.read(), (byte)in.read() };
					break;
				case CONSTANT_Long:
				case CONSTANT_Double:
					cp[i] = new byte[] { tag, (byte)in.read(), (byte)in.read(),
											  (byte)in.read(), (byte)in.read(),
											  (byte)in.read(), (byte)in.read(),
											  (byte)in.read(), (byte)in.read() };
					cp[++i] = EMPTY;
					break;
				case CONSTANT_NameAndType:
					cp[i] = new byte[] { tag, (byte)in.read(), (byte)in.read(),
											  (byte)in.read(), (byte)in.read() };
					break;
				case CONSTANT_Utf8:
					{
						byte l_h = (byte)in.read();
						byte l_l = (byte)in.read();
						int len = (l_h & 0xFF) << 8 | (l_l & 0xFF);
						cp[i] = new byte[len+3];
						cp[i][0] = tag; cp[i][1] = l_h; cp[i][2] = l_l;
						int off = 3;
						int n;
						while ((n = in.read(cp[i], off, len)) != len) {
							len -= n;
							off += n;
						}
					}
					break;
				default:
					throw new Error("Unknown constant pool tag ("+tag+") at entry "+i+" in "+name);
			}
		}
		return new ArrayList<byte[]>(Arrays.asList(cp));
	}
}
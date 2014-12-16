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

package x10cpp.debug;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.Map;

import polyglot.types.Context;
import polyglot.types.MemberDef;
import polyglot.types.MethodDef;
import polyglot.types.ProcedureDef;
import polyglot.types.Ref;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.QuotedStringTokenizer;
import polyglot.util.StringUtil;
import polyglot.util.CollectionUtil; import x10.util.CollectionFactory;
import x10.util.ClassifiedStream;
import x10cpp.visit.Emitter;

/**
 * Map from generated filename and line to source filename and line.
 * (C++ filename (String), C++ line number range (int, int)) ->
 *     (X10 filename (String), X10 line number (int)).
 * Also stores the method mapping.
 * 
 * @author igor
 */
public class LineNumberMap extends StringTable {
    /** A map key representing a C++ source file/line range combination. */
    private static class Key {
        public final int fileId;
        public final int start_line;
        public final int end_line;
        public Key(int f, int s, int e) {
            fileId = f;
            start_line = s;
            end_line = e;
        }
        public String toString() {
            return fileId + ":" + start_line + "-" + end_line;
        }
        public int hashCode() {
            return fileId + start_line + end_line;
        }
        public boolean equals(Object o) {
            if (getClass() != o.getClass()) return false;
            Key k = (Key) o;
            return fileId == k.fileId && start_line == k.start_line && end_line == k.end_line;
        }
    }
    /** A map entry representing an X10 source file/line combination. */
    private static class Entry {
        public final int fileId;
        public final int line;
        public final int column;
        public Entry(int f, int l, int c) {
            fileId = f;
            line = l;
            column = c;
        }
        public String toString() {
        	return fileId + ":" + line;
        }
    }
    private final Map<Key, Entry> map;
    private static class MethodDescriptor {
        public final int returnType;
        public final int container;
        public final int name;
        public final int[] args;
        public final Key lines;
        public MethodDescriptor(int returnType, int container, int name, int[] args, Key lines) {
            this.returnType = returnType;
            this.container = container;
            this.name = name;
            this.args = args;
            this.lines = lines;
        }
        public boolean equals(Object obj) {
            if (obj == null || obj.getClass() != getClass())
                return false;
            MethodDescriptor md = (MethodDescriptor) obj;
            if (md.name != name || md.container != container ||
                    md.returnType != returnType || md.args.length != args.length)
            {
                return false;
            }
            for (int i = 0; i < args.length; i++) {
                if (args[i] != md.args[i])
                    return false;
            }
            return true;
        }
        public int hashCode() {
            int h = name;
            h = 31*h + container;
            h = 31*h + returnType;
            for (int arg : args) {
                h = 31*h + arg;
            }
            return h;
        }
        public String toString() {
            StringBuilder res = new StringBuilder();
            res.append(returnType).append(" ");
            res.append(container).append(".");
            res.append(name).append("(");
            boolean first = true;
            for (int arg : args) {
                if (!first) res.append(",");
                first = false;
                res.append(arg);
            }
            res.append(")");
            res.append("{").append(lines.fileId);
            res.append(",").append(lines.start_line);
            res.append(",").append(lines.end_line);
            res.append("}");
            return res.toString();
        }
        public static MethodDescriptor parse(String str) {
            StringTokenizer st = new StringTokenizer(str, ". (),", true);
            String s = st.nextToken(" ");
            int r = Integer.parseInt(s);
            s = st.nextToken();
            assert (s.equals(" "));
            s = st.nextToken(".");
            int c = Integer.parseInt(s);
            s = st.nextToken();
            assert (s.equals("."));
            s = st.nextToken("(");
            int n = Integer.parseInt(s);
            s = st.nextToken();
            assert (s.equals("("));
            ArrayList<String> al = new ArrayList<String>(); 
            while (st.hasMoreTokens()) {
                String t = st.nextToken(",)");
                if (t.equals(")"))
                    break;
                al.add(t);
                t = st.nextToken();
                if (t.equals(")"))
                    break;
                assert (t.equals(","));
            }
            int[] a = new int[al.size()];
            for (int i = 0; i < a.length; i++) {
                a[i] = Integer.parseInt(al.get(i));
            }
            s = st.nextToken("{,}");
            assert (s.equals("{"));
            s = st.nextToken();
            int f = Integer.parseInt(s);
            s = st.nextToken();
            int l = Integer.parseInt(s);
            s = st.nextToken();
            int e = Integer.parseInt(s);
            s = st.nextToken();
            assert (s.equals("}"));
            Key k = new Key(f, l, e);
            assert (!st.hasMoreTokens());
            return new MethodDescriptor(r, c, n, a, k);
        }
        public String toPrettyString(LineNumberMap map) {
            return toPrettyString(map, true);
        }
        public String toPrettyString(LineNumberMap map, boolean includeReturnType) {
            StringBuilder res = new StringBuilder();
            if (includeReturnType)
                res.append(map.lookupString(returnType)).append(" ");
            res.append(map.lookupString(container)).append("::");
            res.append(map.lookupString(name)).append("(");
            boolean first = true;
            for (int arg : args) {
                if (!first) res.append(",");
                first = false;
                res.append(map.lookupString(arg));
            }
            res.append(")");
            return res.toString();
        }
    }
    private final Map<MethodDescriptor, MethodDescriptor> methods;

    /**
     */
    public LineNumberMap() {
    	this(new ArrayList<String>());
    }

    private LineNumberMap(ArrayList<String> strings) {
    	super(strings);
        this.map = CollectionFactory.newHashMap();
        this.methods = CollectionFactory.newHashMap();
    }

    /**
     * @param cppFile C++ filename
     * @param startLine C++ start line number
     * @param endLine C++ end line number
     * @param sourceFile X10 filename
     * @param sourceLine X10 line number
     */
    public void put(String cppFile, int startLine, int endLine, String sourceFile, int sourceLine, int sourceColumn) {
        map.put(new Key(stringId(cppFile), startLine, endLine), new Entry(stringId(sourceFile), sourceLine, sourceColumn));
    }

	private MethodDescriptor createMethodDescriptor(String container, String name, String returnType, String[] args, Key l) {
		int c = stringId(container);
		int n = stringId(name);
		int r = stringId(returnType);
		int[] a = new int[args.length];
		for (int i = 0; i < a.length; i++) {
			a[i] = stringId(args[i]);
		}
		return new MethodDescriptor(r, c, n, a, l);
	}

	/**
	 * @param def X10 method or constructor signature
	 * @param cppFile generated file containing the method body
	 * @param startLine first generated line of the method body
	 * @param endLine last generated line of the method body
	 */
	public void addMethodMapping(MemberDef def, String cppFile, int startLine, int endLine, int lastX10Line) {
	    Key tk = new Key(stringId(cppFile), startLine, endLine);
	    Type container = def.container().get();
	    assert (def instanceof ProcedureDef);
	    String name = (def instanceof MethodDef) ? ((MethodDef) def).name().toString() : null;
	    Type returnType = (def instanceof MethodDef) ? ((MethodDef) def).returnType().get() : null;
	    List<Ref<? extends Type>> formalTypes = ((ProcedureDef) def).formalTypes();
	    addMethodMapping(container, name, returnType, formalTypes, tk, lastX10Line);
	}

	private void addMethodMapping(Type c, String n, Type r, List<Ref<? extends Type>> f, Key tk, int lastX10Line) {
		assert (c != null);
		assert (f != null);
		String sc = c.toString().replace("$", "::");
		String sn = n == null ? TypeSystem.CONSTRUCTOR_NAME : n;
		String sr = r == null ? "" : r.toString();
		String[] sa = new String[f.size()];
		for (int i = 0; i < sa.length; i++) {
			sa[i] = f.get(i).get().toString();
		}
		MethodDescriptor src = createMethodDescriptor(sc, sn, sr, sa, new Key(-1, -1, lastX10Line));
		String tc = Emitter.translateType(c);
		String tn = n == null ? "_constructor" : Emitter.mangled_method_name(n);
		String tr = r == null ? "void" : Emitter.translateType(r, true);
		String[] ta = new String[sa.length];
		for (int i = 0; i < ta.length; i++) {
			ta[i] = Emitter.translateType(f.get(i).get(), true);
		}
		MethodDescriptor tgt = createMethodDescriptor(tc, tn, tr, ta, tk);
		// TODO: This line below causes the X10 standard library to fail to compile with the -DEBUG flag.  Why?
//		assert (methods.get(tgt) == null); 
		methods.put(tgt, src);
	}
	
	private class LocalVariableMapInfo
	{
		int _x10name;			// Index of the X10 variable name in _X10strings
		int _x10type;          // Classification of this type
		int _x10typeIndex; 	// Index of the X10 type into appropriate _X10ClassMap, _X10ClosureMap  (if applicable)
		int _cppName;			// Index of the C++ variable name in _X10strings
	    String _x10index;         // Index of X10 file name in _X10sourceList
		int _x10startLine;     // First line number of X10 line range
		int _x10endLine;       // Last line number of X10 line range
	}
	
	private class MemberVariableMapInfo
	{
		int _x10type;       // Classification of this type
		int _x10typeIndex;  // Index of the X10 type into appropriate _X10typeMap
		int _x10memberName; // Index of the X10 member name in _X10strings
		int _cppMemberName; // Index of the C++ member name in _X10strings
		int _cppClass; // Index of the C++ containing struct/class name in _X10strings
	}
	
	private class ClassMapInfo
	{
		int _x10startLine;     // First line number of X10 line range
		int _x10endLine;       // Last line number of X10 line range
		int _type;			   // used to store the wrapping type id
		String _sizeOfArg;
		String _file;
		ArrayList<MemberVariableMapInfo> _members;
	}
	
	private class LoopVariable
	{
		String realName;
		int startLine;
		int endLine;
	}
	
	private LinkedHashMap<String, ArrayList<LoopVariable>> loopVariables;
	private ArrayList<Integer> arrayMap = new ArrayList<Integer>();
	//private ArrayList<Integer> refMap = new ArrayList<Integer>();
	private ArrayList<LocalVariableMapInfo> localVariables;
	private LinkedHashMap<Integer, ClassMapInfo> memberVariables;
	private LinkedHashMap<Integer, ClassMapInfo> referenceMembers;
	private LinkedHashMap<Integer, ClassMapInfo> closureMembers;	
	
	// the type numbers were provided by Steve Cooper in "x10dbg_types.h"
	static int determineTypeId(String type)
	{
		if (type == null)
			return 0;
		if (type.equals("x10.lang.Int") || type.startsWith("x10.lang.Int{"))
			return 6;
		if (type.startsWith("x10.regionarray.Array"))
			return 200;
		if (type.startsWith("x10.lang.PlaceLocalHandle"))
			return 203;
		if (type.equals("x10.lang.Boolean") || type.startsWith("x10.lang.Boolean{"))
			return 1;
		if (type.equals("x10.lang.Byte") || type.startsWith("x10.lang.Byte{"))
			return 2;
		if (type.equals("x10.lang.Char") || type.startsWith("x10.lang.Char{"))
			return 3;
		if (type.equals("x10.lang.Double") || type.startsWith("x10.lang.Double{"))
			return 4;
		if (type.equals("x10.lang.Float") || type.startsWith("x10.lang.Float{"))
			return 5;
		if (type.equals("x10.lang.Long") || type.startsWith("x10.lang.Long{"))
			return 7;
		if (type.equals("x10.lang.Short") || type.startsWith("x10.lang.Short{"))
			return 8;		
		if (type.equals("x10.lang.UByte") || type.startsWith("x10.lang.UByte{"))
			return 9;
		if (type.equals("x10.lang.UInt") || type.startsWith("x10.lang.UInt{"))
			return 10;
		if (type.equals("x10.lang.ULong") || type.startsWith("x10.lang.ULong{"))
			return 11;
		if (type.equals("x10.lang.UShort") || type.startsWith("x10.lang.UShort{"))
			return 12;
		if (type.startsWith("x10.regionarray.DistArray"))
			return 202;
		if (type.startsWith("x10.regionarray.Dist"))
			return 201;
		if (type.startsWith("x10.lang.Place"))
			return 204;
		if (type.startsWith("x10.util.Random"))
			return 205;
		if (type.startsWith("x10.lang.String"))
			return 206;
		// 207 = valrail
		if (type.startsWith("x10.lang.Point"))
			return 208;
		if (type.startsWith("x10.lang.Any"))
			return 209;
		if (type.startsWith("x10.lang.GlobalRef"))
			return 210;
		if (type.startsWith("x10.lang.IntRange"))
			return 212;
		if (type.startsWith("x10.lang.LongRange"))
			return 213;
		if (type.startsWith("x10.regionarray.Region"))
			return 300;
		if (type.contains("_closure_"))
			return 100;
		return 101; // generic class
		// type 102 = generic structure
		// type 103 = generic base class
	}
	
	static String getInnerType(String type)
	{
		int bracketStart = type.indexOf('[');
		int bracketEnd = type.lastIndexOf(']');
		if (bracketStart != -1 && bracketEnd != -1)
			return type.substring(bracketStart+1, bracketEnd);
		else
			return null;
	}
	
	int determineSubtypeId(String type, ArrayList<Integer> list)
	{
		String subtype = getInnerType(type);
		if (subtype != null)
		{
			int subtypeId = determineTypeId(subtype);			
			int position = list.size();
			list.add(subtypeId);
			
			int innerType = determineSubtypeId(subtype, list);
			if (subtypeId == 101 && innerType == -1) // this may be a locally defined class.  Remember the whole name.
				innerType = stringId(Emitter.mangled_non_method_name(subtype));
			list.add(innerType);
			return position/2;
		}
		else 
			return -1;
	}
	
	public int addReferenceMap(String name, String type, int startline, int endline, int refType)
	{
		if (type.contains("$Anonymous$") || type.equals("x10.lang.PlaceLocalHandle[T]")) // can't properly account for these types
			return -1;
		if (referenceMembers == null)
			referenceMembers = new LinkedHashMap<Integer, ClassMapInfo>();
				
		int id = stringId(name);
		ClassMapInfo cm = referenceMembers.get(id);
		if (cm == null)
		{
			cm = new ClassMapInfo();			
			cm._members = new ArrayList<LineNumberMap.MemberVariableMapInfo>();
			if (refType == 211)
				cm._type = 203; // special case
			else
				cm._type = refType;
			cm._sizeOfArg = type.replace(".", "::").replace('[', '<').replace("]", " >").replace("$", "__");
			int properties = cm._sizeOfArg.indexOf('{');
			if (properties > -1)
			{
				String end;
				while (properties > -1)
				{
					end = cm._sizeOfArg.substring(cm._sizeOfArg.indexOf('}')+1);
					cm._sizeOfArg = cm._sizeOfArg.substring(0, properties);
					cm._sizeOfArg = cm._sizeOfArg.concat(end);
					properties = cm._sizeOfArg.indexOf('{');
				}
			}
			int comma = cm._sizeOfArg.indexOf(',');
			if (comma > -1) // multiple arguments
			{
				// this has template arguments.  Add in the TPMGL stuff
				int start = cm._sizeOfArg.indexOf('<');
				while (start < comma)
				{
					int nextStart = cm._sizeOfArg.indexOf('<', start+1);
					if (nextStart < comma && nextStart != -1)
						start = nextStart;
					else
						break;
				}
				String temp = cm._sizeOfArg.substring(start+1).replace(",", "), class TPMGL(").replaceFirst(">", ")>");
				cm._sizeOfArg = cm._sizeOfArg.substring(0, start+1).concat("class TPMGL(").concat(temp);
			}
			else // single argument
			{
				int argstart = cm._sizeOfArg.lastIndexOf('<');
				if (argstart > 0)					
				{					
					int argend = cm._sizeOfArg.indexOf('>');
					if (cm._sizeOfArg.substring(argstart, argend).indexOf(':') == -1)
					{
						String temp = cm._sizeOfArg.substring(0, argstart+1) + "class TPMGL(";
						temp = temp + cm._sizeOfArg.substring(argstart+1, argend) + ")";
						cm._sizeOfArg = temp + cm._sizeOfArg.substring(argend);
					}
				}
			}
			referenceMembers.put(id, cm);
			int returnValue = referenceMembers.size()-1;
			
			String innerType = getInnerType(type);
			MemberVariableMapInfo v = new MemberVariableMapInfo();
			if (refType == 211)
			{
				v._x10type = 211;
				innerType = getInnerType(type);
			}
			else
				v._x10type = determineTypeId(innerType);
			if (v._x10type == 200 || v._x10type == 202 || v._x10type == 204 || v._x10type == 207 || v._x10type == 211)
				v._x10typeIndex = determineSubtypeId(innerType, arrayMap);
			else
				v._x10typeIndex = -1;
			if (refType == 210) // Debug team wants the target's name, not the variable name, for GlobalRefs
			{
				int nameStart=type.indexOf("self==");
				if (nameStart == -1)
					v._x10memberName = id;
				else
				{
					nameStart+=6;
					int nameEnd = type.indexOf(',', nameStart);
					if (nameEnd == -1) 
						nameEnd = type.indexOf('}', nameStart);
					if (nameEnd == -1)
						v._x10memberName = id;
					else
						v._x10memberName = stringId(type.substring(nameStart, nameEnd));
				}
				v._cppMemberName = v._x10memberName;
			}
			else if (refType == 202) // create additional maps for internal components of DistArray.
			{				
				v._x10typeIndex = addReferenceMap(name+"_localHandle", "x10.lang.PlaceLocalHandle[x10.regionarray.DistArray__LocalState["+innerType+"]]", startline, endline, 211);
				// I hate that this is here.  It's just a hardcoded representation of the internals of DistArray.  
				// It does not belong here, but the debugger people can't seem to work without it.
				// we add something called "dist" here, and the main "v" entry is "localHandle"
				MemberVariableMapInfo dist = new MemberVariableMapInfo();
				dist._x10type = 201;
				dist._x10typeIndex = -1;
				dist._x10memberName = stringId("dist");
				dist._cppMemberName = stringId("x10__dist");
				dist._cppClass = stringId("x10::array::Dist");
				cm._members.add(dist);
				
				v._x10type = 203;
				v._x10memberName = stringId("localHandle");
				v._cppMemberName = stringId("x10__localHandle");
			}
			else if (refType == 211)
			{
				v._x10memberName = id;
				v._cppMemberName = stringId("x10__localStorage");
			}
			else
			{
				v._x10memberName = id;
				v._cppMemberName = v._x10memberName;
			}
			v._cppClass = stringId(cm._sizeOfArg);
			cm._members.add(v);
			return returnValue;
		}
		
		int index = 0;
		for (int key : referenceMembers.keySet())
		{
			if (id == key)
				return index;
			else
				index++;
		}
		// should never reach here
		return -1;
	}
	
	public void rememberLoopVariable(String declaredName, String realName, int startLine, int endLine)
	{
		if (loopVariables == null)
			loopVariables = new LinkedHashMap<String, ArrayList<LoopVariable>>();
		ArrayList<LoopVariable> list = loopVariables.get(declaredName);
		if (list == null)
		{
			list = new ArrayList<LineNumberMap.LoopVariable>();
			loopVariables.put(declaredName, list);
		}
		
		LoopVariable lv = new LoopVariable();
		lv.realName = realName;
		lv.startLine = startLine;
		lv.endLine = endLine;
		list.add(lv);
	}
	
	public void addLocalVariableMapping(String name, String type, int startline, int endline, String file, boolean noMangle, int closureIndex, boolean isStruct)
	{
		//if (name == null || name.startsWith(Context.MAGIC_VAR_PREFIX))
		if (name == null || name.contains("$"))
			return; // skip variables with compiler-generated names.
		
		if (localVariables == null)
			localVariables = new ArrayList<LineNumberMap.LocalVariableMapInfo>();
		
		LocalVariableMapInfo v = new LocalVariableMapInfo();
		v._x10name = stringId(name);
		v._x10type = determineTypeId(type);
		if (v._x10type == 203 || v._x10type == 210 || v._x10type == 202)
			v._x10typeIndex = addReferenceMap(name, type, startline, endline, v._x10type);
		else if (v._x10type == 200 || v._x10type == 204 || v._x10type == 207)
			v._x10typeIndex = determineSubtypeId(type, arrayMap);
		else if (v._x10type == 101)
		{
			int b = type.indexOf('{');
			if (b == -1)
				v._x10typeIndex = stringId(Emitter.mangled_non_method_name(type));
			else
				v._x10typeIndex = stringId(Emitter.mangled_non_method_name(type.substring(0, b)));
			if (isStruct)
				v._x10type = 102;
		}
		else if (v._x10type == 100 || closureIndex == -2)
			v._x10typeIndex = closureIndex;
		else 
			v._x10typeIndex = -1;
		if (noMangle)
			v._cppName = v._x10name;
		else
			v._cppName = stringId(Emitter.mangled_non_method_name(name));
		v._x10index = file;
		v._x10startLine = startline;
		v._x10endLine = endline;
		
		// prevent duplicates
		for (LocalVariableMapInfo existing : localVariables)
		{
			if (existing._x10name == v._x10name && existing._cppName == v._cppName && existing._x10index.equals(v._x10index)
					 && existing._x10startLine == v._x10startLine && existing._x10endLine == v._x10endLine)
			{
				if ((existing._x10type == v._x10type && existing._x10typeIndex == v._x10typeIndex) || v._x10type == 209)
					return; // exact duplicate, or less specific type
				else if (existing._x10type == 209)
				{	// replace "Any" with the more specific type
					existing._x10type = v._x10type;
					existing._x10typeIndex = v._x10typeIndex; 
					return;
				}
			}
		}
		// convert loop indexes
		if (v._x10type < 100 && loopVariables != null && loopVariables.containsKey(name))
		{
			ArrayList<LoopVariable> list = loopVariables.get(name);
			for (LoopVariable lv : list)
			{
				if (lv.startLine == v._x10startLine && lv.endLine == v._x10endLine)
				{
					v._cppName = stringId(lv.realName);
					break;
				}
			}
		}
		localVariables.add(v);
	}
	
	public void addClassMemberVariable(String name, String type, String containingClass, boolean isStruct, boolean isConstructorArg, boolean isSuper)
	{
		if (containingClass.indexOf('{') != -1 || containingClass.indexOf("[") != -1) // skip these - the compiler didn't flag them properly
			return;

		if (memberVariables == null)
			memberVariables = new LinkedHashMap<Integer, ClassMapInfo>();
		ClassMapInfo cm = memberVariables.get(stringId(containingClass));
		if (cm == null)
		{
			cm = new ClassMapInfo();
			cm._members = new ArrayList<LineNumberMap.MemberVariableMapInfo>();
			if (isStruct)
				cm._type = 102;
			else
				cm._type = 101;
			memberVariables.put(stringId(containingClass), cm);
		}
		if (name == null) return; // special case for classes without members
		
		MemberVariableMapInfo v = new MemberVariableMapInfo();
		v._x10type = determineTypeId(type);
		if (v._x10type == 101)
		{
			int b = type.indexOf('{');
			if (b == -1)
				v._x10typeIndex = stringId(Emitter.mangled_non_method_name(type));
			else
				v._x10typeIndex = stringId(Emitter.mangled_non_method_name(type.substring(0, b)));
			if (isStruct)
				v._x10type = 102;
		}
		else if (v._x10type == 203 || v._x10type == 210 || v._x10type == 202)
			v._x10typeIndex = addReferenceMap(name, type, 0, 0, v._x10type);
		else if (v._x10type == 200 || v._x10type == 204 || v._x10type == 207)
			v._x10typeIndex = determineSubtypeId(type, arrayMap);
		else 
			v._x10typeIndex = -1;
		if (isConstructorArg)
			v._x10memberName = stringId("{...}");
		else
			v._x10memberName = stringId(name);
		
		if (isSuper)
		{
			v._x10type = 103;
			v._cppMemberName = stringId(Emitter.mangled_non_method_name(name));
		}
		else
			v._cppMemberName = stringId("x10__"+Emitter.mangled_non_method_name(name));
		v._cppClass = stringId(Emitter.translateFQN(containingClass));
		cm._members.add(v);
	}
	
	public void addClosureMember(String name, String type, String containingClass, String containerWithTemplateArgs, String file, int startLine, int endLine)
	{
		if (closureMembers == null)
			closureMembers = new LinkedHashMap<Integer, ClassMapInfo>();
		ClassMapInfo cm = closureMembers.get(stringId(containingClass));
		if (cm == null)
		{
			addLocalVariableMapping("this", containingClass, startLine, endLine, file, true, closureMembers.size(), false);
			cm = new ClassMapInfo();			
			cm._members = new ArrayList<LineNumberMap.MemberVariableMapInfo>();
			cm._sizeOfArg = containerWithTemplateArgs.replace("TPMGL(", " class TPMGL(");
			cm._type = 100; // all closures are type 100
			cm._file = file;
			closureMembers.put(stringId(containingClass), cm);
		}
		
		MemberVariableMapInfo v = new MemberVariableMapInfo();
		v._x10type = determineTypeId(type);
		if (v._x10type == 203 || v._x10type == 210 || v._x10type == 202)
			v._x10typeIndex = addReferenceMap(name, type, startLine, endLine, v._x10type);
		else if (v._x10type == 200 || v._x10type == 204 || v._x10type == 207)
			v._x10typeIndex = determineSubtypeId(type, arrayMap);
		else if (v._x10type == 101) // save the type for later - it may be a class in our class table
			v._x10typeIndex = stringId(Emitter.mangled_non_method_name(type));
		else
			v._x10typeIndex = -1;
		v._x10memberName = stringId(name);
		v._cppMemberName = stringId(Emitter.mangled_non_method_name(name));
		v._cppClass = stringId(containingClass);

		// prevent duplicates
		for (MemberVariableMapInfo existing : cm._members)
		{
			if (existing._x10memberName == v._x10memberName && existing._cppMemberName == v._cppMemberName && existing._cppClass == v._cppClass)
			{
				if ((existing._x10type == v._x10type && existing._x10typeIndex == v._x10typeIndex) || v._x10type == 209)
					return; // exact duplicate, or less specific type
				else if (existing._x10type == 209)
				{	// replace "Any" with the more specific type
					existing._x10type = v._x10type;
					existing._x10typeIndex = v._x10typeIndex; 
					cm._x10startLine = startLine;
					cm._x10endLine = endLine;
					return;
				}
			}
		}
		cm._x10startLine = startLine;
		cm._x10endLine = endLine;
		cm._members.add(v);
	}

	/**
	 * @param method target method signature
	 * @return source method signature
	 */
	public String getMappedMethod(String method) {
		for (MethodDescriptor m : methods.keySet()) {
			String mm = m.toPrettyString(this, false);
			if (mm.equals(method))
				return methods.get(m).toPrettyString(this, false);
		}
		return null;
	}

    public String toString() {
        final StringBuilder sb = new StringBuilder();
        for (Key pos : map.keySet()) {
            Entry entry = map.get(pos);
            sb.append(lookupString(pos.fileId)).append(":").append(pos.start_line).append("->");
            sb.append(lookupString(entry.fileId)).append(":").append(entry.line).append("\n");
        }
        sb.append("\n");
        for (MethodDescriptor md : methods.keySet()) {
			MethodDescriptor sm = methods.get(md);
			sb.append(md.toPrettyString(this, true)).append("->");
			sb.append(sm.toPrettyString(this, true)).append("\n");
		}
        return sb.toString();
    }

    /**
     * Is the map empty?
     * @return true if the map is empty.
     */
    public boolean isEmpty() {
        return map.isEmpty();
    }

    /**
     * Produces a string suitable for initializing a field in the generated file.
     * The resulting string can be parsed by {@link #importMap(String)}.
     */
    public String exportMap() {
        final StringBuilder sb = new StringBuilder();
        sb.append("F");
        exportStringMap(sb);
        sb.append(" L{");
        for (Key pos : map.keySet()) {
            Entry entry = map.get(pos);
            sb.append(pos.fileId).append(":");
            sb.append(pos.start_line).append("-").append(pos.end_line).append("->");
            sb.append(entry.fileId).append(":").append(entry.line).append(",");
        }
        sb.append("} M{");
        for (MethodDescriptor md : methods.keySet()) {
			MethodDescriptor sm = methods.get(md);
			sb.append(md.toString()).append("->").append(sm.toString()).append(";");
		}
        sb.append("}");
        return sb.toString();
    }

    /**
     * Parses a string into a {@link LineNumberMap}.
     * The string is produced by {@link #exportMap()}.
     * @param input the input string
     */
    public static LineNumberMap importMap(String input) {
        StringTokenizer st = new QuotedStringTokenizer(input, " ", "\"\'", '\\', true);
        String s = st.nextToken("{}");
        assert (s.equals("F"));
        ArrayList<String> strings = importStringMap(st);
        LineNumberMap res = new LineNumberMap(strings);
        if (!st.hasMoreTokens())
        	return res;
        s = st.nextToken("{}");
        assert (s.equals(" L"));
        s = st.nextToken();
        assert (s.equals("{"));
        while (st.hasMoreTokens()) {
            String t = st.nextToken(":}");
            if (t.equals("}"))
                break;
            int n = Integer.parseInt(t);
            t = st.nextToken();
            assert (t.equals(":"));
            int i = Integer.parseInt(st.nextToken("-"));
            t = st.nextToken();
            assert (t.equals("-"));
            int e = Integer.parseInt(st.nextToken("-"));
            t = st.nextToken(">");
            assert (t.equals("-"));
            t = st.nextToken(">");
            assert (t.equals(">"));
            int f = Integer.parseInt(st.nextToken(":"));
            t = st.nextToken();
            assert (t.equals(":"));
            int l = Integer.parseInt(st.nextToken(","));
            res.map.put(new Key(n, i, e), new Entry(f, l, -1));
            t = st.nextToken();
            assert (t.equals(","));
        }
        if (!st.hasMoreTokens())
        	return res;
        s = st.nextToken("{}");
        assert (s.equals(" M"));
        s = st.nextToken();
        assert (s.equals("{"));
        while (st.hasMoreTokens()) {
            String t = st.nextToken("-}");
            if (t.equals("}"))
                break;
            MethodDescriptor md = MethodDescriptor.parse(t);
            t = st.nextToken(">");
            assert (t.equals("-"));
            t = st.nextToken(">");
            assert (t.equals(">"));
            MethodDescriptor sm = MethodDescriptor.parse(st.nextToken(";"));
            res.methods.put(md, sm);
            t = st.nextToken();
            assert (t.equals(";"));
        }
        assert (!st.hasMoreTokens());
        return res;
    }

	/**
	 * Merges a set of new entries into a given map.  Changes the map in place!
	 * @param map the target map (changed in place!)
	 * @param newEntries the set of new entries
	 */
	private static void mergeMap(LineNumberMap map, LineNumberMap newEntries) {
		assert (map != null);
		final LineNumberMap m = map;
		final LineNumberMap n = newEntries;
		for (Key p : n.map.keySet()) {
//			assert (!m.map.containsKey(p));
			Entry e = n.map.get(p);
			m.put(n.lookupString(p.fileId), p.start_line, p.end_line, n.lookupString(e.fileId), e.line, e.column);
		}
		for (MethodDescriptor d : n.methods.keySet()) {
//		    if (m.methods.containsKey(d))
//		        assert (false) : d.toPrettyString(n)+" already present";
			assert (!m.methods.containsKey(d)) : d.toPrettyString(n)+" already present";
			MethodDescriptor e = n.methods.get(d);
			assert (e.lines == null);
			Key dk = new Key(m.stringId(n.lookupString(d.lines.fileId)), d.lines.start_line, d.lines.end_line);
			MethodDescriptor dp = m.createMethodDescriptor(n.lookupString(d.container),
					n.lookupString(d.name), n.lookupString(d.returnType), n.lookupStrings(d.args), dk);
			MethodDescriptor ep = m.createMethodDescriptor(n.lookupString(e.container),
					n.lookupString(e.name), n.lookupString(e.returnType), n.lookupStrings(e.args), null);
			m.methods.put(dp, ep);
		}
	}

	/**
	 * Merges a set of maps into a new map.  Returns the new map.
	 * @param maps the set of maps to merge
	 * @return the newly-created map containing merged entries from maps
	 */
	public static LineNumberMap mergeMaps(LineNumberMap[] maps) {
	    LineNumberMap map = new LineNumberMap();
	    for (int i = 0; i < maps.length; i++) {
	        mergeMap(map, maps[i]);
	    }
	    return map;
	}

	private String[] allFiles() {
	    TreeSet<String> files = new TreeSet<String>();
	    for (Entry e : map.values()) {
	        files.add(lookupString(e.fileId));
	    }
	    return files.toArray(new String[files.size()]);
	}

	private static int findFile(String name, String[] files) {
	    // files is assumed sorted alphanumerically
	    return Arrays.binarySearch(files, name);
	}

	private static class CPPLineInfo {
	    public final int x10index;
	    public final int x10method;
	    public final int cppindex;
	    public final int x10line;
	    public final int x10column;
	    public final int cppfromline;
	    public final int cpptoline;
	    public final int fileId;
	    public CPPLineInfo(int x10index, int x10method, int cppindex, int x10line, int cppfromline, int cpptoline, int fileId, int x10column) {
	        this.x10index = x10index;
	        this.x10method = x10method;
	        this.cppindex = cppindex;
	        this.x10line = x10line;
	        this.x10column = x10column;
	        this.cppfromline = cppfromline;
	        this.cpptoline = cpptoline;
	        this.fileId = fileId;
	    }
	    public static Comparator<CPPLineInfo> byX10info() {
	        return new Comparator<CPPLineInfo>() {
	            public int compare(CPPLineInfo o1, CPPLineInfo o2) {
	                int index_d = o1.x10index - o2.x10index;
	                if (index_d != 0) return index_d;
	                return o1.x10line - o2.x10line;
	            }
	        };
	    }
	    public static Comparator<CPPLineInfo> byCPPinfo() {
	        return new Comparator<CPPLineInfo>() {
	            public int compare(CPPLineInfo o1, CPPLineInfo o2) {
	                int index_d = o1.cppindex - o2.cppindex;
	                if (index_d != 0) return index_d;
	                return o1.cppfromline - o2.cppfromline;
	            }
	        };
	    }
	}

	private class CPPMethodInfo implements Comparable<CPPMethodInfo> {
	    public final int x10class;
	    public final int x10method;
	    public final int x10rettype;
	    public final int[] x10args;
	    public final int cppclass;
	    public int cpplineindex;
	    public int lastX10Line;
	    public CPPMethodInfo(int x10class, int x10method, int x10rettype, int[] x10args, int cppclass, int lastX10Line) {
	        this.x10class = x10class;
	        this.x10method = x10method;
	        this.x10rettype = x10rettype;
	        this.x10args = x10args;
	        this.cppclass = cppclass;
	        this.lastX10Line = lastX10Line;
	    }
	    private String concatArgs() {
	        StringBuilder sb = new StringBuilder();
	        for (int i = 0; i < x10args.length; i++) {
	            sb.append(lookupString(x10args[i]));
	        }
	        return sb.toString();
	    }
	    public int compareTo(CPPMethodInfo o) {
	        int class_d = lookupString(x10class).compareTo(lookupString(o.x10class));
	        if (class_d != 0) return class_d;
	        int name_d = lookupString(x10method).compareTo(lookupString(o.x10method));
	        if (name_d != 0) return name_d;
	        return concatArgs().compareTo(o.concatArgs());
	    }
	}

	/**
	 * Generates code for the line number map as required by the Toronto C++
	 * Debugger backend into the specified stream.
	 * @param w the output stream
	 * @param m the map to export
	 */
	public void exportForCPPDebugger(ClassifiedStream w, LineNumberMap m) {
	    String debugSectionAttr = "__attribute__((_X10_DEBUG_SECTION))";
	    String debugDataSectionAttr = "__attribute__((_X10_DEBUG_DATA_SECTION))";
	    int size = m.size();
	    int offset = 0;
	    int[] offsets = new int[size];
	    // All strings, concatenated, with intervening nulls.
	    w.writeln("static const char _X10strings[] __attribute__((used)) "+debugDataSectionAttr+" =");
	    for (int i = 0; i < size; i++) {
	        offsets[i] = offset;
	        String s = m.lookupString(i);
	        w.writeln("    \""+StringUtil.escape(s)+"\\0\" //"+offsets[i]);
	        offset += s.length()+1;
	    }
	    w.writeln("    \"\";");
	    w.forceNewline();

        if (!m.isEmpty()) {
		    String[] files = m.allFiles();
		    // A list of X10 source files that contributed to the generation of the current C++ file.
		    w.writeln("static const struct _X10sourceFile _X10sourceList[] __attribute__((used)) "+debugDataSectionAttr+" = {");
		    for (int i = 0; i < files.length; i++) {
		        w.write("    { ");
		        w.write(""+0+", ");                                                // FIXME: _numLines
		        w.write(""+offsets[m.stringId(files[i])]);                         // _stringIndex
		        w.writeln(" },");
		    }
		    w.writeln("};");
		    w.forceNewline();
	
	//	    // A cross reference of X10 statements to the first C++ statement.
	//	    // Sorted by X10 file index and X10 source file line.
		    ArrayList<CPPLineInfo> x10toCPPlist = new ArrayList<CPPLineInfo>(m.map.size());
		    for (Key p : m.map.keySet()) {
		        Entry e = m.map.get(p);
		        x10toCPPlist.add(
		                new CPPLineInfo(findFile(m.lookupString(e.fileId), files), // _X10index
		                                0,                                         // FIXME: _X10method
		                                offsets[p.fileId],                         // _CPPindex
		                                e.line,                                    // _X10line
		                                p.start_line,                              // _CPPline
		                                p.end_line,
		                                p.fileId,
		                                e.column));                                  // _X10column	                                
		    }
		    Collections.sort(x10toCPPlist, CPPLineInfo.byX10info());
		    
		    // remove items that have duplicate lines, leaving only the one with the earlier column
		    CPPLineInfo previousCppDebugInfo = x10toCPPlist.get(0);
		    for (int i=1; i<x10toCPPlist.size();)
		    {
		    	CPPLineInfo cppDebugInfo = x10toCPPlist.get(i);
		    	if (cppDebugInfo.x10line == previousCppDebugInfo.x10line)
		    	{
		    		if (cppDebugInfo.x10column > previousCppDebugInfo.x10column)
			    		x10toCPPlist.remove(i); // keep the previous one, delete this one
		    		else
		    		{
		    			// keep this one, delete the previous one
		    			x10toCPPlist.remove(i-1);
		    			previousCppDebugInfo = cppDebugInfo;
		    		}
		    	}
		    	else
		    	{
		    		previousCppDebugInfo = cppDebugInfo;
			    	i++;
		    	}
		    }	

		    w.writeln("static const struct _X10toCPPxref _X10toCPPlist[] __attribute__((used)) "+debugDataSectionAttr+" = {");
		    for (CPPLineInfo cppDebugInfo : x10toCPPlist) {
		        w.write("    { ");
		        w.write(""+cppDebugInfo.x10index+", ");                            // _X10index
//		        w.write(""+cppDebugInfo.x10method+", ");                           // _X10method
		        w.write(""+cppDebugInfo.cppindex+", ");                            // _CPPindex
		        w.write(""+cppDebugInfo.x10line+", ");                             // _X10line
		        w.write(""+cppDebugInfo.cppfromline+", ");                         // _CPPFromLine
		        w.write(""+cppDebugInfo.cpptoline);                                // _CPPtoLine
		        w.writeln(" },");
		    }
		    w.writeln("};");
		    w.forceNewline();
	
		    // A list of the X10 method names.
		    // Sorted by X10 method name.
		    ArrayList<CPPMethodInfo> x10MethodList = new ArrayList<CPPMethodInfo>(m.methods.size());
		    Map<Key, CPPMethodInfo> keyToMethod = CollectionFactory.newHashMap();
		    for (MethodDescriptor md : m.methods.keySet()) {
		        MethodDescriptor sm = m.methods.get(md);
		        final CPPMethodInfo cmi = m.new CPPMethodInfo(sm.container,        // _x10class
		                                                      sm.name,             // _x10method
		                                                      sm.returnType,       // _x10returnType
		                                                      sm.args,             // _x10args
		                                                      md.container,        // _cppClass
		                                                      sm.lines.end_line);  // _lastX10Line
		        x10MethodList.add(cmi);
		        keyToMethod.put(md.lines, cmi);
		    }
	
		    // A cross reference of C++ statements to X10 statements.
		    // Sorted by C++ file index and C++ source file line. 
		    // A line range is used to minimize the storage required.
		    ArrayList<CPPLineInfo> cpptoX10xrefList = new ArrayList<CPPLineInfo>(m.map.size());
		    for (Key p : m.map.keySet()) {
		        Entry e = m.map.get(p);
		        cpptoX10xrefList.add(
		                new CPPLineInfo(findFile(m.lookupString(e.fileId), files), // _X10index
		                                0,                                         // FIXME: _X10method
		                                offsets[p.fileId],                         // _CPPindex
		                                e.line,                                    // _X10line
		                                p.start_line,                              // _CPPfromline
		                                p.end_line,                                // _CPPtoline
		                                p.fileId,
		                                e.column));
		    }
		    Collections.sort(cpptoX10xrefList, CPPLineInfo.byCPPinfo());
		    w.writeln("static const struct _CPPtoX10xref _CPPtoX10xrefList[] __attribute__((used)) "+debugDataSectionAttr+" = {");
		    int i = 0;
		    for (CPPLineInfo cppDebugInfo : cpptoX10xrefList) {
		        w.write("    { ");
		        w.write(""+cppDebugInfo.x10index+", ");                            // _X10index
//		        w.write(""+cppDebugInfo.x10method+", ");                           // _X10method
		        w.write(""+cppDebugInfo.cppindex+", ");                            // _CPPindex
		        w.write(""+cppDebugInfo.x10line+", ");                             // _X10line
		        w.write(""+cppDebugInfo.cppfromline+", ");                         // _CPPfromline
		        w.write(""+cppDebugInfo.cpptoline);                                // _CPPtoline
		        w.writeln(" },");
		        Key k = new Key(cppDebugInfo.fileId, cppDebugInfo.cppfromline, cppDebugInfo.cpptoline);
		        CPPMethodInfo methodInfo = keyToMethod.get(k);
		        if (methodInfo != null) {
		            methodInfo.cpplineindex = i;                                   // _lineIndex
		        }
		        i++;
		    }
		    w.writeln("};");
		    w.forceNewline();
	
	        if (!m.methods.isEmpty()) {
		        Collections.sort(x10MethodList);
		        // FIXME: Cannot put _X10methodNameList in debugDataSectionAttr, because it's not constant
		        // (the strings cause static initialization for some reason)
		        w.writeln("static const struct _X10methodName _X10methodNameList[] __attribute__((used)) = {");
		        w.writeln("#if defined(__xlC__)");
		        for (CPPMethodInfo cppMethodInfo : x10MethodList) {        	
		            w.write("    { ");
		            w.write(""+offsets[cppMethodInfo.x10class]+", ");                  // _x10class
		            w.write(""+offsets[cppMethodInfo.x10method]+", ");                 // _x10method
		            w.write(""+offsets[cppMethodInfo.x10rettype]+", ");                // _x10returnType
		            w.write(""+offsets[cppMethodInfo.cppclass]+", ");                  // _cppClass
		            w.write("(uint64_t) 0, "); // TODO - this needs to be re-designed, with the debugger team            
		            w.write(""+cppMethodInfo.x10args.length+", ");                     // _x10argCount
		            w.write(""+cppMethodInfo.cpplineindex+", ");                       // _lineIndex
		            w.write(""+cppMethodInfo.lastX10Line);                             // _lastX10Line
		            w.writeln(" }, // "+m.lookupString(cppMethodInfo.x10class)+'.'+m.lookupString(cppMethodInfo.x10method)+"()");
		        }
		        w.writeln("#else");
		        for (CPPMethodInfo cppMethodInfo : x10MethodList) {        	
		            w.write("    { ");
		            w.write(""+offsets[cppMethodInfo.x10class]+", ");                  // _x10class
		            w.write(""+offsets[cppMethodInfo.x10method]+", ");                 // _x10method
		            w.write(""+offsets[cppMethodInfo.x10rettype]+", ");                // _x10returnType
		            w.write(""+offsets[cppMethodInfo.cppclass]+", ");                  // _cppClass
		            w.write("(uint64_t) ");
		            for (i = 0; i < cppMethodInfo.x10args.length; i++) {
		                int a = cppMethodInfo.x10args[i];
		                w.write("\""+encodeIntAsChars(offsets[a])+"\" ");              // _x10args
		            }
		            w.write("\"\", ");
		            w.write(""+cppMethodInfo.x10args.length+", ");                     // _x10argCount
		            w.write(""+cppMethodInfo.cpplineindex+", ");                       // _lineIndex
		            w.write(""+cppMethodInfo.lastX10Line);                             // _lastX10Line
		            w.writeln(" }, // "+m.lookupString(cppMethodInfo.x10class)+'.'+m.lookupString(cppMethodInfo.x10method)+"()");
		        }
		        w.writeln("#endif");
		        w.writeln("};");
		        w.forceNewline();
	        }
	        
	        // variable map stuff
	        if (localVariables != null)
	        {
		        w.writeln("static const struct _X10LocalVarMap _X10variableNameList[] __attribute__((used)) "+debugDataSectionAttr+" = {");
		        for (LocalVariableMapInfo v : localVariables)
		        {
		        	int typeIndex = 0;
		        	// convert types from simple names to memberVariable table indexes.
		        	if (v._x10type >= 101 && v._x10type <= 103)
		        	{
		        		if (memberVariables != null && memberVariables.containsKey(v._x10typeIndex))
		        		{
		        			int index = 0;
		        			for (Integer classId : memberVariables.keySet())
		        			{
		        				if (classId == v._x10typeIndex)
		        				{
		        					typeIndex = index;
		        					if (v._x10type==102 && "this".equals(m.lookupString(v._x10name)))
		        						v._x10type=101; // hack requested by the debugger team
		        					else
		        					{
		        						// convert the type to what's in the main table
		        						ClassMapInfo cmi = memberVariables.get(classId);
		        						v._x10type = cmi._type;
		        					}
		        					break;
		        				}
		        				else
		        					index++;
		        			}
		        		}
		        		else
		        			typeIndex = offsets[v._x10typeIndex] * -1;
		        	}
		        	else if (v._x10type==100 || v._x10typeIndex==-2)
		        	{
			        	if (v._x10startLine == v._x10endLine) // skip generated closures
			        		continue;
			        	if (closureMembers != null)
		        		{
		        			int index = 0;
		        			for (Integer classId : closureMembers.keySet())
		        			{
		        				ClassMapInfo value = closureMembers.get(classId);
		        				if (value._x10startLine == v._x10startLine && value._x10endLine == v._x10endLine)
		        				{
		        					typeIndex = index;
		        					break;
		        				}
		        				else if (value._x10startLine != value._x10endLine)
		        					index++;
		        			}
		        		}
		        		else
		        			typeIndex = offsets[v._x10typeIndex] * -1;
		        	}
		        	else
		        		typeIndex = v._x10typeIndex;
	        		w.writeln("    { "+offsets[v._x10name]+", "+v._x10type+", "+typeIndex+", "+offsets[v._cppName]+", "+findFile(v._x10index, files)+", "+v._x10startLine+", "+v._x10endLine+" }, // "+m.lookupString(v._x10name));
		        }
		        w.writeln("};");
		        w.forceNewline();
	        }
            
	        if (memberVariables != null)
	        {
	        	for (Integer classId : memberVariables.keySet())
	        	{
	        		String classname = m.lookupString(classId);
			        w.writeln("static const struct _X10TypeMember _X10"+classname.substring(classname.lastIndexOf('.')+1)+"Members[] __attribute__((used)) "+debugDataSectionAttr+" = {");
	        		ClassMapInfo cmi = memberVariables.get(classId);
			        boolean someMembersWritten = false;
			        for (int j=0; j<cmi._members.size();)
			        {
			        	MemberVariableMapInfo v = cmi._members.get(j);
			        	boolean skip = false;
			        	if (v._x10type >= 101 && v._x10type <= 103)
			        	{
				        	int index = 0;
				            for (Integer memberId : memberVariables.keySet())
				            {
				            	if (memberId == v._x10typeIndex)
				            	{				            							            		
				            		if ("{...}".equals(m.lookupString(v._x10memberName)) && (memberVariables.get(memberId)._members.size() == 0))
				            		{
				            			skip = true;
				            			cmi._members.remove(j);
				            		}
				            		else
				            			v._x10typeIndex = index;
				            		break;
				            	}
				            	index++;
				            }
				            if (index >= memberVariables.size() && v._x10typeIndex > 0)
				            	v._x10typeIndex = offsets[v._x10typeIndex] * -1;
			        	}
			        	if (!skip)
			        	{
			        		someMembersWritten = true;
			        		w.writeln("    { "+v._x10type+", "+v._x10typeIndex+", "+offsets[v._x10memberName]+", "+offsets[v._cppMemberName]+", "+offsets[v._cppClass]+" }, // "+m.lookupString(v._x10memberName));
			        		j++;
			        	}
			        }
			        if (!someMembersWritten)
			        	w.writeln("NULL };");
			        else
			        	w.writeln("};");
				    w.forceNewline();
	        	}
	        	w.writeln("static const struct _X10ClassMap _X10ClassMapList[] __attribute__((used)) = {");
	        	for (Integer classId : memberVariables.keySet())
	        	{
	        		String classname = m.lookupString(classId);
	        		int stringIndex = offsets[classId];
	        		if (classname.contains("__")) // remove the prefix from the name, for debugger display purposes
	        			stringIndex = stringIndex+classname.lastIndexOf('_')+1;
		        	w.writeln("    { "+memberVariables.get(classId)._type+", "+stringIndex+", sizeof("+classname.replace(".", "::")+"), "+memberVariables.get(classId)._members.size()+", _X10"+classname.substring(classname.lastIndexOf('.')+1)+"Members },");
	        	}
	        	w.writeln("};");
	        	w.forceNewline();
	        }
	        	    
		    if (closureMembers != null)
		    {
		    	for (Integer classId : closureMembers.keySet())
	        	{
		    		String classname = m.lookupString(classId);
		    		ClassMapInfo cmi = closureMembers.get(classId);
		    		if (cmi._x10endLine != cmi._x10startLine) // this is a hack to skip generated closures
		    		{
		    			w.writeln("static const struct _X10TypeMember _X10"+classname.substring(classname.lastIndexOf('.')+1)+"Members[] __attribute__((used)) "+debugDataSectionAttr+" = {");
				        for (MemberVariableMapInfo v : cmi._members)
				        {
				        	int typeIndex;
				        	if (v._x10type >= 101 && v._x10type <= 103)
				        	{
				        		// see if this class is defined in our class mappings				        	
				        		typeIndex = -1;
					        	if (memberVariables != null)
					        	{
					        		int index = 0;
					            	for (Integer memberId : memberVariables.keySet())
					            	{
					            		if (memberId == v._x10typeIndex)
					            		{
					            			typeIndex = index;
					            			break;
					            		}
					            		index++;
					            	}
					        	}				        		
				        	}
				        	else
				        		typeIndex = v._x10typeIndex;
				        	w.writeln("    { "+v._x10type+", "+typeIndex+", "+offsets[v._x10memberName]+", "+offsets[v._cppMemberName]+", "+offsets[v._cppClass]+" }, // "+m.lookupString(v._x10memberName));
				        }
					    w.writeln("};");
					    w.forceNewline();
		    		}
	        	}	    	
			    w.writeln("static const struct _X10ClosureMap _X10ClosureMapList[] __attribute__((used)) = {"); // inclusion of debugDataSectionAttr causes issues on Macos.  See XTENLANG-2318.
			    boolean closureMembersWritten = false;
			    for (Integer classId : closureMembers.keySet())
			    {
			    	String classname = m.lookupString(classId);
			    	ClassMapInfo cmi = closureMembers.get(classId);
			    	if (cmi._x10endLine != cmi._x10startLine)
			    	{
			    		closureMembersWritten = true;
			    		w.writeln("    { "+cmi._type+", "+offsets[classId]+", sizeof("+cmi._sizeOfArg.replace(".", "::")+"), "+cmi._members.size()+", "+findFile(cmi._file, files)+", "+cmi._x10startLine +", "+cmi._x10endLine+", _X10"+classname.substring(classname.lastIndexOf('.')+1)+"Members },");
			    	}
			    }
			    if (!closureMembersWritten)
			    	w.writeln("NULL };");
			    else
			    	w.writeln("};");
			    w.forceNewline();
		    }
        }
	    
	    if (!arrayMap.isEmpty())
	    {
		    w.writeln("static const struct _X10ArrayMap _X10ArrayMapList[] __attribute__((used)) "+debugDataSectionAttr+" = {");
		    Iterator<Integer> iterator = arrayMap.iterator();
		    while(iterator.hasNext())
		    {
		    	int maintype = iterator.next();
		    	int innertype = iterator.next();
		    	if ((maintype >= 101 && maintype <= 103) && innertype != -1)
		    	{
		    		int lookingFor = innertype;
		    		innertype = -1;
		    		// see if this is a local class
		    		if (memberVariables != null)
		        	{
		        		int index = 0;
		            	for (Integer memberId : memberVariables.keySet())
		            	{
		            		if (memberId == lookingFor)
		            		{
		            			innertype = index;
		            			// convert the type to what's in the main table
	        					ClassMapInfo cmi = memberVariables.get(memberId);
	        					maintype = cmi._type;
		            			break;
		            		}
		            		index++;
		            	}
		        	}
		    	}
		    	w.writeln("    { "+maintype+", "+innertype+" },");
		    }
		    w.writeln("};");
		    w.forceNewline();
	    }
	    
	    if (referenceMembers != null)
	    {
    		for (Integer classId : referenceMembers.keySet())
        	{
	    		String classname = m.lookupString(classId);
	    		ClassMapInfo cmi = referenceMembers.get(classId);
    			w.writeln("static const struct _X10TypeMember _X10Ref"+classname.substring(classname.lastIndexOf('.')+1)+"Members[] __attribute__((used)) "+debugDataSectionAttr+" = {");
		        for (MemberVariableMapInfo v : cmi._members)
		        	w.writeln("    { "+v._x10type+", "+v._x10typeIndex+", "+offsets[v._x10memberName]+", "+offsets[v._cppMemberName]+", "+offsets[v._cppClass]+" }, // "+m.lookupString(v._x10memberName));
			    w.writeln("};");
			    w.forceNewline();
        	}	    	
		    w.writeln("static const struct _X10ClassMap _X10RefMapList[] __attribute__((used)) = {"); // inclusion of debugDataSectionAttr causes issues on Macos.  See XTENLANG-2318.
		    int index = 0;
		    for (Integer classId : referenceMembers.keySet())
		    {
		    	String classname = m.lookupString(classId);
		    	ClassMapInfo cmi = referenceMembers.get(classId);
		    	w.writeln("    { "+cmi._type+", "+offsets[classId]+", sizeof("+cmi._sizeOfArg+"), "+cmi._members.size()+", _X10Ref"+classname.substring(classname.lastIndexOf('.')+1)+"Members },");		    																		    
		    	index++;
		    }
		    w.writeln("};");
		    w.forceNewline();
	    }
        // A meta-structure that refers to all of the above
        w.write("static const struct _MetaDebugInfo_t _MetaDebugInfo __attribute__((used)) "+debugSectionAttr+" = {");
        w.newline(4); w.begin(0);
        w.writeln("sizeof(struct _MetaDebugInfo_t),");
        w.writeln("X10_META_LANG,");
        w.writeln("0x0C091910, // 2012-09-25, 16:00"); // Format: "YYMMDDHH". One byte for year, month, day, hour.
        w.writeln("sizeof(_X10strings),");
        if (!m.isEmpty()) {
            w.writeln("sizeof(_X10sourceList),");
            w.writeln("sizeof(_X10toCPPlist),"); 
            w.writeln("sizeof(_CPPtoX10xrefList),");
        } else {
            w.writeln("0,");
            w.writeln("0,");
            w.writeln("0,");
        }
        if (!m.methods.isEmpty()) {
            w.writeln("sizeof(_X10methodNameList),");
        } else {
            w.writeln("0, // no member variable mappings");
        }
        if (!m.isEmpty() && localVariables != null)
        	w.writeln("sizeof(_X10variableNameList),");
        else
        	w.writeln("0,  // no local variable mappings");
        if (!m.isEmpty() && memberVariables != null)
        	w.writeln("sizeof(_X10ClassMapList),");
        else
        	w.writeln("0, // no class mappings");
        if (closureMembers != null)        	
        	w.writeln("sizeof(_X10ClosureMapList),");
        else
        	w.writeln("0,  // no closure mappings");
        if (!arrayMap.isEmpty())
        	w.writeln("sizeof(_X10ArrayMapList),");
        else
        	w.writeln("0, // no array mappings");
        if (referenceMembers != null)
        	w.writeln("sizeof(_X10RefMapList),");
        else
        	w.writeln("0, // no reference mappings");
        
        w.writeln("_X10strings,");
        if (!m.isEmpty()) {
            w.writeln("_X10sourceList,");
            w.writeln("_X10toCPPlist,");
            w.writeln("_CPPtoX10xrefList,");
        } else {
            w.writeln("NULL,");
            w.writeln("NULL,");
            w.writeln("NULL,");
        }
        if (!m.methods.isEmpty()) {
            w.writeln("_X10methodNameList,");
            m.methods.clear();
        } else {
            w.writeln("NULL,");
        }
        
        if (localVariables != null)
        {
        	if (!m.isEmpty()) 
        		w.writeln("_X10variableNameList,");
        	else 
        		w.writeln("NULL,");
        	localVariables.clear();
        	localVariables = null;
        }
        else
        	w.writeln("NULL,");
        if (!m.isEmpty() && memberVariables != null)
        {
        	w.writeln("_X10ClassMapList,");
        	memberVariables.clear();
        	memberVariables = null;
        }
        else
        	w.writeln("NULL,");
        if (closureMembers != null)
        {
        	w.writeln("_X10ClosureMapList,");
        	closureMembers.clear();
        	closureMembers = null;
        }
        else
        	w.writeln("NULL,");
        if (!arrayMap.isEmpty())
        {
        	arrayMap.clear();
        	w.writeln("_X10ArrayMapList,");
        }
        else
        	w.writeln("NULL,");
        if (referenceMembers != null)
        {
        	referenceMembers.clear();
        	referenceMembers = null;
        	w.write("_X10RefMapList");
        }
        else
        	w.write("NULL");
        
        w.end(); w.newline();
        w.writeln("};");
        
        if (loopVariables != null)
        {
        	loopVariables.clear();
        	loopVariables = null;
        }
	}

	private static String encodeIntAsChars(int i) {
	    String b1 = "\\"+Integer.toOctalString((i >> 24) & 0xFF);
	    String b2 = "\\"+Integer.toOctalString((i >> 16) & 0xFF);
	    String b3 = "\\"+Integer.toOctalString((i >>  8) & 0xFF);
	    String b4 = "\\"+Integer.toOctalString((i >>  0) & 0xFF);
	    return b1+b2+b3+b4;
	}
}

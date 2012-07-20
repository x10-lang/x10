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

package x10.emitter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import polyglot.ast.Binary;
import polyglot.ast.Block;
import polyglot.ast.Block_c;
import polyglot.ast.Call;
import polyglot.ast.Cast;
import polyglot.ast.ClassMember;
import polyglot.ast.ConstructorCall;
import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.Field_c;
import polyglot.ast.Formal;
import polyglot.ast.Id;
import polyglot.ast.Instanceof;
import polyglot.ast.Lit;
import polyglot.ast.Local;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Node_c;
import polyglot.ast.Receiver;
import polyglot.ast.Stmt;
import polyglot.ast.TypeNode;
import polyglot.ast.Typed;
import polyglot.ast.Unary;
import polyglot.types.ClassType;
import polyglot.types.ContainerType;
import polyglot.types.Context;
import polyglot.types.Def;
import polyglot.types.Flags;
import polyglot.types.MethodDef;
import polyglot.types.Name;
import polyglot.types.NullType;
import polyglot.types.QName;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.TypeSystem_c;
import polyglot.types.Types;
import polyglot.util.CodeWriter;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.util.StringUtil;
import polyglot.visit.Translator;
import x10.X10CompilerOptions;
import x10.ast.ClosureCall;
import x10.ast.Closure_c;
import x10.ast.DepParameterExpr;
import x10.ast.OperatorNames;
import x10.ast.ParExpr_c;
import x10.ast.PropertyDecl;
import x10.ast.SettableAssign;
import x10.ast.TypeParamNode;
import x10.ast.X10Call;
import x10.ast.X10Call_c;
import x10.ast.X10CanonicalTypeNode;
import x10.ast.X10ClassDecl;
import x10.ast.X10ClassDecl_c;
import x10.ast.X10ConstructorDecl;
import x10.ast.X10MethodDecl_c;
import x10.ast.X10New_c;
import x10.ast.X10NodeFactory_c;
import x10.config.ConfigurationError;
import x10.config.OptionError;
import x10.constraint.XVar;
import x10.extension.X10Ext;
import x10.types.ConstrainedType;
import x10.types.FunctionType;
import x10.types.MacroType;
import x10.types.MethodInstance;
import x10.types.ParameterType;
import x10.types.X10ClassDef;
import x10.types.X10ClassType;
import x10.types.X10ConstructorInstance;
import x10.types.X10Def;
import x10.types.X10MethodDef;
import x10.types.X10ParsedClassType_c;
import x10.types.constants.ConstantValue;
import x10.types.constants.StringValue;
import x10.util.AnnotationUtils;
import x10.util.CollectionFactory;
import x10.util.HierarchyUtils;
import x10.visit.ChangePositionVisitor;
import x10.visit.X10PrettyPrinterVisitor;
import x10c.types.BackingArrayType;
import x10c.visit.ClosureRemover;
import x10c.visit.StaticInitializer;

// static methods
import static x10.visit.X10PrettyPrinterVisitor.hasParams;
import static x10.visit.X10PrettyPrinterVisitor.isBoxedType;
import static x10.visit.X10PrettyPrinterVisitor.isIndexedMemoryChunk;
import static x10.visit.X10PrettyPrinterVisitor.isPrimitive;
import static x10.visit.X10PrettyPrinterVisitor.isPrimitiveGenericMethod;
import static x10.visit.X10PrettyPrinterVisitor.isSpecialType;
import static x10.visit.X10PrettyPrinterVisitor.isSpecialTypeForDispatcher;
import static x10.visit.X10PrettyPrinterVisitor.isString;
import static x10.visit.X10PrettyPrinterVisitor.needExplicitBoxing;
// constants
import static x10.visit.X10PrettyPrinterVisitor.BOX_PRIMITIVES;
import static x10.visit.X10PrettyPrinterVisitor.NO_QUALIFIER;
import static x10.visit.X10PrettyPrinterVisitor.NO_VARIANCE;
import static x10.visit.X10PrettyPrinterVisitor.PRINT_TYPE_PARAMS;

public class Emitter {

    private static final String RETURN_PARAMETER_TYPE_SUFFIX = "$G";
    private static final String RETURN_SPECIAL_TYPE_SUFFIX = "$O";
    
    // XTENLANG-2463
    private static final boolean mangleTypeVariable = true;
    private static final String PARAMETER_TYPE_PREFIX = "$";
    private static final String UNSIGNED_NUMERIC_TYPE_SUFFIX = "$u";
    private static String FORMAL_MARKER(int i) {
      return "__" + i;
    }
    
//    private static final boolean manglePrimitivesAsShortName = true;
    private static final boolean manglePrimitivesAsShortName = false;
    
    public static final String NATIVE_ANNOTATION_BOXED_REP_SUFFIX = "$box";
    public static final String NATIVE_ANNOTATION_RUNTIME_TYPE_SUFFIX = "$rtt";

    // WIP XTENLANG-2680
//    public static final boolean supportNativeMethodDecl = false;
    public static final boolean supportNativeMethodDecl = true;

    private static final String JAVA_KEYWORD_PREFIX = "kwd_";
	private static final Set<String> JAVA_KEYWORDS = CollectionFactory.newHashSet(
	        Arrays.asList(new String[]{
	                "abstract", "default",  "if",         "private",    "this",
	                "boolean",  "do",       "implements", "protected",  "throw",
	                "break",    "double",   "import",     "public",     "throws",
	                "byte",     "else",     "instanceof", "return",     "transient",
	                "case",     "extends",  "int",        "short",      "try",
	                "catch",    "final",    "interface",  "static",     "void",
	                "char",     "finally",  "long",       "strictfp",   "volatile",
	                "class",    "float",    "native",     "super",      "while",
	                "const",    "for",      "new",        "switch",
	                "continue", "goto",     "package",    "synchronized",
	                "null",     "true",     "false",
                    // X10 implementation names
	                "serialVersionUID",
	                "x10", "java",	// XTENLANG-2438
	                // X10 implementation names (rename is not needed since they include reserved character $)
//	                X10PrettyPrinterVisitor.GETRTT_NAME, X10PrettyPrinterVisitor.RTT_NAME, X10PrettyPrinterVisitor.GETPARAM_NAME,
	        }
	        )
	);

	CodeWriter w;
	Translator tr;

	public Emitter(CodeWriter w, Translator tr) {
		this.w=w;
		this.tr=tr;
	}

	private static final String[] NON_PRINTABLE = {
	    /* 000 */ "$NUL$",
	    /* 001 */ "$SOH$",
	    /* 002 */ "$STX$",
	    /* 003 */ "$ETX$",
	    /* 004 */ "$EOT$",
	    /* 005 */ "$ENQ$",
	    /* 006 */ "$ACK$",
	    /* 007 */ "$BEL$",
	    /* 008 */ "$BS$",
	    /* 009 */ "$HT$",
	    /* 010 */ "$LF$",
	    /* 011 */ "$VT$",
	    /* 012 */ "$FF$",
	    /* 013 */ "$CR$",
	    /* 014 */ "$SO$",
	    /* 015 */ "$SI$",
	    /* 016 */ "$DLE$",
	    /* 017 */ "$DC1$",
	    /* 018 */ "$DC2$",
	    /* 019 */ "$DC3$",
	    /* 020 */ "$DC4$",
	    /* 021 */ "$NAK$",
	    /* 022 */ "$SYN$",
	    /* 023 */ "$ETB$",
	    /* 024 */ "$CAN$",
	    /* 025 */ "$EM$",
	    /* 026 */ "$SUB$",
	    /* 027 */ "$ESC$",
	    /* 028 */ "$FS$",
	    /* 029 */ "$GS$",
	    /* 030 */ "$RS$",
	    /* 031 */ "$US$",
	    /* 032 */ "$SPACE$",
	    /* 033 */ "$EXCLAMATION$",
	    /* 034 */ "$QUOTE$",
	    /* 035 */ "$HASH$",
	    /* 036 */ null,
	    /* 037 */ "$PERCENT$",
	    /* 038 */ "$AMPERSAND$",
	    /* 039 */ "$APOSTROPHE$",
	    /* 040 */ "$LPAREN$",
	    /* 041 */ "$RPAREN$",
	    /* 042 */ "$STAR$",
	    /* 043 */ "$PLUS$",
	    /* 044 */ "$COMMA$",
	    /* 045 */ "$MINUS$",
	    /* 046 */ "$DOT$",
	    /* 047 */ "$SLASH$",
	    /* 048 */ "$ZERO$",
	    /* 049 */ "$ONE$",
	    /* 050 */ "$TWO$",
	    /* 051 */ "$THREE$",
	    /* 052 */ "$FOUR$",
	    /* 053 */ "$FIVE$",
	    /* 054 */ "$SIX$",
	    /* 055 */ "$SEVEN$",
	    /* 056 */ "$EIGHT$",
	    /* 057 */ "$NINE$",
	    /* 058 */ "$COLON$",
	    /* 059 */ "$SEMICOLON$",
	    /* 060 */ "$LT$",
	    /* 061 */ "$EQ$",
	    /* 062 */ "$GT$",
	    /* 063 */ "$QUESTION$",
	    /* 064 */ "$AT$",
	    /* 065 */ null,
	    /* 066 */ null,
	    /* 067 */ null,
	    /* 068 */ null,
	    /* 069 */ null,
	    /* 070 */ null,
	    /* 071 */ null,
	    /* 072 */ null,
	    /* 073 */ null,
	    /* 074 */ null,
	    /* 075 */ null,
	    /* 076 */ null,
	    /* 077 */ null,
	    /* 078 */ null,
	    /* 079 */ null,
	    /* 080 */ null,
	    /* 081 */ null,
	    /* 082 */ null,
	    /* 083 */ null,
	    /* 084 */ null,
	    /* 085 */ null,
	    /* 086 */ null,
	    /* 087 */ null,
	    /* 088 */ null,
	    /* 089 */ null,
	    /* 090 */ null,
	    /* 091 */ "$LBRACKET$",
	    /* 092 */ "$BACKSLASH$",
	    /* 093 */ "$RBRACKET$",
	    /* 094 */ "$CARET$",
	    /* 095 */ null,
	    /* 096 */ "$BACKQUOTE$",
	    /* 097 */ null,
	    /* 098 */ null,
	    /* 099 */ null,
	    /* 100 */ null,
	    /* 101 */ null,
	    /* 102 */ null,
	    /* 103 */ null,
	    /* 104 */ null,
	    /* 105 */ null,
	    /* 106 */ null,
	    /* 107 */ null,
	    /* 108 */ null,
	    /* 109 */ null,
	    /* 110 */ null,
	    /* 111 */ null,
	    /* 112 */ null,
	    /* 113 */ null,
	    /* 114 */ null,
	    /* 115 */ null,
	    /* 116 */ null,
	    /* 117 */ null,
	    /* 118 */ null,
	    /* 119 */ null,
	    /* 120 */ null,
	    /* 121 */ null,
	    /* 122 */ null,
	    /* 123 */ "$LBRACE$",
	    /* 124 */ "$BAR$",
	    /* 125 */ "$RBRACE$",
	    /* 126 */ "$TILDE$",
	    /* 127 */ "$DEL$",
	};
	private static String translateChar(char c) {
	    if (c > 127) {
	        StringBuilder sb = new StringBuilder("\\u");
	        sb.append(Integer.toHexString(c));
	        return sb.toString();
	    }
	    String s = NON_PRINTABLE[c];
	    if (s != null) {
	        return s;
	    }
	    return ""+c;
	}

	private static final Map<Name,Name> MANGLED_OPERATORS = CollectionFactory.newHashMap();
	static {
		Map<Name,Name> map = MANGLED_OPERATORS;
		map.put(OperatorNames.AS, Name.make("$convert"));
		map.put(OperatorNames.IMPLICIT_AS, Name.make("$implicit_convert"));
		map.put(OperatorNames.SET, Name.make("$set"));
		map.put(OperatorNames.APPLY, Name.make("$apply"));
		map.put(OperatorNames.PLUS, Name.make("$plus"));
		map.put(OperatorNames.MINUS, Name.make("$minus"));
		map.put(OperatorNames.STAR, Name.make("$times"));
		map.put(OperatorNames.SLASH, Name.make("$over"));
		map.put(OperatorNames.PERCENT, Name.make("$percent"));
		map.put(OperatorNames.LT, Name.make("$lt"));
		map.put(OperatorNames.GT, Name.make("$gt"));
		map.put(OperatorNames.LE, Name.make("$le"));
		map.put(OperatorNames.GE, Name.make("$ge"));
		map.put(OperatorNames.LEFT, Name.make("$left"));
		map.put(OperatorNames.RIGHT, Name.make("$right"));
		map.put(OperatorNames.RRIGHT, Name.make("$unsigned_right"));
		map.put(OperatorNames.AMPERSAND, Name.make("$ampersand"));
		map.put(OperatorNames.BAR, Name.make("$bar"));
		map.put(OperatorNames.CARET, Name.make("$caret"));
		map.put(OperatorNames.TILDE, Name.make("$tilde"));
		map.put(OperatorNames.NTILDE, Name.make("$ntilde"));
		map.put(OperatorNames.AND, Name.make("$and"));
		map.put(OperatorNames.OR, Name.make("$or"));
		map.put(OperatorNames.BANG, Name.make("$not"));
		map.put(OperatorNames.EQ, Name.make("$equalsequals"));
		map.put(OperatorNames.NE, Name.make("$ne"));
		map.put(OperatorNames.RANGE, Name.make("$range"));
		map.put(OperatorNames.ARROW, Name.make("$arrow"));
		map.put(OperatorNames.LARROW, Name.make("$larrow"));
		map.put(OperatorNames.FUNNEL, Name.make("$funnel"));
		map.put(OperatorNames.LFUNNEL, Name.make("$lfunnel"));
		map.put(OperatorNames.DIAMOND, Name.make("$diamond"));
		map.put(OperatorNames.BOWTIE, Name.make("$bowtie"));
		map.put(OperatorNames.STARSTAR, Name.make("$starstar"));
		map.put(OperatorNames.inverse(OperatorNames.PLUS), Name.make("$inv_plus"));
		map.put(OperatorNames.inverse(OperatorNames.MINUS), Name.make("$inv_minus"));
		map.put(OperatorNames.inverse(OperatorNames.STAR), Name.make("$inv_times"));
		map.put(OperatorNames.inverse(OperatorNames.SLASH), Name.make("$inv_over"));
		map.put(OperatorNames.inverse(OperatorNames.PERCENT), Name.make("$inv_percent"));
		map.put(OperatorNames.inverse(OperatorNames.LT), Name.make("$inv_lt"));
		map.put(OperatorNames.inverse(OperatorNames.GT), Name.make("$inv_gt"));
		map.put(OperatorNames.inverse(OperatorNames.LE), Name.make("$inv_le"));
		map.put(OperatorNames.inverse(OperatorNames.GE), Name.make("$inv_ge"));
		map.put(OperatorNames.inverse(OperatorNames.LEFT), Name.make("$inv_left"));
		map.put(OperatorNames.inverse(OperatorNames.RIGHT), Name.make("$inv_right"));
		map.put(OperatorNames.inverse(OperatorNames.RRIGHT), Name.make("$inv_unsigned_right"));
		map.put(OperatorNames.inverse(OperatorNames.AMPERSAND), Name.make("$inv_ampersand"));
		map.put(OperatorNames.inverse(OperatorNames.BAR), Name.make("$inv_bar"));
		map.put(OperatorNames.inverse(OperatorNames.CARET), Name.make("$inv_caret"));
		map.put(OperatorNames.inverse(OperatorNames.TILDE), Name.make("$inv_tilde"));
		map.put(OperatorNames.inverse(OperatorNames.NTILDE), Name.make("$inv_ntilde"));
		map.put(OperatorNames.inverse(OperatorNames.AND), Name.make("$inv_and"));
		map.put(OperatorNames.inverse(OperatorNames.OR), Name.make("$inv_or"));
		map.put(OperatorNames.inverse(OperatorNames.BANG), Name.make("$inv_not"));
		map.put(OperatorNames.inverse(OperatorNames.EQ), Name.make("$inv_equalsequals"));
		map.put(OperatorNames.inverse(OperatorNames.NE), Name.make("$inv_ne"));
		map.put(OperatorNames.inverse(OperatorNames.RANGE), Name.make("$inv_range"));
		map.put(OperatorNames.inverse(OperatorNames.ARROW), Name.make("$inv_arrow"));
		map.put(OperatorNames.inverse(OperatorNames.LARROW), Name.make("$inv_larrow"));
		map.put(OperatorNames.inverse(OperatorNames.FUNNEL), Name.make("$inv_funnel"));
		map.put(OperatorNames.inverse(OperatorNames.LFUNNEL), Name.make("$inv_lfunnel"));
		map.put(OperatorNames.inverse(OperatorNames.STARSTAR), Name.make("$inv_starstar"));
	}

    public static final String SERIALIZE_ID_METHOD = "$_get_serialization_id";
    public static final String SERIALIZATION_ID_FIELD = "$_serialization_id";
    public static final String SERIALIZE_METHOD = "$_serialize";
    public static final String DESERIALIZE_BODY_METHOD = "$_deserialize_body";
    public static final String DESERIALIZER_METHOD = "$_deserializer";

	private static Name mangleIdentifier(Name n) {
		Name o = MANGLED_OPERATORS.get(n);
		if (o != null)
			return o;

		String s = n.toString();
		boolean replace = false;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < s.length(); i++) {
		    char c = s.charAt(i);
		    if (i == 0 ? !Character.isJavaIdentifierStart(c) : !Character.isJavaIdentifierPart(c)) {
		        replace = true;
		        sb.append(translateChar(c));
		    } else {
		        sb.append(c);
		    }
		}
		if (replace)
		    return Name.make(sb.toString());
		return n;
	}

	public static String mangleIdentifier(String n) {
		// Workaround an assertion failure in Name.make.
		if (! StringUtil.isNameShort(n))
			return n;
		return mangleIdentifier(Name.make(n)).toString();
	}

	public static QName mangleQName(QName name) {
		String fullName = name.toString();
		String qualifier = StringUtil.getPackageComponent(fullName);
		String shortName = StringUtil.getShortNameComponent(fullName);
		shortName = mangleIdentifier(shortName);
		return QName.make(qualifier, shortName);
	}
	
	private static Name mangleAndFlattenQName(QName name) {
		return Name.make(mangleIdentifier(name.toString().replace(".", "$")));
	}

	private static final String SPECIALTYPE_PREFIX = "$";
        private static final String BYTE_SUFFIX = SPECIALTYPE_PREFIX + "B";
        private static final String SHORT_SUFFIX = SPECIALTYPE_PREFIX + "S";
        private static final String INT_SUFFIX = SPECIALTYPE_PREFIX + "I";
        private static final String LONG_SUFFIX = SPECIALTYPE_PREFIX + "J";
        private static final String FLOAT_SUFFIX = SPECIALTYPE_PREFIX + "F";
        private static final String DOUBLE_SUFFIX = SPECIALTYPE_PREFIX + "D";
        private static final String CHAR_SUFFIX = SPECIALTYPE_PREFIX + "C";
        private static final String BOOLEAN_SUFFIX = SPECIALTYPE_PREFIX + "Z";
        private static final String UBYTE_SUFFIX = SPECIALTYPE_PREFIX + "b";
        private static final String USHORT_SUFFIX = SPECIALTYPE_PREFIX + "s";
        private static final String UINT_SUFFIX = SPECIALTYPE_PREFIX + "i";
        private static final String ULONG_SUFFIX = SPECIALTYPE_PREFIX + "j";
        private static final String STRING_SUFFIX = SPECIALTYPE_PREFIX + "t";
        private static final String VOID_SUFFIX = SPECIALTYPE_PREFIX + "V";
	private static final Name BYTE_NAME = Name.make(BYTE_SUFFIX);
        private static final Name SHORT_NAME = Name.make(SHORT_SUFFIX);
        private static final Name INT_NAME = Name.make(INT_SUFFIX);
        private static final Name LONG_NAME = Name.make(LONG_SUFFIX);
        private static final Name FLOAT_NAME = Name.make(FLOAT_SUFFIX);
        private static final Name DOUBLE_NAME = Name.make(DOUBLE_SUFFIX);
        private static final Name CHAR_NAME = Name.make(CHAR_SUFFIX);
        private static final Name BOOLEAN_NAME = Name.make(BOOLEAN_SUFFIX);
        private static final Name UBYTE_NAME = Name.make(UBYTE_SUFFIX);
        private static final Name USHORT_NAME = Name.make(USHORT_SUFFIX);
        private static final Name UINT_NAME = Name.make(UINT_SUFFIX);
        private static final Name ULONG_NAME = Name.make(ULONG_SUFFIX);
        public static final String specialTypeSuffixForDispatcher(Type type0) {
            Type type = Types.baseType(type0);
            assert isSpecialTypeForDispatcher(type);
            String name = "";
            if (type.isSignedNumeric()) {
                if (type.isByte()) name = BYTE_SUFFIX;
                else if (type.isShort()) name = SHORT_SUFFIX;
                else if (type.isInt()) name = INT_SUFFIX;
                else /*if (type.isLong())*/ name = LONG_SUFFIX;
            } else if (type.isUnsignedNumeric()) {
                if (type.isUByte()) name = UBYTE_SUFFIX; 
                else if (type.isUShort()) name = USHORT_SUFFIX;
                else if (type.isUInt()) name = UINT_SUFFIX;
                else /*if (type.isULong())*/ name = ULONG_SUFFIX;
            } else if (type.isFloat()) {
                return FLOAT_SUFFIX;
            } else if (type.isDouble()) {
                return DOUBLE_SUFFIX;
            } else if (type.isChar()) {
                return CHAR_SUFFIX;
            } else if (type.isBoolean()) {
                return BOOLEAN_SUFFIX;
            } else if (type.isString()) {
                return STRING_SUFFIX;
            } else if (type.isVoid()) {
                return VOID_SUFFIX;
            }
            return name;
        }
        private static final Name NULLTYPE_NAME = Name.make("$null"); // for NullType
        public static Name mangleAndFlattenQName(Type type) {
            if (manglePrimitivesAsShortName) {
                if (type.isSignedNumeric()) {
                    Name name = null;
                    if (type.isByte()) name = BYTE_NAME;
                    else if (type.isShort()) name = SHORT_NAME;
                    else if (type.isInt()) name = INT_NAME;
                    else /*if (type.isLong())*/ name = LONG_NAME;
                    return name;
                } else if (type.isUnsignedNumeric()) {
                    Name name = null;
                    if (type.isUByte()) name = UBYTE_NAME; 
                    else if (type.isUShort()) name = USHORT_NAME;
                    else if (type.isUInt()) name = UINT_NAME;
                    else /*if (type.isULong())*/ name = ULONG_NAME;
                    return name;
                } else if (type.isFloat()) {
                    return FLOAT_NAME;
                } else if (type.isDouble()) {
                    return DOUBLE_NAME;
                } else if (type.isChar()) {
                    return CHAR_NAME;
                } else if (type.isBoolean()) {
                    return BOOLEAN_NAME;
                }
            }
            if (type.isNull()) {
                return NULLTYPE_NAME;
            }
            return mangleAndFlattenQName(type.fullName());
        }

	public static String mangleToJava(Name name) {
	        String str = mangleIdentifier(name).toString();
	        if (str.startsWith(JAVA_KEYWORD_PREFIX)) {
	            str = "_" + str;
	        }
	        if (JAVA_KEYWORDS.contains(str)) {
	            str = JAVA_KEYWORD_PREFIX + str;
	        }
	        return str;
	}

    private static String mangleParameterType(Name name) {
    	String mangledName = mangleToJava(name);
    	if (mangleTypeVariable) {
    		mangledName = PARAMETER_TYPE_PREFIX + mangledName;
    	}
    	return mangledName;
    }
    
    public static String mangleParameterType(ParameterType pt) {
    	return mangleParameterType(pt.name());
    }
    
    public static String mangleParameterType(TypeParamNode tpn) {
    	return mangleParameterType(tpn.name().id());
    }

    public void dumpRegex(String id, Map<String,Object> components, Translator tr, String regex) {
        X10CompilerOptions opts = (X10CompilerOptions) tr.job().extensionInfo().getOptions();
        int len = regex.length();
        int pos = 0;
        int start = 0;
        while (pos < len) {
            if (regex.charAt(pos) == '\n') {
                w.write(regex.substring(start, pos));
                w.newline(0);
                start = pos + 1;
            } else if (regex.charAt(pos) == '#') {
                w.write(regex.substring(start, pos));
                int endpos = pos + 1;
                int idx = -1;
                if (Character.isDigit(regex.charAt(endpos))) {
                    while (endpos < len && Character.isDigit(regex.charAt(endpos))) {
                        ++endpos;
                    }
                } else if (Character.isJavaIdentifierStart(regex.charAt(endpos))) {
                    while (endpos < len && Character.isJavaIdentifierPart(regex.charAt(endpos))) {
                        ++endpos;
                    }
                } else {
                    throw new InternalCompilerError("Template '" + id + "' uses ill-formed key #" + regex.substring(pos + 1));
                }
                String str = regex.substring(pos + 1, endpos);
                Object component = components.get(str);
                if (component == null) {
                    throw new InternalCompilerError("Template '" + id + "' uses undefined key #" + str);
                }
                pos = endpos - 1;
                start = pos + 1;
                if (component instanceof Expr && !isNoArgumentType((Expr) component)) {
                    component = new CastExpander(w, this, (Node) component).castTo(((Expr) component).type(), BOX_PRIMITIVES);
                }
                prettyPrint(component, tr);
            } else if (regex.charAt(pos) == '`') {
                w.write(regex.substring(start, pos));
                int endpos = pos;
                while (regex.charAt(++endpos) != '`') { }
                String optionName = regex.substring(pos + 1, endpos);
                Object optionValue = null;
                try {
                    optionValue = opts.x10_config.get(optionName);
                } catch (ConfigurationError e) {
                    throw new InternalCompilerError("There was a problem while processing the option `" + optionName + "` in template '" + id + "'", e);
                } catch (OptionError e) {
                    throw new InternalCompilerError("Template '" + id + "' uses unrecognized option `" + optionName + "`", e);
                }
                w.write(optionValue.toString());
                pos = endpos;
                start = pos + 1;
            }
            pos++;
        }
        w.write(regex.substring(start));
    }

	/**
	 * Pretty-print a given object.
	 * 
	 * @param o
	 *            object to print
	 */
	public void prettyPrint(Object o, Translator tr) {
		if (o instanceof Expander) {
			((Expander) o).expand(tr);
		} else if (o instanceof Node) {
			((Node) o).del().translate(w, tr);
		} else if (o instanceof Type) {
			throw new InternalCompilerError("Should not attempt to pretty-print a type");
		} else if (o != null) {
			w.write(o.toString());
		}
	}

	public static String getJavaImplForStmt(Stmt n, TypeSystem xts) {
		if (n.ext() instanceof X10Ext) {
			X10Ext ext = (X10Ext) n.ext();
			Type java = xts.NativeType();
			List<X10ClassType> as = ext.annotationMatching(java);
			for (Type at : as) {
				assertNumberOfInitializers(at, 2);
				String lang = getPropertyInit(at, 0);
				if (lang != null && lang.equals("java")) {
					String lit = getPropertyInit(at, 1);
					return lit;
				}
			}
		}
		return null;
	}

	public static String getJavaImplForDef(X10Def o) {
		TypeSystem xts = o.typeSystem();
		Type java = xts.NativeType();
		List<Type> as = o.annotationsMatching(java);
		for (Type at : as) {
			assertNumberOfInitializers(at, 2);
			String lang = getPropertyInit(at, 0);
			if (lang != null && lang.equals("java")) {
				String lit = getPropertyInit(at, 1);
				return lit;
			}
		}
		return null;
	}
	
	private static final HashMap<String,String> boxedPrimitives = new HashMap<String,String>();
	static {
		boxedPrimitives.put("x10.lang.Boolean", "x10.core.Boolean");
		boxedPrimitives.put("x10.lang.Char", "x10.core.Char");
		boxedPrimitives.put("x10.lang.Byte", "x10.core.Byte");
		boxedPrimitives.put("x10.lang.Short", "x10.core.Short");
		boxedPrimitives.put("x10.lang.Int", "x10.core.Int");
		boxedPrimitives.put("x10.lang.Long", "x10.core.Long");
		boxedPrimitives.put("x10.lang.Float", "x10.core.Float");
		boxedPrimitives.put("x10.lang.Double", "x10.core.Double");
		boxedPrimitives.put("x10.lang.UByte", "x10.core.UByte");
		boxedPrimitives.put("x10.lang.UShort", "x10.core.UShort");
		boxedPrimitives.put("x10.lang.UInt", "x10.core.UInt");
		boxedPrimitives.put("x10.lang.ULong", "x10.core.ULong");
	}
	public static String getJavaRep(X10ClassDef def, boolean boxPrimitives) {
	    String pat = getJavaRep(def);
	    if (pat != null && boxPrimitives) {
	        String orig = def.fullName().toString();
	        String boxed = boxedPrimitives.get(orig);
	        if (boxed != null) {
	        	pat = boxedPrimitives.get(orig);
	        }
	    }
	    return pat;
	}
	
	public static String getJavaRep(X10ClassDef def) {
		return getJavaRepParam(def, 1);
	}

//    // XTENLANG-2529 : use the third parameter of @NativeRep as an expression to get zero value
//    public static String getJavaZeroValueRep(X10ClassDef def) {
//        return getJavaRepParam(def, 2);
//    }

	public static String getJavaRTTRep(X10ClassDef def) {
		return getJavaRepParam(def, 3);
	}

	private static String getJavaRepParam(X10ClassDef def, int i) {
	    Type rep = def.typeSystem().NativeRep();
	    List<Type> as = def.annotationsMatching(rep);
	    for (Type at : as) {
	        String lang = getPropertyInit(at, 0);
	        if (lang != null && lang.equals("java")) {
	            return getPropertyInit(at, i);
	        }
	    }
	    return null;
    }

	public static boolean isNativeRepedToJava(Type ct) {
	    Type bt = Types.baseType(ct);
	    if (bt.isClass()) {
	        X10ClassDef def = bt.toClass().x10Def();
	        String pat = getJavaRep(def);
	        if (pat != null && pat.startsWith("java.")) {
	            return true;
	        }
	    }
	    return false;
	}

        public static boolean isNativeRepedToJavaRecursive(Type type) {
            String typeName = type.fullName().toString();
            // N.B. currently following four class are such classes. but for safety, we exclude all Atomic classes.
//            if (typeName.equals("x10.util.concurrent.AtomicInteger")) return true;
//            if (typeName.equals("x10.util.concurrent.AtomicLong")) return true;
//            if (typeName.equals("x10.util.concurrent.AtomicReference")) return true;
//            if (typeName.equals("x10.util.concurrent.AtomicBoolean")) return true;
            if (typeName.startsWith("x10.util.concurrent.Atomic")) return true;
            if (type.isObject()) return true; // x.l.Object is subtype of x.l.Any which is @NativeRep'ed to j.l.Object. since every method of x.l.Object implements the method of x.l.Any, we need to handle x.l.Object as x.l.Any.
            return false;
            // TODO check if NativeRep'ed target (i.e. pat) recursively extends or implements Java type in Java implementation level. 
//            Type bt = Types.baseType(type);
//            if (bt.isClass()) {
//                X10ClassDef def = bt.toClass().x10Def();
//                String pat = getJavaRep(def);
//                if (pat != null) {
//                    return true;
//                }
//            }
//            return false;
        }

        // not used
//        public static boolean isNativeReped(Type ct) {
//            Type bt = Types.baseType(ct);
//            if (bt.isClass()) {
//                X10ClassDef def = bt.toClass().x10Def();
//                String pat = getJavaRep(def);
//                if (pat != null) {
//                    return true;
//                }
//            }
//            return false;
//        }

	private static String getNativeClassJavaRepParam(X10ClassDef def, int i) {
        List<Type> as = def.annotationsMatching(def.typeSystem().NativeClass());
        for (Type at : as) {
            String lang = getPropertyInit(at, 0);
            if (lang != null && lang.equals("java")) {
                return getPropertyInit(at, i);
            }
        }
        return null;
    }
	
	public static boolean isNativeClassToJava(Type ct) {
        Type bt = Types.baseType(ct);
        if (bt.isClass()) {
            X10ClassDef cd = bt.toClass().x10Def();
            String pat = getNativeClassJavaRepParam(cd, 1);
            if (pat != null && pat.startsWith("java.")) {
                return true;
            }
        }
        return false;
	}
	
	// not used
//	public static boolean isJavaTypeRecursive(Type ct) {
//        Type bt = Types.baseType(ct);
//        if (bt.isClass()) {
//        	X10ClassType xct = bt.toClass();
//        	if (xct.isJavaType()) return true;
//        	Type superClass = xct.superClass();
//        	if (superClass != null && isJavaTypeRecursive(superClass)) return true;
//        	for (Type intf : xct.interfaces()) {
//        		if (isJavaTypeRecursive(intf)) return true;
//        	}
//        }
//        return false;
//	}

	// check if the specified method overrides or implements Java method (= a method whose container is a Java type)
        public static boolean canOverrideOrImplementJavaMethod(MethodDef def) {
            Context context = def.typeSystem().emptyContext();
            MethodInstance mi = def.asInstance();
            for (MethodInstance overridden : mi.overrides(context)) {
                X10ClassType type = overridden.container().toClass();
                if (type.isJavaType()) return true;
                if (isNativeClassToJava(type)) return true;
                if (isNativeRepedToJava(type)) return true;
                if (isNativeRepedToJavaRecursive(type)) return true;
            }
            for (MethodInstance implemented : mi.implemented(context)) {
                X10ClassType type = implemented.container().toClass();
                if (type.isJavaType()) return true;
                if (isNativeClassToJava(type)) return true;
                if (isNativeRepedToJava(type)) return true;
                if (isNativeRepedToJavaRecursive(type)) return true;
            }
            return false;
        }

	private static String getPropertyInit(Type at, int index) {
		at = Types.baseType(at);
		if (at.isClass()) {
			X10ClassType act = at.toClass();
			if (index < act.propertyInitializers().size()) {
				Expr e = act.propertyInitializer(index);
				if (e != null && e.isConstant()) {
					ConstantValue v = e.constantValue();
					if (v instanceof StringValue) {
						return ((StringValue) v).value();
					}
				}
			}
		}
		return null;
	}

	private static void assertNumberOfInitializers(Type at, int len) {
		at = Types.baseType(at);
		if (at.isClass()) {
			X10ClassType act = at.toClass();
			assert len == act.propertyInitializers().size();
		}
	}

	private boolean printRepType(Type type, int flags) {
		boolean printTypeParams = (flags & PRINT_TYPE_PARAMS) != 0;
		boolean boxPrimitives = (flags & BOX_PRIMITIVES) != 0;
//		boolean inSuper = (flags & NO_VARIANCE) != 0;

		if (type.isVoid()) {
			w.write("void");
			return true;
		}

		// If the type has a native representation, use that.
		if (type.isClass()) {
			X10ClassDef cd = type.toClass().x10Def();
			String pat = getJavaRep(cd, boxPrimitives);	// @NativeRep("java", JavaRep, n/a, JavaRTTRep) 
			if (pat != null) {
                List<ParameterType> classTypeParams  = cd.typeParameters();
//                if (classTypeParams == null) classTypeParams = Collections.<ParameterType>emptyList();
                Iterator<ParameterType> classTypeParamsIter = null;
                if (classTypeParams != null) {
                    classTypeParamsIter = classTypeParams.iterator();
                }
				List<Type> classTypeArgs = type.toClass().typeArguments();
				if (classTypeArgs == null) classTypeArgs = Collections.<Type>emptyList();
				Map<String,Object> components = new HashMap<String,Object>();
				int i = 0;
				Object component;
                String name;
				component = new TypeExpander(this, type, flags);
				components.put(String.valueOf(i++), component);
                components.put("class", component);
				for (Type at : classTypeArgs) {
                    if (classTypeParamsIter != null) {
                        name = classTypeParamsIter.next().name().toString();
                    } else {
                        name = null;
                    }
                    component = new TypeExpander(this, at, flags & ~BOX_PRIMITIVES);
                    components.put(String.valueOf(i++), component);
                    if (name != null) { components.put(name, component); }
					component = new TypeExpander(this, at, flags | BOX_PRIMITIVES);
					components.put(String.valueOf(i++), component);
                    if (name != null) { components.put(name+NATIVE_ANNOTATION_BOXED_REP_SUFFIX, component); }
                    component = new RuntimeTypeExpander(this, at);
                    components.put(String.valueOf(i++), component);
                    if (name != null) { components.put(name+NATIVE_ANNOTATION_RUNTIME_TYPE_SUFFIX, component); }
				}
				if (!printTypeParams) {
					pat = pat.replaceAll("<.*>", "");
				}
				dumpRegex("NativeRep", components, tr, pat);
				return true;
			}
		}

		return false;
	}
	
	public void printBoxConversion(Type type) {
	    // treat unsigned types specially
	    if (needExplicitBoxing(type)) {
	    	printType(type, BOX_PRIMITIVES);
	        w.write("." + X10PrettyPrinterVisitor.BOX_METHOD_NAME);
	        // it requires parentheses to be printed after
	    }
	    else if (isString(type)) {
	    	w.write(X10PrettyPrinterVisitor.X10_CORE_STRING);
	        w.write("." + X10PrettyPrinterVisitor.BOX_METHOD_NAME);
	        // it requires parentheses to be printed after
	    }
	    else {
                // type == T comes here
	        // FIXME: maybe this is not needed at all? -- boxing of non-boxable types
	        w.write("(");
	        printType(type, BOX_PRIMITIVES);
	        w.write(")");
	    }
	}
	
	/**
	 * @param type - a type to print
	 * @return Returns true if an additional closing parenthesis needs to be printed after expression
	 */
	public boolean printUnboxConversion(Type type) {
            if (needExplicitBoxing(type)) {
	    	printType(type, BOX_PRIMITIVES);
	        w.write("." + X10PrettyPrinterVisitor.UNBOX_METHOD_NAME + "(");
	        return true;
	    }
	    else if (isString(type)) {
	    	w.write(X10PrettyPrinterVisitor.X10_CORE_STRING);
	        w.write("." + X10PrettyPrinterVisitor.UNBOX_METHOD_NAME + "(");
	        return true;
	    }
	    else {
	        // FIXME: maybe this is not needed at all? -- unboxing of non-boxable types
	        w.write("(");
	        printType(type, BOX_PRIMITIVES);
	        w.write(")");
	        return false;
	    }
	}

	public void printType(Type type, int flags) {
		boolean printTypeParams = (flags & PRINT_TYPE_PARAMS) != 0;
//		boolean boxPrimitives = (flags & BOX_PRIMITIVES) != 0;
		boolean inSuper = (flags & NO_VARIANCE) != 0;
		boolean ignoreQual = (flags & NO_QUALIFIER) != 0;

		type = Types.baseType(type);

		if (type.isClass()) {
			X10ClassType ct = type.toClass();
			if (ct.isAnonymous()) {
				if (ct.interfaces().size() > 0) {
					printType(ct.interfaces().get(0), flags);
					return;
				} else if (ct.superClass() != null) {
					printType(ct.superClass(), flags);
					return;
				} else {
					assert false;
					printType(type.typeSystem().Object(), flags);
					return;
				}
			}
		}

		if (printRepType(type, flags))
			return;

		if (type.isParameterType()) {
			w.write(mangleParameterType((ParameterType) type));
			return;
		}

		if (type instanceof FunctionType) {
			FunctionType ct = (FunctionType) type;
			List<Type> args = ct.argumentTypes();
			Type ret = ct.returnType();
			if (ret.isVoid()) {
				w.write(X10PrettyPrinterVisitor.X10_VOIDFUN_CLASS_PREFIX);
			} else {
				w.write(X10PrettyPrinterVisitor.X10_FUN_CLASS_PREFIX);
			}
			w.write("_" + ct.typeParameters().size());
			w.write("_" + args.size());
			if (printTypeParams && args.size() + (ret.isVoid() ? 0 : 1) > 0) {
				w.write("<");
				String sep = "";
				for (Type a : args) {
					w.write(sep);
					sep = ",";
					printType(a, PRINT_TYPE_PARAMS | BOX_PRIMITIVES);
				}
				if (!ret.isVoid()) {
					w.write(sep);
					printType(ret, PRINT_TYPE_PARAMS | BOX_PRIMITIVES);
				}
				w.write(">");
			}
			return;
		}

		// Shouldn't get here.
		if (type instanceof MacroType) {
			MacroType mt = (MacroType) type;
			printType(mt.definedType(), PRINT_TYPE_PARAMS);
			return;
		}

		if (type instanceof BackingArrayType) {
		    Type base = ((BackingArrayType) type).base();
		    printType(base, 0);
		    w.write("[]");
		    return;
		}

		// Print the class name
		if (ignoreQual) {
			if (type.isClass()) {
				w.write(mangleToJava(type.toClass().name()));
			} else {
				type.print(w);
			}
		} else if (type.isNull()) {
			w.write(X10PrettyPrinterVisitor.JAVA_LANG_OBJECT);
		} else {
			w.write(mangleQName(type.fullName()).toString());
		}

		if (printTypeParams) {
			if (type.isClass()) {
				X10ClassType ct = type.toClass();
				List<Type> typeArgs = ct.typeArguments();
				if (typeArgs == null) typeArgs = new ArrayList<Type>(ct.x10Def().typeParameters());
				String sep = "<";
				for (int i = 0; i < typeArgs.size(); i++) {
					w.write(sep);
					sep = ", ";

					Type a = typeArgs.get(i);

					final boolean variance = false;
					if (!inSuper && variance) {
						ParameterType.Variance v = ct.x10Def().variances().get(i);
						switch (v) {
						case CONTRAVARIANT:
							w.write("? super ");
							break;
						case COVARIANT:
							w.write("? extends ");
							break;
						case INVARIANT:
							break;
						}
					}
					printType(a, PRINT_TYPE_PARAMS | BOX_PRIMITIVES);
				}
				if (typeArgs.size() > 0)
					w.write(">");
			}
		}
	}

    // See comments in Native.x10
    /**
     * Annotation to mark methods and fields as having a particular native implementation.
     * lang is the name of the language, typically "java" or "c++".
     * code is the code to insert for a call to the method or an access to the field.
     *
     * For "java" annotations:
     *
     * Given a method with signature:
     *     def m[X, Y](x, y);
     * and a call
     *     o.m[A, B](a, b);
     * #0 = #this = o
     * #1 = #X = A
     * #2 = #X$box = boxed representation of A
     * #3 = #X$rtt = run-time Type object for A
     * #4 = #Y = B
     * #5 = #Y$box = boxed representation of B
     * #6 = #Y$rtt = run-time Type object for B
     * #7 = #x = a
     * #8 = #y = b
     *
     * For "c++" annotations:
     *
     * As for "java" except boxed and run-time representations of type vars should not be used.
     */
	public void emitNativeAnnotation(String pat, Object receiver, List<ParameterType> typeParams, List<Type> typeArgs, List<String> params, List<? extends Object> args, List<ParameterType> classTypeParams, List<Type> classTypeArgs) {
//      Object[] components = new Object[1 + typeArgs.size() * 3 + args.size() + classTypeArgs.size() * 3];
      Map<String,Object> components = new HashMap<String,Object>();
      int i = 0;
      Object component;
      String name;
      if (receiver != null) {
          component = receiver;
          components.put(String.valueOf(i++), component);
          components.put("this", component);
      } else {
          i++;
      }
      Iterator<ParameterType> typeParamsIter = null;
      if (typeParams != null) {
    	  typeParamsIter = typeParams.iterator();
      }
      for (Type at : typeArgs) {
          if (typeParamsIter != null) {
        	  name = typeParamsIter.next().name().toString();
          } else {
        	  name = null;
          }
          component = new TypeExpander(this, at, PRINT_TYPE_PARAMS);
          components.put(String.valueOf(i++), component);
          if (name != null) { components.put(name, component); }
          component = new TypeExpander(this, at, PRINT_TYPE_PARAMS | BOX_PRIMITIVES);
          components.put(String.valueOf(i++), component);
          if (name != null) { components.put(name+NATIVE_ANNOTATION_BOXED_REP_SUFFIX, component); }
          component = new RuntimeTypeExpander(this, at);
          components.put(String.valueOf(i++), component);
          if (name != null) { components.put(name+NATIVE_ANNOTATION_RUNTIME_TYPE_SUFFIX, component); }
      }
      Iterator<String> paramsIter = null;
      if (params != null) {
    	  paramsIter = params.iterator();
      }
      for (Object e : args) {
    	  if (paramsIter != null) {
    		  name = paramsIter.next();
    	  } else {
    		  name = null;
    	  }
          component = e;
          components.put(String.valueOf(i++), component);
          if (name != null) { components.put(name, component); }
      }
      Iterator<ParameterType> classTypeParamsIter = null;
      if (classTypeParams != null) {
    	  classTypeParamsIter = classTypeParams.iterator();
      }
      for (Type at : classTypeArgs) {
          if (classTypeParamsIter != null) {
        	  name = classTypeParamsIter.next().name().toString();
          } else {
        	  name = null;
          }
          component = new TypeExpander(this, at, PRINT_TYPE_PARAMS);
          components.put(String.valueOf(i++), component);
          if (name != null) { components.put(name, component); }
          component = new TypeExpander(this, at, PRINT_TYPE_PARAMS | BOX_PRIMITIVES);
          components.put(String.valueOf(i++), component);
          if (name != null) { components.put(name+NATIVE_ANNOTATION_BOXED_REP_SUFFIX, component); }
          component = new RuntimeTypeExpander(this, at);
          components.put(String.valueOf(i++), component);
          if (name != null) { components.put(name+NATIVE_ANNOTATION_RUNTIME_TYPE_SUFFIX, component); }
      }
      this.dumpRegex("Native", components, tr, pat);
	}

    public void generateMethodDecl(X10MethodDecl_c n, boolean boxPrimitives) {
        
        Context c = tr.context();
        boolean isInterface = c.currentClass().flags().isInterface();

        Flags flags = n.flags().flags();
        if (isInterface) {
            flags = flags.clearPublic();
            flags = flags.clearAbstract();
        }
        // XTENLANG-2680
        Flags javaFlags = flags.retainJava(); // ensure that X10Flags are not printed out .. javac will not know what to do with them.
        // TODO expand @Native annotation of interface method to the types that implement the interface and don't have its implementation.
        boolean hasNativeAnnotation = supportNativeMethodDecl && getJavaImplForDef(n.methodDef()) != null && !isInterface/*for Comparable[T].compareTo(T)*/;
        if (hasNativeAnnotation) {
            // N.B. clear native as well since it has @Native annotation. 
            javaFlags = javaFlags.clearNative();
        }

        boolean isDispatcher = X10PrettyPrinterVisitor.isSelfDispatch && isInterface && isDispatcher(n);
        // XTENLANG-2993
        boolean isSpecialTypeForDispatcher = isSpecialTypeForDispatcher(n.returnType().type());
        boolean needSpecialDispatcher = isDispatcher && (X10PrettyPrinterVisitor.generateSpecialDispatcher && isSpecialTypeForDispatcher);

        // N.B. @NativeRep'ed interface (e.g. Comparable) does not use dispatch method nor mangle method. primitives need to be boxed to allow instantiating type parameter.
        boolean canMangleMethodName = canMangleMethodName(n.methodDef());
        
        boolean first = false;
        int formalNum = 0;
        boolean isFirst = false;

        
        // XTENLANG-2993
        // stop generating interface of dispatcher method returning j.l.Object if the return type is special type
        if (X10PrettyPrinterVisitor.generateSpecialDispatcherNotUse || !needSpecialDispatcher) {
        
        w.begin(0);
        w.write(javaFlags.translate());
        
        // print the method type parameters
        printTypeParams(n, c, n.typeParameters());
        
        // print the return type
        if (isDispatcher) {
            w.write(X10PrettyPrinterVisitor.JAVA_LANG_OBJECT);
        } else {
            printType(n.returnType().type(), X10PrettyPrinterVisitor.supportTypeConstraintsWithErasure ? 0 : PRINT_TYPE_PARAMS);
        }
        
        w.allowBreak(2, 2, " ", 1);
        
        // decl
        // print the method name
        printMethodName(n.methodDef(), isInterface, isDispatcher, false, null);
        
        // print formals
        w.write("(");
        
        w.allowBreak(2, 2, "", 0);
        w.begin(0);
        
        first = true;
        // Add a formal parameter of type Type for each type parameters.
        for (TypeParamNode p : n.typeParameters()) {
            if (!first) {
                w.write(",");
                w.allowBreak(0, " ");
            } else {
                first = false;
            }
	        
            w.write("final ");
            w.write(X10PrettyPrinterVisitor.X10_RUNTIME_TYPE_CLASS);
            w.write(" ");
            w.write(mangleParameterType(p));
        }
        formalNum = 1;
        for (int i = 0; i < n.formals().size(); i++) {
            boolean forceBoxing = false;
            if (!canMangleMethodName) {
                // for methods, for which we cannot mangle name, a different boxing rule applies:
                // we force boxing of an argument if the method implements a method
                // with a boxed (generic) argument type in corresponding position
                for (MethodInstance supermeth : n.methodDef().asInstance().implemented(tr.context())) {
                    if (isBoxedType(supermeth.def().formalTypes().get(i).get())) {
                        forceBoxing = true;
                        break;
                    }
                }
            }
            if (!first) {
                w.write(",");
                w.allowBreak(0, " ");
            } else {
                first = false;
            }
            
            Formal f = n.formals().get(i);
            tr.print(n, f.flags(), w);
            
            Type type = f.type().type();
            if (isDispatcher && containsTypeParam(type)) {
                // XTENLANG-2998
                // Java backend erases type parameters of interface and merge multiple instantiations of the same generic interface (e.g. I[Int] and I[Float]) into a single Java generic interface (I).
                if (type.isParameterType()) {
                    w.write(X10PrettyPrinterVisitor.JAVA_LANG_OBJECT);
                } else {
                    printType(type, 0);
                }
                w.write(" ");
                Name name = f.name().id();
                if (name.toString().equals("")) {
                    name = Name.makeFresh("a");
                }
                tr.print(n, f.name().id(name), w);
                
                w.write(",");
                w.write(X10PrettyPrinterVisitor.X10_RUNTIME_TYPE_CLASS);
                w.write(" ");
                Name name1 = Name.make("t" + formalNum++);
                tr.print(n, f.name().id(name1), w);
            }
            else {
                printType(
                    type,
                    (n.flags().flags().isStatic() ? PRINT_TYPE_PARAMS : 0) |
                    // N.B. @NativeRep'ed interface (e.g. Comparable) does not use dispatch method nor mangle method. primitives need to be boxed to allow instantiating type parameter.
                    (boxPrimitives || forceBoxing ? BOX_PRIMITIVES : 0)
                );
                w.write(" ");
                Name name = f.name().id();
                if (name.toString().equals("")) {
                    name = Name.makeFresh("a");
                }
                tr.print(n, f.name().id(name), w);
            }
        }
        
        w.end();
        w.write(")");
        
//        // Removed throw types.
//        if (!n.throwTypes().isEmpty()) {
//            w.allowBreak(6);
//            w.write("throws ");
//            
//            for (Iterator<TypeNode> i = n.throwTypes().iterator(); i.hasNext();) {
//                TypeNode tn = (TypeNode) i.next();
//                // vj 09/26/08: Changed to print out translated version of throw type
//                // tr.print(n, tn, w);
//                // TODO: Nate to check.
//                printType(tn.type(), PRINT_TYPE_PARAMS);
//                
//                if (i.hasNext()) {
//                    w.write(",");
//                    w.allowBreak(4, " ");
//                }
//            }
//        }
        
        isFirst = true;
        for (Type _throws : AnnotationUtils.getThrowsTypes(n)) {
            if (isFirst) {
                w.write(" throws ");
                isFirst = false;
            } else {
                w.write(", ");
            }
            printType(_throws, 0);
        }
        
        w.end();
        
        // XTENLANG-2680
        // print @Native annotation as method body
        if (hasNativeAnnotation) {
            printNativeMethodDecl(n);
        } else {
            if (n.body() != null) {
                tr.print(n, n.body(), w);
            } else {
                w.write(";");
            }
        }
        
        // XTENLANG-2993
        // stop generating interface of dispatcher method returning j.l.Object if the return type is special type
        }

        
        // XTENLANG-2993
        // dispatcher method returing special type
        if (needSpecialDispatcher) {

            w.begin(0);
            w.write(javaFlags.translate());
            
            // print the method type parameters
            printTypeParams(n, c, n.typeParameters());

            // XTENLANG-2993
//            // print the return type
//            if (isDispatcher) {
//                w.write(X10PrettyPrinterVisitor.JAVA_LANG_OBJECT);
//            } else {
                printType(n.returnType().type(), X10PrettyPrinterVisitor.supportTypeConstraintsWithErasure ? 0 : PRINT_TYPE_PARAMS);
                // XTENLANG-2993
//            }

            
            w.allowBreak(2, 2, " ", 1);

            // decl
            // print the method name
            // XTENLANG-2993
            printMethodName(n.methodDef(), isInterface, isDispatcher, true, n.returnType().type());
            
            // print formals
            w.write("(");
            
            w.allowBreak(2, 2, "", 0);
            w.begin(0);
  
            first = true;
            // Add a formal parameter of type Type for each type parameters.
            for (TypeParamNode p : n.typeParameters()) {
                if (!first) {
                    w.write(",");
                    w.allowBreak(0, " ");
                } else {
                    first = false;
                }
                    
                w.write("final ");
                w.write(X10PrettyPrinterVisitor.X10_RUNTIME_TYPE_CLASS);
                w.write(" ");
                w.write(mangleParameterType(p));
            }
            formalNum = 1;
            for (int i = 0; i < n.formals().size(); i++) {
                boolean forceBoxing = false;
                if (!canMangleMethodName) {
                    // for methods, for which we cannot mangle name, a different boxing rule applies:
                    // we force boxing of an argument if the method implements a method
                    // with a boxed (generic) argument type in corresponding position
                    for (MethodInstance supermeth : n.methodDef().asInstance().implemented(tr.context())) {
                        if (isBoxedType(supermeth.def().formalTypes().get(i).get())) {
                            forceBoxing = true;
                            break;
                        }
                    }
                }
                if (!first) {
                    w.write(",");
                    w.allowBreak(0, " ");
                } else {
                    first = false;
                }
                
                Formal f = n.formals().get(i);
                tr.print(n, f.flags(), w);
                
                Type type = f.type().type();
                if (isDispatcher && containsTypeParam(type)) {
                    // XTENLANG-2998
                    // Java backend erases type parameters of interface and merge multiple instantiations of the same generic interface (e.g. I[Int] and I[Float]) into a single Java generic interface (I).
                    if (type.isParameterType()) {
                        w.write(X10PrettyPrinterVisitor.JAVA_LANG_OBJECT);
                    } else {
                        printType(type, 0);
                    }
                    w.write(" ");
                    Name name = f.name().id();
                    if (name.toString().equals("")) {
                        name = Name.makeFresh("a");
                    }
                    tr.print(n, f.name().id(name), w);
                    
                    w.write(",");
                    w.write(X10PrettyPrinterVisitor.X10_RUNTIME_TYPE_CLASS);
                    w.write(" ");
                    Name name1 = Name.make("t" + formalNum++);
                    tr.print(n, f.name().id(name1), w);
                }
                else {
                    printType(
                        type,
                        (n.flags().flags().isStatic() ? PRINT_TYPE_PARAMS : 0) |
                        // N.B. @NativeRep'ed interface (e.g. Comparable) does not use dispatch method nor mangle method. primitives need to be boxed to allow instantiating type parameter.
                        (boxPrimitives || forceBoxing ? BOX_PRIMITIVES : 0)
                    );
                    w.write(" ");
                    Name name = f.name().id();
                    if (name.toString().equals("")) {
                        name = Name.makeFresh("a");
                    }
                    tr.print(n, f.name().id(name), w);
                }
            }
            
            w.end();
            w.write(")");
            
//            // Removed throw types.
//            if (!n.throwTypes().isEmpty()) {
//                w.allowBreak(6);
//                w.write("throws ");
//                
//                for (Iterator<TypeNode> i = n.throwTypes().iterator(); i.hasNext();) {
//                    TypeNode tn = (TypeNode) i.next();
//                    // vj 09/26/08: Changed to print out translated version of throw type
//                    // tr.print(n, tn, w);
//                    // TODO: Nate to check.
//                    printType(tn.type(), PRINT_TYPE_PARAMS);
//                    
//                    if (i.hasNext()) {
//                        w.write(",");
//                        w.allowBreak(4, " ");
//                    }
//                }
//            }
  
            isFirst = true;
            for (Type _throws : AnnotationUtils.getThrowsTypes(n)) {
                if (isFirst) {
                    w.write(" throws ");
                    isFirst = false;
                } else {
                    w.write(", ");
                }
                printType(_throws, 0);
            }
            
            w.end();
            
            // XTENLANG-2680
            // print @Native annotation as method body
            if (hasNativeAnnotation) {
                printNativeMethodDecl(n);
            } else {
                if (n.body() != null) {
                    tr.print(n, n.body(), w);
                } else {
                    w.write(";");
                }
            }

            
        }

        
    }

    // decl and call
    private void printMethodName(MethodDef def, boolean isInterface, boolean isDispatcher, boolean useSpecialDispatcher, Type returnTypeForDispatcher) {
        Type returnType = def.returnType().get();
        printMethodName(def, isInterface, isDispatcher, useSpecialDispatcher, returnTypeForDispatcher, isSpecialType(returnType), tr.typeSystem().isParameterType(returnType));
    }

    public static final boolean canMangleMethodName(MethodDef def) {

        // static methods are basically safe to be mangled since they don't implement or override Java methods. 
        if (def.flags().isStatic()) {            
            // N.B. following methods are executed with Java reflection during static initialization phase, therefore their names are important  
            String methodName = def.name().toString();
            if (methodName.startsWith(StaticInitializer.initializerPrefix) || methodName.startsWith(StaticInitializer.deserializerPrefix)) return false;/*intrinsic*/
            return true;
        }

//        ContainerType containerType = def.container().get();

//        if (isNativeClassToJava(containerType)) return false;
//        if (isNativeRepedToJava(containerType)) return false; // included in canOverrideOrImplementJavaMethod(def)
        
//        if (isNativeReped(containerType)) {
//            // exclude classes that are @NativeRep'ed to non-java classes that extend java class
//            // since some instance methods of the class may match its super class's java method (therefore cannot mangle) 
//            String containerName = containerType.fullName().toString();
//            // N.B. currently following four class are such classes. but for safety, we exclude all Atomic classes.
////            if (containerName.equals("x10.util.concurrent.AtomicInteger")) return false;
////            if (containerName.equals("x10.util.concurrent.AtomicLong")) return false;
////            if (containerName.equals("x10.util.concurrent.AtomicReference")) return false;
////            if (containerName.equals("x10.util.concurrent.AtomicBoolean")) return false;
//            if (containerName.startsWith("x10.util.concurrent.Atomic")) return false;
//            // TODO looks like there is no reason to implement AtomicInteger as XRJ.
//            // we may directly map it to j.u.c.a.AtomicInteger later and remove special handling here.  
//        }
        
//        if (!def.flags().isStatic()) {
//            String methodName = def.name().toString();
            // instance methods
//            List<Ref<? extends Type>> formalTypes = def.formalTypes();
//            int numFormals = formalTypes.size();
//            if (methodName.equals("toString") && numFormals == 0) return false;/*Any=j.l.Object*/
//            if (methodName.equals("hashCode") && numFormals == 0) return false;/*Any=j.l.Object*/
//            if (methodName.equals("equals") && numFormals == 1 && formalTypes.get(0).get().isAny()) return false;/*Any=j.l.Object*/
//            if (methodName.equals("compareTo") && numFormals == 1) return false;/*Comparable=j.l.Comparable*/
            // TODO want to check with the fact that x.l.Comparable is @NativeRep'ed to j.l.Comparable
            // XTENLANG-2929
            if (canOverrideOrImplementJavaMethod(def)) return false;/*CharSequence etc.*/
//        }
        
        return true;
    }
    
    public void printMethodName(MethodDef def, boolean isInterface, boolean isDispatcher, boolean useSpecialDispatcher, Type returnTypeForDispatcher, boolean isSpecialReturnType, boolean isParamReturnType) {
        // N.B. @NativeRep'ed interface (e.g. Comparable) does not use dispatch method nor mangle method. primitives need to be boxed to allow instantiating type parameter.
        // WIP XTENLANG-2680 (ComparableTest.x10)
        // enable it after enhancing canMangleMethodName with parameter list
        if (X10PrettyPrinterVisitor.isGenericOverloading && canMangleMethodName(def)) {
            w.write(getMangledMethodName(def, !isInterface));
        }
        else {
            w.write(mangleToJava(def.name()));
        }
        if (!isDispatcher) {
            if (isSpecialReturnType) {
                if (canMangleMethodName(def)) {
                    w.write(RETURN_SPECIAL_TYPE_SUFFIX);
                }
            }
            // print $G
            else if (isParamReturnType) {
                w.write(RETURN_PARAMETER_TYPE_SUFFIX);
            }
        }
        else {
            // XTENLANG-2993
            // isDispatcher
            if (useSpecialDispatcher && isSpecialTypeForDispatcher(returnTypeForDispatcher)) {
                if (canMangleMethodName(def)) {
                    w.write(specialTypeSuffixForDispatcher(returnTypeForDispatcher));
                }
            }
        }
    }
    
    public void printMethodName(MethodDef def, boolean isInterface, boolean isDispatcher, boolean isSpecialReturnType, boolean isParamReturnType) {
        // N.B. @NativeRep'ed interface (e.g. Comparable) does not use dispatch method nor mangle method. primitives need to be boxed to allow instantiating type parameter.
        // WIP XTENLANG-2680 (ComparableTest.x10)
        // enable it after enhancing canMangleMethodName with parameter list
        if (X10PrettyPrinterVisitor.isGenericOverloading && canMangleMethodName(def)) {
            w.write(getMangledMethodName(def, !isInterface));
        }
        else {
            w.write(mangleToJava(def.name()));
        }
        if (!isDispatcher) {
            if (isSpecialReturnType) {
                if (canMangleMethodName(def)) {
                    w.write(RETURN_SPECIAL_TYPE_SUFFIX);
                }
            }
            // print $G
            else if (isParamReturnType) {
                w.write(RETURN_PARAMETER_TYPE_SUFFIX);
            }
        }
    }

    private void printMethodName(ClassType ct, MethodInstance mi) {
        if (X10PrettyPrinterVisitor.isGenericOverloading) {
            w.write(getMangledMethodName(ct, mi, true));
        } else {
            w.write(mangleToJava(mi.name()));
        }
        if (isSpecialType(mi.returnType())) {
            if (canMangleMethodName(mi.def())) {
                w.write(RETURN_SPECIAL_TYPE_SUFFIX);
            }
        }
        // print $G
        else if (tr.typeSystem().isParameterType(mi.returnType())) {
            w.write(RETURN_PARAMETER_TYPE_SUFFIX);
        }
    }

    public void printApplyMethodName(MethodInstance mi, boolean newClosure) {
        w.write(mangleToJava(ClosureCall.APPLY));
        if (X10PrettyPrinterVisitor.isSelfDispatch && (!newClosure && !mi.returnType().isVoid() && mi.formalTypes().size() == 0)) {
            w.write(RETURN_PARAMETER_TYPE_SUFFIX);
        }
        else if (!X10PrettyPrinterVisitor.isSelfDispatch && !(mi.returnType().isVoid() || (newClosure && !tr.typeSystem().isParameterType(mi.returnType())))) {
            w.write(RETURN_PARAMETER_TYPE_SUFFIX);
        }
    }
    
    public void printApplyMethodName(final Closure_c n, boolean isParamReturyType) {
        w.write(mangleToJava(ClosureCall.APPLY));
        if (!n.returnType().type().isVoid() && isParamReturyType && (!X10PrettyPrinterVisitor.isSelfDispatch || (X10PrettyPrinterVisitor.isSelfDispatch && n.formals().size() == 0))) {
            w.write(RETURN_PARAMETER_TYPE_SUFFIX);
        }
    }

    public static boolean isDispatcher(MethodInstance mi) {
        for (Ref<? extends Type> ref: mi.def().formalTypes()) {
            Type type = ref.get();
            if (containsTypeParam(type)) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean isDispatcher(X10MethodDecl_c n) {
        for (int i = 0; i < n.formals().size(); i++) {
            Type type = n.formals().get(i).type().type();
            if (containsTypeParam(type)) {
                return true;
            }
        }
        return false;
    }

    public void printTypeParams(Node_c n, Context context, List<TypeParamNode> typeParameters) {
        if (typeParameters.size() <= 0) return;
        w.write("<");
        w.begin(0);
        String sep = "";
        for (TypeParamNode tp : typeParameters) {
            w.write(sep);
            w.write(mangleParameterType(tp));
            List<Type> sups = new LinkedList<Type>(tp.upperBounds());
                            
            Type supClassType = null;
            for (Iterator<Type> it = sups.iterator(); it.hasNext();) {
                Type type = Types.baseType(it.next());
                if (type.isParameterType()) {
                    it.remove();
                }
                if (type.isClass()) {
                    TypeSystem ts = context.typeSystem();
                    if (ts.isAny(type) || ts.isObjectType(type, context)) {
                        it.remove();
                    }
                    else if (!type.toClass().flags().isInterface()) {
                        if (supClassType != null ) {
                            if (type.isSubtype(supClassType, context)) {
                                supClassType = type;
                            }
                        } else {
                            supClassType = type;
                        }
                        it.remove();
                    }
                    // TODO quick fix for boxing String
                    if (type instanceof FunctionType) {
                        List<Type> argTypes = ((FunctionType) type).argumentTypes();
                        if (argTypes.size() == 1 && argTypes.get(0).isInt() && ((FunctionType) type).returnType().isChar()) {
                            it.remove();
                        }
                    }
                }
            }
            if (supClassType != null) {
                sups.add(0, supClassType);
            }
            
            // FIXME need to check strategy for bounds of type parameter
            if (sups.size() > 0) {
                w.write(" extends ");
                List<Type> alreadyPrintedTypes = new ArrayList<Type>();
                for (int i = 0; i < sups.size(); ++i) {
                    Type type = sups.get(i);
                    if (!alreadyPrinted(alreadyPrintedTypes, type)) {
                        if (alreadyPrintedTypes.size() != 0) w.write(" & ");
                        printType(sups.get(i), BOX_PRIMITIVES);
                        alreadyPrintedTypes.add(type);
                    }
                }
            }
            sep = ", ";
        }
        w.end();
        w.write(">");
    }

    public void printMethodParams(List<? extends Type> methodTypeParams) {
        if (methodTypeParams.size() > 0) {
            w.write("<");
            for (Iterator<? extends Type> i = methodTypeParams.iterator(); i.hasNext();) {
                final Type at = i.next();
                printType(at, PRINT_TYPE_PARAMS | BOX_PRIMITIVES);
                if (i.hasNext()) {
                    w.write(",");
                    w.allowBreak(0, " ");
                }
            }
            w.write(">");
        }
    }
    
    public static boolean alreadyPrinted(List<Type> alreadyPrintedTypes, Type type) {
        boolean alreadyPrinted = false;
        if (type instanceof FunctionType) {
            if (((FunctionType) type).returnType().isVoid()) {
                for (Type apt : alreadyPrintedTypes) {
                    if (apt instanceof FunctionType && ((FunctionType) apt).returnType().isVoid()) {
                        if (((FunctionType) apt).typeArguments().size() == ((FunctionType) type).typeArguments().size()) {
                            alreadyPrinted = true;
                            break;
                        }
                    }
                }
            } else {
                for (Type apt : alreadyPrintedTypes) {
                    if (apt instanceof FunctionType && !((FunctionType) apt).returnType().isVoid()) {
                        if (((FunctionType) apt).typeArguments().size() == ((FunctionType) type).typeArguments().size()) {
                            alreadyPrinted = true;
                            break;
                        }
                    }
                }
            }
        }
        else if (type.isClass()) {
            X10ClassType ct = type.toClass();
            if (ct.typeArguments() != null && ct.typeArguments().size() > 0) {
                for (Type apt : alreadyPrintedTypes) {
                    if (apt.isClass() && !(apt instanceof FunctionType)) {
                        if (apt.toClass().name().toString().equals(type.toClass().name().toString())) {
                            alreadyPrinted = true;
                            break;
                        }
                    }
                }
            }
        }
        return alreadyPrinted;
    }
    
    public static String getMangledMethodSuffix(ClassType ct, List<Ref<? extends Type>> formalTypes, boolean printIncludingGeneric) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < formalTypes.size(); ++i) {
            Type type = formalTypes.get(i).get();
            buildMangledMethodName(ct, sb, i, type, printIncludingGeneric);
        }
        return sb.toString();
    }

    private static String getMangledMethodName(MethodDef md, boolean printIncludingGeneric) {
        String methodName = mangleToJava(md.name());
        ClassType ct = (ClassType) md.container().get();
        List<Ref<? extends Type>> formalTypes = md.formalTypes();
        String methodSuffix = getMangledMethodSuffix(ct, formalTypes, printIncludingGeneric);
        return methodName + methodSuffix;
    }
    
    private static String getMangledMethodName(ClassType ct, MethodInstance mi, boolean printIncludingGeneric) {
        StringBuilder sb = new StringBuilder(mangleToJava(mi.name()));
        List<Type> formalTypes = mi.formalTypes();
        for (int i = 0; i < formalTypes.size(); ++i) {
            Type type = formalTypes.get(i);
            buildMangledMethodName(ct, sb, i, type, printIncludingGeneric);
        }
        return sb.toString();
    }

    private static void buildMangledMethodName(ClassType ct, StringBuilder sb, int i, Type type, boolean printIncludingGeneric) {
        Type t = Types.baseType(type);
        if (t.isUnsignedNumeric()) {
            sb.append(FORMAL_MARKER(i));
            sb.append(UNSIGNED_NUMERIC_TYPE_SUFFIX);
        }
        else if (t.isClass() && (printIncludingGeneric || !containsTypeParam(t))) {
            // def g(l:x10.util.Map[x10.lang.Int,x10.lang.Float]) {...}
            //  -> g__0$1x10$lang$Int$3x10$lang$Float$2(x10.util.Map l) {...}  ("$1", "$2" and "$3" means "[", "]" and ",", respectively)
            X10ClassType x10t = t.toClass();
            List<Type> ts = x10t.typeArguments();
            if (ts != null && ts.size() > 0) {
                sb.append(FORMAL_MARKER(i));
                sb.append("$1"); // "$1" means "["
                String delim = null;
                for (Type t1 : ts) {
                    if (delim != null) sb.append(delim);
                    delim = "$3"; // "$3" means ","
                    appendParameterizedType(sb, ct, Types.baseType(t1));
                }
                sb.append("$2"); // "$2" means "]"
            }
        }
        else if (printIncludingGeneric && t.isParameterType()) {
            // class I[T] { def foo(t:T) {...} }
            //  -> class I<T> { foo__0I$$T(T t) {...} }   ("I$$T" means T of I)
            sb.append(FORMAL_MARKER(i));
            appendParameterType(ct, sb, (ParameterType) t);
        }
    }

    private static void appendParameterizedType(StringBuilder sb, ClassType ct, Type t) {
        if (t.isClass()) {
            X10ClassType x10t = t.toClass();
            sb.append(mangleAndFlattenQName(x10t));
            if (x10t.typeArguments() != null && x10t.typeArguments().size() > 0) {
                List<Type> ts = x10t.typeArguments();
                if (ts.size() > 0) {
                    sb.append("$1"); // "$1" means "["
                    String delim = null;
                    for (Type t1 : ts) {
                        if (delim != null) sb.append(delim);
                        delim = "$3"; // "$3" means ","
                        appendParameterizedType(sb, ct, Types.baseType(t1));
                    }
                    sb.append("$2"); // "$2" means "]"
                }
            }
        }
        else if (t.isParameterType()) {
            appendParameterType(ct, sb, (ParameterType) t);
        }
        else {
            sb.append(mangleAndFlattenQName(t));
        }
    }

    private static void appendParameterType(ClassType ct, StringBuilder sb, ParameterType t) {
        // N.B. As of September 2011, type parameters are alpha-renamed so that each type parameter has a unique name at every location.
        // Therefore the full name of the type which the type parameter belongs to is not necessarily needed.
        sb.append(mangleAndFlattenQName(ct));
        sb.append("$$");
        sb.append(mangleIdentifier(t.name()));
    }

	public static boolean containsTypeParam(Type type) {
	    if (type.isParameterType()) {
	        return true;
	    }
	    else if (type.isClass()) {
	        List<Type> tas = type.toClass().typeArguments();
	        if (tas != null) {
	            for (Type type1 : tas) {
	                if (containsTypeParam(type1)) {
	                    return true;
	                }
	            }
	        }
	    }
        return false;
    }

    /*
     * The word 'instantiated' means the instantiation of type parameter (e.g. T) with concrete type (e.g. Int). 
     */
    private void generateBridgeMethodsForGenerics(X10ClassDef cd) {
	    if (cd.flags().isInterface()) {
	        return;
	    }

	    X10ClassType ct = cd.asType();
	    for (MethodDef md : cd.methods()) {
	        List<MethodInstance> methods = getInstantiatedMethods(ct, md.asInstance());
	        for (MethodInstance mi : methods) {
	            printBridgeMethod(ct, md.asInstance(), mi.def(), false);
	        }
	    }
	    
	    List<MethodInstance> inheriteds = new ArrayList<MethodInstance>();
        List<MethodInstance> overrides = new ArrayList<MethodInstance>();
	    getInheritedMethods(ct, inheriteds, overrides);
	    for (MethodInstance mi : inheriteds) {
	        if (isInstantiated(mi.def().returnType().get(), mi.returnType())) {
	            printBridgeMethodForInheritedMethod(ct, mi);
	            continue;
	        }
	        for (int i = 0; i < mi.formalTypes().size(); ++ i) {
	            if (
	                    isPrimitive(mi.formalTypes().get(i)) &&
	                    isInstantiated(mi.def().formalTypes().get(i).get(), mi.formalTypes().get(i))
	            ) {
	                printBridgeMethodForInheritedMethod(ct, mi);
	                break;
	            }
	        }
	        List<MethodInstance> implMethods = new ArrayList<MethodInstance>();
	        List<Type> interfaces = ct.interfaces();
	        getImplMethods(mi, implMethods, interfaces);
	        for (MethodInstance mi2 : implMethods) {
	            printBridgeMethod(ct, mi, mi2.def(), false);
	        }
	    }
	}

    /*
     * 'inheriteds' will include all visible methods that are inherited from super classes to ct.
     */
    private void getInheritedMethods(X10ClassType ct, List<MethodInstance> inheriteds, List<MethodInstance> overrides) {
        ArrayList<MethodInstance> list = new ArrayList<MethodInstance>(ct.methods());
        list.addAll(inheriteds);
        for (MethodInstance mi : list) {
            for (MethodInstance mi2 : mi.overrides(tr.context())) {
                if (X10PrettyPrinterVisitor.isGenericOverloading || (ct.superClass() != null && mi2.container().typeEquals(ct.superClass(), tr.context()))) {
                    overrides.add(mi2);
                }
            }
        }
        Type sup = ct.superClass();
        if (sup != null && sup.isClass()) {
            for (MethodInstance mi : sup.toClass().methods()) {
                if (!mi.flags().isStatic() && !mi.flags().isPrivate()) {
                    boolean contains = false;
                    for (MethodInstance mi2 : overrides) {
                        if (mi2.isSameMethod(mi, tr.context())) {
                            contains = true;
                            break;
                        }
                    }
                    if (!contains) {
                        inheriteds.add(mi);
                    }
                }
            }
            getInheritedMethods(sup.toClass(), inheriteds, overrides);
        }       
    }

    private void getImplMethods(MethodInstance mi, List<MethodInstance> implMethods, List<Type> interfaces) {
        for (Type type : interfaces) {
            if (type.isClass()) {
                List<MethodInstance> imis = type.toClass().methods();
                for (MethodInstance imi : imis) {
                    if (!(imi.name().equals(mi.name()) && imi.formalTypes().size() == mi.formalTypes().size())) continue;

                    if (containsInstantiatedMethod(implMethods, imi)) continue;

                    Type returnType = mi.returnType();
                    if (X10PrettyPrinterVisitor.isGenericOverloading) {
                        if (
                                returnType.typeEquals(imi.returnType() , tr.context())
                                && isInstantiated(imi.def().returnType().get(), returnType)
                        ) {
                            boolean containsParam = false;
                            List<Ref<? extends Type>> types = imi.def().formalTypes();
                            for (int i = 0;i < types.size(); ++i) {
                                if (
                                        mi.formalTypes().get(i).typeEquals(imi.formalTypes().get(i), tr.context())
                                        && containsTypeParam(imi.def().formalTypes().get(i).get())
                                ) {
                                    containsParam = true;
                                    break;
                                }
                            }
                            if (!containsParam) {
                                implMethods.add(imi);
                                break;
                            }
                        }
                    } else {
                        if (
                                returnType.typeEquals(imi.returnType() , tr.context())
                                && isInstantiated(imi.def().returnType().get(), returnType)
                        ) {
                            implMethods.add(imi);
                            break;
                        }
                        List<Ref<? extends Type>> types = imi.def().formalTypes();
                        for (int i = 0;i < types.size(); ++i) {
                            if (
                                    mi.formalTypes().get(i).typeEquals(imi.formalTypes().get(i), tr.context())
                                    && isPrimitive(mi.formalTypes().get(i))
                                    && isInstantiated(types.get(i).get(), mi.formalTypes().get(i))
                            ) {
                                implMethods.add(imi);
                                break;
                            }
                        }
                    }
                }
                getImplMethods(mi, implMethods, type.toClass().interfaces());
            }
        }
    }

    private List<MethodInstance> getInstantiatedMethods(X10ClassType ct, MethodInstance mi) {
        List<MethodInstance> methods = new ArrayList<MethodInstance>();
        if (mi.flags().isPrivate()) return methods;       // N.B. shortcut for (*1) is this needed?
        for (MethodInstance impled : mi.implemented(tr.context())) {
//            if (mi.flags().isPrivate()) continue;       // N.B. (*1) is this needed?
            if (mi.container().typeEquals(impled.container(), tr.context())) continue;

            // Fix for XTENLANG-2940
//            if (X10PrettyPrinterVisitor.isGenericOverloading) {
            if (false) {
                boolean contains = false;
                for (MethodInstance mi1 : methods) {
                    if (mi1.def().equals(impled.def())) {
                        contains = true;
                        break;
                    }
                }
                if (contains) continue;
            }
            else {
                if (containsInstantiatedMethod(methods, impled)) continue;
            }

            Type ti = impled.container();
            ti = Types.baseType(ti);

            if (ti.isClass() && !ti.toClass().flags().isInterface()) {
                if (
                        X10PrettyPrinterVisitor.isGenericOverloading
                        || (ti.typeEquals(ct.superClass(), tr.context()) || (ct.isMember() && ti.typeEquals(ct.container(), tr.context())))
                ) {
                    Type returnType = mi.returnType();
                    if (isInstantiated(impled.def().returnType().get(), returnType)) {
                        methods.add(impled);
                        continue;
                    }
                    else {
                        List<Ref<? extends Type>> types = impled.def().formalTypes();
                        for (int i = 0;i < types.size(); ++i) {
                            if (
                                    (
                                            X10PrettyPrinterVisitor.isGenericOverloading
                                            && containsTypeParam(types.get(i).get())
                                    ) 
                                    || (
                                            !X10PrettyPrinterVisitor.isGenericOverloading
                                            && isPrimitive(mi.formalTypes().get(i))
                                            && isInstantiated(types.get(i).get(), mi.formalTypes().get(i))
                                    )
                            ) {
                                methods.add(impled);
                                break;
                            }
                        }
                    }
                }
            }
            else {
                for (Type t : ct.interfaces()) {
                    if (existMethodInterface(t, ti, impled, mi)) {
                        methods.add(impled);
                        break;
                    }
                }
            }
        }
        return methods;
    }

    private static boolean isInstantiated(Type sup, Type t) {
        return sup.isParameterType() && !t.isParameterType();
    }

    private boolean existMethodInterface(Type t, Type type, MethodInstance mi, MethodInstance mdi) {
	    if (t.typeEquals(type, tr.context())) {
	        Type returnType = mdi.returnType();
	        if (isInstantiated(mi.def().returnType().get(), returnType)) {
	            if (X10PrettyPrinterVisitor.isGenericOverloading) {
	                boolean containsTypeParam = false;
	                List<Ref<? extends Type>> types = mi.def().formalTypes();
	                for (int i = 0;i < types.size(); ++i) {
	                    if (containsTypeParam(types.get(i).get())) {
	                        containsTypeParam = true;
	                        break;
	                    }
	                }
	                if (!containsTypeParam) return true;
	            } else {
	                return true;
	            }
	        }
	        if (!X10PrettyPrinterVisitor.isGenericOverloading) {
	            List<Ref<? extends Type>> types = mi.def().formalTypes();
	            for (int i = 0;i < types.size(); ++i) {
	                if (containsTypeParam(types.get(i).get())) return false;
	                
	                if (
	                        isPrimitive(mdi.formalTypes().get(i))
	                        && isInstantiated(types.get(i).get(), mdi.formalTypes().get(i))
	                ) {
	                    return true;
	                }
	            }
	        }
	    }
	    t = Types.baseType(t);
	    if (t.isClass()) {
	        for (Type ti : t.toClass().interfaces()) {
	            if (existMethodInterface(ti, type, mi, mdi)) {
	                return true;
	            }
	        }
	    }
	    return false;
	}

    // old and potentially buggy code
//	private boolean containsInstantiatedMethod(List<MethodInstance> methods, MethodInstance impled) {
//        for (MethodInstance mi : methods) {
//            if (
//                !(
//                    (mi.def().returnType().get().isParameterType() && impled.def().returnType().get().isParameterType())
//                    || (!mi.def().returnType().get().isParameterType() && !impled.def().returnType().get().isParameterType())
//                )
//            ) {
//                continue;
////                return false;
//            }
//            List<Ref<? extends Type>> types = mi.def().formalTypes();
//            for (int i = 0;i < types.size(); ++i) {
//                if (
//                    !(
//                        (types.get(i).get().isParameterType() && impled.def().formalTypes().get(i).get().isParameterType()))
//                        || (!types.get(i).get().isParameterType() && !impled.def().formalTypes().get(i).get().isParameterType())
//                ) {
//                    continue;
////                    return false;
//                }
//            }
//            return true;
//        }
//        return false;
//    }
    // correct code
    private static boolean containsInstantiatedMethod(List<MethodInstance> methods, MethodInstance impled) {
        MethodDef impledDef = impled.def();
        List<Ref<? extends Type>> implFormalTypes = impledDef.formalTypes();
        methods: for (MethodInstance mi : methods) {
            MethodDef miDef = mi.def();
            Type miReturnType = miDef.returnType().get();
            Type impledReturnType = impledDef.returnType().get();
            if (
                !(
                    (miReturnType.isParameterType() && impledReturnType.isParameterType())
                    || (!miReturnType.isParameterType() && !impledReturnType.isParameterType())
                )
            ) continue;
            List<Ref<? extends Type>> miFormalTypes = miDef.formalTypes();
            for (int i = 0; i < miFormalTypes.size(); ++i) {
                Type miFormalType = miFormalTypes.get(i).get();
                Type implFormalType = implFormalTypes.get(i).get();
                if (
                    !(
                        (miFormalType.isParameterType() && implFormalType.isParameterType())
                        || (!miFormalType.isParameterType() && !implFormalType.isParameterType())
                    )
                ) continue methods;
            }
            return true;
        }
        return false;
    }

    /*
     * given interface I { f():Any; } and class C implements I { f():String; }, C.f() returns x10.core.String.
     * given interface I[T] { f():T; } and class C[T] implements I[T] { f():String; }, C.f() returns java.lang.String.
     */
    private void printBridgeMethod(ClassType ct, MethodInstance impl, MethodDef def, boolean isCovariantOverride) {
        // bridge method should not be needed for unmangled method
    	if (!canMangleMethodName(def)) return;
    	
	    w.write("// bridge for " + def);
	    w.newline();

	    Flags flags = impl.flags();

	    w.begin(0);
	    w.write(flags.clearAbstract().clearProtected().Public()
	        .clear(Flags.NATIVE)
	        .translateJava()
	    );
        
	    ContainerType st = def.container().get();
	    
	    if (def instanceof X10MethodDef) {
	        List<ParameterType> tps = ((X10MethodDef) def).typeParameters();
	        if (tps.size() > 0) {
	            w.write("<");
	            String delim = "";
	            for (ParameterType pt : tps) {
	                w.write(delim);
	                delim = ",";
	                w.write(mangleParameterType(pt));
	            }
	            w.write(">");
	            w.write(" ");
	        }
	    }
	    
	    // e.g int m() overrides or implements T m()
            boolean instantiateReturnType = isBoxedType(Types.baseType(def.returnType().get()));
            int intflags = instantiateReturnType ? BOX_PRIMITIVES : 0;
            if (!X10PrettyPrinterVisitor.isGenericOverloading) intflags |= PRINT_TYPE_PARAMS;
            boolean boxReturnString = isCovariantOverride && isString(impl.returnType());
            if (boxReturnString) {
                w.write(X10PrettyPrinterVisitor.X10_CORE_STRING);
            } else {
                printType(impl.returnType(), intflags);
            }

	    boolean isInterface = st.isClass() && st.toClass().flags().isInterface();
	    
	    w.allowBreak(2, 2, " ", 1);

	    // decl
	    // print the method name
	    printMethodName(def, isInterface, false, false, null);
	    
	    // print the formals
	    w.write("(");
        boolean first = true;
        
        if (X10PrettyPrinterVisitor.isGenericOverloading && def instanceof X10MethodDef && !isInterface) {
            X10MethodDef x10def = (X10MethodDef) def;
            for (ParameterType p : x10def.typeParameters()) {
                if (!first) {
                    w.write(",");
                    w.allowBreak(0, " ");
                }
                first = false;
                
                w.write("final ");
                w.write(X10PrettyPrinterVisitor.X10_RUNTIME_TYPE_CLASS);
                w.write(" ");
                w.write(mangleParameterType(p));
            }
        }

	    for (int i = 0; i < def.formalTypes().size(); i++) {
	        Type f = impl.formalTypes().get(i);
	        if (!first || i != 0) {
	            w.write(",");
	            w.allowBreak(0, " ");
	        }
	        if (def.formalTypes().get(i).get().isParameterType()) {
	            printType(f, (X10PrettyPrinterVisitor.isGenericOverloading ? 0 : PRINT_TYPE_PARAMS) | BOX_PRIMITIVES);
	        } else {
	            printType(f, X10PrettyPrinterVisitor.isGenericOverloading ? 0 : PRINT_TYPE_PARAMS);
	        }
	        w.write(" ");

	        Name name = Name.make("a" + (i + 1));
	        w.write(name.toString());
	    }

	    w.end();
	    w.write(")");

	    /* Remove throw types support
	    if (!impl.throwTypes().isEmpty()) {
	        w.allowBreak(6);
	        w.write("throws ");
	        for (Iterator<Type> i = impl.throwTypes().iterator(); i.hasNext();) {
	            Type t = i.next();
	            printType(t, PRINT_TYPE_PARAMS);
	            if (i.hasNext()) {
	                w.write(",");
	                w.allowBreak(4, " ");
	            }
	        }
	    }
*/

	    boolean isFirst = true;
	    for (Type _throws : AnnotationUtils.getThrowsTypes(impl.def())) {
	        if (isFirst) {
	            w.write(" throws ");
	            isFirst = false;
	        } else {
	            w.write(", ");				
	        }
	        printType(_throws, 0);
	    }

	    w.write("{");
	    if (!impl.returnType().isVoid()) {
	        w.write("return ");
	    }

        boolean closeParen = false;
        if (boxReturnString || (instantiateReturnType && !isBoxedType(impl.returnType()))) {
        	printBoxConversion(impl.returnType());
        	w.write("(");
            closeParen = true;
        }

	    TypeSystem xts = tr.typeSystem();
	    boolean isInterface2 = false;
	    ContainerType st2 = impl.container();
	    Type bst = Types.baseType(st2);
        if (st2.isClass()) {
	        if (xts.isInterfaceType(bst) || (xts.isFunctionType(bst) && bst.toClass().isAnonymous())) {
	            isInterface2 = true;
	        }
	    }

        // call
        printMethodName(impl.def(), isInterface2, false, false, null);

        // print the argument list
	    w.write("(");
	    
	    boolean first2 = true;
	    MethodInstance dmi = def.asInstance();
	    for (Iterator<Type> i = dmi.typeParameters().iterator(); i.hasNext(); ) {
	        final Type at = i.next();
	        first2 = false;
	        // TODO
	        new RuntimeTypeExpander(this, at).expand(tr);
	        if (i.hasNext()) {
	            w.write(",");
	            w.allowBreak(0, " ");
	        }
	    }
	    
	    for (int i = 0; i < impl.formalTypes().size(); i++) {
	        Type f = impl.formalTypes().get(i);
	        if (!first2 || i != 0) {
	            w.write(",");
	            w.allowBreak(0, " ");
	        }
	        Name name = Name.make("a" + (i + 1));
	        boolean closeParenArg = false;
	        if (isPrimitive(f) && isBoxedType(def.formalTypes().get(i).get())) {
	            closeParenArg = printUnboxConversion(f);
	        }
	        w.write(name.toString());
	        if (closeParenArg) w.write(")");
	    }
	    w.write(")");

	    if (closeParen) {
	        w.write(")");
	    }
	    w.write(";");

	    w.write("}");
	    w.newline();
	}

    private void printBridgeMethodForInheritedMethod(ClassType ct, MethodInstance mi) {
    	    MethodDef def = mi.def();
    	    w.write("// bridge for " + def);
    	    w.newline();
    
    	    Flags flags = mi.flags();
    
    	    w.begin(0);
    	    w.write(flags.clearAbstract()
    	        .clear(Flags.NATIVE)
    	        .translateJava()
    	    );
    
    	    printType(mi.returnType(), PRINT_TYPE_PARAMS);

    	    w.allowBreak(2, 2, " ", 1);
    
    	    // print the method name
    	    printMethodName(ct, mi);
    
    	    w.write("(");
    	    for (int i = 0; i < def.formalTypes().size(); i++) {
    	        Type f = mi.formalTypes().get(i);
    	        if (i != 0) {
    	            w.write(",");
    	            w.allowBreak(0, " ");
    	        }
                printType(f, (X10PrettyPrinterVisitor.isGenericOverloading ? 0 : PRINT_TYPE_PARAMS));
    	        w.write(" ");
    
    	        Name name = Name.make("a" + (i + 1));
    	        w.write(name.toString());
    	    }
    
    	    w.end();
    	    w.write(")");

    	    /* Remove throw types support.
    	    if (!mi.throwTypes().isEmpty()) {
    	        w.allowBreak(6);
    	        w.write("throws ");
    	        for (Iterator<Type> i = mi.throwTypes().iterator(); i.hasNext();) {
    	            Type t = i.next();
    	            printType(t, PRINT_TYPE_PARAMS);
    	            if (i.hasNext()) {
    	                w.write(",");
    	                w.allowBreak(4, " ");
    	            }
    	        }
    	    }
    */

    	    boolean isFirst = true;
    	    for (Type _throws : AnnotationUtils.getThrowsTypes(mi.def())) {
    	        if (isFirst) {
    	            w.write(" throws ");
    	            isFirst = false;
    	        } else {
    	            w.write(", ");				
    	        }
    	        printType(_throws, 0);
    	    }

    	    w.write("{");
    	    if (!mi.returnType().isVoid()) {
    	        w.write("return ");
    	    }
    	    
    	    boolean closeParen = false;
    	    if (!isBoxedType(mi.returnType()) && isBoxedType(mi.def().returnType().get())) {
    	        // handle unboxing of the UInt values
    	        closeParen = printUnboxConversion(mi.returnType());
    	    }
    
    	    w.write("super.");
    	    
    	    // call
    	    printMethodName(def, false, false, false, null);

    	    // print the argument list
    	    w.write("(");
    	    for (int i = 0; i < mi.formalTypes().size(); i++) {
    	        Type f = mi.formalTypes().get(i);
    	        if (i != 0) {
    	            w.write(",");
    	            w.allowBreak(0, " ");
    	        }
    	        if (isPrimitive(f) && isInstantiated(def.formalTypes().get(i).get(), f)) {
    	            printBoxConversion(f);
    	        }
    	        w.write("("); // required by printBoxConversion()
    	        Name name = Name.make("a" + (i + 1));
    	        w.write(name.toString());
    	        w.write(")");
    	    }
    	    w.write(")");
    	    if (closeParen) w.write(")");
    
    	    w.write(";");
    	    w.write("}");
    	    w.newline();
    }

    private void generateBridgeMethodsForCovariantOverride(X10ClassDef cd) {
        if (cd.flags().isInterface()) {
            return;
        }
    
        X10ClassType ct = cd.asType();
        for (MethodDef md : cd.methods()) {
            List<MethodInstance> methods = getCovarientOverriddenMethods(ct, md.asInstance());
            for (MethodInstance mi : methods) {
                printBridgeMethod(ct, md.asInstance(), mi.def(), true);
            }
        }
        
        List<MethodInstance> inheriteds = new ArrayList<MethodInstance>();
        List<MethodInstance> overrides = new ArrayList<MethodInstance>();
        getInheritedMethods(ct, inheriteds, overrides);
        for (MethodInstance mi : inheriteds) {
//            if (isCovariantOverride(mi.def().returnType().get(), mi.returnType())) {
//                printBridgeMethodForInheritedMethod(ct, mi);
//                continue;
//            }
            List<MethodInstance> implMethods = new ArrayList<MethodInstance>();
            List<Type> interfaces = ct.interfaces();
            getImplMethodsForCovariantOverride(mi, implMethods, interfaces);
            for (MethodInstance mi2 : implMethods) {
                printBridgeMethod(ct, mi, mi2.def(), true);
            }
        }
    }
    
    // Fix for XTENLANG-3035
    // old and buggy code
//    public void generateBridgeMethods(X10ClassDef cd) {
//        generateBridgeMethodsForGenerics(cd);
//        generateBridgeMethodsForCovariantOverride(cd);
//    }
    public void generateBridgeMethods(X10ClassDef cd) {
        if (cd.flags().isInterface()) return;

        List<MethodDef> methoddefs = cd.methods();

        X10ClassType ct = cd.asType();
        List<Type> interfaces = ct.interfaces();
        List<MethodInstance> inheriteds = new ArrayList<MethodInstance>();
        List<MethodInstance> overrides = new ArrayList<MethodInstance>();
        getInheritedMethods(ct, inheriteds, overrides);

        // first half
      for (MethodDef md : methoddefs) {
          MethodInstance impl = md.asInstance();
//          generateBridgeMethodsForGenerics(cd);
          List<MethodInstance> instantiatedMethods = getInstantiatedMethods(ct, impl);
          for (MethodInstance instantiatedMethod : instantiatedMethods) {
              printBridgeMethod(ct, impl, instantiatedMethod.def(), false);
          }
//          generateBridgeMethodsForCovariantOverride(cd);
          List<MethodInstance> overriddenMethods = getCovarientOverriddenMethods(ct, impl);
          alreadyGenerated: for (MethodInstance overriddenMethod : overriddenMethods) {
              // N.B. skip already generated bridge method
              if (!isString(impl.returnType())) {
                  MethodDef overriddenMethodDef = overriddenMethod.def();
                  for (MethodInstance instantiatedMethod : instantiatedMethods) {
                      if (hasSameSignature(overriddenMethodDef, instantiatedMethod.def())) continue alreadyGenerated;
                  }
              }
              printBridgeMethod(ct, impl, overriddenMethod.def(), true);
          }
      }

      // second half        
//        generateBridgeMethodsForGenerics(cd);
        for (MethodInstance mi : inheriteds) {
            if (isInstantiated(mi.def().returnType().get(), mi.returnType())) {
                printBridgeMethodForInheritedMethod(ct, mi);
                continue;
            }
            for (int i = 0; i < mi.formalTypes().size(); ++i) {
                if (isPrimitive(mi.formalTypes().get(i)) && isInstantiated(mi.def().formalTypes().get(i).get(), mi.formalTypes().get(i))) {
                    printBridgeMethodForInheritedMethod(ct, mi);
                    break;
                }
            }
            List<MethodInstance> implMethods = new ArrayList<MethodInstance>();
            getImplMethods(mi, implMethods, interfaces);
            for (MethodInstance mi2 : implMethods) {
                printBridgeMethod(ct, mi, mi2.def(), false);
            }
        }
//        generateBridgeMethodsForCovariantOverride(cd);
        for (MethodInstance mi : inheriteds) {
//            if (isCovariantOverride(mi.def().returnType().get(), mi.returnType())) {
//                printBridgeMethodForInheritedMethod(ct, mi);
//                continue;
//            }
            List<MethodInstance> implMethods = new ArrayList<MethodInstance>();
            getImplMethodsForCovariantOverride(mi, implMethods, interfaces);
            for (MethodInstance mi2 : implMethods) {
                printBridgeMethod(ct, mi, mi2.def(), true);
            }
        }
        
    }

    private List<MethodInstance> getCovarientOverriddenMethods(X10ClassType ct, MethodInstance mi) {
        List<MethodInstance> methods = new ArrayList<MethodInstance>();
        if (mi.flags().isPrivate()) return methods;     // N.B. shortcut for (*1) is this needed?
        for1:for (MethodInstance impled : mi.implemented(tr.context())) {
//            if (mi.flags().isPrivate()) continue;     // N.B. (*1) is this needed?
            if (mi.container().typeEquals(impled.container(), tr.context())) continue;
    
            if (X10PrettyPrinterVisitor.isGenericOverloading) {
                for2:for (MethodInstance mi1 : methods) {
                    if (mi1.def().equals(impled.def())) {
                        continue for1;
                    }
                    List<Ref<? extends Type>> types = impled.def().formalTypes();
                    List<Ref<? extends Type>> types2 = mi1.def().formalTypes();
                    for (int i = 0;i < types.size(); ++i) {
                        if (!types.get(i).get().typeEquals(types2.get(i).get(), tr.context())) {
                            continue for2;
                        }
                    }
                    continue for1;
                }
            }
            else {
                if (containsCovariantOverridingMethod(methods, impled)) continue;
            }
    
            Type ti = impled.container();
            ti = Types.baseType(ti);
            
            if (ti.isClass() && !ti.toClass().flags().isInterface()) {
                if (
                        X10PrettyPrinterVisitor.isGenericOverloading
                        || (ti.typeEquals(ct.superClass(), tr.context()) || (ct.isMember() && ti.typeEquals(ct.container(), tr.context())))
                ) {
                    Type returnType = mi.returnType();
                    if (isCovariantOverride(impled.def().returnType().get(), returnType)) {
                        methods.add(impled);
                        continue;
                    }
                }
            }
            else {
                for (Type t : ct.interfaces()) {
                    if (existMethodInterfaceForCovariantOverride(t, ti, impled, mi)) {
                        methods.add(impled);
                        break;
                    }
                }
            }
        }
        return methods;
    }

    private boolean isCovariantOverride(Type sup, Type returnType) {
        return !sup.typeSystem().isParameterType(sup) && !Types.baseType(sup).typeEquals(Types.baseType(returnType), tr.context()) && isSpecialType(Types.baseType(returnType));
    }

    private boolean containsCovariantOverridingMethod(List<MethodInstance> methods, MethodInstance impled) {
        for (MethodInstance mi : methods) {
            if (isCovariantOverride(impled.def().returnType().get(), mi.returnType())) {
                return true;
            }
        }
        return false;
    }

    private boolean existMethodInterfaceForCovariantOverride(Type t, Type type, MethodInstance mi, MethodInstance mdi) {
        if (t.typeEquals(type, tr.context())) {
            Type returnType = mdi.returnType();
            if (isCovariantOverride(mi.def().returnType().get(), returnType)) {
                    return true;
            }
        }
        t = Types.baseType(t);
        if (t.isClass()) {
            for (Type ti : t.toClass().interfaces()) {
                if (existMethodInterfaceForCovariantOverride(ti, type, mi, mdi)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void getImplMethodsForCovariantOverride(MethodInstance mi, List<MethodInstance> implMethods, List<Type> interfaces) {
        for (Type type : interfaces) {
            if (type.isClass()) {
                List<MethodInstance> imis = type.toClass().methods();
                for (MethodInstance imi : imis) {
                    if (!(imi.name().equals(mi.name()) && imi.formalTypes().size() == mi.formalTypes().size())) continue;

                    if (containsCovariantOverridingMethod(implMethods, imi)) continue;
                    
                    Type returnType = mi.returnType();
                    if (X10PrettyPrinterVisitor.isGenericOverloading) {
                        if (isCovariantOverride(imi.def().returnType().get(), returnType)) {
                            boolean containsParam = false;
                            List<Ref<? extends Type>> types = imi.def().formalTypes();
                            for (int i = 0;i < types.size(); ++i) {
                                if (
                                        mi.formalTypes().get(i).typeEquals(imi.formalTypes().get(i), tr.context())
                                        && containsTypeParam(imi.def().formalTypes().get(i).get())
                                ) {
                                    containsParam = true;
                                    break;
                                }
                            }
                            if (!containsParam) {
                                implMethods.add(imi);
                                break;
                            }
                        }
                    } else {
                        if (isCovariantOverride(imi.def().returnType().get(), returnType)) {
                            implMethods.add(imi);
                            break;
                        }
                    }
                }
                getImplMethodsForCovariantOverride(mi, implMethods, type.toClass().interfaces());
            }
        }
    }    

    public void generateDispatchMethods(X10ClassDef cd) {
        if (cd.flags().isInterface()) {
            return;
        }
        
        X10ClassType ct = cd.asType();
        
        List<MethodInstance> methods = ct.methods();
        List<Type> interfaces = ct.interfaces();
        List<Type> allInterfaces = new ArrayList<Type>();
        getAllInterfaces(interfaces, allInterfaces);

        Map<MethodInstance, List<MethodInstance>> dispatcherToMyMethods = CollectionFactory.newHashMap();
        for (MethodInstance myMethod : methods) {
            if (myMethod.flags().isStatic()) continue;

            List<MethodInstance> implementeds = myMethod.implemented(tr.context());
            List<MethodInstance> targets = new ArrayList<MethodInstance>();
            for (MethodInstance implemented : implementeds) {
                if (implemented.def().equals(myMethod.def())) continue;
                
                // only interface
                ContainerType st = implemented.def().container().get();
                if (st.isClass()) {
                    if (!st.toClass().flags().isInterface()) {
                        continue;
                    }
                    // N.B. @NativeRep'ed interface (e.g. Comparable) does not use dispatch method nor mangle method. primitives need to be boxed to allow instantiating type parameter.
                    if (isNativeRepedToJava(st)) {
                    	continue;
                    }
                }
                
                boolean containsTypeParam = false;
                List<Ref<? extends Type>> formalTypes = implemented.def().formalTypes();
                for (Ref<? extends Type> ref : formalTypes) {
                    Type type = ref.get();
                    if (containsTypeParam(type)) {
                        containsTypeParam = true;
                        break;
                    }
                }
                if (!containsTypeParam) continue;
                
                // only implements by itself not super class's
                boolean containsInterface = false;
                for (Type type : allInterfaces) {
                    if (type.typeEquals(implemented.container(), tr.context())) {
                        containsInterface = true;
                        break;
                    }
                }
                if (!containsInterface) continue;
                
                if (!containsSameSignature(targets, implemented)) {
                    targets.add(implemented);
                }
            }
            add(dispatcherToMyMethods, myMethod, targets);
        }
        
        List<MethodInstance> inheriteds = new ArrayList<MethodInstance>();
        List<MethodInstance> overrides = new ArrayList<MethodInstance>();
        getInheritedMethods(ct, inheriteds, overrides);
        for (MethodInstance mi : inheriteds) {
            List<MethodInstance> implMethods = new ArrayList<MethodInstance>();
            getImplMethodsForDispatch(mi, implMethods, interfaces);
            add(dispatcherToMyMethods, mi, implMethods);
        }
    
        Set<Entry<MethodInstance, List<MethodInstance>>> entrySet = dispatcherToMyMethods.entrySet();
        for (Entry<MethodInstance, List<MethodInstance>> entry : entrySet) {
            printDispatchMethod(entry.getKey(), entry.getValue());
        }
    }

    private static boolean hasSameSignature(MethodDef md, MethodDef td) {
        if (!md.name().equals(td.name())) return false;
        List<Ref<? extends Type>> mdFormalTypes = md.formalTypes();
        List<Ref<? extends Type>> tdFormalTypes = td.formalTypes();
        if (mdFormalTypes.size() != tdFormalTypes.size()) return false;
        for (int i = 0; i < mdFormalTypes.size(); ++i) {
            Type ft = mdFormalTypes.get(i).get();
            Type tt = tdFormalTypes.get(i).get();
            if (ft.isParameterType() && tt.isParameterType()) {}
            else if (ft.isClass() && tt.isClass() && ft.toClass().name().toString().equals(tt.toClass().name().toString())) {}
            else {
                return false;
            }
        }
        return true;
    }

    private static void add(Map<MethodInstance, List<MethodInstance>> dispatcherToMyMethods, MethodInstance myMethod, List<MethodInstance> targets) {
        for (MethodInstance target : targets) {
            boolean containsSameSignature = false;
            Set<Entry<MethodInstance, List<MethodInstance>>> entrySet = dispatcherToMyMethods.entrySet();
            for (Entry<MethodInstance, List<MethodInstance>> entry : entrySet) {
                MethodDef md = entry.getKey().def();
                MethodDef td = target.def();
                List<Ref<? extends Type>> mdFormalTypes = md.formalTypes();
                List<Ref<? extends Type>> tdFormalTypes = td.formalTypes();
                if (md.name().equals(td.name()) && mdFormalTypes.size() == tdFormalTypes.size()) {
                    containsSameSignature = true;
                    for (int i = 0; i < mdFormalTypes.size(); ++i) {
                        Type ft = mdFormalTypes.get(i).get();
                        Type tt = tdFormalTypes.get(i).get();
                        if (ft.isParameterType() && tt.isParameterType()) {}
                        else if (ft.isClass() && tt.isClass() && ft.toClass().name().toString().equals(tt.toClass().name().toString())) {}
                        else {
                            containsSameSignature = false;
                            break;
                        }
                    }
                    if (containsSameSignature) {
                        entry.getValue().add(myMethod);
                    }
                }
            }
            if (containsSameSignature) break;
            ArrayList<MethodInstance> mis = new ArrayList<MethodInstance>();
            mis.add(myMethod);
            dispatcherToMyMethods.put(target, mis);
        }
    }

    private void getImplMethodsForDispatch(MethodInstance mi, List<MethodInstance> implMethods, List<Type> interfaces) {
        for (Type type : interfaces) {
            if (type.isClass()) {
                List<MethodInstance> imis = type.toClass().methods();
                for (MethodInstance imi : imis) {
                    if (!(imi.name().equals(mi.name()) && imi.formalTypes().size() == mi.formalTypes().size())) continue;
                    if (containsSameSignature(implMethods, imi)) continue;
                    List<Ref<? extends Type>> types = imi.def().formalTypes();
                    for (int i = 0;i < types.size(); ++i) {
                        if (containsTypeParam(types.get(i).get()) ) {
                            implMethods.add(imi);
                            break;
                        }
                    }
                }
                getImplMethodsForDispatch(mi, implMethods, type.toClass().interfaces());
            }
        }
    }

    private static void getAllInterfaces(List<Type> interfaces, List<Type> allInterfaces) {
        allInterfaces.addAll(interfaces);
        for (Type type : interfaces) {
        	type = Types.baseType(type);
            if (type.isClass()) {
                List<Type> interfaces1 = type.toClass().interfaces();
                getAllInterfaces(interfaces1, allInterfaces);
            }
        }
    }

    private boolean hasSameSignature(MethodInstance mi2, MethodInstance mi1) {
        if (!mi2.name().equals(mi1.name())) return false;
        List<Type> formalTypes1 = mi1.formalTypes();
        List<Type> formalTypes2 = mi2.formalTypes();
        if (formalTypes1.size() != formalTypes2.size()) return false;
        // old and potentially buggy code
//        for (int i = 0; i < formalTypes1.size(); ++i) {
//            Type type1 = formalTypes1.get(i);
//            Type type2 = formalTypes2.get(i);
//            if (type1.typeEquals(type2, tr.context()) || (type1.isParameterType() && type2.isParameterType()))
//                return true;
//        }
//        return false;
        // correct code
        for (int i = 0; i < formalTypes1.size(); ++i) {
            Type type1 = formalTypes1.get(i);
            Type type2 = formalTypes2.get(i);
            if (!(type1.typeEquals(type2, tr.context()) || (type1.isParameterType() && type2.isParameterType())))
                return false;
        }
        return true;
    }
    private boolean containsSameSignature(List<MethodInstance> targets, MethodInstance mi1) {
        for (MethodInstance mi2 : targets) {
            if (hasSameSignature(mi2, mi1)) return true;
        }
        return false;
    }
    
    private void printDispatchMethod(MethodInstance dispatch, List<MethodInstance> mis, boolean isSpecialTypeForDispatcher, Type returnTypeForDispatcher) {
        
        MethodDef def = dispatch.def();
        Flags flags = dispatch.flags();
        X10MethodDef x10def = (X10MethodDef) def;
        Name[] names = null;
        boolean first = false;

        w.write("// dispatcher for " + def);
        w.newline();
        
        w.begin(0);
        w.write(flags.clearAbstract().clear(Flags.NATIVE).translateJava());
        
        // print return type
	// XTENLANG-2993
        if (!isSpecialTypeForDispatcher) {
            w.write(X10PrettyPrinterVisitor.JAVA_LANG_OBJECT);
        } else {
            printType(returnTypeForDispatcher, X10PrettyPrinterVisitor.supportTypeConstraintsWithErasure ? 0 : PRINT_TYPE_PARAMS);
        }
        
        w.write(" ");
        
        // decl
        // print method name
	// XTENLANG-2993
        printMethodName(def, true, true, isSpecialTypeForDispatcher, returnTypeForDispatcher);
        
        w.write("(");
        
        first = true;
        for (ParameterType p : x10def.typeParameters()) {
            if (!first) {
                w.write(", ");
            } else {
                first = false;
            }
            w.write("final ");
            w.write(X10PrettyPrinterVisitor.X10_RUNTIME_TYPE_CLASS);
            w.write(" ");
            w.write(mangleParameterType(p));
        }
        
	names = new Name[def.formalTypes().size()];
        for (int i = 0; i < def.formalTypes().size(); i++) {
            Type f = dispatch.formalTypes().get(i);
            if (!first || i != 0) {
                w.write(", ");
            }
            Type type = def.formalTypes().get(i).get();
            if (containsTypeParam(type)) {
                w.write("final ");
                if (type.isParameterType()) {
                    w.write(X10PrettyPrinterVisitor.JAVA_LANG_OBJECT);
                } else {
                    printType(type, 0);
                }
                
                w.write(" ");
                
                Name name = Name.make("a" + (i + 1));
                w.write(name.toString());
                
                w.write(", ");
                w.write("final ");
                w.write(X10PrettyPrinterVisitor.X10_RUNTIME_TYPE_CLASS);
                w.write(" ");
                Name name1 = Name.make("t" + (i + 1));
                w.write(name1.toString());
                
                names[i] = name1;
            } else {
                w.write("final ");
                printType(f, 0);
                
                w.write(" ");
                
                Name name = Name.make("a" + (i + 1));
                w.write(name.toString());
            }
        }
        
        w.end();
        w.write(")");

//        // Remove throw types support
//        if (!dispatch.throwTypes().isEmpty()) {
//            w.allowBreak(6);
//            w.write("throws ");
//            for (Iterator<Type> i = dispatch.throwTypes().iterator(); i.hasNext();) {
//                Type t = i.next();
//                printType(t, PRINT_TYPE_PARAMS);
//                if (i.hasNext()) {
//                    w.write(",");
//                    w.allowBreak(4, " ");
//                }
//            }
//        }
        
        w.write(" {");
        w.newline();
        
        for (MethodInstance mi : mis) {
            if (mis.size() != 1) {
                boolean first3 = true;
                for (int i = 0; i < names.length; i++) {
                    Name name = names[i];
                    if (name == null) continue;
                    if (first3) {
                        w.write("if (");
                        first3 = false;
                    }
                    else {
                        w.write(" && ");
                    }
                    
                    w.write(name.toString());
                    w.write(".equals(");
                    new RuntimeTypeExpander(this, mi.formalTypes().get(i)).expand();
                    w.write(")");
                }
                w.write(") {");
            }
            
            if (!mi.returnType().isVoid()) {
                w.write("return ");
            }
            
            boolean needParen = false;
	    // XTENLANG-2993
            if (!isSpecialTypeForDispatcher) {
            // this dispatch methods returns Object, so box if the underlying type is not boxed
            if (!isBoxedType(mi.returnType()) && !mi.returnType().isVoid()) {
                printBoxConversion(mi.returnType());
                w.write("(");
                needParen = true;
            }
            }
            
            // call
            printMethodName(mi.def(), false, false, false, null);
            
            // print the argument list
            w.write("(");
            
            boolean first2 = true;
            MethodInstance x10mi = mi;
            assert (x10mi.typeParameters().size() == x10def.typeParameters().size());
            for (Type t : x10def.typeParameters()) {
                if (!first2) {
                    w.write(", ");
                }
                first2 = false;
                new RuntimeTypeExpander(this, t).expand(tr);
            }
            
            for (int i = 0; i < mi.formalTypes().size(); i++) {
                Type f = mi.formalTypes().get(i);
                if (!first2 || i != 0) {
                    w.write(", ");
                }
                boolean closeParen = false;
                if (isBoxedType(def.formalTypes().get(i).get())) {
                    Type bf = Types.baseType(f);
                    if (!isBoxedType(f)) {
                        closeParen = printUnboxConversion(f);
                    } else if (!isMethodParameter(bf, mi, tr.context())) {
                        // TODO:CAST
                        w.write("(");
                        // XTENLANG-3010 : for boxed type, boxing isn't needed anyway. stop boxing for printing "int[]" for "Java.array[Int]".
                        printType(f, 0);
                        w.write(")");
                    }
                }
                
                Name name = Name.make("a" + (i + 1));
                w.write(name.toString());
                if (closeParen)
                    w.write(")");
            }
            w.write(")");
            if (needParen) {
                w.write(")");
            }
            w.write(";");
            // XTENLANG-2993
            if (!isSpecialTypeForDispatcher) {
            if (mi.returnType().isVoid()) {
                w.write("return null;");
            }
            }
            if (mis.size() != 1) {
                w.write("}");
            }
            w.newline();
        }
        
        if (mis.size() != 1) {
            w.write("throw new x10.lang.Error(\"not implemented dispatch mechanism based on contra-variant type completely\");");
        }
        
        w.write("}");
        w.newline();
        
    }

    
    // N.B. return type of dispatch is one of the return types which are merged into this dispatch method
    private void printDispatchMethod(MethodInstance dispatch, List<MethodInstance> mis) {
	printDispatchMethod(dispatch, mis, false, null);

	// XTENLANG-2993
	if (X10PrettyPrinterVisitor.generateSpecialDispatcher) {
	    Map<Type,List<MethodInstance>> specialTypeDispatchers = new HashMap<Type,List<MethodInstance>>();
	    for (MethodInstance mi : mis) {
	        Type type = Types.baseType(mi.returnType());
	        if (isSpecialTypeForDispatcher(type)) {
	            List<MethodInstance> specialTypeMethods = specialTypeDispatchers.get(type);
	            if (specialTypeMethods == null) {
	                specialTypeMethods = new ArrayList<MethodInstance>();
	                specialTypeDispatchers.put(type, specialTypeMethods);
	            }
	            specialTypeMethods.add(mi);
	        }
	    }
	    for (Map.Entry<Type, List<MethodInstance>> entry : specialTypeDispatchers.entrySet()) {
                printDispatchMethod(dispatch, entry.getValue(), true, entry.getKey());
	    }
	}
	
    }


    private static boolean isMethodParameter(Type bf, MethodInstance mi, Context context) {
        if (bf.isParameterType()) {
            Def def = ((ParameterType) bf).def().get();
            if (def instanceof MethodDef) {
                if (((MethodDef) def).container().get().typeEquals(mi.container(), context)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
	 * @param pos
	 * @param left
	 *            TODO
	 * @param name
	 * @param right
	 *            TODO
	 */
	public void generateStaticOrInstanceCall(Position pos, Expr left,
			Name name, Expr... right) {
		List<Expr> sargs = new ArrayList<Expr>();
		List<Type> stypes = new ArrayList<Type>();
		sargs.add(left);
		stypes.add(left.type());
		for (Expr e : right) {
			sargs.add(e);
			stypes.add(e.type());
		}
		List<Type> types = stypes.subList(1, stypes.size());
		List<Expr> args = sargs.subList(1, sargs.size());

		TypeSystem xts = tr.typeSystem();
		NodeFactory nf = tr.nodeFactory();
		try {
			MethodInstance mi = xts.findMethod(left.type(), xts.MethodMatcher(
					left.type(), name, types, tr.context()));
			tr.print(null, nf.Call(pos, left, nf.Id(pos, name), args)
					.methodInstance(mi).type(mi.returnType()), w);
		} catch (SemanticException e) {
			throw new InternalCompilerError(e.getMessage(), pos, e);
		}
	}

	public void printFormal(Translator tr, Node n, Formal f, boolean mustBox) {
		tr.print(n, f.flags(), w);
		printType(f.type().type(), PRINT_TYPE_PARAMS | (mustBox ? BOX_PRIMITIVES : 0));
		w.write(" ");

		Name name = f.name().id();
		if (name.toString().equals(""))
			tr.print(n, f.name().id(Name.makeFresh("a")), w);
		else {
			tr.print(n, f.name(), w);
		}
	}
	
	private boolean isNoArgumentType(Expr e) {
		while (e instanceof ParExpr_c) {
			e = ((ParExpr_c) e).expr();
			if (e == null) {
			    return true;
			}
		}
		
		Type exprTgtType = null;
		if (e instanceof Field_c) {
			exprTgtType = ((Field_c) e).target().type();
		} else if (e instanceof Call) {
			exprTgtType = ((Call) e).target().type();
		}
		
		if (exprTgtType != null) {
			if (exprTgtType instanceof X10ParsedClassType_c) {
				List<Type> typeArguments = ((X10ParsedClassType_c) exprTgtType).typeArguments();
				if (typeArguments != null && !typeArguments.isEmpty()) {
					return false;
				}
			}
		}
		return true;
	}

	private static Type actualType(Type type) {
	    if (type instanceof ConstrainedType) {
	        ConstrainedType ct = (ConstrainedType) type;
	        XVar selfVarBinding = ct.constraint().get().selfVarBinding();
                if (selfVarBinding != null && selfVarBinding instanceof Typed) {
                    // x10.lang.Object{self=="abc"} -> x10.lang.String
                    Type actualType = ((Typed) selfVarBinding).type();
                    // N.B. stop infinite recursion with x10.lang.String{self=="abc"} 
//                    actualType = actualType(actualType);
                    if (actualType instanceof ConstrainedType) {
                        actualType = ((ConstrainedType) actualType).baseType().get();
                    }
                    return actualType;
                }
                else {
                    return ct.baseType().get();
                }
	    }
	    return type;
	}
	
	// TODO:CAST
	public void coerce(Node parent, Expr e, Type expected) {
	    Type actual = e.type();

	    Type expectedBase = expected;
	    if (expectedBase instanceof ConstrainedType) {
	        expectedBase = ((ConstrainedType) expectedBase).baseType().get();
	    }
	    if (actual instanceof ConstrainedType) {
	        // XTENLANG-3085 if selfVarBinding is available, use its type
//	        actual = ((ConstrainedType) actual).baseType().get();
	        actual = actualType(actual);
	    }
	    CastExpander expander = new CastExpander(w, this, e);
	    if (actual.isNull() || e.isConstant() && !expectedBase.isParameterType() && !actual.isParameterType() && !isBoxedType(expectedBase)) {
	        prettyPrint(e, tr);
	    }
	    // for primitive
	    else if (needExplicitBoxing(actual)) {
	        if (actual.typeEquals(expectedBase, tr.context())) {
	            if (e instanceof X10Call && isBoxedType(Types.baseType(((X10Call) e).methodInstance().def().returnType().get()))) {
	                expander = expander.unboxTo(expectedBase);
	                expander.expand(tr);
	            }
	            else {
	                prettyPrint(e, tr);
	            }
	        } else {
	            if (isBoxedType(expectedBase)) {
	                // when expected type is T or Any, include an explicit boxing transformation
	                expander = expander.boxTo(actual).castTo(expectedBase);
	                expander.expand(tr);
	            }
	            else {
	                //cast to actual primitive to expected primitive to expected boxed primitive.
	                expander = expander.castTo(actual).castTo(expectedBase);
	                expander.expand(tr);
	            }
	        }
	    }
	    else {
	        if (actual.typeEquals(expected, tr.context()) && !(expected instanceof ConstrainedType) && !expectedBase.isParameterType() && !actual.isParameterType()) {
	            prettyPrint(e, tr);
	        }
	        else if (isString(actual) && !expectedBase.isParameterType() && !isString(expectedBase)
	            && !expectedBase.isAny()/*N.B. Any is NativeRep'ed to j.l.Object*/
                    && !expectedBase.isComparable(tr.typeSystem().String())/*N.B. x.l.Comparable is NativeRep'ed to j.l.Comparable*/
	            ) {
	        	expander = expander.boxTo(actual).castTo(expectedBase);
	        	expander.expand(tr);
	        }
	        else {
	            //cast eagerly
	            if (isBoxedType(actual) && !isBoxedType(expectedBase))
    	            expander = expander.unboxTo(expectedBase);
	            else {
	            	// java primitive arrays do not use boxed types
	            	final boolean isJavaArray = tr.typeSystem().isJavaArray(expectedBase);
    	            expander = expander.castTo(expectedBase, isJavaArray ? 0 : BOX_PRIMITIVES);
	            }
	            expander.expand(tr);
	        }
	    }
	}

	public boolean hasEffects(Receiver e) {
		if (e instanceof TypeNode)
			return false;
		if (e instanceof Local)
			return false;
		if (e instanceof Lit)
			return false;
		if (e instanceof Field) {
			Field f = (Field) e;
			return hasEffects(f.target());
		}
		if (e instanceof Unary) {
			Unary u = (Unary) e;
			if (u.operator() == Unary.BIT_NOT || u.operator() == Unary.NOT
					|| u.operator() == Unary.POS || u.operator() == Unary.NEG)
				return hasEffects(u.expr());
		}
		if (e instanceof Binary) {
			Binary b = (Binary) e;
			return hasEffects(b.left()) || hasEffects(b.right());
		}
		if (e instanceof Cast) {
			Cast c = (Cast) e;
			return hasEffects(c.expr());
		}
		if (e instanceof Instanceof) {
			Instanceof i = (Instanceof) e;
			return hasEffects(i.expr());
		}
		return true;
	}

	public static String convertToString(Object[] a) {
		StringBuffer s = new StringBuffer("[");
		for (int i = 0; i < a.length; ++i) {
			s.append(a[i].toString());
			if (i + 1 < a.length) {
				s.append(", ");
			}
		}
		s.append("]");
		return s.toString();
	}

	public static String convertToString(List<?> a) {
		StringBuffer s = new StringBuffer("[");
		final int size = a.size();
		for (int i = 0; i < size; ++i) {
			s.append(a.get(i).toString());
			if (i + 1 < size) {
				s.append(", ");
			}
		}
		s.append("]");
		return s.toString();
	}

	public void generateRTTInstance(X10ClassDef def) {
	    // for static inner classes that are compiled from closures
	    boolean isStaticFunType = def.name().toString().startsWith(ClosureRemover.STATIC_NESTED_CLASS_BASE_NAME);
	    boolean isVoidFun = false;
	    if (isStaticFunType) {
	        // Note: assume that the first interface in this X10ClassDef is a function type
	        Type type = def.interfaces().get(0).get();
            assert type instanceof FunctionType;
            isVoidFun = ((FunctionType) type).returnType().isVoid();
	    }

	    w.write("public static final x10.rtt.RuntimeType");
        w.write("<");
        printType(def.asType(), BOX_PRIMITIVES | NO_QUALIFIER);
        w.write("> " + X10PrettyPrinterVisitor.RTT_NAME + " = ");
        if (isStaticFunType) {
            // Option for closures
//            w.write("x10.rtt.RuntimeType");
            if (isVoidFun) {
                w.write("x10.rtt.StaticVoidFunType");
            } else {
                w.write("x10.rtt.StaticFunType");
            }
        } else {
            // Option for non-closures
//            w.write("x10.rtt.RuntimeType");
            w.write("x10.rtt.NamedType");
        }
        w.write(".<");
        printType(def.asType(), BOX_PRIMITIVES | NO_QUALIFIER);
        w.write(">");
        w.write(" make(");
        w.newline();
        if (!isStaticFunType) {
            // Option for non-closures
            w.write("\"" + def.asType() + "\", ");
        }
        w.write("/* base class */");
        printType(def.asType(), BOX_PRIMITIVES | NO_QUALIFIER);
        w.write(".class");
        
        if (def.variances().size() > 0) {
            boolean allInvariants = true;
            for (int i = 0; i < def.variances().size(); ++i) {
                if (def.variances().get(i) != ParameterType.Variance.INVARIANT) {
                    allInvariants = false;
                    break;
                }
            }
            if (allInvariants) {
                // use cached one to avoid creating array of Variance repeatedly
                w.write(", ");
                w.newline();
                w.write("/* variances */ x10.rtt.RuntimeType.INVARIANTS(" + def.variances().size() + ")");
            }
            else {
                for (int i = 0; i < def.variances().size(); ++i) {
                    w.write(", ");
                    w.newline();
                    if (i == 0) w.write("/* variances */ new x10.rtt.RuntimeType.Variance[] {");
                    ParameterType.Variance v = def.variances().get(i);
                    switch (v) {
                    case INVARIANT:
                        w.write("x10.rtt.RuntimeType.Variance.INVARIANT");
                        break;
                    case COVARIANT:
                        w.write("x10.rtt.RuntimeType.Variance.COVARIANT");
                        break;
                    case CONTRAVARIANT:
                        w.write("x10.rtt.RuntimeType.Variance.CONTRAVARIANT");
                        break;
                    }
                    if (i == def.variances().size() - 1) w.write("}");
                }
            }
        }
        w.newline();
        
        TypeSystem xts = tr.typeSystem();
        if (def.interfaces().size() > 0 || def.superType() != null) {
            w.write(", ");
            w.write("/* parents */ new x10.rtt.Type[] {");
            boolean needComma = false;
            for (int i = 0 ; i < def.interfaces().size(); i ++) {
                Type type = def.interfaces().get(i).get();
                // N.B. any X10 type is either Object or Struct that has Any as parents
                if (xts.isAny(type)) continue;
                if (needComma) {
                    w.write(", ");
                } else {
                    needComma = true;
                }
                printRTT(def, type);
            }
            if (def.superType() != null) {
                if (needComma) {
                    w.write(", ");
                } else {
                    needComma = true;
                }
                printRTT(def, def.superType().get());
            }
            if (def.isStruct()) {
                if (needComma) {
                    w.write(", ");
                } else {
                    needComma = true;
                }
                // Struct is not an X10 type, but it has RTT for runtime type checking such as instanceof
                w.write("x10.rtt.Types.STRUCT");
            }
            w.write("}");
        }
        w.newline();
        w.write(")");

        // override methods of RuntimeType as needed
        if (isStaticFunType) {
            // Option for closures
            /*
            // for static inner classes that are compiled from closures
            w.write("{");

            // Note: assume that the first parent in this RuntimeType is the parameterized type which corresponds to the above function type
            w.write("public String typeName(Object o) {");
            if (isVoidFun) {
                w.write("return ((x10.rtt.ParameterizedType<?>) getParents()[0]).typeNameForVoidFun();");
            } else {
                w.write("return ((x10.rtt.ParameterizedType<?>) getParents()[0]).typeNameForFun();");
            }
            w.write("}");
            
            w.write("}");
            */
        } else {
            // Option for non-closures
            /*
            w.write("{");

            w.write("public String typeName() {");
            w.write("return \"" + def.asType() + "\";");
            w.write("}");

            w.write("}");
            */
        }

        w.write(";");
        w.newline();
        
        if (!def.flags().isInterface()) {
            w.write("public x10.rtt.RuntimeType<?> " + X10PrettyPrinterVisitor.GETRTT_NAME + "() {");
            w.write("return " + X10PrettyPrinterVisitor.RTT_NAME + ";");
            w.write("}");
            w.newline();
            w.newline();
            
            if (!def.typeParameters().isEmpty()) {
              w.write("public x10.rtt.Type<?> " + X10PrettyPrinterVisitor.GETPARAM_NAME + "(int i) {");
              for (int i = 0; i < def.typeParameters().size(); i++) {
                  ParameterType pt = def.typeParameters().get(i);
                  w.write("if (i ==" + i + ")");
                  w.write("return ");
                  w.write(mangleParameterType(pt));
                  w.write(";");
              }
                w.write("return null;");
                w.write("}");
            }
            w.newline();
        }
	}

    private void printRTT(final X10ClassDef def, Type type) {
        type = Types.baseType(type);
        if (type.isClass()) {
            X10ClassType x10Type = type.toClass();
            if (x10Type.isJavaType()) {
            	w.write("x10.rtt.Types.getRTT(");
            	printType(x10Type, 0);
            	w.write(".class)");
                return;
            }
            X10ClassDef cd = x10Type.x10Def();
            String pat = getJavaRTTRep(cd);	// @NativeRep("java", JavaRep, n/a, JavaRTTRep)
            if (pat != null) {
                List<ParameterType> classTypeParams  = cd.typeParameters();
//                if (classTypeParams == null) classTypeParams = Collections.<ParameterType>emptyList();
                Iterator<ParameterType> classTypeParamsIter = null;
                if (classTypeParams != null) {
                    classTypeParamsIter = classTypeParams.iterator();
                }
                List<Type> classTypeArgs = x10Type.typeArguments();
                if (classTypeArgs == null) classTypeArgs = Collections.<Type>emptyList();
                HashMap<String,Object> components = new HashMap<String,Object>();
                int i = 0;
                Object component;
                String name;
                component = new TypeExpander(this, x10Type, PRINT_TYPE_PARAMS | BOX_PRIMITIVES);
                components.put(String.valueOf(i++), component);
                components.put("class", component);
                for (final Type at : classTypeArgs) {
                    if (classTypeParamsIter != null) {
                        name = classTypeParamsIter.next().name().toString();
                    } else {
                        name = null;
                    }
                    // XTENLANG-3010 : runtime type of Java.array[T] is defined as "Types.getRTT(#T[].class)" 
                    component = new TypeExpander(this, at, PRINT_TYPE_PARAMS);
                    // Note: to avoid changing number based key, we only register this with name based key 
//                    components.put(String.valueOf(i++), component);
                    if (name != null) { components.put(name, component); }
                    component = new TypeExpander(this, at, PRINT_TYPE_PARAMS | BOX_PRIMITIVES);
                    components.put(String.valueOf(i++), component);
                    if (name != null) { components.put(name+NATIVE_ANNOTATION_BOXED_REP_SUFFIX, component); }
                    if (Types.baseType(at).typeEquals(def.asType(), tr.context())) {
                        component = "x10.rtt.UnresolvedType.THIS";
                    } else if (at.isParameterType()) {
                        component = "x10.rtt.UnresolvedType.PARAM(" + getIndex(def.typeParameters(), (ParameterType) Types.baseType(at)) + ")";
                    } else {
                        component = new Expander(this) {
                            public void expand(Translator tr) {
                                printRTT(def, at);
                            }
                        };
                    }
                    components.put(String.valueOf(i++), component);
                    if (name != null) { components.put(name+NATIVE_ANNOTATION_RUNTIME_TYPE_SUFFIX, component); }
                }
                dumpRegex("NativeRep", components, tr, pat);
            }
            else if (x10Type.typeArguments() != null && x10Type.typeArguments().size() > 0) {
                w.write("x10.rtt.ParameterizedType.make(");
                if (x10Type instanceof FunctionType) {
                    FunctionType ft = (FunctionType) x10Type;
                    List<Type> args = ft.argumentTypes();
                    Type ret = ft.returnType();
                    if (ret.isVoid()) {
                        w.write(X10PrettyPrinterVisitor.X10_VOIDFUN_CLASS_PREFIX);
                    } else {
                        w.write(X10PrettyPrinterVisitor.X10_FUN_CLASS_PREFIX);
                    }
                    w.write("_" + ft.typeParameters().size());
                    w.write("_" + args.size());
                    w.write("." + X10PrettyPrinterVisitor.RTT_NAME);
                }
                else {
                    if (getJavaRep(cd) != null) {
                        w.write("new x10.rtt.RuntimeType(");
                        printType(x10Type, 0);
                        w.write(".class");
                        w.write(")");
                    }
                    else {
                        printType(x10Type, 0);
                        w.write("." + X10PrettyPrinterVisitor.RTT_NAME);
                    }
                }
                for (int i = 0; i < x10Type.typeArguments().size(); i++) {
                    w.write(", ");
                    Type ta = Types.baseType(x10Type.typeArguments().get(i));
                    if (ta.typeEquals(def.asType(), tr.context())) {
                        w.write("x10.rtt.UnresolvedType.THIS");
                    } else if (ta.isParameterType()) {
                        w.write("x10.rtt.UnresolvedType.PARAM(" + getIndex(def.typeParameters(), (ParameterType) ta) + ")");
                    } else {
                        printRTT(def, ta);
                    }
                }
                w.write(")");
            } else {
                new RuntimeTypeExpander(this, x10Type).expand(tr);
            }
        }
        else if (type instanceof NullType) {
            w.write("x10.rtt.Types.OBJECT");
        }
    }

    private static int getIndex(List<ParameterType> pts, ParameterType t) {
        for (int i = 0; i < pts.size(); i ++) {
            if (pts.get(i).name().equals(t.name())) {
                return i;
            }
        }
        throw new InternalCompilerError(""); // TODO
    }

    // not used
    private static boolean hasCustomSerializer(X10ClassDef def) {
        for (MethodDef md: def.methods()) {
            if ("serialize".equals(md.name().toString())) {
                if (md.formalTypes().size() == 0) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private static X10ConstructorDecl hasDefaultConstructor(X10ClassDecl n) {
        for (ClassMember member : n.body().members()) {
            if (member instanceof X10ConstructorDecl) {
                X10ConstructorDecl ctor = (X10ConstructorDecl) member;
                if (ctor.formals().size() == 0) {
                    return ctor;
                }
            }
        }
        return null;
    }
    
    // not used
    // copy of X10ClassDecl_c.createDefaultConstructor
    private static X10ConstructorDecl
    createDefaultConstructor(X10ClassDef thisType, X10NodeFactory_c xnf, X10ClassDecl n) {
        Position pos = Position.compilerGenerated(n.body().position());

        Ref<? extends Type> superType = thisType.superType();
        Stmt s1 = null;
        if (superType != null) {
            s1 = xnf.SuperCall(pos, Collections.<Expr>emptyList());
        }

        Stmt s2 = null; 
        List<TypeParamNode> typeFormals = Collections.<TypeParamNode>emptyList();
        List<Formal> formals = Collections.<Formal>emptyList();
        DepParameterExpr guard = null;

        List<PropertyDecl> properties = n.properties();
        
        if (! properties.isEmpty()) {
            // build type parameters.
            /*typeFormals = new ArrayList<TypeParamNode>(typeParameters.size());
            List<TypeNode> typeActuals = new ArrayList<TypeNode>(typeParameters.size());
            for (TypeParamNode tp : typeParameters) {
                typeFormals.add(xnf.TypeParamNode(pos, tp.name()));
                typeActuals.add(xnf.CanonicalTypeNode(pos, tp.type()));
            }*/

            formals = new ArrayList<Formal>(properties.size());
            List<Expr> actuals = new ArrayList<Expr>(properties.size());
            ChangePositionVisitor changePositionVisitor = new ChangePositionVisitor(pos);
            for (PropertyDecl pd: properties) {
                Id name = (Id) pd.name().position(pos);
                TypeNode typeNode = (TypeNode) pd.type().copy();
                Node newNode = typeNode.visit(changePositionVisitor);
                formals.add(xnf.Formal(pos, xnf.FlagsNode(pos, Flags.FINAL), (TypeNode) newNode, name));
                actuals.add(xnf.Local(pos, name));
            }

            guard = n.classInvariant();
            s2 = xnf.AssignPropertyCall(pos, Collections.<TypeNode>emptyList(), actuals);
            // TODO: add constraint on the return type
        }
        
        Block block = s2 == null ? (s1 == null ? xnf.Block(pos) : xnf.Block(pos, s1))
                : (s1 == null ? xnf.Block(pos, s2) : xnf.Block(pos, s1, s2));

        X10ClassType resultType = thisType.asType();
        // for Generic classes
        final List<ParameterType> typeParams = thisType.typeParameters();
        resultType = resultType.typeArguments((List) typeParams);
        X10CanonicalTypeNode returnType = (X10CanonicalTypeNode) xnf.CanonicalTypeNode(pos, resultType);

        X10ConstructorDecl cd = xnf.X10ConstructorDecl(pos,
                                                       xnf.FlagsNode(pos, Flags.PUBLIC),
                                                       xnf.Id(pos, TypeSystem.CONSTRUCTOR_NAME), 
                                                       returnType,
                                                       typeFormals,
                                                       formals,
                                                       guard, 
                                                       null, // offerType
                                                       block);
        return cd;
    }

	public void generateCustomSerializer(X10ClassDef def, X10ClassDecl_c n) {
	    X10CompilerOptions opts = (X10CompilerOptions) tr.job().extensionInfo().getOptions();
	    String fieldName = X10PrettyPrinterVisitor.SERIAL_DATA_FIELD_NAME;
	    w.write("// custom serializer");
	    w.newline();
        w.write("private transient x10.io.SerialData " + fieldName + ";");
        w.newline();
        w.write("private Object writeReplace() { ");
        if (!opts.x10_config.NO_TRACES && !opts.x10_config.OPTIMIZE) {
            w.write("if (" + X10PrettyPrinterVisitor.X10_RUNTIME_IMPL_JAVA_RUNTIME + ".TRACE_SER) { ");
            w.write("java.lang.System.out.println(\"Serializer: serialize() of \" + this + \" calling\"); ");
            w.write("} ");
        }
        w.write(fieldName + " = serialize(); ");
        if (!opts.x10_config.NO_TRACES && !opts.x10_config.OPTIMIZE) {
            w.write("if (" + X10PrettyPrinterVisitor.X10_RUNTIME_IMPL_JAVA_RUNTIME + ".TRACE_SER) { ");
            w.write("java.lang.System.out.println(\"Serializer: serialize() of \" + this + \" returned \" + " + fieldName + "); ");
            w.write("} ");
        }
        w.write("return this; }");
        w.newline();
        w.write("private Object readResolve() { return ");
	if (X10PrettyPrinterVisitor.generateFactoryMethod) {
	    printType(def.asType(), BOX_PRIMITIVES | NO_QUALIFIER);
	    w.write(".");
	    w.write(X10PrettyPrinterVisitor.CREATION_METHOD_NAME);
	} else {
	    assert X10PrettyPrinterVisitor.generateOnePhaseConstructor;
	    w.write("new ");
	    printType(def.asType(), BOX_PRIMITIVES | NO_QUALIFIER);
	}
        w.write("(");
        for (ParameterType type : def.typeParameters()) {
        	w.write(mangleParameterType(type) + ", ");
        }
        w.write(fieldName + "); }");
        w.newline();
        w.write("private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException {");
        w.newline();
        for (ParameterType type : def.typeParameters()) {
        	w.write("oos.writeObject(" + mangleParameterType(type) + ");");
            w.newline();
        }
        w.write("oos.writeObject(" + fieldName + "); }");
        w.newline();
        w.write("private void readObject(java.io.ObjectInputStream ois) throws java.io.IOException, java.lang.ClassNotFoundException {");
        w.newline();
        for (ParameterType type : def.typeParameters()) {
        	w.write(mangleParameterType(type) + " = (" + X10PrettyPrinterVisitor.X10_RUNTIME_TYPE_CLASS + ") ois.readObject();");
            w.newline();
        }
        w.write(fieldName + " = (x10.io.SerialData) ois.readObject(); }");
        w.newline();

        /*
        if (!hasCustomSerializer(def)) {
            w.write("// default custom serializer");
            w.newline();
//            w.write("public x10.io.SerialData serialize() { return new x10.io.SerialData(null, super.serialize()); }");
            w.write("public x10.io.SerialData serialize() { return super.serialize(); }");
            w.newline();
        }
        */

        if (!def.hasDeserializationConstructor(tr.context())) {
            w.write("// default deserialization constructor");
            w.newline();
            w.write("public " + def.name().toString() + "(");
            for (ParameterType type : def.typeParameters()) {
            	w.write("final x10.rtt.Type " + mangleParameterType(type) + ", ");
            }
            w.write("final x10.io.SerialData a) { ");

            // call super deserialization constructor
            Ref<? extends Type> superType0Ref = def.superType();
            if (superType0Ref != null) {
                Type superType0 = superType0Ref.get();
                X10ClassType superType;
                if (superType0 instanceof ConstrainedType) {
                    superType = ((ConstrainedType) superType0).baseType().get().toClass();
                } else {
                    superType = superType0.toClass();
                }
                w.write("super(");
                if (superType.typeArguments() != null) {
                    for (Type type : superType.typeArguments()) {
                        // pass rtt of the type
                    	// TODO mangle typa variable
                        new RuntimeTypeExpander(this, type).expand(tr);
                        w.write(", ");
                    }
                }
//                w.write("a.superclassData); ");
                w.write("a); ");
            }
            
            // initialize rtt
            for (ParameterType type : def.typeParameters()) {
            	w.write("this." + mangleParameterType(type) + " = " + mangleParameterType(type) + "; ");            		
            }
            
            // copy the rest of default (standard) constructor to initialize properties and fields
            X10ConstructorDecl ctor = hasDefaultConstructor(n);
            // we must have default constructor to initialize properties
//          assert ctor != null;
            /*
            if (ctor == null) {
                ctor = createDefaultConstructor(def, (X10NodeFactory_c) tr.nodeFactory(), n);
                // TODO apply FieldInitializerMover
            }
            */
            if (ctor != null) {
                // initialize properties and call field initializer
                Block_c body = (Block_c) ctor.body();
                if (body.statements().size() > 0) {
                    if (body.statements().get(0) instanceof ConstructorCall) {
                        body = (Block_c) body.statements(body.statements().subList(1, body.statements().size()));
                    }
                    // X10PrettyPrinterVisitor.visit(Block_c body)
                    String s = getJavaImplForStmt(body, tr.typeSystem());
                    if (s != null) {
                        w.write(s);
                    } else {
                        body.translate(w, tr);
                    }
                }
            }

            w.write("}");
            w.newline();
        }

        //_deserialize_body method
        w.write("public static x10.x10rt.X10JavaSerializable " + Emitter.DESERIALIZE_BODY_METHOD + "(");
        w.writeln(Emitter.mangleToJava(def.name()) + " $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { ");
        w.newline(4);
        w.begin(0);

        if (!opts.x10_config.NO_TRACES && !opts.x10_config.OPTIMIZE) {
            w.write("if (" + X10PrettyPrinterVisitor.X10_RUNTIME_IMPL_JAVA_RUNTIME + ".TRACE_SER) { ");
            w.write("java.lang.System.out.println(\"X10JavaSerializable: " + Emitter.DESERIALIZE_BODY_METHOD + "() of \" + "  + Emitter.mangleToJava(def.name()) + ".class + \" calling\"); ");
            w.writeln("} ");
        }

        ArrayList<String> params = new ArrayList<String>();
        w.writeln("x10.io.SerialData " +  fieldName +  " = (x10.io.SerialData) $deserializer.readRef();");
        for (ParameterType at : def.typeParameters()) {
            w.write(X10PrettyPrinterVisitor.X10_RUNTIME_TYPE_CLASS + " ");
            printType(at, PRINT_TYPE_PARAMS | BOX_PRIMITIVES);
            w.write(" = ( " + X10PrettyPrinterVisitor.X10_RUNTIME_TYPE_CLASS + " ) ");
            w.writeln("$deserializer.readRef();");
            params.add(mangleParameterType(at));
        }

        // XTENLANG-2974
        // N.B. we cannot reinstantiating $_obj since it may have already been serialized (and thus registered).
//        w.write("$_obj = (");
//        printType(def.asType(), BOX_PRIMITIVES | NO_QUALIFIER);
//        w.write(") ");
//        if (X10PrettyPrinterVisitor.generateFactoryMethod) {
//            printType(def.asType(), BOX_PRIMITIVES | NO_QUALIFIER);
//            w.write(".");
//            w.write(X10PrettyPrinterVisitor.CREATION_METHOD_NAME);
//        } else {
//            assert X10PrettyPrinterVisitor.generateOnePhaseConstructor;
//            w.write("new ");
//            printType(def.asType(), BOX_PRIMITIVES | NO_QUALIFIER);
//        }
//        String paramNames = "";
//        for (String param : params) {
//            paramNames = paramNames + param + ", ";
//        }
//        w.writeln("(" + paramNames + fieldName + ");");
        // set type objects to the fields of $_obj and initialize $_obj by calling $init(SerialData). 
        for (String param : params) {
            w.writeln("$_obj." + param + " = " + param + ";");
        }
        w.writeln("$_obj." + X10PrettyPrinterVisitor.CONSTRUCTOR_METHOD_NAME(def) + "(" + fieldName + ");");
        
        w.writeln("return $_obj;");
        w.end();
        w.newline();
        w.writeln("}");
        w.newline();

        // _deserializer  method
        w.writeln("public static x10.x10rt.X10JavaSerializable " + Emitter.DESERIALIZER_METHOD + "(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { ");
        w.newline(4);
        w.begin(0);
        w.write(Emitter.mangleToJava(def.name()) + " $_obj = new " + Emitter.mangleToJava(def.name()) + "(");
        if (X10PrettyPrinterVisitor.supportConstructorSplitting
            // XTENLANG-2830
            /*&& !ConstructorSplitterVisitor.isUnsplittable(Types.baseType(def.asType()))*/
            && !def.flags().isInterface()) {
            w.write("(" + X10PrettyPrinterVisitor.DUMMY_PARAM_TYPE1 + "[]) null");
            // N.B. in custom deserializer, initialize type params with null
            for (ParameterType typeParam : def.typeParameters()) {
                w.write(", (" + X10PrettyPrinterVisitor.X10_RUNTIME_TYPE_CLASS + ") null");
            }
            w.write(");");
            w.newline();
        } else {
            for (int i = 0; i < def.typeParameters().size(); i++) {
                w.write("null, ");
            }
            w.writeln("(x10.io.SerialData) null);");
        }
        w.writeln("$deserializer.record_reference($_obj);");
        w.writeln("return " + Emitter.DESERIALIZE_BODY_METHOD + "($_obj, $deserializer);");
        w.end();
        w.newline();
        w.writeln("}");
        w.newline();

        // _serialize_id()
        w.writeln("public short " + Emitter.SERIALIZE_ID_METHOD + "() {");
        w.newline(4);
        w.begin(0);
        w.writeln(" return " + Emitter.SERIALIZATION_ID_FIELD + ";");
        w.end();
        w.newline();
        w.writeln("}");
        w.newline();

        // _serialize()
        w.writeln("public void " + Emitter.SERIALIZE_METHOD + "(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {");
        w.newline(4);
        w.begin(0);
        if (!opts.x10_config.NO_TRACES && !opts.x10_config.OPTIMIZE) {
            w.write("if (" + X10PrettyPrinterVisitor.X10_RUNTIME_IMPL_JAVA_RUNTIME + ".TRACE_SER) { ");
            w.write("java.lang.System.out.println(\" CustomSerialization : " + Emitter.SERIALIZE_METHOD + " of \" + this + \" calling\"); ");
            w.writeln("} ");
        }

        w.writeln(fieldName + " = serialize(); ");
        w.writeln("$serializer.write(" + fieldName + ");");
        for (ParameterType at : def.typeParameters()) {
            w.writeln("$serializer.write(" + mangleParameterType(at) + ");");
        }
        w.end();
        w.newline();
        w.writeln("}");
        w.newline();

        // XTENLANG-2974 generate dummy $init(SerialData) for non-splittable type to simplify above _deserialize_body method.
        if (!X10PrettyPrinterVisitor.isSplittable(def.asType())) {
            w.writeln("// dummy 2nd-phase constructor for non-splittable type");
            w.writeln("public void " + X10PrettyPrinterVisitor.CONSTRUCTOR_METHOD_NAME(def) + "(" + X10PrettyPrinterVisitor.SERIAL_DATA +  " " + fieldName + ") {");
            w.newline(4);
            w.begin(0);
            w.writeln("throw new x10.lang.Error(\"dummy 2nd-phase constructor for non-splittable type should never be called.\");");
            w.end();
            w.newline();
            w.writeln("}");
            w.newline();
        }

	}

    // Emits the code to serialize the super class
    public void serializeSuperClass(TypeNode superClassNode) {
        X10CompilerOptions opts = (X10CompilerOptions) tr.job().extensionInfo().getOptions();
        // Check whether need to serialize super class
        if (superClassNode != null && superClassNode.type().isClass()) {
            if (!(superClassNode.type().toString().equals("x10.lang.Thread") ||
                    superClassNode.type().toString().equals("x10.lang.Object") ||
                    superClassNode.type().toString().equals("x10.lang.Any"))) {
                if (superClassNode.type().toClass().isJavaType()) {
                    w.write("$serializer.serializeClassUsingReflection(this, ");
                    printType(superClassNode.type(), BOX_PRIMITIVES);
                    w.writeln(".class);");
                } else {
                    w.write("super." + Emitter.SERIALIZE_METHOD + "($serializer);");
                    w.newline();
                }
            }
        }
    }

    // Emits the code to deserialize the super class
    public void deserializeSuperClass(TypeNode superClassNode) {
        // Check whether we need to deserialize the super class
        if (superClassNode != null && superClassNode.type().isClass()) {
            if (!(superClassNode.type().toString().equals("x10.lang.Thread") ||
                    superClassNode.type().toString().equals("x10.lang.Object") ||
                    superClassNode.type().toString().equals("x10.lang.Any"))) {
                // If the super class is a pure java class we need to deserialize it using reflection
                if (superClassNode.type().toClass().isJavaType()) {
                    w.write("$deserializer.deserializeClassUsingReflection(");
                    printType(superClassNode.type(), BOX_PRIMITIVES);
                    w.writeln(".class, $_obj, 0);");
                } else {
                    printType(superClassNode.type(), BOX_PRIMITIVES);
                    w.writeln("." + Emitter.DESERIALIZE_BODY_METHOD + "($_obj, $deserializer);");
                }
            }
        }
    }

    // TODO haszero
	public void generateZeroValueConstructor(X10ClassDef def, X10ClassDecl_c n) {
        w.write("// zero value constructor");
        w.newline();
        w.write("public " + def.name().toString() + "(");
        for (ParameterType type : def.typeParameters()) {
        	w.write("final x10.rtt.Type " + mangleParameterType(type) + ", ");
        }
        w.write("final " + X10PrettyPrinterVisitor.DUMMY_PARAM_TYPE1 + " $dummy) { ");

        /* struct does not have super type
        // call super zero value constructor
        Ref<? extends Type> superType0Ref = def.superType();
        if (superType0Ref != null) {
            Type superType0 = superType0Ref.get();
            X10ClassType superType;
            if (superType0 instanceof ConstrainedType) {
                superType = ((ConstrainedType) superType0).baseType().get().toClass();
            } else {
                superType = superType0.toClass();
            }
            w.write("super(");
            if (superType.typeArguments() != null) {
                for (Type type : superType.typeArguments()) {
                    // pass rtt of the type
                    new RuntimeTypeExpander(this, type).expand(tr);
                    w.write(", ");
                }
            }
            w.write("$dummy); ");
        }
        */
        
        // initialize rtt
        for (ParameterType type : def.typeParameters()) {
        	w.write("this." + mangleParameterType(type) + " = " + mangleParameterType(type) + "; ");
        }
        
        // initialize instance fields with zero value
        TypeSystem xts = def.typeSystem();
        for (polyglot.types.FieldDef field : def.fields()) {
            if (field.flags().isStatic()) continue;
            Type type = field.type().get();
            if (type instanceof ConstrainedType) {
                type = ((ConstrainedType) type).baseType().get();
            }
            String lhs = "this." + field.name().toString() + " = ";
            String zero = null;
//            // XTENLANG-2529 : use the third parameter of @NativeRep as an expression to get zero value
//            if (type.isClass() && getJavaZeroValueRep(type.toClass().x10Def()) != null) {
//                zero = getJavaZeroValueRep(type.toClass().x10Def());
//            } else
            if (xts.isStruct(type)) {
                if (xts.isUByte(type)) {
                    zero = "(x10.core.UByte) x10.rtt.Types.UBYTE_ZERO";
                } else if (xts.isUShort(type)) {
                    zero = "(x10.core.UShort) x10.rtt.Types.USHORT_ZERO";
                } else if (xts.isUInt(type)) {
                    zero = "(x10.core.UInt) x10.rtt.Types.UINT_ZERO";
                } else if (xts.isULong(type)) {
                    zero = "(x10.core.ULong) x10.rtt.Types.ULONG_ZERO";
                } else if (xts.isByte(type)) {
                    zero = "(byte) 0";
                } else if (xts.isShort(type)) {
                    zero = "(short) 0";
                } else if (xts.isInt(type)) {
                    zero = "0";
                } else if (xts.isLong(type)) {
                    zero = "0L";
                } else if (xts.isFloat(type)) {
                    zero = "0.0F";
                } else if (xts.isDouble(type)) {
                    zero = "0.0";
                } else if (xts.isChar(type)) {
                    zero = "(char) 0";
                } else if (xts.isBoolean(type)) {
                    zero = "false";
                } else {
                    // user-defined struct type
                    // for struct a.b.S[T], "new a.b.S(T, (java.lang.System) null);"
                    w.write(lhs); lhs = "";
                    w.write("new ");
                    printType(type, PRINT_TYPE_PARAMS);
                    w.write("(");
                    X10ParsedClassType_c pcType = (X10ParsedClassType_c) type;
                    if (pcType.typeArguments() != null) {
                        for (Type typeArgument : pcType.typeArguments()) {
                            // pass rtt of the type
                            new RuntimeTypeExpander(this, typeArgument).expand(tr);
                            w.write(", ");
                        }
                    }
                    w.write("$dummy); ");
                }
            } else if (xts.isParameterType(type)) {
                // for type parameter T, "(T) x10.rtt.Types.zeroValue(T);"
                ParameterType paramType = (ParameterType) type;
                zero = "(" + mangleParameterType(paramType) + ") x10.rtt.Types.zeroValue(" + mangleParameterType(paramType) + ")";
            } else {
                // reference (i.e. non-struct) type
                zero = "null";
            }
            if (zero != null) w.write(lhs + zero + "; ");
        }

        w.write("}");
        w.newline();
	}
	
	// N.B. these conditions are cut&pasted from printInlinedCode()
	public boolean isInlinedCall(X10Call c) {
	    TypeSystem xts = tr.typeSystem();
	    if (!isMethodInlineTarget(xts, Types.baseType(c.target().type()))) return false;
	    MethodInstance mi = c.methodInstance();
	    if (mi.name() == SettableAssign.SET || mi.name() == ClosureCall.APPLY) return true;
	    return false;
	}

    public boolean printInlinedCode(X10Call_c c) {
        TypeSystem xts = tr.typeSystem();
        Type ttype = Types.baseType(c.target().type());
        
        if (isMethodInlineTarget(xts, ttype)) {
            Type ptype = ttype.toClass().typeArguments().get(0);
            Name methodName = c.methodInstance().name();
            // e.g. rail.set(a,i) -> ((Object[]) rail.value)[i] = a or ((int[]/* primitive array */)rail.value)[i] = a
            if (methodName==SettableAssign.SET) {
                w.write("(");
                w.write("(");
                printType(ptype, 0);
                w.write("[]");
                w.write(")");
                c.print(c.target(), w, tr);
                w.write(".value");
                w.write(")");

                w.write("[");
                c.print(c.arguments().get(0), w, tr);
                w.write("]");

                w.write(" = ");
                c.print(c.arguments().get(1), w, tr);
                return true;
            }
            // e.g. rail.apply(i) -> ((String)((String[])rail.value)[i]) or ((int[])rail.value)[i]
            if (methodName==ClosureCall.APPLY) {
                
                w.write("(");
                w.write("(");
                printType(ptype, 0);
                w.write("[]");
                w.write(")");
                c.print(c.target(), w, tr);
                w.write(".value");
                w.write(")");

                w.write("[");
                c.print(c.arguments().get(0), w, tr);
                w.write("]");

                return true;
            }
        }

        return false;
    }

    public boolean isMethodInlineTarget(TypeSystem xts, Type ttype) {
        ttype = Types.baseType(ttype);
        if (!isIndexedMemoryChunk(ttype)) {
            return false;
        }
        if (!hasParams(ttype)) {
            return true;
        }
        List<Type> ta = ttype.toClass().typeArguments();
        if (ta != null && !ta.isEmpty() && !xts.isParameterType(ta.get(0))) {
            return true;
        }
        return false;
    }

    public boolean printNativeMethodCall(X10Call c) {
        TypeSystem xts = tr.typeSystem();
        MethodInstance mi = c.methodInstance();
        String pat = getJavaImplForDef(mi.x10Def());
    	if (pat != null) {
            Receiver target = c.target();
            Type t = target.type();
    	    boolean cast = xts.isParameterType(t) || hasParams(t);
    		CastExpander targetArg = new CastExpander(w, this, target);
    		if (cast) {
    		    targetArg = targetArg.castTo(mi.container(), BOX_PRIMITIVES | PRINT_TYPE_PARAMS);
    		    // in native methods of numerics (Int etc), the #this argument is expected to be unboxed
    		    if (needExplicitBoxing(mi.container()))
    		        targetArg = targetArg.unboxTo(mi.container());
    		}
    		
	        List<ParameterType> classTypeParams  = Collections.<ParameterType>emptyList();
    		List<Type> classTypeArguments  = Collections.<Type>emptyList();
    		if (mi.container().isClass() && !mi.flags().isStatic()) {
    		    X10ClassType ct = mi.container().toClass();
	            classTypeParams = ct.x10Def().typeParameters();
    		    classTypeArguments = ct.typeArguments();
	            if (classTypeParams == null) classTypeParams = Collections.<ParameterType>emptyList();
    		    if (classTypeArguments == null) classTypeArguments = Collections.<Type>emptyList();
    		}
    		
    		List<String> params = new ArrayList<String>();
    		List<CastExpander> args = new ArrayList<CastExpander>();
    		List<Expr> arguments = c.arguments();
    		for (int i = 0; i < arguments.size(); ++ i) {
    		    params.add(mi.def().formalNames().get(i).name().toString());
    		    Type ft = mi.def().formalTypes().get(i).get();
    		    Type at = arguments.get(i).type();
    		    if (isPrimitive(at) && xts.isParameterType(ft) && !isPrimitiveGenericMethod(mi)) {
    		        args.add(new CastExpander(w, this, arguments.get(i)).boxTo(at));
    		    }
    		    else if (isPrimitive(at)) {
    		        args.add(new CastExpander(w, this, arguments.get(i)).castTo(at, 0));
    		    }
    		    else {
    		        args.add(new CastExpander(w, this, arguments.get(i)));                                    
    		    }
    		}
    		
    		emitNativeAnnotation(pat, targetArg, mi.x10Def().typeParameters(), mi.typeParameters(), params, args, classTypeParams, classTypeArguments);
    		return true;
    	}
    	return false;
    }
    
    public boolean printNativeNew(X10New_c c, X10ConstructorInstance mi) {
        String pat = getJavaImplForDef(mi.x10Def());
        if (pat != null) {
	        List<ParameterType> classTypeParams  = Collections.<ParameterType>emptyList();
            List<Type> classTypeArguments  = Collections.<Type>emptyList();
            if (mi.container().isClass() && !mi.flags().isStatic()) {
                X10ClassType ct = mi.container().toClass();
	            classTypeParams = ct.x10Def().typeParameters();
                classTypeArguments = ct.typeArguments();
	            if (classTypeParams == null) classTypeParams = Collections.<ParameterType>emptyList();
                if (classTypeArguments == null) classTypeArguments = Collections.<Type>emptyList();
            }
            
    		List<String> params = new ArrayList<String>();
    		List<CastExpander> args = new ArrayList<CastExpander>();
            List<Expr> arguments = c.arguments();
            for (int i = 0; i < arguments.size(); ++ i) {
    		    params.add(mi.def().formalNames().get(i).name().toString());
                Type ft = c.constructorInstance().def().formalTypes().get(i).get();
                Type at = arguments.get(i).type();
                if (isPrimitive(at) && ft.isParameterType()) {
                    args.add(new CastExpander(w, this, arguments.get(i)).castTo(at, BOX_PRIMITIVES));
                }
                else if (isPrimitive(at)) {
                    args.add(new CastExpander(w, this, arguments.get(i)).castTo(at, 0));
                }
                else {
                    args.add(new CastExpander(w, this, arguments.get(i)));                                    
                }
            }
            
            emitNativeAnnotation(pat, null, Collections.<ParameterType>emptyList(), Collections.<Type>emptyList(), params, args, classTypeParams, classTypeArguments);
            return true;
        }
        return false;
    }
    
    // WIP XTENLANG-2680
	// print @Native annotation as method body
    public boolean printNativeMethodDecl(X10MethodDecl_c n) {
    	assert supportNativeMethodDecl;
    	
        TypeSystem xts = tr.typeSystem();
        MethodInstance mi = n.methodDef().asInstance();
        String pat = getJavaImplForDef(mi.x10Def());
        assert pat != null;
    	if (pat != null) {
////            Receiver target = c.target();
////            Type t = target.type();
//    		Type t = mi.container();
//    	    boolean cast = xts.isParameterType(t) || hasParams(t);
//    		CastExpander targetArg = new CastExpander(w, this, target);
//    		if (cast) {
//    		    targetArg = targetArg.castTo(mi.container(), BOX_PRIMITIVES | PRINT_TYPE_PARAMS);
//    		}
    		String targetArg = null;
    		if (!mi.flags().isStatic()) {
    			targetArg = "this";
    		}
    		
	        List<ParameterType> classTypeParams  = Collections.<ParameterType>emptyList();
    		List<Type> classTypeArguments  = Collections.<Type>emptyList();
    		if (mi.container().isClass() && !mi.flags().isStatic()) {
    		    X10ClassType ct = mi.container().toClass();
	            classTypeParams = ct.x10Def().typeParameters();
    		    classTypeArguments = ct.typeArguments();
	            if (classTypeParams == null) classTypeParams = Collections.<ParameterType>emptyList();
    		    if (classTypeArguments == null) classTypeArguments = Collections.<Type>emptyList();
    		}
    		
    		List<String> params = new ArrayList<String>();
//    		List<CastExpander> args = new ArrayList<CastExpander>();
    		List<String> args = new ArrayList<String>();
//    		List<Expr> arguments = c.arguments();
//    		for (int i = 0; i < arguments.size(); ++ i) {
    		for (int i = 0; i < mi.def().formalNames().size(); ++ i) {
    		    params.add(mi.def().formalNames().get(i).name().toString());
    		    /*
    		    Type ft = mi.def().formalTypes().get(i).get();
    		    Type at = arguments.get(i).type();
    		    if (isPrimitive(at) && xts.isParameterType(ft)) {
    		        args.add(new CastExpander(w, this, arguments.get(i)).castTo(at, BOX_PRIMITIVES));
    		    }
    		    else if (isPrimitive(at)) {
    		        args.add(new CastExpander(w, this, arguments.get(i)).castTo(at, 0));
    		    }
    		    else {
    		        args.add(new CastExpander(w, this, arguments.get(i)));                                    
    		    }
    		    */
    		    args.add(mi.def().formalNames().get(i).name().toString());
    		}
    		
    		w.write("{");
    		w.write("try {"); // XTENLANG-2686: handle Java exceptions inside @Native method
    		// always same?
    		if (!n.returnType().type().isVoid()) {
//    		if (!mi.returnType().isVoid()) {
    			w.write("return ");
    		}
    		
    		emitNativeAnnotation(pat, targetArg, mi.x10Def().typeParameters(), mi.typeParameters(), params, args, classTypeParams, classTypeArguments);
    		
    		w.write(";}");
    		w.write("catch (java.lang.Throwable $exc$) { throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$); } }"); // XTENLANG-2686
    		w.newline();
    		
    		return true;
    	}
    	return false;
    }

    public boolean printMainMethod(X10MethodDecl_c n) {
        if (HierarchyUtils.isMainMethod(n.methodDef(), tr.context())) {
            /*Expander throwsClause = new Inline(er, "");
            if (n.throwTypes().size() > 0) {
                List<Expander> l = new ArrayList<Expander>();
                for (TypeNode tn : n.throwTypes()) {
                    l.add(new TypeExpander(er, tn.type(), PRINT_TYPE_PARAMS));
                }
                throwsClause = new Join(er, "", "throws ", new Join(er, ", ", l));
            }*/

            Expander throwsClause = new Inline(this, "");
            List<Type> throwsTypes = AnnotationUtils.getThrowsTypes(n);
            if (throwsTypes.size() > 0) {
                List<Expander> l = new ArrayList<Expander>(throwsTypes.size());
                for (Type _throws : throwsTypes) {
                    l.add(new TypeExpander(this, _throws, 0));
                }
                throwsClause = new Join(this, "", "throws ", new Join(this, ", ", l));
            }

            // SYNOPSIS: #2.main(#0) #1    #0=args #1=body #2=mainclass #3=throws
            String regex = "public static class " + X10PrettyPrinterVisitor.MAIN_CLASS + " extends " + X10PrettyPrinterVisitor.X10_RUNTIME_IMPL_JAVA_RUNTIME + " {\n" +
                "private static final long serialVersionUID = 1L;\n" +
                "public static void main(java.lang.String[] args) #throws {\n" +
                    "// start native runtime\n" +
                    "new " + X10PrettyPrinterVisitor.MAIN_CLASS + "().start(args);\n" +
                "}\n" +
                "\n" +
                "// called by native runtime inside main x10 thread\n" +
                "public void runtimeCallback(final x10.array.Array<java.lang.String> args) #throws {\n" +
                    "// call the original app-main method\n" +
                    "#mainclass.main(args);\n" +
                "}\n" +
            "}\n" +
            "\n" +
            "// the original app-main method\n" +
            "public static void main(#args) #throws #body";
            Map<String,Object> components = new HashMap<String,Object>();
            Object component;
            int i = 0;
            component = n.formals().get(0);
//            components.put(String.valueOf(i++), component);
            components.put("args", component);
            component = n.body();
//            components.put(String.valueOf(i++), component);
            components.put("body", component);
            component = tr.context().currentClass().name();
//            components.put(String.valueOf(i++), component);
            components.put("mainclass", component);
            component = throwsClause;
//            components.put(String.valueOf(i++), component);
            components.put("throws", component);
            dumpRegex(X10PrettyPrinterVisitor.MAIN_CLASS, components, tr, regex);

            return true;
        }
        return false;
    }
    
}

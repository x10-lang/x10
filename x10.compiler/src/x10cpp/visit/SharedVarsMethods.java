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

package x10cpp.visit;
import polyglot.types.SemanticException;
import polyglot.types.QName;
import polyglot.ast.Expr;
import polyglot.visit.Translator;
import polyglot.types.JavaArrayType;
import polyglot.types.ClassType;
import polyglot.types.FieldInstance;
import polyglot.types.Flags;
import polyglot.types.LocalInstance;

import polyglot.types.Name;
import polyglot.types.ObjectType;
import polyglot.types.Package;
import polyglot.types.ParsedClassType;

import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.VarInstance;
import x10.types.X10ClassType;
import x10.types.MethodInstance;
import x10.types.X10ParsedClassType;
import polyglot.types.TypeSystem;

import x10cpp.types.X10CPPContext_c;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
/* A class containing all the static constants and variables that many
 * code generators share.
 */
public class SharedVarsMethods {
	
	private static int nextId_;
	/* to provide a unique name for local variables introduce in the templates */
	static Integer getUniqueId_() {
		return Integer.valueOf(nextId_++);
	}

	public static String getId() {
		return "__var" + getUniqueId_() + "__";
	}

	// FIXME: [IP] cannot enable refsAsPointers unless the library was also rebuilt
	// without refs and the Main template was changed
	static final boolean refsAsPointers = false;
	static final String VOID = "void";
	static final String VOID_PTR = "void*";
	public static final String SAVED_THIS = "saved_this";
    public static final String THIS = "this";
    public static final String STRUCT_THIS = "(*this)";
	static final String ALLOC = "_alloc";
	static final String CONSTRUCTOR = "_constructor";
	static final String MAKE = "_make";
	public static final String SERIALIZATION_ID_FIELD = "_serialization_id";
	public static final String NETWORK_ID_FIELD = "_network_id";
    public static final String SERIALIZATION_BUFFER = "::x10aux::serialization_buffer";
	static final String SERIALIZE_METHOD = "_serialize";
	static final String SERIALIZE_ID_METHOD = "_get_serialization_id";
	static final String NETWORK_ID_METHOD = "_get_network_id";
	public static final String SERIALIZE_BODY_METHOD = "_serialize_body";
    public static final String DESERIALIZATION_BUFFER = "::x10aux::deserialization_buffer";
	public static final String DESERIALIZE_METHOD = "_deserialize";
	static final String DESERIALIZER_METHOD = "_deserializer";
    static final String DESERIALIZE_BODY_METHOD = "_deserialize_body";
    public static final String DESERIALIZE_CUDA_METHOD = "_deserialize_cuda";
    public static final String POST_CUDA_METHOD = "_post_cuda";
    static final String STRUCT_EQUALS = "::x10aux::struct_equals";
    public static final String STRUCT_EQUALS_METHOD = "_struct_equals";
    public static final String REFERENCE_TYPE = "::x10::lang::Reference";
    public static final String CLASS_TYPE = "::x10::lang::X10Class";
    public static final String CLOSURE_TYPE = "::x10::lang::Closure";

    static final String VIM_MODELINE = "vim:tabstop=4:shiftwidth=4:expandtab";
    
    static final String ASYNC_INIT_VALS_KEY = "AYSNC_LOCAL_INITS";

    public static final String CPP_NATIVE_STRING = "c++";
    public static final String CUDA_NATIVE_STRING = "cuda";

    public static String chevrons(String type) {
        return (type.startsWith(":") ? "< ": "<") + type + (type.endsWith(">")?" >":">");
    }

	public static String make_ref(String type) {
	    return type+"*";
	}
	
	public static String make_captured_lval(Type type) {
	    if (type.isClass() && !((X10ClassType)type.toClass()).isX10Struct()) {
	        return "x10aux::captured_ref_lval"+chevrons(Emitter.translateType(type, false));
	    } else {
            return "x10aux::captured_struct_lval"+chevrons(Emitter.translateType(type, false));
	    }
	}

	static String closure_name(String prefix, int id) {
		return prefix + id;
	}
	static String args_name(String prefix, int id) {
		return closure_name(prefix, id) + "_args";
	}
}

// vim: shiftwidth=4:tabstop=4:expandtab

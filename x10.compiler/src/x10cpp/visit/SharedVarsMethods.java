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

package x10cpp.visit;
import polyglot.types.SemanticException;
import polyglot.types.QName;
import polyglot.ast.Expr;
import polyglot.visit.Translator;
import polyglot.types.ArrayType;
import polyglot.types.ClassType;
import polyglot.types.FieldInstance;
import polyglot.types.Flags;
import polyglot.types.LocalInstance;
import polyglot.types.MethodInstance;
import polyglot.types.Name;
import polyglot.types.Package;
import polyglot.types.ParsedClassType;
import polyglot.types.ReferenceType;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.VarInstance;
import x10.ast.ConstantDistMaker_c;
import x10.types.X10MethodInstance;
import x10.types.X10ParsedClassType;
import x10.types.X10TypeSystem;
import x10.types.X10TypeSystem_c;
import x10cpp.types.X10CPPContext_c;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
/* A class containing all the static constants and variables that many
 * code generators share.
 */
public class SharedVarsMethods {
	
	public static final boolean WARN_NONSPMD_EXTERN = true;
	private static int nextId_;
	/* to provide a unique name for local variables introduce in the templates */
	static Integer getUniqueId_() {
		return new Integer(nextId_++);
	}

	public static String getId() {
		return "__var" + getUniqueId_() + "__";
	}
	/* FIXME: -- SPMD compilation --
	static boolean isUnique(Expr distribution) {
		return ((X10ParsedClassType) distribution.type()).isUniqueDist();
	}
	*/

	static final boolean asyncSwitchRequired = true;
	static final boolean asyncRegistrationRequired = false;
	static final boolean arrayCopySwitchRequired = true;
	static final boolean useStructsForArrayInitArgs = true;
	static final boolean useStructsForClosureArgs = true;
	static final boolean useStructsForAsyncArgs = false;
	static final boolean useStructsForArrayCopyArgs = false;
	static final boolean useIndicesForArrayCopy = false;
	static final boolean inlineArrayAccesses = true;
	static final boolean arraysAsRefs = true;
	static final boolean ignoreExceptions = false;
	// FIXME: [IP] cannot enable refsAsPointers unless the library was also rebuilt
	// without refs and the Main template was changed
	static final boolean refsAsPointers = false;
	static final String VOID = "void";
	static final String VOID_PTR = "void*";
	public static final String SAVED_THIS = "saved_this";
    public static final String THIS = "this";
	static final String INSTANCE_INIT = "_instance_init"; // instance field initialisers
	static final String CONSTRUCTOR = "_constructor";
	static final String MAKE = "_make";
	public static final String SERIALIZATION_ID_FIELD = "_serialization_id";
	public static final String SERIALIZATION_MARKER = "x10aux::SERIALIZATION_MARKER";
    public static final String SERIALIZATION_BUFFER = "x10aux::serialization_buffer";
	static final String SERIALIZE_METHOD = "_serialize";
	static final String SERIALIZE_ID_METHOD = "_get_serialization_id";
	public static final String SERIALIZE_BODY_METHOD = "_serialize_body";
    public static final String DESERIALIZATION_BUFFER = "x10aux::deserialization_buffer";
	public static final String DESERIALIZE_METHOD = "_deserialize";
	static final String DESERIALIZER_METHOD = "_deserializer";
    static final String DESERIALIZE_BODY_METHOD = "_deserialize_body";
    public static final String DESERIALIZE_CUDA_METHOD = "_deserialize_cuda";
    static final String STRUCT_EQUALS = "x10aux::struct_equals";
    static final String STRUCT_EQUALS_METHOD = "_struct_equals";

    static final String VIM_MODELINE = "vim:tabstop=4:shiftwidth=4:expandtab";

    public static final String CPP_NATIVE_STRING = "c++";
    public static final String CUDA_NATIVE_STRING = "cuda";

    public static String chevrons(String type) {
        return "<" + type + (type.endsWith(">")?" ":"")+">";
    }

	public static String make_ref(String type) {
		if (refsAsPointers)
			return type+"*";
		return "x10aux::ref"+chevrons(type);
	}
	static String closure_name(String prefix, int id) {
		return prefix + id;
	}
	static String args_name(String prefix, int id) {
		return closure_name(prefix, id) + "_args";
	}

	/* FIXME: -- SPMD compilation --
	static boolean isLocal(Expr distribution) {
		return distribution instanceof ConstantDistMaker_c ||
		((X10ParsedClassType) distribution.type()).isConstantDist();
	}
	*/
	static final ArrayList<ReferenceType> knownSafeClasses = new ArrayList<ReferenceType>();
	static final ArrayList<X10MethodInstance> knownSafeMethods = new ArrayList<X10MethodInstance>();
	static final ArrayList<FieldInstance> knownSafeFields = new ArrayList<FieldInstance>();
	static void populateKnownSafeEntries(Translator tr){
		populateKnownSafeClasses(tr);
		populateKnownSafeMethodsAndFields(tr);
	}
	static void populateKnownSafeClasses(Translator tr){
		X10CPPContext_c context = (X10CPPContext_c) tr.context();
		X10TypeSystem ts = (X10TypeSystem) tr.typeSystem();
		if (knownSafeClasses.size() == 0) {
			try {
				ReferenceType j_l_Math = (ReferenceType) ts.forName(QName.make("java.lang.Math"));
				knownSafeClasses.add(j_l_Math);
				ReferenceType j_i_DIS = (ReferenceType) ts.forName(QName.make("java.io.DataInputStream"));
				knownSafeClasses.add(j_i_DIS);
				ReferenceType j_i_FIS = (ReferenceType) ts.forName(QName.make("java.io.FileInputStream"));
				knownSafeClasses.add(j_i_DIS);
				ReferenceType j_i_BAOS = (ReferenceType) ts.forName(QName.make("java.io.ByteArrayOutputStream"));
				knownSafeClasses.add(j_i_BAOS);
				ReferenceType j_i_PS = (ReferenceType) ts.forName(QName.make("java.io.PrintStream"));
				knownSafeClasses.add(j_i_PS);
				ReferenceType j_u_R = (ReferenceType) ts.forName(QName.make("java.util.Random"));
				knownSafeClasses.add(j_u_R);
			} catch (SemanticException e) { assert (false); }
		}
	}	
	static void populateKnownSafeMethodsAndFields(Translator tr){
		X10CPPContext_c context = (X10CPPContext_c) tr.context();
		X10TypeSystem_c ts = (X10TypeSystem_c) tr.typeSystem();
		if (knownSafeMethods.size() == 0 && knownSafeFields.size() == 0) {
			try {
//				Type[] II = { ts.Int(), ts.Int() };
//				Type[] JJ = { ts.Long(), ts.Long() };
//				Type[] FF = { ts.Float(), ts.Float() };
//				Type[] DD = { ts.Double(), ts.Double() };
//				knownSafeMethods.add(ts.findMethod(j_l_Math, "max", Arrays.asList(II), context.currentClass()));
//				knownSafeMethods.add(ts.findMethod(j_l_Math, "max", Arrays.asList(JJ), context.currentClass()));
//				knownSafeMethods.add(ts.findMethod(j_l_Math, "max", Arrays.asList(FF), context.currentClass()));
//				knownSafeMethods.add(ts.findMethod(j_l_Math, "max", Arrays.asList(DD), context.currentClass()));
//				knownSafeMethods.add(ts.findMethod(j_l_Math, "min", Arrays.asList(II), context.currentClass()));
//				knownSafeMethods.add(ts.findMethod(j_l_Math, "min", Arrays.asList(JJ), context.currentClass()));
//				knownSafeMethods.add(ts.findMethod(j_l_Math, "min", Arrays.asList(FF), context.currentClass()));
//				knownSafeMethods.add(ts.findMethod(j_l_Math, "min", Arrays.asList(DD), context.currentClass()));
				Type x_l_place = ts.Place();
				knownSafeFields.add(ts.findField(x_l_place, ts.FieldMatcher(x_l_place, Name.make("MAX_PLACES"), context)));
				Type x_l_region = ts.Region();
				knownSafeMethods.add(ts.findMethod(x_l_region, ts.MethodMatcher(x_l_region, Name.make("toDistribution"), Collections.EMPTY_LIST, context)));
				knownSafeFields.add(ts.findField(x_l_region, ts.FieldMatcher(x_l_region, Name.make("factory"), context)));
				Type x_l_dist = ts.Dist();
				Type[] X_L_P = { x_l_place };
				knownSafeMethods.add(ts.findMethod(x_l_dist, ts.MethodMatcher(x_l_dist, Name.make("restriction"), Arrays.asList(X_L_P), context)));
				knownSafeFields.add(ts.findField(x_l_dist, ts.FieldMatcher(x_l_dist, Name.make("factory"), context)));
				knownSafeFields.add(ts.findField(x_l_dist, ts.FieldMatcher(x_l_dist, Name.make("UNIQUE"), context)));
				ReferenceType x_l_dist_factory = (ReferenceType) ts.forName(QName.make("x10.lang.dist.factory"));
				Type[] X_L_R = { x_l_region };
				knownSafeMethods.add(ts.findMethod(x_l_dist, ts.MethodMatcher(x_l_dist_factory, Name.make("block"), Arrays.asList(X_L_R), context)));
			} catch (SemanticException e) { assert (false); }
		}
	}	
	
	static final ArrayList<X10MethodInstance> knownInlinableMethods = new ArrayList<X10MethodInstance>();

	static void populateIninableMethodsIfEmpty(Translator tr) {
		X10CPPContext_c context = (X10CPPContext_c) tr.context();
		X10TypeSystem_c ts = (X10TypeSystem_c) tr.typeSystem();
		if (knownInlinableMethods.size() == 0) {
			try {
				Type x_l_Runtime = (Type) ts.forName(QName.make("x10.lang.Runtime"));
				Type[] A_I_A_I_I = { ts.Array(), ts.Int(), ts.Array(), ts.Int(), ts.Int() };
				knownInlinableMethods.add(ts.findMethod(x_l_Runtime, ts.MethodMatcher(x_l_Runtime, Name.make("arrayCopy"), Arrays.asList(A_I_A_I_I), context)));
				Type[] A_A = { ts.Array(), ts.Array() };
				knownInlinableMethods.add(ts.findMethod(x_l_Runtime, ts.MethodMatcher(x_l_Runtime, Name.make("arrayCopy"), Arrays.asList(A_A), context)));
				// TODO
//				ReferenceType x_l_region = ts.region();
//				Type[] A_R_A_R = { array, x_l_region, array, x_l_region };
//				knownArrayCopyMethods.add(ts.findMethod(x_l_Runtime, "arrayCopy", Arrays.asList(A_R_A_R), context.currentClass()));
			} catch (SemanticException e) { //FIXME: assert (false); 
			}
		}
	}
}

// vim: shiftwidth=4:tabstop=4:expandtab

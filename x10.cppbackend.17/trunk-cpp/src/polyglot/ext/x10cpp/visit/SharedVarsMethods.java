package polyglot.ext.x10cpp.visit;
import polyglot.types.SemanticException;
import polyglot.ast.Expr;
import polyglot.visit.Translator;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.ext.x10.ast.ConstantDistMaker_c;
import polyglot.ext.x10.types.X10ParsedClassType;
import polyglot.ext.x10cpp.types.X10CPPContext_c;
import polyglot.types.ArrayType;
import polyglot.types.ClassType;
import polyglot.types.FieldInstance;
import polyglot.types.Flags;
import polyglot.types.LocalInstance;
import polyglot.types.MethodInstance;
import polyglot.types.Package;
import polyglot.types.ParsedClassType;
import polyglot.types.ReferenceType;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.VarInstance;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
/* A class containing all the static constants and variables that many
 * code generators share.
 */
public class SharedVarsMethods{
	
	public static final boolean WARN_NONSPMD_EXTERN = true;
	private static int nextId_;
	/* to provide a unique name for local variables introduce in the templates */
	static Integer getUniqueId_() {
		return new Integer(nextId_++);
	}

	public static String getId() {
		return "__var" + getUniqueId_() + "__";
	}
	static boolean isUnique(Expr distribution) {
		return ((X10ParsedClassType) distribution.type()).isUniqueDist();
	}
	static final ArrayList knownSpecialPackages = new ArrayList();
	static void populateKnownSpecialPackages(X10TypeSystem xts){
		if (knownSpecialPackages.size() == 0) {
			try {
				Package x_l = xts.packageForName("x10.lang");
				knownSpecialPackages.add(x_l);
				Package j_l = xts.packageForName("java.lang");
				knownSpecialPackages.add(j_l);
				Package j_i = xts.packageForName("java.io");
				knownSpecialPackages.add(j_i);
				Package j_u = xts.packageForName("java.util");
				knownSpecialPackages.add(j_u);
				Package j_u_c = xts.packageForName("java.util.concurrent");
				knownSpecialPackages.add(j_u_c);
				Package j_u_c_a = xts.packageForName("java.util.concurrent.atomic");
				knownSpecialPackages.add(j_u_c_a);
				Package x = xts.packageForName("x10");
				knownSpecialPackages.add(x);
				Package x_c = xts.packageForName("x10.compilergenerated");
				knownSpecialPackages.add(x_c);
			} catch (SemanticException e) { assert (false); }
		}

	}

	static final boolean asyncSwitchRequired = true;
	static final boolean asyncRegistrationRequired = false;
	static final boolean arrayCopySwitchRequired = true;
	static final boolean useStructsForArrayInitArgs = true;
	static final boolean useStructsForAsyncArgs = false;
	static final boolean useStructsForArrayCopyArgs = false;
	static final boolean useIndicesForArrayCopy = false;
	static final boolean inlineArrayAccesses = true;
	static final boolean arraysAsRefs = true;
	static final boolean ignoreExceptions = false;
	// FIXME: [IP] cannot enable refsAsPointers unless the library was also rebuilt
	// without refs and the Main template was changed
	static final boolean refsAsPointers = false;
	static final String ASYNC_PREFIX = "async__";
	static final String INIT_PREFIX = "__init__";
	static final String ARRAY_COPY_PREFIX = "array_copy__";
	static final String CLOSURE_WRAPPER_PREFIX = "__closure__";
	static final String ASYNC_SWITCH = "AsyncSwitch";
	static final String ARRAY_COPY_SWITCH = "ArrayCopySwitch";
	static final String VOID = "void";
	static final String VOID_PTR = "void*";
	static final String SAVED_THIS = "saved_this";
	static final String GLOBAL_STATE = "GLOBAL_STATE";
	static final String THIS = "this";
	static final String RUN_INITIALIZERS = "_run_initializers";
	static final String STATIC_INIT = "__static_init";
	static final int MAX_OBJECT_ARRAY_INIT = 6;
	static final String RAW_ARRAY = "raw";
	static final String RAW_ADJUSTED = "rawRegion";
	static final boolean eagerArrayCopyNotification = true;
	static final String GET_SOURCE_ARRAY = "getSourceArray";
	static final String GET_DEST_ARRAY = "getDestArray";
	static final String POST_COPY_RUN = "postCopyRun";
	static final boolean optimizePrimitiveBroadcasts = true;
	static final String SERIALIZATION_ID_FIELD = "SERIALIZATION_ID";
	static final String SERIALIZATION_MARKER = "x10::SERIALIZATION_MARKER";
	static final String SERIALIZATION_BUFFER = "x10::serialization_buffer";
	static final String SERIALIZE_METHOD = "_serialize";
	static final String SERIALIZE_FIELDS_METHOD = "_serialize_fields";
	static final String DESERIALIZE_FIELDS_METHOD = "_deserialize_fields";

	static String make_ref(String type) {
		if (refsAsPointers)
			return type+"*";
		return "x10::ref<"+type+(type.endsWith(">")?" ":"")+">";
	}
	static String closure_name(String prefix, int id) {
		return prefix + id;
	}
	static String args_name(String prefix, int id) {
		return closure_name(prefix, id) + "_args";
	}

	static boolean isLocal(Expr distribution) {
		return distribution instanceof ConstantDistMaker_c ||
		((X10ParsedClassType) distribution.type()).isConstantDist();
	}
	static final ArrayList knownSafeClasses = new ArrayList();
	static final ArrayList knownSafeMethods = new ArrayList();
	static final ArrayList knownSafeFields = new ArrayList();
	static void populateKnownSafeEntries(Translator tr){
		populateKnownSafeClasses(tr);
		populateKnownSafeMethodsAndFields(tr);
	}
	static void populateKnownSafeClasses(Translator tr){
		X10CPPContext_c context = (X10CPPContext_c) tr.context();
		X10TypeSystem ts = (X10TypeSystem) tr.typeSystem();
		if (knownSafeClasses.size() == 0) {
			try {
				ReferenceType j_l_Math = (ReferenceType) ts.forName("java.lang.Math");
				knownSafeClasses.add(j_l_Math);
				ReferenceType j_i_DIS = (ReferenceType) ts.forName("java.io.DataInputStream");
				knownSafeClasses.add(j_i_DIS);
				ReferenceType j_i_FIS = (ReferenceType) ts.forName("java.io.FileInputStream");
				knownSafeClasses.add(j_i_DIS);
				ReferenceType j_i_BAOS = (ReferenceType) ts.forName("java.io.ByteArrayOutputStream");
				knownSafeClasses.add(j_i_BAOS);
				ReferenceType j_i_PS = (ReferenceType) ts.forName("java.io.PrintStream");
				knownSafeClasses.add(j_i_PS);
				ReferenceType j_u_R = (ReferenceType) ts.forName("java.util.Random");
				knownSafeClasses.add(j_u_R);
			} catch (SemanticException e) { assert (false); }
		}
	}	
	static void populateKnownSafeMethodsAndFields(Translator tr){
		X10CPPContext_c context = (X10CPPContext_c) tr.context();
		X10TypeSystem ts = (X10TypeSystem) tr.typeSystem();
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
				ReferenceType x_l_place = ts.place();
				knownSafeFields.add(ts.findField(x_l_place, "MAX_PLACES", context.currentClass()));
				ReferenceType x_l_region = ts.region();
				knownSafeMethods.add(ts.findMethod(x_l_region, "toDistribution", Collections.EMPTY_LIST, context.currentClass()));
				knownSafeFields.add(ts.findField(x_l_region, "factory", context.currentClass()));
				ReferenceType x_l_dist = ts.distribution();
				Type[] X_L_P = { x_l_place };
				knownSafeMethods.add(ts.findMethod(x_l_dist, "restriction", Arrays.asList(X_L_P), context.currentClass()));
				knownSafeFields.add(ts.findField(x_l_dist, "factory", context.currentClass()));
				knownSafeFields.add(ts.findField(x_l_dist, "UNIQUE", context.currentClass()));
				ReferenceType x_l_dist_factory = (ReferenceType) ts.forName("x10.lang.dist.factory");
				Type[] X_L_R = { x_l_region };
				knownSafeMethods.add(ts.findMethod(x_l_dist_factory, "block", Arrays.asList(X_L_R), context.currentClass()));
			} catch (SemanticException e) { assert (false); }
		}
	}	
	
	static final ArrayList knownIgnoredExternMethods = new ArrayList();
	static final ArrayList knownInlinableMethods = new ArrayList();

	static void populateIninableMethodsIfEmpty(Translator tr) {
		X10CPPContext_c context = (X10CPPContext_c) tr.context();
		X10TypeSystem ts = (X10TypeSystem) tr.typeSystem();
		if (knownInlinableMethods.size() == 0) {
			try {
				ReferenceType x_l_Runtime = (ReferenceType) ts.forName("x10.lang.Runtime");
				Type[] A_I_A_I_I = { ts.array(), ts.Int(), ts.array(), ts.Int(), ts.Int() };
				knownInlinableMethods.add(ts.findMethod(x_l_Runtime, "arrayCopy", Arrays.asList(A_I_A_I_I), context.currentClass()));
				Type[] A_A = { ts.array(), ts.array() };
				knownInlinableMethods.add(ts.findMethod(x_l_Runtime, "arrayCopy", Arrays.asList(A_A), context.currentClass()));
				// TODO
//				ReferenceType x_l_region = ts.region();
//				Type[] A_R_A_R = { array, x_l_region, array, x_l_region };
//				knownArrayCopyMethods.add(ts.findMethod(x_l_Runtime, "arrayCopy", Arrays.asList(A_R_A_R), context.currentClass()));
			} catch (SemanticException e) { assert (false); }
		}
	}
}

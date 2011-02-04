package x10.types.constraints;

import java.util.List;

import polyglot.types.FieldDef;
import polyglot.types.LocalDef;
import polyglot.types.MethodDef;
import polyglot.types.Name;
import polyglot.types.Type;
import polyglot.types.VarDef;
import x10.constraint.XField;
import x10.constraint.XTerm;
import x10.constraint.XVar;
import x10.types.MethodInstance;
import x10.types.X10LocalDef;

/**
 * Analogous to x10.constraint.XTerm. Provides static methods
 * for generating various kinds of XTerms that know about type
 * information.
 * @author vijay
 *
 */
public class CTerms {

    public static final String SELF_VAR_PREFIX="self";
    public static final String THIS_VAR_PREFIX="this";
    public static final CThis THIS_THIS = new CThis(0);
	
	
	static int selfId=0;
	public static final CSelf makeSelf() {
	    return new CSelf(selfId++);
	}
	static int thisId=1;
    public static final CThis makeThis() {
        return new CThis(thisId++);
    }
    public static final CThis makeThis(Type t) {
        return new CThis(t, thisId++);
    }
    
    public static final CField makeField(XVar var, MethodDef mi) {
        return new CField(var, mi);
    }
    public static final CField makeField(XVar var, FieldDef mi) {
        return new CField(var, mi);
    }
    
    public static final CLocal makeLocal(X10LocalDef ld) {
        return new CLocal(ld);
    }
    public static final CLocal makeLocal(X10LocalDef ld, String s) {
        return new CLocal(ld, s);
    }
    public static final CAtom makeAtom(MethodDef md, XTerm... t ) {
        return new CAtom(md, md, t);
    }
    public static final CAtom makeAtom(MethodDef md, MethodDef mdAsExpr, XTerm... t ) {
        return new CAtom(md, mdAsExpr, t);
    }
    public static final CAtom makeAtom(MethodDef md, List<XTerm> t ) {
        return new CAtom(md, md, t);
    }
    public static final CAtom makeAtom(FieldDef md, XTerm... t ) {
        return new CAtom(md, md, t);
    }
    public static final CAtom makeAtom(FieldDef md, FieldDef mdAsExpr, XTerm... t ) {
        return new CAtom(md, mdAsExpr, t);
    }
    public static final CAtom makeAtom(FieldDef md, List<XTerm> t ) {
        return new CAtom(md, md, t);
    }
   
}

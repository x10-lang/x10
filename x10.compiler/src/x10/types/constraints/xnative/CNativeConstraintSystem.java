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

package x10.types.constraints.xnative;

import java.util.List;

import polyglot.ast.IntLit;
import x10.constraint.XLit;
import x10.constraint.XTerm;
import x10.constraint.XVar;
import x10.constraint.xnative.XNativeConstraintSystem;
import x10.types.X10LocalDef;
import x10.types.constraints.CAtom;
import x10.types.constraints.CConstraint;
import x10.types.constraints.CConstraintSystem;
import x10.types.constraints.CField;
import x10.types.constraints.CLocal;
import x10.types.constraints.CNativeConstraint;
import x10.types.constraints.CSelf;
import x10.types.constraints.CThis;
import x10.types.constraints.ConstraintManager;
import polyglot.types.FieldDef;
import polyglot.types.MethodDef;
import polyglot.types.Type;
import polyglot.types.TypeSystem;

public class CNativeConstraintSystem extends XNativeConstraintSystem implements CConstraintSystem {
    public static final XLit NULL = ConstraintManager.getConstraintSystem().xnull();
    public static final XLit TRUE = ConstraintManager.getConstraintSystem().xtrue();
    public static final XLit FALSE = ConstraintManager.getConstraintSystem().xfalse();
    static int selfId = 0;

    public CSelf makeSelf() {return new CNativeSelf(selfId++);}
    static int thisId = 1;
    public CThis makeThis() {return makeThis(null);}
    public CThis makeThis(Type t) {
    	return new CNativeThis(thisId++, t);
    }
    public XVar makeQualifiedThis(Type qualifier, Type base) {
        return makeQualifiedVar(qualifier, makeThis(base)); 
    }
    public XVar makeQualifiedVar(Type qualifier, XVar var) {
    	return new QualifiedVar(qualifier, var); 
    }
    public CField makeField(XVar var, MethodDef mi) {
    	return new CNativeField(var, mi);
    }
    public CField makeField(XVar var, FieldDef mi) {
    	return new CNativeField(var, mi);
    }
    public CLocal makeLocal(X10LocalDef ld) {
    	return new CNativeLocal(ld);
    }
    public CLocal makeLocal(X10LocalDef ld, String s) {
    	return new CNativeLocal(ld, s);
    }
    public CAtom makeAtom(MethodDef md, XTerm... t ) {
    	return new CNativeAtom(md, md, t);
    }
    public CAtom makeAtom(MethodDef md, MethodDef mdAsExpr, XTerm... t ) {
    	return new CNativeAtom(md, mdAsExpr, t);
    }
    public CAtom makeAtom(MethodDef md, List<XTerm> t ) {
    	return new CNativeAtom(md, md, t.toArray(new XTerm[0]));
    }
    public CAtom makeAtom(FieldDef md, XTerm... t ) {
    	return new CNativeAtom(md, md, t);
    }
    public CAtom makeAtom(FieldDef md, FieldDef mdAsExpr, XTerm... t ) {
    	return new CNativeAtom(md, mdAsExpr, t);
    }
    public CAtom makeAtom(FieldDef md, List<XTerm> t ) {
    	return new CNativeAtom(md, md, t.toArray(new XTerm[0]));
    }
    public XLit makeLit(Object val, Type type) {
        if (val == null) return NULL;
        if (val.equals(true)) return TRUE;
        if (val.equals(false)) return FALSE;
        return new CNativeLit(val, type);
    }
    public XNativeTypeLit makeTypeLit(Type t) {
    	return new XNativeTypeLit(t);
    }
    public XLit makeZero(Type type) {
        TypeSystem ts = type.typeSystem();
        if (type.isBoolean()) return FALSE;
        else if (type.isChar()) return new CNativeLit(Character.valueOf('\0'), type);
        else if (type.isIntOrLess() || type.isUInt()) return new CNativeLit(0, type);
        else if (type.isLongOrLess()) return new CNativeLit(0L, type);
        else if (type.isFloat()) return new CNativeLit(0.0f, type);
        else if (type.isDouble())return new CNativeLit(0.0, type);
        else if (ts.isObjectOrInterfaceType(type, ts.emptyContext())) 
        	return ConstraintManager.getConstraintSystem().xnull(); 
        else return null;
    }
    
        public IntLit.Kind getIntLitKind(Type type) {
        if (type.isByte())   return IntLit.BYTE;
        if (type.isUByte())  return IntLit.UBYTE;
        if (type.isShort())  return IntLit.SHORT;
        if (type.isUShort()) return IntLit.USHORT;
        if (type.isInt())    return IntLit.INT;
        if (type.isUInt())   return IntLit.UINT;
        if (type.isLong())   return IntLit.LONG;
        if (type.isULong())  return IntLit.ULONG;
        return null;
    }
    /**
     *  Creates a field access term corresponding to an @Opaque declaration. Currently 
     *  this is handled in a rather naive way, by modeling it as a field access where
     *  the field consists of the opaque method name and arguments
     */
    public XTerm makeOpaque(Object op, XTerm target, List<XTerm> args) {
    	// constructing the fake field name
    	// TODO: can handle this as nested field dereferencing
    	StringBuilder sb = new StringBuilder();
    	sb.append(op.toString());
    	for (XTerm arg : args) {
    		sb.append(arg.toString());
    	}
    	return makeField((XVar)target, sb.toString()); 
    }
        
    public CConstraint makeCConstraint() { return new CNativeConstraint(); }
    public CConstraint makeCConstraint(XVar self) { return new CNativeConstraint(self); }

}

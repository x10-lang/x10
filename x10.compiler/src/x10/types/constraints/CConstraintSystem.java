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

package x10.types.constraints;

import java.util.List;

import polyglot.ast.IntLit;
import polyglot.types.FieldDef;
import polyglot.types.LocalDef;
import polyglot.types.MethodDef;
import polyglot.types.Type;
import x10.constraint.XConstraintSystem;
import x10.constraint.XLit;
import x10.constraint.XTerm;
import x10.constraint.XVar;
import x10.types.X10LocalDef;

/**
 * Factory interface that provides methods to create various XTerms that
 * have type information. 
 * @author lshadare
 *
 */
public interface CConstraintSystem extends XConstraintSystem {
    public CSelf makeSelf(); 
    public CThis makeThis(); 
    public CThis makeThis(Type t); 
    public XVar makeQualifiedThis(Type qualifier, Type base); 
    public XVar makeQualifiedVar(Type qualifier, XVar var); 
    public CField makeField(XVar var, MethodDef mi); 
    public CField makeField(XVar var, FieldDef mi); 
    public CLocal makeLocal(X10LocalDef ld); 
    public CLocal makeLocal(X10LocalDef ld, String s); 
 
    public CAtom makeAtom(MethodDef md, XTerm... t ); 
    public CAtom makeAtom(MethodDef md, MethodDef mdAsExpr, XTerm... t ); 
    public CAtom makeAtom(MethodDef md, List<XTerm> t ); 
    public CAtom makeAtom(FieldDef md, XTerm... t ); 
    public CAtom makeAtom(FieldDef md, FieldDef mdAsExpr, XTerm... t ); 
    public CAtom makeAtom(FieldDef md, List<XTerm> t ); 
    public XLit makeLit(Object val, Type type); 
    public XLit makeZero(Type type); 
    public XTypeLit makeTypeLit(Type type);
    
    public IntLit.Kind getIntLitKind(Type type);
    public CConstraint makeCConstraint(); 
    public CConstraint makeCConstraint(XVar self); 
    
    public XTerm makeOpaque(Object op, XTerm target, List<XTerm> args); 
}

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

package x10.ast;

import java.util.ArrayList;
import java.util.List;

import polyglot.ast.Assign;
import polyglot.ast.Expr;
import polyglot.ast.FieldAssign;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Stmt;
import polyglot.ast.Stmt_c;
import polyglot.ast.Term;
import polyglot.ast.TypeNode;
import polyglot.frontend.Job;
import polyglot.types.Context;
import polyglot.types.FieldInstance;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.types.UnknownType;
import polyglot.util.Position;
import polyglot.util.TypedList;
import polyglot.visit.CFGBuilder;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import polyglot.visit.TypeBuilder;

import x10.Configuration;
import x10.constraint.XFailure;
import x10.constraint.XVar;
import x10.constraint.XTerm;
import x10.constraint.XVar;
import x10.errors.Errors;
import x10.types.X10ConstructorDef;
import polyglot.types.Context;
import x10.types.X10FieldInstance;
import x10.types.X10ParsedClassType;
import polyglot.types.TypeSystem;
import x10.types.XTypeTranslator;
import x10.types.X10Context_c;
import x10.types.checker.ThisChecker;
import x10.types.constraints.CConstraint;
import x10.types.constraints.CConstraint;
import x10.types.constraints.ConstraintMaker;

/**
 * @author vj
 * @author igor
 */
public class AssignPropertyCall_c extends Stmt_c implements AssignPropertyCall {

    List<Expr> arguments;
    List<X10FieldInstance> properties;

    /**
     * @param pos
     * @param arguments
     * @param target
     * @param name
     */
    public AssignPropertyCall_c(Position pos, List<Expr> arguments) {
        super(pos);
        this.arguments = TypedList.copyAndCheck(arguments, Expr.class, true);
    }

    public Term firstChild() {
        return listChild(arguments, null);
    }

    /* (non-Javadoc)
     * @see polyglot.ast.Term#acceptCFG(polyglot.visit.CFGBuilder, java.util.List)
     */
    public <S> List<S> acceptCFG(CFGBuilder v, List<S> succs) {
        v.visitCFGList(arguments, this, EXIT);
        return succs;
    }

    public AssignPropertyCall arguments(List<Expr> args) {
        if (args == arguments) return this;
        AssignPropertyCall_c n = (AssignPropertyCall_c) copy();
        n.arguments = TypedList.copyAndCheck(args, Expr.class, true);
        return n;
    }

    public List<Expr> arguments() {
        return arguments;
    }

    public AssignPropertyCall properties(List<X10FieldInstance> properties) {
        if (properties == this.properties) return this;
        AssignPropertyCall_c n = (AssignPropertyCall_c) copy();
        n.properties = TypedList.copyAndCheck(properties, FieldInstance.class, true);
        return n;
    }

    public List<X10FieldInstance> properties() {
        return properties;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("property");
        sb.append("(");
        boolean first = true;
        for (Expr e : arguments) {
            if (first) {
                first = false;
            }
            else {
                sb.append(", ");
            }
            sb.append(e);
        }
        sb.append(");");
        return sb.toString();
    }

    public static X10ConstructorDef getConstructorDef(TypeBuilder tb) {
        for (; tb != null && tb.inCode(); tb = tb.pop())
            if (tb.def() instanceof X10ConstructorDef)
                return (X10ConstructorDef) tb.def();
        return null;
    }

    @Override
    public Node buildTypes(TypeBuilder tb) {
        X10ConstructorDef cd = getConstructorDef(tb);
        if (cd != null) {
            cd.derivedReturnType(true);
        }
        return this;
    }

    @Override
    public Node typeCheck(ContextVisitor tc) {
        TypeSystem ts = tc.typeSystem();
        Context ctx = tc.context();
        NodeFactory nf = (NodeFactory) tc.nodeFactory();
        Position pos = position();
        Job job = tc.job();
        X10ConstructorDef thisConstructor = ((X10Context_c)ctx).getCtorIgnoringAsync();
        X10ParsedClassType container = (X10ParsedClassType) ctx.currentClass();
        if (thisConstructor==null) {
            Errors.issue(job,
                         new SemanticException("A property statement may only occur in the body of a constructor.", position()));
        } else {
            container = (X10ParsedClassType) thisConstructor.asInstance().container();
        }
        // Now check that the types of each actual argument are subtypes of the corresponding
        // property for the class reachable through the constructor.
        List<FieldInstance> definedProperties = container.definedProperties();
        int pSize = definedProperties.size();
        int aSize = arguments.size();
        if (aSize != pSize) {
            Errors.issue(job,
                         new SemanticException("The property initializer must have the same number of arguments as properties for the class.", position()));
        }

        checkAssignments(tc, pos, definedProperties, arguments);
        if (thisConstructor != null) {
            checkReturnType(tc, pos, thisConstructor, definedProperties, arguments);
        }

        /* We check that "this" is not allowed in CheckEscapingThis.CheckCtor
		ThisChecker thisC = (ThisChecker) new ThisChecker(tc.job()).context(tc.context());
		for (int i=0; i < aSize; i++) {
		    Expr arg = arguments.get(i);
		    thisC.clearError();
		    visitChild(arg, thisC);
		    if (thisC.error()) {
		        Errors.issue(job, new Errors.ThisNotPermittedInPropertyInitializer(arg, position()));
		    }
		} */

        List<X10FieldInstance> properties = new ArrayList<X10FieldInstance>();
        for (FieldInstance fi : definedProperties) {
            properties.add((X10FieldInstance) fi);
        }
        return this.properties(properties);
    }

    protected static void checkAssignments(ContextVisitor tc, Position pos,
                                           List<FieldInstance> props, List<Expr> args)
    {
        TypeSystem xts = (TypeSystem) tc.typeSystem();
        // First check that the base types are correct.
        for (int i=0; i < args.size() && i < props.size(); ++i) {
            if (!xts.isSubtype(Types.baseType(args.get(i).type()), Types.baseType(props.get(i).type()))) {
                Errors.issue(tc.job(),
                             new SemanticException("The type " + args.get(i).type() + " of the initializer for property " + props.get(i) + " is not a subtype of the property type " + props.get(i).type(), pos));
            }
        }
        // Now we check that the constraints are correct.
    }

    protected void checkReturnType(ContextVisitor tc, Position pos,
                                   X10ConstructorDef thisConstructor, List<FieldInstance> definedProperties,
                                   List<Expr> args)
    {
        TypeSystem ts =  tc.typeSystem();
        final Context ctx =  tc.context();
        if (ts.hasUnknown(Types.getCached(thisConstructor.returnType()))) {
            return;
        }

        Type returnType = Types.getCached(thisConstructor.returnType());
        CConstraint result = Types.xclause(returnType);

        if (result != null && result.valid())
            result = null;   // FIXME: the code below that infers the return type of a ctor is buggy, since it infers "this". see XTENLANG-1770

        {
            CConstraint known = Types.get(thisConstructor.supClause());
            known = (known==null ? new CConstraint() : known.copy());
            try {
                known.addIn(Types.get(thisConstructor.guard()));

                XVar thisVar = thisConstructor.thisVar();

                for (int i = 0; i < args.size() && i < definedProperties.size(); i++) {
                    Expr initializer = args.get(i);
                    Type initType = initializer.type();
                    final FieldInstance fii = definedProperties.get(i);
                    XVar prop = (XVar) ts.xtypeTranslator().translate(known.self(), fii);

                    // Add in the real clause of the initializer with [self.prop/self]
                    CConstraint c = Types.realX(initType);
                    if (c != null)
                        known.addIn(c.substitute(prop, c.self()));

                    XTerm initVar = ts.xtypeTranslator().translate(known, initializer, (Context) ctx);
                    if (initVar != null)
                        known.addBinding(prop, initVar);
                }

                // Set the return type of the enclosing constructor to be this inferred type.
                Type inferredResultType = Types.addConstraint(Types.baseType(returnType), known);
                Ref <? extends Type> r = thisConstructor.returnType();
                ((Ref<Type>) r).update(inferredResultType);
                // bind this==self; sup clause may constrain this.
                if (thisVar != null) {
                    known = known.instantiateSelf(thisVar);

                    // known.addSelfBinding(thisVar);
                    // known.setThisVar(thisVar);
                }
                if (result != null) {
                    final CConstraint rr =  result.instantiateSelf(thisVar);
                    final CConstraint k = known;
                    if (!k.entails(rr, new ConstraintMaker() {
                        public CConstraint make() throws XFailure {
                            return ctx.constraintProjection(k, rr);
                        }}))

                        Errors.issue(tc.job(),
                                     new Errors.ConstructorReturnTypeNotEntailed(known, result, pos));
                }
            }
            catch (XFailure e) {
                Errors.issue(tc.job(), new SemanticException(e.getMessage(), e), this);
            }
        }
    }

    /** Visit the children of the statement. */
    public Node visitChildren(NodeVisitor v) {
        List<Expr> args = visitList(this.arguments, v);
        return arguments(args);
    }
}

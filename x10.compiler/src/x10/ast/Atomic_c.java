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
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import polyglot.ast.Expr;
import polyglot.ast.Id;
import polyglot.ast.Node;
import polyglot.ast.Stmt;
import polyglot.ast.Term;
import polyglot.ast.Stmt_c;
import polyglot.types.ClassDef;
import polyglot.types.ConstructorDef;
import polyglot.types.Context;
import polyglot.types.FieldDef;
import polyglot.types.MethodDef;
import polyglot.types.Name;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.visit.CFGBuilder;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import polyglot.types.Context;
import polyglot.types.TypeSystem;
import x10.errors.Errors;
import x10.types.X10CodeDef;
import x10.types.X10FieldDef;
import x10.types.X10LocalDef;
import x10.types.X10LocalInstance;
import x10.types.X10MethodDef;
import x10.util.X10TypeUtils;

/**
 * @author Philippe Charles
 * @author vj
 */
public class Atomic_c extends Stmt_c 
implements Atomic {
	
	public Stmt body;
	
	//keep all accessed x0 identifies declared in an atomic section
	public List<Id> identifiers = null;
	
	//when doing the type checking, convert Id to local defs and field def
	private List<X10LocalDef> locals = new LinkedList<X10LocalDef>();
	private List<X10FieldDef> fields = new LinkedList<X10FieldDef>();
	private List<X10LocalDef> formals = new LinkedList<X10LocalDef>();
	
	public Atomic_c(Position p, Expr place, Stmt body) {
		super(p);
		this.body = body;
	}
	
	public Atomic_c(Position p) {
		super(p);
	}
	
	/* (non-Javadoc)
	 * @see x10.ast.Future#body()
	 */
	public Stmt body() {
		return body;
	}
	
	/** Set the body of the statement. */
	public Atomic body(Stmt body) {
		Atomic_c n = (Atomic_c) copy();
		n.body = body;
		return n;
	}
	
	/** Set the identifier list */
	//this is for data-centric synchronization
	public Atomic identifierList(List<Id> idlist) {
		Atomic_c n = (Atomic_c) copy();
		n.identifiers = idlist;
		return n;
	}
	
	/** Reconstruct the statement. */
	protected Atomic reconstruct( Stmt body ) {
		if ( body != this.body ) {
			Atomic_c n = (Atomic_c) copy();
			n.body = body;
			return n;
		}
		
		return this;
	}
	
	/** Visit the children of the statement. */
	public Node visitChildren( NodeVisitor v ) {
		Stmt body = (Stmt) visitChild(this.body, v);
		return reconstruct(body);
	}
	
	@Override
	public Node checkLinkProperty(ContextVisitor tc) {
		//check if all identifiers are formals, locals, or fields in the scope
		//System.out.println("@Atomic_c, all locals: " + tc.context().allLocals());
		if(this.identifiers != null) {
			//check it must be in either constructor or method declaration
			X10CodeDef currentCode = tc.context().currentCode();
			if(!(currentCode instanceof ConstructorDef || currentCode instanceof MethodDef)) {
				Errors.issue(tc.job(), new SemanticException("Can not use atomic section in a non-method / non-constructor.",
						this.position));
				//issue an error and return
				return this;
			}
			List<String> allFormalNames = X10TypeUtils.getAllFormalNames(currentCode);
			for(Id id : this.identifiers) {
				//it includes local and parameters
				X10LocalDef localDef = X10TypeUtils.findLocalDef(tc, id.id());
				if(localDef != null) {
					    this.locals.add(localDef);
					    if(allFormalNames.contains(localDef.name().toString())) {
					    	if(X10TypeUtils.isPrimitiveType(localDef.type().get())) {
					    		Errors.issue(tc.job(), new SemanticException("Can not use primitive argument: "
					    				+ localDef + ", in atomic section.",	id.position()));
					    	}
					    }
				} else {
					//find fields
					X10FieldDef fieldDef = X10TypeUtils.findFieldDef(tc, id.id());
					if(fieldDef != null) {
						this.fields.add(fieldDef);
					} else {
						Errors.issue(tc.job(), new SemanticException("The variable: " + id
								+ " is undefined.", id.position()));
					}
				}
			}
			//check the correctness
			//assert this.identifiers.size() == this.fields.size() + this.locals.size();
//			System.out.println("@Atomic_c, after checking link property, locals: " + this.locals
//					+ ",  fields: " + this.fields);
		}	
		
		return this;
	}
	
	public List<X10LocalDef> getLocalsInAtomic() {
		return Collections.unmodifiableList(this.locals);
	}
	
	public List<X10FieldDef> getFieldsInAtomic() {
		return Collections.unmodifiableList(this.fields);
	}
	
//	public List<X10LocalDef> getFormalsInAtomic() {
//		return Collections.unmodifiableList(this.formals);
//	}
	
	/** Type check the statement. */
	public Node typeCheck(ContextVisitor tc) {		
		return this;
	}
	
	public Context enterScope(Context c) {
		Context cc = (Context) super.enterScope(c);
		 cc = cc.pushAtomicBlock();
		return cc;
		    
	}
	public String toString() {
		return "atomic " + body;
	}
	
	/** Write the statement to an output file. */
	public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
		w.write("atomic ");
		printSubStmt(body, w, tr);
	}
	
	/**
	 * Return the first (sub)term performed when evaluating this
	 * term.
	 */
	public Term firstChild() {
		return body;
	}
	
	/**
	 * Visit this term in evaluation order.
	 */
	public <S> List<S> acceptCFG(CFGBuilder v, List<S> succs) {
		v.push(this).visitCFG(body, this, EXIT);
		return succs;
	}
	
}

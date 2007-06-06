/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package polyglot.ext.x10.ast;

import polyglot.ast.CanonicalTypeNode_c;
import polyglot.ast.Expr;
import polyglot.ast.FieldDecl;
import polyglot.ast.Id;
import polyglot.ast.Node;
import polyglot.ast.StringLit;
import polyglot.ast.TypeNode;
import polyglot.ast.FieldDecl_c;
import polyglot.ext.x10.extension.X10Ext;
import polyglot.ext.x10.types.X10FieldInstance;
import polyglot.ext.x10.types.X10ReferenceType;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.ext.x10.types.X10TypeSystem_c;
import polyglot.main.Report;
import polyglot.types.FieldInstance;
import polyglot.types.Flags;
import polyglot.types.InitializerInstance;
import polyglot.types.ParsedClassType;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.util.Position;
import polyglot.visit.AmbiguityRemover;
import polyglot.visit.TypeBuilder;
import polyglot.visit.TypeChecker;

public class X10FieldDecl_c extends FieldDecl_c implements X10FieldDecl {
	
	// TODO: Use this during type-checking.
	DepParameterExpr thisClause;
	public X10FieldDecl_c(Position pos, DepParameterExpr thisClause, Flags flags, TypeNode type,
			Id name, Expr init)
	{
		super(pos, flags, type, name, init);
		this.thisClause = thisClause;
	}
	protected X10FieldDecl_c(Position pos,  Flags flags, TypeNode type,
			Id name, Expr init) {
		this(pos, null, flags, type, name, init);
	}
	public DepParameterExpr thisClause() {
		return thisClause;
	}
	public boolean isDisambiguated() {
		return (type == null || type.isDisambiguated()) && super.isDisambiguated();
	}
	public Node typeCheck(TypeChecker tc) throws SemanticException {
		Node result = super.typeCheck(tc);
		
		// Ensure that the FieldInstance type is updated to reflect
		// any deptype.
		X10FieldInstance myFI = (X10FieldInstance) this.fi;
		myFI.setDepType(declType());
		myFI.setSelfClauseIfFinal();
		
		//
		// Any occurrence of a non-final static field in X10
		// should be reported as an error.
		// 
		if (flags().isStatic() && (!flags().isFinal())) {
			throw new SemanticException("Non-final static field is illegal in X10",
					this.position());
		}
		return result;
	}
	
	public Node buildTypes(TypeBuilder tb) throws SemanticException {
		X10TypeSystem ts = (X10TypeSystem) tb.typeSystem();
		
		ParsedClassType ct = tb.currentClass();
		
		if (ct == null) {
			return this;
		}
		
		Flags f = flags;
		
		if (ct.flags().isInterface()) {
			f = f.Public().Static().Final();
		}
		
		FieldDecl n;
		
		if (init != null) {
			Flags iflags = f.isStatic() ? Flags.STATIC : Flags.NONE;
			InitializerInstance ii = ts.initializerInstance(init.position(),
					ct, iflags);
			n = initializerInstance(ii);
		}
		else {
			n = this;
		}
		
		// XXX: MutableFieldInstance
		FieldInstance fi = ts.fieldInstance(position(), ct, f,
				ts.unknownType(position()), name.id());
		
		// vj - shortcut and initialize the field instance if the decl has an initializer
		// This is the hack to permit reading the list of properties from the StringLit initializer
		// of a field, without waiting for a ConstantsChecked pass to run.
		boolean isString =false;
		if (type != null && type.type() != null && init instanceof StringLit) {
			String val = ((StringLit) init).value();
			fi = ts.fieldInstance(position(), ct, f, name.id(), val);
			fi.constantValue(val);
			fi = fi.type(ts.String());
			//Report.report(1, "X10FieldDecl_c: ****GOLDEN initialized field instance |" + fi + "|");
			isString = true;
		} 	        	
		
		ct.addField(fi);
		FieldDecl result = (FieldDecl) n.flags(f).fieldInstance(fi);
		return isString? result.type(new CanonicalTypeNode_c(Position.COMPILER_GENERATED,ts.String())) : result;
	}
	
	public Node disambiguate(AmbiguityRemover ar) throws SemanticException {
		X10FieldDecl_c f = (X10FieldDecl_c) super.disambiguate(ar);
		// [IP] All fields in value types should be final
		// [IP] FIXME: this will produce an "assignment to final" message --
		//      is that good enough?
		if (f.flags().isFinal())
			return f;
		FieldInstance fi = f.fieldInstance();
		X10ReferenceType ref = (X10ReferenceType) fi.container();
		X10TypeSystem xts = (X10TypeSystem) ref.typeSystem();
		// Report.report(5, "[X10FieldDecl_c] disambiguate: " + fi + " " + ref + ref.getClass());
		if (!(xts.isValueType(ref)))
			return f;
		fi.setFlags(fi.flags().Final());
		return f.flags(f.flags().Final());
	}
}


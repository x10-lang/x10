package polyglot.ext.x10.ast;

import java.util.List;

import polyglot.ast.Id;
import polyglot.ast.Node;
import polyglot.ast.Term;
import polyglot.ast.Term_c;
import polyglot.ext.x10.types.ParameterType_c;
import polyglot.ext.x10.types.TypeProperty_c;
import polyglot.ext.x10.types.X10Context;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.types.CodeDef;
import polyglot.types.Context;
import polyglot.types.Def;
import polyglot.types.FieldDef;
import polyglot.types.Named;
import polyglot.types.ProcedureDef;
import polyglot.types.Qualifier;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.Types;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.visit.CFGBuilder;
import polyglot.visit.NodeVisitor;
import polyglot.visit.TypeBuilder;

public class TypeParamNode_c extends Term_c implements TypeParamNode {
	Id id;
	protected Ref<? extends Type> type;

	public TypeParamNode_c(Position pos, Id id) {
		super(pos);
		this.id = id;
	}

	public String name() {
		return id.id();
	}

	public Id id() {
		return id;
	}

	public TypeParamNode id(Id id) {
		TypeParamNode_c n = (TypeParamNode_c) copy();
		n.id = id;
		return n;
	}

	/** Get the type as a qualifier. */
	public Ref<? extends Qualifier> qualifierRef() {
		return typeRef();
	}

	/** Get the type this node encapsulates. */
	public Ref<? extends Type> typeRef() {
		return this.type;
	}

	public Type type() {
		return Types.get(this.type);
	}

	/** Set the type this node encapsulates. */
	protected TypeParamNode typeRef(Ref<? extends Type> type) {
		TypeParamNode_c n = (TypeParamNode_c) copy();
		n.type = type;
		return n;
	}

	public Node buildTypes(TypeBuilder tb) throws SemanticException {
		X10TypeSystem xts = (X10TypeSystem) tb.typeSystem();
		
	        Def def = tb.def();
	        
	        if (! (def instanceof ProcedureDef)) {
	            throw new SemanticException("Type parameter cannot occur outside method, constructor, or closure definition.", position());
	        }
	        
	        ProcedureDef code = (ProcedureDef) def;

		Type t = new ParameterType_c(xts, position(), name(), Types.ref(code));
		return typeRef(Types.ref(t));
	}

	public Term firstChild() {
		return null;
	}

	public List<Term> acceptCFG(CFGBuilder v, List<Term> succs) {
		return succs;
	}

	public String toString() {
		return name();
	}

	public void addDecls(Context c) {
		X10Context xc = (X10Context) c;
		c.addNamed((Named) type());
	}

	public Node visitChildren(NodeVisitor v) {
		Id id = (Id) visitChild(this.id, v);
		if (id != this.id) return id(id);
		return this;
	}
}

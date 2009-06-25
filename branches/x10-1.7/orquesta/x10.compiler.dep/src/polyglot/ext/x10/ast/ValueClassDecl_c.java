/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created on Dec 9, 2004
 *
 * 
 */
package polyglot.ext.x10.ast;

import java.util.List;

import polyglot.ast.CanonicalTypeNode;
import polyglot.ast.ClassBody;
import polyglot.ast.Expr;
import polyglot.ast.Id;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.TypeNode;
import polyglot.ast.ClassDecl_c;
import polyglot.ext.x10.types.X10ParsedClassType;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.ext.x10.types.X10TypeSystem_c;
import polyglot.types.ClassType;
import polyglot.types.Flags;
import polyglot.types.SemanticException;
import polyglot.types.Types;
import polyglot.util.Position;
import polyglot.util.TypedList;
import polyglot.visit.NodeVisitor;
import polyglot.visit.TypeChecker;

/**
 * We must preserve information about value classes at runtime.
 * The best way would be to use meta-data annoations from Java 5,
 * but in the meantime (for 1.4 compatibility) we simply add an
 * interface ("implements ValueType") to mark value types.  
 * 
 * @author vj
 * @author Christian Grothoff
 * 
 */
public class ValueClassDecl_c extends X10ClassDecl_c implements ValueClassDecl {
	/**
	 * @param pos
	 * @param flags
	 * @param name
	 * @param superClass
	 * @param interfaces
	 * @param body
	 */
	public ValueClassDecl_c(Position pos, Flags flags, Id name,
            DepParameterExpr ci, TypeNode superClass, List<TypeNode> interfaces, ClassBody body,
            X10NodeFactory nf) {
            
		this(pos, flags, name, ci, superClass, addValueToInterfaces(interfaces, nf), body);
	}
	
	static List<TypeNode> addValueToInterfaces(List<TypeNode> interfaces, X10NodeFactory nf) {
		List<TypeNode> elist = TypedList.copy(interfaces, TypeNode.class, false);
		X10TypeSystem ts = (X10TypeSystem) nf.extensionInfo().typeSystem();
		ClassType value = ts.value();
		CanonicalTypeNode ctn = nf.CanonicalTypeNode(Position.COMPILER_GENERATED, Types.ref(value));
		elist.add(0, ctn); 
		return elist;
	}

	public ValueClassDecl_c(Position pos, Flags flags, Id name,
            DepParameterExpr ci, TypeNode superClass, List<TypeNode> interfaces, ClassBody body) {
            
		super(pos, flags, name, ci, superClass, interfaces, body);
	}
      
        public Node typeCheck(TypeChecker tc) throws SemanticException {
        	ValueClassDecl_c result = (ValueClassDecl_c) super.typeCheck(tc);
        	
        	if (this.type instanceof X10ParsedClassType) {
        		X10ParsedClassType xpType = (X10ParsedClassType) type;
        		xpType.checkRealClause();
        	}
        	return result;
        }
}

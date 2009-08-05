/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package polyglot.ext.x10.ast;

import java.util.List;

import polyglot.ast.Block;
import polyglot.ast.ClassDecl;
import polyglot.ast.Formal;
import polyglot.ast.Id;
import polyglot.ast.MethodDecl;
import polyglot.ast.TypeNode;
import polyglot.ext.x10.types.TypeDef;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.types.Flags;
import polyglot.types.LocalDef;
import polyglot.types.Name;
import polyglot.types.Ref;
import polyglot.types.Type;
import polyglot.visit.TypeChecker;

public interface X10ClassDecl extends ClassDecl {
	DepParameterExpr classInvariant();
	X10ClassDecl classInvariant(DepParameterExpr classInvariant);
	
	List<TypeParamNode> typeParameters();
	X10ClassDecl typeParameters(List<TypeParamNode> typeParameters);
	
	List<PropertyDecl> properties();
	X10ClassDecl properties(List<PropertyDecl> ps);
	
	  /**
     * Create a synthetic MethodDecl from the given data and return
     * a new Class with this MethodDecl added in. No duplicate method
     * checks will be performed. It is up to the user to make sure
     * the constructed method will not duplicate an existing method.
     * 
     * Should be called after the class has been typechecked.
     * 
     * @param flags -- The flags for the method
     * @param name -- The name of the method
     * @param fmls -- A list of LocalDefs specifying the parameters to the method.
     * @param returnType -- The return type of this method.
     * @param trow  -- The types of throwables from the method.
     * @param block -- The body of the method
     * @param xnf -- The X10NodeFactory to be used to create new AST nodes.
     * @param xts  -- The X10TypeSystem object to be used to create new types.
     * @return  this, with the method added.
     * 
     * TODO: Ensure that type parameters and a guard can be supplied as well.
     */
	
	X10ClassDecl_c addSyntheticMethod(Flags flags, Name name, List<LocalDef> formals, 
    		Type returnType, List<Type> throwTypes, Block block,
    		X10NodeFactory xnf, X10TypeSystem xts);
}

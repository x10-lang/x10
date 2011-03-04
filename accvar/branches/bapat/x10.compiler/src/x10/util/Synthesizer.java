package x10.util;

import java.util.ArrayList;
import java.util.List;

import polyglot.ast.Block;
import polyglot.ast.ClassBody;
import polyglot.ast.Expr;
import polyglot.ast.FlagsNode;
import polyglot.ast.Formal;
import polyglot.ast.Id;
import polyglot.ast.Local;
import polyglot.ast.MethodDecl;
import polyglot.ast.TypeNode;
import polyglot.types.Flags;
import polyglot.types.LocalDef;
import polyglot.types.MethodDef;
import polyglot.types.Name;
import polyglot.types.Ref;
import polyglot.types.Type;
import polyglot.types.Types;
import polyglot.util.Position;
import x10.ast.X10ClassDecl_c;
import x10.ast.X10NodeFactory;
import x10.types.X10TypeSystem;

/**
 * A utility to help synthesize fragments of ASTs. Most of the methods on this class are intended to
 * be used after an AST has been type-checked. These methods construct and add type-checked
 * AST nodes.
 * @author vj
 *
 */
public class Synthesizer {
	

	  /**
   * Create a synthetic MethodDecl from the given data and return
   * a new Class with this MethodDecl added in. No duplicate method
   * checks will be performed. It is up to the user to make sure
   * the constructed method will not duplicate an existing method.
   * 
   * Should be called after the class has been typechecked.
   * @param ct  -- The clas to which this code has to be added
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
	
	 public static X10ClassDecl_c addSyntheticMethod(X10ClassDecl_c ct, Flags flags, Name name, List<LocalDef> fmls, 
	    		Type returnType, List<Type> trow, Block block,
	    		X10NodeFactory xnf, X10TypeSystem xts) {
	    	assert ct.classDef() != null;
	    	
	    	Position CG = Position.COMPILER_GENERATED;
	    	List<Expr> args = new ArrayList<Expr>();
	    	List<Ref<? extends Type>> argTypes = new ArrayList<Ref<? extends Type>>();
	    	List<Formal> formals = new ArrayList<Formal>(fmls.size());
	    	for (LocalDef f : fmls) {
	    		Id id = xnf.Id(CG, f.name()); 

	    		Formal ff = xnf.Formal(CG,xnf.FlagsNode(CG, Flags.NONE), 
	    				xnf.CanonicalTypeNode(CG, f.type()),
	    				id);
	    		Local loc = xnf.Local(CG, id);
	    		LocalDef li = xts.localDef(CG, ff.flags().flags(), ff.type().typeRef(), id.id());
	    		ff = ff.localDef(li);
	    		loc = loc.localInstance(li.asInstance());
	    		loc = (Local) loc.type(li.asInstance().type());
	    		formals.add(ff);
	    		args.add(loc);
	    		argTypes.add(li.type());
	    	}
	    	FlagsNode newFlags = xnf.FlagsNode(CG, flags);
	    	TypeNode rt = xnf.CanonicalTypeNode(CG, returnType);


	    	List<TypeNode> throwTypeNodes = new ArrayList<TypeNode>();
	    	List<Ref<? extends Type>> throwTypes = new ArrayList<Ref<? extends Type>>();
	    	for (Type  t: trow) {
	    		Ref<Type> tref = Types.ref(t);
	    		throwTypes.add(tref);
	    		throwTypeNodes.add(xnf.CanonicalTypeNode(CG, t));
	    	}

	    	// Create the method declaration node and the CI.
	    	MethodDecl result = 
	    		xnf.MethodDecl(CG, newFlags, rt, xnf.Id(CG,name), formals, throwTypeNodes, block);

	    	MethodDef rmi = xts.methodDef(CG, Types.ref(ct.classDef().asType()), 
	    			newFlags.flags(), rt.typeRef(), name, argTypes, throwTypes);
	    	ct.classDef().addMethod(rmi);
	    	result = result.methodDef(rmi);
	    
	    	ClassBody b = ct.body();
	    	b = b.addMember(result);
	    	return (X10ClassDecl_c) ct.body(b);
	    }

}

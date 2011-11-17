package x10.visit;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import polyglot.ast.ConstructorCall;
import polyglot.ast.Expr;
import polyglot.ast.FieldDecl_c;
import polyglot.ast.LocalAssign_c;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.NullLit_c;
import polyglot.ast.PackageNode;
import polyglot.ast.Special;
import polyglot.frontend.Job;
import polyglot.frontend.Source;
import polyglot.types.LocalDef;
import polyglot.types.LocalInstance;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.Position;
import polyglot.visit.NodeVisitor;
import x10.ast.ParExpr_c;
import x10.ast.X10Call_c;
import x10.ast.X10Conditional_c;
import x10.ast.X10ConstructorCall_c;
import x10.ast.X10FieldAssign_c;
import x10.ast.X10FieldDecl_c;
import x10.ast.X10Field_c;
import x10.ast.X10Formal_c;
import x10.ast.X10LocalAssign_c;
import x10.ast.X10LocalDecl_c;
import x10.ast.X10Local_c;
import x10.ast.X10New_c;
import x10.ast.X10Special_c;
import x10.errors.Errors;
import x10.types.MethodInstance;
import x10.types.X10MethodDef;


public class X10LinkedChecker extends NodeVisitor{

	private static String THIS = "this";
	private static String NULL = "null";
	private static String RAW = "raw";
	private Job job;
	private TypeSystem ts;
	private NodeFactory nf;
	
	private AtomicSets atomicSets = new AtomicSets();
	
	public X10LinkedChecker(Job job, TypeSystem ts, NodeFactory nf) {
		this.job = job;
		this.ts = ts;
		this.nf = nf;
		
	}
	
	@Override
    public NodeVisitor enter(Node n) {
		if (n instanceof X10FieldDecl_c){
			X10FieldDecl_c field = (X10FieldDecl_c) n;
			String aset = getAtomicSet(field);
			if (!aset.equals(RAW)) {
				atomicSets.setEqual(THIS, aset);
				if (!field.init().toString().equals("null") && !atomicSets.isEqual(THIS, getAtomicSet(field.init()))) {
					Errors.issue(job, new SemanticException("Left and right of the assignment must have the same atomic set.", n.position()));
				}
			}
		
		} else if (n instanceof X10LocalDecl_c) {
			X10LocalDecl_c variable = (X10LocalDecl_c) n;
			String aset = getAtomicSet(variable);
			if (!aset.equals(RAW)){
				atomicSets.setEqual(THIS, aset);
				if (!variable.init().toString().equals("null") && !atomicSets.isEqual(THIS, getAtomicSet(variable.init()))) {
					Errors.issue(job, new SemanticException("Left and right of the assignment must have the same atomic set.", n.position()));
				}
			}
			
		} else if (n instanceof X10Formal_c) {
			X10Formal_c variable = (X10Formal_c) n;
			String aset = getAtomicSet(variable);
			if (!aset.equals(RAW)){
				atomicSets.setEqual(THIS, aset);
			}
	
		} else if (n instanceof X10LocalAssign_c){
			X10LocalAssign_c assign = (X10LocalAssign_c) n;
			Expr left = assign.left();
			Expr right = assign.right();
			String leftAset = getAtomicSet(left);
			if (!leftAset.equals(RAW)) { // --- left was declared with linked
				// --- Check that left and right have the same atomic set
				if (!atomicSets.isEqual(leftAset, getAtomicSet(right))){
					Errors.issue(job, new SemanticException("Left and right of the assignment must have the same atomic set.", n.position()));
				}
			}
		
		} else if (n instanceof X10FieldAssign_c){
			X10FieldAssign_c assign = (X10FieldAssign_c) n;
			Expr left = assign.left();
			Expr right  = assign.right();
			String leftAset = getAtomicSet(left);
			if (!leftAset.equals(RAW)) {
				if (!atomicSets.isEqual(leftAset, getAtomicSet(right))){
					Errors.issue(job, new SemanticException("Left and right of the assignment must have the same atomic set.", n.position()));
				}
			}
		
		} else if (n instanceof X10Call_c){
			X10Call_c call = (X10Call_c) n;
			List<LocalDef> formalNames = call.methodInstance().x10Def().formalNames();
			for(int i = 0; i < formalNames.size(); i++){
				LocalDef formal = formalNames.get(i);
				Expr argument = call.arguments().get(i);
				if (formal.flags().isLinked()){
					if (!atomicSets.isEqual(getAtomicSet(argument), getAtomicSet(call.target()))) {
					Errors.issue(job, new SemanticException("Linked argument must have the same atomic set as receiver.", n.position()));
					}
				}
			}
		
		} else if (n instanceof X10New_c){
			X10New_c call = (X10New_c) n;
			List<LocalDef> formalNames = call.constructorInstance().x10Def().formalNames();
			for(int i = 0; i < formalNames.size(); i++){
				LocalDef formal = formalNames.get(i);
				Expr argument = call.arguments().get(i);
				if (formal.flags().isLinked()){
					if (!atomicSets.isEqual(getAtomicSet(argument), getAtomicSet(n))) {
						Errors.issue(job, new SemanticException("Linked argument must have the same atomic set as receiver.", n.position()));
					}
				}
			}
			
		}
	
	
        return super.enter(n);
    }
	
	/*
	 * Returns one of RAW, NULL, or a.b.c (representing the atomic set of a linked entity)
	 * 
	 */
	
	private String getAtomicSet(Node n){
		if (n instanceof X10FieldDecl_c){
			X10FieldDecl_c field = (X10FieldDecl_c) n;
			if (field.flags().flags().isLinked() && field.type().type().isReference()) {
				return THIS + "." + field.name().toString();
			}
		}
		if (n instanceof X10FieldAssign_c){
			X10FieldAssign_c assign = (X10FieldAssign_c) n;
			return getAtomicSet(assign.right());
		}
		if (n instanceof X10Conditional_c){
			X10Conditional_c cond = (X10Conditional_c) n;
			String thenAset = getAtomicSet(cond.consequent());
			String elseAset = getAtomicSet(cond.alternative());
			if (!atomicSets.isEqual(thenAset, elseAset)){
				Errors.issue(job, new SemanticException("Conditional must have the same atomicity for the consequent and alternative.", n.position()));
			}
			return thenAset;
		}
		if (n instanceof X10LocalDecl_c){
			X10LocalDecl_c variable = (X10LocalDecl_c) n;
			if (variable.flags().flags().isLinked()  && variable.type().type().isReference()){
				return variable.name().toString();
			}
		}
		if (n instanceof X10Formal_c){
			X10Formal_c formal = (X10Formal_c) n;
			if (formal.flags().flags().isLinked() && formal.type().type().isReference()){
				return formal.name().toString();
			}
		}
		if (n instanceof X10Local_c){
			X10Local_c local = (X10Local_c) n;
			if (local.flags().isLinked()){
				return local.name().toString();
			}
		}
		if (n instanceof X10New_c){
			X10New_c cons = (X10New_c) n;
			if (cons.isLinked()) {
				return THIS;
			}
		}
		if (n instanceof X10Field_c){
			X10Field_c field = (X10Field_c) n;
			if (field.fieldInstance().flags().isLinked()){
				return getAtomicSet(field.target());
			}
		}
		if (n instanceof X10Special_c){
			X10Special_c special = (X10Special_c) n;
			if (special.kind() == Special.THIS){
				return THIS;
			}
			
		}
		if (n instanceof NullLit_c){
			return NULL;
		}
		if (n instanceof ParExpr_c){
			ParExpr_c expr = (ParExpr_c) n;
			return getAtomicSet(expr.expr());
		}
		if (n instanceof X10Call_c){
			X10Call_c call = (X10Call_c) n;
			X10MethodDef def = (X10MethodDef) call.methodInstance().def();
			if (def.hasLinkedResult()){
				return THIS;
			}
		}
		return RAW;
	}
	
	
	private class AtomicSets {
		Map<String, Set<String>> eq = new HashMap<String, Set<String>>();
		
		boolean isEqual(String one, String two){
			if (one != null && one.equals(two))
				return true;
			if (one.equals(NULL) || two.equals(NULL))
				return true;
			if (eq.get(one) != null && eq.get(two) != null){
				return eq.get(one) == eq.get(two);
			}
			return false;
		}
		
		void setEqual(String one, String two){
			Set<String> oneSet = eq.get(one);
			Set<String> twoSet = eq.get(two);
			
			Set<String> mergedSet = new HashSet<String>();
			if (oneSet != null){
				mergedSet.addAll(oneSet);
			} else {
				mergedSet.add(one);
			}
			if (twoSet != null){
				mergedSet.addAll(twoSet);
			} else {
				mergedSet.add(two);
			}
			
			// --- Now make sure that all elements in the mergeSet point to mergeSet
			for(String k: mergedSet){
				eq.put(k, mergedSet);
			}			
		}
		
		public String toString(){
			return eq.toString();
		}
		
	}
	

	

}

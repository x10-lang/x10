package x10.visit;

import java.util.LinkedHashSet;
import java.util.Set;

import polyglot.ast.ArrayAccessAssign_c;
import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.types.FieldDef;
import polyglot.types.Flags;
import polyglot.types.LocalDef;
import polyglot.visit.NodeVisitor;
import x10.ast.SettableAssign_c;
import x10.ast.X10FieldAssign_c;
import x10.ast.X10FieldDecl_c;
import x10.ast.X10Field_c;
import x10.ast.X10LocalAssign_c;
import x10.ast.X10LocalDecl_c;
import x10.ast.X10Local_c;
import x10.types.X10FieldDef;

/**
 * Normally, you do not need to use this visitor. Only for experimental
 * purpose to compare different techniques.
 * 
 * @author Sai Zhang (szhang@cs.washington.edu)
 * */

@Deprecated
class LocalAndFieldReassignVisitor extends NodeVisitor {
	public final Set<LocalDef> atomicLocalReassigns = new LinkedHashSet<LocalDef>();
	public final Set<LocalDef> atomicLocalDecls = new LinkedHashSet<LocalDef>();
	
	public final Set<FieldDef> atomicFieldReassigns = new LinkedHashSet<FieldDef>();
	public final Set<FieldDef> atomicFieldDecls = new LinkedHashSet<FieldDef>();
	
	@Override
	public Node leave(Node old, Node n, NodeVisitor v) {
		
		if(n instanceof X10LocalDecl_c) {
			X10LocalDecl_c x10localdecl = (X10LocalDecl_c)n;
			if(x10localdecl.localDef().flags() != null
					&& x10localdecl.localDef().flags().contains(Flags.ATOMIC)) {
				atomicLocalDecls.add(x10localdecl.localDef());
			}
		}
		if(n instanceof X10FieldDecl_c) {
			X10FieldDecl_c x10fieldDecl = (X10FieldDecl_c)n;
			X10FieldDef def = x10fieldDecl.fieldDef();
			if(def.flags() != null && def.flags().contains(Flags.ATOMIC)) {
				atomicFieldDecls.add(def);
			}
		}
		//TODO is it complete?
		//see the assignment
		if(n instanceof X10FieldAssign_c) {
			X10FieldAssign_c fieldAssign = (X10FieldAssign_c)n;
			FieldDef def = fieldAssign.fieldInstance().def();
			if(def.flags() != null && def.flags().contains(Flags.ATOMIC)) {
				atomicFieldReassigns.add(def);
			}
		}
		if(n instanceof X10LocalAssign_c) {
			X10LocalAssign_c localAssign = (X10LocalAssign_c)n;
			LocalDef def = localAssign.local().localInstance().def();
			if(def.flags() != null && def.flags().contains(Flags.ATOMIC)) {
				atomicLocalReassigns.add(def);
			}
		}
		//FIXME not sure is it correct
		if(n instanceof ArrayAccessAssign_c) {
			ArrayAccessAssign_c arrayAssign = (ArrayAccessAssign_c)n;
			Expr array_expr = arrayAssign.array();
			if(array_expr instanceof X10Local_c) {
				X10Local_c x10local = (X10Local_c)array_expr;
				LocalDef def = x10local.localInstance().def();
				if(def.flags() != null && def.flags().contains(Flags.ATOMIC)) {
					atomicLocalReassigns.add(def);
				}
			}
			if(array_expr instanceof X10Field_c) {
				X10Field_c x10field = (X10Field_c)array_expr;
				FieldDef def = x10field.fieldInstance().def();
				if(def.flags() != null && def.flags().contains(Flags.ATOMIC)) {
					atomicFieldReassigns.add(def);
				}
			}
		}
		if(n instanceof SettableAssign_c) {
			//Do we need to implement it
		}
        return super.leave(old, n, v);
	}
	
	//get the escaped local defs and fields
	public Set<LocalDef> escapedLocalDefs() {
		Set<LocalDef> escaped = new LinkedHashSet<LocalDef>();
		escaped.addAll(atomicLocalReassigns);
		escaped.removeAll(atomicLocalDecls);
		return escaped;
	}
	public Set<FieldDef> escapedFieldDefs() {
		Set<FieldDef> escaped = new LinkedHashSet<FieldDef>();
		escaped.addAll(atomicFieldReassigns);
		escaped.removeAll(atomicFieldDecls);
		return escaped;
	}
	public boolean hasEscaped() {
		return !this.escapedFieldDefs().isEmpty() || !this.escapedLocalDefs().isEmpty();
	}
}

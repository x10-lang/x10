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
import x10.ast.Atomic_c;
import x10.ast.SettableAssign_c;
import x10.ast.X10FieldAssign_c;
import x10.ast.X10FieldDecl_c;
import x10.ast.X10Field_c;
import x10.ast.X10LocalDecl_c;
import x10.ast.X10Local_c;
import x10.types.X10FieldDef;
import x10.types.X10LocalDef;

class AtomicLocalAndFieldAccessVisitor extends NodeVisitor {

	public final Set<LocalDef> atomicLocalRefs = new LinkedHashSet<LocalDef>();
	public final Set<LocalDef> atomicLocalDecls = new LinkedHashSet<LocalDef>();

	public final Set<FieldDef> atomicFieldDefs = new LinkedHashSet<FieldDef>();
	public final Set<FieldDef> atomicFieldDecls = new LinkedHashSet<FieldDef>();

	public final Set<X10Field_c> allAtomicFields = new LinkedHashSet<X10Field_c>();
	
	public final Set<X10FieldDef> fieldsInAtomic = new LinkedHashSet<X10FieldDef>();
	public final Set<X10LocalDef> localsInAtomic = new LinkedHashSet<X10LocalDef>();

	@Override
	public Node leave(Node old, Node n, NodeVisitor v) {
		if(n instanceof Atomic_c) {
			Atomic_c atomic = (Atomic_c)n;
			fieldsInAtomic.addAll(atomic.getFieldsInAtomic());
			localsInAtomic.addAll(atomic.getLocalsInAtomic());
		}
		if(n instanceof X10Local_c) {
			X10Local_c x10local = (X10Local_c)n;
			if(x10local.localInstance() != null && x10local.localInstance().def() != null) {
			  if(x10local.localInstance().def().flags() != null
					  && x10local.localInstance().def().flags().contains(Flags.ATOMIC)) {
				//System.out.println("   --- Local def has flags: " + x10local);
				atomicLocalRefs.add(x10local.localInstance().def());
			  }
			}
		}
		if(n instanceof X10LocalDecl_c) {
			X10LocalDecl_c x10localdecl = (X10LocalDecl_c)n;
			if(x10localdecl.localDef().flags() != null
					&& x10localdecl.localDef().flags().contains(Flags.ATOMIC)) {
				atomicLocalDecls.add(x10localdecl.localDef());
			}
		}
		if(n instanceof X10Field_c) {
			X10Field_c x10field = (X10Field_c)n;
			if(x10field.fieldInstance() != null && x10field.fieldInstance().def() != null) {
    		  FieldDef def = x10field.fieldInstance().def();
			  if(def.flags() != null && def.flags().contains(Flags.ATOMIC)) {
//				  System.out.println("add def: " + def + ",  and field: " + x10field);
				atomicFieldDefs.add(def);
				//also save the field
				allAtomicFields.add(x10field);
			  }
			}
				
		}
		if(n instanceof X10FieldDecl_c) {
			X10FieldDecl_c x10fieldDecl = (X10FieldDecl_c)n;
			X10FieldDef def = x10fieldDecl.fieldDef();
			if(def.flags() != null && def.flags().contains(Flags.ATOMIC)) {
				atomicFieldDecls.add(def);
			}
		}
		if(n instanceof X10FieldAssign_c) {
			X10FieldAssign_c fieldAssign = (X10FieldAssign_c)n;
			FieldDef def = fieldAssign.fieldInstance().def();
			if(def.flags() != null && def.flags().contains(Flags.ATOMIC)) {
				atomicFieldDefs.add(def);
				//also save the field
				assert fieldAssign.left() instanceof X10Field_c;
				allAtomicFields.add((X10Field_c)fieldAssign.left());
			}
		}
		//FIXME not sure is it correct
		//the case of array declaration and array access
		//array declaration is included in local or field declaration
//		if(n instanceof ArrayAccessAssign_c) {
//			System.out.println("visiting array access: " + n);
//			ArrayAccessAssign_c arrayAssign = (ArrayAccessAssign_c)n;
//			Expr array_expr = arrayAssign.array();
//			if(array_expr instanceof X10Local_c) {
//				X10Local_c x10local = (X10Local_c)array_expr;
//				LocalDef def = x10local.localInstance().def();
//				if(def.flags() != null && def.flags().contains(Flags.ATOMIC)) {
//					atomicLocalRefs.add(def);
//				}
//			}
//			if(array_expr instanceof X10Field_c) {
//				X10Field_c x10field = (X10Field_c)array_expr;
//				FieldDef def = x10field.fieldInstance().def();
//				if(def.flags() != null && def.flags().contains(Flags.ATOMIC)) {
//					atomicFieldDefs.add(def);
//					//add to the field decl list
//					allAtomicFields.add(x10field);
//				}
//			}
//		}
		if(n instanceof SettableAssign_c) {
			//Do we need to implement it
		}
        return super.leave(old, n, v);
    }

	public Set<LocalDef> escapedLocalDefs() {
		Set<LocalDef> escaped = new LinkedHashSet<LocalDef>();
		escaped.addAll(atomicLocalRefs);
		escaped.removeAll(atomicLocalDecls);
		return escaped;
	}

	public Set<FieldDef> escapedFieldDefs() {
		Set<FieldDef> escaped = new LinkedHashSet<FieldDef>();
		escaped.addAll(atomicFieldDefs);
		escaped.removeAll(atomicFieldDecls);
		return escaped;
	}

	public boolean hasEscaped() {
		return !this.escapedFieldDefs().isEmpty()
				|| !this.escapedLocalDefs().isEmpty();
	}

	public Set<X10Field_c> getAllX10FieldsForEscapedFieldDefs() {
		Set<X10Field_c> x10fieldset = new LinkedHashSet<X10Field_c>();
		Set<FieldDef> allEscapedFields = this.escapedFieldDefs();
		for (FieldDef def : allEscapedFields) {
			X10Field_c f = this.getDefinedField(def);
			assert f != null;
			x10fieldset.add(f);
		}
		assert x10fieldset.size() == allEscapedFields.size();
		return x10fieldset;
	}

	public X10Field_c getDefinedField(FieldDef def) {
		for (X10Field_c x10field : allAtomicFields) {
			if (x10field.fieldInstance().def().equals(def)) {
				return x10field;
			}
		}
		// if contains, it must be defined
		assert !this.escapedFieldDefs().contains(def) : "Def: " + def
				+ " not found.";
		return null;
	}
}

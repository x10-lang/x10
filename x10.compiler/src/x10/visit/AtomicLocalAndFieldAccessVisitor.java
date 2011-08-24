package x10.visit;

import java.util.LinkedHashSet;
import java.util.Set;

import polyglot.ast.Node;
import polyglot.types.FieldDef;
import polyglot.types.Flags;
import polyglot.types.LocalDef;
import polyglot.visit.NodeVisitor;
import x10.ast.X10FieldDecl_c;
import x10.ast.X10Field_c;
import x10.ast.X10LocalDecl_c;
import x10.ast.X10Local_c;
import x10.types.X10FieldDef;

class AtomicLocalAndFieldAccessVisitor extends NodeVisitor {

	public final Set<LocalDef> atomicLocalRefs = new LinkedHashSet<LocalDef>();
	public final Set<LocalDef> atomicLocalDecls = new LinkedHashSet<LocalDef>();
	
	public final Set<FieldDef> atomicFieldDefs = new LinkedHashSet<FieldDef>();
	public final Set<FieldDef> atomicFieldDecls = new LinkedHashSet<FieldDef>();
	
	@Override
	public Node leave(Node old, Node n, NodeVisitor v) {
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
				atomicFieldDefs.add(def);
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
		return !this.escapedFieldDefs().isEmpty() || !this.escapedLocalDefs().isEmpty();
	}
}

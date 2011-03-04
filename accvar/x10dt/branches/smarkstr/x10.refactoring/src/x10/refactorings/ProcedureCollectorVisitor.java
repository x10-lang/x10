package x10.refactorings;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import polyglot.ast.Node;
import polyglot.ast.ProcedureDecl;
import polyglot.types.ProcedureInstance;
import polyglot.visit.NodeVisitor;

import com.ibm.wala.cast.x10.client.X10EclipseSourceAnalysisEngine;
import com.ibm.wala.types.ClassLoaderReference;
import com.ibm.wala.types.MethodReference;
import com.ibm.wala.types.Selector;

/**
 * A Polyglot visitor that determines all of the procedure declarations in
 * the AST.
 * 
 * @author Shane Markstrum
 * @author Robert Fuhrer
 */
class ProcedureCollectorVisitor extends polyglot.visit.NodeVisitor {

	ArrayList<ProcedureInstance> procs;

	ArrayList<ProcedureDecl> procDecls;

	X10EclipseSourceAnalysisEngine engine;

	public ProcedureCollectorVisitor(X10EclipseSourceAnalysisEngine engine) {
		super();
		procs = new ArrayList<ProcedureInstance>(10);
		procDecls = new ArrayList<ProcedureDecl>(10);
		this.engine = engine;
	}

	public Node leave(Node old, Node n, NodeVisitor v) {
		if (n instanceof ProcedureDecl
				&& !((ProcedureDecl) n).flags().isPrivate()) {
			procs.add(((ProcedureDecl) n).procedureInstance());
			procDecls.add((ProcedureDecl) n);
		}
		return n;
	}

	public ProcedureInstance[] getProcs() {
		ProcedureInstance[] processedProcs = new ProcedureInstance[procs
				.toArray().length];
		int j = 0;
		for (Iterator<ProcedureInstance> i = procs.iterator(); i.hasNext();) {
			processedProcs[j++] = i.next();
		}
		return processedProcs;
	}

	public ProcedureDecl[] getProcDecls() {
		ProcedureDecl[] processedProcs = new ProcedureDecl[procDecls
				.toArray().length];
		int j = 0;
		for (Iterator<ProcedureDecl> i = procDecls.iterator(); i.hasNext();) {
			processedProcs[j++] = i.next();
		}
		return processedProcs;
	}

	public List<MethodReference> getProcRefs(ClassLoaderReference cLoader) {
		ArrayList<MethodReference> procRefs = new ArrayList<MethodReference>(
				10);
		for (Iterator<ProcedureInstance> i = procs.iterator(); i.hasNext();) {
			procRefs.add(engine.getTranslatorExtension()
					.getIdentityMapper().getMethodRef(i.next()));
		}
		return procRefs;
	}

	public List<Selector> getProcSelectors(ClassLoaderReference cLoader) {
		ArrayList<Selector> procSels = new ArrayList<Selector>(10);
		for (Iterator<ProcedureInstance> i = procs.iterator(); i.hasNext();) {
			procSels.add(engine.getTranslatorExtension()
					.getIdentityMapper().getMethodRef(i.next())
					.getSelector());
		}
		return procSels;
	}
}
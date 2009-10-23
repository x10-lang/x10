package x10.refactorings;

import static x10.refactorings.ExtractAsyncStaticTools.IFilePathtoCAstPath;
import static x10.refactorings.ExtractAsyncStaticTools.dumpIR;
import static x10.refactorings.ExtractAsyncStaticTools.extractArrayName;
import static x10.refactorings.ExtractAsyncStaticTools.extractAsyncEntities;
import static x10.refactorings.ExtractAsyncStaticTools.extractProcEntities;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarFile;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.imp.parser.IParseController;
import org.eclipse.imp.parser.ISourcePositionLocator;
import org.eclipse.imp.services.IASTFindReplaceTarget;
import org.eclipse.imp.x10dt.ui.parser.ParseController;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.swt.graphics.Point;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.editors.text.TextEditor;

import polyglot.ast.ArrayAccess;
import polyglot.ast.Assign;
import polyglot.ast.Call;
import polyglot.ast.ClassDecl;
import polyglot.ast.CompoundStmt;
import polyglot.ast.Expr;
import polyglot.ast.Expr_c;
import polyglot.ast.Field;
import polyglot.ast.Local;
import polyglot.ast.Loop;
import polyglot.ast.NamedVariable;
import polyglot.ast.Node;
import polyglot.ast.Node_c;
import polyglot.ast.ProcedureDecl;
import polyglot.ast.Stmt;
import polyglot.ast.VarDecl;
import polyglot.ast.Variable;
import polyglot.ext.x10.ast.Atomic;
import polyglot.ext.x10.ast.Next;
import polyglot.ext.x10.ast.X10ArrayAccess;
import polyglot.ext.x10.ast.X10ArrayAccess1;
import polyglot.ext.x10.ast.X10Loop;
import polyglot.types.ClassType;
import polyglot.types.FieldInstance;
import polyglot.types.LocalInstance;
import polyglot.types.ProcedureInstance;
import polyglot.types.VarInstance;
import polyglot.visit.NodeVisitor;
import x10.refactorings.ExtractVarsVisitor.VarUseType;

import com.ibm.wala.cast.java.client.JavaSourceAnalysisEngine;
import com.ibm.wala.cast.java.translator.polyglot.IRTranslatorExtension;
import com.ibm.wala.cast.loader.AstMethod;
import com.ibm.wala.cast.tree.CAstEntity;
import com.ibm.wala.cast.tree.CAstNode;
import com.ibm.wala.cast.tree.CAstSourcePositionMap;
import com.ibm.wala.cast.x10.client.X10EclipseSourceAnalysisEngine;
import com.ibm.wala.cast.x10.ssa.AsyncCallSiteReference;
import com.ibm.wala.cast.x10.ssa.AsyncInvokeInstruction;
import com.ibm.wala.cast.x10.ssa.SSAAtomicInstruction;
import com.ibm.wala.cast.x10.ssa.SSAFinishInstruction;
import com.ibm.wala.cast.x10.ssa.SSARegionIterNextInstruction;
import com.ibm.wala.cast.x10.ssa.X10ArrayReferenceInstruction;
import com.ibm.wala.cast.x10.ssa.X10ArrayStoreByIndexInstruction;
import com.ibm.wala.cast.x10.ssa.X10ArrayStoreByPointInstruction;
import com.ibm.wala.cast.x10.translator.polyglot.X10IRTranslatorExtension;
import com.ibm.wala.classLoader.CallSiteReference;
import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.classLoader.IField;
import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.classLoader.JarFileModule;
import com.ibm.wala.classLoader.SourceFileModule;
import com.ibm.wala.client.AbstractAnalysisEngine;
import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.ipa.callgraph.Entrypoint;
import com.ibm.wala.ipa.callgraph.impl.DefaultEntrypoint;
import com.ibm.wala.ipa.callgraph.propagation.ArrayContentsKey;
import com.ibm.wala.ipa.callgraph.propagation.ConcreteTypeKey;
import com.ibm.wala.ipa.callgraph.propagation.InstanceFieldKey;
import com.ibm.wala.ipa.callgraph.propagation.InstanceKey;
import com.ibm.wala.ipa.callgraph.propagation.LocalPointerKey;
import com.ibm.wala.ipa.callgraph.propagation.NormalAllocationInNode;
import com.ibm.wala.ipa.callgraph.propagation.PointerAnalysis;
import com.ibm.wala.ipa.callgraph.propagation.PointerKey;
import com.ibm.wala.ipa.callgraph.propagation.PropagationCallGraphBuilder;
import com.ibm.wala.ipa.cha.ClassHierarchyException;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.ssa.IR;
import com.ibm.wala.ssa.SSAAbstractInvokeInstruction;
import com.ibm.wala.ssa.SSAAbstractThrowInstruction;
import com.ibm.wala.ssa.SSAArrayReferenceInstruction;
import com.ibm.wala.ssa.SSAArrayStoreInstruction;
import com.ibm.wala.ssa.SSAConditionalBranchInstruction;
import com.ibm.wala.ssa.SSAFieldAccessInstruction;
import com.ibm.wala.ssa.SSAGetInstruction;
import com.ibm.wala.ssa.SSAGotoInstruction;
import com.ibm.wala.ssa.SSAInstruction;
import com.ibm.wala.ssa.SSASwitchInstruction;
import com.ibm.wala.types.ClassLoaderReference;
import com.ibm.wala.types.FieldReference;
import com.ibm.wala.types.MethodReference;
import com.ibm.wala.types.Selector;
import com.ibm.wala.types.TypeName;
import com.ibm.wala.util.CancelException;
import com.ibm.wala.util.debug.Assertions;
import com.ibm.wala.util.debug.Trace;
import com.ibm.wala.util.intset.OrdinalSet;


public class ExtractAsyncRefactoring extends Refactoring {

	// Refactoring fields
	
	private final IFile fSourceFile;

	private final Node fNode;

	private Expr fPivot;

	private ProcedureDecl fNodeMethod;

	private IASTFindReplaceTarget ed;

	private String place;

	// DOMO/WALA analysis fields

	private CallGraph fCallGraph;

	private MethodReference fMethodRef;
	
	private IR fMethodIR;

	// Debug flag
	
	private static final boolean debugOutput = false;

	
	/**
	 * A dummy Expr class for representing gamma(var), to be used in sets of
	 * Expr's and gamma's, e.g. fPhi.
	 */
	// private class GammaExpr_c extends Local_c {
	// private final VarDecl fVarDecl;
	//
	// public GammaExpr_c(VarDecl vd) {
	// super(vd.position(), vd.name());
	// fVarDecl= vd;
	// }
	//
	// public VarDecl getVarDecl() {
	// return fVarDecl;
	// }
	// }
	private static class ThrowableStatus extends Exception {
		public RefactoringStatus status;

		public ThrowableStatus(RefactoringStatus status) {
			this.status = status;
		}

		public static ThrowableStatus createFatalErrorStatus(String msg) {
			return new ThrowableStatus(RefactoringStatus
					.createFatalErrorStatus(msg));
		}

		public static ThrowableStatus createErrorStatus(String msg) {
			return new ThrowableStatus(RefactoringStatus.createErrorStatus(msg));
		}

		public static ThrowableStatus createWarningStatus(String msg) {
			return new ThrowableStatus(RefactoringStatus
					.createWarningStatus(msg));
		}

		public static ThrowableStatus createInfoStatus(String msg) {
			return new ThrowableStatus(RefactoringStatus.createInfoStatus(msg));
		}
	}

	/**
	 * Maps variables onto a set of pointer keys
	 */
	private Map<Variable, PointerKey> fVar2PtrKeyMap;

	/**
	 * Maps statements to sets of lvalues that flow into them
	 */
	private Map<Stmt, Set<Expr>> fPhi;

	/**
	 * Maps lvalues onto a boolean that identifies whether the lvalue is a
	 * loop-carried dependency
	 */
	private Map<Expr, Boolean> fRho;

	/**
	 * The set of statements bearing loop-carried dependencies.
	 */
	private Set<Stmt> fDelta;

	private X10EclipseSourceAnalysisEngine fEngine;

	public String getName() {
		return "Extract Async";
	}

	/**
	 * Set up the extract async refactoring
	 * 
	 * @param editor - Must be a UniversalEditor or cast to
	 * 		IASTFindReplaceTarget is unsafe.
	 */
	public ExtractAsyncRefactoring(TextEditor editor) {
		super();

		ed = (IASTFindReplaceTarget) editor;
		IEditorInput input = editor.getEditorInput();

		fNodeMethod = null;
		if (input instanceof IFileEditorInput) {
			IFileEditorInput fileInput = (IFileEditorInput) input;

			fSourceFile = fileInput.getFile();
			fNode = findNode();//editor);
			// System.out.println(fNode.getClass());
		} else {
			fSourceFile = null;
			fNode = null;
		}
		place = "here";
	}

	public Node_c findNode(){//UniversalEditor editor) {
		Point sel = ed.getSelection();

		// System.out.println(.singleTestSrc(fGrammarFile));
		IParseController parseController = ed.getParseController();
		Node_c root = (Node_c) parseController.getCurrentAst();
//		SourceFile_c root = (SourceFile_c) parseController.getCurrentAst();
		ISourcePositionLocator locator = ((ParseController) parseController).getNodeLocator();

		Node_c retNode = (Node_c) locator.findNode(root, sel.x);
		Expr_c innerExprNode = (retNode instanceof Expr_c)?(Expr_c)retNode:null;
		Node_c tmp;
		Node_c last = null;
		for (int pos_index = sel.x; pos_index >= 0; pos_index--) {
			tmp = (Node_c) locator.findNode(root, pos_index, sel.x);
			if (!tmp.equals(last)) {System.out.println(tmp + " "+ tmp.getClass()); last = tmp;}
			if (innerExprNode == null && tmp instanceof Expr_c)
				innerExprNode = (Expr_c) tmp;
			if (tmp instanceof ProcedureDecl) {
				fNodeMethod = (ProcedureDecl) tmp;
				break;
			}
		}

		return innerExprNode;
	}

	private static class VarWithFirstUse {
		private VarInstance fVarInstance;

		private Expr fFirstUseExpr;

		private PointerKey fPtrKeyOfFirstUse;

		public VarWithFirstUse(VarInstance vi, Expr firstUse,
				PointerKey ptrKeyOfFirstUse) {
			fVarInstance = vi;
			fFirstUseExpr = firstUse;
			fPtrKeyOfFirstUse = ptrKeyOfFirstUse;
		}

		public Expr getFirstUse() {
			return fFirstUseExpr;
		}

		public VarInstance getVarInstance() {
			return fVarInstance;
		}

		public PointerKey getPtrKeyOfFirstUse() {
			return fPtrKeyOfFirstUse;
		}
		
		public boolean equals(Object o) {
			return o!=null && (o instanceof VarWithFirstUse) && (((VarWithFirstUse) o).getVarInstance() == null)?(fVarInstance == null && ((VarWithFirstUse)o).getFirstUse().equals(fFirstUseExpr)):((VarWithFirstUse) o).getVarInstance().equals(fVarInstance);
		}
		
		public String debug() {
			return "{{"+fVarInstance+","+fFirstUseExpr+","+fPtrKeyOfFirstUse+"}}";
		}
		
		public String toString() {
			return debugOutput?debug():fFirstUseExpr.toString();
		}
	}

	/*
	 * analyzeSource is pretty much a copy of the method from IRTests.java. It
	 * will be modified to do the appropriate pre-condition checking for the
	 * extract async/future refactoring.
	 */
	private RefactoringStatus analyzeSource(Collection sources, List libs, IJavaProject javaProject) throws CancelException {

		try {
			NodePathComputer pathSaver = new NodePathComputer(fNodeMethod,
					fPivot);
			List<Node> path = pathSaver.getPath();

			if (path.size() == 0)
				return RefactoringStatus
				.createFatalErrorStatus("Couldn't find path to selected node from containing method???");

			System.out.println("Path from method to pivot expr:");
			for (Node node : path) {
				System.out.println(node);
			}
			CompoundStmt loop = (CompoundStmt) PolyglotUtils
			.findInnermostNodeOfTypes(path, new Class[] { Loop.class,
					X10Loop.class });

			if (loop == null)
				return RefactoringStatus
				.createFatalErrorStatus("Couldn't find loop enclosing the selected node???");

			if (inAtomic(loop, fNodeMethod))
				return RefactoringStatus
				.createFatalErrorStatus("Cannot create asynchronous activity inside atomic block");
			if (inAtomic(fPivot, fNodeMethod))
				return RefactoringStatus
				.createFatalErrorStatus("Cannot create asynchronous activity inside atomic block");
			if (badClockUse(loop))
				return RefactoringStatus
				.createFatalErrorStatus("Bad clock use in target loop");

			doCreateIR(sources, libs, javaProject);

//			JavaCAst2IRTranslator irTranslator = ((X10IRTranslatorExtension) fEngine.getTranslatorExtension())
//			.getJavaCAst2IRTranslator();
//			CAstEntity rootEntity = irTranslator.sourceFileEntity();
			String filepath = IFilePathtoCAstPath(fSourceFile.getFullPath().toString());
			CAstEntity rootEntity = ((X10IRTranslatorExtension)fEngine.getTranslatorExtension()).getCAstEntity(filepath);

			// TODO Compute the var2PtrKey for the method, not the whole file
			fVar2PtrKeyMap = buildVarToPtrKeyMap(rootEntity);

			Set<VarDecl> inductionVars = findInductionVars(loop);

			// The information flow and set of statements having loop-carried
			// depe ndencies are used in several places, so compute them once up
			// front, save them in fields, and refer to them later.

			Set<VarWithFirstUse> loopRefedLVals = findLoopRefedLVals(loop);
			Map<VarWithFirstUse, Collection<InstanceKey>> pointerInfo = findPointsToSets(loopRefedLVals);
			Map<VarWithFirstUse, Set<VarWithFirstUse>> aliasInfo = computeAliasInfo(pointerInfo);

//			calculateInfoFlow(loop, aliasInfo); // saves phi and rho in fields
			calculateInfoFlow(loop, pointerInfo); // saves phi and rho in fields
			calculateLoopCarriedStatements(loop, inductionVars); // saves
			// delta in
			// a field

			if (!preconditionsHold(loop, fPivot, fNodeMethod))
				return RefactoringStatus
				.createFatalErrorStatus("Preconditions don't hold");
		} catch (ClassHierarchyException e) {
			e.printStackTrace();
			return RefactoringStatus.createFatalErrorStatus(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			return RefactoringStatus.createFatalErrorStatus(e.getMessage());
		} catch (ThrowableStatus ts) {
			return ts.status;
		}
		return new RefactoringStatus();
	}

	private Set<VarDecl> findInductionVars(CompoundStmt loop) {
		if (loop instanceof X10Loop) {
			X10Loop x10Loop = (X10Loop) loop;
			return Collections.singleton((VarDecl) x10Loop.formal());
		}
		// TODO handle other types of loops
		return Collections.emptySet();
	}

	/**
	 * Finds all lvalues referenced within the given AST sub-tree. The set of
	 * Variable's be the first uses of each given variable/field. We claim that
	 * these references can be on either the left-hand or right-hand sides of an
	 * assignment without adversely affecting the aliasing information.
	 */
	private static class LValueFinder extends NodeVisitor {
		private final Set<Variable> fLValues = new HashSet<Variable>();

		@Override
		public NodeVisitor enter(Node n) {
			if (n instanceof Variable && ((Variable)n).type().isReference()) {
			if (n instanceof Field) {
				Field f = (Field) n;
				if (f.target() instanceof Variable) {
					if (!containsLVal(f.fieldInstance(), extractArrayName((Variable)f.target())))
							fLValues.add(f);
				} else
					if (!containsLVal(f.fieldInstance(), null))
						fLValues.add(f);
			} else if (n instanceof Local) {
				Local l = (Local) n;

				if (!containsLVal(l.localInstance(), null))
					fLValues.add(l);
			} else if (n instanceof ArrayAccess) {
				ArrayAccess aa = (ArrayAccess) n;
				
				if (aa.array() instanceof Variable)
					if (!containsLVal(null,extractArrayName((Variable)aa.array())))
						fLValues.add(aa);
				// do nothing - we treat all array accesses as the same (given
				// that
				// we check that all loop array accesses of an array are indexed
				// using the induction variable (otherwise there are
				// loop-carried
				// dependencies).
			} else if (n instanceof X10ArrayAccess1) {
				X10ArrayAccess1 aa = (X10ArrayAccess1) n;
				
				if (aa.array() instanceof Variable)
					if (!containsLVal(null,extractArrayName((Variable)aa.array())))
						fLValues.add(aa);
			}
			}
			return this;
		}

		private boolean containsLVal(VarInstance vi, NamedVariable par) {
			for (Variable var : fLValues) {
				if (var instanceof Field) {
					FieldInstance fi = ((Field) var).fieldInstance();

					if (fi.equals(vi)) {
						if ((par == null) || !((((Field)var).target() instanceof NamedVariable)||(((Field)var).target() instanceof ArrayAccess)))
							return true;
						else {
							NamedVariable rcv = extractArrayName((Variable)((Field)var).target());
							VarInstance parIns = par.varInstance();
							VarInstance rcvIns = rcv.varInstance();
							if (par.equals(rcv) || parIns.equals(rcvIns))
								return true;
						}
					}
				} else if (var instanceof Local) {
					LocalInstance li = ((Local) var).localInstance();

					if (li.equals(vi))
						return true;
				} else if ((var instanceof ArrayAccess) && vi == null) {
					NamedVariable arrayName = extractArrayName((Variable)((ArrayAccess)var).array());
					VarInstance vi2 = arrayName.varInstance();
					if (arrayName.equals(par) || vi2.equals(par.varInstance()))
						return true;
				}
			}
			return false;
		}

		public Set<Variable> getLValues() {
			return fLValues;
		}
	}

	private Set<VarWithFirstUse> findLoopRefedLVals(Stmt loop) {
		Set<VarWithFirstUse> result = new HashSet<VarWithFirstUse>();
		LValueFinder lvalFinder = new LValueFinder();
		loop.visit(lvalFinder);
		Set<Variable> lvals = lvalFinder.getLValues();

		for (Variable var : lvals) {
			VarInstance vi;
			if (var instanceof Local)
				vi = ((Local) var).localInstance();
			else if (var instanceof Field)
				vi = ((Field) var).fieldInstance();
			else
				vi = null;

			PointerKey ptrKey = fVar2PtrKeyMap.get(var);
			VarWithFirstUse vwfu = new VarWithFirstUse(vi, var, ptrKey);
			if (!result.contains(vwfu))
				result.add(vwfu);
		}
		return result;
	}

	private Map<VarWithFirstUse, Collection<InstanceKey>> findPointsToSets(
			Set<VarWithFirstUse> vars) {
		Map<VarWithFirstUse, Collection<InstanceKey>> result = new HashMap<VarWithFirstUse, Collection<InstanceKey>>();
		PointerAnalysis analysis = fEngine.getPointerAnalysis();
		ArrayList<VarWithFirstUse> fieldWorklist = new ArrayList<VarWithFirstUse>();

		for (VarWithFirstUse var : vars) {
			if (var.getFirstUse() instanceof Local) {
				PointerKey key = var.getPtrKeyOfFirstUse();
				if (key != null){
					OrdinalSet<InstanceKey> ptSet = analysis.getPointsToSet(key);
					result.put(var, OrdinalSet.toCollection(ptSet));
				} else {
					Collection<InstanceKey> emptyCol = Collections.emptySet();
					result.put(var, emptyCol);
				}
			} else
				fieldWorklist.add(var);
		}
		
		while(!fieldWorklist.isEmpty()) {
			VarWithFirstUse field = fieldWorklist.remove(0);
			Variable fieldTarget;
			if (field.getFirstUse() instanceof Field) {
			Field fieldVar = (Field) field.getFirstUse();
			fieldTarget = (Variable) fieldVar.target();
			} else if (field.getFirstUse() instanceof ArrayAccess){
				ArrayAccess arrayVar = (ArrayAccess) field.getFirstUse();
				fieldTarget = (Variable) arrayVar.array();
			} else if (field.getFirstUse() instanceof X10ArrayAccess){
				X10ArrayAccess arrayVar = (X10ArrayAccess) field.getFirstUse();
				fieldTarget = (Variable) arrayVar.array();
			} else {
				X10ArrayAccess1 arrayVar = (X10ArrayAccess1) field.getFirstUse();
				fieldTarget = (Variable) arrayVar.array();
			}
			// TODO distinguish between an array variable and its elements?
			VarWithFirstUse target = getVarWithFirstUse(vars, fieldTarget);
			if (fieldWorklist.contains(target))
				fieldWorklist.add(field);
			else {
				Collection<InstanceKey> targetPtSet = result.get(target);
				HashSet<InstanceKey> fieldPtSet = new HashSet<InstanceKey>();
				for (InstanceKey ikey : targetPtSet) {
					PointerKey fieldKey;
					if (field.getPtrKeyOfFirstUse() instanceof InstanceFieldKey)
						fieldKey =new InstanceFieldKey(ikey,((InstanceFieldKey)field.getPtrKeyOfFirstUse()).getField());
					else
						fieldKey = new ArrayContentsKey(ikey); // I think this should be ArrayContentsKey
//						fieldKey = new ArrayInstanceKey(ikey); // I think this should be ArrayContentsKey
					OrdinalSet<InstanceKey> instancePtSet = analysis.getPointsToSet(fieldKey);
					fieldPtSet.addAll(OrdinalSet.toCollection(instancePtSet));
				}
				result.put(field, fieldPtSet);
			}
		}
		return result;
	}

	private VarWithFirstUse getVarWithFirstUse(Set<VarWithFirstUse> vars, Variable var){
		boolean arrayMode = ((var instanceof ArrayAccess)||(var instanceof X10ArrayAccess)||(var instanceof X10ArrayAccess1))?true:false;
		for (VarWithFirstUse v : vars){
			if (arrayMode && v.getVarInstance() == null) {
				VarWithFirstUse varTarget = getVarWithFirstUse(vars, extractArrayName(var));
				VarWithFirstUse vTarget = getVarWithFirstUse(vars, extractArrayName((Variable)v.getFirstUse()));
				if ((varTarget == null) || (vTarget == null) || (varTarget.getVarInstance().equals(vTarget.getVarInstance())) || (varTarget.getFirstUse().equals(vTarget.getFirstUse())))
					return v;
			}
			else if (!arrayMode && v.getVarInstance() == extractArrayName(var).varInstance()) {
				if ((var instanceof Field)&&(v.getFirstUse() instanceof Field)){
					VarWithFirstUse varTarget = getVarWithFirstUse(vars, (Variable)((Field)var).target());
					VarWithFirstUse vTarget = getVarWithFirstUse(vars, (Variable)((Field)v.getFirstUse()).target());
					if ((varTarget == null) || (varTarget.getFirstUse().equals(vTarget.getFirstUse()) || varTarget.getVarInstance().equals(vTarget.getVarInstance())))
						return v;
				} else
					return v;
			}
		}
		return null;
	}
	
	private Map<VarWithFirstUse, Set<VarWithFirstUse>> computeAliasInfo(
			Map<VarWithFirstUse, Collection<InstanceKey>> pointsToInfo) {
		Map<VarWithFirstUse, Set<VarWithFirstUse>> resultMap = new HashMap<VarWithFirstUse, Set<VarWithFirstUse>>();
		for (Map.Entry<VarWithFirstUse, Collection<InstanceKey>> e : pointsToInfo.entrySet()){
			Set<VarWithFirstUse> eAlias = new HashSet<VarWithFirstUse>();
			for (Map.Entry<VarWithFirstUse, Collection<InstanceKey>> e2: pointsToInfo.entrySet())
				if (!(Collections.disjoint(e.getValue(), e2.getValue())))
					eAlias.add(e2.getKey());
			resultMap.put(e.getKey(),eAlias);
		}
		return resultMap;
	}

	/**
	 * Analyzes the given loop and sets fPhi and fRho accordingly.
	 * 
	 * @param loop
	 */
	private void calculateInfoFlow(Stmt loop,
//			Map<VarWithFirstUse, Set<VarWithFirstUse>> aliasInfo) {
			Map<VarWithFirstUse, Collection<InstanceKey>> aliasInfo) {
		fPhi = new HashMap<Stmt, Set<Expr>>();
		fRho = new HashMap<Expr, Boolean>();
	}

	/**
	 * Analyzes the given loop and sets fDelta accordingly.
	 * 
	 * @param loop
	 * @param inductionVars
	 */
	private void calculateLoopCarriedStatements(Stmt loop,
			Set<VarDecl> inductionVars) {
		fDelta = new HashSet<Stmt>();
	}

	private boolean inAtomic(Node node, Node methodParent) {
		NodePathComputer pathSaver = new NodePathComputer(methodParent, node);
		List<Node> path = pathSaver.getPath();

		return (PolyglotUtils.findNodeOfType(path, Atomic.class) != null);
	}

	private class BadClockVisitor extends NodeVisitor {
		private boolean fResult = false;

		public BadClockVisitor() {
		}

		@Override
		public Node override(Node n) {
			return fResult ? n : null;
		}

		@Override
		public Node leave(Node old, Node n, NodeVisitor v) {
			if (n instanceof Call) {
				Call c = (Call) n;
				ClassType type = c.target().type().toClass();
				if (type != null && type.fullName().equals("x10.lang.clock")
						&& !c.name().equals("resume"))
					fResult = true;
			}
			if (n instanceof Next)
				fResult = true;
			return n;
		}

		public boolean getResult() {
			return fResult;
		}
	}

	// Examines loop body for any clock manipulations other than resume()
	private boolean badClockUse(Stmt stmt) {
		BadClockVisitor bc = new BadClockVisitor();
		stmt.visit(bc);
		return bc.getResult();
	}

	/**
	 * Computes the set of lvalues written to by a given AST sub-tree. Ignores
	 * aliasing effects (that's handled elsewhere). N.B.: At the moment, a given
	 * instance of this class can't be reused to scan multiple AST's; the
	 * results will aggregate.
	 */
	private static class EffectsVisitor extends NodeVisitor {
		private final Set<Expr> fLValsToWatch;

		private final Set<Expr> fEffects = new HashSet<Expr>();

		public EffectsVisitor(Set<Expr> lvalsToWatch) {
			fLValsToWatch = lvalsToWatch;
		}

		@Override
		public NodeVisitor enter(Node n) {
			if (n instanceof Assign) {
				Assign a = (Assign) n;

				fEffects.add(a.left());
			}
			return this;
		}

		public Set<Expr> getEffects() {
			return fEffects;
		}
	}

	/**
	 * Returns true if the given node contains any side-effects on any of the
	 * given lvalues.
	 */
	private boolean hasEffectsOn(Node node, Set<Expr> lvals) {
		EffectsVisitor ev = new EffectsVisitor(lvals);
		node.visit(ev);
		return (ev.getEffects().size() > 0);
	}

	private boolean preconditionsHold(Stmt loop, Expr pivot,
			ProcedureDecl method) {
		if (hasEffectsOn(pivot, null))
			return false;
		return true;
	}

	private Map<Variable, PointerKey> buildVarToPtrKeyMap(CAstEntity rootEntity)
	throws ThrowableStatus {
		Map<Variable, PointerKey> resultMap = new HashMap<Variable, PointerKey>();

		ExtractVarsVisitor ev = new ExtractVarsVisitor();
		((Node_c) ed.getParseController().getCurrentAst()).visit(ev);
		Map<RefactoringPosition, Variable> evMap = ev.getVarMap();

		// Map from variable references to the corresponding CAstNode.
		HashMap<CAstNode, Variable> cast2VarMap = new HashMap<CAstNode, Variable>(
				evMap.size());

		// Collect the necessary CAstEntities to access all of the necessary
		// CAstNodes

		Collection<CAstEntity> procEntities = extractProcEntities(rootEntity);
		procEntities.addAll(extractAsyncEntities(procEntities));

		// A multi-map from positions to variable CAstNodes
		RefactoringMap<RefactoringPosition, CAstNode> castNodeVarMap = new RefactoringMap<RefactoringPosition, CAstNode>();
		// A map from variable CAstNodes to their VarUseType
		Map<CAstNode, NodeType> castNodeType = new HashMap<CAstNode, NodeType>();

		// Iterate over the CAstNodes and match source positions
		buildCast2PolyglotVariableMap(ev, evMap, cast2VarMap, procEntities, castNodeVarMap, castNodeType);

		// Not used in practice, but useful for debugging
		Set<AstMethod> astMeths = new HashSet<AstMethod>();

		// A multi-map from positions to ssa instructions and cgnodes
		RefactoringMap<RefactoringPosition, InstructionContainer> sourceToInstructionMap = new RefactoringMap<RefactoringPosition, InstructionContainer>();

		// Build up the AstMethod set (for debugging purposes) and source-to-SSAInstruction map
		buildSourceToInstructionMap(fMethodRef, astMeths,
				sourceToInstructionMap);

		// Build CAstNode to PointerKey map.
		Map<CAstNode, PointerKey> cast2KeyMap = buildCAst2PointerKeyMap(cast2VarMap, castNodeVarMap, castNodeType, sourceToInstructionMap);

		// Now map CAstNode variable references to the corresponding
		// PointerKey's.
		for (CAstNode c : cast2VarMap.keySet()){
			resultMap.put(cast2VarMap.get(c), cast2KeyMap.get(c));
		}
		
		// Resulting map now allows you to determine what pointer keys are necessary
		// for analysis of methods/loops
		return resultMap;
	}

	/**
	 * Builds the map from CAstNodes to Polyglot Variables. Also builds
	 * the maps from source positions to CAstNodes (castNodeVarMap) and from
	 * CAstNodes to VarUseType values (castNodeType).
	 * 
	 * @param ev ExtractVarsVisitor - used for constructing castNodeType
	 * @param evMap Map from source position to Polyglot Variables
	 * @param cast2VarMap Maps CAstNodes to Polyglot Variables (Constructed by this method)
	 * @param procEntities The CAstEntities for the source file containing the refactoring pivot expression
	 * @param castNodeVarMap Maps source positions to CAstNodes (Constructed by this method)
	 * @param castNodeType Maps CAstNodes to VarUseType values (Constructed by this method)
	 */
	private void buildCast2PolyglotVariableMap(ExtractVarsVisitor ev, Map<RefactoringPosition, Variable> evMap, HashMap<CAstNode, Variable> cast2VarMap, Collection<CAstEntity> procEntities, RefactoringMap<RefactoringPosition, CAstNode> castNodeVarMap, Map<CAstNode, NodeType> castNodeType) {
		for (CAstEntity procEntity : procEntities) {
			for (Iterator i = procEntity.getSourceMap().getMappedNodes(); i
			.hasNext();) {
				CAstNode c = (CAstNode) i.next();
				CAstSourcePositionMap.Position p = procEntity.getSourceMap()
				.getPosition(c);
				
				// If Position values are negative, then this is a generated node
				if (p.getFirstLine() > 0 && p.getLastLine() > 0 && p.getFirstCol() > 0 && p.getLastCol() > 0){
					RefactoringPosition rp = new RefactoringPosition(p);
					Variable testVar = evMap.get(rp);
					if (testVar != null && !cast2VarMap.containsValue(testVar)) {
						int type = ev.getVarType(testVar);
						if ((type & VarUseType.DECL) != 0) {
							if ((c.getKind() == CAstNode.DECL_STMT) && (testVar instanceof NamedVariable) && (c.getChild(0).getValue().toString().equals(((NamedVariable)testVar).name()))) {
								// add mapping from current CAstNode to associated Polyglot Variable
								cast2VarMap.put(c, testVar);

								// For variable declarations, CAstNode should be mapped to by an
								// initialization expression position (if it exists)

								for (int j = 1; j < c.getChildCount(); j++){
									CAstSourcePositionMap.Position cp = procEntity.getSourceMap().getPosition(c.getChild(j));
									if (cp != null) {
										RefactoringPosition rcp = new RefactoringPosition(cp);
										castNodeVarMap.putVoid(rcp, c);
									}
								}

								// Add mapping from CAstNode source position to CAstNode
								castNodeVarMap.putVoid(rp, c);

								// Add mapping from CAstNode to VarUseType value
								castNodeType.put(c, new NodeType(type,
										((type & VarUseType.PARAM)!=0)?ev.vutype.getParamNumber(testVar):-1,
												((type & VarUseType.DREF)!=0)?ev.vutype.getDrefNumber(testVar):-1));
							}
						} else {
							// add mapping from current CAstNode to associated Polyglot Variable
							cast2VarMap.put(c, testVar);
							// Add mapping from CAstNode source position to CAstNode
							castNodeVarMap.putVoid(rp, c);

							// Add mapping from CAstNode to VarUseType value
							castNodeType.put(c, new NodeType(type,
									((type & VarUseType.PARAM)!=0)?ev.vutype.getParamNumber(testVar):-1,
											((type & VarUseType.DREF)!=0)?ev.vutype.getDrefNumber(testVar):-1));
						}

//						// add mapping from current CAstNode to associated Polyglot Variable
//						cast2VarMap.put(c, testVar);
//						
//						// For variable declarations, CAstNode should be mapped to by an
//						// initialization expression position (if it exists)
//						if (c.getKind() == CAstNode.DECL_STMT)
//						for (int j = 1; j < c.getChildCount(); j++){
//							CAstSourcePositionMap.Position cp = procEntity.getSourceMap().getPosition(c.getChild(j));
//							if (cp != null) {
//								RefactoringPosition rcp = new RefactoringPosition(cp);
//								castNodeVarMap.putVoid(rcp, c);
//							}
//						}
//						
//						// Add mapping from CAstNode source position to CAstNode
//						castNodeVarMap.putVoid(rp, c);
//						
//						// Add mapping from CAstNode to VarUseType value
//						int type = ev.getVarType(testVar);
//						castNodeType.put(c, new NodeType(type,
//								((type & VarUseType.PARAM)!=0)?ev.vutype.getParamNumber(testVar):-1,
//								((type & VarUseType.DREF)!=0)?ev.vutype.getDrefNumber(testVar):-1));
					}
				}
			}
		}
	}

	private class NodeType {
		public int type = -1;
		public int param = -1;
		public int dref = -1;
		
		public NodeType(int type, int param, int dref){
			this.type = type;
			this.param = param;
			this.dref = dref;
		}
	}
	
	private Map<CAstNode, PointerKey> buildCAst2PointerKeyMap(HashMap<CAstNode, Variable> cast2VarMap, RefactoringMap<RefactoringPosition, CAstNode> castNodeVarMap, Map<CAstNode, NodeType> castNodeType, RefactoringMap<RefactoringPosition, InstructionContainer> sourceToInstructionMap) {
		Map<CAstNode, PointerKey> cast2KeyMap = new HashMap<CAstNode, PointerKey>();

		// Will involve correlating CAstNode with relative position within
		// assignments
		// and binary statements (i.e., left/right) of SSAInstructions
		for (Map.Entry<RefactoringPosition, InstructionContainer> e : sourceToInstructionMap
				.entryCollection()) {
			// Variable corrVariable = evMap.get(e.getKey());
			SSAInstruction inst = e.getValue().instruction;
			CGNode srcNode = e.getValue().callGraphNode;
			RefactoringPosition.setContainsMode();
			Collection<CAstNode> cNodes = castNodeVarMap.getAll(e.getKey());
			RefactoringPosition.unsetContainsMode();
			for (CAstNode cNode : cNodes){
				NodeType cNodeType = castNodeType.get(cNode);
				if (cNodeType != null) {
					Integer cType = cNodeType.type;
					if (cType != null && !cast2KeyMap.containsKey(cNode)) {
						if ((cType & VarUseType.ARRAY) != 0){
							createDummyArrayInstanceKey(cast2VarMap, cast2KeyMap, cNode, srcNode);
						}
						else if((cType & VarUseType.LVAL) != 0) {
							if ((cType & VarUseType.PUTFIELD) != 0)
								createDummyInstanceFieldKey(cast2VarMap, cast2KeyMap, cNode);
							else if (!(inst instanceof AsyncInvokeInstruction)) {
								if ((cType & VarUseType.LOOP_VAR) != 0) {
									if (inst instanceof SSARegionIterNextInstruction)
										cast2KeyMap.put(cNode, new LocalPointerKey(srcNode, inst.getDef()));
								} else
									cast2KeyMap.put(cNode, new LocalPointerKey(srcNode, 
											(inst.getDef() == -1)?inst.getUse(0):inst.getDef()));
							}
						}
						else if ((cType & (VarUseType.RVAL | VarUseType.LEFT)) != 0) {
							if ((cType & VarUseType.GETFIELD) != 0)
								createDummyInstanceFieldKey(cast2VarMap, cast2KeyMap, cNode);
							else {
								if ((inst instanceof SSAArrayStoreInstruction) || (inst instanceof X10ArrayStoreByPointInstruction)){
									cast2KeyMap.put(cNode, new LocalPointerKey(srcNode, inst.getUse(2)));
								}
								else if (inst instanceof X10ArrayStoreByIndexInstruction) {
									cast2KeyMap.put(cNode, new LocalPointerKey(srcNode, inst.getUse(inst.getNumberOfUses()-1)));
								}
								else if ((inst instanceof SSAArrayReferenceInstruction) || (inst instanceof SSAFieldAccessInstruction)
										|| (inst instanceof X10ArrayReferenceInstruction))
									cast2KeyMap.put(cNode, new LocalPointerKey(srcNode, 
											(inst.getDef() == -1)?inst.getUse(1):inst.getUse(0)));
							}
						}
						else if ((cType & VarUseType.RIGHT) != 0)
							cast2KeyMap.put(cNode, new LocalPointerKey(srcNode, inst
									.getUse(1)));
						else if ((cType & VarUseType.PARAM) != 0) {
							if ((inst instanceof SSAAbstractInvokeInstruction) && !(inst instanceof AsyncInvokeInstruction)){
								int paramOffset;
								if (inst instanceof SSAAbstractInvokeInstruction) {
									SSAAbstractInvokeInstruction invokeInst = (SSAAbstractInvokeInstruction) inst;
									paramOffset = (invokeInst.isStatic())?-1:0;
								} else
									paramOffset = -1;

								int param = castNodeType.get(cNode).param;
								cast2KeyMap.put(cNode, new LocalPointerKey(srcNode, inst.getUse(((param==4)?3:param)+paramOffset)));
							} else if (inst instanceof SSAGetInstruction) {
								if ((cType & VarUseType.GETFIELD) != 0){
									createDummyInstanceFieldKey(cast2VarMap, cast2KeyMap, cNode);
								} else
									cast2KeyMap.put(cNode, new LocalPointerKey(srcNode, inst.getUse(0)));
							}
						}
						else if ((cType & VarUseType.INVOKE_TARGET)!=0){
							if ((inst instanceof SSAAbstractInvokeInstruction) && !(inst instanceof AsyncInvokeInstruction)){
								if ((cType & VarUseType.DREF) == 0)
									cast2KeyMap.put(cNode, new LocalPointerKey(srcNode, inst.getUse(0)));
							} else if (inst instanceof SSAGetInstruction) {
								if ((cType & VarUseType.GETFIELD) != 0){
									createDummyInstanceFieldKey(cast2VarMap, cast2KeyMap, cNode);
								} else
									cast2KeyMap.put(cNode, new LocalPointerKey(srcNode, inst.getUse(0)));
							}
						}
						else if ((cType & VarUseType.POINT)!=0){
							if (inst instanceof X10ArrayReferenceInstruction)
								cast2KeyMap.put(cNode, new LocalPointerKey(srcNode, inst.getUse(1)));
						}
					}
				}
			}
		}
		return cast2KeyMap;
	}

	private void createDummyInstanceFieldKey(HashMap<CAstNode, Variable> cast2VarMap, Map<CAstNode, PointerKey> cast2KeyMap, CAstNode cNode) {
		TypeName typeName;
		if (cast2VarMap.get(cNode.getChild(0)) != null)
			typeName = TypeName.findOrCreate("L"+cast2VarMap.get(cNode.getChild(0)).type().toString().replace('.', '/'));
		else
			typeName = ((FieldReference)cNode.getChild(1).getValue()).getDeclaringClass().getName();
		IClass cNodeClass = ((X10IRTranslatorExtension) fEngine.getTranslatorExtension()).getSourceLoader().lookupClass(typeName);
		IField cNodeField = cNodeClass.getField(((FieldReference) cNode.getChild(1).getValue()).getName());
		cast2KeyMap.put(cNode, new InstanceFieldKey(new ConcreteTypeKey(cNodeClass), cNodeField));
	}

	private void createDummyArrayInstanceKey(HashMap<CAstNode, Variable> cast2VarMap, Map<CAstNode, PointerKey> cast2KeyMap, CAstNode cNode, CGNode srcNode) {
		TypeName typeName = TypeName.findOrCreate("[L"+cast2VarMap.get(cNode.getChild(0)).type().toString().replace('.', '/'));
		IClass cNodeClass = ((X10IRTranslatorExtension) fEngine.getTranslatorExtension()).getSourceLoader().lookupClass(typeName);
// Shane's change
		cast2KeyMap.put(cNode, new ArrayContentsKey(new NormalAllocationInNode(srcNode,null,cNodeClass)));
//		cast2KeyMap.put(cNode, new ArrayContentsKey(new NormalAllocationSiteKey(srcNode,null,cNodeClass)));
//		cast2KeyMap.put(cNode, new ArrayInstanceKey(new NormalAllocationSiteKey(srcNode,null,cNodeClass)));
	}

	private void buildSourceToInstructionMap(MethodReference methRef,
			Set<AstMethod> astMeths,
			// TODO Multiple InstructionContainers might need to be stored for
			// a given Position: allow them to be chained?
//			Map<CAstSourcePositionMap.Position, InstructionContainer> map)
			RefactoringMap<RefactoringPosition, InstructionContainer> map)
	throws ThrowableStatus {
		Set<CGNode> srcNodes = fCallGraph.getNodes(methRef);
		if (srcNodes.size() == 0)
			throw ThrowableStatus
			.createFatalErrorStatus("Unreachable/non-existent method: "
					+ methRef);
		if (srcNodes.size() > 1)
			throw ThrowableStatus
			.createFatalErrorStatus("Context-sensitive call graph?");
		CGNode srcNode = srcNodes.iterator().next();
		IR amIR = srcNode.getIR();

//		IMethod iMethod = fLoader.lookupClass(
//		methRef.getDeclaringClass().getName()).getMethod(
//		methRef.getSelector());
		IMethod iMethod = srcNode.getMethod();
		if (!(iMethod instanceof AstMethod))
			throw ThrowableStatus
			.createFatalErrorStatus("Nonexistent parse tree for method???");
		AstMethod astMethod = (AstMethod) iMethod;
		astMeths.add(astMethod);
		int index = 0;
//		IR amIR = fEngine.Options.getSSACache().findOrCreateIR(astMethod,
//		Everywhere.EVERYWHERE, SSAOptions.defaultOptions());

		for (SSAInstruction sa : amIR.getInstructions()) {
			index++;
			if (sa == null || nonproductiveSSAInstruction(sa))
				continue;


			// System.out.println(astMethod.getName()+" SSAInstruction "+sa+" @
			// "+astMethod.getSourcePosition(index));
			CAstSourcePositionMap.Position ipos = astMethod.getSourcePosition(index-1);
			if (ipos != null){// && !map.containsKey(ipos)) {
				RefactoringPosition ripos = new RefactoringPosition(ipos);
				map.putVoid(ripos,
						new InstructionContainer(sa, srcNode));
			}

			if (sa instanceof SSAAbstractInvokeInstruction) {
				SSAAbstractInvokeInstruction ssai = (SSAAbstractInvokeInstruction) sa;
				CallSiteReference csr = ssai.getCallSite();
				if (csr instanceof AsyncCallSiteReference) {
					AsyncCallSiteReference acsr = (AsyncCallSiteReference) csr;
					MethodReference srcMethodRef = acsr.getDeclaredTarget();
					buildSourceToInstructionMap(srcMethodRef, astMeths, map);
				}
			}
		}
	}

	private boolean nonproductiveSSAInstruction(SSAInstruction ssai) {
	  return (ssai instanceof SSAGotoInstruction) || (ssai instanceof SSASwitchInstruction)
	  || (ssai instanceof SSAFinishInstruction) || (ssai instanceof SSAConditionalBranchInstruction)
	  || (ssai instanceof SSAAtomicInstruction) || (ssai instanceof SSAAbstractThrowInstruction);
	}
	
	private void doCreateIR(Collection sources, List libs, IJavaProject javaProject) throws IOException,
	ClassHierarchyException, ThrowableStatus, CancelException {
//		EclipseProjectSourceAnalysisEngine engine = new X10EclipseSourceAnalysisEngine(javaProject);
//		JavaSourceAnalysisEngine engine = new X10SourceAnalysisEngine();

//		populateScope(engine, sources, libs);

		ProcedureCollectorVisitor procedureCollection = new ProcedureCollectorVisitor();

		((Node) ed.getParseController().getCurrentAst())
		.visit(procedureCollection);

		final ProcedureInstance[] procs = procedureCollection.getProcs();
		AbstractAnalysisEngine.EntrypointBuilder entrypointBuilder = new AbstractAnalysisEngine.EntrypointBuilder() {
			public Iterable<Entrypoint> createEntrypoints(AnalysisScope scope,
					IClassHierarchy cha) {
				return new TestEntryPoints(procs, cha);
			}
		};
		fEngine.setEntrypointBuilder(entrypointBuilder);
		PropagationCallGraphBuilder builder = (PropagationCallGraphBuilder)fEngine.defaultCallGraphBuilder();
		fCallGraph = builder.makeCallGraph(builder.getOptions());

		// If we've gotten this far, IR has been produced.

		dumpIR(fCallGraph);

		com.ibm.wala.cast.ipa.callgraph.Util.dumpCG(builder, fCallGraph);
		
		List<ProcedureInstance> pc = procedureCollection.procs;

		checkCallGraphShape();
	}

	private static void populateScope(
//			EclipseProjectSourceAnalysisEngine engine,
			JavaSourceAnalysisEngine engine,
			Collection/* <String> */sources, List/* <String> */libs)
	throws IOException {
		boolean foundLib = false;
		for (Iterator iter = libs.iterator(); iter.hasNext();) {
			String lib = (String) iter.next();

			File libFile = new File(lib);
			if (libFile.exists()) {
				foundLib = true;
				engine.addSystemModule(new JarFileModule(new JarFile(libFile)));
			}
		}
		Assertions._assert(foundLib);

		for (Iterator iter = sources.iterator(); iter.hasNext();) {
			String srcFilePath = (String) iter.next();
			String srcFileName = srcFilePath.substring(srcFilePath
					.lastIndexOf(File.separator) + 1);

			engine.addSourceModule(new SourceFileModule(new File(srcFilePath),
					srcFileName));
		}
	}

	private void methodRefDebugOutput() throws IOException {
		/*
		 * int index= 0; for(Iterator irIter=
		 * fMethodIR.iterateAllInstructions(); irIter.hasNext();) {
		 * SSAInstruction sa= (SSAInstruction) irIter.next();
		 * System.out.println((index++) + " " + fMethodRef.getSignature() + " " +
		 * sa.getClass());
		 * 
		 * for(int i= 0; i < sa.getNumberOfUses(); i++) { System.out.print(">> " +
		 * sa.getUse(i)); String[] saUseVarNames= fMethodIR.getLocalNames(index,
		 * sa .getUse(i)); if (saUseVarNames == null) { System.out.println("
		 * local map is null"); break; } for(int j= 0; j < saUseVarNames.length;
		 * j++) System.out.print(" " + saUseVarNames[j]); System.out.println(); } }
		 * System.out.println();
		 */}

	/**
	 * Prints out all IR information for the selected
	 * method and stores the MethodReference for later analysis
	 *  
	 * @throws IOException
	 * @throws ThrowableStatus
	 */
	private void checkCallGraphShape()
	throws IOException, ThrowableStatus {

		// Our main concern is the method in which the selected text is located.
		// Below code prints out information specific to that method.

		Trace.println(fCallGraph.toString());

		ProcedureInstance srcMethod = fNodeMethod.procedureInstance();

		fMethodRef = fEngine.getTranslatorExtension().getIdentityMapper().getMethodRef(srcMethod);
		Set<CGNode> srcNodes = fCallGraph.getNodes(fMethodRef);

		// Print out debug information about the current method
		if (debugOutput)
			methodRefDebugOutput();
		if (srcNodes.size() == 0)
			throw ThrowableStatus
			.createFatalErrorStatus("Unreachable/non-existent method: "
					+ srcMethod);
		if (srcNodes.size() > 1)
			throw ThrowableStatus
			.createFatalErrorStatus("Context-sensitive call graph?");
		CGNode srcNode = srcNodes.iterator().next();

		fMethodIR = srcNode.getIR();

		Collection<LocalPointerKey> pointerKeys = new HashSet<LocalPointerKey>(
				23);

		System.out.println("CGNode info for " + fMethodRef.getSignature());
		IR methIR = fMethodIR;
		System.out.println(((com.ibm.wala.cast.loader.AstMethod) srcNode
				.getMethod()).getSourcePosition(0));
		for (Iterator irIter = methIR.iterateAllInstructions(); irIter
		.hasNext();) {
			SSAInstruction sa = (SSAInstruction) irIter.next();
			for (int i = 0; i < sa.getNumberOfUses(); i++)
				pointerKeys.add(new LocalPointerKey(srcNode, sa.getUse(i)));
		}
		for (LocalPointerKey p : pointerKeys)
			System.out.println(">> " + p.getValueNumber() + " PointerKey " + p);
	}

	public RefactoringStatus checkInitialConditions(IProgressMonitor pm)
	throws CoreException, OperationCanceledException {
		// Check for preconditions here

		if (fNodeMethod == null)
			return RefactoringStatus
			.createFatalErrorStatus("Unable to find method containing selected code");

		String javaHomePath = ExtractAsyncStaticTools.javaHomePath;

		// Only valid if it's equivalent to a statement: async, atomic, assign,
		// method call, etc.
		List<String> rtJar = new LinkedList<String>();
		rtJar.add(javaHomePath + File.separator + "lib" + File.separator
				+ "rt.jar");
		rtJar.add(javaHomePath + File.separator + "lib" + File.separator
				+ "core.jar");
		rtJar.add(javaHomePath + File.separator + "lib" + File.separator
				+ "vm.jar");

		List<String> x10RTJar = new ArrayList<String>();
		x10RTJar.addAll(rtJar);
		IJavaProject javaProject = JavaCore.create(fSourceFile.getProject());
		try{
			fEngine = new X10EclipseSourceAnalysisEngine(javaProject);
		} catch (IOException ioe) {
			return RefactoringStatus
			.createFatalErrorStatus("Unexpected exception generated when creating analysis engine.");
		}
		x10RTJar.add(ExtractAsyncStaticTools
				.getLanguageRuntimePath(javaProject).toString());

		if (!(fNode instanceof Expr))
			return RefactoringStatus
			.createFatalErrorStatus("Extract Async is only valid for expressions");

		fPivot = (Expr) fNode;

		NodeTypeFindingVisitor typeFinder = new NodeTypeFindingVisitor(
				ClassDecl.class);
		fNodeMethod.visit(typeFinder);

		if (typeFinder.getResult() != null)
			return RefactoringStatus
			.createFatalErrorStatus("Extract Async does not currently handle types nested inside method to be transformed.");

		
		try {
		  return this.analyzeSource(ExtractAsyncStaticTools
				.singleTestSrc(fSourceFile), x10RTJar, javaProject);
		} catch (CancelException e) {
			return RefactoringStatus.createFatalErrorStatus("Call-graph construction canceled by WALA");
		}
	}

	public RefactoringStatus checkFinalConditions(IProgressMonitor pm)
	throws CoreException, OperationCanceledException {
		// Atomic
		return new RefactoringStatus();
	}

	public void setPlace(String place) {
		if (place != "")
			this.place = place;
		else
			this.place = "here";
	}

	public Change createChange(IProgressMonitor pm) throws CoreException,
	OperationCanceledException {
		// Replace hand-written transform code with usage of SAFARI AST
		// rewriter.

		IParseController parseController = ed.getParseController();
		ISourcePositionLocator locator = ((ParseController) parseController).getNodeLocator();
		int startOffset = locator.getStartOffset(fPivot);
		int endOffset = locator.getEndOffset(fPivot);

		TextFileChange tfc = new TextFileChange("Extract Async", fSourceFile);
		tfc.setEdit(new MultiTextEdit());
		tfc.addEdit(new ReplaceEdit(startOffset, endOffset - startOffset + 1,
				"async (" + place + ") {" + fPivot + "}"));

		return tfc;
	}

	private final class TestEntryPoints implements Iterable<Entrypoint> {
		private final ProcedureInstance[] procs;

		private final IClassHierarchy cha;

		private TestEntryPoints(ProcedureInstance[] procs, IClassHierarchy cha) {
			this.procs = procs;
			this.cha = cha;
		}

		public Iterator<Entrypoint> iterator() {

			return new Iterator<Entrypoint>() {
				private int index = 0;

				public void remove() {
					Assertions.UNREACHABLE();
				}

				public boolean hasNext() {
					return index < procs.length;
				}

				public Entrypoint next() {
					ProcedureInstance proc = procs[index++];
					IRTranslatorExtension irx = fEngine.getTranslatorExtension();
					// N.B.: Don't use getMethodRef() here; in the process of canonicalizing
					// the MethodReferences it has to compare ProcedureInstances from two
					// different instances of Polyglot (one from the editor's ParseController,
					// and one from the WALA analysis engine), resulting in a "we are TypeSystem_c,
					// but type Foo is from TypeSystem_c" exception.
					return new DefaultEntrypoint(irx.getIdentityMapper().referenceForMethod(proc), cha);
				}
			};
		}
	}

	class ProcedureCollectorVisitor extends polyglot.visit.NodeVisitor {

		ArrayList<ProcedureInstance> procs;

		ArrayList<ProcedureDecl> procDecls;

		public ProcedureCollectorVisitor() {
			super();
			procs = new ArrayList<ProcedureInstance>(10);
			procDecls = new ArrayList<ProcedureDecl>(10);
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
			for (Iterator i = procs.iterator(); i.hasNext();) {
				processedProcs[j++] = (ProcedureInstance) i.next();
			}
			return processedProcs;
		}

		public ProcedureDecl[] getProcDecls() {
			ProcedureDecl[] processedProcs = new ProcedureDecl[procDecls
			                                                   .toArray().length];
			int j = 0;
			for (Iterator i = procDecls.iterator(); i.hasNext();) {
				processedProcs[j++] = (ProcedureDecl) i.next();
			}
			return processedProcs;
		}

		public List<MethodReference> getProcRefs(ClassLoaderReference cLoader) {
			ArrayList<MethodReference> procRefs = new ArrayList<MethodReference>(
					10);
			for (Iterator<ProcedureInstance> i = procs.iterator(); i.hasNext();) {
				procRefs.add(fEngine.getTranslatorExtension().getIdentityMapper()
						.getMethodRef(i.next()));
			}
			return procRefs;
		}

		public List<Selector> getProcSelectors(ClassLoaderReference cLoader) {
			ArrayList<Selector> procSels = new ArrayList<Selector>(10);
			for (Iterator<ProcedureInstance> i = procs.iterator(); i.hasNext();) {
				procSels.add(fEngine.getTranslatorExtension().getIdentityMapper()
						.getMethodRef(i.next()).getSelector());
			}
			return procSels;
		}
	}

	class InstructionContainer {
		public final SSAInstruction instruction;

		public final CGNode callGraphNode;
		
		public InstructionContainer(SSAInstruction instruction,
				CGNode callGraphNode) {
			this.instruction = instruction;
			this.callGraphNode = callGraphNode;
		}

		public String toString() {
			return instruction.toString();
		}
	}
}

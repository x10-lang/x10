package x10.refactorings;

import static x10.refactorings.utils.ExtractAsyncStaticTools.IFilePathtoCAstPath;
import static x10.refactorings.utils.ExtractAsyncStaticTools.dumpIR;
import static x10.refactorings.utils.ExtractAsyncStaticTools.extractArrayName;
import static x10.refactorings.utils.ExtractAsyncStaticTools.extractAsyncEntities;
import static x10.refactorings.utils.ExtractAsyncStaticTools.extractProcEntities;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
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
import polyglot.ast.Block;
import polyglot.ast.Block_c;
import polyglot.ast.Call;
import polyglot.ast.ClassDecl;
import polyglot.ast.CompoundStmt;
import polyglot.ast.Eval;
import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.Formal;
import polyglot.ast.Local;
import polyglot.ast.LocalDecl;
import polyglot.ast.Loop;
import polyglot.ast.NamedVariable;
import polyglot.ast.Node;
import polyglot.ast.Node_c;
import polyglot.ast.ProcedureDecl;
import polyglot.ast.Stmt;
import polyglot.ast.Stmt_c;
import polyglot.ast.VarDecl;
import polyglot.ast.Variable;
import polyglot.ext.x10.ast.AtEach;
import polyglot.ext.x10.ast.Atomic;
import polyglot.ext.x10.ast.Finish;
import polyglot.ext.x10.ast.ForEach;
import polyglot.ext.x10.ast.ForLoop;
import polyglot.ext.x10.ast.Next;
import polyglot.ext.x10.ast.X10ArrayAccess;
import polyglot.ext.x10.ast.X10ArrayAccess1;
import polyglot.ext.x10.ast.X10Loop;
import polyglot.ext.x10.ast.X10NodeFactory;
import polyglot.types.ClassType;
import polyglot.types.FieldInstance;
import polyglot.types.LocalInstance;
import polyglot.types.ProcedureInstance;
import polyglot.types.Type;
import polyglot.types.VarInstance;
import polyglot.visit.NodeVisitor;
import x10.refactorings.utils.ExtractAsyncStaticTools;
import x10.refactorings.utils.ExtractVarsVisitor;
import x10.refactorings.utils.Function;
import x10.refactorings.utils.NodePathComputer;
import x10.refactorings.utils.NodeTypeFindingVisitor;
import x10.refactorings.utils.PolyglotUtils;
import x10.refactorings.utils.ExtractVarsVisitor.VarUseType;

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
import com.ibm.wala.classLoader.SourceDirectoryTreeModule;
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
import com.ibm.wala.types.FieldReference;
import com.ibm.wala.types.MethodReference;
import com.ibm.wala.types.TypeName;
import com.ibm.wala.util.CancelException;
import com.ibm.wala.util.debug.Assertions;
import com.ibm.wala.util.intset.OrdinalSet;

public class ExtractAsyncRefactoring extends Refactoring {

	// Refactoring fields

	private final IFile fSourceFile;

	private final Node fNode;

	private Stmt fPivot;

	private ProcedureDecl fNodeMethod;

	private IASTFindReplaceTarget ed;

	private String place;

	// DOMO/WALA analysis fields

	private CallGraph fCallGraph;

	private MethodReference fMethodRef;

	private IR fMethodIR;

	// Debug flag

	static final boolean debugOutput = false;

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
	@SuppressWarnings( { "serial", "unused" })
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
	private Map<VarWithFirstUse, Boolean> fRho;

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
	 * @param editor
	 *            - Must be a UniversalEditor or cast to IASTFindReplaceTarget
	 *            is unsafe.
	 */
	public ExtractAsyncRefactoring(TextEditor editor) {
		super();

		ed = (IASTFindReplaceTarget) editor;
		IEditorInput input = editor.getEditorInput();

		fNodeMethod = null;
		if (input instanceof IFileEditorInput) {
			IFileEditorInput fileInput = (IFileEditorInput) input;

			fSourceFile = fileInput.getFile();
			fNode = findNode();// editor);
			// System.out.println(fNode.getClass());
		} else {
			fSourceFile = null;
			fNode = null;
		}
		place = "here";
	}

	/**
	 * Determines the statement that the user either highlighted or clicked on
	 * before selecting to apply the "Extract Concurrent" refactoring. This is
	 * only a best guess approximation that works out the statement by looking
	 * at successively larger selections of the code until a Stmt type is
	 * encountered. It will also continue to scan until it encounters the
	 * enclosing procedure declaration (stored in the fNodeMethod field).
	 * 
	 * @return the nearest Stmt node to the selected text
	 */
	public Node_c findNode() {// UniversalEditor editor) {
		Point sel = ed.getSelection();

		// System.out.println(.singleTestSrc(fGrammarFile));
		IParseController parseController = ed.getParseController();
		Node_c root = (Node_c) parseController.getCurrentAst();
		// SourceFile_c root = (SourceFile_c) parseController.getCurrentAst();
		ISourcePositionLocator locator = parseController
				.getSourcePositionLocator();

		Node_c retNode = (Node_c) locator.findNode(root, sel.x, sel.x + sel.y);
		Stmt_c innerStmtNode = (retNode instanceof Stmt_c) ? (Stmt_c) retNode
				: null;
		Node_c tmp;
		Node_c last = null;
		for (int pos_index = sel.x; pos_index >= 0; pos_index--) {
			tmp = (Node_c) locator.findNode(root, pos_index, sel.x + sel.y);
			if (!tmp.equals(last)) {
				System.out.println(tmp + " " + tmp.getClass());
				last = tmp;
			}
			if (innerStmtNode == null && tmp instanceof Stmt_c) {
				innerStmtNode = (Stmt_c) tmp;
			}
			if (tmp instanceof ProcedureDecl) {
				fNodeMethod = (ProcedureDecl) tmp;
				break;
			}
		}

		NodePathComputer pathSaver = new NodePathComputer(innerStmtNode,
				retNode);
		List<Node> path = pathSaver.getPath();

		innerStmtNode = (Stmt_c) PolyglotUtils.findInnermostNodeOfTypes(path,
				new Class[] { Stmt_c.class });

		return innerStmtNode;
	}

	/*
	 * analyzeSource is pretty much a copy of the method from IRTests.java. It
	 * will be modified to do the appropriate pre-condition checking for the
	 * extract async/future refactoring.
	 */
	private RefactoringStatus analyzeSource(Collection<String> sources,
			List<String> libs, IJavaProject javaProject) throws CancelException {
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

			// JavaCAst2IRTranslator irTranslator = ((X10IRTranslatorExtension)
			// fEngine.getTranslatorExtension())
			// .getJavaCAst2IRTranslator();
			// CAstEntity rootEntity = irTranslator.sourceFileEntity();
			String filepath = IFilePathtoCAstPath(fSourceFile.getFullPath()
					.toString());
			CAstEntity rootEntity = ((X10IRTranslatorExtension) fEngine
					.getTranslatorExtension()).getCAstEntity(filepath);

			// TODO Compute the var2PtrKey for the method, not the whole file
			fVar2PtrKeyMap = buildVarToPtrKeyMap(rootEntity);

			Set<VarDecl> inductionVars = findInductionVars(loop);

			// The information flow and set of statements having loop-carried
			// depe ndencies are used in several places, so compute them once up
			// front, save them in fields, and refer to them later.

			Set<VarWithFirstUse> loopRefedLVals = findLoopRefedLVals(loop);
			Map<VarWithFirstUse, Collection<InstanceKey>> pointerInfo = findPointsToSets(loopRefedLVals);
			Map<VarWithFirstUse, Set<VarWithFirstUse>> aliasInfo = computeAliasInfo(pointerInfo);

			// calculateInfoFlow(loop, aliasInfo); // saves phi and rho in
			// fields
			calculateInfoFlow(loop, aliasInfo); // saves phi and rho in fields
			calculateLoopCarriedStatements(loop, inductionVars); // saves
			// delta in
			// a field
			// randomlyGenerateLoopCarriedStatements(loop, inductionVars);

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

	/**
	 * Determines the set of variables that are used as induction variables for
	 * the provided loop.
	 * 
	 * @param loop
	 *            an X10 loop with induction variables
	 * @return the set of variables defined by the loop
	 */
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
			if (n instanceof Variable && ((Variable) n).type().isReference()) {
				if (n instanceof Field) {
					Field f = (Field) n;
					if (f.target() instanceof Variable) {
						if (!containsLVal(f.fieldInstance(),
								extractArrayName((Variable) f.target())))
							fLValues.add(f);
					} else if (!containsLVal(f.fieldInstance(), null))
						fLValues.add(f);
				} else if (n instanceof Local) {
					Local l = (Local) n;

					if (!containsLVal(l.localInstance(), null))
						fLValues.add(l);
				} else if (n instanceof ArrayAccess) {
					ArrayAccess aa = (ArrayAccess) n;

					if (aa.array() instanceof Variable)
						if (!containsLVal(null, extractArrayName((Variable) aa
								.array())))
							fLValues.add(aa);
					// do nothing - we treat all array accesses as the same
					// (given
					// that
					// we check that all loop array accesses of an array are
					// indexed
					// using the induction variable (otherwise there are
					// loop-carried
					// dependencies).
				} else if (n instanceof X10ArrayAccess1) {
					X10ArrayAccess1 aa = (X10ArrayAccess1) n;

					if (aa.array() instanceof Variable)
						if (!containsLVal(null, extractArrayName((Variable) aa
								.array())))
							fLValues.add(aa);
				}
			}
			return this;
		}

		/**
		 * Determines if the given variable has already been seen by the finder.
		 * Based on the variable instance, not the name of the variable.
		 * 
		 * @param vi
		 *            the instance to compare
		 * @param par
		 *            the name, in the case of fields
		 * @return true, if the variable has already been seen
		 */
		private boolean containsLVal(VarInstance vi, NamedVariable par) {
			for (Variable var : fLValues) {
				if (var instanceof Field) {
					FieldInstance fi = ((Field) var).fieldInstance();

					if (fi.equals(vi)) {
						if ((par == null)
								|| !((((Field) var).target() instanceof NamedVariable) || (((Field) var)
										.target() instanceof ArrayAccess)))
							return true;
						else {
							NamedVariable rcv = extractArrayName((Variable) ((Field) var)
									.target());
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
					NamedVariable arrayName = extractArrayName((Variable) ((ArrayAccess) var)
							.array());
					VarInstance vi2 = arrayName.varInstance();
					if (arrayName.equals(par) || vi2.equals(par.varInstance()))
						return true;
				}
			}
			return false;
		}

		/**
		 * Gets the calculated set of variables. Note: will return null in the
		 * case that the visitor has not been run.
		 * 
		 * @return the calculated set of variables.
		 */
		public Set<Variable> getLValues() {
			return fLValues;
		}
	}

	/**
	 * Determines the variables that are referenced in the loop and creates a
	 * set of associations between those variables and the previously calculated
	 * pointer keys for the method.
	 * 
	 * @param loop
	 *            the loop which will be duplicated.
	 * @return a set of variables and pointer keys.
	 */
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

	/**
	 * Creates a map between variables and instance keys for determining aliases
	 * and information flow.
	 * 
	 * @param vars
	 *            a set of variables to seed the points to set
	 * @return a points to map from variables to instance pointer keys
	 */
	private Map<VarWithFirstUse, Collection<InstanceKey>> findPointsToSets(
			Set<VarWithFirstUse> vars) {
		Map<VarWithFirstUse, Collection<InstanceKey>> result = new HashMap<VarWithFirstUse, Collection<InstanceKey>>();
		PointerAnalysis analysis = fEngine.getPointerAnalysis();
		ArrayList<VarWithFirstUse> fieldWorklist = new ArrayList<VarWithFirstUse>();

		// Seed points to set with basic points to information from pointer
		// analysis
		for (VarWithFirstUse var : vars) {
			if (var.getFirstUse() instanceof Local) {
				PointerKey key = var.getPtrKeyOfFirstUse();
				if (key != null) {
					OrdinalSet<InstanceKey> ptSet = analysis
							.getPointsToSet(key);
					result.put(var, OrdinalSet.toCollection(ptSet));
				} else {
					Collection<InstanceKey> emptyCol = Collections.emptySet();
					result.put(var, emptyCol);
				}
			} else
				fieldWorklist.add(var);
		}

		// Iterate over worklist until it is empty (standard algo)
		while (!fieldWorklist.isEmpty()) {
			VarWithFirstUse field = fieldWorklist.remove(0);
			Variable fieldTarget;
			if (field.getFirstUse() instanceof Field) {
				Field fieldVar = (Field) field.getFirstUse();
				fieldTarget = (Variable) fieldVar.target();
			} else if (field.getFirstUse() instanceof ArrayAccess) {
				ArrayAccess arrayVar = (ArrayAccess) field.getFirstUse();
				fieldTarget = (Variable) arrayVar.array();
			} else if (field.getFirstUse() instanceof X10ArrayAccess) {
				X10ArrayAccess arrayVar = (X10ArrayAccess) field.getFirstUse();
				fieldTarget = (Variable) arrayVar.array();
			} else {
				X10ArrayAccess1 arrayVar = (X10ArrayAccess1) field
						.getFirstUse();
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
						fieldKey = new InstanceFieldKey(
								ikey,
								((InstanceFieldKey) field.getPtrKeyOfFirstUse())
										.getField());
					else
						fieldKey = new ArrayContentsKey(ikey); // I think this
					// should be
					// ArrayContentsKey
					// fieldKey = new ArrayInstanceKey(ikey); // I think
					// this should be ArrayContentsKey
					OrdinalSet<InstanceKey> instancePtSet = analysis
							.getPointsToSet(fieldKey);
					fieldPtSet.addAll(OrdinalSet.toCollection(instancePtSet));
				}
				result.put(field, fieldPtSet);
			}
		}
		return result;
	}

	/**
	 * Translate between Polyglot variable and internal representation of a
	 * variable.
	 * 
	 * @param vars
	 *            internal representations of variables
	 * @param var
	 *            a Polyglot variable
	 * @return the internal representation of the Polyglot variable
	 */
	private VarWithFirstUse getVarWithFirstUse(Set<VarWithFirstUse> vars,
			Variable var) {
		boolean arrayMode = ((var instanceof ArrayAccess)
				|| (var instanceof X10ArrayAccess) || (var instanceof X10ArrayAccess1)) ? true
				: false;
		for (VarWithFirstUse v : vars) {
			if (arrayMode && v.getVarInstance() == null) {
				VarWithFirstUse varTarget = getVarWithFirstUse(vars,
						extractArrayName(var));
				VarWithFirstUse vTarget = getVarWithFirstUse(vars,
						extractArrayName((Variable) v.getFirstUse()));
				if ((varTarget == null)
						|| (vTarget == null)
						|| (varTarget.getVarInstance().equals(vTarget
								.getVarInstance()))
						|| (varTarget.getFirstUse().equals(vTarget
								.getFirstUse())))
					return v;
			} else if (!arrayMode
					&& v.getVarInstance() == extractArrayName(var)
							.varInstance()) {
				if ((var instanceof Field)
						&& (v.getFirstUse() instanceof Field)) {
					VarWithFirstUse varTarget = getVarWithFirstUse(vars,
							(Variable) ((Field) var).target());
					VarWithFirstUse vTarget = getVarWithFirstUse(vars,
							(Variable) ((Field) v.getFirstUse()).target());
					if ((varTarget == null)
							|| (varTarget.getFirstUse().equals(
									vTarget.getFirstUse()) || varTarget
									.getVarInstance().equals(
											vTarget.getVarInstance())))
						return v;
				} else
					return v;
			}
		}
		return null;
	}

	/**
	 * Convert points to map into an alias map: var -> list var.
	 * 
	 * @param pointsToInfo
	 *            the points to map
	 * @return an alias map
	 */
	private Map<VarWithFirstUse, Set<VarWithFirstUse>> computeAliasInfo(
			Map<VarWithFirstUse, Collection<InstanceKey>> pointsToInfo) {
		Map<VarWithFirstUse, Set<VarWithFirstUse>> resultMap = new HashMap<VarWithFirstUse, Set<VarWithFirstUse>>();
		for (Map.Entry<VarWithFirstUse, Collection<InstanceKey>> e : pointsToInfo
				.entrySet()) {
			Set<VarWithFirstUse> eAlias = new HashSet<VarWithFirstUse>();
			for (Map.Entry<VarWithFirstUse, Collection<InstanceKey>> e2 : pointsToInfo
					.entrySet())
				if (!(Collections.disjoint(e.getValue(), e2.getValue())))
					eAlias.add(e2.getKey());
			resultMap.put(e.getKey(), eAlias);
		}
		return resultMap;
	}

	/**
	 * DummyVar is a token for use in calculating the information flow of a
	 * loop. It represents a variable actually used in the loop and, if added to
	 * the info flow set of the variable upon its first update, exposes a
	 * loop-carried dependency.
	 */
	private class DummyVar implements VarKey {

		VarWithFirstUse var;

		DummyVar(VarWithFirstUse v) {
			var = v;
		}

		public VarWithFirstUse getVar() {
			return var;
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof DummyVar) {
				DummyVar other = (DummyVar) o;
				return var.equals(other.var);
			}
			return false;
		}

		@Override
		public int hashCode() {
			return var.hashCode();
		}

		@Override
		public String toString() {
			return "Dummy[" + var + "]";
		}
	}

	/**
	 * A representation of the VarKeys associated with an assignment or
	 * declaration statement for use in determining the information flow of a
	 * loop.
	 * 
	 * @author sm053
	 * 
	 */
	private class StmtKeys {
		private Stmt stmt;
		private VarWithFirstUse lvalkey;
		private Collection<VarKey> rvalkeys;

		public StmtKeys(Stmt stmt, VarWithFirstUse lvalkey,
				Collection<VarKey> rvalkeys) {
			this.stmt = stmt;
			this.lvalkey = lvalkey;
			this.rvalkeys = rvalkeys;
		}

		public Stmt getStmt() {
			return stmt;
		}

		public VarWithFirstUse getLvalKey() {
			return lvalkey;
		}

		public Collection<VarKey> getRvalKeys() {
			return rvalkeys;
		}
	}

	/**
	 * Analyzes the given loop and sets fPhi and fRho accordingly.
	 * 
	 * @param loop
	 */
	private void calculateInfoFlow(Stmt loop,
			final Map<VarWithFirstUse, Set<VarWithFirstUse>> aliasInfo) {
		// Since fPhi and fRho are instance fields, changing them in this
		// method is visible outside of the method.
		fPhi = new LinkedHashMap<Stmt, Set<Expr>>();
		fRho = new HashMap<VarWithFirstUse, Boolean>();

		// Used to track info flow changes
		Map<Stmt, Set<VarKey>> internal_phi = new HashMap<Stmt, Set<VarKey>>();
		Map<VarKey, Set<VarKey>> psi = new HashMap<VarKey, Set<VarKey>>();

		// for V : lvalue \in l
		for (VarWithFirstUse v : aliasInfo.keySet()) {
			// \psi(V) = \sigma(V) + \gamma_V
			psi.put(v, new HashSet<VarKey>());
			psi.get(v).addAll(aliasInfo.get(v));
			psi.get(v).add(new DummyVar(v));

			// \rho(V) = false
			fRho.put(v, Boolean.FALSE);
		}

		// extract the updates from the loop body (assuming single level)
		Collection<Stmt> assignStmts = new ArrayList<Stmt>();

		if (loop instanceof X10Loop) {
			X10Loop xloop = (X10Loop) loop;
			ExtractAssignStmtsVisitor assignmentExtractor = new ExtractAssignStmtsVisitor();
			xloop.visit(assignmentExtractor);
			assignStmts = assignmentExtractor.getAssignStmts();
			// if (xloop.body() instanceof Block) {
			// Block xloop_body = (Block) xloop.body();
			// for (Object stmt : xloop_body.statements()) {
			// if (stmt instanceof Eval) {
			// Eval eval_stmt = (Eval) stmt;
			// if (eval_stmt.expr() instanceof Assign) {
			// assignStmts.add(eval_stmt);
			// internal_phi.put(eval_stmt, new HashSet<VarKey>());
			// }
			// } else if (stmt instanceof LocalDecl) {
			// assignStmts.add((LocalDecl) stmt);
			// internal_phi.put((LocalDecl) stmt,
			// new HashSet<VarKey>());
			// }
			// }
			// }
		} else {
			ThrowableStatus
					.createFatalErrorStatus("Refactoring analysis failed on non X10 loop.");
		}

		X10NodeFactory nf = (X10NodeFactory) ((ParseController) ed
				.getParseController()).getCompilerDelegate().getExtensionInfo()
				.nodeFactory();
		boolean changed = true;

		// translate a statement from variables into varkeys
		List<StmtKeys> stmtKeys = new LinkedList<StmtKeys>();
		for (Stmt stmt : assignStmts) {
			internal_phi.put(stmt, new HashSet<VarKey>());

			Expr lval = null;
			Expr rexpr = null;

			if (stmt instanceof Eval) {
				Assign assignment = (Assign) ((Eval) stmt).expr();

				// extract the key associated with X
				lval = assignment.left();
				rexpr = assignment.right();
			} else {
				LocalDecl ld = (LocalDecl) stmt;
				lval = nf.Local(ld.id().position(), ld.id());
				lval = ((Local) lval).localInstance(ld.localInstance());
				rexpr = ld.init();
			}

			VarWithFirstUse lvalkey = null;
			if (lval instanceof Variable)
				lvalkey = getVarWithFirstUse(aliasInfo.keySet(),
						(Variable) lval);
			else
				// there's something funny going on! quit!
				ThrowableStatus
						.createFatalErrorStatus("A non-lvalue was found on the left hand side of an assignment. Refactoring aborted!");

			// extract the keys associated with the E_i
			ExtractVarsVisitor rval_visitor = new ExtractVarsVisitor(nf);
			rexpr.visit(rval_visitor);
			Collection<Variable> rvals = rval_visitor.getVars();
			Collection<VarKey> rvalkeys = ExtractAsyncStaticTools.map(rvals,
					new Function<Variable, VarKey>() {
						public VarKey eval(Variable v) {
							return getVarWithFirstUse(aliasInfo.keySet(), v);
						}
					});
			stmtKeys.add(new StmtKeys(stmt, lvalkey, rvalkeys));
		}

		// calculate first update map
		Map<VarKey, Stmt> firstUpdates = new HashMap<VarKey, Stmt>();
		for (StmtKeys stmtKey : stmtKeys) {
			if (!firstUpdates.containsKey(stmtKey.getLvalKey())) {
				firstUpdates.put(stmtKey.getLvalKey(), stmtKey.getStmt());
			}
		}

		// while \phi changes
		while (changed) {
			changed = false;

			// for S : \{ X = E_1 op \ldots op E_k \} \in l
			for (StmtKeys stmtKey : stmtKeys) {

				// \phi(S) = \Cup_{i \in [1,k]} \psi(E_i)
				Set<VarKey> new_phi_stmt = new HashSet<VarKey>();
				for (VarKey rvalkey : stmtKey.getRvalKeys()) {
					new_phi_stmt.addAll(psi.get(rvalkey));
				}

				// determine if there were any changes in calculating \phi(S)
				// since
				// previous iteration
				new_phi_stmt.removeAll(internal_phi.get(stmtKey.getStmt()));
				changed |= !new_phi_stmt.isEmpty();

				// if (\gamma_V \in \phi(S)) \wedge firstUpdate (X, l))
				// \rho(V) = true
				if (new_phi_stmt.contains(new DummyVar(stmtKey.getLvalKey()))) {
					if (stmtKey.getStmt().equals(
							firstUpdates.get(stmtKey.getLvalKey())))
						fRho.put(stmtKey.getLvalKey(), Boolean.TRUE);
				}

				// commit changes to \phi(S)
				internal_phi.get(stmtKey.getStmt()).addAll(new_phi_stmt);

				// \psi(X) = \psi(X) \Cup \phi(S)
				psi.get(stmtKey.getLvalKey()).addAll(
						internal_phi.get(stmtKey.getStmt()));
			}
		}

		// remove abstract locations from \phi and convert \gamma_V into V
		for (Map.Entry<Stmt, Set<VarKey>> entry : internal_phi.entrySet()) {
			Set<Expr> infoExprs = (Set<Expr>) ExtractAsyncStaticTools.map(entry
					.getValue(), new Function<VarKey, Expr>() {
				public Expr eval(VarKey v) {
					if (v instanceof DummyVar) {
						return ((DummyVar) v).getVar().getFirstUse();
					} else {
						return null;
					}
				}
			});
			infoExprs.remove(null);
			fPhi.put(entry.getKey(), infoExprs);
		}
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

		if (loop instanceof X10Loop) {
			X10Loop xloop = (X10Loop) loop;
			X10NodeFactory nf = (X10NodeFactory) ((ParseController) ed
					.getParseController()).getCompilerDelegate()
					.getExtensionInfo().nodeFactory();
			if (xloop.body() instanceof Block) {
				Block xloop_body = (Block) xloop.body();
				mainloop: for (Object stmt : xloop_body.statements()) {
					Stmt castStmt = (Stmt) stmt;
					ExtractVarsVisitor varVisitor = new ExtractVarsVisitor(nf);
					castStmt.visit(varVisitor);
					Collection<Variable> vars = varVisitor.getVars();
					for (Variable var : vars) {
						VarWithFirstUse varTrans = getVarWithFirstUse(fRho
								.keySet(), var);
						if (fRho.get(varTrans)) {
							fDelta.add(castStmt);
							continue mainloop;
						}
					}

					if (castStmt instanceof Eval) {
						Eval evalStmt = (Eval) castStmt;
						if (evalStmt.expr() instanceof Assign) {
							Assign assignment = (Assign) evalStmt.expr();
							if (assignment.left() instanceof Local) {
								Local updateVar = (Local) assignment.left();
								for (VarDecl var : inductionVars) {
									if (var.localInstance().equals(
											updateVar.localInstance())) {
										fDelta.add(castStmt);
										continue mainloop;
									}
								}
							}
						}
					}
				}
			}
		}

	}

	/**
	 * Randomly generates a set of loop carried statements for the given loop.
	 * 
	 * @param loop
	 * @param inductionVars
	 */
	private void randomlyGenerateLoopCarriedStatements(Stmt loop,
			Set<VarDecl> inductionVars) {
		fDelta = new HashSet<Stmt>();
		loop.visitChildren(new NodeVisitor() {
			public NodeVisitor enter(Node par, Node n) {
				if ((n instanceof Stmt) && !(n instanceof CompoundStmt)) {
					if (Math.random() < 0.5)
						fDelta.add((Stmt) n);
				}
				return super.enter(par, n);
			}
		});
	}

	/**
	 * Determines if the given node is in an atomic statement within the given
	 * method.
	 * 
	 * @param node
	 *            the tested node
	 * @param methodParent
	 *            the method node
	 * @return whether node is within an atomic section of the method
	 */
	private boolean inAtomic(Node node, Node methodParent) {
		NodePathComputer pathSaver = new NodePathComputer(methodParent, node);
		List<Node> path = pathSaver.getPath();

		return (PolyglotUtils.findNodeOfType(path, Atomic.class) != null);
	}

	/**
	 * BadClockVisitor can be used to detect inappropriate usage of a clock that
	 * would prevent application of the "extract concurrent" refactoring.
	 * 
	 * @author smarkstr, rmfuhrer
	 */
	private class BadClockVisitor extends NodeVisitor {
		private boolean fResult = false;

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

		/**
		 * Determines whether visit over AST uncovered bad clock usage.
		 * 
		 * @return true, if the AST had bad clock usage; false, otherwise.
		 */
		public boolean getResult() {
			return fResult;
		}
	}

	/**
	 * Examines loop body for any clock manipulations other than resume().
	 * 
	 * @param stmt
	 *            The statement to examine for bad clock usage
	 * @return true, if the statement uses clocks; false, otherwise.
	 */
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

		/**
		 * Constructs a visitor which will watch for effecting expressions on
		 * the provided l-values.
		 * 
		 * @param lvalsToWatch
		 *            the l-values which the effects visitor will monitor.
		 */
		public EffectsVisitor(Set<Expr> lvalsToWatch) {
			if (lvalsToWatch != null)
				fLValsToWatch = lvalsToWatch;
			else
				fLValsToWatch = Collections.emptySet();
		}

		@Override
		public NodeVisitor enter(Node n) {
			if (n instanceof Assign) {
				Assign a = (Assign) n;

				fEffects.add(a.left());
			}
			return this;
		}

		/**
		 * Returns a set of expressions which have effects on the monitored
		 * l-values.
		 * 
		 * @return set of effecting expressions.
		 */
		public Set<Expr> getEffects() {
			fEffects.retainAll(fLValsToWatch);
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

	/**
	 * Determines if the preconditions for applying the refactoring are met.
	 * 
	 * @param loop
	 *            the loop to be transformed
	 * @param pivot
	 *            the statement which acts as the focus of the refactoring
	 * @param method
	 *            the method in which the code resides
	 * @return true, if the preconditions are met; false, otherwise.
	 */
	private boolean preconditionsHold(Stmt loop, Stmt pivot,
			ProcedureDecl method) {
		if (hasEffectsOn(pivot, null))
			return false;
		return true;
	}

	/**
	 * Builds a map from Polyglot variables to WALA pointer analysis results.
	 * 
	 * @param rootEntity
	 *            the WALA CAst entity root for the program
	 * @return a map from Polyglot variables to WALA pointer keys
	 * @throws ThrowableStatus
	 *             when source to CAst entity map cannot be made
	 */
	private Map<Variable, PointerKey> buildVarToPtrKeyMap(CAstEntity rootEntity)
			throws ThrowableStatus {
		Map<Variable, PointerKey> resultMap = new HashMap<Variable, PointerKey>();

		ExtractVarsVisitor ev = new ExtractVarsVisitor(((ParseController) ed
				.getParseController()).getCompilerDelegate().getExtensionInfo()
				.nodeFactory());
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
		buildCast2PolyglotVariableMap(ev, evMap, cast2VarMap, procEntities,
				castNodeVarMap, castNodeType);

		// Not used in practice, but useful for debugging
		Set<AstMethod> astMeths = new HashSet<AstMethod>();

		// A multi-map from positions to ssa instructions and cgnodes
		RefactoringMap<RefactoringPosition, InstructionContainer> sourceToInstructionMap = new RefactoringMap<RefactoringPosition, InstructionContainer>();

		// Build up the AstMethod set (for debugging purposes) and
		// source-to-SSAInstruction map
		buildSourceToInstructionMap(fMethodRef, astMeths,
				sourceToInstructionMap);

		// Build CAstNode to PointerKey map.
		Map<CAstNode, PointerKey> cast2KeyMap = buildCAst2PointerKeyMap(
				cast2VarMap, castNodeVarMap, castNodeType,
				sourceToInstructionMap);

		// Now map CAstNode variable references to the corresponding
		// PointerKey's.
		for (CAstNode c : cast2VarMap.keySet()) {
			resultMap.put(cast2VarMap.get(c), cast2KeyMap.get(c));
		}

		// Resulting map now allows you to determine what pointer keys are
		// necessary
		// for analysis of methods/loops
		return resultMap;
	}

	/**
	 * Builds the map from CAstNodes to Polyglot Variables. Also builds the maps
	 * from source positions to CAstNodes (castNodeVarMap) and from CAstNodes to
	 * VarUseType values (castNodeType).
	 * 
	 * @param ev
	 *            ExtractVarsVisitor - used for constructing castNodeType
	 * @param evMap
	 *            Map from source position to Polyglot Variables
	 * @param cast2VarMap
	 *            Maps CAstNodes to Polyglot Variables (Constructed by this
	 *            method)
	 * @param procEntities
	 *            The CAstEntities for the source file containing the
	 *            refactoring pivot expression
	 * @param castNodeVarMap
	 *            Maps source positions to CAstNodes (Constructed by this
	 *            method)
	 * @param castNodeType
	 *            Maps CAstNodes to VarUseType values (Constructed by this
	 *            method)
	 */
	private void buildCast2PolyglotVariableMap(ExtractVarsVisitor ev,
			Map<RefactoringPosition, Variable> evMap,
			HashMap<CAstNode, Variable> cast2VarMap,
			Collection<CAstEntity> procEntities,
			RefactoringMap<RefactoringPosition, CAstNode> castNodeVarMap,
			Map<CAstNode, NodeType> castNodeType) {
		for (CAstEntity procEntity : procEntities) {
			for (Iterator<CAstNode> i = procEntity.getSourceMap()
					.getMappedNodes(); i.hasNext();) {
				CAstNode c = i.next();
				CAstSourcePositionMap.Position p = procEntity.getSourceMap()
						.getPosition(c);

				// If Position values are negative, then this is a generated
				// node
				if (p.getFirstLine() > 0 && p.getLastLine() > 0
						&& p.getFirstCol() > 0 && p.getLastCol() > 0) {
					RefactoringPosition rp = new RefactoringPosition(p);
					Variable testVar = evMap.get(rp);
					if (testVar != null && !cast2VarMap.containsValue(testVar)) {
						int type = ev.getVarType(testVar);
						if ((type & VarUseType.DECL) != 0) {
							if ((c.getKind() == CAstNode.DECL_STMT)
									&& (testVar instanceof NamedVariable)
									&& (c.getChild(0).getValue().toString()
											.equals(((NamedVariable) testVar)
													.name()))) {
								// add mapping from current CAstNode to
								// associated Polyglot Variable
								cast2VarMap.put(c, testVar);

								// For variable declarations, CAstNode should be
								// mapped to by an
								// initialization expression position (if it
								// exists)

								for (int j = 1; j < c.getChildCount(); j++) {
									CAstSourcePositionMap.Position cp = procEntity
											.getSourceMap().getPosition(
													c.getChild(j));
									if (cp != null) {
										RefactoringPosition rcp = new RefactoringPosition(
												cp);
										castNodeVarMap.putVoid(rcp, c);
									}
								}

								// Add mapping from CAstNode source position to
								// CAstNode
								castNodeVarMap.putVoid(rp, c);

								// Add mapping from CAstNode to VarUseType value
								castNodeType.put(c, new NodeType(type,
										((type & VarUseType.PARAM) != 0) ? ev
												.getVUType().getParamNumber(
														testVar) : -1,
										((type & VarUseType.DREF) != 0) ? ev
												.getVUType().getDrefNumber(
														testVar) : -1));
							}
						} else {
							// add mapping from current CAstNode to associated
							// Polyglot Variable
							cast2VarMap.put(c, testVar);
							// Add mapping from CAstNode source position to
							// CAstNode
							castNodeVarMap.putVoid(rp, c);

							// Add mapping from CAstNode to VarUseType value
							castNodeType.put(c, new NodeType(type,
									((type & VarUseType.PARAM) != 0) ? ev
											.getVUType()
											.getParamNumber(testVar) : -1,
									((type & VarUseType.DREF) != 0) ? ev
											.getVUType().getDrefNumber(testVar)
											: -1));
						}

						// // add mapping from current CAstNode to associated
						// Polyglot Variable
						// cast2VarMap.put(c, testVar);
						//						
						// // For variable declarations, CAstNode should be
						// mapped to by an
						// // initialization expression position (if it exists)
						// if (c.getKind() == CAstNode.DECL_STMT)
						// for (int j = 1; j < c.getChildCount(); j++){
						// CAstSourcePositionMap.Position cp =
						// procEntity.getSourceMap().getPosition(c.getChild(j));
						// if (cp != null) {
						// RefactoringPosition rcp = new
						// RefactoringPosition(cp);
						// castNodeVarMap.putVoid(rcp, c);
						// }
						// }
						//						
						// // Add mapping from CAstNode source position to
						// CAstNode
						// castNodeVarMap.putVoid(rp, c);
						//						
						// // Add mapping from CAstNode to VarUseType value
						// int type = ev.getVarType(testVar);
						// castNodeType.put(c, new NodeType(type,
						// ((type &
						// VarUseType.PARAM)!=0)?ev.vutype.getParamNumber(testVar):-1,
						// ((type &
						// VarUseType.DREF)!=0)?ev.vutype.getDrefNumber(testVar):-1));
					}
				}
			}
		}
	}

	/**
	 * A class used for expanding type information to include what level of
	 * dereference is necessary to retrieve this type from a reference (as in,
	 * field dereferences), and which parameter to a method this type is
	 * associated with.
	 * 
	 * A -1 value indicates no dereference or param necessary.
	 * 
	 * @author sm053
	 * 
	 */
	private class NodeType {
		public int type = -1;
		public int param = -1;
		public int dref = -1;

		public NodeType(int type, int param, int dref) {
			this.type = type;
			this.param = param;
			this.dref = dref;
		}
	}

	/**
	 * Creates a mapping from WALA CAstNodes to WALA pointer keys.
	 * 
	 * @param cast2VarMap
	 *            a map from CAstNodes to Polyglot Variables
	 * @param castNodeVarMap
	 *            a map from source positions to CAstNodes
	 * @param castNodeType
	 *            a map from CAstNodes to internal type representations
	 * @param sourceToInstructionMap
	 *            a map from source positions to WALA SSAInstructions
	 * @return
	 */
	private Map<CAstNode, PointerKey> buildCAst2PointerKeyMap(
			HashMap<CAstNode, Variable> cast2VarMap,
			RefactoringMap<RefactoringPosition, CAstNode> castNodeVarMap,
			Map<CAstNode, NodeType> castNodeType,
			RefactoringMap<RefactoringPosition, InstructionContainer> sourceToInstructionMap) {
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
			for (CAstNode cNode : cNodes) {
				NodeType cNodeType = castNodeType.get(cNode);
				if (cNodeType != null) {
					Integer cType = cNodeType.type;
					if (cType != null && !cast2KeyMap.containsKey(cNode)) {
						if ((cType & VarUseType.ARRAY) != 0) {
							createDummyArrayInstanceKey(cast2VarMap,
									cast2KeyMap, cNode, srcNode);
						} else if ((cType & VarUseType.LVAL) != 0) {
							if ((cType & VarUseType.PUTFIELD) != 0)
								createDummyInstanceFieldKey(cast2VarMap,
										cast2KeyMap, cNode);
							else if (!(inst instanceof AsyncInvokeInstruction)) {
								if ((cType & VarUseType.LOOP_VAR) != 0) {
									if (inst instanceof SSARegionIterNextInstruction)
										cast2KeyMap.put(cNode,
												new LocalPointerKey(srcNode,
														inst.getDef()));
								} else
									cast2KeyMap
											.put(
													cNode,
													new LocalPointerKey(
															srcNode,
															(inst.getDef() == -1) ? inst
																	.getUse(0)
																	: inst
																			.getDef()));
							}
						} else if ((cType & (VarUseType.RVAL | VarUseType.LEFT)) != 0) {
							if ((cType & VarUseType.GETFIELD) != 0)
								// problematic now because of generated field
								// derefs...
								createDummyInstanceFieldKey(cast2VarMap,
										cast2KeyMap, cNode);
							else {
								if ((inst instanceof SSAArrayStoreInstruction)
										|| (inst instanceof X10ArrayStoreByPointInstruction)) {
									cast2KeyMap.put(cNode, new LocalPointerKey(
											srcNode, inst.getUse(2)));
								} else if (inst instanceof X10ArrayStoreByIndexInstruction) {
									cast2KeyMap.put(cNode, new LocalPointerKey(
											srcNode, inst.getUse(inst
													.getNumberOfUses() - 1)));
								} else if ((inst instanceof SSAArrayReferenceInstruction)
										|| (inst instanceof SSAFieldAccessInstruction)
										|| (inst instanceof X10ArrayReferenceInstruction))
									cast2KeyMap
											.put(
													cNode,
													new LocalPointerKey(
															srcNode,
															(inst.getDef() == -1) ? inst
																	.getUse(1)
																	: inst
																			.getUse(0)));
							}
						} else if ((cType & VarUseType.RIGHT) != 0)
							cast2KeyMap.put(cNode, new LocalPointerKey(srcNode,
									inst.getUse(1)));
						else if ((cType & VarUseType.PARAM) != 0) {
							if ((inst instanceof SSAAbstractInvokeInstruction)
									&& !(inst instanceof AsyncInvokeInstruction)) {
								int paramOffset;
								if (inst instanceof SSAAbstractInvokeInstruction) {
									SSAAbstractInvokeInstruction invokeInst = (SSAAbstractInvokeInstruction) inst;
									paramOffset = (invokeInst.isStatic()) ? -1
											: 0;
								} else
									paramOffset = -1;

								int param = castNodeType.get(cNode).param;
								cast2KeyMap.put(cNode, new LocalPointerKey(
										srcNode, inst.getUse(((param == 4) ? 3
												: param)
												+ paramOffset)));
							} else if (inst instanceof SSAGetInstruction) {
								if ((cType & VarUseType.GETFIELD) != 0) {
									createDummyInstanceFieldKey(cast2VarMap,
											cast2KeyMap, cNode);
								} else
									cast2KeyMap.put(cNode, new LocalPointerKey(
											srcNode, inst.getUse(0)));
							}
						} else if ((cType & VarUseType.INVOKE_TARGET) != 0) {
							if ((inst instanceof SSAAbstractInvokeInstruction)
									&& !(inst instanceof AsyncInvokeInstruction)) {
								if ((cType & VarUseType.DREF) == 0)
									cast2KeyMap.put(cNode, new LocalPointerKey(
											srcNode, inst.getUse(0)));
							} else if (inst instanceof SSAGetInstruction) {
								if ((cType & VarUseType.GETFIELD) != 0) {
									createDummyInstanceFieldKey(cast2VarMap,
											cast2KeyMap, cNode);
								} else
									cast2KeyMap.put(cNode, new LocalPointerKey(
											srcNode, inst.getUse(0)));
							}
						} else if ((cType & VarUseType.POINT) != 0) {
							if (inst instanceof X10ArrayReferenceInstruction)
								cast2KeyMap.put(cNode, new LocalPointerKey(
										srcNode, inst.getUse(1)));
						}
					}
				}
			}
		}
		return cast2KeyMap;
	}

	/**
	 * Creates a placeholder instance key for field references to obtain some
	 * localized field sensitivity. Places this key into the current WALA
	 * CAstNode to pointer key map.
	 * 
	 * @param cast2VarMap
	 *            map from WALA CAstNodes to Polyglot variables
	 * @param cast2KeyMap
	 *            map from WALA CAstNodes to pointer keys
	 * @param cNode
	 *            the CAstNode which will be used to create the dummy key
	 */
	private void createDummyInstanceFieldKey(
			HashMap<CAstNode, Variable> cast2VarMap,
			Map<CAstNode, PointerKey> cast2KeyMap, CAstNode cNode) {
		TypeName typeName;
		if (cast2VarMap.get(cNode.getChild(0)) != null)
			typeName = TypeName.findOrCreate("L"
					+ cast2VarMap.get(cNode.getChild(0)).type().toString()
							.replace('.', '/'));
		else
			typeName = ((FieldReference) cNode.getChild(1).getValue())
					.getDeclaringClass().getName();
		IClass cNodeClass = ((X10IRTranslatorExtension) fEngine
				.getTranslatorExtension()).getSourceLoader().lookupClass(
				typeName);
		IField cNodeField = cNodeClass.getField(((FieldReference) cNode
				.getChild(1).getValue()).getName());
		cast2KeyMap.put(cNode, new InstanceFieldKey(new ConcreteTypeKey(
				cNodeClass), cNodeField));
	}

	/**
	 * Creates a placeholder instance key for array references to obtain some
	 * localized array sensitivity. Places this key into the current WALA
	 * CAstNode to pointer key map.
	 * 
	 * @param cast2VarMap
	 *            map from WALA CAstNodes to Polyglot variables
	 * @param cast2KeyMap
	 *            map from WALA CAstNodes to pointer keys
	 * @param cNode
	 *            the CAstNode which will be used to create the dummy key
	 */
	private void createDummyArrayInstanceKey(
			HashMap<CAstNode, Variable> cast2VarMap,
			Map<CAstNode, PointerKey> cast2KeyMap, CAstNode cNode,
			CGNode srcNode) {
		// CAstNode child = cNode.getChild(0);
		Variable variable = cast2VarMap.get(cNode);
		// Variable variable = cast2VarMap.get(child);
		Type type = variable.type();
		TypeName typeName = TypeName.findOrCreate("[L"
				+ type.toString().replace('.', '/'));
		IClass cNodeClass = ((X10IRTranslatorExtension) fEngine
				.getTranslatorExtension()).getSourceLoader().lookupClass(
				typeName);
		// Shane's change
		cast2KeyMap.put(cNode, new ArrayContentsKey(new NormalAllocationInNode(
				srcNode, null, cNodeClass)));
		// cast2KeyMap.put(cNode, new ArrayContentsKey(new
		// NormalAllocationSiteKey(srcNode,null,cNodeClass)));
		// cast2KeyMap.put(cNode, new ArrayInstanceKey(new
		// NormalAllocationSiteKey(srcNode,null,cNodeClass)));
	}

	/**
	 * Recursively creates a source position to WALA SSAInstruction map.
	 * 
	 * @param methRef
	 *            a WALA method reference
	 * @param astMeths
	 *            a set of visited method references (debug purposes)
	 * @param map
	 *            a map from source positions to SSAInstructions
	 * @throws ThrowableStatus
	 *             when WALA data is incomplete for the method reference
	 */
	private void buildSourceToInstructionMap(MethodReference methRef,
			Set<AstMethod> astMeths,
			// TODO Multiple InstructionContainers might need to be stored for
			// a given Position: allow them to be chained?
			// Map<CAstSourcePositionMap.Position, InstructionContainer> map)
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

		// IMethod iMethod = fLoader.lookupClass(
		// methRef.getDeclaringClass().getName()).getMethod(
		// methRef.getSelector());
		IMethod iMethod = srcNode.getMethod();
		if (!(iMethod instanceof AstMethod))
			throw ThrowableStatus
					.createFatalErrorStatus("Nonexistent parse tree for method???");
		AstMethod astMethod = (AstMethod) iMethod;
		astMeths.add(astMethod);
		int index = 0;
		// IR amIR = fEngine.Options.getSSACache().findOrCreateIR(astMethod,
		// Everywhere.EVERYWHERE, SSAOptions.defaultOptions());

		for (SSAInstruction sa : amIR.getInstructions()) {
			index++;
			if (sa == null || nonproductiveSSAInstruction(sa))
				continue;

			// System.out.println(astMethod.getName()+" SSAInstruction "+sa+" @
			// "+astMethod.getSourcePosition(index));
			CAstSourcePositionMap.Position ipos = astMethod
					.getSourcePosition(index - 1);
			if (ipos != null) {// && !map.containsKey(ipos)) {
				RefactoringPosition ripos = new RefactoringPosition(ipos);
				map.putVoid(ripos, new InstructionContainer(sa, srcNode));
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

	/**
	 * Determines if a given SSAInstruction will affect the refactoring
	 * analysis.
	 * 
	 * @param ssai
	 *            an SSAInstruction
	 * @return true, if the instruction has no result on analysis; false,
	 *         otherwise.
	 */
	private static boolean nonproductiveSSAInstruction(SSAInstruction ssai) {
		return (ssai instanceof SSAGotoInstruction)
				|| (ssai instanceof SSASwitchInstruction)
				|| (ssai instanceof SSAFinishInstruction)
				|| (ssai instanceof SSAConditionalBranchInstruction)
				|| (ssai instanceof SSAAtomicInstruction)
				|| (ssai instanceof SSAAbstractThrowInstruction);
	}

	/**
	 * Using collected information, calls the appropriate WALA methods to create
	 * the WALA intermediate representation.
	 * 
	 * @param sources
	 *            the source files to analyze
	 * @param libs
	 *            the libraries to include in analysis
	 * @param javaProject
	 *            the project being edited
	 * @throws IOException
	 *             when source files or entry points are non-existent
	 * @throws ClassHierarchyException
	 * @throws ThrowableStatus
	 * @throws CancelException
	 */
	private void doCreateIR(Collection<String> sources, List<String> libs,
			IJavaProject javaProject) throws IOException,
			ClassHierarchyException, ThrowableStatus, CancelException {
		// EclipseProjectSourceAnalysisEngine engine = new
		// X10EclipseSourceAnalysisEngine(javaProject);
		// JavaSourceAnalysisEngine engine = new X10SourceAnalysisEngine();

		populateScope(sources, libs);

		ProcedureCollectorVisitor procedureCollection = new ProcedureCollectorVisitor(
				fEngine);

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
		fEngine.setExclusionsFile(ExtractAsyncStaticTools.cheapHack());
		PropagationCallGraphBuilder builder = (PropagationCallGraphBuilder) fEngine
				.defaultCallGraphBuilder();
		fCallGraph = builder.makeCallGraph(builder.getOptions());

		// If we've gotten this far, IR has been produced.

		dumpIR(fCallGraph);

		com.ibm.wala.cast.ipa.callgraph.Util.dumpCG(builder, fCallGraph);

		List<ProcedureInstance> pc = procedureCollection.procs;

		checkCallGraphShape();
	}

	/**
	 * Converts string paths into analysis modules.
	 * 
	 * @param sources
	 *            paths to the source files
	 * @param libs
	 *            paths to the library files
	 * @throws IOException
	 */
	private void populateScope(Collection<String> sources, List<String> libs)
			throws IOException {
		boolean foundLib = false;
		for (String lib : libs) {
			File libFile = new File(lib);

			if (libFile.exists()) {
				foundLib = true;
				fEngine.addX10SystemModule(new JarFileModule(new JarFile(
						libFile)));
			}
		}
		Assertions.productionAssertion(foundLib);

		for (String srcFilePath : sources) { // HACK Just add the parent
			// directory of each file in
			// 'sources'
			// String srcFileName =
			// srcFilePath.substring(srcFilePath.lastIndexOf(File.separator) +
			// 1);
			String srcDir = srcFilePath.substring(0, srcFilePath
					.lastIndexOf(File.separator) + 1);

			fEngine.addX10SourceModule(new SourceDirectoryTreeModule(new File(
					srcDir), "x10"));
			// fEngine.addX10SourceModule(new SourceFileModule(new
			// File(srcFilePath), srcFileName));
		}
	}

	// private void methodRefDebugOutput() throws IOException {
	// /*
	// * int index= 0; for(Iterator irIter=
	// * fMethodIR.iterateAllInstructions(); irIter.hasNext();) {
	// * SSAInstruction sa= (SSAInstruction) irIter.next();
	// * System.out.println((index++) + " " + fMethodRef.getSignature() + " "
	// * + sa.getClass());
	// *
	// * for(int i= 0; i < sa.getNumberOfUses(); i++) { System.out.print(">> "
	// * + sa.getUse(i)); String[] saUseVarNames=
	// * fMethodIR.getLocalNames(index, sa .getUse(i)); if (saUseVarNames ==
	// * null) { System.out.println(" local map is null"); break; } for(int j=
	// * 0; j < saUseVarNames.length; j++) System.out.print(" " +
	// * saUseVarNames[j]); System.out.println(); } } System.out.println();
	// */}

	/**
	 * Prints out all IR information for the selected method and stores the
	 * MethodReference for later analysis
	 * 
	 * @throws IOException
	 * @throws ThrowableStatus
	 */
	private void checkCallGraphShape() throws IOException, ThrowableStatus {

		// Our main concern is the method in which the selected text is located.
		// Below code prints out information specific to that method.

		System.err.println(fCallGraph.toString());

		ProcedureInstance srcMethod = fNodeMethod.procedureInstance();

		fMethodRef = fEngine.getTranslatorExtension().getIdentityMapper()
				.getMethodRef(srcMethod);
		Set<CGNode> srcNodes = fCallGraph.getNodes(fMethodRef);

		// Print out debug information about the current method
		// if (debugOutput)
		// methodRefDebugOutput();
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
		for (Iterator<SSAInstruction> irIter = methIR.iterateAllInstructions(); irIter
				.hasNext();) {
			SSAInstruction sa = irIter.next();
			for (int i = 0; i < sa.getNumberOfUses(); i++)
				pointerKeys.add(new LocalPointerKey(srcNode, sa.getUse(i)));
		}
		for (LocalPointerKey p : pointerKeys)
			System.out.println(">> " + p.getValueNumber() + " PointerKey " + p);
	}

	@Override
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

		List<String> x10RTJars = new ArrayList<String>();
		x10RTJars.addAll(rtJar);
		IJavaProject javaProject = JavaCore.create(fSourceFile.getProject());
		try {
			fEngine = new X10EclipseSourceAnalysisEngine(javaProject);
		} catch (IOException ioe) {
			return RefactoringStatus
					.createFatalErrorStatus("Unexpected exception generated when creating analysis engine.");
		}
		x10RTJars.add(ExtractAsyncStaticTools.getLanguageRuntimePath(
				javaProject).toString());

		if (!(fNode instanceof Stmt))
			return RefactoringStatus
					.createFatalErrorStatus("Extract Async is only valid for expressions");

		fPivot = (Stmt) fNode;

		NodeTypeFindingVisitor typeFinder = new NodeTypeFindingVisitor(
				ClassDecl.class);
		fNodeMethod.visit(typeFinder);

		if (typeFinder.getResult() != null) {
			return RefactoringStatus
					.createFatalErrorStatus("Extract Async does not currently handle types nested inside method to be transformed.");
		}

		try {
			return this.analyzeSource(ExtractAsyncStaticTools
					.singleTestSrc(fSourceFile), x10RTJars, javaProject);
		} catch (CancelException e) {
			return RefactoringStatus
					.createFatalErrorStatus("Call-graph construction canceled by WALA");
		}
	}

	@Override
	public RefactoringStatus checkFinalConditions(IProgressMonitor pm)
			throws CoreException, OperationCanceledException {
		// Atomic
		return new RefactoringStatus();
	}

	/**
	 * Sets the place where concurrent execution should occur. Defaults to
	 * "here".
	 * 
	 * @param place
	 *            an X10 place expression
	 */
	public void setPlace(String place) {
		if (place != "")
			this.place = place;
		else
			this.place = "here";
	}

	@SuppressWarnings("unchecked")
	@Override
	public Change createChange(IProgressMonitor pm) throws CoreException,
			OperationCanceledException {

		IParseController parseController = ed.getParseController();
		ISourcePositionLocator locator = parseController
				.getSourcePositionLocator();

		NodePathComputer pathComputer = new NodePathComputer(fNodeMethod,
				fPivot);
		Stmt lStmt = (Stmt) PolyglotUtils.findInnermostNodeOfTypes(pathComputer
				.getPath(), new Class[] { Loop.class, X10Loop.class });

		int startOffset = locator.getStartOffset(lStmt);
		int endOffset = locator.getEndOffset(lStmt);
		// int startOffset = locator.getStartOffset(fPivot);
		// int endOffset = locator.getEndOffset(fPivot);

		StringWriter loopWriter = new StringWriter();
		List<Stmt> bodyStmts = Arrays
				.asList(fPhi.keySet().toArray(new Stmt[0]));
		if (lStmt instanceof X10Loop) {
			X10Loop loop = (X10Loop) lStmt;
			if (loop.body() instanceof Block) {
				Block loopBody = (Block) loop.body();
				int pivotIndex = bodyStmts.indexOf(fPivot);
				if (pivotIndex < 0) {
					ThrowableStatus
							.createFatalErrorStatus("Pivot statement not found in body of the loop.");
				}
				LoopCarriedStmtVisitor lcs = new LoopCarriedStmtVisitor(
						bodyStmts.subList(0, pivotIndex), fRho);
				List<Stmt> block1Stmts = bodyStmts.subList(0, pivotIndex);
				List<Stmt> block2Stmts = Collections.emptyList();

				if (pivotIndex < (bodyStmts.size() - 1)) {
					block2Stmts = bodyStmts.subList(pivotIndex + 1, bodyStmts
							.size());
				}

				Block b1 = new Block_c(null, block1Stmts);
				Block b2 = new Block_c(null, block2Stmts);

				X10NodeFactory nf = (X10NodeFactory) ((ParseController) ed
						.getParseController()).getCompilerDelegate()
						.getExtensionInfo().nodeFactory();
				X10Loop loop1 = distributeX10Loop(loop, b1, b2, nf.Async(fPivot
						.position(), nf.ExprFromQualifiedName(
						fPivot.position(), this.place), null, fPivot), false);
				Finish finishedLoop1 = nf.Finish(loop.position(), loop1);
				X10Loop loop2 = distributeX10Loop(loop, b2, b1, null, true);

				// System.out.println(loop1);
				// System.out.println(loop2);

				finishedLoop1.prettyPrint(loopWriter);
				if (nonEmptyBody(loop2.body())) {
					loopWriter.append('\n');
					loop2.prettyPrint(loopWriter);
				}

			}
		}

		TextFileChange tfc = new TextFileChange("Extract Async", fSourceFile);
		tfc.setEdit(new MultiTextEdit());
		tfc.addEdit(new ReplaceEdit(startOffset, endOffset - startOffset + 1,
		// "async ("
				// + place
				// + ") {"
				// + ((fPivot instanceof Eval) ? fPivot.firstChild()
				// : fPivot) + ";}"
				loopWriter.toString()));

		return tfc;
	}

	/**
	 * Determines if a the body of a loop is non-empty.
	 * 
	 * @param body
	 *            the body to check
	 * @return true, if the body is non-empty; false, otherwise
	 */
	private static boolean nonEmptyBody(Stmt body) {
		if (body instanceof Block) {
			Block bBody = (Block) body;
			return bBody.statements().size() > 0;
		} else {
			return true;
		}
	}

	/**
	 * Distributes a loop by keeping all elements of the first block and the
	 * necessary loop carried dependent statements from the second block. It
	 * will add a pivot change inbetween these two sets of statements, if
	 * provided.
	 * 
	 * @param loop
	 *            the loop whose header should be replicated
	 * @param b1
	 *            the primary block of expressions
	 * @param b2
	 *            the secondary block of expressions (for lcds)
	 * @param pivotChange
	 *            the pivotal statement between b1 and b2
	 * @param reverseBlocks
	 *            determines in which order the statements should be included in
	 *            the generated loop
	 * @return a loop distributed around b1
	 */
	@SuppressWarnings("unchecked")
	private X10Loop distributeX10Loop(X10Loop loop, Block b1, Block b2,
			Stmt pivotChange, boolean reverseBlocks) {
		X10NodeFactory nf = (X10NodeFactory) ((ParseController) ed
				.getParseController()).getCompilerDelegate().getExtensionInfo()
				.nodeFactory();

		// Determine relevant variables from b1 and the loop header
		Node formal = loop.formal();
		ExtractVarsVisitor blockVisitor = new ExtractVarsVisitor(nf);
		b1.visit(blockVisitor);
		Collection<Variable> b1Vars = blockVisitor.getVars();
		if (formal instanceof Formal) {
			Local lFormal = (nf
					.Local(formal.position(), ((Formal) formal).id()));
			lFormal = lFormal.localInstance(((Formal) formal).localInstance());
			b1Vars.add((Variable) lFormal);
		} else if (formal instanceof Variable) {
			b1Vars.add((Variable) formal);
		}

		// Determine relevant statements from b2
		List<Stmt> delta = slice(b1Vars);
		System.out.println(delta);
		delta.retainAll(b2.statements());

		// Create block statements
		List<Stmt> loopStmts;
		if (reverseBlocks) {
			loopStmts = delta;
			if (pivotChange != null)
				loopStmts.add(pivotChange);
			loopStmts.addAll(b1.statements());
		} else {
			loopStmts = new ArrayList<Stmt>(b1.statements().size()
					+ delta.size() + 1);
			for (int i = 0; i < b1.statements().size(); i++) {
				loopStmts.add(null);
			}
			Collections.copy(loopStmts, b1.statements());
			if (pivotChange != null)
				loopStmts.add(pivotChange);
			loopStmts.addAll(delta);
		}
		Block loopBody = nf.Block(loop.body().position(), loopStmts);

		// Create new loop
		X10Loop loop1;
		if (loop instanceof ForLoop) {
			loop1 = nf.ForLoop(loop.position(), ((ForLoop) loop).formal(),
					((ForLoop) loop).domain(), loopBody);
		} else if (loop instanceof AtEach) {
			loop1 = nf.AtEach(loop.position(), loop.formal(), loop.domain(),
					((AtEach) loop).clocks(), loopBody);
		} else {
			loop1 = nf.ForEach(loop.position(), loop.formal(), loop.domain(),
					((ForEach) loop).clocks(), loopBody);
		}

		return loop1;
	}

	/**
	 * Determines the slice of the loop body surrounding the pivot expression
	 * that has loop carried dependencies and affects the value of the specified
	 * variables.
	 * 
	 * @param vars
	 *            the potentially affected variables
	 * @return the loop-carried dependent slice associated with the variables
	 */
	private List<Stmt> slice(Collection<Variable> vars) {
		List<Stmt> delta = new ArrayList<Stmt>(fDelta.size());
		for (Stmt s : fDelta) {
			if (PolyglotUtils.updatesVarFromCollection(s, vars)) {
				delta.add(s);
			}
		}

		int deltaSize = 0;
		while (deltaSize != delta.size()) {
			deltaSize = delta.size();

			for (Stmt s : fDelta) {
				for (Stmt s_prime : delta) {
					if (s != s_prime) {
						Set<Expr> sSet = fPhi.get(s);
						Set<Expr> s_primeSet = fPhi.get(s_prime);
						if (sSet != null
								&& s_primeSet != null
								&& !Collections.disjoint(fPhi.get(s), fPhi
										.get(s_prime)) && !delta.contains(s)) {
							delta.add(s);
							break;
						}
					}
				}
			}
		}
		return delta;
	}

	/**
	 * Entry point container for the WALA analysis.
	 * 
	 * @author Shane Markstrum
	 * @author Robert Fuhrer
	 * 
	 */
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
					IRTranslatorExtension irx = fEngine
							.getTranslatorExtension();
					// N.B.: Don't use getMethodRef() here; in the process of
					// canonicalizing
					// the MethodReferences it has to compare ProcedureInstances
					// from two
					// different instances of Polyglot (one from the editor's
					// ParseController,
					// and one from the WALA analysis engine), resulting in a
					// "we are TypeSystem_c,
					// but type Foo is from TypeSystem_c" exception.
					return new DefaultEntrypoint(irx.getIdentityMapper()
							.referenceForMethod(proc), cha);
				}
			};
		}
	}

	/**
	 * A record for associating SSAInstructions with CGNodes.
	 * 
	 * @author Shane Markstrum
	 * @author Robert Fuhrer
	 */
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

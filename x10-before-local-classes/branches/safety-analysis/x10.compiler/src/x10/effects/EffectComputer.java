package x10.effects;

import polyglot.ast.Binary;
import polyglot.ast.Block;
import polyglot.ast.Call;
import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.FieldAssign;
import polyglot.ast.Formal;
import polyglot.ast.If;
import polyglot.ast.Local;
import polyglot.ast.LocalAssign;
import polyglot.ast.LocalDecl;
import polyglot.ast.New;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Receiver;
import polyglot.ast.Stmt;
import polyglot.ast.Unary;
import polyglot.ast.VarDecl;
import polyglot.frontend.Job;
import polyglot.types.ClassType;
import polyglot.types.ConstructorInstance;
import polyglot.types.MethodInstance;
import polyglot.types.StructType;
import polyglot.types.TypeSystem;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;

import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import polyglot.ast.*;
import polyglot.ast.Unary.Operator;
import polyglot.frontend.*;
import polyglot.main.Report;
import polyglot.types.*;
import polyglot.util.ErrorInfo;
import polyglot.util.ErrorQueue;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import x10.ast.Async;
import x10.ast.Atomic;
import x10.ast.ForEach;
import x10.ast.ForLoop;
import x10.ast.SettableAssign;
import x10.constraint.XFailure;
import x10.constraint.XTerm;
import x10.constraint.XTerms;
import x10.effects.constraints.Effect;
import x10.effects.constraints.Effects;
import x10.effects.constraints.Locs;
import x10.extension.X10Del;
import x10.types.X10Context;
import x10.types.X10FieldInstance;
import x10.types.X10LocalInstance;
import x10.types.X10TypeEnv;
import x10.types.X10TypeSystem;

/**
 * Visitor that computes effects for statements and blocks.
 * @author vj
 *
 */
public class EffectComputer extends ContextVisitor {

	/**
	 * @param job
	 * @param ts
	 * @param nf
	 */
	public EffectComputer(Job job, TypeSystem ts, NodeFactory nf) {
		super(job, ts, nf);
		fEQ= (job != null) ? job.extensionInfo().compiler().errorQueue() : null;
	}

	X10TypeSystem xts() { return (X10TypeSystem) ts;}
	
	@Override
	public X10Context context() { return (X10Context) context;}
	@Override
	public X10TypeSystem typeSystem() { return (X10TypeSystem) super.typeSystem();}
	
	boolean fVerbose;
	public void setVerbose(PrintStream diagStream) {
        fVerbose= true;
        fDiagStream= diagStream;
    }
	public X10TypeEnv env() { return typeSystem().env(context());}
	public Node override(Node parent, Node n) {

		try {
			if (Report.should_report(Report.visit, 2))
				Report.report(2, ">> " + this + "::override " + n);

			Node m = ((X10Del) n.del()).computeEffectsOverride(parent, this);

			return m;
		}
		catch (SemanticException e) {
			if (e.getMessage() != null) {
				Position position = e.position();

				if (position == null) {
					position = n.position();
				}

				this.errorQueue().enqueue(ErrorInfo.SEMANTIC_ERROR,
						e.getMessage(), position);
			}
			else {
				// silent error; these should be thrown only
				// when the error has already been reported 
			}

			// IMPORTANT: Mark the goal as failed, otherwise we may run dependent goals
			// that depend on this pass completing successfully.
			if (goal() != null)
				goal().fail();

			return n;
		}
	}

	protected NodeVisitor enterCall(Node n) throws SemanticException {
		EffectComputer v = ((X10Del) (EffectComputer) n.del()).computeEffectsEnter(this);
		return v;
	}

	protected Node leaveCall(Node old, final Node n, NodeVisitor v) throws SemanticException {
		final EffectComputer tc = (EffectComputer) v;
		Node m = n;
		m = ((X10Del) m.del()).computeEffects(tc);
		return m;
	}   
	
	 private PrintStream fDiagStream= System.out;
	 private final ErrorQueue fEQ;
	 public void emitMessage(String msg, Position pos) {
			if (fEQ != null) {
				fEQ.enqueue(ErrorInfo.SEMANTIC_ERROR, msg, pos);
			} else {
				System.err.println(msg);
			}
		}
	 public void diag(String s) {
		 if (fVerbose)
			 if (fDiagStream != null)
				 fDiagStream.println(s);
	 }
	/* public void dump() {
	      fDiagStream.println("*** Effects: ");
	      for (Node n : fEffects.keySet()) {
	          Effect e= fEffects.get(n);
	          fDiagStream.println(n.toString() + ":");
	          fDiagStream.println("   " + e);
	          fDiagStream.println();
	      }
	      */
}

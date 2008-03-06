package polyglot.ext.x10.plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import polyglot.ast.ClassDecl;
import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.StringLit;
import polyglot.ext.x10.ExtensionInfo;
import polyglot.ext.x10.ExtensionInfo.X10Scheduler;
import polyglot.ext.x10.ast.DepParameterExpr;
import polyglot.ext.x10.types.X10ClassDef;
import polyglot.ext.x10.types.X10ClassType;
import polyglot.ext.x10.types.X10ParsedClassType;
import polyglot.frontend.Globals;
import polyglot.frontend.Goal;
import polyglot.frontend.Job;
import polyglot.frontend.Pass;
import polyglot.frontend.Scheduler;
import polyglot.frontend.SourceGoal_c;
import polyglot.frontend.VisitorGoal;
import polyglot.frontend.VisitorPass;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.visit.NodeVisitor;
import polyglot.visit.ReachChecker;

public class LoadJobPlugins extends VisitorGoal {
	public static class LoadJobPluginsVisitor extends NodeVisitor {
		Job job;
		
		LoadJobPluginsVisitor(Job job) {
			this.job = job;
		}
		
		public Node leave(Node old, Node n, NodeVisitor v) {
//			if (n instanceof TypeNode) {
//				TypeNode tn = (TypeNode) n;
//				Type t = tn.type();
//				if (t instanceof X10ClassType) {
//					X10Scheduler x10Sched = (X10Scheduler) job.extensionInfo().scheduler();
//					x10Sched.addDependencyAndEnqueue(x10Sched.RegisterPlugins(job), 
//							x10Sched.LoadTypePlugins((X10ClassType) t), true);
//				}
//			}
			
			if (n instanceof ClassDecl) {
				ClassDecl cd = (ClassDecl) n;
				X10ClassDef ct = (X10ClassDef) cd.classDef();
				try {
					X10ClassType baseClass = (X10ClassType) ct.typeSystem().systemResolver().find("x10.lang.annotation.PluginClass");
					List<X10ClassType> pluginClasses = ct.annotationsMatching(baseClass);
					for (Iterator<X10ClassType> i = pluginClasses.iterator(); i.hasNext(); ) {
						X10ClassType pluginClass = i.next();
						if (pluginClass.propertyInitializers().size() == 1) {
						    Expr e = (Expr) pluginClass.propertyInitializer(0);
						    if (e instanceof StringLit) {
						        StringLit s = (StringLit) e;
						        LoadPlugins.loadPlugin((ExtensionInfo) job.extensionInfo(), s.value());
						    }
						}
					}
				}
				catch (SemanticException e) {
					// ignore
				}
			}
			return super.leave(old, n, v);
		}
	}
	
	public LoadJobPlugins(Job job) {
		super("LoadJobPlugins", job, new LoadJobPluginsVisitor(job));
	}

	public List<Goal> prereqs() {
		X10Scheduler x10Sched = (X10Scheduler) Globals.Scheduler();
		List<Goal> l = new ArrayList<Goal>();
		l.add(x10Sched.LoadPlugins());
		l.add(x10Sched.PropagateAnnotations(job));
		l.addAll(super.prereqs());
		return l;
	}
}

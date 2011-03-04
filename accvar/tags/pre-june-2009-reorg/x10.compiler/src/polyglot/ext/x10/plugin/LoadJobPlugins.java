package polyglot.ext.x10.plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import polyglot.ast.ClassDecl;
import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.StringLit;
import polyglot.ast.TypeNode;
import polyglot.ext.x10.Configuration;
import polyglot.frontend.AbstractPass;
import polyglot.ext.x10.ExtensionInfo;
import polyglot.ext.x10.ExtensionInfo.X10Scheduler;
import polyglot.ext.x10.ast.DepParameterExpr;
import polyglot.ext.x10.extension.X10Ext;
import polyglot.ext.x10.plugin.RegisterPlugins.RegisterPluginsPass;
import polyglot.ext.x10.types.X10ClassType;
import polyglot.ext.x10.types.X10ParsedClassType;
import polyglot.ext.x10.types.X10Type;
import polyglot.frontend.Job;
import polyglot.frontend.Pass;
import polyglot.frontend.Scheduler;
import polyglot.frontend.VisitorPass;
import polyglot.frontend.goals.AbstractGoal;
import polyglot.frontend.goals.Goal;
import polyglot.frontend.goals.VisitorGoal;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.util.ErrorInfo;
import polyglot.util.ErrorQueue;
import polyglot.visit.NodeVisitor;

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
				X10ParsedClassType ct = (X10ParsedClassType) cd.type();
				try {
					X10ClassType baseClass = (X10ClassType) ct.typeSystem().systemResolver().find("x10.lang.annotation.PluginClass");
					List<X10ClassType> pluginClasses = ct.annotationMatching(baseClass);
					for (Iterator<X10ClassType> i = pluginClasses.iterator(); i.hasNext(); ) {
						X10ClassType pluginClass = i.next();
						DepParameterExpr dep = pluginClass.dep();
						Expr e = (Expr) dep.args().get(0);
						if (e instanceof StringLit) {
							StringLit s = (StringLit) e;
							LoadPlugins.loadPlugin((ExtensionInfo) job.extensionInfo(), s.value());
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
		super(job, new LoadJobPluginsVisitor(job));
	}

	public static Goal create(X10Scheduler scheduler, Job job) {
		return scheduler.internGoal(new LoadJobPlugins(job));
	}
	
	public Collection prerequisiteGoals(Scheduler scheduler) {
		X10Scheduler x10Sched = (X10Scheduler) scheduler;
		List<Goal> l = new ArrayList<Goal>();
		l.add(x10Sched.LoadPlugins());
		l.add(x10Sched.PropagateAnnotations(job));
		l.addAll(super.prerequisiteGoals(scheduler));
		return l;
	}
}

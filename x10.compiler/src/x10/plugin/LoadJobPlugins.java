/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

package x10.plugin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import polyglot.ast.ClassDecl;
import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.StringLit;
import polyglot.frontend.Globals;
import polyglot.frontend.Goal;
import polyglot.frontend.Job;
import polyglot.frontend.VisitorGoal;
import polyglot.types.QName;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.visit.NodeVisitor;
import x10.ExtensionInfo;
import x10.ExtensionInfo.X10Scheduler;
import x10.types.X10ClassDef;
import x10.types.X10ClassType;

public class LoadJobPlugins extends VisitorGoal {
	private static final long serialVersionUID = 6125852391079139183L;
	
	public static class LoadJobPluginsVisitor extends NodeVisitor {
		Job job;
		
		LoadJobPluginsVisitor(Job job) {
			this.job = job;
		}
		
        @Override
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
					TypeSystem ts = ct.typeSystem();
					Type baseClass = ts.systemResolver().findOne(QName.make("x10.lang.annotation.PluginClass"));
					List<Type> pluginClasses = ct.annotationsMatching(baseClass);
					for (Iterator<Type> i = pluginClasses.iterator(); i.hasNext(); ) {
						Type pluginType = i.next();
						if (pluginType instanceof X10ClassType) {
						    X10ClassType pluginClass = (X10ClassType) pluginType;
						    if (pluginClass.propertyInitializers().size() == 1) {
							Expr e = (Expr) pluginClass.propertyInitializer(0);
							if (e instanceof StringLit) {
							    StringLit s = (StringLit) e;
							    LoadPlugins.loadPlugin((ExtensionInfo) job.extensionInfo(), QName.make(s.value()));
							}
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
		X10Scheduler x10Sched = (X10Scheduler) scheduler;
		List<Goal> l = new ArrayList<Goal>();
		l.add(x10Sched.LoadPlugins());
		l.add(x10Sched.PropagateAnnotations(job));
		l.addAll(super.prereqs());
		return l;
	}
}

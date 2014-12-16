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

package x10.dom;


import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import polyglot.ast.Node;
import polyglot.ast.SourceFile;
import x10.Configuration;
import x10.ExtensionInfo;
import x10.ExtensionInfo.X10Scheduler;
import x10.ast.X10NodeFactory;
import x10.plugin.CompilerPlugin;
import x10.types.X10TypeSystem;
import polyglot.frontend.AbstractPass;
import polyglot.frontend.Job;
import polyglot.frontend.Pass;
import polyglot.frontend.Scheduler;
import polyglot.frontend.goals.AbstractGoal;
import polyglot.frontend.goals.Goal;
import polyglot.main.Report;
import polyglot.util.ErrorInfo;
import polyglot.util.ErrorQueue;
import polyglot.util.QuotedStringTokenizer;
import polyglot.visit.NodeVisitor;

/**
 * This plugin exports the AST as an XML file, forks an external tool to process
 * the file, waits until the tool finishes, then reads back in the XML,
 * replacing the AST.
 */
public class ExternalizerPlugin implements CompilerPlugin {
	public ExternalizerPlugin() {
		super();
	}
	
	protected static class ExternalizerPass extends AbstractPass {
		private final polyglot.frontend.ExtensionInfo info;
		
		protected ExternalizerPass(Goal goal, polyglot.frontend.ExtensionInfo info) {
			super(goal);
			this.info = info;
		}
		
		public boolean run() {
			Job job = this.goal().job();
			X10TypeSystem ts = (X10TypeSystem) info.typeSystem();
			X10NodeFactory nf = (X10NodeFactory) info.nodeFactory();
			
			String xmlFile = getXMLFileName(info, job);
			ErrorQueue eq = job.compiler().errorQueue();
			
			String xmlProcessor = Configuration.XML_PROCESSOR;
			boolean exportXML = Configuration.EXTERNALIZE_ASTS;
			boolean importXML = false;
			
			if (xmlProcessor != null && ! xmlProcessor.equals("")) {
				exportXML = true;
				importXML = true;
			}
			
			if (Report.should_report("xml", 1)) {
				exportXML = true;
				importXML = true;
			}
			
			X10Dom dom = new X10Dom(ts, nf);

			if (exportXML) {
				DomGenerator gen = new DomGenerator();
				org.w3c.dom.Element e = gen.gen(dom, job.ast());
				
				try {
					XMLWriter w = new XMLWriter(xmlFile);
					w.writeElement(e);
					w.close();
				}
				catch (IOException ex) {
					eq.enqueue(ErrorInfo.IO_ERROR, ex.getMessage());
					return false;
				}
			}
			
			if (xmlProcessor != null && ! xmlProcessor.equals("")) {
				if (! processXMLFile(xmlFile, xmlProcessor, eq)) {
					return false;
				}
			}
			
			if (importXML) {
				org.w3c.dom.Element e;
				try {
					XMLReader r = new XMLReader(xmlFile);
					e = r.readElement();
					r.close();
				}
				catch (IOException ex) {
					eq.enqueue(ErrorInfo.IO_ERROR, ex.getMessage());
					return false;
				}
				
				DomReader ungen = new DomReader(ts, nf, job.source());
				Node n = ungen.fromXML(dom, e);
				
				job.ast(n);
			}
			
			return true;
		}

		private boolean processXMLFile(String xmlFile, String xmlProcessor, ErrorQueue eq) {
			boolean okay = true;
			
			Runtime runtime = Runtime.getRuntime();
			QuotedStringTokenizer st = new QuotedStringTokenizer(xmlProcessor);
			int pc_size = st.countTokens();
			String[] cmd = new String[pc_size+1];
			int j = 0;
			for (int i = 0; i < pc_size; i++) {
				cmd[j++] = st.nextToken();
			}
			cmd[j++] = xmlFile;
			
			if (Report.should_report(Report.verbose, 1)) {
				StringBuffer cmdStr = new StringBuffer();
				for (int i = 0; i < cmd.length; i++)
					cmdStr.append(cmd[i]+" ");
				Report.report(1, "Executing XML processor " + cmdStr);
			}
			
			try {
				Process proc = runtime.exec(cmd);
				InputStreamReader err = new InputStreamReader(proc.getErrorStream());
				
				try {
					char[] c = new char[72];
					int len;
					StringBuffer sb = new StringBuffer();
					while((len = err.read(c)) > 0) {
						sb.append(String.valueOf(c, 0, len));
					}
					
					if (sb.length() != 0) {
						eq.enqueue(ErrorInfo.POST_COMPILER_ERROR, sb.toString());
					}
				}
				finally {
					err.close();
				}
				
				proc.waitFor();
				
				if (proc.exitValue() > 0) {
					eq.enqueue(ErrorInfo.POST_COMPILER_ERROR,
							"Non-zero return code: " + proc.exitValue());
					okay = false;
				}
			}
			catch(Exception ex) {
				eq.enqueue(ErrorInfo.POST_COMPILER_ERROR, ex.getMessage());
				okay = false;
			}
			return okay;
		}
		
		protected String getXMLFileName(final polyglot.frontend.ExtensionInfo extInfo, Job job) {
			final String[] packageName = new String[1];
			job.ast().visit(new NodeVisitor() {
				public Node override(Node n) {
					if (n instanceof SourceFile) {
						SourceFile sf = (SourceFile) n;
						if (sf.package_() != null) {
							packageName[0] = sf.package_().package_().fullName();
						}
						return n;
					}
					return null;
				}
			});
			
			File f = extInfo.targetFactory().outputFile(packageName[0], job.source());
			String xmlFile = f.getPath();
			int index = f.getPath().lastIndexOf(".java");
			if (index >= 0) {
				xmlFile = f.getPath().substring(0, index) + ".x10ml";
			}
			else {
				xmlFile = f.getPath() + ".x10ml";
			}
			return xmlFile;
		}
	}
	
	public static class ExternalizerPluginGoal extends AbstractGoal {
		public static Goal create(Scheduler scheduler, Job job) {
			return scheduler.internGoal(new ExternalizerPluginGoal(job));
		}
		
		private ExternalizerPluginGoal(Job job) {
			super(job);
		}
		
		public Collection prerequisiteGoals(Scheduler scheduler) {
			X10Scheduler x10Sched = (X10Scheduler) scheduler;
			List<Goal> l = new ArrayList<Goal>();
			l.add(x10Sched.TypeChecked(job));
			l.add(x10Sched.ConstantsChecked(job));
			l.add(x10Sched.PropagateAnnotations(job));
			l.addAll(super.prerequisiteGoals(scheduler));
			return l;
		}
		
		@Override
		public Pass createPass(final polyglot.frontend.ExtensionInfo extInfo) {
			return new ExternalizerPass(this, extInfo);
		}
	}
	
	public Goal register(ExtensionInfo extInfo, Job job) {
		Goal g = ExternalizerPluginGoal.create(extInfo.scheduler(), job);
		X10Scheduler x10Sched = (X10Scheduler) extInfo.scheduler();
		x10Sched.addDependencyAndEnqueue(x10Sched.X10Boxed(job), g, true);
		return g;
	}
}

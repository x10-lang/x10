package com.ibm.wala.cast.x10.translator.polyglot;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import polyglot.frontend.ExtensionInfo;
import polyglot.frontend.Job;
import polyglot.frontend.SourceGoal_c;
import polyglot.main.Report;
/* FIXME: uncomment after fixing compiler
import x10.finish.table.CallTableKey;
import x10.finish.table.CallTableUtil;
import x10.finish.table.CallTableVal;
*/
import com.ibm.wala.cast.java.translator.polyglot.PolyglotIdentityMapper;
import com.ibm.wala.cast.tree.CAstEntity;
import com.ibm.wala.cast.x10.analysis.X10FinishAsyncAnalysis;
import com.ibm.wala.cast.x10.analysis.util.GraphUtil;
import com.ibm.wala.cast.x10.client.X10SourceAnalysisEngine;
import com.ibm.wala.cast.x10.ipa.cha.X10ClassHierarchy;
import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.ipa.callgraph.Entrypoint;
import com.ibm.wala.ipa.callgraph.impl.DefaultEntrypoint;
import com.ibm.wala.types.ClassLoaderReference;
import com.ibm.wala.types.Descriptor;
import com.ibm.wala.types.MethodReference;
import com.ibm.wala.types.TypeReference;
import com.ibm.wala.util.strings.Atom;

public class X102IRGoal extends SourceGoal_c {
    // TODO: get rid of static state
    // TODO: figure out whether we need multiple loaders
	static {
		if(Report.should_report("verbose", 1))
			Report.report(5,"WALA is invoked!");
	}
    private static final ClassLoaderReference X10LOADER = X10SourceLoaderImpl.X10SourceLoader;

    private static PolyglotIdentityMapper mapper = new X10PolyglotIdentityMapper(X10LOADER);
    
    private static X10SourceAnalysisEngine engine = new X10SourceAnalysisEngine() {
        {
            try {
            	if(Report.should_report("verbose", 1))
        			Report.report(5,"building analysis scope ...");
                
            	buildAnalysisScope();
            } catch (Throwable t) {}
            
            if(Report.should_report("verbose", 1))
    			Report.report(5,"initializing class hierarchy ...");
            
            X10ClassHierarchy cha = initClassHierarchy();
            setClassHierarchy(cha);
            
            if(Report.should_report("verbose", 1))
    			Report.report(5,"translating AST to IR ...");
        }
        
        public String getExclusionsFile() {
            return null;
        }
    };

    private static final List mainClasses = new ArrayList();
    
    private static final X10SourceLoaderImpl fSourceLoader = (X10SourceLoaderImpl) engine.getClassHierarchy().getLoader(X10LOADER);

    public X102IRGoal(Job job) {
        super(job); 
    }

    @Override
    public boolean runTask() {
        ExtensionInfo extInfo = job.extensionInfo();
        if(Report.should_report("verbose", 2))
			Report.report(2,"translating " + job.source().name() + " x10 ast to wala ast ...");
        X10toCAstTranslator fTranslator = new X10toCAstTranslator(X10LOADER, extInfo.nodeFactory(), extInfo.typeSystem(), mapper, false);
        CAstEntity entity = fTranslator.translate(job.ast(), job.source().name());
        
        if(Report.should_report("verbose", 2))
			Report.report(2,"translating " + job.source().name() + " wala ast to ir ...");
        new X10CAst2IRTranslator(entity, fSourceLoader).translate();
        return true;
    }

    @Override
    public String name() {
        return "<X10 to IR goal for " + job().source().path() + ">";
    }

    public static void hasMain(String mainClass) {
        mainClasses.add(mainClass);
    }

    // test method to be called once the IR has been generated for all jo
    public static void printCallGraph() {
    	//System.err.println(buildCallGraph());
    	GraphUtil.printNumberedGraph(buildCallGraph(), (String)mainClasses.get(mainClasses.size()-1));
    }
    
	private static CallGraph buildCallGraph(){
    	try {
            engine.consolidateClassHierarchy();
            List<Entrypoint> entrypoints = new ArrayList<Entrypoint>();
            for (Iterator it = mainClasses.iterator(); it.hasNext();) {
                String mainClass = (String) it.next();
                final TypeReference typeRef = TypeReference.findOrCreate(X10LOADER, "L" + mainClass);
                final MethodReference mainRef = MethodReference.findOrCreate(
                        typeRef, Atom.findOrCreateAsciiAtom("main"),
                        Descriptor.findOrCreateUTF8("(Lx10/lang/Rail;)V"));
                entrypoints.add(new DefaultEntrypoint(mainRef, engine.getClassHierarchy()));
            }
            if(Report.should_report("verbose", 1))
    			Report.report(5,"building call graph ...");
            return engine.buildCallGraph(entrypoints);
        }catch (Throwable t) {
        	System.err.println(t); 
        	t.printStackTrace();
        	return null;
        }
    	
    }

    /* FIXME: uncomment after fixing compiler
    public static HashMap<CallTableKey, LinkedList<CallTableVal>> analyze() throws Exception {
        boolean[] options = {
        		false, // whether to do fixed-point computation on calltable
        		false, // whether to print the calltable
        		true, // whether to expand everthing call in "at"
        		true,// whether to expand everthing call in "async"
        		true // whether to expand everthing call in "method"
        };
        return analyze(options);
    }
	public static HashMap<CallTableKey, LinkedList<CallTableVal>> analyze(boolean[] options) throws Exception {
		boolean ifExpanded = options[0];
		boolean ifDump = options[1];
		boolean[] mask = { options[2], options[3], options[4] };

		HashMap<CallTableKey, LinkedList<CallTableVal>> calltable = new HashMap<CallTableKey, LinkedList<CallTableVal>>();
		X10FinishAsyncAnalysis x10fa = new X10FinishAsyncAnalysis();
		CallGraph cg = buildCallGraph();
		if(Report.should_report("verbose", 1))
			Report.report(5,"call graph built!\nanalyzing programs ...");
		calltable = x10fa.build(cg,calltable);
		calltable = CallTableUtil.findPatterns(calltable);
		if (ifDump) {
			CallTableUtil.dumpCallTable(calltable);
		}
		if (ifExpanded) {
			if(Report.should_report("verbose", 1))
    			Report.report(5,"expanding talbe ...");
			CallTableUtil.expandCallTable(calltable, mask);
			// CallTableUtil.updateAllArity(calltable);
			// CallTableUtil.expandCallTable(calltable, mask);
		}
		if (ifDump && ifExpanded) {
			if(Report.should_report("verbose", 1))
    			Report.report(5,"New Talbe:");
			CallTableUtil.dumpCallTable(calltable);
		}
		if(Report.should_report("verbose", 1))
			Report.report(5,"done!");
		return calltable;
	}
    */
}

package x10.wala.translator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import polyglot.ast.ClassDecl;
import polyglot.ast.MethodDecl;
import polyglot.ast.Node;
import polyglot.ast.SourceFile;
import polyglot.ast.TopLevelDecl;
import polyglot.frontend.ExtensionInfo;
import polyglot.frontend.Job;
import polyglot.frontend.SourceGoal_c;
import polyglot.main.Report;
import polyglot.main.Reporter;
import polyglot.visit.NodeVisitor;
import x10.compiler.ws.util.WSTransformationContent;
import x10.wala.client.X10SourceAnalysisEngine;
import x10.wala.ipa.cha.X10ClassHierarchy;
import x10.wala.loader.X10SourceLoaderImpl;

import com.ibm.wala.cast.tree.CAstEntity;
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
		if(Report.verbose)
			Report.reporter.report(5,"WALA is invoked!");
	}
    private static final ClassLoaderReference X10LOADER = X10SourceLoaderImpl.X10SourceLoader;

    private static X10IdentityMapper mapper = new X10IdentityMapper(X10LOADER);
    
    private static X10SourceAnalysisEngine engine = new X10SourceAnalysisEngine();

    private static final List mainClasses = new ArrayList();
    
    private static final X10SourceLoaderImpl fSourceLoader = (X10SourceLoaderImpl) engine.getClassHierarchy().getLoader(X10LOADER);

    public X102IRGoal(Job job) {
        super(job); 
    }

    @Override
    public boolean runTask() {
        ExtensionInfo extInfo = job.extensionInfo();
        Reporter reporter = extInfo.getOptions().reporter;
        boolean shouldReport = reporter.should_report(Reporter.verbose, 2);
        if(shouldReport)
			reporter.report(2,"translating " + job.source().name() + " x10 ast to wala ast ...");
        X10toCAstTranslator fTranslator = new X10toCAstTranslator(X10LOADER, extInfo.nodeFactory(), extInfo.typeSystem(), mapper);
        CAstEntity entity = fTranslator.translate(job.ast(), job.source().name());
        
        if(shouldReport)
			reporter.report(2,"translating " + job.source().name() + " wala ast to ir ...");
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
    	System.err.println(buildCallGraph());
    	//GraphUtil.printNumberedGraph(buildCallGraph(), (String)mainClasses.get(mainClasses.size()-1));
    }
    
    // A simple method to analyze the call graph and identify transformation taret;
    public static WSTransformationContent wsAnalyzeCallGraph(Collection<Job> jobs) {
    	final WSTransformationContent targets = new X10WSCallGraphAnalyzer(buildCallGraph()).simpleAnalyze();
    	
    	NodeVisitor deadCodeFinderVisitor = new NodeVisitor(){
            public Node leave(Node old, Node n, NodeVisitor v) {
                if(n instanceof MethodDecl
                       // || n instanceof ConstructorDecl
                       // || (n instanceof Closure && !(n instanceof PlacedClosure))
                   ){           //Note, PlacedClosure are not treated as normal closure, not build node
                    targets.checkAndMarkDeadMethodDef((MethodDecl)n);                  
                }
                return n;
            }
        };
    	
    	//it is still in all barrier, so we can visit all the ast, and mark the dead defs
        for(Job job : jobs){
            if(job == null){
                System.err.println("[WALA_WS_ERR] Mark Dead Method: Find one job is empty!");
                continue;
            }
            Node node = job.ast();
            if(node != null && node instanceof SourceFile){
                for(TopLevelDecl tld : ((SourceFile)node).decls()){
                    if(tld instanceof ClassDecl){
                    	//visit the class decl
                    	tld.visit(deadCodeFinderVisitor);                  	
                    }
                }
            }
            else{
                if(node == null){
                    System.err.println("[WALA_WS_ERR] Mark Dead Method: AST node == null for job: " + job.source().toString());
                    continue;
                }
                if(! (node instanceof SourceFile)){
                    System.err.println("[WALA_WS_ERR] Mark Dead Method:  AST node is not SourceFile for job: " + job.source().toString());
                    continue;
                } 
            }
        }
    	return targets;
    }
    
	private static CallGraph buildCallGraph(){
    	try {
            engine.buildClassHierarchy();
            List<Entrypoint> entrypoints = new ArrayList<Entrypoint>();
            for (Iterator it = mainClasses.iterator(); it.hasNext();) {
                String mainClass = (String) it.next();
                final TypeReference typeRef = TypeReference.findOrCreate(X10LOADER, "L" + mainClass);
                final MethodReference mainRef = MethodReference.findOrCreate(
                        typeRef, Atom.findOrCreateAsciiAtom("main"),
                        Descriptor.findOrCreateUTF8("(Lx10.regionarray/Array;)V"));
                entrypoints.add(new DefaultEntrypoint(mainRef, engine.getClassHierarchy()));
            }
            if(Report.verbose)
    			Report.reporter.report(5,"building call graph ...");
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
		if(Report.verbose)
			Report.reporter.report(5,"call graph built!\nanalyzing programs ...");
		calltable = x10fa.build(cg,calltable);
		calltable = CallTableUtil.findPatterns(calltable);
		if (ifDump) {
			CallTableUtil.dumpCallTable(calltable);
		}
		if (ifExpanded) {
			if(Report.verbose)
    			Report.reporter.report(5,"expanding table ...");
			CallTableUtil.expandCallTable(calltable, mask);
			// CallTableUtil.updateAllArity(calltable);
			// CallTableUtil.expandCallTable(calltable, mask);
		}
		if (ifDump && ifExpanded) {
			if(Report.verbose)
    			Report.reporter.report(5,"New Table:");
			CallTableUtil.dumpCallTable(calltable);
		}
		if(Report.verbose)
			Report.reporter.report(5,"done!");
		return calltable;
	}
    */
}

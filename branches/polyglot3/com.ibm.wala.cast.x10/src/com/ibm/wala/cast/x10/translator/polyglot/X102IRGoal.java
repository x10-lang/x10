package com.ibm.wala.cast.x10.translator.polyglot;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;

import polyglot.frontend.ExtensionInfo;
import polyglot.frontend.Job;
import polyglot.frontend.Scheduler;
import polyglot.frontend.SourceGoal_c;

import com.ibm.wala.cast.java.translator.polyglot.PolyglotIdentityMapper;
import com.ibm.wala.cast.tree.CAstEntity;
import com.ibm.wala.cast.x10.client.X10SourceAnalysisEngine;
import com.ibm.wala.cast.x10.ipa.cha.X10ClassHierarchy;
import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.callgraph.Entrypoint;
import com.ibm.wala.ipa.callgraph.impl.DefaultEntrypoint;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.types.ClassLoaderReference;
import com.ibm.wala.types.Descriptor;
import com.ibm.wala.types.MethodReference;
import com.ibm.wala.types.TypeReference;
import com.ibm.wala.util.strings.Atom;

public class X102IRGoal extends SourceGoal_c {
    private final ClassLoaderReference fSourceLoaderRef;
    private final X10SourceLoaderImpl fSourceLoader;
    final protected PolyglotIdentityMapper fMapper;
    static X10SourceAnalysisEngine engine = new X10SourceAnalysisEngine() {

	    public String getExclusionsFile() {
		return null;
	    }
	    
	    protected Iterable<Entrypoint> makeDefaultEntrypoints(
			    final AnalysisScope scope, final IClassHierarchy cha) {
			final ClassLoaderReference loaderRef = X10SourceLoaderImpl.X10SourceLoader;
			final TypeReference typeRef = TypeReference.findOrCreate(
				loaderRef, "LFoo");
			final MethodReference mainRef = MethodReference.findOrCreate(
				typeRef, Atom.findOrCreateAsciiAtom("run"),
				Descriptor.findOrCreateUTF8("()Lx10/lang/Boolean;"));
			// Lx10/lang/Rail;
			final Collection<Entrypoint> entryPoints = new ArrayList<Entrypoint>(
				1);
			entryPoints.add(new DefaultEntrypoint(mainRef, getClassHierarchy()));
			return entryPoints;
		    }


    	X10SourceAnalysisEngine make() {
			try { buildAnalysisScope(); } catch (Throwable t) {}
			setClassHierarchy(initClassHierarchy());
			return this;
		}
    }.make();
    
    public X102IRGoal(Job job) {
		super(job);
		fSourceLoaderRef = X10SourceLoaderImpl.X10SourceLoader;
		fSourceLoader = (X10SourceLoaderImpl) engine.getClassHierarchy().getLoader(X10SourceLoaderImpl.X10SourceLoader);
		Scheduler scheduler = job.extensionInfo().scheduler();
		fMapper= new X10PolyglotIdentityMapper(fSourceLoaderRef, job.extensionInfo().typeSystem());
    }

    @Override
    public boolean runTask() {
        ExtensionInfo extInfo = job.extensionInfo();
        X10toCAstTranslator fTranslator = new X10toCAstTranslator(fSourceLoaderRef, extInfo.nodeFactory(), extInfo.typeSystem(), fMapper, false);
        CAstEntity entity = fTranslator.translate(job.ast(), job.source().name());
        PrintWriter pw= new PrintWriter(System.out);

      //  X10CAstPrinter.printTo(entity, pw);
        X10CAst2IRTranslator fTranslator2 = new X10CAst2IRTranslator(entity, fSourceLoader);
        fTranslator2.translate();
        return true;
    }

    public String name() {
	return "<X10 to IR goal for " + job().source().path() + ">";
    }
    
    public static void test() {
    	try { 
    		((X10ClassHierarchy) engine.getClassHierarchy()).build(); 
    		System.err.println(engine.buildCallGraph());
    	} catch (Throwable t) {System.err.println(t); }
    }
}

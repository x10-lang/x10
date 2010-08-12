package com.ibm.wala.cast.x10.translator.polyglot;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import polyglot.frontend.ExtensionInfo;
import polyglot.frontend.Job;
import polyglot.frontend.SourceGoal_c;

import com.ibm.wala.cast.java.translator.polyglot.PolyglotIdentityMapper;
import com.ibm.wala.cast.tree.CAstEntity;
import com.ibm.wala.cast.x10.client.X10SourceAnalysisEngine;
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

    private static final ClassLoaderReference X10LOADER = X10SourceLoaderImpl.X10SourceLoader;

    private static PolyglotIdentityMapper mapper = new X10PolyglotIdentityMapper(X10LOADER);

    private static X10SourceAnalysisEngine engine = new X10SourceAnalysisEngine() {
        {
            try {
                buildAnalysisScope();
            } catch (Throwable t) {}
            setClassHierarchy(initClassHierarchy());
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
        X10toCAstTranslator fTranslator = new X10toCAstTranslator(X10LOADER, extInfo.nodeFactory(), extInfo.typeSystem(), mapper, false);
        CAstEntity entity = fTranslator.translate(job.ast(), job.source().name());
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

    // test method to be called once the IR has been generated for all jobs
    public static void printCallGraph() {
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
            System.err.println(engine.buildCallGraph(entrypoints));
        } catch (Throwable t) {System.err.println(t); }
    }
}

/*
 * Created on Mar 25, 2006
 */
package com.ibm.domo.ast.x10.translator.polyglot;

import com.ibm.domo.ast.x10.analysis.AnalysisJobExt;

import polyglot.ext.x10.ExtensionInfo;
import polyglot.frontend.Job;
import polyglot.frontend.JobExt;
import polyglot.frontend.Scheduler;
import polyglot.frontend.goals.Goal;

import com.ibm.wala.cast.java.ipa.callgraph.JavaSourceAnalysisScope;
import com.ibm.wala.cast.java.translator.polyglot.IRTranslatorExtension;
import com.ibm.wala.cast.java.translator.polyglot.PolyglotIdentityMapper;
import com.ibm.wala.cast.java.translator.polyglot.PolyglotSourceLoaderImpl;
import com.ibm.wala.cast.tree.impl.*;
import com.ibm.wala.eclipse.util.*;
import com.ibm.wala.types.ClassLoaderReference;

public abstract class X10ExtensionInfo extends ExtensionInfo implements IRTranslatorExtension {
    protected X10SourceLoaderImpl fSourceLoader;
    protected PolyglotIdentityMapper fMapper;
    protected CAstRewriterFactory rewriterFactory;

    public void setSourceLoader(PolyglotSourceLoaderImpl sourceLoader) {
	fSourceLoader = (X10SourceLoaderImpl) sourceLoader;
	fMapper= new X10PolyglotIdentityMapper(fSourceLoader.getReference(), typeSystem());
    }

    public X10SourceLoaderImpl getSourceLoader() {
        return fSourceLoader;
    }

    public ClassLoaderReference getSourceLoaderReference() {
	return X10SourceLoaderImpl.X10SourceLoader;
    }

    protected Scheduler createScheduler() {
        return new X10DOMOScheduler(this);
    }

    public JobExt jobExt() {
	return new AnalysisJobExt();
    }

    public abstract Goal getCompileGoal(Job job);

    public PolyglotIdentityMapper getIdentityMapper() {
        return fMapper;
    }

    public void setCAstRewriterFactory(CAstRewriterFactory factory) {
      rewriterFactory = factory;
    }
    
    public CAstRewriterFactory getCAstRewriterFactory() {
      return rewriterFactory;
    }
}

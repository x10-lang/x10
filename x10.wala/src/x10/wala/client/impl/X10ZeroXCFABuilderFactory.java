package x10.wala.client.impl;

import java.io.InputStream;

import x10.wala.ipa.callgraph.AstX10ZeroXCFABuilder;

import com.ibm.wala.ipa.callgraph.AnalysisCache;
import com.ibm.wala.ipa.callgraph.AnalysisOptions;
import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.callgraph.CallGraphBuilder;
import com.ibm.wala.ipa.callgraph.ClassTargetSelector;
import com.ibm.wala.ipa.callgraph.MethodTargetSelector;
import com.ibm.wala.ipa.callgraph.impl.Util;
import com.ibm.wala.ipa.callgraph.propagation.cfa.ZeroXInstanceKeys;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.ipa.summaries.BypassClassTargetSelector;
import com.ibm.wala.ipa.summaries.BypassMethodTargetSelector;
import com.ibm.wala.ipa.summaries.XMLMethodSummaryReader;

public class X10ZeroXCFABuilderFactory {
    public CallGraphBuilder make(AnalysisOptions options, AnalysisCache cache, IClassHierarchy cha) {
        Util.addDefaultSelectors(options, cha);
//        Util.addDefaultBypassLogic(options, scope, Util.class.getClassLoader(), cha);
        return new AstX10ZeroXCFABuilder(cha, options, cache, null, null, ZeroXInstanceKeys.ALLOCATIONS);
    }
}

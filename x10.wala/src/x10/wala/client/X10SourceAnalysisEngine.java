package x10.wala.client;

import java.io.IOException;

import x10.wala.client.impl.X10ZeroXCFABuilderFactory;
import x10.wala.ipa.callgraph.X10SourceAnalysisScope;
import x10.wala.ipa.cha.X10ClassHierarchy;

import com.ibm.wala.cast.ir.ssa.AstIRFactory;
import com.ibm.wala.ipa.callgraph.AnalysisCache;
import com.ibm.wala.ipa.callgraph.AnalysisOptions;
import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.ipa.callgraph.CallGraphBuilder;
import com.ibm.wala.ipa.callgraph.Entrypoint;
import com.ibm.wala.ipa.callgraph.propagation.PointerAnalysis;
import com.ibm.wala.ipa.cha.ClassHierarchy;
import com.ibm.wala.ipa.cha.ClassHierarchyException;
import com.ibm.wala.ssa.SSAOptions;
import com.ibm.wala.ssa.SymbolTable;
import com.ibm.wala.util.CancelException;

public class X10SourceAnalysisEngine {
    /**
     * Governing class hierarchy
     */
    protected X10ClassHierarchy cha;

    public X10ClassHierarchy getClassHierarchy() {
        return cha;
    }

    public X10SourceAnalysisEngine() {
        try {
            cha = X10ClassHierarchy.make(new X10SourceAnalysisScope());
        } catch (ClassHierarchyException e) {
            System.err.println("Class Hierarchy construction failed");
        }
    }

    public CallGraph buildCallGraph(Iterable<Entrypoint> eps) throws IllegalArgumentException, CancelException, IOException {
        AnalysisCache cache = new AnalysisCache(AstIRFactory.makeDefaultFactory());
        AnalysisOptions options = new AnalysisOptions(cha.getScope(), eps);
        SSAOptions ssaOptions = new SSAOptions();
        ssaOptions.setDefaultValues(new SSAOptions.DefaultValues() {
            public int getDefaultValue(SymbolTable symtab, int valueNumber) {
                return symtab.getDefaultValue(valueNumber);
            }
        });
        options.setSSAOptions(ssaOptions);
        return new X10ZeroXCFABuilderFactory().make(options, cache, cha).makeCallGraph(options, null);
    }

    public void buildClassHierarchy() {
        try {
            cha.consolidate();
        } catch (ClassHierarchyException e) {
            System.err.println("Class Hierarchy construction failed");
        }
    }
}

package x10.wala.ipa.callgraph;

import x10.wala.loader.X10SourceLoaderImpl;

import com.ibm.wala.cast.ipa.callgraph.AstCallGraph;
import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.classLoader.IClassLoader;
import com.ibm.wala.ipa.callgraph.AnalysisCache;
import com.ibm.wala.ipa.callgraph.AnalysisOptions;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.ContextSelector;
import com.ibm.wala.ipa.callgraph.impl.DefaultContextSelector;
import com.ibm.wala.ipa.callgraph.impl.DelegatingContextSelector;
import com.ibm.wala.ipa.callgraph.impl.Everywhere;
import com.ibm.wala.ipa.callgraph.impl.ExplicitCallGraph;
import com.ibm.wala.ipa.callgraph.impl.FakeRootClass;
import com.ibm.wala.ipa.callgraph.impl.FakeRootMethod;
import com.ibm.wala.ipa.callgraph.propagation.SSAContextInterpreter;
import com.ibm.wala.ipa.callgraph.propagation.cfa.ZeroXInstanceKeys;
import com.ibm.wala.ipa.callgraph.propagation.cfa.nCFAContextSelector;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.types.Descriptor;
import com.ibm.wala.types.MethodReference;
import com.ibm.wala.types.TypeName;
import com.ibm.wala.types.TypeReference;
import com.ibm.wala.util.CancelException;
import com.ibm.wala.util.strings.Atom;

public class AstX10ZeroXCFABuilder extends AstX10CFABuilder {

    /**
     * @param cha
     * @param entrypoints
     * @param bypass
     * @param contextProvider
     */
    public AstX10ZeroXCFABuilder(IClassHierarchy cha, 
  			   AnalysisOptions options,
			   AnalysisCache cache,
  			   ContextSelector appContextSelector,
  			   SSAContextInterpreter appContextInterpreter, 
  			   int instancePolicy) {
      super(cha, options, cache);

      SSAContextInterpreter contextInterpreter = 
	      makeDefaultContextInterpreters(appContextInterpreter, options, cha);
      setContextInterpreter(contextInterpreter);

      ContextSelector def = new DefaultContextSelector(options);

      ContextSelector  contextSelector = 
	  appContextSelector == null? 
			             def: 
			            new DelegatingContextSelector(appContextSelector, def);
      setContextSelector(contextSelector);

      setInstanceKeys(
        new X10ScopeMappingInstanceKeys(cha, this, 
          new ZeroXInstanceKeys(options, cha, contextInterpreter, instancePolicy)));
    }

    @Override
    protected ExplicitCallGraph createEmptyCallGraph(IClassHierarchy cha, AnalysisOptions options) {
        return new AstCallGraph(cha, options, getAnalysisCache()) {
            @Override
            protected CGNode makeFakeRootNode() throws CancelException {
                final TypeReference fakeTypeRef = TypeReference.findOrCreate(X10SourceLoaderImpl.X10SourceLoader, FakeRootClass.FAKE_ROOT_CLASS.getName());
                Atom fakeMethodName = FakeRootMethod.name;
                Descriptor fakeMethodDesc = FakeRootMethod.descr;
                MethodReference fakeMethodRef = MethodReference.findOrCreate(fakeTypeRef, fakeMethodName, fakeMethodDesc);
                IClass fakeClass = new FakeRootClass(fakeTypeRef, cha) {
                    @Override
                    public TypeReference getReference() {
                        return fakeTypeRef;
                    }
                    @Override
                    public IClassLoader getClassLoader() {
                        return getClassHierarchy().getLoader(getReference().getClassLoader());
                    }
                };
                return findOrCreateNode(new AstFakeRoot(fakeMethodRef, fakeClass, cha, options, getAnalysisCache()), Everywhere.EVERYWHERE);
            }
            @Override
            protected CGNode makeFakeWorldClinitNode() throws CancelException {
              final TypeReference fakeTypeRef = TypeReference.findOrCreate(X10SourceLoaderImpl.X10SourceLoader, FakeRootClass.FAKE_ROOT_CLASS.getName());
              final Atom fakeMethodName = Atom.findOrCreateAsciiAtom("fakeWorldClinit");
              final Descriptor fakeMethodDesc = Descriptor.findOrCreate(new TypeName[0], TypeReference.VoidName);
              final MethodReference fakeMethodRef = MethodReference.findOrCreate(fakeTypeRef, fakeMethodName, fakeMethodDesc);
              final IClass fakeClass = new FakeRootClass(fakeTypeRef, cha) {
                  @Override
                  public TypeReference getReference() {
                      return fakeTypeRef;
                  }
                  @Override
                  public IClassLoader getClassLoader() {
                      return getClassHierarchy().getLoader(getReference().getClassLoader());
                  }
              };
              return findOrCreateNode(new AstFakeRoot(fakeMethodRef, fakeClass, cha, options, getAnalysisCache()), Everywhere.EVERYWHERE);
            }
        };
    }
}

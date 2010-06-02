package com.ibm.wala.cast.x10.ipa.callgraph;

import com.ibm.wala.cast.ipa.callgraph.AstCallGraph;
import com.ibm.wala.cast.ipa.callgraph.MiscellaneousHacksContextSelector;
import com.ibm.wala.cast.x10.loader.X10SyntheticLoaderImpl;
import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.classLoader.IClassLoader;
import com.ibm.wala.ipa.callgraph.AnalysisCache;
import com.ibm.wala.ipa.callgraph.AnalysisOptions;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.ContextSelector;
import com.ibm.wala.ipa.callgraph.ReflectionSpecification;
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

public class X10ZeroXCFABuilder extends X10CFABuilder {

    /**
     * @param cha
     * @param entrypoints
     * @param bypass
     * @param contextProvider
     */
    public X10ZeroXCFABuilder(IClassHierarchy cha, 
  			   AnalysisOptions options,
			   AnalysisCache cache,
  			   ContextSelector appContextSelector,
  			   SSAContextInterpreter appContextInterpreter, 
  			   ReflectionSpecification reflect, 
  			   int instancePolicy) {
      super(cha, options, cache);

      SSAContextInterpreter contextInterpreter = 
	      makeDefaultContextInterpreters(appContextInterpreter, options, cha, reflect);
      setContextInterpreter(contextInterpreter);

      ContextSelector def = new DefaultContextSelector(options);

      ContextSelector  contextSelector = 
	  appContextSelector == null? 
			             def: 
			            new DelegatingContextSelector(appContextSelector, def);
			   
	  contextSelector = new MiscellaneousHacksContextSelector(
			       		  new nCFAContextSelector(1, contextSelector),
			       		  contextSelector,
			       		  cha,
			       		  new String[][]{
			       			  new String[]{"X10Primordial", 
			       					  	   "X10", 
			       					  	   "Lx10/lang/clock$factory",
			       					  	   "clock", 
			       					  	   "()Lx10/lang/clock;"},
			       			 new String[]{"X10Primordial", 
			   					  	   "X10", 
			   					  	   "Lx10/lang/clock",
			   					  	   "registered", 
			   					  	   "()Z"},
			   				new String[]{"X10Primordial", 
			   					  	   "X10", 
			   					  	   "Lx10/lang/clock",
			   					  	   "drop", 
			   					  	   "()V"},
			   				new String[]{"X10Primordial", 
			   					  	   "X10", 
			   					  	   "Lx10/lang/clock",
			   					  	   "doNext", 
			   					  	   "()V"},
			   				new String[]{"X10Primordial", 
			   					  	   "X10", 
			   					  	   "Lx10/lang/clock",
			   					  	   "resume", 
			   					  	   "()V"}
			   
			       		  });
			         
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
                final TypeReference fakeTypeRef = TypeReference.findOrCreate(X10SyntheticLoaderImpl.X10SyntheticLoader, FakeRootClass.FAKE_ROOT_CLASS.getName());
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
              final TypeReference fakeTypeRef = TypeReference.findOrCreate(X10SyntheticLoaderImpl.X10SyntheticLoader, FakeRootClass.FAKE_ROOT_CLASS.getName());
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

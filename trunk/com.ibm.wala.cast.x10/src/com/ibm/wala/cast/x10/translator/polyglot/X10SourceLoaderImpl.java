/*
 * Created on Apr 28, 2006
 */
package com.ibm.domo.ast.x10.translator.polyglot;

import java.io.IOException;

import com.ibm.capa.ast.CAstEntity;
import com.ibm.domo.ast.java.translator.SourceModuleTranslator;
import com.ibm.domo.ast.java.translator.polyglot.IRTranslatorExtension;
import com.ibm.domo.ast.java.translator.polyglot.PolyglotSourceLoaderImpl;
import com.ibm.domo.ast.loader.AstMethod.DebuggingInformation;
import com.ibm.domo.ast.loader.AstMethod.LexicalInformation;
import com.ibm.domo.ast.x10.translator.X10CAstEntity;
import com.ibm.domo.cfg.AbstractCFG;
import com.ibm.domo.classLoader.IClass;
import com.ibm.domo.classLoader.IClassLoader;
import com.ibm.domo.ipa.callgraph.impl.SetOfClasses;
import com.ibm.domo.ipa.cha.ClassHierarchy;
import com.ibm.domo.ssa.SymbolTable;
import com.ibm.domo.types.ClassLoaderReference;
import com.ibm.domo.types.TypeReference;
import com.ibm.domo.util.warnings.WarningSet;

public class X10SourceLoaderImpl extends PolyglotSourceLoaderImpl {
    public X10SourceLoaderImpl(ClassLoaderReference loaderRef, IClassLoader parent, SetOfClasses exclusions,
	    ClassHierarchy cha, WarningSet warnings, IRTranslatorExtension extInfo) throws IOException {
	super(loaderRef, parent, exclusions, cha, warnings, extInfo);
    }

    public void defineAsync(CAstEntity fn, TypeReference asyncRef, String fileName) {
	fTypeMap.put(fn, new X10AsyncObject(asyncRef, this, fileName));
    }

    /**
     * Ideally we'd want to create some sort of IMethod to represent the async from
     * within X10Cast2IRTranslatingVisitor, but sadly that doesn't have a defineFunction()
     * method (since it doesn't actually inherit AstTranslator). So we create the
     * IMethod here inside the loader, where we can actually trap defineFunction().
     * Gack.
     */
    public void defineFunction(CAstEntity n, IClass owner, AbstractCFG cfg, SymbolTable symtab, boolean hasCatchBlock, TypeReference[][] catchTypes, LexicalInformation lexicalInfo, DebuggingInformation debugInfo) {
	if (n.getKind() == X10CAstEntity.ASYNC_BODY) {
	    X10AsyncObject asyncObject= (X10AsyncObject) fTypeMap.get(n);

	    asyncObject.setCodeBody(new ConcreteJavaMethod(n, owner, cfg, symtab, hasCatchBlock, catchTypes, lexicalInfo, debugInfo));
	} else
	    super.defineFunction(n, owner, cfg, symtab, hasCatchBlock, catchTypes, lexicalInfo, debugInfo);
    }
}

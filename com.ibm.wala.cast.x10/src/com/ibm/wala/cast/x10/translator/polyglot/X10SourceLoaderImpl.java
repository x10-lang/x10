/*
 * Created on Apr 28, 2006
 */
package com.ibm.domo.ast.x10.translator.polyglot;

import java.io.IOException;

import com.ibm.domo.ast.x10.translator.X10CAstEntity;
import com.ibm.wala.cast.java.translator.polyglot.IRTranslatorExtension;
import com.ibm.wala.cast.java.translator.polyglot.PolyglotSourceLoaderImpl;
import com.ibm.wala.cast.loader.AstMethod.DebuggingInformation;
import com.ibm.wala.cast.loader.AstMethod.LexicalInformation;
import com.ibm.wala.cast.tree.CAstEntity;
import com.ibm.wala.cast.tree.CAstSourcePositionMap;
import com.ibm.wala.cfg.AbstractCFG;
import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.classLoader.IClassLoader;
import com.ibm.wala.ipa.callgraph.impl.SetOfClasses;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.ssa.SymbolTable;
import com.ibm.wala.types.ClassLoaderReference;
import com.ibm.wala.types.TypeReference;
import com.ibm.wala.util.warnings.WarningSet;

public class X10SourceLoaderImpl extends PolyglotSourceLoaderImpl {

    public X10SourceLoaderImpl(ClassLoaderReference loaderRef, IClassLoader parent, SetOfClasses exclusions,
	    IClassHierarchy cha, WarningSet warnings, IRTranslatorExtension extInfo) throws IOException {
	super(loaderRef, parent, exclusions, cha, warnings, extInfo);
    }

    public void defineAsync(CAstEntity fn, TypeReference asyncRef, CAstSourcePositionMap.Position fileName) {
	X10AsyncObject asyncType= new X10AsyncObject(asyncRef, TypeReference.JavaLangObject, this, fileName, cha);
	fTypeMap.put(fn, asyncType);
	loadedClasses.put(asyncType.getName(), asyncType);
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

	    asyncObject.setCodeBody(new ConcreteJavaMethod(n, asyncObject, cfg, symtab, hasCatchBlock, catchTypes, lexicalInfo, debugInfo));
	} else
	    super.defineFunction(n, owner, cfg, symtab, hasCatchBlock, catchTypes, lexicalInfo, debugInfo);
    }
}

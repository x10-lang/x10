/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package polyglot.ext.x10.types;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import polyglot.frontend.Compiler;
import polyglot.frontend.ExtensionInfo;
import polyglot.types.ClassDef;
import polyglot.types.ClassType;
import polyglot.types.LazyRef;
import polyglot.types.Matcher;
import polyglot.types.MethodDef;
import polyglot.types.Named;
import polyglot.types.QName;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Name;
import polyglot.types.SourceClassResolver;
import polyglot.types.Type;
import polyglot.types.Types;
import polyglot.types.TypeSystem_c.TypeMatcher;
import polyglot.types.reflect.ClassFileLoader;
import polyglot.util.StringUtil;

public class X10SourceClassResolver extends SourceClassResolver {
	public X10SourceClassResolver(Compiler compiler, ExtensionInfo ext, String classpath, ClassFileLoader loader, boolean allowRawClasses,
			boolean compileCommandLineOnly, boolean ignoreModTimes) {
		super(compiler, ext, classpath, loader, allowRawClasses, compileCommandLineOnly, ignoreModTimes);
	}

	@Override
	public Named find(QName name) throws SemanticException {
		X10TypeSystem ts = (X10TypeSystem) this.ts;
		
		if (name.equals(QName.make("x10.lang.Box"))) return (Named) ts.Box();
		if (name.equals(QName.make("x10.lang.Void"))) return (Named) ts.Void();

		return super.find(name);
	}
}

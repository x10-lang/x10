package polyglot.ext.x10.types;

import java.util.ArrayList;
import java.util.List;

import polyglot.frontend.Compiler;
import polyglot.frontend.ExtensionInfo;
import polyglot.types.Flags;
import polyglot.types.MethodDef;
import polyglot.types.Named;
import polyglot.types.SemanticException;
import polyglot.types.SourceClassResolver;
import polyglot.types.Types;
import polyglot.types.reflect.ClassFileLoader;
import polyglot.util.Position;
import sun.text.CompactShortArray.Iterator;

public class X10SourceClassResolver extends SourceClassResolver {

	public X10SourceClassResolver(Compiler compiler, ExtensionInfo ext, String classpath, ClassFileLoader loader, boolean allowRawClasses,
			boolean compileCommandLineOnly, boolean ignoreModTimes) {
		super(compiler, ext, classpath, loader, allowRawClasses, compileCommandLineOnly, ignoreModTimes);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Named find(String name) throws SemanticException {
//		if (name.equals("x10.lang.Box")) {
//			Named n = super.find(name);
//			X10TypeSystem		xts = (X10TypeSystem) ts;
//			if (n instanceof X10ParsedClassType)
//				return xts.createBoxFromTemplate(((X10ParsedClassType) n).x10Def());
//			else
//				throw new SemanticException("Could not load " + name + ".");
//		}
		
	        X10TypeSystem ts = (X10TypeSystem) this.ts;
	        
		if (name.equals("x10.lang.Void")) return ts.Void();
		if (name.equals("x10.lang.Boolean")) return ts.Boolean();
		if (name.equals("x10.lang.Byte")) return ts.Byte();
		if (name.equals("x10.lang.Short")) return ts.Short();
		if (name.equals("x10.lang.Char")) return ts.Char();
		if (name.equals("x10.lang.Int")) return ts.Int();
		if (name.equals("x10.lang.Long")) return ts.Long();
		if (name.equals("x10.lang.Float")) return ts.Float();
		if (name.equals("x10.lang.Double")) return ts.Double();
	
		// Change java.lang.Object to x10.lang.Object
		if (name.equals("x10.lang.Object")) {
			Named n = super.find("java.lang.Object");
			if (n instanceof X10ParsedClassType) {
				X10ParsedClassType ct = (X10ParsedClassType) n;
				X10ClassDef cd = ct.x10Def();
				
				cd.setPackage(Types.ref(ts.packageForName(ts.packageForName("x10"), "lang")));
				
				List<MethodDef> methods = new ArrayList<MethodDef>();
				for (MethodDef m : cd.methods()) {
					if (m.name().equals("equals"))
						methods.add(m);
					if (m.name().equals("hashCode"))
						methods.add(m);
					if (m.name().equals("toString"))
						methods.add(m);
				}
				cd.setMethods(methods);

				assert cd.asType() == n;
				n = cd.asType();
				return n;
			}
		}
		
		// Change x10.lang.Array to x10.lang.GenericReferenceArray
		if (name.equals("x10.lang.Array")) {
		    Named n = super.find("x10.lang.GenericReferenceArray");
		    if (n instanceof X10ParsedClassType) {
			X10ParsedClassType ct = (X10ParsedClassType) n;
			X10ClassDef cd = ct.x10Def();
			n = cd.asType();
			return n;
		    }
		}
		
		// Change x10.lang.ValArray to x10.lang.genericArray
		if (name.equals("x10.lang.ValArray")) {
		    Named n = super.find("x10.lang.genericArray");
		    if (n instanceof X10ParsedClassType) {
			X10ParsedClassType ct = (X10ParsedClassType) n;
			X10ClassDef cd = ct.x10Def();
			n = cd.asType();
			return n;
		    }
		}
		
		// Change java.lang.String to x10.lang.String
		if (name.equals("x10.lang.String")) {
			Named n = super.find("java.lang.String");
			if (n instanceof X10ParsedClassType) {
				X10ParsedClassType ct = (X10ParsedClassType) n;
				X10ClassDef cd = ct.x10Def();
				
				cd.setPackage(Types.ref(ts.packageForName(ts.packageForName("x10"), "lang")));
				
//				List<MethodDef> methods = new ArrayList<MethodDef>();
//				MethodDef charAt = null;
//				for (MethodDef m : cd.methods()) {
//						methods.add(m);
//						if (m.name().equals("charAt"))
//							charAt = m;
//				}
//				// Add def apply(i) = charAt(i);
//				MethodDef m = cd.typeSystem().methodDef(Position.COMPILER_GENERATED, Types.ref(ct), Flags.PUBLIC, charAt.container(), "apply", charAt.formalTypes(), charAt.throwTypes());
//				methods.add(m);
//				cd.setMethods(methods);
				
				assert cd.asType() == n;
				n = cd.asType();
				return n;
			}
		}
		
		return super.find(name);
	}
}

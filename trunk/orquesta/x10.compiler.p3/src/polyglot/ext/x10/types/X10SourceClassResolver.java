package polyglot.ext.x10.types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import polyglot.util.StringUtil;
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
//			X10TypeSystem xts = (X10TypeSystem) ts;
//			if (n instanceof X10ParsedClassType)
//				return xts.createBoxFromTemplate(((X10ParsedClassType) n).x10Def());
//			else
//				throw new SemanticException("Could not load " + name + ".");
//		}
		
	        X10TypeSystem ts = (X10TypeSystem) this.ts;
	        
//	        if (name.equals("x10.lang.NativeRail")) return ts.NativeRail();
//	        if (name.equals("x10.lang.NativeValRail")) return ts.NativeValRail();
	        
		if (name.equals("x10.lang.Void")) return (Named) ts.Void();
		if (name.equals("x10.lang.Boolean")) return(Named) ts.Boolean();
		if (name.equals("x10.lang.Byte")) return (Named)ts.Byte();
		if (name.equals("x10.lang.Short")) return(Named) ts.Short();
		if (name.equals("x10.lang.Char")) return (Named)ts.Char();
		if (name.equals("x10.lang.Int")) return(Named) ts.Int();
		if (name.equals("x10.lang.Long")) return (Named)ts.Long();
		if (name.equals("x10.lang.Float")) return(Named) ts.Float();
		if (name.equals("x10.lang.UByte")) return (Named)ts.UByte();
		if (name.equals("x10.lang.UShort")) return(Named) ts.UShort();
		if (name.equals("x10.lang.UInt")) return(Named) ts.UInt();
		if (name.equals("x10.lang.ULong")) return(Named) ts.ULong();
	
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
		
//		// Change x10.lang.Array to x10.lang.GenericReferenceArray
//		if (name.equals("x10.lang.Array")) {
//		    Named n = super.find("x10.lang.GenericReferenceArray");
//		    if (n instanceof X10ParsedClassType) {
//			X10ParsedClassType ct = (X10ParsedClassType) n;
//			X10ClassDef cd = ct.x10Def();
//			n = cd.asType();
//			return n;
//		    }
//		}
//		
//		// Change x10.lang.ValArray to x10.lang.genericArray
//		if (name.equals("x10.lang.ValArray")) {
//		    Named n = super.find("x10.lang.genericArray");
//		    if (n instanceof X10ParsedClassType) {
//			X10ParsedClassType ct = (X10ParsedClassType) n;
//			X10ClassDef cd = ct.x10Def();
//			n = cd.asType();
//			return n;
//		    }
//		}
		
		Map<String,String> classMap = new HashMap<String, String>();
		classMap.put("x10.lang.String", "java.lang.String");
		classMap.put("x10.lang.Throwable", "java.lang.Throwable");
		classMap.put("x10.lang.Exception", "java.lang.Exception");
		classMap.put("x10.lang.RuntimeException", "java.lang.RuntimeException");
		
		String newName = classMap.get(name);
		
		if (newName != null) {
			Named n = super.find(newName);
			if (n instanceof X10ParsedClassType) {
				X10ParsedClassType ct = (X10ParsedClassType) n;
				X10ClassDef cd = ct.x10Def();
				
				String newPackage = StringUtil.getPackageComponent(newName);
				cd.setPackage(Types.ref(ts.packageForName(newPackage)));
				
				assert cd.asType() == n;
				n = cd.asType();
				return n;
			}
		}

		return super.find(name);
	}
}

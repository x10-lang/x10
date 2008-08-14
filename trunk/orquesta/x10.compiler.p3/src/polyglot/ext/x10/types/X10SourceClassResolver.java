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
import polyglot.types.Type;
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
	        
		Map<String,String> classMap = new HashMap<String, String>();
		classMap.put("x10.lang.String", "java.lang.String");
		classMap.put("x10.lang.Class", "java.lang.Class");
		classMap.put("x10.lang.Throwable", "java.lang.Throwable");
		classMap.put("x10.lang.Exception", "java.lang.Exception");
		classMap.put("x10.lang.Error", "java.lang.Error");
		classMap.put("x10.lang.RuntimeException", "java.lang.RuntimeException");
		classMap.put("x10.lang.NullPointerException", "java.lang.NullPointerException");
		classMap.put("x10.lang.ArrayIndexOutOfBoundsException", "java.lang.ArrayIndexOutOfBoundsException");
		classMap.put("x10.lang.ArithmeticException", "java.lang.ArithmeticException");
		classMap.put("x10.lang.Cloneable", "java.lang.Cloneable");
		classMap.put("x10.io.Serializable", "java.io.Serializable");
		classMap.put("x10.lang.ClassCastException", "java.lang.ClassCastException");
		classMap.put("x10.lang.ArrayStoreException", "java.lang.ArrayStoreException");

		Map<String,String> revMap = new HashMap<String, String>();
		for (Map.Entry<String, String> e : classMap.entrySet()) {
		    revMap.put(e.getValue(), e.getKey());
		}
		
		if (revMap.get(name) != null) {
	            return find(revMap.get(name));
	        }
	        
	        if (name.equals("x10.lang.Value")) return (Named) ts.Value();
	        if (name.equals("x10.lang.Box")) return (Named) ts.Box();
	        
		if (name.equals("x10.lang.Void")) return (Named) ts.Void();
		if (name.equals("x10.lang.Boolean")) return(Named) ts.Boolean();
		if (name.equals("x10.lang.Byte")) return (Named) ts.Byte();
		if (name.equals("x10.lang.Short")) return(Named) ts.Short();
		if (name.equals("x10.lang.Char")) return (Named) ts.Char();
		if (name.equals("x10.lang.Int")) return(Named) ts.Int();
		if (name.equals("x10.lang.Long")) return (Named) ts.Long();
		if (name.equals("x10.lang.Float")) return(Named) ts.Float();
		if (name.equals("x10.lang.Double")) return(Named) ts.Double();
		if (name.equals("x10.lang.UByte")) return (Named)ts.UByte();
		if (name.equals("x10.lang.UShort")) return(Named) ts.UShort();
		if (name.equals("x10.lang.UInt")) return(Named) ts.UInt();
		if (name.equals("x10.lang.ULong")) return(Named) ts.ULong();
	
		// Change java.lang.Object to x10.lang.Object.
		if (name.equals("x10.lang.Object")) {
			Named n = super.find("java.lang.Object");
			if (n instanceof X10ParsedClassType) {
				X10ParsedClassType ct = (X10ParsedClassType) n;
				X10ClassDef cd = ct.x10Def();
				
				cd.setPackage(Types.ref(ts.packageForName(ts.packageForName("x10"), "lang")));
				
				// Object is X10 is an interface!
				cd.flags(cd.flags().Interface());
				
				List<MethodDef> methods = new ArrayList<MethodDef>();
				for (MethodDef m : cd.methods()) {
//				    m.setFlags(m.flags().Abstract().clearNative());
				    if (m.name().equals("equals"))
					methods.add(m);
				    if (m.name().equals("hashCode"))
					methods.add(m);
				    if (m.name().equals("toString"))
					methods.add(m);
				    m.setFlags(m.flags().clearNative().Abstract());
				}
				cd.setMethods(methods);

				assert cd.asType() == n;
				n = cd.asType();
				return n;
			}
		}

		String newName = classMap.get(name);
		
		if (newName != null) {
		    Named n = ts.systemResolver().check(newName);
		    if (n == null) {
			n = super.find(newName);
		    }
		    if (n instanceof X10ParsedClassType) {
			X10ParsedClassType ct = (X10ParsedClassType) n;
			X10ClassDef cd = ct.x10Def();

			String newPackage = StringUtil.getPackageComponent(name);
			cd.setPackage(Types.ref(ts.packageForName(newPackage)));
			
			ts.systemResolver().install(newName, n);
			ts.systemResolver().install(name, n);
		    }
		    return n;
		}

		return super.find(name);
	}
}

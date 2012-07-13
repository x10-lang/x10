package x10.types.constraints.xsmt;

import java.util.ArrayList;
import java.util.List;

import polyglot.types.Def;
import polyglot.types.FieldDef;
import polyglot.types.MemberDef;
import polyglot.types.MethodDef;
import polyglot.types.Ref;
import polyglot.types.Type;
import polyglot.types.Types;
import x10.constraint.xsmt.SmtBaseType;
import x10.constraint.xsmt.SmtFuncSymbol;
import x10.constraint.xsmt.SmtType;
import x10.constraint.xsmt.SmtUtil;

public class CSmtUtil {

	/**
	 * Convert a polyglot Type into an SmtType 
	 * @param type
	 * @return
	 */
	public static SmtBaseType toSmtType(Type type) {
		//FIXME: this means that the type for self has not been properly set
		// let's just hope it's a class and not an Int
		if (type == null)
			return SmtType.USort(); 
		
    	if (type.isLongOrLess())
    		return SmtType.IntType();
    	
    	if (type.isBoolean())
    		return SmtType.BoolType();
    	
    	if (type.isFloat() || type.isDouble())
    		return SmtType.RealType();

    	// TODO: return the proper type
    	return SmtType.USort(); 
    	//return SmtType.USort(SmtUtil.mangle(type.toString()));
	}
	/**
	 * Construct a properly typed uninterpretd function symbol to model 
	 * the member function or method in SMT
	 * @param def definition of the member field/method
	 * @return
	 */
	public static SmtFuncSymbol makeFunctionSymbol(Def def) {
		if (def instanceof FieldDef) {
			FieldDef field = (FieldDef) def; 
			String name = field.name().toString();
			// prefix the field name with the type of the container
			name = Types.get(field.container()) + name; 
			SmtBaseType argType = toSmtType(Types.get(field.container()));
			SmtBaseType resultType = toSmtType(Types.get(field.type()));
			SmtType funcType = SmtType.makeType(argType, resultType);
			return new SmtFuncSymbol(SmtUtil.mangle(name), funcType);
		}
		
		if (def instanceof MethodDef) {
			MethodDef meth = (MethodDef) def; 
			String name = meth.name().toString();
			name = Types.get(meth.container()) + name;
			
			List<SmtBaseType> argTypes = new ArrayList<SmtBaseType>(meth.formalTypes().size()+1); 
			SmtBaseType containterType = toSmtType(Types.get(meth.container()));
			argTypes.add(containterType);
			
			for (Ref<? extends Type> t :meth.formalTypes()) {
				SmtBaseType type = toSmtType(Types.get(t));
				argTypes.add(type);
			}
			SmtBaseType resultType = toSmtType(Types.get(meth.returnType())); 
			SmtType funcType = SmtType.makeType(argTypes, resultType);
			return new SmtFuncSymbol(SmtUtil.mangle(name), funcType);
		}
		throw new IllegalArgumentException("Unsupported def type " + def);
	}

}

package x10.constraint.xsmt;

import java.util.ArrayList;
import java.util.List;

// TODO: rewrite this in a more reasonable way 

public class SmtFuncType extends SmtType {
	
	
	private final List<SmtBaseType> argTypes;
	private final SmtBaseType retType;
	
	public SmtFuncType(SmtBaseType t1, SmtBaseType t2) {
		this.argTypes = new ArrayList<SmtBaseType>(1);
		argTypes.add(t1);
		retType = t2;
	}
	
	public SmtFuncType(List<SmtBaseType> args, SmtBaseType ret) {
		retType = ret;
		this.argTypes = args;
	}
	
	public SmtType getArgType(int i) {
		if (argTypes == null)
			throw new UnsupportedOperationException();
		return argTypes.get(i);
	}

	public SmtType getReturnType() {
		if (argTypes == null)
			throw new UnsupportedOperationException();
		return argTypes.get(argTypes.size()-1);
	}
	
	public String toSmt2() {
		StringBuilder sb = new StringBuilder(); 
		sb.append("(");
		for (SmtBaseType t : argTypes) {
			sb.append(" "+ t.name());
		}
		sb.append(") ");
		sb.append(retType.name());
		return sb.toString(); 
	}

	@Override
	public int arity() {
		return argTypes.size();
	}

	@Override
	public SmtBaseType get(int i) {
		if (i > arity())
			throw new IllegalArgumentException(i + " greater than arity. ");
		
		if (i == arity())
			return retType;
		
		return argTypes.get(i);
	}

	@Override
	public boolean isUSort() {
		return false;
	}

	@Override
	public boolean isBoolean() {
		return false;
	}
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(); 
		sb.append("(");
		for (SmtBaseType arg: argTypes) {
			sb.append(" " + arg.toString());
		}
		sb.append(") "); 
		sb.append(retType.toString());
		return sb.toString(); 
	}
}

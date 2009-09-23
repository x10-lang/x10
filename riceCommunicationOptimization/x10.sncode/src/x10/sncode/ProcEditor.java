package x10.sncode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class ProcEditor extends MemberEditor {

	protected List<Type> typeArgs;
	protected List<LocalEditor> args;
	protected Type ret;
	protected Constraint guard;
    List<Type> exceptions;

	public ProcEditor() {
		super();
		typeArgs = new ArrayList<Type>();
		args = new ArrayList<LocalEditor>();
		exceptions = new ArrayList<Type>();
	}

    public void setExceptions(Type[] t) {
        setExceptions(Arrays.asList(t));
    }

    private void setExceptions(List<Type> l) {
        exceptions = l;
    }
    
	public List<Type> getTypeFormals() {
		return typeArgs;
	}

	public Type getReturnType() {
	    return ret;
	}

	public void setReturnType(Type t) {
	    ret = t;
	}

	public List<LocalEditor> getFormals() {
	    return args;
	}

	public void setFormals(LocalEditor[] t) {
	    setFormals(Arrays.asList(t));
	}

	public void setFormals(List<LocalEditor> l) {
	    args = l;
	}

	public Constraint getGuard() {
		return guard;
	}

	public void setGuard(Constraint c) {
	    guard = c;
	}

	public List<Type> getThrowTypes() {
		return exceptions;
	}



}
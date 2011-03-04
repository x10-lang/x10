package x10.wala.loader;

import com.ibm.wala.cast.loader.AstFunctionClass;
import com.ibm.wala.cast.tree.CAstSourcePositionMap;
import com.ibm.wala.classLoader.IClassLoader;
import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.types.Selector;
import com.ibm.wala.types.TypeReference;
import com.ibm.wala.util.debug.Assertions;

// TODO refactor to share code with X10AsyncObject

public class X10ClosureObject extends AstFunctionClass {
    private final IClassHierarchy cha;

    public X10ClosureObject(TypeReference reference, IClassLoader loader, CAstSourcePositionMap.Position fileName,
	    IClassHierarchy cha) {
	super(reference, loader, fileName);
	this.cha= cha;
    }

    @Override
    public IMethod getMethod(Selector selector) {
	if (functionBody.getSelector().equals(selector)) {
	    return functionBody;
	} else {
	    return null;
	}
    }

    public void setCodeBody(IMethod method) {
	Assertions.productionAssertion(method.getReference().getDeclaringClass().equals(getReference()));
	functionBody= method;
    }

    public String toString() {
	return "Closure@" + getSourcePosition();
    }

    public IClassHierarchy getClassHierarchy() {
	return cha;
    }
}

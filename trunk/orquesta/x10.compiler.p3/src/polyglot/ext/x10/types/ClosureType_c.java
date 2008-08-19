/*
 * Created on Mar 1, 2007
 */
package polyglot.ext.x10.types;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import polyglot.ast.Expr;
import polyglot.frontend.Job;
import polyglot.frontend.Source;
import polyglot.types.ClassDef;
import polyglot.types.ClassType;
import polyglot.types.ClassType_c;
import polyglot.types.ConstructorInstance;
import polyglot.types.FieldInstance;
import polyglot.types.Flags;
import polyglot.types.LazyRef;
import polyglot.types.LocalInstance;
import polyglot.types.MethodAsTypeTransform;
import polyglot.types.MethodDef;
import polyglot.types.MethodInstance;
import polyglot.types.Named;
import polyglot.types.Package;
import polyglot.types.Ref;
import polyglot.types.Resolver;
import polyglot.types.StructType;
import polyglot.types.Type;
import polyglot.types.Types;
import polyglot.types.ClassDef.Kind;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.util.Transformation;
import polyglot.util.TransformingList;
import x10.constraint.XConstraint;

public class ClosureType_c extends ClassType_c implements ClosureType {
    private static final long serialVersionUID = 2768150875334536668L;

    protected ClosureInstance ci;

    // Called by X10TypeSystem
    ClosureType_c(final X10TypeSystem ts, Position pos, final X10ClassDef def, ClosureInstance ci) {
	super(ts, pos, Types.ref(def));
	this.ci = ci;
    }
    
    public ClosureType_c(final X10TypeSystem ts, Position pos, final ClosureInstance ci) {
	super(ts, pos, null);
	final LazyRef<ClassDef> cd = Types.lazyRef(null);
	cd.setResolver(new Runnable() {
	    public void run() {
		cd.update(ts.closureInterfaceDef(ci.def()));
	    }
	});
	this.def = cd;
	this.ci = ci;
    }
    
    public ClosureInstance closureInstance() {
	return ci;
    }

    public ClosureType closureInstance(ClosureInstance ci) {
	ClosureType_c n = (ClosureType_c) copy();
	n.ci = ci;
	return n;
    }
    
    public ClosureDef closureDef() {
	return ci.def();
    }

    public Type returnType() {
	return ci.returnType();
    }

    public ClosureType returnType(Type l) {
	return closureInstance((ClosureInstance) ci.returnType(l));
    }

    public XConstraint guard() {
	return ci.guard();
    }

    public ClosureType guard(XConstraint l) {
	return closureInstance((ClosureInstance) ci.guard(l));
    }

    public List<Type> typeParameters() {
	return ci.typeParameters();
    }

    public ClosureType typeParameters(List<Type> l) {
	return closureInstance((ClosureInstance) ci.typeParameters(l));
    }

    public List<LocalInstance> formalNames() {
	return ci.formalNames();
    }
    
    public List<Type> argumentTypes() {
	return ci.formalTypes();
    }

    public ClosureType argumentTypes(List<Type> l) {
	return closureInstance((ClosureInstance) ci.formalTypes(l));
    }

    public List<Type> throwTypes() {
	return ci.throwTypes();
    }
    
    public ClosureType throwTypes(List<Type> l) {
	return closureInstance((ClosureInstance) ci.throwTypes(l));
    }
    
    public List<Type> typeArguments() {
	List<Type> l = new ArrayList<Type>();
	l.addAll(argumentTypes());
	l.add(returnType());
	return l;
    }
    
    public ClosureType typeArguments(List<Type> l) {
	ClosureInstance ci = this.ci;
	ci = (ClosureInstance) ci.formalTypes(l.subList(0, l.size()-1));
	ci = (ClosureInstance) ci.returnType(l.get(l.size()-1));
	return closureInstance(ci);
    }

    @Override
    public String translate(Resolver c) {
	// Just combine the result of calling translate() on each of the
	// component types?
	throw new InternalCompilerError("Fix Me: cannot translate() a closure type yet.");
    }

    public boolean safe() {
	return true;
    }

    @Override
    public String toString() {
	return ci.signature() + " => " + ci.returnType();
    }

    @Override
    public List<ConstructorInstance> constructors() {
	return Collections.EMPTY_LIST;
    }

    @Override
    public List<FieldInstance> fields() {
	return Collections.EMPTY_LIST;
    }

    @Override
    public Flags flags() {
	return def().flags();
    }

    @Override
    public List<Type> interfaces() {
	return Collections.EMPTY_LIST;
    }

    @Override
    public Job job() {
	return null;
    }

    @Override
    public Kind kind() {
	// TODO Auto-generated method stub
	return ClassDef.TOP_LEVEL;
    }

    @Override
    public List<ClassType> memberClasses() {
	return Collections.EMPTY_LIST;
    }
    
    @Override
    public List<MethodInstance> methods() {
	TypeParamSubst subst = new TypeParamSubst((X10TypeSystem) ts, typeArguments(), x10Def().typeParameters());
	List<MethodInstance> methods = subst.reinstantiate(new TransformingList<MethodDef, MethodInstance>(def().methods(), new MethodAsTypeTransform()));
	class SetContainerTransform implements Transformation<MethodInstance, MethodInstance> {
	    public MethodInstance transform(MethodInstance mi) {
		return (MethodInstance) mi.container(ClosureType_c.this);
	    }
	}
	return new TransformingList<MethodInstance, MethodInstance>(methods, new SetContainerTransform());
    }

    @Override
    public String name() {
	return def().name();
    }

    @Override
    public ClassType outer() {
	return null;
    }

    @Override
    public Package package_() {
	return Types.get(def().package_());
    }

    @Override
    public Type superClass() {
	return Types.get(def().superType());
    }

    public boolean isJavaType() {
	return false;
    }

    public boolean defaultConstructorNeeded() {
	return false;
    }

    public Source fromSource() {
	return null;
    }

    public ClassType container(StructType container) {
	return this;
    }

    public ClassType flags(Flags flags) {
	return this;
    }

    public boolean inStaticContext() {
	return false;
    }

    public List<FieldInstance> definedProperties() {
	return Collections.EMPTY_LIST;
    }

    public boolean isIdentityInstantiation() {
	return false;
    }

    public List<FieldInstance> properties() {
	return Collections.EMPTY_LIST;
    }

    public Expr propertyInitializer(int i) {
	return null;
    }

    public List<Expr> propertyInitializers() {
	return Collections.EMPTY_LIST;
    }

    public X10ClassType propertyInitializers(List<Expr> inits) {
	return this;
    }

    public List<Type> typeMembers() {
	return Collections.EMPTY_LIST;
    }

    public List<Type> typeMembersNamed(String name) {
	return Collections.EMPTY_LIST;
    }

    public List<Type> typeProperties() {
	return Collections.EMPTY_LIST;
    }

    public Named typePropertyNamed(String name) {
	return null;
    }

    public List<Type> annotations() {
	return Collections.EMPTY_LIST;
    }

    public List<Type> annotationsMatching(Type t) {
	return Collections.EMPTY_LIST;
    }

    public X10ClassDef x10Def() {
	return (X10ClassDef) def();
    }
}

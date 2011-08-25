package x10.util;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import polyglot.ast.Block;
import polyglot.ast.Expr;
import polyglot.ast.FieldDecl;
import polyglot.ast.FlagsNode;
import polyglot.ast.Formal;
import polyglot.ast.Id;
import polyglot.ast.LocalDecl;
import polyglot.ast.New;
import polyglot.ast.NodeFactory;
import polyglot.ast.Receiver;
import polyglot.ast.Stmt;
import polyglot.ast.TypeNode;
import polyglot.types.ContainerType;
import polyglot.types.Context;
import polyglot.types.FieldDef;
import polyglot.types.Flags;
import polyglot.types.LocalDef;
import polyglot.types.Name;
import polyglot.types.QName;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.Position;
import x10.ast.X10ClassDecl_c;
import x10.ast.X10LocalDecl_c;
import x10.ast.X10MethodDecl;
import x10.types.MethodInstance;
import x10.types.X10ClassDef;
import x10.types.X10ConstructorInstance;
import x10.types.X10FieldDef;
import x10.types.X10MethodDef;

public class X10TypeUtils {
	
    public static Type lookUpType(TypeSystem ts, QName qname) {
    	try {
			return ts.systemResolver().findOne(qname);
		} catch (SemanticException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
    }
    
    public static FieldDecl createFieldDecl(NodeFactory nf, TypeSystem ts, Position pos,
    		Flags flags, Type fieldType, Name fieldName,
    		ContainerType containerType, Expr initExpr) {
		Id fieldId = nf.Id(pos, fieldName);
		TypeNode lockTypeNode = nf.CanonicalTypeNode(pos, fieldType);
		X10FieldDef fd = ts.fieldDef(pos, Types.ref(containerType), flags,
					Types.ref(fieldType), fieldName);	
		FieldDecl fDecl = nf.FieldDecl(pos, nf.FlagsNode(pos, flags),
					lockTypeNode, fieldId, initExpr).fieldDef(fd);
		return fDecl;
    }
	
    public static X10MethodDecl createX10MethodDecl(NodeFactory nf, TypeSystem ts, Position pos,
    		Flags flags, ContainerType containerType, Type retType,
    		Name methodName, List<Stmt> bodyStatements) {
		Id methodId = nf.Id(pos, methodName);
		TypeNode retTypeNode = nf.CanonicalTypeNode(pos, retType);
		X10MethodDef methodDef = ts.methodDef(pos, Types.ref(containerType),
		           flags, Types.ref(retType), methodName,
		            Collections.<Ref<? extends Type>>emptyList(),  null);
		Block body = nf.Block(pos, bodyStatements);
		X10MethodDecl methodDecl = nf.MethodDecl(pos, nf.FlagsNode(pos, flags),
					retTypeNode, methodId, Collections.<Formal>emptyList(), body).methodDef(methodDef);
		return methodDecl;
    }
    
    public static LocalDecl createLocalDecl(NodeFactory nf, TypeSystem ts, Position pos,
    		Flags flags, Type localType, Name localName, Expr init) {
    	final LocalDef li = ts.localDef(pos, flags, Types.ref(localType), localName);
        final Id varId = nf.Id(pos, localName);
        final LocalDecl ld = nf.LocalDecl(pos, nf.FlagsNode(pos, flags),
        		nf.CanonicalTypeNode(pos, localType), varId, init).
        		localDef(li);
        return ld;
    }
    
    public static New createNewObjectByDefaultConstructor(NodeFactory nf, TypeSystem ts, Context context,
    		Position pos, Type clazzType) {
        TypeNode typeNode = nf.CanonicalTypeNode(pos, clazzType);
        X10ConstructorInstance ci = findDefaultConstructor(ts, context, clazzType);
	    New newInstance = (New) nf.New(pos, typeNode,
			Collections.<Expr>emptyList()).constructorInstance(ci).type(clazzType);
	    return newInstance;
    }
    
    public static X10ConstructorInstance findDefaultConstructor(TypeSystem ts, Context context, Type t) {
    	try {
			return ts.findConstructor(t, ts.ConstructorMatcher(t, Collections.<Type>emptyList(),
					context));
		} catch (SemanticException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
    }
    
    public static MethodInstance findMethod(TypeSystem ts, Context context, Type container, Name name, List<Type> argTypes) {
    	try {
			Collection<MethodInstance> collections = ts.findMethods(container, ts.MethodMatcher(container, name, 
					argTypes, context));
			assert collections.size() == 1;
			
			List<MethodInstance> mis = new LinkedList<MethodInstance>();
			mis.addAll(collections);
			assert mis.size() == 1;
			
			return mis.get(0);
		} catch (SemanticException e) {
			throw new RuntimeException(e);
		}
    }
    
    public static boolean hasStaticAtomicField(X10ClassDef clazzDef) {
    	for(FieldDef fdef : clazzDef.getAtomicFields()) {
    		if(fdef.flags().contains(Flags.STATIC)) {
    			return true;
    		}
    	}
    	return false;
    }
    
    public static boolean hasNonStaticAtomicField(X10ClassDef clazzDef) {
    	for(FieldDef fdef : clazzDef.getAtomicFields()) {
    		if(!fdef.flags().contains(Flags.STATIC)) {
    			return true;
    		}
    	}
    	return false;
    }
    
    public static List<Ref<? extends Type>> typesToTypeRefs(List<Type> types) {
    	List<Ref<? extends Type>> refs = new LinkedList<Ref<? extends Type>>();
    	for(Type t : types) {
    		refs.add(Types.ref(t));
    	}
    	return refs;
    }
    
    public static boolean hasFlag(FlagsNode flags, Flags f) {
    	return flags != null && flags.flags().contains(f);
    }
    
    public static boolean hasAtomic(FlagsNode flags) {
    	return hasFlag(flags, Flags.ATOMIC); //flags != null && flags.flags().contains(Flags.ATOMIC);
    }
    
    public static boolean isAtomicLocalDecl(X10LocalDecl_c localDecl) {
    	return hasAtomic(localDecl.flags()); // localDecl.flags() != null && localDecl.flags().flags().contains(Flags.ATOMIC);
    }
}
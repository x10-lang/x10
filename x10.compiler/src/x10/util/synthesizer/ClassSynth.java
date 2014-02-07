/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */
package x10.util.synthesizer;

import java.util.ArrayList;
import java.util.List;

import polyglot.ast.ClassBody;
import polyglot.ast.ClassDecl;
import polyglot.ast.ClassMember;
import polyglot.ast.Expr;
import polyglot.ast.FlagsNode;
import polyglot.ast.Id;
import polyglot.ast.NodeFactory;
import polyglot.ast.Receiver;
import polyglot.ast.Stmt;
import polyglot.ast.TypeNode;
import polyglot.types.ClassDef;
import polyglot.types.ClassType;
import polyglot.types.FieldDef;
import polyglot.types.Flags;
import polyglot.types.Name;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.Types;
import polyglot.types.ClassDef.Kind;
import polyglot.util.Position;
import polyglot.frontend.Job;
import x10.ast.TypeParamNode;
import x10.ast.X10ClassDecl;
import x10.ast.X10ConstructorDecl;
import x10.types.ParameterType;
import x10.types.ParameterType.Variance;
import x10.types.X10ClassDef;
import polyglot.types.Context;
import x10.types.checker.PlaceChecker;
import x10.types.constraints.TypeConstraint;

/**
 * Synthesizer to construct a class
 * 
 */
public class ClassSynth extends AbstractStateSynth implements IClassMemberSynth {

    X10ClassDef classDef;
    X10ClassDecl classDecl;

    List<IClassMemberSynth> membersSynth;

    public ClassSynth(Job job, NodeFactory xnf, Context xct, ClassDecl classDecl) {
        super(xnf, xct, classDecl.position());
        this.classDecl = (X10ClassDecl) classDecl;
        this.classDef = (X10ClassDef) classDecl.classDef();
        // no need to set others
        membersSynth = new ArrayList<IClassMemberSynth>();
        classDef.setJob(job);
    }

    public ClassSynth(Job job, NodeFactory xnf, Context xct, Position pos, Type superType, Name className,
            List<Type> interfaces, Flags flags, Kind kind) {
        super(xnf, xct, pos);
        membersSynth = new ArrayList<IClassMemberSynth>();

        classDef = (X10ClassDef) xts.createClassDef();
        classDef.setThisDef(xts.thisDef(pos, Types.ref(classDef.asType())));
        classDef.superType(Types.ref(superType)); // And the super Type

        List<Ref<? extends Type>> interfacesRef = new ArrayList<Ref<? extends Type>>();
        for (Type t : interfaces) {
            interfacesRef.add(Types.ref(t));
        }
        classDef.setInterfaces(interfacesRef);
        classDef.name(className);
        classDef.setFlags(flags);
        classDef.kind(kind); // important to set kind
        classDef.setJob(job);
    }

    /**
     * Create a class def with no interaces, Flags.None and top.level
     * 
     * @param xnf
     * @param xct
     * @param superType
     * @param className
     */
    public ClassSynth(Job job, NodeFactory xnf, Context xct, Position pos, Type superType, String className) {
        this(job, xnf, xct, pos, superType, Name.make(className), new ArrayList<Type>(), Flags.NONE, ClassDef.MEMBER);
    }

    public X10ClassDef getClassDef() {
        return classDef;
    }

    public X10ClassDef getDef() {
        return classDef;
    }

    public void setSuperType(Type superType) {
        classDef.superType(Types.ref(superType));
    }
    
    public void setOuter(X10ClassDef outClassDef){
        if(outClassDef != null){
            classDef.outer(Types.ref(outClassDef));            
        }
    }
    
    public X10ClassDef getOuter(){
        Ref<? extends X10ClassDef> outRef = classDef.outer();
        if(outRef != null){
            return outRef.get();
        }
        return null;
    }
    

    public Flags getFlags(){
        return classDef.flags();
    }
    
    public void setFlags(Flags flags) {
        try {
            checkClose();
            classDef.setFlags(flags);
        } catch (StateSynthClosedException e) {
            e.printStackTrace();
        }
    }

    public void setKind(Kind kind) {
        try {
            checkClose();
            classDef.kind(kind);
        } catch (StateSynthClosedException e) {
            e.printStackTrace();
        }

    }

    public void addInterface(Type interf) {
        try {
            checkClose();
            classDef.addInterface(Types.ref(interf));
        } catch (StateSynthClosedException e) {
            e.printStackTrace();
        }
    }
    
    public void addTypeParameter(ParameterType p, ParameterType.Variance v){
        try {
            checkClose();
            classDef.addTypeParameter(p, v);
        } catch (StateSynthClosedException e) {
            e.printStackTrace();
        }
    }
    
    public void addTypeParameters(List<ParameterType> params, List<ParameterType.Variance> variances){
    	assert(params.size() == variances.size());
    	int size = params.size();
    	for(int i = 0 ; i < size ; i++){
    		addTypeParameter(params.get(i), variances.get(i));
    	}
    }
    

    public FieldSynth createField(Position pos, String name, Type type) {
        FieldSynth fsynth = new FieldSynth(xnf, xct, pos, classDef, name, type);
        membersSynth.add(fsynth);
        return fsynth;
    }

    public ConstructorSynth createConstructor(Position pos) {
        ConstructorSynth conSynth = new ConstructorSynth(xnf, xct, pos, classDef);
        membersSynth.add(conSynth);
        return conSynth;
    }

    public ClassSynth createInnerClass(Position pos, Type type, String className) {
        ClassSynth cSynth = new ClassSynth(classDef.job(), xnf, xct, pos, type, className);
        membersSynth.add(cSynth);
        return cSynth;
    }

    public MethodSynth createMethod(Position pos, String methodName) {
        MethodSynth mSynth = new MethodSynth(xnf, xct, pos, classDef, methodName);
        membersSynth.add(mSynth);
        return mSynth;
    }

    public X10ClassDecl close() throws SemanticException {
        // should trigger all member gen to generate the decl,
        // add add it into the class decl
        if (closed) {
            return classDecl;
        }

        closed = true;

        if (classDecl == null) {
            // a new class - classdecl
            FlagsNode fNode = xnf.FlagsNode(pos, classDef.flags());
            Id id = xnf.Id(pos, classDef.name());
            TypeNode superTN = (TypeNode) xnf.CanonicalTypeNode(pos, classDef.superType());
            List<ClassMember> cmembers = new ArrayList<ClassMember>();
            // iterate all memberSynths

            ClassBody body = xnf.ClassBody(pos, cmembers);
            List<TypeNode> interfaceTN = new ArrayList<TypeNode>();
            for (Ref<? extends Type> t : classDef.interfaces()) {
                interfaceTN.add((TypeNode) xnf.CanonicalTypeNode(pos, t));
            }

            classDecl = (X10ClassDecl) xnf.ClassDecl(pos, fNode, id, superTN, interfaceTN, body);

            //add type parameters if classDef has type parameters
            if(classDef.typeParameters().size() > 0){
                List<TypeParamNode> typeParamNodes  = new ArrayList<TypeParamNode>();
                List<Variance> vars = classDef.variances();
                List<ParameterType> params = classDef.typeParameters();
                for(int i = 0; i < params.size(); i++){
                	TypeParamNode tn = xnf.TypeParamNode(compilerPos, xnf.Id(compilerPos, params.get(i).name()), vars.get(i));
                	typeParamNodes.add(tn.type(params.get(i)));
                }
                classDecl = classDecl.typeParameters(typeParamNodes);
            }
        	//After it has type parameters, we need create a type bounds here in all causes
            //In fact, some times, no need setting. For example, the container class has no parameter type
            if(classDef.typeBounds() == null){
            	classDef.setTypeBounds(Types.ref(new TypeConstraint()));
            }
            
            classDecl = (X10ClassDecl) classDecl.classDef(classDef);
        }
        // now tries to add all the members
        ClassBody b = classDecl.body();
        for (IClassMemberSynth cms : membersSynth) {
            if (cms instanceof ConstructorSynth) {
            	//need handle the type parameters for the constructor
            	ArrayList<ParameterType> paramTypes = new ArrayList<ParameterType>();
            	ArrayList<ParameterType.Variance> variances = new ArrayList<ParameterType.Variance>();
            	for(TypeParamNode tpn : classDecl.typeParameters()){
            		paramTypes.add(tpn.type());
            		variances.add(tpn.variance());
            	}
                ((ConstructorSynth) cms).setTypeParameters(paramTypes, variances);
            }
        	ClassMember cm = cms.close();
            b = b.addMember(cm);
        }
        classDecl = (X10ClassDecl) classDecl.body(b);
        return classDecl;
    }

    public X10ClassDecl getClassDecl() {
        if (closed) {
            return classDecl;
        } else {
            System.out.println("[ClassSynth_ERR]Try to get the ClassDecl before the class synthesizer is closed!");
            return null; // the class is not generated yet
        }
    }
}

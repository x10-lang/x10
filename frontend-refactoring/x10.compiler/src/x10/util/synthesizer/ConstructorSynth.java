/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */
package x10.util.synthesizer;

import java.util.ArrayList;
import java.util.List;

import polyglot.util.Position;
import x10.ast.AnnotationNode;
import x10.ast.Block;
import x10.ast.Expr;
import x10.ast.FlagsNode;
import x10.ast.Formal;
import x10.ast.TypeNode;
import x10.ast.X10ConstructorDecl;
import x10.ast.X10MethodDecl;
import x10.ast.NodeFactory;
import x10.extension.X10Del;
import x10.types.ClassDef;
import x10.types.ClassType;
import x10.types.Flags;
import x10.types.LocalDef;
import x10.types.Name;
import x10.types.Ref;
import x10.types.SemanticException;
import x10.types.Type;
import x10.types.Types;
import x10.types.X10ConstructorDef;
import x10.types.Context;
import x10.types.X10Flags;
import x10.types.TypeSystem;

/**
 * Synthesizer to construct a method
 */
public class ConstructorSynth extends AbstractStateSynth implements IClassMemberSynth {

    // ClassDef classDef;
    // List<Type> throwTypes;
    // Flags flag;

    List<Formal> formals;
    List<AnnotationNode> annotations;  // annotations of the new instance
    CodeBlockSynth codeBlockSynth;
    X10ConstructorDef conDef; // only be created once;
    X10ConstructorDecl conDecl; // only be created once;
    ClassDef classDef;

    public ConstructorSynth(NodeFactory xnf, Context xct, Position pos, ClassDef classDef, Flags flags,
            List<Formal> formals, List<Type> throwTypes) {
        super(xnf, xct, pos);
        this.formals = formals;
        this.classDef = classDef;

        ClassType classType = classDef.asType();

        // reference to formal
        annotations = new ArrayList<AnnotationNode>();
        List<Ref<? extends Type>> formalTypeRefs = new ArrayList<Ref<? extends Type>>();
        List<Ref<? extends Type>> throwTypeRefs = new ArrayList<Ref<? extends Type>>();
        for (Formal f : formals) {
            formalTypeRefs.add(f.type().typeRef());
        }
        for (Type t : throwTypes) {
            throwTypeRefs.add(Types.ref(t));
        }

        conDef = (X10ConstructorDef) xts.constructorDef(pos, Types.ref(classType), flags, formalTypeRefs); // formal
                                                                                                          // types
                                                                       
        classDef.addConstructor(conDef);

    }

    public ConstructorSynth(NodeFactory xnf, Context xct, Position pos, ClassDef classDef) {
        this(xnf, xct, pos, classDef, Flags.NONE, new ArrayList<Formal>(), new ArrayList<Type>());
    }

    public ConstructorSynth(NodeFactory xnf, Context xct, ClassDef classDef) {
        this(xnf, xct, compilerPos, classDef);
    }

    
    public void addAnnotation(AnnotationNode annotation){
        annotations.add(annotation);
    }
    
    public void setFlags(Flags flags) {
        try {
            checkClose();
            conDef.setFlags(flags);
        } catch (StateSynthClosedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Add a formal to this method, and return a ref to this formal. Flag is
     * None
     * 
     * @param pos
     * @param type
     * @param name
     * @return
     */
    public Expr addFormal(Position pos, Type type, String name) {
        return addFormal(pos, Flags.NONE, type, Name.make(name));
    }

    /**
     * Add a formal to this method, and return a ref to this formal.
     * 
     * @param pos
     * @param flags
     * @param type
     * @param name
     * @return
     */
    public Expr addFormal(Position pos, Flags flags, Type type, String name) {
        return addFormal(pos, flags, type, Name.make(name));
    }

    /**
     * Add a formal to this method, and return a ref to this formal
     * 
     * @param pos
     * @param flags
     * @param type
     * @param name
     * @return
     */
    public Expr addFormal(Position pos, Flags flags, Type type, Name name) {
        TypeSystem xts = (TypeSystem) xct.typeSystem();
        LocalDef lDef = xts.localDef(pos, flags, Types.ref(type), name);
        Formal f = xnf.Formal(pos, xnf.FlagsNode(pos, flags), xnf.CanonicalTypeNode(pos, type), xnf.Id(pos, name))
                .localDef(lDef);
        return addFormal(f);
    }

    public Expr addFormal(Formal formal) {
        try {
            checkClose();
            ArrayList<LocalDef> formalNames = new ArrayList<LocalDef>(conDef.formalNames());
            ArrayList<Ref<? extends Type>> formalRefs = new ArrayList<Ref<? extends Type>>(conDef.formalTypes());

            formalNames.add(formal.localDef());
            formalRefs.add(formal.type().typeRef());

            conDef.setFormalNames(formalNames);
            conDef.setFormalTypes(formalRefs);
            formals.add(formal);

            // now prepare the local ref
            Name name = formal.name().id();
            LocalDef lDef = formal.localDef();
            Type type = formal.type().type();
            return xnf.Local(pos, xnf.Id(pos, name)).localInstance(lDef.asInstance()).type(type);
        } catch (StateSynthClosedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public CodeBlockSynth createConstructorBody(Position pos) {
        if(codeBlockSynth == null){
            codeBlockSynth = new CodeBlockSynth(xnf, xct, this, pos);     
        }
        return codeBlockSynth;
    }

    public X10ConstructorDef getDef() {
        return conDef;
    }

    public X10ConstructorDecl close() throws SemanticException {

        if (conDecl != null) return conDecl;

        if (conDef == null) {
            getDef();
        }

        closed = true;
        
        // Method Decl
      
        FlagsNode flagNode = xnf.FlagsNode(pos, conDef.flags());

        Block block;
        if (codeBlockSynth == null) {
            block = xnf.Block(pos);
        } else {
            block = codeBlockSynth.close();
        }

        // constructor
        conDecl = (X10ConstructorDecl) xnf.ConstructorDecl(pos, flagNode, xnf.Id(pos, classDef.name()), formals, // formal
                                                                                                                 // types
                                                           block);
        // FIXME: need set the constructor's type parameters
        // conDecl.typeParameters(cDecl.typeParameters());
        conDecl = conDecl.returnType(xnf.CanonicalTypeNode(pos, conDef.returnType()));
        if(annotations.size() > 0){
            conDecl = (X10ConstructorDecl) ((X10Del)conDecl.del()).annotations(annotations);           
            List<Ref<? extends Type>> ats = new ArrayList<Ref<? extends Type>>(annotations.size());
            for (AnnotationNode an : annotations) {
                ats.add(an.annotationType().typeRef());
            }
            conDef.setDefAnnotations(ats);
        }
        conDecl = (X10ConstructorDecl) conDecl.constructorDef(conDef);
        
        return conDecl;
    }
}

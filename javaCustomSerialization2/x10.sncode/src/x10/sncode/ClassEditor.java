/*******************************************************************************
 * Copyright (c) 2002,2006 IBM Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package x10.sncode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;


/**
 * This is the core class for reading class file data.
 * 
 * ClassReader performs lazy parsing, and thus most of the methods can throw an
 * InvalidClassFileException.
 */
public final class ClassEditor extends MemberEditor implements SnConstants {

    public final static int INV = 0;
    public final static int COV = 1;
    public final static int CONTRAV = -1;

    /** List of fields */
    List<FieldEditor> fields;
    /** List of methods */
    List<MethodEditor> methods;
    /** List of constructors */
    List<ConstructorEditor> constructors;
    /** List of typedefs */
    List<TypedefEditor> typedefs;
    /** List of member classes */
    List<ClassEditor> memberClasses;

    String packageName;
    Type superClass;

    List<Type> interfaces;
    List<Type> typeFormals;
    List<Type> typeActuals;
    List<Constraint> typeActualConstraints;
    List<Integer> variances;

    Constraint classInvariant;

    public ClassEditor() {
        interfaces = new ArrayList<Type>(0);
        typeFormals = new ArrayList<Type>(0);
        typeActuals = new ArrayList<Type>(0);
        typeActualConstraints = new ArrayList<Constraint>(0);
        variances = new ArrayList<Integer>(0);

        fields = new ArrayList<FieldEditor>(1);
        methods = new ArrayList<MethodEditor>(1);
        constructors = new ArrayList<ConstructorEditor>(1);
        typedefs = new ArrayList<TypedefEditor>(1);
        memberClasses = new ArrayList<ClassEditor>(1);
        attributes = new ArrayList<Tree>(1);
    }
    
    public String getPackageName() {
    	return packageName;
    }
    
	public void setPackage(String pkg) {
		packageName = pkg;
	}

    public void setSuperClass(Type t) {
        superClass = t;
    }

    public Type getSuperClass() throws InvalidClassFileException {
        return superClass;
    }

    public void addInterface(Type t) {
        interfaces.add(t);
    }

    public List<Type> getInterfaces() {
        return interfaces;
    }
    
    public void addTypeFormal(Type t, int variance) {
        typeFormals.add(t);
        variances.add(variance);
    }

    public List<Type> getTypeFormals() {
        return typeFormals;
    }

    public void addTypeActual(Type t) {
        typeActuals.add(t);
    }

    public List<Type> getTypeActuals() {
        return typeActuals;
    }

    public void addTypeActualConstraint(Constraint c) {
        typeActualConstraints.add(c);
    }

    public List<Constraint> getTypeActualConstraints() {
        return typeActualConstraints;
    }

    public Constraint getClassInvariant() {
        return classInvariant;
    }

    public void setClassInvariant(Constraint t) {
        classInvariant = t;
    }

    public List<FieldEditor> fields() {
        return fields;
    }

    public List<MethodEditor> methods() {
        return methods;
    }

    public List<ConstructorEditor> constructors() {
        return constructors;
    }

    public List<TypedefEditor> typedefs() {
        return typedefs;
    }

    public List<ClassEditor> memberClasses() {
        return memberClasses;
    }
    
    List toList(Object o) throws InvalidClassFileException {
        if (o instanceof Object[])
            return Arrays.asList((Object[]) o);
        throw new InvalidClassFileException(0, "bad array " + o);
    }

    public void readFrom(final SnFile sn, Tree tree) throws InvalidClassFileException {
        name = (String) tree.find("Name");
        typeFormals = (List) toList(tree.find("TypeFormals"));
        variances = (List) toList(tree.find("Variances"));
        typeActuals = (List) toList(tree.find("TypeActuals"));
        typeActualConstraints = (List) toList(tree.find("TypeActualConstraints"));
        classInvariant = (Constraint) tree.find("ClassInvariant");
        superClass = (Type) tree.find("SuperClass");
        interfaces = (List<Type>) toList(tree.find("Interfaces"));

        typedefs = mapList(tree.findAll("Typedef"), new Mapper<Tree, TypedefEditor, InvalidClassFileException>() {
            TypedefEditor map(Tree t) throws InvalidClassFileException {
            	TypedefEditor e = new TypedefEditor();
                e.readFrom(sn, t);
                return e;
            }
        });
        fields = mapList(tree.findAll("Field"), new Mapper<Tree, FieldEditor, InvalidClassFileException>() {
            FieldEditor map(Tree t) throws InvalidClassFileException {
                FieldEditor e = new FieldEditor();
                e.readFrom(sn, t);
                return e;
            }
        });
        methods = mapList(tree.findAll("Method"), new Mapper<Tree, MethodEditor, InvalidClassFileException>() {
            MethodEditor map(Tree t) throws InvalidClassFileException {
                MethodEditor e = new MethodEditor();
                e.readFrom(sn, t);
                return e;
            }
        });
        constructors = mapList(tree.findAll("Constructor"), new Mapper<Tree, ConstructorEditor, InvalidClassFileException>() {
            ConstructorEditor map(Tree t) throws InvalidClassFileException {
                ConstructorEditor e = new ConstructorEditor();
                e.readFrom(sn, t);
                return e;
            }
        });
        memberClasses = mapList(tree.findAll("Class"), new Mapper<Tree, ClassEditor, InvalidClassFileException>() {
            ClassEditor map(Tree t) throws InvalidClassFileException {
                ClassEditor e = new ClassEditor();
                e.readFrom(sn, t);
                return e;
            }
        });
        
        String[] keys = new String[] { "Name", "TypeFormals", "TypeActuals", "TypeActualConstraints", "ClassInvariant", 
                                      "SuperClass", "Interfaces", "Field", "Method", "Constructor", "Typedef", "Class" };
        
        attributes = new ArrayList<Tree>(tree.getChildren().size());
        for (Iterator<Tree> i = tree.getChildren().iterator(); i.hasNext();) {
            Tree ti = i.next();
            if (Arrays.asList(keys).contains(ti.key))
                ;
            else
                attributes.add(ti);
        }
    }

    public Tree makeTree() {
        Tree.Branch t = new Tree.Branch("Class",
        		                        new Tree.Leaf("Package", packageName),
        		                        new Tree.Leaf("Name", name),
                                        new Tree.Leaf("TypeFormals", SnFile.nonnull(typeFormals).toArray(new Type[0])),
                                        new Tree.Leaf("Variances", SnFile.nonnull(variances).toArray(new Integer[0])),
                                        new Tree.Leaf("TypeActuals", SnFile.nonnull(typeActuals).toArray(new Type[0])),
                                        new Tree.Leaf("TypeActualConstraints", SnFile.nonnull(typeActualConstraints).toArray(new Constraint[0])),
                                        new Tree.Leaf("ClassInvariant", classInvariant), new Tree.Leaf("SuperClass", superClass),
                                        new Tree.Leaf("Interfaces", SnFile.nonnull(interfaces).toArray(new Type[0])));
        for (FieldEditor f : fields) {
            t.add(f.makeTree());
        }
        for (MethodEditor m : methods) {
            t.add(m.makeTree());
        }
        for (ConstructorEditor c : constructors) {
            t.add(c.makeTree());
        }
        for (ProcEditor d : typedefs) {
            t.add(d.makeTree());
        }
        for (ClassEditor c : memberClasses) {
            t.add(c.makeTree());
        }
        for (Tree a : attributes) {
            t.add(a);
        }
        return t;
    }

	public List<Integer> getTypeFormalVariances() {
		return variances;
	}
}

package x10.sncode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class MethodEditor extends Container {
    String name;
    List<Type> typeArgs;
    List<Type> args;
    Type ret;
    Constraint retConstraint;
    List<Type> exceptions;
    Constraint guard;
    // int typeGuard;
    Constraint body;

    public MethodEditor() {}

    public String getName() {
        return name;
    }

    public void setName(String n) {
        name = n;
    }

    public Type getReturnType() {
        return ret;
    }

    public void setReturnType(Type t) {
        ret = t;
    }

    public Constraint getReturnConstraint() {
        return retConstraint;
    }

    public void setReturnConstraint(Constraint c) {
        retConstraint = c;
    }

    public List<Type> getFormals() {
        return args;
    }

    public void setFormals(Type[] t) {
        setFormals(Arrays.asList(t));
    }

    public void setFormals(List<Type> l) {
        args = l;
    }
    
    public void setExceptions(Type[] t) {
        setExceptions(Arrays.asList(t));
    }

    private void setExceptions(List<Type> l) {
        exceptions = l;
    }

    public Constraint getPropertyBody() {
        return body;
    }

    public void setPropertyBody(Constraint t) {
        body = t;
    }

    public void setGuard(Constraint c) {
        guard = c;
    }

    @Override
    public Tree makeTree() {
        Tree.Branch t = new Tree.Branch("Method");
        t.add(new Tree.Leaf("Name", name));
        t.add(new Tree.Leaf("TypeFormals", SnFile.nonnull(typeArgs).toArray(new Type[0])));
        t.add(new Tree.Leaf("Formals", SnFile.nonnull(args).toArray(new Type[0])));
        t.add(new Tree.Leaf("ReturnType", ret));
        t.add(new Tree.Leaf("ReturnConstraint", retConstraint));
        t.add(new Tree.Leaf("Guard", guard));
        t.add(new Tree.Leaf("Throws", SnFile.nonnull(exceptions).toArray(new Type[0])));
        if (body != null)
            t.add(new Tree.Leaf("PropertyBody", body));
        for (Tree a : attributes) {
            t.add(a);
        }
        return t;
    }
    
    @Override
    public void readFrom(SnFile sn, Tree tree) throws InvalidClassFileException {
        name = (String) tree.find("Name");
        args = SnFile.toList(tree.find("Formals"));
        typeArgs = SnFile.toList(tree.find("TypeFormals"));
        ret = (Type) tree.find("ReturnType");
        retConstraint = (Constraint) tree.find("ReturnConstraint");
        guard = (Constraint) tree.find("Guard");
        exceptions = SnFile.toList(tree.find("Throws"));
        body = (Constraint) tree.find("PropertyBody");

        String[] keys = new String[] { "Name", "Formals", "TypeFormals", "ReturnType", "ReturnConstraint", "Guard", "Throws", "PropertyBody" };

        attributes = new ArrayList<Tree>(tree.getChildren().size());
        for (Iterator<Tree> i = tree.getChildren().iterator(); i.hasNext();) {
            Tree ti = i.next();
            if (Arrays.asList(keys).contains(ti.key))
                ;
            else
                attributes.add(ti);
        }
    }
}

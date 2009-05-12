package x10.sncode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class FieldEditor extends Container {
    String name;
    Type type;
    Object value;
    boolean isConstant;

    public FieldEditor() {
        super();
    }

    public String getName() {
        return name;
    }

    public void setName(String n) {
        name = n;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type t) {
        type = t;
    }

    boolean isConstant() {
        return isConstant;
    }

    public Object getConstantValue() {
        return value;
    }

    public void setConstantValue(Object t) {
        isConstant = true;
        value = t;
    }

    @Override
    public Tree makeTree() {
        Tree.Branch t = new Tree.Branch("Field", new Tree.Leaf("Name", name), new Tree.Leaf("Type", type));
        if (isConstant)
            t.add(new Tree.Leaf("ConstantValue", value));
        for (Tree a : attributes) {
            t.add(a);
        }
        return t;
    }

    @Override
    public void readFrom(SnFile sn, Tree tree) throws InvalidClassFileException {
        name = (String) tree.find("Name");
        type = (Type) tree.find("Type");
        if (tree.contains("ConstantValue")) {
            isConstant = true;
            value = tree.find("ConstantValue");
        }
        else {
            isConstant = false;
            value = null;
        }

        String[] keys = new String[] { "Name", "Type", "ConstantValue" };

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

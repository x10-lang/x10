package x10.sncode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import x10.sncode.Constraint.Term;

public class FieldEditor extends MemberEditor {
    Type type;
    Object value;
    boolean isConstant;

    public FieldEditor() {
        super();
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
        if (isConstant) {
            String ctype;
            if (value == null)
            	ctype = "n";
            else if (value instanceof String)
            	ctype = "T";
            else if (value instanceof Boolean) {
            	ctype = "Z"; value = ((Boolean) value) ? 1 : 0;
            }
            else if (value instanceof Byte) {
            	ctype = "B"; value = (Integer) (int) (Byte) value;
            }
            else if (value instanceof Short) {
            	ctype = "S"; value = (Integer) (int) (Short) value;
            }
            else if (value instanceof Character) {
            	ctype = "C"; value = (Integer) (int) (Character) value;
            }
            else if (value instanceof Integer)
            	ctype = "I";
            else if (value instanceof Long)
            	ctype = "L";
            else if (value instanceof Float)
            	ctype = "F";
            else if (value instanceof Double)
            	ctype = "D";
            else
            	ctype = null;
            if (ctype != null) {
            	t.add(new Tree.Leaf("ConstantValue", value));
            	t.add(new Tree.Leaf("ConstantType", ctype));
            }
        }
        for (Tree a : attributes) {
            t.add(a);
        }
        return t;
    }

    @Override
    public void readFrom(SnFile sn, Tree tree) throws InvalidClassFileException {
        name = (String) tree.find("Name");
        type = (Type) tree.find("Type");
        if (tree.contains("ConstantValue") && tree.contains("ConstantType")) {
            isConstant = true;
            value = tree.find("ConstantValue");
            String ctype = (String) tree.find("ConstantType");
            if (ctype != null && ctype.length() == 1) {
            	switch (ctype.charAt(0)) {
            	case 'Z':
            		value = ((Integer) value) != 0;
            		break;
            	case 'B':
            		value = (Byte) (byte) (int) (Integer) value;
            		break;
            	case 'S':
					value = (Short) (short) (int) (Integer) value;
					break;
				case 'C':
					value = (Character) (char) (int) (Integer) value;
					break;
            	}
            }
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

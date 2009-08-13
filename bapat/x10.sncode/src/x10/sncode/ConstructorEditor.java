package x10.sncode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import x10.sncode.Constraint.Term;

public class ConstructorEditor extends ProcEditor {
    public ConstructorEditor() {}

    @Override
    public Tree makeTree() {
        Tree.Branch t = new Tree.Branch("Constructor");
        
        t.add(new Tree.Leaf("TypeFormals", SnFile.nonnull(typeArgs).toArray(new Type[0])));
        t.add(new Tree.Leaf("ReturnType", ret));
        t.add(new Tree.Leaf("Guard", guard));
        t.add(new Tree.Leaf("Throws", SnFile.nonnull(exceptions).toArray(new Type[0])));

        List<Tree> fs = new ArrayList<Tree>();
        for (LocalEditor f : args) {
            fs.add(f.makeTree());
        }
        t.add(new Tree.Branch("Formals", fs));

        for (Tree a : attributes) {
            t.add(a);
        }
        return t;
    }
    
    @Override
    public void readFrom(final SnFile sn, Tree tree) throws InvalidClassFileException {
        typeArgs = SnFile.toList(tree.find("TypeFormals"));
        ret = (Type) tree.find("ReturnType");
        guard = (Constraint) tree.find("Guard");
        exceptions = SnFile.toList(tree.find("Throws"));

        Tree f = tree.findTree("Formals");
        
        args = mapList(f.findAll("Local"), new Mapper<Tree, LocalEditor, InvalidClassFileException>() {
        	LocalEditor map(Tree t) throws InvalidClassFileException {
            	LocalEditor e = new LocalEditor();
                e.readFrom(sn, t);
                return e;
            }
        });

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

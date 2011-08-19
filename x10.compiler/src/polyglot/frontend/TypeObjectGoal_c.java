package polyglot.frontend;

import java.io.IOException;
import java.io.ObjectInputStream;

import polyglot.frontend.JLScheduler.LookupGlobalTypeDefAndSetFlags;
import polyglot.types.Ref;
import polyglot.types.TypeObject;
import polyglot.util.TypeInputStream;

public class TypeObjectGoal_c<T extends TypeObject> extends AbstractGoal_c implements TypeObjectGoal<T> {
    private static final long serialVersionUID = -1495559169113722008L;

    Ref<T> v;
    
    public TypeObjectGoal_c(String name, Ref<T> v) {
        super(name);
        this.v = v;
    }

    public TypeObjectGoal_c(Ref<T> v) {
        this.v = v;
    }
    
   public Ref<T> typeRef() {
       return v;
   }
    
   public boolean runTask() {
	   return true;
   }
   
   @Override
   public int hashCode() {
       return super.hashCode() + v.hashCode();
   }
   
   @Override
   public boolean equals(Object o) {
       if (o instanceof TypeObjectGoal<?>) {
           TypeObjectGoal<?> g = (TypeObjectGoal<?>) o;
           return v.equals(g.typeRef());
       }
       return false;
   }
   
   private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
       in.defaultReadObject();
       
       if (in instanceof TypeInputStream) {
           // When deserializing, mark the goal as unreached to force the ref to be re-resolved.
	       update(Status.NEW);
       }
   }
}

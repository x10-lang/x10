package polyglot.types;

public interface MemberInstance<T extends Def> extends Use<T> {
    Flags flags();
    MemberInstance<T> flags(Flags flags);
    
    StructType container();
    MemberInstance<T> container(StructType t);
}

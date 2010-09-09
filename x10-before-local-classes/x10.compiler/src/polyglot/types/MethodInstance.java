package polyglot.types;

import java.util.List;

public interface MethodInstance extends FunctionInstance<MethodDef>, MemberInstance<MethodDef>, Use<MethodDef> {
    /**
     * The method's name.
     */
    Name name();

    MethodInstance name(Name name);
    MethodInstance returnType(Type returnType);
    MethodInstance formalTypes(List<Type> formalTypes);
    MethodInstance throwTypes(List<Type> throwTypes);
    
    /**
     * Get the list of methods this method (potentially) overrides, in order
     * from this class (i.e., including <code>this</code>) to super classes.
     * @param context TODO
     * @return A list of <code>MethodInstance</code>, starting with
     * <code>this</code>. Note that this list does not include methods declared
     * in interfaces. Use <code>implemented</code> for that.
     * @see polyglot.types.MethodDef
     */
    List<MethodInstance> overrides(Context context);

    /**
     * Return true if this method can override <code>mi</code>, false otherwise.
     * @param context TODO
     */
    boolean canOverride(MethodInstance mi, Context context);

    /**
     * Return true if this method can override <code>mi</code>, throws
     * a SemanticException otherwise.
     * @param context TODO
     */
    void checkOverride(MethodInstance mi, Context context) throws SemanticException;

    /**
     * Get the set of methods this method implements.  No ordering is
     * specified since the superinterfaces need not form a linear list
     * (i.e., they can form a tree).  
     * @param context TODO
     * @return List[MethodInstance]
     */
    List<MethodInstance> implemented(Context context); 

    /**
     * Return true if this method has the same signature as <code>mi</code>.
     * @param context TODO
     */
    boolean isSameMethod(MethodInstance mi, Context context);

    public MethodInstance returnTypeRef(Ref<? extends Type> returnType);
}

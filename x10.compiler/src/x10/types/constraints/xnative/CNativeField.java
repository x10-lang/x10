///**
// * 
// */
//package x10.types.constraints.xnative;
//import polyglot.ast.Typed;
//import polyglot.types.Def;
//import polyglot.types.FieldDef;
//import polyglot.types.LocalDef;
//import polyglot.types.MethodDef;
//import polyglot.types.Ref;
//import polyglot.types.Type;
//import polyglot.types.TypeObject;
//import polyglot.types.Types;
//import polyglot.types.VarDef;
//import x10.constraint.XConstraintSystem;
//import x10.constraint.XDef;
//import x10.constraint.XField;
//import x10.constraint.XTerm;
//import x10.constraint.XVar;
//import x10.constraint.xnative.XNativeField;
//import x10.constraint.xnative.XNativeLocal;
//import x10.constraint.xnative.XNativeTerm;
//import x10.types.X10ClassDef;
//import x10.types.X10FieldDef;
//import x10.types.X10FieldInstance;
//import x10.types.X10MethodDef;
//import x10.types.constraints.CField;
//import x10.types.constraints.ConstraintManager;
//
///**
// * An XField with type information (either a MethodDef or a FieldDef).
// * [DC] MethodDef seems to be so we can use property methods as fields if they have no params
// * 
// * @author vj
// */
//public class CNativeField extends XNativeField<Type,XDef<Type>> implements CField, Typed {
//    private static final long serialVersionUID = -1015395555006123008L;
//    // lazily initialized
//
//    public CNativeField(XNativeTerm<Type> r, XDef<Type> def, boolean hidden) {super(r, def, def.resultType(), hidden);}
//
//    /**
//     * Return a new CNativeField the same as the current one except that it has a new receiver.
//     * (In particular the new CNativeField has the same Def information as the old CNativeField.)
//     */
//    @Override
//    public CNativeField copyReceiver(XConstraintSystem<Type> sys, XNativeTerm<Type> newReceiver) {
//        return (CNativeField)super.copyReceiver(sys, newReceiver);
//    }
//
//    //@Override
//    //public String toString() {return (receiver == null ? "" : receiver.toString() + ".") + field.getName();}
//}

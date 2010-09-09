package polyglot.types;

import polyglot.types.VarDef_c.ConstantValue;
import polyglot.util.Position;

public class FieldInstance_c extends VarInstance_c<FieldDef> implements FieldInstance {
    public FieldInstance_c(TypeSystem ts, Position pos, Ref<? extends FieldDef> def) {
        super(ts, pos, def);
    }

    protected StructType container;

    public StructType container() {
        if (container == null) {
            return Types.get(def().container());
        }
        return container;
    }

    public FieldInstance container(StructType container) {
        FieldInstance_c v = (FieldInstance_c) copy();
        v.container = container;
        return v;
    }

    public FieldInstance flags(Flags flags) {
        return (FieldInstance) super.flags(flags);
    }

    public FieldInstance name(Name name) {
        return (FieldInstance) super.name(name);
    }

    public FieldInstance type(Type type) {
        return (FieldInstance) super.type(type);
    }

    public FieldInstance constantValue(Object o) {
        return (FieldInstance) super.constantValue(o);
    }

    public FieldInstance notConstant() {
        return (FieldInstance) super.notConstant();
    }
    
    public String toString() {
	FieldDef r = def.getCached();
	ConstantValue cv = r.constantValueRef().getCached();
	String cvStr = "";
	
	if (cv != null && cv.isConstant()) {
		Object v = cv.value();
		if (v instanceof String) {
			String s = (String) v;
	
			if (s.length() > 8) {
				s = s.substring(0, 8) + "...";
			}
	
			v = "\"" + s + "\"";
		}
	
		cvStr = " = " + v;
	}
	
	return "field " + (flags != null ? flags.translate() : r.flags().translate()) + (type != null ? type : r.type()) + " " + (container != null ? container : r.container()) + "." + (name != null ? name : r.name()) + cvStr;
    }

}

/**
 * 
 */
package x10.emitter;

import polyglot.types.Type;
import polyglot.visit.Translator;
import x10.visit.X10PrettyPrinterVisitor;

public class TypeExpander extends Expander {
	    /**
		 * 
		 */
		
		Type t;
	    int flags;

	    public TypeExpander(Emitter er, Type t, int flags) {
	    	super(er);
	      
			this.t = t;
	        this.flags = flags;
	    }
	    
	    public TypeExpander(Emitter er, Type t, boolean printGenerics, boolean boxPrimitives, 
	    		boolean inSuper) {
	        this(er, t, (printGenerics ? X10PrettyPrinterVisitor.PRINT_TYPE_PARAMS: 0) 
	        		| (boxPrimitives ? X10PrettyPrinterVisitor.BOX_PRIMITIVES : 0) 
	        		| (inSuper ? X10PrettyPrinterVisitor.NO_VARIANCE : 0));
	    }
	    
	    public String toString() {
	    	if ((flags & X10PrettyPrinterVisitor.BOX_PRIMITIVES) != 0)
	    		return "BP<" + t.toString() + ">";
	        return t.toString();
	    }

	    @Override
	    public void expand(Translator tr) {
//	        Translator old = X10PrettyPrinterVisitor.this.tr;
	        try {
//	            X10PrettyPrinterVisitor.this.tr = tr;
	            er.printType(t, flags);
	        }
	        finally {
//	            X10PrettyPrinterVisitor.this.tr = old;
	        }
	    }
	}
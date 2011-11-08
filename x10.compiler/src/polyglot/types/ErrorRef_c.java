/**
 * 
 */
package polyglot.types;

import polyglot.util.InternalCompilerError;
import polyglot.util.Position;

public class ErrorRef_c<T> extends TypeObject_c implements Ref<T> {
    private static final long serialVersionUID = -9197233963214096876L;

    String errorMessage;
    
    public ErrorRef_c(TypeSystem ts, Position pos, String errorMessage) {
        super(ts, pos, pos);
        this.errorMessage = errorMessage;
    }

    public T get() {
        throw new InternalCompilerError(errorMessage, position());
    }

    public T getCached() {
        throw new InternalCompilerError(errorMessage, position());
    }

    public boolean known() {
        return false;
    }

    public void update(T v) {
        throw new InternalCompilerError("Cannot update reference: " + errorMessage, position());
    }
}

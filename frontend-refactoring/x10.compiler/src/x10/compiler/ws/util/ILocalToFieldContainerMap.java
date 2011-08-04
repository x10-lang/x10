package x10.compiler.ws.util;

import java.util.HashMap;
import java.util.List;

import x10.ast.Expr;
import x10.ast.Stmt;
import x10.types.Name;
import x10.types.SemanticException;
import x10.types.Type;

/**
 * Originally, all locals are just locals
 * After WS transformation, locals are transformed as fields in different levels' fields
 * So all local access should be transformed as field acess
 *   e.g. n = 10 ==> _fib.n = 10
 * However, the field container's ref "_fib" should be provided by the WS code gen, too.
 * So here we provide this interface to do this transformation.
 * 
 * @author Haichuan
 *
 */
public interface ILocalToFieldContainerMap {

    public HashMap<Expr, Stmt> getRefToDeclMap(); //return all container ref's declare so that to add into the statements
    
    public Expr getFieldContainerRef(Name fieldName, Type type) throws SemanticException;  //return the field container's ref

}
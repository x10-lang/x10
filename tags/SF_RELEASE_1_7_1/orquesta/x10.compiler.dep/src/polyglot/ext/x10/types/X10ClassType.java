/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created on Nov 30, 2004
 *
 * 
 */
package polyglot.ext.x10.types;

import java.util.List;

import polyglot.ast.Expr;
import polyglot.ext.x10.ast.DepParameterExpr;
import polyglot.ext.x10.types.constr.C_Term;
import polyglot.types.ClassType;
import polyglot.types.FieldInstance;

/** The representative of ClassType in the X10 type hierarchy. A class is a
 * reference; arrays are examples of references which are not classes.
 * 
 * @author vj
 *
 */
public interface X10ClassType extends ClassType, X10Type, X10Use<X10ClassDef> {

    /** Property initializers, used in annotations. */
    List<Expr> propertyInitializers();
    Expr propertyInitializer(int i);
    X10ClassType propertyInitializers(List<Expr> inits);
    
    /**
     * The list of properties of the class. 
     * @return
     */
    List<FieldInstance> properties();

    /**
     * The sublist of properties defined at this class.
     * All and exactly the properties in this list need to be 
     * set in each constructor using a property(...) construct.
     * @return
     */
    List<FieldInstance> definedProperties();
}

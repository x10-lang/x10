/*
 *
 * (C) Copyright IBM Corporation 2006-2008
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

import java.util.ArrayList;
import java.util.List;

import polyglot.ast.Expr;
import polyglot.types.ClassType;
import polyglot.types.FieldInstance;
import polyglot.types.Matcher;
import polyglot.types.Named;
import polyglot.types.Name;
import polyglot.types.Type;
import polyglot.util.TransformingList;

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
    
    boolean isIdentityInstantiation();
    
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

    List<Type> typeArguments();
    X10ClassType typeArguments(List<Type> typeArgs);
    
    List<Type> typeMembers();

    MacroType typeMemberMatching(Matcher<Named> matcher);
    
    boolean isJavaType();

}

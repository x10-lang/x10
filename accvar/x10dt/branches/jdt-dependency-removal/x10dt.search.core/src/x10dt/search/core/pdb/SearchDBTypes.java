/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.search.core.pdb;

import static x10dt.search.core.pdb.X10FactTypeNames.APPLICATION;
import static x10dt.search.core.pdb.X10FactTypeNames.LIBRARY;
import static x10dt.search.core.pdb.X10FactTypeNames.RUNTIME;
import static x10dt.search.core.pdb.X10FactTypeNames.X10_AllFields;
import static x10dt.search.core.pdb.X10FactTypeNames.X10_AllMethods;
import static x10dt.search.core.pdb.X10FactTypeNames.X10_AllTypes;
import static x10dt.search.core.pdb.X10FactTypeNames.X10_Field;
import static x10dt.search.core.pdb.X10FactTypeNames.X10_FieldName;
import static x10dt.search.core.pdb.X10FactTypeNames.X10_Method;
import static x10dt.search.core.pdb.X10FactTypeNames.X10_MethodName;
import static x10dt.search.core.pdb.X10FactTypeNames.X10_Type;
import static x10dt.search.core.pdb.X10FactTypeNames.X10_TypeHierarchy;
import static x10dt.search.core.pdb.X10FactTypeNames.X10_TypeName;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.imp.pdb.facts.type.Type;
import org.eclipse.imp.pdb.facts.type.TypeFactory;
import org.eclipse.imp.pdb.facts.type.TypeStore;
import org.eclipse.imp.utils.Pair;

/**
 * Manages and provides services for the X10 Search PDB types.
 * 
 * @author egeay
 */
public final class SearchDBTypes {

  // --- Public services
  
  /**
   * Initializes all the relevant types for X10 Search facilities.
   * 
   * @see X10FactTypeNames
   * 
   * @return A non-null and unique instance of {@link SearchDBTypes}.
   */
  public static SearchDBTypes getInstance() {
    if (fInstance == null) {
      fInstance = new SearchDBTypes();
    }
    return fInstance;
  }
  
  /**
   * Returns the fact type for the name provided, if any is present.
   * 
   * @param name The type name to look for.
   * @return A non-null type instance if we have found it, otherwise <b>null</b>.
   */
  public Type getType(final String name) {
    return this.fTypeStore.lookupAlias(name);
  }
  
  /**
   * Returns the fact database type identified from the names provided.
   * 
   * @param parametricTypeName The parametric type name.
   * @param scopeTypeName The scope type name to bound to the parametric type.
   * @return A non-null {@link Type} instance if the type is managed by the database, otherwise <b>null</b>.
   */
  public Type getType(final String parametricTypeName, final String scopeTypeName) {
    return getTypeManager(parametricTypeName, scopeTypeName).getType();
  }
  
  /**
   * Returns the type manager instance that manages the type identified from the names provided.
   * 
   * @param parametricTypeName The parametric type name.
   * @param scopeTypeName The scope type name to bound to the parametric type.
   * @return A non-null {@link ITypeManager} instance if the type is managed by the database, otherwise <b>null</b>.
   */
  public ITypeManager getTypeManager(final String parametricTypeName, final String scopeTypeName) {
    return this.fScopedTypeToManager.get(new Pair<String, String>(parametricTypeName, scopeTypeName));
  }
  
  // --- Internal services
  
  TypeStore getTypeStore() {
    return this.fTypeStore;
  }
  
  // --- Private code
  
  private SearchDBTypes() {
    final TypeFactory typeFactory = TypeFactory.getInstance();
    this.fTypeStore = new TypeStore();
    
    final Type typeName = typeFactory.aliasType(this.fTypeStore, X10_TypeName, typeFactory.stringType());
    final Type methodName = typeFactory.aliasType(this.fTypeStore, X10_MethodName, typeFactory.stringType());
    final Type fieldName = typeFactory.aliasType(this.fTypeStore, X10_FieldName, typeFactory.stringType());
    final Type x10Type = typeFactory.aliasType(this.fTypeStore, X10_Type, 
                                               typeFactory.tupleType(typeName, typeFactory.sourceLocationType(),
                                                                     typeFactory.integerType(), typeName));
    final Type x10Method = typeFactory.aliasType(this.fTypeStore, X10_Method, 
                                                 typeFactory.tupleType(typeFactory.sourceLocationType(), methodName, typeName,
                                                                       typeFactory.listType(typeName), 
                                                                       typeFactory.integerType()));
    final Type x10Field = typeFactory.aliasType(this.fTypeStore, X10_Field,
                                                typeFactory.tupleType(typeFactory.sourceLocationType(), fieldName, typeName,
                                                                      typeFactory.integerType()));
    
    final Type application = typeFactory.aliasType(this.fTypeStore, APPLICATION, typeFactory.stringType());
    final Type library = typeFactory.aliasType(this.fTypeStore, LIBRARY, typeFactory.stringType());
    final Type runtime = typeFactory.aliasType(this.fTypeStore, RUNTIME, typeFactory.stringType());
    
    final Type scopeType = typeFactory.parameterType("scope"); //$NON-NLS-1$
    
    final Type allTypes = typeFactory.aliasType(this.fTypeStore, X10_AllTypes, typeFactory.setType(x10Type), scopeType);
    final Type allMethods = typeFactory.aliasType(this.fTypeStore, X10_AllMethods, 
                                                  typeFactory.relType(typeName, typeFactory.listType(x10Method)), scopeType);
    final Type allFields = typeFactory.aliasType(this.fTypeStore, X10_AllFields, 
                                                 typeFactory.relType(typeName, typeFactory.listType(x10Field)), scopeType);
    final Type typeHierarchy = typeFactory.aliasType(this.fTypeStore, X10_TypeHierarchy, 
                                                     typeFactory.relType(typeName, typeName), scopeType);
    
    this.fScopedTypeToManager = new HashMap<Pair<String,String>, ITypeManager>(6);
    
    this.fScopedTypeToManager.put(new Pair<String, String>(X10_AllMethods, APPLICATION), 
                                  new AllMethodsManager(instantiate(allMethods, scopeType, application)));
    this.fScopedTypeToManager.put(new Pair<String, String>(X10_AllMethods, LIBRARY), 
                                  new AllMethodsManager(instantiate(allMethods, scopeType, library)));
    this.fScopedTypeToManager.put(new Pair<String, String>(X10_AllMethods, RUNTIME), 
                                  new AllMethodsManager(instantiate(allMethods, scopeType, runtime)));
    
    this.fScopedTypeToManager.put(new Pair<String, String>(X10_AllFields, APPLICATION), 
                                  new AllFieldsManager(instantiate(allFields, scopeType, application)));
    this.fScopedTypeToManager.put(new Pair<String, String>(X10_AllFields, LIBRARY), 
                                  new AllFieldsManager(instantiate(allFields, scopeType, library)));
    this.fScopedTypeToManager.put(new Pair<String, String>(X10_AllFields, RUNTIME), 
                                  new AllFieldsManager(instantiate(allFields, scopeType, runtime)));
    
    this.fScopedTypeToManager.put(new Pair<String, String>(X10_AllTypes, APPLICATION), 
                                  new AllTypesManager(instantiate(allTypes, scopeType, application),
                                                      getTypeManager(X10_AllMethods, APPLICATION),
                                                      getTypeManager(X10_AllFields, APPLICATION)));
    this.fScopedTypeToManager.put(new Pair<String, String>(X10_AllTypes, LIBRARY), 
                                  new AllTypesManager(instantiate(allTypes, scopeType, library),
                                                      getTypeManager(X10_AllMethods, LIBRARY),
                                                      getTypeManager(X10_AllFields, LIBRARY)));
    this.fScopedTypeToManager.put(new Pair<String, String>(X10_AllTypes, RUNTIME), 
                                  new AllTypesManager(instantiate(allTypes, scopeType, runtime),
                                                      getTypeManager(X10_AllMethods, RUNTIME),
                                                      getTypeManager(X10_AllFields, RUNTIME)));
    
    this.fScopedTypeToManager.put(new Pair<String, String>(X10_TypeHierarchy, APPLICATION), 
                                  new TypeHierarchyManager(instantiate(typeHierarchy, scopeType, application),
                                                           getTypeManager(X10_AllTypes, APPLICATION)));
    this.fScopedTypeToManager.put(new Pair<String, String>(X10_TypeHierarchy, LIBRARY), 
                                  new TypeHierarchyManager(instantiate(typeHierarchy, scopeType, library),
                                                           getTypeManager(X10_AllTypes, LIBRARY)));
    this.fScopedTypeToManager.put(new Pair<String, String>(X10_TypeHierarchy, RUNTIME), 
                                  new TypeHierarchyManager(instantiate(typeHierarchy, scopeType, runtime),
                                                           getTypeManager(X10_AllTypes, RUNTIME)));
  }
  
  private Type instantiate(final Type parametricType, final Type scopeType, final Type boundType) {
    final Map<Type, Type> bindings = new HashMap<Type, Type>(1);
    bindings.put(scopeType, boundType);
    return parametricType.instantiate(bindings);
  }
  
  // --- Fields
  
  private static SearchDBTypes fInstance;
  
  private final TypeStore fTypeStore;
  
  private final Map<Pair<String, String>, ITypeManager> fScopedTypeToManager;

}

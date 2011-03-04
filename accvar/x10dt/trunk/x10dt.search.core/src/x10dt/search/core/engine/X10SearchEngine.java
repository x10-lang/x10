/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.search.core.engine;

import static x10dt.search.core.pdb.X10FactTypeNames.APPLICATION;
import static x10dt.search.core.pdb.X10FactTypeNames.LIBRARY;
import static x10dt.search.core.pdb.X10FactTypeNames.RUNTIME;
import static x10dt.search.core.pdb.X10FactTypeNames.X10_AllFields;
import static x10dt.search.core.pdb.X10FactTypeNames.X10_AllMethods;
import static x10dt.search.core.pdb.X10FactTypeNames.X10_AllTypes;
import static x10dt.search.core.pdb.X10FactTypeNames.X10_FieldRefs;
import static x10dt.search.core.pdb.X10FactTypeNames.X10_MethodRefs;
import static x10dt.search.core.pdb.X10FactTypeNames.X10_MethodToTypeRefs;
import static x10dt.search.core.pdb.X10FactTypeNames.X10_TypeToTypeRefs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.imp.model.ModelFactory.ModelException;
import org.eclipse.imp.pdb.facts.IInteger;
import org.eclipse.imp.pdb.facts.IList;
import org.eclipse.imp.pdb.facts.ISet;
import org.eclipse.imp.pdb.facts.ISourceLocation;
import org.eclipse.imp.pdb.facts.IString;
import org.eclipse.imp.pdb.facts.ITuple;
import org.eclipse.imp.pdb.facts.IValue;
import org.eclipse.imp.pdb.facts.db.FactBase;
import org.eclipse.imp.pdb.facts.db.IFactContext;
import org.eclipse.imp.pdb.facts.db.context.WorkspaceContext;

import x10dt.search.core.SearchCoreActivator;
import x10dt.search.core.elements.IFieldInfo;
import x10dt.search.core.elements.IMethodInfo;
import x10dt.search.core.elements.ITypeInfo;
import x10dt.search.core.engine.scope.IX10SearchScope;
import x10dt.search.core.engine.scope.SearchScopeFactory;
import x10dt.search.core.engine.scope.X10SearchScope;
import x10dt.search.core.utils.FactBaseUtils;
import x10dt.search.core.utils.PDBValueUtils;
import x10dt.ui.launch.core.utils.ICountableIterable;
import x10dt.ui.launch.core.utils.IFilter;


/**
 * Searches for X10 elements following a search pattern. The search can also be limited to a search scope.
 * 
 * @author egeay
 */
public final class X10SearchEngine {
  
  // --- Public services
  
  /**
   * Finds all the methods that use the particular field transmitted within the given scope.
   * 
   * @param searchScope The search scope for all the referencing methods.
   * @param fieldInfo The field of interest.
   * @param searchResultObserver The observer to use for notifying the search results.
   * @param monitor The progress monitor to use to report progress or cancel the operation. Can be <b>null</b>
   * @throws InterruptedException Occurs if the operation get canceled.
   * @throws ModelException Occurs if we could not build the type hierarchy because the project does not exist.
   */
  public static void collectFieldUses(final IX10SearchScope searchScope, final IFieldInfo fieldInfo,
                                      final ISearchResultObserver searchResultObserver,
                                      final IProgressMonitor monitor) throws InterruptedException, ModelException {
    final FactBase factBase = SearchCoreActivator.getIndexer().getFactBase();
    final SubMonitor subMonitor = SubMonitor.convert(monitor);
    try {
      final ICountableIterable<IFactContext> searchContexts = searchScope.createSearchContexts();
      subMonitor.beginTask(null, 2 * searchContexts.getSize() + 1);
      
      ISet[] allTypeSets = null;
      for (final IFactContext context : searchContexts) {
        if (subMonitor.isCanceled()) {
          throw new InterruptedException();
        }
        allTypeSets = getAllTypeSets(factBase, context, subMonitor);
        if ((searchScope.getSearchMask() & X10SearchScope.APPLICATION) != 0) {
          final ISet allMethods = FactBaseUtils.getFactBaseSetValue(factBase, context, X10_AllMethods, APPLICATION, 
                                                                    subMonitor);
          collectFieldUses(FactBaseUtils.getFactBaseSetValue(factBase, context, X10_FieldRefs, APPLICATION, subMonitor),
                           allTypeSets, X10SearchScope.APPLICATION >> 1, allMethods, searchResultObserver, fieldInfo, 
                           subMonitor);
        }
        
        if (subMonitor.isCanceled()) {
          throw new InterruptedException();
        }
        if ((searchScope.getSearchMask() & X10SearchScope.LIBRARY) != 0) {
          final ISet allMethods = FactBaseUtils.getFactBaseSetValue(factBase, context, X10_AllMethods, LIBRARY, subMonitor);
          collectFieldUses(FactBaseUtils.getFactBaseSetValue(factBase, context, X10_FieldRefs, LIBRARY, subMonitor),
                           allTypeSets, X10SearchScope.LIBRARY >> 1, allMethods, searchResultObserver, fieldInfo, subMonitor);
        }
      }
      
      if ((searchScope.getSearchMask() & X10SearchScope.RUNTIME) != 0) {
        if (subMonitor.isCanceled()) {
          throw new InterruptedException();
        }
        final ISet allMethods = FactBaseUtils.getFactBaseSetValue(factBase, WorkspaceContext.getInstance(), X10_AllMethods, 
                                                                  RUNTIME, subMonitor);
        collectFieldUses(FactBaseUtils.getFactBaseSetValue(factBase, WorkspaceContext.getInstance(), X10_FieldRefs, 
                                                           RUNTIME, subMonitor),
                         allTypeSets, X10SearchScope.RUNTIME >> 1, allMethods, searchResultObserver, fieldInfo, subMonitor);
      }
    } finally {
      subMonitor.done();
    }
  }
  
  /**
   * Finds all the methods that call/use the particular one transmitted within the given scope.
   * 
   * @param searchScope The search scope for all the referencing methods.
   * @param methodInfo The method of interest.
   * @param searchResultObserver The observer to use for notifying the search results.
   * @param monitor The progress monitor to use to report progress or cancel the operation. Can be <b>null</b>
   * @throws InterruptedException Occurs if the operation get canceled.
   * @throws ModelException Occurs if we could not build the type hierarchy because the project does not exist.
   */
  public static void collectMethodUses(final IX10SearchScope searchScope, final IMethodInfo methodInfo,
                                       final ISearchResultObserver searchResultObserver,
                                       final IProgressMonitor monitor) throws InterruptedException, ModelException {
    final FactBase factBase = SearchCoreActivator.getIndexer().getFactBase();
    final SubMonitor subMonitor = SubMonitor.convert(monitor);
    final ITypeHierarchy typeHierarchy = createTypeHierarchy(searchScope, methodInfo.getDeclaringType(), subMonitor);
    try {
      final ICountableIterable<IFactContext> searchContexts = searchScope.createSearchContexts();
      subMonitor.beginTask(null, 2 * searchContexts.getSize() + 1);
      
      ISet[] allTypeSets = null;
      for (final IFactContext context : searchContexts) {
        if (subMonitor.isCanceled()) {
          throw new InterruptedException();
        }
        allTypeSets = getAllTypeSets(factBase, context, subMonitor);
        if ((searchScope.getSearchMask() & X10SearchScope.APPLICATION) != 0) {
          final ISet allMethods = FactBaseUtils.getFactBaseSetValue(factBase, context, X10_AllMethods, APPLICATION, 
                                                                    subMonitor);
          collectMethodUses(FactBaseUtils.getFactBaseSetValue(factBase, context, X10_MethodRefs, APPLICATION, subMonitor),
                               allTypeSets, context, X10SearchScope.APPLICATION >> 1, allMethods, searchResultObserver, 
                               methodInfo, typeHierarchy, subMonitor);
        }
        
        if (subMonitor.isCanceled()) {
          throw new InterruptedException();
        }
        if ((searchScope.getSearchMask() & X10SearchScope.LIBRARY) != 0) {
          final ISet allMethods = FactBaseUtils.getFactBaseSetValue(factBase, context, X10_AllMethods, LIBRARY, subMonitor);
          collectMethodUses(FactBaseUtils.getFactBaseSetValue(factBase, context, X10_MethodRefs, LIBRARY, subMonitor),
                               allTypeSets, context, X10SearchScope.LIBRARY >> 1, allMethods, searchResultObserver, 
                               methodInfo, typeHierarchy, subMonitor);
        }
      }
      
      if ((searchScope.getSearchMask() & X10SearchScope.RUNTIME) != 0) {
        if (subMonitor.isCanceled()) {
          throw new InterruptedException();
        }
        final ISet allMethods = FactBaseUtils.getFactBaseSetValue(factBase, WorkspaceContext.getInstance(), X10_AllMethods, 
                                                                  RUNTIME, subMonitor);
        collectMethodUses(FactBaseUtils.getFactBaseSetValue(factBase, WorkspaceContext.getInstance(), X10_MethodRefs, 
                                                               RUNTIME, subMonitor),
                             allTypeSets, WorkspaceContext.getInstance(), X10SearchScope.RUNTIME >> 1, allMethods, 
                             searchResultObserver, methodInfo, typeHierarchy, subMonitor);
      }
    } finally {
      subMonitor.done();
    }
  }
  
  /**
   * Finds all the methods/fields that use the particular type transmitted within the given scope.
   * 
   * @param searchScope The search scope for all the referencing methods.
   * @param typeInfo The type of interest.
   * @param searchResultObserver The observer to use for notifying the search results.
   * @param monitor The progress monitor to use to report progress or cancel the operation. Can be <b>null</b>.
   * @throws InterruptedException Occurs if the operation get canceled.
   * @throws ModelException Occurs if we could not build the type hierarchy because the project does not exist.
   */
  public static void collectTypeUses(final IX10SearchScope searchScope, final ITypeInfo typeInfo,
                                     final ISearchResultObserver searchResultObserver,
                                     final IProgressMonitor monitor) throws InterruptedException, ModelException {
    final FactBase factBase = SearchCoreActivator.getIndexer().getFactBase();
    final SubMonitor subMonitor = SubMonitor.convert(monitor);
    try {
      final ICountableIterable<IFactContext> searchContexts = searchScope.createSearchContexts();
      subMonitor.beginTask(null, 2 * searchContexts.getSize() + 1);
      
      ISet[] allTypeSets = null;
      for (final IFactContext context : searchContexts) {
        if (subMonitor.isCanceled()) {
          throw new InterruptedException();
        }
        allTypeSets = getAllTypeSets(factBase, context, subMonitor);
        if ((searchScope.getSearchMask() & X10SearchScope.APPLICATION) != 0) {
          final ISet allMethods = FactBaseUtils.getFactBaseSetValue(factBase, context, X10_AllMethods, APPLICATION, 
                                                                    subMonitor);
          collectTypeUses(FactBaseUtils.getFactBaseSetValue(factBase, context, X10_MethodToTypeRefs, APPLICATION, subMonitor),
                          FactBaseUtils.getFactBaseSetValue(factBase, context, X10_TypeToTypeRefs, APPLICATION, subMonitor),
                          X10SearchScope.APPLICATION >> 1, allTypeSets, allMethods, searchResultObserver, typeInfo, 
                          subMonitor);
        }
        
        if (subMonitor.isCanceled()) {
          throw new InterruptedException();
        }
        if ((searchScope.getSearchMask() & X10SearchScope.LIBRARY) != 0) {
          final ISet allMethods = FactBaseUtils.getFactBaseSetValue(factBase, context, X10_AllMethods, LIBRARY, subMonitor);
          collectTypeUses(FactBaseUtils.getFactBaseSetValue(factBase, context, X10_MethodToTypeRefs, LIBRARY, subMonitor),
                          FactBaseUtils.getFactBaseSetValue(factBase, context, X10_TypeToTypeRefs, LIBRARY, subMonitor),
                          X10SearchScope.LIBRARY >> 1, allTypeSets, allMethods, searchResultObserver, typeInfo, 
                          subMonitor);
        }
      }
      
      if ((searchScope.getSearchMask() & X10SearchScope.RUNTIME) != 0) {
        if (subMonitor.isCanceled()) {
          throw new InterruptedException();
        }
        final ISet allMethods = FactBaseUtils.getFactBaseSetValue(factBase, WorkspaceContext.getInstance(), X10_AllMethods, 
                                                                  RUNTIME, subMonitor);
        collectTypeUses(FactBaseUtils.getFactBaseSetValue(factBase, WorkspaceContext.getInstance(), X10_MethodToTypeRefs, 
                                                          RUNTIME, subMonitor),
                        FactBaseUtils.getFactBaseSetValue(factBase, WorkspaceContext.getInstance(), X10_TypeToTypeRefs, 
                                                          RUNTIME, subMonitor),
                        X10SearchScope.RUNTIME >> 1, allTypeSets, allMethods, searchResultObserver, typeInfo, 
                        subMonitor);
      }
    } finally {
      subMonitor.done();
    }
  }
  
  /**
   * Creates a type hierarchy for the given type information transmitted.
   * 
   * <p>Note that there is no refresh action at this point. If one wants an updated version of it, a new one needs to be
   * created.
   * 
   * <p>Such method <b>must</b> be executed <b>outside of the UI thread</b>.
   * 
   * @param searchScope The scope of the search. See {@link SearchScopeFactory}.
   * @param typeInfo The type info to consider for the type hierarchy.
   * @param monitor The monitor to use to report progress or cancel the operation.
   * @return A non-null instance of {@link ITypeHierarchy}.
   * @throws ModelException Occurs if the project does not exist.
   * @throws InterruptedException Occurs if the operation gets canceled.
   */
  public static ITypeHierarchy createTypeHierarchy(final IX10SearchScope searchScope, final ITypeInfo typeInfo,
                                                   final IProgressMonitor monitor) throws ModelException, 
                                                                                          InterruptedException {
    return new TypeHierarchy(searchScope, typeInfo, monitor);
  }
  
  /**
   * Returns all the field information that matches the Java regular expression identifying a potential set of field names
   * for a given type.
   * 
   * <p>Such method <b>must</b> be executed <b>outside of the UI thread</b>.
   * 
   * @param searchScope The scope of the search. See {@link SearchScopeFactory}.
   * @param typeInfo The type information that is needed to locate the field.
   * @param fieldNameRegEx The regular expression identifying the field names to search for.
   * @param isCaseSensitive Indicates if the pattern matching is case sensitive or not.
   * @param monitor The monitor to use to report progress or cancel the operation.
   * @return A non-null, but possibly empty (if no match was found), array of field information.
   * @throws InterruptedException Occurs if the operation gets canceled.
   */
  public static IFieldInfo[] getAllMatchingFieldInfo(final IX10SearchScope searchScope, final ITypeInfo typeInfo, 
                                                     final String fieldNameRegEx, final boolean isCaseSensitive,
                                                     final IProgressMonitor monitor) throws InterruptedException {
    return getFieldInfo(searchScope, typeInfo, new PatternFilter(fieldNameRegEx, isCaseSensitive), monitor);
  }
  
  /**
   * Returns all the field information that matches the Java regular expression identifying a potential set of field names
   * independently of the declaring type.
   * 
   * <p>Such method <b>must</b> be executed <b>outside of the UI thread</b>.
   * 
   * @param searchScope The scope of the search. See {@link SearchScopeFactory}.
   * @param fieldNameRegEx The regular expression identifying the field names to search for.
   * @param isCaseSensitive Indicates if the pattern matching is case sensitive or not.
   * @param monitor The monitor to use to report progress or cancel the operation.
   * @return A non-null, but possibly empty (if no match was found), array of field information.
   * @throws InterruptedException Occurs if the operation gets canceled.
   */
  public static IFieldInfo[] getAllMatchingFieldInfo(final IX10SearchScope searchScope, 
                                                     final String fieldNameRegEx, final boolean isCaseSensitive,
                                                     final IProgressMonitor monitor) throws InterruptedException {
    return getFieldInfo(searchScope, null, new PatternFilter(fieldNameRegEx, isCaseSensitive), monitor);
  }
  
  /**
   * Returns all the method information that matches the Java regular expression identifying a potential set of method names
   * for a given type.
   * 
   * <p>Such method <b>must</b> be executed <b>outside of the UI thread</b>.
   * 
   * @param searchScope The scope of the search. See {@link SearchScopeFactory}.
   * @param typeInfo The type information that is needed to locate the method.
   * @param methodNameRegEx The regular expression identifying the method names to search for.
   * @param isCaseSensitive Indicates if the pattern matching is case sensitive or not.
   * @param monitor The monitor to use to report progress or cancel the operation.
   * @return A non-null, but possibly empty (if no match was found), array of method information.
   * @throws InterruptedException Occurs if the operation gets canceled.
   */
  public static IMethodInfo[] getAllMatchingMethodInfo(final IX10SearchScope searchScope, final ITypeInfo typeInfo, 
                                                       final String methodNameRegEx, final boolean isCaseSensitive,
                                                       final IProgressMonitor monitor) throws InterruptedException {
    return getMethodInfos(searchScope, typeInfo, new PatternFilter(methodNameRegEx, isCaseSensitive), monitor);
  }
  
  /**
   * Returns all the method information that matches the Java regular expression identifying a potential set of method names
   * independently of the declaring type.
   * 
   * <p>Such method <b>must</b> be executed <b>outside of the UI thread</b>.
   * 
   * @param searchScope The scope of the search. See {@link SearchScopeFactory}.
   * @param methodNameRegEx The regular expression identifying the method names to search for.
   * @param isCaseSensitive Indicates if the pattern matching is case sensitive or not.
   * @param monitor The monitor to use to report progress or cancel the operation.
   * @return A non-null, but possibly empty (if no match was found), array of method information.
   * @throws InterruptedException Occurs if the operation gets canceled.
   */
  public static IMethodInfo[] getAllMatchingMethodInfo(final IX10SearchScope searchScope, 
                                                       final String methodNameRegEx, final boolean isCaseSensitive,
                                                       final IProgressMonitor monitor) throws InterruptedException {
    return getMethodInfos(searchScope, null, new PatternFilter(methodNameRegEx, isCaseSensitive), monitor);
  }
  
  /**
   * Returns all the type information that matches the Java regular expression identifying a potential set of type names.
   * 
   * <p>Such method <b>must</b> be executed <b>outside of the UI thread</b>.
   * 
   * @param searchScope The scope of the search. See {@link SearchScopeFactory}.
   * @param typeNameRegEx The regular expression identifying the type names to search for.
   * @param isCaseSensitive Indicates if the pattern matching is case sensitive or not.
   * @param monitor The monitor to use to report progress or cancel the operation.
   * @return A non-null, but possibly empty (if no match was found), array of type information.
   * @throws InterruptedException Occurs if the operation gets canceled.
   */
  public static ITypeInfo[] getAllMatchingTypeInfo(final IX10SearchScope searchScope, final String typeNameRegEx,
                                                   final boolean isCaseSensitive,
                                                   final IProgressMonitor monitor) throws InterruptedException {
    return getTypeInfo(searchScope, new TypePatternFilter(typeNameRegEx, isCaseSensitive), monitor);
  }
  
  /**
   * Returns the field info wrapper for a given type and field name.
   * 
   * <p>Such method <b>must</b> be executed <b>outside of the UI thread</b>.
   * 
   * @param searchScope The scope of the search. See {@link SearchScopeFactory}.
   * @param typeInfo The type information that is needed to locate the field.
   * @param fieldName The field name to look for.
   * @param monitor The progress monitor to use to report progress or cancel the operation. Can be <b>null</b>.
   * @return A non-null {@link IFieldInfo} if it is found, otherwise <b>null</b>.
   * @throws InterruptedException Occurs if the operation get canceled.
   */
  public static IFieldInfo getFieldInfo(final IX10SearchScope searchScope, final ITypeInfo typeInfo, final String fieldName,
                                        final IProgressMonitor monitor) throws InterruptedException {
    final IFieldInfo[] fieldInfos = getFieldInfo(searchScope, typeInfo, new EqualsFilter(fieldName), monitor);
    return (fieldInfos.length == 0) ? null : fieldInfos[0];
  }
  
  /**
   * Returns the method info wrappers for a given type and method name.
   * 
   * <p>Such method <b>must</b> be executed <b>outside of the UI thread</b>.
   * 
   * @param searchScope The scope of the search. See {@link SearchScopeFactory}.
   * @param typeInfo The type information that is needed to locate the method.
   * @param methodName The method name to look for.
   * @param monitor The progress monitor to use to report progress or cancel the operation. Can be <b>null</b>.
   * @return A non-null, but possibly empty (if no match was found), array of method information.
   * @throws InterruptedException Occurs if the operation get canceled.
   */
  public static IMethodInfo[] getMethodInfos(final IX10SearchScope searchScope, final ITypeInfo typeInfo, 
                                             final String methodName, 
                                             final IProgressMonitor monitor) throws InterruptedException {
    return getMethodInfos(searchScope, typeInfo, new EqualsFilter(methodName), monitor);
  }

  /**
   * Returns the type info wrapper for a given type name.
   * 
   * <p>Such method <b>must</b> be executed <b>outside of the UI thread</b>.
   * 
   * @param searchScope The scope of the search. See {@link SearchScopeFactory}.
   * @param typeName The type name to look for.
   * @param monitor The progress monitor to use to report progress or cancel the operation. Can be <b>null</b>.
   * @return A non-null but possibly empty array if we haven't found the type in the particular scope.
   * @throws InterruptedException Occurs if the operation get canceled.
   */
  public static ITypeInfo[] getTypeInfo(final IX10SearchScope searchScope, final String typeName,
                                        final IProgressMonitor monitor) throws InterruptedException {
    return getTypeInfo(searchScope, new EqualsFilter(typeName), monitor);
  }
  
  // --- Internal services

  static ITypeInfo findTypeInfo(final ISet allTypes, final ITuple typeTuple) {
    if ((allTypes != null) && allTypes.contains(typeTuple)) {
      if (typeTuple.arity() < 4) {
        return new TypeInfo(typeTuple, null);
      } else {
        return new TypeInfo(typeTuple, findDeclaringTypeInfo(allTypes, ((IString) typeTuple.get(3)).getValue(), 
                                                             (ISourceLocation) typeTuple.get(1)));
      }
    } else {
      return null;
    }
  }
  
  // --- Private code
  
  private static void collectFieldInfo(final Collection<IFieldInfo> fieldInfos, final ISet allFields, final ITypeInfo typeInfo,
                                       final IFilter<String> filter, final ISet[] allTypes, final int scopeIndex,
                                       final IProgressMonitor monitor) throws InterruptedException {
    if (allFields != null) {
      for (final IValue value : allFields) {
        final ITuple tuple = (ITuple) value;
        if ((typeInfo == null) || isSameType((ITuple) tuple.get(0), typeInfo)) {
          final IList list = (IList) tuple.get(1);
          for (final IValue listElement : list) {
            final ITuple fieldTuple = (ITuple) listElement;
            if (filter.accepts(((IString) fieldTuple.get(1)).getValue())) {
              final ITypeInfo type = (typeInfo == null) ? findTypeInfo(allTypes, scopeIndex, (ITuple) tuple.get(0), 
                                                                       monitor) : typeInfo;
              if (type != null) {
                fieldInfos.add(createFieldInfo(fieldTuple, allTypes, scopeIndex, type, monitor));
              }
            }
          }
        }
      }
    }
  }
  
  private static void collectFieldUses(final ISet fieldRfsSet, final ISet[] allTypesSets, final int scopeIndex,
                                       final ISet allMethodsSet, final ISearchResultObserver searchResultObserver,
                                       final IFieldInfo fieldInfo, 
                                       final IProgressMonitor monitor) throws InterruptedException {
    if ((fieldRfsSet != null) && (allMethodsSet != null)) {
      final ITuple targetField = PDBValueUtils.convert(fieldInfo);
      for (final IValue value : fieldRfsSet) {
        final ITuple tuple = (ITuple) value;
        final ISet references = (ISet) tuple.get(1);
        final ITuple finding = findTupleInReferences(references, targetField);
        if (finding != null) {
          final ITuple foundMethod = (ITuple) tuple.get(0);

          for (final IValue typeToMethodsValue : allMethodsSet) {
            final ITuple typeToMethods = (ITuple) typeToMethodsValue;
            if (((IList) typeToMethods.get(1)).contains(foundMethod)) {
              final ITypeInfo declaringType = findTypeInfo(allTypesSets[scopeIndex], (ITuple) typeToMethods.get(0));
              searchResultObserver.accept(createMethodInfo(foundMethod, allTypesSets, scopeIndex, declaringType, monitor),
                                          (ISourceLocation) finding.get(1));
              break;
            }
          }
        }
      }
    }
  }
  
  private static void collectMethodInfo(final Collection<IMethodInfo> methodInfos, final ISet allMethods, 
                                        final ITypeInfo typeInfo,  final IFilter<String> filter,
                                        final ISet[] allTypes, final int scopeIndex,
                                        final IProgressMonitor monitor) throws InterruptedException {
    if (allMethods != null) {
      for (final IValue value : allMethods) {
        final ITuple tuple = (ITuple) value;
        if ((typeInfo == null) || isSameType((ITuple) tuple.get(0), typeInfo)) {
          final IList list = (IList) tuple.get(1);
          for (final IValue listElement : list) {
            final ITuple methodTuple = (ITuple) listElement;
            if (filter.accepts(((IString) methodTuple.get(1)).getValue())) {
              final ITypeInfo type = (typeInfo == null) ? findTypeInfo(allTypes, scopeIndex, (ITuple) tuple.get(0), 
                                                                       monitor) : typeInfo;
              if (type != null) {
                methodInfos.add(createMethodInfo(methodTuple, allTypes, scopeIndex, type, monitor));
              }
            }
          }
        }
      }
    }
  }
  
  private static void collectMethodUses(final ISet methodRfsSet, final ISet[] allTypesSets, final IFactContext context, 
                                        final int scopeIndex, final ISet allMethodsSet, 
                                        final ISearchResultObserver searchResultObserver, final IMethodInfo methodInfo, 
                                        final ITypeHierarchy typeHierarchy,
                                        final IProgressMonitor monitor) throws InterruptedException {
    if ((methodRfsSet != null) && (allMethodsSet != null)) {
      final Collection<ITuple> methods = collectSameMethodsFromTH(typeHierarchy, scopeIndex, context, methodInfo, monitor);

      for (final IValue value : methodRfsSet) {
        final ITuple tuple = (ITuple) value;
        final ISet references = (ISet) tuple.get(1);
        
        ITuple finding = null;
        for (final ITuple method : methods) {
          finding = findTupleInReferences(references, method);
          if (finding != null) {
            break;
          }
        }
        if (finding != null) {
          final ITuple foundMethod = (ITuple) tuple.get(0);

          for (final IValue typeToMethodsValue : allMethodsSet) {
            final ITuple typeToMethods = (ITuple) typeToMethodsValue;
            if (((IList) typeToMethods.get(1)).contains(foundMethod)) {
              final ITypeInfo declaringType = findTypeInfo(allTypesSets[scopeIndex], (ITuple) typeToMethods.get(0));
              searchResultObserver.accept(createMethodInfo(foundMethod, allTypesSets, scopeIndex, declaringType, monitor),
                                          (ISourceLocation) finding.get(1));
              break;
            }
          }
        }
      }
    }
  }
  
  private static Collection<ITuple> collectSameMethodsFromTH(final ITypeHierarchy typeHierarchy, final int scopeIndex,
                                                             final IFactContext context, final IMethodInfo methodInfo,
                                                             final IProgressMonitor monitor) throws InterruptedException {
    final Collection<ITuple> methods = new ArrayList<ITuple>();
    final ITuple targetMethod = PDBValueUtils.convert(methodInfo);
    methods.add(targetMethod);
    
    final ISet[] allMethods = getAllMethodSets(SearchCoreActivator.getIndexer().getFactBase(), context, monitor);
    
    for (final ITypeInfo superType : typeHierarchy.getSuperTypes(methodInfo.getDeclaringType())) {
      final ITuple method = collectSameMethodsFromTH(superType, scopeIndex, targetMethod, allMethods, monitor);
      if (method != null) {
        methods.add(method);
      }
    }
    for (final ITypeInfo subType : typeHierarchy.getSubTypes(methodInfo.getDeclaringType())) {
      final ITuple method = collectSameMethodsFromTH(subType, scopeIndex, targetMethod, allMethods, monitor);
      if (method != null) {
        methods.add(method);
      }
    }
    
    return methods;
  }
  
  private static ITuple collectSameMethodsFromTH(final ITypeInfo typeInfo, final int scopeIndex,
                                                 final ITuple targetMethod, final ISet[] allMethods,
                                                 final IProgressMonitor monitor) throws InterruptedException {
    final ITuple type = PDBValueUtils.convert(typeInfo);
    for (int i = scopeIndex; i < allMethods.length; ++i) {
      if (allMethods[i] != null) {
        for (final IValue typeToMethodsValue : allMethods[i]) {
          if (monitor.isCanceled()) {
            throw new InterruptedException();
          }
          final ITuple typeToMethods = (ITuple) typeToMethodsValue;
          if (type.equals(typeToMethods.get(0))) {
            // We found the type, let's try to look for the the method.
            for (final IValue methodValue : (IList) typeToMethods.get(1)) {
              final ITuple method = (ITuple) methodValue;
              if (targetMethod.get(1).equals(method.get(1))) { // Names are equal
                final IList targetParams = (IList) targetMethod.get(3);
                final IList curParams = (IList) method.get(3);
                if (targetParams.length() == curParams.length()) {
                  final Iterator<IValue> targetIt = targetParams.iterator();
                  final Iterator<IValue> curIt = curParams.iterator();
                  boolean hasSameParams = true;
                  while (targetIt.hasNext()) {
                    if (!targetIt.next().equals(curIt.next())) {
                      hasSameParams = false;
                      break;
                    }
                  }
                  if (hasSameParams) {
                    return method;
                  }
                }
              }
            }
          }
        }
      }
    }
    return null;
  }
  
  private static void collectTypeInfo(final Collection<ITypeInfo> typeInfos, final ISet allTypes, 
                                      final IFilter<String> filter,  final String scopeTypeName, 
                                      final IX10SearchScope searchScope,  
                                      final IProgressMonitor monitor) throws InterruptedException {
    if (allTypes != null) {
      for (final IValue value : allTypes) {
        if (monitor.isCanceled()) {
          throw new InterruptedException();
        }
        final ITuple tuple = (ITuple) value;
        final String name = ((IString) tuple.get(0)).getValue();
        final ISourceLocation sourceLoc = (ISourceLocation) tuple.get(1);
        final int x10FlagsCode = ((IInteger) tuple.get(2)).intValue();
        if (filter.accepts(name) && ((scopeTypeName == RUNTIME) || searchScope.contains(sourceLoc.getURI().toString()))) {
          final ITypeInfo declaringType;
          if (tuple.arity() < 4) {
            declaringType = null;
          } else {
            final IString declaringTypeName = (IString) tuple.get(3);
            declaringType = findDeclaringTypeInfo(allTypes, declaringTypeName.getValue(), sourceLoc);
          }
          typeInfos.add(new TypeInfo(name, sourceLoc, x10FlagsCode, declaringType));
        }
      }
    }
  }
  
  private static void collectTypeUses(final ISet methodToTypeRefs, final ISet typeToTypeRefs, final int scopeIndex, 
                                      final ISet[] allTypes, final ISet allMethods, 
                                      final ISearchResultObserver searchResultObserver,
                                      final ITypeInfo typeInfo, final IProgressMonitor monitor) throws InterruptedException {
    final ITuple targetType = PDBValueUtils.convert(typeInfo);
    
    if ((methodToTypeRefs != null) && (allMethods != null)) {
      for (final IValue value : methodToTypeRefs) {
        final ITuple tuple = (ITuple) value;
        final ISet references = (ISet) tuple.get(1);
        final ITuple finding = findTupleInReferences(references, targetType);
        if (finding != null) {
          final ITuple foundMethod = (ITuple) tuple.get(0);

          for (final IValue typeToMethodsValue : allMethods) {
            final ITuple typeToMethods = (ITuple) typeToMethodsValue;
            if (((IList) typeToMethods.get(1)).contains(foundMethod)) {
              final ITypeInfo declaringType = findTypeInfo(allTypes[scopeIndex], (ITuple) typeToMethods.get(0));
              searchResultObserver.accept(createMethodInfo(foundMethod, allTypes, scopeIndex, declaringType, monitor),
                                          (ISourceLocation) finding.get(1));
              break;
            }
          }
        }
      }
    }
    
    if (typeToTypeRefs != null) {
      for (final IValue value : typeToTypeRefs) {
        final ITuple tuple = (ITuple) value;
        final ISet references = (ISet) tuple.get(1);
        final ITuple finding = findTupleInReferences(references, targetType);
        if (finding != null) {
          searchResultObserver.accept(findTypeInfo(allTypes, scopeIndex, (ITuple) tuple.get(0), monitor), 
                                      (ISourceLocation) finding.get(1));
        }
      }
    }
  }
  
  private static IFieldInfo createFieldInfo(final ITuple tuple, final ISet[] allTypes, final int scopeIndex, 
                                            final ITypeInfo declaringTypeInfo,
                                            final IProgressMonitor monitor) throws InterruptedException {
    ITypeInfo fieldTypeInfo = findTypeInfo(allTypes, scopeIndex, (ITuple) tuple.get(2), monitor);
    if (fieldTypeInfo == null) {
      fieldTypeInfo = new UnknownTypeInfo(((IString) ((ITuple) tuple.get(2)).get(0)).getValue());
    }
    return new FieldInfo(((IString) tuple.get(1)).getValue(), fieldTypeInfo, (ISourceLocation) tuple.get(0), 
                         ((IInteger) tuple.get(3)).intValue(), declaringTypeInfo);
  }
  
  private static IMethodInfo createMethodInfo(final ITuple tuple, final ISet[] allTypes, final int scopeIndex, 
                                              final ITypeInfo declaringTypeInfo,
                                              final IProgressMonitor monitor) throws InterruptedException {
    ITypeInfo returnTypeInfo = findTypeInfo(allTypes, scopeIndex, (ITuple) tuple.get(2), monitor);
    if (returnTypeInfo == null) {
      returnTypeInfo = new UnknownTypeInfo(((IString) ((ITuple) tuple.get(2)).get(0)).getValue());
    }
    final IList parameters = (IList) tuple.get(3);
    final ITypeInfo[] paramInfos = new ITypeInfo[parameters.length()];
    int i = -1;
    for (final IValue paramValue : parameters) {
      paramInfos[++i] = findTypeInfo(allTypes, scopeIndex, (ITuple) paramValue, monitor);
      if (paramInfos[i] == null) {
        paramInfos[i] = new UnknownTypeInfo(((IString) (((ITuple) paramValue).get(0))).getValue());
      }
    }
    return new MethodInfo(((IString) tuple.get(1)).getValue(), returnTypeInfo, paramInfos, (ISourceLocation) tuple.get(0), 
                          ((IInteger) tuple.get(4)).intValue(), declaringTypeInfo);
  }
  
  private static ITypeInfo findDeclaringTypeInfo(final ISet allTypes, final String typeName,
                                                 final ISourceLocation sourceLocation) {
    for (final IValue value : allTypes) {
      final ITuple tuple = (ITuple) value;
      if (((IString) tuple.get(0)).getValue().equals(typeName) && 
           hasOverlappingLocation((ISourceLocation) tuple.get(1), sourceLocation)) {
        final ITypeInfo declaringType;
        if (tuple.arity() < 4) {
          declaringType = null;
        } else {
          final IString declaringTypeName = (IString) tuple.get(3);
          declaringType = findDeclaringTypeInfo(allTypes, declaringTypeName.getValue(), 
                                                (ISourceLocation) tuple.get(1));
        }
        return new TypeInfo(tuple, declaringType);
      }
    }
    return null;
  }
  
  private static ITuple findTupleInReferences(final ISet references, final ITuple targetTuple) {
    for (final IValue reference : references) {
      final ITuple refTuple = (ITuple) reference;
      if (refTuple.get(0).equals(targetTuple)) {
        return refTuple;
      }
    }
    return null;
  }
  
  private static ITypeInfo findTypeInfo(final ISet[] allTypes, final int scopeIndex, final ITuple typeTuple,
                                        final IProgressMonitor monitor) throws InterruptedException {
    final String typeName = ((IString) typeTuple.get(0)).getValue();
    if (typeName.charAt(0) == '[') {
      return new ParameterTypeInfo(typeName.substring(1));
    }
    for (int i = scopeIndex; i <= 2; ++i) {
      if (monitor.isCanceled()) {
        throw new InterruptedException();
      }
      ITypeInfo typeInfo = findTypeInfo(allTypes[i], typeTuple);
      if (typeInfo != null) {
        return typeInfo;
      }
    }
    return null;
  }
  
  private static ISet[] getAllMethodSets(final FactBase factBase, final IFactContext context, 
                                         final IProgressMonitor monitor) throws InterruptedException {
    final ISet[] allSets = new ISet[3];
    allSets[0] = FactBaseUtils.getFactBaseSetValue(factBase, context, X10_AllMethods, APPLICATION, monitor);
    allSets[1] = FactBaseUtils.getFactBaseSetValue(factBase, context, X10_AllMethods, LIBRARY, monitor);
    allSets[2] = FactBaseUtils.getFactBaseSetValue(factBase, WorkspaceContext.getInstance(), X10_AllMethods, RUNTIME, monitor);
    return allSets;
  }
  
  private static ISet[] getAllTypeSets(final FactBase factBase, final IFactContext context, 
                                       final IProgressMonitor monitor) throws InterruptedException {
    final ISet[] allSets = new ISet[3];
    allSets[0] = FactBaseUtils.getFactBaseSetValue(factBase, context, X10_AllTypes, APPLICATION, monitor);
    allSets[1] = FactBaseUtils.getFactBaseSetValue(factBase, context, X10_AllTypes, LIBRARY, monitor);
    allSets[2] = FactBaseUtils.getFactBaseSetValue(factBase, WorkspaceContext.getInstance(), X10_AllTypes, RUNTIME, monitor);
    return allSets;
  }
  
  private static IFieldInfo[] getFieldInfo(final IX10SearchScope searchScope, final ITypeInfo typeInfo, 
                                           final IFilter<String> filter, 
                                           final IProgressMonitor monitor) throws InterruptedException {
    final FactBase factBase = SearchCoreActivator.getIndexer().getFactBase(); 
    final SubMonitor subMonitor = SubMonitor.convert(monitor);
    final Collection<IFieldInfo> fieldInfos = new ArrayList<IFieldInfo>();
    try {
      final ICountableIterable<IFactContext> searchContexts = searchScope.createSearchContexts();
      subMonitor.beginTask(null, 2 * searchContexts.getSize() + 1);
      
      ISet[] allTypeSets = null;
      for (final IFactContext context : searchContexts) {
        if (subMonitor.isCanceled()) {
          throw new InterruptedException();
        }
        allTypeSets = getAllTypeSets(factBase, context, subMonitor);
        if ((searchScope.getSearchMask() & X10SearchScope.APPLICATION) != 0) {
          final ISet allFields = FactBaseUtils.getFactBaseSetValue(factBase, context, X10_AllFields, APPLICATION, subMonitor);
          collectFieldInfo(fieldInfos, allFields, typeInfo, filter, allTypeSets, X10SearchScope.APPLICATION >> 1,
                           subMonitor.newChild(1));
        }
        
        if (subMonitor.isCanceled()) {
          throw new InterruptedException();
        }
        if ((searchScope.getSearchMask() & X10SearchScope.LIBRARY) != 0) {
          final ISet allFields = FactBaseUtils.getFactBaseSetValue(factBase, context, X10_AllFields, LIBRARY, subMonitor);
          collectFieldInfo(fieldInfos, allFields, typeInfo, filter, allTypeSets,  X10SearchScope.LIBRARY >> 1,
                           subMonitor.newChild(1));
        }
      }
      
      if (subMonitor.isCanceled()) {
        throw new InterruptedException();
      }
      if ((allTypeSets != null) && (searchScope.getSearchMask() & X10SearchScope.RUNTIME) != 0) {
        final ISet allFields = FactBaseUtils.getFactBaseSetValue(factBase, WorkspaceContext.getInstance(), X10_AllFields, 
                                                                 RUNTIME, subMonitor);
        collectFieldInfo(fieldInfos, allFields, typeInfo, filter, allTypeSets, X10SearchScope.RUNTIME >> 1,
                         subMonitor.newChild(1));
      }
    } finally {
      subMonitor.done();
    }
    return fieldInfos.toArray(new IFieldInfo[fieldInfos.size()]);
  }
  
  private static IMethodInfo[] getMethodInfos(final IX10SearchScope searchScope, final ITypeInfo typeInfo, 
                                              final IFilter<String> filter, 
                                              final IProgressMonitor monitor) throws InterruptedException {
    final FactBase factBase = SearchCoreActivator.getIndexer().getFactBase();
    final SubMonitor subMonitor = SubMonitor.convert(monitor);
    final Collection<IMethodInfo> methodInfos = new ArrayList<IMethodInfo>();
    try {
      final ICountableIterable<IFactContext> searchContexts = searchScope.createSearchContexts();
      subMonitor.beginTask(null, 2 * searchContexts.getSize() + 1);
      
      ISet[] allTypeSets = null;
      for (final IFactContext context : searchContexts) {
        if (subMonitor.isCanceled()) {
          throw new InterruptedException();
        }
        allTypeSets = getAllTypeSets(factBase, context, subMonitor);
        if ((searchScope.getSearchMask() & X10SearchScope.APPLICATION) != 0) {
          final ISet allMethods = FactBaseUtils.getFactBaseSetValue(factBase, context, X10_AllMethods, APPLICATION, 
                                                                    subMonitor);
          collectMethodInfo(methodInfos, allMethods, typeInfo, filter, allTypeSets, X10SearchScope.APPLICATION >> 1, 
                            subMonitor.newChild(1));
        }
        
        if (subMonitor.isCanceled()) {
          throw new InterruptedException();
        }
        if ((searchScope.getSearchMask() & X10SearchScope.LIBRARY) != 0) {
          final ISet allMethods = FactBaseUtils.getFactBaseSetValue(factBase, context, X10_AllMethods, LIBRARY, subMonitor);
          collectMethodInfo(methodInfos, allMethods, typeInfo, filter, allTypeSets, X10SearchScope.LIBRARY >> 1,
                            subMonitor.newChild(1));
        }
      }
      
      if (subMonitor.isCanceled()) {
        throw new InterruptedException();
      }
      if ((allTypeSets != null) && (searchScope.getSearchMask() & X10SearchScope.RUNTIME) != 0) {
        final ISet allMethods = FactBaseUtils.getFactBaseSetValue(factBase, WorkspaceContext.getInstance(), X10_AllMethods, 
                                                                  RUNTIME, subMonitor);
        collectMethodInfo(methodInfos, allMethods, typeInfo, filter, allTypeSets, X10SearchScope.RUNTIME >> 1,
                          subMonitor.newChild(1));
      }
    } finally {
      subMonitor.done();
    }
    return methodInfos.toArray(new IMethodInfo[methodInfos.size()]);
  }
  
  private static ITypeInfo[] getTypeInfo(final IX10SearchScope searchScope, final IFilter<String> filter,
                                         final IProgressMonitor monitor) throws InterruptedException {
    final FactBase factBase = SearchCoreActivator.getIndexer().getFactBase();
    
    final SubMonitor subMonitor = SubMonitor.convert(monitor);
    final Collection<ITypeInfo> typeInfos = new ArrayList<ITypeInfo>();
    try {      
      final ICountableIterable<IFactContext> searchContexts = searchScope.createSearchContexts();
      subMonitor.beginTask(null, 2 * searchContexts.getSize() + 1);
      
      ISet[] allTypeSets = null;
      for (final IFactContext context : searchContexts) {
        if (subMonitor.isCanceled()) {
          throw new InterruptedException();
        }
        allTypeSets = getAllTypeSets(factBase, context, subMonitor);
        if ((searchScope.getSearchMask() & X10SearchScope.APPLICATION) != 0) {
          collectTypeInfo(typeInfos, allTypeSets[0], filter, APPLICATION, searchScope, subMonitor.newChild(1));
        }
        
        if (subMonitor.isCanceled()) {
          throw new InterruptedException();
        }
        if ((searchScope.getSearchMask() & X10SearchScope.LIBRARY) != 0) {
          collectTypeInfo(typeInfos, allTypeSets[1], filter, LIBRARY, searchScope, subMonitor.newChild(1));
        }
      }
      
      if (subMonitor.isCanceled()) {
        throw new InterruptedException();
      }
      if ((allTypeSets != null) && (searchScope.getSearchMask() & X10SearchScope.RUNTIME) != 0) {
        collectTypeInfo(typeInfos, allTypeSets[2], filter, RUNTIME, searchScope, subMonitor.newChild(1));
      }
    } finally {
      subMonitor.done();
    }
    return typeInfos.toArray(new ITypeInfo[typeInfos.size()]);
  }
  
  private static boolean hasOverlappingLocation(final ISourceLocation container, final ISourceLocation contained) {
    if (container.getURI().equals(contained.getURI())) {
      return (container.getOffset() <= contained.getOffset()) &&
             ((container.getOffset() + container.getLength()) >= (contained.getOffset() + contained.getLength()));
    } else {
      return false;
    }
  }
  
  private static boolean isSameType(final ITuple tuple, final ITypeInfo typeInfo) {
    if (typeInfo.getName().equals(((IString) tuple.get(0)).getValue())) {
      if (typeInfo.getLocation() == null) {
        return (tuple.arity() == 1);
      } else {
        return typeInfo.getLocation().equals(tuple.get(1));
      }
    } else {
      return false;
    }
  }
    
  // --- Private classes
  
  private static final class EqualsFilter implements IFilter<String> {
   
    EqualsFilter(final String base) {
      this.fBase = base;
    }

    // --- Interface methods implementation
    
    public boolean accepts(final String element) {
      return this.fBase.equals(element);
    }
    
    // --- Fields
    
    private final String fBase;
    
  }
  
  private static final class PatternFilter implements IFilter<String> {
    
    PatternFilter(final String regex, final boolean isCaseSensitive) {
      this.fPattern = isCaseSensitive ? Pattern.compile(regex) : Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
    }

    // --- Interface methods implementation
    
    public boolean accepts(final String element) {
      return this.fPattern.matcher(element).matches();
    }
    
    // --- Fields
    
    private final Pattern fPattern;
    
  }
  
  private static final class TypePatternFilter implements IFilter<String> {
    
    TypePatternFilter(final String regex, final boolean isCaseSensitive) {
      this.fPattern = isCaseSensitive ? Pattern.compile(regex) : Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
    }

    // --- Interface methods implementation
    
    public boolean accepts(final String element) {
      final String typeName = element.substring(element.lastIndexOf('.') + 1);
      return this.fPattern.matcher(typeName).matches();
    }
    
    // --- Fields
    
    private final Pattern fPattern;
    
  }

}

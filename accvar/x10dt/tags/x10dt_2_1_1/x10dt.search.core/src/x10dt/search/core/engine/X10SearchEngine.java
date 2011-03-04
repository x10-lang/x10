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

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.imp.model.IPathEntry;
import org.eclipse.imp.model.IPathEntry.PathEntryType;
import org.eclipse.imp.model.ISourceProject;
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
import org.eclipse.imp.pdb.facts.db.context.ProjectContext;
import org.eclipse.imp.pdb.facts.db.context.WorkspaceContext;

import x10dt.search.core.elements.IFieldInfo;
import x10dt.search.core.elements.IMethodInfo;
import x10dt.search.core.elements.ITypeInfo;
import x10dt.search.core.engine.scope.IX10SearchScope;
import x10dt.search.core.engine.scope.SearchScopeFactory;
import x10dt.search.core.engine.scope.X10SearchScope;
import x10dt.search.core.utils.FactBaseUtils;
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
   * Creates a type hierarchy for the given type name accessible from the project transmitted.
   * 
   * <p>Note that there is no refresh action at this point. If one wants an updated version of it, a new one needs to be
   * created.
   * 
   * @param searchScope The scope of the search. See {@link SearchScopeFactory}.
   * @param typeName The type name to use for the type hierarchy.
   * @param monitor The monitor to use to report progress or cancel the operation.
   * @return A non-null instance of {@link ITypeHierarchy}.
   * @throws ModelException Occurs if the project does not exist.
   * @throws InterruptedException Occurs if the operation gets canceled.
   */
  public static ITypeHierarchy createTypeHierarchy(final IX10SearchScope searchScope, final String typeName,
                                                   final IProgressMonitor monitor) throws ModelException, 
                                                                                          InterruptedException {
    return new TypeHierarchy(searchScope, typeName, monitor);
  }
  
  /**
   * Returns all the field information that matches the Java regular expression identifying a potential set of field names
   * for a given type.
   * 
   * @param searchScope The scope of the search. See {@link SearchScopeFactory}.
   * @param typeName The type name that is needed to be located first.
   * @param fieldNameRegEx The regular expression identifying the field names to search for.
   * @param isCaseSensitive Indicates if the pattern matching is case sensitive or not.
   * @param monitor The monitor to use to report progress or cancel the operation.
   * @return A non-null, but possibly empty (if no match was found), array of field information.
   * @throws InterruptedException Occurs if the operation gets canceled.
   */
  public static IFieldInfo[] getAllMatchingFieldInfo(final IX10SearchScope searchScope, final String typeName, 
                                                     final String fieldNameRegEx, final boolean isCaseSensitive,
                                                     final IProgressMonitor monitor) throws InterruptedException {
    return getFieldInfo(searchScope, typeName, new PatternFilter(fieldNameRegEx, isCaseSensitive), monitor);
  }
  
  /**
   * Returns all the field information that matches the Java regular expression identifying a potential set of field names
   * independently of the declaring type.
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
   * @param searchScope The scope of the search. See {@link SearchScopeFactory}.
   * @param typeName The type name that is needed to be located first.
   * @param methodNameRegEx The regular expression identifying the method names to search for.
   * @param isCaseSensitive Indicates if the pattern matching is case sensitive or not.
   * @param monitor The monitor to use to report progress or cancel the operation.
   * @return A non-null, but possibly empty (if no match was found), array of method information.
   * @throws InterruptedException Occurs if the operation gets canceled.
   */
  public static IMethodInfo[] getAllMatchingMethodInfo(final IX10SearchScope searchScope, final String typeName, 
                                                       final String methodNameRegEx, final boolean isCaseSensitive,
                                                       final IProgressMonitor monitor) throws InterruptedException {
    return getMethodInfos(searchScope, typeName, new PatternFilter(methodNameRegEx, isCaseSensitive), monitor);
  }
  
  /**
   * Returns all the method information that matches the Java regular expression identifying a potential set of method names
   * independently of the declaring type.
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
   * @param searchScope The scope of the search. See {@link SearchScopeFactory}.
   * @param typeName The type name that is supposed to contain the field name.
   * @param fieldName The field name to look for.
   * @param monitor The progress monitor to use to report progress or cancel the operation. Can be <b>null</b>.
   * @return A non-null {@link IFieldInfo} if it is found, otherwise <b>null</b>.
   * @throws InterruptedException Occurs if the operation get canceled.
   */
  public static IFieldInfo getFieldInfo(final IX10SearchScope searchScope, final String typeName, final String fieldName,
                                        final IProgressMonitor monitor) throws InterruptedException {
    final IFieldInfo[] fieldInfos = getFieldInfo(searchScope, typeName, new EqualsFilter(fieldName), monitor);
    return (fieldInfos.length == 0) ? null : fieldInfos[0];
  }
  
  /**
   * Returns the method info wrappers for a given type and method name.
   * 
   * @param searchScope The scope of the search. See {@link SearchScopeFactory}.
   * @param typeName The type name that is supposed to contain the method name(s).
   * @param methodName The method name to look for.
   * @param monitor The progress monitor to use to report progress or cancel the operation. Can be <b>null</b>.
   * @return A non-null, but possibly empty (if no match was found), array of method information.
   * @throws InterruptedException Occurs if the operation get canceled.
   */
  public static IMethodInfo[] getMethodInfos(final IX10SearchScope searchScope, final String typeName, final String methodName,
                                             final IProgressMonitor monitor) throws InterruptedException {
    return getMethodInfos(searchScope, typeName, new EqualsFilter(methodName), monitor);
  }
  
  /**
   * Returns the type info wrapper for a given type name.
   * 
   * @param searchScope The scope of the search. See {@link SearchScopeFactory}.
   * @param typeName The type name to look for.
   * @param monitor The progress monitor to use to report progress or cancel the operation. Can be <b>null</b>.
   * @return A non-null but possibly empty array if we haven't found the type in the particular scope.
   * @throws InterruptedException Occurs if the operation get canceled.
   * @throws ExecutionException Occurs if the search threw an exception.
   */
  public static ITypeInfo[] getTypeInfo(final IX10SearchScope searchScope, final String typeName,
                                        final IProgressMonitor monitor) throws InterruptedException, ExecutionException {
    return getTypeInfo(searchScope, new EqualsFilter(typeName), monitor);
  }
  
  // --- Private code
  
  private static IFieldInfo createFieldInfo(final ITuple tuple, final FactBase factBase, final IFactContext context,
                                            final ITypeInfo declaringTypeInfo,
                                            final IProgressMonitor monitor) throws InterruptedException {
    final String fieldTypeName = getBaseTypeName(((IString) tuple.get(2)).getValue());
    ITypeInfo fieldTypeInfo = findTypeInfo(factBase, context, fieldTypeName, monitor);
    if (fieldTypeInfo == null) {
      fieldTypeInfo = new UnknownTypeInfo(fieldTypeName);
    }
    return new FieldInfo(((IString) tuple.get(1)).getValue(), fieldTypeInfo, (ISourceLocation) tuple.get(0), 
                         ((IInteger) tuple.get(3)).intValue(), declaringTypeInfo);
  }
  
  private static IMethodInfo createMethodInfo(final ITuple tuple, final FactBase factBase, final IFactContext context,
                                              final ITypeInfo declaringTypeInfo,
                                              final IProgressMonitor monitor) throws InterruptedException {
    final String returnTypeName = getBaseTypeName(((IString) tuple.get(2)).getValue());
    ITypeInfo returnTypeInfo;
    if (VoidTypeInfo.VOID_TYPE_NAME.equals(returnTypeName)) {
      returnTypeInfo = new VoidTypeInfo();
    } else {
      returnTypeInfo = findTypeInfo(factBase, context, returnTypeName, monitor);
    }
    if (returnTypeInfo == null) {
      returnTypeInfo = new UnknownTypeInfo(returnTypeName);
    }
    final IList parameters = (IList) tuple.get(3);
    final ITypeInfo[] paramInfos = new ITypeInfo[parameters.length()];
    int i = -1;
    for (final IValue paramValue : parameters) {
      final String paramTypeName = getBaseTypeName(((IString) paramValue).getValue());
      paramInfos[++i] = findTypeInfo(factBase, context, paramTypeName, monitor);
      if (paramInfos[i] == null) {
        paramInfos[i] = new UnknownTypeInfo(paramTypeName);
      }
    }
    return new MethodInfo(((IString) tuple.get(1)).getValue(), returnTypeInfo, paramInfos, (ISourceLocation) tuple.get(0), 
                          ((IInteger) tuple.get(4)).intValue(), declaringTypeInfo);
  }
  
  private static boolean collectFieldInfo(final Collection<IFieldInfo> fieldInfos, final FactBase factBase, 
                                          final IFactContext context, final String typeName, final String scopeTypeName, 
                                          final IFilter<String> filter,  
                                          final IProgressMonitor monitor) throws InterruptedException {
    final ISet allTypesValue = FactBaseUtils.getFactBaseSetValue(factBase, context, X10_AllFields, scopeTypeName, monitor);
    if (allTypesValue != null) {
      for (final IValue value : allTypesValue) {
        final ITuple tuple = (ITuple) value;
        if ((typeName == null) || ((IString) tuple.get(0)).getValue().equals(typeName)) {
          final String realTypeName = (typeName == null) ? ((IString) tuple.get(0)).getValue() : typeName;
          final ITypeInfo typeInfo = findTypeInfo(factBase, context, realTypeName, scopeTypeName, monitor);
          final IList list = (IList) tuple.get(1);
          for (final IValue listElement : list) {
            final ITuple fieldTuple = (ITuple) listElement;
            if (filter.accepts(((IString) fieldTuple.get(1)).getValue())) {
              fieldInfos.add(createFieldInfo(fieldTuple, factBase, context, typeInfo, monitor));
            }
          }
          return true;
        }
      }
    }
    return false;
  }
  
  private static boolean collectMethodInfo(final Collection<IMethodInfo> methodInfos, final FactBase factBase, 
                                           final IFactContext context, final String typeName, 
                                           final String scopeTypeName, final IFilter<String> filter,
                                           final IProgressMonitor monitor) throws InterruptedException {
    final ISet allTypesValue = FactBaseUtils.getFactBaseSetValue(factBase, context, X10_AllMethods, scopeTypeName, monitor);
    if (allTypesValue != null) {
      for (final IValue value : allTypesValue) {
        final ITuple tuple = (ITuple) value;
        if ((typeName == null) || ((IString) tuple.get(0)).getValue().equals(typeName)) {
          final String realTypeName = (typeName == null) ? ((IString) tuple.get(0)).getValue() : typeName;
          final ITypeInfo typeInfo = findTypeInfo(factBase, context, realTypeName, scopeTypeName, monitor);
          final IList list = (IList) tuple.get(1);
          for (final IValue listElement : list) {
            final ITuple methodTuple = (ITuple) listElement;
            if (filter.accepts(((IString) methodTuple.get(1)).getValue())) {
              methodInfos.add(createMethodInfo(methodTuple, factBase, context, typeInfo, monitor));
            }
          }
          return true;
        }
      }
    }
    return false;
  }
  
  private static void collectTypeInfo(final Collection<ITypeInfo> typeInfos, final FactBase factBase, 
                                      final IFactContext context, final IFilter<String> filter, 
                                      final String scopeTypeName, final IX10SearchScope searchScope, 
                                      final IProgressMonitor monitor) throws InterruptedException {
    final ISet allTypesValue = FactBaseUtils.getFactBaseSetValue(factBase, context, X10_AllTypes, scopeTypeName, monitor);
    if (allTypesValue != null) {
      for (final IValue value : allTypesValue) {
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
            declaringType = findTypeInfo(factBase, context, declaringTypeName.getValue(), scopeTypeName, monitor);
          }
          typeInfos.add(new TypeInfo(name, sourceLoc, x10FlagsCode, declaringType));
        }
      }
    }
  }
  
  private static String getBaseTypeName(final String typeName) {
    final int squareBracketIndex = typeName.indexOf('[');
    String baseTypeName = typeName;
    if (squareBracketIndex != -1) {
      baseTypeName = baseTypeName.substring(0, squareBracketIndex);
    }
    final int bracketIndex = baseTypeName.indexOf('{');
    if (bracketIndex != -1) {
      baseTypeName = baseTypeName.substring(0, bracketIndex);
    }
    return baseTypeName;
  }
  
  private static IFieldInfo[] getFieldInfo(final IX10SearchScope searchScope, final String typeName, 
                                           final IFilter<String> filter, 
                                           final IProgressMonitor monitor) throws InterruptedException {
    final FactBase factBase = FactBase.getInstance();    
    final SubMonitor subMonitor = SubMonitor.convert(monitor);
    final Collection<IFieldInfo> fieldInfos = new ArrayList<IFieldInfo>();
    try {
      final ICountableIterable<IFactContext> searchContexts = searchScope.createSearchContexts();
      subMonitor.beginTask(null, 2 * searchContexts.getSize() + 1);
      
      for (final IFactContext context : searchContexts) {
        if (subMonitor.isCanceled()) {
          throw new InterruptedException();
        }
        if ((searchScope.getSearchMask() & X10SearchScope.APPLICATION) != 0) {
          collectFieldInfo(fieldInfos, factBase, context, typeName, APPLICATION, filter, subMonitor.newChild(1));
        }
        
        if (subMonitor.isCanceled()) {
          throw new InterruptedException();
        }
        if ((searchScope.getSearchMask() & X10SearchScope.LIBRARY) != 0) {
          collectFieldInfo(fieldInfos, factBase, context, typeName, LIBRARY, filter, subMonitor.newChild(1));
        }
      }
      
      if (subMonitor.isCanceled()) {
        throw new InterruptedException();
      }
      if ((searchScope.getSearchMask() & X10SearchScope.RUNTIME) != 0) {
        collectFieldInfo(fieldInfos, factBase, WorkspaceContext.getInstance(), typeName, RUNTIME, filter,
                          subMonitor.newChild(1));
      }
    } finally {
      subMonitor.done();
    }
    return fieldInfos.toArray(new IFieldInfo[fieldInfos.size()]);
  }
  
  private static IMethodInfo[] getMethodInfos(final IX10SearchScope searchScope, final String typeName, 
                                              final IFilter<String> filter, 
                                              final IProgressMonitor monitor) throws InterruptedException {
    final FactBase factBase = FactBase.getInstance();    
    final SubMonitor subMonitor = SubMonitor.convert(monitor);
    final Collection<IMethodInfo> methodInfos = new ArrayList<IMethodInfo>();
    try {
      final ICountableIterable<IFactContext> searchContexts = searchScope.createSearchContexts();
      subMonitor.beginTask(null, 2 * searchContexts.getSize() + 1);
      
      for (final IFactContext context : searchContexts) {
        if (subMonitor.isCanceled()) {
          throw new InterruptedException();
        }
        if ((searchScope.getSearchMask() & X10SearchScope.APPLICATION) != 0) {
          collectMethodInfo(methodInfos, factBase, context, typeName, APPLICATION, filter, subMonitor.newChild(1));
        }
        
        if (subMonitor.isCanceled()) {
          throw new InterruptedException();
        }
        if ((searchScope.getSearchMask() & X10SearchScope.LIBRARY) != 0) {
          collectMethodInfo(methodInfos, factBase, context, typeName, LIBRARY, filter, subMonitor.newChild(1));
        }
      }
      
      if (subMonitor.isCanceled()) {
        throw new InterruptedException();
      }
      if ((searchScope.getSearchMask() & X10SearchScope.RUNTIME) != 0) {
        collectMethodInfo(methodInfos, factBase, WorkspaceContext.getInstance(), typeName, RUNTIME, filter,
                          subMonitor.newChild(1));
      }
    } finally {
      subMonitor.done();
    }
    return methodInfos.toArray(new IMethodInfo[methodInfos.size()]);
  }
  
  private static ITypeInfo[] getTypeInfo(final IX10SearchScope searchScope, final IFilter<String> filter,
                                         final IProgressMonitor monitor) throws InterruptedException {
    final FactBase factBase = FactBase.getInstance();
    
    final SubMonitor subMonitor = SubMonitor.convert(monitor);
    final Collection<ITypeInfo> typeInfos = new ArrayList<ITypeInfo>();
    try {      
      final ICountableIterable<IFactContext> searchContexts = searchScope.createSearchContexts();
      subMonitor.beginTask(null, 2 * searchContexts.getSize() + 1);
      
      for (final IFactContext context : searchContexts) {
        if (subMonitor.isCanceled()) {
          throw new InterruptedException();
        }
        if ((searchScope.getSearchMask() & X10SearchScope.APPLICATION) != 0) {
          collectTypeInfo(typeInfos, factBase, context, filter, APPLICATION, searchScope, subMonitor.newChild(1));
        }
        
        if (subMonitor.isCanceled()) {
          throw new InterruptedException();
        }
        if ((searchScope.getSearchMask() & X10SearchScope.LIBRARY) != 0) {
          collectTypeInfo(typeInfos, factBase, context, filter, LIBRARY, searchScope, subMonitor.newChild(1));
        }
      }
      
      if (subMonitor.isCanceled()) {
        throw new InterruptedException();
      }
      if ((searchScope.getSearchMask() & X10SearchScope.RUNTIME) != 0) {
        collectTypeInfo(typeInfos, factBase, WorkspaceContext.getInstance(), filter, RUNTIME, searchScope,
                        subMonitor.newChild(1));
      }
    } finally {
      subMonitor.done();
    }
    return typeInfos.toArray(new ITypeInfo[typeInfos.size()]);
  }
  
  private static ITypeInfo findTypeInfo(final FactBase factBase, final IFactContext context, final String typeName,
                                        final IProgressMonitor monitor) throws InterruptedException {
    ITypeInfo typeInfo = findTypeInfo(factBase, context, typeName, APPLICATION, monitor);
    if (typeInfo != null) {
      return typeInfo;
    }
    typeInfo = findTypeInfo(factBase, context, typeName, LIBRARY, monitor);
    if (typeInfo != null) {
      return typeInfo;
    }
    typeInfo = findTypeInfo(factBase, WorkspaceContext.getInstance(), typeName, RUNTIME, monitor);
    if (typeInfo != null) {
      return typeInfo;
    }
    // If we reach here, then the type must be accessible in project dependencies
    if (context instanceof ProjectContext) {
      return findTypeInfo(factBase, context, typeName, ((ProjectContext) context).getProject(), monitor);
    } else {
      return null;
    }
  }
  
  private static ITypeInfo findTypeInfo(final FactBase factBase, final IFactContext context, final String typeName,
                                        final ISourceProject sourceProject,
                                        final IProgressMonitor monitor) throws InterruptedException {
    for (final IPathEntry pathEntry : sourceProject.getBuildPath()) {
      if (pathEntry.getEntryType() == PathEntryType.PROJECT) {
        //TODO To implement once Adam has committed his code.
      }
    }
    return null;
  }
  
  private static ITypeInfo findTypeInfo(final FactBase factBase, final IFactContext context, final String typeName,
                                        final String scopeTypeName, 
                                        final IProgressMonitor monitor) throws InterruptedException {
    final ISet allTypesValue = FactBaseUtils.getFactBaseSetValue(factBase, context, X10_AllTypes, scopeTypeName, monitor);
    if (allTypesValue != null) {
      for (final IValue value : allTypesValue) {
        final ITuple tuple = (ITuple) value;
        if (((IString) tuple.get(0)).getValue().equals(typeName)) {
          final ITypeInfo declaringType;
          if (tuple.arity() < 4) {
            declaringType = null;
          } else {
            final IString declaringTypeName = (IString) tuple.get(3);
            declaringType = findTypeInfo(factBase, context, declaringTypeName.getValue(), scopeTypeName, monitor);
          }
          return new TypeInfo(tuple, declaringType);
        }
      }
    }
    return null;
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

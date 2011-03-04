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
import static x10dt.search.core.pdb.X10FactTypeNames.X10_AllTypes;
import static x10dt.search.core.pdb.X10FactTypeNames.X10_TypeHierarchy;
import static x10dt.search.core.pdb.X10FactTypeNames.X10_TypeName;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.imp.pdb.facts.ISet;
import org.eclipse.imp.pdb.facts.IString;
import org.eclipse.imp.pdb.facts.ITuple;
import org.eclipse.imp.pdb.facts.IValue;
import org.eclipse.imp.pdb.facts.db.FactBase;
import org.eclipse.imp.pdb.facts.db.FactKey;
import org.eclipse.imp.pdb.facts.db.IFactContext;
import org.eclipse.imp.pdb.facts.db.context.WorkspaceContext;
import org.eclipse.imp.pdb.facts.impl.fast.ValueFactory;
import org.eclipse.imp.pdb.facts.type.Type;
import org.eclipse.imp.pdb.indexing.IndexManager;
import org.eclipse.osgi.util.NLS;

import x10dt.search.core.Messages;
import x10dt.search.core.SearchCoreActivator;
import x10dt.search.core.elements.ITypeInfo;
import x10dt.search.core.engine.scope.IX10SearchScope;
import x10dt.search.core.engine.scope.X10SearchScope;
import x10dt.search.core.pdb.SearchDBTypes;
import x10dt.search.core.pdb.X10FlagsEncoder.X10;
import x10dt.ui.launch.core.utils.ICountableIterable;


final class TypeHierarchy implements ITypeHierarchy {
  
  TypeHierarchy(final IX10SearchScope searchScope, final String typeName, 
                final IProgressMonitor monitor) throws InterruptedException {
    if (monitor.isCanceled()) {
      throw new InterruptedException();
    }
    
    this.fClassToSuperClass = new HashMap<String, ITypeInfo>();
    this.fTypeToSuperInterfaces = new HashMap<String, Set<ITypeInfo>>();
    this.fTypeToSubInterfaces = new HashMap<String, Set<ITypeInfo>>();
    this.fTypeToSubClasses = new HashMap<String, Set<ITypeInfo>>();
    this.fAllTypes = new HashSet<ITypeInfo>();

    final FactBase factBase = FactBase.getInstance();
    final Job buildJob = new Job(Messages.TH_BuildTHJobName) {
      
      // --- Abstract methods implementation
      
      protected IStatus run(final IProgressMonitor jobMonitor) {
        while (! IndexManager.isAvailable() && ! monitor.isCanceled())
          ;
        if (monitor.isCanceled()) {
          return new Status(IStatus.CANCEL, SearchCoreActivator.PLUGIN_ID, Messages.TH_SearchCanceled);
        }
        final ICountableIterable<IFactContext> searchContexts = searchScope.createSearchContexts();
        jobMonitor.beginTask(null, searchContexts.getSize() + 20);
        jobMonitor.subTask(Messages.TH_CollectingData);
        
        ISet allTypesAppValue = null;
        ISet allTypesLibValue = null;
        ISet allTypesRuntimeValue = null;
        ISet typeHierarchyAppValue = null;
        ISet typeHierarchyLibValue = null;
        ISet typeHierarchyRuntimeValue = null;
        for (final IFactContext context : searchContexts) {
          if ((searchScope.getSearchMask() & X10SearchScope.APPLICATION) != 0) {
            allTypesAppValue = union(allTypesAppValue, getFactBaseSetValue(factBase, context, X10_AllTypes, APPLICATION));
            typeHierarchyAppValue = union(typeHierarchyAppValue, 
                                          getFactBaseSetValue(factBase, context, X10_TypeHierarchy, APPLICATION));
          }
          if ((searchScope.getSearchMask() & X10SearchScope.LIBRARY) != 0) {
            allTypesLibValue = union(allTypesLibValue, getFactBaseSetValue(factBase, context, X10_AllTypes, LIBRARY));
            typeHierarchyLibValue = union(typeHierarchyLibValue, 
                                          getFactBaseSetValue(factBase, context, X10_TypeHierarchy, LIBRARY));
          }
          if ((searchScope.getSearchMask() & X10SearchScope.RUNTIME) != 0) {
            allTypesRuntimeValue = union(allTypesRuntimeValue, getFactBaseSetValue(factBase, WorkspaceContext.getInstance(), 
                                                                                   X10_AllTypes, RUNTIME));
            typeHierarchyRuntimeValue = union(typeHierarchyRuntimeValue, 
                                              getFactBaseSetValue(factBase, WorkspaceContext.getInstance(), 
                                                                  X10_TypeHierarchy, RUNTIME));
          }
          jobMonitor.worked(1);
        }
        
        try {
          final Type typeNameType = SearchDBTypes.getInstance().getType(X10_TypeName);
          final IValue typeNameValue = typeNameType.make(ValueFactory.getInstance(), typeName);
          
          buildHierarchy(union(allTypesAppValue, allTypesLibValue, allTypesRuntimeValue),
                         union(typeHierarchyAppValue, typeHierarchyLibValue, typeHierarchyRuntimeValue),
                         typeNameValue, new SubProgressMonitor(monitor, 20));
          return Status.OK_STATUS;
        } catch (InterruptedException except) {
          return new Status(IStatus.CANCEL, SearchCoreActivator.PLUGIN_ID, Messages.TH_SearchCanceled);
        } catch (TypeHierarchyException except) {
          return new Status(IStatus.ERROR, SearchCoreActivator.PLUGIN_ID, except.getMessage());
        }
      }
      
    };
    buildJob.schedule();
    buildJob.join();
  }
  
  // --- Interface methods implementation

  public boolean contains(final String typeName) {
    for (final ITypeInfo typeInfo : this.fAllTypes) {
      if (typeName.equals(typeInfo.getName())) {
        return true;
      }
    }
    return false;
  }
  
  public ITypeInfo[] getAllClasses() {
    final Set<ITypeInfo> classes = new HashSet<ITypeInfo>();
    for (final ITypeInfo typeInfo : this.fAllTypes) {
      if ((X10.INTERFACE.getCode() & typeInfo.getX10FlagsCode()) == 0) {
        classes.add(typeInfo);
      }
    }
    return classes.toArray(new ITypeInfo[classes.size()]);
  }

  public ITypeInfo[] getAllInterfaces() {
    final Set<ITypeInfo> interfaces = new HashSet<ITypeInfo>();
    for (final ITypeInfo typeInfo : this.fAllTypes) {
      if ((X10.INTERFACE.getCode() & typeInfo.getX10FlagsCode()) != 0) {
        interfaces.add(typeInfo);
      }
    }
    return interfaces.toArray(new ITypeInfo[interfaces.size()]);
  }
  
  public ITypeInfo[] getAllSubClasses(final String typeName) {
    final Set<ITypeInfo> subClasses = new HashSet<ITypeInfo>();
    collectMapElements(this.fTypeToSubClasses, subClasses, typeName);
    return subClasses.toArray(new ITypeInfo[subClasses.size()]);
  }
  
  public ITypeInfo[] getAllSubTypes(final String typeName) {
    final Set<ITypeInfo> subTypes = new HashSet<ITypeInfo>();
    collectMapElements(this.fTypeToSubClasses, subTypes, typeName);
    collectMapElements(this.fTypeToSubInterfaces, subTypes, typeName);
    return subTypes.toArray(new ITypeInfo[subTypes.size()]);
  }
  
  public ITypeInfo[] getAllSuperClasses(final String typeName) {
    final Collection<ITypeInfo> superClasses = new ArrayList<ITypeInfo>();
    collectAllSuperClasses(superClasses, typeName);
    return superClasses.toArray(new ITypeInfo[superClasses.size()]);
  }
  
  public ITypeInfo[] getAllSuperInterfaces(final String typeName) {
    final Set<ITypeInfo> superTypes = new HashSet<ITypeInfo>();
    collectMapElements(this.fTypeToSuperInterfaces, superTypes, typeName);
    String name = typeName;    
    while (name != null) {
      final ITypeInfo superClass = this.fClassToSuperClass.get(name);      
      if (superClass != null) {
        name = superClass.getName();
        collectMapElements(this.fTypeToSuperInterfaces, superTypes, name);
      } else {
        name = null;
      }
    }
    return superTypes.toArray(new ITypeInfo[superTypes.size()]);
  }
  
  public ITypeInfo[] getAllSuperTypes(final String typeName) {
    final Set<ITypeInfo> superTypes = new HashSet<ITypeInfo>();
    collectMapElements(this.fTypeToSuperInterfaces, superTypes, typeName);
    String name = typeName;    
    while (name != null) {
      final ITypeInfo superClass = this.fClassToSuperClass.get(name);      
      if (superClass != null) {
        superTypes.add(superClass);
        name = superClass.getName();
        collectMapElements(this.fTypeToSuperInterfaces, superTypes, name);
      } else {
        name = null;
      }
    }
    return superTypes.toArray(new ITypeInfo[superTypes.size()]);
  }
  
  public ITypeInfo[] getInterfaces(final String typeName) {
    final Set<ITypeInfo> interfaces = this.fTypeToSuperInterfaces.get(typeName);
    if (interfaces == null) {
      return new ITypeInfo[0];
    } else {
      return interfaces.toArray(new ITypeInfo[interfaces.size()]);
    }
  }
  
  public ITypeInfo[] getSubTypes(final String typeName) {
    final Set<ITypeInfo> subTypes = new HashSet<ITypeInfo>();
    final Set<ITypeInfo> subClasses = this.fTypeToSubClasses.get(typeName);
    if (subClasses != null) {
      for (final ITypeInfo subClass : subClasses) {
        subTypes.add(subClass);
      }
    }
    final Set<ITypeInfo> subInterfaces = this.fTypeToSubInterfaces.get(typeName);
    if (subInterfaces != null) {
      for (final ITypeInfo subInterface : subInterfaces) {
        subTypes.add(subInterface);
      }
    }
    return subTypes.toArray(new ITypeInfo[subTypes.size()]);
  }
  
  public ITypeInfo getSuperClass(final String typeName) {
    final ITypeInfo typeInfo = this.fClassToSuperClass.get(typeName);
    return (typeInfo == null) ? null : typeInfo;
  }
  
  public ITypeInfo[] getSuperTypes(final String typeName) {
    final Set<ITypeInfo> superTypes = new HashSet<ITypeInfo>();
    final ITypeInfo superClass = this.fClassToSuperClass.get(typeName);
    if (superClass != null) {
      superTypes.add(superClass);
    }
    final Set<ITypeInfo> superInterfaces = this.fTypeToSuperInterfaces.get(typeName);
    if (superInterfaces != null) {
      for (final ITypeInfo superInterface : superInterfaces) {
        superTypes.add(superInterface);
      }
    }
    return superTypes.toArray(new ITypeInfo[superTypes.size()]);
  }
  
  public ITypeInfo getType() {
    return this.fMainType;
  }
    
  // --- Private code
  
  private void addToMap(final Map<String, Set<ITypeInfo>> container, final String key, final ITypeInfo value) {
    final Set<ITypeInfo> set = container.get(key);
    if (set == null) {
      final Set<ITypeInfo> newSet = new HashSet<ITypeInfo>();
      newSet.add(value);
      container.put(key, newSet);
    } else {
      set.add(value);
    }
  }
  
  private void buildHierarchy(final ISet allTypes, final ISet globalTypeHierarchy, final IValue typeNameValue,
                              final IProgressMonitor monitor) throws InterruptedException, TypeHierarchyException {
    final ITypeInfo mainTypeInfo = getTypeInfo(allTypes, ((IString) typeNameValue).getValue());
    if (mainTypeInfo == null) {
      throw new TypeHierarchyException(NLS.bind(Messages.TH_MainTypeInfoError, typeNameValue));
    }
    this.fMainType = mainTypeInfo;
    
    final Queue<IValue> subTypesWork = new LinkedList<IValue>();
    final Queue<IValue> superTypesWork = new LinkedList<IValue>();
    
    subTypesWork.add(typeNameValue);
    superTypesWork.add(typeNameValue);
    
    final SubMonitor progress = SubMonitor.convert(monitor);
    progress.subTask(Messages.TH_BuildTHTaskName);
    
    while (subTypesWork.size() > 0 || superTypesWork.size() > 0) {
      if (progress.isCanceled()) {
        throw new InterruptedException();
      }
      progress.setWorkRemaining(subTypesWork.size() + superTypesWork.size());
      
      final IValue superWorkItem = superTypesWork.isEmpty() ? null : superTypesWork.poll();
      final IValue subWorkItem = subTypesWork.isEmpty() ? null : subTypesWork.poll();
        
      for (final IValue value : globalTypeHierarchy) {
        final ITuple tuple = (ITuple) value;
        if ((superWorkItem != null) && superWorkItem.equals(tuple.get(0))) {
          buildHierarchy(allTypes, tuple);
          superTypesWork.offer(tuple.get(1));
          progress.worked(1);
        }
        if ((subWorkItem != null) && subWorkItem.equals(tuple.get(1))) {
          buildHierarchy(allTypes, tuple);
          subTypesWork.offer(tuple.get(0));
          progress.worked(1);
        }
      }
    }
    
    progress.done();
  }

  private void buildHierarchy(final ISet allTypes, final ITuple tuple) {
    final String typeName = ((IString) tuple.get(0)).getValue();
    final String parentTypeName = ((IString) tuple.get(1)).getValue();

    final ITypeInfo type = getTypeInfo(allTypes, typeName);
    if (type != null) {
      this.fAllTypes.add(type);
      final ITypeInfo parentType = getTypeInfo(allTypes, parentTypeName);
      if (parentType != null) {
        this.fAllTypes.add(parentType);

        if ((X10.INTERFACE.getCode() & type.getX10FlagsCode()) != 0) {
          addToMap(this.fTypeToSuperInterfaces, typeName, parentType);
          addToMap(this.fTypeToSubInterfaces, parentTypeName, type);
        } else {
          if ((X10.INTERFACE.getCode() & parentType.getX10FlagsCode()) != 0) {
            addToMap(this.fTypeToSuperInterfaces, typeName, parentType);
            addToMap(this.fTypeToSubClasses, parentTypeName, type);
          } else {
            this.fClassToSuperClass.put(typeName, parentType);
            addToMap(this.fTypeToSubClasses, parentTypeName, type);
          }
        }
      }
    }
  }
  
  private void collectAllSuperClasses(final Collection<ITypeInfo> container, final String typeName) {
    final ITypeInfo superClass = this.fClassToSuperClass.get(typeName);
    if (superClass != null) {
      container.add(superClass);
      collectAllSuperClasses(container, superClass.getName());
    }
  }
  
  private void collectMapElements(final Map<String, Set<ITypeInfo>> map, final Set<ITypeInfo> container, 
                                  final String typeName) {
    final Set<ITypeInfo> set = map.get(typeName);
    if (set != null) {
      for (final ITypeInfo element : set) {
        if (! container.contains(element)) {
          container.add(element);
          collectMapElements(map, container, element.getName());
        }
      }
    }
  }
  
  private ISet getFactBaseSetValue(final FactBase factBase, final IFactContext context, final String parametricTypeName,
                                   final String scopeTypeName) {
    return (ISet) factBase.queryFact(new FactKey(SearchDBTypes.getInstance().getType(parametricTypeName, scopeTypeName), 
                                                 context));
  }
  
  private ITypeInfo getTypeInfo(final ISet allTypes, final String typeName) {
    for (final IValue value : allTypes) {
      final ITuple tuple = (ITuple) value;
      if (((IString) tuple.get(0)).getValue().equals(typeName)) {
        final ITypeInfo declaringType;
        if (tuple.arity() < 4) {
          declaringType = null;
        } else {
          final IString declaringTypeName = (IString) tuple.get(3);
          declaringType = getTypeInfo(allTypes, declaringTypeName.getValue());
        }
        return new TypeInfo(tuple, declaringType);
      }
    }
    return null;
  }
  
  private <T extends ISet> T union(final T ... sets) {
    T finalSet = null;
    for (final T set : sets) {
      if (finalSet == null) {
        if (set != null) {
          finalSet = set;
        }
      } else if (set != null) {
        finalSet = finalSet.union(set);
      }
    }
    return finalSet;
  }
  
  // --- Fields
  
  private ITypeInfo fMainType;
  
  private final Map<String, ITypeInfo> fClassToSuperClass;
  
  private final Map<String, Set<ITypeInfo>> fTypeToSuperInterfaces;
  
  private final Map<String, Set<ITypeInfo>> fTypeToSubClasses;
  
  private final Map<String, Set<ITypeInfo>> fTypeToSubInterfaces;
  
  private final Set<ITypeInfo> fAllTypes;
  
}

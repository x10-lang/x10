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
import org.eclipse.imp.pdb.facts.ITuple;
import org.eclipse.imp.pdb.facts.IValue;
import org.eclipse.imp.pdb.facts.db.FactBase;
import org.eclipse.imp.pdb.facts.db.FactKey;
import org.eclipse.imp.pdb.facts.db.IFactContext;
import org.eclipse.imp.pdb.facts.db.context.WorkspaceContext;

import x10dt.search.core.Messages;
import x10dt.search.core.SearchCoreActivator;
import x10dt.search.core.elements.ITypeInfo;
import x10dt.search.core.engine.scope.IX10SearchScope;
import x10dt.search.core.engine.scope.X10SearchScope;
import x10dt.search.core.pdb.SearchDBTypes;
import x10dt.search.core.pdb.X10FlagsEncoder.X10;
import x10dt.search.core.utils.PDBValueUtils;
import x10dt.ui.launch.core.utils.ICountableIterable;


final class TypeHierarchy implements ITypeHierarchy {
  
  TypeHierarchy(final IX10SearchScope searchScope, final ITypeInfo typeInfo, 
                final IProgressMonitor monitor) throws InterruptedException {
    if (monitor.isCanceled()) {
      throw new InterruptedException();
    }
    
    this.fClassToSuperClass = new HashMap<ITypeInfo, ITypeInfo>();
    this.fTypeToSuperInterfaces = new HashMap<ITypeInfo, Set<ITypeInfo>>();
    this.fTypeToSubInterfaces = new HashMap<ITypeInfo, Set<ITypeInfo>>();
    this.fTypeToSubClasses = new HashMap<ITypeInfo, Set<ITypeInfo>>();
    this.fAllTypes = new HashSet<ITypeInfo>();
    this.fMainType = typeInfo;

    final FactBase factBase = SearchCoreActivator.getIndexer().getFactBase();
    final Job buildJob = new Job(Messages.TH_BuildTHJobName) {
      
      // --- Abstract methods implementation
      
      protected IStatus run(final IProgressMonitor jobMonitor) {
        while (! SearchCoreActivator.getIndexer().isAvailable() && ! monitor.isCanceled())
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
          buildHierarchy(union(allTypesAppValue, allTypesLibValue, allTypesRuntimeValue),
                         union(typeHierarchyAppValue, typeHierarchyLibValue, typeHierarchyRuntimeValue),
                         new SubProgressMonitor(monitor, 20));
          return Status.OK_STATUS;
        } catch (InterruptedException except) {
          return new Status(IStatus.CANCEL, SearchCoreActivator.PLUGIN_ID, Messages.TH_SearchCanceled);
        }
      }
      
    };
    buildJob.schedule();
    buildJob.join();
  }
  
  // --- Interface methods implementation

  public boolean contains(final ITypeInfo typeInfo) {
    return this.fAllTypes.contains(typeInfo);
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
  
  public ITypeInfo[] getAllSubClasses(final ITypeInfo typeInfo) {
    final Set<ITypeInfo> subClasses = new HashSet<ITypeInfo>();
    collectMapElements(this.fTypeToSubClasses, subClasses, typeInfo);
    return subClasses.toArray(new ITypeInfo[subClasses.size()]);
  }
  
  public ITypeInfo[] getAllSubTypes(final ITypeInfo typeInfo) {
    final Set<ITypeInfo> subTypes = new HashSet<ITypeInfo>();
    collectMapElements(this.fTypeToSubClasses, subTypes, typeInfo);
    collectMapElements(this.fTypeToSubInterfaces, subTypes, typeInfo);
    return subTypes.toArray(new ITypeInfo[subTypes.size()]);
  }
  
  public ITypeInfo[] getAllSuperClasses(final ITypeInfo typeInfo) {
    final Collection<ITypeInfo> superClasses = new ArrayList<ITypeInfo>();
    collectAllSuperClasses(superClasses, typeInfo);
    return superClasses.toArray(new ITypeInfo[superClasses.size()]);
  }
  
  public ITypeInfo[] getAllSuperInterfaces(final ITypeInfo typeInfo) {
    final Set<ITypeInfo> superTypes = new HashSet<ITypeInfo>();
    collectMapElements(this.fTypeToSuperInterfaces, superTypes, typeInfo);
    ITypeInfo curTypeInfo = typeInfo;    
    while (curTypeInfo != null) {
      final ITypeInfo superClass = this.fClassToSuperClass.get(curTypeInfo);
      if (superClass != null) {
        curTypeInfo = superClass;
        collectMapElements(this.fTypeToSuperInterfaces, superTypes, curTypeInfo);
      } else {
        curTypeInfo = null;
      }
    }
    return superTypes.toArray(new ITypeInfo[superTypes.size()]);
  }
  
  public ITypeInfo[] getAllSuperTypes(final ITypeInfo typeInfo) {
    final Set<ITypeInfo> superTypes = new HashSet<ITypeInfo>();
    collectMapElements(this.fTypeToSuperInterfaces, superTypes, typeInfo);
    ITypeInfo curTypeInfo = typeInfo; 
    while (curTypeInfo != null) {
      final ITypeInfo superClass = this.fClassToSuperClass.get(curTypeInfo);      
      if (superClass != null) {
        superTypes.add(superClass);
        curTypeInfo = superClass;
        collectMapElements(this.fTypeToSuperInterfaces, superTypes, curTypeInfo);
      } else {
        curTypeInfo = null;
      }
    }
    return superTypes.toArray(new ITypeInfo[superTypes.size()]);
  }
  
  public ITypeInfo[] getInterfaces(final ITypeInfo typeInfo) {
    final Set<ITypeInfo> interfaces = this.fTypeToSuperInterfaces.get(typeInfo);
    if (interfaces == null) {
      return new ITypeInfo[0];
    } else {
      return interfaces.toArray(new ITypeInfo[interfaces.size()]);
    }
  }
  
  public ITypeInfo[] getSubTypes(final ITypeInfo typeInfo) {
    final Set<ITypeInfo> subTypes = new HashSet<ITypeInfo>();
    final Set<ITypeInfo> subClasses = this.fTypeToSubClasses.get(typeInfo);
    if (subClasses != null) {
      for (final ITypeInfo subClass : subClasses) {
        subTypes.add(subClass);
      }
    }
    final Set<ITypeInfo> subInterfaces = this.fTypeToSubInterfaces.get(typeInfo);
    if (subInterfaces != null) {
      for (final ITypeInfo subInterface : subInterfaces) {
        subTypes.add(subInterface);
      }
    }
    return subTypes.toArray(new ITypeInfo[subTypes.size()]);
  }
  
  public ITypeInfo getSuperClass(final ITypeInfo typeInfo) {
    return this.fClassToSuperClass.get(typeInfo);
  }
  
  public ITypeInfo[] getSuperTypes(final ITypeInfo typeInfo) {
    final Set<ITypeInfo> superTypes = new HashSet<ITypeInfo>();
    final ITypeInfo superClass = this.fClassToSuperClass.get(typeInfo);
    if (superClass != null) {
      superTypes.add(superClass);
    }
    final Set<ITypeInfo> superInterfaces = this.fTypeToSuperInterfaces.get(typeInfo);
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
  
  private void addToMap(final Map<ITypeInfo, Set<ITypeInfo>> container, final ITypeInfo key, final ITypeInfo value) {
    final Set<ITypeInfo> set = container.get(key);
    if (set == null) {
      final Set<ITypeInfo> newSet = new HashSet<ITypeInfo>();
      newSet.add(value);
      container.put(key, newSet);
    } else {
      set.add(value);
    }
  }
  
  private void buildHierarchy(final ISet allTypes, final ISet globalTypeHierarchy, 
                              final IProgressMonitor monitor) throws InterruptedException {
    final Queue<IValue> subTypesWork = new LinkedList<IValue>();
    final Queue<IValue> superTypesWork = new LinkedList<IValue>();
    
    final IValue typeTuple = PDBValueUtils.convert(this.fMainType);
    
    subTypesWork.add(typeTuple);
    superTypesWork.add(typeTuple);
    
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
    final ITuple type = (ITuple) tuple.get(0);
    final ITuple parentType = (ITuple) tuple.get(1);

    final ITypeInfo typeInfo = X10SearchEngine.findTypeInfo(allTypes, type);
    if (type != null) {
      this.fAllTypes.add(typeInfo);
      final ITypeInfo parentTypeInfo = X10SearchEngine.findTypeInfo(allTypes, parentType);
      if (parentTypeInfo != null) {
        this.fAllTypes.add(parentTypeInfo);

        if ((X10.INTERFACE.getCode() & typeInfo.getX10FlagsCode()) != 0) {
          addToMap(this.fTypeToSuperInterfaces, typeInfo, parentTypeInfo);
          addToMap(this.fTypeToSubInterfaces, parentTypeInfo, typeInfo);
        } else {
          if ((X10.INTERFACE.getCode() & parentTypeInfo.getX10FlagsCode()) != 0) {
            addToMap(this.fTypeToSuperInterfaces, typeInfo, parentTypeInfo);
            addToMap(this.fTypeToSubClasses, parentTypeInfo, typeInfo);
          } else {
            this.fClassToSuperClass.put(typeInfo, parentTypeInfo);
            addToMap(this.fTypeToSubClasses, parentTypeInfo, typeInfo);
          }
        }
      }
    }
  }
  
  private void collectAllSuperClasses(final Collection<ITypeInfo> container, final ITypeInfo typeInfo) {
    final ITypeInfo superClass = this.fClassToSuperClass.get(typeInfo);
    if (superClass != null) {
      container.add(superClass);
      collectAllSuperClasses(container, superClass);
    }
  }
  
  private void collectMapElements(final Map<ITypeInfo, Set<ITypeInfo>> map, final Set<ITypeInfo> container, 
                                  final ITypeInfo typeInfo) {
    final Set<ITypeInfo> set = map.get(typeInfo);
    if (set != null) {
      for (final ITypeInfo element : set) {
        if (! container.contains(element)) {
          container.add(element);
          collectMapElements(map, container, element);
        }
      }
    }
  }
  
  private ISet getFactBaseSetValue(final FactBase factBase, final IFactContext context, final String parametricTypeName,
                                   final String scopeTypeName) {
    return (ISet) factBase.queryFact(new FactKey(SearchDBTypes.getInstance().getType(parametricTypeName, scopeTypeName), 
                                                 context));
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
  
  private final ITypeInfo fMainType;
  
  private final Map<ITypeInfo, ITypeInfo> fClassToSuperClass;
  
  private final Map<ITypeInfo, Set<ITypeInfo>> fTypeToSuperInterfaces;
  
  private final Map<ITypeInfo, Set<ITypeInfo>> fTypeToSubClasses;
  
  private final Map<ITypeInfo, Set<ITypeInfo>> fTypeToSubInterfaces;
  
  private final Set<ITypeInfo> fAllTypes;
  
}

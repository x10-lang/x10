/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.utils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.imp.model.ISourceEntity;
import org.eclipse.imp.model.ISourceProject;
import org.eclipse.imp.model.ModelFactory;
import org.eclipse.imp.utils.Pair;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;

import polyglot.types.ClassType;
import x10dt.ui.Messages;
import x10dt.ui.launching.X10TypeSelectionDialog;

/**
 * Utility methods for launching activities.
 * 
 * @author egeay
 */
public final class LaunchUtils {
  
  /**
   * Finds and returns a unique main type, possibly selected by end-user among a list of possible choices, from a given
   * list of Java elements.
   * 
   * @param javaElements The Java elements representing the searching scope. Can be <b>null</b> or empty. In such case,
   * it will automatically selects all the projects that have the nature id given with the next parameter.
   * @param projectNatureId The nature id for the projects of interest in case we receive no Java elements.
   * @param shell The shell to use for the dialogs we may have to create.
   * @return A non-null pair identifying uniquely the X10 main type, or <b>null</b> if we could not find any.
   * @throws InvocationTargetException Occurs if something wrong happened during the search. Such wrapper will contain a 
   * {@link CoreException} as its target in this case.
   * @throws InterruptedException Occurs if the operation got canceled by end-user.
   */
  public static Pair<ClassType, ISourceEntity> findMainType(final ISourceEntity[] javaElements,
                                                           final String projectNatureId,
                                                           final Shell shell) throws InvocationTargetException, 
                                                                                     InterruptedException {
    final ISourceEntity[] elements;
    if ((javaElements == null) || (javaElements.length == 0)) {
      try {
        final Collection<ISourceProject> projects = new ArrayList<ISourceProject>();
        for (final ISourceProject javaProject : ModelFactory.getModelRoot().getProjects()) {
          if (javaProject.getRawProject().getDescription().hasNature(projectNatureId)) {
            projects.add(javaProject);
          }
        }
        elements = projects.toArray(new ISourceProject[projects.size()]);
      } catch (CoreException except) {
        throw new InvocationTargetException(except);
      }
    } else {
      elements = javaElements;
    }
    
    final MainClassesWrapper jeMainClasses = findMainTypes(elements, PlatformUI.getWorkbench().getProgressService());
    final int numberOfTypes = jeMainClasses.numberOfTypes();
    if (numberOfTypes == 0) {
      final MessageBox msgBox = new MessageBox(shell, SWT.OK | SWT.ICON_INFORMATION);
      msgBox.setText(Messages.LU_MainTypeSearchResult);
      msgBox.setMessage(Messages.LU_NoMainTypeFound);
      msgBox.open();
      return null;
    } else if (numberOfTypes == 1) {
      return jeMainClasses.getUniqueMainType();
    } else {
      return chooseType(jeMainClasses, shell);
    }
  }
  
  // --- Private code
  
  private LaunchUtils() {}
  
  private static Pair<ClassType, ISourceEntity> chooseType(final MainClassesWrapper mainClasses, 
                                                          final Shell shell) throws InterruptedException {
    final X10TypeSelectionDialog dialog = new X10TypeSelectionDialog(shell, mainClasses.getX10Types());
    if (dialog.open() == Window.OK) {
      final ClassType resultingType = (ClassType) dialog.getResult()[0];
      return mainClasses.getClassJavaElementPair(resultingType);
    } else {
      throw new InterruptedException();
    }
  }
  
  private static MainClassesWrapper findMainTypes(final ISourceEntity[] elements, 
                                                  final IProgressService progressService) throws InterruptedException,
                                                                                                 InvocationTargetException {
    final MainClassesWrapper wrapper = new MainClassesWrapper();
    final IRunnableWithProgress runnable = new IRunnableWithProgress() {
      public void run(final IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
        monitor.beginTask(null, elements.length);
        for (final ISourceEntity javaElement : elements) {
          if (monitor.isCanceled()) {
            throw new InterruptedException();
          }
          final Collection<ClassType> x10Types = new HashSet<ClassType>();
          try {
            X10Utils.collectX10MainTypes(x10Types, javaElement, new SubProgressMonitor(monitor, 1));
          } catch (CoreException except) {
            monitor.done();
            throw new InvocationTargetException(except);
          }
          wrapper.add(javaElement, x10Types);
        }
      }
    };
    progressService.busyCursorWhile(runnable);
    return wrapper;
  }
  
  // --- Private classes
  
  private static final class MainClassesWrapper {
    
    // --- Internal services
    
    void add(final ISourceEntity javaElement, final Collection<ClassType> types) {
      for (final ClassType classType : types) {
        this.fMainClasses.put(classType, javaElement);
      }
    }
    
    Pair<ClassType, ISourceEntity> getClassJavaElementPair(final ClassType classType) {
      return new Pair<ClassType, ISourceEntity>(classType, this.fMainClasses.get(classType));
    }
    
    Pair<ClassType, ISourceEntity> getUniqueMainType() {
      final Map.Entry<ClassType, ISourceEntity> entry = this.fMainClasses.entrySet().iterator().next();
      return new Pair<ClassType, ISourceEntity>(entry.getKey(), entry.getValue());
    }
    
    Set<ClassType> getX10Types() {
      return this.fMainClasses.keySet();
    }
    
    int numberOfTypes() {
      return this.fMainClasses.keySet().size();
    }
    
    // --- Fields
    
    private final Map<ClassType, ISourceEntity> fMainClasses = new HashMap<ClassType, ISourceEntity>();
    
  }

}

/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.cpp.nature;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;

import x10dt.ui.launch.cpp.CppLaunchCore;

/**
 * Defines the project nature when using X10 C++ back-end.
 * 
 * @author egeay
 */
public final class CppProjectNature implements IProjectNature {

  // --- Interface methods implementation
  
  public void configure() throws CoreException {
    addToBuildSpec(CppLaunchCore.BUILDER_ID);
  }

  public void deconfigure() throws CoreException {
    removeFromBuildSpec(CppLaunchCore.BUILDER_ID);
  }
  
  public final IProject getProject() {
    return this.fProject;
  }

  public final void setProject(final IProject project) {
    this.fProject = project;
  }
  
  // --- Private code
  
  private void addToBuildSpec(final String builderID) throws CoreException {
    final IProjectDescription description = getProject().getDescription();
    final ICommand builderCommand = getBuilderCommand(description, builderID);

    if (builderCommand == null) {
      // Adds a new build specification.
      final ICommand command = description.newCommand();
      command.setBuilderName(builderID);
      setBuilderCommand(description, command);
    }
  }
  
  private ICommand getBuilderCommand(final IProjectDescription description, final String builderId) {
    for (final ICommand command : description.getBuildSpec()) {
      if (command.getBuilderName().equals(builderId)) {
        return command;
      }
    }
    return null;
  }
  
  private void removeFromBuildSpec(final String builderID) throws CoreException {
    final IProjectDescription description = getProject().getDescription();
    final ICommand[] commands = description.getBuildSpec();

    for (int i = 0; i < commands.length; ++i) {
      if (commands[i].getBuilderName().equals(builderID)) {
        final ICommand[] newCommands = new ICommand[commands.length - 1];

        System.arraycopy(commands, 0, newCommands, 0, i);
        System.arraycopy(commands, i + 1, newCommands, i, commands.length - i - 1);
        
        description.setBuildSpec(newCommands);
        getProject().setDescription(description, null);
        return;
      }
    }
  }
  
  private void setBuilderCommand(final IProjectDescription description, final ICommand newCommand) throws CoreException {
    final ICommand[] oldCommands = description.getBuildSpec();
    final ICommand[] newCommands = new ICommand[oldCommands.length + 1];
    System.arraycopy(oldCommands, 0, newCommands, 1, oldCommands.length);
    newCommands[0] = newCommand;

    // Commits the specification change into the project
    description.setBuildSpec(newCommands);
    getProject().setDescription(description, null);
  }
  
  // --- Fields
  
  private IProject fProject;

}

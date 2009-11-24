/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Robert Fuhrer (rfuhrer@watson.ibm.com) - initial API and implementation

 *******************************************************************************/

/*
 * (C) Copyright IBM Corporation 2007
 * 
 * This file is part of the Eclipse IMP.
 */
/*
 * Created on Feb 6, 2006
 */
package org.eclipse.imp.x10dt.core.wizards;

import java.io.InputStream;
import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.imp.builder.ProjectNatureBase;
import org.eclipse.imp.java.hosted.wizards.NewProjectWizardSecondPage;
import org.eclipse.imp.x10dt.core.builder.X10ProjectNature;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.osgi.framework.Bundle;

public class X10ProjectWizardSecondPage extends NewProjectWizardSecondPage {
    public X10ProjectWizardSecondPage(X10ProjectWizardFirstPage firstPage) {
        super(firstPage);
    }

    protected ProjectNatureBase getProjectNature() {
        return new X10ProjectNature();
    }

    protected IClasspathEntry createLanguageRuntimeEntry() {
        Bundle x10RuntimeBundle= Platform.getBundle("x10.runtime");
        String bundleVersion= (String) x10RuntimeBundle.getHeaders().get("Bundle-Version");
        IPath x10RuntimePath= new Path("ECLIPSE_HOME/plugins/x10.runtime_" + bundleVersion + ".jar");

        return JavaCore.newVariableEntry(x10RuntimePath, null, null);
    }

    @Override
    public void performFinish(IProgressMonitor monitor) throws CoreException, InterruptedException {
        final X10ProjectWizardFirstPage firstPage= (X10ProjectWizardFirstPage) this.getPreviousPage();
        final IProject project= firstPage.getProjectHandle();

        super.performFinish(monitor);

        if (firstPage.isGenHello()) {
            ResourcesPlugin.getWorkspace().run(new IWorkspaceRunnable() {
                public void run(IProgressMonitor monitor) throws CoreException {
                    IFile newFile= project.getFile("src/Hello.x10");
                    IFolder srcFolder= project.getFolder("src");
                    IJavaProject javaProject= JavaCore.create(project);
                    IPackageFragmentRoot pkgFragRoot= javaProject.getPackageFragmentRoot(srcFolder);
                    IPackageFragment pkgFrag= pkgFragRoot.getPackageFragment("");

                    InputStream sourceInputStream= NewX10ClassPage.createContentStream(newFile, "Hello", pkgFrag, "", new ArrayList<String>(), true, true);
                    newFile.create(sourceInputStream, true, monitor);

                    ((X10ProjectWizard) X10ProjectWizardSecondPage.this.getWizard()).selectAndReveal(newFile);
                    openResource(newFile);
                }
            }, monitor);
        }
    }
}

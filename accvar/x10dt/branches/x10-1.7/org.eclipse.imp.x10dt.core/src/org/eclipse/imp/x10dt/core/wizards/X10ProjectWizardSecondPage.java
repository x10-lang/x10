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
import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.imp.builder.ProjectNatureBase;
import org.eclipse.imp.java.hosted.wizards.NewProjectWizardSecondPage;
import org.eclipse.imp.x10dt.core.X10DTCorePlugin;
import org.eclipse.imp.x10dt.core.builder.X10ProjectNature;
import org.eclipse.imp.x10dt.core.runtime.X10RuntimeUtils;
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

    /**
     * Get the installed language runtime path
     * <br> BRT: I have other things to put in the classpath too, like constraints/common.  Where to put them?
     * use (new) createLanguageRuntimeEntries() instead, to add multiple values
     */
//    @Override
//    protected IClasspathEntry createLanguageRuntimeEntry() { 
//        Bundle x10RuntimeBundle= Platform.getBundle(X10Plugin.X10_RUNTIME_BUNDLE_ID);//PORT1.7 was x10.runtime hardcoded
//        //PORT1.7 use common algorithm now in X10RuntimeUtils instead of looking in ECLIPSE_HOME/plugins/x10.runtime. ... etc
//        IPath x10RuntimePath= X10RuntimeUtils.guessRuntimeLocation(x10RuntimeBundle);
//        if(x10RuntimePath==null){ 
//        	return null;
//        }
//        IClasspathEntry langRuntimeCPE = JavaCore.newLibraryEntry(x10RuntimePath, null, null);
//        //PORT1.7 return IClasspathEntry not IPath like previous impl (adapt to change in IMP)
//        return langRuntimeCPE;
//    }    

    /**
     * Get the entries for the runtime classpath.
     * This is used by the builder (X10Builder)
     * <br>
     * If this is overridden instead of createLanguageRuntimeEntry(), the list of classpaths here are used instead of the single value from the other.
     */
    @Override
    protected List<IClasspathEntry> createLanguageRuntimeEntries() {
        List<IClasspathEntry> entries = new ArrayList<IClasspathEntry>();
        addIfNonNull(bundleToCpath(X10DTCorePlugin.X10_RUNTIME_BUNDLE_ID), entries);
        addIfNonNull(bundleToCpath(X10DTCorePlugin.X10_CONSTRAINTS_BUNDLE_ID), entries);
        addIfNonNull(bundleToCpath(X10DTCorePlugin.X10_COMMON_BUNDLE_ID), entries);
        return entries;
    }

    private void addIfNonNull(IClasspathEntry entry, Collection<IClasspathEntry> entries) {
        if (entry != null) {
            entries.add(entry);
        }
    }

    /**
     * Take a bundleID and return the IClasspathEntry for it
     */
    private IClasspathEntry bundleToCpath(String bundleID) {
    	 Bundle bundle= Platform.getBundle(bundleID);
         //IPath path= X10RuntimeUtils.guessRuntimeLocation(bundle);
         //String jarloc = X10Util.getJarLocationForBundle(bundleID);
         IPath jarlocPath=X10RuntimeUtils.guessJarLocation(bundle);
         if(jarlocPath==null)System.out.println("Cannot find classpathentry (jar file) for "+bundleID+".  If you are a developer, have you copied jars to eclipse install?");
         
         //IPath p2=new Path(jarloc);
         if (jarlocPath == null) {
             return null;
         }
         IClasspathEntry cpe = JavaCore.newLibraryEntry(jarlocPath, null, null);
         return cpe;
    }
     
    @Override
    public void performFinish(IProgressMonitor monitor) throws CoreException, InterruptedException {
        final X10ProjectWizardFirstPage firstPage= (X10ProjectWizardFirstPage) this.getPreviousPage();
        final IProject project= firstPage.getProjectHandle();

        super.performFinish(monitor);

        // generate sample "Hello World" X10 application
        if (firstPage.isGenHello()) {
            ResourcesPlugin.getWorkspace().run(new IWorkspaceRunnable() {
                public void run(IProgressMonitor monitor) throws CoreException {
                    IFile newFile= project.getFile("src/Hello.x10");
                    IFolder srcFolder= project.getFolder("src");
                    IJavaProject javaProject= JavaCore.create(project);
                    IPackageFragmentRoot pkgFragRoot= javaProject.getPackageFragmentRoot(srcFolder);
                    IPackageFragment pkgFrag= pkgFragRoot.getPackageFragment("");

                    InputStream sourceInputStream= NewX10ClassPage.createSampleContentStream(newFile, "Hello", pkgFrag, "", new ArrayList<String>(), true, true);
                    newFile.create(sourceInputStream, true, monitor);

                    ((X10ProjectWizard) X10ProjectWizardSecondPage.this.getWizard()).selectAndReveal(newFile);
                    openResource(newFile);
                }
            }, monitor);
        }
    }
}

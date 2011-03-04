/*******************************************************************************
 * Copyright (c) 2000, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package x10dt.ui.navigator.internal;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.imp.language.LanguageRegistry;
import org.eclipse.imp.model.BuildPathChangeEvent;
import org.eclipse.imp.model.IBuildPathChangeListener;
import org.eclipse.imp.model.IBuildPathDelta;
import org.eclipse.imp.model.ICompilationUnit;
import org.eclipse.imp.model.IPathEntry;
import org.eclipse.imp.model.IPathEntry.PathEntryType;
import org.eclipse.imp.model.IPathEntryContent.PathEntryContentType;
import org.eclipse.imp.model.ISourceEntity;
import org.eclipse.imp.model.ISourceFolder;
import org.eclipse.imp.model.ISourceProject;
import org.eclipse.imp.model.ISourceRoot;
import org.eclipse.imp.model.IWorkspaceModel;
import org.eclipse.imp.model.ModelFactory;
import org.eclipse.imp.model.ModelFactory.ModelException;
import org.eclipse.imp.ui.StandardElementContentProvider;
import org.eclipse.imp.ui.navigator.ClassPathContainer;
import org.eclipse.imp.ui.navigator.PackageFragmentRootContainer;
import org.eclipse.imp.utils.BuildPathUtils;
import org.eclipse.imp.utils.LoggingUtils;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.IBasicPropertyConstants;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.progress.UIJob;

import x10dt.ui.navigator.UINavigatorPlugin;
import x10dt.ui.navigator.preferences.PreferenceConstants;

/**
 * Content provider for the PackageExplorer.
 *
 * <p>
 * Since 2.1 this content provider can provide the children for flat or hierarchical
 * layout.
 * </p>
 *
 * @see org.eclipse.jdt.ui.StandardJavaElementContentProvider
 */
public class PackageExplorerContentProvider extends StandardElementContentProvider implements IBuildPathChangeListener, IPropertyChangeListener {

	protected static final int ORIGINAL= 0;
	protected static final int PARENT= 1 << 0;
	protected static final int GRANT_PARENT= 1 << 1;
	protected static final int PROJECT= 1 << 2;

	private TreeViewer fViewer;
	private Object fInput;
	private boolean fIsFlatLayout;
	private boolean fShowLibrariesNode;
	private boolean fFoldPackages;

	private Collection fPendingUpdates;

	private UIJob fUpdateJob;

	/**
	 * Creates a new content provider for Java elements.
	 * @param provideMembers if set, members of compilation units and class files are shown
	 */
	public PackageExplorerContentProvider(boolean provideMembers) {
		super(provideMembers);
		fShowLibrariesNode= false;
		fIsFlatLayout= false;
		fFoldPackages= arePackagesFoldedInHierarchicalLayout();
		fPendingUpdates= null;
		UINavigatorPlugin.getDefault().getPreferenceStore().addPropertyChangeListener(this);

		fUpdateJob= null;
	}

	private boolean arePackagesFoldedInHierarchicalLayout(){
		return PreferenceConstants.getPreferenceStore().getBoolean(PreferenceConstants.APPEARANCE_FOLD_PACKAGES_IN_PACKAGE_EXPLORER);
	}

	protected Object getViewerInput() {
		return fInput;
	}

	/* (non-Javadoc)
	 * Method declared on IElementChangedListener.
	 */
	public void buildPathChanged(final BuildPathChangeEvent event) {
		final ArrayList runnables= new ArrayList();
		try {
			// 58952 delete project does not update Package Explorer [package explorer]
			// if the input to the viewer is deleted then refresh to avoid the display of stale elements
			if (inputDeleted(runnables))
				return;

			processDelta(event.getDelta(), runnables);
		} catch (ModelException e) {
			LoggingUtils.log(e);
		} finally {
			executeRunnables(runnables);
		}
	}

	protected final void executeRunnables(final Collection runnables) {

		// now post all collected runnables
		Control ctrl= fViewer.getControl();
		if (ctrl != null && !ctrl.isDisposed()) {
			final boolean hasPendingUpdates;
			synchronized (this) {
				hasPendingUpdates= fPendingUpdates != null && !fPendingUpdates.isEmpty();
			}
			//Are we in the UIThread? If so spin it until we are done
			if (!hasPendingUpdates && ctrl.getDisplay().getThread() == Thread.currentThread() && !fViewer.isBusy()) {
				runUpdates(runnables);
			} else {
				synchronized (this) {
					if (fPendingUpdates == null) {
						fPendingUpdates= runnables;
					} else {
						fPendingUpdates.addAll(runnables);
					}
				}
				postAsyncUpdate(ctrl.getDisplay());
			}
		}
	}
	private void postAsyncUpdate(final Display display) {
		if (fUpdateJob == null) {
			fUpdateJob= new UIJob(display, PackagesMessages.PackageExplorerContentProvider_update_job_description) {
				public IStatus runInUIThread(IProgressMonitor monitor) {
					TreeViewer viewer= fViewer;
					if (viewer != null && viewer.isBusy()) {
						schedule(100); // reschedule when viewer is busy: bug 184991
					} else {
						runPendingUpdates();
					}
					return Status.OK_STATUS;
				}
			};
			fUpdateJob.setSystem(true);
		}
		fUpdateJob.schedule();
	}

	/**
	 * Run all of the runnables that are the widget updates. Must be called in the display thread.
	 */
	public void runPendingUpdates() {
		Collection pendingUpdates;
		synchronized (this) {
			pendingUpdates= fPendingUpdates;
			fPendingUpdates= null;
		}
		if (pendingUpdates != null && fViewer != null) {
			Control control = fViewer.getControl();
			if (control != null && !control.isDisposed()) {
				runUpdates(pendingUpdates);
			}
		}
	}

	private void runUpdates(Collection runnables) {
		Iterator runnableIterator = runnables.iterator();
		while (runnableIterator.hasNext()){
			((Runnable) runnableIterator.next()).run();
		}
	}


	private boolean inputDeleted(Collection runnables) {
		if (fInput == null)
			return false;
		if (fInput instanceof ISourceEntity && ((ISourceEntity) fInput).getResource().exists())
			return false;
		if (fInput instanceof IResource && ((IResource) fInput).exists())
			return false;
//		if (fInput instanceof WorkingSetModel)
//			return false;
		if (fInput instanceof IWorkingSet) // see https://bugs.eclipse.org/bugs/show_bug.cgi?id=156239
			return false;
		postRefresh(fInput, ORIGINAL, fInput, runnables);
		return true;
	}

	/* (non-Javadoc)
	 * Method declared on IContentProvider.
	 */
	public void dispose() {
		super.dispose();
		ModelFactory.getModelRoot().removeBuildPathChangeListener(this);
		UINavigatorPlugin.getDefault().getPreferenceStore().removePropertyChangeListener(this);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.ui.StandardJavaElementContentProvider#getPackageFragmentRootContent(org.eclipse.jdt.core.ISourceRoot)
	 */
	protected Object[] getPackageFragmentRootContent(ISourceRoot root) throws ModelException {
		if (fIsFlatLayout) {
			return super.getPackageFragmentRootContent(root);
		}

		// hierarchical package mode
		ArrayList result= new ArrayList();
		getHierarchicalPackageChildren(root, null, result);
		if (!isProjectPackageFragmentRoot(root)) {
//			Object[] nonJavaResources= root.getNonJavaResources();
//			for (int i= 0; i < nonJavaResources.length; i++) {
//				result.add(nonJavaResources[i]);
//			}
		}
		return result.toArray();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.ui.StandardJavaElementContentProvider#getPackageContent(org.eclipse.jdt.core.ISourceFolder)
	 */
	protected Object[] getPackageContent(ISourceFolder fragment) throws ModelException {
		if (fIsFlatLayout) {
			return super.getPackageContent(fragment);
		}

		// hierarchical package mode
		ArrayList result= new ArrayList();

		getHierarchicalPackageChildren((ISourceRoot) fragment.getAncestor(ISourceRoot.class), fragment, result);
		Object[] nonPackages= super.getPackageContent(fragment);
		if (result.isEmpty())
			return nonPackages;
		for (int i= 0; i < nonPackages.length; i++) {
			result.add(nonPackages[i]);
		}
		return result.toArray();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.ui.StandardJavaElementContentProvider#getFolderContent(org.eclipse.core.resources.IFolder)
	 */
	protected Object[] getFolderContent(IFolder folder) throws CoreException, ModelException {
		if (fIsFlatLayout) {
			return super.getFolderContent(folder);
		}

		// hierarchical package mode
		ArrayList result= new ArrayList();

		getHierarchicalPackagesInFolder(folder, result);
		Object[] others= super.getFolderContent(folder);
		if (result.isEmpty())
			return others;
		for (int i= 0; i < others.length; i++) {
			result.add(others[i]);
		}
		return result.toArray();
	}


	public Object[] getChildren(Object parentElement) {
		try {
			if (parentElement instanceof IWorkspaceModel)
				return /*concatenate(*/getJavaProjects((IWorkspaceModel)parentElement)/*, getNonJavaProjects((IJavaModel)parentElement)/*);*/;

			if (parentElement instanceof PackageFragmentRootContainer)
				return getContainerPackageFragmentRoots((PackageFragmentRootContainer)parentElement);

			if (parentElement instanceof IProject) {
				IProject project= (IProject) parentElement;
				if (project.isAccessible())
					return project.members();
				return NO_CHILDREN;
			}

			return super.getChildren(parentElement);
		} catch (CoreException e) {
			return NO_CHILDREN;
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.ui.StandardJavaElementContentProvider#getPackageFragmentRoots(org.eclipse.jdt.core.ISourceProject)
	 */
	protected Object[] getPackageFragmentRoots(ISourceProject project) throws ModelException {
		if (!project.getProject().getRawProject().isOpen())
			return NO_CHILDREN;

		List result= new ArrayList();

		List<ISourceRoot> roots= project.getSourceRoots(LanguageRegistry.findLanguage("X10"));
		for (ISourceRoot root : roots) {
			IPathEntry classpathEntry= BuildPathUtils.getRawClasspathEntry(root);
			PathEntryType entryKind= classpathEntry.getEntryType();
			if (entryKind == PathEntryType.CONTAINER) {
				// all ClassPathContainers are added later
			} else if (fShowLibrariesNode && (entryKind == PathEntryType.ARCHIVE || entryKind == PathEntryType.VARIABLE)) {
				IResource resource= root.getResource();
				if (resource != null && project.getResource().equals(resource.getParent())) {
					// show resource as child of project, see https://bugs.eclipse.org/bugs/show_bug.cgi?id=141906
					result.add(resource);
				} else {
					// skip: will add the referenced library node later
				}
			} else {
				if (isProjectPackageFragmentRoot(root)) {
					// filter out package fragments that correspond to projects and
					// replace them with the package fragments directly
					Object[] fragments= getPackageFragmentRootContent(root);
					for (int j= 0; j < fragments.length; j++) {
						result.add(fragments[j]);
					}
				} else {
					result.add(root);
				}
			}
		}

		if (fShowLibrariesNode) {
			result.add(new LibraryContainer(project));
		}

		// separate loop to make sure all containers are on the classpath (even empty ones)
		List<IPathEntry> rawClasspath= project.getBuildPath(LanguageRegistry.findLanguage("X10"));
		for (IPathEntry classpathEntry : rawClasspath) {
			if (classpathEntry.getEntryType() == PathEntryType.CONTAINER) {
				ClassPathContainer cpc = new ClassPathContainer(project, classpathEntry);
				for(ISourceRoot root : cpc.getPackageFragmentRoots())
				{
					if(result.contains(root))
					{
						result.remove(root);
					}
				}
				
				result.add(cpc);
			}
		}
//		Object[] resources= project.getNonJavaResources();
//		for (int i= 0; i < resources.length; i++) {
//			result.add(resources[i]);
//		}
		return result.toArray();
	}

	private Object[] getContainerPackageFragmentRoots(PackageFragmentRootContainer container) {
		return container.getChildren();
	}

//	private Object[] getNonJavaProjects(IJavaModel model) throws ModelException {
//		return model.getNonJavaResources();
//	}

	protected Object internalGetParent(Object element) {
		if (!fIsFlatLayout && element instanceof ISourceFolder) {
			return getHierarchicalPackageParent((ISourceFolder) element);
		} else if (element instanceof ISourceRoot) {
			// since we insert logical package containers we have to fix
			// up the parent for package fragment roots so that they refer
			// to the container and containers refer to the project
			ISourceRoot root= (ISourceRoot)element;

			try {
				IPathEntry entry= BuildPathUtils.getRawClasspathEntry(root);
				PathEntryType entryKind= entry.getEntryType();
				if (entryKind == PathEntryType.CONTAINER) {
					return new ClassPathContainer(root.getProject(), entry);
				} else if (fShowLibrariesNode && (entryKind == PathEntryType.ARCHIVE || entryKind == PathEntryType.VARIABLE)) {
					return new LibraryContainer(root.getProject());
				}
			} catch (ModelException e) {
				// fall through
			}
		} else if (element instanceof PackageFragmentRootContainer) {
			return ((PackageFragmentRootContainer)element).getJavaProject();
		}
		return super.internalGetParent(element);
	}

	/* (non-Javadoc)
	 * Method declared on IContentProvider.
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		super.inputChanged(viewer, oldInput, newInput);
		fViewer= (TreeViewer)viewer;
		if (oldInput == null && newInput != null) {
			ModelFactory.getModelRoot().addBuildPathChangeListener(this, null);
		} else if (oldInput != null && newInput == null) {
			ModelFactory.getModelRoot().removeBuildPathChangeListener(this);
		}
		fInput= newInput;
	}

	// hierarchical packages
	/**
	 * Returns the hierarchical packages inside a given fragment or root.
	 *
	 * @param parent the parent package fragment root
	 * @param fragment the package to get the children for or 'null' to get the children of the root
	 * @param result Collection where the resulting elements are added
	 * @throws ModelException if fetching the children fails
	 */
	private void getHierarchicalPackageChildren(ISourceRoot parent, ISourceFolder fragment, Collection result) throws ModelException {
		ISourceEntity[] children= parent.getChildren();
		String prefix= fragment != null ? fragment.getName() + '.' : ""; //$NON-NLS-1$
		int prefixLen= prefix.length();
		for (int i= 0; i < children.length; i++) {
			ISourceFolder curr= (ISourceFolder) children[i];
			String name= curr.getName();
			if (name.startsWith(prefix) && name.length() > prefixLen && name.indexOf('.', prefixLen) == -1) {
				if (fFoldPackages) {
					curr= getFolded(children, curr);
				}
				result.add(curr);
			} else if (fragment == null && BuildPathUtils.isDefaultPackage(curr.getName())) {
				result.add(curr);
			}
		}
	}

	/**
	 * Returns the hierarchical packages inside a given folder.
	 * @param folder The parent folder
	 * @param result Collection where the resulting elements are added
	 * @throws CoreException thrown when elements could not be accessed
	 */
	private void getHierarchicalPackagesInFolder(IFolder folder, Collection result) throws CoreException, ModelException {
		IResource[] resources= folder.members();
		for (int i= 0; i < resources.length; i++) {
			IResource resource= resources[i];
			if (resource instanceof IFolder) {
				IFolder curr= (IFolder) resource;
				ISourceEntity element= ModelFactory.create(curr);
				if (element instanceof ISourceFolder) {
					if (fFoldPackages) {
						ISourceFolder fragment= (ISourceFolder) element;
						ISourceRoot root= (ISourceRoot) fragment.getAncestor(ISourceRoot.class);
						element= getFolded(root.getChildren(), fragment);
					}
					result.add(element);
				}
			}
		}
	}

	public Object getHierarchicalPackageParent(Object object) {
		ISourceRoot parent= null;
		String name = null;
		if(object instanceof IResource)
		{
			try
			{
				name= ((IResource)object).getName();
				ISourceEntity child = ModelFactory.open(((IResource)object).getParent());
				parent = (ISourceRoot) child.getAncestor(ISourceRoot.class);
			}
			catch(ModelException e)
			{
				return null;
			}
		}
		
		else 
		{
			ISourceEntity child = (ISourceEntity)object;
			name= child.getName();
			parent= (ISourceRoot) child.getAncestor(ISourceRoot.class);
		}
		 
		int index= name.lastIndexOf('.');
		if (index != -1) {
			String realParentName= name.substring(0, index);
			ISourceFolder element = null;
			for(ISourceFolder sf : parent.getChildren())
			{
				if(sf.getName().equals(realParentName))
				{
					element = sf;
					break;
				}
			}
			
			if (element.getResource() != null && element.getResource().exists()) {
				try {
					if (fFoldPackages && isEmpty(element) && findSinglePackageChild(element, parent.getChildren()) != null) {
						return getHierarchicalPackageParent(element);
					}
				} catch (ModelException e) {
					// ignore
				}
				return element;
			} else { // bug 65240
				IResource resource= element.getResource();
				if (resource != null) {
					return resource;
				}
			}
		}
		if (parent != null && parent.getResource() instanceof IProject) {
			return parent.getProject();
		}
		return parent;
	}

	private static ISourceFolder getFolded(ISourceEntity[] children, ISourceFolder pack) throws ModelException {
		while (isEmpty(pack)) {
			ISourceFolder collapsed= findSinglePackageChild(pack, children);
			if (collapsed == null) {
				return pack;
			}
			pack= collapsed;
		}
		return pack;
	}

	private static boolean isEmpty(ISourceFolder fragment) throws ModelException {
		return fragment.getChildren().length == 0;
//		return !fragment.containsJavaResources() && fragment.getNonJavaResources().length == 0;
	}

	private static ISourceFolder findSinglePackageChild(ISourceFolder fragment, ISourceEntity[] children) {
		String prefix= fragment.getName() + '.';
		int prefixLen= prefix.length();
		ISourceFolder found= null;
		for (int i= 0; i < children.length; i++) {
			ISourceEntity element= children[i];
			String name= element.getName();
			if (name.startsWith(prefix) && name.length() > prefixLen && name.indexOf('.', prefixLen) == -1) {
				if (found == null) {
					found= (ISourceFolder) element;
				} else {
					return null;
				}
			}
		}
		return found;
	}

	// ------ delta processing ------
	
	private boolean processDeltaForDelete(IResource resource, IBuildPathDelta delta, Collection runnables) throws ModelException
	{
		int kind= delta.getKind();
		int flags= delta.getFlags();
		
		if (!(resource instanceof IWorkspaceRoot) && !(resource instanceof IProject)) {
			IProject proj= resource.getProject();
			if (proj == null || !proj.isOpen()) // TODO: Not needed if parent already did the 'open' check!
				return false;
		}
		
		if (resource instanceof IFolder) {
			if (!fIsFlatLayout) {
				if (kind == IBuildPathDelta.REMOVED) {
					final Object parent = getHierarchicalPackageParent(resource);
//					if (parent instanceof ISourceRoot) {
//						postRemove(resource,  runnables);
//						return false;
//					} else {
						postRefresh(internalGetParent(parent), GRANT_PARENT, resource, runnables);
						return true;
//					}
				}
				
				handleAffectedChildren(delta, resource, runnables);
				return false;
			}
		}

		if (kind == IBuildPathDelta.REMOVED) {
			Object parent= internalGetParent(resource);
			if (resource instanceof ISourceFolder) {
				// refresh package fragment root to allow filtering empty (parent) packages: bug 72923
				if (fViewer.testFindItem(parent) != null)
					postRefresh(parent, PARENT, resource, runnables);
				return true;
				
//			} else if (element instanceof ISourceRoot
//					&& ((ISourceRoot)element).getContentType() != PathEntryContentType.SOURCE) {
//				// libs and class folders can show up twice (in library container and as resource at original location)
//				if (resource != null)
//					postRemove(resource, runnables);
			}

			postRemove(resource, runnables);
			if (parent instanceof ISourceFolder)
				postUpdateIcon((ISourceFolder)parent, runnables);
			// we are filtering out empty subpackages, so we
			// a package becomes empty we remove it from the viewer.
			if (isPackageFragmentEmpty((ISourceFolder)parent)) {
				if (fViewer.testFindItem(parent) != null)
					postRefresh(internalGetParent(parent), GRANT_PARENT, resource, runnables);
				return true;
			}
			return false;
		}

		handleAffectedChildren(delta, resource, runnables);
		return false;
	}
	
	private boolean processDeltaForEntity(ISourceEntity element, IBuildPathDelta delta, Collection runnables) throws ModelException
	{
		int kind= delta.getKind();
		int flags= delta.getFlags();
		
		if (!(element instanceof IWorkspaceModel) && !(element instanceof ISourceProject)) {
			ISourceProject proj= element.getProject();
			if (proj == null || !proj.getRawProject().isOpen()) // TODO: Not needed if parent already did the 'open' check!
				return false;
		}

		if (element instanceof ISourceFolder) {
			if ((flags & (IBuildPathDelta.F_CONTENT | IBuildPathDelta.F_CHILDREN)) == IBuildPathDelta.F_CONTENT) {
				if (!fIsFlatLayout) {
					Object parent = getHierarchicalPackageParent((ISourceFolder) element);
					if (!(parent instanceof ISourceRoot)) {
						postRefresh(internalGetParent(parent), GRANT_PARENT, element, runnables);
						return true;
					}
				}
				// content change, without children info (for example resource added/removed to class folder package)
				postRefresh(internalGetParent(element), PARENT, element, runnables);
				return true;
			}

			if (!fIsFlatLayout) {
				if (kind == IBuildPathDelta.ADDED) {
					final Object parent = getHierarchicalPackageParent((ISourceFolder) element);
					if (parent instanceof ISourceRoot) {
						if (fFoldPackages) {
							postRefresh(parent, PARENT, element, runnables);
							return true;
						} else {
							postAdd(parent, element, runnables);
							return false;
						}
					} else {
						postRefresh(internalGetParent(parent), GRANT_PARENT, element, runnables);
						return true;
					}
				}
				handleAffectedChildren(delta, element, runnables);
				return false;
			}
		}

		if (element instanceof ICompilationUnit) {
			ICompilationUnit cu= (ICompilationUnit) element;
			
			if (!getProvideMembers() && kind == IBuildPathDelta.CHANGED) {
				return false;
			}

			if (kind == IBuildPathDelta.CHANGED && !isStructuralCUChange(flags)) {
				return false; // test moved ahead
			}

			if (!isOnClassPath(cu)) { // TODO: isOnClassPath expensive! Should be put after all cheap tests
				return false;
			}

		}

		if (element instanceof ISourceProject) {
			// handle open and closing of a project
			if ((flags & (IBuildPathDelta.F_CLOSED | IBuildPathDelta.F_OPENED)) != 0) {
				postRefresh(element, ORIGINAL, element, runnables);
				return false;
			}
			// if the class path has changed we refresh the entire project
			if ((flags & IBuildPathDelta.F_RESOLVED_CLASSPATH_CHANGED) != 0) {
				postRefresh(element, ORIGINAL, element, runnables);
				return false;
			}
			// if added it could be that the corresponding IProject is already shown. Remove it first.
			// bug 184296
			if (kind == IBuildPathDelta.ADDED) {
				postRemove(element.getResource(), runnables);
				postAdd(element.getParent(), element, runnables);
				return false;
			}
		}

		if (kind == IBuildPathDelta.REMOVED) {
			Object parent= internalGetParent(element);
			if (element instanceof ISourceFolder) {
				// refresh package fragment root to allow filtering empty (parent) packages: bug 72923
				if (fViewer.testFindItem(parent) != null)
					postRefresh(parent, PARENT, element, runnables);
				return true;
				
			} else if (element instanceof ISourceRoot
					&& ((ISourceRoot)element).getContentType() != PathEntryContentType.SOURCE) {
				// libs and class folders can show up twice (in library container and as resource at original location)
				IResource resource= element.getResource();
				if (resource != null)
					postRemove(resource, runnables);
			}

			postRemove(element, runnables);
			if (parent instanceof ISourceFolder)
				postUpdateIcon((ISourceFolder)parent, runnables);
			// we are filtering out empty subpackages, so we
			// a package becomes empty we remove it from the viewer.
			if (isPackageFragmentEmpty(element.getParent())) {
				if (fViewer.testFindItem(parent) != null)
					postRefresh(internalGetParent(parent), GRANT_PARENT, element, runnables);
				return true;
			}
			return false;
		}

		if (kind == IBuildPathDelta.ADDED) {
			Object parent= internalGetParent(element);
			// we are filtering out empty subpackages, so we
			// have to handle additions to them specially.
			if (parent instanceof ISourceFolder) {
				Object grandparent= internalGetParent(parent);
				// 1GE8SI6: ITPJUI:WIN98 - Rename is not shown in Packages View
				// avoid posting a refresh to an invisible parent
				if (parent.equals(fInput)) {
					postRefresh(parent, PARENT, element, runnables);
				} else {
					// refresh from grandparent if parent isn't visible yet
					if (fViewer.testFindItem(parent) == null)
						postRefresh(grandparent, GRANT_PARENT, element, runnables);
					else {
						postRefresh(parent, PARENT, element, runnables);
					}
				}
				return true;
			} else {
				if (element instanceof ISourceRoot
						&& ((ISourceRoot)element).getContentType() != PathEntryContentType.SOURCE) {
					// libs and class folders can show up twice (in library container or under project, and as resource at original location)
					IResource resource= element.getResource();
					if (resource != null) {
						Object resourceParent= super.internalGetParent(resource);
						if (resourceParent != null) {
							ISourceProject proj= element.getProject();
							if (fShowLibrariesNode || !resourceParent.equals(proj)) {
								postAdd(resourceParent, resource, runnables);
							}
						}
					}
				}
				postAdd(parent, element, runnables);
			}
		}

		if (element instanceof ICompilationUnit /* || element == ISourceEntity.CLASS_FILE*/) {
			if (kind == IBuildPathDelta.CHANGED) {
				// isStructuralCUChange already performed above
				postRefresh(element, ORIGINAL, element, runnables);
			}
			return false;
		}

		if (element instanceof ISourceRoot) {
			// the contents of an external JAR or class folder has changed
			if ((flags & IBuildPathDelta.F_ARCHIVE_CONTENT_CHANGED) != 0) {
				postRefresh(element, ORIGINAL, element, runnables);
				return false;
			}
			if ((flags & (IBuildPathDelta.F_CONTENT | IBuildPathDelta.F_CHILDREN)) == IBuildPathDelta.F_CONTENT) {
				// content change, without children info (for example resource added/removed to class folder package)
				postRefresh(internalGetParent(element), PARENT, element, runnables);
				return true;
			}

			// the source attachment of a JAR has changed
			if ((flags & (IBuildPathDelta.F_SOURCEATTACHED | IBuildPathDelta.F_SOURCEDETACHED)) != 0)
				postUpdateIcon(element, runnables);

			if (isClassPathChange(delta)) {
				 // throw the towel and do a full refresh of the affected java project.
				postRefresh(element.getProject(), PROJECT, element, runnables);
				return true;
			}
		}

		handleAffectedChildren(delta, element, runnables);
		return false;
	}
	
	
	
	/**
	 * Processes a delta recursively. When more than two children are affected the
	 * tree is fully refreshed starting at this node.
	 *
	 * @param delta the delta to process
	 * @param runnables the resulting view changes as runnables (type {@link Runnable})
	 * @return true is returned if the conclusion is to refresh a parent of an element. In that case no siblings need
	 * to be processed
	 * @throws ModelException thrown when the access to an element failed
	 */
	private boolean processDelta(IBuildPathDelta delta, Collection runnables) throws ModelException {
		IPath path= delta.getElement();
		
		IWorkspaceRoot root = (IWorkspaceRoot)ModelFactory.getModelRoot().getResource();
		path = path.makeRelativeTo(root.getLocation());
		IResource resource = root.findMember(path);
		
		// external to workspace
		if(resource == null)
		{
			File file = new File(path.toString());
//			if(file.isDirectory())
//			{
//				return processDeltaForEntity(new SourceFolder(), delta, runnables);
//			}
//			
//			else if(file.extension().equals())
//			{
//				return processDeltaForEntity(new SourceFolder(), delta, runnables);
//			}
//			
//			else
//			{
//				BuildPathUtils.getSourceRoot()
//				return processDeltaForEntity(new SourceRoot(), delta, runnables);
//			}
			return false;
		}
		
		// entity deleted
		else if(!resource.exists())
		{
			return processDeltaForDelete(resource, delta, runnables);
		}
		
		else
		{
			ISourceEntity element = ModelFactory.open(resource);
			return processDeltaForEntity(element, delta, runnables);
		}
	}

	private static boolean isStructuralCUChange(int flags) {
		// No refresh on working copy creation (F_PRIMARY_WORKING_COPY)
		return (flags & IBuildPathDelta.F_CHILDREN) != 0 || (flags & (IBuildPathDelta.F_CONTENT | IBuildPathDelta.F_FINE_GRAINED)) == IBuildPathDelta.F_CONTENT;
	}

	/* package */ void handleAffectedChildren(IBuildPathDelta delta, ISourceEntity element, Collection runnables) throws ModelException {
		int count= 0;

		IResourceDelta[] resourceDeltas= delta.getResourceDeltas();
		if (resourceDeltas != null) {
			for (int i= 0; i < resourceDeltas.length; i++) {
				int kind= resourceDeltas[i].getKind();
				if (kind == IResourceDelta.ADDED || kind == IResourceDelta.REMOVED) {
					count++;
				}
			}
		}
		IBuildPathDelta[] affectedChildren= delta.getAffectedChildren();
		for (int i= 0; i < affectedChildren.length; i++) {
			int kind= affectedChildren[i].getKind();
			if (kind == IBuildPathDelta.ADDED || kind == IBuildPathDelta.REMOVED) {
				count++;
			}
		}

		if (count > 1) {
			// more than one child changed, refresh from here downwards
			if (element instanceof ISourceFolder) {
				// a package fragment might become non empty refresh from the parent
				ISourceEntity parent= (ISourceEntity) internalGetParent(element);
				// 1GE8SI6: ITPJUI:WIN98 - Rename is not shown in Packages View
				// avoid posting a refresh to an invisible parent
				if (element.equals(fInput)) {
					postRefresh(element, ORIGINAL, element, runnables);
				} else {
					postRefresh(parent, PARENT, element, runnables);
				}
			} else if (element instanceof ISourceRoot) {
				Object toRefresh= internalGetParent(element);
				postRefresh(toRefresh, ORIGINAL, toRefresh, runnables);
			} else {
				postRefresh(element, ORIGINAL, element, runnables);
			}
			return;
		}
		if (resourceDeltas != null) {
			for (int i= 0; i < resourceDeltas.length; i++) {
				if (processResourceDelta(resourceDeltas[i], element, runnables)) {
					return; // early return, element got refreshed
				}
			}
		}
		for (int i= 0; i < affectedChildren.length; i++) {
			if (processDelta(affectedChildren[i], runnables)) {
				return; // early return, element got refreshed
			}
		}
	}

	
	/* package */ void handleAffectedChildren(IBuildPathDelta delta, IResource element, Collection runnables) throws ModelException {
		int count= 0;

		IResourceDelta[] resourceDeltas= delta.getResourceDeltas();
		if (resourceDeltas != null) {
			for (int i= 0; i < resourceDeltas.length; i++) {
				int kind= resourceDeltas[i].getKind();
				if (kind == IResourceDelta.ADDED || kind == IResourceDelta.REMOVED) {
					count++;
				}
			}
		}
		IBuildPathDelta[] affectedChildren= delta.getAffectedChildren();
		for (int i= 0; i < affectedChildren.length; i++) {
			int kind= affectedChildren[i].getKind();
			if (kind == IBuildPathDelta.ADDED || kind == IBuildPathDelta.REMOVED) {
				count++;
			}
		}

		if (count > 1) {
			// more than one child changed, refresh from here downwards
			if (element instanceof IFolder) {
				// a package fragment might become non empty refresh from the parent
				ISourceEntity parent= (ISourceEntity) internalGetParent(element);
				// 1GE8SI6: ITPJUI:WIN98 - Rename is not shown in Packages View
				// avoid posting a refresh to an invisible parent
				if (element.equals(fInput)) {
					postRefresh(element, ORIGINAL, element, runnables);
				} else {
					postRefresh(parent, PARENT, element, runnables);
				}
//			} else if (element instanceof ISourceRoot) {
//				Object toRefresh= internalGetParent(element);
//				postRefresh(toRefresh, ORIGINAL, toRefresh, runnables);
			} else {
				postRefresh(element, ORIGINAL, element, runnables);
			}
			return;
		}
		if (resourceDeltas != null) {
			for (int i= 0; i < resourceDeltas.length; i++) {
				if (processResourceDelta(resourceDeltas[i], element, runnables)) {
					return; // early return, element got refreshed
				}
			}
		}
		for (int i= 0; i < affectedChildren.length; i++) {
			if (processDelta(affectedChildren[i], runnables)) {
				return; // early return, element got refreshed
			}
		}
	}
	protected void processAffectedChildren(IBuildPathDelta[] affectedChildren, Collection runnables) throws ModelException {
		for (int i= 0; i < affectedChildren.length; i++) {
			processDelta(affectedChildren[i], runnables);
		}
	}

	private boolean isOnClassPath(ICompilationUnit element) {
		ISourceProject project= element.getProject();
		if (project == null || !project.getResource().exists())
			return false;
//		return project.isOnClasspath(element);
		return true;
	}

	/**
	 * Updates the package icon
	 * @param element the element to update
	 * @param runnables the resulting view changes as runnables (type {@link Runnable})
	 */
	 private void postUpdateIcon(final Object element, Collection runnables) {
		 runnables.add(new Runnable() {
			public void run() {
				// 1GF87WR: ITPUI:ALL - SWTEx + NPE closing a workbench window.
				fViewer.update(element, new String[]{IBasicPropertyConstants.P_IMAGE});
			}
		});
	 }

	/**
	 * Process a resource delta.
	 *
	 * @param delta the delta to process
	 * @param parent the parent
	 * @param runnables the resulting view changes as runnables (type {@link Runnable})
	 * @return true if the parent got refreshed
	 */
	private boolean processResourceDelta(IResourceDelta delta, Object parent, Collection runnables) {
		int status= delta.getKind();
		int flags= delta.getFlags();

		IResource resource= delta.getResource();
		// filter out changes affecting the output folder
		if (resource == null)
			return false;

		// this could be optimized by handling all the added children in the parent
		if ((status & IResourceDelta.REMOVED) != 0) {
			if (parent instanceof ISourceFolder) {
				// refresh one level above to deal with empty package filtering properly
				postRefresh(internalGetParent(parent), PARENT, parent, runnables);
				return true;
			} else {
				postRemove(resource, runnables);
				return false;
			}
		}
		if ((status & IResourceDelta.ADDED) != 0) {
			if (parent instanceof ISourceFolder) {
				// refresh one level above to deal with empty package filtering properly
				postRefresh(internalGetParent(parent), PARENT, parent, runnables);
				return true;
			} else {
				postAdd(parent, resource, runnables);
				return false;
			}
		}
		if ((status & IResourceDelta.CHANGED) != 0) {
			if ((flags & IResourceDelta.TYPE) != 0) {
				postRefresh(parent, PARENT, resource, runnables);
				return true;
			}
		}
		// open/close state change of a project
		if ((flags & IResourceDelta.OPEN) != 0) {
			postProjectStateChanged(internalGetParent(parent), runnables);
			return true;
		}
		IResourceDelta[] resourceDeltas= delta.getAffectedChildren();

		int count= 0;
		for (int i= 0; i < resourceDeltas.length; i++) {
			int kind= resourceDeltas[i].getKind();
			if (kind == IResourceDelta.ADDED || kind == IResourceDelta.REMOVED) {
				count++;
				if (count > 1) {
					postRefresh(parent, PARENT, resource, runnables);
					return true;
				}
			}
		}
		for (int i= 0; i < resourceDeltas.length; i++) {
			if (processResourceDelta(resourceDeltas[i], resource, runnables)) {
				return false; // early return, element got refreshed
			}
		}
		return false;
	}

	public void setIsFlatLayout(boolean state) {
		fIsFlatLayout= state;
	}

	public void setShowLibrariesNode(boolean state) {
		fShowLibrariesNode= state;
	}

	private void postRefresh(Object root, int relation, Object affectedElement, Collection runnables) {
		// JFace doesn't refresh when object isn't part of the viewer
		// Therefore move the refresh start down to the viewer's input
		if (isParent(root, fInput) || root instanceof IWorkspaceModel || root instanceof IWorkspaceRoot)
			root= fInput;
		List toRefresh= new ArrayList(1);
		toRefresh.add(root);
		augmentElementToRefresh(toRefresh, relation, affectedElement);
		postRefresh(toRefresh, true, runnables);
	}

	/**
	 * Can be implemented by subclasses to add additional elements to refresh
	 *
	 * @param toRefresh the elements to refresh
	 * @param relation the relation to the affected element ({@link #GRANT_PARENT}, {@link #PARENT}, {@link #ORIGINAL}, {@link #PROJECT})
	 * @param affectedElement the affected element
	 */
	protected void augmentElementToRefresh(List toRefresh, int relation, Object affectedElement) {
	}

	private boolean isParent(Object root, Object child) {
		Object parent= getParent(child);
		if (parent == null)
			return false;
		if (parent.equals(root))
			return true;
		return isParent(root, parent);
	}

	protected void postRefresh(final List toRefresh, final boolean updateLabels, Collection runnables) {
		runnables.add(new Runnable() {
			public void run() {
				for (Iterator iter= toRefresh.iterator(); iter.hasNext();) {
					fViewer.refresh(iter.next(), updateLabels);
				}
			}
		});
	}

	protected void postAdd(final Object parent, final Object element, Collection runnables) {
		runnables.add(new Runnable() {
			public void run() {
				Widget[] items= fViewer.testFindItems(element);
				for (int i= 0; i < items.length; i++) {
					Widget item= items[i];
					if (item instanceof TreeItem && !item.isDisposed()) {
						TreeItem parentItem= ((TreeItem) item).getParentItem();
						if (parentItem != null && !parentItem.isDisposed() && parent.equals(parentItem.getData())) {
							return; // no add, element already added (most likely by a refresh)
						}
					}
				}
				fViewer.add(parent, element);
			}
		});
	}

	protected void postRemove(final Object element, Collection runnables) {
		runnables.add(new Runnable() {
			public void run() {
				fViewer.remove(element);
			}
		});
	}

	protected void postProjectStateChanged(final Object root, Collection runnables) {
		runnables.add(new Runnable() {
			public void run() {
				fViewer.refresh(root, true);
				// trigger a synthetic selection change so that action refresh their
				// enable state.
				fViewer.setSelection(fViewer.getSelection());
			}
		});
	}


	/*
	 * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent event) {
		if (arePackagesFoldedInHierarchicalLayout() != fFoldPackages){
			fFoldPackages= arePackagesFoldedInHierarchicalLayout();
			if (fViewer != null && !fViewer.getControl().isDisposed()) {
				fViewer.getControl().setRedraw(false);
				Object[] expandedObjects= fViewer.getExpandedElements();
				fViewer.refresh();
				fViewer.setExpandedElements(expandedObjects);
				fViewer.getControl().setRedraw(true);
			}
		}
	}
}

package org.eclipse.imp.x10dt.ui.tests.utils;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.osgi.framework.Bundle;

public class ProjectUtils {
	public static IProject copyProjectIntoWorkspace(Bundle srcBundle, Path path)
			throws CoreException, IOException, URISyntaxException {
		URL url = FileLocator.find(srcBundle, path, null);
		url = FileLocator.toFileURL(url);

		IWorkspaceRoot wsRoot = ResourcesPlugin.getWorkspace().getRoot();

		FileUtils.copyDirectory(new File(url.toURI()), new File(wsRoot
				.getLocation().toPortableString()
				+ File.separatorChar + path.lastSegment()));

		IProject project = wsRoot.getProject(path.lastSegment());
		project.create(null);
		project.open(null);

		return project;
	}

	public static IResource copyFolderIntoContainer(Bundle srcBundle,
			IContainer container, Path path) throws CoreException, IOException,
			URISyntaxException {
		URL url = FileLocator.find(srcBundle, path, null);
		url = FileLocator.toFileURL(url);

		FileUtils.copyDirectory(new File(url.toURI()), new File(container
				.getLocation().toPortableString()
				+ File.separatorChar + path.lastSegment()));

		return container.getFolder(path);
	}

	public static IFile copyFileIntoContainer(Bundle srcBundle,
			IContainer container, Path path) throws CoreException, IOException,
			URISyntaxException {
		URL url = FileLocator.find(srcBundle, path, null);
		url = FileLocator.toFileURL(url);

		FileUtils.copyFile(new File(url.toURI()), new File(container
				.getLocation().toPortableString()
				+ File.separatorChar + path.lastSegment()));
		
		FileUtils.copyDirectory(new File(url.toURI()), new File(container
				.getLocation().toPortableString()
				+ File.separatorChar + path.lastSegment()));

		return container.getFile(path);
	}
}

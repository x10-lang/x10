package org.eclipse.imp.x10dt.ui.launch.java.builder;

import java.io.File;
import java.util.Collection;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.imp.x10dt.ui.launch.core.Constants;
import org.eclipse.imp.x10dt.ui.launch.core.builder.target_op.IX10BuilderFileOp;
import org.eclipse.imp.x10dt.ui.launch.core.utils.ICountableIterable;
import org.eclipse.jdt.core.JavaCore;

public class X10JavaBuilderOp implements IX10BuilderFileOp {

	private IProject fProject;
	private X10JavaBuilder fBuilder;
	
	
	public X10JavaBuilderOp(IProject project, X10JavaBuilder builder){
		this.fProject = project;
		this.fBuilder = builder;
	}
	
	
	@Override
	public void archive(IProgressMonitor monitor) throws CoreException {
		// NoOp for Java Backend

	}

	@Override
	public void cleanFiles(ICountableIterable<IFile> files, SubMonitor monitor)
			throws CoreException {
		for(IFile file: files){
			File gen = fBuilder.getMainGeneratedFile(JavaCore.create(fProject), file);
			if (gen != null) {
				IPath javaPath = new Path(gen.getAbsolutePath());
				IPath classPath = javaPath.removeFileExtension().addFileExtension(Constants.CLASS_EXT.substring(1));
				IFileStore javaFileStore = EFS.getLocalFileSystem().getStore(javaPath);
				if (javaFileStore.fetchInfo().exists()) {
					javaFileStore.delete(EFS.NONE, monitor);
				}
				IFileStore classfileStore = EFS.getLocalFileSystem().getStore(classPath);
				if (classfileStore.fetchInfo().exists()) {
					classfileStore.delete(EFS.NONE, monitor);
				}
			}
		}

	}

	@Override
	public boolean compile(IProgressMonitor monitor) throws CoreException {
			return true; //NoOp for Java Backend -- This is done using the post-compilation goal. See JavaBuilderExtensionInfo
	}

	@Override
	public void copyToOutputDir(Collection<IFile> files, SubMonitor monitor)
			throws CoreException {
		// NoOp for Java Backend

	}

	@Override
	public boolean hasAllPrerequisites() {
		return true;
	}

	@Override
	public void transfer(Collection<File> files, IProgressMonitor monitor)
			throws CoreException {
		// NoOp for Jaca Backend

	}

}

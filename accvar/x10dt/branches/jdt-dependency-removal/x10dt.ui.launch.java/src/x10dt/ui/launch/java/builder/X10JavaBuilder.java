package x10dt.ui.launch.java.builder;

import java.io.File;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import x10.ExtensionInfo;
import x10dt.ui.launch.core.Constants;
import x10dt.ui.launch.core.builder.AbstractX10Builder;
import x10dt.ui.launch.core.builder.target_op.IX10BuilderFileOp;
import x10dt.ui.launch.core.utils.IFilter;

public class X10JavaBuilder extends AbstractX10Builder {
	
	public ExtensionInfo createExtensionInfo(String classPath,
			List<File> sourcePath, String localOutputDir,
			boolean withMainMethod, IProgressMonitor monitor) {
		final ExtensionInfo extInfo = new JavaBuilderExtensionInfo(this);
	    buildOptions(classPath, sourcePath, localOutputDir, extInfo.getOptions(), withMainMethod);
	    return extInfo;
	}

	public IFilter<IFile> createNativeFilesFilter() {
		return new IFilter<IFile>(){

			public boolean accepts(IFile element) {
				// -- We do not need a filter for the Java backend.
				return false;
			}
			
		};
	}

	public IX10BuilderFileOp createX10BuilderFileOp() throws CoreException {
		return new X10JavaBuilderOp(getProject(), this);
	}

	public String getFileExtension() {
		return Constants.JAVA_EXT;
	}
	
	  
	 
	  

}

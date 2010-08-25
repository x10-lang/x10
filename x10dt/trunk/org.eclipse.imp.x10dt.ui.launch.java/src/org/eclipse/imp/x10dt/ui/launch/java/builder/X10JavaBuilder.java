package org.eclipse.imp.x10dt.ui.launch.java.builder;

import java.io.File;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.imp.preferences.IPreferencesService;
import org.eclipse.imp.x10dt.core.X10DTCorePlugin;
import org.eclipse.imp.x10dt.core.preferences.generated.X10Constants;
import org.eclipse.imp.x10dt.ui.launch.core.Constants;
import org.eclipse.imp.x10dt.ui.launch.core.builder.AbstractX10Builder;
import org.eclipse.imp.x10dt.ui.launch.core.builder.target_op.IX10BuilderFileOp;
import org.eclipse.imp.x10dt.ui.launch.core.utils.IFilter;

import polyglot.main.Options;
import x10.Configuration;
import x10.ExtensionInfo;
import x10.config.ConfigurationError;
import x10.config.OptionError;

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
	
	  // --- Private code
	  
	  private void buildOptions(final String classPath, final List<File> sourcePath, final String localOutputDir,
	                            final Options options, final boolean withMainMethod) {
	    options.assertions = true;
	    options.classpath = classPath;
	    options.output_classpath = options.classpath;
	    options.serialize_type_info = false;
	    options.output_directory = new File(localOutputDir);
	    options.source_path = sourcePath;
	    options.compile_command_line_only = true;
	   
	    final IPreferencesService prefService = X10DTCorePlugin.getInstance().getPreferencesService();
	    // Compiler prefs
	    Configuration.STATIC_CALLS = prefService.getBooleanPreference(X10Constants.P_STATICCALLS);
	    Configuration.VERBOSE_CALLS = prefService.getBooleanPreference(X10Constants.P_VERBOSECALLS);
	    options.assertions = prefService.getBooleanPreference(X10Constants.P_PERMITASSERT);
	    final String additionalOptions = prefService.getStringPreference(X10Constants.P_ADDITIONALCOMPILEROPTIONS);
	    if ((additionalOptions != null) && (additionalOptions.length() > 0)) {
	      // First initialize to default values.
	      Configuration.DEBUG = false;
	      Configuration.CHECK_INVARIANTS = false;
	      Configuration.ONLY_TYPE_CHECKING = false;
	      Configuration.NO_CHECKS = false;
	      Configuration.FLATTEN_EXPRESSIONS = false;
	      for (final String opt : additionalOptions.split("\\s")) { ////$NON-NLS-1$
	        try {
	          Configuration.parseArgument(opt);
	        } catch (OptionError except) {
	         // CppLaunchCore.log(IStatus.ERROR,  NLS.bind(LaunchMessages.XCB_OptionError, opt), except);
	        } catch (ConfigurationError except) {
	         // CppLaunchCore.log(IStatus.ERROR,  NLS.bind(LaunchMessages.XCB_ConfigurationError, opt), except);
	        }
	      }
	    }
	    // Optimization prefs
	    Configuration.OPTIMIZE = prefService.getBooleanPreference(X10Constants.P_OPTIMIZE);
	    Configuration.LOOP_OPTIMIZATIONS = prefService.getBooleanPreference(X10Constants.P_LOOPOPTIMIZATIONS);
	    Configuration.INLINE_OPTIMIZATIONS = prefService.getBooleanPreference(X10Constants.P_INLINEOPTIMIZATIONS);
	    Configuration.CLOSURE_INLINING = prefService.getBooleanPreference(X10Constants.P_CLOSUREINLINING);
	    Configuration.WORK_STEALING = prefService.getBooleanPreference(X10Constants.P_WORKSTEALING);
	  }
	  

}

package x10dt.core.utils;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.imp.preferences.IPreferencesService;
import org.eclipse.imp.preferences.PreferencesService;
import org.eclipse.osgi.util.NLS;

import polyglot.frontend.ExtensionInfo;
import x10.X10CompilerOptions;
import x10.config.ConfigurationError;
import x10.config.OptionError;
import x10cpp.X10CPPCompilerOptions;
import x10dt.core.X10DTCorePlugin;
import x10dt.core.preferences.generated.X10Constants;

public class CompilerOptionsFactory {
    public static X10CompilerOptions createOptions(IProject project) {
        ExtensionInfo extInfo = null; // BUG Need a non-null extInfo if one wants to parse a "-version" arg... but this should probably be changed in the Options API
        X10CompilerOptions options;

        try {
            if (project.hasNature(X10DTCorePlugin.X10_CPP_PRJ_NATURE_ID)) {
                options = new X10CPPCompilerOptions(extInfo);
            } else if (project.hasNature(X10DTCorePlugin.X10_PRJ_JAVA_NATURE_ID)) {
                options = new X10CompilerOptions(extInfo);
            } else {
                // Odd, the project really should have one of the supported X10 natures.
                // Fall back to using a raw X10CompilerOptions...
                options = new X10CompilerOptions(extInfo);
            }
        } catch (CoreException e) {
            X10DTCorePlugin.getInstance().logException("Error checking for X10 project nature", e);
            options = new X10CompilerOptions(null);
        }
        final IPreferencesService prefService = new PreferencesService(project, X10DTCorePlugin.kLanguageName);
        setOptions(prefService, options);
        return options;

        
    }
    
    public static void setOptions(IPreferencesService prefService, X10CompilerOptions options){
        options.x10_config.DEBUG = true;

        final String additionalOptions = prefService.getStringPreference(X10Constants.P_ADDITIONALCOMPILEROPTIONS);

        if ((additionalOptions != null) && (additionalOptions.length() > 0)) {
            // First initialize to default values.
            options.x10_config.CHECK_INVARIANTS = false;
            options.x10_config.ONLY_TYPE_CHECKING = false;
            options.x10_config.FLATTEN_EXPRESSIONS = false;
            options.x10_config.WALA = false;
            options.x10_config.FINISH_ASYNCS = false;
            for (final String opt : additionalOptions.split("\\s")) { ////$NON-NLS-1$
                try {
                    options.x10_config.parseArgument(opt);
                } catch (OptionError except) {
                    X10DTCorePlugin.getInstance().logException(NLS.bind("Could not recognize or set option ''{0}''.", opt), except);
                } catch (ConfigurationError except) {
                    X10DTCorePlugin.getInstance().logException(NLS.bind("Could not initialize option ''{0}''.", opt), except);
                }
            }
            options.x10_config.NO_CHECKS = prefService.getBooleanPreference(X10Constants.P_DISABLECHECKING);
        }
        // Optimization prefs update
        setOptionsNoCodeGen(prefService, options);

    }
    
    public static void setOptionsNoCodeGen(IPreferencesService prefService, X10CompilerOptions options){
        options.x10_config.STATIC_CALLS = prefService.getBooleanPreference(X10Constants.P_STATICCALLS);
        options.x10_config.VERBOSE_CALLS = prefService.getBooleanPreference(X10Constants.P_VERBOSECALLS);
        options.x10_config.OPTIMIZE = prefService.getBooleanPreference(X10Constants.P_OPTIMIZE);
    }
}

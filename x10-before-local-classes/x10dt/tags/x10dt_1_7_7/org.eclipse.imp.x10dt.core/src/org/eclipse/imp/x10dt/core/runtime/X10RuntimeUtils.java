/**
 * 
 */
package org.eclipse.imp.x10dt.core.runtime;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.imp.preferences.PreferencesService;
import org.eclipse.imp.x10dt.core.X10DTCorePlugin;
import org.eclipse.imp.x10dt.core.X10Util;
import org.eclipse.imp.x10dt.core.preferences.generated.X10Constants;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;

/**
 * Common place for utilities used by several folks
 * 
 * @author beth tibbitts
 *
 */
public class X10RuntimeUtils {

    private static final class BundleJarFileFilter implements FilenameFilter {
        private final Bundle fBundle;

        private BundleJarFileFilter(Bundle bundle) {
            fBundle = bundle;
        }

        public boolean accept(File dir, String name) {
            return name.contains(fBundle.getSymbolicName()) && name.endsWith(".jar"); //PORT1.7 use constant
        }
    }

    public static final String X10_TYPES_TYPE_CLASS = "x10/types/Type.class";

    /**
     * Find the jar file for the currently installed runtime plugin
     * @return
     */
    public static String findX10RuntimeJar() {
        return null;
    }

    public static String findX10ClassPathEntry(IJavaProject proj) {
        return null;
    }

    private X10RuntimeUtils() {}  // no need to instantiate

	/**
	 * Find a valid X10 runtime in a list of class path entries
	 * <br>PORT1.7 moved here from X10Builder
	 * @param entries
	 * @return the index in the given list to look through
	 * @throws JavaModelException
	 */
	 public static int findValidX10RuntimeClasspathEntry(IClasspathEntry[] entries) throws JavaModelException {
	        for(int i= 0; i < entries.length; i++) {
	            IClasspathEntry entry= entries[i];

	            if (entry.getEntryKind() == IClasspathEntry.CPE_LIBRARY || entry.getEntryKind() == IClasspathEntry.CPE_VARIABLE) {
	                IPath entryPath= entry.getPath();
	                File entryFile= entryPath.toFile();

	                if (entryFile.isDirectory()) {
	                	//PORT1.7 -- ask bob: why look for Object here and 'Type' Below?
	                    File x10ObjFile= new File(entryFile.getPath() + File.separator + "x10" + File.separator + "lang" + File.separator + "Object.class");//PORT1.7 -- ?

	                    if (x10ObjFile.exists()) {
	                        return i;
	                    }
	                } else {
	                    try {
	                    	// PORT1.7 Should first check whether this jar's name contains X10Plugin.X10_RUNTIME_BUNDLE_ID
	                    	boolean exists = entryFile.exists();
	                        JarFile x10Jar= new JarFile(entryFile);//PORT1.7 -- todo ask bob: do what?
	                        String jarName = x10Jar.getName();
	                        if (jarName.contains(X10DTCorePlugin.X10_RUNTIME_BUNDLE_ID)) {
	                        	// Look for a type we expect to find in the x10 runtime
	                        	ZipEntry x10ObjEntry = x10Jar.getEntry(X10_TYPES_TYPE_CLASS); // PORT1.7  x10/lang/Object  ->  x10/types/Type
								if (x10ObjEntry != null) {
									return i;
								}
						}
	                    } catch (IOException e) {
	                        ; // I guess this wasn't a jar file, so we don't
								// know what to do with it...
	                    }
	                }
	            }
	        }
	        return -1;
	    }
	 /**
	  * PORT1.7 moved here from X10Builder
	  * @return
	  */
	    public static String getCurrentRuntimeVersion() {    	
	    	//String jarLoc=X10Util.getJarLocationForBundle(X10Plugin.X10_RUNTIME_BUNDLE_ID);
	        Bundle x10RuntimeBundle= Platform.getBundle(X10DTCorePlugin.X10_RUNTIME_BUNDLE_ID);
	        String bundleVersion= (String) x10RuntimeBundle.getHeaders().get("Bundle-Version");
	        return bundleVersion;
	    }  
	    /**
	     * PORT1.7 moved from X10Builder
	     * @return
	     */
	    public static  IPath getLanguageRuntimePath() {
	    	
	        try {
	            // Can't figure out a way to get the location of the x10.runtime jar directly.
	            // First, try the easy way: ask the platform. This often doesn't work
	            Bundle x10RuntimeBundle= Platform.getBundle(X10DTCorePlugin.X10_RUNTIME_BUNDLE_ID); // PORT1.7 use constant
	            String x10RuntimeLoc= FileLocator.toFileURL(x10RuntimeBundle.getResource("")).getFile();

	            // The JDT will allow you to create a folder/library classpath entry, but
	            // it really doesn't support it (at least not until 3.4), so don't create
	            // such an entry.
	            if (new File(x10RuntimeLoc).isDirectory()) {
	                // The platform didn't give us an answer we can use; now we do it the hard way...
	                IPath path= guessRuntimeLocation(x10RuntimeBundle);

	                if (path == null) {
	                    PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
	                        public void run() {
	                            postMsgDialog("Can't find the X10 runtime jar file",
	                                    "The Eclipse Platform seems to believe that the X10 Runtime lives in a folder, " +
	                                    "but the X10DT needs it to be in a jar file. " +
	                                    "This is probably due to running an X10DT version that lives in your workspace. " +
	                                    "[If you're doing this, you'd almost certainly know it.]\n\n" +
	                                    "Please create the appropriate entry manually by going to the Project Properties dialog, " +
	                                    "clicking on 'Add External JARs' in the 'Java Build Path' page, and " +
	                                    "specifying a suitable X10 Runtime jar file.");
	                        }
	                    });
	                }
	                return path;
	            }
	            IPath x10RuntimePath= new Path(x10RuntimeLoc);

	            return x10RuntimePath;
	        } catch (IOException e) {
	            X10DTCorePlugin.getInstance().logException("Unable to resolve X10 runtime location", e);
	            return null;
	        }
	    }
	    public static IPath guessRuntimeLocation(Bundle x10RuntimeBundle) {
	    	
	        // Try to find either the X10 runtime of the same version as the one that's
	        // presently installed and enabled, or, failing that, the most recent.
	        String x10BundleVersion= (String) x10RuntimeBundle.getHeaders().get(Constants.BUNDLE_VERSION);
	        Location installLoc= Platform.getInstallLocation();
	        URL installURL= installLoc.getURL();
  
	        // Note: not every env. puts installed stuff in ECLIPSE_HOME e.g. Linux puts in .eclipse - 
	        // in which case this won't work
            
	        if (installURL.getProtocol().equals("file")) {
	        	String installPath= installURL.getPath();
	            String pluginPath= installPath.concat("/plugins");
	            File pluginDir= new File(pluginPath);

	            if (pluginDir.exists() && pluginDir.isDirectory()) {
	                File[] runtimeJars= pluginDir.listFiles(new FilenameFilter() {
	                    public boolean accept(File dir, String name) {
	                        return name.contains(X10DTCorePlugin.X10_RUNTIME_BUNDLE_ID) && name.endsWith(".jar"); //PORT1.7 use constant
	                    }
	                });
	                if (runtimeJars.length == 0) {
	                    return null;
	                }
	                // First, prefer the version that's installed and enabled in the platform,
	                // if we can find it.
	                for(int i= 0; i < runtimeJars.length; i++) {
	                    File jarFile= runtimeJars[i];
	                    String jarPath= jarFile.getAbsolutePath();
	                    if (jarPath.contains(x10BundleVersion)) {
	                        return new Path(jarPath);
	                    }
	                }
	                // Oh well, try the highest version.
	                TreeSet<String> sortedJars= new TreeSet<String>(new Comparator<String>() {
	                    public int compare(String o1, String o2) {
	                        return -o1.compareTo(o2); // Make the sort order decreasing, so that sortedJars.first() gives the greatest element
	                    }
	                });
	                for(int i= 0; i < runtimeJars.length; i++) {
	                    File jarFile= runtimeJars[i];
	                    String jarPath= jarFile.getAbsolutePath();

	                    sortedJars.add(jarPath);
	                }
	                return new Path(sortedJars.first());
	            }
	        }
	        return null; // we're out of heuristics... (handle the case where install location is remote, for example)
	    }
	    /**
	     * Adapted from guessRuntimeLocation, should probably combine instead of copying code here.
	     * This finds the jar location of an arbitrary bundle (not just the x10 runtme jar, since we now have constraints and common as 'runtime' jars as well)
	     * @param bundle
	     * @return
	     */
	    public static IPath guessJarLocation(final Bundle bundle) {
	        PreferencesService prefsService = X10DTCorePlugin.getInstance().getPreferencesService();
	        String runtimeJarDirName = prefsService.getStringPreference(X10Constants.P_DEFAULTRUNTIME);

	        if (prefsService.isDefined(X10Constants.P_DEFAULTRUNTIME) && runtimeJarDirName.length() > 0) {
	            File runtimeJarDir = new File(runtimeJarDirName);

	            if (runtimeJarDir.exists() && runtimeJarDir.isDirectory()) {
	                File[] runtimeJars = runtimeJarDir.listFiles(new BundleJarFileFilter(bundle));

	                if (runtimeJars.length > 0) {
	                    IPath newestPath = getNewest(runtimeJars);

	                    return newestPath;
	                }
	            }
	        }
	        final String bundleVersion= (String) bundle.getHeaders().get(Constants.BUNDLE_VERSION);
	        Location installLoc= Platform.getInstallLocation();
	        URL installURL= installLoc.getURL();
  
	        // Note: not every env. puts installed stuff in ECLIPSE_HOME e.g. Linux puts in .eclipse - 
	        // in which case this won't work
            
	        if (installURL.getProtocol().equals("file")) {
	            String installPath= installURL.getPath();
	            String pluginPath= installPath.concat("/plugins");
	            File pluginDir= new File(pluginPath);

	            if (pluginDir.exists() && pluginDir.isDirectory()) {
	                File[] runtimeJars= pluginDir.listFiles(new BundleJarFileFilter(bundle));
	                if (runtimeJars.length == 0) {
	                    return null;
	                }
	                // First, prefer the version that's installed and enabled in the platform,
	                // if we can find it.
	                for(int i= 0; i < runtimeJars.length; i++) {
	                    File jarFile= runtimeJars[i];
	                    String jarPath= jarFile.getAbsolutePath();
	                    if (jarPath.contains(bundleVersion)) {
	                        return new Path(jarPath);
	                    }
	                }
	                // Oh well, try the latest version.
	                return getLatestVersion(runtimeJars);
	            }
	        }
	        return null; // we're out of heuristics... (handle the case where install location is remote, for example)
	    }

            private static IPath getNewest(File[] files) {
                TreeSet<File> sortedFiles= new TreeSet<File>(new Comparator<File>() {
                    public int compare(File f1, File f2) {
                        return (int) (f2.lastModified() - f1.lastModified()); // Use decreasing timestamp order, so sortedFiles.first() gives the newest element
                    }
                });
                for(int i= 0; i < files.length; i++) {
                    sortedFiles.add(files[i]);
                }
                return new Path(sortedFiles.first().getAbsolutePath());
            }

            private static IPath getLatestVersion(File[] files) {
                TreeSet<String> sortedFiles= new TreeSet<String>(new Comparator<String>() {
                    public int compare(String o1, String o2) {
                        return -o1.compareTo(o2); // Make the sort order decreasing, so that sortedFiles.first() gives the greatest element
                    }
                });
                for(int i= 0; i < files.length; i++) {
                    File file= files[i];
                    String filePath= file.getAbsolutePath();

                    sortedFiles.add(filePath);
                }
                return new Path(sortedFiles.first());
	    }

	    /**
	     * Finds and returns the index of all classpath entries in the argument that
	     * look like an X10 Runtime entry, including those that may be invalid, so
	     * that they can be removed.
	     * <br>PORT1.7 moved to X10RuntimeUtils from X10Builder
	     */
	    public static List<Integer> findAllX10RuntimeClasspathEntries(IClasspathEntry[] entries) throws JavaModelException {
	        List<Integer> runtimeIndexes= new ArrayList<Integer>();
	        for(int i= 0; i < entries.length; i++) {
	            IClasspathEntry entry= entries[i];

	            if (entry.getEntryKind() == IClasspathEntry.CPE_LIBRARY || entry.getEntryKind() == IClasspathEntry.CPE_VARIABLE) {
	                IPath entryPath= entry.getPath();
	                /** The following uses bundle ID for x10.runtime.*   -- if this were checked out into the
	                 * workspace under a different project name, this would fail.  May have to do something about this later.
					*/
	                if (entryPath.lastSegment().indexOf(X10DTCorePlugin.X10_RUNTIME_BUNDLE_ID) >= 0) {//PORT1.7 use constant
	                    runtimeIndexes.add(i);
	                }
	            }
	        }
	        return runtimeIndexes;
	    }
	    /**
	     * PORT1.7 -- COPIED from X10Builder.  fixme move single copy to ... X10Plugin?
	     * Posts a dialog displaying the given message as soon as "conveniently possible".
	     * This is not a synchronous call, since this method will get called from a
	     * different thread than the UI thread, which is the only thread that can
	     * post the dialog box.
	     */
	    public static void postMsgDialog(final String title, final String msg) {
	        PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
	            public void run() {
	                Shell shell= X10DTCorePlugin.getInstance().getWorkbench().getActiveWorkbenchWindow().getShell();

	                MessageDialog.openInformation(shell, title, msg);
	            }
	        });
	    }
}

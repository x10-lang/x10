package x10dt.refactoring.utils;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.osgi.framework.Bundle;

public class EclipseProjectUtils {
    public static String eclipseHomePath = System.getProperty("user.dir");
    public static String javaHomePath = System.getProperty("java.home");

    private EclipseProjectUtils() { }

    public static IPath getLanguageRuntimePath(IJavaProject javaProject) {
        IClasspathEntry[] entries;
        try {
            entries = javaProject.getResolvedClasspath(true);
            for (int i = 0; i < entries.length; i++) {
                IClasspathEntry entry = entries[i];
                switch (entry.getEntryKind()) {
                case IClasspathEntry.CPE_LIBRARY: {
                    if (entry.getPath().toPortableString().contains("x10.runtime")) {
                        return entry.getPath();
                    }
                    break;
                }
                }
            }
        } catch (JavaModelException e) {
            MessageDialog.openError(null, "Extract Async error", "Cannot resolve project's classpath!");
            e.printStackTrace();
        }
        return getDefaultX10RuntimePath();
    }

    private static IPath getDefaultX10RuntimePath() {
        Bundle x10RuntimeBundle = Platform.getBundle("x10.runtime");
        String bundleVersion = (String) x10RuntimeBundle.getHeaders().get("Bundle-Version");
        IPath x10RuntimePath = new Path("/plugins/x10.runtime_" + bundleVersion + ".jar");

        return x10RuntimePath.append(EclipseProjectUtils.eclipseHomePath);
    }
}

package x10.uide;

import java.net.MalformedURLException;
import java.net.URL;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;

public class X10UIPlugin extends AbstractUIPlugin {
    public static final String PLUGIN_ID= "x10.uide";

    private static X10UIPlugin sInstance;

    public X10UIPlugin() {
	super();
	sInstance= this;
    }

    public static X10UIPlugin getInstance() {
	return sInstance;
    }

    private void initImageRegistry() {
	fgIconBaseURL= X10UIPlugin.getInstance().getBundle().getEntry("/icons/"); //$NON-NLS-1$
	RUNTIME_IMG_DESC= create(RUNTIME_IMG_NAME);
	getInstance().getImageRegistry().put(RUNTIME_IMG_NAME, RUNTIME_IMG_DESC);
    }

    public static void log(Exception e) {
	getInstance().getLog().log(new Status(IStatus.ERROR, PLUGIN_ID, 0, e.getMessage(), e));
    }

    public static final String RUNTIME_IMG_NAME= "runtime_obj.gif";
    public static ImageDescriptor RUNTIME_IMG_DESC;

    private static URL fgIconBaseURL= null;

    private static URL makeIconFileURL(String name) throws MalformedURLException {
	if (fgIconBaseURL == null)
	    throw new MalformedURLException();

	return new URL(fgIconBaseURL, name);
    }

    private static ImageDescriptor create(String name) {
	try {
	    return ImageDescriptor.createFromURL(makeIconFileURL(name));
	} catch (MalformedURLException e) {
	    return ImageDescriptor.getMissingImageDescriptor();
	}
    }

    /**
     * Returns the image managed under the given key in this registry.
     * 
     * @param key the image's key
     * @return the image managed under the given key
     */
    public Image getImage(String key) {
	if (fgIconBaseURL == null)
	    initImageRegistry();
	return getInstance().getImageRegistry().get(key);
    }
}

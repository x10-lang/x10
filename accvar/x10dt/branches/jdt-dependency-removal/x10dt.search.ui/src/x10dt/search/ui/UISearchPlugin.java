package x10dt.search.ui;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.imp.runtime.ImageDescriptorRegistry;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.search.ui.IContextMenuConstants;
import org.eclipse.ui.navigator.ICommonMenuConstants;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import x10dt.search.ui.typeHierarchy.MembersOrderPreferenceCache;
import x10dt.search.ui.typeHierarchy.TypeFilter;
import x10dt.search.ui.typeHierarchy.X10Constants;

/**
 * The activator class controls the plug-in life cycle
 */
public class UISearchPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "x10dt.search.ui";

	// The shared instance
	private static UISearchPlugin plugin;

	private static URL fgIconBaseURL = null;

	public static final String OPENTYPE_NAME = "opentype.gif";
	public static ImageDescriptor OPENTYPE_DESC;

	private TypeFilter fTypeFilter;
	
	private MembersOrderPreferenceCache fMembersOrderPreferenceCache;
	
	private ImageDescriptorRegistry fImageDescriptorRegistry;

	/**
	 * The constructor
	 */
	public UISearchPlugin() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		X10Constants.initializeDefaultValues(getPreferenceStore());
		fMembersOrderPreferenceCache= new MembersOrderPreferenceCache();
		fMembersOrderPreferenceCache.install(getPreferenceStore());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	public void stop(BundleContext context) throws Exception {
		if (fTypeFilter != null) {
			fTypeFilter.dispose();
			fTypeFilter = null;
		}
		
		if (fMembersOrderPreferenceCache != null) {
			fMembersOrderPreferenceCache.dispose();
			fMembersOrderPreferenceCache= null;
		}
		
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static UISearchPlugin getDefault() {
		return plugin;
	}
	
	public static void log(IStatus status) {
		getDefault().getLog().log(status);
	}
	
	public static void logErrorMessage(String message) {
		log(new Status(IStatus.ERROR, PLUGIN_ID, IStatus.ERROR, message, null));
	}

	public static void log(Exception e) {
		getDefault().getLog().log(
				new Status(IStatus.ERROR, PLUGIN_ID, 0, e.getMessage(), e));
	}

	private void initImageRegistry() {
		fgIconBaseURL = getBundle().getEntry("/icons/"); //$NON-NLS-1$

		OPENTYPE_DESC = create(OPENTYPE_NAME);
		getDefault().getImageRegistry().put(OPENTYPE_NAME, OPENTYPE_DESC);
	}

	private static URL makeIconFileURL(String name)
			throws MalformedURLException {
		if (fgIconBaseURL == null)
			plugin.initImageRegistry();

		return new URL(fgIconBaseURL, name);
	}

	public static ImageDescriptor create(String name) {
		try {
			return ImageDescriptor.createFromURL(makeIconFileURL(name));
		} catch (MalformedURLException e) {
			return ImageDescriptor.getMissingImageDescriptor();
		}
	}

	public synchronized TypeFilter getTypeFilter() {
		if (fTypeFilter == null)
			fTypeFilter = new TypeFilter();
		return fTypeFilter;
	}
	
	public static ImageDescriptorRegistry getImageDescriptorRegistry() {
		return getDefault().internalGetImageDescriptorRegistry();
	}

	private synchronized ImageDescriptorRegistry internalGetImageDescriptorRegistry() {
		if (fImageDescriptorRegistry == null)
			fImageDescriptorRegistry= new ImageDescriptorRegistry();
		return fImageDescriptorRegistry;
	}
	
	public synchronized MembersOrderPreferenceCache getMemberOrderPreferenceCache() {
		// initialized on startup
		return fMembersOrderPreferenceCache;
	}
	
	/**
	 * Creates the Java plug-in's standard groups for view context menus.
	 *
	 * @param menu the menu manager to be populated
	 */
	public static void createStandardGroups(IMenuManager menu) {
		if (!menu.isEmpty())
			return;

		menu.add(new Separator(IContextMenuConstants.GROUP_NEW));
		menu.add(new GroupMarker(IContextMenuConstants.GROUP_GOTO));
		menu.add(new Separator(IContextMenuConstants.GROUP_OPEN));
		menu.add(new GroupMarker(IContextMenuConstants.GROUP_SHOW));
		menu.add(new Separator(ICommonMenuConstants.GROUP_EDIT));
		menu.add(new Separator(IContextMenuConstants.GROUP_REORGANIZE));
		menu.add(new Separator(IContextMenuConstants.GROUP_GENERATE));
		menu.add(new Separator(IContextMenuConstants.GROUP_SEARCH));
		menu.add(new Separator(IContextMenuConstants.GROUP_BUILD));
		menu.add(new Separator(IContextMenuConstants.GROUP_ADDITIONS));
		menu.add(new Separator(IContextMenuConstants.GROUP_VIEWER_SETUP));
		menu.add(new Separator(IContextMenuConstants.GROUP_PROPERTIES));
	}
}

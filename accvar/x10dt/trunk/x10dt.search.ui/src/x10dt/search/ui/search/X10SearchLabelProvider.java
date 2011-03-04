package x10dt.search.ui.search;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;

import x10dt.search.core.elements.IMemberInfo;
import x10dt.ui.typeHierarchy.SearchUtils;
import x10dt.ui.typeHierarchy.X10LabelProvider;

/**
 * This class provides labels for X10 search matches.
 * It extends StorageLabelProvider from imp. The getText() method provides path information for each element.
 * @author mvaziri
 *
 */
public class X10SearchLabelProvider extends X10LabelProvider {
	
	public String getText(Object element) {
		if (element instanceof IMemberInfo){
			IPath base = ResourcesPlugin.getWorkspace().getRoot().getLocation();
			String path = SearchUtils.getPath((IMemberInfo)element).makeRelativeTo(base).segment(0);
			return super.getText(element) + " - " + path;
		}
		return super.getText(element);
	}

	
}

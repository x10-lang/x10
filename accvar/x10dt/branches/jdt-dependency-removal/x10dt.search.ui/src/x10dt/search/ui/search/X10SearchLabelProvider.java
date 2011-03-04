package x10dt.search.ui.search;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.imp.language.LanguageRegistry;
import org.eclipse.imp.language.ServiceFactory;
import org.eclipse.imp.services.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import x10dt.search.core.elements.IMemberInfo;
import x10dt.search.ui.typeHierarchy.SearchUtils;

/**
 * This class provides labels for X10 search matches.
 * It extends StorageLabelProvider from imp. The getText() method provides path information for each element.
 * @author mvaziri
 *
 */
public class X10SearchLabelProvider extends LabelProvider {
	ILabelProvider provider;
	
	public X10SearchLabelProvider()
	{
		provider = ServiceFactory.getInstance().getLabelProvider(LanguageRegistry.findLanguage("X10"));
	}
	
	public String getText(Object element) {
		if (element instanceof IMemberInfo){
			IPath base = ResourcesPlugin.getWorkspace().getRoot().getLocation();
			String path = SearchUtils.getPath((IMemberInfo)element).makeRelativeTo(base).segment(0);
			return provider.getText(element) + " - " + path;
		}
		return provider.getText(element);
	}

	@Override
	public Image getImage(Object element) {
		return provider.getImage(element);
	}
}

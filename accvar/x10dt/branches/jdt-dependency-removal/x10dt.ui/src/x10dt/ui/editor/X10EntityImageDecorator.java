package x10dt.ui.editor;

import org.eclipse.core.resources.IProject;
import org.eclipse.imp.language.ILanguageService;
import org.eclipse.imp.services.DecorationDescriptor;
import org.eclipse.imp.services.IEntityImageDecorator;

import x10dt.ui.X10DTUIPlugin;

public class X10EntityImageDecorator implements IEntityImageDecorator, ILanguageService {

	public final static int NATURE = 1 << 2;
    
	public final static DecorationDescriptor NATURE_DECORATION = new DecorationDescriptor(NATURE, X10DTUIPlugin.getInstance().getBundle(), "icons/projectNormal.gif", DecorationDescriptor.Quadrant.TOP_RIGHT);

	DecorationDescriptor[] descs;
	
	public X10EntityImageDecorator() {
		descs = new DecorationDescriptor[]{NATURE_DECORATION};
	}

	public DecorationDescriptor[] getAllDecorations() {
		return descs;
	}

	public int getDecorationAttributes(Object entity) {
		if(entity instanceof IProject)
		{
			return NATURE;
		}
		
		return 0;
	}

}

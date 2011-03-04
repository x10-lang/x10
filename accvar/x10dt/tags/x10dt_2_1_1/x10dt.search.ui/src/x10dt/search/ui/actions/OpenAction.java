package x10dt.search.ui.actions;

import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.imp.editor.UniversalEditor;
import org.eclipse.imp.model.ISourceEntity;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchSite;

import x10dt.search.core.elements.IMemberInfo;
import x10dt.search.ui.UISearchPlugin;
import x10dt.search.ui.typeHierarchy.SearchUtils;

public class OpenAction extends org.eclipse.imp.actions.OpenAction{

	public OpenAction(IWorkbenchSite site) {
		super(site);
	}

	public OpenAction(UniversalEditor editor) {
		super(editor);
	}
	
	@SuppressWarnings("unchecked")
	private boolean checkEnabled(IStructuredSelection selection) {
        if (selection.isEmpty())
            return false;
        for(Iterator iter= selection.iterator(); iter.hasNext(); ) {
            Object element= iter.next();
            if (element instanceof ISourceEntity)
                continue;
            if (element instanceof IFile)
                continue;
            if (element instanceof IStorage)
                continue;
            if (element instanceof IMemberInfo)
                continue;
            return false;
        }
        return true;
    }

	public void run(IStructuredSelection selection) {
        if (!checkEnabled(selection))
            return;
        run(selection.toArray());
    }

	@Override
	public void run(Object[] elements) {
		for(Object element : elements)
		{
			try {
				if(element instanceof IMemberInfo)
				{
					SearchUtils.openEditor((IMemberInfo)element);
				}
				else
				{
					super.run(new Object[]{element});
				}
			} catch (CoreException e) {
				UISearchPlugin.log(e);
			}
		}
	}
}

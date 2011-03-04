package x10dt.ui;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PerspectiveAdapter;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.contexts.IContextActivation;
import org.eclipse.ui.contexts.IContextService;

import x10dt.search.ui.typeHierarchy.X10Constants;

public class EarlyStartup implements IStartup {
	public IContextService contextService;
    public IContextActivation perspectiveActivation = null;
    
	public void earlyStartup() {
		/*
		 * The registration of the listener should have been done in the UI
		 * thread since PlatformUI.getWorkbench().getActiveWorkbenchWindow()
		 * returns null if it is called outside of the UI thread.
		 */
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				final IWorkbenchWindow workbenchWindow = PlatformUI
						.getWorkbench().getActiveWorkbenchWindow();
				if (workbenchWindow != null) {
					contextService = (IContextService)workbenchWindow.getService(IContextService.class);
					
					// started in X10 perspective
					if(workbenchWindow.getActivePage().getPerspective().getId().equals(X10Constants.ID_HIERARCHYPERSPECTIVE))
					{
						activatePerspectiveContext();
					}
					
					workbenchWindow.addPerspectiveListener(new PerspectiveAdapter() {
						@Override
						public void perspectiveActivated(IWorkbenchPage page,
								IPerspectiveDescriptor perspective) {
							super.perspectiveActivated(page, perspective);
							
							if(workbenchWindow.getActivePage().getPerspective().getId().equals(X10Constants.ID_HIERARCHYPERSPECTIVE))
							{
								activatePerspectiveContext();
							}
							
							else
							{
								deactivatePerspectiveContext();
							}
						}
					});
				}
			}
		});
	}
	
	public void activatePerspectiveContext()
    {
    	if(contextService != null && perspectiveActivation == null)
    	{
    		perspectiveActivation = contextService.activateContext(X10DTUIPlugin.PERSPECTIVE_SCOPE);
    	}
    }
    
    public void deactivatePerspectiveContext()
    {
    	if(perspectiveActivation != null)
    	{
    		contextService.deactivateContext(perspectiveActivation);
    		perspectiveActivation = null;
    	}
    }

}

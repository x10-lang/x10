package x10dt.ui.perspective;

import org.eclipse.core.expressions.IPropertyTester;
import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.imp.utils.UIUtils;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IWorkbenchPage;

public class PerspectiveTester extends PropertyTester implements IPropertyTester {
	private static final String PROPERTY_IS_PERSPECTIVE_ID = "perspectiveId"; //$NON-NLS-1$

	public boolean test(Object receiver, String property, Object[] args,
			Object expectedValue) {
		if (PROPERTY_IS_PERSPECTIVE_ID.equals(property)) {
			return isPerspectiveOpen((String)expectedValue);
		}
		return false;
	}
	
	private boolean isPerspectiveOpen(String expectedValue)
	{
		IWorkbenchPage page = UIUtils.getActivePage();
		if (page != null) {
			IPerspectiveDescriptor persp = page.getPerspective();
			if (persp != null) {
				return persp.getId().equals(expectedValue);
			}
		}
		
		return false;
	}
}


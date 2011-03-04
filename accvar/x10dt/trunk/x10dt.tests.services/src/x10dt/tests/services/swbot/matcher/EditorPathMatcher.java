package x10dt.tests.services.swbot.matcher;

import org.eclipse.core.runtime.IPath;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

/**
 * Matches an editor based on the path to the file being edited.
 * @author rfuhrer
 */
public class EditorPathMatcher extends BaseMatcher<IEditorReference> {
    private final IPath srcPath;

    public EditorPathMatcher(IPath srcPath) {
        this.srcPath= srcPath;
    }

    public void describeTo(Description description) {
        description.appendText("Editor matcher for path " + srcPath.toPortableString()); //$NON-NLS-1$
    }

    public boolean matches(Object item) {
        if (item instanceof IEditorReference) {
            try {
                IEditorReference ref = (IEditorReference) item;
                IFileEditorInput fileInput = (IFileEditorInput) ref.getEditorInput();

                return fileInput.getFile().getFullPath().equals(srcPath);
            } catch (PartInitException e) {
                // do nothing
            }
        }
        return false;
    }
}
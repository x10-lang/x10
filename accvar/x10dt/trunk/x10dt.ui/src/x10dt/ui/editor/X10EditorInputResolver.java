package x10dt.ui.editor;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.imp.services.base.EditorInputResolver;
import org.eclipse.jdt.core.IJarEntryResource;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IStorageEditorInput;

public class X10EditorInputResolver extends EditorInputResolver {

	public X10EditorInputResolver() {
	}

	@Override
	public IPath getPath(IEditorInput editorInput) {
		if (editorInput instanceof IStorageEditorInput) {
			try {
				IStorageEditorInput sti = (IStorageEditorInput) editorInput;
				if (sti.getStorage() instanceof IJarEntryResource) {
					IJarEntryResource jar = (IJarEntryResource) sti
							.getStorage();
					IPackageFragmentRoot root = jar.getPackageFragmentRoot();
					return new Path(root.getPath().toPortableString()
							+ ":"
							+ jar.getFullPath().toPortableString()
									.replaceFirst("/", ""));
				}
			} catch (Exception e) {
				// ignore
			}
		}

		return super.getPath(editorInput);
	}
}
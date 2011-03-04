package x10dt.search.ui.globalSearch;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.osgi.framework.Bundle;

import org.eclipse.swt.widgets.Shell;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableContext;

import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.PlatformUI;

import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.NewSearchUI;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;

import org.eclipse.jdt.internal.corext.util.Messages;

import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.dialogs.OptionalMessageDialog;
import org.eclipse.jdt.internal.ui.viewsupport.BasicElementLabels;
import org.eclipse.jdt.internal.ui.workingsets.WorkingSetComparator;

/**
 * This class contains some utility methods for J Search.
 */
public class SearchUtil {

	// LRU working sets
	public static final int LRU_WORKINGSET_LIST_SIZE= 3;
	private static LRUWorkingSetsList fgLRUWorkingSets;

	// Settings store
	private static final String DIALOG_SETTINGS_KEY= "JavaElementSearchActions"; //$NON-NLS-1$
	private static final String STORE_LRU_WORKING_SET_NAMES= "lastUsedWorkingSetNames"; //$NON-NLS-1$

	private static final String BIN_PRIM_CONST_WARN_DIALOG_ID= "BinaryPrimitiveConstantWarningDialog"; //$NON-NLS-1$

	public static boolean isSearchPlugInActivated() {
		return Platform.getBundle("org.eclipse.search").getState() == Bundle.ACTIVE; //$NON-NLS-1$
	}


	/**
	 * This helper method with Object as parameter is needed to prevent the loading
	 * of the Search plug-in: the VM verifies the method call and hence loads the
	 * types used in the method signature, eventually triggering the loading of
	 * a plug-in (in this case ISearchQuery results in Search plug-in being loaded).
	 *
	 * @param query the search query
	 */
	public static void runQueryInBackground(Object query) {
		NewSearchUI.runQueryInBackground((ISearchQuery)query);
	}

	/**
	 * This helper method with Object as parameter is needed to prevent the loading
	 * of the Search plug-in: the VM verifies the method call and hence loads the
	 * types used in the method signature, eventually triggering the loading of
	 * a plug-in (in this case ISearchQuery results in Search plug-in being loaded).
	 *
	 * @param context the runnable context
	 * @param query the search query
	 * @return status
	 */
	public static IStatus runQueryInForeground(IRunnableContext context, Object query) {
		return NewSearchUI.runQueryInForeground(context, (ISearchQuery)query);
	}

	/**
	 * Returns the compilation unit for the given java element.
	 *
	 * @param	element the java element whose compilation unit is searched for
	 * @return	the compilation unit of the given java element
	 */
	static ICompilationUnit findCompilationUnit(IJavaElement element) {
		if (element == null)
			return null;
		return (ICompilationUnit) element.getAncestor(IJavaElement.COMPILATION_UNIT);
	}


	public static String toString(IWorkingSet[] workingSets) {
		Arrays.sort(workingSets, new WorkingSetComparator());
		String result= ""; //$NON-NLS-1$
		if (workingSets != null && workingSets.length > 0) {
			boolean firstFound= false;
			for (int i= 0; i < workingSets.length; i++) {
				String workingSetLabel= BasicElementLabels.getWorkingSetLabel(workingSets[i]);
				if (firstFound)
					result= Messages.format(SearchMessages.SearchUtil_workingSetConcatenation, new String[] {result, workingSetLabel});
				else {
					result= workingSetLabel;
					firstFound= true;
				}
			}
		}
		return result;
	}

	// ---------- LRU working set handling ----------

	/**
	 * Updates the LRU list of working sets.
	 *
	 * @param workingSets	the workings sets to be added to the LRU list
	 */
	public static void updateLRUWorkingSets(IWorkingSet[] workingSets) {
		if (workingSets == null || workingSets.length < 1)
			return;

		getLRUWorkingSets().add(workingSets);
		saveState(getDialogStoreSection());
	}

	private static void saveState(IDialogSettings settingsStore) {
		IWorkingSet[] workingSets;
		Iterator iter= fgLRUWorkingSets.iterator();
		int i= 0;
		while (iter.hasNext()) {
			workingSets= (IWorkingSet[])iter.next();
			String[] names= new String[workingSets.length];
			for (int j= 0; j < workingSets.length; j++)
				names[j]= workingSets[j].getName();
			settingsStore.put(STORE_LRU_WORKING_SET_NAMES + i, names);
			i++;
		}
	}

	public static LRUWorkingSetsList getLRUWorkingSets() {
		if (fgLRUWorkingSets == null) {
			restoreState();
		}
		return fgLRUWorkingSets;
	}

	private static void restoreState() {
		fgLRUWorkingSets= new LRUWorkingSetsList(LRU_WORKINGSET_LIST_SIZE);
		IDialogSettings settingsStore= getDialogStoreSection();

		boolean foundLRU= false;
		for (int i= LRU_WORKINGSET_LIST_SIZE - 1; i >= 0; i--) {
			String[] lruWorkingSetNames= settingsStore.getArray(STORE_LRU_WORKING_SET_NAMES + i);
			if (lruWorkingSetNames != null) {
				Set workingSets= new HashSet(2);
				for (int j= 0; j < lruWorkingSetNames.length; j++) {
					IWorkingSet workingSet= PlatformUI.getWorkbench().getWorkingSetManager().getWorkingSet(lruWorkingSetNames[j]);
					if (workingSet != null) {
						workingSets.add(workingSet);
					}
				}
				foundLRU= true;
				if (!workingSets.isEmpty())
					fgLRUWorkingSets.add((IWorkingSet[])workingSets.toArray(new IWorkingSet[workingSets.size()]));
			}
		}
		if (!foundLRU)
			// try old preference format
			restoreFromOldFormat();
	}

	private static IDialogSettings getDialogStoreSection() {
		IDialogSettings settingsStore= JavaPlugin.getDefault().getDialogSettings().getSection(DIALOG_SETTINGS_KEY);
		if (settingsStore == null)
			settingsStore= JavaPlugin.getDefault().getDialogSettings().addNewSection(DIALOG_SETTINGS_KEY);
		return settingsStore;
	}

	private static void restoreFromOldFormat() {
		fgLRUWorkingSets= new LRUWorkingSetsList(LRU_WORKINGSET_LIST_SIZE);
		IDialogSettings settingsStore= getDialogStoreSection();

		boolean foundLRU= false;
		String[] lruWorkingSetNames= settingsStore.getArray(STORE_LRU_WORKING_SET_NAMES);
		if (lruWorkingSetNames != null) {
			for (int i= lruWorkingSetNames.length - 1; i >= 0; i--) {
				IWorkingSet workingSet= PlatformUI.getWorkbench().getWorkingSetManager().getWorkingSet(lruWorkingSetNames[i]);
				if (workingSet != null) {
					foundLRU= true;
					fgLRUWorkingSets.add(new IWorkingSet[]{workingSet});
				}
			}
		}
		if (foundLRU)
			// save in new format
			saveState(settingsStore);
	}

	public static void warnIfBinaryConstant(IJavaElement element, Shell shell) {
		if (isBinaryPrimitiveConstantOrString(element))
			OptionalMessageDialog.open(
				BIN_PRIM_CONST_WARN_DIALOG_ID,
				shell,
				SearchMessages.Search_FindReferencesAction_BinPrimConstWarnDialog_title,
				null,
				SearchMessages.Search_FindReferencesAction_BinPrimConstWarnDialog_message,
				MessageDialog.INFORMATION,
				new String[] { IDialogConstants.OK_LABEL },
				0);
	}

	private static boolean isBinaryPrimitiveConstantOrString(IJavaElement element) {
		if (element != null && element.getElementType() == IJavaElement.FIELD) {
			IField field= (IField)element;
			int flags;
			try {
				flags= field.getFlags();
			} catch (JavaModelException ex) {
				return false;
			}
			return field.isBinary() && Flags.isStatic(flags) && Flags.isFinal(flags) && isPrimitiveOrString(field);
		}
		return false;
	}

	private static boolean isPrimitiveOrString(IField field) {
		String fieldType;
		try {
			fieldType= field.getTypeSignature();
		} catch (JavaModelException ex) {
			return false;
		}
		char first= fieldType.charAt(0);
		return (first != Signature.C_RESOLVED && first != Signature.C_UNRESOLVED && first != Signature.C_ARRAY)
			|| (first == Signature.C_RESOLVED && fieldType.substring(1, fieldType.length() - 1).equals(String.class.getName()));
	}
}

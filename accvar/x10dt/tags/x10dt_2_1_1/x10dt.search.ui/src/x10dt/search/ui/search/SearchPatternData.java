package x10dt.search.ui.search;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.search.ui.ISearchPageContainer;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.IWorkingSetManager;
import org.eclipse.ui.PlatformUI;

import x10dt.search.core.engine.scope.IX10SearchScope;
import x10dt.search.core.engine.scope.SearchScopeFactory;
import x10dt.search.core.engine.scope.X10SearchScope;

/**
 * This class encapsulates the data gathered from the search page UI.
 * @author mvaziri
 *
 */
public class SearchPatternData {
	// search for
	public final static int TYPE= 0;
	public final static int METHOD= 1;
	public final static int PACKAGE= 2;
	public final static int CONSTRUCTOR= 3;
	public final static int FIELD= 4;

	// limit to
	public final static int DECLARATIONS= 0;
	public final static int IMPLEMENTORS= 1;
	public final static int REFERENCES= 2;
	public final static int SPECIFIC_REFERENCES= 3;
	public final static int ALL_OCCURRENCES= 4;
	public final static int READ_ACCESSES= 5;
	public final static int WRITE_ACCESSES= 6;
	
	private final int searchFor;
	private final int limitTo;
	private final String pattern;
	private final IWorkingSet[] workingSets;
	private final boolean isCaseSensitive;
	private final int scope;
	private final IResource[] resources;
	private final IX10SearchScope X10Scope;

//	public SearchPatternData(int searchFor, int limitTo, boolean isCaseSensitive, String pattern) {
//		this(searchFor, limitTo, pattern, isCaseSensitive,  ISearchPageContainer.WORKSPACE_SCOPE, null);
//	}

	public SearchPatternData(int searchFor, int limitTo, String pattern, boolean isCaseSensitive, int scope, IResource[] resources, IWorkingSet[] workingSets) {
		this.searchFor= searchFor;
		this.limitTo= limitTo;
		if (invalid(pattern)){
			this.pattern = pattern.replace("?", ".?").replace("*", ".*");
		} else {
			this.pattern = pattern;
		}
		this.isCaseSensitive= isCaseSensitive;
		this.workingSets= workingSets;
		this.scope= scope;
		this.resources = resources;
		if ((scope == ISearchPageContainer.SELECTED_PROJECTS_SCOPE ||
				scope == ISearchPageContainer.SELECTION_SCOPE) && resources != null){
			X10Scope = SearchScopeFactory.createSelectiveScope(X10SearchScope.ALL, resources);
		} else if (scope == ISearchPageContainer.WORKING_SET_SCOPE && workingSets != null) {
			Collection<IResource> res = new ArrayList<IResource>();
			for(IWorkingSet ws: workingSets){
				IAdaptable[] elements = ws.getElements();
				for(IAdaptable ia: elements){
					IResource resource = (IResource) ia.getAdapter(IResource.class);
					if (resource != null){
						res.add(resource);
					}
				}
			}
			X10Scope = SearchScopeFactory.createSelectiveScope(X10SearchScope.ALL, res.toArray(new IResource[0]));
		} else {
			X10Scope = SearchScopeFactory.createWorkspaceScope(X10SearchScope.ALL);
		}
	}

	private boolean invalid(String pattern){
		Collection<Integer> is = getAllIndeces(pattern, '*');
		if (!check(pattern, is))
			return true;
		Collection<Integer> js = getAllIndeces(pattern, '?');
		if (!check(pattern, js))
			return true;
		return false;
	}
	
	private Collection<Integer> getAllIndeces(String pattern, char match){
		Collection<Integer> res = new ArrayList<Integer>();
		for(int i = 0; i < pattern.length(); i++){
			if (pattern.charAt(i) == match){
				res.add(i);
			}
		}
		return res;
	}
	
	private boolean check(String pattern, Collection<Integer> is){
		for (int i: is){
			if (i == 0 || (i > 0 && pattern.charAt(i-1) != '.')){
				return false;
			}
		}
		return true;
	}
	
	public boolean isCaseSensitive() {
		return isCaseSensitive;
	}

	public int getLimitTo() {
		return limitTo;
	}

	public String getPattern() {
		return pattern;
	}
	
	public String getPatternLabel() {
		return pattern.replaceAll("\\.\\*", "*").replaceAll("\\.\\?","?");
	}

	public int getScope() {
		return scope;
	}
	
	public IX10SearchScope getX10Scope() {
		return X10Scope;
	}

	public int getSearchFor() {
		return searchFor;
	}

	public IWorkingSet[] getWorkingSets() {
		return workingSets;
	}
	
	public void store(IDialogSettings settings) {
		settings.put("searchFor", searchFor); //$NON-NLS-1$
		settings.put("scope", scope); //$NON-NLS-1$
		settings.put("pattern", pattern); //$NON-NLS-1$
		settings.put("limitTo", limitTo); //$NON-NLS-1$
		settings.put("isCaseSensitive", isCaseSensitive); //$NON-NLS-1$
		if (resources != null){
			String[] rs = new String[resources.length];
			for(int i = 0; i < resources.length; i++){
				rs[i] = resources[i].getFullPath().toOSString();
			}
			settings.put("resources", rs);
		} else {
			settings.put("resources", new String[0]);
		}
		if (workingSets != null) {
			String[] wsIds= new String[workingSets.length];
			for (int i= 0; i < workingSets.length; i++) {
				wsIds[i]= workingSets[i].getName();
			}
			settings.put("workingSets", wsIds); //$NON-NLS-1$
		} else {
			settings.put("workingSets", new String[0]); //$NON-NLS-1$
		}
	}

	public static SearchPatternData create(IDialogSettings settings) {
		String pattern= settings.get("pattern"); //$NON-NLS-1$
		if (pattern.length() == 0) {
			return null;
		}
		try {
			int searchFor= settings.getInt("searchFor"); //$NON-NLS-1$
			int scope= settings.getInt("scope"); //$NON-NLS-1$
			int limitTo= settings.getInt("limitTo"); //$NON-NLS-1$

			boolean isCaseSensitive= settings.getBoolean("isCaseSensitive"); //$NON-NLS-1$
			String[] rs = settings.getArray("resources");
			IResource[] resources = null;
			if (rs != null && rs.length > 0){
				resources = new IResource[rs.length];
				for(int i = 0; i < rs.length; i++){
					resources[i] = ResourcesPlugin.getWorkspace().getRoot().findMember(new Path(rs[i]));
				}
			}
			
			
			String[] wsIds= settings.getArray("workingSets"); //$NON-NLS-1$
			IWorkingSet[] workingSets= null;
			if (wsIds != null && wsIds.length > 0) {
				IWorkingSetManager workingSetManager= PlatformUI.getWorkbench().getWorkingSetManager();
				workingSets= new IWorkingSet[wsIds.length];
				for (int i= 0; workingSets != null && i < wsIds.length; i++) {
					workingSets[i]= workingSetManager.getWorkingSet(wsIds[i]);
					if (workingSets[i] == null) {
						workingSets= null;
					}
				}
			}
			
			return new SearchPatternData(searchFor, limitTo, pattern, isCaseSensitive, scope, resources, workingSets);
		} catch (NumberFormatException e) {
			return null;
		}
	}

}

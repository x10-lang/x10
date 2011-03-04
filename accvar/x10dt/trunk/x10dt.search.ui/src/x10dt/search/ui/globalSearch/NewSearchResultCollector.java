package x10dt.search.ui.globalSearch;

import java.net.URI;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import org.eclipse.search.ui.text.AbstractTextSearchResult;

import org.eclipse.imp.pdb.facts.ISourceLocation;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.search.FieldReferenceMatch;
import org.eclipse.jdt.core.search.LocalVariableReferenceMatch;
import org.eclipse.jdt.core.search.MethodReferenceMatch;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.core.search.SearchParticipant;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.core.search.SearchRequestor;

import x10dt.search.core.elements.ITypeInfo;

public class NewSearchResultCollector extends SearchRequestor {
	private AbstractTextSearchResult fSearch;
	private boolean fIgnorePotentials;

	public NewSearchResultCollector(AbstractTextSearchResult search, boolean ignorePotentials) {
		super();
		fSearch= search;
		fIgnorePotentials= ignorePotentials;
	}

	public void acceptSearchMatch(SearchMatch match) throws CoreException {
		IJavaElement enclosingElement= (IJavaElement) match.getElement();
		if (enclosingElement != null) {
			if (fIgnorePotentials && (match.getAccuracy() == SearchMatch.A_INACCURATE))
				return;
			boolean isWriteAccess= false;
			boolean isReadAccess= false;
			if (match instanceof FieldReferenceMatch) {
				FieldReferenceMatch fieldRef= ((FieldReferenceMatch) match);
				isWriteAccess= fieldRef.isWriteAccess();
				isReadAccess= fieldRef.isReadAccess();
			} else if (match instanceof LocalVariableReferenceMatch) {
				LocalVariableReferenceMatch localVarRef= ((LocalVariableReferenceMatch) match);
				isWriteAccess= localVarRef.isWriteAccess();
				isReadAccess= localVarRef.isReadAccess();
			}
			boolean isSuperInvocation= false;
			if (match instanceof MethodReferenceMatch) {
				MethodReferenceMatch methodRef= (MethodReferenceMatch) match;
				isSuperInvocation= methodRef.isSuperInvocation();
			}
			fSearch.addMatch(new X10ElementMatch(enclosingElement, match.getRule(), match.getOffset(), match.getLength(), match.getAccuracy(), isReadAccess, isWriteAccess, match.isInsideDocComment(), isSuperInvocation));
		}
	}
	
	public void acceptSearchResult(ITypeInfo result){
		URI loc = result.getLocation().getURI();
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(loc.getPath()).makeRelativeTo(ResourcesPlugin.getWorkspace().getRoot().getLocation()));
		fSearch.addMatch(new X10ElementMatch(file, SearchPattern.R_FULL_MATCH, result.getLocation().getOffset(), result.getLocation().getLength(), SearchMatch.A_ACCURATE, true, true, false, false));
		
	}

	public void beginReporting() {
	}

	public void endReporting() {
	}

	public void enterParticipant(SearchParticipant participant) {
	}

	public void exitParticipant(SearchParticipant participant) {
	}

}


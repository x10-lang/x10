package x10dt.search.ui.globalSearch;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.internal.corext.util.Messages;
import org.eclipse.jdt.internal.corext.util.SearchUtils;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jdt.internal.ui.viewsupport.BasicElementLabels;
import org.eclipse.jdt.ui.JavaElementLabels;
import org.eclipse.jdt.ui.search.ElementQuerySpecification;
import org.eclipse.jdt.ui.search.PatternQuerySpecification;
import org.eclipse.jdt.ui.search.QuerySpecification;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.ISearchResult;
import org.eclipse.search.ui.NewSearchUI;

import x10dt.search.core.elements.ITypeInfo;
import x10dt.search.core.engine.X10SearchEngine;
import x10dt.search.core.engine.scope.IX10SearchScope;
import x10dt.search.core.engine.scope.SearchScopeFactory;
import x10dt.search.core.engine.scope.X10SearchScope;

public class X10SearchQuery implements ISearchQuery {

	//private static final String PERF_SEARCH_PARTICIPANT= "org.eclipse.jdt.ui/perf/search/participants"; //$NON-NLS-1$

	private ISearchResult fResult;
	private final QuerySpecification fPatternData;

	public X10SearchQuery(QuerySpecification data) {
		if (data == null) {
			throw new IllegalArgumentException("data must not be null"); //$NON-NLS-1$
		}
		fPatternData= data;
	}

//	private static class SearchRequestor implements ISearchRequestor {
//		private IQueryParticipant fParticipant;
//		private X10SearchResult fSearchResult;
//		public void reportMatch(Match match) {
//			IMatchPresentation participant= fParticipant.getUIParticipant();
//			if (participant == null || match.getElement() instanceof IJavaElement || match.getElement() instanceof IResource) {
//				fSearchResult.addMatch(match);
//			} else {
//				fSearchResult.addMatch(match, participant);
//			}
//		}
//
//		protected SearchRequestor(IQueryParticipant participant, X10SearchResult result) {
//			super();
//			fParticipant= participant;
//			fSearchResult= result;
//		}
//	}

	public IStatus run(IProgressMonitor monitor) {
		final X10SearchResult textResult= (X10SearchResult) getSearchResult();
		textResult.removeAll();
		// Don't need to pass in working copies in 3.0 here
		SearchEngine engine= new SearchEngine();
//		try {

			int totalTicks= 1000;
			IProject[] projects= X10SearchScopeFactory.getInstance().getProjects(fPatternData.getScope());
//			final SearchParticipantRecord[] participantDescriptors= SearchParticipantsExtensionPoint.getInstance().getSearchParticipants(projects);
//			final int[] ticks= new int[participantDescriptors.length];
//			for (int i= 0; i < participantDescriptors.length; i++) {
//				final int iPrime= i;
//				ISafeRunnable runnable= new ISafeRunnable() {
//					public void handleException(Throwable exception) {
//						ticks[iPrime]= 0;
//						String message= SearchMessages.JavaSearchQuery_error_participant_estimate;
//						JavaPlugin.log(new Status(IStatus.ERROR, JavaPlugin.getPluginId(), 0, message, exception));
//					}
//
//					public void run() throws Exception {
//						ticks[iPrime]= participantDescriptors[iPrime].getParticipant().estimateTicks(fPatternData);
//					}
//				};
//
//				SafeRunner.run(runnable);
//				totalTicks+= ticks[i];
//			}

			SearchPattern pattern;
			String stringPattern;
			int searchFor = IJavaSearchConstants.UNKNOWN;
			boolean isCaseSensitive = false;
			if (fPatternData instanceof ElementQuerySpecification) {
				IJavaElement element= ((ElementQuerySpecification) fPatternData).getElement();
				stringPattern= JavaElementLabels.getElementLabel(element, JavaElementLabels.ALL_DEFAULT);
				if (!element.exists()) {
					return new Status(IStatus.ERROR, JavaPlugin.getPluginId(), 0, Messages.format(SearchMessages.JavaSearchQuery_error_element_does_not_exist, stringPattern), null);
				}
				pattern= SearchPattern.createPattern(element, fPatternData.getLimitTo(), SearchUtils.GENERICS_AGNOSTIC_MATCH_RULE);
				isCaseSensitive = true;
			} else {
				PatternQuerySpecification patternSpec = (PatternQuerySpecification) fPatternData;
				searchFor = patternSpec.getSearchFor();
				stringPattern= patternSpec.getPattern();
				int matchMode= getMatchMode(stringPattern) | SearchPattern.R_ERASURE_MATCH;
				if (patternSpec.isCaseSensitive()){
					matchMode |= SearchPattern.R_CASE_SENSITIVE;
					isCaseSensitive = true;
				}
				pattern= SearchPattern.createPattern(patternSpec.getPattern(), patternSpec.getSearchFor(), patternSpec.getLimitTo(), matchMode);
			}

			if (pattern == null) {
				return new Status(IStatus.ERROR, JavaPlugin.getPluginId(), 0, Messages.format(SearchMessages.JavaSearchQuery_error_unsupported_pattern, stringPattern), null);
			}
			monitor.beginTask(Messages.format(SearchMessages.JavaSearchQuery_task_label, stringPattern), totalTicks);
			IProgressMonitor mainSearchPM= new SubProgressMonitor(monitor, 1000);

			boolean ignorePotentials= NewSearchUI.arePotentialMatchesIgnored();
			NewSearchResultCollector collector= new NewSearchResultCollector(textResult, ignorePotentials);

			try {
			IPath[] paths = fPatternData.getScope().enclosingProjectsAndJars();
			stringPattern = stringPattern.replace("*", ".*");
			stringPattern = stringPattern.replace("?", ".?");
			for (IPath p: paths){
				IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(p.toOSString());
				if (searchFor == IJavaSearchConstants.TYPE){
				  final IX10SearchScope scope = SearchScopeFactory.createSelectiveScope(X10SearchScope.ALL, project);
					ITypeInfo[] results = X10SearchEngine.getAllMatchingTypeInfo(scope, stringPattern, isCaseSensitive , mainSearchPM);
					for(ITypeInfo info: results){
						collector.acceptSearchResult(info);
					}
				} else if (searchFor == IJavaSearchConstants.METHOD){
//					IMethodInfo[] results = X10SearchEngine.getAllMatchingMethodInfo(project, stringPattern, isCaseSensitive , mainSearchPM);
//					for(IMethodInfo info: results){
//						collector.acceptSearchResult(info);
//					}
				}
			}
			}catch(Exception e){
				//TODO
			}

			//engine.search(pattern, new SearchParticipant[] { SearchEngine.getDefaultSearchParticipant() }, fPatternData.getScope(), collector, mainSearchPM);
//			for (int i= 0; i < participantDescriptors.length; i++) {
//				final ISearchRequestor requestor= new SearchRequestor(participantDescriptors[i].getParticipant(), textResult);
//				final IProgressMonitor participantPM= new SubProgressMonitor(monitor, ticks[i]);
//
//				final int iPrime= i;
//				ISafeRunnable runnable= new ISafeRunnable() {
//					public void handleException(Throwable exception) {
//						participantDescriptors[iPrime].getDescriptor().disable();
//						String message= SearchMessages.JavaSearchQuery_error_participant_search;
//						JavaPlugin.log(new Status(IStatus.ERROR, JavaPlugin.getPluginId(), 0, message, exception));
//					}
//
//					public void run() throws Exception {
//
//						final IQueryParticipant participant= participantDescriptors[iPrime].getParticipant();
//
//						final PerformanceStats stats= PerformanceStats.getStats(PERF_SEARCH_PARTICIPANT, participant);
//						stats.startRun();
//
//						participant.search(requestor, fPatternData, participantPM);
//
//						stats.endRun();
//					}
//				};
//
//				SafeRunner.run(runnable);
//			}

//		} catch (CoreException e) {
//			return e.getStatus();
//		}
		String message= Messages.format(SearchMessages.JavaSearchQuery_status_ok_message, String.valueOf(textResult.getMatchCount()));
		return new Status(IStatus.OK, JavaPlugin.getPluginId(), 0, message, null);
	}

	private int getMatchMode(String pattern) {
		if (pattern.indexOf('*') != -1 || pattern.indexOf('?') != -1) {
			return SearchPattern.R_PATTERN_MATCH;
		} else if (SearchUtils.isCamelCasePattern(pattern)) {
			return SearchPattern.R_CAMELCASE_MATCH;
		}
		return SearchPattern.R_EXACT_MATCH;
	}

	public String getLabel() {
		return SearchMessages.JavaSearchQuery_label;
	}

	public String getResultLabel(int nMatches) {
		int limitTo= getMaskedLimitTo();
		if (nMatches == 1) {
			String[] args= { getSearchPatternDescription(), fPatternData.getScopeDescription() };
			switch (limitTo) {
				case IJavaSearchConstants.IMPLEMENTORS:
					return Messages.format(SearchMessages.JavaSearchOperation_singularImplementorsPostfix, args);
				case IJavaSearchConstants.DECLARATIONS:
					return Messages.format(SearchMessages.JavaSearchOperation_singularDeclarationsPostfix, args);
				case IJavaSearchConstants.REFERENCES:
					return Messages.format(SearchMessages.JavaSearchOperation_singularReferencesPostfix, args);
				case IJavaSearchConstants.ALL_OCCURRENCES:
					return Messages.format(SearchMessages.JavaSearchOperation_singularOccurrencesPostfix, args);
				case IJavaSearchConstants.READ_ACCESSES:
					return Messages.format(SearchMessages.JavaSearchOperation_singularReadReferencesPostfix, args);
				case IJavaSearchConstants.WRITE_ACCESSES:
					return Messages.format(SearchMessages.JavaSearchOperation_singularWriteReferencesPostfix, args);
				default:
					String matchLocations= MatchLocations.getMatchLocationDescription(limitTo, 3);
					return Messages.format(SearchMessages.JavaSearchQuery_singularReferencesWithMatchLocations, new Object[] { args[0], args[1], matchLocations });
			}
		} else {
			Object[] args= { getSearchPatternDescription(), new Integer(nMatches), fPatternData.getScopeDescription() };
			switch (limitTo) {
				case IJavaSearchConstants.IMPLEMENTORS:
					return Messages.format(SearchMessages.JavaSearchOperation_pluralImplementorsPostfix, args);
				case IJavaSearchConstants.DECLARATIONS:
					return Messages.format(SearchMessages.JavaSearchOperation_pluralDeclarationsPostfix, args);
				case IJavaSearchConstants.REFERENCES:
					return Messages.format(SearchMessages.JavaSearchOperation_pluralReferencesPostfix, args);
				case IJavaSearchConstants.ALL_OCCURRENCES:
					return Messages.format(SearchMessages.JavaSearchOperation_pluralOccurrencesPostfix, args);
				case IJavaSearchConstants.READ_ACCESSES:
					return Messages.format(SearchMessages.JavaSearchOperation_pluralReadReferencesPostfix, args);
				case IJavaSearchConstants.WRITE_ACCESSES:
					return Messages.format(SearchMessages.JavaSearchOperation_pluralWriteReferencesPostfix, args);
				default:
					String matchLocations= MatchLocations.getMatchLocationDescription(limitTo, 3);
					return Messages.format(SearchMessages.JavaSearchQuery_pluralReferencesWithMatchLocations, new Object[] { args[0], args[1], args[2], matchLocations });
			}
		}
	}

	private String getSearchPatternDescription() {
		if (fPatternData instanceof ElementQuerySpecification) {
			IJavaElement element= ((ElementQuerySpecification) fPatternData).getElement();
			return JavaElementLabels.getElementLabel(element, JavaElementLabels.ALL_DEFAULT
					| JavaElementLabels.ALL_FULLY_QUALIFIED | JavaElementLabels.USE_RESOLVED);
		}
		return BasicElementLabels.getFilePattern(((PatternQuerySpecification) fPatternData).getPattern());
	}

	private int getMaskedLimitTo() {
		return fPatternData.getLimitTo() & ~(IJavaSearchConstants.IGNORE_RETURN_TYPE | IJavaSearchConstants.IGNORE_DECLARING_TYPE);
	}

	ImageDescriptor getImageDescriptor() {
		int limitTo= getMaskedLimitTo();
		if (limitTo == IJavaSearchConstants.IMPLEMENTORS || limitTo == IJavaSearchConstants.DECLARATIONS)
			return JavaPluginImages.DESC_OBJS_SEARCH_DECL;
		else
			return JavaPluginImages.DESC_OBJS_SEARCH_REF;
	}

	public boolean canRerun() {
		return true;
	}

	public boolean canRunInBackground() {
		return true;
	}

	public ISearchResult getSearchResult() {
		if (fResult == null) {
			X10SearchResult result= new X10SearchResult(this);
			new SearchResultUpdater(result);
			fResult= result;
		}
		return fResult;
	}

	QuerySpecification getSpecification() {
		return fPatternData;
	}
}


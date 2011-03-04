package x10dt.search.ui.globalSearch;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.StringTokenizer;

import org.eclipse.search.ui.text.Match;
import org.eclipse.search.ui.text.MatchFilter;

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeParameter;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.core.search.SearchPattern;

import org.eclipse.jdt.internal.corext.util.JdtFlags;

import org.eclipse.jdt.ui.search.ElementQuerySpecification;
import org.eclipse.jdt.ui.search.PatternQuerySpecification;
import org.eclipse.jdt.ui.search.QuerySpecification;

import org.eclipse.jdt.internal.ui.JavaPlugin;

abstract class X10MatchFilter extends MatchFilter {

	public abstract boolean filters(X10ElementMatch match);

	/**
	 * Returns whether this filter is applicable for this query.
	 * 
	 * @param query the query
	 * @return <code>true</code> if this match filter is applicable for the given query
	 */
	public abstract boolean isApplicable(X10SearchQuery query);

	/* (non-Javadoc)
	 * @see org.eclipse.search.ui.text.MatchFilter#filters(org.eclipse.search.ui.text.Match)
	 */
	public boolean filters(Match match) {
		if (match instanceof X10ElementMatch) {
			return filters((X10ElementMatch) match);
		}
		return false;
	}

	private static final String SETTINGS_LAST_USED_FILTERS= "filters_last_used";  //$NON-NLS-1$

	public static MatchFilter[] getLastUsedFilters() {
		String string= JavaPlugin.getDefault().getDialogSettings().get(SETTINGS_LAST_USED_FILTERS);
		if (string != null) {
			return decodeFiltersString(string);
		}
		return getDefaultFilters();
	}

	public static void setLastUsedFilters(MatchFilter[] filters) {
		String encoded= encodeFilters(filters);
		JavaPlugin.getDefault().getDialogSettings().put(SETTINGS_LAST_USED_FILTERS, encoded);
	}

	public static MatchFilter[] getDefaultFilters() {
		return new MatchFilter[] { IMPORT_FILTER };
	}

	private static String encodeFilters(MatchFilter[] enabledFilters) {
		StringBuffer buf= new StringBuffer();
		for (int i= 0; i < enabledFilters.length; i++) {
			MatchFilter matchFilter= enabledFilters[i];
			buf.append(matchFilter.getID());
			buf.append(';');
		}
		return buf.toString();
	}

	private static X10MatchFilter[] decodeFiltersString(String encodedString) {
		StringTokenizer tokenizer= new StringTokenizer(encodedString, String.valueOf(';'));
		HashSet result= new HashSet();
		while (tokenizer.hasMoreTokens()) {
			X10MatchFilter curr= findMatchFilter(tokenizer.nextToken());
			if (curr != null) {
				result.add(curr);
			}
		}
		return (X10MatchFilter[]) result.toArray(new X10MatchFilter[result.size()]);
	}

	private static final X10MatchFilter POTENTIAL_FILTER= new PotentialFilter();
	private static final X10MatchFilter IMPORT_FILTER= new ImportFilter();
	private static final X10MatchFilter JAVADOC_FILTER= new X10docFilter();
	private static final X10MatchFilter READ_FILTER= new ReadFilter();
	private static final X10MatchFilter WRITE_FILTER= new WriteFilter();

	private static final X10MatchFilter POLYMORPHIC_FILTER= new PolymorphicFilter();
	private static final X10MatchFilter INEXACT_FILTER= new InexactMatchFilter();
	private static final X10MatchFilter ERASURE_FILTER= new ErasureMatchFilter();

	private static final X10MatchFilter NON_PUBLIC_FILTER= new NonPublicFilter();
	private static final X10MatchFilter STATIC_FILTER= new StaticFilter();
	private static final X10MatchFilter NON_STATIC_FILTER= new NonStaticFilter();
	private static final X10MatchFilter DEPRECATED_FILTER= new DeprecatedFilter();
	private static final X10MatchFilter NON_DEPRECATED_FILTER= new NonDeprecatedFilter();

	private static final X10MatchFilter[] ALL_FILTERS= new X10MatchFilter[] {
			POTENTIAL_FILTER,
			IMPORT_FILTER,
			JAVADOC_FILTER,
			READ_FILTER,
			WRITE_FILTER,

            POLYMORPHIC_FILTER,
			INEXACT_FILTER,
			ERASURE_FILTER,

			NON_PUBLIC_FILTER,
			STATIC_FILTER,
			NON_STATIC_FILTER,
			DEPRECATED_FILTER,
			NON_DEPRECATED_FILTER
	};

	public static X10MatchFilter[] allFilters() {
		return ALL_FILTERS;
	}

	public static X10MatchFilter[] allFilters(X10SearchQuery query) {
		ArrayList res= new ArrayList();
		for (int i= 0; i < ALL_FILTERS.length; i++) {
			X10MatchFilter curr= ALL_FILTERS[i];
			if (curr.isApplicable(query)) {
				res.add(curr);
			}
		}
		return (X10MatchFilter[]) res.toArray(new X10MatchFilter[res.size()]);
	}

	private static X10MatchFilter findMatchFilter(String id) {
		for (int i= 0; i < ALL_FILTERS.length; i++) {
			X10MatchFilter matchFilter= ALL_FILTERS[i];
			if (matchFilter.getID().equals(id))
				return matchFilter;
		}
		return null;
	}
}

class PotentialFilter extends X10MatchFilter {
	public boolean filters(X10ElementMatch match) {
		return match.getAccuracy() == SearchMatch.A_INACCURATE;
	}

	public String getName() {
		return SearchMessages.MatchFilter_PotentialFilter_name;
	}

	public String getActionLabel() {
		return SearchMessages.MatchFilter_PotentialFilter_actionLabel;
	}

	public String getDescription() {
		return SearchMessages.MatchFilter_PotentialFilter_description;
	}

	public boolean isApplicable(X10SearchQuery query) {
		return true;
	}

	public String getID() {
		return "filter_potential"; //$NON-NLS-1$
	}
}

class ImportFilter extends X10MatchFilter {
	public boolean filters(X10ElementMatch match) {
		return match.getElement() instanceof IImportDeclaration;
	}

	public String getName() {
		return SearchMessages.MatchFilter_ImportFilter_name;
	}

	public String getActionLabel() {
		return SearchMessages.MatchFilter_ImportFilter_actionLabel;
	}

	public String getDescription() {
		return SearchMessages.MatchFilter_ImportFilter_description;
	}

	public boolean isApplicable(X10SearchQuery query) {
		QuerySpecification spec= query.getSpecification();
		if (spec instanceof ElementQuerySpecification) {
			ElementQuerySpecification elementSpec= (ElementQuerySpecification) spec;
			IJavaElement element= elementSpec.getElement();
			switch (element.getElementType()) {
				case IJavaElement.TYPE:
				case IJavaElement.METHOD:
				case IJavaElement.FIELD:
				case IJavaElement.PACKAGE_FRAGMENT:
					return true;
				default:
					return false;
			}
		} else if (spec instanceof PatternQuerySpecification) {
			return true;
		}
		return false;
	}

	public String getID() {
		return "filter_imports"; //$NON-NLS-1$
	}
}

abstract class VariableFilter extends X10MatchFilter {
	public boolean isApplicable(X10SearchQuery query) {
		QuerySpecification spec= query.getSpecification();
		if (spec instanceof ElementQuerySpecification) {
			ElementQuerySpecification elementSpec= (ElementQuerySpecification) spec;
			IJavaElement element= elementSpec.getElement();
			return element instanceof IField || element instanceof ILocalVariable;
		} else if (spec instanceof PatternQuerySpecification) {
			PatternQuerySpecification patternSpec= (PatternQuerySpecification) spec;
			return patternSpec.getSearchFor() == IJavaSearchConstants.FIELD;
		}
		return false;
	}

}

class WriteFilter extends VariableFilter {
	public boolean filters(X10ElementMatch match) {
		return match.isWriteAccess() && !match.isReadAccess();
	}
	public String getName() {
		return SearchMessages.MatchFilter_WriteFilter_name;
	}
	public String getActionLabel() {
		return SearchMessages.MatchFilter_WriteFilter_actionLabel;
	}
	public String getDescription() {
		return SearchMessages.MatchFilter_WriteFilter_description;
	}
	public String getID() {
		return "filter_writes"; //$NON-NLS-1$
	}
}

class ReadFilter extends VariableFilter {
	public boolean filters(X10ElementMatch match) {
		return match.isReadAccess() && !match.isWriteAccess();
	}
	public String getName() {
		return SearchMessages.MatchFilter_ReadFilter_name;
	}
	public String getActionLabel() {
		return SearchMessages.MatchFilter_ReadFilter_actionLabel;
	}
	public String getDescription() {
		return SearchMessages.MatchFilter_ReadFilter_description;
	}
	public String getID() {
		return "filter_reads"; //$NON-NLS-1$
	}
}

class X10docFilter extends X10MatchFilter {
	public boolean filters(X10ElementMatch match) {
		return match.isJavadoc();
	}
	public String getName() {
		return SearchMessages.MatchFilter_JavadocFilter_name;
	}
	public String getActionLabel() {
		return SearchMessages.MatchFilter_JavadocFilter_actionLabel;
	}
	public String getDescription() {
		return SearchMessages.MatchFilter_JavadocFilter_description;
	}
	public boolean isApplicable(X10SearchQuery query) {
		return true;
	}
	public String getID() {
		return "filter_javadoc"; //$NON-NLS-1$
	}
}

class PolymorphicFilter extends X10MatchFilter {
    public boolean filters(X10ElementMatch match) {
        return match.isSuperInvocation();
    }

    public String getName() {
        return SearchMessages.MatchFilter_PolymorphicFilter_name;
    }

    public String getActionLabel() {
        return SearchMessages.MatchFilter_PolymorphicFilter_actionLabel;
    }

    public String getDescription() {
        return SearchMessages.MatchFilter_PolymorphicFilter_description;
    }

    public boolean isApplicable(X10SearchQuery query) {
        QuerySpecification spec= query.getSpecification();
        switch (spec.getLimitTo()) {
			case IJavaSearchConstants.REFERENCES:
			case IJavaSearchConstants.ALL_OCCURRENCES:
                if (spec instanceof ElementQuerySpecification) {
                    ElementQuerySpecification elementSpec= (ElementQuerySpecification) spec;
                    return elementSpec.getElement() instanceof IMethod;
                } else if (spec instanceof PatternQuerySpecification) {
                    PatternQuerySpecification patternSpec= (PatternQuerySpecification) spec;
                    return patternSpec.getSearchFor() == IJavaSearchConstants.METHOD;
                }
        }
        return false;
    }

    public String getID() {
        return "filter_polymorphic"; //$NON-NLS-1$
    }
}

abstract class GenericTypeFilter extends X10MatchFilter {
	public boolean isApplicable(X10SearchQuery query) {
		QuerySpecification spec= query.getSpecification();
		if (spec instanceof ElementQuerySpecification) {
			ElementQuerySpecification elementSpec= (ElementQuerySpecification) spec;
			Object element= elementSpec.getElement();
			ITypeParameter[] typeParameters= null;
			try {
				if (element instanceof IType) {
					typeParameters= ((IType)element).getTypeParameters();
				} else if (element instanceof IMethod) {
					typeParameters= ((IMethod)element).getTypeParameters();
				}
			} catch (JavaModelException e) {
				return false;
			}
			return typeParameters != null && typeParameters.length > 0;
		}
		return false;
	}
}

class ErasureMatchFilter extends GenericTypeFilter {
	public boolean filters(X10ElementMatch match) {
		return (match.getMatchRule() & (SearchPattern.R_FULL_MATCH | SearchPattern.R_EQUIVALENT_MATCH)) == 0;
	}
	public String getName() {
		return SearchMessages.MatchFilter_ErasureFilter_name;
	}
	public String getActionLabel() {
		return SearchMessages.MatchFilter_ErasureFilter_actionLabel;
	}
	public String getDescription() {
		return SearchMessages.MatchFilter_ErasureFilter_description;
	}
	public String getID() {
		return "filter_erasure"; //$NON-NLS-1$
	}
}

class InexactMatchFilter extends GenericTypeFilter {
	public boolean filters(X10ElementMatch match) {
		return (match.getMatchRule() & (SearchPattern.R_FULL_MATCH)) == 0;
	}
	public String getName() {
		return SearchMessages.MatchFilter_InexactFilter_name;
	}
	public String getActionLabel() {
		return SearchMessages.MatchFilter_InexactFilter_actionLabel;
	}
	public String getDescription() {
		return SearchMessages.MatchFilter_InexactFilter_description;
	}
	public String getID() {
		return "filter_inexact"; //$NON-NLS-1$
	}
}

abstract class ModifierFilter extends X10MatchFilter {
	public boolean isApplicable(X10SearchQuery query) {
		return true;
	}
}

class NonPublicFilter extends ModifierFilter {
	public boolean filters(X10ElementMatch match) {
		Object element= match.getElement();
		if (element instanceof IMember) {
			try {
				return ! JdtFlags.isPublic((IMember) element);
			} catch (JavaModelException e) {
				JavaPlugin.log(e);
			}
		}
		return false;
	}
	public String getName() {
		return SearchMessages.MatchFilter_NonPublicFilter_name;
	}
	public String getActionLabel() {
		return SearchMessages.MatchFilter_NonPublicFilter_actionLabel;
	}
	public String getDescription() {
		return SearchMessages.MatchFilter_NonPublicFilter_description;
	}
	public String getID() {
		return "filter_non_public"; //$NON-NLS-1$
	}
}

class StaticFilter extends ModifierFilter {
	public boolean filters(X10ElementMatch match) {
		Object element= match.getElement();
		if (element instanceof IMember) {
			try {
				return JdtFlags.isStatic((IMember) element);
			} catch (JavaModelException e) {
				JavaPlugin.log(e);
			}
		}
		return false;
	}
	public String getName() {
		return SearchMessages.MatchFilter_StaticFilter_name;
	}
	public String getActionLabel() {
		return SearchMessages.MatchFilter_StaticFilter_actionLabel;
	}
	public String getDescription() {
		return SearchMessages.MatchFilter_StaticFilter_description;
	}
	public String getID() {
		return 	"filter_static"; //$NON-NLS-1$
	}
}

class NonStaticFilter extends ModifierFilter {
	public boolean filters(X10ElementMatch match) {
		Object element= match.getElement();
		if (element instanceof IMember) {
			try {
				return ! JdtFlags.isStatic((IMember) element);
			} catch (JavaModelException e) {
				JavaPlugin.log(e);
			}
		}
		return false;
	}
	public String getName() {
		return SearchMessages.MatchFilter_NonStaticFilter_name;
	}
	public String getActionLabel() {
		return SearchMessages.MatchFilter_NonStaticFilter_actionLabel;
	}
	public String getDescription() {
		return SearchMessages.MatchFilter_NonStaticFilter_description;
	}
	public String getID() {
		return 	"filter_non_static"; //$NON-NLS-1$
	}
}

class DeprecatedFilter extends ModifierFilter {
	public boolean filters(X10ElementMatch match) {
		Object element= match.getElement();
		if (element instanceof IMember) {
			try {
				return JdtFlags.isDeprecated((IMember) element);
			} catch (JavaModelException e) {
				JavaPlugin.log(e);
			}
		}
		return false;
	}
	public String getName() {
		return SearchMessages.MatchFilter_DeprecatedFilter_name;
	}
	public String getActionLabel() {
		return SearchMessages.MatchFilter_DeprecatedFilter_actionLabel;
	}
	public String getDescription() {
		return SearchMessages.MatchFilter_DeprecatedFilter_description;
	}
	public String getID() {
		return 	"filter_deprecated"; //$NON-NLS-1$
	}
}

class NonDeprecatedFilter extends ModifierFilter {
	public boolean filters(X10ElementMatch match) {
		Object element= match.getElement();
		if (element instanceof IMember) {
			try {
				return !JdtFlags.isDeprecated((IMember) element);
			} catch (JavaModelException e) {
				JavaPlugin.log(e);
			}
		}
		return false;
	}
	public String getName() {
		return SearchMessages.MatchFilter_NonDeprecatedFilter_name;
	}
	public String getActionLabel() {
		return SearchMessages.MatchFilter_NonDeprecatedFilter_actionLabel;
	}
	public String getDescription() {
		return SearchMessages.MatchFilter_NonDeprecatedFilter_description;
	}
	public String getID() {
		return 	"filter_non_deprecated"; //$NON-NLS-1$
	}
}

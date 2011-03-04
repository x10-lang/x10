package x10dt.search.ui.typeHierarchy;

import org.eclipse.imp.preferences.PreferenceConstants;
import org.eclipse.jface.preference.IPreferenceStore;


public class X10Constants {
	public static final String TYPEFILTER_ENABLED = "";
	
	public static final String ID_TYPE_HIERARCHY= "x10dt.ui.TypeHierarchy"; //$NON-NLS-1$
    public static final String ID_HIERARCHYPERSPECTIVE= "x10dt.ui.perspective";

    /**
	 * A named preference that controls return type rendering of methods in the UI.
	 * <p>
	 * Value is of type <code>Boolean</code>: if <code>true</code> return types
	 * are rendered
	 * </p>
	 */
	public static final String APPEARANCE_METHOD_RETURNTYPE= "x10dt.search.ui.methodreturntype";//$NON-NLS-1$

	/**
	 * A named preference that controls type parameter rendering of methods in the UI.
	 * <p>
	 * Value is of type <code>Boolean</code>: if <code>true</code> return types
	 * are rendered
	 * </p>
	 * @since 3.1
	 */
	public static final String APPEARANCE_METHOD_TYPEPARAMETERS= "x10dt.search.ui.methodtypeparametesr";//$NON-NLS-1$

	
	/**
	 * A named preference that controls category rendering of Java elements in the UI.
	 * <p>
	 * Value is of type <code>Boolean</code>: if <code>true</code> category is rendered
	 * </p>
	 * @since 3.2
	 */
	public static final String APPEARANCE_CATEGORY= "x10dt.search.ui.category";//$NON-NLS-1$

	
	/**
	 * Type hierarchy view part: pop-up menu target ID for type hierarchy viewer
	 * (value <code>"org.eclipse.jdt.ui.TypeHierarchy.typehierarchy"</code>).
	 *
	 * @since 2.0
	 */
	public static final String TARGET_ID_HIERARCHY_VIEW= ID_TYPE_HIERARCHY + ".typehierarchy"; //$NON-NLS-1$

	/**
	 * Type hierarchy view part: pop-up menu target ID for supertype hierarchy viewer
	 * (value <code>"org.eclipse.jdt.ui.TypeHierarchy.supertypes"</code>).
	 *
	 * @since 2.0
	 */
	public static final String TARGET_ID_SUPERTYPES_VIEW= ID_TYPE_HIERARCHY + ".supertypes"; //$NON-NLS-1$

	/**
	 * Type hierarchy view part: Pop-up menu target ID for the subtype hierarchy viewer
	 * (value <code>"org.eclipse.jdt.ui.TypeHierarchy.subtypes"</code>).
	 *
	 * @since 2.0
	 */
	public static final String TARGET_ID_SUBTYPES_VIEW= ID_TYPE_HIERARCHY + ".subtypes"; //$NON-NLS-1$

	/**
	 * Type hierarchy view part: pop-up menu target ID for the member viewer
	 * (value <code>"org.eclipse.jdt.ui.TypeHierarchy.members"</code>).
	 *
	 * @since 2.0
	 */
	public static final String TARGET_ID_MEMBERS_VIEW= ID_TYPE_HIERARCHY + ".members"; //$NON-NLS-1$
	
	/**
	 * A string value used by the named preference <code>OPEN_TYPE_HIERARCHY</code>.
	 *
	 * @see #OPEN_TYPE_HIERARCHY
	 */
	public static final String OPEN_TYPE_HIERARCHY_IN_PERSPECTIVE= ID_HIERARCHYPERSPECTIVE; //$NON-NLS-1$

	/**
	 * A named preference that controls whether the 'sub-word navigation' feature is
	 * enabled.
	 * <p>
	 * Value is of type <code>Boolean</code>.
	 * </p>
	 * @since 3.0
	 */
	public final static String EDITOR_SUB_WORD_NAVIGATION= "subWordNavigation"; //$NON-NLS-1$

	/**
	 * A named preferences that controls if Java elements are also sorted by
	 * visibility.
	 * <p>
	 * Value is of type <code>Boolean</code>.
	 * </p>
	 * @since 3.0
	 */
	public static final String APPEARANCE_ENABLE_VISIBILITY_SORT_ORDER= "x10dt.search.ui.enable.visibility.order"; //$NON-NLS-1$

	/**
	 * A named preference that defines how member elements are ordered by the
	 * Java views using the <code>JavaElementSorter</code>.
	 * <p>
	 * Value is of type <code>String</code>: A comma separated list of the
	 * following entries. Each entry must be in the list, no duplication. List
	 * order defines the sort order.
	 * <ul>
	 * <li><b>T</b>: Types</li>
	 * <li><b>C</b>: Constructors</li>
	 * <li><b>I</b>: Initializers</li>
	 * <li><b>M</b>: Methods</li>
	 * <li><b>F</b>: Fields</li>
	 * <li><b>SI</b>: Static Initializers</li>
	 * <li><b>SM</b>: Static Methods</li>
	 * <li><b>SF</b>: Static Fields</li>
	 * </ul>
	 * </p>
	 * @since 2.1
	 */
	public static final String APPEARANCE_MEMBER_SORT_ORDER= "outlinesortoption"; //$NON-NLS-1$

	/**
	 * A named preference that defines how member elements are ordered by visibility in the
	 * Java views using the <code>JavaElementSorter</code>.
	 * <p>
	 * Value is of type <code>String</code>: A comma separated list of the
	 * following entries. Each entry must be in the list, no duplication. List
	 * order defines the sort order.
	 * <ul>
	 * <li><b>B</b>: Public</li>
	 * <li><b>V</b>: Private</li>
	 * <li><b>R</b>: Protected</li>
	 * <li><b>D</b>: Default</li>
	 * </ul>
	 * </p>
	 * @since 3.0
	 */
	public static final String APPEARANCE_VISIBILITY_SORT_ORDER= "x10dt.search.ui.visibility.order"; //$NON-NLS-1$


	public static void initializeDefaultValues(IPreferenceStore store) {
		store.setDefault(APPEARANCE_METHOD_RETURNTYPE, true);
		store.setDefault(APPEARANCE_METHOD_TYPEPARAMETERS, true);
		store.setDefault(APPEARANCE_CATEGORY, true);
		
		store.setDefault(EDITOR_SUB_WORD_NAVIGATION, true);
		
		// MembersOrderPreferencePage
		store.setDefault(APPEARANCE_MEMBER_SORT_ORDER, "T,SF,SI,SM,F,I,C,M"); //$NON-NLS-1$
		store.setDefault(APPEARANCE_VISIBILITY_SORT_ORDER, "B,V,R,D"); //$NON-NLS-1$
		store.setDefault(APPEARANCE_ENABLE_VISIBILITY_SORT_ORDER, false);

	}
}

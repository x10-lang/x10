package x10dt.search.ui.typeHierarchy;


import org.eclipse.osgi.util.NLS;

public class ActionMessages extends NLS {

	private static final String BUNDLE_NAME= "x10dt.search.ui.typeHierarchy.ActionMessages";//$NON-NLS-1$

	private ActionMessages() {
		// Do not instantiate
	}
	
	public static String MemberFilterActionGroup_hide_fields_label;
	public static String MemberFilterActionGroup_hide_fields_tooltip;
	public static String MemberFilterActionGroup_hide_fields_description;
	public static String MemberFilterActionGroup_hide_static_label;
	public static String MemberFilterActionGroup_hide_static_tooltip;
	public static String MemberFilterActionGroup_hide_static_description;
	public static String MemberFilterActionGroup_hide_nonpublic_label;
	public static String MemberFilterActionGroup_hide_nonpublic_tooltip;
	public static String MemberFilterActionGroup_hide_nonpublic_description;
	public static String MemberFilterActionGroup_hide_localtypes_label;
	public static String MemberFilterActionGroup_hide_localtypes_tooltip;
	public static String MemberFilterActionGroup_hide_localtypes_description;
	
	public static String ToggleLinkingAction_label;
	public static String ToggleLinkingAction_tooltip;
	public static String ToggleLinkingAction_description;
	
	public static String OpenTypeHierarchyAction_label;
	public static String OpenTypeHierarchyAction_tooltip;
	public static String OpenTypeHierarchyAction_description;
	public static String OpenTypeHierarchyAction_dialog_title;
	public static String OpenTypeHierarchyAction_messages_title;
	public static String OpenTypeHierarchyAction_messages_no_java_element;
	public static String OpenTypeHierarchyAction_messages_no_java_resources;
	public static String OpenTypeHierarchyAction_messages_no_types;
	public static String OpenTypeHierarchyAction_messages_no_valid_java_element;
	public static String OpenTypeHierarchyAction_messages_unknown_import_decl;
	
	public static String OpenViewActionGroup_showInAction_label;
	
	static {
		NLS.initializeMessages(BUNDLE_NAME, ActionMessages.class);
	}
}

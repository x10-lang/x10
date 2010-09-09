package x10dt.ui.editor.formatting;

/**
 * Interface containing constants identifying the X10 document partition types
 * @author rmfuhrer
 */
public interface IX10Partitions {
	/**
	 * The identifier of the X10 partitioning.
	 */
	String X10_PARTITIONING= "___x10_partitioning";  //$NON-NLS-1$

	/**
	 * The identifier of the single-line end comment partition content type.
	 */
	String X10_SINGLE_LINE_COMMENT= "__x10_singleline_comment"; //$NON-NLS-1$

	/**
	 * The identifier multi-line comment partition content type.
	 */
	String X10_MULTI_LINE_COMMENT= "__x10_multiline_comment"; //$NON-NLS-1$

	/**
	 * The identifier of the X10doc partition content type.
	 */
	String X10_DOC= "__x10_x10doc"; //$NON-NLS-1$

	/**
	 * The identifier of the X10 string partition content type.
	 */
	String X10_STRING= "__x10_string"; //$NON-NLS-1$

	/**
	 * The identifier of the X10 character partition content type.
	 */
	String X10_CHARACTER= "__x10_character";  //$NON-NLS-1$
}

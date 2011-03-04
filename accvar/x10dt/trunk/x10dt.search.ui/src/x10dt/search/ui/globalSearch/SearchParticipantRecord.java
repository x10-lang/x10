package x10dt.search.ui.globalSearch;


import org.eclipse.jdt.ui.search.IQueryParticipant;

/**
 */
public class SearchParticipantRecord {
	private SearchParticipantDescriptor fDescriptor;
	private IQueryParticipant fParticipant;

	public SearchParticipantRecord(SearchParticipantDescriptor descriptor, IQueryParticipant participant) {
		super();
		fDescriptor= descriptor;
		fParticipant= participant;
	}
	/**
	 * @return Returns the descriptor.
	 */
	public SearchParticipantDescriptor getDescriptor() {
		return fDescriptor;
	}
	/**
	 * @return Returns the participant.
	 */
	public IQueryParticipant getParticipant() {
		return fParticipant;
	}
}
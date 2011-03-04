package x10dt.search.ui.globalSearch;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;

import org.eclipse.core.resources.IProject;

import org.eclipse.jdt.internal.ui.JavaPlugin;

public class SearchParticipantsExtensionPoint {

	private Set fActiveParticipants= null;
	private static SearchParticipantsExtensionPoint fgInstance;

	public boolean hasAnyParticipants() {
		return Platform.getExtensionRegistry().getConfigurationElementsFor(X10SearchPage.PARTICIPANT_EXTENSION_POINT).length > 0;
	}

	private synchronized Set getAllParticipants() {
		if (fActiveParticipants != null)
			return fActiveParticipants;
		IConfigurationElement[] allParticipants= Platform.getExtensionRegistry().getConfigurationElementsFor(X10SearchPage.PARTICIPANT_EXTENSION_POINT);
		fActiveParticipants= new HashSet(allParticipants.length);
		for (int i= 0; i < allParticipants.length; i++) {
			SearchParticipantDescriptor descriptor= new SearchParticipantDescriptor(allParticipants[i]);
			IStatus status= descriptor.checkSyntax();
			if (status.isOK()) {
				fActiveParticipants.add(descriptor);
			} else {
				JavaPlugin.log(status);
			}
		}
		return fActiveParticipants;
	}

	private void collectParticipants(Set participants, IProject[] projects) {
		Iterator activeParticipants= getAllParticipants().iterator();
		Set seenParticipants= new HashSet();
		while (activeParticipants.hasNext()) {
			SearchParticipantDescriptor participant= (SearchParticipantDescriptor) activeParticipants.next();
			String id= participant.getID();
			if (participant.isEnabled() && !seenParticipants.contains(id)) {
				for (int i= 0; i < projects.length; i++) {
					try {
						if (projects[i].hasNature(participant.getNature())) {
							participants.add(new SearchParticipantRecord(participant, participant.create()));
							seenParticipants.add(id);
							break;
						}
					} catch (CoreException e) {
						JavaPlugin.log(e.getStatus());
						participant.disable();
					}
				}
			}
		}
	}



	public SearchParticipantRecord[] getSearchParticipants(IProject[] concernedProjects) {
		Set participantSet= new HashSet();
		collectParticipants(participantSet, concernedProjects);
		SearchParticipantRecord[] participants= new SearchParticipantRecord[participantSet.size()];
		return (SearchParticipantRecord[]) participantSet.toArray(participants);
	}

	public static SearchParticipantsExtensionPoint getInstance() {
		if (fgInstance == null)
			fgInstance= new SearchParticipantsExtensionPoint();
		return fgInstance;
	}

	public static void debugSetInstance(SearchParticipantsExtensionPoint instance) {
		fgInstance= instance;
	}
}


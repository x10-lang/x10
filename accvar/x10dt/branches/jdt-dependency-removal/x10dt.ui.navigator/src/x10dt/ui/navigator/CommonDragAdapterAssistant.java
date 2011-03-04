package x10dt.ui.navigator;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.Transfer;

public class CommonDragAdapterAssistant extends
		org.eclipse.ui.navigator.CommonDragAdapterAssistant {

	public CommonDragAdapterAssistant() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Transfer[] getSupportedTransferTypes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean setDragData(DragSourceEvent anEvent,
			IStructuredSelection aSelection) {
		// TODO Auto-generated method stub
		return false;
	}

}

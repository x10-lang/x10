/**
 * 
 */
package x10dt.ui.tests.waits;

import java.util.Iterator;
import java.util.List;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.utils.internal.Assert;

/**
 * @author rick lesniak
 *
 */
public class ViewHasText extends DefaultCondition {

	/**
	 * The tree (SWTBotView) instance to check.
	 */
	private final SWTBot	viewBot;

	/**
	 * Constructs an instance of the condition for the given view. 
	 * 
	 * @param view the view
	 * @return 
	 * @throws NullPointerException Thrown if the view is <code>null</code>.
	 */
	ViewHasText(SWTBotView view) {
			Assert.isNotNull(view, "The view can not be null"); //$NON-NLS-1$
			this.viewBot = view.bot();
		}

		/**
		 * Performs the check to see if the condition is satisfied.
		 * 
		 * @see org.eclipse.swtbot.swt.finder.waits.ICondition#test()
		 * @return <code>true</code> if the view contains text. Otherwise
		 *         <code>false</code> is returned.
		 */
		public boolean test() {
			List<String> viewLines = viewBot.styledText().getLines();
			Iterator<String> textLine = viewLines.listIterator(0);
			return (textLine.hasNext()) && (!textLine.next().equals(""));
		}

		/**
		 * Gets the failure message if the test is not satisfied.
		 * 
		 * @see org.eclipse.swtbot.swt.finder.waits.ICondition#getFailureMessage()
		 * @return The failure message.
		 */
		public String getFailureMessage() {
			return "Timed out waiting for " + viewBot.toString() + " to contain text'."; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}

}

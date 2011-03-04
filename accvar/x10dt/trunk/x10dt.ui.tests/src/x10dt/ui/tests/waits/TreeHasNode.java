/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Rick Lesniak (lesniakr@us.ibm.com) - initial API and implementation
 *******************************************************************************/

package x10dt.ui.tests.waits;

import org.eclipse.swtbot.swt.finder.utils.internal.Assert;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;

/**
 * @author rick lesniak
 *
 */
public class TreeHasNode extends DefaultCondition {

	/**
	 * The node name.
	 */
	private final String		nodeName;
	/**
	 * The tree (SWTBotTree) instance to check.
	 */
	private final SWTBotTree	tree;

	/**
	 * Constructs an instance of the condition for the given tree. The row count is used to know how many rows it needs
	 * to satisfy the condition.
	 * 
	 * @param tree the tree
	 * @param nodeName the item name the tree must have.
	 * @return 
	 * @throws NullPointerException Thrown if the table is <code>null</code>.
	 * @throws IllegalArgumentException Thrown if the item name is null.
	 */
	TreeHasNode(SWTBotTree tree, String nodeName) {
			Assert.isNotNull(tree, "The tree can not be null"); //$NON-NLS-1$
			Assert.isLegal(nodeName != null, "The node name must not be null"); //$NON-NLS-1$
			this.tree = tree;
			this.nodeName = nodeName;
		}

		/**
		 * Performs the check to see if the condition is satisfied.
		 * 
		 * @see org.eclipse.swtbot.swt.finder.waits.ICondition#test()
		 * @return <code>true</code> if the node name appears in the tree. Otherwise
		 *         <code>false</code> is returned.
		 */
		public boolean test() {
			return tree.select(nodeName) != null;
		}

		/**
		 * Gets the failure message if the test is not satisfied.
		 * 
		 * @see org.eclipse.swtbot.swt.finder.waits.ICondition#getFailureMessage()
		 * @return The failure message.
		 */
		public String getFailureMessage() {
			return "Timed out waiting for " + tree + " to contain node '" + nodeName + "'."; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}

}

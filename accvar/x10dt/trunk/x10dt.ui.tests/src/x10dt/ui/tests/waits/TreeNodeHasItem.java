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
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;

/**
 * @author rick lesniak
 *
 */

public class TreeNodeHasItem extends DefaultCondition {

	/**
	 * The item name.
	 */
	private final String		itemName;
	/**
	 * The tree (SWTBotTree) instance to check.
	 */
	private final SWTBotTreeItem	node;

	/**
	 * Constructs an instance of the condition for the given tree. The row count is used to know how many rows it needs
	 * to satisfy the condition.
	 * 
	 * @param node the tree
	 * @param itemName the item name the tree must have.
	 * @return 
	 * @throws NullPointerException Thrown if the table is <code>null</code>.
	 * @throws IllegalArgumentException Thrown if the item name is null.
	 */
	 TreeNodeHasItem(SWTBotTreeItem node, String itemName) {
		Assert.isNotNull(node, "The node can not be null"); //$NON-NLS-1$
		Assert.isLegal(itemName != null, "The item name must not be null"); //$NON-NLS-1$
		this.node = node;
		this.itemName = itemName;
	}

	/**
	 * Performs the check to see if the condition is satisfied.
	 * 
	 * @see org.eclipse.swtbot.swt.finder.waits.ICondition#test()
	 * @return <code>true</code> if the item name appears in the tree. Otherwise
	 *         <code>false</code> is returned.
	 */
	 /* this technique was taken from
	  * http://wiki.eclipse.org/SWTBot/Snippets#Tree_Expansion
	  * the idea is that you won't see additions to a node that's already expanded
	  * unless you collapse an re-expand it.
	  */
	public boolean test() {
		if (node.select().isExpanded())
			node.collapse();
		node.expand();
		return node.select(itemName) != null;
	}

	/**
	 * Gets the failure message if the test is not satisfied.
	 * 
	 * @see org.eclipse.swtbot.swt.finder.waits.ICondition#getFailureMessage()
	 * @return The failure message.
	 */
	public String getFailureMessage() {
		return "Timed out waiting for " + node + " to contain item '" + itemName + "'."; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

}

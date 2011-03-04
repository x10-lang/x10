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

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.waits.ICondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;

/**
 * @author rick lesniak
 *
 */

public abstract class X10DT_Conditions {

	/**
	 * Gets the condition for checking if a tree has a specific item.
	 * 
	 * @param tree the treeItem
	 * @param itemName the item name the tree must have, in order for {@link ICondition#test()} to evaluate to
	 *            <code>true</code>.
	 * @return <code>true</code> if the tree has the item specified. Otherwise <code>false</code>.
	 * @throws IllegalArgumentException Thrown if the item name is null.
	 * @throws NullPointerException Thrown if the view is <code>null</code>.
	 */
	public static ICondition treeNodeHasItem(SWTBotTreeItem tree, String itemName) {
		return new TreeNodeHasItem(tree, itemName);
	}

	/**
	 * Gets the condition for checking if a tree has a specific node.
	 * 
	 * @param tree the tree
	 * @param nodeName the node name the tree must have, in order for {@link ICondition#test()} to evaluate to
	 *            <code>true</code>.
	 * @return <code>true</code> if the tree has the node specified. Otherwise <code>false</code>.
	 * @throws IllegalArgumentException Thrown if the node name is null.
	 * @throws NullPointerException Thrown if the tree is <code>null</code>.
	 */
	public static ICondition treeHasNode(SWTBotTree tree,String nodeName) {
		return new TreeHasNode(tree, nodeName);
	}

	/**
	 * Gets the condition for checking if a view (e.g., Console) has styled text.
	 * 
	 * @param view the view
	 * @return <code>true</code> if the view has at least 1 line of text. Otherwise <code>false</code>.
	 * @throws NullPointerException Thrown if the view is <code>null</code>.
	 */
	public static ICondition viewHasText(SWTBotView view) {
		return new ViewHasText(view);
	}

	/**
	 * Gets the condition for checking tables have a minimum number of rows. Useful in cases where the table is
	 * populated continuously from a non UI thread.
	 *
	 * @param table the table
	 * @param rowCount the minimum number of rows that the table must have, in order for {@link ICondition#test()} to evaluate
	 *            to <code>true</code>.
	 * @return <code>true</code> if the table has the number of rows specified. Otherwise <code>false</code>.
	 * @throws IllegalArgumentException Thrown if the row count is less then 1.
	 * @throws NullPointerException Thrown if the tree is <code>null</code>.
	 */

	public static ICondition tableHasMinimumRows(SWTBotTable table, int rowCount) {
		return new TableHasMinimumRows(table, rowCount);
	}

}

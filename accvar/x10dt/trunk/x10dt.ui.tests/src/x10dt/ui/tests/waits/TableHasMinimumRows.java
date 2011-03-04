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
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;

/**
 * @author rick lesniak
 *
 */

/**
 * This class was adapted from class TableHasRows in the SWTBot distribution
 */
public class TableHasMinimumRows extends DefaultCondition {

	/**
	 * The row count.
	 */
	private final int			rowCount;
	/**
	 * The table (SWTBotTable) instance to check.
	 */
	private final SWTBotTable	table;

		/**
		 * Constructs an instance of the condition for the given table. The row count is used to know how many rows it needs
		 * to satisfy the condition.
		 *
		 * @param table the table
		 * @param rowCount the minimum number of rows needed.
		 * @throws NullPointerException Thrown if the table is <code>null</code>.
		 * @throws IllegalArgumentException Thrown if the row count is less then 1.
		 */
	TableHasMinimumRows(SWTBotTable table, int rowCount) {
			Assert.isNotNull(table, "The table can not be null"); //$NON-NLS-1$
			Assert.isLegal(rowCount >= 0, "The row count must be greater then zero (0)"); //$NON-NLS-1$
			this.table = table;
			this.rowCount = rowCount;
		}

		/**
		 * Performs the check to see if the condition is satisfied.
		 *
		 * @see org.eclipse.swtbot.swt.finder.waits.ICondition#test()
		 * @return <code>true</code> if the condition row count is >= the number of rows in the table. Otherwise
		 *         <code>false</code> is returned.
		 */
		public boolean test() {
			return table.rowCount() >= rowCount;
		}

		/**
		 * Gets the failure message if the test is not satisfied.
		 *
		 * @see org.eclipse.swtbot.swt.finder.waits.ICondition#getFailureMessage()
		 * @return The failure message.
		 */
		public String getFailureMessage() {
			return "Timed out waiting for " + table + " to contain at least" + rowCount + " rows."; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
}

/*
 * Task, statement, and dependency traversal functions for CCCC analysis toy.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 *
 * Copyright (c) 2007 Paul E. McKenney, IBM Corporation.
 */

/*
 * Using the specified integer variable, sequence through all tasks
 * that have at least one statment.
 */
#define for_each_task(taskid) \
	for ((taskid) = 0; (taskid) < MAX_TASKS; (taskid)++) \
		if (task_n_stmts[taskid] > 0)

/*
 * Given task t1, use the integer variable specified by t2 to cycle
 * through all tasks other than t1 that have at least one statement.
 */
#define for_each_other_task(t1, t2) \
	for_each_task(t2) \
		if (t1 != t2)

/*
 * Using the integer variable specified by s1, cycle through all
 * statements in task t.
 */
#define for_each_statement_in_task(t, s1) \
	for ((s1) = 0; (s1) < MAX_STMTS; (s1)++) \
		if (statement_exists((t), (s1)))

/*
 * Using the integer variable "earlier", cycle through all statements
 * in task t that precede statement s.
 */
#define for_each_earlier_statement_in_task(t, s, earlier) \
	for_each_statement_in_task(t, earlier) \
		if (earlier < s)

/*
 * Using the integer variable "later", cycle through all statements
 * in task t that precede statement s.
 */
#define for_each_later_statement_in_task(t, s, later) \
	for_each_statement_in_task(t, later) \
		if (later > s)

/*
 * Using the integer variables s1 and s2, cycle through all pairs
 * of statements in task t.  The earlier statement of the pair will
 * be in s1, the later of the pair in s2.
 */
#define for_each_statement_pair_in_task(t, s1, s2) \
	for_each_statement_in_task(t, s1) \
		for ((s2) = (s1) + 1; (s2) < MAX_STMTS; (s2)++) \
			if (statement_exists((t), (s2)))

/*
 * Using the integer variables s1, s2, and s3, cycle through all triples
 * of statements in task t.  The statements will be ordered s1, s2, and s3.
 */
#define for_each_statement_triple_in_task(t, s1, s2, s3) \
	for_each_statement_pair_in_task(t, s1, s2) \
		for ((s3) = (s2) + 1; (s3) < MAX_STMTS; (s3)++) \
			if (statement_exists((t), (s3)))

/*
 * Using the integer variables taskid and stmtid, cycle through all
 * statements in all tasks, in numerical order.
 */
#define for_each_statement(taskid, stmtid) \
	for_each_task(taskid) \
		for_each_statement_in_task(taskid, stmtid)

/*
 * Using the integer variables taskid and stmtid, cycle through all
 * statements that do reads in all tasks, in numerical order.
 */
#define for_each_statement_does_read(taskid, stmtid) \
	for_each_task(taskid) \
		for_each_statement_in_task(taskid, stmtid) \
			if (statement_does_read(taskid, stmtid))

/*
 * Using the integer variables taskid and stmtid, cycle through all
 * statements that do writes in all tasks, in numerical order.
 */
#define for_each_statement_does_write(taskid, stmtid) \
	for_each_task(taskid) \
		for_each_statement_in_task(taskid, stmtid) \
			if (statement_does_write(taskid, stmtid))

/*
 * Using the integer variables ft (from-task), fs (from-statement),
 * tt (to-task), and ts (to-statement), cycle through each pair of
 * statements across all tasks.  Each pair will be processed twice,
 * once for each order.  This approach is appropriate for dependency
 * processing, as dependencies might occur in either order.
 */
#define for_each_stmtpair(ft, fs, tt, ts) \
	for_each_task(ft) \
		for_each_statement_in_task(ft, fs) \
			for_each_task(tt) \
				for_each_statement_in_task(tt, ts)

/*
 * Cycle through all dependencies recorded in dm, using integer
 * variables ft (from-task), fs (from-statement), tt (to-task),
 * and ts (to-statement) to indicate each such dependency.
 */
#define for_each_dependency(dm, ft, fs, tt, ts) \
	for_each_stmtpair(ft, fs, tt, ts) \
		if ((dm)[ft][fs][tt][ts])

/*
 * Cycle through all variables using integer variable var.
 */
#define for_each_variable(var) \
	for ((var) = 0; (var) < MAX_VARS; (var)++)

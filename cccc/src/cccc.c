/*
 * CCCC analysis toy.  The exact definition of Local Consistency is
 * in a state of flux.
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
 * Copyright (c) 2007 Paul E. McKenney and Vijay Saraswat, IBM Corporation.
 */

#include <stdio.h>
#include <ctype.h>

#include "cccc-data.h"
#include "cccc-traverse.h"
#include "cccc-output.h"
#include "cccc-input.h"
#include "cccc-cycle.h"

/*
 * Check to make sure that write dependencies are full linear chains.
 * This checking assumes that the user has eliminated all warnings!
 */
void check_write_dependencies(int lineno)
{
	int var;
	int ft;
	int fs;
	int tt;
	int ts;
	int ndep[MAX_VARS] = { 0 };
	int nwrite[MAX_VARS] = { 0 };
	char buf[100];

	/* Assume that the user has cleared up other write warnings. */

	for_each_dependency(dep_map, ft, fs, tt, ts) {
		if (statement_does_write(ft, fs) &&
		    statement_does_write(tt, ts) &&
		    (task_vars[ft][fs] == task_vars[tt][ts])) {
			ndep[task_vars[ft][fs]]++;
		}
	}
	for_each_statement(ft, fs) {
		if (statement_does_write(ft, fs)) {
			nwrite[task_vars[ft][fs]]++;
		}
	}
	for_each_variable(var) {
		if ((nwrite[var] != 0) &&
		    (ndep[var] != nwrite[var] - 1)) {
			sprintf(buf,
				"Variable %c has non-linear write chain",
				var + 'a');
			warn(lineno, buf);
		}
	}
}

/*
 * Build all the program-order dependencies into po_map.
 * Later need to pay attention only to memory barriers @@@ .
 */
void build_program_order(void)
{
	int t;
	int fs;
	int ts;

	for_each_task(t) {
		for_each_statement_pair_in_task(t, fs, ts) {
			po_map[t][fs][t][ts] = 1;
		}
	}
}

/*
 * Update prop_map, and return 1 if this was a real change.
 */
int add_prop(ft, fs, tt, ts)
{
	if (prop_map[ft][fs][tt][ts]) {
		return 0;
	}
	prop_map[ft][fs][tt][ts] = 1;
	return 1;
}

/*
 * Do one cycle of the CCCC "propagation" of dependencies from and to
 * prop_map.
 * This function currently implicitly assumes each task's statements
 * run in program order, which will need to be reworked when explicit
 * memory barriers are added.  @@@
 */
int propagate_dependencies_help(void)
{
	int s1;
	int s2a;
	int s2b;
	int t1;
	int t2;
	int pc = 0;

	for_each_task(t2) {
		for_each_statement_pair_in_task(t2, s2a, s2b) {
			for_each_other_task(t2, t1) {
				for_each_statement_in_task(t1, s1) {
					if (dep_map[t1][s1][t2][s2a] ||
					    prop_map[t1][s1][t2][s2a]) {
						prop_map[t1][s1][t2][s2b] = 1;
						pc += add_prop(t1, s1, t2, s2b);
					}
					if (dep_map[t2][s2b][t1][s1] ||
					    prop_map[t2][s2b][t1][s1]) {
						prop_map[t2][s2a][t1][s1] = 1;
						pc += add_prop(t2, s2a, t1, s1);
					}
				}
			}
		}
	}
	return pc;
}

void propagate_dependencies(void)
{
	if (propagate_dependencies_help()) {
		while (propagate_dependencies_help()) {
			fprintf(stderr,
				"propagate_dependencies_help() repeatedly!\n");
		}
	}
}

/*
 * Build the CCCC local-consistency dependencies in array hb, based on the
 * specified taskid and dependency map.  To combine the effects of multiple
 * dependency maps, invoke this function on each in sequence.  This function
 * currently assumes tasks are program-ordered, which will need to change
 * when explicit memory barriers are added.
 */
void build_local_consistency_dep(int taskid, dependency_map_t dep)
{
	int ft;
	int fs;
	int tt;
	int ts;

	for_each_dependency(dep, ft, fs, tt, ts) {
		if ((ft == taskid) ||
		    (tt == taskid) ||
		    (statement_does_write(ft, fs) &&
		     statement_does_write(tt, ts) &&
		     (task_vars[ft][fs] == task_vars[tt][ts]))) {
			hb[ft][fs][tt][ts] = 1;
		}
	}
}

/*
 * Merge the first dependency map onto the second.
 */
void merge_dependency(dependency_map_t depsrc, dependency_map_t depdst)
{
	int ft;
	int fs;
	int tt;
	int ts;

	for_each_dependency(depsrc, ft, fs, tt, ts) {
		depdst[ft][fs][tt][ts] = 1;
	}
}

/*
 * Build the CCCC local-consistency dependencies in array hb for the
 * specified taskid.  Use the dep_map, po_map, and prop_map dependencies.
 */
void build_local_consistency(int taskid)
{

	clear_dependency_map(hb);
	build_local_consistency_dep(taskid, dep_map);
	merge_dependency(po_map, hb);
	/* build_local_consistency_dep(taskid, po_map); */
	build_local_consistency_dep(taskid, prop_map);  /* @@@ needed??? */
}

/*
 * Check the CCCC local-consistency criterion for each task in turn.
 */
void check_local_consistency(void)
{
	int foundcycle = 0;
	int taskid;

	for_each_task(taskid) {
		build_local_consistency(taskid);
		printf("Happens-before dependencies for task %d\n", taskid);
		dump_these_dependencies(hb);
		if (has_cycle(hb)) {
			printf("Task %d sees cycle\n", taskid);
			foundcycle = 1;
		}
	}
	if (foundcycle) {
		printf("Cycle found: illegal CCCC execution\n");
	} else {
		printf("No cycles found: plausible CCCC execution\n");
	}
}

/*
 * Take the program and explicit dependencies on standard input,
 * analyze, and output analysis on standard output.
 *
 * Input lines, statements:
 *	1.2 w a 1 # task 1 statement 2 write variable "a" value 1.
 *	1.3 r a 1 # task 1 statement 3 read variable "a" value 1.
 *	2.0 r a   # task 1 statement 0 read variable "a" value unspecified.
 * Input lines, explicit dependencies:
 *	1.2 -> 2.0 # task 2 statement 0 depends on task 1 statement 2.
 *
 * Fixed format.  Tasks range from 0-9.  Statements within tasks also
 * range from 0-9.  Variables range from a-z.  Values range from 0-9.
 * Operations are "r" (read), "w" (write), and "a" (atomic, e.g., CAS).
 * Memory barriers TBD. @@@
 * Trailing blanks and empty lines are ignored, as are any characters
 * following a pound sign ("#").
 *
 * Statements must be defined before a dependency may reference them.
 * Statements within a given task must appear in numerical order.
 */
int main(int argc, char *argv[])
{
	int nlines;

	initialize();
	nlines = read_program();
	check_write_dependencies(nlines);

	build_program_order();
	propagate_dependencies();

	dump_statements();
	dump_dependencies();
	dump_po_dependencies();
	dump_prop_dependencies();

	check_local_consistency();

	check_warnings();

	exit(0);
}

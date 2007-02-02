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
#include "cccc-output.h"
#include "cccc-input.h"
#include "cccc-cycle.h"
#include "cccc-dependencies.h"

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
 * Build the CCCC local-consistency dependencies in array hb for the
 * specified taskid.  Use the dep_map, po_map, and prop_map dependencies.
 */
void build_local_consistency(int taskid)
{

	clear_dependency_map(hb);
	build_local_consistency_dep(taskid, dep_map);
	build_local_consistency_dep(taskid, read_map);
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
	infer_read_dependencies(nlines);
	check_write_dependencies(nlines);

	build_program_order();
	propagate_dependencies();

	dump_statements();
	dump_dependencies();
	dump_read_dependencies();
	dump_po_dependencies();
	dump_prop_dependencies();

	check_local_consistency();

	check_warnings();

	exit(0);
}

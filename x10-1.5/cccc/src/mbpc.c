/*
 * CCCC analysis toy.  This does MBPC, "Memory-Barrier-Pair consistency",
 * which is looser than CCCC.  MBPC is too weak to support locking, but
 * is the documented behavior of Linux-kernel memory barriers as of
 * the 2.6.19 kernel.
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
#include <stdlib.h>
#include <string.h>

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
		    (tt == taskid)) {
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
		printf("Cycle found: illegal MBPC execution\n");
	} else {
		printf("No cycles found: plausible MBPC execution\n");
	}
}

/*
 * Take the program and explicit dependencies on standard input,
 * analyze, and output analysis on standard output.
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

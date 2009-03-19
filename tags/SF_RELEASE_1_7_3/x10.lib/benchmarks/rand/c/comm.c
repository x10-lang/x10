/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: comm.c,v 1.2 2007-05-03 11:40:33 srkodali Exp $
 * This file is part of X10 Runtime System.
 *
 * Original version of this code using LAPI C/C++ is due to
 * Hanhong Xue <hanhong@us.ibm.com>
 */

/** X10Lib Initialization & Termination **/
#include <stdio.h>
#include <x10/x10lib.h>
#include "gups.h"

/* globals */
int num_tasks;
int my_id;

/* initialize X10Lib */
void
x10lib_init(void)
{
	X10RC(x10_init(NULL, 0));
		
	/* disable parameter bounds checking & set run mode */
	X10RC(x10_setenv(ERROR_CHK, 0));
	if (do_interrupt)
		X10RC(x10_setenv(INTERRUPT_SET, 1));
	else
		X10RC(x10_setenv(INTERRUPT_SET, 0));
		
	/* register header handler function */
	X10RC(x10_register((void *)receive_update, 1));
	
	/* for timebound updates, fill address table */
	if (do_timebound) {
		X10RC(x10_addr_init((void *)&timebound_updates, addr_tbl));
	}
	 
	/* query the task ID and number of tasks */
	X10RC(x10_getenv(TASK_ID, &my_id));
	X10RC(x10_getenv(NUM_TASKS, &num_tasks));
	rrec.num_tasks = num_tasks; /* log number of tasks */
	
	/* get shared lock to prevent others from running */
	X10RC(x10_lock());
	
	X10RC(x10_gfence()); /* sync */
}

/* terminate X10Lib */
void
x10lib_term(void)
{
	X10RC(x10_unlock());
	X10RC(x10_finalize());
}

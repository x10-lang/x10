/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: gups.c,v 1.2 2007-05-03 11:40:34 srkodali Exp $
 * This file is part of X10 Runtime System.
 *
 * Original version of this code using LAPI C/C++ is due to
 * Hanhong Xue <hanhong@us.ibm.com>
 */

/** GUPS X10Lib main **/
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <unistd.h>
#include <x10/x10lib.h>
#include <sys/stat.h>
#include "gups.h"

/* macro for throwing out debugging info */
#define PRINT if (my_id == 0) printf

/* macro for generating random numbers and doing updates */
#define GENERATE_UPDATE(updates) \
do { \
	for (i = 0; i < (updates); i++) { \
		ran = (ran << 1) ^ ((int64_t) ran < 0 ? POLY : 0); \
		dest = (ran >> log_table_size) & (num_tasks - 1); \
		if (dest == my_id || do_embarassing) { \
			UPDATE((ran & (table_size - 1)), ran); \
		} else { \
			if (do_aggregate) \
				add_to_bucket(dest, ran); \
			else \
				send_update(dest, ran); \
		} \
	} \
} while (0)

/* number of updates */
int log_num_updates = -1;
uint64_t num_updates;

/* table and its size */
int log_table_size = TABLE_SIZE;
uint64_t table_size;
uint64_t *table = NULL;

/* output filename and its handler */
char *outfile = OUTPUT_FILE;
FILE *fp = NULL;

/* flags */
bool_t do_aggregate = FALSE;
bool_t do_embarassing = FALSE;
bool_t do_interrupt = FALSE;
/* timebound execution */
bool_t do_timebound = FALSE;
long timebound = TIME_BOUND;
uint64_t timebound_updates;
void *addr_tbl[MAX_TASKS];
/* for recording results */
results_rec rrec;

/* command line option string */
char *optstr = "aehn:s:t:f:";
char *prog_name;

/* lapi exposed handle */
lapi_handle_t hndl;

/* free table memory and other things upon exit */
void
clean_up(void)
{
	if (table) free ((void *)table);
	if (fp) fclose(fp);
	x10lib_term();
#ifdef DEBUG
	PRINT("%s: %s called; cleaning up...\n", prog_name, __FUNCTION__);
#endif
	return;
}

/* program entry point */
int
main(int argc, char *argv[])
{
	int ch;
	extern int optind;
	extern char *optarg;
	double tstart, tend, duration, gups;
	int64_t i;
	uint64_t ran;
	int dest;
	struct stat sb;
		
	
	if (prog_name = rindex(argv[0], '/'))
		prog_name += 1;
	else
		prog_name = argv[0];
		
	while ((ch = getopt(argc, argv, optstr)) != EOF) {
		switch (ch) {
			case 'a':
				do_aggregate = TRUE;
				break;
			case 'e':
				do_embarassing = TRUE;
				break;
			case 'i':
				do_interrupt = TRUE;
				break;
			case 't':
				do_timebound = TRUE;
				timebound = atoi(optarg);
				if (timebound <= 0) {
					fprintf(stderr, "!!Warning!! Timebound %d is invalid.",
								"  Defaulting to %d seconds...\n", timebound, TIME_BOUND);
					timebound = TIME_BOUND;
				}
				break;
			case 'f':
				outfile = optarg;
				if (access(optarg, F_OK) == 0 && access(optarg, W_OK) < 0) {
					fprintf(stderr, "!!Warning!! Couldn't access file \"%s\" for writing.\n",
							optarg);
					fprintf(stderr, "Results will be logged to \"%s\" file.\n",
							outfile);
					outfile = OUTPUT_FILE;
				}
				break;
			case 'n':
				log_num_updates = atoi(optarg);
				break;
			case 's':
				log_table_size = atoi(optarg);
				if (log_table_size <= 0) {
					fprintf(stderr, "!!Warning!! Table size %d is invalid."
							"  Defaulting to %d...\n", log_table_size, TABLE_SIZE);
					log_table_size = TABLE_SIZE;
				}
				break;
			case 'h':
				usage();
				exit(0);
			case '?':
			default:
				usage();
				exit(1);
		}
	}
	
	if (optind < argc) {
		usage();
		exit(1);
	}
	
	/* open log file for writing */
	if (!(fp = fopen(outfile, "a+"))) {
		fprintf(stderr, "%s: can't open file \"%s\" for logging.\n",
				prog_name, outfile);
		exit(1);
	}
	
	/* set table size and allocate table */
	table_size = 1UL << log_table_size;
	table = (uint64_t *)malloc(table_size * sizeof(uint64_t));
	if (!table) {
		fprintf(stderr, "%s: failed to allocate table of size %llu.\n",
				prog_name, table_size);
		exit(1);
	}
	memset(table, 0, table_size);
	
	/* set number of updates */	
	if (log_num_updates <= 0) {
		if (log_num_updates != -1) {
			fprintf(stderr, "!!Warning!! Number of updates %d is invalid."
					"  Defaulting to %d...\n", log_num_updates, (log_table_size + 2));
		}
		log_num_updates = log_table_size + 2;
	}
	num_updates = 1UL << log_num_updates;
	
	/* register exit handlers */
	atexit(clean_up);
	
	/* initialize messaging system  & expose handle */
	x10lib_init();
	hndl = x10_get_handle();
	
	/* we restrict to even number of processors */
	if (num_tasks & (num_tasks - 1)) {
		fprintf(stderr, "%s: number of tasks must be power of 2.\n", prog_name);
		exit(1);
	}
	
#ifdef DEBUG
	PRINT("prog_name: %s\n", prog_name);
	PRINT("num_tasks: %d\n", num_tasks);
	PRINT("log_table_size: %d table_size: %lld\n", log_table_size, table_size);
	PRINT("log_num_updates: %d num_updates: %lld\n", log_num_updates, num_updates);
	PRINT("flags=> do_embarassing: %d do_aggregate: %d do_timebound: %d do_interrupt: %d\n",
		do_embarassing, do_aggregate, do_timebound, do_interrupt);
	PRINT("outfile: %s\n", outfile);
#endif

	
	/* record table size, updates, and flags */
	rrec.log_table_size = log_table_size;
	rrec.table_size = table_size;
	rrec.log_num_updates = log_num_updates;
	rrec.num_updates = num_updates;
	rrec.do_embarassing = do_embarassing;
	rrec.do_aggregate = do_aggregate;
	rrec.do_timebound = do_timebound;
	rrec.do_interrupt = do_interrupt;
				
	/* initialize node buckets */
	init_buckets();
	
	/* start random number generator */
	ran = start_ran(my_id * num_updates);
#ifdef DEBUG
	PRINT("starting ran: %lld\n", ran);
#endif
	
	/* estimate number of updates in case of timebound */
	if (do_timebound) {
		/* compute time for sample updates */
		tstart = cur_time();
#ifdef DEBUG
	PRINT("sample updates for timebound start: %f usecs\n", tstart);
#endif
		GENERATE_UPDATE(num_updates / SAMPLE_FACTOR);
		X10RC(x10_gfence());
		tend = cur_time();
#ifdef DEBUG
	PRINT("sample updates for timebound end: %f usecs\n", tend);
#endif
		duration = tend - tstart;
		/* timebound in microseconds / time per update */
		timebound_updates = (timebound * 1e6) / (duration / (num_updates / SAMPLE_FACTOR));
#ifdef DEBUG
	PRINT("(before distribution) timebound_updates: %lld num_updates: %lld\n",
			timebound_updates, num_updates);
#endif
		if (my_id == 0) {
			uint64_t updates;
			int j;
			
			/* get timebound updates from each of the other tasks and compute
			 * the smallest number of updates
			 */
			for (j = 1; j < num_tasks; j++) {
				x10_gas_ref_t gas_ref;

				gas_ref = MakeGasRef(j, &addr_tbl[j]);
				X10RC(x10_get(gas_ref, (void *)&updates, 
						sizeof(timebound_updates)));
				X10RC(x10_fence());
				if (timebound_updates > updates)
					timebound_updates = updates;
			}
			/* distribute computed value to all other tasks */
			for (j = 1; j < num_tasks; j++) {
				x10_gas_ref_t gas_ref;

				gas_ref = MakeGasRef(j, &addr_tbl[j]);
				X10RC(x10_put((void *)&timebound_updates, gas_ref,
						sizeof(timebound_updates)));
			}
		}
		X10RC(x10_gfence());
#ifdef DEBUG
	PRINT("(after distribution) timebound_updates: %lld num_updates: %lld\n",
			timebound_updates, num_updates);
#endif
		num_updates = timebound_updates;
	}
	
	/* log actual number of updates to be done */
	rrec.done_updates = num_updates;
			
	/* start timer */
	tstart = cur_time();
#ifdef DEBUG
	PRINT("begin actual updates: %f usecs\n", tstart);
#endif
	
	/* perform actual updates */
	GENERATE_UPDATE(num_updates);
			
	if (do_aggregate)
		flush_buckets();
		
	X10RC(x10_gfence()); /* sync */
	
	/* compute gups */
	tend = cur_time();
#ifdef DEBUG
	PRINT("end actual updates: %f usecs\n", tend);
#endif
	duration = tend - tstart;
	gups = num_updates * num_tasks / duration / 1000;
#ifdef DEBUG
	PRINT("gups rate: %f\n", gups);
#endif
	
	/* log gups rate and duration */
	rrec.gups = gups;
	rrec.duration = duration * 1e-6;
	rrec.nsends = nsends;
	rrec.search_count = search_count;
#ifdef DEBUG
	PRINT("nsends: %ld search_count: %ld\n", nsends, search_count);
#endif

	/* write record entry to the log file */
	if (my_id == 0) {
		/* {num_tasks|log_table_size|table_size|log_num_updates|num_updates|
		 * done_updates|gups|duration|nsends|search_count|do_aggregate|
		 * do_interrupt|do_timebound|do_embarassing}
		 */
		/* write log file header only once */
		if (stat(outfile, &sb) == 0 && sb.st_size == 0) {
			fprintf(fp, "#\n");
			fprintf(fp, "##tasks|table size(log)|table size|total updates(log)|"
					"total updates|done updates|gups|time(secs)|sends|"
					"search count|aggregation|interrupt|timebound|embarassing##\n");
			fprintf(fp, "#\n");
			fflush(fp);
		}
		fprintf(fp, "{%d|%d|%lld|%d|%lld|%lld|%f|%.1f|%ld|%ld|%d|%d|%d|%d}\n",
				rrec.num_tasks, rrec.log_table_size, rrec.table_size,
				rrec.log_num_updates, rrec.num_updates, rrec.done_updates,
				rrec.gups, rrec.duration, rrec.nsends, rrec.search_count,
				rrec.do_aggregate, rrec.do_interrupt, rrec.do_timebound, rrec.do_embarassing);
		fflush(fp);
	}
		
	return 0;
}				

/* print command line help */
void
usage(void)
{
	fprintf(stderr, "Usage: HPCC RandomAccess Benchmark (X10Lib version)\n");
	fprintf(stderr, "    %s [-a|-e|-h|-i] [-n num_updates] [-s table_size]\n", prog_name);
	fprintf(stderr, "              [-t timebound] [-f filename]\n");
	fprintf(stderr, "  -a    use bucket sorting of updates (aggregation)\n");
	fprintf(stderr, "  -e    run in embarassingly parallel mode\n");
	fprintf(stderr, "  -i    run in interrupt mode\n");
	fprintf(stderr, "  -n    specify the number of updates to perform (log value)\n");
	fprintf(stderr, "  -s    specify the table size (log value)\n");
	fprintf(stderr, "  -t    specify the time bound for execution (seconds)\n");
	fprintf(stderr, "  -f    specify the filename to use for logging results\n");
	fprintf(stderr, "  -h    print this help screen\n");
	return;
}

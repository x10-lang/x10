/**
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2012
 *
 *  This file was written by Ben Herta for IBM: bherta@us.ibm.com
 *
 * This program will report the available collective names and indexes
 * for each collective used by X10.  It queries the world geometry only.
 *
 * This is a stand-alone program, which only needs PAMI, not X10.  Compile with
 * "mpCC BenchmarkCollectiveOptions.c"
 */

#ifndef _GNU_SOURCE
	#define _GNU_SOURCE
#endif
#include <stdio.h>
#include <stdlib.h>
#include <stdarg.h>
#include <string.h>
#include <errno.h>
#include <sys/time.h>
#include <sched.h>
#include <pami.h>
#include <pami_ext_hfi.h>

#define NUM_COLLECTIVES 6
// datasize is fixed at 3Gb per process, which is chopped up into bits based on nplaces
#define DATASIZE 1610612736
// how many times to repeat each test
#define REPEAT 3
// the smallest team worth testing.  If MP_PROCS is less than this value, we test just one team size: MP_PROCS
#define MIN_TEAM_SIZE 2048

pami_xfer_type_t collectives[] = {PAMI_XFER_BROADCAST, PAMI_XFER_BARRIER, PAMI_XFER_SCATTER, PAMI_XFER_ALLTOALL, PAMI_XFER_ALLREDUCE, PAMI_XFER_ALLGATHER};
const char* collectiveNames[] = {"PAMI_XFER_BROADCAST", "PAMI_XFER_BARRIER", "PAMI_XFER_SCATTER", "PAMI_XFER_ALLTOALL", "PAMI_XFER_ALLREDUCE", "PAMI_XFER_ALLGATHER"};

struct pami_state
{
	uint32_t numPlaces;
	uint32_t myPlaceId;
	pami_client_t client; // the PAMI client instance used for this place
	pami_context_t context; // PAMI context associated with the client
	pami_geometry_t world_geometry;
	pami_algorithm_t world_barrier;
//	struct result *results; // this is used only at task 0
#if !defined(__bgq__)
	pami_extension_t hfi_extension;
	hfi_remote_update_fn hfi_update;
#endif
} state;

static unsigned long long nano_time()
{
    struct timeval tv;
    gettimeofday(&tv, NULL);
    return (unsigned long long)(tv.tv_sec * 1000000000ULL + tv.tv_usec * 1000ULL);
}

void error(const char* msg, ...) {
	char buffer[1200];
	va_list ap;
	va_start(ap, msg);
	vsnprintf(buffer, sizeof(buffer), msg, ap);
	va_end(ap);
	strcat(buffer, "  ");
	int blen = strlen(buffer);
	PAMI_Error_text(buffer + blen, 1199 - blen);
	fprintf(stderr, "X10 PAMI error: %s\n", buffer);
	if (errno != 0)
		fprintf(stderr, "X10 PAMI errno: %s\n", strerror(errno));

	fflush(stderr);
	exit(EXIT_FAILURE);
}

// method to bind the process to a single processor
void thread_bind_cpu()
{
	// open the file specified by X10RT_CPUMAP
	char * filename = getenv("X10RT_CPUMAP");
	if (filename == NULL) return;
#ifdef __linux__
	FILE * fd = fopen(filename, "r");
	if (fd == NULL)
	{
		fprintf(stderr, "Unable to read %s, specified by X10RT_CPUMAP.  Continuing without cpu binding...\n", filename);
		return;
	}

	int lineNumber = 0;
	char buffer[32];
	while (lineNumber <= state.myPlaceId)
	{
		char* s = fgets(buffer, sizeof(buffer), fd);
		if (s == NULL)
		{
			fprintf(stderr, "Unable to bind place %u to a CPU because there %s only %i line%s in the file %s. Continuing without cpu binding...\n", state.myPlaceId, lineNumber==1?"is":"are", lineNumber, lineNumber==1?"":"s",filename);
			fclose(fd);
			return;
		}

		if (lineNumber < state.myPlaceId)
		{
			lineNumber++;
			continue;
		}

		int processor = (int) strtol(s, (char **)NULL, 10);
		if (processor==0 && (errno == EINVAL || errno == ERANGE))
			fprintf(stderr, "Unable to bind place %u to CPU \"%s\": %s.  Continuing without cpu binding...\n", state.myPlaceId, s, strerror(errno));

		cpu_set_t mask;
		CPU_ZERO(&mask); // disable all CPUs (all are enabled by default)
		CPU_SET(processor, &mask); // enable the one CPU specified in the file
		if( sched_setaffinity(0, sizeof(mask), &mask ) == -1 )
			fprintf(stderr, "Unable to bind place %u to CPU %i: %s. Continuing without cpu binding...\n", state.myPlaceId, processor, strerror(errno));
		break;
	}
	fclose(fd);
#else
	fprintf(stderr, "X10RT_CPUMAP is not supported on this platform.  Continuing without cpu binding....\n");
#endif
}


/*
 * This small method is used to hold-up some calls until the data transmission is complete, by simply decrementing a counter
 */
static void cookie_decrement (pami_context_t   context,
                       void          * cookie,
                       pami_result_t    result)
{
	unsigned * value = (unsigned *) cookie;
	--*value;
}


void test(int collective, int teamSize, int algorithmId, int dataSize, pami_algorithm_t algorithm, char* algName, char* dataSnd, char* dataRcv)
{
	pami_xfer_t operation;
	memset(&operation, 0, sizeof(operation));
	volatile unsigned waitForCompletion = 1;
	unsigned long long time;
	pami_result_t status = PAMI_ERROR;

	if (state.myPlaceId < teamSize)
	{
		// prepare the data structures
		switch(collectives[collective])
		{
			case PAMI_XFER_BROADCAST:
				operation.cmd.xfer_broadcast.root = 0;
				operation.cmd.xfer_broadcast.buf = dataSnd;
				operation.cmd.xfer_broadcast.type = PAMI_TYPE_BYTE;
				operation.cmd.xfer_broadcast.typecount = dataSize;
			break;
			case PAMI_XFER_SCATTER:
				operation.cmd.xfer_scatter.root = 0;
				operation.cmd.xfer_scatter.rcvbuf = dataRcv;
				operation.cmd.xfer_scatter.rtype = PAMI_TYPE_BYTE;
				operation.cmd.xfer_scatter.rtypecount = dataSize;
				operation.cmd.xfer_scatter.sndbuf = dataSnd;
				operation.cmd.xfer_scatter.stype = PAMI_TYPE_BYTE;
				operation.cmd.xfer_scatter.stypecount = dataSize;
			break;
			case PAMI_XFER_ALLTOALL:
				operation.cmd.xfer_alltoall.rcvbuf = dataRcv;
				operation.cmd.xfer_alltoall.rtype = PAMI_TYPE_BYTE;
				operation.cmd.xfer_alltoall.rtypecount = dataSize;
				operation.cmd.xfer_alltoall.sndbuf = dataSnd;
				operation.cmd.xfer_alltoall.stype = PAMI_TYPE_BYTE;
				operation.cmd.xfer_alltoall.stypecount = dataSize;
			break;
			case PAMI_XFER_ALLREDUCE:
				operation.cmd.xfer_allreduce.rcvbuf = dataRcv;
				operation.cmd.xfer_allreduce.rtype = PAMI_TYPE_UNSIGNED_LONG_LONG;
				operation.cmd.xfer_allreduce.rtypecount = dataSize/8;
				operation.cmd.xfer_allreduce.sndbuf = dataSnd;
				operation.cmd.xfer_allreduce.stype = PAMI_TYPE_UNSIGNED_LONG_LONG;
				operation.cmd.xfer_allreduce.stypecount = dataSize/8;
				operation.cmd.xfer_allreduce.op = PAMI_DATA_MAX;

				// known to segfault on triloka4
				if (strcmp("I1:ShortAllreduce:P2P:P2P", algName) == 0 || strcmp("I1:HybridShortAllreduce:SHMEM:CAU", algName) == 0)
					return;
			break;
			case PAMI_XFER_ALLGATHER:
				operation.cmd.xfer_allgather.rcvbuf = dataRcv;
				operation.cmd.xfer_allgather.rtype = PAMI_TYPE_BYTE;
				operation.cmd.xfer_allgather.rtypecount = dataSize;
				operation.cmd.xfer_allgather.sndbuf = dataSnd;
				operation.cmd.xfer_allgather.stype = PAMI_TYPE_BYTE;
				operation.cmd.xfer_allgather.stypecount = dataSize;
			break;
		}
		operation.algorithm = algorithm;
		operation.cookie = (void*)&waitForCompletion;
		operation.cb_done = cookie_decrement;

		if (state.myPlaceId == 0)
			printf("Testing Collective %s, team size %u, algorithm %u (%s), per-place datasize %u\n", collectiveNames[collective], teamSize, algorithmId, algName, dataSize);

		for (int i=1; i<=REPEAT; i++)
		{
			waitForCompletion = 1;
			if (state.myPlaceId == 0)
			{
				printf("   Run #%i: ", i);
				fflush(stdout);
				time = -nano_time();
			}
			status = PAMI_Collective(state.context, &operation);
			if (status != PAMI_SUCCESS) error("Unable to issue %s on teamsize %u", collectiveNames[collective], teamSize);
			while (waitForCompletion) PAMI_Context_advance(state.context, 100);
			if (state.myPlaceId == 0)
			{
				time+=nano_time();
				printf(" %lu ns\n", time);
			}
		}
	}

	// wait here, until all tasks, including those not in the team under test, complete
	waitForCompletion = 1;
	operation.algorithm = state.world_barrier;
	operation.cookie = (void*)&waitForCompletion;
	operation.cb_done = cookie_decrement;
	status = PAMI_Collective(state.context, &operation);
	if (status != PAMI_SUCCESS) error("Unable to issue a barrier on teamsize %u", teamSize);
	while (waitForCompletion) PAMI_Context_advance(state.context, 100);
}

int main(int argc, char ** argv) {
	size_t num_algorithms[2];
	pami_extension_t hfi_extension;
	hfi_remote_update_fn hfi_update;
	volatile unsigned waitForCompletion;
	pami_geometry_t currentGeometry;
	unsigned int geometryId = 0;
	int j;

	const char    *name = "X10";
	setenv("MP_MSG_API", name, 1);

	pami_result_t status = PAMI_Client_create(name, &state.client, NULL, 0);
	if (status != PAMI_SUCCESS)
		error("Unable to initialize PAMI client\n");

	pami_configuration_t configuration[2];
	configuration[0].name = PAMI_CLIENT_TASK_ID;
	configuration[1].name = PAMI_CLIENT_NUM_TASKS;

	if ((status = PAMI_Client_query(state.client, configuration, 2)) != PAMI_SUCCESS)
		error("Unable to query the PAMI_CLIENT: %i\n", status);
	state.myPlaceId = configuration[0].value.intval;
	state.numPlaces = configuration[1].value.intval;

	thread_bind_cpu();

	if ((status = PAMI_Context_createv(state.client, NULL, 0, &state.context, 1)) != PAMI_SUCCESS)
		error("Unable to initialize the PAMI context: %i\n", status);

	status = PAMI_Extension_open(state.client, "EXT_hfi_extension", &hfi_extension);
	if (status == PAMI_SUCCESS)
	{
		#ifdef __GNUC__
		__extension__
		#endif
		hfi_update = (hfi_remote_update_fn) PAMI_Extension_symbol(hfi_extension, "hfi_remote_update"); // This may succeed even if HFI is not available
	}

	{ // prepare the world geometry and barrier for between tests
		status = PAMI_Geometry_world(state.client, &state.world_geometry);
		if (status != PAMI_SUCCESS) error("Unable to create the world geometry");
		status = PAMI_Geometry_algorithms_num(state.world_geometry, PAMI_XFER_BARRIER, num_algorithms);
		if (status != PAMI_SUCCESS || num_algorithms[0] == 0) error("Unable to query the world barrier counts\n");

		// query what the different algorithms are
		pami_algorithm_t *always_works_alg = (pami_algorithm_t*) alloca(sizeof(pami_algorithm_t)*num_algorithms[0]);
		pami_metadata_t	*always_works_md = (pami_metadata_t*) alloca(sizeof(pami_metadata_t)*num_algorithms[0]);
		pami_algorithm_t *must_query_alg = (pami_algorithm_t*) alloca(sizeof(pami_algorithm_t)*num_algorithms[1]);
		pami_metadata_t	*must_query_md = (pami_metadata_t*) alloca(sizeof(pami_metadata_t)*num_algorithms[1]);
		status = PAMI_Geometry_algorithms_query(state.world_geometry, PAMI_XFER_BARRIER, always_works_alg,
				always_works_md, num_algorithms[0], must_query_alg, must_query_md, num_algorithms[1]);
		if (status != PAMI_SUCCESS) error("Unable to query the PAMI_XFER_BARRIER algorithm");
		state.world_barrier = always_works_alg[0];
	}

	// prepare a list of members for the teams that we create
	pami_task_t* teamMembers = (pami_task_t *)malloc(sizeof(pami_task_t)*state.numPlaces);
	for (int i=0; i<state.numPlaces; i++)
		teamMembers[i] = i;

	int teamSize = MIN_TEAM_SIZE;
	if (teamSize > state.numPlaces)
		teamSize = state.numPlaces;

	// data array for transfers
	char* dataSnd = (char*)malloc(DATASIZE);
	char* dataRcv = (char*)malloc(DATASIZE);
	if (dataRcv == NULL) error("Not enough memory!\n");

   for (int collective = 0; collective < NUM_COLLECTIVES; collective++) {
		do {
			if (state.myPlaceId == 0) printf("New team size = %u\n", teamSize);
			if (teamSize >= state.numPlaces) // handle teams that aren't a power of 2 in size
			{
				teamSize = state.numPlaces;
				currentGeometry = state.world_geometry;
				if (state.myPlaceId == 0) printf("using world geometry\n");
			}
			else
			{
				// create the geometry
				waitForCompletion = 1;
				pami_configuration_t config;
				config.name = PAMI_GEOMETRY_OPTIMIZE;
				pami_result_t status = PAMI_Geometry_create_tasklist(state.client, 0, &config, 1, &currentGeometry, state.world_geometry, ++geometryId, teamMembers, teamSize, state.context, cookie_decrement, (void*)&waitForCompletion);
				if (status != PAMI_SUCCESS) error("Unable to create a new team");
				while (waitForCompletion) PAMI_Context_advance(state.context, 100);

				if (state.myPlaceId == 0) printf("created geometry %u\n", geometryId);
			}

			// query the algorithms
			status = PAMI_Geometry_algorithms_num(currentGeometry, collectives[collective], num_algorithms);
			if (status != PAMI_SUCCESS || num_algorithms[0] == 0)
				error("Unable to query the algorithm counts\n");

			// query what the different algorithms are
			pami_algorithm_t *always_works_alg = (pami_algorithm_t*) alloca(sizeof(pami_algorithm_t)*num_algorithms[0]);
			pami_metadata_t	*always_works_md = (pami_metadata_t*) alloca(sizeof(pami_metadata_t)*num_algorithms[0]);
			pami_algorithm_t *must_query_alg = (pami_algorithm_t*) alloca(sizeof(pami_algorithm_t)*num_algorithms[1]);
			pami_metadata_t	*must_query_md = (pami_metadata_t*) alloca(sizeof(pami_metadata_t)*num_algorithms[1]);
			status = PAMI_Geometry_algorithms_query(currentGeometry, collectives[collective], always_works_alg,
					always_works_md, num_algorithms[0], must_query_alg, must_query_md, num_algorithms[1]);
			if (status != PAMI_SUCCESS)
				error("Unable to query the supported algorithm %s", collectiveNames[collective]);

			if (state.myPlaceId == 0) printf("found %u algorithms\n", num_algorithms[0]+num_algorithms[1]);

			// calculate the amount of data to shuffle around
			int dataSize = DATASIZE/teamSize;

			// test the algorithms
			for (j=0; j<num_algorithms[0]; j++)
				test(collective, teamSize, j, dataSize, always_works_alg[j], always_works_md[j].name, dataSnd, dataRcv);
			for (j=0; j<num_algorithms[1]; j++)
				test(collective, teamSize, j+num_algorithms[0], dataSize, must_query_alg[j], must_query_md[j].name, dataSnd, dataRcv);

			// destroy the team
			if (teamSize < state.numPlaces)
			{
				if (state.myPlaceId == 0) printf("destroying team %u\n", geometryId);
				waitForCompletion = 1;
				status = PAMI_Geometry_destroy(state.client, &currentGeometry, state.context, cookie_decrement, (void*)&waitForCompletion);
				if (status != PAMI_SUCCESS) error("Unable to destroy geometry");
				while (waitForCompletion) PAMI_Context_advance(state.context, 100);
			}
			
			teamSize = teamSize << 1;
		} while (teamSize <= state.numPlaces);
	}

	PAMI_Extension_close (hfi_extension);

	if ((status = PAMI_Context_destroyv(&state.context, 1)) != PAMI_SUCCESS)
		fprintf(stderr, "Error closing PAMI context: %i\n", status);
	if ((status = PAMI_Client_destroy(&state.client)) != PAMI_SUCCESS)
		fprintf(stderr, "Error closing PAMI client: %i\n", status);
	return 0;
}

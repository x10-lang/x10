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
 * "g++ -lpami ListCollectiveOptions.c"
 */

#include <stdio.h>
#include <stdlib.h>
#include <stdarg.h>
#include <string.h>
#include <errno.h> // for the strerror function
#include <pami.h>

#if !defined(__bgq__)
#include <pami_ext_hfi.h>
#endif

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

int main(int argc, char ** argv) {
	pami_client_t client;
	pami_context_t context;
	size_t num_contexts = 1;
	pami_task_t task_id;
	size_t num_tasks;
	pami_geometry_t world_geometry;
	/* Barrier variables */
	size_t num_algorithms[2];
	pami_xfer_type_t barrier_xfer = PAMI_XFER_BARRIER;
	pami_xfer_t barrier;
#if !defined(__bgq__)
	pami_extension_t hfi_extension;
	hfi_remote_update_fn hfi_update;
#endif
	int numAlgorithms = 6;
	pami_xfer_type_t algorithms[] = {PAMI_XFER_BROADCAST, PAMI_XFER_BARRIER,
			PAMI_XFER_SCATTER, PAMI_XFER_ALLTOALL, PAMI_XFER_ALLREDUCE, PAMI_XFER_ALLGATHER};
	const char* algorithmNames[] = {"PAMI_XFER_BROADCAST", "PAMI_XFER_BARRIER",
				"PAMI_XFER_SCATTER", "PAMI_XFER_ALLTOALL", "PAMI_XFER_ALLREDUCE", "PAMI_XFER_ALLGATHER"};

	const char    *name = "X10";
	setenv("MP_MSG_API", name, 1);
	pami_configuration_t config;
	config.name = PAMI_GEOMETRY_OPTIMIZE;
	config.value.intval = 1;

	pami_result_t status = PAMI_Client_create(name, &client, &config, 1);
	if (status != PAMI_SUCCESS)
		error("Unable to initialize PAMI client\n");

	if ((status = PAMI_Context_createv(client, &config, 1, &context, 1)) != PAMI_SUCCESS)
		error("Unable to initialize the PAMI context: %i\n", status);

#if !defined(__bgq__)
	status = PAMI_Extension_open (client, "EXT_hfi_extension", &hfi_extension);
	if (status == PAMI_SUCCESS)
	{
		#ifdef __GNUC__
		__extension__
		#endif
		hfi_update = (hfi_remote_update_fn) PAMI_Extension_symbol(hfi_extension, "hfi_remote_update"); // This may succeed even if HFI is not available
	}
#endif

	status = PAMI_Geometry_world(client, &world_geometry);
		if (status != PAMI_SUCCESS) error("Unable to create the world geometry");

	pami_configuration_t configuration[2];
	configuration[0].name = PAMI_CLIENT_TASK_ID;
	configuration[1].name = PAMI_CLIENT_NUM_TASKS;
	if ((status = PAMI_Client_query(client, configuration, 2)) != PAMI_SUCCESS)
		error("Unable to query the PAMI_CLIENT: %i\n", status);
	int myPlaceId = configuration[0].value.intval;

	if (myPlaceId == 0)
	{
		for (int i = 0; i < numAlgorithms; i++) {
			status = PAMI_Geometry_algorithms_num(world_geometry, algorithms[i], num_algorithms);
			if (status != PAMI_SUCCESS || num_algorithms[0] == 0)
				error("Unable to query the algorithm counts for barrier\n");

			// query what the different algorithms are
			pami_algorithm_t *always_works_alg = (pami_algorithm_t*) alloca(sizeof(pami_algorithm_t)*num_algorithms[0]);
			pami_metadata_t	*always_works_md = (pami_metadata_t*) alloca(sizeof(pami_metadata_t)*num_algorithms[0]);
			pami_algorithm_t *must_query_alg = (pami_algorithm_t*) alloca(sizeof(pami_algorithm_t)*num_algorithms[1]);
			pami_metadata_t	*must_query_md = (pami_metadata_t*) alloca(sizeof(pami_metadata_t)*num_algorithms[1]);
			status = PAMI_Geometry_algorithms_query(world_geometry, algorithms[i], always_works_alg,
					always_works_md, num_algorithms[0], must_query_alg, must_query_md, num_algorithms[1]);
			if (status != PAMI_SUCCESS)
				error("Unable to query the supported algorithm %s for world", algorithmNames[i]);

			// print out
			printf("Collective: %s\n", algorithmNames[i]);
			printf("Always supported: %i algorithms\n", num_algorithms[0]);
			for (int j=0; j<num_algorithms[0]; j++)
				printf("\t%s = %i\n", always_works_md[j].name, j);
			printf("Locally supported: %i algorithms\n", num_algorithms[1]);
			for (int j=0; j<num_algorithms[1]; j++)
				printf("\t%s = %i\n", must_query_md[j].name, num_algorithms[0]+j);
			printf("\n");
		}
	}

#if !defined(__bgq__)
	PAMI_Extension_close (hfi_extension);
#endif

	if ((status = PAMI_Context_destroyv(&context, 1)) != PAMI_SUCCESS)
		fprintf(stderr, "Error closing PAMI context: %i\n", status);
	if ((status = PAMI_Client_destroy(&client)) != PAMI_SUCCESS)
		fprintf(stderr, "Error closing PAMI client: %i\n", status);
	return 0;
}

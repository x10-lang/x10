/* $Id: standalone.cc,v 1.2 2007-12-10 16:44:40 ganeshvb Exp $ */
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <unistd.h>
#include <lapi.h>
#include <arpa/inet.h>

/*
** Following #defines are for use with the -list option only. They
** are ignored with the -hndlr option.
*/ 
#define IP_STRING "127.0.0.1"   /* change to addr for even tasks */
#define BASE_PORT      62555           /* change this to the base port number */ 

/* Setup the initial environment, reading command line args and */
/* number of tasks from env var */
static int do_setup(int* num_tasks)
{
    char *mp_procs = NULL;   /* for MP_PROCS */

    /*
    ** query number of tasks from the user environment.  Note that when
    ** running in standalone mode, this information must be known
    ** before calling LAPI_Init and thus cannot be queried from LAPI
    ** as is normally done.
    */ 
    mp_procs = getenv("MP_PROCS");
    *num_tasks = atoi(mp_procs);
    if ( *num_tasks <= 0 ) {
      fprintf(stderr,"Error: MP_PROCS is %s, " 
	      "should be the number of tasks.", mp_procs);
      return 1;
    }
    
    return LAPI_SUCCESS;    
}
   

static int setup_addr_list(lapi_udp_t *addr_list, int num_tasks)
{
    int i;               /* loop counter */


    /* set ip, port info array */
    for( i = 0; i < num_tasks; i++ ) {
      addr_list[i].ip_addr = inet_addr(IP_STRING); 
      addr_list[i].port_no = (unsigned) (BASE_PORT+i);            
      //printf("Assigning addr_list[%d].ip_addr:%lu\n",
//	      i, addr_list[i].ip_addr);
  //   printf( "Assigning addr_list[%d].port_no:%u\n",
//	      i, addr_list[i].port_no);
    }
    return LAPI_SUCCESS;
}


void InitStandAlone (lapi_info_t* info) {
  int           rc;            /* Return code from func. calls */
  lapi_handle_t lapi_handle;   /* LAPI handle */
  lapi_info_t   lapi_info;     /* Info to pass to LAPI_Init */
  lapi_extend_t* extend_info;   /* Structure to hold IP addresses and ports */
  int           task_id;       /* Our LAPI Task id */
  int           num_tasks;     /* number of tasks in job */
  int           i, j;          /* Loop counters */
  lapi_udp_t    *udp_info;     /* List of ip, port info to pass to LAPI */

  if ( (rc = do_setup(&num_tasks)) != 0 ) {
    fprintf(stderr,"Error during setup\n");
    exit(rc);
  }
  
  /* Clear the struct (values in future fields will cause error). */
  memset(&lapi_info, 0, sizeof(lapi_info_t));
  extend_info = new lapi_extend_t;
 
  /* Assign the address of the lapi_extend_t to pass to LAPI */
  lapi_info.add_info = extend_info;
  
  udp_info = new lapi_udp_t[num_tasks];
  if ( (rc = setup_addr_list(udp_info, num_tasks)) != 0 ) {
    fprintf(stderr,"Error during setup_addr_list\n");
    exit(rc);
  }

 /* assign list to info struct */
  lapi_info.add_info->add_udp_addrs = udp_info; 
  lapi_info.add_info->num_udp_addr = num_tasks;
  
  /* clear handler so not used */
  lapi_info.add_info->udp_hndlr = 0;

  *info = lapi_info;  
}

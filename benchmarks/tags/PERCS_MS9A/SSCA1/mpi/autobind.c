#include <unistd.h>
#include <stdio.h>
#include <string.h>
#include <assert.h>
#include <stdlib.h>

#ifdef AIX

#include <sys/processor.h>
#include <sys/thread.h>

/* *********************************************************************** */
/*    build a list of tasks that are local. Return my index in the list.   */
/* *********************************************************************** */

static void GetLocalTaskIndex (int ntasks, int mytask,
			       int * nlocaltasks, int * myindex)
{
  /* ----------------------------------- */
  /*  read # of common tasks             */
  /* ----------------------------------- */

  int nctask = 0;
  const char * ctask = (const char *) getenv ("MP_COMMON_TASKS");
  {
    assert (ctask != NULL);
    sscanf (ctask, "%d", &nctask);
    ctask = strchr(ctask, ':');
    * nlocaltasks = nctask + 1;
  }

  /* ----------------------------------- */
  /* scan list of common tasks           */
  /* ----------------------------------- */

  int counter = 0;
  while (nctask>0)
    {
      int taskID;
      assert (ctask != NULL);
      sscanf (ctask+1, "%d", & taskID);
      ctask = strchr(ctask+1, ':');
      if (taskID < mytask) counter++;
      nctask--;
    }
  * myindex = counter;
}

void autobind (int ntasks, int mytask)
{
  int taskspernode, mytaskindex;
  GetLocalTaskIndex (ntasks, mytask, &taskspernode, &mytaskindex);

  int bindspernode = taskspernode;
  int mybind       = mytaskindex;

  int noProcessorConf   = sysconf(_SC_NPROCESSORS_CONF);

  if (bindspernode > noProcessorConf) 
    {
      fprintf(stderr, "Cannot bind threads: too many tasks\n");
      return;
    }

  while (2*bindspernode <= noProcessorConf)
    {
      bindspernode = bindspernode * 2;
      mybind = mybind * 2;
    }

  char hostname[1024];
  gethostname (hostname, sizeof(hostname));
  fprintf (stderr, "Binding task=%d/%d to thread=%d on host=%s\n",
	   mytask, ntasks, mybind, hostname);
  bindprocessor (BINDTHREAD, thread_self(), mybind);
}

#else
void autobind (int ntasks, int mytask) {
    fprintf (stderr, "autobind not implemented on this platform\n");
}
#endif

    


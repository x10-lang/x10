#include <stdio.h>
#include "mach_def.h"
#if (_MACH_LOAD == TRUE)
#include <sys/table.h>
#endif /* _MACH_LOAD */

#include "simple.h"
#include "alg_load.h"

#define DEBUG 0

double *work_load;
int _simple_load_init = 0;

#if 0
#if (_MACH_LOAD == TRUE)
int get_load_avg(double *load_avg) {
    int i;
    struct tbl_loadavg labuf;

    /* get load averages */
    if (table(TBL_LOADAVG,0,&labuf,1,sizeof(struct tbl_loadavg))<0) {
        perror("TBL_LOADAVG");
        return(-1);
    }
    if (labuf.tl_lscale)   /* scaled */
        for(i=0;i<3;i++) 
            load_avg[i] = ((double)labuf.tl_avenrun.l[i] / 
                                            (double)labuf.tl_lscale );
    else                   /* not scaled */
        for(i=0;i<3;i++) 
            load_avg[i] = labuf.tl_avenrun.d[i];

    return(0);
}
#else /* _MACH_LOAD */
int get_load_avg(double *load_avg) {
  int i;
  for (i=0 ; i<3 ; i++)
    load_avg[i] = 0.0;
  return(0);
}
#endif /* _MACH_LOAD */

void simple_load_init() {
    _simple_load_init = 1;
    work_load = (double *)malloc(NODES*sizeof(double));
    return;
}

void simple_load_free() {
    _simple_load_init = 0;
    free(work_load);
    return;
}
    
double get_load() {
    double load_avg[4], total_load, frac, fsum, la;
    int i;

    if (!_simple_load_init)
	simple_load_init();

    get_load_avg(load_avg);

    la = load_avg[0];

#if DEBUG
    fprintf(outfile,"PE%3d: load: %4.4f\n",MYNODE,la);
    fflush(outfile);
#endif /* DEBUG */

    load_avg[0] = 1.0/(1.0 + load_avg[0]);
    
    MPI_Allreduce(load_avg, &total_load, 1, MPI_DOUBLE, MPI_SUM,
		  MPI_COMM_WORLD);

    frac = load_avg[0] / total_load;

#if DEBUG
    fprintf(outfile,"PE%3d: normload: %f  frac: %f\n",
	    MYNODE,load_avg[0],frac);
    fflush(outfile);
#endif /* DEBUG */

    frac = rint(1000.0 * frac)/1000.0;
    MPI_Allgather(&frac, 1, MPI_DOUBLE, work_load, 1, MPI_DOUBLE,
		  MPI_COMM_WORLD);

    fsum = 0.0;
    for (i=1 ; i<NODES ; i++)
	fsum += work_load[i];
    work_load[0] = 1.0 - fsum;

    return (la);
}

void partition_work(int n, int *part) {

    register int i, psum;

    check_load();

    psum = 0;
    for (i=1 ; i<NODES ; i++) {
	part[i] = (int)floor(work_load[i] * (double)n);
	psum += part[i];
    }
    part[0] = n - psum;
    
#if DEBUG
    on_one 
	for (i=0 ; i<NODES ; i++)
	    fprintf(outfile,"FracWork%3d: %4.3f  part[%3d]: %12d\n",
		    i,work_load[i],i,part[i]);
    fflush(outfile);
#endif /* DEBUG */
    return;
}
#endif /* 0 */

#include <stdio.h>
#include <stdlib.h>
#include "types.h"
#include "mach_def.h"

FILE *outfile;

void
assert_malloc(void *ptr) {
  if (ptr==NULL) 
    perror("ERROR: assert_malloc");
}

#if (_MACH_CLOCK == _MACH_CLOCK_GETTIME)
#if 0
struct timeval umd_base_time;

double get_seconds()
{
	struct timeval t,lapsed;
	struct timezone z;
	
	gettimeofday(&t,&z);
	if (umd_base_time.tv_usec > t.tv_usec) {
		t.tv_usec += 1000000;
		t.tv_sec--;
	}
    
	lapsed.tv_usec = t.tv_usec - umd_base_time.tv_usec;
	lapsed.tv_sec = t.tv_sec - umd_base_time.tv_sec;

	return (double)lapsed.tv_sec+((double)lapsed.tv_usec/(double)1000000.0) ;
}
#else /* 1 */
double get_seconds()
{
	struct timeval t;
	struct timezone z;
	gettimeofday(&t,&z);
	return (double)t.tv_sec+((double)t.tv_usec/(double)1000000.0);
}
#endif /* 0 */
#endif /* (_MACH_CLOCK == _MACH_CLOCK_GETTIME) */

void
main_get_args(int argc, char **argv) {
  char
    *outfilename = NULL,
    *s,**argvv = argv;

#if 0
  {
    int i;
    printf("main argc: %d\n",argc);
    for (i=0 ; i <argc ; i++)
      printf("main argv[%2d]: %s\n",i,argv[i]);
  }
#endif /* 0 */
  
  while (--argc > 0 && (*++argvv)[0] == '-')
    for (s=argvv[0]+1; *s != '\0'; s++) 
      switch (*s) {
      case 'o':
	if (argc <= 1) 
	  perror("output filename expected after -o (e.g. -o filename)");
	argc--;
	outfilename = (char *)malloc(MAXLEN*sizeof(char));
	strcpy(outfilename, *++argvv); 
#if 0
	printf("main_get_args: outfile: %s\n",outfilename);
#endif /* 0 */
	break;
      case 'h':
	fprintf(stdout,"Main Options:\n");
	fprintf(stdout," -o outfilename\n");
	fprintf(stdout,"\n\n");
	break;
/*    default: perror("illegal option"); */
      }

  if (outfilename == NULL)
    outfile = stdout;
  else
    outfile = fopen(outfilename,"a+");
  
}

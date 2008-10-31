/*
 * Define CLASS accordingly
 *
 * CLASS=1 => NPB CLASS S
 * CLASS=2 => NPB CLASS W
 * CLASS=3 => NPB CLASS A
 * CLASS=4 => NPB CLASS B
 * CLASS=5 => NPB CLASS C
 * CLASS=6 => NPB CLASS D
 * others  => Debug cube
 */

#define SS 1
#define WW 2
#define AA 3
#define BB 4
#define CC 5
#define DD 6

#ifndef CLASS
  #define CLASS AA
#endif

#if CLASS == SS /* Class S */
#define NX	    4
#define NY	    4
#define NZ	    4
#define MAXDIM	    4
#define MAX_ITER    6
static char *class_id_str = "NPB CLASS S 64x64x64 6 iterations";
static char class_id_char = 'S';

#elif CLASS == WW /* Class W */
#define NX	    128
#define NY	    128
#define NZ	    32 
#define MAXDIM	    128
#define MAX_ITER    6
static char *class_id_str = "NPB CLASS W 128x128x32 6 iterations";
static char class_id_char = 'W';

#elif CLASS == AA /* Class A */
#define NX	    256
#define NY	    256
#define NZ	    128
#define MAXDIM	    256
#define MAX_ITER    6
static char *class_id_str = "NPB CLASS A 256x256x128 6 iterations";
static char class_id_char = 'A';

#elif CLASS == BB /* Class B */
#define NX	    512
#define NY	    256
#define NZ	    256
#define MAXDIM	    512
#define MAX_ITER    20
static char *class_id_str = "NPB CLASS B 512x256x256 20 iterations";
static char class_id_char = 'B';

#elif CLASS == CC /* Class C */
#define NX	    512
#define NY	    512
#define NZ	    512
#define MAXDIM	    512
#define MAX_ITER    20
static char *class_id_str = "NPB CLASS C 512x512x512 20 iterations";
static char class_id_char = 'C';

#elif CLASS == DD /* Class D */
#define NX	    2048
#define NY	    1024
#define NZ	    1024
#define MAXDIM	    2048
#define MAX_ITER    25
static char *class_id_str = "NPB CLASS D 2048x1024x1024 25 iterations";
static char class_id_char = 'D';

#elif CLASS == XX /* Some debug cube */
#define NX	   16 
#define NY	   16 
#define NZ	   16 
#define MAXDIM	   16 
#define MAX_ITER   1 
static char *class_id_str = "DEBUG 4x4x2 6 iterations";
static char class_id_char = '?';
#endif


#define NTOTAL NX*NY*NZ
#define A (5.0*5.0*5.0*5.0*5.0*5.0*5.0*5.0*5.0*5.0*5.0*5.0*5.0)
#define SEED 314159265.0


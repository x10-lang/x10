#ifndef _TIMING_H
#define _TIMING_H

#include <string.h>

#define TIME_STEPS   25
#define STEP_MAXLEN  80
#define TSTRLEN      25

/******************************************************************/

static int _timer_level = 0;

#define all_timer_init() \
  int step; \
  double _secs[TIME_STEPS]; \
  char   _stepname[TIME_STEPS][STEP_MAXLEN];
#define all_timer_start() \
  all_Barrier(TH); \
  _secs[step] = get_seconds(); {}
#define all_timer_reset() \
  step = 0; \
  on_one_thread _timer_level++; \
  node_Barrier(); \
  all_timer_start(); {}
#define all_timer_mark(msg) \
  _secs[step] = get_seconds() - _secs[step]; \
  strcpy(_stepname[step], msg); \
  step++; \
  _secs[step] = get_seconds(); {}
#define all_timer_report(strm, msg) { \
  int ts, sl; \
  for (ts = 0 ; ts<step ; ts++) \
    _secs[ts] = all_Reduce_d(_secs[ts], MAX, TH); \
  on_one { \
    for (ts=0 ; ts<step ; ts++) { \
      fprintf(strm,"(%3d)PE%3d: (L%2d) %s: ", \
	      NODES,MYNODE,_timer_level,_stepname[ts]); \
      sl = strlen(_stepname[ts]); \
      while (sl++<TSTRLEN) fprintf(strm," "); \
      fprintf(strm," %9.6f\n",_secs[ts]); \
    } \
    for (ts=1 ; ts<step ; ts++) \
      _secs[0] += _secs[ts]; \
    fprintf(strm,"(%3d)PE%3d:   +   %s: ",NODES,MYNODE,msg); \
    sl = strlen(msg); \
    while (sl++<TSTRLEN) fprintf(strm," "); \
    fprintf(strm," %9.6f\n",_secs[0]); \
    fflush(strm); \
  } \
  on_one_thread _timer_level--; \
  node_Barrier(); \
  }

/******************************************************************/

#define INIT_STEP() \
  int step = 0; \
  double secs[TIME_STEPS],tsec[TIME_STEPS]; \
  on_one_node { \
    fprintf(outfile,"PE%3d: \n",MYNODE); \
    fflush(outfile); \
  } {}

#define INIT_STEP_TH() \
  int step = 0; \
  double secs[TIME_STEPS],tsec[TIME_STEPS]; \
  on_one { \
    fprintf(outfile,"PE%3d: \n",MYNODE); \
    fflush(outfile); \
  } {}

#define START_STEP() \
  UMD_Barrier(); \
  secs[step] = get_seconds(); {}

#define START_STEP_TH() \
  all_Barrier(TH); \
  secs[step] = get_seconds(); {}

#define END_STEP(a) \
  secs[step] = get_seconds() - secs[step]; \
  tsec[step] = UMD_Reduce_d(secs[step], MAX); \
  on_one_node { \
    fprintf(outfile,"PE%3d:     %s: %9.6f\n",MYNODE,a,tsec[step]); \
    fflush(outfile); \
  } \
  step++; {}

#define END_STEP1(a,b,c) \
  secs[step] = get_seconds() - secs[step]; \
  tsec[step] = UMD_Reduce_d(secs[step], MAX); \
  on_one_node { \
    fprintf(outfile,"(%3d)PE%3d: n: %12d bo: %3d %s: %9.6f\n",NODES,MYNODE,b*NODES,c,a,tsec[step]); \
    fflush(outfile); \
  } \
  step++; {}

#define END_STEP_TH(a) \
  secs[step] = get_seconds() - secs[step]; \
  tsec[step] = all_Reduce_d(secs[step], MAX, TH); \
  on_one { \
    fprintf(outfile,"(%3d)PE%3d:     %s: %9.6f\n",NODES,MYNODE,a,tsec[step]); \
    fflush(outfile); \
  } \
  step++; {}

#define END_STEP1_TH(a,b,c) \
  secs[step] = get_seconds() - secs[step]; \
  tsec[step] = all_Reduce_d(secs[step], MAX, TH); \
  on_one { \
    fprintf(outfile,"(%3d %3d)PE%3d: n: %12d bo: %3d %s: %9.6f\n",NODES,THREADS,MYNODE,b*NODES,c,a,tsec[step]); \
    fflush(outfile); \
  } \
  step++; {}

#define REPORT_STEP(a) { \
  int ts; \
  on_one_node { \
    for (ts=1 ; ts<step ; ts++) \
      tsec[0] += tsec[ts]; \
    fprintf(outfile,"(%3d)PE%3d:     %s: %9.6f\n",NODES,MYNODE,a,tsec[0]); \
    fflush(outfile); \
  } }

#define REPORT_STEP1(a,b,c) { \
  int ts; \
  on_one_node { \
    for (ts=1 ; ts<step ; ts++) \
      tsec[0] += tsec[ts]; \
    fprintf(outfile,"(%3d)PE%3d: n: %12d bo: %3d %s: %9.6f\n",NODES,MYNODE,b*NODES,c,a,tsec[0]); \
    fflush(outfile); \
  } }

#define REPORT_STEP_TH(a) { \
  int ts; \
  on_one { \
    for (ts=1 ; ts<step ; ts++) \
      tsec[0] += tsec[ts]; \
    fprintf(outfile,"(%3d)PE%3d:     %s: %9.6f\n",NODES,MYNODE,a,tsec[0]); \
    fflush(outfile); \
  } }

#define REPORT_STEP1_TH(a,b,c) { \
  int ts; \
  on_one { \
    for (ts=1 ; ts<step ; ts++) \
      tsec[0] += tsec[ts]; \
    fprintf(outfile,"(%3d %3d)PE%3d: n: %12d bo: %3d %s: %9.6f\n",NODES,THREADS,MYNODE,b*NODES,c,a,tsec[0]); \
    fflush(outfile); \
  } }

#endif

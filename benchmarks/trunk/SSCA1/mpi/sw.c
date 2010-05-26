#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>
#include <math.h>
#include <sys/time.h>
#include <mpi.h>

static int   openGapPenalty = 2;    /* the cost of inserting a gap after a real character */
static int   extendGapPenalty = 1;  /* the cost of inserting a gap after another gap */
static int  *scoringMatrix;         /* entries are the values assigned to matching one character against another */
static char  alphabet[65];          /* the set of characters from which the entries are taken */
static short alphabetSize = 0;      /* the number of characters in the alphabet */
static short alphabetIndex[128];    /* maps the ASCII code for a character to its position in the alphabet */


static void exitGracefully(const char * const message, int returnCode, int checkFor0);

/*********************** MPI STATIC VARIABLES ***********************/

static int processId;      /* identifies me and tells me what part of the longer sequence I need to look at */
static int winningId;	   /* the process id of the process having the best segment */
static int processCount;   /* the total number of processes available */
static const int true = 1;  /* looks general, but we are using these mostly to discriminate between */
static const int false = 0; /* debugging done for process 0 only, versus any process */

/************************* READ INPUT FILES *************************/

static const int BUFFER_SIZE = 2048;
static char     *buffer;  /* shared input buffer for reading the parameter and sequence files */ 

/*  Read a file until the next non-empty line. The parameter fp is a FILE pointer returned  
 *  by a call to fopen.  The file is read into the character array pointed to by buffer,  
 *  and at most "limit" characters are put there.  A pointer to the first non-blank character  
 *  is returned if we succeed, NULL is returned if we reach the end of the file.  
 *
 *  For non-C programmers: fgets normally returns the same pointer (here "buffer") passed  
 *  in: we do not quite do that: we read into the buffer array until we hit a non-blank,  
 *  THEN we return that address as the array's starting point.
 */ 
static char *nextNonblankLine(char *buffer, int limit, FILE *fp) {
   char *line;           /*  the line-start value return from fgets */ 
   char *commentStart; /*  first point in 'line', if any, where '#' or ';' appears */ 
   char *eol;           /*  points at the end of the line */ 
   for (line = fgets(buffer, limit, fp); line != NULL; line = fgets(buffer, limit, fp)) {
      // fprintf(stderr, "read line length %d\n", (int)strlen(line));
      commentStart = strchr(line, '#');  /*  kill from '#' to end of line */ 
      if (commentStart) *commentStart = '\0';
      else {
         commentStart = strchr(line, ';');
         if (commentStart) *commentStart = '\0';
      }
      while(isspace(*line)) ++line;
      eol = line + (strlen(line) - 1);
      while(*line && isspace(*eol)) *eol-- = '\0';
      if( *line ) return line;
   }
   return NULL;
}

/*  Reads starting at the character pointed to by "line" until an integer has been
 *  read and the next character is not a digit.
 */ 
static int readIntParameter(char *line) {
   char *s = line;  /* used as "moving pointer" into the array line[] */
   /*  skip all characters until end-of-line, a digit, or a minus sign is seen: */ 
   while(*s && !(isdigit(*s) || *s=='-')) ++s;
   /*  Non-C programmers: atoi does "ASCII string to integer" conversion and is part */ 
   /*  of the standard library routines provided with the C environments */ 
   if (*s) return atoi(s); 
   else {
      sprintf(buffer, "Missing value in '%s'\n", line);
      exitGracefully(buffer, 13, false);
   }
}

/*  Given a line of text that contains the alphabet, extracts the alphabet.  The
 *  string may begin with some descriptive text.  The alphabet begins at the first  
 *  non-blank character following the first '=' sign. We build the alphabet from  
 *  the alphabetic characters and any of "*?-@" that we find.  Order counts: a  
 *  reverse index called "alphabetIndex" is built that maps the ASCII value of a  
 *  character in our alphabet to its position in that alphabet.  The position is  
 *  used to index the scoring matrix.  
 *
 *  On exit, the file-wide ("static") variables alphabet, alphabetIndex and   
 *  alphabetSize are all initialized.  In case of errors, we kill the process.
 */ 
static void readTheAlphabet(char *line) {
   int i,                  /* standard running loop index */
       charactersKept = 0, /* the size of the alphabet    */
       last = -1;
   char c;
   for(i=0; i<128; ++i) alphabetIndex[i] = -1;
   line = strchr(line, '=') + 1;
   last = strlen(line) - 1;
   for(i=0; i<=last; ++i) {
      c = line[i];
      if (isalpha(c) || c == '*' || c == '?' || c == '-' || c == '@') {  /*  skips whitespace, etc */ 
         if ( alphabetIndex[(int)c] != -1 ) {
            sprintf(buffer, "Duplicate character %c at index %d in alphabet", c, i);
            exitGracefully(buffer, 201, false);
         }
         else {
            alphabetIndex[(int)c] = charactersKept;
            alphabet[charactersKept++] = c;
         }
      }
   }
   alphabet[charactersKept] = '\0';
   alphabetSize = charactersKept; /*  it is cheaper to compute automatic variable in the loop! */ 
}

/*  On entry, 'line' points to the start of the line that begins the scoring matrix in the  
 *  input file pointed to by fp. We assume that that line looks like either:  
 *    name-of-alphabet = ...  
 *  or  
 *     ...  
 *  and that the matrix begins at the ellipsis (...) in both cases.  We skip over all non-numeric  
 *  characters in the matrix's description: we assume that the only interesting data is either  
 *  a decimal integer or a minus sign followed by a decimal integer.  We assume that the alphabet  
 *  has already been read in, so that we know how large the scoring matrix has to be.  
 *
 *  Non-C-programmers:  C does not have doubly-indexed arrays as such, although one can mimic them.  
 *  This is one of several examples we hit here.  We lay out the values of the matrix in a singly  
 *  indexed array "scoringMatrix" in "row-major order".  That is the first row is stored, then the  
 *  second, and so on.
 */ 

static int debugSMX = 0;
static void readTheScoringMatrix(char *line, FILE *fp) {
   int scoresExpected = alphabetSize*alphabetSize;
   int scoresProcessed = 0;
   int nextValue;
   int minus;
   if ( scoresExpected == 0 ) {
      sprintf(buffer, "Alphabet must appear BEFORE scores!\n");
      exitGracefully(buffer, 15, false);
   }
   scoringMatrix = (int *)malloc(alphabetSize * alphabetSize * sizeof(int));
   line  = strchr(line, '=') + 1;  /* C idiom for "skip until after '=', if there is one" */
   if (debugSMX) printf("Scoring matrix:\n");
   do {                           /* the outer loop reads a line at a time from the file */
      while (*line) {            /* ie while we are not at the end of the current line */
         minus = 0;               /* 0 here is used to mean "false": no minus sign seen yet */
         while(*line && !isdigit(*line)) { /* skip non-digits, breaking on line-end */
            if (*line == '-') {  /* break out of the loop if we see '-' */
               minus = 1; 
               ++line; /* the character immediately following the '-' MUST BE a digit */
               break;
            } 
            line++;
         }
         if (*line != '\0')  {        /* if we are not at the end, *line must be a digit */
            nextValue = *line - '0';  /* this C idiom translates ASCII code for a digit to the digit */
            for(++line; isdigit(*line); ++line) { /* the innermost loop reads one signed decimal integer */
               nextValue = 10*nextValue + (*line - '0');
            }
            scoringMatrix[scoresProcessed++] = minus ? -nextValue : nextValue;
            if (debugSMX) {
               printf("[%d] %3d%s", processId, scoringMatrix[scoresProcessed-1], scoresProcessed%alphabetSize == 0 ? ",\n" : ", ");
            }
            if (scoresProcessed == scoresExpected) {  /* not an error to provide too many, only too few! */
               return;
            }
         }
      }
   } while( (line = nextNonblankLine(buffer, BUFFER_SIZE, fp)) != NULL);
   sprintf(buffer, "%d scores processed, %d expected\n", scoresProcessed, scoresExpected);
   exitGracefully(buffer, 14, false);
}

/*  Open the file named by "path" and read the scoring parameters from it.
 *
 * Non C programmers: the 'if (strstr' is a C idiom for "find string in string": it returns
 * the address in the first string of the first occurence of the second string, NULL in case
 * of errors
 */
static void readParameters(char *path) {
   /* fprintf(stderr, "Parameters from '%s'\n", path); */
   FILE *fp = fopen(path, "rb"); /* rb means open for reading as a binary (ie arbitrary bunch of bits) file */
   if (!fp) {
      sprintf(buffer, "Unable to open file '%s' for reading: check the path\n", path);
      exitGracefully(buffer, 5, false);
   }
   char *line;
   while((line = nextNonblankLine(buffer, BUFFER_SIZE, fp)) != NULL) {
      if (strstr(line, "openGap") == line)       openGapPenalty = readIntParameter(line);
      else if(strstr(line, "extendGap") == line) extendGapPenalty = readIntParameter(line);
      else if(strstr(line, "alphabet") == line)  readTheAlphabet(line);
      else if (strstr(line, "scores") == line )  readTheScoringMatrix(line, fp);
   }
   fclose(fp);
}

/*  Open the file named by "path" and read from it a sequence of characters.  Only  
 *  those characters in the alphabet are kept.  The address of the string's start is  
 *  returned.  
 *
 *  Non-C programmers: the function "calloc" allocates storage from a reserve known as  
 *  the "heap" AND ZEROES it.  So you are guaranteed to have a clean slate to work on.  
 *  The drill is: calloc( how_many_things, what_size_each_thing_is).  Don't ask me why  
 *  it is not just calloc(how_much_I_want).  I don't know.
 */ 

static char *readASequence(char *path) {
   FILE *fp = fopen(path, "rb");
   if (!fp) {
      sprintf(buffer, "Unable to open file %s for reading: check the path\n", path);
      exitGracefully(buffer, 5, false);
   }
   fseek(fp, 0, SEEK_END);
   long size = 1 + ftell(fp);
   char *sequence = calloc(size, sizeof(char)); /*  points to buffer holding the final result */ 
   int sn, tn;  /*  indicies into the sequence and line buffers, respectively */ 
   char *line;     /*  a line of raw input from the file: may contain white space */ 
   fseek(fp, 0, SEEK_SET);
   sn = 0;
   while((line = nextNonblankLine(buffer, BUFFER_SIZE, fp)) !=NULL) {
      for (tn=0; line[tn]!='\0'; tn++) {
         /* We allow white space and commas and the like in the line for ease of human reading  */
         /* The next test says: if it is one of ours, keep it!" N.b. we depends on the alphabet */
         /* having already been read in and alphabetIndex computed!                             */
         if (alphabetIndex[(int)(line[tn])]>=0) { 
            sequence[sn++] = line[tn];
         }
      }
   }
   fclose(fp);
   sequence[sn] = '\0';
   return sequence;
}

/************************* DISPLAYING OUTPUT *************************/

static const int debugSW = 0;     /*  if true, prints out the scoring */
static const int debugSW_ALL = 0; /*  if true, prints out verbose scoring */
static const int debugTB = 0;     /*  if true, prints out the steps in the traceback. */

typedef struct Timings {
   const char * const *phaseDescriptions; /* short descriptions of what each phase accomplishes */
   int iterations; /* the number of timings for each phase */
   int phases;	   /* the number of time intervals into which the process has been divided */
   int *data;	   /* for each iteration and phase, the time in milliseconds it required */
   int *maxs;	   /* for each phase, the largest of the times it required */
   int *means;     /* for each phase, the average time that it required */
   int *medians;   /* for each phase, the median of the times it required */
   int *mins;	   /* for each phase, the smallest of the times it required */
   int *sigmas;    /* for each phase, the std deviation of the times it required */
   int totalTime;  /* the end to end time for the whole set of iterations */
   int statsAreAvailable;  /* if true, the current values of the stats reflect the current timings data */
} Timings;

/**
 * There are two instances of Output: one is static and is used to accumulate timing
 * information, and the other is created per iteration.  The problem we are solving is
 * that we cannot get input data big enough to test realistically the effects of
 * parallelization, so we take one set of data and process it as many times as we need
 * to get meaningful numbers for the various parts of the processing time.
 */

typedef struct Output {
   char *shorterPath;      /* locates the file for the shorter sequence */
   char *longerPath;       /* locates the file for the longer sequence */
   char *outputPath;       /* locates the file to which to write the output */
   char *fromShorter;      /* a pointer into shortAlignedBuffer to the short sequence */
   char *fromLonger;       /* a pointer into shortAlignedBuffer to the long sequence */
   long  commonLength;     /* of the two matched sequences, gaps included */
   long  shortSliceStart;  /* index into the short sequence for this process's part's start */
   long  shortSliceEnd;    /* index into the short sequence for this process's part's end */
   long  longSliceStart;   /* index into the long sequence for this process's part's start */
   long  longSliceEnd;     /* index into the long sequence for this process's part's end */
   int	 longOffset;       /* where in the whole long sequence this process's data begins */
   int   processesUsed;   /* as computed by scoreFromPaths: bounded by max available and segment count  */
   int	 iterations;	   /* the number of times we have been here. */
   int   phases;	       /* the number of phases we already have filled in this iteration */
   Timings timings;	   	   /* shows the elapsed time for the three phases for each iteration, and the whole show */
} Output;
static void saveResults(Output *output, const char * const shorter, const char * const longer) {
   if (output->fromShorter != NULL) return;
   int length = output->commonLength;
   output->fromShorter = (char *)malloc(length + 1);
   strncpy(output->fromShorter, shorter, length);
   output->fromShorter[length] = '\0';
   output->fromLonger  = (char *)malloc(length + 1);
   strncpy(output->fromLonger, longer, length);
   output->fromLonger[length] = '\0';
}

/**
 * initializes the timings from the number of iterations and the array of descriptive
 * info about each of the phases.
 */
void startTimings(Output *output, int iterations, int phases, const char * const *phaseDescriptions) {
   output->iterations = output->phases = 0;
   output->fromShorter = NULL;
   output->fromLonger = NULL;
   Timings *t = &(output->timings);
   t->phaseDescriptions = phaseDescriptions;
   t->phases = phases;
   t->iterations = iterations;
   t->maxs = (int *)calloc(phases, sizeof(int));
   t->mins = (int *)calloc(phases, sizeof(int));
   t->medians = (int *)calloc(phases, sizeof(int));
   t->means = (int *)calloc(phases, sizeof(int));
   t->sigmas = (int *)calloc(phases, sizeof(int));
   /*fprintf(stderr, "Timing "+iterations_+" iterations, each "+phases+" phases");*/
   t->data =  (int *)calloc(iterations*phases, sizeof(int));
   t->statsAreAvailable = 0;
   t->totalTime = 0;
}


/** records the time required for the given phase during the given iteration
 * @param phaseNumber which phase is at stake, as an index into phaseDescriptions
 * @param iteration as the name suggests
 * @param t the time required to complete the phase.
 */
void addPhaseTime(Timings *t, int phaseNumber, int iteration, int time) {
   /*fprintf(stderr, "Phase %d, Iteration %d, Time %ld\r\n", phaseNumber, iteration, time); */
   t->data[phaseNumber+ t->phases*iteration] = (int)time;
   t->statsAreAvailable = 0;
}

/* copies the elapsed time since "starts" to the next slot in the output's timingLines
 * array.  The string "phase" names the part of the computation.
 */
double writeElapsedTime(Output *output, double starts) {
   double finishes = MPI_Wtime();
   addPhaseTime(&(output->timings), output->phases++, output->iterations, (int)((1000.0*(finishes - starts))));
   return finishes;
}


/**
 * records the total time required by the whole set of iterations
 */
void addTotalTime(Timings *t, int totalTime) {
   t->totalTime = (int)totalTime;
}

/**
 * if the stats have not yet been computed, or if timings have been updated since
 * the last attempt, we compute the maxs and so on.
 * @throws IllegalArgumentException if the timings data is incomplete
 */
void computeStats(Timings *t) {
   if (t->statsAreAvailable) return;

   int hasOddMean = (t->iterations%2 == 1);
   int mid = t->iterations/2;
   int i, pn;
   for(pn = 0; pn<t->phases; pn++) {
      int* sorted = (int *)malloc(t->iterations*sizeof(int));
      long total = 0;
      double delta, meanPN, sumOfSquares;
      for(i=0;  i<t->iterations; i++) {
     	int offset = pn+i*t->phases;
         int j, k;
         total += t->data[offset];
         for(j=0; j<i && t->data[offset]<=sorted[j]; j++) { }
         for(k = i-1; k >= j; k--) sorted[k+1] = sorted[k];
         sorted[j] = t->data[offset];
      }
      t->mins[pn] = sorted[0];
      t->maxs[pn] = sorted[t->iterations - 1];
      t->medians[pn] = (hasOddMean ? sorted[mid] : (sorted[mid-1]+sorted[mid])/2) ;
      meanPN = (double)total/(double)t->iterations;
      t->means[pn] = meanPN;
      sumOfSquares = 0.0;
      for(i=0; i<t->iterations; i++) {
         delta = (double)(t->data[pn+i*t->phases]) - meanPN;
         sumOfSquares += delta*delta;
      }
      t->sigmas[pn] = (int)sqrt(sumOfSquares/(double)t->iterations);
   }
   t->statsAreAvailable = 1;
}

/** as simple way to right justify some of the ints */
static void catPaddedInt(char* buffer, int n) {
	  char answer[11];
	  sprintf(answer, "%10d", n);
   strcat(buffer, answer);
}

/**
 * computes a (reasonably) pretty printed portion of that part of this object
 * that deals with the computed statistics
 * @returns a string suitable for direct display as plain text
 */
static char *statsToString(Timings *t) {
   computeStats(t);
   if (t->iterations == 1) (char *)calloc(1, sizeof(char)); /* in case some twit tries to free this */
   else {
 	 char *answer = calloc(500+ 8*128*t->iterations, sizeof(char));
      int leaderSize = 0;
      int n, length, delta, descSize;
      int cursor;
      for(n = 0; n<t->phases; n++) {
         length = strlen(t->phaseDescriptions[n]);
         if (length>leaderSize) leaderSize = length;
      }
      delta = leaderSize%4 != 0 ? 4-leaderSize%4 : 0;
      for(n=0; n<leaderSize+delta; n++) answer[n] = ' ';
      strcat(answer, "     Min      Mean      Median     Max     Sigma\r\n");
      for(n= 0;  n<t->phases; n++) {
         strcat(answer, t->phaseDescriptions[n]);
         cursor = strlen(answer);
         descSize = strlen(t->phaseDescriptions[n]);
         while(descSize++<leaderSize) answer[cursor++] = ' ';
         catPaddedInt(answer, t->maxs[n]);
         catPaddedInt(answer, t->means[n]);
         catPaddedInt(answer, t->medians[n]);
         catPaddedInt(answer, t->mins[n]);
         catPaddedInt(answer, floor(t->sigmas[n]));
         strcat(answer, "\r\n");
      }
      return answer;
   }
}


char *timingsToString(Timings *t, int totalTime){
   char *answer;
   int cursor, i, leaderSize, length, n, size_n;
   if (t->iterations > 1) {
 	 char *stats = statsToString(t);
 	 answer = calloc(40000+strlen(stats), sizeof(char));
 	 sprintf(answer, "There were %d iterations, which took %d milliseconds in all.\r\n\r\n", t->iterations, totalTime);
 	 strcat(answer, stats);
 	 //free(stats);
 	 strcat(answer, "\r\nIndividual timings:\r\n\r\n");
   }
   else {
 	 answer = calloc(40000, sizeof(char));
   }
   leaderSize = 0;
   for(n = 0;  n<t->phases; n++) {
      length = strlen(t->phaseDescriptions[n]);
      if (length>leaderSize) leaderSize = length;
   }
   leaderSize += leaderSize%4 != 0 ? 4-leaderSize%4 : 0;
   for(i=0; i<t->iterations; i+=4) {
      for(n = 0; n<t->phases; n++) {
         strcat(answer, t->phaseDescriptions[n]);
         size_n = strlen(t->phaseDescriptions[n]);
         cursor = strlen(answer);
         while(size_n++<leaderSize) answer[cursor++] = ' ';
         catPaddedInt(answer, (t->data[n+i*t->phases]));
         if(i+1<t->iterations) catPaddedInt(answer, (t->data[n+ t->phases*(i+1)]));
         if(i+2<t->iterations) catPaddedInt(answer, (t->data[n+ t->phases*(i+2)]));
         if(i+3<t->iterations) catPaddedInt(answer, (t->data[n+ t->phases*(i+3)]));
         strcat(answer, "\r\n");
      }
      strcat(answer, "\r\n");
   }
   return answer;
}

/* writes a neatly formatted version of "output" to the file named by outputPath.
 * If outputPath is null, stdout, the console stream, is the target.
 */
void writeOutput(Output *output, int totalTime) {
	char *outputPath = output->outputPath;
	FILE *fp;
	if (outputPath) {
	   if (outputPath[0] == '+') fp = fopen(outputPath+1, "ab");
	   else fp = fopen(outputPath, "wb");
	}
	else fp = stdout;
    fprintf(fp, "Shorter Path = %s\n",   output->shorterPath);
    fprintf(fp, "Longer  Path = %s\n\n", output->longerPath);
    fprintf(fp, "Shorter      = %s\n",   output->fromShorter);
    fprintf(fp, "Longer       = %s\n\n", output->fromLonger);
    fprintf(fp, "Length       = %ld\n",  output->commonLength);
    fprintf(fp, "From short   = [%2ld, %2ld]\n", output->shortSliceStart, output->shortSliceEnd);
    fprintf(fp, "From long    = [%2ld, %2ld]\n\n", output->longSliceStart, output->longSliceEnd);
    fprintf(fp, timingsToString(&output->timings, totalTime));
}

/**
 * If we have a non-recoverable error, we want to abort ALL processes, but just send one
 * message to stderr.
 * PARAMETERS:  message: what you want sent to stderr, returnCode: what the calling shell
 * 		will see as the exit status (ie "$?"), and checkFor0: if non-zero, only process
 * 		the request if the calling process has id == 0.
 */
static void exitGracefully(const char * const message, int returnCode, int checkFor0) {
    if (checkFor0) {
       if (processId == 0) {
          fprintf(stderr, message);
          MPI_Abort(MPI_COMM_WORLD, returnCode);
       }
    }
    else {
       fprintf(stderr, "[%d] ", processId);
       fprintf(stderr, message);
       MPI_Abort(MPI_COMM_WORLD, returnCode);
    }
}

/**
 * if an MPI call fails, we send an error message to stderr.  mpiFunction is the name of the entry
 * that died, errorCode is what it returned.
 */
int writeMPIFailure(char *mpiFunction, int errorCode) {
   fprintf(stderr, "[%d] Call to MPI_%s failed with error code %d", processId, mpiFunction, errorCode);
   return 101;
}
/*
static const char const *directionNames[5] = {
   "BAD", "STOP", "LEFT", "DIAGONAL", "UP"
};
*/

/************************** SCORE THE PAIR **************************/

enum  { /*  which way to move while tracing back */ 
   BAD = 0, STOP = 1, LEFT = 2, DIAGONAL = 3, UP = 4
};

/* Read the score from the scoring matrix: no protection against bad input a or b!
 * Non C programmmers: as noted above, this function just serves to hide the lack of
 * doubly indexed arrays in C: it translates a pair of indices into a single one .
 */
static int score(char a, char b) {
   return scoringMatrix[alphabetIndex[(int)a]*alphabetSize + alphabetIndex[(int)b]];
}

/* Return the largest of 3 integers or 0 if the largest is negative */
static int debugMZ = 0;
static int maxOrZero(int a, int b, int c) {
   if (debugMZ) {
      printf("max or zero scoreUsingLatestIJ %d, bestIfGapInsertedInI %d, bestIfGapInsertedInJ %d\n", a, b, c);
   }
   if (a > b) {
      if (a > c) return a > 0 ? a : 0;
      else        return c > 0 ? c : 0;
   }
   else if (b > c) return b > 0 ? b : 0;
   else            return c > 0 ? c : 0;
}

static char *tracebackMoves;         /* a "doubly-indexed array" that tells us how we got to the best score */
static int tracebackMatrixSize = 0;  /* eventually set to the total storage needed for tracebackMoves     */
static int winningShortSequenceEnd = -1; /* also: where in the short sequence we begin the traceback      */
static int winningLongSequenceEnd =  -1; /* als: where in the long sequence we begin the traceback        */

/* 
 * Convert matrix coordinates (i,j) to an index into the tracebackMoves array and return the
 * corresponding entry
 */
static char tracebackMove(int i,int j, int longLength) {
   return tracebackMoves[i*longLength + j];
}
/*
 * return the appropriate penalty depending on whether the move traced back came
 * from the start of a gap or the continuation of an existing gap.
 */
static int penalty(int i, int j, int longLength, int direction) {
	switch(tracebackMove(i,j,longLength)) {
		case UP: return extendGapPenalty ;
		case LEFT: return extendGapPenalty;
		default: return openGapPenalty;
	}
}
/*
 * Assign the value 'value' to the traceback move that should be executed from position i in the short
 * sequence, j in the long sequence.  "longLength" is the length of the long sequence
 */
static char setTracebackMove(int i, int j, int longLength, char value) {
/*   if (i*longLength + j >= tracebackMatrixSize) {
      printf("STM: i=%d, j=%d, ll=%d, product=%ld\n", i, j, longLength, (long)(i*longLength + j));
      exit(223);
   }
*/   return tracebackMoves[i*longLength + j] = value;
}

/* 
 * Save the positions in the short and long sequences that mark the ends of the winning sequences (and
 * hence the starting points for the traceback)
 */
static void setTracebackStartingPoint(int i,int j) {
   winningShortSequenceEnd = i;  
   winningLongSequenceEnd = j; 
}


/**
 * Smith-Waterman algorithm in the simplest serial implementation: the outer loop reads left-to-right
 * over one of the sequences--by convention, we assume it is the shorter, although we do not make any
 * use of that in this particular algorithm.  The inner loop is over the second sequence.
 * 
 * The indexing in this function may be a little confusing on first reading.  In reading the inner
 * loop below to keep in mind that for a given i and j, we are looking at the effect of the i-1-st
 * element in the short sequence and the j-1-st element in the long.  The reason for this--what might
 * appear to be an "off by one" error--is that it makes tracing back from the ends much cleaner if the
 * traceback matrix has a "landing spot" at 0,j and i,0 where we can place a "STOP" sentinel.
 * 
 * As for the loop bounds: we go from 1 to length-1 (inclusive) in each case.  Again, this might
 * seem wrong, but remember the lengths in question are the buffer sizes for the short and long
 * sequences, which are 1 longer than the actual sequence lengths, because of the C convention for
 * terminating strings with a 0 byte.  ALl the indexing is shifted "up 1" from what one might expect.
 */
int computeTheBestScores(char shortSequence[], int shortLength, char longSequence[], int longLength) {
   int previousBestScore;     /*  in the inner loop: the best score at (i-1,j-1) */ 
   int *bestScoreUpTo_I_J;    /*  best overall score: see note in the inner loop below */ 
   int scoreUsingLatestIJ;    /*  the best score at I,J based on a match at I-1,J-1 and the previous best score */ 
   int bestIfGapInsertedInI,  /*  best score at I,J if we need to insert a gap at I */ 
      bestIfGapInsertedInJ;   /*     ... or J */ 
   int i, j;                  /*  indices into short and long sequences */ 
   int winner;                /*  best score at I,J */ 
   int tracebackScore = 0;
   double starts = MPI_Wtime();
   tracebackMatrixSize = shortLength*longLength*sizeof(char);
   tracebackMoves = (char *)malloc(tracebackMatrixSize);
   for (i = 0; i < shortLength; i++) setTracebackMove(i, 0, longLength, STOP);
   for (j = 1; j < longLength;  j++) setTracebackMove(0, j, longLength, STOP);
   winningShortSequenceEnd = -1;
   winningLongSequenceEnd =  -1;
   bestScoreUpTo_I_J = calloc(longLength, sizeof(int)); /*  allocates and ZEROES the storage */ 
   for (i = 1; i < shortLength; i++) {
	  previousBestScore = 0;
      for (j = 1; j < longLength; j++) {
         scoreUsingLatestIJ = previousBestScore + score(shortSequence[i - 1], longSequence[j - 1]);
         bestIfGapInsertedInI = bestScoreUpTo_I_J[j]   - penalty(i-1,j,longLength,UP);
         bestIfGapInsertedInJ = bestScoreUpTo_I_J[j-1] - penalty(i,j-1,longLength,LEFT);
         previousBestScore = bestScoreUpTo_I_J[j];  /* save for the next time around the loop. */
         winner = bestScoreUpTo_I_J[j] = maxOrZero(scoreUsingLatestIJ, bestIfGapInsertedInI, bestIfGapInsertedInJ);
         if (winner == 0)                          setTracebackMove(i, j, longLength, (char)STOP);
         else if (winner == scoreUsingLatestIJ)    setTracebackMove(i, j, longLength, (char)DIAGONAL);
         else if (winner == bestIfGapInsertedInI)  setTracebackMove(i, j, longLength, (char)UP);
         else                                      setTracebackMove(i, j, longLength, (char)LEFT);
         if (winner > tracebackScore) { /*  we should start the traceback at this i,j */
            tracebackScore = winner;
            setTracebackStartingPoint(i,j); 
         }
      }
   }
   free(bestScoreUpTo_I_J);
   /* fprintf(stderr, "%d took %9.0f ms\r\n", processId, (MPI_Wtime()-starts)*1000.0); */
   return tracebackScore;
}

/********************** TRACEBACK THE SOLUTION **********************/

/**
 * In order to size the arrays that hold the two final matched strings, the key number is
 * the size of the longest string, counting gaps, that can come out of the algorithm.  The
 * highest score we can get is when the short sequence occurs as a subsequence of the
 * long sequence.  The only way to get a longer match is to insert gaps, each of which
 * incurs a penalty, whence the bound: a winning match cannot have a total score below
 * that for matching the first entry.  It is possible to get a sharper bound by 
 * strictly enforcing the rule that "shortest with highest score wins"
 */
static int computeMaxAlignedLength(char *shorter, int shorterLength, int longerLength) {
   int i, maxScoreOnMatch = 0;
   for(i=0; i<shorterLength-1; i++) maxScoreOnMatch += score(shorter[i], shorter[i]);
   int gapBasedBound = (openGapPenalty <= extendGapPenalty) ?
               shorterLength - 1 + maxScoreOnMatch/openGapPenalty :            /* gap between each    */
               shorterLength + (maxScoreOnMatch-openGapPenalty)/extendGapPenalty;   /* one BIG gap         */
   int naiveBound = shorterLength + longerLength - 4; /* matched first and last, all others against a gap! */
   return gapBasedBound > naiveBound ? naiveBound : gapBasedBound;
}

/**
 * Follow the directions in the tracebackMove array to work back from the traceback's
 * starting point (remember: we are tracing back from the ends of the winning sequences)
 * to reconstruct the winning alignment.  The code computes the sequences from back to
 * front, inserting a "gap" character in each as needed to keep all the matched entries
 * aligned.  When the sequence starting points are finally arrived at, we reverse the
 * buffered aligned sequences. 
 */
void doTheTraceback(char shortSequence[], int shortLength, char longSequence[], int longLength, Output *output) {
   char nextMove;
   int i = winningShortSequenceEnd; /* i now marks the end of the winning subsequence of the shorter sequence */
   int j = winningLongSequenceEnd;  /* j marks the end of the winning subsequence of the longer sequence */
   int bufferSizeNeeded = 1+computeMaxAlignedLength(shortSequence, shortLength, longLength);
   char *shortAlignedBuffer = malloc(bufferSizeNeeded*sizeof(char)); /* buffer to hold winner with gaps */
   char *longAlignedBuffer  = malloc(bufferSizeNeeded*sizeof(char)); /*         "                       */
   char *shortAligned = shortAlignedBuffer + (bufferSizeNeeded-1);
   char *longAligned  = longAlignedBuffer  + (bufferSizeNeeded-1);
   char *shortEnd     = shortAligned;
   const char GAP = '-';
   *shortAligned-- = *longAligned-- = '\0';
   do { /*  until we are at the tracebackMove matrix boundary marked by the STOP sentinels */
      nextMove = tracebackMove(i, j, longLength);
      switch (nextMove) {
      case UP:       /* UP means we got here by HOLDING J THE SAME and adding one to I */
         *shortAligned-- = shortSequence[--i];
         *longAligned--  = GAP;
         break;
      case DIAGONAL: /* DIAGONAL means we got here by matching two elements */
         *shortAligned-- = shortSequence[--i];
         *longAligned--  = longSequence[--j];
         break;
      case LEFT:     /* LEFT means we got here by holding I the same and adding one to J */
    	 *shortAligned-- = GAP;
         *longAligned--  = longSequence[--j];
         break;
      case STOP:    /* If we move back further the score goes non-positive */
         output->shortSliceStart = i+1;
         output->shortSliceEnd   = winningShortSequenceEnd;
         output->longSliceStart  = j+1;
         output->longSliceEnd    = winningLongSequenceEnd;
         output->commonLength	 = shortEnd - shortAligned;
         saveResults(output, ++shortAligned, ++longAligned);
         free(tracebackMoves);
         free(shortAlignedBuffer);
         free(longAlignedBuffer);
        return;
      case BAD: exit(21);
      }
    } while(1);
}

/************************* MAIN ENTRY POINT *************************/
/**
 * The first steps are to read the files for the two sequences and the scoring
 * parameters.  We then check how much parallelism we can eke out by segmenting
 * the longer sequence.  This sets up a call to computeTheBestScores, which
 * finds the best match in each of the segments.  MPIAllreduce allows us to
 * aggregate this information and find the best match over all segments.
 * Finally, doTheTraceback is called by the winning process to construct the
 * matched output strings and report our results to the waiting world.
 */
int scoreFromPaths(Output *out) {
   char  *shortSequence = NULL,			 /* buffers the whole shorter sequence */
         *longSequence  = NULL;          /* buffers the whole longer sequence */
   int    shortLength=0, longLength=0;   /* space required for each of those buffers */
   double start, dataRead, scoringDone;  /* timestamps to compute elapsed time */
   start = MPI_Wtime();
   shortSequence = readASequence(out->shorterPath);
   shortLength = 1+strlen(shortSequence);  /* this is the WHOLE BUFFER SIZE: add 1 for the sentinel 0 byte */
   longSequence  = readASequence(out->longerPath);
   longLength = 1+strlen(longSequence);   /* this is the WHOLE BUFFER SIZE: add 1 for the sentinel 0 byte */
   if (debugSMX) fprintf(stderr, "[%d] Alphabet: \"%s\"\n", processId, alphabet);
   if (debugSW) {
      fprintf(stderr, "[%d] First sequence:  \"%s\"\n", processId, shortSequence);
      fprintf(stderr, "[%d] Second sequence: \"%s\"\n", processId, longSequence);
   }
   dataRead = writeElapsedTime(out, start);
   
   int maxAlignedLength = computeMaxAlignedLength(shortSequence, shortLength, longLength);
   int maxFromLong = maxAlignedLength > longLength ? longLength : maxAlignedLength;
   int overlap = maxFromLong - 1;
   int mostWeCanUse = (2*overlap >= longLength) ? 1 : (longLength-overlap)/overlap;
   int segmentCount = mostWeCanUse>processCount ? processCount : mostWeCanUse;
   int segmentLength = overlap + (longLength - overlap)/segmentCount;
   int shortfall  = (longLength - overlap) % segmentCount;
   int segmentOffset = (segmentLength <= overlap) ? 0 : processId*(segmentLength - overlap);  /* where to start if shortfall == 0 */

   int tracebackScore = 0; /* actually set in the "if" that follows */
   if (processId < segmentCount) { /* for larger id's, there is no work to do */
      out->processesUsed = segmentCount;
      if (processId < shortfall) { /* make the length 1 longer and adjust offset */
         segmentOffset += processId;
         segmentLength += 1;
      }
      else { /* no size adjustment, but the offset takes into account the previous additions: */
         segmentOffset += shortfall;
      }
      tracebackScore = computeTheBestScores(shortSequence, shortLength, longSequence+segmentOffset, segmentLength);
   }
   MPI_Barrier(MPI_COMM_WORLD);
   scoringDone = writeElapsedTime(out, dataRead);
   MPI_Barrier(MPI_COMM_WORLD);
   int maxScore;
   int errorCode = MPI_Allreduce(&tracebackScore, &maxScore, 1,  MPI_INT , MPI_MAX, MPI_COMM_WORLD);
   if (errorCode == MPI_SUCCESS) { /* did this process have the winning score? Let us see: */
      if (debugSW) {
         fprintf(stderr, "[%d] saw max score %d and has score %d\n", processId, maxScore, tracebackScore);
      }
      int postedId = (tracebackScore == maxScore)? processId : processCount+1;
      errorCode = MPI_Allreduce(&postedId, &winningId, 1, MPI_INT, MPI_MIN, MPI_COMM_WORLD);
      if (errorCode == MPI_SUCCESS) { 
          if (debugSW) {
            fprintf(stderr, "[%d] saw winning id %d\n", processId, winningId);
          }
          if (winningId == processId) { /* Yes, this is the smallest processId with the winning score: it alone traces back */
             doTheTraceback(shortSequence, shortLength, longSequence+segmentOffset, segmentLength, out);
          }
      }
      else return writeMPIFailure("Allreduce", errorCode);
   }
   else return writeMPIFailure("Allreduce", errorCode);
   
   free(shortSequence);
   free(longSequence);
   return 0;
}

extern void autobind (int ntasks, int mytask);

/**
 * The command line has options
 *    -s path    path for the file containing the shorter of the two sequences
 *    -l path    path for the file containing the longer of the two sequences
 *    -p path    path for the scoring parameters file
 *    -o path	 optional: the path for the output file, with stdout being the default
 *    -r reps	 optional: the number of repetitions, the default being 100
 * We adhere to the Unix convention for exit codes: 0 is "normal", non-zero identifies
 * an error.
 */
int main(int argc, char **argv) {
   const char * const usage = "sw: usage is \r\n\tsw -s shortPath -l longPath -p paramPath [-o outputPath] [-r reps] \r\n\r\n";
   const char * const phases[] = {"Input phase:", "Scoring phase:", "Traceback phase:"};
   int rc = 0,		      /* return code: what the calling shell sees on exit */
	   n,			      /* just another pretty loop variable: here an index into argv */
	   repetitions,
	   repetitionCount;	  /* command line option: how many times to process the data */
   double begin, totalTime;
   Output output;
   MPI_Init(&argc,&argv);
   MPI_Comm_rank(MPI_COMM_WORLD, &processId);
   MPI_Comm_size(MPI_COMM_WORLD, &processCount);
   autobind(processCount, processId);
   output.shorterPath = output.longerPath = output.outputPath = NULL;
   repetitionCount = 1;
   begin = MPI_Wtime();
   buffer = malloc(BUFFER_SIZE*sizeof(char));
   for(n=1; n<argc-1; n+=2) {  /*   pull the sequence number from the command line */
      char *value = argv[n+1];
      switch(argv[n][1]) {
         case 's': output.shorterPath = value; break;
         case 'l': output.longerPath  = value; break;
         case 'o': output.outputPath  = value; break;
         case 'p': readParameters(value);      break;
         case 'r': repetitionCount = atoi(value); break;
         default: fprintf(stderr, usage); return 10;
      }
   }
   if (output.shorterPath == NULL) {
      if (processId == 0) fprintf(stderr, "No path for shorter sequence supplied\n");
      rc += 1;
   }
   if (output.longerPath == NULL)  {
      if (processId == 0) fprintf(stderr, "No path for longer sequence supplied\n");
      rc += 2;
   }
   if (alphabetSize == 0)  {
      if (processId == 0)  fprintf(stderr, "No path for scoring parameters supplied\n");
      rc += 4;
   }
   if (rc != 0 && processId == 0) exitGracefully(usage, 20+rc, 0);
   startTimings(&output, repetitionCount, 3, phases);
   for(repetitions = 1; rc == 0 && repetitions <= repetitionCount; repetitions++) {
      MPI_Barrier(MPI_COMM_WORLD);
      output.iterations = repetitions-1; output.phases = 0;
	  rc = scoreFromPaths(&output);
	  if (rc != 0) {
		 char message[64];
		 sprintf(message, "Died on repetition %d\r\n", (repetitionCount-repetitions + 1));
		 exitGracefully(message, rc, 0);
	  }
	  else MPI_Barrier(MPI_COMM_WORLD);
   }
   free(buffer);
   totalTime = 1000*(MPI_Wtime() - begin);
   if (processId == winningId) writeOutput(&output, (int)totalTime);
   MPI_Finalize();
   return rc;
}

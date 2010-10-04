
class betweenessCentrality implements ((graph) => Rail(DOUBLE_T)) {

   def apply (graph: G): Rail(DOUTLE_T)) {
     foreach((tid): (0..nthreds-1)) {
       val numV = 1 << K4Approx;
    
        val myCount = 0;
        val MAX_NUM_PHASES = 100;
        val permV: Rail(LONG_T);

        if (tid == 0) {
          permV = Rail.make(LONG_T)(n);
          chunkSize = n/nthreads;
        }
  
    /* #pragma omp barrier */

    foreach ((i): (tid*chunksize..(tid+1)*chunkSize-1)) {
      permV(i)= i;
    }

    if (tid == 0) {
        srand48(SCALE*387543);
        for((i):(0..n-1)) {
            j = lrand48() % n;
            k = permV(i);
            permV(i) = permV(j);
            permV(j) = k;
    }     
   }

    /* #pragma omp barrier */


   if (tid==0)  time =omp_get_wtime();

    /* #pragma omp barrier */

   if (tid == 0) {
     S = Rail(VERT_T)(n);
     P = Rail(dynArray)(n);
     sig = Rail(LONG_T)(n);
     d = Rail(LONG_T)(n);
     del =Rail(DOUBLE_T)(n);
     start=Rail(LONG_T)(MAX_NUM_PHASES);
     end=Rail(LONG_T)(MAX_NUM_PHASES);
     psCount = Rail(LONG_T)(nthreds+1);
   }

    myS = Rail(LONG_T)(n);
    myCount = 0;

    for((i):(0..n-1)) {
      intiDynArray(P(i));
      d(i) = -1;
    }  

    /* #pragma omp barrier */

    for((p): (0..numV-1)) {
      i = permV(p);
      if (tid==0) {
        sig(i) = 1;
        d(i) = 0;
        S(0) = i;
        start(0) = 0;
        end(0) = 1;
      }
    }

        count = 1;
        numPhases = 0;

        /* #pragma omp barrier */

        while (end(numPhases) - start(numPhases) > 0) {

            myCount = 0;

            /* #pragma omp barrier */

            for ((vert): (start(numPhases)..end(numPhases)-1)) {
              async {
                v = S(vert);
                for ((j):(G.numEdges(v)..G.numEdges(v+1)-1)) {

                    /* Filter edges with weights divisible by 8 */
                    if ((G.weight(j) & 7) != 0) {
                        w = G.endV(j);
                        if (v != w) {
                            atomic {
                                /* w found for the first time? */
                                if (d(w) == -1) {
                                    myS(myCount++) = w;
                                    d(w) = d(v) + 1;
                                    sig(w) = 0;
                                    sig(w) += sig(v);
                                    dynArrayInsert(P(w), v);
                                } else if (d(w) == d(v) + 1) {
                                   sig(w) += sig(v);
                                    dynArrayInsert(P(w), v);
                                }
                              }
                            } else {
                                if ((d(w) == -1) || (d(w) == d(v)+ 1)) {
                                   atomic {
                                     sig(w) += sig(v);
                                     dynArrayInsert(P(w), v);
                                   }
                                }
                            }
                        }
                    }
                }
            }

            /* Merge all local stacks for next iteration */
            numPhases++;

            psCount(tid+1) = myCount;

            /* #pragma omp barrier */

            if (tid == 0) {
                start(numPhases) = end(numPhases-1);
                psCount(0) = start(numPhases);
                for(k=1; k<=nthreads; k++) {
                    psCount(k) = psCount(k-1) + psCount(k);
                }
                end(numPhases) = psCount(nthreads);
            }

             /* #pragma omp barrier */

            for (k = psCount(tid); k < psCount(tid+1); k++) {
                S(k) = myS(k-psCount(tid));
            }

            /* #pragma omp barrier */
            count = end(numPhases);
        }

        numPhases--;

        /* #pragma omp barrier */

          while (numPhases > 0) {
            for (j=start(numPhases); j<end(numPhases); j++) {
              async {
                w = S(j);
                for (k = 0; k<P(w).count; k++) {
                    v = P(w).vals(k);
                    atomic {
                      del(v) = del(v) + sig(v)*(1+del(w))/sig(w);
                    }
                }
                BC(w) += del(w);
              }
            }

            /* #pragma omp barrier */

            numPhases--;

            /* #pragma omp barrier */

        }

        if (tid == 0) {
            chunkSize = count/nthreads;
        }

        /* #pragma omp barrier*/
        for ((j):[tid*chunkSize..(tid+1)*chunkSize-1]) {
            w = S[j];
            d[w] = -1;
            del[w] = 0;
            P[w].count = 0;
        }

        /* #pragma omp barrier */

    }

   if (tid == 0) {
        time = omp_get_wtime() - time;
    }
}

 /* Verification */
    sum = 0;
    n = G.n;
    numV = 1<<K4Approx;
    for ((i): [0..n-1]) {
        sum += BC(i);
        // fprintf(stderr, "%lf ", BC[i]);
    }
    x10.io.Console.OUT.println("BC sum: " + sum);
}


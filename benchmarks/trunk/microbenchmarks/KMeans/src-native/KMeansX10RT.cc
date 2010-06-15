#include "KMeansX10RT.h"

static int* closestCluster;
static float* cluster;
static float* points;


static void doComputation(int myK, int numPoints, int numDimensions) {
    float MAX_FLOAT=1000000.0;
    
    for (int pointNumber = 0; pointNumber<numPoints; pointNumber++) {
        int closest = -1;
        float closestDist = MAX_FLOAT;
        for (int k=0; k<myK; k++) {
            float dist = 0;
            for (int dim=0; dim<numDimensions; dim++) {
                float tmp = cluster[k*numDimensions + dim] - points[pointNumber*numDimensions + dim];
                dist += tmp*tmp;
            }
            if (dist < closestDist) {
                closestDist = dist;
                closest = k;
            }
        }
        closestCluster[pointNumber] = closest;
    }
}


/*
 * Class:     KMeansX10RT
 * Method:    nativeInit
 * Signature: (III[F)V
 */
void JNICALL Java_KMeansX10RT_nativeInit(JNIEnv *env, jclass klazz,
                                         jint myK, jint numPoints, jint numDimensions,
                                         jfloatArray jpoints) {
    closestCluster = new int[numPoints];
    cluster = new float[myK*numDimensions];
    points = new float[numPoints*numDimensions];

    env->GetFloatArrayRegion(jpoints, 0, numPoints*numDimensions, points);
}


/*
 * Class:     KMeansX10RT
 * Method:    computeClosestClusterNative
 * Signature: (III[F[F[I)V
 */
void JNICALL Java_KMeansX10RT_computeClosestClusterNative(JNIEnv *env, jclass klazz,
                                                          jint myK, jint numPoints, jint numDimensions,
                                                          jfloatArray jpoints, jfloatArray jcluster,
                                                          jintArray jclosestCluster) {
    env->GetFloatArrayRegion(jcluster, 0, myK*numDimensions, cluster);

    doComputation(myK, numPoints, numDimensions);

    env->SetIntArrayRegion(jclosestCluster, 0, numPoints, closestCluster);
}


#include <cstdlib>
#include <cstdio>
#include <cstring>
#include <cmath>
#include <climits>
#include <cfloat>

#include <ctime>

#include <algorithm>

int round_up (int x, int n) { return (x-1) - ((x-1)%n) + n; }

template<class T> T *zmalloc(size_t sz) {
    T *ptr = (T*)malloc(sz*sizeof(T));
    ::memset(ptr, 0, sz);
    return ptr;
}

unsigned long long currentTimeMillis() {
    struct ::timespec ts;
    ::clock_gettime(CLOCK_MONOTONIC, &ts);
    return (unsigned long long)(ts.tv_sec * 1000ULL + ts.tv_nsec / 1000000);
}

void print_clusters (float *clusters, int num_clusters, int dims) {
        for (int d=0 ; d<dims ; ++d) {
                for (int k=0 ; k<num_clusters ; ++k) {
                        if (k>0)
                                printf(" ");
                        printf("%.2f", clusters[k*dims+d]);
                }
                printf("\n");
        }
}


int main (int argc, char **argv)
{
        if (argc!=5) {
                fprintf(stderr, "Usage: %s <num_points> <num_clusters> <dimensions> <iterations>\n", argv[0]);
                exit(EXIT_FAILURE);
        }
        int num_slice_points = strtol(argv[1], NULL, 10);
        int num_clusters = strtol(argv[2], NULL, 10);
        int dim = strtol(argv[3], NULL, 10);
        int iterations = strtol(argv[4], NULL, 10);

        // clusters are dimension-major
        float *host_clusters     = zmalloc<float>(num_clusters*dim);
        float *old_clusters      = zmalloc<float>(num_clusters*dim);
        int *host_cluster_counts = zmalloc<int>(num_clusters);
            
        int num_slice_points_stride = num_slice_points;
            
        // these are pretty big so allocate up front
        float *host_points = zmalloc<float>(num_slice_points_stride*dim);
        for (int p=0 ; p<num_slice_points ; ++p) {
                for (int d=0 ; d<dim ; ++d) {
                        host_points[p+d*num_slice_points_stride] = rand()/(RAND_MAX+1.0);
                }
        }
        for (int p=0 ; p<num_clusters ; ++p) {
                for (int d=0 ; d<dim ; ++d) {
                        host_clusters[p*dim+d] = host_points[p+d*num_slice_points_stride];
                        //host_clusters[p*dim+d] = host_points[p*dim+d];
                }
        }
            
        unsigned long long k_start_time = currentTimeMillis();
        for (int i=0 ; i<iterations ; ++i) {

                std::swap(host_clusters, old_clusters);
                ::memset(host_clusters, 0, num_clusters*dim*sizeof(*host_clusters));
                ::memset(host_cluster_counts, 0, num_clusters*sizeof(*host_cluster_counts));

                for (int p=0 ; p<num_slice_points ; p++) {
                        int closest = -1;
                        float closest_dist = FLT_MAX;
                        for (int k=0 ; k<num_clusters ; ++k) {
                                float dist = 0;
                                for (int d=0 ; d<dim ; ++d) {
                                        float tmp = host_points[p+d*num_slice_points_stride] - old_clusters[k*dim+d];
                                        //float tmp= host_points[p*dim+d] - old_clusters[k*dim+d];
                                        dist += tmp * tmp;
                                }
                                if (dist < closest_dist) {
                                        closest_dist = dist;
                                        closest = k;
                                }
                        }
                        for (int d=0 ; d<dim ; ++d) {
                                host_clusters[closest*dim+d] += host_points[p+d*num_slice_points_stride];
                                //host_clusters[closest*dim+d] += host_points[p*dim+d];
                        }
                        host_cluster_counts[closest]++;
                }

                for (int k=0 ; k<num_clusters ; ++k) {
                        for (int d=0 ; d<dim ; ++d) host_clusters[k*dim+d] /= host_cluster_counts[k];
                }

        }
        unsigned long long k_stop_time = currentTimeMillis();

        print_clusters(host_clusters, num_clusters, dim);

        printf("time taken: %f seconds\n", (k_stop_time - k_start_time)/1E3);

            
        return EXIT_SUCCESS;
}

// vim: shiftwidth=8:tabstop=8:expandtab


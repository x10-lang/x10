/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

import x10.io.Console;

import x10.io.File;
import x10.io.Marshal;
import x10.io.IOException;

import x10.util.Team;
import x10.util.Pair;
import x10.util.HashMap;

import x10.util.OptionsParser;
import x10.util.Option;
import x10.util.CUDAUtilities;

import x10.array.RemoteArray;
import x10.array.Array;

import x10.compiler.Unroll;
import x10.compiler.CUDADirectParams;
import x10.compiler.CUDA;
import x10.compiler.Native;
import x10.compiler.NoInline;

import x10.gl.GL;


public class KMeansCUDADemo(gpu:Place) {

    val numPoints : Int;
    val stride : Int;
    val numClusters : Int;
    val dim : Int;
    val hostClusters : Array[Float]{rank==1, zeroBased, rect, rail};
    val hostClusterCounts : Array[Int]{rank==1, zeroBased, rect, rail};
    val hostClustersCopy : Array[Float]{rank==1, zeroBased, rect, rail};
    var hostPoints : Array[Float]{rank==1, zeroBased, rect, rail};
    val glPoints : Array[Float]{rank==1, zeroBased, rect, rail};
    var gpuPoints : RemoteArray[Float]{rank==1, home==gpu};
    var hostNearest : Array[Int]{rank==1, zeroBased, rect, rail};
    var gpuNearest : RemoteArray[Int]{rank==1, home==gpu};
    val vbos = new Array[Int](1);

    var posX:Float = 0;
    var posY:Float = 0;
    var zoom:Float = 1;
    var brightness:Float = 1/255.0f;
    var aspect:Float = 1;
    
    var iterating:Boolean = false;
    var step:Boolean = false;

    private static def round_up (x:Int, n:Int) = (x-1) - ((x-1)%n) + n;

    static def reset (num_points:Int, num_clusters:Int, dim:Int, points:Array[Float](1), clusters:Array[Float](1)) {
        for ([p] in 0..(num_clusters-1)) {
            for ([d] in 0..(dim-1)) {
                // stretch them out over the whole points array
                val p2 = ((p as Float)/num_clusters * num_points) as Int;
                clusters(p*dim + d) = points(p2 * dim + d);
            }
        }
    }

    def this (args: Array[String]{rank==1, zeroBased, rect, rail}) {
        
        property(here.numChildren()==0 ? here : here.child(0));

        GL.glutInit(args);

        val numPoints = 5612730;
        val numClusters = args.size > 0 ? Int.parseInt(args(0)) : 60;
        val dim = 2;

        // file is dimension-major
        val file = new File("points.dat");
        if (!file.exists()) {
            Console.ERR.println("The points.dat file cannot be found.  It is not part of the svn repository, but can be fetched from the following URL:");
            Console.ERR.println("http://dist.codehaus.org/x10/misc/points.dat");
            Console.ERR.println("You must download this file before you can run the demo.");
        }
        val fr = file.openRead();
        assert file.size() / 4 / dim == numPoints as Long;
        val glPoints = new Array[Float](numPoints*dim, (Int) => {
            return Float.fromIntBits(Marshal.INT.read(fr).reverseBytes());
        });
        hostClusters = new Array[Float](numClusters*dim, 0.0f);
        reset(numPoints, numClusters, dim, glPoints, hostClusters);

        val stride = round_up(numPoints,32);
        hostPoints = new Array[Float](stride*dim, (i:Int) => {
            val d=i/stride, p=i%stride;
            return p<numPoints ? glPoints(p*dim + d) : 0f;
        });

        hostClustersCopy = new Array[Float](numClusters*dim);
        hostClusterCounts = new Array[Int](numClusters, 100000);

        gpuPoints = CUDAUtilities.makeRemoteArray[Float](gpu, stride*dim, hostPoints);
        Console.OUT.println(gpuPoints.size);
        hostNearest = new Array[Int](numPoints, 0);
        gpuNearest = CUDAUtilities.makeRemoteArray[Int](gpu, numPoints, 0);


        GL.glutInitDisplayMode(GL.GLUT_RGBA | GL.GLUT_DOUBLE); // double buffered
        GL.glutInitWindowSize(800, 600);
        GL.glutCreateWindow("X10 KMeans Demo");

        GL.glewInit();

        val points_sz = numPoints * dim * 4;

        GL.glGenBuffers(vbos.size, vbos, 0);
        GL.glBindBuffer(GL.GL_ARRAY_BUFFER, vbos(0));
        GL.glBufferData(GL.GL_ARRAY_BUFFER, points_sz, glPoints, 0, GL.GL_DYNAMIC_DRAW);
        {
            val test_size = new Array[Int](1);
            GL.glGetBufferParameteriv(GL.GL_ARRAY_BUFFER, GL.GL_BUFFER_SIZE, test_size, 0);
            assert test_size(0) == points_sz : "Buffer size was incorrect "+test_size(0)+" not "+points_sz+".";
        }
        GL.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);

        this.numPoints = numPoints;
        this.numClusters = numClusters;
        this.dim = dim;
        this.stride = stride;
        this.glPoints = glPoints;
    }

    def iterate () {

        Array.copy(hostClusters, 0, hostClustersCopy, 0, numClusters*dim);

        // avoid serialising 'this'
        val stride = this.stride;
        val num_points = numPoints;
        val num_clusters = numClusters;
        val gpu_points = gpuPoints;
        val gpu_nearest = gpuNearest;
        val clusters_copy = hostClustersCopy;
        val dim = 2;

        // classify kernel
        val gpu2 = gpu;
        finish async at (gpu2) @CUDA @CUDADirectParams {
            val blocks = CUDAUtilities.autoBlocks(),
                threads = CUDAUtilities.autoThreads();
            finish for (block in 0..(blocks-1)) async {
                val clustercache = new Array[Float](clusters_copy);
                clocked finish for (thread in 0..(threads-1)) clocked async {
                    val tid = block * threads + thread;
                    val tids = blocks * threads;
                    for (var p:Int=tid ; p<num_points ; p+=tids) {
                        var closest:Int = -1;
                        var closest_dist:Float = Float.MAX_VALUE;
                        @Unroll(20) for (k in 0..(num_clusters-1)) {
                            // Pythagoras (in dim dimensions)
                            var dist : Float = 0;
                            for (d in 0..(dim-1)) {
                                val tmp = gpu_points(p+d*stride)
                                          - @NoInline clustercache(k*dim+d);
                                dist += tmp * tmp;
                            }
                            // record closest cluster seen so far
                            if (dist < closest_dist) {
                                closest_dist = dist;
                                closest = k;
                            }
                        }
                        gpu_nearest(p) = closest;
                    }
                }
            }
        }

        // bring gpu results onto host
        finish Array.asyncCopy(gpu_nearest, 0, hostNearest, 0, numPoints);

        // compute new clusters
        hostClusters.fill(0);
        hostClusterCounts.fill(0);

        val host_nearest_raw = hostNearest.raw();
        val host_clusters_raw = hostClusters.raw();
        val host_points_raw = hostPoints.raw();
        val host_cluster_counts_raw = hostClusterCounts.raw();
        for (var p:Int=0 ; p<numPoints ; p++) {
            val closest = host_nearest_raw(p);
            for (var d:Int=0 ; d<dim ; ++d)
                host_clusters_raw(closest*dim+d) += host_points_raw(p+d*stride);
            host_cluster_counts_raw(closest)++;
        }

        for (var k:Int=0 ; k<num_clusters ; ++k) {
            if (hostClusterCounts(k) <= 0) Console.ERR.println("host_cluster_counts("+k+") = "+hostClusterCounts(k));
            for (var d:Int=0 ; d<dim ; ++d) hostClusters(k*dim+d) /= hostClusterCounts(k);
        }

    }

    class FrameEventHandler extends GL.FrameEventHandler {
        public def display () {

            val before = System.nanoTime();

            GL.glClear(GL.GL_COLOR_BUFFER_BIT);

            // render points
            GL.glColor4f(1, 1, 1, brightness);
            GL.glPointSize(1);
            GL.glEnable(GL.GL_POINT_SMOOTH);
            GL.glEnable(GL.GL_BLEND);
            GL.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);

            GL.glEnableClientState(GL.GL_VERTEX_ARRAY);
                GL.glDisable(GL.GL_DEPTH_TEST);
                GL.glDisable(GL.GL_CULL_FACE);
                GL.glBindBuffer(GL.GL_ARRAY_BUFFER, vbos(0));
                    GL.glVertexPointer[Float](2, GL.GL_FLOAT, 0, null, 0);
                    GL.glDrawArrays(GL.GL_POINTS, 0, numPoints);
                GL.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
            GL.glDisableClientState(GL.GL_VERTEX_ARRAY);

            // render clusters
            GL.glDisableClientState(GL.GL_TEXTURE_COORD_ARRAY);
            GL.glDisable(GL.GL_TEXTURE_2D);

            GL.glBlendFunc(GL.GL_ONE, GL.GL_ONE);
            GL.glBegin(GL.GL_QUADS);
                for ([p] in 0..(numClusters-1)) {
                    val rad = (Math.sqrt(hostClusterCounts(p) as Double)/Math.PI) as Float * 0.0002f;
                    if (iterating || step) {
                        GL.glColor3f(0.3f, 0.0f, 0.0f);
                    } else {
                        GL.glColor3f(0.0f, 0.3f, 0.0f);
                    }
                    val x = hostClusters(2*p), y = hostClusters(2*p + 1);
                    GL.glVertex2f(x-rad, y-rad);
                    GL.glVertex2f(x+rad, y-rad);
                    GL.glVertex2f(x+rad, y+rad);
                    GL.glVertex2f(x-rad, y+rad);
                }
            GL.glEnd();

            GL.glutSwapBuffers();

            if (iterating || step) {
                iterate();
                step = false;
            }

            val after = System.nanoTime();
            val seconds = (after-before)/1E9;
            Console.OUT.println("Frame time: " + seconds + "s " + 1/seconds + " FPS.");

            GL.glutPostRedisplay();

        }
        public def idle () {
            GL.glutPostRedisplay();
        }
        def setMatrixes ()
        {
            GL.glMatrixMode(GL.GL_PROJECTION);
            GL.glLoadIdentity();
            if (aspect > 1) {
                GL.glOrtho(posX - 1/zoom*aspect, posX + 1/zoom*aspect, posY - 1/zoom, posY + 1/zoom, 0, 1);
            } else {
                GL.glOrtho(posX - 1/zoom, posX + 1/zoom, posY - 1/zoom/aspect, posY + 1/zoom/aspect, 0, 1);
            }

            GL.glMatrixMode(GL.GL_MODELVIEW);
            GL.glLoadIdentity();
        }

        public def keyboard (key: Char, x:Int, y:Int) {
            if (key == '+') {
                zoom *= 2;
                zoom = Math.min(zoom, Math.pow(2,31)) as Float;
                setMatrixes();
            } else if (key == '-') {
                zoom /= 2;
                zoom = Math.max(zoom, 0.5f);
                setMatrixes();
            } else if (key == 'B') {
                brightness += 1/255.0;
                brightness = Math.min(1.0f, brightness);
            } else if (key == 'b') {
                brightness -= 1/255.0;
                brightness = Math.max(1/255.0f, brightness);
            } else if (key == 'v') {
                brightness = 1/255.0f;
            } else if (key == 'V') {
                brightness = 1;
            } else if (key == 'w' || key == 'a' || key == 's' || key == 'd') {
                var xoff:Float=0, yoff:Float=0;
                if (key=='a') xoff= -1;
                else if (key=='d') xoff= 1;
                else if (key=='w') yoff= 1;
                else if (key=='s') yoff = -1;
                posX += xoff/zoom * 0.5;
                posY += yoff/zoom * 0.5;
                posX = Math.max(-1.0f, Math.min(1.0f, posX));
                posY = Math.max(-1.0f, Math.min(1.0f, posY));
                setMatrixes();
            } else if (key == 'c') {
                posX = 0;
                posY = 0;
                setMatrixes();
            } else if (key == 'r') {
                reset(numPoints, numClusters, dim, glPoints, hostClusters);
                Console.OUT.println("Reset.");
            } else if (key == 'g') {
                iterating = !iterating;
                Console.OUT.println("Iterating: "+iterating);
            } else if (key == 'f') {
                step = true;
                Console.OUT.println("Single step.");
            }
        }
        public def reshape (x:Int, y:Int) {
            GL.glViewport(0, 0, x, y);
            aspect = x as Float/y;
            setMatrixes();
            GL.glutPostRedisplay();
        }
        public def motion () {
            GL.glutPostRedisplay();
        }
    }

    public def run () {

        GL.glutMainLoop(new FrameEventHandler());

        GL.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
        GL.glDeleteBuffers(vbos.size, vbos, 0);
    }

    public static def main (args : Array[String]{rank==1, rect, zeroBased, rail}) {
        try {

            new KMeansCUDADemo(args).run();

        } catch (e : Throwable) {
            e.printStackTrace(Console.ERR);
        }
    }
}

// vim: shiftwidth=4:tabstop=4:expandtab

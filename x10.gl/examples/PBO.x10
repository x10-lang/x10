// Based on code by Song Ho Ahn (song.ahn@gmail.com) from http://www.songho.ca/opengl/gl_pbo.html

import x10.io.Console;

import x10.gl.GL;

public class PBO {

    static val IMAGE_WIDTH = 800;
    static val IMAGE_HEIGHT = 600;
    static val CHANNEL_COUNT = 3;
    static val DATA_SIZE = IMAGE_WIDTH * IMAGE_HEIGHT * CHANNEL_COUNT;
    static val PIXEL_FORMAT = GL.GL_RGB8;

    val pboFront = new Array[Int](1);
    val pboBack = new Array[Int](1);
    val tex = new Array[Int](1);

    def this (args: Array[String]{rank==1, zeroBased, rect, rail}) {

        GL.glutInit(args);
        GL.glutInitDisplayMode(GL.GLUT_RGB | GL.GLUT_DOUBLE); // double buffered

        GL.glutInitWindowSize(IMAGE_WIDTH, IMAGE_HEIGHT);

        GL.glutCreateWindow("PBO test");

        GL.glewInit();

        GL.glGenTextures(1, tex, 0);
        GL.glBindTexture(GL.GL_TEXTURE_2D, tex(0));
        GL.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
        GL.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
        GL.glTexImage2D[Byte](GL.GL_TEXTURE_2D, 0, PIXEL_FORMAT, IMAGE_WIDTH, IMAGE_HEIGHT, 0, GL.GL_RGB, GL.GL_UNSIGNED_BYTE, null, 0);
        GL.glBindTexture(GL.GL_TEXTURE_2D, 0);

        GL.glGenBuffers(1, pboFront, 0);
        GL.glBindBuffer(GL.GL_PIXEL_UNPACK_BUFFER, pboFront(0));
        GL.glBufferData[Byte](GL.GL_PIXEL_UNPACK_BUFFER, DATA_SIZE, null, 0, GL.GL_STREAM_DRAW);
        GL.glBindBuffer(GL.GL_PIXEL_UNPACK_BUFFER, 0);

        GL.glGenBuffers(1, pboBack, 0);
        GL.glBindBuffer(GL.GL_PIXEL_UNPACK_BUFFER, pboBack(0));
        GL.glBufferData[Byte](GL.GL_PIXEL_UNPACK_BUFFER, DATA_SIZE, null, 0, GL.GL_STREAM_DRAW);
        GL.glBindBuffer(GL.GL_PIXEL_UNPACK_BUFFER, 0);

    }



    class FrameEventHandler extends GL.FrameEventHandler {
        var counter:Int = 0;
        public def display () {

            //val before = System.nanoTime();

            GL.glBindTexture(GL.GL_TEXTURE_2D, tex(0));
    
            // background dma
            GL.glBindBuffer(GL.GL_PIXEL_UNPACK_BUFFER, pboBack(0));
            GL.glTexSubImage2D(GL.GL_TEXTURE_2D, 0, 0, 0, IMAGE_WIDTH, IMAGE_HEIGHT, GL.GL_RGB, GL.GL_UNSIGNED_BYTE, 0);
    
    
            // update other pbo
            GL.glBindBuffer(GL.GL_PIXEL_UNPACK_BUFFER, pboFront(0));
            GL.glBufferData[Byte](GL.GL_PIXEL_UNPACK_BUFFER, DATA_SIZE, null, 0, GL.GL_STREAM_DRAW); // discard
            val raw = GL.glMapBuffer[Byte](GL.GL_PIXEL_UNPACK_BUFFER, GL.GL_WRITE_ONLY, DATA_SIZE);
            val h = counter;
            val width = IMAGE_WIDTH;
            val height = IMAGE_HEIGHT;
            for (var y:Int=0 ; y<height ; ++y) {
                for (var x:Int=0 ; x<width ; ++x) {
                    raw(y*width*3 + x*3 + 0) = ((x * x) / 256 + 3 * y + h) as Byte;
                    raw(y*width*3 + x*3 + 1) = ((y * y) / 256 + x + h) as Byte;
                    raw(y*width*3 + x*3 + 2) = h as Byte;
                }
            }
            counter += 10;
            GL.glUnmapBuffer(GL.GL_PIXEL_UNPACK_BUFFER);
            GL.glBindBuffer(GL.GL_PIXEL_UNPACK_BUFFER, 0);

            // swap pbos
            val tmp = pboFront(0);
            pboFront(0) = pboBack(0);
            pboBack(0) = tmp;

            GL.glEnable(GL.GL_TEXTURE_2D);
            GL.glBegin(GL.GL_QUADS);
            GL.glTexCoord2f(0.0f, 1.0f);   GL.glVertex3f(-1.0f, -1.0f, 0.0f);
            GL.glTexCoord2f(1.0f, 1.0f);   GL.glVertex3f( 1.0f, -1.0f, 0.0f);
            GL.glTexCoord2f(1.0f, 0.0f);   GL.glVertex3f( 1.0f,  1.0f, 0.0f);
            GL.glTexCoord2f(0.0f, 0.0f);   GL.glVertex3f(-1.0f,  1.0f, 0.0f);
            GL.glEnd();
            GL.glDisable(GL.GL_TEXTURE_2D);

            GL.glBindTexture(GL.GL_TEXTURE_2D, 0);

            GL.glutSwapBuffers();


            //val after = System.nanoTime();
            //val seconds = (after-before)/1E9;
            //Console.OUT.println("Frame time: " + seconds + "s " + 1/seconds + " FPS.");


        }

        public def idle () {
            GL.glutPostRedisplay();
        }

        def setMatrixes ()
        {
            GL.glMatrixMode(GL.GL_PROJECTION);
            GL.glLoadIdentity();

            GL.glMatrixMode(GL.GL_MODELVIEW);
            GL.glLoadIdentity();
        }

        public def reshape (x:Int, y:Int) {
            GL.glViewport(0, 0, x, y);
            setMatrixes();
        }
    }


    public def run () {

        GL.glutMainLoop(new FrameEventHandler());

    }

    public static def main (args : Array[String]{rank==1, rect, zeroBased, rail}) {
        try {

            new PBO(args).run();

        } catch (e : Throwable) {
            e.printStackTrace(Console.ERR);
        }
    }

}

// vim: shiftwidth=4:tabstop=4:expandtab


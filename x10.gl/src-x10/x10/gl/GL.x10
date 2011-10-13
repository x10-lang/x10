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

package x10.gl;

import x10.compiler.*;
import x10.util.IndexedMemoryChunk;

@NativeCPPInclude("GLaux.h")
public final class GL {

    public static abstract class FrameEventHandler {
        public def display () : void { }
        public def idle () : void { }
        public def keyboard (key: Char, x:Int, y:Int) : void { }
        public def keyboardUp (key: Char, x:Int, y:Int) : void { }
        public def click (button:Int, updown:Int, x:Int, y:Int) : void { }
        public def reshape (x:Int, y:Int) : void { }
        public def motion (x:Int, y:Int) : void { }
        public def passiveMotion (x:Int, y:Int) : void { }
    }

    static the_feh = new Cell[FrameEventHandler](null);

    static def display () { the_feh().display(); }
    static def keyboard (key: UByte, x:Int, y:Int) { the_feh().keyboard(key as Byte as Char, x, y); }
    static def keyboardUp (key: UByte, x:Int, y:Int) { the_feh().keyboardUp(key as Byte as Char, x, y); }
    static def click (button:Int, updown:Int, x:Int, y:Int) { the_feh().click(button, updown, x, y); }
    static def reshape (x:Int, y:Int) { the_feh().reshape(x,y); }   
    static def motion (x:Int, y:Int) { the_feh().motion(x,y); }
    static def passiveMotion (x:Int, y:Int) { the_feh().passiveMotion(x,y); }
    static def idle () { the_feh().idle(); }

    public static def glutInit (args: Array[String]{rail}) : Array[String]{rail} {
        @Native("c++",
            "    typedef x10::array::Array<x10aux::ref<x10::lang::String> > AS;\n"+
            "    typedef x10aux::ref<AS> pAS;\n"+
            "    int argc = args->FMGL(size);\n"+
            "    char **argv = x10aux::alloc<char*>(sizeof(char*)*argc);\n"+
            "    for (int i=0 ; i<argc ; ++i)\n"+
            "        argv[i] = const_cast<char*>(args->raw()->raw()[i]->c_str());\n"+
            "    ::glutInit(&argc, argv);\n"+
            "\n"+
            "    pAS new_args = pAS((new (memset(x10aux::alloc<AS>(), 0, sizeof(AS))) AS()));\n"+
            "    new_args->AS::_constructor(argc);\n"+
            "\n"+
            "    for (int i=0 ; i<argc ; ++i)\n"+
            "        new_args->raw()->raw()[i] = x10::lang::String::Lit(argv[i]);\n"+
            "    args = new_args;\n"
        ) { }
        return args;
    }
    @Native("c++", "glutInitDisplayMode(#mode)")
    public static def glutInitDisplayMode (mode:Int) : void { }
    @Native("c++", "glutInitWindowSize(#w, #h)")
    public static def glutInitWindowSize (w:Int, h:Int) : void { }
    @Native("c++", "glutCreateWindow((char*)(#title->c_str()))")
    public static def glutCreateWindow (title:String) : Int = 0;
    @Native("c++", "glutSwapBuffers()")
    public static def glutSwapBuffers () : void { }
    @Native("c++", "glutPostRedisplay()")
    public static def glutPostRedisplay () : void { }
    public static def glutMainLoop (callbacks : FrameEventHandler) {
        the_feh() = callbacks;
        @Native("c++",
            "    ::glutDisplayFunc(display);\n"+
            "    ::glutKeyboardFunc(keyboard);\n"+
            "    ::glutKeyboardUpFunc(keyboardUp);\n"+
            "    ::glutMouseFunc(click);\n"+
            "    ::glutMotionFunc(motion);\n"+
            "    ::glutPassiveMotionFunc(passiveMotion);\n"+
            "    ::glutReshapeFunc(reshape);\n"+
            "    ::glutIdleFunc(idle);\n"+
            "    ::glutMainLoop();\n"
        ) { };
    }
    @Native("c++", "GLUT_RGB")
    public static GLUT_RGB : Int = 0;
    @Native("c++", "GLUT_RGBA")
    public static GLUT_RGBA : Int = 0;
    @Native("c++", "GLUT_DOUBLE")
    public static GLUT_DOUBLE : Int = 0;

    @Native("c++", "glewInit()")
    public static def glewInit () : Boolean = true;

    @Native("c++", "glGenTextures(#n, (GLuint*)&(#buffers)->raw()->raw()[#offset])")
    public static def glGenTextures (n:Int, buffers: Array[Int]{rail}, offset: Int) : void { }
    @Native("c++", "glGenBuffers(#n, (GLuint*)&(#buffers)->raw()->raw()[#offset])")
    public static def glGenBuffers (n:Int, buffers: Array[Int]{rail}, offset: Int) : void { }
    @Native("c++", "glDeleteBuffers(#n, (GLuint*)&(#buffers)->raw()->raw()[#offset])")
    public static def glDeleteBuffers (n:Int, buffers: Array[Int]{rail}, offset: Int) : void { }
    @Native("c++", "glBindBuffer(#target, #buffer)")
    public static def glBindBuffer (target:Int, buffer:Int) : void { }
    @Native("c++", "glBindTexture(#target, #buffer)")
    public static def glBindTexture (target:Int, buffer:Int) : void { }
    @Native("c++", "glBufferData(#target, (#count), x10aux::lookup_or_null(#data, #offset), #usage)")
    public static def glBufferData[T] (target:Int, count:Int, data:Array[T]{rail}, offset:Int, usage:Int) : void { }
    @Native("c++", "glGetBufferParameteriv(#target, #value, &(#data)->raw()->raw()[#offset])")
    public static def glGetBufferParameteriv (target:Int, value:Int, data:Array[Int]{rail}, offset:Int) : void { }
    @Native("c++", "glColor4f(#r, #g, #b, #a)")
    public static def glColor4f(r:Float, g:Float, b:Float, a:Float) : void { }
    @Native("c++", "glColor3f(#r, #g, #b)")
    public static def glColor3f(r:Float, g:Float, b:Float) : void { }
    @Native("c++", "glPointSize(#v)")
    public static def glPointSize(v:Float) : void { }
    @Native("c++", "glOrtho(#minx, #maxx, #miny, #maxy, #minz, #maxz)")
    public static def glOrtho(minx:Float, maxx:Float, miny:Float, maxy:Float, minz:Float, maxz:Float) : void { }
    @Native("c++", "glLoadIdentity()")
    public static def glLoadIdentity() : void { }
    @Native("c++", "glViewport(#left, #top, #right, #bottom)")
    public static def glViewport(left:Int, top:Int, right:Int, bottom:Int) : void { }
    @Native("c++", "glClear(#v)")
    public static def glClear(v:Int) : void { }
    @Native("c++", "glBegin(#v)")
    public static def glBegin(v:Int) : void { }
    @Native("c++", "glEnd()")
    public static def glEnd() : void { }
    @Native("c++", "glEnable(#v)")
    public static def glEnable(v:Int) : void { }
    @Native("c++", "glDisable(#v)")
    public static def glDisable(v:Int) : void { }
    @Native("c++", "glEnableClientState(#v)")
    public static def glEnableClientState(v:Int) : void { }
    @Native("c++", "glDisableClientState(#v)")
    public static def glDisableClientState(v:Int) : void { }
    @Native("c++", "glBlendFunc(#x, #y)")
    public static def glBlendFunc(x:Int, y:Int) : void { }
    @Native("c++", "glMatrixMode(#x)")
    public static def glMatrixMode(x:Int) : void { }
    @Native("c++", "glTexCoord2f(#x, #y)")
    public static def glTexCoord2f(x:Float, y:Float) : void { }
    @Native("c++", "glVertex2f(#x, #y)")
    public static def glVertex2f(x:Float, y:Float) : void { }
    @Native("c++", "glVertex3f(#x, #y, #z)")
    public static def glVertex3f(x:Float, y:Float, z:Float) : void { }
    @Native("c++", "glVertexPointer(#size, #typ, #stride, x10aux::lookup_or_null(#data, #offset))")
    public static def glVertexPointer[T](size:Int, typ:Int, stride:Int, data:Array[T], offset:Int) : void { }
    @Native("c++", "glDrawArrays(#mode, #first, #count)")
    public static def glDrawArrays(mode:Int, first:Int, count:Int) : void { }
    @Native("c++", "glTexParameteri(#target, #value, #data)")
    public static def glTexParameteri (target:Int, value:Int, data:Int) : void { }
    // this the version associated with GL.GL_PIXEL_UNPACK_BUFFER
    @Native("c++", "glTexSubImage2D(#target, #level, #xOffset, #yOffset, #width, #height, #format, #typ, #bufferOffset)")
    public static def glTexSubImage2D (target:Int, level:Int, xOffset:Int, yOffset:Int, width:Int, height:Int, format:Int, typ:Int, bufferOffset:Int) : void { }
    @Native("c++", "glTexImage2D(#target, #level, #xOff, #yOff, #w, #h, #border, #fmt, x10aux::lookup_or_null(#data, #dataOffset))")
    public static def glTexImage2D[T] (target:Int, level:Int, xOff:Int, yOff:Int, w:Int, h:Int, border:Int, fmt:Int, data:Array[T](1), dataOffset:Int) : void { }
    public static def glMapBuffer[T] (target:Int, value:Int, len:Int) : IndexedMemoryChunk[T] {
        @Native("c++", "TPMGL(T) *tmp = (TPMGL(T)*)::glMapBuffer(target, value);") { }
        @Native("c++", "return typename x10::util::IndexedMemoryChunk<TPMGL(T)>::IndexedMemoryChunk(tmp, tmp, len);") { }
        return Zero.get[IndexedMemoryChunk[T]]();
    }
    @Native("c++", "glUnmapBuffer(#target)")
    public static def glUnmapBuffer (target:Int) : void { }
    @Native("c++", "GL_ARRAY_BUFFER")
    public static GL_ARRAY_BUFFER : Int = 0;
    @Native("c++", "GL_DYNAMIC_DRAW")
    public static GL_DYNAMIC_DRAW : Int = 0;
    @Native("c++", "GL_BUFFER_SIZE")
    public static GL_BUFFER_SIZE : Int = 0;
    @Native("c++", "GL_COLOR_BUFFER_BIT")
    public static GL_COLOR_BUFFER_BIT : Int = 0;
    @Native("c++", "GL_POINT_SMOOTH")
    public static GL_POINT_SMOOTH : Int = 0;
    @Native("c++", "GL_BLEND")
    public static GL_BLEND : Int = 0;
    @Native("c++", "GL_SRC_ALPHA")
    public static GL_SRC_ALPHA : Int = 0;
    @Native("c++", "GL_ONE")
    public static GL_ONE: Int = 0;
    @Native("c++", "GL_ONE_MINUS_SRC_ALPHA")
    public static GL_ONE_MINUS_SRC_ALPHA : Int = 0;
    @Native("c++", "GL_DEPTH_TEST")
    public static GL_DEPTH_TEST : Int = 0;
    @Native("c++", "GL_CULL_FACE")
    public static GL_CULL_FACE : Int = 0;
    @Native("c++", "GL_FLOAT")
    public static GL_FLOAT : Int = 0;
    @Native("c++", "GL_POINTS")
    public static GL_POINTS : Int = 0;
    @Native("c++", "GL_QUADS")
    public static GL_QUADS : Int = 0;
    @Native("c++", "GL_VERTEX_ARRAY")
    public static GL_VERTEX_ARRAY : Int = 0;
    @Native("c++", "GL_TEXTURE_COORD_ARRAY")
    public static GL_TEXTURE_COORD_ARRAY : Int = 0;
    @Native("c++", "GL_TEXTURE_2D")
    public static GL_TEXTURE_2D : Int = 0;
    @Native("c++", "GL_PROJECTION")
    public static GL_PROJECTION : Int = 0;
    @Native("c++", "GL_MODELVIEW")
    public static GL_MODELVIEW : Int = 0;
    @Native("c++", "GL_RGB")
    public static GL_RGB : Int = 0;
    @Native("c++", "GL_RGB8")
    public static GL_RGB8 : Int = 0;
    @Native("c++", "GL_UNSIGNED_BYTE")
    public static GL_UNSIGNED_BYTE : Int = 0;
    @Native("c++", "GL_PIXEL_UNPACK_BUFFER")
    public static GL_PIXEL_UNPACK_BUFFER : Int = 0;
    @Native("c++", "GL_STREAM_DRAW")
    public static GL_STREAM_DRAW : Int = 0;
    @Native("c++", "GL_WRITE_ONLY")
    public static GL_WRITE_ONLY : Int = 0;
    @Native("c++", "GL_TEXTURE_MIN_FILTER")
    public static GL_TEXTURE_MIN_FILTER : Int = 0;
    @Native("c++", "GL_TEXTURE_MAG_FILTER")
    public static GL_TEXTURE_MAG_FILTER : Int = 0;
    @Native("c++", "GL_NEAREST")
    public static GL_NEAREST : Int = 0;
    @Native("c++", "GL_LINEAR")
    public static GL_LINEAR : Int = 0;

}

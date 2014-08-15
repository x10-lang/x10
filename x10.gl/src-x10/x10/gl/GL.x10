/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

package x10.gl;

import x10.compiler.*;

@NativeCPPInclude("GLaux.h")
public final class GL {

    public static abstract class FrameEventHandler {
        public def display () : void { }
        public def idle () : void { }
        public def keyboard (key:Char, x:Int, y:Int) : void { }
        public def keyboardUp (key:Char, x:Int, y:Int) : void { }
        public def click (button:Int, updown:Int, x:Int, y:Int) : void { }
        public def reshape (x:Int, y:Int) : void { }
        public def motion (x:Int, y:Int) : void { }
        public def passiveMotion (x:Int, y:Int) : void { }
        public def mouse (button:Int, state:Int, x:Int, y:Int) : void { }
    }

    static the_feh = new Cell[FrameEventHandler](null);

    static def display () { the_feh().display(); }
    static def keyboard (key:UByte, x:Int, y:Int) { the_feh().keyboard(key as Byte as Char, x, y); }
    static def keyboardUp (key:UByte, x:Int, y:Int) { the_feh().keyboardUp(key as Byte as Char, x, y); }
    static def click (button:Int, updown:Int, x:Int, y:Int) { the_feh().click(button, updown, x, y); }
    static def reshape (x:Int, y:Int) { the_feh().reshape(x,y); }   
    static def motion (x:Int, y:Int) { the_feh().motion(x,y); }
    static def passiveMotion (x:Int, y:Int) { the_feh().passiveMotion(x,y); }
    static def mouse (button:Int, state:Int, x:Int, y:Int) : void { the_feh().mouse(button,state,x,y); }
    static def idle () { the_feh().idle(); }

    public static def glutInit (args:Rail[String]) : Rail[String]{
        @Native("c++",
            "    typedef ::x10::lang::Rail< ::x10::lang::String *> RS;\n"+
            "    typedef RS* pRS;\n"+
            "    int argc = args->FMGL(size);\n"+
            "    char **argv = ::x10aux::alloc<char*>(sizeof(char*)*argc);\n"+
            "    for (int i=0 ; i<argc ; ++i)\n"+
            "        argv[i] = const_cast<char*>(args->raw[i]->c_str());\n"+
            "    ::glutInit(&argc, argv);\n"+
            "\n"+
            "    pRS new_args = pRS((new (memset(::x10aux::alloc<RS>(), 0, sizeof(RS))) RS(argc)));\n"+
            "    new_args->RS::_constructor(argc);\n"+
            "\n"+
            "    for (int i=0 ; i<argc ; ++i)\n"+
            "        new_args->raw[i] = ::x10::lang::String::Lit(argv[i]);\n"+
            "    args = new_args;\n"
        ) { }
        return args;
    }
    @Native("c++", "::glutInitDisplayMode(#mode)")
    public static def glutInitDisplayMode (mode:Int) : void { }
    @Native("c++", "::glutInitWindowSize(#w, #h)")
    public static def glutInitWindowSize (w:Int, h:Int) : void { }
    @Native("c++", "::glutCreateWindow((char*)(#title->c_str()))")
    public static def glutCreateWindow (title:String) : Int = 0n;
    @Native("c++", "::glutSwapBuffers()")
    public static def glutSwapBuffers () : void { }
    @Native("c++", "::glutPostRedisplay()")
    public static def glutPostRedisplay () : void { }
    @Native("c++", "::glutSetCursor(#x)")
    public static def glutSetCursor (x:Int) : void { }
    @Native("c++", "::glutWarpPointer(#x,#y)")
    public static def glutWarpPointer (x:Int, y:Int) : void { }
    @Native("c++", "::glutGet(#x)")
    public static def glutGet (x:Int) : Int = 0n;
    public static def glutMainLoop (callbacks:FrameEventHandler) {
        the_feh() = callbacks;
        @Native("c++",
            "    ::glutDisplayFunc(display);\n"+
            "    ::glutKeyboardFunc(keyboard);\n"+
            "    ::glutKeyboardUpFunc(keyboardUp);\n"+
            "    ::glutMouseFunc(click);\n"+
            "    ::glutMotionFunc(motion);\n"+
            "    ::glutPassiveMotionFunc(passiveMotion);\n"+
            "    ::glutMouseFunc(mouse);\n"+
            "    ::glutReshapeFunc(reshape);\n"+
            "    ::glutIdleFunc(idle);\n"+
            "    ::glutMainLoop();\n"
        ) { };
    }
    @Native("c++", "GLUT_RGB") public static GLUT_RGB : Int = 0n;
    @Native("c++", "GLUT_RGBA") public static GLUT_RGBA : Int = 0n;
    @Native("c++", "GLUT_DOUBLE") public static GLUT_DOUBLE : Int = 0n;
    //s/.*/@Native("c++", "&") public static & : Int = 0n;/g
    @Native("c++", "GLUT_CURSOR_RIGHT_ARROW") public static GLUT_CURSOR_RIGHT_ARROW : Int = 0n;
    @Native("c++", "GLUT_CURSOR_LEFT_ARROW") public static GLUT_CURSOR_LEFT_ARROW : Int = 0n;
    @Native("c++", "GLUT_CURSOR_INFO") public static GLUT_CURSOR_INFO : Int = 0n;
    @Native("c++", "GLUT_CURSOR_DESTROY") public static GLUT_CURSOR_DESTROY : Int = 0n;
    @Native("c++", "GLUT_CURSOR_HELP") public static GLUT_CURSOR_HELP : Int = 0n;
    @Native("c++", "GLUT_CURSOR_CYCLE") public static GLUT_CURSOR_CYCLE : Int = 0n;
    @Native("c++", "GLUT_CURSOR_SPRAY") public static GLUT_CURSOR_SPRAY : Int = 0n;
    @Native("c++", "GLUT_CURSOR_WAIT") public static GLUT_CURSOR_WAIT : Int = 0n;
    @Native("c++", "GLUT_CURSOR_TEXT") public static GLUT_CURSOR_TEXT : Int = 0n;
    @Native("c++", "GLUT_CURSOR_CROSSHAIR") public static GLUT_CURSOR_CROSSHAIR : Int = 0n;
    @Native("c++", "GLUT_CURSOR_UP_DOWN") public static GLUT_CURSOR_UP_DOWN : Int = 0n;
    @Native("c++", "GLUT_CURSOR_LEFT_RIGHT") public static GLUT_CURSOR_LEFT_RIGHT : Int = 0n;
    @Native("c++", "GLUT_CURSOR_TOP_SIDE") public static GLUT_CURSOR_TOP_SIDE : Int = 0n;
    @Native("c++", "GLUT_CURSOR_BOTTOM_SIDE") public static GLUT_CURSOR_BOTTOM_SIDE : Int = 0n;
    @Native("c++", "GLUT_CURSOR_LEFT_SIDE") public static GLUT_CURSOR_LEFT_SIDE : Int = 0n;
    @Native("c++", "GLUT_CURSOR_RIGHT_SIDE") public static GLUT_CURSOR_RIGHT_SIDE : Int = 0n;
    @Native("c++", "GLUT_CURSOR_TOP_LEFT_CORNER") public static GLUT_CURSOR_TOP_LEFT_CORNER : Int = 0n;
    @Native("c++", "GLUT_CURSOR_TOP_RIGHT_CORNER") public static GLUT_CURSOR_TOP_RIGHT_CORNER : Int = 0n;
    @Native("c++", "GLUT_CURSOR_BOTTOM_RIGHT_CORNER") public static GLUT_CURSOR_BOTTOM_RIGHT_CORNER : Int = 0n;
    @Native("c++", "GLUT_CURSOR_BOTTOM_LEFT_CORNER") public static GLUT_CURSOR_BOTTOM_LEFT_CORNER : Int = 0n;
    @Native("c++", "GLUT_CURSOR_FULL_CROSSHAIR") public static GLUT_CURSOR_FULL_CROSSHAIR : Int = 0n;
    @Native("c++", "GLUT_CURSOR_NONE") public static GLUT_CURSOR_NONE : Int = 0n;
    @Native("c++", "GLUT_CURSOR_INHERIT") public static GLUT_CURSOR_INHERIT : Int = 0n;
    @Native("c++", "GLUT_WINDOW_X") public static GLUT_WINDOW_X : Int = 0n;
    @Native("c++", "GLUT_WINDOW_Y") public static GLUT_WINDOW_Y : Int = 0n;
    @Native("c++", "GLUT_WINDOW_WIDTH") public static GLUT_WINDOW_WIDTH : Int = 0n;
    @Native("c++", "GLUT_WINDOW_HEIGHT") public static GLUT_WINDOW_HEIGHT : Int = 0n;
    @Native("c++", "GLUT_WINDOW_BUFFER_SIZE") public static GLUT_WINDOW_BUFFER_SIZE : Int = 0n;
    @Native("c++", "GLUT_WINDOW_STENCIL_SIZE") public static GLUT_WINDOW_STENCIL_SIZE : Int = 0n;
    @Native("c++", "GLUT_WINDOW_DEPTH_SIZE") public static GLUT_WINDOW_DEPTH_SIZE : Int = 0n;
    @Native("c++", "GLUT_WINDOW_RED_SIZE") public static GLUT_WINDOW_RED_SIZE : Int = 0n;
    @Native("c++", "GLUT_WINDOW_GREEN_SIZE") public static GLUT_WINDOW_GREEN_SIZE : Int = 0n;
    @Native("c++", "GLUT_WINDOW_BLUE_SIZE") public static GLUT_WINDOW_BLUE_SIZE : Int = 0n;
    @Native("c++", "GLUT_WINDOW_ALPHA_SIZE") public static GLUT_WINDOW_ALPHA_SIZE : Int = 0n;
    @Native("c++", "GLUT_WINDOW_ACCUM_RED_SIZE") public static GLUT_WINDOW_ACCUM_RED_SIZE : Int = 0n;
    @Native("c++", "GLUT_WINDOW_ACCUM_GREEN_SIZE") public static GLUT_WINDOW_ACCUM_GREEN_SIZE : Int = 0n;
    @Native("c++", "GLUT_WINDOW_ACCUM_BLUE_SIZE") public static GLUT_WINDOW_ACCUM_BLUE_SIZE : Int = 0n;
    @Native("c++", "GLUT_WINDOW_ACCUM_ALPHA_SIZE") public static GLUT_WINDOW_ACCUM_ALPHA_SIZE : Int = 0n;
    @Native("c++", "GLUT_WINDOW_DOUBLEBUFFER") public static GLUT_WINDOW_DOUBLEBUFFER : Int = 0n;
    @Native("c++", "GLUT_WINDOW_RGBA") public static GLUT_WINDOW_RGBA : Int = 0n;
    @Native("c++", "GLUT_WINDOW_PARENT") public static GLUT_WINDOW_PARENT : Int = 0n;
    @Native("c++", "GLUT_WINDOW_NUM_CHILDREN") public static GLUT_WINDOW_NUM_CHILDREN : Int = 0n;
    @Native("c++", "GLUT_WINDOW_COLORMAP_SIZE") public static GLUT_WINDOW_COLORMAP_SIZE : Int = 0n;
    @Native("c++", "GLUT_WINDOW_NUM_SAMPLES") public static GLUT_WINDOW_NUM_SAMPLES : Int = 0n;
    @Native("c++", "GLUT_WINDOW_STEREO") public static GLUT_WINDOW_STEREO : Int = 0n;
    @Native("c++", "GLUT_WINDOW_CURSOR") public static GLUT_WINDOW_CURSOR : Int = 0n;
    @Native("c++", "GLUT_SCREEN_WIDTH") public static GLUT_SCREEN_WIDTH : Int = 0n;
    @Native("c++", "GLUT_SCREEN_HEIGHT") public static GLUT_SCREEN_HEIGHT : Int = 0n;
    @Native("c++", "GLUT_SCREEN_WIDTH_MM") public static GLUT_SCREEN_WIDTH_MM : Int = 0n;
    @Native("c++", "GLUT_SCREEN_HEIGHT_MM") public static GLUT_SCREEN_HEIGHT_MM : Int = 0n;
    @Native("c++", "GLUT_MENU_NUM_ITEMS") public static GLUT_MENU_NUM_ITEMS : Int = 0n;
    @Native("c++", "GLUT_DISPLAY_MODE_POSSIBLE") public static GLUT_DISPLAY_MODE_POSSIBLE : Int = 0n;
    @Native("c++", "GLUT_INIT_DISPLAY_MODE") public static GLUT_INIT_DISPLAY_MODE : Int = 0n;
    @Native("c++", "GLUT_INIT_WINDOW_X") public static GLUT_INIT_WINDOW_X : Int = 0n;
    @Native("c++", "GLUT_INIT_WINDOW_Y") public static GLUT_INIT_WINDOW_Y : Int = 0n;
    @Native("c++", "GLUT_INIT_WINDOW_WIDTH") public static GLUT_INIT_WINDOW_WIDTH : Int = 0n;
    @Native("c++", "GLUT_INIT_WINDOW_HEIGHT") public static GLUT_INIT_WINDOW_HEIGHT : Int = 0n;
    @Native("c++", "GLUT_ELAPSED_TIME") public static GLUT_ELAPSED_TIME : Int = 0n;
    @Native("c++", "GLUT_WINDOW_FORMAT_ID") public static GLUT_WINDOW_FORMAT_ID : Int = 0n;

    @Native("c++", "glewInit()")
    public static def glewInit () : Boolean = true;

    @Native("c++", "::glGenTextures(#n, (GLuint*)&(#buffers)->raw[#offset])")
    public static def glGenTextures (n:Int, buffers:Rail[Int], offset:Int) : void { }
    @Native("c++", "::glGenBuffers(#n, (GLuint*)&(#buffers)->raw[#offset])")
    public static def glGenBuffers (n:Int, buffers:Rail[Int], offset:Int) : void { }
    @Native("c++", "::glDeleteBuffers(#n, (GLuint*)&(#buffers)->raw[#offset])")
    public static def glDeleteBuffers (n:Int, buffers:Rail[Int], offset:Int) : void { }
    @Native("c++", "::glBindBuffer(#target, #buffer)")
    public static def glBindBuffer (target:Int, buffer:Int) : void { }
    @Native("c++", "::glBindTexture(#target, #buffer)")
    public static def glBindTexture (target:Int, buffer:Int) : void { }
    @Native("c++", "::glBufferData(#target, (#count), ::x10aux::lookup_or_null(#data, #offset), #usage)")
    public static def glBufferData[T] (target:Int, count:Int, data:Rail[T], offset:Int, usage:Int) : void { }
    @Native("c++", "::glGetBufferParameteriv(#target, #value, &(#data)->raw[#offset])")
    public static def glGetBufferParameteriv (target:Int, value:Int, data:Rail[Int], offset:Int) : void { }
    @Native("c++", "::glColor4f(#r, #g, #b, #a)")
    public static def glColor4f(r:Float, g:Float, b:Float, a:Float) : void { }
    @Native("c++", "::glColor3f(#r, #g, #b)")
    public static def glColor3f(r:Float, g:Float, b:Float) : void { }
    @Native("c++", "::glPointSize(#v)")
    public static def glPointSize(v:Float) : void { }
    @Native("c++", "::glOrtho(#minx, #maxx, #miny, #maxy, #minz, #maxz)")
    public static def glOrtho(minx:Float, maxx:Float, miny:Float, maxy:Float, minz:Float, maxz:Float) : void { }
    @Native("c++", "::glLoadIdentity()")
    public static def glLoadIdentity() : void { }
    @Native("c++", "::glViewport(#left, #top, #right, #bottom)")
    public static def glViewport(left:Int, top:Int, right:Int, bottom:Int) : void { }
    @Native("c++", "::glClear(#v)")
    public static def glClear(v:Int) : void { }
    @Native("c++", "::glBegin(#v)")
    public static def glBegin(v:Int) : void { }
    @Native("c++", "::glEnd()")
    public static def glEnd() : void { }
    @Native("c++", "::glEnable(#v)")
    public static def glEnable(v:Int) : void { }
    @Native("c++", "::glDisable(#v)")
    public static def glDisable(v:Int) : void { }
    @Native("c++", "::glEnableClientState(#v)")
    public static def glEnableClientState(v:Int) : void { }
    @Native("c++", "::glDisableClientState(#v)")
    public static def glDisableClientState(v:Int) : void { }
    @Native("c++", "::glBlendFunc(#x, #y)")
    public static def glBlendFunc(x:Int, y:Int) : void { }
    @Native("c++", "::glMatrixMode(#x)")
    public static def glMatrixMode(x:Int) : void { }
    @Native("c++", "::glTexCoord2f(#x, #y)")
    public static def glTexCoord2f(x:Float, y:Float) : void { }
    @Native("c++", "::glVertex2f(#x, #y)")
    public static def glVertex2f(x:Float, y:Float) : void { }
    @Native("c++", "::glVertex3f(#x, #y, #z)")
    public static def glVertex3f(x:Float, y:Float, z:Float) : void { }
    @Native("c++", "::glVertexPointer(#size, #typ, #stride, ::x10aux::lookup_or_null(#data, #offset))")
    public static def glVertexPointer[T](size:Int, typ:Int, stride:Int, data:Rail[T], offset:Int) : void { }
    @Native("c++", "::glDrawArrays(#mode, #first, #count)")
    public static def glDrawArrays(mode:Int, first:Int, count:Int) : void { }
    @Native("c++", "::glTexParameteri(#target, #value, #data)")
    public static def glTexParameteri (target:Int, value:Int, data:Int) : void { }
    // this the version associated with GL.GL_PIXEL_UNPACK_BUFFER
// FIXME raytracer#GLFrontend.x10 needs this
//    @Native("c++", "::glTexSubImage2D(#target, #level, #xOffset, #yOffset, #width, #height, #format, #typ, #bufferOffset)")
//    public static def glTexSubImage2D (target:Int, level:Int, xOffset:Int, yOffset:Int, width:Int, height:Int, format:Int, typ:Int, bufferOffset:Int) : void { }
    @Native("c++", "::glTexImage2D(#target, #level, #xOff, #yOff, #w, #h, #border, #fmt, ::x10aux::lookup_or_null(#data, #dataOffset))")
    public static def glTexImage2D[T] (target:Int, level:Int, xOff:Int, yOff:Int, w:Int, h:Int, border:Int, fmt:Int, data:Rail[T], dataOffset:Int) : void { }
    public static def glMapBuffer[T] (target:Int, value:Int, len:Int) : Rail[T] {
        @Native("c++", "TPMGL(T) *tmp = (TPMGL(T)*)::glMapBuffer(target, value);") { }
// FIXME raytracer#GLFrontend.x10 needs this
        @Native("c++", "return typename ::x10::lang::Rail<TPMGL(T)>::Rail(tmp, tmp, len);") { }
        return Zero.get[Rail[T]]();
    }
    @Native("c++", "::glUnmapBuffer(#target)")
    public static def glUnmapBuffer (target:Int) : void { }
    @Native("c++", "GL_ARRAY_BUFFER")
    public static GL_ARRAY_BUFFER : Int = 0n;
    @Native("c++", "GL_DYNAMIC_DRAW")
    public static GL_DYNAMIC_DRAW : Int = 0n;
    @Native("c++", "GL_BUFFER_SIZE")
    public static GL_BUFFER_SIZE : Int = 0n;
    @Native("c++", "GL_COLOR_BUFFER_BIT")
    public static GL_COLOR_BUFFER_BIT : Int = 0n;
    @Native("c++", "GL_POINT_SMOOTH")
    public static GL_POINT_SMOOTH : Int = 0n;
    @Native("c++", "GL_BLEND")
    public static GL_BLEND : Int = 0n;
    @Native("c++", "GL_SRC_ALPHA")
    public static GL_SRC_ALPHA : Int = 0n;
    @Native("c++", "GL_ONE")
    public static GL_ONE: Int = 0n;
    @Native("c++", "GL_ONE_MINUS_SRC_ALPHA")
    public static GL_ONE_MINUS_SRC_ALPHA : Int = 0n;
    @Native("c++", "GL_DEPTH_TEST")
    public static GL_DEPTH_TEST : Int = 0n;
    @Native("c++", "GL_CULL_FACE")
    public static GL_CULL_FACE : Int = 0n;
    @Native("c++", "GL_FLOAT")
    public static GL_FLOAT : Int = 0n;
    @Native("c++", "GL_POINTS")
    public static GL_POINTS : Int = 0n;
    @Native("c++", "GL_QUADS")
    public static GL_QUADS : Int = 0n;
    @Native("c++", "GL_VERTEX_ARRAY")
    public static GL_VERTEX_ARRAY : Int = 0n;
    @Native("c++", "GL_TEXTURE_COORD_ARRAY")
    public static GL_TEXTURE_COORD_ARRAY : Int = 0n;
    @Native("c++", "GL_TEXTURE_2D")
    public static GL_TEXTURE_2D : Int = 0n;
    @Native("c++", "GL_PROJECTION")
    public static GL_PROJECTION : Int = 0n;
    @Native("c++", "GL_MODELVIEW")
    public static GL_MODELVIEW : Int = 0n;
    @Native("c++", "GL_RGB")
    public static GL_RGB : Int = 0n;
    @Native("c++", "GL_RGB8")
    public static GL_RGB8 : Int = 0n;
    @Native("c++", "GL_UNSIGNED_BYTE")
    public static GL_UNSIGNED_BYTE : Int = 0n;
    @Native("c++", "GL_PIXEL_UNPACK_BUFFER")
    public static GL_PIXEL_UNPACK_BUFFER : Int = 0n;
    @Native("c++", "GL_STREAM_DRAW")
    public static GL_STREAM_DRAW : Int = 0n;
    @Native("c++", "GL_WRITE_ONLY")
    public static GL_WRITE_ONLY : Int = 0n;
    @Native("c++", "GL_TEXTURE_MIN_FILTER")
    public static GL_TEXTURE_MIN_FILTER : Int = 0n;
    @Native("c++", "GL_TEXTURE_MAG_FILTER")
    public static GL_TEXTURE_MAG_FILTER : Int = 0n;
    @Native("c++", "GL_NEAREST")
    public static GL_NEAREST : Int = 0n;
    @Native("c++", "GL_LINEAR")
    public static GL_LINEAR : Int = 0n;

}

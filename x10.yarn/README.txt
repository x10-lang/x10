Launching under yarn is still very much in development.  Currently only Managed X10 works.
The yarn launcher copies your program's jar files to a yarn cluster, and runs the program
there.  Output for each place is currently sent to logfiles in the yarn filesystem.

To compile, you must have Hadoop 2.5.0+ installed and configured. The classes under the 
x10.yarn project must be compiled against your hadoop jars, into a new jar file locally, 
such as "x10yarn.jar".

Your program must be compiled into a jar file (e.g. "x10c -o HelloWholeWorld.jar HelloWholeWorld.x10").

You must have the hadoop command in your path.

Running your program is similar to running any managed X10 program, but you must specify 
"-x10rt yarn", and add your e.g. x10yarn.jar and HelloWholeWorld.jar jar files to the classpath:
"X10_NPLACES=2 x10 -x10rt yarn -classpath HelloWholeWorld.jar:x10yarn.jar HelloWholeWorld arg1"

If you have not set up your hadoop configuration files to point to a real server, such as if you
simply downloaded and extracted the hadoop binary distribution, you can add the needed configuration 
settings to your command line via -D arguments to specify where to find the hadoop server.  
For example, "x10 -Dyarn.resourcemanager.hostname=yourhost.yourdomain.com -x10rt yarn ...."
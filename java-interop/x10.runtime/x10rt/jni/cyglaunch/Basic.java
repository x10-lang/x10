import x10.x10rt.X10RT;
import x10.core.fun.*;
import x10.rtt.*;
import java.io.*;

public class Basic {

    private static final short _RunnableWithBuf_id = x10.x10rt.DeserializationDispatcher.addDispatcher( x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, RunnableWithBuf.class);
    private static final short _Handler_id = x10.x10rt.DeserializationDispatcher.addDispatcher( x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_SIMPLE_ASYNC, Handler.class);

    private static class RunnableWithBuf implements Serializable, x10.x10rt.X10JavaSerializable {
        public static final int Ping = 1;
        public static final int Pong = 2;
        public static final int Quit = 3;
        private int id;
        private RunnableWithBuf() { }
        public RunnableWithBuf(int id) {
            this.id = id;
        }
        public void run(byte[] buf) {
            switch (id) {
                case Ping: Basic.recv_msg_ping(buf); break;
                case Pong: Basic.recv_msg_pong(buf); break;
                case Quit: Basic.recv_quit(buf); break;
            }
        }
        public static RunnableWithBuf $_deserialize_body(RunnableWithBuf _obj, x10.x10rt.X10JavaDeserializer deserializer) throws java.io.IOException {
            _obj.id = deserializer.readInt();
            return _obj;
        }
        public static RunnableWithBuf $_deserializer(x10.x10rt.X10JavaDeserializer deserializer) throws java.io.IOException {
            RunnableWithBuf _obj = new RunnableWithBuf();
            deserializer.record_reference(_obj);
            return $_deserialize_body(_obj, deserializer);
        }
        public short $_get_serialization_id() { return _RunnableWithBuf_id; }
        public void $_serialize(x10.x10rt.X10JavaSerializer serializer) throws java.io.IOException {
            serializer.write(this.id);
        }
    }
    static RunnableWithBuf Ping, Pong, Quit;
    static {
        Ping = new RunnableWithBuf(RunnableWithBuf.Ping);
        Pong = new RunnableWithBuf(RunnableWithBuf.Pong);
        Quit = new RunnableWithBuf(RunnableWithBuf.Quit);
    }

    byte[] buf;
    int len = 1024;
    boolean validate = false;
    static int pongs_outstanding = 0;
    static boolean finished = false;

    static void recv_msg_ping(byte[] buf) {
        //System.err.println(X10RT.here()+": Received a ping message");
        sendMsg(0, Pong, buf, buf.length);
    }

    static void recv_msg_pong(byte[] buf) {
        //System.err.println(X10RT.here()+": Received a pong message");
        pongs_outstanding--;
    }

    static void recv_quit(byte[] buf) {
        //System.err.println(X10RT.here()+": Received a quit message");
        finished = true;
    }

    public void show_help(java.io.PrintStream out, String name) {
        if (X10RT.here() != 0) return;
        out.println("Usage: " + name + " <args>");
        out.print  ("-h (--help)        ");
        out.println("this message");
        out.print  ("-l (--length) <n>      ");
        out.println("size of individual message");
        out.print  ("-w (--window) <n>       ");
        out.println("number of pongs to wait for in parallel (window size)");
        out.print  ("-i (--iterations) <n>  ");
        out.println("top-level iterations (round trips)");
        out.print  ("-v (--validate)    ");
        out.println("check whether messages are mangled in transit");
        out.print  ("-p (--put)    ");
        out.println("use x10rt_send_put instead of x10rt_send_msg");
        out.print  ("-g (--get)    ");
        out.println("use x10rt_send_get instead of x10rt_send_msg");
        out.print  ("-a (--auto)    ");
        out.println("test a variety of --length and --window");
    }
    long run_test(int iters, int window, int len) {
        return run_test(iters, window, len, false);
    }
    long run_test(int iters, int window, int len, boolean put) {
        return run_test(iters, window, len, put, false);
    }
    long run_test(int iters, int window, int len, boolean put, boolean get) {
        long nanos = -System.nanoTime();
        for (int i = 0; i < iters; ++i) {
            for (int j = 0; j < window; ++j) {
                for (int k = 1; k < X10RT.numPlaces(); ++k) {
                    sendMsg(k, Ping, buf, len);
                    pongs_outstanding++;
                }
            }
            while (pongs_outstanding > 0) X10RT.probe();
        }
        nanos += System.nanoTime();
        return nanos;
    }
    public void run(String[] argv) {
        if (X10RT.numPlaces()==1) {
            System.err.println("This is a communications test so needs at least 2 hosts.");
            System.exit(EXIT_FAILURE);
        }

        int iterations = 32;
        int window = 100;
        boolean put = false;
        boolean get = false;
        boolean automatic = false;

        for (int i = 0; i < argv.length; ++i) {
            if (!strcmp(argv[i], "--help")) {
                show_help(System.out, getClass().getName());
                System.exit(EXIT_SUCCESS);
            } else if (!strcmp(argv[i], "-h")) {
                show_help(System.err, getClass().getName());
                System.exit(EXIT_SUCCESS);

            } else if (!strcmp(argv[i], "--length")) {
                len = strtoul(argv[++i]);
            } else if (!strcmp(argv[i], "-l")) {
                len = strtoul(argv[++i]);

            } else if (!strcmp(argv[i], "--window")) {
                window = strtoul(argv[++i]);
            } else if (!strcmp(argv[i], "-w")) {
                window = strtoul(argv[++i]);

            } else if (!strcmp(argv[i], "--iterations")) {
                iterations = strtoul(argv[++i]);
            } else if (!strcmp(argv[i], "-i")) {
                iterations = strtoul(argv[++i]);

            } else if (!strcmp(argv[i], "--validate")) {
                validate = true;
            } else if (!strcmp(argv[i], "-v")) {
                validate = true;

            } else if (!strcmp(argv[i], "--auto")) {
                automatic = true;
            } else if (!strcmp(argv[i], "-a")) {
                automatic = true;

            } else if (!strcmp(argv[i], "--put")) {
                put = true;
            } else if (!strcmp(argv[i], "-p")) {
                put = true;

            } else if (!strcmp(argv[i], "--get")) {
                get = true;
            } else if (!strcmp(argv[i], "-g")) {
                get = true;

            } else {
                if (X10RT.here() == 0) {
                    System.err.println("Didn't understand: \"" + argv[i] + "\"");
                    show_help(System.err, getClass().getName());
                }
                System.exit(EXIT_FAILURE);
            }
        }

        if (put || get) {
            if (X10RT.here() == 0) {
                System.err.println("Put and get are not supported.");
            }
            System.exit(EXIT_FAILURE);
        }

        if (put && get) {
            if (X10RT.here() == 0) {
                System.err.println("You can't specify both put and get.");
                show_help(System.err, getClass().getName());
            }
            System.exit(EXIT_FAILURE);
        }

        {
            int sz = len < 1024*1024 ? 1024*1024 : len;
            buf = new byte[sz];
            for (int i = 0; i < sz; ++i) {
                buf[i] = (byte) (i & 0xFF);
            }
        }

        if (X10RT.here() == 0) {
            // warm up
            for (int i=0; i < 16; ++i) {
                run_test(1, 1, 1024, false, false);
                run_test(1, 1, 1024, true, false);
                run_test(1, 1, 1024, true, true);
            }
        }

        if (X10RT.here() == 0) {

            if (automatic) {
                System.out.print("         ");
                for (int j = 1; j <= 16; ++j) {
                    System.out.printf("%5d", j);
                }
                System.out.println(" b/w (MB)");
                for (int l = 0; l<512*1024; l=(l*2)>0?(l*2):1) {
                    System.out.printf("%8d", l);
                    double micros = 0;
                    for (int j = 1; j <= 16; ++j) {
                        micros = run_test(iterations/j, j, l, put, get)
                            / 1E3 / (iterations/j*j) / 2 / (X10RT.numPlaces() - 1);
                        System.out.printf("%6.3f ", micros);
                    }
                    System.out.printf("%.3f\n", l/micros);
                }
            } else {
                double micros = run_test(iterations, window, len, put, get)
                    / 1E3 / (iterations*window) / 2 / (X10RT.numPlaces() - 1);
                System.out.println("Half roundtrip time: " + micros + " us  Bandwidth: " + (len/micros) + " MB/s");
            }

            for (int i = 1; i < X10RT.numPlaces(); ++i) {
                sendMsg(i, Quit, null, 0);
            }
            finished = true;
        }

        while (!finished) X10RT.probe();
    }

    private static class Handler implements VoidFun_0_0 {
        public RunnableWithBuf id;
        public byte[] buf;
        private Handler() { }
        public Handler(RunnableWithBuf id, byte[] buf, int len) {
            this.id = id;
            byte[] newbuf = buf;
            if (len > 0 && buf.length != len) {
                newbuf = new byte[len];
                System.arraycopy(buf, 0, newbuf, 0, len);
            }
            this.buf = newbuf;
        }
        public void $apply() { id.run(buf); }
        public RuntimeType<?> $getRTT() { return x10.core.Any.$RTT; }
        public Type<?> $getParam(int i) { throw new IllegalArgumentException(); }
        public static Handler $_deserialize_body(Handler _obj, x10.x10rt.X10JavaDeserializer deserializer) throws java.io.IOException {
            _obj.id = (RunnableWithBuf) deserializer.readRef();
            _obj.buf = deserializer.readByteArray();
            return _obj;
        }
        public static Handler $_deserializer(x10.x10rt.X10JavaDeserializer deserializer) throws java.io.IOException {
            Handler _obj = new Handler();
            deserializer.record_reference(_obj);
            return $_deserialize_body(_obj, deserializer);
        }
        public short $_get_serialization_id() { return _Handler_id; }
        public void $_serialize(x10.x10rt.X10JavaSerializer serializer) throws java.io.IOException {
            serializer.write((x10.x10rt.X10JavaSerializable) this.id);
            if (this.buf == null) {
                serializer.write((Object) this.buf);
            } else {
                serializer.write(this.buf);
            }
        }
    }
    // serialization helper
    public static void sendMsg(int place, RunnableWithBuf runner, byte[] buf, int len) {
        try {
            VoidFun_0_0 body = new Handler(runner, buf, len);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            if (x10.runtime.impl.java.Runtime.CUSTOM_JAVA_SERIALIZATION) {
                DataOutputStream objStream = new DataOutputStream(baos); // TODO: use Runtime.serialize()
                x10.x10rt.X10JavaSerializer serializer = new x10.x10rt.X10JavaSerializer(objStream);
                serializer.write((x10.x10rt.X10JavaSerializable) body);
            } else {
                (new java.io.ObjectOutputStream(baos)).writeObject(body);
            }
            byte[] msg = baos.toByteArray();
            int msgLen = baos.size();
            //System.err.println(X10RT.here()+": About to send a message to place "+place);
            x10.x10rt.MessageHandlers.runClosureAtSend(place, msgLen, msg, x10.x10rt.DeserializationDispatcher.getMessageID(_Handler_id));
            //System.err.println(X10RT.here()+": Sent a message to place "+place);
        } catch (java.io.IOException e){
            e.printStackTrace();
        }
    }

    // stdlib simulation stuff
    public static final int EXIT_SUCCESS = 1;
    public static final int EXIT_FAILURE = 1;
    public static boolean strcmp(String a, String b) {
        return !a.equals(b);
    }
    public static int strtoul(String s) {
        return Integer.parseInt(s);
    }

    public static void main(String[] argv) {
        int n = X10RT.numPlaces();
        int i = X10RT.here();
        System.out.println("There are "+n+" Nodes and I am Node "+i);
        new Basic().run(argv);
    }
} 
// vim:tabstop=4:shiftwidth=4:expandtab

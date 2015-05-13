/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2015
 */

package apgas.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import x10.network.NetworkTransportCallbacks;
import apgas.Configuration;

/**
 * The {@link SocketTransport} class manages the Hazelcast cluster and
 * implements active messages via direct socket links.
 */
public class SocketTransport extends Transport implements
    NetworkTransportCallbacks {

  private final x10.network.SocketTransport localTransport;
  private final ExecutorService immediateThreads;
  private boolean running = true;
  private final GlobalRuntimeImpl runtime;
  private final boolean useSnappy;

  // private final Kryo kryo;

  /**
   * @see Transport
   */
  SocketTransport(GlobalRuntimeImpl runtime, String master, String localhost,
      boolean compact) {
    super(runtime, master == null ? null : master.split(",")[0], localhost,
        compact);
    this.runtime = runtime;
    this.useSnappy = System.getProperty(
        Configuration.APGAS_NETWORKTRANSPORT_COMPRESSION, "snappy").equals(
        "snappy");
    localTransport = new x10.network.SocketTransport(this);
    if (master == null) {
      localTransport.establishLinks(0, null); // place 0
      runPlaceAddedHandler(0);
    } else {
      localTransport.establishLinks(master.split(",")[1]); // other place
      final int livePlaces = localTransport.x10rt_nplaces()
          - localTransport.numDead();
      final ArrayList<Integer> added = new ArrayList<Integer>(livePlaces);
      for (int i = 0; i < livePlaces; i++) {
        added.add(i);
      }
      runtime.updatePlaces(added, new ArrayList<Integer>(0));
    }
    immediateThreads = Executors.newCachedThreadPool();
    /*
     * kryo = new Kryo(); kryo.register(DeadPlaceError.class);
     * kryo.register(DefaultFinish.class, new JavaSerializer());
     * kryo.register(Finish.class); kryo.register(GlobalRuntimeImpl.class);
     * kryo.register(NullFinish.class); kryo.register(ResilientFinish.class);
     * kryo.register(ResilientFinishOpt.class);
     * kryo.register(ResilientFinishState.class);
     * kryo.register(SerializableRunnable.class);
     * kryo.register(SerializableThrowable.class); kryo.register(Task.class, new
     * JavaSerializer()); kryo.register(UncountedTask.class);
     */
  }

  @Override
  synchronized void start() {
    // super.start();
    immediateThreads.execute(new Runnable() {
      @Override
      public void run() {
        while (running) {
          final x10.network.SocketTransport.Message message = localTransport
              .x10rt_probe(x10.network.SocketTransport.PROBE_TYPE.ALL, true);
          if (message != null && message.callbackId != -1) {
            byte[] data;
            if (message.data.hasArray()) {
              data = message.data.array();
            } else {
              data = new byte[message.data.remaining()];
              message.data.get(data);
            }
            /*
             * final Input input = new Input(data); final SerializableRunnable f
             * = (SerializableRunnable) kryo .readClassAndObject(input);
             */

            try {
              if (useSnappy) {
                data = org.xerial.snappy.Snappy.uncompress(data);
              }
              final ObjectInputStream ois = new ObjectInputStream(
                  new ByteArrayInputStream(data));
              final SerializableRunnable f = (SerializableRunnable) ois
                  .readObject();
              immediateThreads.submit(f);
            } catch (final Exception e) {
              e.printStackTrace();
            }
          }
        }
      }
    });
  }

  @Override
  String getAddress() {
    return super.getAddress() + ',' + localTransport.getLocalConnectionInfo();
  }

  @Override
  int maxPlace() {
    return localTransport.x10rt_nplaces();
  }

  @Override
  int here() {
    return localTransport.x10rt_here();
  }

  @Override
  void send(int place, SerializableRunnable f) {
    if (place == here()) {
      f.run();
    } else {
      try {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(f);
        byte[] data = baos.toByteArray();
        if (useSnappy) {
          data = org.xerial.snappy.Snappy.compress(baos.toByteArray());
        }
        localTransport.sendMessage(place, 0, data);
      } catch (final IOException e) {
        e.printStackTrace();
      }
      /*
       * final long freeMem = Runtime.getRuntime().freeMemory(); final Output
       * output = new Output(4096, freeMem < Integer.MAX_VALUE ? (int) freeMem :
       * Integer.MAX_VALUE); kryo.writeClassAndObject(output, f); //
       * org.xerial.snappy.Snappy.compress(output.getBuffer(), arg1);
       * localTransport.sendMessage(place, 0, output.getBuffer());
       */
    }
  }

  @Override
  synchronized void shutdown() {
    // super.shutdown();
    running = false;
    localTransport.shutdown();
    immediateThreads.shutdown();
    localTransport.wakeup();
    hazelcast.shutdown();
  }

  /**
   * Called by the sockets transport. Forwards place updates to the Global
   * Runtime
   *
   * @param placeId
   *          the place added
   */
  @Override
  public void runPlaceAddedHandler(int placeId) {
    final ArrayList<Integer> added = new ArrayList<Integer>(1);
    added.add(placeId);
    runtime.updatePlaces(added, new ArrayList<Integer>(0));
  }

  /**
   * Called by the sockets transport. Forwards place updates to the Global
   * Runtime
   *
   * @param placeId
   *          the place added
   */
  @Override
  public void runPlaceRemovedHandler(int placeId) {
    final ArrayList<Integer> removed = new ArrayList<Integer>(1);
    removed.add(placeId);
    runtime.updatePlaces(new ArrayList<Integer>(0), removed);
  }

  @Override
  public void initDataStore(String connectTo) {
    // Not used in apgas. Hazelcast is initialized independently, not over this
    // link
  }

  @Override
  public long getEpoch() {
    // Epoch does not exist in apgas
    return -1;
  }

  @Override
  public void setEpoch(long epoch) {
    // Epoch does not exist in apgas
  }
}

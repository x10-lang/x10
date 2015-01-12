/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

package apgas.impl;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import apgas.NoSuchPlaceException;

import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IExecutorService;
import com.hazelcast.core.IMap;
import com.hazelcast.core.InitialMembershipEvent;
import com.hazelcast.core.InitialMembershipListener;
import com.hazelcast.core.Member;
import com.hazelcast.core.MemberAttributeEvent;
import com.hazelcast.core.MembershipEvent;

/**
 * The {@link Transport} class manages the Hazelcast cluster and implements
 * active messages.
 */
final class Transport implements InitialMembershipListener {
  private static String HERE = "_APGAS_HERE_";
  private static String PLACES = "_APGAS_PLACES_";
  private static String EXECUTOR = "_APGAS_EXECUTOR_";

  /**
   * The Hazelcast instance for this JVM.
   */
  private final HazelcastInstance hazelcast;

  /**
   * The place ID for this JVM.
   */
  private final int here;

  /**
   * The first unused place ID.
   */
  private int places;

  /**
   * The current members indexed by place ID.
   */
  private final Map<Integer, Member> members = new ConcurrentHashMap<Integer, Member>();

  /**
   * The local member.
   */
  private final Member me;

  /**
   * Registration ID.
   */
  private String regMembershipListener;

  /**
   * Executor service for sending active messages.
   */
  private final IExecutorService executor;

  /**
   * The global runtime instance to notify of new and dead places.
   */
  private final GlobalRuntimeImpl runtime;

  /**
   * Initializes the {@link HazelcastInstance} for this global runtime instance.
   *
   * @param runtime
   *          the global runtime instance
   * @param master
   *          member to connect to or null
   * @param localhost
   *          the preferred ip address of this host
   */
  Transport(GlobalRuntimeImpl runtime, String master, String localhost) {
    this.runtime = runtime;
    // config
    final Config config = new Config();
    config.setProperty("hazelcast.logging.type", "none");
    config.setProperty("hazelcast.wait.seconds.before.join", "0");

    // join config
    final JoinConfig join = config.getNetworkConfig().getJoin();
    join.getMulticastConfig().setEnabled(false);
    join.getTcpIpConfig().setEnabled(true);
    System.setProperty("hazelcast.local.localAddress", localhost);
    if (master != null) {
      join.getTcpIpConfig().addMember(master);
      // also replace localhost will real ip as master is likely to expect this
      if (master.startsWith("127.0.0.1") || master.startsWith("localhost")) {
        join.getTcpIpConfig().addMember(
            master.replaceFirst("127.0.0.1|localhost", localhost));
      }
    }

    hazelcast = Hazelcast.newHazelcastInstance(config);

    executor = hazelcast.getExecutorService(EXECUTOR);
    here = (int) hazelcast.getAtomicLong(PLACES).getAndIncrement();
    me = hazelcast.getCluster().getLocalMember();
    places = here + 1;
  }

  /**
   * Starts monitoring cluster membership events.
   */
  void start() {
    me.setIntAttribute(HERE, here);
    regMembershipListener = hazelcast.getCluster().addMembershipListener(this);
  }

  /**
   * Returns the distributed map instance with the given name.
   *
   * @param <K>
   *          key type
   * @param <V>
   *          value type
   * @param name
   *          map name
   * @return the map
   */
  <K, V> IMap<K, V> getMap(String name) {
    return hazelcast.<K, V> getMap(name);
  }

  /**
   * Returns the socket address of this Hazelcast instance.
   *
   * @return an address in the form "ip:port"
   */
  String getAddress() {
    final InetSocketAddress address = me.getSocketAddress();
    return address.getAddress().getHostAddress() + ":" + address.getPort();
  }

  /**
   * Shuts down this Hazelcast instance.
   */
  void shutdown() {
    hazelcast.getCluster().removeMembershipListener(regMembershipListener);
    hazelcast.shutdown();
  }

  /**
   * Terminates this Hazelcast instance forcefully.
   */
  void terminate() {
    hazelcast.getLifecycleService().terminate();
  }

  /**
   * Returns the first unused place ID.
   *
   * @return a place ID.
   */
  int places() {
    return places;
  }

  /**
   * Returns the current place ID.
   *
   * @return the place ID of this Hazelcast instance
   */
  int here() {
    return here;
  }

  /**
   * Executes a function at the given place.
   *
   * @param place
   *          the requested place of execution
   * @param f
   *          the function to execute
   * @throws NoSuchPlaceException
   *           if the cluster does not contain this place
   */
  void send(int place, SerializableRunnable f) {
    if (place == here) {
      f.run();
    } else {
      final Member member = members.get(place);
      if (member == null) {
        throw new NoSuchPlaceException();
      }
      executor.executeOnMember(f, member);
    }
  }

  @Override
  public void init(InitialMembershipEvent event) {
    for (final Member member : event.getMembers()) {
      final Integer place = member.getIntAttribute(HERE);
      if (place != null) {
        // ignore members that have not yet specified their place ID
        if (place >= places) {
          places = place + 1;
        }
        members.put(place, member);
      }
    }
    runtime.initPlaces(members.keySet());
  }

  @Override
  public void memberAdded(MembershipEvent membershipEvent) {
    // ignored since we wait for the memberAttributeEvent to get the place ID
  }

  @Override
  public void memberRemoved(MembershipEvent membershipEvent) {
    final Member member = membershipEvent.getMember();
    final Integer place = member.getIntAttribute(HERE);
    if (place != null) {
      // System.err.println(here + " observing the removal of " + place);
      members.remove(place);
      runtime.removePlace(place);
    }
  }

  @Override
  public void memberAttributeChanged(MemberAttributeEvent memberAttributeEvent) {
    if (!memberAttributeEvent.getKey().equals(HERE)) {
      return;
    }
    final Member member = memberAttributeEvent.getMember();
    final int place = (int) memberAttributeEvent.getValue();
    if (place >= places) {
      places = place + 1;
    }
    // System.err.println(here + " observing the arrival of " + place);
    members.put(place, member);
    runtime.addPlace(place);
  }
}

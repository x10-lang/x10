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

package apgas.impl;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import apgas.DeadPlaceException;

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
 * The {@link Transport} class implements the global runtime by means of an
 * Hazelcast cluster.
 * <p>
 * It implements active messages on top of a distributed executor service.
 */
final class Transport implements InitialMembershipListener {
  /**
   * The hazelcast instance for this JVM.
   */
  private final HazelcastInstance hazelcast;

  /**
   * The place ID for this JVM.
   */
  private final int here;

  /**
   * Sparse list of members in join order.
   */
  private final Map<Integer, Member> members = new ConcurrentHashMap<Integer, Member>();

  /**
   * The local member.
   */
  private final Member me;

  /**
   * Registration ID.
   */
  private final String regMembershipListener;

  /**
   * Executor service for sending active messages.
   */
  private final IExecutorService executor;

  private final GlobalRuntimeImpl runtime;

  /**
   * Initializes the {@link HazelcastInstance} for this global runtime instance.
   *
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
    executor = hazelcast.getExecutorService("APGAS");

    here = (int) hazelcast.getAtomicLong("APGAS").getAndIncrement();
    me = hazelcast.getCluster().getLocalMember();
    me.setIntAttribute("APGAS", here);
    regMembershipListener = hazelcast.getCluster().addMembershipListener(this);

    // topic = hazelcast.getTopic("APGAS" + here);
    // regTopic = topic.addMessageListener(this);
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
   * Returns the socket address of this {@link Hazelcast} instance.
   *
   * @return an address in the form "ip:port"
   */
  String getAddress() {
    final InetSocketAddress address = me.getSocketAddress();
    return address.getAddress().getHostAddress() + ":" + address.getPort();
  }

  /**
   * Shuts down this hazelcast instance.
   */
  void shutdown() {
    hazelcast.getCluster().removeMembershipListener(regMembershipListener);
    // topic.removeMessageListener(regTopic);
    hazelcast.shutdown();
  }

  /**
   * Terminates this hazelcast instance forcefully.
   */
  void terminate() {
    hazelcast.getLifecycleService().terminate();
  }

  /**
   * Returns the number of live and dead places in the global runtime.
   *
   * @return the number of Hazelcast instances that have joined the Hazelcast
   *         cluster
   */
  int places() {
    return members.size();
  }

  /**
   * Returns the current place ID.
   *
   * @return the ID of this Hazelcast instance in the Hazelcast cluster.
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
   */
  void send(int place, SerializableRunnable f) {
    if (place == here) {
      f.run();
    } else {
      final Member member = members.get(place);
      if (member == null) {
        throw new DeadPlaceException();
      }
      executor.executeOnMember(f, member);
    }
  }

  @Override
  public void init(InitialMembershipEvent event) {
    for (final Member member : event.getMembers()) {
      final Integer place = member.getIntAttribute("APGAS");
      if (place != null) {
        // ignore members that have not yet specified their place ID
        members.put(place, member);
        runtime.addPlace(place);
      }
    }
  }

  @Override
  public void memberAdded(MembershipEvent membershipEvent) {
    // ignored since we wait for the memberAttributeEvent to get the place ID
  }

  @Override
  public void memberRemoved(MembershipEvent membershipEvent) {
    final Member member = membershipEvent.getMember();
    final Integer place = member.getIntAttribute("APGAS");
    if (place != null) {
      System.err.println(here + " observing the removal of " + place);
      members.remove(place);
      runtime.removePlace(place);
    }
  }

  @Override
  public void memberAttributeChanged(MemberAttributeEvent memberAttributeEvent) {
    final Member member = memberAttributeEvent.getMember();
    if (!memberAttributeEvent.getKey().equals("APGAS")) {
      return;
    }
    final int place = (int) memberAttributeEvent.getValue();
    System.err.println(here + " observing the arrival of " + place);
    members.put(place, member);
    runtime.addPlace(place);
  }
}

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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import apgas.DeadPlaceException;
import apgas.Place;

import com.hazelcast.config.Config;
import com.hazelcast.config.ExecutorConfig;
import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.MapConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IExecutorService;
import com.hazelcast.core.IList;
import com.hazelcast.core.IMap;
import com.hazelcast.core.InitialMembershipEvent;
import com.hazelcast.core.InitialMembershipListener;
import com.hazelcast.core.ItemEvent;
import com.hazelcast.core.Member;
import com.hazelcast.core.MemberAttributeEvent;
import com.hazelcast.core.MembershipEvent;
import com.hazelcast.spi.ExecutionService;

/**
 * The {@link Transport} class manages the Hazelcast cluster and implements
 * active messages.
 */
public class Transport implements com.hazelcast.core.ItemListener<Member>,
    InitialMembershipListener {
  private static String APGAS = "apgas";
  private static String APGAS_PLACES = "apgas:places";
  private static String APGAS_EXECUTOR = "apgas:executor";
  private static String APGAS_FINISH = "apgas:finish";

  /**
   * The Hazelcast instance for this JVM.
   */
  protected final HazelcastInstance hazelcast;

  /**
   * The place ID for this JVM.
   */
  private final int here;

  /**
   * The first unused place ID.
   */
  private int maxPlace;

  /**
   * The current members indexed by place ID.
   */
  private final Map<Integer, Member> map = new ConcurrentHashMap<Integer, Member>();

  /**
   * Past and present members indexed by place ID.
   */
  private final IList<Member> allMembers;

  /**
   * Current members.
   */
  private Set<Member> currentMembers;

  /**
   * The local member.
   */
  private final Member me;

  /**
   * Registration ID.
   */
  private String regMembershipListener;

  /**
   * Registration ID.
   */
  private String regItemListener;
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
   * @param compact
   *          reduces thread creation if set
   */
  protected Transport(GlobalRuntimeImpl runtime, String master,
      String localhost, boolean compact) {
    this.runtime = runtime;
    // config
    final Config config = new Config();
    config.setProperty("hazelcast.logging.type", "none");
    config.setProperty("hazelcast.wait.seconds.before.join", "0");
    if (compact) {
      config.setProperty("hazelcast.operation.thread.count", "2");
      config.setProperty("hazelcast.operation.generic.thread.count", "2");
      config.setProperty("hazelcast.io.thread.count", "2");
      config.setProperty("hazelcast.event.thread.count", "2");
      config.addExecutorConfig(new ExecutorConfig(
          ExecutionService.ASYNC_EXECUTOR, 2));
      config.addExecutorConfig(new ExecutorConfig(
          ExecutionService.SYSTEM_EXECUTOR, 2));
      config.addExecutorConfig(new ExecutorConfig(
          ExecutionService.SCHEDULED_EXECUTOR, 2));
    }

    config.addMapConfig(new MapConfig(APGAS_FINISH)
        .setInMemoryFormat(InMemoryFormat.OBJECT));

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
    config.setInstanceName(APGAS);

    hazelcast = Hazelcast.newHazelcastInstance(config);
    me = hazelcast.getCluster().getLocalMember();

    allMembers = hazelcast.getList(APGAS_PLACES);
    allMembers.add(me);
    int id = 0;
    for (final Member member : allMembers) {
      if (member.getUuid().equals(me.getUuid())) {
        break;
      }
      ++id;
    }
    here = id;

    executor = hazelcast.getExecutorService(APGAS_EXECUTOR);
  }

  /**
   * Starts monitoring cluster membership events.
   */
  protected synchronized void start() {
    regItemListener = allMembers.addItemListener(this, false);
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
   * Returns the distributed map instance implementing resilient finish.
   *
   * @param <K>
   *          key type
   * @param <V>
   *          value type
   * @return the map
   */
  <K, V> IMap<K, V> getResilientFinishMap() {
    return hazelcast.<K, V> getMap(APGAS_FINISH);
  }

  /**
   * Returns the socket address of this Hazelcast instance.
   *
   * @return an address in the form "ip:port"
   */
  protected String getAddress() {
    final InetSocketAddress address = me.getSocketAddress();
    return address.getAddress().getHostAddress() + ":" + address.getPort();
  }

  /**
   * Shuts down this Hazelcast instance.
   */
  protected synchronized void shutdown() {
    hazelcast.getCluster().removeMembershipListener(regMembershipListener);
    allMembers.removeItemListener(regItemListener);
    hazelcast.shutdown();
  }

  /**
   * Returns the first unused place ID.
   *
   * @return a place ID.
   */
  protected int maxPlace() {
    return maxPlace;
  }

  /**
   * Returns the current place ID.
   *
   * @return the place ID of this Hazelcast instance
   */
  protected int here() {
    return here;
  }

  /**
   * Executes a function at the given place.
   *
   * @param place
   *          the requested place of execution
   * @param f
   *          the function to execute
   * @throws DeadPlaceException
   *           if the cluster does not contain this place
   */
  protected void send(int place, SerializableRunnable f) {
    if (place == here) {
      f.run();
    } else {
      final Member member = map.get(place);
      if (member == null) {
        throw new DeadPlaceException(new Place(place));
      }
      executor.executeOnMember(f, member);
    }
  }

  private boolean live(String uuid) {
    for (final Member member : currentMembers) {
      if (uuid.equals(member.getUuid())) {
        return true;
      }
    }
    return false;
  }

  private synchronized void updatePlaces() {
    if (currentMembers == null) {
      return;
    }
    final Iterator<Member> it = allMembers.iterator();
    final ArrayList<Integer> added = new ArrayList<Integer>();
    final ArrayList<Integer> removed = new ArrayList<Integer>();
    int place = 0;
    while (it.hasNext()) {
      final Member member = it.next();
      if (live(member.getUuid())) {
        if (!map.containsKey(place)) {
          added.add(place);
          map.put(place, member);
        }
      } else {
        if (map.containsKey(place)) {
          removed.add(place);
          map.remove(place);
        }
      }
      ++place;
    }
    if (place > maxPlace) {
      maxPlace = place;
    }
    runtime.updatePlaces(added, removed);
  }

  @Override
  synchronized public void init(InitialMembershipEvent event) {
    currentMembers = event.getMembers();
    updatePlaces();
  }

  @Override
  synchronized public void memberAdded(MembershipEvent membershipEvent) {
    currentMembers = membershipEvent.getMembers();
    updatePlaces();
  }

  @Override
  synchronized public void memberRemoved(MembershipEvent membershipEvent) {
    currentMembers = membershipEvent.getMembers();
    updatePlaces();

  }

  @Override
  synchronized public void memberAttributeChanged(
      MemberAttributeEvent memberAttributeEvent) {
    // unused
  }

  @Override
  synchronized public void itemAdded(ItemEvent<Member> item) {
    updatePlaces();
  }

  @Override
  synchronized public void itemRemoved(ItemEvent<Member> item) {
    // unused
  }
}

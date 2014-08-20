package apgas.impl;

import java.net.InetSocketAddress;

import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IExecutorService;
import com.hazelcast.core.IList;
import com.hazelcast.core.ItemEvent;
import com.hazelcast.core.ItemListener;
import com.hazelcast.core.Member;
import com.hazelcast.core.MemberAttributeEvent;
import com.hazelcast.core.MembershipEvent;
import com.hazelcast.core.MembershipListener;
import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;

/**
 * The {@link HazelcastTransport} class implements the global runtime by means
 * of an Hazelcast cluster.
 * <p>
 * It implements active messages on top of a distributed executor service.
 */
final class HazelcastTransport implements ItemListener<Member>,
    MessageListener<Runnable>, MembershipListener {
  /**
   * The hazelcast instance for this JVM.
   */
  private final HazelcastInstance hazelcast;

  /**
   * The place ID for this JVM.
   */
  private final int here;

  /**
   * Current place count including dead places.
   */
  private int places;

  /**
   * List of past and present members in join order.
   */
  private final IList<Member> members;

  /**
   * Registration ID.
   */
  private final String regMembershipListener;

  /**
   * Registration ID.
   */
  private final String regItemListener;

  /**
   * Executor service for sending active messages.
   */
  private final IExecutorService executor;

  /**
   * Callback invoked when a member is removed from the cluster.
   */
  private final Runnable callback;

  // private final ITopic<VoidFun> topic;
  // private final String regTopic;

  /**
   * Initializes the {@link HazelcastInstance} for this global runtime instance.
   *
   * @param callback
   *          a function to invoke when a member is removed from the cluster.
   * @param master
   *          member to connect to or null
   * @param localhost
   *          the ip address of this host
   */
  HazelcastTransport(Runnable callback, String master, String localhost) {
    this.callback = callback;

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
    members = hazelcast.<Member> getList("APGAS");

    members.add(hazelcast.getCluster().getLocalMember());
    regItemListener = members.addItemListener(this, false);

    int here = 0;
    for (final Member m : members) {
      if (m.localMember()) {
        break;
      }
      here++;
    }
    this.here = here;
    places = members.size();

    // topic = hazelcast.getTopic("APGAS" + here);
    // regTopic = topic.addMessageListener(this);

    regMembershipListener = hazelcast.getCluster().addMembershipListener(this);
  }

  /**
   * Returns the socket address of this {@link Hazelcast} instance.
   *
   * @return an address in the form "ip:port"
   */
  String getAddress() {
    final InetSocketAddress address = hazelcast.getCluster().getLocalMember()
        .getSocketAddress();
    return address.getAddress().getHostAddress() + ":" + address.getPort();
  }

  /**
   * Shuts down this hazelcast instance.
   */
  void shutdown() {
    hazelcast.getCluster().removeMembershipListener(regMembershipListener);
    members.removeItemListener(regItemListener);
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
   * Returns the number of places in the global runtime.
   *
   * @return the number of Hazelcast instances that have joined the Hazelcast
   *         cluster
   */
  int places() {
    return places;
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
      executor.executeOnMember(f, members.get(place));
      // hazelcast.getTopic("APGAS" + place).publish(f);
    }
  }

  @Override
  public void memberAdded(MembershipEvent membershipEvent) {
    // we use itemAdded instead to keep track of past and present members
  }

  @Override
  public void memberRemoved(MembershipEvent membershipEvent) {
    callback.run();
  }

  @Override
  public void memberAttributeChanged(MemberAttributeEvent memberAttributeEvent) {
    // unused
  }

  @Override
  public void itemAdded(ItemEvent<Member> item) {
    places = members.size();
  }

  @Override
  public void itemRemoved(ItemEvent<Member> item) {
    // we never remove members from the list
  }

  @Override
  public void onMessage(Message<Runnable> message) {
    message.getMessageObject().run();
  }
}

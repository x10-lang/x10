/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2014.
 */

package x10.x10rt.yarn;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.impl.SimpleLog;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DataOutputBuffer;
import org.apache.hadoop.net.NetUtils;
import org.apache.hadoop.security.Credentials;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.security.token.Token;
import org.apache.hadoop.util.ExitUtil;
import org.apache.hadoop.yarn.api.ApplicationConstants;
import org.apache.hadoop.yarn.api.ApplicationConstants.Environment;
import org.apache.hadoop.yarn.api.protocolrecords.RegisterApplicationMasterResponse;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.api.records.Container;
import org.apache.hadoop.yarn.api.records.ContainerExitStatus;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.api.records.ContainerLaunchContext;
import org.apache.hadoop.yarn.api.records.ContainerStatus;
import org.apache.hadoop.yarn.api.records.FinalApplicationStatus;
import org.apache.hadoop.yarn.api.records.LocalResource;
import org.apache.hadoop.yarn.api.records.LocalResourceType;
import org.apache.hadoop.yarn.api.records.LocalResourceVisibility;
import org.apache.hadoop.yarn.api.records.NodeReport;
import org.apache.hadoop.yarn.api.records.Priority;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.client.api.AMRMClient.ContainerRequest;
import org.apache.hadoop.yarn.client.api.async.AMRMClientAsync;
import org.apache.hadoop.yarn.client.api.async.NMClientAsync;
import org.apache.hadoop.yarn.client.api.async.impl.NMClientAsyncImpl;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.security.AMRMTokenIdentifier;
import org.apache.hadoop.yarn.util.ConverterUtils;

public class ApplicationMaster {
	
	private static final Log LOG = new SimpleLog(ApplicationMaster.class.getName());

	// environment variables used by X10, set by the AM
	public static final String X10_NPLACES = "X10_NPLACES";
	public static final String X10_NTHREADS = "X10_NTHREADS";
	public static final String X10_LAUNCHER_PLACE = "X10_LAUNCHER_PLACE";
	public static final String X10_LAUNCHER_PARENT = "X10_LAUNCHER_PARENT";
	public static final String X10_LAUNCHER_HOST = "X10_LAUNCHER_HOST";
	public static final String X10_HDFS_JARS = "X10_HDFS_JARS";
	public static final String X10_YARN_NATIVE = "X10_YARN_NATIVE";
	public static final String X10_YARN_MAIN = "X10_YARN_MAIN";
	public static final String X10_YARNUPLOAD = "X10_YARNUPLOAD";
	public static final String X10YARNENV_ = "X10YARNENV_";
	private static final String DEAD = "DEAD";
	static enum CTRL_MSG_TYPE {HELLO, GOODBYE, PORT_REQUEST, PORT_RESPONSE, LAUNCH_REQUEST, LAUNCH_RESPONSE}; // Correspond to values in Launcher.h
	private static final int headerLength = 16;
	private static final int PORT_UNKNOWN = -1;
	private static final int PORT_DEAD = -2;
	
	private Configuration conf;
	private String appMasterHostname;
	private int appMasterPort;
	private int appMasterRpcPort;	// Port on which the app master listens for status updates from clients
	private String appMasterTrackingUrl; // Tracking url to which app master publishes info for clients to monitor
	private volatile boolean running = true; // flag for when to shut everything down
	private final String[] args;
	private final String appName;

	// information about a specific place.
	private class CommunicationLink {
		private CommunicationLink(String hostname) {
			super();
			this.hostname = hostname;
			this.sc = null;
			this.port = PORT_UNKNOWN;
			this.pendingPortRequests = null;
		}
		final String hostname;
		SocketChannel sc;
    	int port;
    	ArrayList<Integer> pendingPortRequests;
	};
	private ServerSocketChannel launcherChannel; // server socket for X10rt to communicate with
//	private int appMasterX10LauncherPort; // Port used by X10 places to communicate directly with the AM
	private final Selector selector;
	private final HashMap<Integer, CommunicationLink> links;
	private final HashMap<ContainerId, Integer> places; // map of containers to places
	private final HashMap<SocketChannel, ByteBuffer> pendingReads;
	
	private AMRMClientAsync<ContainerRequest> resourceManager; // Handle to communicate with the Resource Manager
	private NMClientAsync nodeManager; // Handle to communicate with the Node Manager
	protected ApplicationAttemptId appAttemptID; // ID assigned to us by the resource manager
	private ByteBuffer allTokens; // security and other yarn tokens
	
	private int initialNumPlaces;
	private int coresPerPlace;
	private int memoryPerPlaceInMb;
	
	// these are all atomic, since they get updated in callbacks
	// count of requests we make for new places
	private AtomicInteger numRequestedContainers = new AtomicInteger();
	// count of allocated containers we start places in.
	private AtomicInteger numAllocatedContainers = new AtomicInteger();
	// count of containers that were allocated to us but not requested (bug in yarn v2.5.1)
	private AtomicInteger numExtraContainers = new AtomicInteger();
	// count of completed containers, will eventually reach allocated+extra
	private AtomicInteger numCompletedContainers = new AtomicInteger();
	// Count of failed containers
	private AtomicInteger numFailedContainers = new AtomicInteger();
	
	public static void main(String[] args) {
		ApplicationMaster appMaster;
		try {
			 LOG.info("Initializing ApplicationMaster");
			 appMaster = new ApplicationMaster(args);
			 appMaster.setup();
			 appMaster.handleX10();
			 appMaster.shutdown();
		} catch (Exception e) {
			LOG.warn("Error caught in main", e);
			ExitUtil.terminate(1, e);
		}
		System.exit(0);
	}
	
	public ApplicationMaster(String[] args) throws Exception {
		this.conf = new YarnConfiguration();
		Map<String, String> envs = System.getenv();
		
		ContainerId containerId = ConverterUtils.toContainerId(envs.get(Environment.CONTAINER_ID.name()));
		appAttemptID = containerId.getApplicationAttemptId();
		
		LOG.info("Application master for app" + ", appId="
				+ appAttemptID.getApplicationId().getId() + ", clustertimestamp="
				+ appAttemptID.getApplicationId().getClusterTimestamp()
				+ ", attemptId=" + appAttemptID.getAttemptId());
		
		initialNumPlaces = Integer.parseInt(envs.get(X10_NPLACES));
		coresPerPlace = Integer.parseInt(envs.get(X10_NTHREADS));
		links = new HashMap<Integer, ApplicationMaster.CommunicationLink>(initialNumPlaces);
		places = new HashMap<ContainerId, Integer>(initialNumPlaces);
		pendingReads = new HashMap<SocketChannel, ByteBuffer>();
		selector = Selector.open();
		this.args = args;
		this.appName = System.getProperty(X10_YARN_MAIN);
		
		// look for max memory argument
		this.memoryPerPlaceInMb = 1024; // default of 1Gb per place
		for (int i=0; i<args.length; i++) {
			try {
				if (args[i].startsWith("-Xmx")) {
					if (args[i].endsWith("m"))
						this.memoryPerPlaceInMb = Integer.parseInt(args[i].substring(4, args[i].length()-1));
					else if (args[i].endsWith("k"))
						this.memoryPerPlaceInMb = Integer.parseInt(args[i].substring(4, args[i].length()-1))/1024;
					else
						this.memoryPerPlaceInMb = Integer.parseInt(args[i].substring(4))/1024/1024;
					break;
				}
			} catch (NumberFormatException e) {
				// ignore, use default value
				e.printStackTrace();
			}
		}
	}
	
	private void setup() throws IOException, YarnException {
		 LOG.info("Starting ApplicationMaster");

		 // Remove the AM->RM token so that containers cannot access it.
		 Credentials credentials = UserGroupInformation.getCurrentUser().getCredentials();
		 DataOutputBuffer dob = new DataOutputBuffer();
		 credentials.writeTokenStorageToStream(dob);
		 Iterator<Token<?>> iter = credentials.getAllTokens().iterator();
		 LOG.info("Executing with tokens:");
		 while (iter.hasNext()) {
			 Token<?> token = iter.next();
			 LOG.info(token);
			 if (token.getKind().equals(AMRMTokenIdentifier.KIND_NAME)) {
				 iter.remove();
			 }
		 }
		 allTokens = ByteBuffer.wrap(dob.getData(), 0, dob.getLength());
		 // Create appSubmitterUgi and add original tokens to it
		 String appSubmitterUserName = System.getenv(ApplicationConstants.Environment.USER.name());
		 UserGroupInformation appSubmitterUgi = UserGroupInformation.createRemoteUser(appSubmitterUserName);
		 appSubmitterUgi.addCredentials(credentials);
		 
		 resourceManager = AMRMClientAsync.createAMRMClientAsync(1000, new RMCallbackHandler());
		 resourceManager.init(conf);
		 resourceManager.start();
		 
		 nodeManager = new NMClientAsyncImpl(new NMCallbackHandler(this));
		 nodeManager.init(conf);
		 nodeManager.start();
		 
		 // Register self with ResourceManager
		 // This will start heartbeating to the RM
		 appMasterHostname = NetUtils.getHostname();
		 RegisterApplicationMasterResponse response = resourceManager.registerApplicationMaster(appMasterHostname, 
				 appMasterRpcPort, appMasterTrackingUrl);
		 {
			 int slash = appMasterHostname.indexOf('/');
			 if (slash != -1) appMasterHostname = appMasterHostname.substring(0, slash);
		 }
		 // Dump out information about cluster capability as seen by the
		 // resource manager
		 int maxMem = response.getMaximumResourceCapability().getMemory();
		 LOG.info("Max mem capabililty of resources in this cluster " + maxMem);
		 int maxVCores = response.getMaximumResourceCapability().getVirtualCores();
		 LOG.info("Max vcores capabililty of resources in this cluster " + maxVCores);
		 // A resource ask cannot exceed the max.
		 
		 // TODO: should we reject instead of modifying to fit?
		 if (memoryPerPlaceInMb > maxMem) {
			 LOG.info("Container memory specified above max threshold of cluster."
					 + " Using max value." + ", specified=" + memoryPerPlaceInMb + ", max="
					 + maxMem);
			 memoryPerPlaceInMb = maxMem;
		 }
		 if (coresPerPlace > maxVCores) {
			 LOG.info("Container virtual cores specified above max threshold of cluster."
					 + " Using max value." + ", specified=" + coresPerPlace + ", max="
					 + maxVCores);
			 coresPerPlace = maxVCores;
		 }
		 else if (coresPerPlace == 0) {
			 LOG.info("Container virtual cores specified as auto (X10_NTHREADS=0)."
					 + " Using max value." + ", specified=" + coresPerPlace + ", max="
					 + maxVCores);
			 coresPerPlace = maxVCores;
		 }
		 List<Container> previousAMRunningContainers = response.getContainersFromPreviousAttempts();
		 LOG.info(appAttemptID + " received " + previousAMRunningContainers.size()
				 + " previous attempts' running containers on AM registration.");
		 numAllocatedContainers.addAndGet(previousAMRunningContainers.size());
		 int numTotalContainersToRequest = initialNumPlaces - previousAMRunningContainers.size();
		 
		 // open a local port for X10rt management, and register it with the selector
		 launcherChannel = ServerSocketChannel.open();
		 //launcherChannel.bind(new InetSocketAddress(appMasterHostname, 0)); // bind to the visible network hostname and random port
		 launcherChannel.bind(null);
		 launcherChannel.configureBlocking(false);
		 appMasterPort = launcherChannel.socket().getLocalPort();
		 launcherChannel.register(selector, SelectionKey.OP_ACCEPT);
		 
		 numRequestedContainers.set(initialNumPlaces);
		 // Send request for containers to RM
		 for (int i = 0; i < numTotalContainersToRequest; ++i) {
			 Resource capability = Resource.newInstance(memoryPerPlaceInMb, coresPerPlace);
			 ContainerRequest request = new ContainerRequest(capability, null, null, Priority.newInstance(0));
			 LOG.info("Requested container ask: " + request.toString());
			 resourceManager.addContainerRequest(request);
		 }
	}
	

	protected void handleX10() {
		// handle X10 place requests
		Iterator<SelectionKey> events = null;
		while (running) {
			try {
				SelectionKey key;
				// check for previously unhandled events
				if (events != null && events.hasNext()) {
    				key = events.next();
	    			events.remove();
    			}
				else if (selector.select() == 0) // check for new events
					continue; // nothing to process, go back and block on select again
				else { // select returned some events
					events = selector.selectedKeys().iterator();
	    			key = events.next();
	    			events.remove();
				}
				
				// process the selectionkey
				if (key.isAcceptable()) {
					LOG.info("New connection from X10 detected");
					// accept any connections on the server socket, and look for things to read from it
					ServerSocketChannel ssc = (ServerSocketChannel)key.channel();
					SocketChannel sc = ssc.accept();
					sc.configureBlocking(false);
					sc.register(selector, SelectionKey.OP_READ);
				}
				if (key.isReadable()) {
					SocketChannel sc = (SocketChannel) key.channel();
					
					ByteBuffer incomingMsg;
					if (pendingReads.containsKey(sc))						
						incomingMsg = pendingReads.remove(sc);
					else
						incomingMsg = ByteBuffer.allocateDirect(headerLength).order(ByteOrder.nativeOrder());
					
					LOG.info("Reading message from X10");
					try {
						if (sc.read(incomingMsg) == -1) {
							// socket closed
							sc.close();
							key.cancel();
							pendingReads.remove(sc);
						}
						else if (incomingMsg.hasRemaining()) {
							LOG.info("Message header partially read. "+incomingMsg.remaining()+" bytes remaining");
							pendingReads.put(sc, incomingMsg);
						}
						else { // buffer is full
							if (incomingMsg.capacity() == headerLength) {
								// check to see if there is a body associated with this message header
								int datalen = incomingMsg.getInt(headerLength-4);
								//System.err.println("Byte order is "+incomingMsg.order()+" datalen="+datalen);
								if (datalen == 0)
									processMessage(incomingMsg, sc);
								else { // create a larger array to hold the header+body
									ByteBuffer newBuffer = ByteBuffer.allocateDirect(headerLength+datalen).order(ByteOrder.nativeOrder());
									incomingMsg.rewind();
									newBuffer.put(incomingMsg);
									incomingMsg = newBuffer;
									sc.read(incomingMsg); // read in the body, if available
									if (incomingMsg.hasRemaining()) {
										LOG.info("Message partially read. "+incomingMsg.remaining()+" bytes remaining");
										pendingReads.put(sc, incomingMsg);
									}
									else
										processMessage(incomingMsg, sc);
								}
							}
						}
					} catch (IOException e) {
						LOG.warn("Error reading in message from socket channel", e);
					}
				}
			} catch (IOException e) {
				LOG.warn("Error handling X10 links", e);
			}
		}
	}
	
	private void processMessage(ByteBuffer msg, SocketChannel sc) {
		assert(msg.capacity() >= headerLength);
		
		msg.rewind(); // reset the buffer for reading from the beginning
		CTRL_MSG_TYPE type = CTRL_MSG_TYPE.values()[msg.getInt()];
		int destination = msg.getInt();
		int source = msg.getInt();
		int datalen = msg.getInt();
		assert(datalen == msg.remaining());
		
		LOG.info("Processing message of type "+type+", size "+(headerLength+datalen)+" from place "+source);
/*		System.err.print("Message contents:");
		for (int i=0; i<msg.capacity(); i++)
			System.err.print(" "+Integer.toHexString(msg.get(i)).toUpperCase());
		System.err.println();
*/		
		switch (type) {
			case HELLO:
			{
				// read the port information, and update the record for this place
				assert(datalen == 4 && source < numRequestedContainers.get() && source >= 0);
				CommunicationLink linkInfo;
				LOG.info("Getting link for place "+source);
				synchronized (links) {
					linkInfo = links.get(source);
				}
				linkInfo.port = ((msg.get() & 0xFF) << 8) | (msg.get() & 0xFF); // unsigned short in network order
				linkInfo.sc = sc;
				
				// check if there are pending port requests for this place
				if (linkInfo.pendingPortRequests != null) {
					// prepare response message
					String linkString = linkInfo.hostname+":"+linkInfo.port;
					byte[] linkBytes = linkString.getBytes(); // matches existing code.  TODO: switch to UTF8
					ByteBuffer response = ByteBuffer.allocateDirect(headerLength+linkBytes.length).order(ByteOrder.nativeOrder());
					response.putInt(CTRL_MSG_TYPE.PORT_RESPONSE.ordinal());
					response.putInt(-1);
					response.putInt(source);
					response.putInt(linkBytes.length);
					response.put(linkBytes);
					// send response to each waiting place
					for (int place : linkInfo.pendingPortRequests) {
						response.putInt(4, place); // set the destination to the requesting place
						response.rewind();
						// TODO: this code may get stuck here if the reciever isn't reading properly
						try {
							while (response.hasRemaining())
								links.get(place).sc.write(response);
						} catch (IOException e) {
							LOG.warn("Unable to send out port response for place "+place+" to place "+source, e);
						}
					}
					linkInfo.pendingPortRequests = null;
				}
				LOG.info("HELLO from place "+source+" at port "+linkInfo.port);
			}
			break;
			case GOODBYE:
			{
				try {
					CommunicationLink link = links.get(source);
					assert(link.pendingPortRequests == null);
					sc.close();
					link.port = PORT_DEAD;
				} catch (IOException e) {
					LOG.warn("Error closing socket channel", e);
				}
				LOG.info("GOODBYE to place "+source);
			}
			break;
			case PORT_REQUEST:
			{
				LOG.info("Got PORT_REQUEST from place "+source+" for place "+destination);
				// check to see if we know the requested information
				CommunicationLink linkInfo = links.get(destination);
				if (linkInfo.port != PORT_UNKNOWN) {
					String linkString;
					if (linkInfo.port == PORT_DEAD)
						linkString = DEAD;
					else
						linkString = linkInfo.hostname+":"+linkInfo.port;
					LOG.info("Telling place "+source+" that place "+destination+" is at "+linkString);
					byte[] linkBytes = linkString.getBytes(); // matches existing code.  TODO: switch to UTF8
					ByteBuffer response = ByteBuffer.allocateDirect(headerLength+linkBytes.length).order(ByteOrder.nativeOrder());
					response.putInt(CTRL_MSG_TYPE.PORT_RESPONSE.ordinal());
					response.putInt(source);
					response.putInt(destination);
					response.putInt(linkBytes.length);
					response.put(linkBytes);
					response.rewind();
					// TODO: this code may get stuck here if the reciever isn't reading properly
					try {
						while (response.hasRemaining())
							sc.write(response);
					} catch (IOException e) {
						LOG.warn("Unable to send out port response for place "+destination+" to place "+source, e);
					}
				}
				else { // port is not known.  remember we have a place asking for it when it becomes available
					if (linkInfo.pendingPortRequests == null)
						linkInfo.pendingPortRequests = new ArrayList<Integer>(2);
					linkInfo.pendingPortRequests.add(source);
					LOG.info("Stashing PORT_REQUEST from place "+source+" for place "+destination+" until the answer is known");
				}
			}
			break;
			case LAUNCH_REQUEST:
			{
				assert(datalen == 8);
				int numPlacesRequested = (int) msg.getLong();

				int oldvalue = numRequestedContainers.getAndAdd((int)numPlacesRequested);

				// Send request for containers to RM
				for (int i = 0; i < numPlacesRequested; i++) {
					Resource capability = Resource.newInstance(memoryPerPlaceInMb, coresPerPlace);
					ContainerRequest request = new ContainerRequest(capability, null, null, Priority.newInstance(0));
					LOG.info("Adding a new container request " + request.toString());
					resourceManager.addContainerRequest(request);
				}

				LOG.info("Requested an increase of "+numPlacesRequested+" places on top of the previous "+oldvalue+" places");
				msg.rewind();
				msg.putInt(CTRL_MSG_TYPE.LAUNCH_RESPONSE.ordinal());
				msg.rewind();
				try {
					while (msg.hasRemaining())
						sc.write(msg);
				} catch (IOException e) {
					LOG.warn("Unable to send out launch response to place "+source, e);
				}
			}
			break;
			default:
				LOG.warn("unknown message type "+type);
		}
		LOG.info("Finished processing message of size "+(headerLength+datalen)+" from place "+source);
	}
	
	protected boolean shutdown() {
		
		// close selector, and any remaining links to places
		try {
			selector.close();
		} catch (IOException e1) {
			// shutting down... ignore
		}
		links.clear();
		places.clear();
		
		// When the application completes, it should stop all running containers
		LOG.info("X10 program "+appName+" completed. Stopping running containers");
		nodeManager.stop();
		// When the application completes, it should send a finish application
		// signal to the RM
		LOG.info("Signalling finish to RM");
		FinalApplicationStatus appStatus;
		String appMessage = null;
		boolean success = true;
		if (numFailedContainers.get() == 0) {
			appStatus = FinalApplicationStatus.SUCCEEDED;
		} else {
			appStatus = FinalApplicationStatus.FAILED;
			appMessage = "Diagnostics." + ", total=" + numRequestedContainers.get()
					+ ", completed=" + numCompletedContainers.get() + ", allocated="
					+ numAllocatedContainers.get() + ", failed="
					+ numFailedContainers.get() + ", extra="
					+ numExtraContainers.get();
			success = false;
		}
		try {
			resourceManager.unregisterApplicationMaster(appStatus, appMessage, null);
		} catch (YarnException ex) {
			LOG.error("Failed to unregister application", ex);
		} catch (IOException e) {
			LOG.error("Failed to unregister application", e);
		}
		resourceManager.stop();
		return success;
	}	
		
	private class RMCallbackHandler implements AMRMClientAsync.CallbackHandler {

		@Override
		public float getProgress() {
			// ramp up to 50% as places start up, then up to 100% as they shut down
			return (float) (numAllocatedContainers.get()+numCompletedContainers.get()-numExtraContainers.get()) / (numRequestedContainers.get()*2);
		}

		@Override
		public void onContainersAllocated(List<Container> allocatedContainers) {
			LOG.info("Got response from RM for container ask, allocatedCnt="+ allocatedContainers.size());
			for (Container allocatedContainer : allocatedContainers) {
				if (numAllocatedContainers.get() >= numRequestedContainers.get()) {
					// There is currently a bug in yarn, where it will allocate more
					// containers than requested, if the request comes in later.  Workaround.
					LOG.info("Dropping unexpected container "+allocatedContainer.getId());
					numExtraContainers.incrementAndGet();
					resourceManager.releaseAssignedContainer(allocatedContainer.getId());
					continue;
				}
				
				int placeId = numAllocatedContainers.getAndIncrement();
				String hostname = allocatedContainer.getNodeId().getHost();
				LOG.info("Launching place on a new container."
						+ ", containerId=" + allocatedContainer.getId()
						+ ", containerNode=" + hostname
						+ ":" + allocatedContainer.getNodeId().getPort()
						+ ", containerNodeURI=" + allocatedContainer.getNodeHttpAddress()
						+ ", containerResourceMemory"
						+ allocatedContainer.getResource().getMemory()
						+ ", containerResourceVirtualCores"
						+ allocatedContainer.getResource().getVirtualCores());
				
				// prepare the container context for X10 places
				try {
					// put x10.jar and other needed jar files into the container resources
					FileSystem fs = FileSystem.get(conf);
					String appId = appAttemptID.getApplicationId().toString();
					Map<String, LocalResource> localResources = new HashMap<String, LocalResource>();
					
					String[] jarfiles = System.getenv(X10_HDFS_JARS).split(":");
					for (String jar: jarfiles) {
						if (jar.length() > 0) {
							LOG.info("Added "+jar+" to local resources for "+appId);
							addToRuntimeResources(fs, jar, appId, localResources);
						}
					}
					
					// upload any files specified via -copy argument
					String upload = System.getProperty(ApplicationMaster.X10_YARNUPLOAD);
					if (upload != null) {
						String[] files = upload.split(",");
						for (String file : files) {
							LOG.info("Added file "+file+" to local resources for "+appId);
							addToRuntimeResources(fs, file, appId, localResources);
						}
					}
		
					// set environment variables
					LOG.info("Set the environment for container for place "+placeId);
					Map<String, String> env = new HashMap<String, String>();
					//env.putAll(System.getenv()); // copy all environment variables from the client side to the application side
					// copy over existing environment variables
					final int prefixlen = X10YARNENV_.length();
					for (String key : System.getenv().keySet()) {
						if (key.startsWith(X10YARNENV_))
							env.put(key.substring(prefixlen), System.getenv(key));
					}
					env.put(ApplicationMaster.X10_NPLACES, Integer.toString(Math.max(initialNumPlaces, numRequestedContainers.get())));
					env.put(ApplicationMaster.X10_NTHREADS, Integer.toString(coresPerPlace));
					env.put(ApplicationMaster.X10_LAUNCHER_PLACE, Integer.toString(placeId));
					env.put(ApplicationMaster.X10_LAUNCHER_HOST, allocatedContainer.getNodeId().getHost());
					env.put(ApplicationMaster.X10_LAUNCHER_PARENT, appMasterHostname+':'+appMasterPort);

					// Set the necessary command to execute the runtime
					Vector<CharSequence> vargs = new Vector<CharSequence>(args.length + 30);

					boolean isNative = Boolean.getBoolean(X10_YARN_NATIVE);
					if (!isNative) {
						// Set up the class path, to include all local jar files addid via addToRuntimeResources above
						StringBuilder classPathEnv = new StringBuilder(Environment.CLASSPATH.$$()).append(ApplicationConstants.CLASS_PATH_SEPARATOR).append("./*");
	/*					for (String c : conf.getStrings(YarnConfiguration.YARN_APPLICATION_CLASSPATH,
								YarnConfiguration.DEFAULT_YARN_CROSS_PLATFORM_APPLICATION_CLASSPATH)) {
							classPathEnv.append(ApplicationConstants.CLASS_PATH_SEPARATOR);
							classPathEnv.append(c.trim());
						}
						env.put("CLASSPATH", classPathEnv.toString());
	*/					
						LOG.info("Completed setting up runtime environment " + env.toString());
						
						// Set java executable command
						vargs.add(Environment.JAVA_HOME.$$() + "/bin/java");
						// set classpath
						vargs.add("-classpath");
						vargs.add(classPathEnv.toString());
						// set remaining arguments
						for (int i=0; i<args.length; i++)
							vargs.add(args[i]);
					}
					else {
						// set binary name
						vargs.add("./"+appName);
						for (int i=1; i<args.length; i++)
							vargs.add(args[i]);
					}

					
					vargs.add("1>" + ApplicationConstants.LOG_DIR_EXPANSION_VAR + '/'+appName+"_Place"+placeId+".stdout");
					vargs.add("2>" + ApplicationConstants.LOG_DIR_EXPANSION_VAR + '/'+appName+"_Place"+placeId+".stderr");
					
					// Get final commmand
					StringBuilder command = new StringBuilder();
					for (CharSequence str : vargs) {
						command.append(str).append(" ");
					}
					
					LOG.info("Completed setting up runtime launch command " + command.toString());
					List<String> commands = new ArrayList<String>();
					commands.add(command.toString());
					
					ContainerLaunchContext ctx = ContainerLaunchContext.newInstance(
							localResources, env, commands, null, allTokens.duplicate(), null);
					LOG.info("Storing link for place "+placeId);
					links.put(placeId, new CommunicationLink(hostname)); // save the hostname of the machine with this place
					places.put(allocatedContainer.getId(), placeId);
					nodeManager.startContainerAsync(allocatedContainer, ctx);
				}
				catch (IOException e) {
					LOG.error("Failed to start Place " + placeId, e);
				}
			}
		}

		@Override
		public void onContainersCompleted(List<ContainerStatus> completedContainers) {
			LOG.info("Got response from RM for container ask, completedCnt="+ completedContainers.size());
			for (ContainerStatus containerStatus : completedContainers) {
				LOG.info(appAttemptID + " got container status for containerID="
						+ containerStatus.getContainerId() + ", state="
						+ containerStatus.getState() + ", exitStatus="
						+ containerStatus.getExitStatus() + ", diagnostics="
						+ containerStatus.getDiagnostics());

				// increment counters for completed/failed containers
				numCompletedContainers.incrementAndGet();
				int exitStatus = containerStatus.getExitStatus();
				if (0 != exitStatus) {
					// container failed
					if (ContainerExitStatus.ABORTED == exitStatus) {
						// killed by framework, not really a failure?
						LOG.info("Container aborted");
					} else {
						numFailedContainers.incrementAndGet();
						LOG.info("Container exited with exit code "+exitStatus);
					}
				} else {
					// nothing to do
					// container completed successfully
					LOG.info("Container completed successfully." + ", containerId="+ containerStatus.getContainerId());
				}
				links.get(places.get(containerStatus.getContainerId())).port = PORT_DEAD; // mark the place as dead
			}
			// check to see if everything is down, and if so, exit ourselves
			if (numCompletedContainers.get() == (numAllocatedContainers.get()+numExtraContainers.get())) {
				LOG.info("Shutting down now that all "+numRequestedContainers.get()+" containers have exited");
				running = false;
				selector.wakeup();
			}
		}

		@Override
		public void onError(Throwable arg0) {
			running = false;
			selector.wakeup();
			resourceManager.stop();
		}

		@Override
		public void onNodesUpdated(List<NodeReport> arg0) {}

		@Override
		public void onShutdownRequest() {
			running = false;
			selector.wakeup();
		}
		
		private void addToRuntimeResources(FileSystem fs, String fileDstPath, String appId, Map<String, LocalResource> localResources) throws IOException {
			Path dst = new Path(fs.getUri()+fileDstPath);
			FileStatus scFileStatus = fs.getFileStatus(dst);
			LocalResource scRsrc = LocalResource.newInstance(
							ConverterUtils.getYarnUrlFromURI(dst.toUri()),
							LocalResourceType.FILE, LocalResourceVisibility.APPLICATION,
							scFileStatus.getLen(), scFileStatus.getModificationTime());
			localResources.put(fileDstPath.substring(fileDstPath.lastIndexOf('/')+1), scRsrc);
		}
	}


	private static class NMCallbackHandler implements NMClientAsync.CallbackHandler {
		private final ApplicationMaster applicationMaster;
		
		public NMCallbackHandler(ApplicationMaster applicationMaster) {
			this.applicationMaster = applicationMaster;
		}

		@Override
		public void onContainerStarted(ContainerId containerId, Map<String, ByteBuffer> arg1) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Succeeded to start Container " + containerId);
			}
		}

		@Override
		public void onContainerStatusReceived(ContainerId containerId, ContainerStatus containerStatus) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Container Status: id=" + containerId + ", status=" + containerStatus);
			}
		}

		@Override
		public void onContainerStopped(ContainerId containerId) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Succeeded to stop Container " + containerId);
			}	
		}

		@Override
		public void onGetContainerStatusError(ContainerId containerId, Throwable arg1) {
			LOG.error("Failed to query the status of Container " + containerId);		
		}

		@Override
		public void onStartContainerError(ContainerId containerId, Throwable e) {
			LOG.error("Failed to start Container " + containerId, e);
			applicationMaster.numCompletedContainers.incrementAndGet();
			applicationMaster.numFailedContainers.incrementAndGet();
		}

		@Override
		public void onStopContainerError(ContainerId containerId, Throwable e) {
			LOG.error("Failed to stop Container " + containerId, e);
		}
	}
}

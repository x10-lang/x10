package apgas.yarn;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.api.ApplicationConstants;
import org.apache.hadoop.yarn.api.ApplicationConstants.Environment;
import org.apache.hadoop.yarn.api.protocolrecords.AllocateResponse;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationSubmissionContext;
import org.apache.hadoop.yarn.api.records.Container;
import org.apache.hadoop.yarn.api.records.ContainerLaunchContext;
import org.apache.hadoop.yarn.api.records.FinalApplicationStatus;
import org.apache.hadoop.yarn.api.records.Priority;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.apache.hadoop.yarn.client.api.AMRMClient;
import org.apache.hadoop.yarn.client.api.AMRMClient.ContainerRequest;
import org.apache.hadoop.yarn.client.api.NMClient;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.exceptions.YarnException;

/**
 * The {@link Launcher} class implements a launcher based on Yarn.
 */
public class Launcher implements apgas.util.Launcher {
  private static void redirect(List<String> command) {
    command.add("1>" + ApplicationConstants.LOG_DIR_EXPANSION_VAR + "/stdout");
    command.add("2>" + ApplicationConstants.LOG_DIR_EXPANSION_VAR + "/stderr");
  }

  private final org.apache.hadoop.conf.Configuration conf = new YarnConfiguration();
  private final AMRMClient<ContainerRequest> rmClient = AMRMClient
      .createAMRMClient();

  @Override
  public void launch(int n, List<String> command) throws Exception {
    redirect(command); // TODO clone before mutating the command
    rmClient.init(conf);
    rmClient.start();
    final NMClient nmClient = NMClient.createNMClient();
    nmClient.init(conf);
    nmClient.start();
    rmClient.registerApplicationMaster("", 0, "");
    for (int i = 0; i < n; ++i) {
      final ContainerRequest request = new ContainerRequest(
          Resource.newInstance(256, 1), null, null, Priority.newInstance(0));
      rmClient.addContainerRequest(request);
    }
    int responseId = 0;
    for (int containers = 0; containers < n;) {
      final AllocateResponse response = rmClient.allocate(responseId++);
      for (final Container container : response.getAllocatedContainers()) {
        final ContainerLaunchContext ctx = ContainerLaunchContext.newInstance(
            null, null, command, null, null, null);
        nmClient.startContainer(container, ctx);
      }
      containers += response.getAllocatedContainers().size();
      try {
        Thread.sleep(100);
      } catch (final InterruptedException e) {
      }
    }
  }

  @Override
  public boolean healthy() {
    return true;
  }

  @Override
  public void shutdown() {
    try {
      rmClient.unregisterApplicationMaster(FinalApplicationStatus.SUCCEEDED,
          "", null);
    } catch (final YarnException | IOException e) {
    }
    rmClient.stop();
  }

  /**
   * Submits an application to Yarn,
   *
   * @param args
   *          the program arguments
   * @throws IOException
   *           if an error occurs
   * @throws YarnException
   *           if an error occurs
   */
  public static void main(String[] args) throws IOException, YarnException {
    if (args.length <= 0) {
      System.err.println("Usage: yarn " + Launcher.class.getCanonicalName()
          + " COMMAND");
      System.exit(1);
    }
    final List<String> command = new ArrayList<String>();
    command.add(args[0]);
    command.add("-D" + apgas.Configuration.APGAS_LAUNCHER + "="
        + Launcher.class.getCanonicalName());
    command.add("-D" + apgas.Configuration.APGAS_JAVA + "=" + args[0]);
    String classpath = "";
    for (int i = 1; i < args.length; i++) {
      if (args[i].equals("-cp") || args[i].equals("-classpath")) {
        classpath = args[++i];
      } else {
        command.add(args[i]);
      }
    }
    redirect(command);
    final Configuration conf = new YarnConfiguration();
    final YarnClient yarnClient = YarnClient.createYarnClient();
    yarnClient.init(conf);
    yarnClient.start();
    final String cp = String
        .join(
            ApplicationConstants.CLASS_PATH_SEPARATOR,
            conf.getStrings(
                YarnConfiguration.YARN_APPLICATION_CLASSPATH,
                YarnConfiguration.DEFAULT_YARN_CROSS_PLATFORM_APPLICATION_CLASSPATH));
    final Map<String, String> env = Collections.singletonMap(
        Environment.CLASSPATH.name(), classpath
            + ApplicationConstants.CLASS_PATH_SEPARATOR + cp);
    final ContainerLaunchContext ctx = ContainerLaunchContext.newInstance(null,
        env, command, null, null, null);
    final ApplicationSubmissionContext appContext = yarnClient
        .createApplication().getApplicationSubmissionContext();
    appContext.setAMContainerSpec(ctx);
    appContext.setResource(Resource.newInstance(256, 1));
    appContext.setMaxAppAttempts(1);
    final ApplicationId appId = appContext.getApplicationId();
    yarnClient.submitApplication(appContext);
    YarnApplicationState appState;
    for (;;) {
      appState = yarnClient.getApplicationReport(appId)
          .getYarnApplicationState();
      if (appState == YarnApplicationState.FINISHED
          || appState == YarnApplicationState.KILLED
          || appState == YarnApplicationState.FAILED) {
        break;
      }
      try {
        Thread.sleep(100);
      } catch (final InterruptedException e) {
      }
    }
    System.err.println(appId + " finished with state " + appState);
  }
}

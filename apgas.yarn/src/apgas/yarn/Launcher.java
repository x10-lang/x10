package apgas.yarn;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.api.ApplicationConstants;
import org.apache.hadoop.yarn.api.ApplicationConstants.Environment;
import org.apache.hadoop.yarn.api.protocolrecords.AllocateResponse;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationSubmissionContext;
import org.apache.hadoop.yarn.api.records.Container;
import org.apache.hadoop.yarn.api.records.ContainerLaunchContext;
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
public class Launcher implements BiConsumer<Integer, List<String>> {
  private static void redirect(List<String> command) {
    command.add("1>" + ApplicationConstants.LOG_DIR_EXPANSION_VAR + "/stdout");
    command.add("2>" + ApplicationConstants.LOG_DIR_EXPANSION_VAR + "/stderr");
  }

  @Override
  public void accept(Integer p, List<String> command) {
    redirect(command);
    try {
      final org.apache.hadoop.conf.Configuration conf = new YarnConfiguration();
      final AMRMClient<ContainerRequest> rmClient = AMRMClient
          .createAMRMClient();
      rmClient.init(conf);
      rmClient.start();
      final NMClient nmClient = NMClient.createNMClient();
      nmClient.init(conf);
      nmClient.start();
      rmClient.registerApplicationMaster("", 0, "");
      for (int i = 1; i < p; ++i) {
        final ContainerRequest request = new ContainerRequest(
            Resource.newInstance(256, 1), null, null, Priority.newInstance(0));
        rmClient.addContainerRequest(request);
      }
      int responseId = 0;
      int containers = 1;
      while (containers < p) {
        final AllocateResponse response = rmClient.allocate(responseId++);
        for (final Container container : response.getAllocatedContainers()) {
          final ContainerLaunchContext ctx = ContainerLaunchContext
              .newInstance(null, null, command, null, null, null);
          nmClient.startContainer(container, ctx);
        }
        containers += response.getAllocatedContainers().size();
        try {
          Thread.sleep(100);
        } catch (final InterruptedException e) {
        }
      }
    } catch (final RuntimeException e) {
      throw e;
    } catch (final Exception e) {
      throw new RuntimeException(e);
    }
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
    if (args.length <= 2) {
      System.err.println("Usage: yarn apgas.yarn.Launcher JAVA JARS CLASSNAME");
      System.err.println(" where JAVA is the java executable");
      System.err.println("       JARS is a colon-separated list of jar files");
      System.err.println("       CLASSNAME is the name of the main class");
      System.exit(1);
    }
    final List<String> command = new ArrayList<String>();
    command.add(args[0]);
    command.add("-Dapgas.launcher=apgas.yarn.Launcher");
    command.add("-Dapgas.java=" + args[0]);
    command.add(args[2]);
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
        Environment.CLASSPATH.name(), args[1]
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

package org.eclipse.imp.x10dt.core.tests.builder;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(SWTBotJunit4ClassRunner.class)
public class SWTBotTester {

		private static SWTWorkbenchBot	bot;
	 
		@BeforeClass
		public static void beforeClass() throws Exception {
			bot = new SWTWorkbenchBot();
			bot.viewByTitle("Welcome").close();
		}  
	 
	 
		@Test
		public void NoJavaBuilder() throws Exception {
			SWTBotShell mainShell = bot.activeShell();
			
			bot.menu("Window").menu("Open Perspective").menu("X10").click();
			bot.menu("File").menu("New").menu("X10 Project (Java back-end)").click();
			 
			SWTBotShell shell = bot.shell("New X10 Project (Java back-end)");
			shell.activate();

			bot.textWithLabel("Name:").setText("MyFirstProject");
			bot.button("Finish").click();
			
			mainShell.activate();
			
			IWorkspaceRoot wsRoot = ResourcesPlugin.getWorkspace().getRoot();
			IProject project = wsRoot.getProject("MyFirstProject");
			IProjectDescription description = project.getDescription();
	    	ICommand[] commands = description.getBuildSpec();
	    	for(int i = 0; i < commands.length; i++){
	    		if (commands[i].getBuilderName().equals(JavaCore.BUILDER_ID)){
	    			Assert.assertTrue(false);
	    		}
	    	}
		}
	 
	 
		@AfterClass
		public static void sleep() {
			bot.sleep(2000);
		}
	 
}

package org.eclipse.imp.x10dt.core.preferences;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;

import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.imp.x10dt.core.X10Plugin;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.osgi.framework.Bundle;

/**
 * This class represents a preference page that is contributed to the Preferences dialog. By 
 * subclassing <samp>FieldEditorPreferencePage</samp>, we can use the field support built into
 * JFace that allows us to create a page that is small and knows how to 
 * save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They are stored in the preference store that
 * belongs to the main plug-in class. That way, preferences can be accessed directly via the
 * preference store.
 */
public class X10PreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
    private static final String[] EMPTY= new String[0];

    public X10PreferencePage() {
	super(GRID);
	setPreferenceStore(X10Plugin.getInstance().getPreferenceStore());
	setDescription("Preferences for the X10 compiler and runtime");
    }

    /**
     * Creates the field editors. Field editors are abstractions of the common GUI blocks
     * needed to manipulate various types of preferences. Each field editor knows how to
     * save and restore itself.
     */
    public void createFieldEditors() {
//	addField(new BooleanFieldEditor(PreferenceConstants.P_AUTO_ADD_RUNTIME, "Add X10 runtime library in x10.runtime to build path", getFieldEditorParent()));

	String[] configs= findConfigs();
	String[][] configFieldItems= new String[configs.length][];

	for(int i= 0; i < configs.length; i++) {
	    configFieldItems[i]= new String[2];

	    configFieldItems[i][0]= configs[i];
	    configFieldItems[i][1]= configs[i];
	}
	addField(new ComboFieldEditor(PreferenceConstants.P_X10CONFIG_NAME, "Compiler Confi&guration:", configFieldItems, getFieldEditorParent()));

        // RMF 6/7/2006 - The following run-time settings are currently inactive, and probably
	// don't even belong on this preferences page.
        createSpacer();
        addField(new IntegerFieldEditor(PreferenceConstants.P_SAMPLING_FREQ, "&Sampling frequency:", getFieldEditorParent()));
        addField(new RadioGroupFieldEditor(PreferenceConstants.P_STATS_DISABLE, "Statistics disable:", 2,
                new String[][] { { "&None", "none" }, { "&All", "all" } }, getFieldEditorParent(), true));

        createSpacer();
        addField(new BooleanFieldEditor(PreferenceConstants.P_EMIT_MESSAGES, "Emit diagnostic &messages from the builder", getFieldEditorParent()));

	getPreferenceStore().addPropertyChangeListener(new IPropertyChangeListener() {
	    public void propertyChange(PropertyChangeEvent event) {
		if (event.getProperty().equals(PreferenceConstants.P_EMIT_MESSAGES))
		    X10Preferences.builderEmitMessages= ((Boolean) event.getNewValue()).booleanValue();
		else if (event.getProperty().equals(PreferenceConstants.P_X10CONFIG_NAME)) {
		    X10Preferences.x10ConfigName= (String) event.getNewValue();
		    X10Preferences.x10ConfigFile= X10Plugin.x10CompilerPath + "etc/" + X10Preferences.x10ConfigName + ".cfg";
		    System.setProperty("x10.configuration", X10Preferences.x10ConfigFile);
		}
//		else if (event.getProperty().equals(PreferenceConstants.P_AUTO_ADD_RUNTIME))
//		    X10Preferences.autoAddRuntime= ((Boolean) event.getNewValue()).booleanValue();
                else if (event.getProperty().equals(PreferenceConstants.P_SAMPLING_FREQ)) {
                    X10Preferences.samplingFreq= ((Integer) event.getNewValue()).intValue();
                    rewritePrefsFile();
                } else if (event.getProperty().equals(PreferenceConstants.P_STATS_DISABLE)) {
                    X10Preferences.statsDisable= (String) event.getNewValue();
                    rewritePrefsFile();
                }
	    }	
	    private void rewritePrefsFile() {
                String prefsPath= X10Preferences.x10ConfigFile;
                try {
                    File prefsFile= new File(prefsPath);
                    prefsFile.createNewFile();
                    PrintWriter w= new PrintWriter(new FileWriter(prefsFile));

                    w.println("# This file was created by the X10 preferences page in Eclipse.");
                    w.println("COMPILER_FRAGMENT_DATA_DIRECTORY=data/"); //  + X10Preferences.x10CompilerDataDir);
                    // RMF 6/7/2006 - The following run-time settings probably don't even belong in the same
                    // config file as the compiler settings.
                    w.println("SAMPLING_FREQUENCY_MS=" + X10Preferences.samplingFreq);
                    w.println("STATISTICS_DISABLE=" + X10Preferences.statsDisable);
                    w.flush();
                } catch (FileNotFoundException fnf) {
                    X10Plugin.getInstance().writeErrorMsg("File not found: " + fnf.getMessage());
                } catch (IOException e) {
                    X10Plugin.getInstance().writeErrorMsg("Unable to create X10 preferences file: " + e.getMessage());
                }
            }
	});
    }

    private String[] findConfigs() {
	try {
	    // Perhaps this should just use X10Plugin.x10CompilerPath?
	    Bundle x10CompilerBundle= Platform.getBundle("x10.compiler");
	    URL configDirURL= Platform.asLocalURL(Platform.find(x10CompilerBundle, new Path("etc/")));
	    String configDirPath= configDirURL.getPath();
	    File configDir= new File(configDirPath);

	    File[] configFiles= configDir.listFiles(new FilenameFilter() {
		public boolean accept(File dir, String name) {
		    return name.endsWith(".cfg");
		}
	    });

	    String[] result= new String[configFiles.length];
	    for(int i= 0; i < configFiles.length; i++) {
		final String fileName= configFiles[i].getName();
		result[i]= fileName.substring(0, fileName.indexOf('.'));
	    }
	    return result;
	} catch (IOException io) {
	    return EMPTY;
	}
    }

    private void createSpacer() {
        Label label= new Label(getFieldEditorParent(), SWT.NONE);
        GridData gd= new GridData();

        gd.horizontalSpan= 3;
        label.setLayoutData(gd);
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
     */
    public void init(IWorkbench workbench) {}
}

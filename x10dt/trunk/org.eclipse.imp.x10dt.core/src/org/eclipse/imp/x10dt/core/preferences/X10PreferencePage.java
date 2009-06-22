package com.ibm.watson.safari.x10.preferences;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.ibm.watson.safari.x10.X10Plugin;

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
	addField(new DirectoryFieldEditor(PreferenceConstants.P_X10COMMON_PATH, "&Common directory:", getFieldEditorParent()));
	addField(new FileFieldEditor(PreferenceConstants.P_X10CONFIG_FILE, "Confi&guration file:", getFieldEditorParent()));
//	addField(new BooleanFieldEditor(PreferenceConstants.P_AUTO_ADD_RUNTIME, "Add X10 runtime library in x10.runtime to build path", getFieldEditorParent()));

        addField(new DirectoryFieldEditor(PreferenceConstants.P_COMPILER_DATA_DIR, "Compiler &template directory:", getFieldEditorParent()));

        createSpacer();
        addField(new IntegerFieldEditor(PreferenceConstants.P_SAMPLING_FREQ, "&Sampling frequency:", getFieldEditorParent()));
        addField(new RadioGroupFieldEditor(PreferenceConstants.P_STATS_DISABLE, "Statistics disable:", 2,
                new String[][] { { "&None", "none" }, { "&All", "all" } }, getFieldEditorParent(), true));

        createSpacer();
        addField(new BooleanFieldEditor(PreferenceConstants.P_EMIT_MESSAGES, "Emit diagnostic &messages from the builder", getFieldEditorParent()));

//	addField(new StringFieldEditor(PreferenceConstants.P_STRING, "A &text preference:", getFieldEditorParent()));

	getPreferenceStore().addPropertyChangeListener(new IPropertyChangeListener() {
	    public void propertyChange(PropertyChangeEvent event) {
		if (event.getProperty().equals(PreferenceConstants.P_EMIT_MESSAGES))
		    X10Preferences.builderEmitMessages= ((Boolean) event.getNewValue()).booleanValue();
		else if (event.getProperty().equals(PreferenceConstants.P_X10COMMON_PATH))
		    X10Preferences.x10CommonPath= (String) event.getNewValue();
		else if (event.getProperty().equals(PreferenceConstants.P_X10CONFIG_FILE))
		    X10Preferences.x10ConfigFile= (String) event.getNewValue();
		else if (event.getProperty().equals(PreferenceConstants.P_AUTO_ADD_RUNTIME))
		    X10Preferences.autoAddRuntime= ((Boolean) event.getNewValue()).booleanValue();

                else if (event.getProperty().equals(PreferenceConstants.P_COMPILER_DATA_DIR)) {
                    X10Preferences.x10CompilerDataDir= (String) event.getNewValue();
                    rewritePrefsFile();
                } else if (event.getProperty().equals(PreferenceConstants.P_SAMPLING_FREQ)) {
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
                    PrintWriter w= new PrintWriter(prefsFile);

                    w.println("# This file was created by the X10 preferences page in Eclipse.");
                    w.println("COMPILER_FRAGMENT_DATA_DIRECTORY=" + X10Preferences.x10CompilerDataDir);
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

    private void createSpacer() {
        Label label = new Label(getFieldEditorParent(), SWT.NONE);
        GridData gd = new GridData();
        gd.horizontalSpan = 3;
        label.setLayoutData(gd);
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
     */
    public void init(IWorkbench workbench) {}
}

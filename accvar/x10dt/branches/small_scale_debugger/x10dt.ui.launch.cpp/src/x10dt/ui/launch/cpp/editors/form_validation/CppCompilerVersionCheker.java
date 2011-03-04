/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.cpp.editors.form_validation;

import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.editor.IFormPage;

import x10dt.ui.launch.core.platform_conf.EArchitecture;
import x10dt.ui.launch.core.platform_conf.ETargetOS;
import x10dt.ui.launch.core.utils.IProcessOuputListener;
import x10dt.ui.launch.cpp.LaunchMessages;
import x10dt.ui.launch.cpp.builder.target_op.ITargetOpHelper;

final class CppCompilerVersionCheker extends AbstractFormControlChecker implements IFormControlChecker {
  
  CppCompilerVersionCheker(final ITargetOpHelper targetOpHelper, final ETargetOS targetOS, final EArchitecture architecture,
                           final IFormPage formPage, final Control control) {
    super(formPage, control);
    this.fTargetOpHelper = targetOpHelper;
    this.fTargetOS = targetOS;
    this.fArchitecture = architecture;
  }

  // --- Interface methods implementation
  
  public boolean validate(final String text) {
    removeMessages();
    if (getControl().isEnabled() && (text.length() > 0)) {
      final AbstractCompilerVersionOutputListener listener;
      final String lowerCaseName = text.toLowerCase();
      if (((text.length() >= 3) && (lowerCaseName.charAt(0) == 'x') && (lowerCaseName.charAt(1) == 'l') && 
          (lowerCaseName.charAt(2) == 'c')) || MPCC.equals(text)) {
        listener = new XlC_CompilerVersionOutputListener();
      } else if (text.startsWith(GPP) || text.startsWith(GCC)){
        listener = new GnuCompilerVersionOutputListener();
      } else if (text.equals(MPICXX)) {
        listener = new MpicxxAccessibilityListener();
      } else {
        listener = null;
      }
      if (listener == null) {
        addMessages(LaunchMessages.CCVC_CompilerNotSupportedWarning, IMessageProvider.WARNING);
      } else {
        try {
          this.fTargetOpHelper.run(Arrays.asList(text, listener.getOption()), listener);
          return listener.validateVersion();
        } catch (Exception except) {
          addMessages(NLS.bind(LaunchMessages.CCVC_VersionCheckingCmdError, except.getMessage()), IMessageProvider.ERROR);
          return false;
        }
      }
    }
    return true;
  }
  
  // --- Overridden methods
  
  public boolean equals(final Object rhs) {
    if (! (rhs instanceof CppCompilerVersionCheker)) {
      return false;
    }
    return getControl().equals(((AbstractFormControlChecker) rhs).getControl());
  }
  
  public int hashCode() {
    return getControl().hashCode();
  }
  
  // --- Private classes
  
  private static abstract class AbstractCompilerVersionOutputListener implements IProcessOuputListener {
    
    // --- Abstract methods definition
    
    protected abstract String getOption();
    
    protected abstract boolean validateVersion();
    
    // --- Interface methods implementation
    
    public final void readError(final String line) {
      this.fErrorLineBuilder.append(line);
    }
    
    // --- Code for descendants
    
    protected final void errorChecking() {
      if (this.fErrorLineBuilder.length() > 0) {
        throw new InvalidParameterException(this.fErrorLineBuilder.toString());
      }
    }
    
    // --- Fields
    
    private StringBuilder fErrorLineBuilder = new StringBuilder();
    
  }
  
  private final class XlC_CompilerVersionOutputListener extends AbstractCompilerVersionOutputListener
                                                        implements IProcessOuputListener {

    // --- Interface methods implementation

    public void read(final String line) {
      if (line.startsWith(XLC_VERSION_LINE_START)) {
        this.fVersionLine = line.substring(XLC_VERSION_LINE_START.length() + 1);
      }
    }
    
    // --- Abstract methods implementation
    
    protected String getOption() {
      return XLC_COMPILER_OPTION;
    }
    
    protected boolean validateVersion() {
      super.errorChecking();
      if (this.fVersionLine == null) {
        addMessages(LaunchMessages.CCVC_NoFoundVersionWarning, IMessageProvider.WARNING);
      } else {
        final Matcher matcher = Pattern.compile(IBM_VERSION_REGEX_PATTERN).matcher(this.fVersionLine);
        if (matcher.find()) {
          final int major = Integer.parseInt(matcher.group(1));
          final int minor = Integer.parseInt(matcher.group(2));
          final int maintenance = Integer.parseInt(matcher.group(3));
          final int build = Integer.parseInt(matcher.group(4));
          if ((major >= 10) && (minor >= 1) && (maintenance >= 0)) {
            if (CppCompilerVersionCheker.this.fTargetOS == ETargetOS.AIX) {
              if (build < 4) {
                addMessages(LaunchMessages.CCVC_WrongAIXVersionRequirement, IMessageProvider.ERROR);
                return false;
              } else {
                removeMessages();
              }
            } else if ((CppCompilerVersionCheker.this.fTargetOS == ETargetOS.LINUX) &&
                       (CppCompilerVersionCheker.this.fArchitecture != EArchitecture.x86)) {
              if (build < 2) {
                addMessages(LaunchMessages.CCVC_WrongLinuxPPCVersionRequirement, IMessageProvider.ERROR);
                return false;
              } else {
                removeMessages();
              }
            } else {
              addMessages(LaunchMessages.CCVC_IBMCompilerVersionNotTested, IMessageProvider.WARNING);
            }
          } else {
            addMessages(LaunchMessages.CCVC_WrongIBMVersionRequirement, IMessageProvider.ERROR);
            return false;
          }
        } else {
          addMessages(LaunchMessages.CCVC_NoFoundVersionWarning, IMessageProvider.WARNING);
        }
      }
      return true;
    }
    
    // --- Fields
    
    private String fVersionLine;
    
  }
  
  private final class GnuCompilerVersionOutputListener extends AbstractCompilerVersionOutputListener 
                                                       implements IProcessOuputListener {

    // --- Interface methods implementation

    public void read(final String line) {
      this.fVersionLine = line;
    }

    // --- Abstract methods implementation

    protected String getOption() {
      return GNU_COMPILER_OPTION;
    }

    protected boolean validateVersion() {
      super.errorChecking();
      if (this.fVersionLine == null) {
        addMessages(LaunchMessages.CCVC_NoFoundVersionWarning, IMessageProvider.WARNING);
      } else {
        final Matcher matcher = Pattern.compile(GNU_VERSION_REGEX_PATTERN).matcher(this.fVersionLine);
        if (matcher.find()) {
          final int major = Integer.parseInt(matcher.group(1));
          final int minor = Integer.parseInt(matcher.group(2));
          if ((major >= 4) && (minor >= 2)) {
            removeMessages();
          } else {
            addMessages(LaunchMessages.CCVC_WrongGnuVersionRequirement, IMessageProvider.ERROR);
            return false;
          }
        } else {
          addMessages(LaunchMessages.CCVC_NoFoundVersionWarning, IMessageProvider.WARNING);
        }
      }
      return true;
    }
    
    // --- Fields
    
    private String fVersionLine;

  }
  
  private static final class MpicxxAccessibilityListener extends AbstractCompilerVersionOutputListener
                                                         implements IProcessOuputListener {

    // --- Interface methods implementation
    
    public void read(final String line) {
      // Simply forget.
    }

    // --- Abstract methods implementation
    
    protected String getOption() {
      return GNU_COMPILER_OPTION;
    }

    protected boolean validateVersion() {
      super.errorChecking();
      return true;
    }
    
  }
  
  // --- Fields
  
  private final ITargetOpHelper fTargetOpHelper;
  
  private final ETargetOS fTargetOS;
  
  private final EArchitecture fArchitecture;
  

  private static final String GCC = "gcc"; //$NON-NLS-1$
  
  private static final String GPP = "g++"; //$NON-NLS-1$
  
  private static final String MPICXX = "mpicxx"; //$NON-NLS-1$
  
  private static final String MPCC = "mpCC_r"; //$NON-NLS-1$
  
  private static final String GNU_COMPILER_OPTION = "-dumpversion"; //$NON-NLS-1$
  
  private static final String XLC_COMPILER_OPTION = "-qversion"; //$NON-NLS-1$
  
  private static final String XLC_VERSION_LINE_START = "Version: "; //$NON-NLS-1$
  
  private static final String GNU_VERSION_REGEX_PATTERN = "(\\d+)\\.(\\d+)"; //$NON-NLS-1$
  
  private static final String IBM_VERSION_REGEX_PATTERN = "(\\d+)\\.(\\d+)\\.(\\d+)\\.(\\d+)"; //$NON-NLS-1$

}

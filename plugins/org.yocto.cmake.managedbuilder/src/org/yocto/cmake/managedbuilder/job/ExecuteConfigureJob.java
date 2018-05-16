/*******************************************************************************
 * Copyright (c) 2013 BMW Car IT GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * BMW Car IT - initial API and implementation
 *******************************************************************************/
package org.yocto.cmake.managedbuilder.job;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.CommandLauncherManager;
import org.eclipse.cdt.core.ICommandLauncher;
import org.eclipse.cdt.core.envvar.IContributedEnvironment;
import org.eclipse.cdt.core.envvar.IEnvironmentVariable;
import org.eclipse.cdt.core.envvar.IEnvironmentVariableManager;
import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.settings.model.ICConfigurationDescription;
import org.eclipse.cdt.core.settings.model.ICProjectDescription;
import org.eclipse.cdt.managedbuilder.core.BuildException;
import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.cdt.managedbuilder.core.ITool;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.console.IOConsoleOutputStream;
import org.yocto.cmake.managedbuilder.Activator;
import org.yocto.cmake.managedbuilder.YoctoCMakeMessages;
import org.yocto.cmake.managedbuilder.util.ConsoleUtility;


public class ExecuteConfigureJob extends Job {
	private Process configureProcess;
	private IProject project;
	private IConfiguration configuration;
	private IPath location;


	public ExecuteConfigureJob(String name,
			IProject project, IConfiguration configuration, IPath location) {
		super(name);
		this.project = project;
		this.configuration = configuration;
		this.location = location;


	}


	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected IStatus run(IProgressMonitor monitor) {
		monitor.beginTask(
				YoctoCMakeMessages.getString("ExecuteConfigureJob.runConfigure"), 20); //$NON-NLS-1$

		IOConsoleOutputStream cos =
				ConsoleUtility.getConsoleOutput(YoctoCMakeMessages.getFormattedString("ExecuteConfigureJob.consoleName", //$NON-NLS-1$
						project.getName()));
		ByteArrayOutputStream ces = new ByteArrayOutputStream();
		monitor.worked(1);

		try {
			return buildProject(monitor, cos, ces);
		} catch (InterruptedException e) {
			return new Status(Status.WARNING,
					Activator.PLUGIN_ID,
					YoctoCMakeMessages.getString("ExecuteConfigureJob.warning.aborted")); //$NON-NLS-1$
		} catch (CoreException e) {
			return new Status(Status.ERROR,
					Activator.PLUGIN_ID, Status.OK,
					YoctoCMakeMessages.getString("ExecuteConfigureJob.error.couldNotStart"), e); //$NON-NLS-1$	
		} catch (BuildException e) {
			return new Status(Status.ERROR,
					Activator.PLUGIN_ID, Status.OK,
					YoctoCMakeMessages.getString("ExecuteConfigureJob.error.couldNotStart"), e); //$NON-NLS-1$	
		} finally {
			try {
				cos.close();
			} catch (IOException e) {
				cos = null;
			}
		}
	}

	private IStatus buildProject(IProgressMonitor monitor,
			OutputStream stdout, OutputStream stderr) throws InterruptedException, CoreException, BuildException {

		ITool[] configureTools = configuration.getToolsBySuperClassId("org.yocto.cmake.managedbuilder.cmakeconfigure.gnu.exe"); //$NON-NLS-1$
		ITool configureTool = configureTools[0];
		String configureCommand = configuration.getToolCommand(configureTool);

		String[] toolFlags = configureTool.getToolCommandFlags(project.getLocation(), location);
		List<String> configureCommandFlags = new ArrayList<String>();
		
		for (String toolFlag : toolFlags) {
			if (toolFlag.contains(" ")) { //$NON-NLS-1$
				String[] separatedFlags = toolFlag.trim().split(" "); //$NON-NLS-1$
				configureCommandFlags.addAll(Arrays.asList(separatedFlags));
			} else {
				configureCommandFlags.add(toolFlag);
			}
		}
		
		List<String> result = new ArrayList<String>();

		ICProjectDescription cpdesc = CoreModel.getDefault().getProjectDescription(project, true);
		ICConfigurationDescription ccdesc = cpdesc.getActiveConfiguration();
		IEnvironmentVariableManager manager = CCorePlugin.getDefault().getBuildEnvironmentManager();
		IContributedEnvironment env = manager.getContributedEnvironment();

		for(IEnvironmentVariable var : env.getVariables(ccdesc)) {
			result.add(var.getName() + "=" + var.getValue());
		}

		
		ICommandLauncher launcher = CommandLauncherManager.getInstance().getCommandLauncher(project);
		launcher.setProject(project);

		monitor.subTask(YoctoCMakeMessages.getString("ExecuteConfigureJob.buildingMakefile")); //$NON-NLS-1$
		configureProcess = launcher.execute(new Path(configureCommand), configureCommandFlags.toArray(new String[]{}), result.toArray(new String[]{}), location, monitor);
		int exitValue = launcher.waitAndRead(stdout, stderr, monitor);
		
		monitor.worked(15);

		if (exitValue != 0) {
			return new Status(Status.ERROR, Activator.PLUGIN_ID,
					YoctoCMakeMessages.getFormattedString("ExecuteConfigureJob.error.buildFailed", //$NON-NLS-1$
															stderr.toString().trim()));
		}

		return Status.OK_STATUS;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.jobs.Job#canceling()
	 */
	/**
	 * Cancels the job and interrupts the system process.
	 * {@inheritDoc}
	 */
	@Override
	protected void canceling() {
		if (configureProcess != null)
			configureProcess.destroy();
	}
}

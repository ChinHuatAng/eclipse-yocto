package org.yocto.crops.docker.launcher.ui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.eclipse.core.resources.IProject;
import org.eclipse.linuxtools.docker.ui.launch.IErrorMessageHolder;

public class YoctoCropsContainerLauncher extends ContainerLauncher {

	@Override
	public Process runCommand(String connectionName, String imageName, IProject project,
			IErrorMessageHolder errMsgHolder, String command, String commandDir, String workingDir,
			List<String> additionalDirs, Map<String, String> origEnv, Properties envMap, boolean supportStdin,
			boolean privilegedMode, HashMap<String, String> labels, boolean keepContainer, Integer uid) {
		
		String pokyEntryCommand = "--workdir " + workingDir + " --cmd '" + command + "'"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				
		return super.runCommand(connectionName, imageName, project, errMsgHolder, pokyEntryCommand, commandDir, workingDir,
				additionalDirs, origEnv, envMap, supportStdin, privilegedMode, labels, keepContainer, uid);
	}

}

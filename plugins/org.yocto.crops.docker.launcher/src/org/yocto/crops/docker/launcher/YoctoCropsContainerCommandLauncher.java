/*******************************************************************************
 * Copyright (c) 2017 Intel Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Intel Corporation - initial API and implementation
 *******************************************************************************/ 
package org.yocto.crops.docker.launcher;

import org.eclipse.cdt.core.ICommandLauncher;
import org.eclipse.linuxtools.docker.ui.launch.IErrorMessageHolder;

public class YoctoCropsContainerCommandLauncher
		extends ContainerCommandLauncher
		implements ICommandLauncher, IErrorMessageHolder {

	@Override
	protected Integer getUid() {
		return null;
	}


}

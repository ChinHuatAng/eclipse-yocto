/*******************************************************************************
 * Copyright (c) 2010 Intel Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v1.0 which accompany this distribution.
 * The Eclipse Public License is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Intel - initial API and implementation
 *******************************************************************************/

package org.yocto.sdk.ide.actions;

import org.eclipse.linuxtools.internal.cdt.autotools.ui.Console;
import org.yocto.sdk.ide.YoctoSDKMessages;

@SuppressWarnings("restriction")
public class YoctoConsole extends Console {
	private static final String CONTEXT_MENU_ID = "YoctoConsole"; 
	private static final String CONSOLE_NAME = YoctoSDKMessages.getString("Console.SDK.Name");
	
	public YoctoConsole() {
		super(CONSOLE_NAME, CONTEXT_MENU_ID);
	}
}

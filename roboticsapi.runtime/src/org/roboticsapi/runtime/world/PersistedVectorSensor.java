/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.world;

import org.roboticsapi.core.Command;
import org.roboticsapi.core.CommandException;
import org.roboticsapi.core.CommandStatus;
import org.roboticsapi.runtime.rpi.NetcommValue;
import org.roboticsapi.world.sensor.VectorSensor;

public class PersistedVectorSensor extends VectorSensor {

	private final Command command;
	private final NetcommValue key;

	public PersistedVectorSensor(Command command, NetcommValue key) {
		super(command.getRuntime());
		this.command = command;
		this.key = key;
	}

	@Override
	public boolean isAvailable() {
		try {
			return command.getCommandHandle() != null
					&& command.getCommandHandle().getStatus() == CommandStatus.RUNNING;
		} catch (CommandException e) {
			return false;
		}
	}

	public Command getCommand() {
		return command;
	}

	public NetcommValue getKey() {
		return key;
	}

}
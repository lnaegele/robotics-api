/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.mockclass;

import org.roboticsapi.core.RoboticsRuntime;
import org.roboticsapi.world.Transformation;
import org.roboticsapi.world.sensor.TransformationSensor;

public class MockTransformationSensor extends TransformationSensor {
	private boolean available = true;

	public MockTransformationSensor(RoboticsRuntime runtime) {
		super(runtime);
	}

	public void setAvailable(boolean available) {
		this.available = available;
	}

	@Override
	public boolean isAvailable() {
		return available;
	}

	@Override
	protected Transformation calculateCheapValue() {
		return getDefaultValue();
	}
}
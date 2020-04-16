/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.core.mapper;

import org.roboticsapi.core.sensor.DoubleIsGreaterSensor;
import org.roboticsapi.runtime.SoftRobotRuntime;
import org.roboticsapi.runtime.core.primitives.DoubleGreater;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.net.StateDataflow;
import org.roboticsapi.runtime.mapping.parts.SensorMapper;
import org.roboticsapi.runtime.mapping.parts.SensorMappingContext;
import org.roboticsapi.runtime.mapping.parts.SensorMappingPorts;
import org.roboticsapi.runtime.mapping.result.SensorMapperResult;
import org.roboticsapi.runtime.mapping.result.impl.BooleanSensorMapperResult;

public class DoubleIsGreaterSensorMapper implements SensorMapper<SoftRobotRuntime, Boolean, DoubleIsGreaterSensor> {

	@Override
	public SensorMapperResult<Boolean> map(SoftRobotRuntime runtime, DoubleIsGreaterSensor sensor,
			SensorMappingPorts ports, SensorMappingContext context) throws MappingException {

		NetFragment result = new NetFragment("Double Greater");
		SensorMapperResult<Double> left = runtime.getMapperRegistry().mapSensor(runtime, sensor.getLeft(), result,
				context);
		SensorMapperResult<Double> right = runtime.getMapperRegistry().mapSensor(runtime, sensor.getRight(), result,
				context);

		NetFragment val = new NetFragment("Comparison");
		DoubleGreater greater = val.add(new DoubleGreater());

		val.connect(left.getSensorPort(), val.addInPort(left.getSensorPort().getType(), true, greater.getInFirst()));
		val.connect(right.getSensorPort(), val.addInPort(right.getSensorPort().getType(), true, greater.getInSecond()));

		result.add(val);
		DataflowOutPort resultPort = val.addOutPort(new StateDataflow(), false, greater.getOutValue());
		return new BooleanSensorMapperResult(result, resultPort);
	}
}
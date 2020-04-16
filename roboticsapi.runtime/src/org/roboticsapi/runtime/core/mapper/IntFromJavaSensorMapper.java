/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.core.mapper;

import org.roboticsapi.core.SensorListener;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.sensor.IntegerFromJavaSensor;
import org.roboticsapi.runtime.SoftRobotRuntime;
import org.roboticsapi.runtime.core.netcomm.WriteIntToNet;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.IntDataflow;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.net.TimestampDataflow;
import org.roboticsapi.runtime.mapping.parts.SensorMapper;
import org.roboticsapi.runtime.mapping.parts.SensorMappingContext;
import org.roboticsapi.runtime.mapping.parts.SensorMappingPorts;
import org.roboticsapi.runtime.mapping.result.impl.IntSensorMapperResult;

public class IntFromJavaSensorMapper implements SensorMapper<SoftRobotRuntime, Integer, IntegerFromJavaSensor> {
	private static int nr = 0;

	@Override
	public IntSensorMapperResult map(final SoftRobotRuntime runtime, final IntegerFromJavaSensor sensor,
			SensorMappingPorts ports, SensorMappingContext context) throws MappingException {
		NetFragment net = new NetFragment("IntFromJavaSensor");
		final NetFragment inputNet = new NetFragment("Value");
		net.add(inputNet);
		final WriteIntToNet netcomm = new WriteIntToNet("IntFromJava" + nr++, sensor.getDefaultValue());
		inputNet.add(netcomm);
		DataflowOutPort valuePort = inputNet.addOutPort(new IntDataflow(), true, netcomm.getOutValue());
		try {
			sensor.addListener(new SensorListener<Integer>() {
				@Override
				public void onValueChanged(Integer newValue) {
					netcomm.getNetcomm().setString("" + newValue);
				}
			});
		} catch (RoboticsException e) {
			throw new MappingException(e);
		}
		DataflowOutPort timePort = inputNet.addOutPort(new TimestampDataflow(), true, netcomm.getOutLastUpdated());
		return new IntSensorMapperResult(net, valuePort, timePort);
	}

}
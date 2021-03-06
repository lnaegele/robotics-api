/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.multijoint.runtime.rpi.mapper;

import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.core.actuator.ActuatorNotOperationalException;
import org.roboticsapi.core.actuator.ConcurrentAccessException;
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;
import org.roboticsapi.facet.runtime.rpi.RpiException;
import org.roboticsapi.facet.runtime.rpi.core.primitives.BooleanIsNull;
import org.roboticsapi.facet.runtime.rpi.core.primitives.BooleanNot;
import org.roboticsapi.facet.runtime.rpi.core.primitives.BooleanPre;
import org.roboticsapi.facet.runtime.rpi.core.primitives.BooleanValue;
import org.roboticsapi.facet.runtime.rpi.mapping.ActuatorDriverMapper;
import org.roboticsapi.facet.runtime.rpi.mapping.ActuatorFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.IndexedActuatorDriver;
import org.roboticsapi.facet.runtime.rpi.mapping.MapperRegistry;
import org.roboticsapi.facet.runtime.rpi.mapping.MappingException;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueConsumerFragment;
import org.roboticsapi.framework.multijoint.IllegalJointValueException;
import org.roboticsapi.framework.multijoint.Joint;
import org.roboticsapi.framework.multijoint.runtime.rpi.primitives.JointPosition;
import org.roboticsapi.framework.multijoint.runtime.rpi.result.JointResetActionResult;

public class JointDriverJointResetMapper
		implements ActuatorDriverMapper<IndexedActuatorDriver<Joint>, JointResetActionResult> {

	@Override
	public RealtimeValueConsumerFragment map(IndexedActuatorDriver<Joint> actuatorDriver,
			JointResetActionResult actionResult, DeviceParameterBag parameters, MapperRegistry registry,
			RealtimeBoolean cancel, RealtimeDouble override, RealtimeDouble time)
			throws MappingException, RpiException {

		JointPosition jointPosition = new JointPosition(actuatorDriver.getRpiDeviceName(), actuatorDriver.getIndex());

		// Fix for forcing joint velocity to 0
		// Apply position for two cycles
		BooleanValue value = new BooleanValue(true);
		BooleanPre pre = new BooleanPre();
		BooleanIsNull isNull = new BooleanIsNull();
		BooleanNot not = new BooleanNot();
		
		ActuatorFragment ret = new ActuatorFragment(actuatorDriver, not.getOutValue(), jointPosition, value, pre, isNull, not);
		ret.connect(value.getOutValue(), pre.getInValue());
		ret.connect(pre.getOutValue(), isNull.getInValue());
		ret.connect(isNull.getOutValue(), not.getInValue());
		
		ret.addDependency(actionResult.getPosition(), "inPosition", jointPosition.getInPosition());

		ret.addException(ConcurrentAccessException.class, jointPosition.getOutErrorConcurrentAccess(),
				"outConcurrentAccess");
		ret.addException(ActuatorNotOperationalException.class, jointPosition.getOutErrorJointFailed(),
				"outJointFailed");
		ret.addException(IllegalJointValueException.class, jointPosition.getOutErrorIllegalPosition(),
				"outIllegalPosition");

		return ret;
	}
}

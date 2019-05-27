/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.robot.mapper;

import org.roboticsapi.cartesianmotion.action.CartesianMotionPlan;
import org.roboticsapi.cartesianmotion.action.PathMotion;
import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.core.action.PlannedAction;
import org.roboticsapi.core.actuator.OverrideParameter;
import org.roboticsapi.multijoint.parameter.JointDeviceParameters;
import org.roboticsapi.robot.action.NullspacePTPFromMotion;
import org.roboticsapi.runtime.SoftRobotRuntime;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.ComposedDataflowOutPort;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.parts.ActionMapper;
import org.roboticsapi.runtime.mapping.parts.ActionMappingContext;
import org.roboticsapi.runtime.mapping.result.ActionMapperResult;
import org.roboticsapi.runtime.mapping.result.ActionResult;
import org.roboticsapi.runtime.mapping.result.impl.WrappedActionMapperResult;
import org.roboticsapi.runtime.multijoint.mapper.JointPositionActionResult;
import org.roboticsapi.runtime.multijoint.mapper.fragments.PTPFromMotionFragment;
import org.roboticsapi.runtime.robot.NullspaceJointsDataflow;
import org.roboticsapi.runtime.rpi.RPIException;

public class NullspacePTPFromMotionMapper implements ActionMapper<SoftRobotRuntime, NullspacePTPFromMotion> {

	@Override
	public ActionMapperResult map(SoftRobotRuntime runtime, NullspacePTPFromMotion action,
			DeviceParameterBag parameters, ActionMappingContext ports) throws MappingException, RPIException {

		final double[] from = action.getFrom(), to = action.getTo();
		final double[] realStart = action.getRealStart(), realStartVel = action.getRealStartVel();

		final PathMotion<?> path = action.getPath();

		// check validity of parameters and positions
		final JointDeviceParameters p = parameters.get(JointDeviceParameters.class);
		if (p == null) {
			throw new MappingException("No joint device parameters given");
		}

		if (from == null || from.length != p.getJointCount()) {
			throw new MappingException("Invalid start position (numAxes)");
		}
		if (to == null || to.length != p.getJointCount()) {
			throw new MappingException("Invalid destination position (numAxes)");
		}

		final ActionMapperResult mappedPath = runtime.getMapperRegistry().mapAction(runtime, path, parameters,
				ports.cancelPort, ports.overridePort, ports.actionPlans);

		CartesianMotionPlan plan = (CartesianMotionPlan) ports.actionPlans.get(path);
		double duration = plan.getTotalTime();
		double timeProgressStart = plan.getTimeAtFirstOccurence(new PlannedAction.TimeProgressState(0));

		NetFragment ret = new NetFragment("NullspacePTP");

		PTPFromMotionFragment nullspacePTP = ret
				.add(new PTPFromMotionFragment(ports, p, parameters.get(OverrideParameter.class), from, to, realStart,
						realStartVel, (duration - timeProgressStart) / 2, 0, (duration + timeProgressStart) / 2));

		ret.add(mappedPath.getNetFragment());
		DataflowOutPort hintjointPort = nullspacePTP.reinterpret(nullspacePTP.getResultPort(),
				new NullspaceJointsDataflow(from.length, null));

		DataflowOutPort pathPort = mappedPath.getActionResult().getOutPort();

		ComposedDataflowOutPort resultPort = new ComposedDataflowOutPort(false, hintjointPort, pathPort);

		ActionResult result = new JointPositionActionResult(resultPort);
		return new WrappedActionMapperResult(action, path, ret, result, mappedPath);
	}

}
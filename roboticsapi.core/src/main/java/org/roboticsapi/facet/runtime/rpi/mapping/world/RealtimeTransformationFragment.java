/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.mapping.world;

import org.roboticsapi.core.Command;
import org.roboticsapi.core.RealtimeValue;
import org.roboticsapi.core.RealtimeValueListener;
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;
import org.roboticsapi.core.world.Transformation;
import org.roboticsapi.facet.runtime.rpi.ActiveFragment;
import org.roboticsapi.facet.runtime.rpi.FragmentInPort;
import org.roboticsapi.facet.runtime.rpi.NetcommListener;
import org.roboticsapi.facet.runtime.rpi.NetcommValue;
import org.roboticsapi.facet.runtime.rpi.OutPort;
import org.roboticsapi.facet.runtime.rpi.Primitive;
import org.roboticsapi.facet.runtime.rpi.mapping.InterNetcommFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.MappingException;
import org.roboticsapi.facet.runtime.rpi.mapping.ObserverFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueConsumerFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueFragment;
import org.roboticsapi.facet.runtime.rpi.world.netcomm.ReadFrameFromNet;
import org.roboticsapi.facet.runtime.rpi.world.primitives.FrameNetcommOut;
import org.roboticsapi.facet.runtime.rpi.world.primitives.FrameSetNull;
import org.roboticsapi.facet.runtime.rpi.world.types.RPIFrame;

public class RealtimeTransformationFragment extends RealtimeValueFragment<Transformation> {

	public RealtimeTransformationFragment(RealtimeValue<Transformation> value, OutPort result, OutPort time,
			Primitive... children) {
		super(value, result, time, children);
	}

	public RealtimeTransformationFragment(RealtimeValue<Transformation> value, OutPort result, Primitive... children) {
		super(value, result, children);
	}

	public RealtimeTransformationFragment(RealtimeValue<Transformation> value) {
		super(value);
	}

	private static int nr = 0;

	@Override
	public RealtimeValueConsumerFragment createObserverFragment(RealtimeBoolean condition,
			final RealtimeValueListener<Transformation> observer) throws MappingException {
		ReadFrameFromNet netcomm = new ReadFrameFromNet("r" + (nr++));
		RealtimeValueConsumerFragment ret = new ObserverFragment(netcomm);
		netcomm.getNetcomm().addNetcommListener(new NetcommListener() {
			@Override
			public void valueChanged(NetcommValue value) {
				RPIFrame rpi = new RPIFrame(value.getString());
				observer.onValueChanged(new Transformation(rpi.getPos().getX().get(), rpi.getPos().getY().get(),
						rpi.getPos().getZ().get(), rpi.getRot().getA().get(), rpi.getRot().getB().get(),
						rpi.getRot().getC().get()));
			}

			@Override
			public void updatePerformed() {
			}
		});

		if (condition != null) {
			FrameSetNull setNull = ret.add(new FrameSetNull());
			ret.connect(setNull.getOutValue(), netcomm.getInValue());
			ret.addDependency(getRealtimeValue(), ret.addInPort("inValue", setNull.getInValue()));
			ret.addDependency(condition.not(), ret.addInPort("inCondition", setNull.getInNull()));
		} else {
			ret.addDependency(getRealtimeValue(), ret.addInPort("inValue", netcomm.getInValue()));
		}
		return ret;
	}

	@Override
	public InterNetcommFragment createInterNetcommFragment(RealtimeBoolean condition) {
		ActiveFragment active = new ActiveFragment();
		String key = "itr" + (nr++);
		FrameNetcommOut netcomm = active.add(new FrameNetcommOut(key, false));
		FragmentInPort inPort = active.addInPort("inValue", netcomm.getInValue());
		InterNetcommFragment ret = new InterNetcommFragment(key);
		ret.add(active);
		ret.addDependency(getRealtimeValue(), ret.addInPort("inValue", inPort));
		ret.addDependency(condition, ret.addInPort("inEnabled", active.getInActive()));
		return ret;
	}

	@Override
	public RealtimeValue<Transformation> createInterNetcommValue(Command command, String key) {
		return new PersistedRealtimeTransformation(command, key);
	}

}

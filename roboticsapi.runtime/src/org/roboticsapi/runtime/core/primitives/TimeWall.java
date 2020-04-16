package org.roboticsapi.runtime.core.primitives;

import org.roboticsapi.runtime.rpi.OutPort;
import org.roboticsapi.runtime.rpi.Primitive;

/**
 * A time module, returns the current (physical, wall) time
 */
public class TimeWall extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "Core::TimeWall";

	/** The current time in ticks */
	private final OutPort outValue = new OutPort("outValue");

	public TimeWall() {
		super(PRIMITIVE_TYPE);

		// Add all ports
		add(outValue);

		// Add all parameters
	}

	/**
	 * The current time in ticks
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutValue() {
		return this.outValue;
	}

}
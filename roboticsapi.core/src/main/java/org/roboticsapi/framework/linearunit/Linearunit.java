/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.linearunit;

import org.roboticsapi.framework.multijoint.MultiJointDevice;

public interface Linearunit extends MultiJointDevice {

	public int getSliderCount();

	public LinearunitSlider getSlider(int index);

	public LinearunitSlider[] getSliders();

}

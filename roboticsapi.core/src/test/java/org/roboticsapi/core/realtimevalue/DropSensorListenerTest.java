/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.realtimevalue;

import static org.junit.Assert.assertNull;

import org.junit.Test;

public class DropSensorListenerTest {
	private class MockDropSensorListener<T> extends DropRealtimeValueListener<T> {
		private Object currentValue = null;

		public Object getCurrentValue() {
			return currentValue;
		}

		@Override
		protected void valueChanged(Object newValue) {
			currentValue = newValue;
		}
	}

	@Test
	public void testOnValueChangedWithMockDropSensorListenerSimpleTest() {
		MockDropSensorListener<Integer> mockListener = new MockDropSensorListener<Integer>();

		assertNull(mockListener.getCurrentValue());

		mockListener.onValueChanged(1);

		// assertSame(1, mockListener.getCurrentValue());
	}
}

/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world.realtimevalue.realtimematrix;

import org.roboticsapi.core.matrix.Matrix;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDoubleArray;

public class AppliedMatrixRealtimeDoubleArray extends RealtimeDoubleArray {

	private final RealtimeMatrix matrix;
	private final RealtimeDoubleArray vector;

	public AppliedMatrixRealtimeDoubleArray(RealtimeMatrix matrix, RealtimeDoubleArray vector) {
		super(matrix.getRowDimension(), matrix, vector);
		this.matrix = matrix;
		this.vector = vector;
	}

	@Override
	protected Double[] calculateCheapValue() {
		Matrix mat = matrix.getCheapValue();
		Double[] vec = vector.getCheapValue();
		if (mat == null || vec == null) {
			return null;
		}
		double[] vec2 = new double[vec.length];
		for (int i = 0; i < vec2.length; i++) {
			vec2[i] = vec[i];
		}
		double[] ret = mat.apply(vec2);
		Double[] ret2 = new Double[ret.length];
		for (int i = 0; i < ret.length; i++) {
			ret2[i] = ret[i];
		}
		return ret2;
	}

	public RealtimeMatrix getMatrix() {
		return matrix;
	}

	public RealtimeDoubleArray getVector() {
		return vector;
	}

	@Override
	public boolean isAvailable() {
		return areAvailable(matrix, vector);
	}

}

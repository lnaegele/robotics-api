/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.mapping.result.impl;

import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.result.TransactionMapperResult;

public class SimpleTransactionMapperResult implements TransactionMapperResult {

	private final NetFragment fragment;
	private final DataflowOutPort terminationPort;
	private final DataflowOutPort failurePort;

	public SimpleTransactionMapperResult(NetFragment fragment, DataflowOutPort terminationPort,
			DataflowOutPort failurePort) {
		this.fragment = fragment;
		this.terminationPort = terminationPort;
		this.failurePort = failurePort;
	}

	@Override
	public NetFragment getNetFragment() {
		return fragment;
	}

	@Override
	public DataflowOutPort getTerminationPort() {
		return terminationPort;
	}

	@Override
	public DataflowOutPort getFailurePort() {
		return failurePort;
	}

}
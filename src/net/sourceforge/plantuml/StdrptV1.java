/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2020, Arnaud Roques
 *
 * Project Info:  http://plantuml.com
 * 
 * If you like this project or if you find it useful, you can support us at:
 * 
 * http://plantuml.com/patreon (only 1$ per month!)
 * http://plantuml.com/paypal
 * 
 * This file is part of PlantUML.
 *
 * Licensed under The MIT License (Massachusetts Institute of Technology License)
 * 
 * See http://opensource.org/licenses/MIT
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR
 * IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
 *
 * Original Author:  Arnaud Roques
 */
package net.sourceforge.plantuml;

import java.io.PrintStream;

import net.sourceforge.plantuml.command.PSystemAbstractFactory;
import net.sourceforge.plantuml.core.Diagram;
import net.sourceforge.plantuml.eggs.PSystemWelcome;
import net.sourceforge.plantuml.error.PSystemError;

public class StdrptV1 implements Stdrpt {

	public void printInfo(final PrintStream output, Diagram sys) {
		if (sys instanceof PSystemWelcome) {
			sys = null;
		}
		if (sys == null || sys instanceof PSystemError) {
			out(output, (PSystemError) sys);
		}
	}

	private void out(final PrintStream output, final PSystemError err) {
		output.println("protocolVersion=1");
		if (empty(err)) {
			output.println("status=NO_DATA");
		} else {
			output.println("status=ERROR");
			output.println("lineNumber=" + err.getLineLocation().getPosition());
			for (ErrorUml er : err.getErrorsUml()) {
				output.println("label=" + er.getError());
			}
		}
		output.flush();
	}

	private boolean empty(final PSystemError err) {
		if (err == null) {
			return true;
		}
		for (ErrorUml er : err.getErrorsUml()) {
			if (PSystemAbstractFactory.EMPTY_DESCRIPTION.equals(er.getError()))
				return true;
		}
		return false;
	}

}

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
package net.sourceforge.plantuml.timingdiagram;

import java.math.BigDecimal;

import net.sourceforge.plantuml.command.regex.IRegex;
import net.sourceforge.plantuml.command.regex.RegexConcat;
import net.sourceforge.plantuml.command.regex.RegexLeaf;
import net.sourceforge.plantuml.command.regex.RegexOptional;
import net.sourceforge.plantuml.command.regex.RegexOr;
import net.sourceforge.plantuml.command.regex.RegexResult;

public class TimeTickBuilder {

	public static IRegex expressionAtWithoutArobase(String name) {
		return new RegexOr( //
				new RegexLeaf(name + "DATE", "(\\d+)/(\\d+)/(\\d+)"), //
				new RegexLeaf(name + "HOUR", "(\\d+):(\\d+):(\\d+)"), //
				new RegexLeaf(name + "DIGIT", "(\\+?)(-?\\d+\\.?\\d*)"), //
				new RegexLeaf(name + "CLOCK", "([\\p{L}0-9_.@]+)\\*(\\d+)"));
	}

	public static IRegex expressionAtWithArobase(String name) {
		return new RegexConcat( //
				new RegexLeaf("@"), //
				expressionAtWithoutArobase(name));
	}

	public static IRegex optionalExpressionAtWithArobase(String name) {
		return new RegexOptional(expressionAtWithArobase(name));
	}

	public static TimeTick parseTimeTick(String name, RegexResult arg, Clocks clock) {
		final String clockName = arg.get(name + "CLOCK", 0);
		if (clockName != null) {
			final int number = Integer.parseInt(arg.get(name + "CLOCK", 1));
			return clock.getClockValue(clockName, number);
		}
		final String hour = arg.get(name + "HOUR", 0);
		if (hour != null) {
			final int h = Integer.parseInt(arg.get(name + "HOUR", 0));
			final int m = Integer.parseInt(arg.get(name + "HOUR", 1));
			final int s = Integer.parseInt(arg.get(name + "HOUR", 2));
			final BigDecimal value = new BigDecimal(3600 * h + 60 * m + s);
			return new TimeTick(value, TimingFormat.HOUR);
		}
		final String date = arg.get(name + "DATE", 0);
		if (date != null) {
			final int yy = Integer.parseInt(arg.get(name + "DATE", 0));
			final int mm = Integer.parseInt(arg.get(name + "DATE", 1));
			final int dd = Integer.parseInt(arg.get(name + "DATE", 2));

			return TimingFormat.createDate(yy, mm, dd);
		}
		final String number = arg.get(name + "DIGIT", 1);
		if (number == null) {
			return clock.getNow();
		}
		final boolean isRelative = "+".equals(arg.get(name + "DIGIT", 0));
		BigDecimal value = new BigDecimal(number);
		if (isRelative) {
			value = clock.getNow().getTime().add(value);
		}
		return new TimeTick(value, TimingFormat.DECIMAL);
	}

}

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
package net.sourceforge.plantuml.activitydiagram3.ftile;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.plantuml.ugraphic.UTranslate;

public class WormMutation {

	private final List<UTranslate> translations = new ArrayList<UTranslate>();

	private WormMutation() {

	}

	public static WormMutation create(Worm worm, double delta) {
		final String signature = worm.getDirectionsCode();
		final String definition = getDefinition(signature);
		if (definition == null) {
			return createFromLongSignature(signature, delta);
		}
		return new WormMutation(definition, delta);
	}

	private static WormMutation createFromLongSignature(final String signature, final double delta) {
		final WormMutation result = new WormMutation();
		for (int i = 0; i < signature.length() - 1; i++) {
			WormMutation tmp = new WormMutation(getDefinition(signature.substring(i, i + 2)), delta);
			if (i == 0) {
				result.translations.add(tmp.translations.get(0));
			} else {
				UTranslate last = result.getLast();
				if (last.isAlmostSame(tmp.translations.get(0)) == false) {
					tmp = tmp.reverse();
				}
			}
			result.translations.add(tmp.translations.get(1));
			if (i == signature.length() - 2) {
				result.translations.add(tmp.translations.get(2));
			}
		}
		return result;
	}

	private WormMutation reverse() {
		final WormMutation result = new WormMutation();
		for (UTranslate tr : translations) {
			result.translations.add(tr.reverse());
		}
		return result;
	}

	public UTranslate getLast() {
		return translations.get(translations.size() - 1);
	}

	public UTranslate getFirst() {
		return translations.get(0);
	}

	public int size() {
		return translations.size();
	}

	private static String getDefinition(final String signature) {
		if (signature.equals("D") || signature.equals("U")) {
			return "33";
		} else if (signature.equals("L") || signature.equals("R")) {
			return "55";
		} else if (signature.equals("RD")) {
			return "123";
		} else if (signature.equals("RU")) {
			return "543";
		} else if (signature.equals("LD")) {
			return "187";
		} else if (signature.equals("DL")) {
			return "345";
		} else if (signature.equals("DR")) {
			return "765";
		} else if (signature.equals("UL")) {
			return "321";
		} else if (signature.equals("UR")) {
			return "781";
			// } else if (signature.equals("DLD")) {
			// return "3443";
		}
		return null;
	}

	private WormMutation(String definition, double delta) {
		if (definition == null) {
			throw new IllegalArgumentException();
		}
		for (int i = 0; i < definition.length(); i++) {
			this.translations.add(translation(Integer.parseInt(definition.substring(i, i + 1)), delta));
		}

	}

	private static UTranslate translation(int type, double delta) {
		switch (type) {
		case 1:
			return new UTranslate(0, -delta);
		case 2:
			return new UTranslate(delta, -delta);
		case 3:
			return new UTranslate(delta, 0);
		case 4:
			return new UTranslate(delta, delta);
		case 5:
			return new UTranslate(0, delta);
		case 6:
			return new UTranslate(-delta, delta);
		case 7:
			return new UTranslate(-delta, 0);
		case 8:
			return new UTranslate(-delta, -delta);
		}
		throw new IllegalArgumentException();
	}

	static private class MinMax {

		private double min = Double.MAX_VALUE;
		private double max = Double.MIN_VALUE;

		private void append(double v) {
			if (v > max) {
				max = v;
			}
			if (v < min) {
				min = v;
			}
		}

		private double getExtreme() {
			if (Math.abs(max) > Math.abs(min)) {
				return max;
			}
			return min;
		}

	}

	public UTranslate getTextTranslate(int size) {
		final MinMax result = new MinMax();
		for (UTranslate tr : translations) {
			result.append(tr.getDx());
		}
		return new UTranslate(result.getExtreme() * (size - 1), 0);
	}

	public boolean isDxNegative() {
		return translations.get(0).getDx() < 0;
	}

	public Worm mute(Worm original) {
		final Worm result = new Worm();
		for (int i = 0; i < original.size(); i++) {
			result.addPoint(translations.get(i).getTranslated(original.get(i)));
		}
		return result;
	}

}

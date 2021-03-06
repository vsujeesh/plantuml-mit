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
package net.sourceforge.plantuml.svek;

import java.awt.Color;
import java.awt.geom.Dimension2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.plantuml.BackSlash;
import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.OptionPrint;
import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.cucadiagram.dot.GraphvizUtils;
import net.sourceforge.plantuml.flashcode.FlashCodeFactory;
import net.sourceforge.plantuml.flashcode.FlashCodeUtils;
import net.sourceforge.plantuml.fun.IconLoader;
import net.sourceforge.plantuml.graphic.AbstractTextBlock;
import net.sourceforge.plantuml.graphic.GraphicPosition;
import net.sourceforge.plantuml.graphic.GraphicStrings;
import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.graphic.HtmlColorUtils;
import net.sourceforge.plantuml.graphic.QuoteUtils;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UImage;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.version.Version;

public class GraphvizCrash extends AbstractTextBlock implements IEntityImage {

	private final TextBlockBackcolored graphicStrings;
	private final BufferedImage flashCode;
	private final String text;

	public GraphvizCrash(String text) {
		this.text = text;
		final FlashCodeUtils utils = FlashCodeFactory.getFlashCodeUtils();
		this.flashCode = utils.exportFlashcode(text, Color.BLACK, Color.WHITE);
		this.graphicStrings = GraphicStrings.createBlackOnWhite(init(), IconLoader.getRandom(),
				GraphicPosition.BACKGROUND_CORNER_TOP_RIGHT);
	}

	public static List<String> anErrorHasOccured(Throwable exception, String text) {
		final List<String> strings = new ArrayList<String>();
		if (exception == null) {
			strings.add("An error has occured!");
		} else {
			strings.add("An error has occured : " + exception);
		}
		final String quote = StringUtils.rot(QuoteUtils.getSomeQuote());
		strings.add("<i>" + quote);
		strings.add(" ");
		strings.add("Diagram size: " + lines(text) + " lines / " + text.length() + " characters.");
		strings.add(" ");
		return strings;
	}

	private static int lines(String text) {
		int result = 0;
		for (int i = 0; i < text.length(); i++) {
			if (text.charAt(i) == BackSlash.CHAR_NEWLINE) {
				result++;
			}
		}
		return result;
	}

	public static void checkOldVersionWarning(final List<String> strings) {
		final long days = (System.currentTimeMillis() - Version.compileTime()) / 1000L / 3600 / 24;
		if (days >= 90) {
			strings.add("This version of PlantUML is " + days + " days old, so you should");
			strings.add("  consider upgrading from http://plantuml.com/download");
		}
	}

	public static void pleaseGoTo(final List<String> strings) {
		strings.add(" ");
		strings.add("Please go to http://plantuml.com/graphviz-dot to check your GraphViz version.");
		strings.add(" ");
	}

	public static void youShouldSendThisDiagram(final List<String> strings) {
		strings.add("You should send this diagram and this image to <b>plantuml@gmail.com</b> or");
		strings.add("post to <b>http://plantuml.com/qa</b> to solve this issue.");
		strings.add("You can try to turn arround this issue by simplifing your diagram.");
	}

	public static void thisMayBeCaused(final List<String> strings) {
		strings.add("This may be caused by :");
		strings.add(" - a bug in PlantUML");
		strings.add(" - a problem in GraphViz");
	}

	private List<String> init() {
		final List<String> strings = anErrorHasOccured(null, text);
		strings.add("For some reason, dot/GraphViz has crashed.");
		strings.add("This has been generated with PlantUML (" + Version.versionString() + ").");
		checkOldVersionWarning(strings);
		strings.add(" ");
		addProperties(strings);
		strings.add(" ");
		try {
			final String dotVersion = GraphvizUtils.dotVersion();
			strings.add("Default dot version: " + dotVersion);
		} catch (Throwable e) {
			strings.add("Cannot determine dot version: " + e.toString());
		}
		pleaseGoTo(strings);
		youShouldSendThisDiagram(strings);
		if (flashCode != null) {
			addDecodeHint(strings);
		}

		return strings;
	}

	public static void addDecodeHint(final List<String> strings) {
		strings.add(" ");
		strings.add(" Diagram source: (Use http://zxing.org/w/decode.jspx to decode the qrcode)");
	}

	public static void addProperties(final List<String> strings) {
		strings.addAll(OptionPrint.interestingProperties());
		strings.addAll(OptionPrint.interestingValues());
	}

	// private static void addTextProperty(final List<String> strings, String prop) {
	// strings.add(prop + ": " + System.getProperty(prop));
	// }

	public boolean isHidden() {
		return false;
	}

	public HtmlColor getBackcolor() {
		return HtmlColorUtils.WHITE;
	}

	public Dimension2D calculateDimension(StringBounder stringBounder) {
		Dimension2D result = graphicStrings.calculateDimension(stringBounder);
		if (flashCode != null) {
			result = Dimension2DDouble.mergeTB(result,
					new Dimension2DDouble(flashCode.getWidth(), flashCode.getHeight()));
		}
		return result;
	}

	public void drawU(UGraphic ug) {
		graphicStrings.drawU(ug);
		if (flashCode != null) {
			final double h = graphicStrings.calculateDimension(ug.getStringBounder()).getHeight();
			ug = ug.apply(new UTranslate(0, h));
			ug.draw(new UImage(flashCode).scaleNearestNeighbor(3));
		}
	}

	public ShapeType getShapeType() {
		return ShapeType.RECTANGLE;
	}

	public Margins getShield(StringBounder stringBounder) {
		return Margins.NONE;
	}
	
	public double getOverscanX(StringBounder stringBounder) {
		return 0;
	}


}

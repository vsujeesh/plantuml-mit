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
package net.sourceforge.plantuml.style;

import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;

import net.sourceforge.plantuml.ISkinSimple;
import net.sourceforge.plantuml.LineBreakStrategy;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.graphic.IHtmlColorSet;
import net.sourceforge.plantuml.graphic.SymbolContext;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.TextBlockUtils;
import net.sourceforge.plantuml.graphic.color.ColorType;
import net.sourceforge.plantuml.graphic.color.Colors;
import net.sourceforge.plantuml.ugraphic.UChangeColor;
import net.sourceforge.plantuml.ugraphic.UFont;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UStroke;

public class Style {

	private final Map<PName, Value> map;
	private final StyleSignature signature;

	public Style(StyleSignature signature, Map<PName, Value> map) {
		this.map = map;
		this.signature = signature;
	}

	@Override
	public String toString() {
		return signature + " " + map;
	}

	public Value value(PName name) {
		final Value result = map.get(name);
		if (result == null) {
			return ValueNull.NULL;
		}
		return result;
	}

	public Style mergeWith(Style other) {
		if (other == null) {
			return this;
		}
		final EnumMap<PName, Value> both = new EnumMap<PName, Value>(this.map);
		for (Entry<PName, Value> ent : other.map.entrySet()) {
			final Value previous = this.map.get(ent.getKey());
			if (previous == null || ent.getValue().getPriority() > previous.getPriority()) {
				both.put(ent.getKey(), ent.getValue());
			}
		}
		return new Style(this.signature.mergeWith(other.getSignature()), both);
		// if (this.name.equals(other.name)) {
		// return new Style(this.kind.add(other.kind), this.name, both);
		// }
		// return new Style(this.kind.add(other.kind), this.name + "," + other.name, both);
	}

	public Style eventuallyOverride(PName param, HtmlColor color) {
		if (color == null) {
			return this;
		}
		final EnumMap<PName, Value> result = new EnumMap<PName, Value>(this.map);
		final Value old = result.get(param);
		result.put(param, new ValueColor(color, old.getPriority()));
		// return new Style(kind, name + "-" + color, result);
		return new Style(this.signature, result);
	}

	public Style eventuallyOverride(Colors colors) {
		Style result = this;
		if (colors != null) {
			final HtmlColor back = colors.getColor(ColorType.BACK);
			if (back != null) {
				result = result.eventuallyOverride(PName.BackGroundColor, back);
			}
			final HtmlColor line = colors.getColor(ColorType.LINE);
			if (line != null) {
				result = result.eventuallyOverride(PName.LineColor, line);
			}
		}
		return result;
	}

	public Style eventuallyOverride(SymbolContext symbolContext) {
		Style result = this;
		if (symbolContext != null) {
			final HtmlColor back = symbolContext.getBackColor();
			if (back != null) {
				result = result.eventuallyOverride(PName.BackGroundColor, back);
			}
		}
		return result;
	}

	public StyleSignature getSignature() {
		return signature;
	}

	public UFont getUFont() {
		final String family = value(PName.FontName).asString();
		final int fontStyle = value(PName.FontStyle).asFontStyle();
		final int size = value(PName.FontSize).asInt();
		return new UFont(family, fontStyle, size);
	}

	public FontConfiguration getFontConfiguration(IHtmlColorSet set) {
		final UFont font = getUFont();
		final HtmlColor color = value(PName.FontColor).asColor(set);
		final HtmlColor hyperlinkColor = value(PName.HyperLinkColor).asColor(set);
		return new FontConfiguration(font, color, hyperlinkColor, true);
	}

	public SymbolContext getSymbolContext(IHtmlColorSet set) {
		final HtmlColor backColor = value(PName.BackGroundColor).asColor(set);
		final HtmlColor foreColor = value(PName.LineColor).asColor(set);
		final double deltaShadowing = value(PName.Shadowing).asDouble();
		return new SymbolContext(backColor, foreColor).withStroke(getStroke()).withDeltaShadow(deltaShadowing);
	}

	public UStroke getStroke() {
		final double thickness = value(PName.LineThickness).asDouble();
		final String dash = value(PName.LineStyle).asString();
		if (dash.length() == 0) {
			return new UStroke(thickness);
		}
		try {
			final StringTokenizer st = new StringTokenizer(dash, "-;,");
			final double dashVisible = Double.parseDouble(st.nextToken().trim());
			double dashSpace = dashVisible;
			if (st.hasMoreTokens()) {
				dashSpace = Double.parseDouble(st.nextToken().trim());
			}
			return new UStroke(dashVisible, dashSpace, thickness);
		} catch (Exception e) {
			return new UStroke(thickness);
		}
	}

	public LineBreakStrategy wrapWidth() {
		final String value = value(PName.MaximumWidth).asString();
		return new LineBreakStrategy(value);
	}

	public ClockwiseTopRightBottomLeft getPadding() {
		final double padding = value(PName.Padding).asDouble();
		return new ClockwiseTopRightBottomLeft(padding);
	}

	public ClockwiseTopRightBottomLeft getMargin() {
		final double padding = value(PName.Margin).asDouble();
		return new ClockwiseTopRightBottomLeft(padding);
	}

	public HorizontalAlignment getHorizontalAlignment() {
		return value(PName.HorizontalAlignment).asHorizontalAlignment();
	}

	private TextBlock createTextBlockInternal(Display display, IHtmlColorSet set, ISkinSimple spriteContainer,
			HorizontalAlignment alignment) {
		final FontConfiguration fc = getFontConfiguration(set);
		return display.create(fc, alignment, spriteContainer);
	}

	public TextBlock createTextBlockBordered(Display note, IHtmlColorSet set, ISkinSimple spriteContainer) {
		// final HorizontalAlignment alignment = HorizontalAlignment.LEFT;
		final HorizontalAlignment alignment = this.getHorizontalAlignment();
		final TextBlock textBlock = this.createTextBlockInternal(note, set, spriteContainer, alignment);

		final HtmlColor legendBackgroundColor = this.value(PName.BackGroundColor).asColor(set);
		final HtmlColor legendColor = this.value(PName.LineColor).asColor(set);
		final UStroke stroke = this.getStroke();
		final int cornersize = this.value(PName.RoundCorner).asInt();
		final double margin = this.value(PName.Margin).asDouble();
		final double padding = this.value(PName.Padding).asDouble();

		final TextBlock result = TextBlockUtils.bordered(textBlock, stroke, legendColor, legendBackgroundColor,
				cornersize, padding, padding);
		return TextBlockUtils.withMargin(result, margin, margin);
	}

	public UGraphic applyStrokeAndLineColor(UGraphic ug, IHtmlColorSet colorSet) {
		ug = ug.apply(new UChangeColor(value(PName.LineColor).asColor(colorSet)));
		ug = ug.apply(getStroke());
		return ug;
	}

}

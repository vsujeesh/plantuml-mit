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
package net.sourceforge.plantuml.svek.image;

import java.awt.geom.Dimension2D;

import net.sourceforge.plantuml.ColorParam;
import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.FontParam;
import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.SkinParam;
import net.sourceforge.plantuml.SkinParamUtils;
import net.sourceforge.plantuml.Url;
import net.sourceforge.plantuml.cucadiagram.ILeaf;
import net.sourceforge.plantuml.cucadiagram.Stereotype;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.color.ColorType;
import net.sourceforge.plantuml.style.PName;
import net.sourceforge.plantuml.style.SName;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.style.StyleSignature;
import net.sourceforge.plantuml.svek.AbstractEntityImage;
import net.sourceforge.plantuml.svek.Bibliotekon;
import net.sourceforge.plantuml.svek.Shape;
import net.sourceforge.plantuml.svek.ShapeType;
import net.sourceforge.plantuml.ugraphic.Shadowable;
import net.sourceforge.plantuml.ugraphic.UChangeBackColor;
import net.sourceforge.plantuml.ugraphic.UChangeColor;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.URectangle;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class EntityImageActivity extends AbstractEntityImage {

	private double shadowing = 0;
	public static final int CORNER = 25;
	final private TextBlock desc;
	final private static int MARGIN = 10;
	final private Url url;
	private final Bibliotekon bibliotekon;

	public EntityImageActivity(ILeaf entity, ISkinParam skinParam, Bibliotekon bibliotekon) {
		super(entity, skinParam);
		this.bibliotekon = bibliotekon;
		final Stereotype stereotype = entity.getStereotype();

		final FontConfiguration fontConfiguration;
		final HorizontalAlignment horizontalAlignment;
		if (SkinParam.USE_STYLES()) {
			final Style style = getDefaultStyleDefinition().getMergedStyle(getSkinParam().getCurrentStyleBuilder());
			fontConfiguration = style.getFontConfiguration(skinParam.getIHtmlColorSet());
			horizontalAlignment = style.getHorizontalAlignment();
			shadowing = style.value(PName.Shadowing).asDouble();
		} else {
			fontConfiguration = new FontConfiguration(getSkinParam(), FontParam.ACTIVITY, stereotype);
			horizontalAlignment = HorizontalAlignment.CENTER;
			if (getSkinParam().shadowing(getEntity().getStereotype())) {
				shadowing = 4;
			}
		}
		this.desc = entity.getDisplay().create(fontConfiguration, horizontalAlignment, skinParam);
		this.url = entity.getUrl99();
	}

	public Dimension2D calculateDimension(StringBounder stringBounder) {
		final Dimension2D dim = desc.calculateDimension(stringBounder);
		return Dimension2DDouble.delta(dim, MARGIN * 2);
	}

	final public void drawU(UGraphic ug) {
		if (url != null) {
			ug.startUrl(url);
		}
		if (getShapeType() == ShapeType.ROUND_RECTANGLE) {
			ug = drawNormal(ug);
		} else if (getShapeType() == ShapeType.OCTAGON) {
			ug = drawOctagon(ug);
		} else {
			throw new UnsupportedOperationException();
		}
		if (url != null) {
			ug.closeAction();
		}
	}

	private UGraphic drawOctagon(UGraphic ug) {
		final Shape shape = bibliotekon.getShape(getEntity());
		final Shadowable octagon = shape.getOctagon();
		octagon.setDeltaShadow(shadowing);
		ug = applyColors(ug);
		ug.apply(new UStroke(1.5)).draw(octagon);
		desc.drawU(ug.apply(new UTranslate(MARGIN, MARGIN)));
		return ug;

	}

	private UGraphic drawNormal(UGraphic ug) {
		final StringBounder stringBounder = ug.getStringBounder();
		final Dimension2D dimTotal = calculateDimension(stringBounder);

		final double widthTotal = dimTotal.getWidth();
		final double heightTotal = dimTotal.getHeight();
		final Shadowable rect = new URectangle(widthTotal, heightTotal, CORNER, CORNER);
		rect.setDeltaShadow(shadowing);

		ug = applyColors(ug);
		UStroke stroke = new UStroke(1.5);
		if (SkinParam.USE_STYLES()) {
			final Style style = getDefaultStyleDefinition().getMergedStyle(getSkinParam().getCurrentStyleBuilder());
			stroke = style.getStroke();
		}
		ug.apply(stroke).draw(rect);

		desc.drawU(ug.apply(new UTranslate(MARGIN, MARGIN)));
		return ug;
	}

	public StyleSignature getDefaultStyleDefinition() {
		return StyleSignature.of(SName.root, SName.element, SName.activityDiagram, SName.activity).with(getStereo());
	}

	private UGraphic applyColors(UGraphic ug) {
		HtmlColor borderColor = SkinParamUtils.getColor(getSkinParam(), getStereo(), ColorParam.activityBorder);
		HtmlColor backcolor = getEntity().getColors(getSkinParam()).getColor(ColorType.BACK);
		if (backcolor == null) {
			backcolor = SkinParamUtils.getColor(getSkinParam(), getStereo(), ColorParam.activityBackground);
		}

		if (SkinParam.USE_STYLES()) {
			final Style style = getDefaultStyleDefinition().getMergedStyle(getSkinParam().getCurrentStyleBuilder());
			borderColor = style.value(PName.LineColor).asColor(getSkinParam().getIHtmlColorSet());
			backcolor = getEntity().getColors(getSkinParam()).getColor(ColorType.BACK);
			if (backcolor == null) {
				backcolor = style.value(PName.BackGroundColor).asColor(getSkinParam().getIHtmlColorSet());
			}
		}
		ug = ug.apply(new UChangeColor(borderColor));
		ug = ug.apply(new UChangeBackColor(backcolor));
		return ug;
	}

	public ShapeType getShapeType() {
		final Stereotype stereotype = getStereo();
		if (getSkinParam().useOctagonForActivity(stereotype)) {
			return ShapeType.OCTAGON;
		}
		return ShapeType.ROUND_RECTANGLE;
	}

}

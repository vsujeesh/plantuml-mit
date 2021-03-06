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
package net.sourceforge.plantuml.ugraphic.g2d;

import java.awt.BasicStroke;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.TexturePaint;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

import net.sourceforge.plantuml.EnsureVisible;
import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.graphic.HtmlColorGradient;
import net.sourceforge.plantuml.graphic.HtmlColorSimple;
import net.sourceforge.plantuml.ugraphic.ColorMapper;
import net.sourceforge.plantuml.ugraphic.UDriver;
import net.sourceforge.plantuml.ugraphic.UParam;
import net.sourceforge.plantuml.ugraphic.UPattern;
import net.sourceforge.plantuml.ugraphic.URectangle;
import net.sourceforge.plantuml.ugraphic.UShape;
import net.sourceforge.plantuml.ugraphic.UShapeSized;

public class DriverRectangleG2d extends DriverShadowedG2d implements UDriver<Graphics2D> {

	private final double dpiFactor;
	private final EnsureVisible visible;

	public DriverRectangleG2d(double dpiFactor, EnsureVisible visible) {
		this.dpiFactor = dpiFactor;
		this.visible = visible;
	}

	public void draw(UShape ushape, double x, double y, ColorMapper mapper, UParam param, Graphics2D g2d) {
		g2d.setStroke(new BasicStroke((float) param.getStroke().getThickness()));
		final URectangle rect = (URectangle) ushape;
		final double rx = rect.getRx();
		final double ry = rect.getRy();
		final Shape shape;
		if (rx == 0 && ry == 0) {
			shape = new Rectangle2D.Double(x, y, rect.getWidth(), rect.getHeight());
		} else {
			shape = new RoundRectangle2D.Double(x, y, rect.getWidth(), rect.getHeight(), rx, ry);
		}

		visible.ensureVisible(x, y);
		visible.ensureVisible(x + rect.getWidth(), y + rect.getHeight());

		// Shadow
		if (rect.getDeltaShadow() != 0) {
			drawShadow(g2d, shape, rect.getDeltaShadow(), dpiFactor);
		}

		final HtmlColor back = param.getBackcolor();
		final HtmlColor color = param.getColor();
		if (back instanceof HtmlColorGradient) {
			final GradientPaint paint = getPaintGradient(x, y, mapper, rect.getWidth(), rect.getHeight(), back);
			g2d.setPaint(paint);
			g2d.fill(shape);
			drawBorder(param, color, mapper, rect, shape, g2d, x, y);
		} else {
			if (param.getBackcolor() != null) {
				g2d.setColor(mapper.getMappedColor(param.getBackcolor()));
				DriverLineG2d.manageStroke(param, g2d);
				managePattern(param, g2d);
				g2d.fill(shape);
			}
			if (color != null && color.equals(param.getBackcolor()) == false) {
				drawBorder(param, color, mapper, rect, shape, g2d, x, y);
			}
		}
	}

	public static void drawBorder(UParam param, HtmlColor color, ColorMapper mapper, UShapeSized sized, Shape shape,
			Graphics2D g2d, double x, double y) {
		if (color == null) {
			return;
		}
		if (color instanceof HtmlColorGradient) {
			final GradientPaint paint = getPaintGradient(x, y, mapper, sized.getWidth(), sized.getHeight(), color);
			g2d.setPaint(paint);
		} else {
			g2d.setColor(mapper.getMappedColor(color));
		}
		DriverLineG2d.manageStroke(param, g2d);
		g2d.draw(shape);
	}

	public static GradientPaint getPaintGradient(double x, double y, ColorMapper mapper, double width, double height,
			final HtmlColor back) {
		final HtmlColorGradient gr = (HtmlColorGradient) back;
		final char policy = gr.getPolicy();
		final GradientPaint paint;
		if (policy == '|') {
			paint = new GradientPaint((float) x, (float) (y + height) / 2, mapper.getMappedColor(gr.getColor1()),
					(float) (x + width), (float) (y + height) / 2, mapper.getMappedColor(gr.getColor2()));
		} else if (policy == '\\') {
			paint = new GradientPaint((float) x, (float) (y + height), mapper.getMappedColor(gr.getColor1()),
					(float) (x + width), (float) y, mapper.getMappedColor(gr.getColor2()));
		} else if (policy == '-') {
			paint = new GradientPaint((float) (x + width) / 2, (float) y, mapper.getMappedColor(gr.getColor1()),
					(float) (x + width) / 2, (float) (y + height), mapper.getMappedColor(gr.getColor2()));
		} else {
			// for /
			paint = new GradientPaint((float) x, (float) y, mapper.getMappedColor(gr.getColor1()), (float) (x + width),
					(float) (y + height), mapper.getMappedColor(gr.getColor2()));
		}
		return paint;
	}

	public static void managePattern(UParam param, Graphics2D g2d) {
		final UPattern pattern = param.getPattern();
		if (pattern == UPattern.VERTICAL_STRIPE) {
			final BufferedImage bi = new BufferedImage(4, 4, BufferedImage.TYPE_INT_ARGB);
			final Rectangle r = new Rectangle(0, 0, 4, 4);
			final int rgb = ((HtmlColorSimple) param.getBackcolor()).getColor999().getRGB();
			for (int i = 0; i < 4; i++) {
				for (int j = 0; j < 4; j++) {
					if (i == 0 || i == 1) {
						bi.setRGB(i, j, rgb);
					}
				}
			}
			g2d.setPaint(new TexturePaint(bi, r));
		} else if (pattern == UPattern.HORIZONTAL_STRIPE) {
			final BufferedImage bi = new BufferedImage(4, 4, BufferedImage.TYPE_INT_ARGB);
			final Rectangle r = new Rectangle(0, 0, 4, 4);
			final int rgb = ((HtmlColorSimple) param.getBackcolor()).getColor999().getRGB();
			for (int i = 0; i < 4; i++) {
				for (int j = 0; j < 4; j++) {
					if (j == 0 || j == 1) {
						bi.setRGB(i, j, rgb);
					}
				}
			}
			g2d.setPaint(new TexturePaint(bi, r));
		} else if (pattern == UPattern.SMALL_CIRCLE) {
			final BufferedImage bi = new BufferedImage(4, 4, BufferedImage.TYPE_INT_ARGB);
			final Rectangle r = new Rectangle(0, 0, 4, 4);
			final int rgb = ((HtmlColorSimple) param.getBackcolor()).getColor999().getRGB();
			bi.setRGB(0, 1, rgb);
			bi.setRGB(1, 0, rgb);
			bi.setRGB(1, 1, rgb);
			bi.setRGB(1, 2, rgb);
			bi.setRGB(2, 1, rgb);
			g2d.setPaint(new TexturePaint(bi, r));
		}
	}

}

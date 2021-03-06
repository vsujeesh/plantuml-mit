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
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.Guillemet;
import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.SkinParamUtils;
import net.sourceforge.plantuml.Url;
import net.sourceforge.plantuml.cucadiagram.BodyEnhanced;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.cucadiagram.EntityPortion;
import net.sourceforge.plantuml.cucadiagram.IEntity;
import net.sourceforge.plantuml.cucadiagram.ILeaf;
import net.sourceforge.plantuml.cucadiagram.Link;
import net.sourceforge.plantuml.cucadiagram.PortionShower;
import net.sourceforge.plantuml.cucadiagram.Stereotype;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.graphic.SkinParameter;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.SymbolContext;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.TextBlockUtils;
import net.sourceforge.plantuml.graphic.USymbol;
import net.sourceforge.plantuml.graphic.color.ColorType;
import net.sourceforge.plantuml.graphic.color.Colors;
import net.sourceforge.plantuml.svek.AbstractEntityImage;
import net.sourceforge.plantuml.svek.Margins;
import net.sourceforge.plantuml.svek.ShapeType;
import net.sourceforge.plantuml.ugraphic.UComment;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.utils.MathUtils;

public class EntityImageDescription extends AbstractEntityImage {

	private final ShapeType shapeType;

	final private Url url;

	private final TextBlock asSmall;

	private final TextBlock name;
	private final TextBlock desc;

	private TextBlock stereo;

	private final boolean hideText;
	private final Collection<Link> links;
	private final boolean useRankSame;
	private final boolean fixCircleLabelOverlapping;

	public EntityImageDescription(ILeaf entity, ISkinParam skinParam, PortionShower portionShower,
			Collection<Link> links) {
		super(entity, entity.getColors(skinParam).mute(skinParam));
		this.useRankSame = skinParam.useRankSame();
		this.fixCircleLabelOverlapping = skinParam.fixCircleLabelOverlapping();

		this.links = links;
		final Stereotype stereotype = entity.getStereotype();
		USymbol symbol = getUSymbol(entity);
		if (symbol == USymbol.FOLDER) {
			this.shapeType = ShapeType.FOLDER;
		} else if (symbol == USymbol.INTERFACE) {
			this.shapeType = skinParam.fixCircleLabelOverlapping() ? ShapeType.RECTANGLE_WITH_CIRCLE_INSIDE
					: ShapeType.RECTANGLE;
		} else {
			this.shapeType = ShapeType.RECTANGLE;
		}
		this.hideText = symbol == USymbol.INTERFACE;

		final Display codeDisplay = Display.getWithNewlines(entity.getCode());
		desc = (entity.getDisplay().equals(codeDisplay) && symbol.getSkinParameter() == SkinParameter.PACKAGE)
				|| entity.getDisplay().isWhite() ? TextBlockUtils.empty(0, 0) : new BodyEnhanced(entity.getDisplay(),
				symbol.getFontParam(), getSkinParam(), HorizontalAlignment.LEFT, stereotype,
				symbol.manageHorizontalLine(), false, entity);

		this.url = entity.getUrl99();

		final Colors colors = entity.getColors(skinParam);
		HtmlColor backcolor = colors.getColor(ColorType.BACK);
		if (backcolor == null) {
			backcolor = SkinParamUtils.getColor(getSkinParam(), getStereo(), symbol.getColorParamBack());
		}

		assert getStereo() == stereotype;
		final HtmlColor forecolor = SkinParamUtils.getColor(getSkinParam(), stereotype, symbol.getColorParamBorder());
		final double roundCorner = symbol.getSkinParameter().getRoundCorner(getSkinParam(), stereotype);
		final double diagonalCorner = symbol.getSkinParameter().getDiagonalCorner(getSkinParam(), stereotype);
		final UStroke stroke = colors.muteStroke(symbol.getSkinParameter().getStroke(getSkinParam(), stereotype));

		final SymbolContext ctx = new SymbolContext(backcolor, forecolor).withStroke(stroke)
				.withShadow(getSkinParam().shadowing2(getEntity().getStereotype(), symbol.getSkinParameter()) ? 3 : 0)
				.withCorner(roundCorner, diagonalCorner);

		stereo = TextBlockUtils.empty(0, 0);

		if (stereotype != null && stereotype.getSprite(getSkinParam()) != null) {
			// symbol = symbol.withStereoAlignment(HorizontalAlignment.RIGHT);
			stereo = stereotype.getSprite(getSkinParam());
		} else if (stereotype != null && stereotype.getLabel(Guillemet.DOUBLE_COMPARATOR) != null
				&& portionShower.showPortion(EntityPortion.STEREOTYPE, entity)) {
			stereo = Display.getWithNewlines(stereotype.getLabel(getSkinParam().guillemet())).create(
					new FontConfiguration(getSkinParam(), symbol.getFontParamStereotype(), stereotype),
					HorizontalAlignment.CENTER, getSkinParam());
		}

		name = new BodyEnhanced(codeDisplay, symbol.getFontParam(), getSkinParam(), HorizontalAlignment.CENTER,
				stereotype, symbol.manageHorizontalLine(), false, entity);

		if (hideText) {
			asSmall = symbol.asSmall(TextBlockUtils.empty(0, 0), TextBlockUtils.empty(0, 0),
					TextBlockUtils.empty(0, 0), ctx, skinParam.getStereotypeAlignment());
		} else {
			asSmall = symbol.asSmall(name, desc, stereo, ctx, skinParam.getStereotypeAlignment());
		}
	}

	private USymbol getUSymbol(ILeaf entity) {
		final USymbol result = entity.getUSymbol() == null ? (getSkinParam().useUml2ForComponent() ? USymbol.COMPONENT2
				: USymbol.COMPONENT1) : entity.getUSymbol();
		if (result == null) {
			throw new IllegalArgumentException();
		}
		return result;
	}

	public Dimension2D getNameDimension(StringBounder stringBounder) {
		if (hideText) {
			return new Dimension2DDouble(0, 0);
		}
		return name.calculateDimension(stringBounder);
	}

	public Dimension2D calculateDimension(StringBounder stringBounder) {
		return asSmall.calculateDimension(stringBounder);
	}

	@Override
	public Margins getShield(StringBounder stringBounder) {
		if (hideText == false) {
			return Margins.NONE;
		}
		// if (useRankSame && hasSomeHorizontalLink((ILeaf) getEntity(), links)) {
		// return Margins.NONE;
		// }
		if (isThereADoubleLink((ILeaf) getEntity(), links)) {
			return Margins.NONE;
		}
		if (fixCircleLabelOverlapping == false && hasSomeHorizontalLinkVisible((ILeaf) getEntity(), links)) {
			return Margins.NONE;
		}
		if (hasSomeHorizontalLinkDoubleDecorated((ILeaf) getEntity(), links)) {
			return Margins.NONE;
		}
		final Dimension2D dimStereo = stereo.calculateDimension(stringBounder);
		final Dimension2D dimDesc = desc.calculateDimension(stringBounder);
		final Dimension2D dimSmall = asSmall.calculateDimension(stringBounder);
		final double x = Math.max(dimStereo.getWidth(), dimDesc.getWidth());
		double suppX = x - dimSmall.getWidth();
		if (suppX < 1) {
			suppX = 1;
		}
		final double y = MathUtils.max(1, dimDesc.getHeight(), dimStereo.getHeight());
		return new Margins(suppX / 2, suppX / 2, y, y);
	}

	private boolean hasSomeHorizontalLinkVisible(ILeaf leaf, Collection<Link> links) {
		for (Link link : links) {
			if (link.getLength() == 1 && link.contains(leaf) && link.isInvis() == false) {
				return true;
			}
		}
		return false;
	}

	private boolean isThereADoubleLink(ILeaf leaf, Collection<Link> links) {
		final Set<IEntity> others = new HashSet<IEntity>();
		for (Link link : links) {
			if (link.contains(leaf)) {
				final IEntity other = link.getOther(leaf);
				final boolean changed = others.add(other);
				if (changed == false) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean hasSomeHorizontalLinkDoubleDecorated(ILeaf leaf, Collection<Link> links) {
		for (Link link : links) {
			if (link.getLength() == 1 && link.contains(leaf) && link.getType().isDoubleDecorated()) {
				return true;
			}
		}
		return false;
	}

	final public void drawU(UGraphic ug) {
		ug.draw(new UComment("entity " + getEntity().getCode().getFullName()));
		if (url != null) {
			ug.startUrl(url);
		}
		asSmall.drawU(ug);
		if (hideText) {
			final double space = 8;
			final Dimension2D dimSmall = asSmall.calculateDimension(ug.getStringBounder());
			final Dimension2D dimDesc = desc.calculateDimension(ug.getStringBounder());
			final double posx1 = (dimSmall.getWidth() - dimDesc.getWidth()) / 2;
			desc.drawU(ug.apply(new UTranslate(posx1, space + dimSmall.getHeight())));
			final Dimension2D dimStereo = stereo.calculateDimension(ug.getStringBounder());
			final double posx2 = (dimSmall.getWidth() - dimStereo.getWidth()) / 2;
			stereo.drawU(ug.apply(new UTranslate(posx2, -space - dimStereo.getHeight())));
		}

		if (url != null) {
			ug.closeAction();
		}
	}

	public ShapeType getShapeType() {
		return shapeType;
	}

	@Override
	public double getOverscanX(StringBounder stringBounder) {
		if (hideText) {
			final Dimension2D dimSmall = asSmall.calculateDimension(stringBounder);
			final Dimension2D dimDesc = desc.calculateDimension(stringBounder);
			final Dimension2D dimStereo = stereo.calculateDimension(stringBounder);
			final double posx1 = (dimSmall.getWidth() - dimDesc.getWidth()) / 2;
			final double posx2 = (dimSmall.getWidth() - dimStereo.getWidth()) / 2;
			return MathUtils.max(-posx1, -posx2, 0);
		}
		return 0;
	}
}

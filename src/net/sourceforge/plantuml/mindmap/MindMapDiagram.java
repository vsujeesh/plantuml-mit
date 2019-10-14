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
package net.sourceforge.plantuml.mindmap;

import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.OutputStream;

import net.sourceforge.plantuml.AnnotatedWorker;
import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.Direction;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.Scale;
import net.sourceforge.plantuml.UmlDiagram;
import net.sourceforge.plantuml.UmlDiagramType;
import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.command.regex.Matcher2;
import net.sourceforge.plantuml.core.DiagramDescription;
import net.sourceforge.plantuml.core.ImageData;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.graphic.InnerStrategy;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.style.StyleBuilder;
import net.sourceforge.plantuml.svek.TextBlockBackcolored;
import net.sourceforge.plantuml.ugraphic.ImageBuilder;
import net.sourceforge.plantuml.ugraphic.MinMax;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.wbs.WBSDiagram;

public class MindMapDiagram extends UmlDiagram {

	private Branch left = new Branch();
	private Branch right = new Branch();

	private Direction defaultDirection = Direction.RIGHT;

	public final void setDefaultDirection(Direction defaultDirection) {
		this.defaultDirection = defaultDirection;
	}

	public DiagramDescription getDescription() {
		return new DiagramDescription("MindMap");
	}

	@Override
	public UmlDiagramType getUmlDiagramType() {
		return UmlDiagramType.MINDMAP;
	}

	@Override
	protected ImageData exportDiagramInternal(OutputStream os, int index, FileFormatOption fileFormatOption)
			throws IOException {
		final Scale scale = getScale();

		final double dpiFactor = scale == null ? getScaleCoef(fileFormatOption) : scale.getScale(100, 100);
		final ISkinParam skinParam = getSkinParam();
		final ImageBuilder imageBuilder = new ImageBuilder(skinParam.getColorMapper(), dpiFactor,
				skinParam.getBackgroundColor(), fileFormatOption.isWithMetadata() ? getMetadata() : null, "", 10, 10,
				null, skinParam.handwritten());
		TextBlock result = getTextBlock();

		result = new AnnotatedWorker(this, skinParam, fileFormatOption.getDefaultStringBounder()).addAdd(result);
		imageBuilder.setUDrawable(result);

		return imageBuilder.writeImageTOBEMOVED(fileFormatOption, seed(), os);
	}

	private TextBlockBackcolored getTextBlock() {
		return new TextBlockBackcolored() {

			public void drawU(UGraphic ug) {
				drawMe(ug);
			}

			public Rectangle2D getInnerPosition(String member, StringBounder stringBounder, InnerStrategy strategy) {
				return null;
			}

			public Dimension2D calculateDimension(StringBounder stringBounder) {
				computeFinger();
				final double y1 = right.finger == null ? 0 : right.finger.getFullThickness(stringBounder) / 2;
				final double y2 = left.finger == null ? 0 : left.finger.getFullThickness(stringBounder) / 2;
				final double y = Math.max(y1, y2);

				final double x = left.finger == null ? 0 : left.finger.getFullElongation(stringBounder);

				final double width = right.finger == null ? x : x + right.finger.getFullElongation(stringBounder);
				final double height = y
						+ Math.max(left.finger == null ? 0 : left.finger.getFullThickness(stringBounder) / 2,
								right.finger == null ? 0 : right.finger.getFullThickness(stringBounder) / 2);
				return new Dimension2DDouble(width, height);

			}

			public MinMax getMinMax(StringBounder stringBounder) {
				throw new UnsupportedOperationException();
			}

			public HtmlColor getBackcolor() {
				return null;
			}
		};
	}

	private void drawMe(UGraphic ug) {
		if (left.root == null && right.root == null) {
			return;
		}
		computeFinger();

		final StringBounder stringBounder = ug.getStringBounder();
		final double y1 = right.finger == null ? 0 : right.finger.getFullThickness(stringBounder) / 2;
		final double y2 = left.finger == null ? 0 : left.finger.getFullThickness(stringBounder) / 2;
		final double y = Math.max(y1, y2);

		final double x = left.finger == null ? 0 : left.finger.getFullElongation(stringBounder)
				+ ((FingerImpl) left.finger).getX12();
		if (right.finger != null) {
			right.finger.drawU(ug.apply(new UTranslate(x, y)));
		}
		if (left.finger != null) {
			left.finger.drawU(ug.apply(new UTranslate(x, y)));
		}
	}

	private void computeFinger() {
		if (left.finger == null && right.finger == null) {
			if (left.root.hasChildren()) {
				left.finger = FingerImpl.build(left.root, getSkinParam(), Direction.LEFT);
			}
			if (left.finger == null || right.root.hasChildren()) {
				right.finger = FingerImpl.build(right.root, getSkinParam(), Direction.RIGHT);
			}
			if (left.finger != null && right.finger != null) {
				left.finger.doNotDrawFirstPhalanx();
			}
		}
	}

	public CommandExecutionResult addIdea(HtmlColor backColor, int level, Display label, IdeaShape shape) {
		return addIdea(backColor, level, label, shape, defaultDirection);
	}

	public CommandExecutionResult addIdea(HtmlColor backColor, int level, Display label, IdeaShape shape,
			Direction direction) {
		final Matcher2 m = WBSDiagram.patternStereotype.matcher(label.get(0));
		String stereotype = null;
		if (m.matches()) {
			label = Display.getWithNewlines(m.group(1));
			stereotype = m.group(2);
		}
		if (level == 0) {
			if (this.right.root != null) {
				return CommandExecutionResult
						.error("I don't know how to draw multi-root diagram. You should suggest an image so that the PlantUML team implements it :-)");
			}
			right.initRoot(getSkinParam().getCurrentStyleBuilder(), label, shape, stereotype);
			left.initRoot(getSkinParam().getCurrentStyleBuilder(), label, shape, stereotype);
			return CommandExecutionResult.ok();
		}
		if (direction == Direction.LEFT) {
			return left.add(getSkinParam().getCurrentStyleBuilder(), backColor, level, label, shape, stereotype);
		}
		return right.add(getSkinParam().getCurrentStyleBuilder(), backColor, level, label, shape, stereotype);
	}

	static class Branch {
		private Idea root;
		private Idea last;
		private Finger finger;

		private void initRoot(StyleBuilder styleBuilder, Display label, IdeaShape shape, String stereotype) {
			root = new Idea(styleBuilder, label, shape, stereotype);
			last = root;
		}

		private Idea getParentOfLast(int nb) {
			Idea result = last;
			for (int i = 0; i < nb; i++) {
				result = result.getParent();
			}
			return result;
		}

		private CommandExecutionResult add(StyleBuilder styleBuilder, HtmlColor backColor, int level, Display label,
				IdeaShape shape, String stereotype) {
			if (last == null) {
				return CommandExecutionResult.error("Check your indentation ?");
			}
			if (level == last.getLevel() + 1) {
				final Idea newIdea = last.createIdea(styleBuilder, backColor, level, label, shape, stereotype);
				last = newIdea;
				return CommandExecutionResult.ok();
			}
			if (level <= last.getLevel()) {
				final int diff = last.getLevel() - level + 1;
				final Idea newIdea = getParentOfLast(diff).createIdea(styleBuilder, backColor, level, label, shape,
						stereotype);
				last = newIdea;
				return CommandExecutionResult.ok();
			}
			return CommandExecutionResult.error("error42L");
		}

	}

}
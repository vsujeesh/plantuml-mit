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
package net.sourceforge.plantuml.graphic;

import net.sourceforge.plantuml.ColorParam;
import net.sourceforge.plantuml.CornerParam;
import net.sourceforge.plantuml.FontParam;
import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.LineParam;
import net.sourceforge.plantuml.cucadiagram.Stereotype;
import net.sourceforge.plantuml.ugraphic.UStroke;

public class SkinParameter {

	public static final SkinParameter DATABASE = new SkinParameter("DATABASE", ColorParam.databaseBackground,
			ColorParam.databaseBorder, FontParam.DATABASE, FontParam.DATABASE_STEREOTYPE);

	public static final SkinParameter ARTIFACT = new SkinParameter("ARTIFACT", ColorParam.artifactBackground,
			ColorParam.artifactBorder, FontParam.ARTIFACT, FontParam.ARTIFACT_STEREOTYPE);

	public static final SkinParameter COMPONENT1 = new SkinParameter("COMPONENT1", ColorParam.componentBackground,
			ColorParam.componentBorder, FontParam.COMPONENT, FontParam.COMPONENT_STEREOTYPE, LineParam.componentBorder,
			CornerParam.component);

	public static final SkinParameter NODE = new SkinParameter("NODE", ColorParam.nodeBackground,
			ColorParam.nodeBorder, FontParam.NODE, FontParam.NODE_STEREOTYPE);

	public static final SkinParameter STORAGE = new SkinParameter("STORAGE", ColorParam.storageBackground,
			ColorParam.storageBorder, FontParam.STORAGE, FontParam.STORAGE_STEREOTYPE);

	public static final SkinParameter QUEUE = new SkinParameter("QUEUE", ColorParam.queueBackground,
			ColorParam.queueBorder, FontParam.QUEUE, FontParam.QUEUE_STEREOTYPE);

	public static final SkinParameter STACK = new SkinParameter("STACK", ColorParam.stackBackground,
			ColorParam.stackBorder, FontParam.STACK, FontParam.STACK_STEREOTYPE);

	public static final SkinParameter CLOUD = new SkinParameter("CLOUD", ColorParam.cloudBackground,
			ColorParam.cloudBorder, FontParam.CLOUD, FontParam.CLOUD_STEREOTYPE);

	public static final SkinParameter FRAME = new SkinParameter("FRAME", ColorParam.frameBackground,
			ColorParam.frameBorder, FontParam.FRAME, FontParam.FRAME_STEREOTYPE);

	public static final SkinParameter COMPONENT2 = new SkinParameter("COMPONENT2", ColorParam.componentBackground,
			ColorParam.componentBorder, FontParam.COMPONENT, FontParam.COMPONENT_STEREOTYPE, LineParam.componentBorder,
			CornerParam.component);

	public static final SkinParameter AGENT = new SkinParameter("AGENT", ColorParam.agentBackground,
			ColorParam.agentBorder, FontParam.AGENT, FontParam.AGENT_STEREOTYPE, LineParam.agentBorder, CornerParam.agent);

	public static final SkinParameter FOLDER = new SkinParameter("FOLDER", ColorParam.folderBackground,
			ColorParam.folderBorder, FontParam.FOLDER, FontParam.FOLDER_STEREOTYPE);

	public static final SkinParameter FILE = new SkinParameter("FILE", ColorParam.fileBackground,
			ColorParam.fileBorder, FontParam.FILE, FontParam.FILE_STEREOTYPE);

	public static final SkinParameter PACKAGE = new SkinParameter("PACKAGE", ColorParam.packageBackground,
			ColorParam.packageBorder, FontParam.PACKAGE, FontParam.PACKAGE_STEREOTYPE);

	public static final SkinParameter CARD = new SkinParameter("CARD", ColorParam.cardBackground,
			ColorParam.cardBorder, FontParam.CARD, FontParam.CARD_STEREOTYPE, LineParam.cardBorder,
			CornerParam.card);

	public static final SkinParameter RECTANGLE = new SkinParameter("RECTANGLE", ColorParam.rectangleBackground,
			ColorParam.rectangleBorder, FontParam.RECTANGLE, FontParam.RECTANGLE_STEREOTYPE, LineParam.rectangleBorder,
			CornerParam.rectangle);

	public static final SkinParameter ARCHIMATE = new SkinParameter("ARCHIMATE", ColorParam.archimateBackground,
			ColorParam.archimateBorder, FontParam.ARCHIMATE, FontParam.ARCHIMATE_STEREOTYPE, LineParam.archimateBorder,
			CornerParam.archimate);

	public static final SkinParameter COLLECTIONS = new SkinParameter("COLLECTIONS", ColorParam.collectionsBackground,
			ColorParam.collectionsBorder, FontParam.RECTANGLE, FontParam.RECTANGLE_STEREOTYPE);

	public static final SkinParameter ACTOR = new SkinParameter("ACTOR", ColorParam.actorBackground,
			ColorParam.actorBorder, FontParam.ACTOR, FontParam.ACTOR_STEREOTYPE);

	public static final SkinParameter USECASE = new SkinParameter("USECASE", ColorParam.usecaseBackground,
			ColorParam.usecaseBorder, FontParam.USECASE, FontParam.USECASE_STEREOTYPE);

	public static final SkinParameter BOUNDARY = new SkinParameter("BOUNDARY", ColorParam.boundaryBackground,
			ColorParam.boundaryBorder, FontParam.BOUNDARY, FontParam.BOUNDARY_STEREOTYPE);

	public static final SkinParameter CONTROL = new SkinParameter("CONTROL", ColorParam.controlBackground,
			ColorParam.controlBorder, FontParam.CONTROL, FontParam.CONTROL_STEREOTYPE);

	public static final SkinParameter ENTITY_DOMAIN = new SkinParameter("ENTITY_DOMAIN", ColorParam.entityBackground,
			ColorParam.entityBorder, FontParam.ENTITY, FontParam.ENTITY_STEREOTYPE);

	public static final SkinParameter INTERFACE = new SkinParameter("INTERFACE", ColorParam.interfaceBackground,
			ColorParam.interfaceBorder, FontParam.INTERFACE, FontParam.INTERFACE_STEREOTYPE);

	public static final SkinParameter PARTICIPANT = new SkinParameter("PARTICIPANT", ColorParam.participantBackground,
			ColorParam.participantBorder, FontParam.PARTICIPANT, FontParam.PARTICIPANT_STEREOTYPE);

	private final ColorParam colorParamBorder;
	private final ColorParam colorParamBack;
	private final FontParam fontParam;
	private final FontParam fontParamStereotype;
	private final String name;
	private final LineParam lineParam;
	private final CornerParam roundParam;

	private SkinParameter(String name, ColorParam colorParamBack, ColorParam colorParamBorder, FontParam fontParam,
			FontParam fontParamStereotype, LineParam lineParam, CornerParam roundParam) {
		this.name = name;
		this.colorParamBack = colorParamBack;
		this.colorParamBorder = colorParamBorder;
		this.fontParam = fontParam;
		this.fontParamStereotype = fontParamStereotype;
		this.lineParam = lineParam;
		this.roundParam = roundParam;
	}

	private SkinParameter(String name, ColorParam colorParamBack, ColorParam colorParamBorder, FontParam fontParam,
			FontParam fontParamStereotype) {
		this(name, colorParamBack, colorParamBorder, fontParam, fontParamStereotype, null, CornerParam.DEFAULT);
	}

	public String getUpperCaseName() {
		if (name.endsWith("1") || name.endsWith("2")) {
			return name.substring(0, name.length() - 1);
		}
		return name;
	}

	public ColorParam getColorParamBorder() {
		return colorParamBorder;
	}

	public ColorParam getColorParamBack() {
		return colorParamBack;
	}

	public FontParam getFontParam() {
		return fontParam;
	}

	public FontParam getFontParamStereotype() {
		return fontParamStereotype;
	}

	public double getRoundCorner(ISkinParam skinParam, Stereotype stereotype) {
		return skinParam.getRoundCorner(roundParam, stereotype);
	}

	public double getDiagonalCorner(ISkinParam skinParam, Stereotype stereotype) {
		return skinParam.getDiagonalCorner(roundParam, stereotype);
	}

	public UStroke getStroke(ISkinParam skinParam, Stereotype stereotype) {
		UStroke result = null;
		if (lineParam != null) {
			result = skinParam.getThickness(lineParam, stereotype);
		}
		if (result == null) {
			result = new UStroke(1.5);
		}
		return result;
	}

}

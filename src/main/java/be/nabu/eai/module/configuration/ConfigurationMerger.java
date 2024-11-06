/*
* Copyright (C) 2016 Alexander Verbruggen
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public License
* along with this program. If not, see <https://www.gnu.org/licenses/>.
*/

package be.nabu.eai.module.configuration;

import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import be.nabu.eai.developer.ComplexContentEditor;
import be.nabu.eai.developer.ComplexContentEditor.ValueWrapper;
import be.nabu.eai.developer.api.ArtifactMerger;
import be.nabu.eai.repository.api.Repository;
import be.nabu.jfx.control.tree.Tree;
import be.nabu.libs.types.api.ComplexType;
import be.nabu.libs.types.mask.MaskedContent;

public class ConfigurationMerger implements ArtifactMerger<ConfigurationArtifact> {

	@Override
	public boolean merge(ConfigurationArtifact source, ConfigurationArtifact target, AnchorPane pane, Repository targetRepository) {
		// do a verbatim copy of the target to the source, then allow you to edit it
		if (source.getConfig().isEnvironmentSpecific() && target != null) {
			source.setContent(new MaskedContent(target.getContent(), (ComplexType) source.getConfig().getType()));
		}
		ComplexContentEditor editor = new ComplexContentEditor(source.getContent(), true, targetRepository);
		Tree<ValueWrapper> build = editor.getTree();
		build.getRootCell().expandedProperty().set(true);
		ScrollPane scroll = new ScrollPane();
		build.prefWidthProperty().bind(scroll.widthProperty());
		scroll.setContent(build);
		AnchorPane.setRightAnchor(scroll, 0d);
		AnchorPane.setTopAnchor(scroll, 0d);
		AnchorPane.setLeftAnchor(scroll, 0d);
		AnchorPane.setBottomAnchor(scroll, 0d);
		pane.getChildren().add(scroll);
		return true;
	}

	@Override
	public Class<ConfigurationArtifact> getArtifactClass() {
		return ConfigurationArtifact.class;
	}

}

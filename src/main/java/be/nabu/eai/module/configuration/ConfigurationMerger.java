package be.nabu.eai.module.configuration;

import javafx.scene.layout.AnchorPane;
import be.nabu.eai.developer.ComplexContentEditor;
import be.nabu.eai.developer.ComplexContentEditor.ValueWrapper;
import be.nabu.eai.developer.api.ArtifactMerger;
import be.nabu.eai.repository.api.Repository;
import be.nabu.jfx.control.tree.Tree;

public class ConfigurationMerger implements ArtifactMerger<ConfigurationArtifact> {

	@Override
	public boolean merge(ConfigurationArtifact source, ConfigurationArtifact target, AnchorPane pane, Repository targetRepository) {
		// do a verbatim copy of the target to the source, then allow you to edit it
		source.setContent(target.getContent());
		ComplexContentEditor editor = new ComplexContentEditor(source.getContent(), true, targetRepository);
		Tree<ValueWrapper> build = editor.getTree();
		pane.getChildren().add(build);
		build.getRootCell().expandedProperty().set(true);
		AnchorPane.setRightAnchor(build, 0d);
		AnchorPane.setTopAnchor(build, 0d);
		AnchorPane.setLeftAnchor(build, 0d);
		return true;
	}

	@Override
	public Class<ConfigurationArtifact> getArtifactClass() {
		return ConfigurationArtifact.class;
	}

}

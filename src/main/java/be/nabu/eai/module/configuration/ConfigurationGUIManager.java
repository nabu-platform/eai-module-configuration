package be.nabu.eai.module.configuration;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import be.nabu.eai.developer.ComplexContentEditor;
import be.nabu.eai.developer.ComplexContentEditor.ValueWrapper;
import be.nabu.eai.developer.MainController;
import be.nabu.eai.developer.managers.base.BaseArtifactGUIInstance;
import be.nabu.eai.developer.managers.base.BaseGUIManager;
import be.nabu.eai.developer.managers.base.BasePortableGUIManager;
import be.nabu.eai.developer.managers.util.SimpleProperty;
import be.nabu.eai.repository.api.Entry;
import be.nabu.eai.repository.api.ResourceEntry;
import be.nabu.eai.repository.resources.RepositoryEntry;
import be.nabu.jfx.control.tree.Tree;
import be.nabu.libs.property.api.Property;
import be.nabu.libs.property.api.Value;
import be.nabu.libs.types.api.DefinedType;

public class ConfigurationGUIManager extends BasePortableGUIManager<ConfigurationArtifact, BaseArtifactGUIInstance<ConfigurationArtifact>> {

	private ComplexContentEditor editor;

	public ConfigurationGUIManager() {
		super("Configuration", ConfigurationArtifact.class, new ConfigurationManager());
	}
	@Override
	protected List<Property<?>> getCreateProperties() {
		List<Property<?>> properties = new ArrayList<Property<?>>();
		properties.add(new SimpleProperty<DefinedType>("Type", DefinedType.class, true));
		return properties;
	}

	@Override
	protected ConfigurationArtifact newInstance(MainController controller, RepositoryEntry entry, Value<?>...values) throws IOException {
		DefinedType value = BaseGUIManager.getValue("Type", DefinedType.class, values);
		if (value == null) {
			throw new IllegalArgumentException("Expecting a type");
		}
		ConfigurationArtifact configurationArtifact = new ConfigurationArtifact(entry.getId(), entry.getContainer(), entry.getRepository());
		configurationArtifact.getConfiguration().setType(value);
		configurationArtifact.save(entry.getContainer());
		return configurationArtifact;
	}

	@Override
	protected BaseArtifactGUIInstance<ConfigurationArtifact> newGUIInstance(Entry entry) {
		return new BaseArtifactGUIInstance<ConfigurationArtifact>(this, entry);
	}
	@Override
	protected void setEntry(BaseArtifactGUIInstance<ConfigurationArtifact> guiInstance, ResourceEntry entry) {
		guiInstance.setEntry(entry);
	}
	@Override
	protected void setInstance(BaseArtifactGUIInstance<ConfigurationArtifact> guiInstance, ConfigurationArtifact instance) {
		guiInstance.setArtifact(instance);
	}

	@Override
	public String getCategory() {
		return "Miscellaneous";
	}

	@Override
	public void display(MainController controller, AnchorPane pane, ConfigurationArtifact artifact) throws IOException, ParseException {
		editor = new ComplexContentEditor(artifact.getContent(), true, artifact.getRepository());
		ScrollPane scroll = new ScrollPane();
		Tree<ValueWrapper> build = editor.getTree();
		build.getRootCell().expandedProperty().set(true);
		build.prefWidthProperty().bind(scroll.widthProperty());
		scroll.setContent(build);
		pane.getChildren().add(scroll);
		AnchorPane.setRightAnchor(scroll, 0d);
		AnchorPane.setTopAnchor(scroll, 0d);
		AnchorPane.setLeftAnchor(scroll, 0d);
		AnchorPane.setBottomAnchor(scroll, 0d);
	}
}

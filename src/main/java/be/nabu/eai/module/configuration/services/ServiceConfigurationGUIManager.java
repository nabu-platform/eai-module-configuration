package be.nabu.eai.module.configuration.services;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

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
import be.nabu.libs.services.api.DefinedService;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public class ServiceConfigurationGUIManager extends BasePortableGUIManager<ServiceConfigurationArtifact, BaseArtifactGUIInstance<ServiceConfigurationArtifact>> {

	private ComplexContentEditor editor;

	public ServiceConfigurationGUIManager() {
		super("Configured Service", ServiceConfigurationArtifact.class, new ServiceConfigurationManager());
	}
	@Override
	protected List<Property<?>> getCreateProperties() {
		List<Property<?>> properties = new ArrayList<Property<?>>();
		properties.add(new SimpleProperty<DefinedService>("Service", DefinedService.class, true));
		return properties;
	}

	@Override
	protected ServiceConfigurationArtifact newInstance(MainController controller, RepositoryEntry entry, Value<?>...values) throws IOException {
		DefinedService value = BaseGUIManager.getValue("Service", DefinedService.class, values);
		if (value == null) {
			throw new IllegalArgumentException("Expecting a service");
		}
		ServiceConfigurationArtifact configurationArtifact = new ServiceConfigurationArtifact(entry.getId(), entry.getContainer(), entry.getRepository());
		configurationArtifact.getConfiguration().setService(value);
		configurationArtifact.save(entry.getContainer());
		return configurationArtifact;
	}

	@Override
	protected BaseArtifactGUIInstance<ServiceConfigurationArtifact> newGUIInstance(Entry entry) {
		return new BaseArtifactGUIInstance<ServiceConfigurationArtifact>(this, entry);
	}
	@Override
	protected void setEntry(BaseArtifactGUIInstance<ServiceConfigurationArtifact> guiInstance, ResourceEntry entry) {
		guiInstance.setEntry(entry);
	}
	@Override
	protected void setInstance(BaseArtifactGUIInstance<ServiceConfigurationArtifact> guiInstance, ServiceConfigurationArtifact instance) {
		guiInstance.setArtifact(instance);
	}

	@Override
	public String getCategory() {
		return "Miscellaneous";
	}

	@Override
	public void display(MainController controller, AnchorPane pane, ServiceConfigurationArtifact artifact) throws IOException, ParseException {
		editor = new ComplexContentEditor(artifact.getContent(), true, artifact.getRepository());
		ScrollPane scroll = new ScrollPane();
		Tree<ValueWrapper> build = editor.getTree();
		build.getRootCell().expandedProperty().set(true);
		VBox box = new VBox();
		box.prefWidthProperty().bind(scroll.widthProperty());
		build.prefWidthProperty().bind(box.widthProperty());
		CheckBox checkbox = new CheckBox("Is environment specific?");
		checkbox.setPadding(new Insets(10));
		checkbox.setSelected(artifact.getConfig().isEnvironmentSpecific());
		checkbox.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) {
				artifact.getConfig().setEnvironmentSpecific(arg2 != null && arg2);
				controller.setChanged();
			}
		});
		box.getChildren().addAll(checkbox, build);
		scroll.setContent(box);
		pane.getChildren().add(scroll);
		AnchorPane.setRightAnchor(scroll, 0d);
		AnchorPane.setTopAnchor(scroll, 0d);
		AnchorPane.setLeftAnchor(scroll, 0d);
		AnchorPane.setBottomAnchor(scroll, 0d);
	}
}
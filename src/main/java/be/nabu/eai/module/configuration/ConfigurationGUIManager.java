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

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import be.nabu.eai.developer.ComplexContentEditor;
import be.nabu.eai.developer.ComplexContentEditor.ValueWrapper;
import be.nabu.eai.developer.MainController;
import be.nabu.eai.developer.managers.base.BaseArtifactGUIInstance;
import be.nabu.eai.developer.managers.base.BaseGUIManager;
import be.nabu.eai.developer.managers.base.BasePortableGUIManager;
import be.nabu.eai.developer.managers.util.SimpleProperty;
import be.nabu.eai.developer.util.EAIDeveloperUtils;
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
		
		TextField context = new TextField(artifact.getConfig().getContext());
		context.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				artifact.getConfig().setContext(newValue == null || newValue.trim().isEmpty() ? null : newValue.trim());
				MainController.getInstance().setChanged();
			}
		});
		HBox contextWrapper = EAIDeveloperUtils.newHBox("Context", context);
		box.getChildren().add(contextWrapper);
		
		scroll.setContent(box);
		pane.getChildren().add(scroll);
		AnchorPane.setRightAnchor(scroll, 0d);
		AnchorPane.setTopAnchor(scroll, 0d);
		AnchorPane.setLeftAnchor(scroll, 0d);
		AnchorPane.setBottomAnchor(scroll, 0d);
	}
}

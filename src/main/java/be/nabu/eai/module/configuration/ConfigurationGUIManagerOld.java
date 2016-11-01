package be.nabu.eai.module.configuration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import be.nabu.eai.developer.MainController;
import be.nabu.eai.developer.managers.base.BaseArtifactGUIInstance;
import be.nabu.eai.developer.managers.base.BaseConfigurationGUIManager;
import be.nabu.eai.developer.managers.base.BaseGUIManager;
import be.nabu.eai.developer.managers.base.BasePropertyOnlyGUIManager;
import be.nabu.eai.developer.managers.util.SimpleProperty;
import be.nabu.eai.repository.api.Entry;
import be.nabu.eai.repository.api.Repository;
import be.nabu.eai.repository.api.ResourceEntry;
import be.nabu.eai.repository.resources.RepositoryEntry;
import be.nabu.libs.property.api.Property;
import be.nabu.libs.property.api.Value;
import be.nabu.libs.types.api.ComplexType;
import be.nabu.libs.types.api.DefinedType;
import be.nabu.libs.types.base.ComplexElementImpl;

public class ConfigurationGUIManagerOld extends BasePropertyOnlyGUIManager<ConfigurationArtifact, BaseArtifactGUIInstance<ConfigurationArtifact>> {

	public ConfigurationGUIManagerOld() {
		super("Configuration", ConfigurationArtifact.class, new ConfigurationManager());
	}
	private List<Property<?>> properties;
	
	@Override
	public Repository getRepository(ConfigurationArtifact instance) {
		return instance.getRepository();
	}

	@Override
	public Collection<Property<?>> getModifiableProperties(ConfigurationArtifact instance) {
		if (properties == null) {
			try {
				properties = BaseConfigurationGUIManager.createProperty(new ComplexElementImpl((ComplexType) instance.getConfiguration().getType(), null));
				for (Property<?> property : properties) {
					if (property instanceof SimpleProperty) {
						((SimpleProperty<?>) property).setEnvironmentSpecific(true);
					}
				}
			}
			catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return properties;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <V> V getValue(ConfigurationArtifact instance, Property<V> property) {
		return (V) instance.getContent().get(property.getName());
	}

	@Override
	public <V> void setValue(ConfigurationArtifact instance, Property<V> property, V value) {
		instance.getContent().set(property.getName(), value);
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
}

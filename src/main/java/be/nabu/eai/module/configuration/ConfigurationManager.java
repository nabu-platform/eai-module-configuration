package be.nabu.eai.module.configuration;

import be.nabu.eai.repository.api.Repository;
import be.nabu.eai.repository.managers.base.JAXBArtifactManager;
import be.nabu.libs.resources.api.ResourceContainer;

public class ConfigurationManager extends JAXBArtifactManager<Configuration, ConfigurationArtifact> {

	public ConfigurationManager() {
		super(ConfigurationArtifact.class);
	}

	@Override
	protected ConfigurationArtifact newInstance(String id, ResourceContainer<?> container, Repository repository) {
		return new ConfigurationArtifact(id, container, repository);
	}


}

package be.nabu.eai.module.configuration.services;

import be.nabu.eai.repository.api.Repository;
import be.nabu.eai.repository.managers.base.JAXBArtifactManager;
import be.nabu.libs.resources.api.ResourceContainer;

public class ServiceConfigurationManager extends JAXBArtifactManager<ServiceConfiguration, ServiceConfigurationArtifact> {

	public ServiceConfigurationManager() {
		super(ServiceConfigurationArtifact.class);
	}

	@Override
	protected ServiceConfigurationArtifact newInstance(String id, ResourceContainer<?> container, Repository repository) {
		return new ServiceConfigurationArtifact(id, container, repository);
	}

}

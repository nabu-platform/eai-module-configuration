package nabu.misc.configuration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.validation.constraints.NotNull;

import be.nabu.eai.module.configuration.ConfigurationArtifact;
import be.nabu.eai.repository.EAIResourceRepository;
import be.nabu.eai.repository.events.ResourceEvent;
import be.nabu.eai.repository.events.ResourceEvent.ResourceState;
import be.nabu.eai.repository.impl.RepositoryArtifactResolver;
import be.nabu.libs.services.ServiceRuntime;
import be.nabu.libs.services.api.DefinedService;
import be.nabu.libs.services.api.ExecutionContext;
import be.nabu.libs.types.ComplexContentWrapperFactory;
import be.nabu.libs.types.api.ComplexContent;
import be.nabu.libs.types.api.ComplexType;
import be.nabu.libs.types.api.DefinedType;
import be.nabu.libs.types.api.Type;

@WebService
public class Services {
	
	private ExecutionContext executionContext;
	
	@WebResult(name = "configurations")
	public List<Object> configurations(@WebParam(name = "typeId") @NotNull String typeId, @WebParam(name = "context") String context) {
		DefinedType type = executionContext.getServiceContext().getResolver(DefinedType.class).resolve(typeId);
		if (type == null) {
			throw new IllegalArgumentException("Can not find the type: " + typeId);
		}
		List<Object> configurations = new ArrayList<Object>();
		List<ConfigurationArtifact> artifacts = EAIResourceRepository.getInstance().getArtifacts(ConfigurationArtifact.class);
		
		List<ConfigurationArtifact> applicable = new ArrayList<ConfigurationArtifact>();
		// filter out the configurations that apply to the data type
		for (ConfigurationArtifact artifact : artifacts) {
			try {
				Type current = artifact.getConfiguration().getType();
				while (current != null) {
					if (current.equals(type)) {
						applicable.add(artifact);
						break;
					}
					current = current.getSuperType();
				}
			}
			catch (Exception e) {
				// ignore
			}
		}
		artifacts = applicable;
		
		// let's order it to get the most contextually relevant in the front 
		if (ServiceRuntime.getRuntime() != null && ServiceRuntime.getRuntime().getParent() != null && ServiceRuntime.getRuntime().getParent().getService() instanceof DefinedService) {
			String forId = context == null ? ((DefinedService) ServiceRuntime.getRuntime().getParent().getService()).getId() : context;
			String resolvedId = new RepositoryArtifactResolver<ConfigurationArtifact>(EAIResourceRepository.getInstance(), ConfigurationArtifact.class).getResolvedId(forId, artifacts);
			if (resolvedId != null) {
				ConfigurationArtifact important = null;
				for (ConfigurationArtifact artifact : artifacts) {
					if (artifact.getId().equals(resolvedId)) {
						important = artifact;
						break;
					}
				}
				// move it to the front if relevant
				if (important != null && artifacts.indexOf(important) > 0) {
					artifacts.remove(important);
					artifacts.add(0, important);
				}
			}
		}
		
		// add the actual configurations
		for (ConfigurationArtifact artifact : artifacts) {
			configurations.add(artifact.getContent());
		}
		return configurations;
	}
	
	@SuppressWarnings("unchecked")
	public void configure(@NotNull @WebParam(name = "configurationId") String id, @NotNull @WebParam(name = "configuration") Object configuration) throws IOException {
		ConfigurationArtifact artifact = (ConfigurationArtifact) EAIResourceRepository.getInstance().resolve(id);
		if (configuration != null) {
			configuration = configuration instanceof ComplexContent ? configuration : ComplexContentWrapperFactory.getInstance().getWrapper().wrap(configuration);
			ComplexType type = ((ComplexContent) configuration).getType();
			if (!(type instanceof DefinedType)) {
				throw new IllegalArgumentException("The configuration does not have a defined type");
			}
			else if (!((DefinedType) type).getId().equals(artifact.getConfig().getType().getId())) {
				throw new IllegalArgumentException("The configuration type does not match the type of the instance passed in");
			}
		}
		artifact.setContent((ComplexContent) configuration);
		artifact.save(artifact.getDirectory());
		
		ResourceEvent event = new ResourceEvent();
		event.setArtifactId(artifact.getId());
		event.setState(ResourceState.UPDATE);
		EAIResourceRepository.getInstance().getEventDispatcher().fire(event, this);
	}
}

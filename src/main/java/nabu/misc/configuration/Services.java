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
	public List<Object> configurations(@WebParam(name = "typeId") @NotNull String typeId) {
		DefinedType type = executionContext.getServiceContext().getResolver(DefinedType.class).resolve(typeId);
		if (type == null) {
			throw new IllegalArgumentException("Can not find the type: " + typeId);
		}
		List<Object> configurations = new ArrayList<Object>();
		List<ConfigurationArtifact> artifacts = EAIResourceRepository.getInstance().getArtifacts(ConfigurationArtifact.class);
		for (ConfigurationArtifact artifact : artifacts) {
			try {
				Type current = artifact.getConfiguration().getType();
				while (current != null) {
					if (current.equals(type)) {
						configurations.add(artifact.getContent());
						break;
					}
					current = current.getSuperType();
				}
			}
			catch (Exception e) {
				// ignore
			}
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
	}
}

package nabu.misc.configuration;

import java.util.ArrayList;
import java.util.List;

import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.validation.constraints.NotNull;

import be.nabu.eai.module.configuration.ConfigurationArtifact;
import be.nabu.eai.repository.EAIResourceRepository;
import be.nabu.libs.services.api.ExecutionContext;
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
}

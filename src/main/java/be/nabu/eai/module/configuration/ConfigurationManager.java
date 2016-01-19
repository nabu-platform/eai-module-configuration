package be.nabu.eai.module.configuration;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import be.nabu.eai.repository.api.ArtifactManager;
import be.nabu.eai.repository.api.ModifiableNodeEntry;
import be.nabu.eai.repository.api.ResourceEntry;
import be.nabu.eai.repository.managers.base.JAXBArtifactManager;
import be.nabu.libs.validator.api.Validation;
import be.nabu.libs.validator.api.ValidationMessage;
import be.nabu.libs.validator.api.ValidationMessage.Severity;

public class ConfigurationManager implements ArtifactManager<ConfigurationArtifact> {

	@Override
	public List<Validation<?>> save(ResourceEntry entry, ConfigurationArtifact artifact) throws IOException {
		try {
			artifact.save(entry.getContainer());
		}
		catch (IOException e) {
			List<Validation<?>> messages = new ArrayList<Validation<?>>();
			messages.add(new ValidationMessage(Severity.ERROR, "Could not save " + artifact.getId() + ": " + e.getMessage()));
			return messages;
		}
		if (entry instanceof ModifiableNodeEntry) {
			((ModifiableNodeEntry) entry).updateNode(getReferences(artifact));
		}
		return null;
	}

	@Override
	public ConfigurationArtifact load(ResourceEntry entry, List<Validation<?>> messages) throws IOException, ParseException {
		return new ConfigurationArtifact(entry.getId(), entry.getContainer(), entry.getRepository());
	}
	
	@Override
	public Class<ConfigurationArtifact> getArtifactClass() {
		return ConfigurationArtifact.class;
	}

	@Override
	public List<String> getReferences(ConfigurationArtifact artifact) throws IOException {
		return JAXBArtifactManager.getObjectReferences(artifact.getConfiguration());
	}

	@Override
	public List<Validation<?>> updateReference(ConfigurationArtifact artifact, String from, String to) throws IOException {
		return JAXBArtifactManager.updateObjectReferences(artifact.getConfiguration(), artifact.getRepository(), from, to);
	}

}

package be.nabu.eai.module.configuration;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import be.nabu.eai.repository.jaxb.ArtifactXMLAdapter;
import be.nabu.libs.types.api.DefinedType;

@XmlRootElement(name = "meta")
public class Configuration {
	
	private boolean environmentSpecific = true;
	private DefinedType type;

	@XmlJavaTypeAdapter(value = ArtifactXMLAdapter.class)
	public DefinedType getType() {
		return type;
	}
	public void setType(DefinedType type) {
		this.type = type;
	}
	public boolean isEnvironmentSpecific() {
		return environmentSpecific;
	}
	public void setEnvironmentSpecific(boolean environmentSpecific) {
		this.environmentSpecific = environmentSpecific;
	}
	
}

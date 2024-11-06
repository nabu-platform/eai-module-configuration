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

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import be.nabu.eai.repository.jaxb.ArtifactXMLAdapter;
import be.nabu.libs.types.api.DefinedType;

@XmlRootElement(name = "meta")
public class Configuration {
	
	private boolean environmentSpecific = true;
	private DefinedType type;
	private String context;

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
	// like jdbc pools -> the context this is active in if asked
	public String getContext() {
		return context;
	}
	public void setContext(String context) {
		this.context = context;
	}
}

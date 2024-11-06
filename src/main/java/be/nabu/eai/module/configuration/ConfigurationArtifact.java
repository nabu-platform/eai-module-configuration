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
import java.nio.charset.Charset;
import java.util.Set;

import be.nabu.eai.module.types.structure.StructureManager;
import be.nabu.eai.repository.api.Repository;
import be.nabu.eai.repository.artifacts.jaxb.JAXBArtifact;
import be.nabu.libs.artifacts.api.ContextualArtifact;
import be.nabu.libs.resources.api.ManageableContainer;
import be.nabu.libs.resources.api.ReadableResource;
import be.nabu.libs.resources.api.Resource;
import be.nabu.libs.resources.api.ResourceContainer;
import be.nabu.libs.resources.api.WritableResource;
import be.nabu.libs.services.api.DefinedService;
import be.nabu.libs.services.api.ExecutionContext;
import be.nabu.libs.services.api.Service;
import be.nabu.libs.services.api.ServiceException;
import be.nabu.libs.services.api.ServiceInstance;
import be.nabu.libs.services.api.ServiceInterface;
import be.nabu.libs.types.api.ComplexContent;
import be.nabu.libs.types.api.ComplexType;
import be.nabu.libs.types.base.ComplexElementImpl;
import be.nabu.libs.types.base.ValueImpl;
import be.nabu.libs.types.binding.api.Window;
import be.nabu.libs.types.binding.xml.XMLBinding;
import be.nabu.libs.types.properties.NillableProperty;
import be.nabu.libs.types.structure.Structure;
import be.nabu.utils.io.IOUtils;
import be.nabu.utils.io.api.ByteBuffer;
import be.nabu.utils.io.api.ReadableContainer;
import be.nabu.utils.io.api.WritableContainer;

public class ConfigurationArtifact extends JAXBArtifact<Configuration> implements DefinedService, ContextualArtifact {

	private ComplexContent content;
	private Structure input = new Structure();
	private Structure output;
	
	public ConfigurationArtifact(String id, ResourceContainer<?> directory, Repository repository) {
		super(id, directory, repository, "meta.xml", Configuration.class);
		input.setName("input");
	}

	public ComplexContent getContent() {
		if (content == null) {
			synchronized(this) {
				if (content == null) {
					try {
						Resource child = getDirectory().getChild("configuration.xml");
						if (child == null) {
							content = ((ComplexType) getConfiguration().getType()).newInstance();
						}
						else {
							XMLBinding binding = new XMLBinding((ComplexType) getConfiguration().getType(), Charset.forName("UTF-8"));
							binding.setIgnoreUndefined(true);
							try {
								ReadableContainer<ByteBuffer> readable = ((ReadableResource) child).getReadable();
								try {
									content = binding.unmarshal(IOUtils.toInputStream(readable), new Window[0]);
								}
								finally {
									readable.close();
								}
							}
							catch (Exception e) {
								throw new RuntimeException(e);
							}
						}
					}
					catch (IOException e) {
						throw new RuntimeException(e);
					}
				}
			}
		}
		return content;
	}
	
	public void setContent(ComplexContent content) {
		this.content = content;
	}

	public void save(ResourceContainer<?> container) throws IOException {
		super.save(container);
		ComplexContent content = getContent();
		XMLBinding binding = new XMLBinding((ComplexType) getConfiguration().getType(), Charset.forName("UTF-8"));
		Resource child = container.getChild("configuration.xml");
		if (child == null) {
			child = ((ManageableContainer<?>) container).create("configuration.xml", "application/xml");
		}
		WritableContainer<ByteBuffer> writable = ((WritableResource) child).getWritable();
		try {
			binding.marshal(IOUtils.toOutputStream(writable, true), content, new ValueImpl<Boolean>(NillableProperty.getInstance(), false));
		}
		finally {
			writable.close();
		}
		// we also write the definition in a standardized format because
		// - we can't guarantee the definition is in the same project
		// - we can't guarantee the definition is a structure
		// we need the definition for merge scripts etc to know what is possible in the configuration rather than what is currently there
		if (getConfig().getType() instanceof ComplexType) {
			StructureManager.format(container, (ComplexType) getConfig().getType(), "definition.xml", true);
		}
	}

	@Override
	public ServiceInterface getServiceInterface() {
		return new ServiceInterface() {
			@Override
			public ComplexType getInputDefinition() {
				return input;
			}
			@Override
			public ComplexType getOutputDefinition() {
				if (output == null) {
					synchronized(ConfigurationArtifact.this) {
						try {
							Structure output = new Structure();
							output.setName("output");
							output.add(new ComplexElementImpl("configuration", (ComplexType) getConfiguration().getType(), output));
							ConfigurationArtifact.this.output = output;
						}
						catch (IOException e) {
							throw new RuntimeException(e);
						}
					}
				}
				return output;
			}
			@Override
			public ServiceInterface getParent() {
				return null;
			}
		};
	}

	@Override
	public ServiceInstance newInstance() {
		return new ServiceInstance() {
			@Override
			public Service getDefinition() {
				return ConfigurationArtifact.this;
			}
			@Override
			public ComplexContent execute(ExecutionContext executionContext, ComplexContent input) throws ServiceException {
				ComplexContent newInstance = getServiceInterface().getOutputDefinition().newInstance();
				newInstance.set("configuration", getContent());
				return newInstance;
			}
		};
	}

	@Override
	public Set<String> getReferences() {
		return null;
	}

	@Override
	public String getContext() {
		return getConfig().getContext();
	}

}

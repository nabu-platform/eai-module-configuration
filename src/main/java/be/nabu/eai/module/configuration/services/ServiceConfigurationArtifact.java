package be.nabu.eai.module.configuration.services;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Set;

import be.nabu.eai.repository.api.Repository;
import be.nabu.eai.repository.artifacts.jaxb.JAXBArtifact;
import be.nabu.libs.resources.api.ManageableContainer;
import be.nabu.libs.resources.api.ReadableResource;
import be.nabu.libs.resources.api.Resource;
import be.nabu.libs.resources.api.ResourceContainer;
import be.nabu.libs.resources.api.WritableResource;
import be.nabu.libs.services.api.DefinedService;
import be.nabu.libs.services.api.ServiceInstance;
import be.nabu.libs.services.api.ServiceInterface;
import be.nabu.libs.types.api.ComplexContent;
import be.nabu.libs.types.api.ComplexType;
import be.nabu.libs.types.base.ValueImpl;
import be.nabu.libs.types.binding.api.Window;
import be.nabu.libs.types.binding.xml.XMLBinding;
import be.nabu.libs.types.properties.NillableProperty;
import be.nabu.libs.types.structure.Structure;
import be.nabu.utils.io.IOUtils;
import be.nabu.utils.io.api.ByteBuffer;
import be.nabu.utils.io.api.ReadableContainer;
import be.nabu.utils.io.api.WritableContainer;

public class ServiceConfigurationArtifact extends JAXBArtifact<ServiceConfiguration> implements DefinedService {

	private ComplexContent content;
	private Structure input, output;
	
	public ServiceConfigurationArtifact(String id, ResourceContainer<?> directory, Repository repository) {
		super(id, directory, repository, "service-configuration.xml", ServiceConfiguration.class);
	}
	
	public ComplexContent getContent() {
		if (content == null) {
			synchronized(this) {
				if (content == null) {
					try {
						Resource child = getDirectory().getChild("configuration.xml");
						if (child == null) {
							content = ((ComplexType) getConfiguration().getService().getServiceInterface().getInputDefinition()).newInstance();
						}
						else {
							XMLBinding binding = new XMLBinding((ComplexType) getConfiguration().getService().getServiceInterface().getInputDefinition(), Charset.forName("UTF-8"));
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
		XMLBinding binding = new XMLBinding((ComplexType) getConfiguration().getService().getServiceInterface().getInputDefinition(), Charset.forName("UTF-8"));
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
	}

	@Override
	public ServiceInterface getServiceInterface() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ServiceInstance newInstance() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> getReferences() {
		return null;
	}

}

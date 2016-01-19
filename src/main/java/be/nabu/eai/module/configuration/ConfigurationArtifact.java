package be.nabu.eai.module.configuration;

import java.io.IOException;
import java.nio.charset.Charset;

import be.nabu.eai.repository.api.Repository;
import be.nabu.libs.artifacts.api.Artifact;
import be.nabu.libs.resources.api.ManageableContainer;
import be.nabu.libs.resources.api.ReadableResource;
import be.nabu.libs.resources.api.WritableResource;
import be.nabu.libs.resources.api.Resource;
import be.nabu.libs.resources.api.ResourceContainer;
import be.nabu.libs.types.api.ComplexContent;
import be.nabu.libs.types.api.ComplexType;
import be.nabu.libs.types.base.ValueImpl;
import be.nabu.libs.types.binding.api.Window;
import be.nabu.libs.types.binding.xml.XMLBinding;
import be.nabu.libs.types.java.BeanResolver;
import be.nabu.libs.types.properties.NillableProperty;
import be.nabu.utils.io.IOUtils;
import be.nabu.utils.io.api.ByteBuffer;
import be.nabu.utils.io.api.ReadableContainer;
import be.nabu.utils.io.api.WritableContainer;

public class ConfigurationArtifact implements Artifact {

	private String id;
	private Repository repository;
	private ComplexContent configuration;
	private ResourceContainer<?> directory;
	
	public ConfigurationArtifact(String id, ResourceContainer<?> directory, Repository repository) {
		this.id = id;
		this.directory = directory;
		this.repository = repository;
	}
	
	@Override
	public String getId() {
		return id;
	}

	public Repository getRepository() {
		return repository;
	}
	
	public ComplexContent getConfiguration() {
		if (configuration == null) {
			synchronized(this) {
				if (configuration == null) {
					XMLBinding binding = new XMLBinding((ComplexType) BeanResolver.getInstance().resolve(Object.class), Charset.forName("UTF-8"));
					Resource child = directory.getChild("configuration.xml");
					if (child == null) {
						throw new IllegalStateException("Missing configuration file");
					}
					try {
						ReadableContainer<ByteBuffer> readable = ((ReadableResource) child).getReadable();
						try {
							configuration = binding.unmarshal(IOUtils.toInputStream(readable), new Window[0]);
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
		}
		return configuration;
	}
	
	public void setConfiguration(ComplexContent configuration) {
		this.configuration = configuration;
	}

	public void save(ResourceContainer<?> container) throws IOException {
		ComplexContent content = getConfiguration();
//		XMLBinding binding = new XMLBinding(content.getType(), Charset.forName("UTF-8"));
		XMLBinding binding = new XMLBinding((ComplexType) BeanResolver.getInstance().resolve(Object.class), Charset.forName("UTF-8"));
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
}

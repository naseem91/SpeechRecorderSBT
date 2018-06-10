package ipsk.db.speech;

import ipsk.beans.PreferredDisplayOrder;
import ipsk.persistence.ObjectImmutableIfReferenced;
import ipsk.util.ResourceBundleName;
import ipsk.util.ResourceKey;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

@Entity
@PreferredDisplayOrder("id,propertyClassId,propertyName,required,show,formConfiguration")
@ResourceBundleName("ipsk.db.speech.PropertyNames")
@ResourceKey("property.configuration")

public class PropertyConfiguration implements ipsk.beans.form.PropertyConfiguration{
	private int id;
	private FormConfiguration formConfiguration;
	
	@ManyToOne
	@ObjectImmutableIfReferenced
	@ResourceKey("form.configuration")
	public FormConfiguration getFormConfiguration() {
		return formConfiguration;
	}
	public void setFormConfiguration(FormConfiguration formConfiguration) {
		this.formConfiguration = formConfiguration;
	}
	//	private Set<FormConfiguration> formConfigurations;
//	
//	@ManyToMany(mappedBy="inputPropertyConfigurations")
//	@ResourceKey("form.configurations")
//	public Set<FormConfiguration> getFormConfigurations() {
//		return formConfigurations;
//	}
//	public void setFormConfigurations(Set<FormConfiguration> formConfigurations) {
//		this.formConfigurations = formConfigurations;
//	}
	@Id
	@Column(name = "id", unique = true, nullable = false)
    @GeneratedValue(generator="id_gen")
	@ResourceKey("id")
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	private String propertyClassId;
	
	@Column
	@ResourceKey("class.id")
	public String getPropertyClassId() {
		return propertyClassId;
	}
	public void setPropertyClassId(String propertyClassId) {
		this.propertyClassId = propertyClassId;
	}
	@Column
	@ResourceKey("name")
	public String getPropertyName() {
		return propertyName;
	}
	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}
	@Column
	@ResourceKey("property.descriptor")
	public TypedPropertyDescriptor getTypedPropertyDescriptor() {
		return typedPropertyDescriptor;
	}
	public void setTypedPropertyDescriptor(
			TypedPropertyDescriptor typedPropertyDescriptor) {
		this.typedPropertyDescriptor = typedPropertyDescriptor;
	}
	@Column
	@ResourceKey("form.property.required")
	public boolean isRequired() {
		return required;
	}
	public void setRequired(boolean required) {
		this.required = required;
	}
	@Column
	@ResourceKey("show")
	public boolean isShow() {
		return show;
	}
	public void setShow(boolean show) {
		this.show = show;
	}
	private String propertyName;
	private TypedPropertyDescriptor typedPropertyDescriptor;
	private boolean required=true;
	private boolean show=true;

//	@Override
	@Transient
	public Class<?> getBeanClass() {
		
		String pcid=getPropertyClassId();
		String pcClassName=pcid.replaceFirst("java:","");
		try {
			return Class.forName(pcClassName);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO see Speechrecorder XML class name coding
		return null;
	}
//	@Override
	@Transient
	public PropertyDescriptor getPropertyDescriptor() {
		Class<?> c=getBeanClass();
		BeanInfo bi;
		try {
			bi = Introspector.getBeanInfo(c);
			PropertyDescriptor[] pds=bi.getPropertyDescriptors();
			String pName=getPropertyName();
			for(PropertyDescriptor pd:pds){
				if(pd.getName().equals(pName)){
					return pd;
				}
			}
		} catch (IntrospectionException e) {
			return null;
		}
		
		return null;
	}
}

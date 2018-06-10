package ipsk.db.speech;

import ipsk.beans.PreferredDisplayOrder;

import ipsk.persistence.ObjectImmutableIfReferenced;
import ipsk.util.PluralResourceKey;
import ipsk.util.ResourceBundleName;
import ipsk.util.ResourceKey;
import ipsk.util.annotations.TextAreaView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Transient;


@Entity
@ResourceBundleName("ipsk.db.speech.PropertyNames")
@ResourceKey("form.configuration")
@PluralResourceKey("form.configurations")
@PreferredDisplayOrder("id,displayName,description,defaultRequired,*")
public class FormConfiguration implements ipsk.beans.form.FormConfiguration{

	
	private int id;
//	private Set<Project> projects=new HashSet<Project>(); 
//	
	
	private Set<Project> speakerFormProjects=new HashSet<Project>();
	@OneToMany(mappedBy="speakerFormConfiguration")
	@ResourceKey("projects")
	@ObjectImmutableIfReferenced
	public Set<Project> getSpeakerFormProjects() {
		return speakerFormProjects;
	}
	public void setSpeakerFormProjects(Set<Project> speakerFormProjects) {
		this.speakerFormProjects = speakerFormProjects;
	}
	
//	@ManyToMany
//	public Set<Project> getProjects() {
//		return projects;
//	}
//	public void setProjects(Set<Project> projects) {
//		this.projects = projects;
//	}
	//	private Project project;
//	
//	@ManyToOne
//	@JoinColumn(name="project")
//	@ResourceKey("project")
//	public Project getProject() {
//		return project;
//	}
//	public void setProject(Project project) {
//		this.project = project;
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
	private String displayName;
	
	@ResourceKey("name.display")
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	private String description;
	
	
	@Column(length=1000)
	@TextAreaView
	@ResourceKey("description")
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
//	private boolean defaultShow=true;
	private String propertyClassId;
	
	@ResourceKey("class.id")
	public String getPropertyClassId() {
		return propertyClassId;
	}
	public void setPropertyClassId(String propertyClassId) {
		this.propertyClassId = propertyClassId;
	}
//	@ResourceKey("defaults.show")
	
	@Transient
	public boolean isDefaultShow() {
		return false;
	}
//	public void setDefaultShow(boolean defaultShow) {
//		this.defaultShow = defaultShow;
//	}
//	@ManyToMany()
//	@JoinTable(
//		        name="form_configuration_input_property_configuration",
//		        joinColumns={@JoinColumn(name="form_configuration_id")},
//		        inverseJoinColumns={@JoinColumn(name="property_configuration_id")}
//		    )
	@OneToMany(mappedBy="formConfiguration",orphanRemoval=true,cascade=CascadeType.ALL)
	@OrderColumn(name="position")
    @ResourceKey("property.configurations")
	public List<PropertyConfiguration> getInputPropertyConfigurations() {
		return inputPropertyConfigurations;
	}
	public void setInputPropertyConfigurations(
			List<PropertyConfiguration> inputpropertyConfiguartions) {
		this.inputPropertyConfigurations = inputpropertyConfiguartions;
	}
	private boolean defaultRequired=true;
	
	@ResourceKey("defaults.required")
	public boolean isDefaultRequired() {
		return defaultRequired;
	}
	public void setDefaultRequired(boolean defaultRequired) {
		this.defaultRequired = defaultRequired;
	}
	private List<PropertyConfiguration> inputPropertyConfigurations=new ArrayList<PropertyConfiguration>();


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
////	@Override
	@Transient
	public List<ipsk.beans.form.PropertyConfiguration> getPropertyConfigurations() {
		List<PropertyConfiguration> persInputPropCfgs=getInputPropertyConfigurations();
		ArrayList<ipsk.beans.form.PropertyConfiguration> list=new ArrayList<ipsk.beans.form.PropertyConfiguration>();
		
		list.addAll(persInputPropCfgs);
		return list;
	}
	
	@Transient
	public String toString(){
		StringBuffer sb=new StringBuffer();
		if(displayName!=null){
			sb.append(displayName);
			sb.append(" (ID: ");
			sb.append(id);
			sb.append(")");
		}else{
			sb.append("ID: ");
			sb.append(id);
		}
		return sb.toString();
	}
	
	public static void main(String[] args){
		FormConfiguration fc=new FormConfiguration();
		fc.setPropertyClassId("java:");
	}
	
}

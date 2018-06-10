package ipsk.beans.form;

import java.util.List;

public interface  FormConfiguration {
	
	
	public String getDisplayName();
	
	
	public String getDescription();

	
	public Class<?> getBeanClass();
	
	
	public boolean isDefaultShow();
	
	
	public List<PropertyConfiguration> getPropertyConfigurations();
	
	
	public boolean isDefaultRequired();
}

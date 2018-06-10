package ipsk.beans.form;

import java.beans.PropertyDescriptor;


public interface PropertyConfiguration {
	
	public Class<?> getBeanClass();
	
	public PropertyDescriptor getPropertyDescriptor();
	
	public boolean isRequired();
	
	public boolean isShow();
}

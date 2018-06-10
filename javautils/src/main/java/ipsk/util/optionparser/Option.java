//    IPS Java Utils
//    (c) Copyright 2009-2011
//    Institute of Phonetics and Speech Processing,
//    Ludwig-Maximilians-University, Munich, Germany
//
//
//    This file is part of IPS Java Utils
//
//
//    IPS Java Utils is free software: you can redistribute it and/or modify
//    it under the terms of the GNU Lesser General Public License as published by
//    the Free Software Foundation, version 3 of the License.
//
//    IPS Java Utils is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU Lesser General Public License for more details.
//
//    You should have received a copy of the GNU Lesser General Public License
//    along with IPS Java Utils.  If not, see <http://www.gnu.org/licenses/>.


package ipsk.util.optionparser;


/**
 * Represents an command line option.
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */
public class Option {

	private String name;
	private boolean hasParam;
	private String param;
	private boolean value=false;
	
	/**
	 * Create an option without parameter.
	 * @param option name
	 */
	public Option(String option) {
			this.name=option;
			this.hasParam=false;
			this.value=false;
		}
	
	/**
	 * Create an option with parameter
	 * @param option option name
	 * @param defaultValue default value of parameter
	 */
	public Option(String option,String defaultValue) {
		this.name=option;
		this.hasParam=true;
		this.param=defaultValue;	
	}
	
	/** 
	 * Returns true if the option has a parameter
	 * @return true if option has a parameter
	 */
	public boolean hasParam() {
		return hasParam;
	}


	/**
	 * Return name of option
	 * @return name of option
	 */
	public String getOptionName() {
		return name;
	}

	/**
	 * Returns parameter
	 * @return parameter
	 */
	public String getParam() {
		return param;
	}

	/**
	 * Sets parameter.
	 * @param string parameter
	 */
	public void setParam(String string) {
		param = string;
		value=true;
	}

	/**
	 * Returns true if parameter is set.
	 * @return true if parameter is set
	 */
	public boolean isSet() {
		return value;
	}

	/**
	 * Set/unset if parameter is set.
	 * @param b 
	 */
	public void setValue(boolean b) {
		value = b;
	}

}

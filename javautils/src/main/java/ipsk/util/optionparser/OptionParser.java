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

import java.util.Enumeration;
import java.util.Vector;


/**
 * Parses command line parameter tokens.
 * Splits the given parameter tokens into options and parameters.
 * An option starts with an '-' character.
 * Currently only short options ( option name has only one char) are supported.
 * The parameter token '--' is interpreted as end of th option section.
 *  
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 */
public class OptionParser extends Vector<Option> {

	private String[] params;

	/**
	 * Create option parser.
	 *
	 */
	public OptionParser() {
		super();
	}

	/**
	 * Add a syntactically allowed option.
	 * @param o option
	 */
	public void addOption(Option o) {
		add(o);
	}
	/**
		 * Remove an allowed option.
		 * @param o option
		 */
	public void removeOption(Option o) {
		remove(o);
	}

	/**
	   * Add an syntactically allowed option by name.
	   * @param opt 
	   */
	public void addOption(String opt) {
		add(new Option(opt));
	}
	/**
	   * Add an syntactically allowed option with parameter by name.
	   * @param opt option
	   * @param value default value
	*/
	public void addOption(String opt, String value) {
		add(new Option(opt, value));
	}

/**
 * Parse a command line.
 * @param args command line as string array
 * @throws OptionParserException if an syntax error occurs
 */
	public void parse(String[] args) throws OptionParserException {
		
		int i = 0;
		while (i < args.length) {
			if (args[i].startsWith("-")) {

				String optionChar = args[i].substring(1);
				if (optionChar.equals("-")) {
					// UNIX syntax: indicates end of options
					i++;
					break;
				}
				boolean found = false;
				Option opt = null;
				for (int j = 0; j < size(); j++) {
					opt = (Option) elementAt(j);
					if (optionChar.equals(opt.getOptionName())) {
						found = true;
						if (opt.hasParam()) {
							i++;
							if (i == args.length)
								throw new OptionParserException(
									"Missing parmeter to option "
										+ args[i
										- 1]);
							opt.setParam(args[i]);
						} else {
							opt.setValue(true);
						}
					}
				}
				if (!found)
					throw new OptionParserException(
						"Unknown option: " + args[i]);

			} else {
				break;
			}
			i++;
		}
		// Options parsed, now parameters
		int numParams = args.length - i;
		params = new String[numParams];
		for (int j = 0; j < numParams; j++) {
			params[j] = args[i + j];
		}
	}

	/**
	 * Returns true if the option is set on the command line.
	 * @param opt name of the option
	 * @return true if set
	 */
	public boolean isOptionSet(String opt) {
		Option o = getOption(opt);
		return o.isSet();
	}

	/**
	 * Get option by name.
	 * @param opt name of option
	 * @return option
	 */
	public Option getOption(String opt) {
		for (Enumeration<Option> e = elements(); e.hasMoreElements();) {
			Option o =e.nextElement();
			if (o.getOptionName().equals(opt))
				return o;
		}
		return null;
	}

	/**
	 * Returns command line parameters.
	 * @return command line parameters
	 */
	public String[] getParams() {
		return params;
	}

	/**
	 * Set command line parameters.
	 * @param strings command line parameters
	 */
	public void setParams(String[] strings) {
		params = strings;
	}

	
	/**
	 * Get all set options.
	 * @return options
	 */
	public Option[] getOptions() {
		return (Option[]) toArray(new Option[0]);
	}
	
	public static void main(String args[]) {
			OptionParser op = new OptionParser();
			op.addOption("v");
			op.addOption("a");
			op.addOption("t", "default_value");
			try {
				op.parse(args);
			} catch (OptionParserException e) {
				e.printStackTrace();
			}
		}


}

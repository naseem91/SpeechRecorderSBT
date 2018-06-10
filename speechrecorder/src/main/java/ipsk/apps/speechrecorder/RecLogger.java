//    Speechrecorder
//    (c) Copyright 2009-2011
// 	  Institute of Phonetics and Speech Processing,
//    Ludwig-Maximilians-University, Munich, Germany
//
//
//    This file is part of Speechrecorder
//
//
//    Speechrecorder is free software: you can redistribute it and/or modify
//    it under the terms of the GNU Lesser General Public License as published by
//    the Free Software Foundation, version 3 of the License.
//
//    Speechrecorder is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU Lesser General Public License for more details.
//
//    You should have received a copy of the GNU Lesser General Public License
//    along with Speechrecorder.  If not, see <http://www.gnu.org/licenses/>.


package ipsk.apps.speechrecorder;
import java.util.*;
import java.io.*;

public class RecLogger {

	private static RecLogger _instance = null;

	private Hashtable tagList = null;
	private File logDirectory;

	private RecLogger() {
		tagList = new Hashtable();
	}

	public static RecLogger getInstance() {
		if (_instance == null)
			_instance = new RecLogger();
		return _instance;
	}

	public void setDirectory(File directory) {
		logDirectory = directory;
	}

	public File getDirectory() {
		return logDirectory;
	}

	public void setLogEntry(String label, String entry) {
		tagList.put(label, entry);
	}

	private String generateNewName(String inName) {
		File newName = new File(inName);
		File tmpName = new File(inName);

		if (tmpName.exists()) {
			int versionCount = 0;
			while (tmpName.exists()) {
				versionCount++;
				tmpName = new File(inName + "_" + versionCount);
				//System.out.println("Directory or file exists; trying : " + tmpName);
			}
			newName.renameTo(tmpName);
		}
		return inName;
	}

	public void createLabelFile(String fileName) {
		File lblFile =
			new File(
				generateNewName(
					logDirectory.getAbsolutePath()
						+ File.separator
						+ fileName));
		try {
			FileWriter fw = new FileWriter(lblFile);
			Enumeration e = tagList.keys();

			while (e.hasMoreElements()) {
				String key = (String) e.nextElement();
				fw.write(key + tagList.get(key) + "\n");
			}

			fw.close();
		} catch (IOException e) {
			System.out.println("createLabelFile(" + fileName + "): " + e);
		}
	}
}

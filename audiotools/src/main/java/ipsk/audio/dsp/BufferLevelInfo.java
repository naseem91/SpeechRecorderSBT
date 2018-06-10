//    IPS Java Audio Tools
//    (c) Copyright 2009-2011
//    Institute of Phonetics and Speech Processing,
//    Ludwig-Maximilians-University, Munich, Germany
//
//
//    This file is part of IPS Java Audio Tools
//
//
//    IPS Java Audio Tools is free software: you can redistribute it and/or modify
//    it under the terms of the GNU Lesser General Public License as published by
//    the Free Software Foundation, version 3 of the License.
//
//    IPS Java Audio Tools is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU Lesser General Public License for more details.
//
//    You should have received a copy of the GNU Lesser General Public License
//    along with IPS Java Audio Tools.  If not, see <http://www.gnu.org/licenses/>.

/*
 * Created on 24.08.2005
 * 
 */
package ipsk.audio.dsp;

public class BufferLevelInfo extends BufferInfo {

	private LevelInfo[] levelInfos = null;

	public BufferLevelInfo() {
		super();
	}

	public BufferLevelInfo(long framePos, int length) {
		super(framePos, length);
	}

	public BufferLevelInfo(long framePos, int length, LevelInfo[] levelInfos) {
		this(framePos, length);
		this.levelInfos = levelInfos;
	}

	/**
	 * @return Returns the levelInfos.
	 */
	public LevelInfo[] getLevelInfos() {
		return levelInfos;
	}

	/**
	 * Set audio level infos.
	 * @param levelInfo array of level infos
	 */
	public void setLevelInfos(LevelInfo[] levelInfo) {
		this.levelInfos = levelInfo;
	}
}

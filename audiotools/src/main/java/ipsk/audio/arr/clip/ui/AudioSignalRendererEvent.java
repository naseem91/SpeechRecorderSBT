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

package ipsk.audio.arr.clip.ui;

import ipsk.audio.arr.clip.ui.AudioSignalRenderer.RenderResult;

import java.util.EventObject;

public class AudioSignalRendererEvent extends EventObject {
	
	public static enum Type {UPDATE,DONE};
	private RenderResult renderResult;
	private Throwable renderException=null;
	private Type type=Type.DONE;
	private int startPixel=-1;
	/**
	 * @return the type
	 */
	public Type getType() {
		return type;
	}
	/**
	 * @return the startPixel
	 */
	public int getStartPixel() {
		return startPixel;
	}
	/**
	 * @return the len
	 */
	public int getLen() {
		return len;
	}
	private int len=-1;
	public AudioSignalRendererEvent(Object arg0,AudioSignalRenderer.RenderResult res) {
		super(arg0);
		this.renderResult=res;
	}
	public AudioSignalRendererEvent(Object source,
			Throwable e) {
		super(source);
		renderResult=null;
		this.renderException=e;
	}
	
	public AudioSignalRendererEvent(Object src,
			RenderResult rs, int startPixel, int len) {
			super(src);
			this.renderResult=rs;
			type=Type.UPDATE;
			this.startPixel=startPixel;
			this.len=len;
	}
	public RenderResult getRenderResult() {
		return renderResult;
	}
	public void setRenderResult(RenderResult renderResult) {
		this.renderResult = renderResult;
	}
	public Throwable getRenderException() {
		return renderException;
	}

}

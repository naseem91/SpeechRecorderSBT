package ipsk.audio.arr.clip;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.sound.sampled.AudioFormat;

import ipsk.audio.AudioFormatNotSupportedException;
import ipsk.audio.AudioSource;
import ipsk.audio.AudioSourceException;
import ipsk.audio.FileAudioSource;
import ipsk.audio.arr.clip.AudioClip;
import ipsk.audio.arr.clip.ui.AudioClipUIContainer;
import ipsk.audio.arr.clip.ui.AudioSignalRenderer;
import ipsk.audio.arr.clip.ui.AudioSignalRendererEvent;
import ipsk.audio.arr.clip.ui.AudioSignalRendererListener;
import ipsk.audio.arr.clip.ui.AudioSignalUI;

public class AudioClipPainter implements AudioSignalRendererListener {

	public AudioSource audioSource;
	
	public AudioClipPainter(AudioSource audioSource){
		this.audioSource=audioSource;
	}
	public void paintToImage(BufferedImage img) throws AudioFormatNotSupportedException, AudioSourceException{
		paintToImage(img, 1.0);
	}
	public void paintToImage(BufferedImage img,double xZoom) throws AudioFormatNotSupportedException, AudioSourceException{
		int w=img.getWidth();
		int h=img.getHeight();
		Dimension size=new Dimension(w, h);
		AudioClip ac=new AudioClip(audioSource);
		
//		AudioClipUIContainer acUI=new AudioClipUIContainer(ac);
//		acUI.setXZoom(xZoom);

		AudioSignalUI signalUI=new AudioSignalUI(ac);
		signalUI.setSize(size);
		signalUI.doLayout();
//		acUI.add(signalUI);
//		ac.setAudioSource(audioSource);
//
//		acUI.setSize(size);
//		acUI.setXZoom(xZoom);
//		acUI.doLayout();

		Graphics g=img.getGraphics();
//		acUI.printAll(g);
	}
	
	
	public RenderedImage createImage(double xZoom,int height) throws AudioFormatNotSupportedException, AudioSourceException{
		AudioFormat af=audioSource.getFormat();
		long frameLength=audioSource.getFrameLength();
		double lengthSeconds=(double)frameLength/(double)af.getSampleRate();
		int w=(int)(lengthSeconds*xZoom);
		double framesPerPixel = (double) frameLength / (double) w;
		Dimension size=new Dimension(w, height);
		BufferedImage img=new BufferedImage(w, height, BufferedImage.TYPE_INT_ARGB);
//		AudioClip ac=new AudioClip();
//		
//		AudioClipUIContainer acUI=new AudioClipUIContainer(ac);
//		acUI.setXZoom(xZoom);
////		acUI.setFixXZoomFitToPanel(true);
//		AudioSignalUI signalUI=new AudioSignalUI(ac);
//		
//		acUI.add(signalUI);
//		ac.setAudioSource(audioSource);
////		acUI.setAudioClip(ac);
//		
////		signalUI.setSize(size);
//		
//		acUI.setSize(size);
//		acUI.setXZoom(xZoom);
//		acUI.doLayout();
//		
////		acUI.add(signalUI);
//		Graphics g=img.getGraphics();
////	
////		g.setColor(Color.WHITE);
////		g.fillRect(0, 0, img.getWidth(), img.getHeight());
////		acUI.print(g);
//		acUI.printAll(g);
//		return img;
		
		AudioSignalRenderer asr=new AudioSignalRenderer(audioSource, this);
		asr.setBackgroundColor(Color.BLACK);
		return asr.renderImage(w,height,framesPerPixel);
	}
	
	
	public static void main(String[] args){
//		BufferedImage img=new BufferedImage(300, 40, BufferedImage.TYPE_INT_ARGB);
		
		FileAudioSource fas=new FileAudioSource(new File(args[0]));
		AudioClipPainter acp=new AudioClipPainter(fas);
		try {
//			acp.paintToImage(img);
			RenderedImage img;
			img=acp.createImage(20, 50);
			ImageIO.write(img, "png", new File(args[1]));
		} catch (AudioFormatNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AudioSourceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.exit(0);
		
	}
	/* (non-Javadoc)
	 * @see ipsk.audio.arr.clip.ui.AudioSignalRendererListener#update(ipsk.audio.arr.clip.ui.AudioSignalRendererEvent)
	 */
	public void update(AudioSignalRendererEvent event) {
		// TODO Auto-generated method stub
		
	}
}

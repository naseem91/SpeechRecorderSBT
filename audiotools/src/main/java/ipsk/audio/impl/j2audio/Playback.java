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

package ipsk.audio.impl.j2audio;

import ipsk.audio.AudioFormatNotSupportedException;
import ipsk.audio.AudioSource;
import ipsk.audio.AudioSourceException;
import ipsk.audio.dsp.PeakDetector;

import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Control;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
/**
* @deprecated Please use {@link ipsk.audio.player.Player} instead.
*/
public class Playback implements Runnable, LineListener {

    static final boolean DEBUG = false;

    static final long END_OF_AUDIO_FILE = -1;

    public class PlayerStatus extends SynchronizedStatus {
        public static final String IDLE = "Idle";

        public static final String PREPARING = "Preparing";

        public static final String PREPARED = "Prepared";

        public static final String PLAYING = "Playing";

        public static final String PAUSE = "Pause";

        public static final String PAUSE_FLUSHING = "Pause flushing";

        public static final String FLUSHED = "Flushed";

        public static final String DRAINING = "Draining";

        public static final String STOP = "Stop";

        public PlayerStatus(String status) {
            super(status);
        }
    }

	private class BufferLevel {
		public long framePos;

		public int length;

		public float[] peakLevels;

		public BufferLevel() {
		}
	}

    private Mixer mixer;

    private Line.Info lineInfo;

    private SourceDataLine line = null;

    private int lineBufferSize;

    private LineStatus lineStatus = new LineStatus(LineStatus.CLOSED);

    private boolean open;

    private PlayerStatus ps = new PlayerStatus(PlayerStatus.IDLE);

    private PlaybackListener pbl;

    private AudioFormat format;

    private int numChannels;

    private long lengthInFrames;

    private int frameSize;

	//private boolean conversionAllowed = true;

    private long stopPosition = END_OF_AUDIO_FILE;

    private long runStopPosition = Long.MAX_VALUE;

    private long startPosition = 0;

    private long framePosition = 0;

    private long playAisPos = 0;

    private long framePosOffset;

    boolean posIsSynced = true;

    private InputStream inputStream = null;

    private AudioInputStream playAis;

    private AudioSource playSource = null;

    //private float latencyTime = (float) 0.02; //latency time in seconds
    //private float latencyTime = (float) 0.5; //latency time in seconds

    private Thread sdlThread = null;

    private int read;

    private int toWrite, wrote;

    private int numBuffers = 2;

    private int bufferSize;

    private byte[][] buffers;

    private PeakDetector peakDetector = null;

    private float levels[];

    private Vector<BufferLevel> peaks;

    private long statisticPos;

    public Playback(PlaybackListener pbl, Mixer m) {
        this.pbl = pbl;
        mixer = m;
        peaks = new Vector<BufferLevel>();
        open = false;
    }

    public Playback(PlaybackListener pbl, SourceDataLine line) {
        this.pbl = pbl;
        this.line = line;
        mixer = null;
        peaks = new Vector<BufferLevel>();
        open = false;
    }

    public SourceDataLine open(AudioFormat af)
            throws AudioFormatNotSupportedException, LineUnavailableException {
        format = af;
        //this.latencyTime = latencyTime;
        numChannels = af.getChannels();
        frameSize = af.getFrameSize();
        framePosOffset = 0;
        levels = new float[numChannels];
        resetLevels();
        peaks.removeAllElements();
        // We need a new line on some platforms
        lineInfo = new DataLine.Info(SourceDataLine.class, format);
        if (mixer != null) {
            line = (SourceDataLine) mixer.getLine(lineInfo);
        }

        lineStatus = new LineStatus(LineStatus.CLOSED);
        line.addLineListener(this);
        if (DEBUG)
            log("Opening " + line);
        if (DEBUG) System.out.println("SDL Opening with: "+format.getSampleRate());
        line.open(format);
        lineBufferSize = line.getBufferSize();
        if (DEBUG)
            log("SDL buffersize: " + lineBufferSize);

        if (DEBUG)
            log("Source line opened");
        bufferSize = lineBufferSize / 4;
        int bufferSizeInFrames = bufferSize / frameSize;
        bufferSize = bufferSizeInFrames * frameSize;
        if (DEBUG)
            log("Playback engine buffersize: " + bufferSize);
        //sdlBuf = new byte[sdlBufSize];
        buffers = new byte[numBuffers][bufferSize];
        Control sampleRateCtrl = null;
        // Tritonus throws an Exception here !
        try {
            sampleRateCtrl = line.getControl(FloatControl.Type.SAMPLE_RATE);
        } catch (IllegalArgumentException e) {
            // O.K. we cannot set sample rate via control additionally
        }
        if (sampleRateCtrl != null) {
            ((FloatControl) sampleRateCtrl).setValue(format.getSampleRate());
        }
		peakDetector = new PeakDetector(af);

        open = true;
		statisticPos = 0;
        return line;
    }

    public synchronized boolean isPlaying() {
        synchronized (ps) {
            if (ps.getStatus() == PlayerStatus.PLAYING)
                return true;
            else
                return false;
        }
    }

    private void resetLevels() {
        for (int i = 0; i < numChannels; i++) {
            levels[i] = 0;
        }
    }

    public void prepareToPlay(AudioSource src) throws AudioSourceException,
            UnsupportedAudioFileException, IOException {
        inputStream = null;
        playSource = src;
        peaks.removeAllElements();
        prepareToPlay(startPosition);

    }

    //	public void prepareToPlay(File f)
    //		throws UnsupportedAudioFileException, IOException {
    //		prepareToPlay(f.toURL());
    //
    //	}

    public void prepareToPlay(InputStream is) throws AudioSourceException,
            UnsupportedAudioFileException, IOException {
        playSource = null;
        this.inputStream = is;
        peaks.removeAllElements();
        prepareToPlay(startPosition);
    }

    private void prepareToPlay(long position) throws AudioSourceException,
            UnsupportedAudioFileException, IOException {

        if (ps.getStatus() == PlayerStatus.PLAYING)
            throw new IOException("Playback is busy.");

        if (playAis != null)
            playAis.close();
        if (playSource != null) {
            //playAis = AudioSystem.getAudioInputStream(playUrl);
            playAis = playSource.getAudioInputStream();
        } else if (inputStream != null) {
            playAis = AudioSystem.getAudioInputStream(inputStream);
        }
        if (playAis == null)
            throw new AudioSourceException("cannot get audio stream");
        if (line != null) {
            line.flush();

        }
        //playAis.mark(Integer.MAX_VALUE); //Causes dropouts !!! on linux32 ??

        lengthInFrames = playAis.getFrameLength();

        if (lengthInFrames < startPosition)
            startPosition = lengthInFrames;
        if (stopPosition != END_OF_AUDIO_FILE) {
            if (lengthInFrames < stopPosition)
                stopPosition = lengthInFrames;
            if (position > stopPosition)
                position = stopPosition;
        }
        if (position < startPosition)
            position = startPosition;
        if (lengthInFrames < position)
            position = lengthInFrames;
        long toSkip = frameSize * position;
        if (DEBUG)
            log("Prepare segment: " + startPosition + " to " + stopPosition);
        while (toSkip > 0) {
            toSkip -= playAis.skip(toSkip);
        }
        playAisPos = position;
        if (stopPosition == END_OF_AUDIO_FILE) {
            runStopPosition = lengthInFrames;
        } else {
            runStopPosition = stopPosition;
        }
		//		if (runStopPosition >= 0) {
		//			segmentFrameLength = runStopPosition - startPosition;
		//		} else {
		//			segmentFrameLength = playAis.getFrameLength() - startPosition;
		//		}
        if (line != null) {
            line.flush();
            framePosOffset = line.getLongFramePosition() - position;

        }
        //posIsSynced = true;
        if (ps.getStatus() != PlayerStatus.PAUSE)
            ps.setStatus(PlayerStatus.PREPARED);
    }

    public void play() {
        if (ps.getStatus() == PlayerStatus.PLAYING)
            return;
        startSourceDataLine();
        if (DEBUG)
            log("AudioController playing ...");

        return;

    }

    public float[] getLevels() {
		// JavaSound getLevel() is not implemented
        //System.out.println(sdl.getLevel());
        if (ps.getStatus() == PlayerStatus.PLAYING
                || ps.getStatus() == PlayerStatus.DRAINING) {
			BufferLevel ps = null;
            long pos = getFramePosition();
			int i;
			// search for the matching level
			for (i = 0; i < peaks.size(); i++) {
				ps = (BufferLevel) peaks.elementAt(i);

				if (ps.framePos <= pos && ps.framePos + ps.length > pos) {
                    break;
                }
            }
            if (ps == null) {
                //System.out.println("No match!");
				return null; // No matching statistic
            }
			// Remove old values
			for (int r = 0; r < i - 1; r++) {
				peaks.remove(0);
            }
			levels = ps.peakLevels;
		} else {
			resetLevels();
		}
		return levels;

    }

    public void close() {
        if (ps.getStatus() == PlayerStatus.PLAYING) {
            stopPlayback();
        }
        if (line != null && line.isOpen()) {
            line.close();
            while (line.isOpen()) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    // OK
                }
                line.close();
            }
        }

        //lineStatus.waitFor(LineStatus.CLOSED);
        if (line != null)
            line.removeLineListener(this);
        //line = null;
        //playAis = null;
        open = false;
        if (DEBUG)
            log("Source line closed");
    }

    public boolean isOpen() {
        return open;
    }

    public synchronized boolean startPlayback() {

        String psSt = ps.getStatus();
        if (psSt == PlayerStatus.PREPARED) {
            ps.setStatus(PlayerStatus.PLAYING);

            if (playAisPos < runStopPosition) {
                line.start();
                startNewPlaybackThread();
                return true;
            } else {
                ps.setStatus(PlayerStatus.IDLE);
                return false;
            }

        } else if (psSt == PlayerStatus.PAUSE) {
            //if (playAisPos < runStopPosition) {
            if (framePosition < runStopPosition) {
                ps.setStatus(PlayerStatus.PLAYING);
                line.start();
                return true;
            } else {
                ps.setStatus(PlayerStatus.IDLE);
                return false;

            }

        }

        //		if (segmentFrameLength > 0) {
        //
        //			sdlStatus.waitFor(LineStatus.STARTED);
        //			return true;
        //		}
        else {
            return false;
        }

    }

    public synchronized boolean pausePlayback() {
        if (DEBUG)
            log("pausePlayback() PlayerStatus " + ps);
        if (ps.getStatus() == PlayerStatus.PLAYING) {
            ps.setStatus(PlayerStatus.PAUSE);
            line.stop();

            if (DEBUG)
                log("Wait for line status open.");
            //lineStatus.waitFor(LineStatus.OPENED);

            return true;
        } else if (ps.getStatus() == PlayerStatus.PAUSE) {
            startPlayback();
            return true;
        } else if (ps.getStatus() == PlayerStatus.PREPARED) {
            ps.setStatus(PlayerStatus.PAUSE);
            startNewPlaybackThread();
            return true;

        } else
            return false;

    }

    public void stopPlayback() {
        synchronized (ps) {
            if (ps.getStatus() == PlayerStatus.PLAYING) {

                ps.setStatus(PlayerStatus.STOP);
                line.stop();
                line.flush();
                //lineStatus.waitFor(LineStatus.OPENED);
                ps.waitFor(PlayerStatus.IDLE);
            } else if (ps.getStatus() == PlayerStatus.PAUSE) {
                ps.setStatus(PlayerStatus.STOP);
                line.flush();
                if (sdlThread != null && sdlThread.isAlive()) {
                    ps.waitFor(PlayerStatus.IDLE);

                }
                ps.setStatus(PlayerStatus.IDLE);
            } else if (ps.getStatus() == PlayerStatus.DRAINING
                    || ps.getStatus() == PlayerStatus.IDLE) {
                // Do nothing playback already finished (or draining)
                ps.waitFor(PlayerStatus.IDLE);
            } else {
                ps.setStatus(PlayerStatus.STOP);
            }
        }

    }

    public long getFrameLength() {
        if (playAis == null)
            return -1;
        return playAis.getFrameLength();
    }

    public long getFramePosition() {

        if (posIsSynced) {
            return playAisPos;
        } else {
            return line.getLongFramePosition() - framePosOffset;
        }

    }

    public long getMicrosecondPosition() {
        return line.getMicrosecondPosition();
    }

    public synchronized void setFramePosition(long framePos) throws IOException {

        String oldStatus = ps.getStatus();

        if (oldStatus == PlayerStatus.PAUSE) {
            ps.setStatus(PlayerStatus.PAUSE_FLUSHING);
            ps.waitFor(PlayerStatus.FLUSHED);
        }
        if (oldStatus == PlayerStatus.PAUSE
                || oldStatus == PlayerStatus.PREPARED) {
            try {
                prepareToPlay(framePos);
            } catch (Exception e) {
                // should never happen
                e.printStackTrace();
            }
        }
        if (oldStatus == PlayerStatus.PAUSE) {
            ps.setStatus(PlayerStatus.PAUSE);
            if (playAisPos <= runStopPosition) {

                startNewPlaybackThread();
            }

        }

    }

    public long setStartFramePosition(long startPosition) {
        if (lengthInFrames < startPosition) {
            this.startPosition = lengthInFrames;
        } else if (startPosition < 0) {
            this.startPosition = 0;
        } else {
            this.startPosition = startPosition;
        }
        if (DEBUG)
            log("start position: " + startPosition);

        return this.startPosition;
    }

    public long setStopFramePosition(long stopPosition) {
        if (stopPosition != END_OF_AUDIO_FILE) {
            if (lengthInFrames < stopPosition) {
                this.stopPosition = lengthInFrames;
            } else if (stopPosition < 0) {
                this.stopPosition = 0;
            } else {
                this.stopPosition = stopPosition;
            }
        } else {
            this.stopPosition = stopPosition;
        }
        if (DEBUG)
            log("stop position: " + stopPosition);

        return this.stopPosition;
    }

    private void startSourceDataLine() {

        ps.setStatus(PlayerStatus.PLAYING);
        line.start();
        startNewPlaybackThread();

        if (DEBUG)
            log("Sdl status: " + lineStatus);
    }

    private void startNewPlaybackThread() {
        try {
            if (sdlThread != null)
                sdlThread.join();
        } catch (InterruptedException e) {
            // No problem
        }
        sdlThread = new Thread(this);
        sdlThread.setName("Audio-Playback");

        //sdlThread.setPriority(Thread.MIN_PRIORITY);

        sdlThread.start();
    }

    /* LineListener implementation */

    public void update(LineEvent le) {
        LineEvent.Type type = le.getType();
        Line src = le.getLine();
        if (DEBUG)
            log("LineEvent: " + le.toString());

        if (src == (Line) line) {
            if (type.equals(LineEvent.Type.OPEN)) {
                lineStatus.setStatus(LineStatus.OPENED);
            } else if (type.equals(LineEvent.Type.CLOSE)) {
                lineStatus.setStatus(LineStatus.CLOSED);
            } else if (type.equals(LineEvent.Type.START)) {
                lineStatus.setStatus(LineStatus.STARTED);
                pbl.update(this, new Playback.PlayerStatus(
                        Playback.PlayerStatus.PLAYING));
            } else if (type.equals(LineEvent.Type.STOP)) {
                lineStatus.setStatus(LineStatus.OPENED);
            }
        }
    }

    public void run() {

        read = 0;
        long toRead;
        posIsSynced = false;
        int currSdlBuf = -1;

        while (read >= 0
                && (ps.getStatus() == PlayerStatus.PLAYING || ps.getStatus() == PlayerStatus.PAUSE)) {
            currSdlBuf++;
            if (currSdlBuf == numBuffers) {
                currSdlBuf = 0;
            }

            if (runStopPosition >= 0) {
                toRead = (runStopPosition - playAisPos) * frameSize;
            } else {
                toRead = bufferSize;
            }
            if (toRead <= 0) {
                // stop position reached
                read = -1;
            } else {
                if (toRead > bufferSize) {
                    toRead = bufferSize;
                }
                try {
                    read = 0;
                    do {
                        read += playAis.read(buffers[currSdlBuf], read,
                                (int) toRead - read);
                    } while (read >= 0 && read < toRead);
                    if (DEBUG)
                        log("Read " + read);
                    playAisPos += read / frameSize;
                } catch (Exception e) {
                    // TODO handle exception
                    System.err.println("Cannot read " + e);
                    return;
                }
            }
            if (read > 0) {
                toWrite = read;

                while (toWrite > 0
                        && (ps.getStatus() == PlayerStatus.PLAYING || ps
                                .getStatus() == PlayerStatus.PAUSE)) {

                    if (DEBUG)
                        log("Look for pause status.");

                    ps.waitForNot(PlayerStatus.PAUSE);

                    if (DEBUG) {
                        log("Try to write: " + toWrite);
                        log("RBuffer avail: " + line.available());
                    }
                    wrote = line.write(buffers[currSdlBuf], read - toWrite,
                            toWrite);

                    //					if (ps.getStatus() == PlayerStatus.PAUSE)
                    //						line.flush();
                    if (DEBUG)
                        log("Wrote: " + wrote);

                    toWrite -= wrote;
                    if (DEBUG && (toWrite > 0))
                        log("toWrite: " + toWrite);

                }
				peakDetector.process(buffers[currSdlBuf], 0, read);
                    // calculate actual play position for level meter
                    // it differs from the position of written data

				float[] levels = peakDetector.getPeakLevels();
				float[] levelsCopy = new float[levels.length];
				for (int i = 0; i < levels.length; i++) {
					levelsCopy[i] = levels[i];
				}
				if (DEBUG)
					System.out.println("level " + levels[0] + " at: "
							+ statisticPos);
				BufferLevel bufLevel = new BufferLevel();
				bufLevel.framePos = statisticPos;
				bufLevel.length = (read / frameSize);
				bufLevel.peakLevels = levelsCopy;
				peaks.add(bufLevel);
                    statisticPos = playAisPos - read / frameSize;
                //Thread.yield();
            } else {
                if (DEBUG)
                    System.out.println("zero read.");
            }

        }
        synchronized (ps) {
            if (ps.getStatus() == PlayerStatus.PAUSE_FLUSHING) {

                line.flush();
                ps.setStatus(PlayerStatus.FLUSHED);
                return;
            }
            if (ps.getStatus() == PlayerStatus.PLAYING) {
                // for short files we have to wait for started line
                // hangs if no data is written to the line !!
                lineStatus.waitFor(LineStatus.STARTED, 1000);
                if (lineStatus.getStatus() == LineStatus.STARTED) {
                    if (DEBUG)
                        log("Draining...");
                    lineStatus.setStatus(LineStatus.DRAINING);
                    ps.setStatus(PlayerStatus.DRAINING);
                    line.drain();
                    line.stop();

                }
            }

            //sdlStatus.waitForNot(LineStatus.STARTED);
            line.flush();

            posIsSynced = true;
            try {
                if (playAis != null)
                    playAis.close();

            } catch (IOException e) {
                // TODO handle exception
                System.err.println("Cannot close audio input stream " + e);
            }

            ps.setStatus(PlayerStatus.IDLE);

        }
        if (ps.getStatus() == PlayerStatus.IDLE) {
			//pbl.played(this);
			pbl.update(this, ps);
            if (DEBUG)
                log("called played");
        }
		//peakDetector.stop();
        resetLevels();

    }

    public AudioFormat getAudioFormat() {
        return format;
    }

    private void log(String msg) {
        System.out.println(this.getClass().getName() + ": " + msg);
    }

    /**
     * 
     * @return frame position to start from
     */
    public long getStartFramePosition() {

        return startPosition;
    }

    /**
     * @return frame position to stop at
     */
    public long getStopFramePosition() {
        return stopPosition;
    }

}

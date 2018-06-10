//    IPS Speech database tools
// 	  (c) Copyright 2016
// 	  Institute of Phonetics and Speech Processing,
//    Ludwig-Maximilians-University, Munich, Germany
//
//
//    This file is part of IPS Speech database tools
//
//
//    IPS Speech database tools is free software: you can redistribute it and/or modify
//    it under the terms of the GNU Lesser General Public License as published by
//    the Free Software Foundation, version 3 of the License.
//
//    IPS Speech database tools is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU Lesser General Public License for more details.
//
//    You should have received a copy of the GNU Lesser General Public License
//    along with IPS Speech database tools.  If not, see <http://www.gnu.org/licenses/>.

/*
 * Date  : Feb 15, 2011
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */

package ips.annot.partitur;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import javax.sound.sampled.AudioFormat;

import org.apache.http.ParseException;

import ips.annot.model.PredefinedLevelDefinition;
import ips.annot.model.db.Bundle;
import ips.annot.model.db.Item;
import ips.annot.model.db.Label;
import ips.annot.model.db.Level;
import ips.annot.model.db.LevelDefinition;
import ips.annot.model.db.Link;
import ips.annot.model.db.LinkDefinition;
import ipsk.text.Version;

/**
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */

public class PartiturParser {

	public static Version PARTITUR_VERSION = new Version(new int[] { 1, 3 });
	public static long VOID_SYM_LINK = -1;

	public static class PartiturItem extends Item {
		private Long bpfSymLink = null;

		public Long getBpfSymLink() {
			return bpfSymLink;
		}

		public void setBpfSymLink(Long bpfSymLink) {
			this.bpfSymLink = bpfSymLink;
		}

		public PartiturItem() {
			super();

		}

		public PartiturItem(String lvl, String label, Long symLink) {
			this();
			getLabels().put(lvl, label);
			this.bpfSymLink = symLink;
		}

		public PartiturItem(String lvl, String label) {
			this(lvl, label, null);

		}
	}

	private AudioFormat audioFormat = null;
	private String recordingLocation = null;
	private String speakerID = null;

	private Set<LinkDefinition> linkDefinitions;

	public Set<LinkDefinition> getLinkDefinitions() {
		return linkDefinitions;
	}

	public void setLinkDefinitions(Set<LinkDefinition> linkDefinitions) {
		this.linkDefinitions = linkDefinitions;
	}

	private Pattern whiteSpacePattern = Pattern.compile("\\s+");

	public PartiturParser() {
		super();
	}

	public PartiturParser(AudioFormat audioFormat) {
		this(audioFormat, "", "");
	}

	public PartiturParser(AudioFormat audioFormat, String recordingLocation, String speakerID) {
		this();
		this.audioFormat = audioFormat;
		this.recordingLocation = recordingLocation;
		this.speakerID = speakerID;
	}

	// // KAN,ORT
	// private PartiturItem parseLinkedItem(String string){
	// String[] splt=string.split("\\s+", 2);
	// String lnkStr=splt[0];
	// String lbl=splt[1].trim();
	// int lnkId=Integer.parseInt(lnkStr);
	// PartiturItem li=new PartiturItem( lbl,lnkId);
	// return li;
	// }
	//// MAU
	// private LinkedSegmentItem parseLinkedSegmentItem(String string){
	// String[] splt=string.split("\\s+", 4);
	// long start=Long.parseLong(splt[0]);
	// long dur=Long.parseLong(splt[1]);
	// String lnkStr=splt[2];
	// String lbl=splt[3].trim();
	// int lnkId=Integer.parseInt(lnkStr);
	// LinkedSegmentItem li=new LinkedSegmentItem( lbl,lnkId,start,dur);
	// return li;
	// }

	private Level levelByNameCreateIfNotExists(Bundle bundle, String name, String type) {
		List<Level> lvls = bundle.getLevels();
		Level lvl = null;
		for (Level l : lvls) {
			if (name.equals(l.getName())) {
				lvl = l;
				break;
			}
		}
		if (lvl == null) {
			lvl = new Level();
			lvl.setName(name);
			lvl.setType(type);
			lvls.add(lvl);

		}
		return lvl;
	}

	public Bundle parse(Bundle bundle, Reader kanCont) throws IOException {

		LineNumberReader lnr = new LineNumberReader(kanCont);
		String line = null;
		if (bundle == null) {
			bundle = new Bundle();
		}
		Integer hstId = bundle.highestID();
		int idOffset = 0;
		if (hstId != null) {
			idOffset = hstId;
		}
		int idCnt = idOffset;
		List<PartiturItem> parIts = new ArrayList<PartiturItem>();
		while ((line = lnr.readLine()) != null) {
			String trimmedLine = line.trim();
			if ("".equals(trimmedLine)) {
				continue;
			}
			String[] splt = trimmedLine.split(":", 2);
			String lvlNm = splt[0];
			PartiturItem pi = null;
			if ("KAN".equals(lvlNm) || "ORT".equals(lvlNm)) {
				String liStr = splt[1].trim();
				String[] liStrSplt = liStr.split("\\s+", 2);
				String lnkStr = liStrSplt[0];
				String lbl = liStrSplt[1].trim();
				long lnkId = Long.parseLong(lnkStr);
				if (lnkId == VOID_SYM_LINK) {
					pi = new PartiturItem(lvlNm, lbl);
				} else {
					pi = new PartiturItem(lvlNm, lbl, lnkId);
				}
				Level lvl = levelByNameCreateIfNotExists(bundle, lvlNm, LevelDefinition.ITEM);
				pi.setLevel(lvl);
				lvl.getItems().add(pi);
			} else if ("MAU".equals(lvlNm)) {
				String segStr = splt[1].trim();
				String[] segSplt = segStr.split("\\s+", 4);
				long start = Long.parseLong(segSplt[0]);
				long dur = Long.parseLong(segSplt[1]);
				String lnkStr = segSplt[2];
				String lbl = segSplt[3].trim();
				long lnkId = Long.parseLong(lnkStr);
				if (lnkId == VOID_SYM_LINK) {
					pi = new PartiturItem(lvlNm, lbl);
				} else {
					pi = new PartiturItem(lvlNm, lbl, lnkId);
				}
				pi.setSampleStart(start);
				pi.setSampleDur(dur);
				Level lvl = levelByNameCreateIfNotExists(bundle, lvlNm, LevelDefinition.SEGMENT);
				pi.setLevel(lvl);
				lvl.getItems().add(pi);
			}
			if (pi != null) {
				pi.setBundleId(++idCnt);

				parIts.add(pi);
			}
		}

		// create model items and connect links
		Set<Link> links = new HashSet<Link>();
		if (linkDefinitions != null) {
			// connect from->to
			for (LinkDefinition ld : linkDefinitions) {
				LevelDefinition fromLvl = ld.getSuperTier();
				LevelDefinition toLvl = ld.getSubTier();
				for (PartiturItem parIt : parIts) {
					Label pLbl = parIt.getLabelsList().get(0);
					String lvlNm = pLbl.getName();
					if (lvlNm.equals(fromLvl.getName())) {
						Long parSl = parIt.getBpfSymLink();
						if(parSl!=null){
							String parSlstr = parSl.toString();
							// find to items
							for (PartiturItem parItTo : parIts) {
								Label pLblTo = parItTo.getLabelsList().get(0);
								String lvlNmTo = pLblTo.getName();
								if (lvlNmTo.equals(toLvl.getName())) {
									Long parSlTo = parItTo.getBpfSymLink();
									if (parSlTo != null) {
										String parSlTostr = parSlTo.toString();
										if(parSlstr.equals(parSlTostr)){
											Link nl = new Link();
											nl.setLabel(parSlstr);
											nl.setFrom(parIt);
											nl.setTo(parItTo);
											links.add(nl);
										}
									}
								}
							}
						}
					}
				}
			}
		}

		
		bundle.getLinksAsSet().addAll(links);
		return bundle;
	}

	private String buildHeader() {
		StringBuffer sb = new StringBuffer();
		sb.append("LHD: Partitur " + PARTITUR_VERSION.toString());
		if (recordingLocation != null) {
			sb.append("REP: " + recordingLocation);
		}
		if (audioFormat != null) {
			sb.append("SNB: " + audioFormat.getFrameSize() / audioFormat.getChannels());
			sb.append("SAM: " + audioFormat.getSampleRate());
			String sbf;
			if (audioFormat.isBigEndian()) {
				sbf = "01";
			} else {
				sbf = "10";
			}
			sb.append("SBF: " + sbf);
			sb.append("SSB: " + audioFormat.getSampleSizeInBits());
			sb.append("NCH: " + audioFormat.getChannels());
		}
		if (speakerID != null) {
			sb.append("SPN: " + speakerID);
		}
		sb.append("LBD:");
		return sb.toString();
	}

	public String write(Bundle bundle) {
		StringBuffer parContBuf = new StringBuffer();
		parContBuf.append(buildHeader());
		List<Level> lvls = bundle.getLevels();
		// List<String[]> itsList=new ArrayList<String[]>();

		String lvlCont = writeLevels(lvls, bundle.getLinksAsSet());
		parContBuf.append(lvlCont);

		return parContBuf.toString();
	}

	public String writeLevels(List<Level> levelList, Set<Link> links) {
		StringBuffer parContBuf = new StringBuffer();
		int symLinkCnt = 0;
		Set<Link> linksCopy = new HashSet<Link>();
		for (Link l : links) {
			Link lc = new Link();
			lc.setFrom(l.getFrom());
			lc.setTo(l.getTo());
			linksCopy.add(lc);
		}
		for (Level level : levelList) {
			String lvlType = level.getType();
			List<ips.annot.model.db.Item> its = level.getItems();
			for (ips.annot.model.db.Item it : its) {
				String llbl = null;
				Integer itId = it.getBundleId();
				for (Link link : linksCopy) {

					int fromId = link.getFromID();
					int toId = link.getToID();
					if (fromId == itId || toId == itId) {
						llbl = link.getLabel();
						if (llbl == null) {
							llbl = Integer.toString(symLinkCnt++);
							link.setLabel(llbl);
						}

					}
				}
				List<Label> lblsList = it.getLabelsList();
				for (Label lbl : lblsList) {
					String key = lbl.getName();
					String val = lbl.getValueString();
					parContBuf.append(key);
					parContBuf.append(": ");
					if (LevelDefinition.ITEM.equals(lvlType)) {
						if (llbl != null) {
							parContBuf.append(llbl);
							parContBuf.append(" ");
						}
						parContBuf.append(val);
						parContBuf.append("\n");
					} else {
						throw new ParseException("Writing of level type " + lvlType + " currenty not supported!");
					}

				}
			}
		}

		return parContBuf.toString();
	}

	public static void main(String[] args) {
		StringBuffer test = new StringBuffer("KAN: 0 ?aInts@ltsIm6\nKAN: 1 tsvaIhUnd6t?axtUntfi:rtsIC\nKAN: 2 mark\n");
		test.append("ORT: 0 Einzelzimmer\nORT: 1 zweihundertachtundvierzig\nORT: 2 Mark\n");
		test.append(
				"MAU:    0   639 -1  <p:>\nMAU:    640 479 0   ?\nMAU:    1120    799 0   aI\nMAU:    1920    639 0   n\n");
		StringReader sr = new StringReader(test.toString());
		
		PartiturParser pp=new PartiturParser();
        Set<LinkDefinition> linkDefs=new HashSet<LinkDefinition>();
        LinkDefinition kanMauLd=new LinkDefinition();
        kanMauLd.setSuperTier(PredefinedLevelDefinition.KAN.getLevelDefinition());
        kanMauLd.setSubTier(PredefinedLevelDefinition.MAU.getLevelDefinition());
        kanMauLd.setType(LinkDefinition.ONE_TO_MANY);
        linkDefs.add(kanMauLd);
        pp.setLinkDefinitions(linkDefs);
		try {
			Bundle bundle=pp.parse(null, sr);
			System.out.println(bundle);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}

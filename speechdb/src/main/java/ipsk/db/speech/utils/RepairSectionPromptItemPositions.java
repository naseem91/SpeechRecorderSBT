/*
 * Date  : 29.05.2016
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */
 
package ipsk.db.speech.utils;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;

import ipsk.db.speech.PromptItem;
import ipsk.db.speech.Script;
import ipsk.db.speech.Section;

/**
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */

/**
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */
public class RepairSectionPromptItemPositions {
	private static final String PERSISTENCE_UNIT_NAME = "SpeechDBPU";
	/**
	 * 
	 */
	public RepairSectionPromptItemPositions() {
		super();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	
	EntityManagerFactory factory=Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
		
		// Persistence.generateSchema(PERSISTENCE_UNIT_NAME, null);
//		 factory.
		 EntityManager em = factory.createEntityManager();
		 CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Script> cq = cb.createQuery(Script.class);
			Root<Script> rt = cq.from(Script.class);
			cq.select(rt);
			
			TypedQuery<Script> q = em.createQuery(cq);
			
			List<Script> allScripts = q.getResultList();
			System.out.println("Found: " +allScripts.size() +" scripts.");
			EntityTransaction tr=em.getTransaction();
			tr.begin();
			for(Script scr:allScripts){
				List<Section> sections=scr.getSections();
				for(Section section:sections){
					List<PromptItem> pis=section.getPromptItems();
//					List<PromptItem> copyPis=new ArrayList<PromptItem>();
//					copyPis.addAll(pis);
//					pis.clear();
//					section.setPromptItems(pis);
//					em.merge(section);
					
//					// isolate prompt items
//					for(PromptItem pi:copyPis){
//						em.detach(pi);
//						pi.setSection(null);
//						em.merge(pi);
//					}
//					
					// 
//					section.setPromptItems(copyPis);
//					em.merge(section);
					for(PromptItem pi:pis){
						em.detach(pi);
						em.merge(pi);
					}
					em.merge(section);
				}
			}
			tr.commit();
		 em.close();
		 
		 factory.close();
		 
	}

}

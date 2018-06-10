//    IPS Java Utils
// 	  (c) Copyright 2011
// 	  Institute of Phonetics and Speech Processing,
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

package ipsk.swing.action.tree;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;

/**
 * @author klausj
 *
 */
public class JMenuBuilder {

//    public class ActionButtonGroup extends ButtonGroup{
//        private Set<RadioActionLeaf> actionGroup;
//
//        
//        public ActionButtonGroup(Set<RadioActionLeaf> actionGroup){
//            super();
//            this.actionGroup=actionGroup;
//        }
//        public Set<RadioActionLeaf> getActionGroup() {
//            return actionGroup;
//        }
//
//        public void setActionGroup(Set<RadioActionLeaf> actionGroup) {
//            this.actionGroup = actionGroup;
//        }
//        
//    }
    
//    private HashSet<ActionButtonGroup> actionButtonGroups=new HashSet<ActionButtonGroup>();
    private ActionFolder rootNode;
    public JMenuBuilder(ActionFolder rootNode){
        super();
        this.rootNode=rootNode;
        
    }
   
    public JMenuItem buildMenu(ActionLeaf cn2){
        ActionLeaf al=(ActionLeaf)cn2;
        
        if(al instanceof CheckActionLeaf){
            JCheckBoxMenuItem checkMi=new JCheckBoxMenuItem(al);
            
            return checkMi;
        }else if(al instanceof RadioActionLeaf){
//            ActionButtonGroup actionButtonGroup=null;
            RadioActionLeaf ral=(RadioActionLeaf)al;
            JRadioButtonMenuItem mi=new JRadioButtonMenuItem(ral);
//            Set<RadioActionLeaf> ralGr=ral.getGroup();
//            if(ralGr!=null){
//                boolean exists=false;
//              for(ActionButtonGroup abg:actionButtonGroups){
//                  if(abg.getActionGroup().equals(ralGr)){
//                      actionButtonGroup=abg;
//                      exists=true;
//                      break;
//                  }
//              }
//              if(!exists){
//                  actionButtonGroup=new ActionButtonGroup(ralGr);
//                  actionButtonGroups.add(actionButtonGroup);
//              }
//              actionButtonGroup.add(mi);
//            }
//            
            
            return mi;
        }else{
            JMenuItem mi=new JMenuItem(al);
            return mi;
        }
    }
   
    public JMenuItem buildMenu(ActionNode cn2){
        
            if(cn2 instanceof ActionFolder){
                ActionFolder af=(ActionFolder)cn2;
               
               
                JMenu menu=new JMenu(af.getDisplayName().localize());
                List<ActionNode> chs=af.getChildren();
//                for(ActionNode cn:chs){
//                    
//                    JMenuItem chMi=buildMenu(cn);
//                    if(chMi!=null){
//                        menu.add(chMi);
//                    }
//                }
                addChildren(menu, chs);
                
                return menu;
                
           
            }else if(cn2 instanceof ActionLeaf){
                ActionLeaf al=(ActionLeaf)cn2;
                
                if(al instanceof CheckActionLeaf){
                    JCheckBoxMenuItem checkMi=new JCheckBoxMenuItem(al);
                    
                    return checkMi;
                }else if(al instanceof RadioActionLeaf){
//                    ActionButtonGroup actionButtonGroup=null;
                    RadioActionLeaf ral=(RadioActionLeaf)al;
                    JRadioButtonMenuItem mi=new JRadioButtonMenuItem(ral);
//                    Set<RadioActionLeaf> ralGr=ral.getGroup();
//                    if(ralGr!=null){
//                        boolean exists=false;
//                      for(ActionButtonGroup abg:actionButtonGroups){
//                          if(abg.getActionGroup().equals(ralGr)){
//                              actionButtonGroup=abg;
//                              exists=true;
//                              break;
//                          }
//                      }
//                      if(!exists){
//                          actionButtonGroup=new ActionButtonGroup(ralGr);
//                          actionButtonGroups.add(actionButtonGroup);
//                      }
//                      actionButtonGroup.add(mi);
//                    }
//                    
                    
                    return mi;
                }else{
                    JMenuItem mi=new JMenuItem(al);
                    return mi;
                }
            }
        return null;
    }
    
    
    private void addChildren(JMenu m,List<ActionNode> ans){
        int numAns=ans.size();
        
        // never show separator at the beginning
        boolean separated=true;
        for(int i=0;i<numAns;i++){
            ActionNode an=ans.get(i);

            if(an instanceof ActionFolder){
                ActionFolder af=(ActionFolder)an;
                JMenu menu=new JMenu(af.getDisplayName().localize());
                List<ActionNode> chAns=af.getChildren();
                addChildren(menu, chAns);
                m.add(menu);
                separated=false;
            }else if(an instanceof ActionGroup){
                ActionGroup ag=(ActionGroup)an;
                if(!separated){
                    m.addSeparator();
                    separated=true;
                }
                List<ActionNode> chAns=ag.getChildren();
                
                addChildren(m, chAns);
             
                // adds separator if not last item
                if(i<numAns-1){
                    m.addSeparator();
                    separated=true;
                }
            }else if(an instanceof ActionLeaf){
                m.add(buildMenu((ActionLeaf)an));
                separated=false;
            }
        }

    }
    
    
    public JMenu buildMenu(String topLevelKey){
       
        List<ActionNode> ch=rootNode.getChildren();
        for(ActionNode n:ch){
            if(n instanceof ActionFolder){
                ActionFolder af=(ActionFolder)n;
                
                if(topLevelKey.equals(af.getKey())){
                    JMenu jMenu=new JMenu(af.getDisplayName().localize());
                    List<ActionNode> tlch=af.getChildren();
                    addChildren(jMenu, tlch);
                    return jMenu;
                }
                
            }
        }
        return null;
    }
    
 
    
    public List<JMenuItem> buildMenus(){
        ArrayList<JMenuItem> mis=new ArrayList<JMenuItem>();
        List<ActionNode> ch=rootNode.getChildren();
        for(ActionNode n:ch){
            JMenuItem mi=buildMenu(n);
            if(mi!=null){
                mis.add(mi);
            }
        }
      
        return mis;
    }
    public void addToJPopupMenu(JPopupMenu popupMenu){
       List<JMenuItem> mis=buildMenus();
       for(JMenuItem mi:mis){
           popupMenu.add(mi);
       }
    }
    public JPopupMenu buildJPopupMenu(String label){
        JPopupMenu pm=new JPopupMenu(label);
        addToJPopupMenu(pm);
        return pm;
    }
    public JPopupMenu buildJPopupMenu(){
 
        JPopupMenu pm=new JPopupMenu();
        addToJPopupMenu(pm);
        return pm;
    }
   
}

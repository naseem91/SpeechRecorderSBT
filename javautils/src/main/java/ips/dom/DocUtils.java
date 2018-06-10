//    IPS Java Utils
// 	  (c) Copyright 2014
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

package ips.dom;

import java.util.ArrayList;

import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author klausj
 *
 */
public class DocUtils {

    private Document document;
    public DocUtils(Document document){
        this.document=document;
    }
    
    public static List<Element> getElementsByTagName(NodeList nodelist,String name){
        List<Element> elList=new ArrayList<Element>();
        int nlLen=nodelist.getLength();
        for(int i=0;i<nlLen;i++){
            Node n=nodelist.item(i);
            if(Node.ELEMENT_NODE==n.getNodeType() && name.equals(n.getNodeName())){
                elList.add((Element)n);
            }
        }
        return elList; 
    }
    
    public static List<Element> getElementsByTagName(Element element,String name){
        NodeList nls=element.getElementsByTagName(name);
        return getElementsByTagName(nls, name);
    }
    
    public static Element getFirstElementByTagName(Element element,String name){
        NodeList nls=element.getElementsByTagName(name);
        List<Element> elems=getElementsByTagName(nls, name);
        if(elems!=null && elems.size()>0){
            return elems.get(0);
        }else{
            return null;
        }
    }
    
    public static Element getFirstElementByTagNameWith(Element element,String name,String condTagName,String condTextValue){
        List<Element> elems=getElementsByTagNameWith(element, name, condTagName, condTextValue);
        if(elems!=null && elems.size()>0){
            return elems.get(0);
        }else{
            return null;
        }
    }
    
    public static List<Element> getElementsByTagNameWith(Element element,String name,String condTagName,String condTextValue){
        NodeList nls=element.getElementsByTagName(name);
        List<Element> elems=getElementsByTagName(nls, name);
        List<Element> matchElems=new ArrayList<Element>();
        for(Element el:elems){
            List<Element> condElems=getElementsByTagName(el, condTagName);
            for(Element condElem:condElems){
                if(condTextValue.equals(condElem.getTextContent())){
                    matchElems.add(el);
                }
            }
        }
        return matchElems;
        
    }
    
    public List<Element> getElementsByTagName(String[] hierarchyElementNames,String name){
        Element current=document.getDocumentElement();
       
//        List<Element> elList=new ArrayList<Element>();
        if(current.getNodeName().equals(hierarchyElementNames[0])){
            for(int i=1;i<hierarchyElementNames.length;i++){
                String hElName=hierarchyElementNames[i];
//                current.getElementsByTagName(hElName);
                List<Element> chEls=DocUtils.getElementsByTagName(current, hElName);
                if(chEls.size()>0){
                    current=chEls.get(0);
                }else{
                  return null;
                }
            }
           return DocUtils.getElementsByTagName(current, name);
            
        }
        return null;
        
    }
    

}

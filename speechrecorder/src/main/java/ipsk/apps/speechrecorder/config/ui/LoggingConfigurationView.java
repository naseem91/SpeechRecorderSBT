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

/*
 * Date  : Jun 24, 2004
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */

package ipsk.apps.speechrecorder.config.ui;

import ipsk.apps.speechrecorder.SpeechRecorder;
import ipsk.apps.speechrecorder.config.Handler;
import ipsk.apps.speechrecorder.config.HandlerView;
import ipsk.apps.speechrecorder.config.Logger;
import ipsk.apps.speechrecorder.config.LoggingConfiguration;
import ipsk.swing.TitledPanel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

/**
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 * 
 */

/**
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 * 
 */
public class LoggingConfigurationView extends JPanel implements ActionListener {
    // private Level[] availLevels = { Level.OFF, Level.SEVERE, Level.WARNING,
    // Level.INFO,
    // Level.CONFIG, Level.FINE, Level.FINER, Level.FINEST, Level.ALL };

//    private LoggingConfiguration p;

	private static final long serialVersionUID = 1L;

	private GridBagConstraints c = new GridBagConstraints();

//    private JTable loggerTable;

//    private JButton removeButton;

    protected TitledPanel handlerPanel;

    protected TitledPanel loggerPanel;

    private JCheckBox[] enableCheckBoxes = new JCheckBox[0];

    private Logger[] displayedLogger;
    private Handler[] displayedHandlers;
    
    private LoggerView[] loggerViews;
    private HandlerView[] handlerViews;

    // private int lastLoggerGridPosition=0;

    // java.util.Locale[] availLocales;

    public LoggingConfigurationView() {
        super(new GridBagLayout());
//        this.p = p;

        // c.fill = GridBagConstraints.NONE;
        c.insets = new Insets(2, 5, 2, 5);
        // c.anchor = GridBagConstraints.PAGE_START;

        c.gridx = 0;
        c.gridy = 0;
        // c.fill=GridBagConstraints.VERTICAL;
        c.anchor = GridBagConstraints.NORTH;
        c.weighty = 1.0;
        // c.weighty=1.0;
        handlerPanel = new TitledPanel("Log-Handler");
        handlerPanel.setLayout(new GridBagLayout());
        add(handlerPanel, c);
        loggerPanel = new TitledPanel("Logger");
        loggerPanel.setLayout(new GridBagLayout());
        c.gridy++;
        add(loggerPanel, c);
        
//        revalidateLoggerPanel();
        displayedHandlers=SpeechRecorder.DEF_LOG_HANDLERS;
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        handlerViews=new HandlerView[displayedHandlers.length];
        for (int i = 0; i < displayedHandlers.length; i++) {
            HandlerView hv = new HandlerView(SpeechRecorder.DEF_LOG_HANDLERS[i]);
            handlerViews[i]=hv;
            handlerPanel.add(hv, c);
            c.gridy++;
        }
        
//        Logger[] logs = p.getLogger();
        displayedLogger=SpeechRecorder.AVAIL_LOGGERS;
        enableCheckBoxes = new JCheckBox[displayedLogger.length];
        loggerViews=new LoggerView[displayedLogger.length];
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        for (int i = 0; i < displayedLogger.length; i++) {
            c.gridx = 0;
            JCheckBox enableBox = new JCheckBox();
            enableBox.addActionListener(this);
            enableCheckBoxes[i] = enableBox;

            loggerPanel.add(enableCheckBoxes[i], c);
            LoggerView lv = new LoggerView();
            loggerViews[i]=lv;
            lv.setEnabled(false);
            lv.setLogger(displayedLogger[i]);
//            for (int j = 0; j < logs.length; j++) {
//                if (displayedLogger[i].getName().equals(logs[j].getName())) {
//                    lv.setEnabled(true);
//                    enableBox.setSelected(true);
//                    break;
//                }
//            }
//            
            c.gridx++;
            loggerPanel.add(lv, c);
            c.gridy++;
        }
    }

    /**
     * @param loggingConfiguration
     */
    public void setLoggingConfiguration(
            LoggingConfiguration loggingConfiguration) {
        Logger[] logs = loggingConfiguration.getLogger();
        for (int i = 0; i < displayedLogger.length; i++) {
            loggerViews[i].setEnabled(false);
            for (int j = 0; j < logs.length; j++) {
                if (displayedLogger[i].getName().equals(logs[j].getName())) {
                    loggerViews[i].setLogger(logs[i]);
                    loggerViews[i].setEnabled(true);
                    enableCheckBoxes[i].setSelected(true);
                    break;
                }
            }
        }
        setDependencies();
    }
//    private void revalidateLoggerPanel() {
//        handlerPanel.removeAll();
//        ArrayList<Handler> handlers = new ArrayList<Handler>();
//        Handler[] logHandlers = p.getHandler();
//        //handlers.addAll(Arrays.asList(SpeechRecorder.DEF_LOG_HANDLERS));
//        for (int i = 0; i < SpeechRecorder.DEF_LOG_HANDLERS.length; i++) {
//           Handler defHandler=SpeechRecorder.DEF_LOG_HANDLERS[i];
//            boolean addDefHandler = true;
//            for (int j = 0; j < logHandlers.length; j++) {
//                if (defHandler.getName().equals(logHandlers[j].getName())) {
//                    handlers.add(logHandlers[j]);
//                    addDefHandler = false;
//                    break;
//                }
//            }
//            if (addDefHandler)
//                handlers.add(defHandler);
//        };
//        displayedHandlers = (Handler[]) handlers.toArray(new Handler[0]);
//        GridBagConstraints c = new GridBagConstraints();
//        c.gridx = 0;
//        c.gridy = 0;
//        for (int i = 0; i < displayedHandlers.length; i++) {
//            HandlerView hv = new HandlerView(displayedHandlers[i]);
//            handlerPanel.add(hv, c);
//            c.gridy++;
//        }
//
//        loggerPanel.removeAll();
//        ArrayList<Logger> logger = new ArrayList<Logger>();
//        Logger[] logs = p.getLogger();
//       
//       
//        for (int i = 0; i < SpeechRecorder.AVAIL_LOGGERS.length; i++) {
//            Logger cfgLogger = SpeechRecorder.AVAIL_LOGGERS[i];
//            boolean addDefLog = true;
//            for (int j = 0; j < logs.length; j++) {
//                if (cfgLogger.getName().equals(logs[j].getName())) {
//                    logger.add(logs[j]);
//                    addDefLog = false;
//                    break;
//                }
//            }
//            if (addDefLog){
//                logger.add(cfgLogger);
//            }
//        }
//        displayedLogger =(Logger[]) logger.toArray(new Logger[0]);
//        enableCheckBoxes = new JCheckBox[displayedLogger.length];
//
//        c = new GridBagConstraints();
//        c.gridx = 0;
//        c.gridy = 0;
//        for (int i = 0; i < displayedLogger.length; i++) {
//            c.gridx = 0;
//            JCheckBox enableBox = new JCheckBox();
//            enableBox.addActionListener(this);
//            enableCheckBoxes[i] = enableBox;
//
//            loggerPanel.add(enableCheckBoxes[i], c);
//            LoggerView lv = new LoggerView(displayedLogger[i]);
//            lv.setEnabled(false);
//            for (int j = 0; j < logs.length; j++) {
//                if (displayedLogger[i].getName().equals(logs[j].getName())) {
//                    lv.setEnabled(true);
//                    enableBox.setSelected(true);
//                    break;
//                }
//            }
//            
//            
//            c.gridx++;
//            loggerPanel.add(lv, c);
//            c.gridy++;
//        }
//
//        // for (int i=0)
//        // c.gridy=0;
//        // removeAll();
//        // HashSet logNames=new HashSet();
//        // Logger[] lgs=p.getLogger();
//        // for(int i=0;i<lgs.length;i++){
//        // logNames.add(lgs[i].getAttributeName());
//        // }
//        // for(int i=0;i<LoggingConfiguration.AVAIL_LOGGER.length;i++){
//        // logNames.add(LoggingConfiguration.AVAIL_LOGGER[i].getAttributeName());
//        // }
//        // String[] unLogNames=(String[])logNames.toArray(new String[0]);
//        // for(int i=0;i<unLogNames.length;i++){
//        // for(int i=0;i<lgs.length;i++){
//        // if (lgs[i])
//        // }
//        //        
//        // for(int i=0;i<LoggingConfiguration.AVAIL_LOGGER.length;i++){
//        // Logger l=LoggingConfiguration.AVAIL_LOGGER[i];
//        //            
//        // c.gridx=0;
//        // JCheckBox cb=new JCheckBox();
//        // enableCheckboxes[i]=cb;
//        // cb.addActionListener(this);
//        // add(cb,c);
//        // LoggerView lv=null;
//        // if (i>=lgs.length){
//        // lv=new LoggerView(LoggingConfiguration.AVAIL_LOGGER[i]);
//        // lv.setEnabled(false);
//        // cb.setSelected(false);
//        // }else{
//        // lv=new LoggerView(p.getLogger()[i]);
//        // cb.setSelected(true);
//        // }
//        // //LoggerView lv=new LoggerView(p.getLogger()[i]);
//        // c.gridx++;
//        // add(lv,c);
//        // c.gridy++;
//        // }
//        revalidate();
//        repaint();
//    }
//    
    
    public void applyValues(LoggingConfiguration l){
        Vector<Logger> newLgs = new Vector<Logger>();
        ArrayList<Handler> newHandlers=new ArrayList<Handler>();
        for (int i = 0; i < displayedLogger.length; i++) {
            if (enableCheckBoxes[i].isSelected()) {
                loggerViews[i].applyValues(displayedLogger[i]);
                newLgs.add(displayedLogger[i]);
                String handlerName=displayedLogger[i].getHandlerName();
                for(int j=0;j<displayedHandlers.length;j++){
                    if(handlerName.equals(displayedHandlers[j].getName())){
                            handlerViews[j].applyValues(displayedHandlers[j]);
                            newHandlers.add(displayedHandlers[j]);
                    }
                }
               
                
            }else{
                
            }
        }
        l.setHandler((Handler[])newHandlers.toArray(new Handler[0]));
        l.setLogger((Logger[]) (newLgs.toArray(new Logger[0])));

    }

    private void setDependencies(){
        for(int i=0;i<enableCheckBoxes.length;i++){
            loggerViews[i].setEnabled(enableCheckBoxes[i].isSelected());
        }
    }
    
    /*
     * /* (non-Javadoc)
     * 
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent arg0) {
//        Object src = arg0.getSource();
//
//        Vector<Logger> newLgs = new Vector<Logger>();
//        ArrayList<Handler> newHandlers=new ArrayList<Handler>();
//        for (int i = 0; i < displayedLogger.length; i++) {
//            if (enableCheckBoxes[i].isSelected()) {
//                newLgs.add(displayedLogger[i]);
//                // make sure the Handler is available
//                //Handler[] hs=p.getHandler();
//                
//                //newHandlers.addAll(Arrays.asList(hs));
//                String handlerName=displayedLogger[i].getHandlerName();
//                for(int j=0;j<displayedHandlers.length;j++){
//                    if(handlerName.equals(displayedHandlers[j].getName())){
////                        boolean addDispHandler=true;
////                        for(int k=0;k<hs.length;k++){
////                            if (hs[k].getAttributeName().equals(displayedHandlers[j].getAttributeName())){
////                                addDispHandler=false;
////                                break;
////                            }
////                        }
////                        if(addDispHandler){
//                            newHandlers.add(displayedHandlers[j]);
//                        //}
//                    }
//                }
//               
//                
//            }else{
//                
//            }
//        }
//        p.setHandler((Handler[])newHandlers.toArray(new Handler[0]));
//        p.setLogger((Logger[]) (newLgs.toArray(new Logger[0])));
//
//        revalidateLoggerPanel();
        setDependencies();

    }

   

   

}

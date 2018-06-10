//    IPS Java Audio Tools
// 	  (c) Copyright 2011
// 	  Institute of Phonetics and Speech Processing,
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

package ipsk.audio;

import ipsk.audio.capture.PrimaryRecordTarget;

/**
 * @author klausj
 *
 */
public class AudioOptions {

        public static final PrimaryRecordTarget DEF_PRIMARY_RECORD_TARGET=PrimaryRecordTarget.TEMP_RAW_FILE;
        private PrimaryRecordTarget primaryRecordTargetDefault=DEF_PRIMARY_RECORD_TARGET;
        private PrimaryRecordTarget primaryRecordTarget;
        private Integer lineBufferSize;

       
        public PrimaryRecordTarget getPrimaryRecordTarget() {
            return primaryRecordTarget;
        }

        public void setPrimaryRecordTarget(PrimaryRecordTarget primaryRecordTarget) {
            this.primaryRecordTarget = primaryRecordTarget;
        }

        public  PrimaryRecordTarget getNNPrimaryRecordTarget() {
            if(primaryRecordTarget!=null){
                return primaryRecordTarget;
            }
            if(primaryRecordTargetDefault!=null){
                return primaryRecordTargetDefault;
            }
            return DEF_PRIMARY_RECORD_TARGET;
        }
        public PrimaryRecordTarget getPrimaryRecordTargetDefault() {
            return primaryRecordTargetDefault;
        }

        public void setPrimaryRecordTargetDefault(
                PrimaryRecordTarget primaryRecordTargetDefault) {
            this.primaryRecordTargetDefault = primaryRecordTargetDefault;
        }

        public Integer getLineBufferSize() {
            return lineBufferSize;
        }

        public void setLineBufferSize(Integer lineBufferSize) {
            this.lineBufferSize = lineBufferSize;
        }
        
    
}

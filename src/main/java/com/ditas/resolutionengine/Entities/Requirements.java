/*
 * This file is part of VDC-Resolution-Engine.
 * 
 * VDC-Resolution-Engine is free software: you can redistribute it 
 * and/or modify it under the terms of the GNU General Public License as 
 * published by the Free Software Foundation, either version 3 of the License, 
 * or (at your option) any later version.
 * 
 * VDC-Resolution-Engine is distributed in the hope that it will be 
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with VDC-Resolution-Engine.  
 * If not, see <https://www.gnu.org/licenses/>.
 * 
 * VDC-Resolution-Engine is being developed for the
 * DITAS Project: https://www.ditas-project.eu/
 */
package com.ditas.resolutionengine.Entities;

import org.json.JSONArray;

public class Requirements {

    private String methodTags;
    private String vdcTags;
	
    public Requirements() {
		
    }

    
    public String getMethodTags() {
		
        return methodTags;
        
    }

    public void setMethodTags(JSONArray methodTags) {
		
        this.methodTags = "";
        
        for (int i = 0 ; i < methodTags.length() ; i++) {
                        
            String str = "";
            if (methodTags.getString(i).length() > 1) {
                            
                str = methodTags.getString(i).substring(0, 1).toUpperCase() + methodTags.getString(i).substring(1);
                        
            }
            else {
                            
                str = methodTags.getString(i).substring(0, 1).toUpperCase();
                    
            }
                        
            this.methodTags += str+" ";
		
        }	
    }


    public String getVdcTags() {
		
        return vdcTags;
	
    }

    public void setVdcTags(JSONArray vdcTags) {
		
        this.vdcTags = "";
		
        for (int i = 0 ; i < vdcTags.length() ; i++) {
			            
            String str = "";
            if (vdcTags.getString(i).length() > 1) {
                            
                str = vdcTags.getString(i).substring(0, 1).toUpperCase() + vdcTags.getString(i).substring(1);
                        
            }
            else {
                            
                str = vdcTags.getString(i).substring(0, 1).toUpperCase();
                        
            }
			           
            this.vdcTags += str+" ";
                    	
        }
    }
}
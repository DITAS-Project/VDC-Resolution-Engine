package com.ditas.resolutionengine.Entities;




import org.json.JSONArray;

public class Requirements {

	private String methodTags;
	private String vdcTags;
	
	
	public Requirements() {
		// TODO Auto-generated constructor stub
	}


	public String getMethodTags() {
		return methodTags;
	}


	public void setMethodTags(JSONArray methodTags) {
		this.methodTags = "";
		for (int i = 0 ; i < methodTags.length() ; i++) {
			this.methodTags += methodTags.getString(i)+" ";
		}
		
	}


	public String getVdcTags() {
		return vdcTags;
	}


	public void setVdcTags(JSONArray vdcTags) {
		this.vdcTags = "";
		for (int i = 0 ; i < vdcTags.length() ; i++) {
			this.vdcTags += vdcTags.getString(i)+" ";
		}
	}
}

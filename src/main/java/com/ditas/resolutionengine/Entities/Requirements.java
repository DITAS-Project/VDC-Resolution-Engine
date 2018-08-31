package com.ditas.resolutionengine.Entities;

import java.util.ArrayList;

public class Requirements {

	private String methodTags;
	private String vdcTags;
	
	
	public Requirements() {
		// TODO Auto-generated constructor stub
	}


	public String getMethodTags() {
		return methodTags;
	}


	public void setMethodTags(ArrayList<String> methodTags) {
		this.methodTags = "";
		for (String s:methodTags) {
			this.methodTags += s+" ";
		}
		
	}


	public String getVdcTags() {
		return vdcTags;
	}


	public void setVdcTags(ArrayList<String> vdcTags) {
		this.vdcTags = "";
		for (String s:vdcTags) {
			this.vdcTags += s+" ";
		}
	}
}

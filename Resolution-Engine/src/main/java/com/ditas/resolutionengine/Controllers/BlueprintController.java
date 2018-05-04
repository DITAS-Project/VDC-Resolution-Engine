package com.ditas.resolutionengine.Controllers;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BlueprintController {
	
	@RequestMapping(method=RequestMethod.POST , value="/searchBP")
	public String searchBP(@RequestBody String searchText){
		
		return null;
	}
}

package controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import domain.Census;

import services.CensusService;

@Controller
@RequestMapping("/welcome")
public class PercentageController {
	
	@Autowired
	private CensusService censusService;
	
	//Constructor----------------------------------
	private PercentageController(){
		super();
		
	}
	//Abstention percentage--------------------------------------------
	@RequestMapping(value="/display",method=RequestMethod.GET)
	public ModelAndView display(){
		ModelAndView result;
		Map<Census,Double> censos=new HashMap<Census,Double>();
		censos=censusService.abstentionPercentage();
		result=new ModelAndView("welcome/display");
		result.addObject("census",censos);
		result.addObject("requestURI", "cwelcome/display.do");
		return result;
	}
	

}

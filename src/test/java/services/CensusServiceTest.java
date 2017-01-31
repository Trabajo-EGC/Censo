package services;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;



import services.CensusService;
import domain.Census;

@ContextConfiguration(locations = { "classpath:spring/datasource.xml", 
									"classpath:spring/config/packages.xml" })

@RunWith(SpringJUnit4ClassRunner.class)

public class CensusServiceTest {
	
	// Service under test -----------------------

	@Autowired
	private CensusService censusService;
	
	// Tests ------------------------------------
	
	@Test
	public void findAllCensus(){
		Collection<Census> result;
		result = censusService.findAllCensus();
	
		System.out.println("Los censos almacenados son: " + result);
	}

	@Test
	public void createCensus() throws ParseException, FileNotFoundException, IOException {
		
		// Inicializamos un censo, inicializando los valores de los atributos -
		
		Census c_1 = censusService.create("C:\\Censo\\votacion.txt");
		
		// Inicializamos HashMap donde especificaremos quién ha votado y quién no, introducimos usuarios 
		// y estos los introducimos en el censo creado ------------------------
		
		HashMap<String, Boolean> vpo = c_1.getVotoPorUsuario();
		
		vpo.put("Pepe88", true);
		vpo.put("Ramiro90", false);
		vpo.put("Demetrio76", true);
		
		c_1.setVotoPorUsuario(vpo);
		
		// Mostramos por pantalla el Censo creado ------------------------------
		
		System.out.println(c_1);

	}
	
	@Test
	public void findOneCensus() throws ParseException{
		
		// Inicializamos una variable de tipo Census y buscamos uno con un id en concreto -
		
		Census result;
		
		result = censusService.findOne(2);
		
		System.out.println(result);
	}
	
	@Test
	public void findAllCensuses() throws ParseException{
		
		/* Inicializamos una variable como colección de Census agrupando por los censos
			creados por un usuario en concretro y buscamos que nos devuelva una colección de 
		 	Census. */
		
		Collection<Census> result;
		
		result = censusService.findCensusByCreator("DavidElecciones75");
		
		for(Census census: result){
		
			System.out.println(census);
		}
	}
	
	@Test
	public void findByKey(){
		
		/* Inicializamos una variable como colección de Census y  almacenamos en ella
			el resultado ( o los resultados en caso de existir más de un censo) del método
			findByKey(). En este caso, debe dar 1. */
		
				
		Collection<Census> result;
		
		result = censusService.findByKey("Elecciones");
		
		Assert.isTrue(result.size()==1);
	}
	
	@Test
	public void mostPopularCensus(){
		
		/* Inicializamos una variable como tipo Map<Census,Integer> y  almacenamos en ella
		el resultado del método mostPopularCensus(), que debe dar 10. */
		
		Map<Census,Integer> result;
		
		result = censusService.mostPopularCensus();
		Assert.isTrue(result.size()==10);
	}
	
}

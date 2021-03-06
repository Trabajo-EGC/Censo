
package services;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import repositories.CensusRepository;
import security.LoginService;
import utilities.Gmail;
import utilities.RESTClient;
import domain.Census;

@Service
@Transactional
public class CensusService {

	// Atributo necesario para mandar email con la modificaci�n del censo
	private static String		cuerpoEmail	= "";

	// Managed repository -----------------------------------------------------

	@Autowired
	private CensusRepository	censusRepository;


	// Constructors -----------------------------------------------------------

	public CensusService() {
		super();
	}

	// Methods ----------------------------------------------------------------

	public Collection<Census> findAllCensus() {
		Collection<Census> result;
		result = censusRepository.allCensus();
		Assert.notNull(result);
		return result;
	}

	/**
	 * Crea un censo a partir de una votaci�n
	 * 
	 * @param idVotacion
	 *            = Identificador de la votaci�n
	 * @param username
	 *            = Nombre de usuario que ha creado la votacion
	 * @param fechaInicio
	 *            = Fecha de inicio de la votacion
	 * @param fechaFin
	 *            = Fecha de fin de la votacion
	 * @param tituloVotacion
	 *            Cadena de texto con el titulo de la votacion
	 * @param tipoVotacion
	 *            Cadena de texto con el tipo de la votacion (abierta o cerrada)
	 * @return census
	 * @throws ParseException
	 */

	public Census create(String archivo) throws FileNotFoundException, IOException, ParseException {

		FileReader f = new FileReader(archivo);
		BufferedReader b = new BufferedReader(f);

		String votacion;
		String username;
		String fechaInicio;
		String fechaFin;
		String tituloVotacion;
		String tipoVotacion;

		votacion = b.readLine();
		username = LoginService.getPrincipal().getUsername();
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		Date aux = dateFormat.parse(b.readLine());
		Date aux1 = dateFormat.parse(b.readLine());
		tituloVotacion = b.readLine();
		tipoVotacion = b.readLine();

		b.close();

		int idVotacion = Integer.parseInt(votacion);

		Assert.isTrue(!username.equals(""));
		Assert.isTrue(tipoVotacion.equals("abierta") || tipoVotacion.equals("cerrada"));
		Census result = new Census();

		Assert.isTrue(aux.before(aux1));

		result.setFechaFinVotacion(aux1);
		result.setFechaInicioVotacion(aux);

		result.setIdVotacion(idVotacion);
		result.setTituloVotacion(tituloVotacion);
		if (tipoVotacion.equals("abierta"))
			result.setTipoCenso("abierto");
		else
			result.setTipoCenso("cerrado");
		result.setUsername(username);
		HashMap<String, Boolean> vpo = new HashMap<String, Boolean>();
		result.setVotoPorUsuario(vpo);

		return result;
	}

	/**
	 * Metodo que devuelve los censos en los que se puede registrar un usuario.
	 * Para ello, el censo tiene que ser abierto, estar la votacion activa y que
	 * el usuario dado no se encuentre ya registrado
	 * 
	 * @param username
	 */

	public Collection<Census> findCensusesToRegisterByUser(String username) {
		Assert.hasLength(username);
		Collection<Census> result = new ArrayList<Census>();
		Collection<Census> openedCensuses = new ArrayList<Census>();
		openedCensuses = censusRepository.findAllOpenedCensuses();

		for (Census c : openedCensuses)
			if (!c.getVotoPorUsuario().containsKey(username) && votacionActiva(c.getFechaInicioVotacion(), c.getFechaFinVotacion()))
				result.add(c);
		return result;
	}

	public Collection<Census> findAll(int censusID) {
		Collection<Census> result;
		result = censusRepository.findAll();
		Assert.notNull(result);
		return result;
	}

	/**
	 * M�todo usado por cabina que actualiza a true el estado de voto de un user
	 * 
	 * @param idVotacion
	 *            = Identificador de la votaci�n
	 * @param username
	 *            = Nombre de usuario
	 * @return boolean
	 * @throws IOException
	 */

	public boolean updateUser(String archivo) throws IOException, FileNotFoundException {

		FileReader f = new FileReader(archivo);
		BufferedReader b = new BufferedReader(f);

		String votacion;
		String tipoVotacion;
		String username;

		votacion = b.readLine();
		tipoVotacion = b.readLine();
		username = b.readLine();

		b.close();

		int idVotacion = Integer.parseInt(votacion);
		System.out.println(username);
		System.out.println(tipoVotacion);
		boolean result = false;
		Assert.isTrue(!username.equals(""));
		Census c = findCensusByVote(idVotacion);
		System.out.println(c);
		HashMap<String, Boolean> vpo = c.getVotoPorUsuario();
		System.out.println(vpo);
		if (vpo.containsKey(username) && !vpo.get(username)) {
			vpo.remove(username);
			vpo.put(username, true);
			result = true;
		}

		c.setVotoPorUsuario(vpo);
		save(c);

		return result;

	}

	/**
	 * Devuelve un json para saber si se puede borrar o no una votaci�n
	 * 
	 * @param idVotacion
	 *            = Identificador de la votaci�n
	 * @param username
	 *            = Nombre de usuario
	 * @return format json
	 */

	public String canDelete(int idVotacion, String username) {
		Assert.hasLength(username);
		String res = "";
		Census c = findCensusByVote(idVotacion);

		if (c.getVotoPorUsuario().isEmpty()) {

			// Si se puede se elimina

			res = "[{\"result\":\"yes\"}]";
			delete(c.getId(), username);
		} else
			res = "[{\"result\":\"no\"}]";
		return res;
	}

	/**
	 * Devuelve un json indicando si un usuario puede votar en una determinada
	 * votaci�n
	 * 
	 * @param idVotacion
	 *            = Identificador de la votaci�n
	 * @param username
	 *            = Nombre de usuario
	 * @return string format json
	 */

	public String canVote(int idVotacion, String username) {
		Assert.isTrue(!username.equals(""));
		String result = "";
		Boolean canVote = false;

		Census census = findCensusByVote(idVotacion);

		if (census.getVotoPorUsuario().containsKey(username) && !census.getVotoPorUsuario().get(username))
			canVote = true;

		if (canVote)
			result = "{\"result\":\"yes\"}";
		else
			result = "{\"result\":\"no\"}";

		return result;
	}

	/**
	 * M�todo que devuelve todos los censos de las votaciones en las que un
	 * usuario puede votar.
	 * 
	 * @param username
	 *            = Nombre de usuario
	 * @return Collection<census>
	 */

	public Collection<Census> findPossibleCensusesByUser(String username) {
		Assert.isTrue(username != "");
		Collection<Census> allCensuses = findAll();
		Collection<Census> result = new ArrayList<Census>();

		for (Census c : allCensuses)
			if (votacionActiva(c.getFechaInicioVotacion(), c.getFechaFinVotacion()))
				if (c.getVotoPorUsuario().containsKey(username) && !c.getVotoPorUsuario().get(username))
					result.add(c);
		return result;
	}

	/**
	 * Devuelve todos los censos que de un propietario
	 * 
	 * @param username
	 *            = Nombre de usuario
	 * @return Collection<census>
	 */

	public Collection<Census> findCensusByCreator(String username) {
		Assert.hasLength(username);
		Collection<Census> result = censusRepository.findCensusByCreator(username);
		return result;
	}

	/**
	 * Devuelve un determinado censo de un propietario
	 * 
	 * @param censusId
	 * @param username
	 * @return Census
	 */

	public Census findOneByCreator(int censusId, String username) {
		Assert.isTrue(!username.equals(""));
		Census result;
		result = findOne(censusId);
		Assert.isTrue(result != null);
		Assert.isTrue(result.getUsername().equals(username));

		return result;
	}

	/**
	 * 
	 * A�ade un usuario con un username determidado a un censo CERRADO
	 * 
	 * @param censusId
	 *            = Identificador del censo al que a�adir el usuario
	 * @param username
	 *            = Creador (propietario) del censo
	 * @param username_add
	 *            = Usuario que se va a a�adir al censo
	 */

	public void addUserToClosedCensus(int censusId, String username, String usernameAdd) {
		Census census = findOne(censusId);
		Assert.isTrue(census.getTipoCenso().equals("cerrado"));
		Assert.isTrue(votacionActiva(census.getFechaInicioVotacion(), census.getFechaFinVotacion()));
		Assert.isTrue(census.getUsername().equals(username));
		HashMap<String, Boolean> vpo = census.getVotoPorUsuario();

		Assert.isTrue(!vpo.containsKey(usernameAdd));
		vpo.put(usernameAdd, false);
		census.setVotoPorUsuario(vpo);
		save(census);

		// Env�o de correo

		String dirEmail;

		// Fecha para controlar cu�ndo se produce un cambio en el censo

		Date currentMoment = new Date();
		Map<String, String> usernamesAndEmails = RESTClient.getMapUSernameAndEmailByJsonAutentication();
		dirEmail = usernamesAndEmails.get(usernameAdd);
		CensusService.cuerpoEmail = currentMoment.toString() + "-> Se ha incorporado al censo de " + census.getTituloVotacion();
		try {

			// Se procede al env�o del correo con el resultado de la inclusi�n
			// en el censo
			Gmail.send(CensusService.cuerpoEmail, dirEmail);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * A�ade un usuario a un censo ABIERTO (registrarse en un censo abierto)
	 * 
	 * @param censusId
	 *            Identificador del censo al que a�adir el usuario
	 * @param username_add
	 *            Nombre de usuario que se va a a�adir al censo
	 */

	public void addUserToOpenedCensus(int censusId, String usernameAdd) {
		Census census = findOne(censusId);
		Assert.isTrue(census.getTipoCenso().equals("abierto"));
		Assert.isTrue(votacionActiva(census.getFechaInicioVotacion(), census.getFechaFinVotacion()));
		HashMap<String, Boolean> vpo = census.getVotoPorUsuario();
		Assert.isTrue(!vpo.containsKey(usernameAdd));
		vpo.put(usernameAdd, false);
		census.setVotoPorUsuario(vpo);
		save(census);

		// Env�o de correo

		String dirEmail;
		// Fecha para controlar cuando se
		// produce un cambio en el censo

		Date currentMoment = new Date();

		Map<String, String> usernamesAndEmails = RESTClient.getMapUSernameAndEmailByJsonAutentication();
		dirEmail = usernamesAndEmails.get(usernameAdd);
		CensusService.cuerpoEmail = currentMoment.toString() + "-> Se ha incorporado al censo de " + census.getTituloVotacion();
		try {

			// Se procede al envio del correo con el resultado de la inclusi�n
			// en el censo
			Gmail.send(CensusService.cuerpoEmail, dirEmail);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * Elimina un usuario con un username determidado de un censo CERRADO,
	 * cumpliendo la condicion de que el usuario no tenga voto en ese censo
	 * 
	 * @param censusId
	 *            = Identificador del censo
	 * @param username
	 *            = Creador (propietario) del censo
	 * @param username_add
	 *            = Usuario que se va a eliminar del censo
	 */
	public void removeUserOfClosedCensus(int censusId, String username, String username_remove) {
		Census census = findOne(censusId);
		Assert.isTrue(census.getTipoCenso().equals("cerrado"));
		Assert.isTrue(votacionActiva(census.getFechaInicioVotacion(), census.getFechaFinVotacion()));
		HashMap<String, Boolean> vpo = census.getVotoPorUsuario();
		Assert.isTrue(census.getUsername().equals(username));

		Assert.isTrue(vpo.containsKey(username_remove) && !vpo.get(username_remove));
		vpo.remove(username_remove);
		census.setVotoPorUsuario(vpo);
		save(census);

		// Env�o de correo

		String dirEmail;

		// Fecha para controlar cuando se produce un cambio en el censo

		Date currentMoment = new Date();

		Map<String, String> usernamesAndEmails = RESTClient.getMapUSernameAndEmailByJsonAutentication();
		dirEmail = usernamesAndEmails.get(username_remove);
		CensusService.cuerpoEmail = currentMoment.toString() + "-> Se ha eliminado del censo de " + census.getTituloVotacion();

		// Se procede al envio del correo con el resultado de la exclusi�n del
		// usuario del censo
		try {
			Gmail.send(CensusService.cuerpoEmail, dirEmail);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Guardar un censo
	 * 
	 * @param census
	 * @return census
	 */

	public Census save(Census census) {
		Census c = censusRepository.save(census);
		return c;
	}

	/**
	 * Elimina un censo si no tiene usuarios
	 * 
	 * @param censusId
	 *            = Identificador del censo
	 * @param username
	 */

	public void delete(int censusId, String username) {
		Census c = findOne(censusId);

		// Puedo borrarlo siempre y cuando no haya usuarios registrados

		Assert.isTrue(c.getVotoPorUsuario().isEmpty());
		Assert.isTrue(c.getUsername().equals(username));
		censusRepository.delete(censusId);
	}

	/**
	 * Encuentra un censo dado su id
	 * 
	 * @param censusId
	 *            = Identificador del censo
	 * @return census
	 */

	public Census findOne(int censusId) {
		Census c = censusRepository.findOne(censusId);
		Assert.notNull(c);
		return c;
	}

	/**
	 * Metodo que devuelve un json informando sobre un determinado usuario y su
	 * estado en el voto
	 * 
	 * @param idVotacion
	 *            = Identificador de la votacion
	 * @param username
	 *            = Usuario del cual queremos obtener su estado de voto
	 * @return String
	 */

	public String createResponseJson(int idVotacion, String username) {
		String response = "";
		Census c = findCensusByVote(idVotacion);

		if (c.getVotoPorUsuario().get(username))
			response = response + "{\"idVotacion\":" + idVotacion + ",\"username\":\"" + username + "\",\"result\":" + c.getVotoPorUsuario().get(username) + "}";
		else
			response = response + "{\"result\":" + "0}";
		return response;
	}

	/**
	 * Encuentra todos los censos del sistema
	 * 
	 * @return Collection<Census>
	 */

	public Collection<Census> findAll() {
		Collection<Census> result;
		result = censusRepository.findAll();
		return result;
	}

	/**
	 * Metodo para buscar el censo de una votaci�n
	 * 
	 * @param idVotacion
	 *            = Id de la votacion sobre la que se busca un censo
	 * @return census
	 */

	public Census findCensusByVote(int idVotacion) {
		Census result = censusRepository.findCensusByVote(idVotacion);
		Assert.notNull(result);
		return result;
	}

	/**
	 * 
	 * M�todo creado para saber si existe una votacion activa en el rango de
	 * fechas. Una votacion sera activa si su fecha de fin es posterior a la
	 * fecha actual.
	 * 
	 * @param fechaInicio
	 *            = Fecha inicio de la votacion
	 * @param fechaFin
	 *            = Fecha fin de la votacion
	 * @return true si est� activa
	 */

	private boolean votacionActiva(Date fechaInicio, Date fechaFin) {
		Boolean res = false;
		Date fechaActual = new Date();
		Long fechaActualLong = fechaActual.getTime();
		Long fechaFinLong = fechaFin.getTime();
		if (fechaFinLong > fechaActualLong)
			res = true;
		return res;
	}

	// NUEVA FUNCIONALIDAD 2015/2016

	/**
	 * M�todo para filtrar usuarios de un censo
	 * 
	 * @param username
	 *            = Username del usuario que buscamos
	 * @param censusId
	 *            = Id del censo sobre el que vamos a realizar la b�squeda
	 * @return el filtro de b�squeda
	 */

	public Collection<String> findByUsername(String username, int censusId) {
		Assert.hasLength(username);
		Assert.isTrue(censusId > 0);
		Census censo = findOne(censusId);
		Assert.notNull(censo);
		Collection<String> result = new ArrayList<String>();
		Map<String, String> map = RESTClient.getMapUSernameAndEmailByJsonAutentication();
		Collection<String> usernames = map.keySet();

		for (String user : usernames)
			if (user.contains(username))
				result.add(user);
		return result;
	}

	// NUEVA FUNCIONALIDAD 2016/2017

	/**
	 * M�todo para obtener los censo que han finalizado recientemente. *
	 * 
	 * @param username
	 *            = Username del usuario que buscamos
	 */
	public Collection<Census> findRecentFinishedCensus(String user) {
		Collection<Census> aux;
		Collection<Census> finished = new HashSet<Census>();
		aux = censusRepository.findAllCensusFinishedRecently();
		for (Census c : aux)
			if (c.getVotoPorUsuario().containsKey(user))
				finished.add(c);
		return finished;
	}

	/**
	 * M�todo para obtener los censos que posean un titulo. *
	 * 
	 * @param key
	 *            = Titulo o parte del titulo del censo
	 */
	public Collection<Census> findByKey(String key) {
		return censusRepository.findByRecipeKeyWord(key);
	}
	/**
	 * M�todo para obtener los 10 censos con mayor participaci�n
	 * 
	 */
	public Map<Census, Integer> mostPopularCensus() {
		Collection<Census> censos = censusRepository.findAll();
		Map<Census, Integer> res = new HashMap<Census, Integer>();
		Map<Census, Integer> result = new HashMap<Census, Integer>();
		for (Census c : censos) {
			Integer cont = 0;
			for (String s : c.getVotoPorUsuario().keySet())
				if (c.getVotoPorUsuario().get(s) == true)
					cont = cont + 1;
			res.put(c, cont);
		}
		res = CensusService.sortByValue(res);
		int cont = 0;
		for (Census c : res.keySet()) {
			if (cont < 10)
				result.put(c, res.get(c));
			else
				break;
			cont++;
		}

		return result;
	}

	/**
	 * M�todo para comparar la participaci�n de los censos para poder ordenarlos
	 * 
	 * @param censos
	 * @return censos ordenados
	 */

	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
		List<Map.Entry<K, V>> list = new LinkedList<>(map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<K, V>>() {

			@Override
			public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
				return (o2.getValue()).compareTo(o1.getValue());
			}
		});

		Map<K, V> result = new LinkedHashMap<>();
		for (Map.Entry<K, V> entry : list)
			result.put(entry.getKey(), entry.getValue());
		return result;
	}

	/**
	 * Muestra el porcentaje de abstenciones para los censos
	 * 
	 * @return Map<Census, Double>
	 */
	public Map<Census, Double> abstentionPercentage() {
		Collection<Census> censos = censusRepository.findAll();
		Map<Census, Double> res = new HashMap<Census, Double>();
		for (Census c : censos) {
			Integer cont = 0;
			for (String s : c.getVotoPorUsuario().keySet())
				if (c.getVotoPorUsuario().get(s) == false)
					cont = cont + 1;
			res.put(c, (double) cont / c.getVotoPorUsuario().keySet().size() * 100);
		}
		return res;
	}

	// NUEVA FUNCIONALIDAD 2016/2017 VERANO

	/**
	 * Muestra los censos activos
	 * 
	 * @return Collection<Census>
	 */

	public Collection<Census> activeCensus() {
		Collection<Census> result;
		result = censusRepository.activeCensus();
		Assert.notNull(result);
		return result;
	}

}

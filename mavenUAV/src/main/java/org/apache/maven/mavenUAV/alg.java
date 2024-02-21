package org.apache.maven.mavenUAV;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.alg.scoring.BetweennessCentrality;
import org.jgrapht.alg.scoring.ClosenessCentrality;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

public class alg {

	private Graph<UAV,DefaultWeightedEdge> g;
	private List<peticion> peticiones;
	private double porcentaje_de_replicacion;
	private Map<String,Integer> mapa_replicacion; //se utiliza para llevar la cuenta de cuantos VNFs hay de cada uno de los UAVs, 
												  //la clave es el id del UAV y el valor el numero de replicas del VNF
	
	private int numero_de_usuarios;
	private Map<UAV,Integer> mapa_de_usuarios_conectados;
	
	//------------parametros para obtener tablas-------------
	Map<Integer,Double> mapa_de_latencias; //contiene una peticion como key y la latencia e esa peticion en el value
	Map<Integer,Integer> mapa_de_saltos; //contiene una peticion como key y el numero de saltos necesarios para ejecutar la peticion
	
	
	private static final int BANDWIDTH= 1000000000; //herz
	private static final double GAIN_AT_ZERO= -43; //db
	private static final int NOISE_POWER_DENSITY= -174;
	private static final double POWER_TRANSMIT_USER= 0.2;
	private static final double POWER_TRANSMIT_UAV= 1; 
	private static final double LIGHT_SPEED= 299792458;
	private static final int PACKET_SIZE= 448; //bits
	private static final int DISTANCE_BEETWEEN_UAVS=900; 
	
	public alg(List<peticion> peticiones, double pdr) {
		this.g = new SimpleWeightedGraph<UAV, DefaultWeightedEdge>(DefaultWeightedEdge.class); //Grafo ofrecido por Jgrapht
		this.mapa_replicacion=new HashMap<String,Integer>();
		this.peticiones=peticiones;
		this.porcentaje_de_replicacion=pdr;
		this.mapa_de_latencias= new TreeMap<Integer,Double>();
		this.mapa_de_saltos= new TreeMap<Integer,Integer>();
	}
	
	
	public Map<Integer, Integer> getMapa_de_saltos() {
		return mapa_de_saltos;
	}

	public void setMapa_de_saltos(Map<Integer, Integer> mapa_de_saltos) {
		this.mapa_de_saltos = mapa_de_saltos;
	}

	public Map<UAV, Integer> getMapa_de_usuarios_conectados() {
		return mapa_de_usuarios_conectados;
	}

	public void setMapa_de_usuarios_conectados(Map<UAV, Integer> mapa_de_usuarios_conectados) {
		this.mapa_de_usuarios_conectados = mapa_de_usuarios_conectados;
	}

	public Map<Integer, Double> getMapa_de_latencias() {
		return mapa_de_latencias;
	}

	public void setMapa_de_latencias(Map<Integer, Double> mapa_de_latencias) {
		this.mapa_de_latencias = mapa_de_latencias;
	}

	public int getNumero_de_usuarios() {
		return numero_de_usuarios;
	}

	public void setNumero_de_usuarios(int numero_de_usuarios) {
		this.numero_de_usuarios = numero_de_usuarios;
	}

	public List<peticion> getPeticiones() {
		return peticiones;
	}

	public void setPeticiones(List<peticion> peticiones) {
		this.peticiones = peticiones;
	}

	/**
	 * @return El grafo que representa la topología
	 * */
	public Graph<UAV,DefaultWeightedEdge> getGraph(){
		return g;
	}
		
	/**
	 * @return El número de UAVs que hay en el grafo
	 * */
	public int getNumberOfUAVs() {
		return getGraph().vertexSet().size();
	}
	
	public double getPorcentaje_de_replicacion() {
		return porcentaje_de_replicacion;
	}

	public void setPorcentaje_de_replicacion(double porcentaje_de_replicacion) {
		this.porcentaje_de_replicacion = porcentaje_de_replicacion;
	}
	
	public Map<String, Integer> getMapa_replicacion() {
		return mapa_replicacion;
	}

	public void setMapa_replicacion(Map<String, Integer> mapa_replicacion) {
		this.mapa_replicacion = mapa_replicacion;
	}
	
	/**
	 * @return El número máximo de VNFs de cada tipo que puede haber en el grafo
	 * */ 
	public int getMaxNumberOfVNFs() {		
		return (int)(getPorcentaje_de_replicacion()*getNumberOfUAVs());
	}
	
	
	
	/**
	 * Revierte los cambios apuntados en la lista de "cambios" la cual contiene los UAVs antes de ser modificados por el algoritmo
	 * de forma que recorremos la lista de cambios y reemplazamos los UAVs de la lista con ids iguales a los UAVs que se encuentran en el grafo
	 * @param cambios lista que contiene los UAVs antes de ser modificados por el algoritmo de colocación.
	 * */ 
	public void limpiarListaCambios(Collection<UAV> cambios) {
		for(UAV u: cambios) {
			for(UAV o :getGraph().vertexSet()) {
				if(u.getId()==o.getId()) {
					o=u;
				}
			}
		}
		cambios.clear();
	
	Map<String, Integer> mapa_replicacion_nuevo= new TreeMap<String, Integer>();
		
	for(UAV o :getGraph().vertexSet())
		for(VNF m :o.getVNF_List())
			if(mapa_replicacion_nuevo.containsKey(m.getNombre()))
				mapa_replicacion_nuevo.put(m.getNombre(),mapa_replicacion_nuevo.get(m.getNombre())+1 );
			else
				mapa_replicacion_nuevo.put(m.getNombre(),1 );
		
	}
	
	
	/**
	 * introduce el microservicio m dentro del UAV current actualizando la memoria ram e introduciendo una copia del UAV current
	 * en la lista de cambios en caso de que la peticion no pueda terminarse
	 * @param current UAV en el que se va a instalar el microservicios m
	 * @param m microservicios a instalar
	 * @param cambios lista que contiene los UAVs antes de ser modificados por el algoritmo de colocación.
	 * */ 
	public void aplicarCambiosUav(UAV current, VNF m, List<UAV> cambios) {
		UAV c= new UAV(current);
		cambios.add(c);
	
		current.addVNF(m);
		current.setCapacidadRAM(current.getCapacidadRAM()-m.getRequerimientoRAM());
		
		System.out.println("	Se instala el microservicio " + m.getNombre() + " en el UAV con id " + current.getId());
		System.out.println("		-La capacidad RAM del auv " + current.getId() + " ha sido reducida a " + current.getCapacidadRAM());
		
		//tendremos que modificar la bateria del UAV al instalarse el UAV. 
		
		if(!getMapa_replicacion().containsKey(m.getNombre())) {
			getMapa_replicacion().put(m.getNombre(),1);
			System.out.println("		-El numero de microservicios de " + m.getNombre() + " es " + getMapa_replicacion().get(m.getNombre()).intValue());
		}
		else{
			getMapa_replicacion().put(m.getNombre(),getMapa_replicacion().get(m.getNombre()).intValue()+1);
			System.out.println("		-El numero de microservicios de " + m.getNombre() + " es " + getMapa_replicacion().get(m.getNombre()).intValue());
		}
		
		
	}
	
	
	
	
	/**
	 * @param u UAV en el que se va a instalar el microservicios m
	 * @param m microservicios a instalar
	 * @return falso en el caso de que el microservicio m ya se encuentre en el UAV u y en el caso de que
	 * se haya llegado al número máximo de replicación de microservicios, en caso contrario el método retorna verdadero
	 * */
	private boolean comprobarReplicacionVNF(VNF m, UAV u) {
		boolean correct=true;
		
		//En este metodo solo vamos a comprobar si el microservicio ha alcanzado el numero de replicaciones.	
		if (u.checkVNF(m)) {
			System.out.println("	el microservicio ya existe en el UAV"); //tenemos este if aki por el simple hecho de tener el mensajede otra forma se complica ponerlo en el metodo principal
			correct=false;
		}else
			if(getMapa_replicacion().get(m.getNombre())!=null)
				if (getMapa_replicacion().get(m.getNombre()) >= getMaxNumberOfVNFs()) {
					System.out.println("	se ha llegado al limite de instancias instaladas del microservicio " + m.getNombre());
					correct=false;
				}
		
		return correct;
	}
	
	
	/**
	 * @param m microservicios a instalar
	 * @return retorna falso en el caso de que se haya llegado al número máximo de replicación 
	 * de microservicios, en caso contrario el método retorna verdadero
	 * */
	private boolean comprobarReplicacionVNF(VNF m) {
		boolean correct=true;
		
		if(getMapa_replicacion().get(m.getNombre())!=null)
			if (getMapa_replicacion().get(m.getNombre()) >= getMaxNumberOfVNFs()) {
				System.out.println("	se ha llegado al limite de instancias instaladas del microservicio " + m.getNombre());
				correct=false;
			}
		
		return correct;
	}
	
	//metodo que encuentra 
	//si el parametro modoEMU es verdadero el algoritmo buscara el mejor UAV en todo el grafo, en caso contrario solo buscara
	//en los adyacentes a current
	

	/**
	 * @param current UAV sin recursos para instalar m
	 * @param m microservicios a instalar
	 * @param modoEMU si el parámetro modoEMU es verdadero el algoritmo busca el mejor UAV en todo el grafo, en caso contrario solo busca en los adyacentes a current
	 * @return el mejor UAV para la satisfacer los requerimientos del microservicio m siguiendo 
	 * unos criterios de busqueda( mayor baterúa RAM restante, menor número de VNF instalados, grado del UAV)
	 * */
	private UAV encontrarMejorUav(UAV current, VNF m, boolean modoEMU) {
		
		UAV mejorUAV = null;
		float capacidadRamMax=0;
		int grado=0;
		int totalVNF=99;
		
		Collection<UAV> posiblesUAV= new ArrayList<UAV>(Graphs.neighborListOf(g, current));
		if(modoEMU==true)
			 posiblesUAV=getGraph().vertexSet();
		
		for(UAV a : posiblesUAV) {
			if(!a.checkVNF(m))
				if(capacidadRamMax<a.getCapacidadRAM() && m.getRequerimientoRAM() <= a.getCapacidadRAM() && m.getRequerimientoBateria() <= a.getBateria()) {
					capacidadRamMax=a.getCapacidadRAM();				
					grado=getGraph().edgesOf(a).size();
					totalVNF=a.getVNF_List().size();
				
					mejorUAV=a;
				}
				else
					if(capacidadRamMax==a.getCapacidadRAM()) {
						if(totalVNF<a.getVNF_List().size() && m.getRequerimientoRAM() <= a.getCapacidadRAM() && m.getRequerimientoBateria() <= a.getBateria()) {
							totalVNF=a.getVNF_List().size();
							grado=getGraph().edgesOf(a).size();
							mejorUAV=a;
						}
						else {
							if(totalVNF==a.getVNF_List().size()) {
								if(grado<getGraph().edgesOf(a).size() && m.getRequerimientoRAM() <= a.getCapacidadRAM() && m.getRequerimientoBateria() <= a.getBateria()) {
									grado=getGraph().edgesOf(a).size();
									mejorUAV=a;		
								}
							}				
						}	
					}
		}//for
		
		return mejorUAV;
	}
	
	
	/**Método de colocación de microservicios basado en peticiones con saturación controlada
	 * @param max_ms_per_uav número máximo (no estricto) de microservicios por UAV
	 */
	public void  VNF_placement_algorithm_Version_Saturacion(int max_ms_per_uav) {
		int peticiones_satisfechas=0;
		UAV currentUAV;
		VNF m;
		List<UAV>cambios= new ArrayList<UAV>();
		Iterator<VNF> it;

		for(peticion w : peticiones) {
			currentUAV=w.getOrigen();
			it = w.getLista_vnf().iterator();
			while(it.hasNext()) {
				m=it.next();
				if(comprobarReplicacionVNF(m,currentUAV)) {
					if(currentUAV.getVNF_List().size()>=max_ms_per_uav) {
						UAV mejorUAV=encontrarMejorUav(currentUAV,m,false);
						if(mejorUAV!=null) {
								currentUAV=mejorUAV;
								aplicarCambiosUav(currentUAV, m, cambios);
						}
						else
							System.out.println("no existe mejor auv");					
					}
					else {
						if(m.getRequerimientoRAM() <= currentUAV.getCapacidadRAM())
							aplicarCambiosUav(currentUAV, m, cambios);
					}
				}
				
			}//while ms
			
		}//for peticiones
	
		for(peticion w: peticiones) {
			boolean x=LRIRA(w);
			if(x) peticiones_satisfechas++;
			System.out.println("peticiones satisfechas: " + peticiones_satisfechas);
			System.out.println("-----------------------------------------------");
		}
	}
	
	
	/**metodo de colocación de microservicios basado en peticiones - Tercera versión
	 * - no recupera el estado anterior del grafo so se ha llegado al límite de replicación
	 * - salta al siguiente UAV si current ya contiene el microservicio m 
	 * @param modoEMU si el parámetro modoEMU es verdadero el algoritmo busca el mejor UAV en todo el grafo, en caso contrario solo busca en los adyacentes a current
	 */
	public void VNF_placement_algorithm_TV(boolean modoEMU) {
		int peticiones_satisfechas=0;
		boolean exit;
		UAV currentUAV;
		VNF m;
		Iterator<VNF> it;
		List<UAV>cambios= new ArrayList<UAV>();
		
		for(peticion w : peticiones) {
			System.out.println("Peticion con origen en el UAV con ID " + w.getOrigen());
			currentUAV= w.getOrigen();
			
			
			it = w.getLista_vnf().iterator(); 
			exit=false;
			while(it.hasNext() && !exit){ //(º0º)
				m=it.next();
				if(comprobarReplicacionVNF(m)) { 
					if(m.getRequerimientoRAM() <= currentUAV.getCapacidadRAM() && m.getRequerimientoBateria() <= currentUAV.getBateria() && !currentUAV.checkVNF(m)) {
						
						aplicarCambiosUav(currentUAV, m,cambios);
						
					}
					else {
						System.out.println("	El UAV con id " + currentUAV.getId() +" no tiene recursos para instalar el microservicio " + m.getNombre());
						System.out.println("	buscando el mejor AUV...");
						UAV mejorUAV=encontrarMejorUav(currentUAV,m,modoEMU);
						if(mejorUAV!=null) {
							System.out.println("	mejor UAV encontrado");
							currentUAV=mejorUAV; //esto se puede quitar
								
							aplicarCambiosUav(currentUAV, m,cambios);
								
						}
						else {
								System.out.println("	Mejor UAV no encontrado-no se puede realizar la peticion");
								limpiarListaCambios(cambios);
								exit=true; //tambien prodiamos simplemente quitar el exit y continuar aunque las peticiones 
						}
												
					}
				}
					
			}//while
			
			boolean x=LRIRA(w);
			if(x) peticiones_satisfechas++;
			System.out.println("peticiones satisfechas: " + peticiones_satisfechas);
			System.out.println("-----------------------------------------------");
		}//for		
				
	}
	
	
	/**metodo de colocación de microservicios basado en peticiones - Segunda versión
	 * - recupera el estado anterior del grafo so se ha llegado al limite de replicación
	 * - saltara al siguiente UAV si current ya contiene el microservicio m 
	 * @param modoEMU si el parámetro modoEMU es verdadero el algoritmo busca el mejor UAV en todo el grafo, en caso contrario solo busca en los adyacentes a current
	 */
	public void VNF_placement_algorithm_SV(boolean modoEMU) {
		int peticiones_satisfechas=0;
		boolean exit;
		UAV currentUAV;
		VNF m;
		Iterator<VNF> it;
		List<UAV>cambios= new ArrayList<UAV>();
		
		for(peticion w : peticiones) {
			System.out.println("Peticion con origen en el UAV con ID " + w.getOrigen());
			currentUAV= w.getOrigen();
			
			it = w.getLista_vnf().iterator(); 
			exit=false;
			while(it.hasNext() && !exit){ //(º0º)
				m=it.next();
				if(comprobarReplicacionVNF(m)) { 
					if(m.getRequerimientoRAM() <= currentUAV.getCapacidadRAM() && m.getRequerimientoBateria() <= currentUAV.getBateria() && !currentUAV.checkVNF(m)) {
						
						aplicarCambiosUav(currentUAV, m,cambios);
						
					}
					else {
						System.out.println("El UAV con id " + currentUAV.getId() +" no tiene recursos para instalar el microservicio " + m.getNombre());
						System.out.println("buscando el mejor AUV...");
						UAV mejorUAV=encontrarMejorUav(currentUAV,m,modoEMU);
						if(mejorUAV!=null) {
							System.out.println("	mejor UAV encontrado");
							currentUAV=mejorUAV; //esto se puede quitar
								
							aplicarCambiosUav(currentUAV, m,cambios);
								
						}
						else {
								System.out.println("	Mejor UAV no encontrado-no se puede realizar la peticion");
								limpiarListaCambios(cambios);
								exit=true; //tambien prodiamos simplemente quitar el exit y continuar aunque las peticiones 
						}
												
					}
				}
				else {
					limpiarListaCambios(cambios);
					exit=true; //tambien prodiamos simplemente quitar el exit y continuar aunque las peticiones
				}
					
			}//while
			boolean x=LRIRA(w);
			if(x) peticiones_satisfechas++;
			System.out.println("peticiones satisfechas: " + peticiones_satisfechas);
			System.out.println("-----------------------------------------------");
		}//for		
		
	}
	
	
	/**metodo de colocación de microservicios basado en peticiones - Primera versión
	 * - no recupera el estado anterior del grafo so se ha llegado al limite de replicación
	 * - no saltara al siguiente UAV si current ya contiene el microservicio m 
	 * @param modoEMU si el parámetro modoEMU es verdadero el algoritmo busca el mejor UAV en todo el grafo, en caso contrario solo busca en los adyacentes a current
	 */
	public void VNF_placement_algorithm_FV(boolean modoEMU) {
		int peticiones_satisfechas=0;
		boolean replicacion_correcta=false;
		boolean exit;
		UAV currentUAV;
		VNF m;
		Iterator<VNF> it;
		List<UAV>cambios= new ArrayList<UAV>();
		
		for(peticion w : peticiones) {
			System.out.println("Peticion con origen en el UAV con ID " + w.getOrigen());
			currentUAV= w.getOrigen();
			cambios.clear();
			
			it = w.getLista_vnf().iterator(); 
			exit=false;
			
			while(it.hasNext() && !exit){ //(º0º)
				m=it.next();
				replicacion_correcta=comprobarReplicacionVNF(m,currentUAV);// 
				if(replicacion_correcta) { 
					if(m.getRequerimientoRAM() <= currentUAV.getCapacidadRAM() && m.getRequerimientoBateria() <= currentUAV.getBateria()) {
						
						aplicarCambiosUav(currentUAV, m,cambios);
						
					}
					else {
						
						System.out.println("El UAV con id " + currentUAV.getId() +" no tiene recursos para instalar el microservicio " + m.getNombre());
						System.out.println("buscando el mejor AUV...");
						UAV mejorUAV=encontrarMejorUav(currentUAV,m,modoEMU);
						if(mejorUAV!=null) {
							System.out.println("	mejor UAV encontrado");
							currentUAV=mejorUAV; //esto se puede quitar
								
							aplicarCambiosUav(currentUAV, m,cambios);
								
						}
						else {
								System.out.println("	Mejor UAV no encontrado-no se puede realizar la peticion");
								limpiarListaCambios(cambios);
								exit=true; //tambien prodiamos simplemente quitar el exit y continuar aunque las peticiones 

						}
							
						
					}
				}
			}//while
			boolean x=LRIRA(w);
			if(x) peticiones_satisfechas++;
			System.out.println("peticiones satisfechas: " + peticiones_satisfechas);
			System.out.println("-----------------------------------------------");
		}//for
	}
	
	/** Método que ordena el mapa m con respecto al valor
	 * @param map mapa a ordenar
	 * @return mapa map ordenado según su valor
	 */
	public Map<UAV,Double> ordenarMapa(Map<UAV, Double> map ){
		
		LinkedHashMap<UAV, Double> sortedMap = new LinkedHashMap<>();
	    ArrayList<Double> list = new ArrayList<>();
	    for (Entry<UAV, Double> entry : map.entrySet()) {
	    	list.add(entry.getValue());
	    }
	    Collections.sort(list,Collections.reverseOrder()); 
	    for (Double num : list) {
	    	for (Entry<UAV, Double> entry : map.entrySet()) {
	    		if (entry.getValue().equals(num)) {
	    			sortedMap.put(entry.getKey(), num);
	            }
	        }
	    }
			
		return sortedMap;
	}
		
	//coloca los microservicios que forman las peticiones de la lista de peticiones de forma que saturen los 
	//UAV en los que se estan instalando estas peticiones, los microservicios se colocaran siguiendo el orden de los UAV de la lista 
	//introducirda por parametro.
	
	
	
	/**
	 * Método que coloca los microservicios que forman las peticiones de la lista de peticiones de forma que saturen los 
	 * UAV en los que se estan instalando estas peticiones, los microservicios se colocaran siguiendo el orden de los UAV de la lista 
	 * introducida por parámetro.
	 * @param Lista de UAVs ordenada según uno de los criterios de prioridad definidos(Node degree, closeness centrality y betweenness centrality)
	 */
	private void colocacion_con_saturacion(List<UAV> Lista_UAVs) {
		boolean exit= false;
		Iterator<UAV> it; 
		UAV currentUAV;
		List<UAV>cambios= new ArrayList<UAV>();

		
		for(peticion p : peticiones) {
			for(VNF m : p.getLista_vnf()) {
				it=Lista_UAVs.iterator();
				currentUAV=it.next();
				exit=false;
				if(comprobarReplicacionVNF(m))
					while(it.hasNext() && !exit) {
						if(currentUAV.checkVNF(m) ||  m.getRequerimientoRAM()>currentUAV.getCapacidadRAM() ){
								
							currentUAV=it.next();
							
							if(!it.hasNext() &&  m.getRequerimientoRAM()>currentUAV.getCapacidadRAM()) {
								limpiarListaCambios(cambios);
							}
							
						}
						else{
							aplicarCambiosUav(currentUAV,m,cambios);	
							exit=true;
						}

					}//while
			}//for
		}//for	
	}
	
	
	/**
	 * Método que coloca los microservicios que forman las peticiones de la lista de peticiones de forma que no saturen los 
	 * UAV en los que se estan instalando estas peticiones, los microservicios se colocaran de forma cíclica en los UAV de la lista ordenada introducida por parámetro
	 * @param Lista de UAVs ordenada según uno de los criterios de prioridad definidos(Node degree, closeness centrality y betweenness centrality)
	 */
	private void colocacion_sin_saturacion(List<UAV> Lista_UAVs) {
		Iterator<UAV> it; 
		UAV currentUAV;
		List<UAV>cambios= new ArrayList<UAV>();
		
		
		it=Lista_UAVs.iterator();
		
		for(peticion p: peticiones) {
			for(VNF m: p.getLista_vnf()) {
				if(comprobarReplicacionVNF(m)) {
					if(it.hasNext()) {	
						currentUAV=it.next();
						if(!currentUAV.checkVNF(m) &&  m.getRequerimientoRAM()<=currentUAV.getCapacidadRAM() ){	
							aplicarCambiosUav(currentUAV,m,cambios);	
						}	
						else 
							System.out.print("el microservicio ya existe en el UAV o el UAV no tiene recursos");
					}
					else{
						it=Lista_UAVs.iterator();
					}
				}
			}			
		}
		
	}
	
	//
	
	/**Algoritmo de colocación de microservicios utilizando las propiedades del grafo(closeness centrality, betweenessCentrality, degree)
	 * @param algoritmo parámetro que elige el criterio de prioridad por que se van a ordenar los UAVs en los que se instalan los microservicios 
	 * (0 para Node degree, 1 para closeness centrality y 2 para betweenness centrality)
	 * @param saturacion parámetro que elige el modo de instalación del algoritmo (True para instalación con saturación y false para instalación sin saturación)
	 */
	public void VNF_placement_algorithm_Version_Propiedades_Grafo(int algoritmo, boolean saturacion) {
		Map<UAV,Double> map= null;
		int peticiones_satisfechas=0;		
		boolean x;

		switch(algoritmo) {
		case 1:
			ClosenessCentrality<UAV, DefaultWeightedEdge> cc= new ClosenessCentrality<UAV, DefaultWeightedEdge>(getGraph());
			map=ordenarMapa(cc.getScores());
			System.out.println("Se ha elegido el modo ClosenessCentrality para ordenar los UAVs candidatos");
			break;
		case 2:
			BetweennessCentrality<UAV, DefaultWeightedEdge> bc= new BetweennessCentrality<UAV, DefaultWeightedEdge>(getGraph());
			map=ordenarMapa(bc.getScores());
			System.out.println("Se ha elegido el modo BetweenessCentrality para ordenar los UAVs candidatos");
			break;
		case 0:
			map= new HashMap<>();
			for(UAV u: getGraph().vertexSet()) {
				map.put(u,(double) getGraph().degreeOf(u));
			}
			map=ordenarMapa(map);
			System.out.println("Se ha elegido el modo NodeDegree para ordenar los UAVs candidatos");
			break;
		
		}
		
		ArrayList<UAV> Lista_UAVs= new  ArrayList<UAV>(map.keySet());
		System.out.println(Lista_UAVs);
		
		if(saturacion)
			colocacion_con_saturacion(Lista_UAVs);
		else
			colocacion_sin_saturacion(Lista_UAVs);
		
		System.out.println("-----------------------------------------------------------------------------------------------------------------");

		for(peticion w: peticiones) {
			x=LRIRA(w);
			if(x) peticiones_satisfechas++;
			System.out.println("peticiones satisfechas: " + peticiones_satisfechas);		
			System.out.println("-----------------------------------------------------------------------------------------------------------------");
		}
	}	
	
	
	
	
	/**
	 * @param m microservicio que se busca ejecutar
	 * @param currentUAV UAV origen de la petición 
	 * @return una lista con los caminos más cortos desde el UAV currentUAV introducido por parámetro a todos los UAV que 
	 * contengan el microservicio m
	 */
	public List<GraphPath<UAV, DefaultWeightedEdge>> CaminosMasCortosHacia(UAV currentUAV,VNF m) {
		List<GraphPath<UAV, DefaultWeightedEdge>> lista_de_caminos =new  ArrayList<GraphPath<UAV, DefaultWeightedEdge>>();
		
		for(UAV u:getGraph().vertexSet()){
			if(u.checkVNF(m)) {
				GraphPath<UAV, DefaultWeightedEdge> l = DijkstraShortestPath.findPathBetween(getGraph(),currentUAV,u);
				List<UAV> lista_vertices = l.getVertexList();
				lista_de_caminos.add(l);	
			}
		}
			
		return lista_de_caminos;
	}
	
	
	
	/**
	 * @param m microservicio que se busca ejecutar
	 * @param gp  almacena un camino con los UAVs necesarios para llegar al UAV que contiene el microservicio m
	 * @return True en el caso de que el UAV final de camino introducido por parámetro (mediante un GraphPath) tenga recursos para ejecutar el microservicio m
	 * y si todos los UAVs que forman el camino introducido tienen batería suficiente para realizar la transmisión de datos. False en caso contrario.
	 */
	private boolean comprobarCamino(GraphPath<UAV, DefaultWeightedEdge> gp, VNF m) {
		boolean operativo= true;
		
		if(gp.getEndVertex().calcularSOC_peticion(m) < 0 || gp.getEndVertex().getCapacidadRAM() < m.getRequerimientoRAM()){
			operativo= false;
		}
		else {
			List<UAV> camino= gp.getVertexList();
			for(UAV u: camino) {
				if( u.calcularSOC_transmision_propagacion(calcular_Retardo_Propagacion_UAV_2_UAV(),calcular_Retardo_Transmision_UAV_2_UAV()) < 0 /* aki tendriamos que comprobar el ram para el archivo*/ ) {
					operativo= false;
				}
			}
		}
		return operativo;
	}
	
	/**
	 * añade el camino almacenado en la estructura GraphPath en la lista camino_total
	 * @param gp almacena un camino con los UAVs necesarios para llegar al UAV que contiene un microservicio necesario para ejecutar una petición
	 * @param camino_total almacena un camino con los UAVs que contienen los necesarios para ejecutar una petición
	 */
	private void GraphPathToList(GraphPath<UAV,DefaultWeightedEdge> gp, List<UAV> camino_total) {
		
		for( UAV u: gp.getVertexList()) {
			if(gp.getVertexList().size()!=1)
				if(camino_total.size()==0)
					camino_total.add(u);
				else
					if(camino_total.get(camino_total.size()-1)!=u) {
						camino_total.add(u);
					}		
		}
	}	
	
	
	/**
	 * Metodo que el camino más corto que contenga los UAVs con los microservicios necesarios para ejecutar la petición w
	 * @param w petición a ejecutar
	 * @return True si existe un camino que pueda ejecutar la petición. False en caso contrario
	 */
	public boolean LRIRA(peticion w) {
		
		boolean ejecucion_correcta=true;
		int mejor_distancia= 999; 
		UAV currentUAV=null;
		VNF m;		
		//contiene el mejor camino para ejecutar el microservicio m de la petición w
		GraphPath<UAV, DefaultWeightedEdge> mejor_camino = null;
		
		//contiene una lista con todos los del mejor camino para ejecutar la petición w
		List<UAV> camino_total=new ArrayList<UAV>();
		
		//contiene todos los caminos posibles del UAV current a otros UAV con el microservicio m
		List<GraphPath<UAV, DefaultWeightedEdge>> lista_de_caminos =new  ArrayList<GraphPath<UAV, DefaultWeightedEdge>>();
		
		//contiene los UAVS que han sido modificados durante la comprobación para revertir los cambios en caso de que alguno de los microservicios no se pueda ejecutar
		Set<UAV> cambios=new HashSet<UAV>();
			
		currentUAV=w.getOrigen();
		Iterator<VNF> it=w.getLista_vnf().iterator();
		
		System.out.println("Comprabando si se puede ejecutar la petición con origen " + w.getOrigen());
		
		while(it.hasNext() && ejecucion_correcta){ //recorremos los microservicios de la peticion
			m=it.next();
			lista_de_caminos=CaminosMasCortosHacia(currentUAV,m);
			mejor_distancia= 999;
			
			for(GraphPath<UAV, DefaultWeightedEdge> gp: lista_de_caminos ) { //recorremos los caminos posibles a los UAV con ese microservicios
				if(comprobarCamino(gp, m)) {
					if(gp.getLength()<=mejor_distancia) {
						mejor_camino=gp;
						mejor_distancia=gp.getLength();
						currentUAV=gp.getEndVertex();	
					}
				}	
			}//for caminos
			
			if(mejor_distancia==999) {
				System.out.println("		-no se ha encontrado un UAV que pueda ejecutar el microservicio m");
				ejecucion_correcta=false;
				limpiarListaCambios(cambios);
			}
			else {
				
				cambios.add(currentUAV);
				
				currentUAV.setCapacidadRAM(currentUAV.getCapacidadRAM()-m.getRequerimientoRAM()); //nose si se necesita
				currentUAV.setBateria(currentUAV.calcularSOC_peticion(m));//gasto de bateria por la ejecucion en el UAV destino.
				
				for(UAV u: mejor_camino.getVertexList()) {
					if(u!=mejor_camino.getEndVertex()) {
						cambios.add(u);
						u.setBateria(u.calcularSOC_transmision_propagacion(calcular_Retardo_Propagacion_UAV_2_UAV(),calcular_Retardo_Transmision_UAV_2_UAV()));
					}
				}
												
				System.out.println("	el nuevo mejor camino es para instalar el microservicio " + m.getNombre() +" es "+ mejor_camino.getVertexList().toString());
				System.out.println("		-el UAV en que nos encontramos es " + currentUAV);
				System.out.println("		-la bateria del UAV " + currentUAV.getId() + " ha cambiado a " + currentUAV.getBateria());
				System.out.println("		-La memoria RAM del UAV " + currentUAV.getId() + " ha cambiado a " + currentUAV.getCapacidadRAM());

				//camino_total + mejor_camino= camino_total;
				GraphPathToList(mejor_camino, camino_total);
					
			}
					
		}//while microservicios
		
		
		/// CAMINO DE VUELTA
		
		GraphPath<UAV, DefaultWeightedEdge> l = DijkstraShortestPath.findPathBetween(getGraph(),currentUAV,w.getOrigen());
		for(UAV u : l.getVertexList()) {
			if( u.calcularSOC_transmision_propagacion(calcular_Retardo_Propagacion_UAV_2_UAV(),calcular_Retardo_Transmision_UAV_2_UAV()) < 0 ) {
				ejecucion_correcta=false;
			}
		}
		
		if(ejecucion_correcta) {
			
			for(UAV u: l.getVertexList()) {
				if(u!=l.getEndVertex()) {
					u.setBateria(u.calcularSOC_transmision_propagacion(calcular_Retardo_Propagacion_UAV_2_UAV(),calcular_Retardo_Transmision_UAV_2_UAV()));
				}
			}
			
			GraphPathToList(l, camino_total);
			
			int n=getMapa_de_usuarios_conectados().get(w.getOrigen());
						
			int numero_de_saltos=camino_total.size()-1;
			if(numero_de_saltos-1<0) numero_de_saltos=0;
			
			getMapa_de_saltos().put(w.getId(), numero_de_saltos);
			
			System.out.println("numero de saltos realizados para ejecutar la peticion: " + numero_de_saltos);
			System.out.println("latencia inicial entre el usuario y el UAV origen: " + calcular_latencia_UAV_2_Usuario(w,n));
				
			double latencia = (numero_de_saltos)*(calcular_Retardo_Propagacion_UAV_2_UAV()+calcular_Retardo_Transmision_UAV_2_UAV()) + 2*calcular_latencia_UAV_2_Usuario(w,n);
			getMapa_de_latencias().put(w.getId(), latencia);
			System.out.println("latencia total de la peticion: " + latencia);
			System.out.println(camino_total);
			
		}
		else {
			limpiarListaCambios(cambios);
			System.out.println("		 la peticion no se ha podido ejecutar ");
		}
		return ejecucion_correcta;
	}
	
	/**
	 * @param p petición cuyo retardo vamos a calcular.
	 * @param numero_de_usuarios número de usuario conectados al UAV origen.
	 * @return retardo de transmisión y propagación impuesto por el canal que conecta el dispositivo de usuario con el UAV origen de la petición p
	 */
	public double calcular_latencia_UAV_2_Usuario(peticion p, int numero_de_usuarios) {
		
		//calculamos el tiempo de propagación entre el usuario y el UAV.
		double retardo_de_propagacion=p.getDistancia()/LIGHT_SPEED;
		
		//calculamos el tiempo de transmision entre el usuario y el UAV.
		double antenna_gain=GAIN_AT_ZERO/(p.getDistancia()*p.getDistancia());
		
		double SNR=POWER_TRANSMIT_USER*antenna_gain/(NOISE_POWER_DENSITY + 10*Math.log10(BANDWIDTH));
		
		double velocidad_de_transmision= (BANDWIDTH/numero_de_usuarios)*(Math.log(1 + SNR)/Math.log(2));
		
		double retardo_de_transmision= PACKET_SIZE/velocidad_de_transmision;
		
		return retardo_de_transmision + retardo_de_propagacion;
	}
	
	
	/**
	 * @return retardo de propagación impuesto por el canal que conecta dos UAVs.
	 */
	public double calcular_Retardo_Propagacion_UAV_2_UAV() {
		return DISTANCE_BEETWEEN_UAVS/LIGHT_SPEED;
	}
	
	/**
	 * @return retardo de transmisión impuesto por el canal que conecta dos UAVs.
	 */
	public double calcular_Retardo_Transmision_UAV_2_UAV() {
		
		double antenna_gain=GAIN_AT_ZERO/(DISTANCE_BEETWEEN_UAVS*DISTANCE_BEETWEEN_UAVS);
		
		double SNR=POWER_TRANSMIT_UAV*antenna_gain/(NOISE_POWER_DENSITY + 10*Math.log10(BANDWIDTH));
		
		double velocidad_de_transmision= (BANDWIDTH)*(Math.log(1 + SNR)/Math.log(2));
		
		double retardo_de_transmision= PACKET_SIZE/velocidad_de_transmision;
			
		return retardo_de_transmision;
	}
	
	
}//class
	
	


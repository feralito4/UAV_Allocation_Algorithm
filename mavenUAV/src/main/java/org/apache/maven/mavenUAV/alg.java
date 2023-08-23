package org.apache.maven.mavenUAV;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Comparator.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.alg.connectivity.BiconnectivityInspector;
import org.jgrapht.alg.scoring.BetweennessCentrality;
import org.jgrapht.alg.scoring.ClosenessCentrality;
import org.jgrapht.alg.shortestpath.BidirectionalDijkstraShortestPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.alg.util.VertexDegreeComparator;
import org.jgrapht.graph.DefaultGraphIterables;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

public class alg {

	private Graph<UAV,DefaultWeightedEdge> g;
	private List<peticion> peticiones;
	private Map<String,Integer> mapa_replicacion;
	private double porcentaje_de_replicacion;
	private int saturacion;

	public alg(List<peticion> peticiones, double pdr, int st) {
		this.g = new SimpleWeightedGraph<UAV, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		this.mapa_replicacion=new HashMap<String,Integer>();
		this.peticiones=peticiones;
		this.porcentaje_de_replicacion=pdr;
		this.saturacion=st;
	}
	
	public Graph<UAV,DefaultWeightedEdge> getGraph(){
		return g;
	}
	
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
	
	public int getSaturacion() {
		return saturacion;
	}

	public void setSaturacion(int saturacion) {
		this.saturacion = saturacion;
	} 
	
	public int getMaxNumberOfVNFs() {		
		return (int)(getPorcentaje_de_replicacion()*getNumberOfUAVs());
	}	
	
	public void limpiarListaCambios(List<UAV> cambios) {
		for(UAV u: cambios) {
			for(UAV o :getGraph().vertexSet()) {
				if(u.getId()==o.getId()) {
					o=u;
				}
			}
		}
		cambios.clear();
	}
	
	public void aplicarCambiosUav(UAV current, VNF m, List<UAV> cambios) {
		UAV c= new UAV(current);
		cambios.add(c);
	
		current.addVNF(m);
		current.setCapacidadRAM(current.getCapacidadRAM()-m.getRequerimientoRAM());
		
		System.out.println("	se instala el microservicio " + m.getNombre() + " en el UAV con id " + current.getId());
		System.out.println("	la bateria del auv " + current.getId() + " ha sido reducida a " + current.getCapacidadRAM());
		
		// para llegar a este metodo ya hemos tenido que comprobar que no se ha alcanzado el numero de replicacion 
		
		if(!getMapa_replicacion().containsKey(m.getNombre())) {
			getMapa_replicacion().put(m.getNombre(),1);
			System.out.println("El numero de microservicios de " + m.getNombre() + " es " + getMapa_replicacion().get(m.getNombre()).intValue());
		}
		else{
			getMapa_replicacion().put(m.getNombre(),getMapa_replicacion().get(m.getNombre()).intValue()+1);
			System.out.println("El numero de microservicios de " + m.getNombre() + " es " + getMapa_replicacion().get(m.getNombre()).intValue());
		}
		
		
	}
	
	private boolean comprobarSaturación(UAV current) {
		return current.getVNF_List().size()<getSaturacion();
	}
	
	private boolean comprobarReplicacionVNF(VNF m, UAV u) {
		boolean correct=true;
		
		//En este metodo solo vamos a comprobar si el microservicio ha alcanzado el numero de replicaciones.
		
		if (u.checkVNF(m)) {
			System.out.println("el microservicio ya existe en el UAV"); //tenemos este if aki por el simple echo de tener el mensajede otra forma se complica ponerlo en el metodo principal
			correct=false;
		}else
			if(getMapa_replicacion().get(m.getNombre())!=null)
				if (getMapa_replicacion().get(m.getNombre()) >= getMaxNumberOfVNFs()) {
					System.out.println("se ha llegado al limite de instancias instaladas del microservicio " + m.getNombre());
					correct=false;
				}
		
		return correct;
	}
	
	private boolean comprobarReplicacionVNF(VNF m) {
		boolean correct=true;
		
		if(getMapa_replicacion().get(m.getNombre())!=null)
			if (getMapa_replicacion().get(m.getNombre()) >= getMaxNumberOfVNFs()) {
				System.out.println("se ha llegado al limite de instancias instaladas del microservicio " + m.getNombre());
				correct=false;
			}
		
		return correct;
	}
	
	
	
	
	
	
	private UAV encontrarMejorUav(UAV current, VNF m) {
		
		UAV mejorUAV = null;
		float capacidadRamMax=0;
		int grado=0;
		int totalVNF=99;
		
		List<UAV> adyacentes= new ArrayList<UAV>(Graphs.neighborListOf(g, current));
		
		for(UAV a : adyacentes) {
			if(!a.checkVNF(m) && comprobarSaturación(a))
				if(capacidadRamMax<a.getCapacidadRAM() && m.getRequerimientoRAM() <= a.getCapacidadRAM()) {
					capacidadRamMax=a.getCapacidadRAM();				
					grado=getGraph().edgesOf(a).size();
					totalVNF=a.getVNF_List().size();
				
					mejorUAV=a;
				}
				else
					if(capacidadRamMax==a.getCapacidadRAM()) {
						if(totalVNF<a.getVNF_List().size() && m.getRequerimientoRAM() <= a.getCapacidadRAM()) {
							totalVNF=a.getVNF_List().size();
							grado=getGraph().edgesOf(a).size();
							mejorUAV=a;
						}
						else {
							if(totalVNF==a.getVNF_List().size()) {
								if(grado<getGraph().edgesOf(a).size() && m.getRequerimientoRAM() <= a.getCapacidadRAM()) {
									grado=getGraph().edgesOf(a).size();
									mejorUAV=a;		
								}
							}				
						}	
					}
		}//for
		
		return mejorUAV;
	}
	
	
	
	
	
	
	public void VNF_placement_algorithm() {
		
		boolean replicacion_correcta=false;
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
				replicacion_correcta=comprobarReplicacionVNF(m,currentUAV);// 
				if(replicacion_correcta) { 
					if(m.getRequerimientoRAM() <= currentUAV.getCapacidadRAM() && comprobarSaturación(currentUAV)) {
						
						aplicarCambiosUav(currentUAV, m,cambios);
						
					}
					else {
						
						System.out.println("El UAV con id " + currentUAV.getId() +" no tiene recursos para instalar el microservicio " + m.getNombre());
						System.out.println("	buscando el mejor AUV...");
						UAV mejorUAV=encontrarMejorUav(currentUAV,m);
						if(mejorUAV!=null) {
							System.out.println("	mejor UAV encontrado");
							currentUAV=mejorUAV; //esto se puede quitar
								
							aplicarCambiosUav(currentUAV, m,cambios);
								
						}
						else {
								System.out.println("Mejor UAV no encontrado-no se puede realizar la peticion");
								limpiarListaCambios(cambios);
								exit=true; //tambien prodiamos simplemente quitar el exit y continuar aunque las peticiones 

						}
							
						
					}
				}
			}//while
			System.out.println("-----------------------------------------------");
		}//for
		getMapa_replicacion().clear();		
	}
	
	
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
	
	
	
	public void VNF_placement_algorithm_CV(int algoritmo) {
		
		
		Map<UAV,Double> map= null;
		Iterator<UAV> it; 
		UAV currentUAV;
		boolean exit= false;
		List<UAV>cambios= new ArrayList<UAV>();
		
		
		switch(algoritmo) {
		case 1:
			ClosenessCentrality<UAV, DefaultWeightedEdge> cc= new ClosenessCentrality<UAV, DefaultWeightedEdge>(getGraph());
			map=ordenarMapa(cc.getScores());
			break;
		case 2:
			BetweennessCentrality<UAV, DefaultWeightedEdge> bc= new BetweennessCentrality<UAV, DefaultWeightedEdge>(getGraph());
			map=ordenarMapa(bc.getScores());
			break;
		default:
			for(UAV u: getGraph().vertexSet()) {
				map.put(u,(double) getGraph().degreeOf(u) );
			}
			map=ordenarMapa(map);
			break;
		
		}
			
		
		ArrayList<UAV> Lista_UAVs= new  ArrayList<UAV>(map.keySet());
		System.out.println(Lista_UAVs);
		
			for(peticion p : peticiones) {
				for(VNF m : p.getLista_vnf()) {
					it=Lista_UAVs.iterator();
					currentUAV=it.next();
					exit=false;
					if(comprobarReplicacionVNF(m))
						while(it.hasNext() && !exit) {
							if(currentUAV.checkVNF(m) || !comprobarSaturación(currentUAV) || m.getRequerimientoRAM()>currentUAV.getCapacidadRAM() ){
									
								currentUAV=it.next();
								
								if(!it.hasNext() && (!comprobarSaturación(currentUAV) || m.getRequerimientoRAM()>currentUAV.getCapacidadRAM())) {
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
	
	
}//class
	
	


package org.apache.maven.mavenUAV;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Comparator.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.alg.connectivity.BiconnectivityInspector;
import org.jgrapht.alg.shortestpath.BidirectionalDijkstraShortestPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

public class alg {

	private Graph<UAV,DefaultWeightedEdge> g;
	private int totalUAVs;
	
	public alg() {
		this.g = new SimpleWeightedGraph<UAV, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		totalUAVs=0;
	}
	
	public Graph<UAV,DefaultWeightedEdge> getGraph(){
		return g;
	}
	
	public void addUAVvertex(int id,float capacidadRAM, float bateria){
		UAV v = new UAV(id,capacidadRAM,bateria);
		g.addVertex(v);
	}
	
	public void populateGraphDefault(int nUav) {
		for(int i=0;i<nUav;i++) {
			addUAVvertex(i,4096,1000);
		}
	}
	
	public UAV encontrarMejorUav(UAV current, VNF m) {
		
		UAV mejorUAV = null;
		float capacidadRamMax=0;
		int totalVNF=99;
		
		List<UAV> adyacentes= new ArrayList<UAV>(Graphs.neighborListOf(g, current));
		
		for(UAV a : adyacentes) {
			if(capacidadRamMax<a.getCapacidadRAM() && m.getRequerimientoRAM() <= a.getCapacidadRAM() && !a.checkVNF(m)) {
				capacidadRamMax=a.getCapacidadRAM();
				mejorUAV=a;				
			}
			else
				if(capacidadRamMax==a.getCapacidadRAM()) {
					if(totalVNF<a.getVNF_List().size() && m.getRequerimientoRAM() <= a.getCapacidadRAM() && !a.checkVNF(m)) {
						totalVNF=a.getVNF_List().size();
						mejorUAV=a;
					}
					else {
						//aki se supone que irian los siguiente criterios del mejor UAV
					}	
				}
		}//for
		
		return mejorUAV;
	}
	
	
	
	public static void main(String[] args) {

		alg alg= new alg();

		//Creación del grafo y creación de las aristas del grafo.
		
		UAV v0 = new UAV(0,1,1000);
		UAV v1 = new UAV(1,1,1000);
		UAV v2 = new UAV(2,4096,1000);
		UAV v3 = new UAV(3,1,1000);
		UAV v4 = new UAV(4,1,1000);
		UAV v5 = new UAV(5,4096,1000);
		UAV v6 = new UAV(6,4096,1000);
		UAV v7 = new UAV(7,4096,1000);
		UAV v8 = new UAV(8,4096,1000);

		Graph<UAV,DefaultWeightedEdge> aux;
		
		aux=alg.getGraph();
		
		aux.addVertex(v8);
		aux.addVertex(v7);
		aux.addVertex(v6);
		aux.addVertex(v5);
		aux.addVertex(v4);
		aux.addVertex(v3);
		aux.addVertex(v2);
		aux.addVertex(v1);
		aux.addVertex(v0);
		
		aux.addEdge(v0,v1);
		aux.addEdge(v0,v3); 
		aux.addEdge(v0,v4);
		
		aux.addEdge(v1,v2);
		aux.addEdge(v1,v3);
		aux.addEdge(v1,v4);
		aux.addEdge(v1,v5);
		
		aux.addEdge(v2,v4);
		aux.addEdge(v2,v5);
		
		aux.addEdge(v3,v4);
		aux.addEdge(v3,v6);
		aux.addEdge(v3,v7);
		
		aux.addEdge(v4,v5);
		aux.addEdge(v4,v6);
		aux.addEdge(v4,v7);
		aux.addEdge(v4,v8);
		
		aux.addEdge(v5,v7);
		aux.addEdge(v5,v8);
		
		aux.addEdge(v6,v7);
		
		aux.addEdge(v7,v8);
		
		//comprobamos si el grafo es conexo
		
		BiconnectivityInspector<UAV, DefaultWeightedEdge> b = new BiconnectivityInspector<UAV, DefaultWeightedEdge>(aux);	
		System.out.println(b.isConnected());
		
		//Creación de VNFs
		
		VNF m1 = new VNF("MonitorizaciónECG", 393, 0);
		VNF m2 = new VNF("Monitorización de presión de sangre", 393, 0);
		VNF m3 = new VNF("Compresión de información", 136, 0);		
		VNF m4 = new VNF("Encriptación de datos", 79, 0);
		
		//Creación de peticiones(crearemos 3 solo)
		
		peticion w1= new peticion(v0, v6);
		w1.addVNF(m1);
		w1.addVNF(m4);
		w1.addVNF(m3);
        
		peticion w2= new peticion(v0, v8);
		w2.addVNF(m1);
		w2.addVNF(m2);
		w2.addVNF(m4);
		w2.addVNF(m3);
		
		peticion w3= new peticion(v2, v7);
		w3.addVNF(m2);
		w3.addVNF(m4);
		w3.addVNF(m3);

		peticion w4= new peticion(v5, v0);
		w4.addVNF(m2);
		w4.addVNF(m4);
		
		List<peticion> peticiones= new ArrayList<peticion>();
		peticiones.add(w1);
		//peticiones.add(w2);
		//peticiones.add(w3);
		//peticiones.add(w4);

		
		
		List<UAV>cambios= new ArrayList<UAV>();
		
		for(peticion w : peticiones) {
			System.out.println("Peticion con origen en el UAV con ID " + w.getOrigen());
			UAV currentUAV= w.getOrigen();
			
			Iterator<VNF> it = w.getLista_vnf().iterator(); 
			boolean exit=false;
			
			while(it.hasNext() && !exit){
				VNF m=it.next();
				if(m.getRequerimientoRAM() <= currentUAV.getCapacidadRAM() && !currentUAV.checkVNF(m)) {
					
					UAV c= new UAV(currentUAV);
					cambios.add(c);
					
					currentUAV.addVNF(m);
					currentUAV.setCapacidadRAM(currentUAV.getCapacidadRAM()-m.getRequerimientoRAM());
					System.out.println("	se instala el microservicio " + m.getNombre() + " en el UAV con id " + currentUAV.getId());
					System.out.println("	la bateria del auv " + currentUAV.getId() + " ha sido reducida a " + currentUAV.getCapacidadRAM());
				}
				else {
					if(m.getRequerimientoRAM() > currentUAV.getCapacidadRAM()) {
						System.out.println("El UAV con id " + currentUAV.getId() +" no tiene recursos para instalar el microservicio " + m.getNombre());
						System.out.println("	buscando el mejor AUV...");
						UAV mejorUAV=alg.encontrarMejorUav(currentUAV,m);
						if(mejorUAV!=null) {
							System.out.println("	mejor UAV encontrado");
							currentUAV=mejorUAV; 
							
							UAV c= new UAV(currentUAV);
							cambios.add(c);
							
							currentUAV.addVNF(m);
							currentUAV.setCapacidadRAM(currentUAV.getCapacidadRAM()-m.getRequerimientoRAM());
							
							System.out.println("	se instala el microservicio " + m.getNombre() + " en el UAV con id " + currentUAV.getId());
							System.out.println("	la bateria del auv " + currentUAV.getId() + " ha sido reducida a " + currentUAV.getCapacidadRAM());
						
						}
						else {
							System.out.println("Mejor UAV no encontrado-no se puede realizar la peticion");
							//aki se deberia restaurar el grafo// esto es una mierda pero bueno
							for(UAV c: cambios) {
								for(UAV o :alg.getGraph().vertexSet()) {
									if(c.getId()==o.getId()) {
										o=c;
									}
								}
							}
							exit=true;
							cambios.clear();

						}
							
					}
					else
						System.out.println("el microservicio ya existe en el UAV");
				}
				
			}
			
			
			System.out.println("-----------------------------------------------");
		}
		
		
		
		
		
		
		
	}//main
	
	
	
}//class
	
	


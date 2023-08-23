package org.apache.maven.mavenUAV;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jgrapht.Graph;
import org.jgrapht.alg.connectivity.BiconnectivityInspector;
import org.jgrapht.alg.scoring.BetweennessCentrality;
import org.jgrapht.graph.DefaultWeightedEdge;

/**
 * Hello world!
 *
 */
public class Client 
{
	
	private alg alg;
	
    public Client() {
    	initData();
    }
	
	
    public void initData() {
    	
		//Creación del grafo y creación de las aristas del grafo.
		
		UAV v0 = new UAV(0,4096,1000);
		UAV v1 = new UAV(1,4096,1000);
		UAV v2 = new UAV(2,4096,1000);
		UAV v3 = new UAV(3,4096,1000);
		UAV v4 = new UAV(4,4096,1000);
		UAV v5 = new UAV(5,4096,1000);
		UAV v6 = new UAV(6,4096,1000);
		UAV v7 = new UAV(7,4096,1000);
		UAV v8 = new UAV(8,4096,1000);
		
		//Creación de VNFs
		
		VNF m1 = new VNF("Monitorización ECG", 393, 0);
		VNF m2 = new VNF("Monitorización de presión de sangre", 393, 0);
		VNF m3 = new VNF("Compresión de información", 136, 0);		
		VNF m4 = new VNF("Encriptación de datos", 79, 0);
				
		//Creación de peticiones(crearemos 3 solo)
				
		peticion w1= new peticion(v0);
		w1.addVNF(m1);
		w1.addVNF(m4);
		w1.addVNF(m3);
		        
		peticion w2= new peticion(v0);
		w2.addVNF(m1);
		w2.addVNF(m2);
		w2.addVNF(m4);
		w2.addVNF(m3);
				
		peticion w3= new peticion(v2);
		w3.addVNF(m2);
		w3.addVNF(m4);
		w3.addVNF(m3);

		peticion w4= new peticion(v5);
		w4.addVNF(m2);
		w4.addVNF(m4);

		List<peticion> peticiones= new ArrayList<peticion>();
		peticiones.add(w1);
		peticiones.add(w2);
		peticiones.add(w3);
		peticiones.add(w4);
		
    	this.alg= new alg(peticiones, 0.25 , 3 );
		
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
		
		//1,2 o default
		//alg.VNF_placement_algorithm_CV(1);	
		
		System.out.println("***************************");
		
		alg.VNF_placement_algorithm();	
		
		
    	
    }
	
	public static void main(String[] args) {
		
		Client c= new Client();
		
		
		
	}//main
	
	
}//class

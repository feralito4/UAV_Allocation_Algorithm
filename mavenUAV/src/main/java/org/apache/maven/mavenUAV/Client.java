package org.apache.maven.mavenUAV;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;

import com.aspose.cells.CellArea;
import com.aspose.cells.DataSorter;
import com.aspose.cells.SortOrder;

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
    	
    	//parametros para modificar los algoritmos de colocación
    	boolean escenario= false; // false para escenario pequeño y true para escenario grande
    	double porcentaje_de_replicacion=0.25; //0.25 0.5 1
    	int algoritmo=0; //0 para Node Degree, 1 para Tercera Version, 2 para saturación controlada
    	int numero_max_ms_uav=1; // 1,2,3
    	int numero_de_usuarios_conectados_TP=45; //número de usuarios conectados a la topología pequeña
    	int numero_de_usuarios_conectados_TG=300; //número de usuarios conectados a la topología grande
    	
		//Creación del grafo y creación de las aristas del grafo.
				
		//Creación de VNFs
		
		VNF m1 = new VNF("Monitorización ECG", 393, 24.4);
		VNF m2 = new VNF("Monitorización de presión de sangre", 393, 24.4);
		VNF m3 = new VNF("Compresión de información", 136, 9.9);		
		VNF m4 = new VNF("Encriptación de datos", 79, 6.1);
		VNF m5 = new VNF("Codificación de video", 1500, 33.3);
				
		List<peticion> peticiones= new ArrayList<peticion>();
		
    	this.alg= new alg(peticiones, porcentaje_de_replicacion);
		
		Graph<UAV,DefaultWeightedEdge> aux=alg.getGraph();
				
		UAV origen=null;
		int distancia;
		int z=1;
		int numero_de_usuarios=0;
		Map<UAV,Integer> mapa_de_usuarios_conectados = new HashMap<UAV,Integer>();
		
		try {
			
			if(escenario) {
				
				int [][] matriz_escenario_grande= {{0, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, 
						{1, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, 
						{0, 1, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, 
						{0, 1, 1, 0, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, 
						{1, 1, 1, 1, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, 
						{1, 1, 0, 0, 1, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, 
						{0, 0, 0, 0, 1, 1, 0, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, 
						{0, 0, 0, 1, 1, 1, 1, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, 
						{0, 0, 0, 1, 1, 0, 0, 1, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0}, 
						{0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0}, 
						{0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, 
						{0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 1, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, 
						{0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, 
						{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, 
						{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, 
						{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, 
						{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, 
						{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 1, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, 
						{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, 
						{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 1, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, 
						{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, 
						{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, 
						{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 1, 0, 0, 1, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, 
						{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, 
						{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, 
						{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 1, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, 
						{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, 
						{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, 
						{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, 
						{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, 
						{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 1, 1, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, 
						{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 1, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, 
						{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, 
						{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 1, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, 
						{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 1, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, 
						{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 1, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, 
						{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 1, 0, 1, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, 
						{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 1, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, 
						{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 1, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0}, 
						{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 1, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, 
						{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1}, 
						{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, 
						{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, 
						{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, 
						{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0}, 
						{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 1, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 1, 1}, 
						{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0}, 
						{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 1, 0, 1, 1, 0, 0, 0, 0, 0}, 
						{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 1, 1, 1, 1, 0, 0, 0, 0}, 
						{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 1, 0, 0, 1, 1, 0, 0, 0, 1}, 
						{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 1, 0, 0, 0, 0, 0}, 
						{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 1, 0, 0, 0, 0}, 
						{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 1, 0, 0, 0, 0, 0}, 
						{0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1}, 
						{0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 1}, 
						{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 1}, 
						{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 1, 1, 0}};
				
				
				Set<UAV> UAV_centrales = new HashSet<UAV>();
				Set<UAV> UAV_carreteras  = new HashSet<UAV>();
				List<UAV> l = new ArrayList<UAV>();
				int n=57;
				
				for (int i = 0; i < n; i++) {
					UAV u=new UAV(i,4096,1000);
		            l.add(u);
		            aux.addVertex(u);
		        }
				
				for (int i = 0; i < n; i++) {
		            for (int j = i + 1; j < n; j++) {
		                if (matriz_escenario_grande[i][j] == 1) {
		                    aux.addEdge(l.get(i), l.get(j));
		                }
		            }
		        }
					
					
				generateInput(1, 2, 2,numero_de_usuarios_conectados_TG);
				
				FileInputStream file = new FileInputStream(new File("C:\\Users\\feral\\git\\UAVAArepo\\mavenUAV\\peticiones_generadas.xlsx")); //abrimos un archivo
				Workbook workbook = new XSSFWorkbook(file); //abrimos el workbook
				DataFormatter dataFormatter = new DataFormatter(); 
				
				Sheet sheet= workbook.getSheetAt(0);//nos colocamos en la primera hoja
				Iterator<Row> itrow = sheet.iterator();
				itrow.next(); //pasamos la fila con los nombres de los parametros
				
				while(itrow.hasNext()) {
					//iteramos sobre las filas
					Row peticion = itrow.next();
					List<VNF> lista_microservicios= new ArrayList<VNF>();
					
					switch((int)peticion.getCell(0).getNumericCellValue()) {
					case 0:
						lista_microservicios.add(m1);
						lista_microservicios.add(m3);
						lista_microservicios.add(m4);
						break;
					case 1:
						lista_microservicios.add(m2);
						lista_microservicios.add(m4);
						break;
					case 2:
						lista_microservicios.add(m5);
						
					}
					
					distancia=(int)peticion.getCell(1).getNumericCellValue();

					for(UAV u: alg.getGraph().vertexSet()) {
						if(u.getId()==peticion.getCell(2).getNumericCellValue())
							origen=u;
					}
						
					peticion p= new peticion(z,"usuario "+z,origen,lista_microservicios,distancia);
					System.out.println(p);
					peticiones.add(p);
					z++;
					
				}//while
				
				
			}
			else {
				
				UAV v0 = new UAV(0,4096,1000);
				UAV v1 = new UAV(1,4096,1000);
				UAV v2 = new UAV(2,4096,1000);
				UAV v3 = new UAV(3,4096,1000);
				UAV v4 = new UAV(4,4096,1000);
				UAV v5 = new UAV(5,4096,1000);
				UAV v6 = new UAV(6,4096,1000);
				UAV v7 = new UAV(7,4096,1000);
				UAV v8 = new UAV(8,4096,1000);
				
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
				
				generateInput(1, 1, 2,numero_de_usuarios_conectados_TP);

					
				FileInputStream file = new FileInputStream(new File("C:\\Users\\feral\\git\\UAVAArepo\\mavenUAV\\peticiones_generadas.xlsx")); //abrimos un archivo
				Workbook workbook = new XSSFWorkbook(file); //abrimos el workbook
				DataFormatter dataFormatter = new DataFormatter(); 
				
				Sheet sheet= workbook.getSheetAt(0);//nos colocamos en la primera hoja
				System.out.println(sheet.getSheetName());
				Iterator<Row> itrow = sheet.iterator();
				itrow.next();//pasamos la fila con los nombres de los parametros
				while(itrow.hasNext()) {
					//iteramos sobre las filas
					Row peticion = itrow.next();
					//obtenemos el UAV especificado en la peticion					
					int indice=Integer.valueOf(dataFormatter.formatCellValue(peticion.getCell(2)));
					for(UAV u: alg.getGraph().vertexSet()) {
						if(indice==u.getId())
							origen=u;	
					}
					//obtenemos el tipo de peticion y creamos una lista de microservicios
					List<VNF> lista_microservicios= new ArrayList<VNF>();
									
					switch((int)peticion.getCell(0).getNumericCellValue()) {
					case 0:
						lista_microservicios.add(m1);
						lista_microservicios.add(m3);
						lista_microservicios.add(m4);
						break;
					case 1:
						lista_microservicios.add(m2);
						lista_microservicios.add(m4);
						break;
					case 3:
						lista_microservicios.add(m5);
					}
					
					//obtenemos la distancia
					distancia=(int)peticion.getCell(1).getNumericCellValue();
					
					//creamos la peticion
									
					peticion p= new peticion(z, "usuario "+z,origen,lista_microservicios,distancia);
					System.out.println(p);
					peticiones.add(p);
					z++;
				}
				}//else
		}//try
		catch(Exception e) {
			e.printStackTrace();
		}
		
		for(peticion p: peticiones) {
			if(mapa_de_usuarios_conectados.containsKey(p.getOrigen())) {
				int valor=mapa_de_usuarios_conectados.get(p.getOrigen());
				mapa_de_usuarios_conectados.put(p.getOrigen(), valor+1);	
			}
			else
				mapa_de_usuarios_conectados.put(p.getOrigen(), 1);
		}
		
		alg.setNumero_de_usuarios(numero_de_usuarios);
		alg.setPeticiones(peticiones);
		alg.setMapa_de_usuarios_conectados(mapa_de_usuarios_conectados);

				
		switch (algoritmo) { 
	    case 0:
	    	alg.VNF_placement_algorithm_Version_Propiedades_Grafo(0,false);
	     break;
	    case 1:
	    	alg.VNF_placement_algorithm_TV(false);
	     break; 
	    case 2:
	    	alg.VNF_placement_algorithm_Version_Saturacion(numero_max_ms_uav);
	     break;
	    default:
	  }
		
		
		System.out.println("------------------------------------------------------------");
		for(UAV u : alg.getGraph().vertexSet()) {
			System.out.println(u.getId());
			for( VNF v : u.getVNF_List()) {
				System.out.println(v.getNombre());
			}
			System.out.println("------------------------------------------------------------");
		}
		
		
		double latencia_media=0;
		int saltos_media=0;
		
		try (Workbook workbook = new XSSFWorkbook()) {
			int num_filas=0;
			
            Sheet pagina_latencias = workbook.createSheet("latencias");
            Row fila_nombres=pagina_latencias.createRow(num_filas);
            Cell nombre_peticion= fila_nombres.createCell(0);
            nombre_peticion.setCellValue("id peticion");
            Cell latencia_peticion= fila_nombres.createCell(1);
            latencia_peticion.setCellValue("latencia");
            Cell saltos_peticion= fila_nombres.createCell(2);
            saltos_peticion.setCellValue("numero de saltos");
            
            Row row;
            for(Map.Entry<Integer,Double> entry : alg.getMapa_de_latencias().entrySet()) {
            	num_filas++;
            	row=pagina_latencias.createRow(num_filas);
            	Cell cell_id_peticion= row.createCell(0);
            	cell_id_peticion.setCellValue(entry.getKey());
            	Cell cell_latencia= row.createCell(1);
            	cell_latencia.setCellValue(entry.getValue());
            	
            	latencia_media=latencia_media+entry.getValue();
            	
            }
            
            num_filas=0;
            for(Map.Entry<Integer,Integer> entry : alg.getMapa_de_saltos().entrySet()) {
            	num_filas++;
            	row=pagina_latencias.getRow(num_filas);
            	Cell cell_saltos= row.createCell(2);
            	cell_saltos.setCellValue(entry.getValue());
            	
            	saltos_media=saltos_media+entry.getValue();

            }
            
            latencia_media=latencia_media/alg.getMapa_de_latencias().size();
            saltos_media=saltos_media/alg.getMapa_de_saltos().size();
            
            System.out.println(latencia_media);
            System.out.println(saltos_media);
            
            try (FileOutputStream fileOut = new FileOutputStream("Resultados_peticiones.xlsx")) {
                workbook.write(fileOut);
            }
            
            
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	    }
      
    
    /**
	 * Algoritmo de creación de peticiones basado en áreas
	 */
    public void generateInput(int numero_de_centros,int radio_del_centro,int tipos_de_peticion,int numero_de_usuarios) {
    
		Row row;
    	int max_peticiones;
    	int peticiones_centrales;
    	int peticiones_fuera;
    	int num_filas=0;
    	UAV uav_aux=null;
    	Set<UAV> uavs_objetivo= new HashSet<UAV>();
    	Set<UAV> uavs_externos= new HashSet<UAV>();
    	Random rand= new Random();
    	
    	max_peticiones=numero_de_usuarios/(numero_de_centros * tipos_de_peticion);//max peticiones de un tipo por centro
    	
  
    	peticiones_centrales=(int) (max_peticiones*0.8);
		peticiones_fuera=max_peticiones-peticiones_centrales;
		
    	Set<UAV> centros_elegibles= new HashSet<UAV>(alg.getGraph().vertexSet());
    	Set<UAV> set_aux=new HashSet<UAV>();
    	
		try (Workbook workbook = new XSSFWorkbook()) {
    		
    		Sheet pagina_1 = workbook.createSheet("Hoja 1");
    		Row fila_nombres=pagina_1.createRow(num_filas);
            Cell tipo_peticion= fila_nombres.createCell(0);
            tipo_peticion.setCellValue("tipo peticion");
            Cell distancia_peticion= fila_nombres.createCell(1);
            distancia_peticion.setCellValue("distancia");
            Cell usuario_peticion= fila_nombres.createCell(2);
            usuario_peticion.setCellValue("usuario origen");
    		
		
            for(int i=0;i<tipos_de_peticion;i++) {
    		
            	for(int j=0;j<numero_de_centros;j++) {
    			
            		uavs_objetivo= obtenerUavsObjetivo(radio_del_centro,centros_elegibles);
            		
            		for(int k=0;k<peticiones_centrales;k++) {
					
            			uav_aux=obtenerUavAleatorio(uavs_objetivo);
            			num_filas++;
					
            			row=pagina_1.createRow(num_filas);
            			Cell cell_tipo_peticion= row.createCell(0);
            			cell_tipo_peticion.setCellValue(i);
            			Cell cell_distancia= row.createCell(1);
            			cell_distancia.setCellValue(100);
            			Cell cell_usuario_origen= row.createCell(2);
            			cell_usuario_origen.setCellValue(uav_aux.getId());
            			Cell cell_suffle= row.createCell(3);
            			cell_suffle.setCellValue(rand.nextInt());
	            	
            		}
            		
            		
            		uavs_externos=obtenerUavsExternos(uavs_objetivo);
    				
    				for(int l=0;l<peticiones_fuera;l++){
    					
    					uav_aux=obtenerUavAleatorio(uavs_externos);
    					num_filas++;
    					
    	            	row=pagina_1.createRow(num_filas);
    	            	Cell cell_tipo_peticion= row.createCell(0);
    	            	cell_tipo_peticion.setCellValue(i);
    	            	Cell cell_distancia= row.createCell(1);
    	            	cell_distancia.setCellValue(100);
    	            	Cell cell_usuario_origen= row.createCell(2);
    	            	cell_usuario_origen.setCellValue(uav_aux.getId());
    	            	Cell cell_suffle= row.createCell(3);
            			cell_suffle.setCellValue(rand.nextInt());
    				}
    				
    				set_aux.addAll(uavs_externos);
    				centros_elegibles=set_aux;
            		
            	}
    			
            }
            
        	//to do
        	if(numero_de_usuarios==max_peticiones*(tipos_de_peticion*numero_de_centros)+1) {
        		uav_aux=obtenerUavAleatorio(uavs_externos);
				num_filas++;
				
            	row=pagina_1.createRow(num_filas);
            	Cell cell_tipo_peticion= row.createCell(0);
            	cell_tipo_peticion.setCellValue(tipos_de_peticion-1);
            	Cell cell_distancia= row.createCell(1);
            	cell_distancia.setCellValue(100);
            	Cell cell_usuario_origen= row.createCell(2);
            	cell_usuario_origen.setCellValue(uav_aux.getId());
            	Cell cell_suffle= row.createCell(3);
    			cell_suffle.setCellValue(rand.nextInt());
        	}
            
        	
            try (FileOutputStream fileOut = new FileOutputStream("peticiones_generadas.xlsx")) {
				workbook.write(fileOut);
				workbook.close();	
	        }
    		
    	} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
		try {
			com.aspose.cells.Workbook w=new com.aspose.cells.Workbook("peticiones_generadas.xlsx");
			DataSorter sorter = w.getDataSorter();

			sorter.setOrder1(SortOrder.ASCENDING); 
			sorter.setKey1(3);
			
			CellArea ca = new CellArea();

			ca.StartRow = 1;
			ca.StartColumn = 0;
			ca.EndRow = numero_de_usuarios+1;
			ca.EndColumn = 3;
			
			sorter.sort(w.getWorksheets().get(0).getCells(), ca);

			// Save the excel file.
			w.save("peticiones_generadas.xlsx");

			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		
    
    }
    
    
    /**
	 * @param s Set del cual se va a obtener un elemento aleatorio
	 * @return UAV elegido aleatoriamente del Set s
	 */
    public UAV obtenerUavAleatorio(Set<UAV> s) {    	
    	if (s == null || s.isEmpty()) {
    		s=this.alg.getGraph().vertexSet();
    	}
    	int randomIndex = new Random().nextInt(s.size());
    	int i = 0;
    	for (UAV element : s) {
    		if (i == randomIndex) {
    			return element;
    	    }
    	    i++;
    	}
    	throw new IllegalStateException("Something went wrong while picking a random element.");
    }
    
    /**
     * @param radio_del_centro radio del área en el que se van a generar las peticiones.
	 * @param posiblesUAVs Set que contiene todos los posibles UAVs que pueden actuar como centros de un área generada
	 * @return set de UAVs que se encuentren a una distancia menor o igual que radio_del_centro del UAV centro elegido de entre los UAVs del conjunto posibles UAVs
	 */
    public Set<UAV> obtenerUavsObjetivo(int radio_del_centro, Set<UAV> posiblesUAVs) {
    	UAV centro = null;
    	Set<UAV> uavs_objetivo= new HashSet<UAV>();
    	Set<UAV> set_aux= new HashSet<UAV>();
    	int i=0;
    	
    	Random rand = new Random();
		int id_uav_centro=rand.nextInt(posiblesUAVs.size());
				
		for(UAV u : posiblesUAVs) {
			if(i==id_uav_centro) 
				centro=u;
			i++;
		}
		
		uavs_objetivo.addAll(Graphs.neighborSetOf(alg.getGraph(), centro));
		uavs_objetivo.add(centro); //esto lo he añadido
		
		if(radio_del_centro!=1)
			for(int j=1;j<radio_del_centro;j++) {
				for(UAV u : uavs_objetivo) {
					set_aux.addAll(Graphs.neighborSetOf(alg.getGraph(), u));
				}
				uavs_objetivo.addAll(set_aux);
			}		
		System.out.print(centro);
		System.out.println(uavs_objetivo);
		
		
		return uavs_objetivo;
    }
    
    /**
	 * @param s Set de UAVs pertenecientes a un área generada
	 * @return Set que contiene los UAVs del grafo que no están en s (UAVs complementarios a s)
	 */
    public Set<UAV> obtenerUavsExternos(Set<UAV> centrales){
    	Set<UAV> uavs_externos= new HashSet<UAV>();
    	for(UAV u: alg.getGraph().vertexSet()) {
    		if(!centrales.contains(u)) {
    			uavs_externos.add(u);
    		}
    	}
    	
    	if(uavs_externos.isEmpty())
    		uavs_externos=centrales;
    	
    	
    	return uavs_externos;
    }
    
    
    
	public static void main(String[] args) {
		
		Client c= new Client();
		 
		
	}//main
	
	
}//class

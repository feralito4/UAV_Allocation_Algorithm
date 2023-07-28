package org.apache.maven.mavenUAV;

import java.util.*;

public class peticion {

	private UAV origen;
	private UAV destino;
	private List<VNF> lista_vnf;
	
	
	public peticion(UAV origen, UAV destino) {
		this.origen = origen;
		this.destino =destino;
		this.lista_vnf = new ArrayList<VNF>();
	}

	
	public UAV getOrigen() {
		return origen;
	}
	
	public void setOrigen(UAV origen) {
		this.origen = origen;
	}
	
	public UAV getDestino() {
		return destino;
	}
	
	public void setDestino(UAV destino) {
		this.destino = destino;
	}
	
	public List<VNF> getLista_vnf() {
		return lista_vnf;
	}
	
	public void setLista_vnf(List<VNF> lista_vnf) {
		this.lista_vnf = lista_vnf;
	}
	
	public void addVNF(VNF v) {
		this.lista_vnf.add(v);
	}
	
	public int getNumeroVNFs() {
		return this.lista_vnf.size();
	}
	
	public VNF getVNF(int index) {
		return this.lista_vnf.get(index);
	}
	
	
}

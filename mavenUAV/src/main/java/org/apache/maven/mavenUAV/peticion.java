package org.apache.maven.mavenUAV;

import java.util.*;

public class peticion {

	private int id;
	private String usuario;
	private UAV origen;
	private List<VNF> lista_vnf;
	private int distancia;
	
	public peticion(UAV origen) {
		this.origen = origen;
		this.lista_vnf = new ArrayList<VNF>();
	}

	public peticion(int id, String usuario, UAV origen, List<VNF> lista_vnf, int distancia) {
		this.id=id;
		this.usuario=usuario;
		this.origen = origen;
		this.lista_vnf = lista_vnf;
		this.distancia= distancia;
	}
	
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public UAV getOrigen() {
		return origen;
	}
	
	public void setOrigen(UAV origen) {
		this.origen = origen;
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
	
	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public int getDistancia() {
		return distancia;
	}

	public void setDistancia(int distancia) {
		this.distancia = distancia;
	}
	
	@Override
	public String toString() {
		String s="la peticion solicitada por " + getUsuario() + " que tiene como origen en el UAV " + origen.getId() + " a una distancia de " + getDistancia()+ "\n";
		String s2="los microservicios solicitados son los siguientes: \n";
		String s3="";
		for(VNF v: lista_vnf) {
			s3=s3 + v.getNombre() +"\n";
		}
		return s+s2+s3;
		
	}

	
	
}

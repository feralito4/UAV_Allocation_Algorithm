package org.apache.maven.mavenUAV;
import java.util.ArrayList;
import java.util.List;

public class UAV{
	
	private int id;
	private float capacidadRAM; //MB
	private float bateria; //w/h
	private List<VNF> VNFs;
	
	public UAV() {
		this.id=999;
		this.capacidadRAM=0;
		this.bateria=0;	
		this.VNFs = new ArrayList<VNF>();
	}  
	
	
	public UAV(int id,float capacidadRAM, float bateria) {
		this.id=id;
		this.capacidadRAM=capacidadRAM;
		this.bateria=bateria;	
		this.VNFs = new ArrayList<VNF>();
	}  
	
	public UAV(UAV u) {
		this.copy(u);
	}
	
	public float getCapacidadRAM() {
		return capacidadRAM;
	}

	public void setCapacidadRAM(float capacidadRAM) {
		this.capacidadRAM = capacidadRAM;
	}

	public float getBateria() {
		return bateria;
	}

	public void setBateria(float bateria) {
		this.bateria = bateria;
	}
	
	public List<VNF>  getVNF_List() {
		return VNFs;
	}
	
	public void setVNF_List(List<VNF> l ) {
		this.VNFs=l;
	}
	
	public void addVNF(VNF v) {
		VNFs.add(v);
	}
	
	public void getVNF(int i) {
		VNFs.get(i);
	}
	
	public int getId() {
		return id;
	}
	
	public boolean checkVNF(VNF m) {
		return VNFs.contains(m);
	}
	
	public void copy(UAV u) {
		setBateria(u.getBateria());
		setCapacidadRAM(u.getCapacidadRAM());
		List<VNF> copy = new ArrayList<VNF>(u.getVNF_List());
		setVNF_List(copy);
	}
	
	@Override
	public String toString() {
		return Integer.toString(id);
	}
	
}

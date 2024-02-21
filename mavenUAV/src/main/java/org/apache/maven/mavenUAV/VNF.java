package org.apache.maven.mavenUAV;

public class VNF {
	
	private float requerimientoRAM; //MB
	private double requerimientoBateria; //gigaciclos
	private String nombre;
	
	
	public VNF(String nombre, float requerimientoRAM, double d){
		this.requerimientoRAM=requerimientoRAM;
		this.requerimientoBateria=d;
		this.nombre=nombre;
	}
	
	public float getRequerimientoRAM() {
		return requerimientoRAM;
	}

	public void setRequerimientoRAM(float requerimientoRAM) {
		this.requerimientoRAM = requerimientoRAM;
	}

	public double getRequerimientoBateria() {
		return requerimientoBateria;
	}

	public void setRequerimientoBateria(float requerimientoBateria) {
		this.requerimientoBateria = requerimientoBateria;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
		
}

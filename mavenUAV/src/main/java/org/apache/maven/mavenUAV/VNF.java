package org.apache.maven.mavenUAV;

public class VNF {
	
	private float requerimientoRAM; //MB
	private float requerimientoBateria; //w/h posiblemente lo obviemos
	private String nombre;
	
	
	public VNF(String nombre, float requerimientoRAM, float requerimientoBateria){
		this.requerimientoRAM=requerimientoRAM;
		this.requerimientoBateria=requerimientoBateria;
		this.nombre=nombre;
	}
	
	public float getRequerimientoRAM() {
		return requerimientoRAM;
	}

	public void setRequerimientoRAM(float requerimientoRAM) {
		this.requerimientoRAM = requerimientoRAM;
	}

	public float getRequerimientoBateria() {
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

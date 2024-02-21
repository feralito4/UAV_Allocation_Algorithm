package org.apache.maven.mavenUAV;
import java.util.ArrayList;
import java.util.List;

public class UAV{
	
	private int id;
	private float capacidadRAM; //MB
	private double bateria; //w/h
	private List<VNF> VNFs;
	
	public static int voltaje_nominal= 3; //voltios
	public static int capacidad_nominal=1000; // vatios-hora
	public static double ratio_potencia_microservicio= 3.5; // watios partido de segundo
	public static double ratio_potencia_transmision_propagacion=0.0002;
	public static double frecuencia_procesador= 1; //GHz
	
	/*
	 * Constructor genérico para UAV vacío
	 * */
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
	
	/**
	 * Constructor por copia
	 * @param u UAV a copiar
	 * */
	public UAV(UAV u) {
		this.copy(u);
	}
	
	public float getCapacidadRAM() {
		return capacidadRAM;
	}

	public void setCapacidadRAM(float capacidadRAM) {
		this.capacidadRAM = capacidadRAM;
	}

	public double getBateria() {
		return bateria;
	}

	public void setBateria(double d) {
		this.bateria = d;
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
	
	
	/**
	 * Comprueba si el microservicio m ya se encuentra instalado en el UAV
	 * @param m microservicio a comprobar
	 * @return True si m ya se encuentra en la lista de microservicios. False en el caso contrario 
	 * */
	public boolean checkVNF(VNF m) {
		return VNFs.contains(m);
	}
	
	/**
	 * Calcula el SOC (State Of Charge) del UAV después de ejecutar/instalar un microservicio. Este cálculo se realiza suponiendo una curva de descarga ideal.
	 * @param m microservicio ejecutado/instalado
	 * @return capacidad restante del UAV despues de ejecutar/instalar m
	 * */
	public double calcularSOC_peticion(VNF m) {
		double soc;
		double soc_anterior;
		double corriente_de_carga;
		
		soc_anterior=bateria/capacidad_nominal*100; //conseguimos el SOC en porcentaje a partir de la capicidad actual y la capacidad nominal
		corriente_de_carga=(double) (m.getRequerimientoBateria()*ratio_potencia_microservicio)/(voltaje_nominal);
		//obtenemos la corriente de carga la cual hemos hayado dividiendo los watios que usa un microservicio para ejecutarse y el voltaje nominal supuesto
		
		soc=(double) (soc_anterior+(-corriente_de_carga/capacidad_nominal)*(m.getRequerimientoBateria()/3600)*100);  //obtenemos el nuevo SOC en porcentaje
		
		return soc*capacidad_nominal/100;
	}

	/**
	 * Calcula el SOC (State Of Charge) del UAV después de propagar y transmitir datos. Este cálculo se realiza suponiendo una curva de descarga ideal.
	 * @param retardo_propagacion tiempo necesario para propagar los datos por el medio.
	 * @param retardo_transmision tiempo necesario para introducir los datos en el enlace.
	 * @return capacidad restante del UAV despues de propagar y transmitir datos.
	 * */
	public double calcularSOC_transmision_propagacion(double retardo_propagacion, double retardo_transmision) {
		double soc;
		double soc_anterior;
		double corriente_de_carga;
		
		soc_anterior=bateria/capacidad_nominal*100; //conseguimos el SOC en porcentaje a partir de la capicidad actual y la capacidad nominal
		corriente_de_carga=(double) (retardo_propagacion*ratio_potencia_transmision_propagacion + retardo_transmision*ratio_potencia_transmision_propagacion )/(voltaje_nominal);
		//obtenemos la corriente de carga la cuel hemos hayado dividiendo los watios que usa un microservicio para ejecutarse y el voltaje nominal supuesto
		
		soc=(double) (soc_anterior+(-corriente_de_carga/capacidad_nominal)*(retardo_propagacion+retardo_transmision/3600)*100);  //obtenemos el nuevo SOC en porcentaje
		
		return soc*capacidad_nominal/100;
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

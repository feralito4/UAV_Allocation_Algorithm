package org.apache.maven.mavenUAV;

import java.util.Comparator;

public class ComparatorUav implements Comparator<UAV>{

	@Override
	public int compare(UAV o1, UAV o2) {
		
		if(o1.getCapacidadRAM()>o2.getCapacidadRAM())
			return 1;
		else
			if(o1.getCapacidadRAM()<o2.getCapacidadRAM())
				return -1;
			else
				return 0;
	}

}

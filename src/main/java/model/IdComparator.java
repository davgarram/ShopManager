package model;

import java.util.Comparator;

/*
 * Clase que implementa Comparator<Product> y compara por Id de producto
 * @version 0.0, 02/12/2020
 * @author davgarram
 * */
public class IdComparator implements Comparator<Product> {


	public int compare(Product p1, Product p2) {
		
		return p1.getId().compareTo(p2.getId());
	} 
	
	
	public boolean equals(Product p1, Product p2) {
		return p1.getId().equals(p2.getId());
	}

	
}

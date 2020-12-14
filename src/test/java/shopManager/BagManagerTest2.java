package shopManager;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.Random;
import java.util.logging.Logger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import model.Order;
import model.Product;
import persistency.OrderRepository;
import shopmanager.MyBagManager;
import shopmanager.StockManager;

class BagManagerTest2 {
private static Logger trazador=Logger.getLogger(ProductTest.class.getName());
	
	//Creo los objetos sustitutos (representantes o mocks)
	//Son objetos contenidos en MyBagManager de los que aún no disponemos el código
	@Mock(serializable = true)
	private static Product producto1Mock= Mockito.mock(Product.class);
	@Mock(serializable = true)
	private static Product producto2Mock= Mockito.mock(Product.class);
	@Mock(serializable = true)
	private static Product producto3Mock= Mockito.mock(Product.class);
	@Mock(serializable = true)
	private static Product producto4Mock= Mockito.mock(Product.class);
	@Mock(serializable = true)
	private static Product producto5Mock= Mockito.mock(Product.class);
	@Mock
	private static StockManager stockMock= Mockito.mock(StockManager.class);
	@Mock 
	private static OrderRepository repositoryMock= Mockito.mock(OrderRepository.class);
	@Mock
	private static Order orderMock=Mockito.mock(Order.class);
	
	//Inyección de dependencias
	//Los objetos contenidos en micestaTesteada son reemplazados automáticamente por los sustitutos (mocks)
	@InjectMocks
	private static MyBagManager micestaTesteada;


	@BeforeEach
	void setUp() throws Exception {
		micestaTesteada.reset();
	}
	
	//El test se repite para reducir la probabilidad de que un orden de vuelta aleatorio pase la prueba
	@RepeatedTest(5)
	@DisplayName("Prueba para el método getIdIterator (repetible)")
	public void testGetIdIterator() {
		
		//Prueba basica
		ArrayList<String> idList1 = new ArrayList<String>();
		idList1.add("id1");idList1.add("id2");idList1.add("id3");idList1.add("id4");idList1.add("id5");
		
		//Prueba con strings sin número
		ArrayList<String> idList2 = new ArrayList<String>();
		idList2.add("a");idList2.add("b");idList2.add("c");idList2.add("d");idList2.add("e");
		
		//Prueba con strings de distinta longitud
		ArrayList<String> idList3 = new ArrayList<String>();
		idList3.add("a");idList3.add("aa");idList3.add("aaa");idList3.add("aaaa");idList3.add("aaaaa");
		
		//Prueba con numeros (el orden de salida debe ser lexicográfico, no numérico)
		ArrayList<String> idList4 = new ArrayList<String>();
		idList4.add("24683");idList4.add("22");idList4.add("335");idList4.add("2788");idList4.add("99");
				
		//Prueba con caracteres extraños y espacios
		ArrayList<String> idList5 = new ArrayList<String>();
		idList5.add(" º?'");idList5.add(".ªº^");idList5.add("&$@ ");idList5.add("_ -*+");idList5.add("{[]}");
		
		//Aquí falta una prueba para el caso de ids repetidos, pero este comportamiento no está definido y no se puede probar
		
		
		ArrayList<String> [] tests = (ArrayList<String> []) new ArrayList[3];
		tests[0]=idList1;
		tests[1]=idList2;
		tests[2]=idList3;
		tests[3]=idList4;
		tests[4]=idList5;
		
		for (ArrayList<String> idList : tests) 
		{
			micestaTesteada.reset();
			//Desordenar los ID para evitar que el iterador devuelva con orden de inserción
			Collections.sort(idList);
			String[] orderedIds = (String[]) idList.toArray(); //Id esperados en orden
			Collections.shuffle(idList);
			
			//Cargar los ID en los productosMock
			Mockito.when(producto1Mock.getId()).thenReturn(idList.get(0));
			Mockito.when(producto1Mock.getNumber()).thenReturn(1);
			Mockito.when(producto2Mock.getId()).thenReturn(idList.get(1));
			Mockito.when(producto2Mock.getNumber()).thenReturn(1);
			Mockito.when(producto3Mock.getId()).thenReturn(idList.get(2));
			Mockito.when(producto3Mock.getNumber()).thenReturn(1);
			Mockito.when(producto4Mock.getId()).thenReturn(idList.get(3));
			Mockito.when(producto4Mock.getNumber()).thenReturn(1);
			Mockito.when(producto5Mock.getId()).thenReturn(idList.get(4));
			Mockito.when(producto5Mock.getNumber()).thenReturn(1);
			
			//Añadir los productos Mock
			try 
			{
			micestaTesteada.addProduct(producto1Mock);
			micestaTesteada.addProduct(producto2Mock);
			micestaTesteada.addProduct(producto3Mock);
			micestaTesteada.addProduct(producto4Mock);
			micestaTesteada.addProduct(producto5Mock);
			}
			catch (Exception e)
			{
				trazador.info(e.getMessage());
				fail("Fallo crítico mientras se insertaban los productos");
			}
			
			try 
			{
			Iterator<Product> myIterator= micestaTesteada.getIdIterator();
			}
			catch (Exception e)
			{
				trazador.info(e.getMessage());
				fail("Fallo crítico mientras se intentaba conseguir el iterador");
			}
			
			//Es necesario repetir esta línea porque no se puede reutilizar una variable definida en un try
			Iterator<Product> myIterator= micestaTesteada.getIdIterator();
			
			if(!myIterator.hasNext()) 
			{
				fail("El iterador devuelto está vacío");
			}
			
			for(String id : orderedIds) 
			{
				if(myIterator.hasNext()) 
				{
					assertEquals(id,myIterator.next().getId(), "El orden de los productos devueltos no es correcto");
				}
				else 
				{
					fail("El iterador devuelto no contiene todos los productos");
				}
				
			}
			
			if(myIterator.hasNext()) 
			{
				fail("El iterador devuelto contiene más productos de los que debería");
			}
		}
		//fail("Not yet completely implemented, work in progress");
	}

}

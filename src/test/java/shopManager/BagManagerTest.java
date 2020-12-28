/**
 * 
 */
package shopManager;

import shopmanager.*;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.mockitoSession;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.logging.Logger;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoExtension;

import exceptions.NoEnoughStock;
import exceptions.NotInStock;
import exceptions.UnknownRepo;
import model.Product;
import model.Order;
import persistency.OrderRepository;


import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;



/**
 * @author Isabel Román
 * Clase para realizar los test a la clase MyBagManager, o a cualquier otra clase que implemente BagManager siempre que se sustituya la declaración private static MyBagManager micestaTesteada;
 *
 */
@ExtendWith(MockitoExtension.class)
class BagManagerTest {
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


	//Servirán para conocer el argumento con el que se ha invocado algún método de alguno de los mocks (sustitutos o representantes)
	//ArgumentCaptor es un genérico, indico al declararlo el tipo del argumento que quiero capturar
	@Captor
	private ArgumentCaptor<Integer> intCaptor;
	@Captor
	private ArgumentCaptor<Product> productCaptor;


	/**
	 * @see BeforeEach {@link org.junit.jupiter.api.BeforeEach}
	 */

	@BeforeEach
	void setUpBeforeClass(){
		//Todos los tests empiezan con la bolsa vacía

		micestaTesteada.reset();
	}
	/**
	 * Test para probar el método de efectuar un pedido  {@link shopmanager.BagManager#order()}
	 * 
	 * @throws NoEnoughStock Se intenta añadir un número de unidades de un producto, pero no hay suficientes en stock
	 * @throws NotInStock Se intenta añadir un producto, pero no existe ese tipo en el stock
	 * @throws UnknownRepo Se intenta guardar algo en un repositorio, pero no se ha establecido bien esta referencia y no sabe dónde guardar
	 */

	@Test
	@Tag("unidad")
	@DisplayName("Prueba del método que asienta el pedido")
	void testOrder() throws NoEnoughStock, NotInStock, UnknownRepo {
		trazador.info("Comienza el test de order");
		//Hago un pedido que no debe tener problemas
		trazador.info("Primero sin problemas");
		//El procedimiento rellenaCesta mete dos productos (mocks) en la cesta
		//REVISE EL PROCEDIMIENTO RELLENACESTA
		rellenaCesta();
		//Si no hay problema se guarda
		micestaTesteada.order();

		Mockito.verify(stockMock,Mockito.times(1)).save();
		//Se ha invocado save con el orderMock
		Mockito.verify(repositoryMock).save(orderMock);

		//EJERCICIO: Elimine este comentario, ejecute los test
		Mockito.verifyZeroInteractions(repositoryMock); 
		// ¿Por qué falla el test si se pone aquí esta comprobación? No falla

		//si no se pueda guardar el stock no se guarda el pedido, no se llega a tocar el repositorio ni se modifica order, y mi cesta gestiona la excepción, no debe propagarse y por tanto no debe lanzarla
		trazador.info("Ahora hago que salte la excepción UnknownRepo en el stock, para ver si la gestiona bien BagManager");
		Mockito.doThrow(new UnknownRepo()).when(stockMock).save();
		try {
			micestaTesteada.order();		
			//Me aseguro de que el pedido no se guarda en el repositorio de pedidos
			Mockito.verifyNoMoreInteractions(repositoryMock);

		}
		catch(Exception e) {
			//Me aseguro de que BagManager gestiona esta excepción y no la propaga
			fail("BagManager debe gestionar la excepción UnknownRepo y no propagarla");

		}

	}

	/**
	 * Test method for {@link shopmanager.BagManager#addProduct(model.Product)}.
	 * @throws NotInStock lanza cualquier excepción de sus clientes, no las gestiona siempre internamente
	 * @throws NoEnoughStock lanza cualquier excepción de sus clientes, no las gestiona siempre internamente
	 */
	@Test
	@Tag("unidad")
	@DisplayName("Prueba del método que añade un producto")
	void testAddProduct() throws NoEnoughStock, NotInStock {
		Mockito.when(producto1Mock.getId()).thenReturn("id1");
		Mockito.when(producto1Mock.getNumber()).thenReturn(1);
		Mockito.when(producto2Mock.getId()).thenReturn("id2");
		Mockito.when(producto2Mock.getNumber()).thenReturn(2);

		micestaTesteada.addProduct(producto1Mock);
		assertFalse(micestaTesteada.findProduct("id1").isEmpty());
		assertEquals(1,micestaTesteada.findProduct("id1").get().getNumber(),"El producto insertado debía tener una unidad");
		micestaTesteada.addProduct(producto2Mock);
		assertEquals(2,micestaTesteada.findProduct("id2").get().getNumber(),"El producto insertado debía tener dos unidades");
		assertTrue(micestaTesteada.findProduct("id1").isPresent());
		/**Cuidado con los mock, no son el objeto de verdad son sustitutos y no implementan la lógica de los objetos**/ 
		/**Analizar por qué estos dos test que vienen a continuación no son correctos, mientras que los de arriba sí*/
		/*
		micestaTesteada.addProduct(producto1Mock);
		assertEquals(2,micestaTesteada.findProduct("id1").get().getNumber(),"El incremento de un producto en una unidad no se hace bien");
		micestaTesteada.addProduct(producto2Mock);	
		assertEquals(4,micestaTesteada.findProduct("id2").get().getNumber(),"El incremento de un producto en dos unidades no se hace bien");
		 */

		//Para ver si realmente hace bien la actualización de valores lo que deberíamos es asegurar que el método 
		//newProduct.setNumber(newProduct.getNumber()+antes);
		//se invoca con el valor correcto (no invoca la primera vez, ni la segunda porque el producto no estaba, la tercera se invoca con 2 y la cuarta con 4, porque hay que cambiarle el valor)
		//estoy suponiendo que se guarda exactamente el mismo producto que se pasa, no se hace ningún tipo de copia (en realidad no tendría por qué suponer esto...
		//Es para probar las prestaciones de los ArgumentCaptors

		//la segunda vez que añado el producto debe sumarse el número de unidades a las que ya había
		micestaTesteada.addProduct(producto1Mock);
		//quiero verificar el argumento que se ha usado en el mock para poner el número de unidades
		Mockito.verify(producto1Mock).setNumber(intCaptor.capture());
		assertEquals(2,intCaptor.getValue(), "El argumento para actualizar el número de unidades en el producto no se calcula bien");


		micestaTesteada.addProduct(producto2Mock);	
		Mockito.verify(producto2Mock).setNumber(intCaptor.capture());
		assertEquals(4,intCaptor.getValue(), "El argumento para actualizar el número de unidades en el producto no se calcula bien");

		//Si hay no hay stock el producto no se debe añadir, parto de nuevo de la cesta vacía
		micestaTesteada.reset();
		Mockito.doThrow(new NoEnoughStock(0)).when(stockMock).lessProduct(producto1Mock);
		try {
			micestaTesteada.addProduct(producto1Mock);
			//debe saltar la excepción así que no debe llegar aquí
			fail("No salta la excepción del stock");
		}catch(NoEnoughStock e){
			assertEquals("No hay suficientes unidades en el Stock, sólo quedan 0",e.getMessage(),"El mensaje de la excepción no es correcto");

		}   
		//Aseguro que si no había suficientes unidades no se ha agregado a la cesta
		assertTrue(micestaTesteada.findProduct("id1").isEmpty(),"Se agrega un producto cuando no había suficientes unidades");
		assertFalse(micestaTesteada.findProduct("id1").isPresent(),"Se agrega un producto cuando no había suficientes unidades");

		//Ahora pruebo la gestión de la excepción NotInStock, no se debe agregar a la cesta y debe lanzar la excepción
		//aseguro que parto de la cesta vacía
		micestaTesteada.reset();
		Mockito.doThrow(new NotInStock("id1")).when(stockMock).lessProduct(producto1Mock);
		try {
			micestaTesteada.addProduct(producto1Mock);
			//debe saltar la excepción así que no debe llegar aquí
			fail("No salta la excepción NotInStock stock");
		}catch(NotInStock e){
			assertEquals("El producto con id id1 no existe en el Stock",e.getMessage(),"El mensaje de la excepción no es correcto");

		}   
		//Aseguro que si no existía en el stock no se ha agregado a la cesta
		assertTrue(micestaTesteada.findProduct("id1").isEmpty(),"Se agrega un producto que no existe en el stock");
		assertFalse(micestaTesteada.findProduct("id1").isPresent(),"Se agrega un producto que no existe en el stock");
	}

	/**
	 * Test method for {@link shopmanager.BagManager#lessProduct(model.Product)}.
	 */
	@Test
	@Tag("unidad")

	void testLessProduct() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link shopmanager.BagManager#removeProduct(model.Product)}.
	 */
	@Test
	@Tag("unidad")
	void testRemoveProductProduct() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link shopmanager.MyBagManager#removeProduct(java.lang.String)}.
	 */
	@Test
	@Tag("unidad")
	void testRemoveProductString() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link shopmanager.MyBagManager#getBag()}.
	 */
	@Test
	@Tag("unidad")
	void testGetBag() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link shopmanager.MyBagManager#findProduct(java.lang.String)}.
	 */
	@Test
	@Tag("unidad")
	void testFindProductString() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link shopmanager.MyBagManager#findProduct(model.Product)}.
	 */
	@Test
	@Tag("unidad")
	void testFindProductProduct() {
		fail("Not yet implemented");
	}
	/**
	 * Rellena una cesta con los dos mocks declarados al inicio
	 * @throws NoEnoughStock Si no hay suficiente stock del producto a añadir
	 * @throws NotInStock Si no existe el producto en el stock
	 */
	void rellenaCesta() throws NoEnoughStock, NotInStock {
		Mockito.when(producto1Mock.getId()).thenReturn("id1");
		Mockito.when(producto1Mock.getNumber()).thenReturn(1);
		Mockito.when(producto2Mock.getId()).thenReturn("id2");
		Mockito.when(producto2Mock.getNumber()).thenReturn(2);
		micestaTesteada.addProduct(producto1Mock);
		micestaTesteada.addProduct(producto2Mock);
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


		ArrayList<String> [] tests = (ArrayList<String> []) new ArrayList[5];
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
			ArrayList<String> orderedIds = new ArrayList<String>(idList); //Id esperados en orden
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

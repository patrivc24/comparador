package smartmarket355521;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.Connection.Response;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

// Clase que permite realizar la busqueda de productos en las paginas de Dia y 
// mediante tecnicas de webscraping (JSoup). 
public class Scraping {
	String producto;
	String productoPCBox;
	ArrayList<Producto> listaProductos;
	
	// Constructor de Scraping
	public Scraping(String producto) {
		// Cambia los espacios por %20, que es como se representa en las URL
		this.producto = producto.replaceAll("\\s+","%20");
		String[] partes = producto.split(" ");
		this.productoPCBox = partes[0];
		listaProductos = new ArrayList<>();
	}
	
	
	// Metodo que realiza webscraping para buscar productos en DIA
	private void buscarDia() throws IOException {
		// Se crea la URL de busqueda con el nombre del producto
		String url1 = "https://www.dia.es/compra-online/search?text=";
		String urlPage = url1 + this.producto + "&x=0&y=0";
				  
		System.out.println("Comprobando entradas de: " + urlPage);
		 
		// Se realiza una conexion a la URL, si tiene exito
		if (getStatusConnectionCode(urlPage) == 200) {
			
			// Se obtiene el HTML de la pagina
			Document document = getHtmlDocument(urlPage);
			
			// Se obtienen todos los divs de los productos
			Elements entradas = document.select("div.product-list__item");
			
			// Por cada div se extrae el nombre, precio, link e imagen del producto
			for (Element elem : entradas) {
				String titulo = "";
				String precio = "";
				String link = "";
				String urlImagen = "";
		
				titulo = elem.getElementsByClass("details").text();
				precio = elem.getElementsByClass("price_container").text();
				
				String[] partes = precio.split(" ");
				
				float precioNumerico = 0.0f;
				StringBuilder precioAux = new StringBuilder(partes[0]);
				
				try {
					precioAux.deleteCharAt(precioAux.indexOf("."));
				} catch (Exception e) {
					// System.out.println("Precio " + precioAux.toString() + ", menor que 1000");
				}
				
				String precioCorrecto = precioAux.toString();
				precioCorrecto = precioCorrecto.replace(",", ".");
				
				try {
					precioNumerico = Float.parseFloat(precioCorrecto);
				} catch (Exception e) {
					//System.out.println("No se puede convertir en float " + partes[0]);
				}
			


				link =  urlPage + elem.getElementsByTag("a").attr("href");
				urlImagen = urlPage + elem.getElementsByTag("img").attr("src");
				
				// Se crea el producto y se añade a la lista de productos
				Producto p = new Producto(titulo, link, urlImagen, "dia_logo.png", precioNumerico);
				listaProductos.add(p);	
			}               
		}else{
			System.out.println("El Status Code no es OK es: "+getStatusConnectionCode(urlPage));
		}     
	}
	
	// Metodo que realiza webscraping para buscar productos en DIA
		private void buscarPcBox() throws IOException {
			// Se crea la URL de busqueda con el nombre del producto
			String url1 = "https://www.hipercor.es/supermercado/buscar/?term=";
			//https://www.hipercor.es/supermercado/buscar/?term=pizza&search=text
			String urlPage = url1 + this.productoPCBox + "&search=text" ;
					  
			System.out.println("Comprobando entradas de: " + urlPage);
			 
			// Se realiza una conexion a la URL, si tiene exito
			if (getStatusConnectionCode(urlPage) == 200) {
				
				// Se obtiene el HTML de la pagina
				Document document = getHtmlDocument(urlPage);
				
				// Se obtienen todos los divs de los productos
				Elements entradas = document.select("div.grid-item.product_tile._retro._hipercor.dataholder.js-product.-has_offer");
				
				// Por cada div se extrae el nombre, precio, link e imagen del producto
				for (Element elem : entradas) {
					String titulo = "";
					String precio = "";
					String link = "";
					String urlImagen = "";
			
					titulo = elem.getElementsByClass("product_tile-description").text();
					precio = elem.getElementsByClass("prices-price _current").text();
					
					String[] partes = precio.split(" ");
					
					float precioNumerico = 0.0f;
					StringBuilder precioAux = new StringBuilder(partes[0]);
					
					try {
						precioAux.deleteCharAt(precioAux.indexOf("."));
					} catch (Exception e) {
						// System.out.println("Precio " + precioAux.toString() + ", menor que 1000");
					}
					
					String precioCorrecto = precioAux.toString();
					precioCorrecto = precioCorrecto.replace(",", ".");
					
					try {
						precioNumerico = Float.parseFloat(precioCorrecto);
					} catch (Exception e) {
						//System.out.println("No se puede convertir en float " + partes[0]);
					}
				


					link = "https://www.hipercor.es" + elem.getElementsByTag("a").attr("href");
					urlImagen = "https://www.hipercor.es" + elem.getElementsByTag("img").attr("src");
					
					// Se crea el producto y se añade a la lista de productos
					Producto p = new Producto(titulo, link, urlImagen, "dia_logo.png", precioNumerico);
					listaProductos.add(p);	
				}               
			}else{
				System.out.println("El Status Code no es OK es: "+getStatusConnectionCode(urlPage));
			}     
		}
	
	// Metodo que llama a las dos funciones de webscraping definidas anteriormente
	public void buscar() throws IOException {
		buscarPcBox();
		buscarDia();
	}
	
	// Funcion para determinar el codigo de conexion a la pagina web
	public static int getStatusConnectionCode(String url) throws IOException {
        Response response = null;
		
        response =Jsoup.connect(url).userAgent("Mozilla/5.0").timeout(100000).ignoreHttpErrors(true).execute();
        return response.statusCode();
    }
	
	// Funcion que permite obtener el documento HTML de una pagina
	public static Document getHtmlDocument(String url) throws IOException {

        Document doc = null;

        doc = Jsoup.connect(url).get();

        return doc;
	}
	

	// Getter de producto
	public String getProducto() {
		return producto;
	}
	
	// Setter de producto
	public void setProducto(String producto) {
		this.producto = producto;
	}
	
	// Getter de la lista de productos
	public ArrayList<Producto> getListaProductos() {
		return listaProductos;
	}
	
	// Setter de la lista de productos
	public void setListaProductos(ArrayList<Producto> listaProductos) {
		this.listaProductos = listaProductos;
	}
}

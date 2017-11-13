package android.adriangalvarez.listadecompras.Bussiness;

/**
 * Created by Adrian on 13/11/2017.
 */

public class Item{
	private String descripcion;
	private int cantidad;

	public Item( String descripcion ){
		this.descripcion = descripcion;
		this.cantidad = 0;
	}

	public Item( String descripcion, int cantidad ){
		this.descripcion = descripcion;
		this.cantidad = cantidad;
	}

	public String getDescripcion(){
		return descripcion;
	}

	public void setDescripcion( String descripcion ){
		this.descripcion = descripcion;
	}

	public int getCantidad(){
		return cantidad;
	}

	public void setCantidad( int cantidad ){
		this.cantidad = cantidad;
	}
}

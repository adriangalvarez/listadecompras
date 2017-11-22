package android.adriangalvarez.listadecompras.Bussiness;

import android.adriangalvarez.listadecompras.Activities.ItemAdapter;
import android.adriangalvarez.listadecompras.Data.ItemDL;
import android.content.Context;
import android.content.SharedPreferences;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by Adrian on 13/11/2017.
 */

public class ItemBL implements Serializable{
	private String descripcion;
	private int cantidad;

	public ItemBL( String descripcion ){
		this.descripcion = descripcion;
		this.cantidad = 0;
	}

	public ItemBL( String descripcion, int cantidad ){
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

	public void addCantidad( Context context ){
		this.cantidad++;
		ItemDL itemDL = new ItemDL( context );
		itemDL.Modify( this, descripcion );
	}

	@Override
	public boolean equals( Object obj ){
		boolean retVal = false;

		if( obj instanceof ItemBL ){
			ItemBL ptr = ( ItemBL ) obj;
			retVal = ptr.getDescripcion().equals( this.descripcion );
		}

		return retVal;
	}

	@Override
	public int hashCode(){
		int hash = 7;
		hash = 17 * hash + ( this.descripcion != null ? this.descripcion.hashCode() : 0 );
		return hash;
	}

	public static Map<String, ?> getAll( Context context ){
		ItemDL data = new ItemDL( context );
		return data.GetAll();
	}

	public void add( Context context ){
		ItemDL data = new ItemDL( context );
		data.Add( this );
	}

	public void deleteFromCompras( Context context ){
		ItemDL data = new ItemDL( context );
		data.DeleteFromCompras( this );
	}

	public void modify(Context context, String descripcionAnterior){
		ItemDL data = new ItemDL( context );
		data.Modify( this, descripcionAnterior );
	}
}

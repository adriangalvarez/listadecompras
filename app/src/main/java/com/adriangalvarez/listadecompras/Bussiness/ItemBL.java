package com.adriangalvarez.listadecompras.Bussiness;

import com.adriangalvarez.listadecompras.Database.AppDatabase;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.content.ContentResolver;
import android.content.Context;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Adrian on 13/11/2017.
 */

@Entity( tableName = "Items" )
public class ItemBL implements Serializable{
	private static final String SEPARATOR = "/";

	@PrimaryKey( autoGenerate = true )
	private long id;

	private String descripcion;
	private int cantidad;
	private String rutaImagen;

	public ItemBL(){
		//Needed for ROOM
	}

	@Ignore
	public ItemBL( String descripcion, int cantidad, String rutaImagen ){
		this.descripcion = descripcion;
		this.cantidad = cantidad;
		this.rutaImagen = rutaImagen;
	}

	public long getId(){
		return id;
	}

	public void setId( long id ){
		this.id = id;
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

	public String getRutaImagen(){
		return rutaImagen;
	}

	public void setRutaImagen( String rutaImagen ){
		this.rutaImagen = rutaImagen;
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

	public static List< ItemBL > getAll( Context context ){
		return AppDatabase.getInstance( context ).getItemDAO().getAll();
	}

	public static List< ItemBL > getAllCompras( Context context ){
		return AppDatabase.getInstance( context ).getItemDAO().getAllCompras();
	}

	public static String getDataForBackup( Context context ){
		StringBuilder result = new StringBuilder();
		for( ItemBL itemBL : getAll( context ) ){
			result.append( itemBL.getDescripcion() )
					.append( SEPARATOR )
					.append( itemBL.getCantidad() )
					.append( System.lineSeparator() );
		}
		return result.toString();
	}

	public static void getDataFromBackup( Context context, String data ){
		AppDatabase appDatabase = AppDatabase.getInstance( context );
		appDatabase.getItemDAO().deleteAll();

		String[] lines = data.split( System.lineSeparator() );
		ItemBL newItem;
		for( String items : lines ){
			String[] keyValue = items.split( SEPARATOR );
			newItem = new ItemBL();
			newItem.setDescripcion( keyValue[ 0 ] );
			newItem.setCantidad( Integer.parseInt( keyValue[ 1 ] ) );
			newItem.setId( newItem.add( context ) );
		}
	}

	public long add( Context context ){
		return AppDatabase.getInstance( context ).getItemDAO().insert( this );
	}

	public void modify( Context context ){
		AppDatabase.getInstance( context ).getItemDAO().update( this );
	}

	public static boolean existe( Context context, String descripcion ){
		return AppDatabase.getInstance( context ).getItemDAO().exists( descripcion );
	}

	public void addCantidad( Context context ){
		this.cantidad++;
		modify( context );
	}

	public void deleteFromCompras( Context context ){
		this.cantidad = 0;
		modify( context );
	}

	public static void resetCompras( Context context ){
		AppDatabase.getInstance( context ).getItemDAO().deleteAllCompras();
	}

	public static boolean existeEnCompras( Context context, long id ){
		return AppDatabase.getInstance( context ).getItemDAO().existeEnCompras( id ) > 0;
	}

	public void updateRutaImagen( Context context ){
		AppDatabase.getInstance( context ).getItemDAO().updateImage( id, rutaImagen );
	}
}

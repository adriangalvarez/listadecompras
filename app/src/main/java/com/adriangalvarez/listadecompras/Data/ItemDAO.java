package com.adriangalvarez.listadecompras.Data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.adriangalvarez.listadecompras.Bussiness.ItemBL;

import java.util.List;

/**
 * Created by adriangalvarez on 19/08/2018.
 */
@Dao
public interface ItemDAO{
	@Insert
	public long insert( ItemBL item );

	@Update
	public void update( ItemBL item );

	@Delete
	public void delete( ItemBL item );

	@Query( "Delete from Items" )
	public void deleteAll();

	@Query( "Select id, descripcion, cantidad from Items" )
	public List< ItemBL > getAll();

	@Query( "Select id from Items where descripcion = :descripcion" )
	public boolean exists( String descripcion );

	@Query( "Update Items set cantidad = 0" )
	public void deleteAllCompras();

	@Query( "Select id, descripcion, cantidad from Items" )
	public List< ItemBL > getAllCompras();

	@Query( "Select count( id ) from Items where id = :id and cantidad > 0" )
	public int existeEnCompras( long id );
}

package com.adriangalvarez.listadecompras.Data;

import android.arch.persistence.room.Dao;
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
	long insert( ItemBL item );

	@Update
	void update( ItemBL item );

	@Query( "Delete from Items" )
	void deleteAll();

	@Query( "Select id, descripcion, cantidad, notas from Items" )
	List< ItemBL > getAll();

	@Query( "Select id from Items where descripcion = :descripcion" )
	boolean exists( String descripcion );

	@Query( "Update Items set cantidad = 0" )
	void deleteAllCompras();

	@Query( "Select id, descripcion, cantidad, notas from Items where cantidad > 0" )
	List< ItemBL > getAllCompras();

	@Query( "Select count( id ) from Items where id = :id and cantidad > 0" )
	int existeEnCompras( long id );
}

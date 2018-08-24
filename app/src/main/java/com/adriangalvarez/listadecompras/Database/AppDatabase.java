package com.adriangalvarez.listadecompras.Database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.adriangalvarez.listadecompras.Bussiness.ItemBL;
import com.adriangalvarez.listadecompras.Data.ItemDAO;

/**
 * Created by adriangalvarez on 19/08/2018.
 */
@Database( entities = { ItemBL.class }, version = 1, exportSchema = false )

public abstract class AppDatabase extends RoomDatabase{

	private static final String DB_NAME = "ShoppingList.db";
	private static volatile AppDatabase instance;

	public static synchronized AppDatabase getInstance( Context context ){
		if( instance == null )
			instance = create( context );

		return instance;
	}

	public static void destroyInstance(){
		instance = null;
	}

	private static AppDatabase create( Context context ){
		return Room.databaseBuilder( context,  AppDatabase.class, AppDatabase.DB_NAME )
				.allowMainThreadQueries()
				.fallbackToDestructiveMigration()
				.build();
	}

	public abstract ItemDAO getItemDAO();
}

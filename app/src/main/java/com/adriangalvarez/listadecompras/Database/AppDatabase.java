package com.adriangalvarez.listadecompras.Database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.support.annotation.NonNull;

import com.adriangalvarez.listadecompras.Bussiness.ItemBL;
import com.adriangalvarez.listadecompras.Data.ItemDAO;

/**
 * Created by adriangalvarez on 19/08/2018.
 */
@Database( entities = { ItemBL.class }, version = 2, exportSchema = false )

public abstract class AppDatabase extends RoomDatabase{

	private static final String DB_NAME = "ShoppingList.db";
	private static volatile AppDatabase instance;
	private static final Migration MIGRATION_1_2 = new Migration( 1, 2 ){
		@Override
		public void migrate( @NonNull SupportSQLiteDatabase database ){
			database.execSQL( "ALTER TABLE Items ADD COLUMN imagen BLOB" );

//			database.execSQL( "CREATE TABLE users_temp( id TEXT, username TEXT, last_update INTEGER, PRIMARY KEY( id ) )" );
//			database.execSQL( "INSERT INTO users_temp( id, username, last_update ) SELECT id, username, last_update FROM users" );
//			database.execSQL( "DROP TABLE users" );
//			database.execSQL( "ALTER TABLE users_temp RENAME TO users" );
		}
	};

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
				.addMigrations( MIGRATION_1_2 )
				.build();
	}

	public abstract ItemDAO getItemDAO();
}

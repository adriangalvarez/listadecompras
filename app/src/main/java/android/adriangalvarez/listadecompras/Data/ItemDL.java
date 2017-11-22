package android.adriangalvarez.listadecompras.Data;

import android.adriangalvarez.listadecompras.Bussiness.ItemBL;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;

/**
 * Created by Adrian on 13/11/2017.
 */

public class ItemDL{
	SharedPreferences mSharedPreferences;
	SharedPreferences.Editor mEditor;

	public ItemDL(Context context){
		mSharedPreferences = context.getSharedPreferences( "sharedPrefs", Context.MODE_PRIVATE );
	}

	public Map<String, ?> GetAll(){
		return mSharedPreferences.getAll();
	}

	public void Add(ItemBL item){
		mEditor = mSharedPreferences.edit();
		mEditor.putInt( item.getDescripcion(), item.getCantidad() );
		mEditor.commit();
	}

	public void Modify(ItemBL item, String descripcionAnterior){
		mEditor = mSharedPreferences.edit();
		mEditor.remove( descripcionAnterior );
		mEditor.putInt( item.getDescripcion(), item.getCantidad() );
		mEditor.commit();
	}

	public void DeleteFromCompras(ItemBL item){
		mEditor = mSharedPreferences.edit();
		mEditor.putInt( item.getDescripcion(), 0 );
		mEditor.commit();
	}
}

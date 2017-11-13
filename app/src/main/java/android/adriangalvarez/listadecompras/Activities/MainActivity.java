package android.adriangalvarez.listadecompras.Activities;

import android.adriangalvarez.listadecompras.R;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity{

	private SharedPreferences compras;
	private List< String > listaCompras;
	private List< String > listaTotal;
	private ListView listViewCompras;
	private ListView listViewTotal;
	private ArrayAdapter< String > adapterCompras;
	private ArrayAdapter< String > adapterTotal;
	private Button buttonAdd;
	private final int REQ_ADD_ITEM = 1;
	private final int REQ_EDIT_ITEM = 2;

	@Override
	protected void onCreate( Bundle savedInstanceState ){
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_main );

		ActionBar actionBar = getSupportActionBar();
		actionBar.setTitle( R.string.app_name );
		actionBar.setDisplayShowHomeEnabled( true );
		actionBar.setIcon( R.drawable.shoppingcart );

		compras = this.getSharedPreferences( "sharedPrefs", MODE_PRIVATE );
		InitListaCompras();

		listViewCompras = findViewById( R.id.list_item_compras );
		adapterCompras = new ArrayAdapter<>( this, android.R.layout.simple_list_item_1, listaCompras );
		OrdenarAdapter( adapterCompras );
		listViewCompras.setAdapter( adapterCompras );
		listViewCompras.setOnItemClickListener( new AdapterView.OnItemClickListener(){
			@Override
			public void onItemClick( AdapterView< ? > parent, View view, int position, long id ){
				String clicked = listaCompras.get( position );
				listaCompras.remove( clicked );
				adapterCompras.notifyDataSetChanged();
				SharedPreferences.Editor editor = compras.edit();
				editor.putBoolean( clicked, false );
				editor.commit();
			}
		} );

		listViewTotal = findViewById( R.id.list_item_total );
		adapterTotal = new ArrayAdapter<>( this, android.R.layout.simple_list_item_1, listaTotal );
		OrdenarAdapter( adapterTotal );
		listViewTotal.setAdapter( adapterTotal );
		listViewTotal.setVisibility( View.INVISIBLE );

		listViewTotal.setOnItemClickListener( new AdapterView.OnItemClickListener(){
			@Override
			public void onItemClick( AdapterView< ? > parent, View view, int position, long id ){
				String clicked = listaTotal.get( position );
				AddItemToAdapter( clicked, listaCompras, adapterCompras );
				SharedPreferences.Editor editor = compras.edit();
				editor.putBoolean( clicked, true );
				editor.commit();
				ToggleListView();
			}
		} );

		listViewTotal.setOnItemLongClickListener( new AdapterView.OnItemLongClickListener(){
			@Override
			public boolean onItemLongClick( AdapterView< ? > parent, View view, int position, long id ){
				Intent intent = new Intent( MainActivity.this, AddItemActivity.class );
				intent.putExtra( "isEditing", true );
				intent.putExtra( "descripcionant", listaTotal.get( position ) );
				intent.putExtra( "listacomprasant", listaCompras.contains( listaTotal.get( position ) ) );
				startActivityForResult( intent, REQ_EDIT_ITEM );
				return true;
			}
		} );

		buttonAdd = findViewById( R.id.buttonAdd );
		buttonAdd.setOnClickListener( new View.OnClickListener(){
			@Override
			public void onClick( View v ){
				Intent intent = new Intent( MainActivity.this, AddItemActivity.class );
				intent.putExtra( "isEditing", false );
				startActivityForResult( intent, REQ_ADD_ITEM );
			}
		} );
		buttonAdd.setVisibility( View.INVISIBLE );
	}

	private void OrdenarAdapter( ArrayAdapter< String > adapter ){
		adapter.sort( new Comparator< String >(){
			@Override
			public int compare( String o1, String o2 ){
				return o1.compareTo( o2 );
			}
		} );
	}

	private void InitListaCompras(){
		listaTotal = new ArrayList<>();
		listaCompras = new ArrayList<>();

		Map< String, ? > allEntries = compras.getAll();
		for( Map.Entry< String, ? > entry : allEntries.entrySet() ){
			listaTotal.add( entry.getKey() );
			if( Boolean.valueOf( entry.getValue().toString() ) )
				listaCompras.add( entry.getKey() );
		}
	}

	@Override
	public boolean onCreateOptionsMenu( Menu menu ){
		getMenuInflater().inflate( R.menu.options_menu, menu );
		return super.onCreateOptionsMenu( menu );
	}

	@Override
	public boolean onOptionsItemSelected( MenuItem item ){
		switch( item.getItemId() ){
			case R.id.menu_view_total:
				ToggleListView();
				return true;
			default:
				return super.onOptionsItemSelected( item );
		}
	}

	private void ToggleListView(){
		if( listViewTotal.getVisibility() == View.VISIBLE ){
			listViewTotal.setVisibility( View.INVISIBLE );
			listViewCompras.setVisibility( View.VISIBLE );
			getSupportActionBar().setTitle( R.string.app_name );
		}else{
			listViewTotal.setVisibility( View.VISIBLE );
			listViewCompras.setVisibility( View.INVISIBLE );
			getSupportActionBar().setTitle( R.string.listaTotal );
		}

		buttonAdd.setVisibility( listViewTotal.getVisibility() );
	}

	@Override
	protected void onActivityResult( int requestCode, int resultCode, Intent data ){
		switch( requestCode ){
			case REQ_ADD_ITEM:
			case REQ_EDIT_ITEM:
				if( resultCode == RESULT_OK ){
					String descripcion = data.getStringExtra( "descripcion" );
					Boolean bListaCompras = data.getBooleanExtra( "listacompras", false );
					int cantidad = data.getIntExtra( "cantidad", 0 );
					SharedPreferences.Editor editor = compras.edit();
					editor.putBoolean( descripcion, bListaCompras );
					if( requestCode == REQ_EDIT_ITEM ){
						String descripcionAnt = data.getStringExtra( "descripcionant" );
						editor.remove( descripcionAnt );
						listaTotal.remove( descripcionAnt );
						listaCompras.remove( descripcionAnt );
					}
					editor.commit();

					AddItemToAdapter( descripcion, listaTotal, adapterTotal );

					if( bListaCompras ){
						AddItemToAdapter( descripcion, listaCompras, adapterCompras );
						ToggleListView();
					}
				}
		}
	}

	private void AddItemToAdapter( String item, List< String > lista, ArrayAdapter< String > adapter ){
		if( !lista.contains( item ) ){
			lista.add( item );
			Toast.makeText( MainActivity.this, item + " " + getString( R.string.itemAgregado ), Toast.LENGTH_SHORT ).show();
			OrdenarAdapter( adapter );
		}else{
			Toast.makeText( MainActivity.this, R.string.itemYaExiste, Toast.LENGTH_SHORT ).show();
		}
	}
}
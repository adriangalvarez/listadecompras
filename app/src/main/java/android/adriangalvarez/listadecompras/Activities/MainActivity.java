package android.adriangalvarez.listadecompras.Activities;

import android.adriangalvarez.listadecompras.Adapters.ItemAdapter;
import android.adriangalvarez.listadecompras.Adapters.TotalItemAdapter;
import android.adriangalvarez.listadecompras.Bussiness.ItemBL;
import android.adriangalvarez.listadecompras.R;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity{

	private List< ItemBL > listaCompras;
	private ItemAdapter mAdapterCompras;
	private RecyclerView mRecyclerCompras;
	private RecyclerView.LayoutManager mLayoutManager;

	private List< String > listaTotal;
	private RecyclerView mRecyclerTotal;
	private RecyclerView.LayoutManager mLayoutManagerTotal;
	private TotalItemAdapter mAdapterTotal;

	private FloatingActionButton buttonAdd;
	private FloatingActionButton buttonShare;

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

		InitListaCompras();

		mRecyclerCompras = findViewById( R.id.recycler_compras );
		mLayoutManager = new LinearLayoutManager( this );
		mAdapterCompras = new ItemAdapter( listaCompras, R.layout.listview_row_item, new ItemAdapter.onItemClickListener(){
			@Override
			public void onItemClick( ItemBL itemBL, int position ){
				listaCompras.remove( itemBL );
				mAdapterCompras.notifyItemRemoved( position );
				itemBL.deleteFromCompras( MainActivity.this );
			}

			@Override
			public void onItemAddClick( ItemBL itemBL, int position ){
				itemBL.addCantidad( MainActivity.this );
				mAdapterCompras.notifyItemChanged( position );
			}
		} );

		OrdenarAdapterCompras();
		mRecyclerCompras.setLayoutManager( mLayoutManager );
		mRecyclerCompras.setAdapter( mAdapterCompras );

		mRecyclerTotal = findViewById( R.id.recycler_total );
		mLayoutManagerTotal = new LinearLayoutManager( MainActivity.this );
		mAdapterTotal = new TotalItemAdapter( R.layout.total_row_item, listaTotal, new TotalItemAdapter.OnItemClickListener(){
			@Override
			public void OnItemClickListener( String descripcion, int position ){
				Intent intent = new Intent( MainActivity.this, AddItemActivity.class );
				intent.putExtra( "isEditing", true );

				ItemBL itemBLAnt = new ItemBL( listaTotal.get( position ) );
				intent.putExtra( "descripcionant", itemBLAnt.getDescripcion() );
				if( listaCompras.contains( itemBLAnt ) ){
					intent.putExtra( "listacomprasant", true );
				}else{
					intent.putExtra( "listacomprasant", false );
				}
				startActivityForResult( intent, REQ_EDIT_ITEM );
			}

			@Override
			public void OnItemClickAdd( String descripcion, int position ){
				ItemBL itemBL = new ItemBL( listaTotal.get( position ), 1 );
				if( !listaCompras.contains( itemBL ) ){
					listaCompras.add( itemBL );
					Toast.makeText( MainActivity.this, itemBL.getDescripcion() + " " + getString( R.string.itemAgregado ), Toast.LENGTH_SHORT ).show();
					OrdenarAdapterCompras();
				}else{
					Toast.makeText( MainActivity.this, R.string.itemYaExiste, Toast.LENGTH_SHORT ).show();
				}

				itemBL.add( MainActivity.this );
				ToggleListView();
			}

		} );

		OrdenarAdapterTotal();
		mRecyclerTotal.setLayoutManager( mLayoutManagerTotal );
		mRecyclerTotal.setAdapter( mAdapterTotal );
		mRecyclerTotal.setVisibility( View.INVISIBLE );

		buttonAdd = findViewById( R.id.buttonAdd);
		buttonAdd.setOnClickListener( new View.OnClickListener(){
			@Override
			public void onClick( View v ){
				Intent intent = new Intent( MainActivity.this, AddItemActivity.class );
				intent.putExtra( "isEditing", false );
				startActivityForResult( intent, REQ_ADD_ITEM );
			}
		} );

		buttonShare = findViewById( R.id.buttonShare );
		buttonShare.setOnClickListener( new View.OnClickListener(){
			@Override
			public void onClick( View v ){
				Intent intentShare = new Intent( Intent.ACTION_SEND );
				intentShare.setType( "text/plain" );
				intentShare.putExtra( Intent.EXTRA_TEXT, GenerarListaComprasToShare() );
				startActivity( Intent.createChooser( intentShare, "Enviar por..." ) );
			}
		} );
	}

	private String GenerarListaComprasToShare(){
		StringBuilder compartir = new StringBuilder( "" );
		for( ItemBL temp : listaCompras )
			compartir.append( String.valueOf( temp.getCantidad() ) + " " + temp.getDescripcion() + "\n" );
		return compartir.toString();
	}

	private void OrdenarAdapterCompras(){
		Collections.sort( listaCompras, new Comparator< ItemBL >(){
			@Override
			public int compare( ItemBL o1, ItemBL o2 ){
				return o1.getDescripcion().compareTo( o2.getDescripcion() );
			}
		});
		mAdapterCompras.notifyDataSetChanged();
	}

	private void OrdenarAdapterTotal(){
		Collections.sort( listaTotal, new Comparator< String >(){
			@Override
			public int compare( String o1, String o2 ){
				return o1.compareTo( o2 );
			}
		} );
		mAdapterTotal.notifyDataSetChanged();
	}

	private void InitListaCompras(){
		listaTotal = new ArrayList<>();
		listaCompras = new ArrayList<>();

		Map< String, ? > allEntries = ItemBL.getAll( MainActivity.this );
		for( Map.Entry< String, ? > entry : allEntries.entrySet() ){
			listaTotal.add( entry.getKey() );
			int cantidad = Integer.parseInt( entry.getValue().toString() );
			if( cantidad > 0 ){
				ItemBL itemBL = new ItemBL( entry.getKey(), cantidad );
				listaCompras.add( itemBL );
			}
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
		if( mRecyclerTotal.getVisibility() == View.VISIBLE ){
			mRecyclerTotal.setVisibility( View.INVISIBLE );
			mRecyclerCompras.setVisibility( View.VISIBLE );
			getSupportActionBar().setTitle( R.string.app_name );
		}else{
			mRecyclerTotal.setVisibility( View.VISIBLE );
			mRecyclerCompras.setVisibility( View.INVISIBLE );
			getSupportActionBar().setTitle( R.string.listaTotal );
		}

		buttonAdd.setVisibility( mRecyclerTotal.getVisibility() );
		buttonShare.setVisibility( mRecyclerCompras.getVisibility() );
	}

	@Override
	protected void onActivityResult( int requestCode, int resultCode, Intent data ){
		switch( requestCode ){
			case REQ_ADD_ITEM:
				if( resultCode == RESULT_OK ){
					ItemBL newItem = (ItemBL) data.getSerializableExtra( "itemBL" );
					newItem.add( MainActivity.this );
				}
			case REQ_EDIT_ITEM:
				if( resultCode == RESULT_OK ){
					ItemBL editedItem = ( ItemBL ) data.getSerializableExtra( "itemBL" );
					if( requestCode == REQ_EDIT_ITEM ){
						String descripcionAnt = data.getStringExtra( "descripcionant" );
						editedItem.modify( MainActivity.this, data.getStringExtra( "descripcionant" ) );
						listaTotal.remove( descripcionAnt );

						if( listaCompras.contains( editedItem ) ){
							editedItem.deleteFromCompras( MainActivity.this );
						}
					}

					AddItemToAdapterTotal( editedItem.getDescripcion() );

					if( data.getBooleanExtra( "listacompras", false ) ){
						AddItemToAdapterCompras( editedItem );
						ToggleListView();
					}
				}
		}
	}

	private void AddItemToAdapterTotal( String item ){
		if( !listaTotal.contains( item ) ){
			listaTotal.add( item );
			Toast.makeText( MainActivity.this, item + " " + getString( R.string.itemAgregado ), Toast.LENGTH_SHORT ).show();
			OrdenarAdapterTotal();
		}else{
			Toast.makeText( MainActivity.this, R.string.itemYaExiste, Toast.LENGTH_SHORT ).show();
		}
	}

	private void AddItemToAdapterCompras( ItemBL itemBL ){
		if( !listaCompras.contains( itemBL ) ){
			listaCompras.add( itemBL );
			Toast.makeText( MainActivity.this, itemBL.getDescripcion() + " " + getString( R.string.itemAgregado ), Toast.LENGTH_SHORT ).show();
			OrdenarAdapterCompras();
		}else{
			Toast.makeText( MainActivity.this, R.string.itemYaExiste, Toast.LENGTH_SHORT ).show();
		}
	}
}
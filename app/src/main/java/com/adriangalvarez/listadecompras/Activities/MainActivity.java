package com.adriangalvarez.listadecompras.Activities;

import com.adriangalvarez.listadecompras.Adapters.ItemAdapter;
import com.adriangalvarez.listadecompras.Adapters.TotalItemAdapter;
import com.adriangalvarez.listadecompras.Bussiness.ItemBL;
import com.adriangalvarez.listadecompras.R;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity{

	public static final String BACKUP_FILE = "ListaDeCompras.backup";
	private final String BACKUP_DIR = "/ListaDeCompras";
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

	@Override
	protected void onCreate( Bundle savedInstanceState ){
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_main );

		ActionBar actionBar = getSupportActionBar();
		actionBar.setTitle( R.string.app_name );
		actionBar.setDisplayShowHomeEnabled( true );
		actionBar.setIcon( R.drawable.shoppingcart );

		listaTotal = new ArrayList<>();
		listaCompras = new ArrayList<>();
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
				AlertDialogAddEditItem( descripcion,  position, true );
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
				AlertDialogAddEditItem( "", -1, false );
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

	private void AlertDialogAddEditItem( final String descripcion, int position, final boolean isEditing ){
		AlertDialog.Builder builder = new AlertDialog.Builder( MainActivity.this );
		View view = LayoutInflater.from( MainActivity.this ).inflate( R.layout.dia_add_edit_item, null );

		final CheckBox checkBoxListaCompras;
		final EditText editTextItem = view.findViewById( R.id.editTextDescripcionItem );
		boolean cancelarAlert = false;

		if( isEditing ){
			ItemBL itemBLAnt = new ItemBL( listaTotal.get( position ) );
			if( listaCompras.contains( itemBLAnt ) ){
				cancelarAlert = true;
				Toast.makeText( MainActivity.this, R.string.errorItemEnListaCompras, Toast.LENGTH_SHORT ).show();
			}
			else{
				editTextItem.setText( descripcion );
				builder.setTitle( R.string.addItemActivityTitleEdit );
			}
		}else
			builder.setTitle( R.string.addItemActivityTitle );

		if( !cancelarAlert ){
			checkBoxListaCompras = view.findViewById( R.id.checkBoxListaComprasItem );

			editTextItem.setHint( R.string.addItemActivityTvAddItem );

			builder.setView( view );
			builder.setPositiveButton( R.string.buttonAceptar, new DialogInterface.OnClickListener(){
				@Override
				public void onClick( DialogInterface dialog, int which ){
					String descripcionNueva = editTextItem.getText().toString();
					if( descripcionNueva.length() == 0 )
						Toast.makeText( MainActivity.this, R.string.errorDescripcionVacia, Toast.LENGTH_SHORT ).show();
					else{
						int iCant = 0;
						if( checkBoxListaCompras.isChecked() )
							iCant = 1;
						ItemBL newItem = new ItemBL( descripcionNueva, iCant );
						AddItemToAdapterTotal( newItem, isEditing, descripcion );
					}
				}
			} );

			builder.show();
		}
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
			case R.id.menu_export_bbdd:
				ExportBBDD();
				return true;
			case R.id.menu_import_bbdd:
				ImportBBDD();
				return true;
			default:
				return super.onOptionsItemSelected( item );
		}
	}

	private void ExportBBDD(){
		if( Environment.getExternalStorageState().equals( Environment.MEDIA_MOUNTED ) ){
			try{
				File file = new File( Environment.getExternalStoragePublicDirectory( Environment.DIRECTORY_DOWNLOADS ), BACKUP_DIR );
				if( !file.exists() )
					file.mkdirs();
				File backupFile = new File( file, BACKUP_FILE );
				FileWriter fileWriter = new FileWriter( backupFile );
				fileWriter.append( ItemBL.getDataForBackup( MainActivity.this ) );
				fileWriter.flush();
				fileWriter.close();

				Toast.makeText( MainActivity.this, getString( R.string.exportOk ), Toast.LENGTH_SHORT ).show();
			}catch( FileNotFoundException e ){
				Toast.makeText( MainActivity.this, getString( R.string.fileNotFound ), Toast.LENGTH_SHORT ).show();
				e.printStackTrace();
			}catch( IOException e ){
				Toast.makeText( MainActivity.this, getString( R.string.errorUnknown ), Toast.LENGTH_SHORT ).show();
				e.printStackTrace();
			}
		}else{
			Toast.makeText( MainActivity.this, R.string.errorMediaNotMounted , Toast.LENGTH_SHORT ).show();
		}
	}

	private void ImportBBDD(){
		if( Environment.getExternalStorageState().equals( Environment.MEDIA_MOUNTED ) ){
			FileInputStream inputStream = null;
			BufferedReader bufferedReader = null;
			String line = null;

			try{
				inputStream = new FileInputStream( new File( Environment.getExternalStoragePublicDirectory( Environment.DIRECTORY_DOWNLOADS ) + "/" + BACKUP_DIR + "/" + BACKUP_FILE) );
				InputStreamReader streamReader = new InputStreamReader( inputStream );
				bufferedReader = new BufferedReader( streamReader );
				StringBuilder builder = new StringBuilder();

				while( ( line = bufferedReader.readLine() ) != null ){
					builder.append( line ).append( System.lineSeparator() );
				}

				ItemBL.getDataFromBackup( MainActivity.this, builder.toString() );

				Toast.makeText( MainActivity.this, getString( R.string.importOk ), Toast.LENGTH_SHORT ).show();
				listaCompras.clear();
				listaTotal.clear();
				InitListaCompras();
				OrdenarAdapterTotal();
				OrdenarAdapterCompras();
			}catch( FileNotFoundException e ){
				Toast.makeText( MainActivity.this, R.string.fileNotFound, Toast.LENGTH_SHORT ).show();
			}catch( IOException e ){
				Toast.makeText( MainActivity.this, R.string.errorUnknown, Toast.LENGTH_SHORT ).show();
			}finally{
				if( inputStream != null )
					try{
						inputStream.close();
					}catch( IOException e ){
						Toast.makeText( MainActivity.this, R.string.errorUnknown, Toast.LENGTH_SHORT ).show();
					}
				if( bufferedReader != null )
					try{
						bufferedReader.close();
					}catch( IOException e ){
						Toast.makeText( MainActivity.this, R.string.errorUnknown, Toast.LENGTH_SHORT ).show();
					}
			}
		}else{
			Toast.makeText( MainActivity.this, R.string.errorMediaNotMounted, Toast.LENGTH_SHORT ).show();
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

	private void AddItemToAdapterTotal( ItemBL item, boolean isEditing, String descripcionAnt ){
		String descripcion = item.getDescripcion();
		if( !listaTotal.contains( descripcion ) ){
			listaTotal.add( descripcion );

			if( isEditing ){
				item.modify( MainActivity.this, descripcionAnt );
				if( listaTotal.contains( descripcionAnt ) ){
					listaTotal.remove( descripcionAnt );
				}
			}
			else
				item.add( MainActivity.this );

			OrdenarAdapterTotal();

			if( item.getCantidad() > 0 ){
				AddItemToAdapterCompras( item );
				ToggleListView();
			}

			Toast.makeText( MainActivity.this, descripcion + " " + getString( R.string.itemAgregado ), Toast.LENGTH_SHORT ).show();
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
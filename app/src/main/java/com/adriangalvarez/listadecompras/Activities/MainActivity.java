package com.adriangalvarez.listadecompras.Activities;

import com.adriangalvarez.listadecompras.Adapters.PagerAdapter;
import com.adriangalvarez.listadecompras.Bussiness.ItemBL;
import com.adriangalvarez.listadecompras.Fragments.ComprasFragment;
import com.adriangalvarez.listadecompras.Fragments.TotalFragment;
import com.adriangalvarez.listadecompras.R;

import android.os.Environment;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity implements TotalFragment.IOnFragmentInteractionListener{

	public static final String BACKUP_FILE = "ListaDeCompras.backup";
	private final String BACKUP_DIR = "/ListaDeCompras";

	private PagerAdapter adapter;

	@Override
	protected void onCreate( Bundle savedInstanceState ){
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_main );

		Toolbar toolbar = findViewById( R.id.toolbar );
		toolbar.setTitle( R.string.app_name);
		setSupportActionBar( toolbar );

		final ViewPager viewPager = findViewById( R.id.viewPager );

		TabLayout tabLayout = findViewById( R.id.tabLayout );
		tabLayout.addTab( tabLayout.newTab().setText( R.string.tabComprasTitle ) );
		tabLayout.addTab( tabLayout.newTab().setText( R.string.tabTotalTitle ) );
		tabLayout.setTabGravity( TabLayout.GRAVITY_FILL );
		tabLayout.addOnTabSelectedListener( new TabLayout.OnTabSelectedListener(){
			@Override
			public void onTabSelected( TabLayout.Tab tab ){
				viewPager.setCurrentItem( tab.getPosition() );
			}

			@Override
			public void onTabUnselected( TabLayout.Tab tab ){

			}

			@Override
			public void onTabReselected( TabLayout.Tab tab ){

			}
		} );

		adapter = new PagerAdapter( getSupportFragmentManager(), tabLayout.getTabCount() );
		viewPager.setAdapter( adapter );
		viewPager.addOnPageChangeListener( new TabLayout.TabLayoutOnPageChangeListener( tabLayout ) );
	}

	@Override
	public boolean onCreateOptionsMenu( Menu menu ){
		getMenuInflater().inflate( R.menu.options_menu, menu );
		return super.onCreateOptionsMenu( menu );
	}

	@Override
	public boolean onOptionsItemSelected( MenuItem item ){
		switch( item.getItemId() ){
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
				ComprasFragment comprasFragment = ( ComprasFragment ) getSupportFragmentManager().getFragments().get( 0 );
				comprasFragment.ActualizarListaCompras();
				TotalFragment totalFragment = ( TotalFragment ) getSupportFragmentManager().getFragments().get( 1 );
				totalFragment.ActualizarListaTotal();
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

	@Override
	public void OnFragmentInteraction( String articulo ){
		ComprasFragment comprasFragment = ( ComprasFragment ) getSupportFragmentManager().getFragments().get( 0 );
		comprasFragment.AddItemToAdapterCompras( articulo );
	}
}
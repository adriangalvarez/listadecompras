package com.adriangalvarez.listadecompras.Fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.adriangalvarez.listadecompras.Adapters.TotalItemAdapter;
import com.adriangalvarez.listadecompras.Bussiness.ItemBL;
import com.adriangalvarez.listadecompras.R;
import com.adriangalvarez.listadecompras.Utils.AlertDialog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

//TODO: ver permisos de camara y escritura en tiempo de ejecucion

public class TotalFragment extends Fragment{

	private static final int GALLERY = 100;
	private static final int IMAGE_CAPTURE = 101;

	private ImageView imageViewDialog;
	private ImageButton imgDialogClose;
	private ImageButton imgDialogCamera;
	private ImageButton imgDialogGallery;
	private ImageButton imgDialogDelete;

	private ItemBL tempItem;

	private List< ItemBL > listaTotal;

	private Context context;
	IOnFragmentInteractionListener sendArticulo;

	private RecyclerView mRecyclerTotal;
	private RecyclerView.LayoutManager mLayoutManagerTotal;
	private TotalItemAdapter mAdapterTotal;

	private android.support.v7.widget.SearchView searchItem;
	private FloatingActionButton buttonAdd;

	public TotalFragment(){
		// Required empty public constructor
	}

	public interface IOnFragmentInteractionListener{
		void OnFragmentInteraction( ItemBL articulo );
	}

	@Override
	public void onAttach( Context context ){
		super.onAttach( context );
		sendArticulo = ( IOnFragmentInteractionListener ) getActivity();
	}

	@Override
	public View onCreateView( LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState ){

		this.context = getContext();

		// Inflate the layout for this fragment
		View view = inflater.inflate( R.layout.fragment_total, container, false );

		searchItem = view.findViewById( R.id.searchItem );
		searchItem.setOnQueryTextListener( new android.support.v7.widget.SearchView.OnQueryTextListener(){
			@Override
			public boolean onQueryTextSubmit( String query ){
				return false;
			}

			@Override
			public boolean onQueryTextChange( String newText ){
				mAdapterTotal.getFilter().filter( newText );
				return false;
			}
		} );

		buttonAdd = view.findViewById( R.id.buttonAdd );
		buttonAdd.setOnClickListener( new View.OnClickListener(){
			@Override
			public void onClick( View v ){
				AlertDialog alertDialog = new AlertDialog();
				alertDialog.setOnAceptarClickListener( new AlertDialog.IAceptar(){
					@Override
					public void Aceptar( ItemBL itemBL ){
						if( itemBL.getDescripcion().length() == 0 )
							Toast.makeText( context, R.string.errorDescripcionVacia, Toast.LENGTH_SHORT ).show();
						else{
							if( ItemBL.existe( context, itemBL.getDescripcion() ) ){
								Toast.makeText( context, R.string.itemYaExiste, Toast.LENGTH_SHORT ).show();
							}else{
								ItemBL newItem = new ItemBL( itemBL.getDescripcion(), itemBL.getCantidad(), itemBL.getRutaImagen() );
								newItem.setId( newItem.add( context ) );
								listaTotal.add( newItem );
								OrdenarAdapterTotal();
								if( newItem.getCantidad() > 0 ){
									sendArticulo.OnFragmentInteraction( newItem );
								}
							}
						}
					}
				} );
				alertDialog.AlertDialogAddEditItem( context, null, false );
				OrdenarAdapterTotal();
			}
		} );

		listaTotal = new ArrayList<>();
		InitListaTotal();

		mRecyclerTotal = view.findViewById( R.id.recycler_total );
		mLayoutManagerTotal = new LinearLayoutManager( context );
		mAdapterTotal = new TotalItemAdapter( R.layout.total_row_item, listaTotal, new TotalItemAdapter.OnItemClickListener(){
			@Override
			public void OnItemClickListener( ItemBL itemBL, final int position ){
				if( ItemBL.existeEnCompras( context, itemBL.getId() ) ){
					Toast.makeText( context, R.string.errorItemEnListaCompras, Toast.LENGTH_SHORT ).show();
				}else{
					AlertDialog alertDialog = new AlertDialog();
					alertDialog.setOnAceptarClickListener( new AlertDialog.IAceptar(){
						@Override
						public void Aceptar( ItemBL itemBLinterno ){
							if( itemBLinterno.getDescripcion().length() == 0 )
								Toast.makeText( context, R.string.errorDescripcionVacia, Toast.LENGTH_SHORT ).show();
							else{
								if( ItemBL.existe( context, itemBLinterno.getDescripcion() ) ){
									Toast.makeText( context, R.string.itemYaExiste, Toast.LENGTH_SHORT ).show();
								}else{
									itemBLinterno.modify( context );
									listaTotal.set( position, itemBLinterno );
									OrdenarAdapterTotal();
									if( itemBLinterno.getCantidad() > 0 ){
										sendArticulo.OnFragmentInteraction( itemBLinterno );
									}
								}
							}
						}
					} );
					alertDialog.AlertDialogAddEditItem( context, itemBL, true );
				}
			}

			@Override
			public void OnItemClickAdd( ItemBL itemBL, int position ){
				sendArticulo.OnFragmentInteraction( itemBL );
			}

			@Override
			public void onItemPictureAddClick( ItemBL itemBL, int position ){
				tempItem = itemBL;

				final Dialog dialog = new Dialog( context );
				dialog.requestWindowFeature( Window.FEATURE_NO_TITLE );
				dialog.setContentView( R.layout.dialog_item_picture );
				dialog.getWindow().setBackgroundDrawable( new ColorDrawable( android.graphics.Color.TRANSPARENT ) );
				dialog.setCancelable( true );

				imgDialogClose = dialog.findViewById( R.id.imgDialogClose );
				imgDialogCamera = dialog.findViewById( R.id.imgDialogCamera );
				imgDialogGallery = dialog.findViewById( R.id.imgDialogGallery );
				imgDialogDelete = dialog.findViewById( R.id.imgDialogDelete );

				imgDialogClose.setOnClickListener( new View.OnClickListener(){
					@Override
					public void onClick( View view ){
						dialog.dismiss();
					}
				} );

				// Mostrar la foto si existe, o una imagen genérica si no hay foto
				imageViewDialog = dialog.findViewById( R.id.imgDialogPicture );
				File imageFile = new File( getAppImageDir() + itemBL.getRutaImagen() );
				if( imageFile.exists() && !imageFile.isDirectory() )
					imageViewDialog.setImageURI( Uri.fromFile( imageFile ) );

				// Borrar la foto del producto
				// TODO: agregar confirmación
				imgDialogDelete.setOnClickListener( new View.OnClickListener(){
					@Override
					public void onClick( View v ){
						updateDB( "" );
						deleteImage();
						dialog.dismiss();
					}
				} );

				// Mostrar galeria para elegir imagen
				imgDialogGallery.setOnClickListener( new View.OnClickListener(){
					@Override
					public void onClick( View v ){
						Intent intent = new Intent( Intent.ACTION_PICK );
						intent.setType( "image/*" );
						String[] mimeTypes = { "image/jpeg", "image/png" };
						intent.putExtra( Intent.EXTRA_MIME_TYPES, mimeTypes );
						startActivityForResult( intent, GALLERY );
					}
				} );

				// Mostrar camara para sacar nueva foto
				imgDialogCamera.setOnClickListener( new View.OnClickListener(){
					@Override
					public void onClick( View v ){
						Intent takePictureIntent = new Intent( MediaStore.ACTION_IMAGE_CAPTURE );
						if( takePictureIntent.resolveActivity( context.getPackageManager() ) != null ){
							File photoFile = null;
							try {
								photoFile = createImageFile();
							} catch (IOException ex) {
								// Error occurred while creating the File
							}

							if (photoFile != null) {
								Uri photoURI = FileProvider.getUriForFile(context,
										"com.adriangalvarez.listadecompras.fileprovider",
										photoFile);
								takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
								startActivityForResult(takePictureIntent, IMAGE_CAPTURE);
							}
						}
					}
				} );

				dialog.show();
			}
		} );

		OrdenarAdapterTotal();
		mRecyclerTotal.setLayoutManager( mLayoutManagerTotal );
		mRecyclerTotal.setAdapter( mAdapterTotal );
		mRecyclerTotal.addOnScrollListener( new RecyclerView.OnScrollListener(){
			@Override
			public void onScrolled( RecyclerView recyclerView, int dx, int dy ){
				super.onScrolled( recyclerView, dx, dy );
				if( dy > 0 && buttonAdd.getVisibility() == View.VISIBLE )
					buttonAdd.hide();
				else if( dy < 0 && buttonAdd.getVisibility() != View.VISIBLE )
					buttonAdd.show();
			}
		} );

		return view;
	}

	public void ActualizarListaTotal(){
		listaTotal.clear();
		InitListaTotal();
		OrdenarAdapterTotal();
	}

	private void InitListaTotal(){
		listaTotal.addAll( ItemBL.getAll( getContext() ) );
	}

	private void OrdenarAdapterTotal(){
		Collections.sort( listaTotal, new Comparator< ItemBL >(){
			@Override
			public int compare( ItemBL o1, ItemBL o2 ){
				return o1.getDescripcion().compareTo( o2.getDescripcion() );
			}
		} );

		mAdapterTotal.notifyDataSetChanged();
	}

	@Override
	public void onActivityResult( int requestCode, int resultCode, Intent data ){
		super.onActivityResult( requestCode, resultCode, data );
		if( resultCode == Activity.RESULT_OK )
			switch( requestCode ){
				case GALLERY:
					//data.getData devuelve la URI para la imagen seleccionada
					Uri selectedImage = data.getData();
					imageViewDialog.setImageURI( selectedImage );

					String realPath = getRealPathFromURI( selectedImage );
					String filename = realPath.substring( realPath.lastIndexOf( File.separator ) + 1 );
					copyToAppImageDir( realPath, filename );
					updateDB( filename );
					tempItem.setRutaImagen( filename );

					//TODO: esconder botones, dejar solo el de CERRAR
					break;
				case IMAGE_CAPTURE:
					imageViewDialog.setImageURI( Uri.fromFile( new File( getAppImageDir() + tempItem.getRutaImagen() ) ) );
					tempItem.updateRutaImagen( context );

					//TODO: esconder botones, dejar solo el de CERRAR
					break;
			}
		else
			switch( requestCode ){
				case IMAGE_CAPTURE:
					deleteImage();
					break;
			}
	}

	private void deleteImage(){
		File temp = new File( getAppImageDir() + tempItem.getRutaImagen() );
		temp.delete();
	}

	private void updateDB( String filename ){
		tempItem.setRutaImagen( filename );
		tempItem.updateRutaImagen( context );
	}

	private void copyToAppImageDir( String realPathFrom, String filenameTo ){
		File from = new File( realPathFrom );
		File to = new File( getAppImageDir() + filenameTo );

		try{
			copyFile( from, to );
		}catch( IOException e ){
			e.printStackTrace();
		}
	}

	private String getAppImageDir(){
		return Environment.getExternalStoragePublicDirectory( Environment.DIRECTORY_PICTURES ) + File.separator + "ListaDeCompras" + File.separator;
	}

	private String getRealPathFromURI( Uri contentUri ){
		Cursor cursor = null;
		try{
			String[] proj = { MediaStore.Images.Media.DATA };
			cursor = context.getContentResolver().query( contentUri, proj, null, null, null );
			int column_index = cursor.getColumnIndexOrThrow( MediaStore.Images.Media.DATA );
			cursor.moveToFirst();
			return cursor.getString( column_index );
		}catch( Exception e ){
			throw e;
		}finally{
			if( cursor != null ){
				cursor.close();
			}
		}
	}

	private void copyFile( File sourceFile, File destFile ) throws IOException{
		if( !sourceFile.exists() ){
			return;
		}

		if( !destFile.getParentFile().exists() )
			destFile.getParentFile().mkdirs();

		FileChannel source;
		FileChannel destination;
		source = new FileInputStream( sourceFile ).getChannel();
		destination = new FileOutputStream( destFile ).getChannel();
		if( source != null ){
			destination.transferFrom( source, 0, source.size() );
		}
		if( source != null ){
			source.close();
		}
		destination.close();
	}

	private File createImageFile() throws IOException {
		// Create an image file name
		String imageFileName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		File image = File.createTempFile(
				imageFileName,  /* prefix */
				".jpg",         /* suffix */
				new File( getAppImageDir() )      /* directory */
		);

		tempItem.setRutaImagen( image.getName() );
		return image;
	}

}

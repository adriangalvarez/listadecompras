package com.adriangalvarez.listadecompras.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.adriangalvarez.listadecompras.Adapters.TotalItemAdapter;
import com.adriangalvarez.listadecompras.Bussiness.ItemBL;
import com.adriangalvarez.listadecompras.R;
import com.adriangalvarez.listadecompras.Utils.AlertDialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class TotalFragment extends Fragment{

	private List< String > listaTotal;

	private Context context;
	IOnFragmentInteractionListener sendArticulo;

	private RecyclerView mRecyclerTotal;
	private RecyclerView.LayoutManager mLayoutManagerTotal;
	private TotalItemAdapter mAdapterTotal;

	private FloatingActionButton buttonAdd;

	public TotalFragment(){
		// Required empty public constructor
	}

	public interface IOnFragmentInteractionListener{
		void OnFragmentInteraction( String articulo );
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

		buttonAdd = view.findViewById( R.id.buttonAdd);
		buttonAdd.setOnClickListener( new View.OnClickListener(){
			@Override
			public void onClick( View v ){
				AlertDialog alertDialog = new AlertDialog();
				alertDialog.setOnAceptarClickListener( new AlertDialog.IAceptar(){
					@Override
					public void Aceptar( String descripcion, int cantidad ){
						if( descripcion.length() == 0 )
							Toast.makeText( context, R.string.errorDescripcionVacia, Toast.LENGTH_SHORT ).show();
						else{
							if( ItemBL.existe( context, descripcion ) ){
								Toast.makeText( context, R.string.itemYaExiste, Toast.LENGTH_SHORT ).show();
							}else{
								ItemBL newItem = new ItemBL( descripcion, cantidad );
								newItem.add( context );
								listaTotal.add( descripcion );
								OrdenarAdapterTotal();
								if( cantidad > 0 ){
									sendArticulo.OnFragmentInteraction( descripcion );
								}
							}
						}
					}
				} );
				alertDialog.AlertDialogAddEditItem( context, "", -1, false );
				OrdenarAdapterTotal();
			}
		} );

		listaTotal = new ArrayList<>();
		InitListaTotal();

		mRecyclerTotal = view.findViewById( R.id.recycler_total );
		mLayoutManagerTotal = new LinearLayoutManager( context );
		mAdapterTotal = new TotalItemAdapter( R.layout.total_row_item, listaTotal, new TotalItemAdapter.OnItemClickListener(){
			@Override
			public void OnItemClickListener( String descripcion, final int position ){
				if( ItemBL.existeEnCompras( context,  descripcion )){
					Toast.makeText( context,  R.string.errorItemEnListaCompras, Toast.LENGTH_SHORT).show();
				}else{
					AlertDialog alertDialog = new AlertDialog();
					alertDialog.setOnAceptarClickListener( new AlertDialog.IAceptar(){
						@Override
						public void Aceptar( String descripcion, int cantidad ){
							if( descripcion.length() == 0 )
								Toast.makeText( context, R.string.errorDescripcionVacia, Toast.LENGTH_SHORT ).show();
							else{
								if( ItemBL.existe( context, descripcion ) ){
									Toast.makeText( context, R.string.itemYaExiste, Toast.LENGTH_SHORT ).show();
								}else{
									ItemBL newItem = new ItemBL( descripcion, cantidad );
									newItem.modify( context, listaTotal.get( position ) );
									listaTotal.set( position, descripcion );
									OrdenarAdapterTotal();
									if( cantidad > 0 ){
										sendArticulo.OnFragmentInteraction( descripcion );
									}
								}
							}
						}
					} );
					alertDialog.AlertDialogAddEditItem( context, descripcion,  position, true );
				}
			}

			@Override
			public void OnItemClickAdd( String descripcion, int position ){
				sendArticulo.OnFragmentInteraction( descripcion );
			}
		} );

		OrdenarAdapterTotal();
		mRecyclerTotal.setLayoutManager( mLayoutManagerTotal );
		mRecyclerTotal.setAdapter( mAdapterTotal );

		return view;
	}

	public void ActualizarListaTotal(){
		listaTotal.clear();
		InitListaTotal();
		OrdenarAdapterTotal();
	}

	private void InitListaTotal(){
		Map< String, ? > allEntries = ItemBL.getAll( getContext() );
		for( Map.Entry< String, ? > entry : allEntries.entrySet() ){
			listaTotal.add( entry.getKey() );
		}
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
}

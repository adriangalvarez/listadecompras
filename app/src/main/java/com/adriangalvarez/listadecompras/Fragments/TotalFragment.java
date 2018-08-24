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

public class TotalFragment extends Fragment{

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

		buttonAdd = view.findViewById( R.id.buttonAdd);
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
								ItemBL newItem = new ItemBL( itemBL.getDescripcion(), itemBL.getCantidad() );
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
					Toast.makeText( context,  R.string.errorItemEnListaCompras, Toast.LENGTH_SHORT).show();
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
					alertDialog.AlertDialogAddEditItem( context, itemBL,  true );
				}
			}

			@Override
			public void OnItemClickAdd( ItemBL itemBL, int position ){
				sendArticulo.OnFragmentInteraction( itemBL );
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
				else
					if( dy < 0 && buttonAdd.getVisibility() != View.VISIBLE )
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
		});

		mAdapterTotal.notifyDataSetChanged();
	}
}

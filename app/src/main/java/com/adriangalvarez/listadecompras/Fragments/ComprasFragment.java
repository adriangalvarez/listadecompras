package com.adriangalvarez.listadecompras.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.adriangalvarez.listadecompras.Adapters.ItemAdapter;
import com.adriangalvarez.listadecompras.Bussiness.ItemBL;
import com.adriangalvarez.listadecompras.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ComprasFragment extends Fragment{

	private Context context;

	private List< ItemBL > listaCompras;
	private ItemAdapter mAdapterCompras;
	private RecyclerView mRecyclerCompras;
	private RecyclerView.LayoutManager mLayoutManager;

	private FloatingActionButton buttonShare;

	public ComprasFragment(){
		// Required empty public constructor
	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ){
		View view = inflater.inflate( R.layout.fragment_compras, container, false );

		this.context = getContext();

		listaCompras = new ArrayList<>();
		InitListaCompras();

		buttonShare = view.findViewById( R.id.buttonShare );
		buttonShare.setOnClickListener( new View.OnClickListener(){
			@Override
			public void onClick( View v ){
				Intent intentShare = new Intent( Intent.ACTION_SEND );
				intentShare.setType( "text/plain" );
				intentShare.putExtra( Intent.EXTRA_TEXT, GenerarListaComprasToShare() );
				startActivity( Intent.createChooser( intentShare, "Enviar por..." ) );
			}
		} );

		mRecyclerCompras = view.findViewById( R.id.recycler_compras );
		mLayoutManager = new LinearLayoutManager( context );
		mAdapterCompras = new ItemAdapter( listaCompras, R.layout.listview_row_item, new ItemAdapter.onItemClickListener(){
			@Override
			public void onItemClick( ItemBL itemBL, int position ){
				listaCompras.remove( itemBL );
				mAdapterCompras.notifyItemRemoved( position );
				itemBL.deleteFromCompras( context );
			}

			@Override
			public void onItemAddClick( ItemBL itemBL, int position ){
				itemBL.addCantidad( context );
				mAdapterCompras.notifyItemChanged( position );
			}
		} );

		OrdenarAdapterCompras();
		mRecyclerCompras.setLayoutManager( mLayoutManager );
		mRecyclerCompras.setAdapter( mAdapterCompras );
		mRecyclerCompras.addOnScrollListener( new RecyclerView.OnScrollListener(){
			@Override
			public void onScrolled( RecyclerView recyclerView, int dx, int dy ){
				super.onScrolled( recyclerView, dx, dy );
				if( dy > 0 && buttonShare.getVisibility() == View.VISIBLE )
					buttonShare.hide();
				else
					if( dy < 0 && buttonShare.getVisibility() != View.VISIBLE )
						buttonShare.show();
			}
		} );

		return view;
	}

	private String GenerarListaComprasToShare(){
		StringBuilder compartir = new StringBuilder( "" );
		for( ItemBL temp : listaCompras )
			compartir.append( String.valueOf( temp.getCantidad() ) + " " + temp.getDescripcion() + "\n" );
		return compartir.toString();
	}

	private void InitListaCompras(){
		for( ItemBL entry : ItemBL.getAll( context ) ){
			if( entry.getCantidad() > 0 ){
				listaCompras.add( entry );
			}
		}
	}

	public void ActualizarListaCompras(){
		listaCompras.clear();
		InitListaCompras();
		OrdenarAdapterCompras();
	}

	public void OrdenarAdapterCompras(){
		Collections.sort( listaCompras, new Comparator< ItemBL >(){
			@Override
			public int compare( ItemBL o1, ItemBL o2 ){
				return o1.getDescripcion().compareTo( o2.getDescripcion() );
			}
		});
		mAdapterCompras.notifyDataSetChanged();
	}

	public void AddItemToAdapterCompras( ItemBL itemBL ){
		if( !listaCompras.contains( itemBL ) ){
			listaCompras.add( itemBL );
			itemBL.setCantidad( 1 );
			itemBL.modify( context );
			Toast.makeText( context, itemBL.getDescripcion() + " " + getString( R.string.itemAgregado ), Toast.LENGTH_SHORT ).show();
			OrdenarAdapterCompras();
		}else{
			Toast.makeText( context, R.string.itemYaExiste, Toast.LENGTH_SHORT ).show();
		}
	}
}

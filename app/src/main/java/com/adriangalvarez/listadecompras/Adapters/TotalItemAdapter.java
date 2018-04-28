package com.adriangalvarez.listadecompras.Adapters;

import com.adriangalvarez.listadecompras.R;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by adriangalvarez on 09/01/2018.
 */

public class TotalItemAdapter extends RecyclerView.Adapter< TotalItemAdapter.ViewHolder > implements Filterable{

	private int mLayout;
	private List< String > mLista;
	private OnItemClickListener mOnItemClickListener;
	private List< String > mListaFiltrada;
	private ItemFilter mFilter;

	public TotalItemAdapter( int layout, List<String> lista, OnItemClickListener onItemClickListener ){
		mLayout = layout;
		mLista = lista;
		mListaFiltrada = lista;
		mOnItemClickListener = onItemClickListener;
		mFilter = new ItemFilter();
	}

	@Override
	public Filter getFilter(){
		return mFilter;
	}

	public interface OnItemClickListener{
		void OnItemClickListener( String descripcion,  int position );
		void OnItemClickAdd( String descripcion, int position );
	}

	@Override
	public ViewHolder onCreateViewHolder( ViewGroup parent, int viewType ){
		return new ViewHolder( LayoutInflater.from( parent.getContext() ).inflate( mLayout, parent, false ) );
	}

	@Override
	public void onBindViewHolder( ViewHolder holder, int position ){
		holder.bind( mListaFiltrada.get( position ), mOnItemClickListener );
	}

	@Override
	public int getItemCount(){
		return mListaFiltrada.size();
	}

	public static class ViewHolder extends RecyclerView.ViewHolder{

		private TextView textViewDescripcion;
		private ImageButton imageButtonEditar;

		public ViewHolder( View itemView ){
			super( itemView );
			textViewDescripcion = itemView.findViewById( R.id.textViewTotalDescripcion );
			imageButtonEditar = itemView.findViewById( R.id.imageEdit );
		}

		public void bind( final String descripcion, final OnItemClickListener listener ){
			textViewDescripcion.setText( descripcion );
			textViewDescripcion.setOnClickListener( new View.OnClickListener(){
				@Override
				public void onClick( View v ){
					listener.OnItemClickAdd( descripcion, getAdapterPosition() );
				}
			} );
			imageButtonEditar.setOnClickListener( new View.OnClickListener(){
				@Override
				public void onClick( View v ){
					listener.OnItemClickListener( descripcion, getAdapterPosition() );
				}
			} );
		}
	}

	private class ItemFilter extends Filter{

		@Override
		protected FilterResults performFiltering( CharSequence constraint ){
			String filterString = constraint.toString().toLowerCase();
			int count = mLista.size();

			FilterResults results = new FilterResults();
			ArrayList<String> nlist = new ArrayList<>( count );

			String filterableString;
			for( int i = 0; i < count ; i++ ){
				filterableString = mLista.get( i );
				if( filterableString.toLowerCase().contains( filterString ) )
					nlist.add( filterableString );
			}

			results.values = nlist;
			results.count = nlist.size();
			return results;
		}

		@SuppressWarnings( "unchecked" )
		@Override
		protected void publishResults( CharSequence constraint, FilterResults results ){
			mListaFiltrada = ( ArrayList< String> ) results.values;
			notifyDataSetChanged();
		}
	}
}

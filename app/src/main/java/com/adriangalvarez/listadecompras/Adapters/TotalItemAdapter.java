package com.adriangalvarez.listadecompras.Adapters;

import com.adriangalvarez.listadecompras.Bussiness.ItemBL;
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
	private List< ItemBL > mLista;
	private OnItemClickListener mOnItemClickListener;
	private List< ItemBL > mListaFiltrada;
	private ItemFilter mFilter;

	public TotalItemAdapter( int layout, List< ItemBL > lista, OnItemClickListener onItemClickListener ){
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
		void OnItemClickListener( ItemBL itemBL, int position );
		void OnItemClickAdd( ItemBL itemBL, int position );
		void onItemPictureAddClick( ItemBL itemBL, int position );
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
		private ImageButton imageButtonImagen;

		public ViewHolder( View itemView ){
			super( itemView );
			textViewDescripcion = itemView.findViewById( R.id.textViewTotalDescripcion );
			imageButtonEditar = itemView.findViewById( R.id.imageEdit );
			imageButtonImagen = itemView.findViewById( R.id.imagePicture );
		}

		public void bind( final ItemBL itemBL, final OnItemClickListener listener ){
			textViewDescripcion.setText( itemBL.getDescripcion() );
			textViewDescripcion.setOnClickListener( new View.OnClickListener(){
				@Override
				public void onClick( View v ){
					listener.OnItemClickAdd( itemBL, getAdapterPosition() );
				}
			} );
			imageButtonEditar.setOnClickListener( new View.OnClickListener(){
				@Override
				public void onClick( View v ){
					listener.OnItemClickListener( itemBL, getAdapterPosition() );
				}
			} );
			imageButtonImagen.setOnClickListener( new View.OnClickListener(){
				@Override
				public void onClick( View v ){
					listener.onItemPictureAddClick( itemBL, getAdapterPosition() );
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
			List< ItemBL > nlist = new ArrayList<>( count );

			for( ItemBL itemBL : mLista ){
				if( itemBL.getDescripcion().toLowerCase().contains( filterString ) )
					nlist.add( itemBL );
			}

			results.values = nlist;
			results.count = nlist.size();
			return results;
		}

		@SuppressWarnings( "unchecked" )
		@Override
		protected void publishResults( CharSequence constraint, FilterResults results ){
			mListaFiltrada = ( ArrayList< ItemBL > ) results.values;
			notifyDataSetChanged();
		}
	}
}

package com.adriangalvarez.listadecompras.Adapters;

import com.adriangalvarez.listadecompras.Bussiness.ItemBL;
import com.adriangalvarez.listadecompras.R;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

/**
 * Created by adriangalvarez on 13/11/2017.
 */

public class ItemAdapter extends RecyclerView.Adapter< ItemAdapter.ViewHolder >{

	private int mLayout;
	private List< ItemBL > itemBLS;
	private onItemClickListener mOnItemClickListener;

	public interface onItemClickListener{
		void onItemClick( ItemBL itemBL, int position );
		void onItemAddClick( ItemBL itemBL, int position );
		void onItemPictureViewClick( ItemBL itemBL, int position );
	}

	public ItemAdapter( List< ItemBL > data, int layout, onItemClickListener listener ){
		this.itemBLS = data;
		this.mLayout = layout;
		this.mOnItemClickListener = listener;
	}

	@Override
	public ViewHolder onCreateViewHolder( ViewGroup parent, int viewType ){
		View v = LayoutInflater.from( parent.getContext() ).inflate( mLayout, parent, false );
		return new ViewHolder( v );
	}

	@Override
	public void onBindViewHolder( ViewHolder holder, int position ){
		holder.bind( itemBLS.get( position ), mOnItemClickListener );
	}

	@Override
	public int getItemCount(){
		return itemBLS.size();
	}

	public static class ViewHolder extends RecyclerView.ViewHolder{
		private TextView textViewDescripcion;
		private TextView textViewCantidad;
		private ImageButton imageButtonImagen;
		private ImageButton imageButtonAdd;

		public ViewHolder( View view ){
			super( view );
			this.textViewCantidad = view.findViewById( R.id.textViewCantidad );
			this.textViewDescripcion = view.findViewById( R.id.textViewDescripcion );
			this.imageButtonImagen = view.findViewById( R.id.imagePictureView );
			this.imageButtonAdd = view.findViewById( R.id.imageAdd );
		}

		public void bind( final ItemBL itemBL, final onItemClickListener listener ){
			textViewDescripcion.setText( itemBL.getDescripcion() );
			textViewCantidad.setText( String.valueOf( itemBL.getCantidad() ) );
			imageButtonAdd.setOnClickListener( new View.OnClickListener(){
				@Override
				public void onClick( View v ){
					listener.onItemAddClick( itemBL, getAdapterPosition() );
				}
			} );
			itemView.setOnClickListener( new View.OnClickListener(){
				@Override
				public void onClick( View v ){
					listener.onItemClick( itemBL, getAdapterPosition() );
				}
			} );
			imageButtonImagen.setOnClickListener( new View.OnClickListener(){
				@Override
				public void onClick( View v ){
					listener.onItemPictureViewClick( itemBL, getAdapterPosition() );
				}
			} );
		}
	}
}

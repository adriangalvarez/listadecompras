package com.adriangalvarez.listadecompras.Adapters;

import com.adriangalvarez.listadecompras.Bussiness.ItemBL;
import com.adriangalvarez.listadecompras.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by adriangalvarez on 13/11/2017.
 */

public class ItemAdapter extends RecyclerView.Adapter< ItemAdapter.ViewHolder >{

	private final int MINIMUM_TEXT_SIZE = 14;
	private final int MAXIMUM_TEXT_SIZE = 22;

	private int mLayout;
	private int mTextSize;
	private List< ItemBL > itemBLS;
	private onItemClickListener mOnItemClickListener;

	public interface onItemClickListener{
		void onItemClick( ItemBL itemBL, int position );
		void onItemAddClick( ItemBL itemBL, int position );
		void onItemPictureViewClick( ItemBL itemBL, int position );
	}

	public ItemAdapter( int textSize, List< ItemBL > data, int layout, onItemClickListener listener ){
		this.itemBLS = data;
		this.mLayout = layout;
		this.mOnItemClickListener = listener;
		this.mTextSize = textSize;
	}

	@Override
	public ViewHolder onCreateViewHolder( ViewGroup parent, int viewType ){
		View v = LayoutInflater.from( parent.getContext() ).inflate( mLayout, parent, false );
		return new ViewHolder( v );
	}

	@Override
	public void onBindViewHolder( ViewHolder holder, int position ){
		holder.textViewDescripcion.setTextSize( TypedValue.COMPLEX_UNIT_SP, mTextSize );
		holder.textViewCantidad.setTextSize( TypedValue.COMPLEX_UNIT_SP, mTextSize );
		holder.bind( itemBLS.get( position ), mOnItemClickListener );
	}

	@Override
	public int getItemCount(){
		return itemBLS.size();
	}

	public void setTextSizes( Context context, boolean makeBigger, String prefsName ){
		boolean update = false;

		if( makeBigger ){
			if( mTextSize < ( MAXIMUM_TEXT_SIZE + 2 ) ){
				this.mTextSize += 2;
				update = true;
			}else
				Toast.makeText( context, R.string.biggest_text, Toast.LENGTH_SHORT ).show();
		}else{
			if( mTextSize > ( MINIMUM_TEXT_SIZE - 2 ) ){
				this.mTextSize -= 2;
				update = true;
			}else
				Toast.makeText( context, R.string.smallest_text, Toast.LENGTH_SHORT ).show();
		}

		if( update ){
			notifyDataSetChanged();
			SharedPreferences settings = context.getSharedPreferences(prefsName, MODE_PRIVATE);
			SharedPreferences.Editor editor = settings.edit();
			editor.putInt( "textSizes", mTextSize );
			editor.apply();
		}
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

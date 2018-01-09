package android.adriangalvarez.listadecompras.Adapters;

import android.adriangalvarez.listadecompras.R;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

/**
 * Created by adriangalvarez on 09/01/2018.
 */

public class TotalItemAdapter extends RecyclerView.Adapter< TotalItemAdapter.ViewHolder >{

	private int mLayout;
	private List< String > mLista;
	private OnItemClickListener mOnItemClickListener;

	public TotalItemAdapter( int layout, List<String> lista, OnItemClickListener onItemClickListener ){
		mLayout = layout;
		mLista = lista;
		mOnItemClickListener = onItemClickListener;
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
		holder.bind( mLista.get( position ), mOnItemClickListener );
	}

	@Override
	public int getItemCount(){
		return mLista.size();
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
}

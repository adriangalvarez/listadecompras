package com.adriangalvarez.listadecompras.Utils;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.adriangalvarez.listadecompras.Bussiness.ItemBL;
import com.adriangalvarez.listadecompras.R;

/**
 * Created by adriangalvarez on 27/03/2018.
 */

public class AlertDialog{
	private IAceptar aceptar;

	public void setOnAceptarClickListener( IAceptar listener ){
		aceptar = listener;
	}

	public interface IAceptar{
		void Aceptar( String descripcion, int cantidad );
	}

	public void AlertDialogAddEditItem( final Context context, final String descripcion, int position, final boolean isEditing ){
		android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder( context );
		View view = LayoutInflater.from( context ).inflate( R.layout.dia_add_edit_item, null );

		final CheckBox checkBoxListaCompras;
		final EditText editTextItem = view.findViewById( R.id.editTextDescripcionItem );

		if( isEditing ){
			editTextItem.setText( descripcion );
			builder.setTitle( R.string.addItemActivityTitleEdit );
		}else
			builder.setTitle( R.string.addItemActivityTitle );

		checkBoxListaCompras = view.findViewById( R.id.checkBoxListaComprasItem );
		editTextItem.setHint( R.string.addItemActivityTvAddItem );

		builder.setView( view );
		builder.setPositiveButton( R.string.buttonAceptar, new DialogInterface.OnClickListener(){
			@Override
			public void onClick( DialogInterface dialog, int which ){
				int iCant = 0;
				if( checkBoxListaCompras.isChecked() )
					iCant = 1;
				aceptar.Aceptar( editTextItem.getText().toString(), iCant );
			}
		} );

		builder.show();
		editTextItem.requestFocus();
		InputMethodManager imm = (InputMethodManager) context.getSystemService( Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
	}
}

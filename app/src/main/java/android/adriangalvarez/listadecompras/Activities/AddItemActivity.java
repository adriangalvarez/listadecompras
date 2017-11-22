package android.adriangalvarez.listadecompras.Activities;

import android.adriangalvarez.listadecompras.Bussiness.ItemBL;
import android.adriangalvarez.listadecompras.R;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

public class AddItemActivity extends AppCompatActivity{

	private Button buttonAceptar;
	private Button buttonCancelar;
	private EditText editTextDescripcion;
	private CheckBox checkBoxListaCompras;
	private TextView textViewCantidad;
	private EditText editTextCantidad;
	private boolean isEditing = false;

	@Override
	protected void onCreate( Bundle savedInstanceState ){
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_add_item );

		buttonAceptar = findViewById( R.id.buttonAccept );
		buttonCancelar = findViewById( R.id.buttonCancel );
		editTextDescripcion = findViewById( R.id.editTextDescripcion );
		checkBoxListaCompras = findViewById( R.id.checkBoxListaCompras );
		textViewCantidad = findViewById( R.id.textViewCantidad );
		editTextCantidad = findViewById( R.id.editTextCantidad );

		buttonCancelar.setOnClickListener( new View.OnClickListener(){
			@Override
			public void onClick( View v ){
				Intent intent = new Intent();
				setResult( RESULT_CANCELED, intent );
				finish();
			}
		} );

		buttonAceptar.setOnClickListener( new View.OnClickListener(){
			@Override
			public void onClick( View v ){
				if( !checkBoxListaCompras.isChecked() )
					editTextCantidad.setText( "0" );
				Intent intent = new Intent();
				ItemBL itemBL = new ItemBL( editTextDescripcion.getText().toString(), Integer.parseInt( editTextCantidad.getText().toString() ) );
				intent.putExtra( "itemBL", itemBL );
				intent.putExtra( "listacompras", checkBoxListaCompras.isChecked() );
				intent.putExtra( "descripcionant", getIntent().getStringExtra( "descripcionant" ) );
				setResult( RESULT_OK, intent );
				finish();
			}
		} );

		checkBoxListaCompras.setOnClickListener( new View.OnClickListener(){
			@Override
			public void onClick( View v ){
				if( checkBoxListaCompras.isChecked() ){
					editTextCantidad.setVisibility( View.VISIBLE );
				}else{
					editTextCantidad.setVisibility( View.INVISIBLE );
				}
				textViewCantidad.setVisibility( editTextCantidad.getVisibility() );
			}
		} );

		Intent intentReferrer = getIntent();
		if( intentReferrer.getBooleanExtra( "isEditing", false ) ){
			editTextDescripcion.setText( intentReferrer.getStringExtra( "descripcionant" ) );
			checkBoxListaCompras.setChecked( intentReferrer.getBooleanExtra( "listacomprasant", false ) );
			editTextCantidad.setText( intentReferrer.getStringExtra( "cantidadant" ) );
			if( checkBoxListaCompras.isChecked() ){
				editTextCantidad.setVisibility( View.VISIBLE );
			}else{
				editTextCantidad.setText( "0" );
				editTextCantidad.setVisibility( View.INVISIBLE );
			}
			textViewCantidad.setVisibility( editTextCantidad.getVisibility() );
			getSupportActionBar().setTitle( R.string.addItemActivityTitleEdit );
		}else{
			getSupportActionBar().setTitle( R.string.addItemActivityTitle );
		}
	}
}

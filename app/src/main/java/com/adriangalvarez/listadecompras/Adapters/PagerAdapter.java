package com.adriangalvarez.listadecompras.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.adriangalvarez.listadecompras.Fragments.ComprasFragment;
import com.adriangalvarez.listadecompras.Fragments.TotalFragment;

/**
 * Created by adriangalvarez on 27/03/2018.
 */

public class PagerAdapter extends FragmentStatePagerAdapter{

	private int cantidadTabs;

	public PagerAdapter( FragmentManager fm, int cantidadTabs ){
		super( fm );
		this.cantidadTabs = cantidadTabs;
	}

	@Override
	public Fragment getItem( int position ){
		switch( position ){
			case 0:
				return new ComprasFragment();
			case 1:
				return new TotalFragment();
			default:
				return null;
		}
	}

	@Override
	public int getCount(){
		return cantidadTabs;
	}
}

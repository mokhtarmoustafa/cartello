package com.twoam.cartello.View

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.twoam.cartello.R
import com.twoam.cartello.Utilities.Base.BaseFragment


class OrdersFragment : BaseFragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
       var  currentView =  inflater.inflate(R.layout.fragment_orders, container, false)

        return currentView
    }

    fun newInstance(): OrdersFragment {
        val fragment = OrdersFragment()
        return fragment
    }

}

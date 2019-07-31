package com.twoam.cartello.Utilities.Adapters

import android.content.Context
import android.content.Intent
import android.support.v4.app.FragmentManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.twoam.cartello.Model.Product
import com.twoam.cartello.R
import com.twoam.cartello.Utilities.General.AppConstants
import com.twoam.cartello.Utilities.General.AppController
import com.twoam.cartello.Utilities.General.LoadActiveOrderDataDialog
import com.twoam.cartello.View.EditDeleteAddressActivity
import com.twoam.cartello.View.ProductDetailActivity
import java.util.*

/**
 * Created by Mokhtar on 6/30/2019.
 */

class LoadActiveOrderProductsAdapter(private val fragmentManager: FragmentManager?,
                                     private val context: Context, private val productList: ArrayList<Product>)
    : RecyclerView.Adapter<LoadActiveOrderProductsAdapter.MyViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var product: Product? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LoadActiveOrderProductsAdapter.MyViewHolder {

        val view = inflater.inflate(R.layout.orders_active_data_layout, parent, false)

        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: LoadActiveOrderProductsAdapter.MyViewHolder, position: Int) {

        product = productList[position]
        

        holder.tvProduct.text = product!!.name
        holder.tvTotal.text = "${product!!.price}"

    }

    override fun getItemCount(): Int {
        return productList.size
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }


    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var tvProduct: TextView
        var tvTotal: TextView


        init {

            tvProduct = itemView.findViewById(R.id.tvProduct)
            tvTotal = itemView.findViewById(R.id.tvTotal)


            itemView.setOnClickListener { v ->

                // get position
                val pos = adapterPosition

                // check if item still exists
                if (pos != RecyclerView.NO_POSITION) {

                    product = productList[pos]
                    AppConstants.CurrentSelectedProduct=product!!
                    //todo open product details fragment from here
//                    context.startActivity(Intent(context, ProductDetailActivity::class.java)
//                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                            .putExtra("productIdPosition", pos))
                }
            }
        }

    }
}
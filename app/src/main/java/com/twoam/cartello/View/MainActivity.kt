package com.twoam.cartello.View


import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.view.View
import com.twoam.Networking.NetworkManager
import com.twoam.cartello.Model.Cart
import com.twoam.cartello.Model.Order
import com.twoam.cartello.R
import com.twoam.cartello.Utilities.Base.BaseDefaultActivity
import com.twoam.cartello.Utilities.Base.BaseFragment
import com.twoam.cartello.Utilities.General.AppConstants
import com.twoam.cartello.Utilities.General.CustomBottomSheetDialog
import com.twoam.cartello.Utilities.Interfaces.IBottomSheetCallback
import com.twoam.cartello.Utilities.Interfaces.IOrderCallback
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : BaseDefaultActivity(), View.OnClickListener, IBottomSheetCallback, IOrderCallback {


    //region Members
    private var homeBottom: CustomBottomSheetDialog = CustomBottomSheetDialog()
    val homeFragment = HomeFragment()
    val medicalFragment = MedicalPrescriptionsFragment()
    val orderFragment = OrdersFragment()
    val moreFragment = MoreFragment()
    val favouriteFragment = FavouriteFragment()
    val productDetailsFragment = ProductDetailsFragment()
    val fm = supportFragmentManager
    var active = BaseFragment()
    var bitmap: Bitmap? = null
    //endregion

    //region Events
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()

       if(NetworkManager().isNetworkAvailable(this))
       {
           getCartData()

           fm.beginTransaction().replace(R.id.layout_container, homeFragment, "homeFragment").commit()
           fm.beginTransaction().add(R.id.layout_container, medicalFragment, "medicalFragment").hide(medicalFragment).commit()
           fm.beginTransaction().add(R.id.layout_container, orderFragment, "orderFragment").hide(orderFragment).commit()
           fm.beginTransaction().add(R.id.layout_container, moreFragment, "moreFragment").hide(moreFragment).commit()
           fm.beginTransaction().add(R.id.layout_container, favouriteFragment, "favouriteFragment").hide(favouriteFragment).commit()
           fm.beginTransaction().add(R.id.layout_container, productDetailsFragment, "productDetailsFragment").hide(productDetailsFragment).commit()
           active = homeFragment
       }
        else
           showAlertDialouge(getString(R.string.error_no_internet))

    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.rlHome -> {

                val f = supportFragmentManager.findFragmentByTag("Custom Bottom Sheet")
                if (f != null && f is CustomBottomSheetDialog) {
                    homeBottom.dismiss()
                    homeBottom.show(fm, "Custom Bottom Sheet")

                } else {
                    homeBottom.show(fm, "Custom Bottom Sheet")
                }


            }
            R.id.ivCart, R.id.tvCartCounter -> {
                var intent = Intent(this@MainActivity, CartActivity::class.java)
                startActivityForResult(intent, 100)
            }
            R.id.ivSearch -> {
                startActivity(Intent(this@MainActivity, SearchActivity::class.java))
            }
        }
    }


    override fun onOrderCancelled(isCanceled: Boolean, order: Order?) {
        if (isCanceled) {
            this.orderFragment.cancelOrder(order!!)
        }
    }

    override fun onBackPressed() {
        if (active == favouriteFragment) {
            fm.beginTransaction().hide(favouriteFragment).show(moreFragment).addToBackStack(null).commit()
            active = moreFragment
        }
        else if(active==productDetailsFragment&&productDetailsFragment.isOpendFromFavoriteView)
        {
            productDetailsFragment.isOpendFromFavoriteView = false
            fm.beginTransaction().hide(active).show(moreFragment).addToBackStack(null).commit()
            active = moreFragment
        }
        else if (fm.backStackEntryCount > 0) {
            fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            active = homeFragment
            tvMainHome.text = getString(R.string.tab_home)
            AppConstants.CURRENTSELECTEDINDEX = 0
        } else {
            super.onBackPressed()
        }
    }


    override fun onBottomSheetClosed(isClosed: Boolean) {

    }

    override fun onBottomSheetSelectedItem(index: Int) {
        when (index) {
            0 -> {
                if (active == homeFragment)
                    return
                fm.beginTransaction().hide(active).show(homeFragment).commit()
                active = homeFragment
                tvMainHome.text = getString(R.string.tab_home)

            }
            1 -> {
                if (active == medicalFragment)
                    return
                fm.beginTransaction().hide(active).show(medicalFragment).addToBackStack(null).commit()
                active = medicalFragment
            }
            2 -> {
                if (active == orderFragment)
                    return
                fm.beginTransaction().hide(active).show(orderFragment).addToBackStack(null).commit()
                active = orderFragment
            }
            3 -> {
                if (active == moreFragment)
                    return
                fm.beginTransaction().hide(active).show(moreFragment).addToBackStack(null).commit()
                active = moreFragment
            }
            4 -> { //update cart counter
                tvCartCounter.text = Cart.getAll().count().toString()

            }
            5 -> //navigate to favorite  view
            {
                fm.beginTransaction().hide(active).show(favouriteFragment).addToBackStack(null).commit()
                active = favouriteFragment

            }

            6 -> //back to more view
            {
                fm.beginTransaction().hide(favouriteFragment).show(moreFragment).addToBackStack(null).commit()
                active = moreFragment
            }

            7 ->//close product details view
            {
                fm.beginTransaction().hide(productDetailsFragment).show(homeFragment).addToBackStack(null).commit()
                active = homeFragment
            }

            8 ->//navigate to product details view
            {
                fm.beginTransaction().hide(active).show(productDetailsFragment).addToBackStack(null).commit()
                active = productDetailsFragment
            }
            9 ->//navigate to product details view from Favorite View
            {
                fm.beginTransaction().hide(active).show(productDetailsFragment).addToBackStack(null).commit()
                productDetailsFragment.isOpendFromFavoriteView=true
                active = productDetailsFragment
            }

            10 ->//close product details view opened from favorite view
            {
                fm.beginTransaction().hide(active).show(moreFragment).addToBackStack(null).commit()
                active = moreFragment
            }


        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {

            if (AppConstants.CurrentCameraGAlleryAction == 0) //camera
            {
                bitmap = data!!.extras.get("data") as Bitmap
                var intent = Intent(this@MainActivity, MedicalPrescriptionsDetailActivity::class.java)
                startActivity(intent.putExtra("image", bitmap))
            } else if (AppConstants.CurrentCameraGAlleryAction == 1) //gallery
            {

                val selectedImage = data!!.data
                if (selectedImage != null) {
                    var intent = Intent(this@MainActivity, MedicalPrescriptionsDetailActivity::class.java)
                    startActivity(intent.putExtra("image", selectedImage))
                }
            }

            if (resultCode == 100) {

                tvCartCounter.text = Cart.getAll().count().toString()
            }


        }
    }

    override fun onResume() {
        super.onResume()
        tvCartCounter.text = Cart.getAll().count().toString()
    }

    //endregion


    //region Helper Functions

    private fun init() {
        rlHome.setOnClickListener(this)
        tvCartCounter.setOnClickListener(this)
        ivCart.setOnClickListener(this)
        ivSearch.setOnClickListener(this)
    }

    private fun getCartData() {
        //get cart data
        Cart.init()
        if (Cart.getAll().count() > 0)
            tvCartCounter.text = Cart.getAll().count().toString()
    }


    //endregion


}

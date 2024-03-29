package com.twoam.cartello.View

import android.content.Intent

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.Toast
import com.twoam.Networking.INetworkCallBack
import com.twoam.Networking.NetworkManager
import com.twoam.cartello.Model.Addresses
import com.twoam.cartello.Model.City
import com.twoam.cartello.R
import com.twoam.cartello.Utilities.API.ApiResponse
import com.twoam.cartello.Utilities.API.ApiServices
import com.twoam.cartello.Utilities.Adapters.AddressAdapter
import com.twoam.cartello.Utilities.Base.BaseDefaultActivity
import com.twoam.cartello.Utilities.DB.PreferenceController
import com.twoam.cartello.Utilities.General.AppConstants
import com.twoam.cartello.Utilities.Interfaces.IBottomSheetCallback
import kotlinx.android.synthetic.main.activity_profile.*
import com.twoam.cartello.Utilities.General.AnimateScroll


class ProfileActivity : BaseDefaultActivity(), View.OnClickListener, IBottomSheetCallback {


    //region Members
    var profileAddressList = ArrayList<Addresses>()
    var addressId: String? = null
    var removedAddressIndex = 0
    var cities = ArrayList<City>()
    //endregion

    //region Events
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        init()

        //CHANGE THE RATING BAR COLOR
        changeRateBarSettings()
        getUserProfileData()
    }


    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.ivBackProfile -> {
                finish()
            }
            R.id.tvAddAddress -> {
                startActivity(Intent(this, CreateAddressActivity::class.java).putExtra("fromProfile",true))
                finish()
            }
            R.id.tvEditProfile -> {
                startActivity(Intent(this, EditAddressProfileActivity::class.java))
                finish()
            }
        }
    }

    override fun onBottomSheetClosed(isClosed: Boolean) {

    }

    override fun onBottomSheetSelectedItem(index: Int) {
        if (index > 0)//delete address
        {
            removedAddressIndex = index
            removeAddress(removedAddressIndex)

        }

    }


    override fun onResume() {
        super.onResume()
        AnimateScroll.scrollToView(scrollView, rlParent)
        profileAddressList = AppConstants.CurrentLoginUser.addresses
        var adapter = AddressAdapter(this@ProfileActivity, profileAddressList)
        rvAddress.adapter = adapter

    }

    //endregion

    //region Helper Functions
    private fun init() {

        ivBackProfile.setOnClickListener(this)
        tvAddAddress.setOnClickListener(this)
        tvEditProfile.setOnClickListener(this)
        if (intent.hasExtra("addressId")) {
            addressId = intent.getStringExtra("addressId")
//            addressList.

        }

    }

    private fun changeRateBarSettings() {
        //        val stars = rbRate.progressDrawable as LayerDrawable
        //        stars.getDrawable(2).setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP)
        //val starDrawable = resources.getDrawable(R.drawable.YOUR_IMAGE)
//        val height = 24
//        val params = rbRate.getLayoutParams() as WindowManager.LayoutParams
//        params.height = height
//        rbRate.setLayoutParams(params)
    }

    private fun getUserProfileData() {

        if (AppConstants.CurrentLoginUser.addresses!!.count() > 0) {

            var user = AppConstants.CurrentLoginUser
            profileAddressList = AppConstants.CurrentLoginUser.addresses!!

            tvProfileName.text = user.name
            tvTelNo.text = user.phone
            tvEmailValue.text = user.email
            tvBirthDateValue.text = user.birthdate

            var citiesData =PreferenceController.instance!!.getCitiesPref(AppConstants.CITIES_DATA)
            if(citiesData==null)
                prepareData()
          else
            {
                var adapter = AddressAdapter(this@ProfileActivity, profileAddressList)
                rvAddress.adapter = adapter
                rvAddress.layoutManager = LinearLayoutManager(this)
            }

        }
    }

    private fun prepareData(): ArrayList<City> {
        showDialogue()
        if (NetworkManager().isNetworkAvailable(this)) {

            var request = NetworkManager().create(ApiServices::class.java)
            var endPoint = request.getCities()
            NetworkManager().request(endPoint, object : INetworkCallBack<ApiResponse<ArrayList<City>>> {
                override fun onFailed(error: String) {
                    hideDialogue()
                    showAlertDialouge(error)
                }

                override fun onSuccess(response: ApiResponse<ArrayList<City>>) {
                    if (response.code == AppConstants.CODE_200) {
                        cities = response.data!!
                        PreferenceController.instance?.setCitiesPref(AppConstants.CITIES_DATA, cities)
                        var adapter = AddressAdapter(this@ProfileActivity, profileAddressList)
                        rvAddress.adapter = adapter
                        rvAddress.layoutManager = LinearLayoutManager(this@ProfileActivity)
                        hideDialogue()

                    } else {
                        hideDialogue()
                        Toast.makeText(applicationContext, response.message, Toast.LENGTH_SHORT).show()
                    }
                }
            })
        } else {
            hideDialogue()
            showAlertDialouge(getString(R.string.error_no_internet))
        }

        return cities
    }


    private fun removeAddress(removedAddressID: Int): Boolean {
        var done = false

        if (NetworkManager().isNetworkAvailable(this@ProfileActivity)) {
            var request = NetworkManager().create(ApiServices::class.java)
            var token = AppConstants.BEARER + AppConstants.CurrentLoginUser.token
            var endpoint = request.removeAddress(removedAddressID, token)
            NetworkManager().request(endpoint, object : INetworkCallBack<ApiResponse<Addresses>> {
                override fun onFailed(error: String) {
                    hideDialogue()
                    showAlertDialouge(error)
                }

                override fun onSuccess(response: ApiResponse<Addresses>) {
                    if (response.code == AppConstants.CODE_200) {

                        var oldAddress = AppConstants.CurrentLoginUser.addresses!!.find { it.id == removedAddressID }
                        AppConstants.CurrentLoginUser.addresses!!.remove(oldAddress)
                        AppConstants.CurrentLoginUser.hasAddress = AppConstants.CurrentLoginUser.addresses!!.count() > 0

                        profileAddressList = AppConstants.CurrentLoginUser.addresses!!
                        PreferenceController.getInstance(applicationContext).setUserPref(AppConstants.USER_DATA, AppConstants.CurrentLoginUser)
                        //saved the address in shared prefrences
                        hideDialogue()
                        //refresh adapter
                        var adapter = AddressAdapter(this@ProfileActivity, profileAddressList)
                        rvAddress.adapter = adapter
                        rvAddress.layoutManager = LinearLayoutManager(this@ProfileActivity)

                    }
                }
            })

        } else {
            hideDialogue()
            showAlertDialouge(getString(R.string.error_no_internet))
        }

        return done
    }
    //endregion


}

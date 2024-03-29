package com.twoam.cartello.View


import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.View
import com.twoam.Networking.INetworkCallBack
import com.twoam.Networking.NetworkManager
import com.twoam.cartello.Model.Cart
import com.twoam.cartello.Model.Product
import com.twoam.cartello.Model.Search
import com.twoam.cartello.R
import com.twoam.cartello.Utilities.API.ApiResponse
import com.twoam.cartello.Utilities.API.ApiServices
import com.twoam.cartello.Utilities.Adapters.SearchResultAdapter
import com.twoam.cartello.Utilities.Base.BaseDefaultActivity
import com.twoam.cartello.Utilities.DB.PreferenceController
import com.twoam.cartello.Utilities.General.AppConstants
import com.twoam.cartello.Utilities.General.FilterBottomSheetDialog
import com.twoam.cartello.Utilities.Interfaces.IBottomSheetCallback
import kotlinx.android.synthetic.main.activity_search_result.*
import java.lang.Exception
import java.util.*

class SearchResultActivity : BaseDefaultActivity(), View.OnClickListener, IBottomSheetCallback {

    //region Members
    var searchList = ArrayList<Product>()
    val TAG = SearchResultActivity::class.java!!.simpleName
    var bottomSheetPrice = FilterBottomSheetDialog()
    var searchDataList = ArrayList<Search>()
    //endregion

    //region Events
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_result)

        init()

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnPrice -> {
                btnPrice.setTextColor(Color.WHITE)
                btnPrice.setBackgroundResource(R.drawable.rounded_button)

                btnAsc.setTextColor(Color.parseColor("#425972"))
                btnAsc.setBackgroundResource(R.drawable.rounded_non_select_button)

                btnDes.setTextColor(Color.parseColor("#425972"))
                btnDes.setBackgroundResource(R.drawable.rounded_non_select_button)
                //show options
//                if (NetworkManager().isNetworkAvailable(this@SearchResultActivity))
                    bottomSheetPrice.show(supportFragmentManager, TAG)
//                else
//                    showAlertDialouge(getString(R.string.error_no_internet))
            }

            R.id.btnAsc -> {
                btnAsc.setTextColor(Color.WHITE)
                btnAsc.setBackgroundResource(R.drawable.rounded_button)

                btnPrice.setTextColor(Color.parseColor("#425972"))
                btnPrice.setBackgroundResource(R.drawable.rounded_non_select_button)

                btnDes.setTextColor(Color.parseColor("#425972"))
                btnDes.setBackgroundResource(R.drawable.rounded_non_select_button)

                //arrange adapter data and notify change
//                if (NetworkManager().isNetworkAvailable(this@SearchResultActivity))
//                {
                   // filterAsc()
                    searchList.sortBy { it.name }
                    prepareProductsResultData(searchList!!)
//                }
//                else
//                    showAlertDialouge(getString(R.string.error_no_internet))
            }

            R.id.btnDes -> {
                btnDes.setTextColor(Color.WHITE)
                btnDes.setBackgroundResource(R.drawable.rounded_button)

                btnPrice.setTextColor(Color.parseColor("#425972"))
                btnPrice.setBackgroundResource(R.drawable.rounded_non_select_button)

                btnAsc.setTextColor(Color.parseColor("#425972"))
                btnAsc.setBackgroundResource(R.drawable.rounded_non_select_button)
                //arrange adapter data and notify change
//                if (NetworkManager().isNetworkAvailable(this@SearchResultActivity))
//                {
//                    filterDes()
                    searchList.reverse()
                    prepareProductsResultData(searchList!!)
//                }
//                else
//                    showAlertDialouge(getString(R.string.error_no_internet))
            }

            R.id.ivCart, R.id.tvCartCounter -> {
                var intent = Intent(this@SearchResultActivity, CartActivity::class.java)
                startActivityForResult(intent, 100)
                finish()
            }

            R.id.ivSearch, R.id.rlBack -> {
                finish()
            }

        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onBottomSheetClosed(isClosed: Boolean) {

    }

    override fun onBottomSheetSelectedItem(index: Int) {
        when (index) {
            1 -> { //Price filterHighToLow
//                filterByPriceLowToHigh()
                searchList.sortByDescending { it.price }
                searchList.reverse()
                prepareProductsResultData(searchList)

            }
            2 -> { // Price filterLowToHigh
//                filterByPriceLowToHigh()
                searchList.sortByDescending { it.price }
                prepareProductsResultData(searchList)
            }
        }
    }

    //endregion

    //region Helper Functions

    fun getSearchData(searchValue: String) {
        showDialogue()
        if (NetworkManager().isNetworkAvailable(this@SearchResultActivity)) {
            var request = NetworkManager().create(ApiServices::class.java)
            var token = AppConstants.BEARER + AppConstants.CurrentLoginUser.token
            var endPoint = request.searchProducts(token, searchValue)
            NetworkManager().request(endPoint, object : INetworkCallBack<ApiResponse<ArrayList<Product>>> {
                override fun onFailed(error: String) {
                    hideDialogue()
                    showAlertDialouge(error)
                }

                override fun onSuccess(response: ApiResponse<ArrayList<Product>>) {
                    if (response.code == AppConstants.CODE_200) {
                        searchList = response.data!!
                        tvTotal.text = searchList.size.toString()
                        prepareProductsResultData(searchList!!)

                        try {
                            searchDataList = PreferenceController.instance?.getSearchPref(AppConstants.SEARCH_DATA)!!
                        } catch (ex: Exception) {
                            ex.printStackTrace()
                        }

                        if (searchDataList.size > 0) {
                            searchDataList.forEach {
                                if (!it.name.contains(searchValue)) {
                                    var newsearch = Search(1, searchValue)
                                    searchDataList?.add(newsearch)
                                    PreferenceController.instance?.setSearchPref(AppConstants.SEARCH_DATA, searchDataList)
                                }
                            }
                        } else {
                            var newsearch = Search(1, searchValue)
                            searchDataList?.add(newsearch)
                            PreferenceController.instance?.setSearchPref(AppConstants.SEARCH_DATA, searchDataList)
                        }


                        hideDialogue()
                    } else {
                        hideDialogue()
                        showAlertDialouge(getString(R.string.error_network))
                    }
                }
            })
        } else {
            hideDialogue()
            showAlertDialouge(getString(R.string.error_no_internet))
        }
    }

    fun getSubCategoryData(subCategoryId: Int) {
        showDialogue()
        if (NetworkManager().isNetworkAvailable(this@SearchResultActivity)) {
            var request = NetworkManager().create(ApiServices::class.java)
            var token = AppConstants.BEARER + AppConstants.CurrentLoginUser.token
            var endPoint = request.getSubCategories(token, subCategoryId)
            NetworkManager().request(endPoint, object : INetworkCallBack<ApiResponse<ArrayList<Product>>> {
                override fun onFailed(error: String) {
                    hideDialogue()
                    showAlertDialouge(error)
                }

                override fun onSuccess(response: ApiResponse<ArrayList<Product>>) {
                    if (response.code == AppConstants.CODE_200) {
                        searchList = response.data!!
                        tvTotal.text = searchList.size.toString()
                        prepareProductsResultData(searchList!!)

                        try {
                            searchDataList = PreferenceController.instance?.getSearchPref(AppConstants.SEARCH_DATA)!!
                        } catch (ex: Exception) {
                            ex.printStackTrace()
                        }

                        hideDialogue()
                    } else {
                        hideDialogue()
                        showAlertDialouge(getString(R.string.error_network))
                    }
                }
            })
        } else {
            hideDialogue()
            showAlertDialouge(getString(R.string.error_no_internet))
        }
    }

    private fun init() {


        if (intent.hasExtra(AppConstants.SEARCH_DATA)) {
            var searchValue = intent.getStringExtra(AppConstants.SEARCH_DATA)
            tvTitle.text = searchValue
            tvCartCounter.text = Cart.getAll().count().toString()
            getSearchData(searchValue)
        } else if (intent.hasExtra(AppConstants.SEARCH_SUB_CATEGORY)) {
            tvTitle.text = AppConstants.CurrentSelectedSubCategory.name
            tvCartCounter.text = Cart.getAll().count().toString()
            getSubCategoryData(AppConstants.CurrentSelectedSubCategory.id)
        }

        btnPrice.setOnClickListener(this)
        btnAsc.setOnClickListener(this)
        btnDes.setOnClickListener(this)
        ivCart.setOnClickListener(this)
        tvCartCounter.setOnClickListener(this)
        ivSearch.setOnClickListener(this)
        rlBack.setOnClickListener(this)

    }

    private fun prepareProductsResultData(searchList: ArrayList<Product>) {

        var adapter = SearchResultAdapter(this@SearchResultActivity, searchList)
        rvSearchResult.adapter = adapter
        rvSearchResult.layoutManager = GridLayoutManager(this@SearchResultActivity, 2)

    }


    private fun filterAsc() {
        if (NetworkManager().isNetworkAvailable(this@SearchResultActivity)) {
            var request = NetworkManager().create(ApiServices::class.java)
            var token = AppConstants.BEARER + AppConstants.CurrentLoginUser.token
            var endPoint = request.filterProducts(token, "")
            NetworkManager().request(endPoint, object : INetworkCallBack<ApiResponse<ArrayList<Product>>> {
                override fun onFailed(error: String) {
                    showAlertDialouge(error)
                }

                override fun onSuccess(response: ApiResponse<ArrayList<Product>>) {
                    if (response.code == AppConstants.CODE_200) {
                        searchList = response.data!!
                        tvTotal.text = searchList.size.toString()
                        prepareProductsResultData(searchList!!)
                    } else {
                        showAlertDialouge(getString(R.string.error_network))
                    }
                }
            })
        } else {
            showAlertDialouge(getString(R.string.error_no_internet))
        }
    }

    private fun filterDes() {
        if (NetworkManager().isNetworkAvailable(this@SearchResultActivity)) {
            var request = NetworkManager().create(ApiServices::class.java)
            var token = AppConstants.BEARER + AppConstants.CurrentLoginUser.token
            var endPoint = request.filterProducts(token, "")
            NetworkManager().request(endPoint, object : INetworkCallBack<ApiResponse<ArrayList<Product>>> {
                override fun onFailed(error: String) {
                    showAlertDialouge(error)
                }

                override fun onSuccess(response: ApiResponse<ArrayList<Product>>) {
                    if (response.code == AppConstants.CODE_200) {
                        searchList = response.data!!
                        tvTotal.text = searchList.size.toString()
                        prepareProductsResultData(searchList!!)
                    } else {
                        showAlertDialouge(getString(R.string.error_network))
                    }
                }
            })
        } else {
            showAlertDialouge(getString(R.string.error_no_internet))
        }
    }

    private fun filterByPriceHighToLow() {
        if (NetworkManager().isNetworkAvailable(this@SearchResultActivity)) {
            var request = NetworkManager().create(ApiServices::class.java)
            var token = AppConstants.BEARER + AppConstants.CurrentLoginUser.token
            var endPoint = request.filterProducts(token, "")
            NetworkManager().request(endPoint, object : INetworkCallBack<ApiResponse<ArrayList<Product>>> {
                override fun onFailed(error: String) {
                    showAlertDialouge(error)
                }

                override fun onSuccess(response: ApiResponse<ArrayList<Product>>) {
                    if (response.code == AppConstants.CODE_200) {
                        searchList = response.data!!
                        tvTotal.text = searchList.size.toString()
                        prepareProductsResultData(searchList!!)
                    } else {
                        showAlertDialouge(getString(R.string.error_network))
                    }
                }
            })
        } else {
            showAlertDialouge(getString(R.string.error_no_internet))
        }
    }

    private fun filterByPriceLowToHigh() {
        if (NetworkManager().isNetworkAvailable(this@SearchResultActivity)) {
            var request = NetworkManager().create(ApiServices::class.java)
            var token = AppConstants.BEARER + AppConstants.CurrentLoginUser.token
            var endPoint = request.filterProducts(token, "")
            NetworkManager().request(endPoint, object : INetworkCallBack<ApiResponse<ArrayList<Product>>> {
                override fun onFailed(error: String) {
                    showAlertDialouge(error)
                }

                override fun onSuccess(response: ApiResponse<ArrayList<Product>>) {
                    if (response.code == AppConstants.CODE_200) {
                        searchList = response.data!!
                        tvTotal.text = searchList.size.toString()
                        prepareProductsResultData(searchList!!)
                    } else {
                        showAlertDialouge(getString(R.string.error_network))
                    }
                }
            })
        } else {
            showAlertDialouge(getString(R.string.error_no_internet))
        }
    }
    //endregion

}

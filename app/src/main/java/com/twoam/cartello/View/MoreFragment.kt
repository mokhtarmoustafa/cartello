package com.twoam.cartello.View


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.CardView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.twoam.cartello.R
import kotlinx.android.synthetic.main.fragment_more.view.*
import android.content.ActivityNotFoundException
import android.content.Context
import android.widget.Switch
import android.widget.Toast
import com.twoam.cartello.Utilities.Base.BaseFragment
import com.twoam.cartello.Utilities.DB.PreferenceController
import com.twoam.cartello.Utilities.General.AppConstants
import com.twoam.cartello.Utilities.General.CloseBottomSheetDialog
import com.twoam.cartello.Utilities.Interfaces.IBottomSheetCallback


class MoreFragment : BaseFragment(), View.OnClickListener {

    //region Members
    private var currentView: View? = null
    private var cvFavourite: CardView? = null
    private var cvProfile: CardView? = null
    private var bottomSheet = CloseBottomSheetDialog()
    private lateinit var listener: IBottomSheetCallback
    private var switchNotification: Switch? = null


    //endregion

    //region Events


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        currentView = inflater.inflate(R.layout.fragment_more, container, false)

        init(currentView!!)

        return currentView
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is IBottomSheetCallback) {
            listener = context
        } else {
            throw ClassCastException(context.toString() + " must implement IBottomSheetCallback.onBottomSheetSelectedItem")
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.switchNotification -> {

                if (switchNotification!!.isChecked) {
                    PreferenceController.getInstance(context!!).Set(AppConstants.NOTIFICATION, AppConstants.TRUE)
                } else {
                    PreferenceController.getInstance(context!!).clear(AppConstants.NOTIFICATION)
                }
            }
            R.id.cvProfile -> {
                startActivity(Intent(context, ProfileActivity::class.java))
            }
            R.id.cvFavourite -> {

                listener.onBottomSheetSelectedItem(5)
            }
            R.id.rlTermsOfUse -> {
                openUrl("http://www.trolley-app.com/terms-conditions/")
            }
            R.id.rlTermsOfSale -> {
                openUrl("http://www.trolley-app.com/terms-conditions/")
            }
            R.id.rlPrivacyPolicy -> {
                openUrl("http://www.trolley-app.com/privacy-policy/")
            }
            R.id.rlAboutUs -> {
                openUrl("http://www.trolley-app.com")
            }
            R.id.rlContactUs -> {
                openUrl("http://www.trolley-app.com")
            }
            R.id.rlRateUs -> {
                Toast.makeText(context, "Send to me tha package name to open the app url in the play store", Toast.LENGTH_SHORT).show()
//                oprnRateUs()
            }

            R.id.rlChangePassword -> {
                context?.startActivity(Intent(context, ForgetPasswordActivity::class.java).putExtra(AppConstants.CHANGE_PASSWORD, 4))
            }
            R.id.rlLogOut -> {
                bottomSheet.show(childFragmentManager, "Custom Bottom Sheet")
                bottomSheet.isCancelable = false


            }

        }
    }

    //endregion

    //region Helper unctions

    private fun init(view: View) {

        switchNotification=view.findViewById(R.id.switchNotification)
        view.switchNotification.setOnClickListener(this)
        view.cvFavourite?.setOnClickListener(this)
        view.cvProfile?.setOnClickListener(this)
        view.rlTermsOfUse.setOnClickListener(this)
        view.rlTermsOfSale.setOnClickListener(this)
        view.rlPrivacyPolicy.setOnClickListener(this)
        view.rlAboutUs.setOnClickListener(this)
        view.rlContactUs.setOnClickListener(this)
        view.rlChangePassword.setOnClickListener(this)
        view.rlLogOut.setOnClickListener(this)

        var isChecked= PreferenceController.getInstance(context!!)[AppConstants.NOTIFICATION]

        switchNotification?.isChecked = isChecked==AppConstants.TRUE

    }

    fun openUrl(url: String) {
        val uri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    }

    fun oprnRateUs() {
        val uri = Uri.parse("market://details?id=" + context?.packageName)
        val goToMarket = Intent(Intent.ACTION_VIEW, uri)
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
        try {
            startActivity(goToMarket)
        } catch (e: ActivityNotFoundException) {
            startActivity(Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + context?.packageName)))
        }

    }


    //endregion


}

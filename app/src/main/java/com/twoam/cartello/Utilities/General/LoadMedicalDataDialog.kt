package com.twoam.cartello.Utilities.General


import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.view.*
import android.widget.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.twoam.cartello.Model.MedicalPrescriptions
import com.twoam.cartello.R
import com.twoam.cartello.Utilities.Interfaces.IBottomSheetCallback


class LoadMedicalDataDialog : BottomSheetDialogFragment(), IBottomSheetCallback {


    //region Members
    internal var view: ViewGroup? = null
    lateinit var layout: RelativeLayout
    private var listener: IBottomSheetCallback? = null
    private var action: Int = 0
    private lateinit var ivImage: ImageView
    private lateinit var tvNote: TextView
    private lateinit var progress_bar: ProgressBar
    private var currentMedical: MedicalPrescriptions? = null


    var CurrentMedical: MedicalPrescriptions
        get() {
            return currentMedical!!
        }
        set(medical) {
            currentMedical = medical
        }
    //endregion

    //region Events
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        view = inflater.inflate(R.layout.bottom_sheet_medical_load, container, false) as ViewGroup
        layout = view!!.findViewById(R.id.rlOptions)

        init()
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is IBottomSheetCallback) {
            listener = context
        } else {
            throw ClassCastException(context.toString() + " must implement IBottomSheetCallback.onBottomSheetSelectedItem")
        }
    }

    override fun onBottomSheetSelectedItem(index: Int) {
        dialog.dismiss()
    }

    override fun onBottomSheetClosed(isClosed: Boolean) {
        dialog.dismiss()
    }


    //endregion

    //region Helper Functions
    fun init() {

        ivImage = layout.findViewById(R.id.ivImage)
        tvNote = layout.findViewById(R.id.tvNote)
        progress_bar = layout.findViewById(R.id.progress_bar)


        if (CurrentMedical != null)
            loadMedicalData(CurrentMedical)
    }


    private fun loadMedicalData(medical: MedicalPrescriptions) {

        progress_bar.visibility = View.VISIBLE
        Glide.with(context!!)
                .load(medical.image)
                .listener(object : RequestListener<Drawable> {
                    override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        progress_bar.visibility = View.GONE
                        return false
                    }

                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                        progress_bar.visibility = View.GONE
                        return false
                    }
                })
                .into(ivImage)

        if (!medical.note.isNullOrEmpty())
            tvNote.text = getString(R.string.note) + " "+medical.note
        else
            tvNote.text = getString(R.string.note)


    }
    //endregion

}
package com.twoam.cartello.View

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.twoam.cartello.R
import com.twoam.cartello.Utilities.Base.BaseDefaultActivity

class SearchActivity : BaseDefaultActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
    }
}

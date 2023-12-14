package com.diipl.moviebeam.ui.hotelinfo

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.diipl.moviebeam.R

class LoginFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
//        Log.i("Title", arguments?.getString("title")?:"")
        return inflater.inflate(
            R.layout.layout_login, container, false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val arguments = arguments
        if (arguments!=null){
            val title = arguments.getString("title")
            Log.d("TITLE - ", "onViewCreated:LoginFragment $title")
        }
    }
}

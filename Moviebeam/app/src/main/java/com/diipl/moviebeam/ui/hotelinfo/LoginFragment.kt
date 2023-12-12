package com.diipl.moviebeam.ui.hotelinfo

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.diipl.moviebeam.R

class LoginFragment : Fragment() {
    // Define a companion object to provide a newInstance method
    companion object {
        private const val ARG_TITLE = "title"

        // Create a new instance of the fragment with arguments
        fun newInstance(title: String): LoginFragment {
            val fragment = LoginFragment()
            val args = Bundle()
            args.putString(ARG_TITLE, title)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
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

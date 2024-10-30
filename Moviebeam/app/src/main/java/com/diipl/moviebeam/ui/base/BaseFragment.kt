package com.diipl.moviebeam.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.diipl.moviebeam.service.handler.PreferenceHandler
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
abstract class BaseFragment : Fragment(){

    abstract fun observeViewModel()

    protected abstract fun initViewBinding()

    @Inject
    lateinit var preferenceHandler: PreferenceHandler

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        preferenceHandler.loadAllData()

        initViewBinding()
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        observeViewModel()
        super.onViewCreated(view, savedInstanceState)
    }

    protected open fun fragmentTransaction(
        transactionType: Int,
        fragment: Fragment,
        container: Int,
        isAddToBackStack: Boolean,
        bundle: Bundle?
    ) {
        if (bundle != null) {
            fragment.arguments = bundle
        }

        val trans = requireActivity().supportFragmentManager.beginTransaction()
        when (transactionType) {
            ADD_FRAGMENT -> {
                trans.add(container, fragment, fragment.javaClass.simpleName)
                if (isAddToBackStack) trans.addToBackStack(null)
            }
            REPLACE_FRAGMENT -> {
                trans.replace(container, fragment, fragment.javaClass.simpleName)
                if (isAddToBackStack) trans.addToBackStack(null)
            }
        }
        trans.commit()
    }

    companion object {
        const val ADD_FRAGMENT = 0
        const val REPLACE_FRAGMENT = 1
    }
}
package com.diipl.moviebeam.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.LiveData
import com.diipl.moviebeam.data.Resource
import com.diipl.moviebeam.data.dto.login.LoginResponse
import com.diipl.moviebeam.databinding.LoginActivityBinding
import com.diipl.moviebeam.ui.base.BaseActivity
import com.diipl.moviebeam.ui.home.HomeActivity
import com.diipl.moviebeam.ui.mainmenu.MainMenuActivity
import com.diipl.moviebeam.utils.*
import com.google.android.material.snackbar.Snackbar.*

class LoginActivity : BaseActivity() {

    private val loginViewModel: LoginViewModel by viewModels()
    private lateinit var binding: LoginActivityBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.login.setOnClickListener { doLogin() }
    }

    override fun initViewBinding() {
        binding = LoginActivityBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    override fun observeViewModel() {
        observe(loginViewModel.loginLiveData, ::handleLoginResult)
        observeSnackBarMessages(loginViewModel.showSnackBar)
        observeToast(loginViewModel.showToast)
    }

    private fun doLogin() {
//        loginViewModel.doLogin(
//            binding.username.text.trim().toString(),
//            binding.password.text.toString()
//        )
        val int = Intent(this, MainMenuActivity::class.java)
        startActivity(int)
    }

    private fun handleLoginResult(status: Resource<LoginResponse>) {
        when (status) {
            is Resource.Loading -> binding.pbLoader.toVisible()
            is Resource.Success -> status.data?.let {
                navigateToMainScreen()
            }
            is Resource.DataError -> {
                status.errorCode?.let { loginViewModel.showToastMessage(getString(it)) }
            }
        }
    }

    private fun navigateToMainScreen() {
        val nextScreenIntent = Intent(this, HomeActivity::class.java)
        startActivity(nextScreenIntent)
        finish()
    }

    private fun observeSnackBarMessages(event: LiveData<SingleEvent<Any>>) {
        binding.root.setupSnackbar(this, event, LENGTH_LONG)
    }

    private fun observeToast(event: LiveData<SingleEvent<Any>>) {
        binding.root.showToast(this, event, LENGTH_LONG)
    }
}

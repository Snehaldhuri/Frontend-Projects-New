package com.diipl.moviebeam.ui.login

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diipl.moviebeam.R
import com.diipl.moviebeam.SPLASH_DELAY
import com.diipl.moviebeam.data.Resource
import com.diipl.moviebeam.data.dto.login.LoginResponse
import com.diipl.moviebeam.utils.RegexUtils.isValidEmail
import com.diipl.moviebeam.utils.SingleEvent
import kotlinx.coroutines.launch


class LoginViewModel : ViewModel() {

    private val loginLiveDataPrivate = MutableLiveData<Resource<LoginResponse>>()
    val loginLiveData: LiveData<Resource<LoginResponse>> get() = loginLiveDataPrivate

    /** Error handling as UI **/

    private val showSnackBarPrivate = MutableLiveData<SingleEvent<Any>>()
    val showSnackBar: LiveData<SingleEvent<Any>> get() = showSnackBarPrivate

    private val showToastPrivate = MutableLiveData<SingleEvent<Any>>()
    val showToast: LiveData<SingleEvent<Any>> get() = showToastPrivate

    fun doLogin(userName: String, passWord: String) {
        val isUsernameValid = isValidEmail(userName)
        val isPassWordValid = passWord.trim().length > 4
        if (isUsernameValid && !isPassWordValid) {
            loginLiveDataPrivate.value = Resource.DataError(code = R.string.invalid_password)
        } else if (!isUsernameValid && isPassWordValid) {
            loginLiveDataPrivate.value = Resource.DataError(code = R.string.invalid_username)
        } else if (!isUsernameValid && !isPassWordValid) {
            loginLiveDataPrivate.value = Resource.DataError(code = R.string.invalid_username_and_password)
        } else {
            viewModelScope.launch {
                loginLiveDataPrivate.value = Resource.Loading()
                Handler(Looper.getMainLooper()).postDelayed({
                    loginLiveDataPrivate.value = Resource.Success(
                        LoginResponse("","","","","","","","",""));
                }, SPLASH_DELAY.toLong())
                //API CALL
//                loginLiveDataPrivate.value = it
            }
        }
    }

    fun showToastMessage(error: String) {
        showToastPrivate.value = SingleEvent(error)
    }
}

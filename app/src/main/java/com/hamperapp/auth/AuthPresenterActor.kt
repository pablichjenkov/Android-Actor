package com.hamperapp.auth

import com.hamperapp.*
import com.hamperapp.actor.Actor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


class AuthPresenterActor(
    private var uiSendChannel: SendChannel<UIActorMsg>
): Actor<AuthPresenterActor.InMsg>() {

    enum class Stage {
        Select,
        Login,
        Signup
    }

    private var stage = Stage.Select

    lateinit var parentChannel: SendChannel<OutMsg>

    lateinit var fragmentChannel: SendChannel<OutMsg.View>

    private val authManager = HamperApplication.instance.authManager


    override fun start() {
        super.start()

        scope.launch {

            checkLoginBefore()
                .collect { loginBefore ->

                    if (loginBefore) {

                        parentChannel.send(OutMsg.Login.AuthSuccess)

                    }
                    else {

                        when (stage) {

                            Stage.Select -> showSelect()

                            Stage.Login -> showLogin()

                            Stage.Signup -> showSignup()

                        }

                    }

                }

        }

    }

    @UseExperimental(InternalCoroutinesApi::class)
    override fun onAction(inMsg: InMsg) {

        when (inMsg) {

            InMsg.View.OnSelectAuthViewReady -> {}

            InMsg.View.OnSelectAuthViewStop -> {}

            InMsg.View.ShowLogin -> { showLogin() }

            InMsg.View.OnLoginViewReady -> {}

            InMsg.View.OnLoginFragmentResult -> {

                scope.launch {

                    checkLoginBefore()
                        .collect { loginBefore ->

                            if (loginBefore) {

                                parentChannel.send(OutMsg.Login.AuthSuccess)

                            }

                        }

                }

            }

            is InMsg.View.DoLogin -> {

                scope.launch {

                    fragmentChannel.send(OutMsg.View.Login.OnLoad)

                    authManager
                        .doLogin(inMsg.loginReq)
                        .catch { th ->

                            // TODO: Make a util class to provide a message per each type of exception.
                            fragmentChannel.send(OutMsg.View.Login.OnError(th))

                        }
                        .collect {

                            fragmentChannel.send(OutMsg.View.Login.OnSuccess)

                            // We send a success to our parent so he will handle the Active stage to other actor
                            parentChannel.send(OutMsg.Login.AuthSuccess)

                        }

                }

            }

            InMsg.View.OnLoginViewStop -> {}

            InMsg.View.ShowSignUp -> { showSignup() }

            InMsg.View.OnSignupViewReady -> { }

            InMsg.View.OnSignupFragmentResult -> {

                scope.launch {

                    checkLoginBefore()
                        .collect { loginBefore ->

                            if (loginBefore) {

                                parentChannel.send(OutMsg.Login.AuthSuccess)

                            }

                        }

                }

            }

            is InMsg.View.DoSignUp -> {

                scope.launch {

                    fragmentChannel.send(OutMsg.View.Signup.OnLoad)

                    authManager
                        .doSignup(inMsg.signupReq)
                        .catch { th ->

                            // TODO: Make a util class to provide a message per each type of exception.
                            fragmentChannel.send(OutMsg.View.Signup.OnError(th))

                        }
                        .collect { resp ->

                            fragmentChannel.send(OutMsg.View.Signup.OnSuccess)

                            // We send a success to our parent so he will handle the Active stage to other actor
                            parentChannel.send(OutMsg.Signup.Success)

                        }

                }

            }

            InMsg.View.OnSignupViewStop -> {}

        }

    }

    override fun back() {
        super.back()

        onBackPressed()
    }

    private fun showSelect() {

        val selectAuthFragment = SelectAuthFragment.newInstance(this)

        val uiMsg = UIActorMsg.SetFragment(selectAuthFragment, "selectAuthFragment")

        scope.launch {

            uiSendChannel.send(uiMsg)

            stage = Stage.Select
        }

    }

    private fun showLogin() {

        val titleMsg = UIActorMsg.SetTitle("Login Screen")

        val loginFragment = LoginFragment.newInstance(this)

        val uiMsg = UIActorMsg.SetFragment(loginFragment, "loginFragment")

        scope.launch {

            uiSendChannel.send(titleMsg)

            uiSendChannel.send(uiMsg)

            stage = Stage.Login
        }

    }

    private fun showSignup() {

        val titleMsg = UIActorMsg.SetTitle("SignUp Screen")

        val signupFragment = SignupFragment.newInstance(this)

        val uiMsg = UIActorMsg.SetFragment(signupFragment, "signupFragment")

        scope.launch {

            uiSendChannel.send(titleMsg)

            uiSendChannel.send(uiMsg)

            stage = Stage.Signup
        }

    }

    private fun onBackPressed() {

        when (stage) {

            Stage.Select -> {

                scope.launch {

                    parentChannel.send(OutMsg.Login.AuthError("Back Pressed Cancelled"))

                }

            }

            Stage.Login -> { showSelect() }

            Stage.Signup -> { showSelect() }

        }

    }

    private fun CoroutineScope.checkLoginBefore(): Flow<Boolean> {

        val hamperAuthToken = authManager.authToken()

        if (hamperAuthToken != null) {

            return flowOf(true)

        }

        if (authManager.verifyFBLogin()) {

            return authManager.doLoginWithFacebookToken()

        }

        return flowOf(false)

    }


    sealed class InMsg {

        sealed class View : InMsg() {

            object OnSelectAuthViewReady : View()

            object OnSelectAuthViewStop : View()

            object ShowLogin : View()

            object OnLoginViewReady : View()

            object OnLoginFragmentResult : View()

            object OnLoginViewStop : View()

            class DoLogin(val loginReq: LoginReq) : View()

            object ShowSignUp : View()

            object OnSignupViewReady : View()

            object OnSignupFragmentResult : View()

            object OnSignupViewStop : View()

            class DoSignUp(val signupReq: SignupReq) : View()

        }

    }

    sealed class OutMsg {

        sealed class Login : OutMsg() {

            object AuthSuccess : Login()

            class AuthError(val error: String) : Login()

        }

        sealed class Signup : OutMsg() {

            object Success : Signup()

            class Error(val error: String) : Signup()

        }

        sealed class View : OutMsg() {

            sealed class Login : View() {

                object OnLoad : Login()

                object OnSuccess : Login()

                class OnError(val th: Throwable) : Login()

            }

            sealed class Signup : View() {

                object OnLoad : Signup()

                object OnSuccess : Signup()

                class OnError(val th: Throwable) : Signup()

            }

        }

    }

}

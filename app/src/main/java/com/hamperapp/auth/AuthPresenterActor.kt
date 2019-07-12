package com.hamperapp.auth

import com.hamperapp.UIActorMsg
import com.hamperapp.actor.BaseActor
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class AuthPresenterActor(
    private var authActor: AuthActor,
    private var uiSendChannel: SendChannel<UIActorMsg>,
    private var observerChannel: SendChannel<OutMsg>?
): BaseActor<AuthPresenterActor.InMsg>() {

    enum class Stage {
        Login,
        Signup
    }

    private var stage = Stage.Login

    lateinit var fragmentSink: SendChannel<OutMsg.View>


    override fun onCommonAction(commonMsg: BaseActor.InMsg) {

        when (commonMsg) {

            BaseActor.InMsg.OnStart -> {

                when (stage) {

                    Stage.Login -> showLogin()

                    Stage.Signup -> showSignup()

                }

            }

            BaseActor.InMsg.OnStop -> {
                scope.coroutineContext.cancelChildren()
            }

            BaseActor.InMsg.OnBack -> { onBackPressed() }

        }

    }

    override fun onAction(inMsg: InMsg) {

        when (inMsg) {

            InMsg.View.OnLoginViewReady -> {}

            is InMsg.View.DoLogin -> {

                scope.launch {

                    fragmentSink.send(OutMsg.View.OnLoad)

                    delay(2000)

                    fragmentSink.send(OutMsg.View.OnSuccess)

                    delay(1000)

                    observerChannel?.send(OutMsg.AuthSuccess("SUCCESS_TOKEN"))

                }

            }

            InMsg.View.ShowSignUp -> { showSignup() }

            InMsg.View.OnLoginViewStop -> {
                scope.coroutineContext.cancelChildren()
            }

            InMsg.View.OnSignupViewReady -> {}

            InMsg.View.DoSignUp -> {}

            InMsg.View.OnSignupViewStop -> {
                scope.coroutineContext.cancelChildren()
            }

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

            Stage.Login -> {

                scope.launch {

                    observerChannel?.send(OutMsg.AuthError("Back Pressed Cancelled"))

                }

            }

            Stage.Signup -> { showLogin() }

        }

    }

    sealed class InMsg {

        sealed class View : InMsg() {

            object OnLoginViewReady : View()

            object OnLoginViewStop : View()

            class DoLogin(val username: String, val password: String) : View()

            object ShowSignUp : View()

            object OnSignupViewReady : View()

            object OnSignupViewStop : View()

            object DoSignUp : View()

        }

    }

    sealed class OutMsg {

        class AuthSuccess(val token: String) : OutMsg()

        class AuthError(val error: String) : OutMsg()


        sealed class View : OutMsg() {

            object OnLoad : View()

            object OnSuccess : View()

            object OnError : View()

        }

    }

}

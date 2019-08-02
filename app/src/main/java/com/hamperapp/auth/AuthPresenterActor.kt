package com.hamperapp.auth

import com.hamperapp.UIActorMsg
import com.hamperapp.actor.Actor
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class AuthPresenterActor(
    private var authActor: AuthActor,
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


    override fun start() {
        super.start()

        when (stage) {

            Stage.Select -> showSelect()

            Stage.Login -> showLogin()

            Stage.Signup -> showSignup()

        }

    }

    override fun onAction(inMsg: InMsg) {

        when (inMsg) {

            InMsg.View.OnSelectAuthViewReady -> {}

            InMsg.View.OnSelectAuthViewStop -> {}

            InMsg.View.ShowLogin -> { showLogin() }

            InMsg.View.OnLoginViewReady -> {}

            is InMsg.View.DoLogin -> {

                scope.launch {

                    fragmentChannel.send(OutMsg.View.OnLoad)

                    delay(1000)

                    fragmentChannel.send(OutMsg.View.OnSuccess)

                    delay(500)

                    parentChannel.send(OutMsg.AuthSuccess("SUCCESS_TOKEN"))

                }

            }

            InMsg.View.OnLoginViewStop -> {}

            InMsg.View.ShowSignUp -> { showSignup() }

            InMsg.View.OnSignupViewReady -> {}

            InMsg.View.DoSignUp -> {}

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

                    parentChannel.send(OutMsg.AuthError("Back Pressed Cancelled"))

                }

            }

            Stage.Login -> { showSelect() }

            Stage.Signup -> { showSelect() }

        }

    }

    sealed class InMsg {

        sealed class View : InMsg() {

            object OnSelectAuthViewReady : View()

            object OnSelectAuthViewStop : View()

            object ShowLogin : View()

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

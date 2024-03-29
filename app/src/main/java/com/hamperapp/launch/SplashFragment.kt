package com.hamperapp.launch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import com.hamperapp.R
import kotlinx.android.synthetic.main.fragment_splash.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.channels.consumeEach


class SplashFragment : Fragment() {

    private val fragmentCoroutineScope = CoroutineScope(Dispatchers.Main)

    private lateinit var actor: SplashActor


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onStart() {
        super.onStart()

        actor.fragmentChannel = fragmentCoroutineScope.actor {

            consumeEach { event ->

                when (event) {

                    /*
                    SplashActor.OutMsg.View.ShowZipInput -> {

                        MaterialDialog(requireContext()).show {

                            cancelOnTouchOutside(false)

                            input(allowEmpty = false, hintRes = R.string.zipcode) { dialog, text ->

                                actor.send(
                                    SplashActor.InMsg.View.OnZipcodeEnter(text.toString())
                                )

                            }

                            positiveButton(R.string.submit)

                        }//.window?.attributes?.windowAnimations

                    }
                    */

                    SplashActor.OutMsg.View.OnLoad -> {

                        progressBar.visibility = View.VISIBLE

                    }

                    SplashActor.OutMsg.View.OnLoadFinish -> {

                        progressBar.visibility = View.GONE

                    }

                    /*(
                    is SplashActor.OutMsg.View.OnZipcodeError -> {

                        progressBar.visibility = View.GONE

                        MaterialDialog(requireContext()).show {

                            title(text = "Error")

                            message(text = event.message)

                        }

                    }

                    */
                }

            }

        }

        actor.send(SplashActor.InMsg.View.OnViewReady)

    }

    override fun onStop() {
        super.onStop()

        actor.send(SplashActor.InMsg.View.OnViewStop)

        fragmentCoroutineScope.coroutineContext.cancelChildren()

    }

    companion object {

        @JvmStatic
        fun newInstance(actor: SplashActor) =

            SplashFragment().apply {

                this.actor = actor

            }

    }

}

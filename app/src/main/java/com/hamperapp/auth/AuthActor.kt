package com.hamperapp.auth

import com.hamperapp.HamperApplication
import com.hamperapp.actor.Actor
import io.reactivex.Single
import io.reactivex.SingleEmitter


class AuthActor : Actor<AuthMsg>() {


    override fun onAction(action: AuthMsg) {


    }

    fun authorize(): Single<AuthMsg> {


        return Single.create { emitter: SingleEmitter<AuthMsg> ->

            val token = HamperApplication
                .instance
                .authAPI
                .authenticate(
                    AuthMsg.AuthReq("", "")
                )
                .execute()
                .body()

            if (token == null) {
                emitter.onSuccess(AuthMsg.AuthFail)
            }
            else {
                emitter.onSuccess(AuthMsg.AuthSuccess(token))
            }

        }

    }

}

sealed class AuthMsg {

    class AuthReq(val userId: String, val password: String) : AuthMsg()

    class AuthSuccess(val token: String) : AuthMsg()

    object AuthFail : AuthMsg()

}

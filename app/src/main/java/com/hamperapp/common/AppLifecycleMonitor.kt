package com.hamperapp.common

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.os.Handler
import android.os.Looper

class AppLifecycleMonitor private constructor(
    private var activityLifecycleCallback: ActivityLifecycleCallbackImpl
) {

    val isOpened: Boolean
        get() = activityLifecycleCallback.isOpened

    val isForeground: Boolean
        get() = activityLifecycleCallback.isForeground


    class Builder {

        fun build(
            app: Application,
            appLifecycleListener: AppLifecycleListener
        ): AppLifecycleMonitor {

            val activityLifecycleCallback = ActivityLifecycleCallbackImpl(appLifecycleListener)

            app.registerActivityLifecycleCallbacks(activityLifecycleCallback)

            return AppLifecycleMonitor(activityLifecycleCallback)
        }

    }

    private class ActivityLifecycleCallbackImpl constructor(
        private val appLifecycleListener: AppLifecycleListener
    ) : Application.ActivityLifecycleCallbacks {

        private val mainHandler: Handler = Handler(Looper.getMainLooper())

        private var activityCreatedCount = 0

        private var activityStartedCount = 0

        internal var isOpened = false

        internal var isForeground = false

        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {

            if (activityCreatedCount == 0) {

                if (!isOpened) {
                    isOpened = true
                    appLifecycleListener.onAppOpened()
                }

            }

            activityCreatedCount++

        }

        override fun onActivityStarted(activity: Activity) {

            if (activityStartedCount == 0) {

                if (!isForeground) {
                    isForeground = true
                    appLifecycleListener.onAppGotoForeground()
                }

            }

            activityStartedCount++

        }

        override fun onActivityResumed(activity: Activity) {}

        override fun onActivityPaused(activity: Activity) {}

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

        override fun onActivityStopped(activity: Activity) {

            // When transitioning from one Activity to another a user may call the finish() method
            // before or after starting the new Activity. Also the asynchronous nature of the
            // finish() method could invoke onStop() on the dying Activity before calling
            // onStart() on the new Activity.
            // To overcome this problem every time onActivityStopped() is called we don't run
            // the logic immediate, but we post our block of code to run on the MainQueue later.
            // It Assumes the finish() method posted its work before our block. Otherwise a delay
            // would be necessary.

            mainHandler.post {
                if (activityStartedCount > 0) {
                    activityStartedCount--
                }

                if (activityStartedCount == 0) {

                    if (isForeground) {
                        isForeground = false
                        appLifecycleListener.onAppGotoBackground()
                    }

                }
            }

        }

        override fun onActivityDestroyed(activity: Activity) {

            // When transitioning from one Activity to another a user may call the finish() method
            // before or after starting the new Activity. Also the asynchronous nature of the
            // finish() method could invoke onDestroy on the dying Activity before calling
            // onCreate() on the new Activity.
            // To overcome this problem every time onActivityDestroyed() is called we don't run
            // the logic immediate, but we post our block of code to run on the MainQueue later.
            // It Assumes the finish() method posted its work before our block. Otherwise a delay
            // would be necessary.

            mainHandler.post {
                if (activityCreatedCount > 0) {
                    activityCreatedCount--
                }

                if (activityCreatedCount == 0) {

                    if (isOpened) {
                        isOpened = false
                        appLifecycleListener.onAppClosed()
                    }

                }
            }

        }

    }

}

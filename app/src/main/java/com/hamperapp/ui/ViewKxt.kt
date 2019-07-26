package com.hamperapp.ui

import android.view.View
import androidx.drawerlayout.widget.DrawerLayout


fun DrawerLayout.toggle(navigationView: View) {

	if (isDrawerOpen(navigationView)) {

		closeDrawer(navigationView)

	} else {

		openDrawer(navigationView)

	}

}
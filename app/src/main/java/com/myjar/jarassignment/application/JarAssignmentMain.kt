package com.myjar.jarassignment.application

import android.app.Application
import io.realm.Realm

class JarAssignmentMain : Application() {
    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
    }
}
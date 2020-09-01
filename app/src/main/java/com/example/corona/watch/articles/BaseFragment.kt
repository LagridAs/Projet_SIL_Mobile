package com.example.corona.watch.articles

import android.os.Bundle
import androidx.fragment.app.Fragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

    abstract class BaseFragment: Fragment(), CoroutineScope {
        private  lateinit var job: Job//background task in coroutine
        override val coroutineContext: CoroutineContext
            get() = job + Dispatchers.Main

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            job= Job()
        }

        override fun onDestroy() {
            super.onDestroy()
            job.cancel()
        }
    }


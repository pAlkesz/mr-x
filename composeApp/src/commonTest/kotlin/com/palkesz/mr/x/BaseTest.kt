package com.palkesz.mr.x

import androidx.annotation.CallSuper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

abstract class BaseTest {

    @CallSuper
    @BeforeTest
    open fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher(TestCoroutineScheduler()))
    }

    @CallSuper
    @AfterTest
    open fun tearDown() {
        Dispatchers.resetMain()
    }
}

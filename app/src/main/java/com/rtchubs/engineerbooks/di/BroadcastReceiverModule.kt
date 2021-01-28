package com.rtchubs.engineerbooks.di

import com.rtchubs.engineerbooks.ui.receiver.TimeChangeBroadcastReceiver
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class BroadcastReceiverModule {
    @ContributesAndroidInjector
    abstract fun contributesTimeChangeBroadcastReceiver() : TimeChangeBroadcastReceiver
}
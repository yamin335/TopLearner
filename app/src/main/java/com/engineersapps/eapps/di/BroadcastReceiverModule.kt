package com.engineersapps.eapps.di

import com.engineersapps.eapps.ui.receiver.TimeChangeBroadcastReceiver
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class BroadcastReceiverModule {
    @ContributesAndroidInjector
    abstract fun contributesTimeChangeBroadcastReceiver() : TimeChangeBroadcastReceiver
}
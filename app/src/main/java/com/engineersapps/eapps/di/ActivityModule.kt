package com.engineersapps.eapps.di

import com.engineersapps.eapps.ui.LiveClassActivity
import com.engineersapps.eapps.ui.LoginActivity
import com.engineersapps.eapps.ui.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityModule {
    @ContributesAndroidInjector(modules = [FragmentBuildersModule::class])
    abstract fun contributeLoginActivity(): LoginActivity
    @ContributesAndroidInjector(modules = [FragmentBuildersModule::class])
    abstract fun contributeMainActivity(): MainActivity
    @ContributesAndroidInjector(modules = [FragmentBuildersModule::class])
    abstract fun contributeLiveClassActivity(): LiveClassActivity
}
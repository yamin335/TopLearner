package com.engineersapps.eapps.di

import com.engineersapps.eapps.worker.DaggerWorkerFactory
import com.engineersapps.eapps.worker.TokenRefreshWorker
import com.engineersapps.eapps.worker.WorkerKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Suppress("unused")
@Module
abstract class WorkerBindingModule {

    /*injector for DaggerWorkerFactory*/
    @Binds
    @IntoMap
    @WorkerKey(TokenRefreshWorker::class)
    abstract fun bindTokenRefreshWorker(factory: TokenRefreshWorker.Factory):
            DaggerWorkerFactory.ChildWorkerFactory


}

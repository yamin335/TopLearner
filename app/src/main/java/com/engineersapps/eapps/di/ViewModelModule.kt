package com.engineersapps.eapps.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.engineersapps.eapps.ViewModelFactory
import com.engineersapps.eapps.ui.LiveClassActivityViewModel
import com.engineersapps.eapps.ui.LoginActivityViewModel
import com.engineersapps.eapps.ui.MainActivityViewModel
import com.engineersapps.eapps.ui.about_us.AboutUsViewModel
import com.engineersapps.eapps.ui.chapter_list.ChapterListViewModel
import com.engineersapps.eapps.ui.e_code.ECodeViewModel
import com.engineersapps.eapps.ui.free_book.FreeBooksViewModel
import com.engineersapps.eapps.ui.history.HistoryViewModel
import com.engineersapps.eapps.ui.home.*
import com.engineersapps.eapps.ui.live_class_schedule.LiveClassScheduleViewModel
import com.engineersapps.eapps.ui.live_video.LiveVideoViewModel
import com.engineersapps.eapps.ui.login.SignInViewModel
import com.engineersapps.eapps.ui.login.ViewPagerViewModel
import com.engineersapps.eapps.ui.more.MoreViewModel
import com.engineersapps.eapps.ui.my_course.BooksViewModel
import com.engineersapps.eapps.ui.my_course.MyCourseViewModel
import com.engineersapps.eapps.ui.notice_board.NoticeBoardViewModel
import com.engineersapps.eapps.ui.offer.OfferViewModel
import com.engineersapps.eapps.ui.otp_signin.OtpSignInViewModel
import com.engineersapps.eapps.ui.payment.PaymentViewModel
import com.engineersapps.eapps.ui.pin_number.PinNumberViewModel
import com.engineersapps.eapps.ui.profile_signin.ClassEditViewModel
import com.engineersapps.eapps.ui.profile_signin.DistrictEditViewModel
import com.engineersapps.eapps.ui.profile_signin.ProfileSignInViewModel
import com.engineersapps.eapps.ui.profile_signin.UpazillaEditViewModel
import com.engineersapps.eapps.ui.profiles.ProfileSettingsViewModel
import com.engineersapps.eapps.ui.settings.SettingsViewModel
import com.engineersapps.eapps.ui.splash.SplashViewModel
import com.engineersapps.eapps.ui.terms_and_conditions.TermsViewModel
import com.engineersapps.eapps.ui.transaction.TransactionViewModel
import com.engineersapps.eapps.ui.video_play.LoadWebViewViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Suppress("unused")
@Module
abstract class ViewModelModule {

    // PSB View Model Bind Here
    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(LoginActivityViewModel::class)
    abstract fun bindLoginActivityViewModel(viewModel: LoginActivityViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MainActivityViewModel::class)
    abstract fun bindMainActivityViewModel(viewModel: MainActivityViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MoreViewModel::class)
    abstract fun bindMoreViewModel(viewModel: MoreViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(HistoryViewModel::class)
    abstract fun bindHistoryViewModel(viewModel: HistoryViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(VideoListViewModel::class)
    abstract fun bindVideoListViewModel(viewModel: VideoListViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(Tab3ViewModel::class)
    abstract fun bindQuizListViewModel(viewModel: Tab3ViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(Tab2ViewModel::class)
    abstract fun bindSetCViewModel(viewModel: Tab2ViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SplashViewModel::class)
    abstract fun bindSplashViewModel(viewModel: SplashViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SignInViewModel::class)
    abstract fun bindSignInViewModel(viewModel: SignInViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel::class)
    abstract fun bindHomeViewModel(viewModel: HomeViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SettingsViewModel::class)
    abstract fun bindSetupSettingsViewModel(viewModel: SettingsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ViewPagerViewModel::class)
    abstract fun bindSetupViewPagerViewModel(viewModel: ViewPagerViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ChapterListViewModel::class)
    abstract fun bindSetupChapterListViewModel(viewModel: ChapterListViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(LoadWebViewViewModel::class)
    abstract fun bindSetupLoadWebViewViewModel(viewModel: LoadWebViewViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(OtpSignInViewModel::class)
    abstract fun bindSetupOtpSignInViewModel(viewModel: OtpSignInViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PinNumberViewModel::class)
    abstract fun bindSetupPinNumberViewModel(viewModel: PinNumberViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ProfileSignInViewModel::class)
    abstract fun bindSetupProfileSignInViewModel(viewModel: ProfileSignInViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TermsViewModel::class)
    abstract fun bindTermsViewModel(viewModel: TermsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ProfileSettingsViewModel::class)
    abstract fun bindProfileSettingsViewModel(viewModel: ProfileSettingsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ClassEditViewModel::class)
    abstract fun bindClassEditViewModel(viewModel: ClassEditViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(DistrictEditViewModel::class)
    abstract fun bindDistrictEditViewModel(viewModel: DistrictEditViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(UpazillaEditViewModel::class)
    abstract fun bindUpazillaEditViewModel(viewModel: UpazillaEditViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PaymentViewModel::class)
    abstract fun bindPaymentViewModel(viewModel: PaymentViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TransactionViewModel::class)
    abstract fun bindTransactionViewModel(viewModel: TransactionViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(LiveVideoViewModel::class)
    abstract fun bindLiveVideoViewModel(viewModel: LiveVideoViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(Tab1ViewModel::class)
    abstract fun bindSolutionViewModel(viewModel: Tab1ViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(NoticeBoardViewModel::class)
    abstract fun bindNoticeBoardViewModel(viewModel: NoticeBoardViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(OfferViewModel::class)
    abstract fun bindOfferViewModel(viewModel: OfferViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ECodeViewModel::class)
    abstract fun bindECodeViewModel(viewModel: ECodeViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AboutUsViewModel::class)
    abstract fun bindAboutUsViewModel(viewModel: AboutUsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(LiveClassScheduleViewModel::class)
    abstract fun bindLiveClassScheduleViewModel(viewModel: LiveClassScheduleViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(LiveClassActivityViewModel::class)
    abstract fun bindLiveClassActivityViewModel(viewModel: LiveClassActivityViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FreeBooksViewModel::class)
    abstract fun bindFreeBooksViewModel(viewModel: FreeBooksViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MyCourseViewModel::class)
    abstract fun bindMyCourseViewModel(viewModel: MyCourseViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CourseDetailsViewModel::class)
    abstract fun bindCourseDetailsViewModel(viewModel: CourseDetailsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(Tab4ViewModel::class)
    abstract fun bindTab4ViewModel(viewModel: Tab4ViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(BooksViewModel::class)
    abstract fun bindBooksViewModel(viewModel: BooksViewModel): ViewModel
}
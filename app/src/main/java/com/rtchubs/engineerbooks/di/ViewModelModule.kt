package com.rtchubs.engineerbooks.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rtchubs.engineerbooks.ViewModelFactory
import com.rtchubs.engineerbooks.nid_scan.NIDScanCameraXViewModel
import com.rtchubs.engineerbooks.ui.LoginActivityViewModel
import com.rtchubs.engineerbooks.ui.MainActivityViewModel
import com.rtchubs.engineerbooks.ui.about_us.AboutUsViewModel
import com.rtchubs.engineerbooks.ui.add_payment_methods.AddBankViewModel
import com.rtchubs.engineerbooks.ui.add_payment_methods.AddCardViewModel
import com.rtchubs.engineerbooks.ui.add_payment_methods.AddPaymentMethodsViewModel
import com.rtchubs.engineerbooks.ui.chapter_list.ChapterListViewModel
import com.rtchubs.engineerbooks.ui.e_code.ECodeViewModel
import com.rtchubs.engineerbooks.ui.exams.ExamsViewModel
import com.rtchubs.engineerbooks.ui.history.HistoryViewModel
import com.rtchubs.engineerbooks.ui.home.*
import com.rtchubs.engineerbooks.ui.how_works.HowWorksViewModel
import com.rtchubs.engineerbooks.ui.info.InfoViewModel
import com.rtchubs.engineerbooks.ui.live_class_schedule.LiveClassScheduleViewModel
import com.rtchubs.engineerbooks.ui.live_video.LiveVideoViewModel
import com.rtchubs.engineerbooks.ui.login.SignInViewModel
import com.rtchubs.engineerbooks.ui.login.ViewPagerViewModel
import com.rtchubs.engineerbooks.ui.more.MoreViewModel
import com.rtchubs.engineerbooks.ui.profiles.ProfileSettingsViewModel
import com.rtchubs.engineerbooks.ui.notice_board.NoticeBoardViewModel
import com.rtchubs.engineerbooks.ui.offer.OfferViewModel
import com.rtchubs.engineerbooks.ui.on_boarding.tou.TouViewModel
import com.rtchubs.engineerbooks.ui.otp.OtpViewModel
import com.rtchubs.engineerbooks.ui.otp_signin.OtpSignInViewModel
import com.rtchubs.engineerbooks.ui.payment.PaymentViewModel
import com.rtchubs.engineerbooks.ui.pin_number.PinNumberViewModel
import com.rtchubs.engineerbooks.ui.pre_on_boarding.PreOnBoardingViewModel
import com.rtchubs.engineerbooks.ui.profile_signin.ClassEditViewModel
import com.rtchubs.engineerbooks.ui.profile_signin.DistrictEditViewModel
import com.rtchubs.engineerbooks.ui.profile_signin.ProfileSignInViewModel
import com.rtchubs.engineerbooks.ui.profile_signin.UpazillaEditViewModel
import com.rtchubs.engineerbooks.ui.profiles.ProfilesViewModel
import com.rtchubs.engineerbooks.ui.registration.RegistrationViewModel
import com.rtchubs.engineerbooks.ui.settings.SettingsViewModel
import com.rtchubs.engineerbooks.ui.setup.SetupViewModel
import com.rtchubs.engineerbooks.ui.setup_complete.SetupCompleteViewModel
import com.rtchubs.engineerbooks.ui.solution.SolutionViewModel
import com.rtchubs.engineerbooks.ui.splash.SplashViewModel
import com.rtchubs.engineerbooks.ui.terms_and_conditions.TermsViewModel
import com.rtchubs.engineerbooks.ui.topup.TopUpAmountViewModel
import com.rtchubs.engineerbooks.ui.topup.TopUpBankCardViewModel
import com.rtchubs.engineerbooks.ui.topup.TopUpMobileViewModel
import com.rtchubs.engineerbooks.ui.topup.TopUpPinViewModel
import com.rtchubs.engineerbooks.ui.transaction.TransactionViewModel
import com.rtchubs.engineerbooks.ui.video_play.LoadWebViewViewModel
import com.rtchubs.engineerbooks.ui.video_play.VideoPlayViewModel
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
    @ViewModelKey(MoreBookListViewModel::class)
    abstract fun bindMoreBookListViewModel(viewModel: MoreBookListViewModel): ViewModel

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
    @ViewModelKey(QuizListViewModel::class)
    abstract fun bindQuizListViewModel(viewModel: QuizListViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SetCViewModel::class)
    abstract fun bindSetCViewModel(viewModel: SetCViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(InfoViewModel::class)
    abstract fun bindInfoViewModel(viewModel: InfoViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ExamsViewModel::class)
    abstract fun bindExamsViewModel(viewModel: ExamsViewModel): ViewModel

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
    @ViewModelKey(NIDScanCameraXViewModel::class)
    abstract fun bindNIDScanCameraXViewModel(viewModel: NIDScanCameraXViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PreOnBoardingViewModel::class)
    abstract fun bindPreOnBoardingViewModel(viewModel: PreOnBoardingViewModel): ViewModel


    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel::class)
    abstract fun bindHomeViewModel(viewModel: HomeViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(HowWorksViewModel::class)
    abstract fun bindHowWorksViewModel(viewModel: HowWorksViewModel): ViewModel


    @Binds
    @IntoMap
    @ViewModelKey(RegistrationViewModel::class)
    abstract fun bindRegistrationViewModel(viewModel: RegistrationViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(OtpViewModel::class)
    abstract fun bindOtpViewModel(viewModel: OtpViewModel): ViewModel


    @Binds
    @IntoMap
    @ViewModelKey(TouViewModel::class)
    abstract fun bindTouViewModel(viewModel: TouViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SetupViewModel::class)
    abstract fun bindSetupViewModel(viewModel: SetupViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SetupCompleteViewModel::class)
    abstract fun bindSetupCompleteViewModel(viewModel: SetupCompleteViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ProfilesViewModel::class)
    abstract fun bindSetupProfilesViewModel(viewModel: ProfilesViewModel): ViewModel

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
    @ViewModelKey(VideoPlayViewModel::class)
    abstract fun bindSetupVideoPlayViewModel(viewModel: VideoPlayViewModel): ViewModel

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
    @ViewModelKey(AddPaymentMethodsViewModel::class)
    abstract fun bindAddPaymentMethodsViewModel(viewModel: AddPaymentMethodsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AddBankViewModel::class)
    abstract fun bindAddBankViewModel(viewModel: AddBankViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AddCardViewModel::class)
    abstract fun bindAddCardViewModel(viewModel: AddCardViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TopUpMobileViewModel::class)
    abstract fun bindTopUpMobileViewModel(viewModel: TopUpMobileViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TopUpAmountViewModel::class)
    abstract fun bindTopUpAmountViewModel(viewModel: TopUpAmountViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TopUpPinViewModel::class)
    abstract fun bindTopUpPinViewModel(viewModel: TopUpPinViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TopUpBankCardViewModel::class)
    abstract fun bindTopUpBankCardViewModel(viewModel: TopUpBankCardViewModel): ViewModel

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
    @ViewModelKey(SolutionViewModel::class)
    abstract fun bindSolutionViewModel(viewModel: SolutionViewModel): ViewModel

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
}
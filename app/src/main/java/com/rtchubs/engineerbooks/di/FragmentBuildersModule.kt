package com.rtchubs.engineerbooks.di

import com.rtchubs.engineerbooks.nid_scan.NIDScanCameraXFragment
import com.rtchubs.engineerbooks.ui.about_us.AboutUsFragment
import com.rtchubs.engineerbooks.ui.add_payment_methods.AddBankFragment
import com.rtchubs.engineerbooks.ui.add_payment_methods.AddCardFragment
import com.rtchubs.engineerbooks.ui.add_payment_methods.AddPaymentMethodsFragment
import com.rtchubs.engineerbooks.ui.bkash.BKashDialogFragment
import com.rtchubs.engineerbooks.ui.chapter_list.ChapterListFragment
import com.rtchubs.engineerbooks.ui.e_code.ECodeFragment
import com.rtchubs.engineerbooks.ui.exams.ExamsFragment
import com.rtchubs.engineerbooks.ui.history.HistoryFragment
import com.rtchubs.engineerbooks.ui.home.*
import com.rtchubs.engineerbooks.ui.how_works.HowWorksFragment
import com.rtchubs.engineerbooks.ui.info.InfoFragment
import com.rtchubs.engineerbooks.ui.live_video.LiveVideoFragment
import com.rtchubs.engineerbooks.ui.login.SignInFragment
import com.rtchubs.engineerbooks.ui.terms_and_conditions.TermsAndConditionsFragment
import com.rtchubs.engineerbooks.ui.on_boarding.tou.TouFragment
import com.rtchubs.engineerbooks.ui.otp.OtpFragment
import com.rtchubs.engineerbooks.ui.pre_on_boarding.PreOnBoardingFragment
import com.rtchubs.engineerbooks.ui.profiles.ProfilesFragment
import com.rtchubs.engineerbooks.ui.registration.RegistrationFragment
import com.rtchubs.engineerbooks.ui.settings.SettingsFragment
import com.rtchubs.engineerbooks.ui.setup.SetupFragment
import com.rtchubs.engineerbooks.ui.setup_complete.SetupCompleteFragment
import com.rtchubs.engineerbooks.ui.splash.SplashFragment
import com.rtchubs.engineerbooks.ui.video_play.LoadWebViewFragment
import com.rtchubs.engineerbooks.ui.video_play.VideoPlayFragment
import com.rtchubs.engineerbooks.ui.login.ViewPagerFragment
import com.rtchubs.engineerbooks.ui.more.MoreFragment
import com.rtchubs.engineerbooks.ui.more.ProfileSettingsFragment
import com.rtchubs.engineerbooks.ui.notice_board.NoticeBoardFragment
import com.rtchubs.engineerbooks.ui.offer.OfferFragment
import com.rtchubs.engineerbooks.ui.otp_signin.OtpSignInFragment
import com.rtchubs.engineerbooks.ui.payment.PaymentFragment
import com.rtchubs.engineerbooks.ui.payment.PaymentFragmentMore
import com.rtchubs.engineerbooks.ui.pin_number.PinNumberFragment
import com.rtchubs.engineerbooks.ui.profile_signin.ClassEditFragment
import com.rtchubs.engineerbooks.ui.profile_signin.DistrictEditFragment
import com.rtchubs.engineerbooks.ui.profile_signin.ProfileSignInFragment
import com.rtchubs.engineerbooks.ui.profile_signin.UpazillaEditFragment
import com.rtchubs.engineerbooks.ui.solution.SolutionFragment
import com.rtchubs.engineerbooks.ui.topup.TopUpAmountFragment
import com.rtchubs.engineerbooks.ui.topup.TopUpBankCardFragment
import com.rtchubs.engineerbooks.ui.topup.TopUpMobileFragment
import com.rtchubs.engineerbooks.ui.topup.TopUpPinFragment
import com.rtchubs.engineerbooks.ui.transaction.TransactionFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class FragmentBuildersModule {

    @ContributesAndroidInjector
    abstract fun contributeSplashFragment(): SplashFragment

    @ContributesAndroidInjector
    abstract fun contributeTermsAndConditionsFragment(): TermsAndConditionsFragment

    @ContributesAndroidInjector
    abstract fun contributeMoreBookListFragment(): MoreBookListFragment

    @ContributesAndroidInjector
    abstract fun contributeSignInFragment(): SignInFragment

    @ContributesAndroidInjector
    abstract fun contributeExamsFragment(): ExamsFragment

    @ContributesAndroidInjector
    abstract fun contributeInfoFragment(): InfoFragment

    @ContributesAndroidInjector
    abstract fun contributeHistoryFragment(): HistoryFragment

    @ContributesAndroidInjector
    abstract fun contributeNIDScanCameraXFragment(): NIDScanCameraXFragment

    @ContributesAndroidInjector
    abstract fun contributePreOnBoardingFragment(): PreOnBoardingFragment

    @ContributesAndroidInjector
    abstract fun contributeHomeFragment(): HomeFragment

    @ContributesAndroidInjector
    abstract fun contributeMoreFragment(): MoreFragment

    @ContributesAndroidInjector
    abstract fun contributeVideoListFragment(): VideoListFragment

    @ContributesAndroidInjector
    abstract fun contributeSetBFragment(): QuizListFragment

    @ContributesAndroidInjector
    abstract fun contributeQuizListFragment(): SetCFragment

    @ContributesAndroidInjector
    abstract fun contributeHome2Fragment(): Home2Fragment

    @ContributesAndroidInjector
    abstract fun contributeAddPaymentMethodsFragment(): AddPaymentMethodsFragment


    @ContributesAndroidInjector
    abstract fun contributeHowWorksFragment(): HowWorksFragment

    @ContributesAndroidInjector
    abstract fun contributeRegistrationFragment(): RegistrationFragment

    @ContributesAndroidInjector
    abstract fun contributeOtpFragment(): OtpFragment

    @ContributesAndroidInjector
    abstract fun contributeTouFragment(): TouFragment

    @ContributesAndroidInjector
    abstract fun contributeSetupFragment(): SetupFragment

    @ContributesAndroidInjector
    abstract fun contributeSetupCompleteFragment(): SetupCompleteFragment

    @ContributesAndroidInjector
    abstract fun contributeProfilesFragment(): ProfilesFragment

    @ContributesAndroidInjector
    abstract fun contributeSettingsFragment(): SettingsFragment

    @ContributesAndroidInjector
    abstract fun contributeViewPagerFragment(): ViewPagerFragment

    @ContributesAndroidInjector
    abstract fun contributeChapterListFragment(): ChapterListFragment

    @ContributesAndroidInjector
    abstract fun contributeVideoPlayFragment(): VideoPlayFragment

    @ContributesAndroidInjector
    abstract fun contributeLoadWebViewFragment(): LoadWebViewFragment

    @ContributesAndroidInjector
    abstract fun contributeOtpSignInFragment(): OtpSignInFragment

    @ContributesAndroidInjector
    abstract fun contributePinNumberFragment(): PinNumberFragment

    @ContributesAndroidInjector
    abstract fun contributeProfileSignInFragment(): ProfileSignInFragment

    @ContributesAndroidInjector
    abstract fun contributeAddBankFragment(): AddBankFragment

    @ContributesAndroidInjector
    abstract fun contributeAddCardFragment(): AddCardFragment

    @ContributesAndroidInjector
    abstract fun contributeTopUpMobileFragment(): TopUpMobileFragment

    @ContributesAndroidInjector
    abstract fun contributeTopUpAmountFragment(): TopUpAmountFragment

    @ContributesAndroidInjector
    abstract fun contributeTopUpPinFragment(): TopUpPinFragment

    @ContributesAndroidInjector
    abstract fun contributeTopUpBankCardFragment(): TopUpBankCardFragment

    @ContributesAndroidInjector
    abstract fun contributeProfileSettingsFragment(): ProfileSettingsFragment

    @ContributesAndroidInjector
    abstract fun contributeClassEditFragment(): ClassEditFragment

    @ContributesAndroidInjector
    abstract fun contributeDistrictEditFragment(): DistrictEditFragment

    @ContributesAndroidInjector
    abstract fun contributeUpazillaEditFragment(): UpazillaEditFragment

    @ContributesAndroidInjector
    abstract fun contributePaymentFragment(): PaymentFragment

    @ContributesAndroidInjector
    abstract fun contributePaymentFragmentMore(): PaymentFragmentMore

    @ContributesAndroidInjector
    abstract fun contributeTransactionFragment(): TransactionFragment

    @ContributesAndroidInjector
    abstract fun contributeLiveVideoFragment(): LiveVideoFragment

    @ContributesAndroidInjector
    abstract fun contributeSolutionFragment(): SolutionFragment

    @ContributesAndroidInjector
    abstract fun contributeNoticeBoardFragment(): NoticeBoardFragment

    @ContributesAndroidInjector
    abstract fun contributeOfferFragment(): OfferFragment

    @ContributesAndroidInjector
    abstract fun contributeECodeFragment(): ECodeFragment

    @ContributesAndroidInjector
    abstract fun contributeAboutUsFragment(): AboutUsFragment

    @ContributesAndroidInjector
    abstract fun contributeBKashDialogFragment(): BKashDialogFragment
}
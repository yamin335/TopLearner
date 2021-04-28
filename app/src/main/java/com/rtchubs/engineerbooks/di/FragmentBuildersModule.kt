package com.rtchubs.engineerbooks.di

import com.rtchubs.engineerbooks.nid_scan.NIDScanCameraXFragment
import com.rtchubs.engineerbooks.ui.about_us.AboutUsFragment
import com.rtchubs.engineerbooks.ui.bkash.BKashDialogFragment
import com.rtchubs.engineerbooks.ui.chapter_list.ChapterListFragment
import com.rtchubs.engineerbooks.ui.e_code.ECodeFragment
import com.rtchubs.engineerbooks.ui.free_book.FreeBooksFragment
import com.rtchubs.engineerbooks.ui.history.HistoryFragment
import com.rtchubs.engineerbooks.ui.home.*
import com.rtchubs.engineerbooks.ui.live_class_schedule.LiveClassScheduleFragment
import com.rtchubs.engineerbooks.ui.live_video.LiveVideoFragment
import com.rtchubs.engineerbooks.ui.login.SignInFragment
import com.rtchubs.engineerbooks.ui.login.ViewPagerFragment
import com.rtchubs.engineerbooks.ui.more.MoreFragment
import com.rtchubs.engineerbooks.ui.my_course.MyCourseFragment
import com.rtchubs.engineerbooks.ui.notice_board.NoticeBoardFragment
import com.rtchubs.engineerbooks.ui.offer.OfferFragment
import com.rtchubs.engineerbooks.ui.otp_signin.OtpSignInFragment
import com.rtchubs.engineerbooks.ui.payment.PaymentFragment
import com.rtchubs.engineerbooks.ui.payment.PaymentFragmentMore
import com.rtchubs.engineerbooks.ui.pin_number.PinNumberFragment
import com.rtchubs.engineerbooks.ui.pin_number.ResetPinDialogFragment
import com.rtchubs.engineerbooks.ui.profile_signin.ClassEditFragment
import com.rtchubs.engineerbooks.ui.profile_signin.DistrictEditFragment
import com.rtchubs.engineerbooks.ui.profile_signin.ProfileSignInFragment
import com.rtchubs.engineerbooks.ui.profile_signin.UpazillaEditFragment
import com.rtchubs.engineerbooks.ui.profiles.PartnerProfileFragment
import com.rtchubs.engineerbooks.ui.profiles.ProfileSettingsFragment
import com.rtchubs.engineerbooks.ui.settings.SettingsFragment
import com.rtchubs.engineerbooks.ui.splash.SplashFragment
import com.rtchubs.engineerbooks.ui.terms_and_conditions.TermsAndConditionsFragment
import com.rtchubs.engineerbooks.ui.transaction.TransactionFragment
import com.rtchubs.engineerbooks.ui.video_play.LoadWebViewFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class FragmentBuildersModule {

    @ContributesAndroidInjector
    abstract fun contributeSplashFragment(): SplashFragment

    @ContributesAndroidInjector
    abstract fun contributeTermsAndConditionsFragment(): TermsAndConditionsFragment

    @ContributesAndroidInjector
    abstract fun contributeSignInFragment(): SignInFragment

    @ContributesAndroidInjector
    abstract fun contributeHistoryFragment(): HistoryFragment

    @ContributesAndroidInjector
    abstract fun contributeNIDScanCameraXFragment(): NIDScanCameraXFragment

    @ContributesAndroidInjector
    abstract fun contributeMoreFragment(): MoreFragment

    @ContributesAndroidInjector
    abstract fun contributeVideoListFragment(): VideoListFragment

    @ContributesAndroidInjector
    abstract fun contributeSetBFragment(): Tab3Fragment

    @ContributesAndroidInjector
    abstract fun contributeQuizListFragment(): Tab2Fragment

    @ContributesAndroidInjector
    abstract fun contributeHome2Fragment(): Home2Fragment

    @ContributesAndroidInjector
    abstract fun contributeSettingsFragment(): SettingsFragment

    @ContributesAndroidInjector
    abstract fun contributeViewPagerFragment(): ViewPagerFragment

    @ContributesAndroidInjector
    abstract fun contributeChapterListFragment(): ChapterListFragment

    @ContributesAndroidInjector
    abstract fun contributeLoadWebViewFragment(): LoadWebViewFragment

    @ContributesAndroidInjector
    abstract fun contributeOtpSignInFragment(): OtpSignInFragment

    @ContributesAndroidInjector
    abstract fun contributePinNumberFragment(): PinNumberFragment

    @ContributesAndroidInjector
    abstract fun contributeProfileSignInFragment(): ProfileSignInFragment

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
    abstract fun contributeSolutionFragment(): Tab1Fragment

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

    @ContributesAndroidInjector
    abstract fun contributeResetPinDialogFragment(): ResetPinDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeLiveClassScheduleFragment(): LiveClassScheduleFragment

    @ContributesAndroidInjector
    abstract fun contributePartnerProfileFragment(): PartnerProfileFragment

    @ContributesAndroidInjector
    abstract fun contributeFreeBooksFragment(): FreeBooksFragment

    @ContributesAndroidInjector
    abstract fun contributeMyCourseFragment(): MyCourseFragment

    @ContributesAndroidInjector
    abstract fun contributeCourseDetailsFragment(): CourseDetailsFragment
}
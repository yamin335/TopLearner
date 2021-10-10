package com.engineersapps.eapps.di

import com.engineersapps.eapps.ui.about_us.AboutUsFragment
import com.engineersapps.eapps.ui.bkash.BKashDialogFragment
import com.engineersapps.eapps.ui.chapter_list.ChapterListFragment
import com.engineersapps.eapps.ui.e_code.ECodeFragment
import com.engineersapps.eapps.ui.free_book.FreeBooksFragment
import com.engineersapps.eapps.ui.history.HistoryFragment
import com.engineersapps.eapps.ui.home.*
import com.engineersapps.eapps.ui.live_class_schedule.LiveClassScheduleFragment
import com.engineersapps.eapps.ui.live_video.LiveVideoFragment
import com.engineersapps.eapps.ui.login.SignInFragment
import com.engineersapps.eapps.ui.login.ViewPagerFragment
import com.engineersapps.eapps.ui.more.MoreFragment
import com.engineersapps.eapps.ui.my_course.BooksFragment
import com.engineersapps.eapps.ui.my_course.MyCourseFragment
import com.engineersapps.eapps.ui.notice_board.NoticeBoardFragment
import com.engineersapps.eapps.ui.offer.OfferFragment
import com.engineersapps.eapps.ui.otp_signin.OtpSignInFragment
import com.engineersapps.eapps.ui.payment.PaymentFragment
import com.engineersapps.eapps.ui.pin_number.PinNumberFragment
import com.engineersapps.eapps.ui.pin_number.ResetPinDialogFragment
import com.engineersapps.eapps.ui.profile_signin.ClassEditFragment
import com.engineersapps.eapps.ui.profile_signin.DistrictEditFragment
import com.engineersapps.eapps.ui.profile_signin.ProfileSignInFragment
import com.engineersapps.eapps.ui.profile_signin.UpazillaEditFragment
import com.engineersapps.eapps.ui.profiles.PartnerProfileFragment
import com.engineersapps.eapps.ui.profiles.ProfileSettingsFragment
import com.engineersapps.eapps.ui.settings.SettingsFragment
import com.engineersapps.eapps.ui.splash.SplashFragment
import com.engineersapps.eapps.ui.terms_and_conditions.TermsAndConditionsFragment
import com.engineersapps.eapps.ui.terms_and_conditions.TermsFragment
import com.engineersapps.eapps.ui.transaction.TransactionFragment
import com.engineersapps.eapps.ui.video_play.LoadWebViewFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class FragmentBuildersModule {

    @ContributesAndroidInjector
    abstract fun contributeSplashFragment(): SplashFragment

    @ContributesAndroidInjector
    abstract fun contributeTermsAndConditionsFragment(): TermsAndConditionsFragment

    @ContributesAndroidInjector
    abstract fun contributeTermsFragment(): TermsFragment

    @ContributesAndroidInjector
    abstract fun contributeSignInFragment(): SignInFragment

    @ContributesAndroidInjector
    abstract fun contributeHistoryFragment(): HistoryFragment

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

    @ContributesAndroidInjector
    abstract fun contributeTab4Fragment(): Tab4Fragment

    @ContributesAndroidInjector
    abstract fun contributeBooksFragment(): BooksFragment
}
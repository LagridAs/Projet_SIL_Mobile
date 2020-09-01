package com.example.corona.watch.attempts

/*
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import androidx.test.rule.GrantPermissionRule
import com.example.corona.watch.R
import com.example.corona.watch.suspectedCase.FormSignalActivity
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.*
import org.hamcrest.TypeSafeMatcher
import org.hamcrest.core.IsInstanceOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


class FormSignalActivityTest {

    @Rule
    @JvmField
    var mActivityTestRule = object: ActivityTestRule<FormSignalActivity>(
        FormSignalActivity::class.java){
        override fun getActivityIntent(): Intent {
            val targetContext = InstrumentationRegistry.getInstrumentation().targetContext
            val intent = Intent(targetContext, FormSignalActivity::class.java)
            intent.putExtra("path", "android.resource://com.example.corona.watch/drawable/cor1")
            intent.putExtra("type","IMAGE" )
            return intent
        }
    }

    @Rule
    @JvmField
    var mGrantPermissionRule =
        GrantPermissionRule.grant(
            "android.permission.READ_EXTERNAL_STORAGE"
        )

    @Test
    fun navigation() {
        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        Thread.sleep(7000)

        val imageView = onView(
            allOf(
                withId(R.id.imageView),
                childAtPosition(
                    childAtPosition(
                        IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java),
                        2
                    ),
                    3
                ),
                isDisplayed()
            )
        )
        imageView.check(matches(isDisplayed()))

        Espresso.onView(ViewMatchers.withId(R.id.description_field)).check(
            ViewAssertions.matches(
                ViewMatchers.isDisplayed()))

        val button = onView(
            allOf(
                withId(R.id.send_btn),
                childAtPosition(
                    childAtPosition(
                        IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java),
                        4
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        button.check(matches(isDisplayed()))

        val textView = onView(
            allOf(
                withId(R.id.date),
                childAtPosition(
                    childAtPosition(
                        IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java),
                        0
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        textView.check(matches(withText(containsString("01-06-2020"))))

        Espresso.onView(ViewMatchers.withId(R.id.nomUtilisateur)).check(
            ViewAssertions.matches(
                ViewMatchers.isDisplayed()))

        val button2 = onView(
            allOf(
                withId(R.id.main_btn),
                childAtPosition(
                    childAtPosition(
                        IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java),
                        0
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        button2.check(matches(isDisplayed()))

        val imageView2 = onView(
            allOf(
                withId(R.id.back_btn),
                childAtPosition(
                    childAtPosition(
                        IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        imageView2.check(matches(isDisplayed()))

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        Thread.sleep(7000)

        val appCompatImageView2 = onView(
            allOf(
                withId(R.id.back_btn),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.LinearLayout")),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        appCompatImageView2.perform(click())

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        Thread.sleep(7000)

        Espresso.onView(ViewMatchers.withId(R.id.signal_activity_layout)).check(
            ViewAssertions.matches(
                ViewMatchers.isDisplayed()))

        pressBack()

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        Thread.sleep(7000)


        Espresso.onView(ViewMatchers.withId(R.id.form_signal_layout)).check(
            ViewAssertions.matches(
                ViewMatchers.isDisplayed()))

        val appCompatButton4 = onView(
            allOf(
                withId(R.id.main_btn), withText("الصفحة الرئيسية"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.LinearLayout")),
                        0
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        appCompatButton4.perform(click())

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        Thread.sleep(7000)

        val frameLayout2 = onView(
            allOf(
                withId(R.id.main_frame),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.map),
                        0
                    ),
                    3
                ),
                isDisplayed()
            )
        )
        frameLayout2.check(matches(isDisplayed()))
    }

    @Test
    fun postTest(){
        mActivityTestRule.activity.postCas("","","IMAGE","01-06-2020",
            object : FormSignalActivity.VolleyCallBack {
                override fun onSuccess(code : String) {
                    Toast.makeText( mActivityTestRule.activity.applicationContext,
                    code,Toast.LENGTH_LONG).show()
                }
            }
            )
    }

    private fun childAtPosition(
        parentMatcher: Matcher<View>, position: Int
    ): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }
}
*/
package com.example.corona.watch.main


import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.rule.ActivityTestRule
import com.example.corona.watch.R
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.hamcrest.core.IsInstanceOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4ClassRunner::class)
class MainActivityTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun mainActivityTest() {

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        Thread.sleep(7000)

        val appCompatImageView = onView(
            allOf(
                withId(R.id.signal_btn_top),
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
        appCompatImageView.perform(click())

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

        val bottomNavigationItemView = onView(
            allOf(
                withId(R.id.maps), withContentDescription("maps"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.bottom_navigation),
                        0
                    ),
                    4
                ),
                isDisplayed()
            )
        )
        bottomNavigationItemView.perform(click())

        val frameLayout = onView(
            allOf(
                withId(R.id.internationalMap),
                withContentDescription("Showing a Map created with Mapbox. Scroll by dragging two fingers. Zoom by pinching two fingers."),
                childAtPosition(
                    allOf(
                        withId(R.id.international_map_fragment),
                        childAtPosition(
                            withId(R.id.main_frame),
                            0
                        )
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        frameLayout.check(matches(isDisplayed()))

        Thread.sleep(7000)
        val view = onView(
            allOf(
                childAtPosition(
                    allOf(
                        withId(R.id.internationalMap),
                        childAtPosition(
                            withId(R.id.international_map_fragment),
                            0
                        )
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        view.check(matches(isDisplayed()))


        val tabView = onView(
            allOf(
                withContentDescription("الوطن"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.tabs),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        tabView.perform(click())

        Thread.sleep(7000)
        val frameLayout2 = onView(
            allOf(
                withId(R.id.mapNationale),
                withContentDescription("Showing a Map created with Mapbox. Scroll by dragging two fingers. Zoom by pinching two fingers."),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.main_frame),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        frameLayout2.check(matches(isDisplayed()))

        val view2 = onView(
            allOf(
                childAtPosition(
                    allOf(
                        withId(R.id.mapNationale),
                        withContentDescription("Showing a Map created with Mapbox. Scroll by dragging two fingers. Zoom by pinching two fingers."),
                        childAtPosition(
                            IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java),
                            0
                        )
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        view2.check(matches(isDisplayed()))


        val bottomNavigationItemView2 = onView(
            allOf(
                withId(R.id.videos), withContentDescription("videos"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.bottom_navigation),
                        0
                    ),
                    3
                ),
                isDisplayed()
            )
        )
        bottomNavigationItemView2.perform(click())

        val bottomNavigationItemView3 = onView(
            allOf(
                withId(R.id.social_media), withContentDescription("social_media"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.bottom_navigation),
                        0
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        bottomNavigationItemView3.perform(click())

        val recyclerView = onView(
            allOf(
                withId(R.id.youtubeRecycler),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.main_frame),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        recyclerView.check(matches(isDisplayed()))

        val bottomNavigationItemView4 = onView(
            allOf(
                withId(R.id.articles), withContentDescription("articles"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.bottom_navigation),
                        0
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        bottomNavigationItemView4.perform(click())

        val recyclerView2 = onView(
            allOf(
                withId(R.id.recycler_articles_view),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.main_frame),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        recyclerView2.check(matches(isDisplayed()))
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

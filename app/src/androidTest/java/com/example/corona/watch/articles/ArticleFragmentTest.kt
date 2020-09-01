package com.example.corona.watch.articles

import android.util.Log
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread
import androidx.test.rule.ActivityTestRule
import com.example.corona.watch.R
import com.example.corona.watch.main.MainActivity
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ArticleFragmentTest {
    @Rule
    @JvmField
    var mActivityTestRule : ActivityTestRule<MainActivity>? = ActivityTestRule(MainActivity::class.java)
    lateinit var fragment: ArticleFragment
    lateinit var details_fragment : DetailsArtFragment
    lateinit var comment_fragment : CommentFragment
    @Before
    fun setUp()  {
        onView(withId(R.id.articles)).perform(ViewActions.click())
        fragment = mActivityTestRule!!.activity
            .supportFragmentManager.findFragmentById(R.id.main_frame) as ArticleFragment
        Log.d("beforeThread","")
    }

    @Test
    fun articlesTest() {
        fragment.urlartcile="https://my-json-server.typicode.com/klugBoy/CoronaWatch_Articles/articles"
        fragment.jsonParse(null)
        while(fragment.dataarticle.isEmpty()){
            Thread.sleep(3000)
        }
        runOnUiThread { fragment.loadArticles() }
        onView(RecyclerViewMatcher(R.id.recycler_articles_view).atPositionOnView(0, R.id.dateArt)).check(
                    matches(withText("12/12/2012")))
        onView(RecyclerViewMatcher(R.id.recycler_articles_view).atPositionOnView(0, R.id.contenuArt)).check(
                    matches(withText("I would like to make a description here")))
        onView(RecyclerViewMatcher(R.id.recycler_articles_view).atPositionOnView(0, R.id.titreArt)).check(
                    matches(withText("COVID-19")))
    }

    @Test
    fun articleDetailsTest(){
        fragment.urlartcile="https://my-json-server.typicode.com/klugBoy/CoronaWatch_Articles/articles"
        fragment.jsonParse(null
        )
        while(fragment.dataarticle.isEmpty()){
            Thread.sleep(3000)
        }
        runOnUiThread { fragment.loadArticles() }
        onView(RecyclerViewMatcher(R.id.recycler_articles_view).atPositionOnView(0, R.id.readMore)).perform(ViewActions.click())
        details_fragment = mActivityTestRule!!.activity
            .supportFragmentManager.findFragmentById(R.id.main_frame) as DetailsArtFragment
        details_fragment.urlArt = "https://my-json-server.typicode.com/klugBoy/CoronaWatch_Articles/article01"
        details_fragment.jsonParse(
            null
        )
        while(details_fragment.article.title == null && details_fragment.article.contenu==null){
            Thread.sleep(3000)
        }
        runOnUiThread { details_fragment.initDetails(details_fragment.article) }

        onView(withId(R.id.titreText)).check(matches(withText("COVID-19")))
        onView(withId(R.id.dateText)).check(matches(withText("12/12/2012")))
        onView(withId(R.id.redText)).check(matches((withText("Islam"))))

    }

    @Test
    fun commentsTest(){
        fragment.urlartcile="https://my-json-server.typicode.com/klugBoy/CoronaWatch_Articles/articles"
        fragment.jsonParse(null
        )
        while(fragment.dataarticle.isEmpty()){
            Thread.sleep(3000)
        }
        runOnUiThread { fragment.loadArticles() }
        onView(RecyclerViewMatcher(R.id.recycler_articles_view).atPositionOnView(0, R.id.readMore)).perform(ViewActions.click())
        details_fragment = mActivityTestRule!!.activity
            .supportFragmentManager.findFragmentById(R.id.main_frame) as DetailsArtFragment
        details_fragment.urlArt = "https://my-json-server.typicode.com/klugBoy/CoronaWatch_Articles/article01"
        details_fragment.jsonParse(
            null
        )
        while(details_fragment.article.title == null && details_fragment.article.contenu==null){
            Thread.sleep(3000)
        }
        runOnUiThread { details_fragment.initDetails(details_fragment.article) }

        onView(withId(R.id.cmtButton)).perform(ViewActions.click())

        comment_fragment = mActivityTestRule!!.activity
            .supportFragmentManager.findFragmentById(R.id.main_frame) as CommentFragment
        comment_fragment.urlcomment = "https://my-json-server.typicode.com/klugBoy/CoronaWatch_Articles/comment01"
        comment_fragment.jsonParse(
            null
        )
        while(comment_fragment.dataComment.isEmpty()){
            Thread.sleep(3000)
        }
        runOnUiThread { comment_fragment.loadComments() }

        onView(RecyclerViewMatcher(R.id.commentRecycler).atPositionOnView(0, R.id.username)).check(
            matches(withText("mahi")))
        onView(RecyclerViewMatcher(R.id.commentRecycler).atPositionOnView(0, R.id.dateComm)).check(
            matches(withText("12/12/2012 12:34")))
        onView(RecyclerViewMatcher(R.id.commentRecycler).atPositionOnView(0, R.id.commentContent)).check(
            matches(withText("awsome")))

        onView(RecyclerViewMatcher(R.id.commentRecycler).atPositionOnView(1, R.id.username)).check(
            matches(withText("mehdi")))
        onView(RecyclerViewMatcher(R.id.commentRecycler).atPositionOnView(1, R.id.dateComm)).check(
            matches(withText("13/12/2012 12:59")))
        onView(RecyclerViewMatcher(R.id.commentRecycler).atPositionOnView(1, R.id.commentContent)).check(
            matches(withText("انه مقال رائع")))


    }
}
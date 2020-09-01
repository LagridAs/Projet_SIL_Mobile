package com.example.corona.watch.database

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.After
import org.junit.Before

import org.junit.Assert.*
import org.junit.Test

class DatabaseTest {

    private lateinit var appDatabase: AppDatabase
    private lateinit var notificationDao: NotificationDao
    private lateinit var notification_article : Notification
    private lateinit var notification_map : Notification
    @Before
    fun createDb() {
        appDatabase = Room.inMemoryDatabaseBuilder(
            InstrumentationRegistry.getInstrumentation().context,
            AppDatabase::class.java).build()
        notificationDao = appDatabase.NotificationDao()
        notification_article = Notification(title = "new article",
            body = "new article has been validated",
            type = "article",
            seen = false,
            nid = (2..50000).random())
        notification_map = Notification(title = "new map data",
            body = "new map data has been validated",
            type = "map",
            seen = false,
            nid = (2..50000).random())
    }

    @After
    fun closeDb() {
        appDatabase.close()
    }

    @Test
    fun addNotificationTypeArticleTest(){
        notificationDao.insertAll(notification_article)
        val returned_data = notificationDao.getAll()
        assertEquals(1,returned_data.size)
    }

    @Test
    fun addNotificationTypeMapTest(){
        notificationDao.insertAll(notification_map)
        val returned_data = notificationDao.getAll()
        assertEquals(1,returned_data.size)
    }
    @Test
    fun getAllNotificationsTypeArticleTest(){
        notificationDao.insertAll(notification_article)
        val returned_data = notificationDao.getAllArticleNotification()[0]
        assertEquals("new article",returned_data.title)
        assertEquals("new article has been validated",returned_data.body)
        assertEquals("article",returned_data.type)
        assertEquals(false,returned_data.seen)
    }

    @Test
    fun getAllNotificationsTypeMapTest(){
        notificationDao.insertAll(notification_map)
        val returned_data = notificationDao.getMapNotification()[0]
        assertEquals("new map data",returned_data.title)
        assertEquals("new map data has been validated",returned_data.body)
        assertEquals("map",returned_data.type)
        assertEquals(false,returned_data.seen)
    }

    @Test
    fun getNotSeenNotificationsTypeArticleTest(){
        notificationDao.insertAll(notification_article)
        val returned_data = notificationDao.getAllArticleNotification()[0]
        assertEquals("new article",returned_data.title)
        assertEquals("new article has been validated",returned_data.body)
        assertEquals("article",returned_data.type)
        assertEquals(false,returned_data.seen)
    }

    @Test
    fun getNotSeenNotificationsTypeMapTest(){
        notificationDao.insertAll(notification_map)
        val returned_data = notificationDao.getNotSeenMapNotification()
        assertEquals("new map data",returned_data.title)
        assertEquals("new map data has been validated",returned_data.body)
        assertEquals("map",returned_data.type)
        assertEquals(false,returned_data.seen)
    }

    @Test
    fun getAllNotificationsTest(){
        notificationDao.insertAll(notification_map,notification_article)
        var returned_data = notificationDao.getAll()
        returned_data = returned_data.sortedBy { it.type }
        assertEquals("article",returned_data[0].type)
        assertEquals("map",returned_data[1].type)
    }

    @Test
    fun deleteAllNotificationsTest() {
        notificationDao.insertAll(notification_map,notification_article)
        notificationDao.deleteAll()
        var returned_data = notificationDao.getAll()
        assertEquals(0,returned_data.size)
    }

    @Test
    fun updateSeenFieldNotificationTest(){
        notificationDao.insertAll(notification_map,notification_article)
        notificationDao.updateSeenNotification(notification_map.nid)
        notificationDao.updateSeenNotification(notification_article.nid)
        var returned_data = notificationDao.getAll()
        assertEquals(true,returned_data[0].seen)
        assertEquals(true,returned_data[1].seen)
    }

    @Test
    fun getSeenNotificationsTypeArticleTest(){
        notificationDao.insertAll(notification_article)
        notificationDao.updateSeenNotification(notification_article.nid)
        val returned_data = notificationDao.getSeenArticleNotification()[0]
        assertEquals("new article",returned_data.title)
        assertEquals("new article has been validated",returned_data.body)
        assertEquals("article",returned_data.type)
        assertEquals(true,returned_data.seen)
    }

    @Test
    fun getSeenNotificationsTypeMapTest(){
        notificationDao.insertAll(notification_map)
        notificationDao.updateSeenNotification(notification_map.nid)
        val returned_data = notificationDao.getSeenMapNotification()[0]
        assertEquals("new map data",returned_data.title)
        assertEquals("new map data has been validated",returned_data.body)
        assertEquals("map",returned_data.type)
        assertEquals(true,returned_data.seen)
    }



}
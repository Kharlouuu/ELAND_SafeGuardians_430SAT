package com.example.coincrate_project

object NotificationStorage {
    val notifications = mutableListOf<NotificationData>()

    fun addNotification(message: String) {
        notifications.add(0, NotificationData(message)) // Add to top
    }

    fun getAllNotifications(): List<NotificationData> = notifications
}
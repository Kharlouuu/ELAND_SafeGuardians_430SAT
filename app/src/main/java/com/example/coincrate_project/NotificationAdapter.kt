package com.example.coincrate_project

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.coincrate_project.NotificationStorage.notifications
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NotificationAdapter(
    private val list: MutableList<NotificationData>) : RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvMessage: TextView = view.findViewById(R.id.tvNotificationMessage)
        val tvTime: TextView = view.findViewById(R.id.tvNotificationTime)
        val btnDelete: ImageView = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notification, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val notification = list[position]
        holder.tvMessage.text = notification.message

        val formatter = SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault())
        val formattedTime = formatter.format(Date(notification.timestamp))
        holder.tvTime.text = formattedTime

        holder.btnDelete.setOnClickListener {
            val context = holder.itemView.context
            AlertDialog.Builder(context).apply {
                setTitle("Delete Notification")
                setMessage("Are you sure you want to delete this notification?")
                setPositiveButton("Yes") { _, _ ->
                    // Remove from both list and storage
                    val removedNotification = list.removeAt(position)
                    NotificationStorage.notifications.remove(removedNotification)
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, list.size)
                }
                setNegativeButton("Cancel", null)
                show()
            }
        }
    }

    override fun getItemCount() = list.size
}
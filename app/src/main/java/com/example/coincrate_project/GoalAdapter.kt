package com.example.coincrate_project

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class GoalAdapter : ListAdapter<GoalEntity, GoalAdapter.GoalViewHolder>(DIFF_CALLBACK) {
    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<GoalEntity>() {
            override fun areItemsTheSame(oldItem: GoalEntity, newItem: GoalEntity) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: GoalEntity, newItem: GoalEntity) =
                oldItem == newItem
        }
    }

    inner class GoalViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tvGoalName)
        val desc: TextView = view.findViewById(R.id.tvGoalDesc)
        val amount: TextView = view.findViewById(R.id.tvGoalAmount)
        val star: ImageView = view.findViewById(R.id.ivStar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoalViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_goal, parent, false)
        return GoalViewHolder(view)
    }

    override fun onBindViewHolder(holder: GoalViewHolder, position: Int) {
        val goal = getItem(position)
        holder.name.text = goal.name
        holder.desc.text = if (goal.isAchieved) "Achieved!" else "Goal"
        holder.amount.text = "₱%.2f".format(goal.amount)
        holder.star.setImageResource(
            if (goal.isAchieved) R.drawable.star_fill else R.drawable.star_icon
        )
    }
}
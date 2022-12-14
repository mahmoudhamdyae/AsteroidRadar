package com.udacity.asteroidradar

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.udacity.asteroidradar.databinding.AsteroidViewItemBinding
import com.udacity.asteroidradar.domain.Asteroid

class AsteroidAdapter(private val onClickListener: OnClickListener) :
    ListAdapter<Asteroid, AsteroidAdapter.AsteroidPropertyViewHolder>(DiffCallback) {


    private var asteroids: List<Asteroid> = emptyList()

    fun setAsteroids(data: List<Asteroid>) {
        this.asteroids = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = AsteroidPropertyViewHolder(AsteroidViewItemBinding.inflate(LayoutInflater.from(parent.context)))

    override fun onBindViewHolder(holder: AsteroidPropertyViewHolder, position: Int) {
        val asteroid = asteroids[position]//getItem(position)
        holder.itemView.setOnClickListener {
            onClickListener.onClick(asteroid)
        }
        holder.bind(asteroid)
    }

    override fun getItemCount() = asteroids.size

    /**
     * The AsteroidPropertyViewHolder constructor takes the binding variable from the associated
     * AsteroidViewItem, which nicely gives it access to the full [Asteroid] information.
     */
    class AsteroidPropertyViewHolder(private val binding: AsteroidViewItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(asteroid: Asteroid) {
            binding.property = asteroid
            binding.executePendingBindings()
        }
    }

    /**
     * Allows the RecyclerView to determine which items have changed when the [List] of [Asteroid]
     * has been updated.
     */
    companion object DiffCallback : DiffUtil.ItemCallback<Asteroid>() {
        override fun areItemsTheSame(oldItem: Asteroid, newItem: Asteroid): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Asteroid, newItem: Asteroid): Boolean {
            return oldItem.id == newItem.id
        }
    }

    /**
     * Custom listener that handles clicks on [RecyclerView] items.  Passes the [Asteroid]
     * associated with the current item to the [onClick] function.
     * @param clickListener lambda that will be called with the current [Asteroid]
     */
    class OnClickListener(val clickListener: (asteroid: Asteroid) -> Unit) {
        fun onClick(asteroid:Asteroid) = clickListener(asteroid)
    }
}
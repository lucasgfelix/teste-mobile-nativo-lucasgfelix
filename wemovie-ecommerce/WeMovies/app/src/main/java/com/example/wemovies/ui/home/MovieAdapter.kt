package com.example.wemovies.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.wemovies.R
import com.example.wemovies.databinding.ItemMovieBinding
import com.example.wemovies.model.Movie

class MovieAdapter(
    private val onCartButtonClick: (Int, Boolean) -> Unit
) : ListAdapter<Movie, MovieAdapter.MovieViewHolder>(DiffCallback) {

    private var cartCounts: Map<Int, Int> = emptyMap()

    fun updateCartCounts(newCartCounts: Map<Int, Int>) {
        cartCounts = newCartCounts
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = ItemMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class MovieViewHolder(private val binding: ItemMovieBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(movie: Movie) {
            binding.title.text = movie.title
            binding.price.text = "R$${movie.price}"

            // Carregar imagem com Glide
            Glide.with(binding.movieImage.context)
                .load(movie.image)
                .into(binding.movieImage)

            val count = cartCounts[movie.id] ?: 0
            binding.cartCounter.text = "$count"
            val isInCart = count > 0
            updateCartButtonColor(isInCart)

            // Configura o clique do botão
            binding.addToCartButton.setOnClickListener {
                onCartButtonClick(movie.id, !isInCart)
            }

        }

        private fun updateCartButtonColor(isInCart: Boolean) {
            val color = if (isInCart) {
                binding.root.context.getColor(R.color.color_button_green)
            } else {
                binding.root.context.getColor(R.color.color_button)
            }
            binding.addToCartButton.setBackgroundColor(color)
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Movie>() {
        override fun areItemsTheSame(oldItem: Movie, newItem: Movie) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Movie, newItem: Movie) = oldItem == newItem
    }
}
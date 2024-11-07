package com.example.wemovies.ui.card

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.wemovies.R
import com.example.wemovies.databinding.ItemCartBinding
import com.example.wemovies.model.Movie
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CartAdapter(
    private val onDuplicateClick: (Int) -> Unit, // Função para duplicar
    private val onDeleteClick: (Int) -> Unit, // Função para excluir
    private val onQuantityChange: (Int, Int) -> Unit // Função para atualizar a quantidade
) : ListAdapter<Movie, CartAdapter.CartViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CartViewHolder(private val binding: ItemCartBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(movie: Movie) {

            binding.title.text = movie.title


            val subtotal = movie.price * movie.quantity


            val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())


            val formattedSubtotal =
                String.format(itemView.context.getString(R.string.item_subtotal), subtotal)
            binding.price.text = formattedSubtotal


            Glide.with(binding.movieImage.context)
                .load(movie.image)
                .into(binding.movieImage)

            binding.quantity.text = movie.quantity.toString()


            binding.btnMenos.setOnClickListener {
                if (movie.quantity > 1) {
                    onQuantityChange(movie.id, movie.quantity - 1)
                }
            }


            binding.btnAdd.setOnClickListener {
                onQuantityChange(movie.id, movie.quantity + 1)
            }


            binding.btnDelete.setOnClickListener {
                onDeleteClick(movie.id)
            }


            binding.data.text = "Adicionado em $currentDate"
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Movie>() {
        override fun areItemsTheSame(oldItem: Movie, newItem: Movie) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Movie, newItem: Movie) = oldItem == newItem
    }
}


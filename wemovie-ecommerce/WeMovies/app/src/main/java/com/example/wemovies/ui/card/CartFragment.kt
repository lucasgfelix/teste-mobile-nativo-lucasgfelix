package com.example.wemovies.ui.card

import com.example.wemovies.ui.home.HomeViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wemovies.R
import com.example.wemovies.databinding.FragmentCartBinding
import com.example.wemovies.model.Movie
import java.text.SimpleDateFormat
import java.util.*

class CartFragment : Fragment() {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var cartAdapter: CartAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        homeViewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]

        setupRecyclerView()

        // Observar mudanças no carrinho e atualizar a UI
        homeViewModel.cartCounts.observe(viewLifecycleOwner) { cartCounts ->
            val cartMovies = getCartMovies(cartCounts, homeViewModel.movies.value ?: emptyList())
            cartAdapter.submitList(cartMovies)
            updateTotalPrice(cartMovies)
            toggleEmptyState(cartMovies.isEmpty())
        }

        // Observar o total do carrinho para atualizar o menu
        homeViewModel.cartTotalCount.observe(viewLifecycleOwner) { totalCount ->
            updateCartMenuItemCount(totalCount)
        }

        // Configuração do botão para voltar à Home quando no estado vazio
        binding.emptyStateButton.setOnClickListener {
            activity?.findNavController(R.id.nav_host_fragment_activity_main)
                ?.navigate(R.id.navigation_home)
        }

        // Configuração do botão para finalizar pedido
        binding.btnFinish.setOnClickListener {
            completePurchase() // Chamamos o método que limpa o carrinho e exibe o estado de sucesso
        }

        // Configuração do botão para voltar à Home no estado de sucesso
        binding.successStateButton.setOnClickListener {
            activity?.findNavController(R.id.nav_host_fragment_activity_main)
                ?.navigate(R.id.navigation_home)
        }

        return binding.root
    }

    private fun completePurchase() {
        homeViewModel.clearCart()
        showSuccessState()
    }

    private fun updateTotalPrice(cartMovies: List<Movie>) {
        val total = cartMovies.sumOf { it.price * it.quantity }
        val formattedTotal = String.format("R$ %.2f", total)
        binding.totalPrice.text = formattedTotal
    }

    private fun setupRecyclerView() {
        cartAdapter = CartAdapter(
            onDuplicateClick = { movieId ->
                homeViewModel.duplicateItem(movieId)
            },
            onDeleteClick = { movieId ->
                homeViewModel.removeFromCart(movieId)
            },
            onQuantityChange = { movieId, quantity ->
                homeViewModel.updateQuantity(movieId, quantity)
            }
        )
        binding.recyclerViewCart.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = cartAdapter
        }
    }

    private fun getCartMovies(cartCounts: Map<Int, Int>, movies: List<Movie>): List<Movie> {
        val cartMovies = mutableListOf<Movie>()
        movies.forEach { movie ->
            val count = cartCounts[movie.id] ?: 0
            if (count > 0) {
                cartMovies.add(movie.copy(quantity = count))
            }
        }
        return cartMovies
    }

    private fun updateCartMenuItemCount(totalCount: Int) {
        val navView =
            activity?.findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(
                R.id.nav_view
            )
        val cartMenuItem = navView?.menu?.findItem(R.id.navigation_dashboard)
        cartMenuItem?.title = getString(R.string.title_carrinho, totalCount)
    }

    private fun toggleEmptyState(isCartEmpty: Boolean) {
        if (isCartEmpty) {
            binding.cartContentLayout.visibility = View.GONE
            binding.emptyStateLayout.visibility = View.VISIBLE
        } else {
            binding.cartContentLayout.visibility = View.VISIBLE
            binding.emptyStateLayout.visibility = View.GONE
        }
    }

    private fun showSuccessState() {

        homeViewModel.clearCart()


        val currentDateAndTime = getCurrentDateAndTime()


        binding.successDate.text = getString(R.string.success_date, currentDateAndTime)


        binding.cartContentLayout.visibility = View.GONE
        binding.emptyStateLayout.visibility = View.GONE
        binding.successStateLayout.visibility = View.VISIBLE
    }

    private fun getCurrentDateAndTime(): String {
        val currentDate = Date()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy 'às' HH:mm", Locale.getDefault())
        return dateFormat.format(currentDate)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
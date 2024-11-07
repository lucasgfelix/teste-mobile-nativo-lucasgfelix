package com.example.wemovies.ui.home

import android.os.Bundle
import android.view.*
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wemovies.databinding.FragmentHomeBinding
import com.example.wemovies.R

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var movieAdapter: MovieAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeViewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        setupRecyclerView()
        setupObservers()

        homeViewModel.loadMovies() // Carregar filmes inicialmente

        binding.reloadButton.setOnClickListener {
            homeViewModel.loadMovies() // Recarregar filmes ao clicar no botão
        }

        return binding.root
    }

    private fun setupRecyclerView() {
        movieAdapter = MovieAdapter { movieId, addToCart ->
            homeViewModel.updateCart(movieId, addToCart)
        }
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = movieAdapter
        }
    }

    private fun setupObservers(): RelativeLayout {
        // Observar o estado de carregamento (loading)
        homeViewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.recyclerView.visibility = View.GONE
                binding.emptyStateLayoutHome.visibility = View.GONE
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }
        }
        // Observar o estado de erro
        homeViewModel.error.observe(viewLifecycleOwner) { hasError ->
            if (hasError) {
                binding.recyclerView.visibility = View.GONE
                binding.emptyStateLayoutHome.visibility = View.VISIBLE
                binding.emptyStateTextViewHome.text = getString(R.string.text_error)
            }
        }

        // Observar a lista de filmes
        homeViewModel.movies.observe(viewLifecycleOwner) { movies ->
            if (movies.isEmpty()) {
                toggleEmptyState(true)
            } else {
                toggleEmptyState(false)
                movieAdapter.submitList(movies)
            }
        }

        // Observar o contador global para atualizar o menu
        homeViewModel.cartTotalCount.observe(viewLifecycleOwner) { totalCartCount ->
            updateCartMenuItemCount(totalCartCount)
        }

        // Observar os contadores individuais e atualizar o adaptador
        homeViewModel.cartCounts.observe(viewLifecycleOwner) { cartCounts ->
            movieAdapter.updateCartCounts(cartCounts)
        }

        // Observar a lista de filmes
        homeViewModel.movies.observe(viewLifecycleOwner) { movies ->
            movieAdapter.submitList(movies)
        }

        return binding.root
    }

    private fun toggleEmptyState(isEmpty: Boolean) {
        if (isEmpty) {
            binding.recyclerView.visibility = View.GONE
            binding.emptyStateLayoutHome.visibility = View.VISIBLE
        } else {
            binding.recyclerView.visibility = View.VISIBLE
            binding.emptyStateLayoutHome.visibility = View.GONE
        }
    }

    private fun updateCartMenuItemCount(totalCount: Int) {
        val navView =
            activity?.findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(
                R.id.nav_view
            )
        val cartMenuItem = navView?.menu?.findItem(R.id.navigation_dashboard)
        cartMenuItem?.title = getString(R.string.title_carrinho, totalCount)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


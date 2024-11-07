package com.example.wemovies.ui.home

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.wemovies.model.Movie
import com.example.wemovies.ui.infra.MovieApiService
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val sharedPreferences =
        application.getSharedPreferences("CART_PREFS", Context.MODE_PRIVATE)

    private val _movies = MutableLiveData<List<Movie>>()
    val movies: LiveData<List<Movie>> get() = _movies

    private val _cartCounts = MutableLiveData<Map<Int, Int>>(loadCartFromPreferences())
    val cartCounts: LiveData<Map<Int, Int>> get() = _cartCounts

    private val _cartTotalCount = MutableLiveData(0)
    val cartTotalCount: LiveData<Int> get() = _cartTotalCount

    private val movieApiService = MovieApiService.create()

    private val _loading = MutableLiveData<Boolean>(false) // LiveData para controle do loading
    val loading: LiveData<Boolean> get() = _loading


    // Função para carregar filmes da API
    fun loadMovies() {
        _loading.value = true
        viewModelScope.launch {
            try {
                val response = movieApiService.getMovies()
                if (response.isSuccessful) {
                    _movies.value = response.body()?.products
                    Log.d("HomeViewModel", "Movies loaded: ${_movies.value?.size}")
                } else {
                    Log.e("HomeViewModel", "Erro ao carregar filmes: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Erro na requisição: ${e.message}")
            } finally {
                _loading.value = false
            }
        }
    }

    // Atualiza quantidade de itens no carrinho
    fun updateQuantity(movieId: Int, quantity: Int) {
        val currentCart = (_cartCounts.value ?: mutableMapOf()).toMutableMap()
        if (quantity > 0) {
            currentCart[movieId] = quantity
        } else {
            currentCart.remove(movieId)
        }
        _cartCounts.value = currentCart
        saveCartToPreferences(currentCart)
        updateTotalCartCount()
    }

    // Auxiliares para manipulação do carrinho
    fun duplicateItem(movieId: Int) {
        updateQuantity(movieId, (_cartCounts.value?.get(movieId) ?: 0) + 1)
    }

    fun removeFromCart(movieId: Int) {
        val currentCart = (_cartCounts.value ?: mutableMapOf()).toMutableMap()
        currentCart.remove(movieId)
        _cartCounts.value = currentCart
        saveCartToPreferences(currentCart)
        updateTotalCartCount()
    }

    fun updateCart(movieId: Int, addToCart: Boolean) {
        val currentCart = (_cartCounts.value ?: mutableMapOf()).toMutableMap()
        val currentCount = currentCart[movieId] ?: 0
        val newCount = if (addToCart) currentCount + 1 else currentCount - 1

        if (newCount > 0) {
            currentCart[movieId] = newCount
        } else {
            currentCart[movieId] = 0
            currentCart.remove(movieId)
        }

        _cartCounts.value = currentCart.filter { it.value > 0 }
        saveCartToPreferences(currentCart)
        updateTotalCartCount()
    }

    // Armazenamento e recuperação de dados do carrinho
    private fun saveCartToPreferences(cartCounts: Map<Int, Int>) {
        val editor = sharedPreferences.edit()
        cartCounts.forEach { (movieId, count) ->
            editor.putInt(movieId.toString(), count)
        }
        editor.apply()
    }

    private fun loadCartFromPreferences(): Map<Int, Int> {
        val cartData = mutableMapOf<Int, Int>()
        sharedPreferences.all.forEach { (key, value) ->
            if (value is Int) {
                cartData[key.toInt()] = value
            }
        }
        return cartData
    }

    private fun updateTotalCartCount() {
        _cartTotalCount.value = _cartCounts.value?.values?.sum() ?: 0
    }


    fun clearCart() {
        _cartCounts.value = emptyMap()
        _cartTotalCount.value = 0
        sharedPreferences.edit().clear().apply()
    }
}
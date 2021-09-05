package com.example.gifapp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gifapp.model.Gif
import com.example.gifapp.repository.Repository
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

sealed class GifState {
    object LoadState : GifState()
    class SuccessState(val gif: Gif?, val hasPrev: Boolean) : GifState()
    class ErrorState<T>(val msg: T) : GifState()
}

class GifViewModel(private val repository: Repository) : ViewModel() {

    val state: LiveData<GifState>
        get() = _state

    private var lastWasFail = false
    private var currGif: Gif? = null
    private val prevGifs = mutableListOf<Gif>()
    private var index = -1
    private val _state = MutableLiveData<GifState>()

    private fun loadGif() {
        _state.postValue(GifState.LoadState)
        val response = repository.getGif()

        response.enqueue(object : Callback<Gif> {
            override fun onResponse(call: Call<Gif>, response: Response<Gif>) {
                Log.d("Retrofit", response.toString())
                lastWasFail = false
                index++
                currGif = response.body()!!
                currGif?.let { prevGifs.add(it) }
                setNormalState()
            }

            override fun onFailure(call: Call<Gif>, t: Throwable) {
                Log.d("Retrofit", "Sobaka ypala")
                lastWasFail = true
                _state.postValue(GifState.ErrorState(t.message))
                currGif = null
            }
        })
    }

    fun initialize() {
        if (currGif != null)
            _state.postValue(GifState.SuccessState(currGif, hasPrev()))
        else
            nextGif()
    }

    fun nextGif() {
        if (index == prevGifs.size - 1) {
            loadGif()
        } else if (index < prevGifs.size - 1) {
            currGif = prevGifs[++index]
            setNormalState()
        }
    }

    fun prevGif() {
        if (hasPrev()) {
            val i = if(lastWasFail) index else --index
            currGif = prevGifs[i]
            lastWasFail = false
            setNormalState()
        }
    }

    fun refresh() {
        if (currGif == null)
            loadGif()
        else
            setNormalState()
    }

    private fun hasPrev() = index > 0 && prevGifs.isNotEmpty()

    private fun setNormalState() = when {
        index >= 0 -> _state.postValue(GifState.SuccessState(currGif, hasPrev()))
        else -> _state.postValue(GifState.ErrorState("Какая-то проблема"))
    }

}
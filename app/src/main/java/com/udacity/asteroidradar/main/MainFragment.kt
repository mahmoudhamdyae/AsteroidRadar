package com.udacity.asteroidradar.main

import android.os.Bundle
import android.view.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.udacity.asteroidradar.AsteroidAdapter
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.databinding.FragmentMainBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@Suppress("DEPRECATION")
class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding
    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater)
        binding.lifecycleOwner = this
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        binding.viewModel = viewModel

        val asteroidAdapter = AsteroidAdapter {
            this.findNavController().navigate(MainFragmentDirections.actionShowDetail(it))
        }

        binding.asteroidRecycler.adapter = asteroidAdapter


        viewModel.state.onEach { asteroidState ->
            asteroidAdapter.setAsteroids(asteroidState.asteroids)
        }.launchIn(lifecycleScope)

        viewModel.loadingState.onEach { isLoading ->
            binding.statusLoadingWheel.isVisible = isLoading
        }.launchIn(lifecycleScope)

        setHasOptionsMenu(true)

        return binding.root
    }

    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    @Deprecated("Deprecated in Java", ReplaceWith("true"))
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        viewModel.updateFilter(
            when (item.itemId) {
                R.id.show_all_menu -> ApiFilter.SHOW_WEEK
                R.id.show_rent_menu -> ApiFilter.SHOW_TODAY
                else -> ApiFilter.SHOW_SAVED
            }
        )
        return true
    }
}
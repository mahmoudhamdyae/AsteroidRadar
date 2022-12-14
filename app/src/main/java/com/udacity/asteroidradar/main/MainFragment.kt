package com.udacity.asteroidradar.main

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.udacity.asteroidradar.AsteroidAdapter
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.databinding.FragmentMainBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@Suppress("DEPRECATION")
class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding
    private lateinit var viewModel: MainViewModel

    private lateinit var asteroidAdapter: AsteroidAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater)
        binding.lifecycleOwner = this
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        binding.viewModel = viewModel

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        asteroidAdapter = AsteroidAdapter { asteroid ->
            this.findNavController().navigate(MainFragmentDirections.actionShowDetail(asteroid))
        }

        binding.asteroidRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.asteroidRecycler.adapter = asteroidAdapter


        viewModel.state.onEach { asteroidState ->
            asteroidAdapter.setAsteroids(asteroidState.asteroids)
        }.launchIn(lifecycleScope)

        viewModel.loadingState.onEach { isLoading ->
            binding.statusLoadingWheel.isVisible = isLoading
        }.launchIn(lifecycleScope)
    }

//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        val binding = FragmentMainBinding.inflate(inflater)
//        binding.lifecycleOwner = this
//        binding.viewModel = viewModel
//
//        binding.asteroidRecycler.layoutManager = GridLayoutManager(context,1)
////        val adapter = AsteroidAdapter(AsteroidAdapter.OnClickListener {
////            findNavController().navigate(MainFragmentDirections.actionShowDetail(it))
////        })
//        val adapter = AsteroidAdapter { asteroid ->
//            this.findNavController().navigate(MainFragmentDirections.actionShowDetail(asteroid))
//        }
//        binding.asteroidRecycler.adapter = adapter
//
////        viewModel.errorMessage.observe(viewLifecycleOwner) {
////            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
////        }
//
//        viewModel.state.onEach { asteroidState ->
//            adapter.setAsteroids(asteroidState.asteroids)
//        }.launchIn(lifecycleScope)
//
//        setHasOptionsMenu(true)
//
//        return binding.root
//    }

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
package com.haroncode.gemini.sample.ui

import android.os.Bundle
import android.view.View
import com.haroncode.gemini.android.StoreViewConnector
import com.haroncode.gemini.sample.R
import com.haroncode.gemini.sample.base.PublisherFragment
import com.haroncode.gemini.sample.databinding.FragmentCounterBinding
import com.haroncode.gemini.sample.presentation.justreducer.CounterConnectionFactory
import com.haroncode.gemini.sample.presentation.justreducer.CounterStore.Action
import com.haroncode.gemini.sample.presentation.justreducer.CounterStore.State
import javax.inject.Inject

class CounterFragment : PublisherFragment<Action, State>(R.layout.fragment_counter) {

    @Inject
    lateinit var counterFactory: CounterConnectionFactory

    private var _binding: FragmentCounterBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        StoreViewConnector.withFactory(counterFactory)
            .connect(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentCounterBinding.bind(view)
        binding.increaseButton.setOnClickListener { postAction(Action.Increment) }
    }

    override fun onViewStateChanged(viewState: State) {
        binding.counterTextView.text = getString(R.string.fragment_counter_amount_format, viewState.count.toString())
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        fun newInstance() = CounterFragment()
    }
}

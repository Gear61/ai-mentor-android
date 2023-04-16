package com.taro.aimentor.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.transition.TransitionInflater
import com.taro.aimentor.R
import com.taro.aimentor.databinding.OccupationFormBinding
import com.taro.aimentor.persistence.PreferencesManager

class OccupationFormFragment : Fragment() {

    companion object {

        fun getInstance(): OccupationFormFragment {
            return OccupationFormFragment()
        }
    }

    private var _binding: OccupationFormBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(R.transition.fade)
        exitTransition = inflater.inflateTransition(android.R.transition.no_transition)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = OccupationFormBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val preferencesManager = PreferencesManager.getInstance(view.context)
        binding.occupationInput.addTextChangedListener {
            val userOccupation = binding.occupationInput.text.toString().lowercase()
            if (userOccupation.isEmpty()) {
                binding.clearOccupationButton.visibility = View.GONE
            } else {
                binding.clearOccupationButton.visibility = View.VISIBLE
            }
            preferencesManager.occupation = userOccupation.trim()
        }
        binding.clearOccupationButton.setOnClickListener {
            binding.occupationInput.setText("")
        }
    }
}

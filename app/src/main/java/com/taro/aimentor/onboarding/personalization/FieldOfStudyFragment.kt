package com.taro.aimentor.onboarding.personalization

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.transition.TransitionInflater
import com.taro.aimentor.R
import com.taro.aimentor.databinding.FieldOfStudyFormBinding
import com.taro.aimentor.persistence.PreferencesManager

class FieldOfStudyFragment : Fragment() {

    companion object {

        fun getInstance(): FieldOfStudyFragment {
            return FieldOfStudyFragment()
        }
    }

    private var _binding: FieldOfStudyFormBinding? = null
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
        _binding = FieldOfStudyFormBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val preferencesManager = PreferencesManager.getInstance(view.context)
        binding.fieldOfStudyInput.addTextChangedListener {
            val userFieldOfStudy = binding.fieldOfStudyInput.text.toString()
            if (userFieldOfStudy.isEmpty()) {
                binding.clearFieldOfStudy.visibility = View.GONE
            } else {
                binding.clearFieldOfStudy.visibility = View.VISIBLE
            }
            preferencesManager.fieldOfStudy = userFieldOfStudy.trim()
        }
        binding.clearFieldOfStudy.setOnClickListener {
            binding.fieldOfStudyInput.setText("")
        }
    }
}

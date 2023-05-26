package com.taro.aimentor.onboarding.personalization

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.transition.TransitionInflater
import com.taro.aimentor.R
import com.taro.aimentor.databinding.InterviewStatusFormBinding
import com.taro.aimentor.persistence.PreferencesManager
import com.taro.aimentor.views.BetterRadioGroup

class InterviewStatusFragment : Fragment(), BetterRadioGroup.Listener {

    companion object {

        fun getInstance(): InterviewStatusFragment {
            return InterviewStatusFragment()
        }
    }

    private var _binding: InterviewStatusFormBinding? = null
    private val binding get() = _binding!!

    private lateinit var preferencesManager: PreferencesManager

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
        _binding = InterviewStatusFormBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preferencesManager = PreferencesManager.getInstance(view.context)
        val options = resources.getStringArray(R.array.interview_status_options)
        val radioGroup = binding.questionOptions
        radioGroup.setSize(options.size)
        for ((index, option) in options.withIndex()) {
            val radioButton = radioGroup.getRadioButton(index)
            radioButton.text = option
            if (index == 0) {
                radioButton.setCheckedImmediately(checked = true)
            }
        }
        radioGroup.setOptionChosenListener(listener = this)
    }

    override fun onOptionChosen(index: Int) {
        when (index) {
            0 -> preferencesManager.isInterviewing = true
            1 -> preferencesManager.isInterviewing = false
        }
    }
}

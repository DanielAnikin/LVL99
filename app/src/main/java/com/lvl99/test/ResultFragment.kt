package com.lvl99.test

import android.animation.ValueAnimator
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.lvl99.test.databinding.FragmentGameBinding
import com.lvl99.test.databinding.FragmentResultBinding

class ResultFragment : Fragment(R.layout.fragment_result) {

    private lateinit var binding: FragmentResultBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentResultBinding.bind(view)

        val score = arguments?.getInt("score") ?: 0
        val bestScore = arguments?.getInt("best_score") ?: 0

        binding.textYourScore.text = "Your score: $score"
        binding.textBestScore.text = "Best score: $bestScore"
        binding.button.setOnClickListener {
            val fragment = GameFragment()

            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.fragment_container, fragment)
                ?.commit()
        }

    }

}


package com.lvl99.test

import android.animation.ValueAnimator
import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.lvl99.test.databinding.FragmentGameBinding

class GameFragment : Fragment(R.layout.fragment_game) {

    private lateinit var binding: FragmentGameBinding
    private var direction = Direction.DOWN
    private var score = 0
    private val ballSpeed = 10
    private val updateInterval = 16L
    private lateinit var mediaPlayer: MediaPlayer
    private val handler = Handler(Looper.getMainLooper())
    private val updateRunnable = object : Runnable {
        override fun run() {
            updateBallPosition()
            handler.postDelayed(this, updateInterval)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentGameBinding.bind(view)

        mediaPlayer = MediaPlayer.create(context, R.raw.click_sound)

        binding.root.setOnClickListener {
            playClickSound()
            changeDirection()
        }

        binding.root.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                binding.ball.y = binding.root.height / 2f - binding.ball.height / 2f

                handler.post(updateRunnable)

                binding.root.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }

    private fun updateBallPosition() {
        val newY = binding.ball.y + if (direction == Direction.DOWN) ballSpeed else -ballSpeed
        if (newY <= 0 || newY + binding.ball.height >= binding.root.height) {
            gameOver()
        } else {
            binding.ball.y = newY
        }
    }

    private fun changeDirection() {
        direction = if (direction == Direction.DOWN) Direction.UP else Direction.DOWN
        score++
        binding.textScore.text = "Score: $score"
    }

    private fun gameOver() {
        handler.removeCallbacks(updateRunnable)

        val bestScore = getBestScore()
        val isNewRecord = if (score > bestScore) {
            saveBestScore(score)
            true
        } else {
            false
        }

        val bundle = Bundle().apply {
            putInt("score", score)
            putInt("best_score", if (isNewRecord) score else bestScore)
        }

        val resultFragment = ResultFragment().apply {
            arguments = bundle
        }

        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.fragment_container, resultFragment)
            ?.commit()
    }


    private fun getBestScore(): Int {
        val sharedPreferences = activity?.getSharedPreferences("game_prefs", Context.MODE_PRIVATE)
        return sharedPreferences?.getInt("best_score", 0) ?: 0
    }

    private fun saveBestScore(newBest: Int) {
        val sharedPreferences = activity?.getSharedPreferences("game_prefs", Context.MODE_PRIVATE)
        sharedPreferences?.edit()?.putInt("best_score", newBest)?.apply()
    }


    enum class Direction {
        UP, DOWN
    }

    private fun playClickSound() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.seekTo(0)
        } else {
            mediaPlayer.start()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(updateRunnable)
        mediaPlayer.release() // Освобождаем ресурсы, связанные с медиаплеером
    }
}


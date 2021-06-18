package com.yuri.bbomodorotimer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.SeekBar
import com.yuri.bbomodorotimer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private var currentDownTimer:CountDownTimer?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.seekBar.setOnSeekBarChangeListener(object:SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    updateRemainTimes(binding.seekBar.progress * 60 * 1000L)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                currentDownTimer?.cancel()
                currentDownTimer = null
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                currentDownTimer = countDownTimer(binding.seekBar.progress*60*1000L)
                currentDownTimer?.start()
            }

        })
    }

    //kotlin은 java대신 = 해서 생성 가능
    private fun countDownTimer(initialMill:Long):CountDownTimer =
         object:CountDownTimer(initialMill, 1000L) {
             override fun onTick(millisUntilFinished: Long) {
                 updateRemainTimes(millisUntilFinished)
                 updateSeekBar(millisUntilFinished)
             }

             override fun onFinish() {
                 updateRemainTimes(0)
                 updateSeekBar(0)
             }

         }

    fun updateRemainTimes(remainMill:Long) {
        val remainSeconds = remainMill / 1000

        binding.tvMinute.text = "%02d".format(remainSeconds/60)
        binding.tvSecond.text = "%02d".format(remainSeconds%60)
    }

    fun updateSeekBar(remainMill: Long){
        binding.seekBar.progress = (remainMill/1000/60).toInt()
    }
}
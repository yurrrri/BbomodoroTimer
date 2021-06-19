package com.yuri.bbomodorotimer

import android.media.SoundPool
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.SeekBar
import com.yuri.bbomodorotimer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private var currentDownTimer:CountDownTimer?=null
    private val soundPool = SoundPool.Builder().build()
    private var tickingSoundId:Int?=null
    private var bellSoundId:Int?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initSounds() //사운드를 가져오는 초기에 가져오는 과정

        binding.seekBar.setOnSeekBarChangeListener(object:SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    updateRemainTimes(progress * 60 * 1000L)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) { //타이머 초기화
                stopCountDown()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) { //타이머 시작
                if (binding.seekBar.progress==0) {
                    stopCountDown()
                } else{
                    startCountDown()
                }
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
                 completeCountDown()
             }
         }

    fun updateRemainTimes(remainMill:Long) {
        val remainSeconds = remainMill / 1000

        binding.tvMinute.text = "%02d'".format(remainSeconds/60)
        binding.tvSecond.text = "%02d".format(remainSeconds%60)
    }

    fun updateSeekBar(remainMill: Long){
        binding.seekBar.progress = (remainMill/1000/60).toInt()
    }

    private fun initSounds(){ //소리 로딩 초기화
        tickingSoundId = soundPool.load(this, R.raw.timer_ticking, 1)
        bellSoundId = soundPool.load(this, R.raw.timer_bell, 1)
    }

    private fun startCountDown(){
        currentDownTimer = countDownTimer(binding.seekBar.progress*60*1000L)
        currentDownTimer?.start()

        tickingSoundId?.let { soundPool.play(it, 1F, 1F, 0, -1, 1F) }
    }

    private fun stopCountDown(){
        currentDownTimer?.cancel()
        currentDownTimer = null

        soundPool.autoPause()
    }

    private fun completeCountDown(){
        updateRemainTimes(0)
        updateSeekBar(0)

        soundPool.autoPause()
        bellSoundId?.let { soundPool.play(it, 1F, 1F, 0, 0, 1F) }
    }

    override fun onResume() {
        super.onResume()

        soundPool.autoResume() //화면이 보이면 다시 사운드 시작
    }

    override fun onPause() {
        super.onPause()

        soundPool.autoPause() //화면이 안보이면 사운드가 멈춰야함
    }

    override fun onDestroy() {
        super.onDestroy()

        soundPool.release() //앱을 종료했을 때, 기존에 로드되었던 사운드 파일 해제
    }
}
    package com.ee.cigarette

import android.animation.ValueAnimator
import android.app.ProgressDialog.show
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.transition.Visibility
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.HandlerCompat.postDelayed
import androidx.work.*
import com.airbnb.lottie.LottieDrawable
import com.ee.cigarette.databinding.ActivityMainBinding
import java.util.concurrent.TimeUnit
import kotlin.math.min


private lateinit var binding: ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var sheredpreferences:SharedPreferences
    var score:Int = 0
    var open:Boolean = false
    var close:Boolean = false
    var reverseFrame = 0.0f
    var frame = 0.0f
    var isPlay = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.imageView4.visibility = View.INVISIBLE
        binding.imageView5.visibility = View.INVISIBLE

        //skor değerini kaydediyoruz telefona
        sheredpreferences = this.getSharedPreferences("com.ee.cigarette", Context.MODE_PRIVATE)
        val skoredatabase = sheredpreferences.getInt("skor",0)
        //sigara animasyonunu kaldığı yerden devam etmesini sağlıyoruz
        val framedatabase:Float = sheredpreferences.getFloat("framme",0.0f)
        frame = framedatabase

        if(frame >0){
            binding.animationView.resumeAnimation()
            binding.animationView.setMaxProgress(frame)
        }


        //ilk açtığımızda son skor değerini yazıyoruz ekrana
        score = skoredatabase
        if(skoredatabase == 0 ){
            binding.textView.text = "0"
        }else{
            binding.textView.text = "$skoredatabase"
            cigarattePack(skoredatabase)
        }

        workrequeest()

    }

    fun workrequeest(){
        val constraint = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .setRequiresCharging(false)
            .build()

        val work  = PeriodicWorkRequestBuilder<workmanager>(15,TimeUnit.MINUTES)
            .setConstraints(constraint)
            .setInputData(skor_bildirim(score))
            .addTag("worker")
            .build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork("worker",ExistingPeriodicWorkPolicy.KEEP,work)
    }
    //kronometre durdur
    fun StopTimer(view: View){
        var cho = binding.choronometer

        if(isPlay) {
            cho.stop()
            cho.base = SystemClock.elapsedRealtime()
            isPlay = false
        }else{
            Toast.makeText(this, "Stop", Toast.LENGTH_SHORT).show()
        }
    }

    //Kronometre başlat
    fun StartTimer(view: View){
        var cho = binding.choronometer

        if(!isPlay){
            cho.base = SystemClock.elapsedRealtime()
            cho.start()
            isPlay = true

        }else{
            Toast.makeText(this, "play", Toast.LENGTH_SHORT).show()
        }
    }

    fun sifir(view: View){
        AlertDialog.Builder(this).setTitle("Are you sure reset ?").setMessage("It is an irreversible process..").setPositiveButton("yes"){dialog,which->
            sheredpreferences.edit().putInt("skor",0).apply()
            sheredpreferences.edit().putFloat("framme",0f).apply()
            finish()
            startActivity(intent)
            overridePendingTransition(R.anim.slide_left,R.anim.slide_right)
            Toast.makeText(this, "score reset", Toast.LENGTH_SHORT).show()
        }.setNegativeButton("No"){dialog,wdfs->
            Toast.makeText(this, "no changing", Toast.LENGTH_SHORT).show()

        }.show()
    }

//Sigara sayısını arttır
    fun increase(view:View) {
        score++
        sheredpreferences.edit().putInt("skor",score).apply()
        binding.textView.text = "$score"
    cigarattePack(score)

        open = true
        animationLoop()
        if(score % 20 == 0){
            frame = 0F
        }

    }
    fun skor_bildirim(skr:Int): Data {
        val mydata = Data.Builder().putInt("skorbildir",skr).build()
        println("$mydata Main activity")
        return mydata
    }

    //Sigara sayısını azalt
    fun decrease(view: View){
        if(score >0) {
            score--
            sheredpreferences.edit().putInt("skor", score).apply()
            binding.textView.text = "$score"
            cigarattePack(score)

        }else
            Toast.makeText(this, "Do you believe that ? ", Toast.LENGTH_SHORT).show()
        //Belki..??
        close = true
        animationLoop()
    }

    fun animationLoop(){

        if(open){
            binding.animationView.speed = +1.0f
            var temp = 0f
            temp = frame
            frame += 0.05f
            binding.animationView.resumeAnimation()
            binding.animationView.setMinAndMaxProgress(temp,frame)
            open = false
        }else if(close){
            if(frame > 0 ) {
                binding.animationView.speed = -1.0f
                var temp = 0f
                temp = frame
                frame -= 0.05f
                binding.animationView.resumeAnimation()
                binding.animationView.setMinAndMaxProgress(frame,temp)
                close = false
            }
        }
        sheredpreferences.edit().putFloat("framme", frame).apply()
    }

    fun cigarattePack(name:Int){
        if(name < 20){
            binding.imageView4.visibility = View.INVISIBLE
            binding.imageView5.visibility = View.INVISIBLE
        }else if(name in 20..39){
            binding.imageView4.visibility = View.VISIBLE
            binding.imageView5.visibility = View.INVISIBLE
        }else if (name >= 40){
            binding.imageView4.visibility = View.VISIBLE
            binding.imageView5.visibility = View.VISIBLE
        }
    }

}


package com.gigawattstechnology.birdandroid

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import com.gigawattstechnology.birdandroid.ml.BirdModel
import org.tensorflow.lite.support.image.TensorImage
import org.w3c.dom.Text
import java.security.Permission

class MainActivity : AppCompatActivity() {
    lateinit var image:ImageView
    lateinit var result:TextView
    lateinit var viewModel: MainActivityViewModel
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel=ViewModelProvider(this).get()

        image=findViewById(R.id.imageView)
        val loadImage=findViewById<Button>(R.id.load)
        val takeImage=findViewById<Button>(R.id.take)
        result=findViewById<TextView>(R.id.result)

        takeImage.setOnClickListener {
            if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.CAMERA)==PackageManager.PERMISSION_GRANTED){
                val intent=Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent,0)
            }else{
                requestPermissions(arrayOf(android.Manifest.permission.CAMERA),1)
            }
        }

        loadImage.setOnClickListener {
            if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED){
                val intent=Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
                startActivityForResult(intent,2)
            }else{
                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),3)
            }
        }

        viewModel.resultOut.observe(this,{res->
            result.text=res
        })

        viewModel.inputBitmap.observe(this,{bitmap->
            if(bitmap!=null){
                image.setImageBitmap(bitmap)
            }
        })

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode==1&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
            val intent=Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent,0)
        }
        if(requestCode==3&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
            val intent=Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(intent,2)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==0&&data!=null){
            val bitmap= data.extras?.get("data") as Bitmap?
//            image.setImageBitmap(bitmap)
            viewModel.setBitMapImage(bitmap)
        }
        if(requestCode==2&&data!=null){
            val uri=data.data
            //image.setImageURI(uri)
            val bitmap=BitmapFactory.decodeStream(contentResolver.openInputStream(uri!!))
            val newbitmap=bitmap?.copy(Bitmap.Config.ARGB_8888,true)

            //image.setImageBitmap(bitmap)

            viewModel.setBitMapImage(bitmap)

//            val model = BirdModel.newInstance(this)
//            val img = TensorImage.fromBitmap(newbitmap)
//
//            val outputs = model.process(img).probabilityAsCategoryList.apply {
//                sortByDescending { it.score }
//            }
//            val highprobability = outputs[0]
//
//            result.text=highprobability.label

            viewModel.ModelOutput(newbitmap,this)

        }
    }

}
package com.gigawattstechnology.birdandroid

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gigawattstechnology.birdandroid.ml.BirdModel
import org.tensorflow.lite.support.image.TensorImage

class MainActivityViewModel : ViewModel() {

    var resultOut=MutableLiveData<String>()
    var inputBitmap=MutableLiveData<Bitmap?>()

    init {
        resultOut.value="Result"
        inputBitmap.value=null
    }

    fun ModelOutput(bitmap:Bitmap?,context:Context){
            val model = BirdModel.newInstance(context)
            val img = TensorImage.fromBitmap(bitmap)

            val outputs = model.process(img).probabilityAsCategoryList.apply {
                sortByDescending { it.score }
            }
            val highprobability = outputs[0]

            resultOut.value=highprobability.label
    }

    fun setBitMapImage(bitmap: Bitmap?){
        inputBitmap.value=bitmap
    }

}
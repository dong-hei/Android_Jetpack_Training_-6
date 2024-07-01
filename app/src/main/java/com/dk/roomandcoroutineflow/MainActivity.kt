package com.dk.roomandcoroutineflow

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.dk.roomandcoroutineflow.db.TextDb
import com.dk.roomandcoroutineflow.db.TextEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Room + Corutine Flow
 *
 * 1. Flow를 사용하지 않을때,  2. 사용했을때 케이스를 비교할것이다.
 *
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val inputArea = findViewById<EditText>(R.id.textInputArea)
        val insertBtn = findViewById<Button>(R.id.insert)
        val deleteBtn = findViewById<Button>(R.id.deleteData)
        val getAllBtn = findViewById<Button>(R.id.getData)

        val resultArea = findViewById<TextView>(R.id.resultArea)
        val db= TextDb.getDb(this)

        insertBtn.setOnClickListener {

            //메인스레드가 아닌 워커스레드에서 하라는 메인스레드에서 이용하지 않게 하는 코루틴 설정
            CoroutineScope(Dispatchers.IO).launch {
                val text = TextEntity(0, inputArea.text.toString())
                db.textDao().insert(text)
                inputArea.setText("")
            }
        }

//        getAllBtn.setOnClickListener {
//            CoroutineScope(Dispatchers.IO).launch {
//                val resultTxt = db.textDao().getAllData().toString()
//
//                // 데이터를 받아오는 작업은 메인스레드에서 해야 오류가 안난다.
//                withContext(Dispatchers.Main){
//                    resultArea.text = resultTxt
//                }
//            }
//        }

        //2번방법 - Flow를 이용해 바로바로 DB 변경사항이 적용되는것을 확인 할 수 있다.
        CoroutineScope(Dispatchers.IO).launch {
            //getAllDataFlow 데이터 수집을 하겠다
            db.textDao().getAllDataFlow().collect(){
                val resultTxt = it.toString()
                withContext(Dispatchers.Main){
                    resultArea.text= resultTxt
                }
            }
        }

        deleteBtn.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
               db.textDao().deleteAllData()
            }
        }
    }
}
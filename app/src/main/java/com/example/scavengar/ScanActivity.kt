package com.example.scavengar

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.FrameLayout
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import io.github.sceneview.ar.ArSceneView
import io.github.sceneview.math.Position
import io.github.sceneview.node.ModelNode
import kotlinx.coroutines.launch

class ScanActivity : AppCompatActivity() {

    private val modelNode = ModelNode()

    private lateinit var sceneView: ArSceneView

    private lateinit var test : FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan)

        sceneView = findViewById(R.id.sceneView)

        sceneView.addChild(modelNode)

        val v1 = intent
        val v2 = v1.extras

        val glbname = v2?.getString("glbname")


        test = findViewById(R.id.test)

//        test.setOnClickListener {
            lifecycleScope.launch {
                if (glbname != null) {
                    ARView(glbname)
                }
            }
//        }




    }


    suspend fun ARView(glbname : String){

        Toast.makeText(this,"Started", Toast.LENGTH_LONG).show()
        modelNode.loadModelGlb(
            context = this,
            glbFileLocation = "models/${glbname}.glb",
            lifecycle = lifecycle,
            autoAnimate = true,
            centerOrigin = Position(x = -1.0f, y = 1.0f,z = 0.0f),
            onError = { exception -> }
        )

    }
}


package com.example.dyncanvas

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.os.Environment
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.dyncanvas.helpers.sessionHelper
import com.google.ar.core.Anchor
import com.google.ar.core.Config
import com.google.ar.core.HitResult
import com.google.ar.core.Session
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.*
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import java.io.File


/**
 * Activity contatining Sceneform fragment, renders shape on command form
 * the previous screen
 */
class ARCanvasActivity : AppCompatActivity() {
    lateinit var sessionHelper: sessionHelper
    lateinit var fragment: ArFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ar)
        sessionHelper = sessionHelper(this)
        sessionHelper.beforeSessionResume = ::configureSession
        fragment = supportFragmentManager
            .findFragmentById(R.id.sceneform_fragment)
                as ArFragment

        fragment.setOnTapArPlaneListener { hitResult, plane, motionEvent ->

            when (intent.extras?.getInt("order")) {

                1 -> makeSmallCanvas(hitResult)

                2 -> makeLargeCanvas(hitResult)
            }

        }
    }

    @SuppressLint("ResourceType")
    private fun makeSmallCanvas(hitResult: HitResult) {
        val img = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
        val bitmap: Bitmap = BitmapFactory.decodeFile(img.path + "/Camera/20220918_185915.jpg")
        val sampler = Texture.Sampler.builder()
            .setWrapMode(Texture.Sampler.WrapMode.CLAMP_TO_EDGE)
            .build()
        Texture.builder()
            .setSource(bitmap)
            .setSampler(sampler)
            .build()
            .thenAccept { texture ->
                MaterialFactory.makeOpaqueWithTexture(
                    this,
                    texture
                )
                    .thenAccept { material ->
                        addNodeToScene(
                            fragment, hitResult.createAnchor(),
                            ShapeFactory.makeCube(
                                Vector3(0.5f, 0.2f, 0.01f),
                                Vector3(0.0f, 0.15f, 0.0f),
                                material
                            )
                        )

                    }
            }
    }

    private fun makeLargeCanvas(hitResult: HitResult) {
        val img = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
        val bitmap: Bitmap = BitmapFactory.decodeFile(img.path + "/Camera/20220918_185915.jpg")
        val sampler = Texture.Sampler.builder()
            .setWrapMode(Texture.Sampler.WrapMode.CLAMP_TO_EDGE)
            .build()

        Texture.builder()
            .setSampler(sampler)
            .setSource(bitmap)
            .build()
            .thenAccept { texture ->
                MaterialFactory.makeTransparentWithTexture(
                    this,
                    texture
                )
                    .thenAccept { material ->
                        addNodeToScene(
                            fragment, hitResult.createAnchor(),
                            ShapeFactory.makeCube(
                                Vector3(0.025f, 0.4f, 1.0f),
                                Vector3(0f, 0.15f, 0f),
                                material
                            )
                        )
                    }
            }
    }

    // Anchor texture to first node child
    private fun addNodeToScene(fragment: ArFragment, anchor: Anchor, modelObject: ModelRenderable) {
        val anchorNode = AnchorNode(anchor)
        TransformableNode(fragment.transformationSystem).apply {
            renderable = modelObject
            setParent(anchorNode)
            select()
        }
        fragment.arSceneView.scene.addChild(anchorNode)
    }

    fun configureSession(session: Session) {
        session.configure(
            session.config.apply {
                lightEstimationMode = Config.LightEstimationMode.ENVIRONMENTAL_HDR
                planeFindingMode = Config.PlaneFindingMode.VERTICAL
            }
        )
    }
}

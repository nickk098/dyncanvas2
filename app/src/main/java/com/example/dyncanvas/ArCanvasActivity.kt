package com.example.dyncanvas

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
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

                1 -> makeSmallCanvas(hitResult, Color.BLUE)

                2 -> makeLargeCanvas(hitResult, Color.GREEN)
            }

        }
    }

    @SuppressLint("ResourceType")
    private fun makeSmallCanvas(hitResult: HitResult, color: Int) {
        Texture.builder()
            .setSource(this, R.drawable.ic_launcher_foreground)
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
                                Vector3(0.2f, 0.2f, 0.01f),
                                Vector3(0.0f, 0.15f, 0.0f),
                                material
                            )
                        )

                    }
            }
    }

    private fun makeLargeCanvas(hitResult: HitResult, color: Int) {
        MaterialFactory.makeOpaqueWithColor(
            this,
            com.google.ar.sceneform.rendering.Color(color)
        )
            .thenAccept { material ->
                addNodeToScene(
                    fragment, hitResult.createAnchor(),
                    ShapeFactory.makeCube(
                        Vector3(0.4f, 0.4f, 0.01f),
                        Vector3(0.0f, 0.15f, 0.0f),
                        material
                    )
                )

            }
    }

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

package edu.farmingdale.alrajab.dragdropanimation_sc

import android.content.ClipData
import android.content.ClipDescription
import android.graphics.Canvas
import android.graphics.Point
import android.graphics.drawable.AnimationDrawable
import android.os.Build
import android.os.Bundle
import android.view.DragEvent
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import edu.farmingdale.alrajab.dragdropanimation_sc.databinding.ActivityDragAndDropViewsBinding

class DragAndDropViews : AppCompatActivity() {
    lateinit var binding: ActivityDragAndDropViewsBinding
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityDragAndDropViewsBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_drag_and_drop_views)

        //adds drag listeners to imageView holders
        findViewById<ImageView>(R.id.holder01).setOnDragListener(arrowDragListener)
        findViewById<ImageView>(R.id.holder02).setOnDragListener(arrowDragListener)
        findViewById<ImageView>(R.id.holder03).setOnDragListener(arrowDragListener)
        findViewById<ImageView>(R.id.holder04).setOnDragListener(arrowDragListener)
        findViewById<ImageView>(R.id.holder05).setOnDragListener(arrowDragListener)

        //add listeners for arrow buttons to enable drag
        findViewById<Button>(R.id.upmoveBtn).setOnLongClickListener(onLongClickListener)
        findViewById<Button>(R.id.downmoveBtn).setOnLongClickListener(onLongClickListener)
        findViewById<Button>(R.id.backmoveBtn).setOnLongClickListener(onLongClickListener)
        findViewById<Button>(R.id.forwardmoveBtn).setOnLongClickListener(onLongClickListener)

        //sets rocket animation
        val rocketImage: ImageView = findViewById(R.id.rocket)
        rocketImage.setBackgroundResource(R.drawable.animation)
        val rocketAnimation = rocketImage.background as AnimationDrawable
        //button on click to start and stop animation
        findViewById<Button>(R.id.play_animation_button).setOnClickListener {
            if (rocketAnimation.isRunning) {
                rocketAnimation.stop()
            } else {
                rocketAnimation.start()
            }
        }

    }


    private val onLongClickListener = View.OnLongClickListener { view: View ->
        val item = ClipData.Item((view as Button).tag as? CharSequence)
        //dragData has the tag of the view
        val dragData = ClipData(
            (view.tag as? CharSequence),
            arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN), item
        )
        //creates shadow when dragging to show users
        val myShadow = ArrowDragShadowBuilder(view)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            view.startDragAndDrop(dragData, myShadow, null, 0)
        } else {
            view.startDrag(dragData, myShadow, null, 0)
        }

        true // Return true to indicate that the long click event is consumed
    }


    private val arrowDragListener = View.OnDragListener { view, dragEvent ->
        (view as? ImageView)?.let {
            when (dragEvent.action) {
                DragEvent.ACTION_DRAG_STARTED -> {
                    //if event's clip has correct mimeType, continue drag
                    if (dragEvent.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                        view.invalidate()
                        true
                    } else {
                        //else stop drag
                        false
                    }
                }

                DragEvent.ACTION_DRAG_ENTERED -> {
                    // Set a background resource when the arrow is over the ImageView
                    view.setBackgroundResource(R.drawable.highlighted_border)
                    view.invalidate()
                    true
                }

                DragEvent.ACTION_DRAG_EXITED -> {
                    // Set back to the original background resource when the arrow leaves the ImageView
                    view.setBackgroundResource(R.drawable.border)
                    view.invalidate()
                    true
                }

                // No need to handle this for our use case.
                DragEvent.ACTION_DRAG_LOCATION -> {
                    true
                }

                DragEvent.ACTION_DROP -> {
                    // Read color data from the clip data and apply it to the ImageView background.
                    val item: ClipData.Item = dragEvent.clipData.getItemAt(0)
                    //lbl is the tag of the button
                    val lbl = item.text.toString()
                    when (lbl) {
                        "UP" -> view.setImageResource(R.drawable.ic_baseline_arrow_upward_24)
                        "DOWN" -> view.setImageResource(R.drawable.ic_baseline_arrow_downward_24)
                        "BACK" -> view.setImageResource(R.drawable.ic_baseline_arrow_back_24)
                        "FORWARD" -> view.setImageResource(R.drawable.ic_baseline_arrow_forward_24)
                    }
                    view.invalidate()
                    true
                }

                DragEvent.ACTION_DRAG_ENDED -> {
                    // Set back to the original background resource when the drag operation ends
                    view.setBackgroundResource(R.drawable.border)
                    view.invalidate()
                    true
                }

                else -> false
            }
        } ?: false
    }


    private class ArrowDragShadowBuilder(view: View) : View.DragShadowBuilder(view) {
        private val shadow = view.background
        override fun onProvideShadowMetrics(size: Point, touch: Point) {
            val width: Int = view.width
            val height: Int = view.height
            shadow?.setBounds(0, 0, width, height)
            size.set(width, height)
            touch.set(width / 2, height / 2)
        }

        override fun onDrawShadow(canvas: Canvas) {
            shadow?.draw(canvas)
        }
    }
}
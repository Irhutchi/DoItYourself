package ie.wit.doityourself.activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.RadioButton
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import ie.wit.doityourself.R
import ie.wit.doityourself.databinding.ActivityDiyBinding
import ie.wit.doityourself.helpers.showImagePicker
import ie.wit.doityourself.main.MainApp
import ie.wit.doityourself.models.DIYModel
import timber.log.Timber
import timber.log.Timber.i

class DIYActivity : AppCompatActivity() {

    // ActivityDiyBinding augmented class needed to access diff View
    // objects on a particular layout
    private lateinit var binding: ActivityDiyBinding
    var task = DIYModel()
    lateinit var app: MainApp // ref to mainApp object (1)
    private lateinit var imageIntentLauncher : ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var edit = false

        //inflate layout using binding class
        binding = ActivityDiyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // In order to present the toolbar - we must explicitly enable it
        binding.toolbarAdd.title = title
        setSupportActionBar(binding.toolbarAdd)

        registerImagePickerCallback()   // initialise the image picker callback func.

        app = this.application as MainApp    // initialise mainApp (2)
        i("DIY Activity started...")

        if(intent.hasExtra("task_edit")) {
            edit = true
            task = intent.extras?.getParcelable("task_edit")!!
            binding.taskTitle.setText(task.title)
            binding.description.setText(task.description)
            binding.btnAdd.setText(R.string.save_task)
            Picasso.get()
                .load(task.image)
                .into(binding.taskImage)
            if (task.image != Uri.EMPTY) {
                binding.chooseImage.setText(R.string.change_task_image)
            }
        }

        binding.btnAdd.setOnClickListener {
            task.title = binding.taskTitle.text.toString()
            task.description = binding.description.text.toString()

            val rgRating: String = if (binding.rgRating.checkedRadioButtonId == R.id.easyBtn) {
                "Easy"
            } else if(binding.rgRating.checkedRadioButtonId == R.id.hardBtn) {
                "Hard"
            } else "Very Hard"
            task.rating = rgRating
            //task.rating = binding.rgRating.toString()
//            app.diyStore.create(DIYModel(rating = rgRating))
            i("Difficulty Rating $rgRating")

//            val checkedRatingButtonId = binding.rgRating.checkedRadioButtonId
//            val rating = findViewById<RadioButton>(checkedRatingButtonId)
//            task.rating = binding.rgRating.toString()


            if(task.title.isEmpty()) {
                Snackbar
                    .make(it, R.string.enter_diyTask_title, Snackbar.LENGTH_LONG)
                    .show()
            } else {
                if (edit) {
                    app.tasks.update(task.copy())
                } else {
                    app.tasks.create(task.copy()) // use mainApp (3)
                    i("add Button Pressed: $task.title")
                }
                setResult(RESULT_OK)
                finish()
            }
        }

        binding.chooseImage.setOnClickListener {
            i("Select image")
            showImagePicker(imageIntentLauncher)    // trigger the image picker
        }

        binding.deleteTaskbtn.setOnClickListener() {
            i("delete task button pressed")
            app.tasks.delete(task)
            setResult(RESULT_OK)
            finish()
        }

    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_diytask, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_cancel -> { finish() }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun registerImagePickerCallback() {
        imageIntentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { result ->
                when(result.resultCode){
                    RESULT_OK -> {
                        if (result.data != null) {
                            i("Got Result ${result.data!!.data}")
                            // Only recovering uri when the result Code is RESULT_OK
                            task.image = result.data!!.data!!
                            Picasso.get()
                                .load(task.image)
                                .into(binding.taskImage)
                            // when an image is changed, also change the label
                            binding.chooseImage.setText(R.string.change_task_image)
                        }
                    }
                    RESULT_CANCELED -> { } else -> { }
                }
            }
    }

}
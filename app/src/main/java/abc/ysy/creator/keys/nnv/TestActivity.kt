package abc.ysy.creator.keys.nnv

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import son.ysy.creator.annotations.KeyCreator
import son.ysy.creator.keys.R

class TestActivity : AppCompatActivity() {
    @KeyCreator(keys = ["aa"])
    object AA {
        @KeyCreator(keys = ["BB"])
        object BB {

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
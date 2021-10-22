package ie.wit.doityourself.main

/*
   A single instance of this class will be created when the application is launched.
   A reference to this application can be acquired in other activities as needed.
*/

import android.app.Application
import ie.wit.doityourself.models.DIYJSONStore
import ie.wit.doityourself.models.DIYMemStore
import ie.wit.doityourself.models.DIYModel
import ie.wit.doityourself.models.DIYStore
import timber.log.Timber
import timber.log.Timber.i


class MainApp : Application() {

    //val tasks = ArrayList<DIYModel>()
    //val tasks = DIYMemStore()
    //lateinit var diyStore: DIYMemStore
    lateinit var tasks: DIYStore

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
//        diyStore = DIYMemStore()
        tasks = DIYJSONStore(applicationContext)
        i("DIY App started")
//        tasks.create(DIYModel(0, "TODO one...", "About One"))
//        tasks.create(DIYModel(1, "TODO two...", "About Three"))
//        tasks.create(DIYModel(2, "TODO three...", "About Three"))
    }
}

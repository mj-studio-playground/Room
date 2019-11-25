package happy.mjstudio.room

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.room.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.concurrent.thread
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var mDatabase : MyDatabase
    private lateinit var mStudentDao: StudentDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mDatabase = Room.databaseBuilder(this,MyDatabase::class.java,"Database-01")
                .build()

        mStudentDao = mDatabase.getStudentDao()

        mStudentDao.listStudentUpperAge(Int.MIN_VALUE).observe(this, Observer {
            Log.e("TAG",it.toString())
        })

        text.setOnClickListener {
            thread {
                mStudentDao.insertStudent(Student(
                        0, Random.nextInt(),Random.nextInt()
                ))
            }
        }
    }
}

@Database(entities = [Student::class],version = 1,exportSchema = true)
abstract class MyDatabase : RoomDatabase() {
    abstract fun getStudentDao() : StudentDao
}

@Entity(tableName = "Student")
data class Student(
        @PrimaryKey(autoGenerate = true)
        val id : Int = 0,
        val age : Int,
        val grade : Int
)

@Dao
interface StudentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertStudent(vararg student: Student)

    @Update
    fun updateStudent(student: Student)

    @Query("SELECT * FROM Student WHERE age >= :minAge ORDER BY grade DESC LIMIT 10")
    fun listStudentUpperAge(minAge : Int) : LiveData<List<Student>>

}
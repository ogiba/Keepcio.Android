package pl.ogiba.keepcio

import android.app.Activity
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import java.lang.Exception
import java.util.concurrent.Executor

/**
 * Created by robertogiba on 27.03.2018.
 */
public abstract class TaskAdapter<T>: Task<T>() {
    override fun isComplete(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun addOnFailureListener(p0: OnFailureListener): Task<T> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun addOnFailureListener(p0: Executor, p1: OnFailureListener): Task<T> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun addOnFailureListener(p0: Activity, p1: OnFailureListener): Task<T> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getResult(): T {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun <X : Throwable?> getResult(p0: Class<X>): T {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun addOnSuccessListener(p0: OnSuccessListener<in T>): Task<T> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun addOnSuccessListener(p0: Executor, p1: OnSuccessListener<in T>): Task<T> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun addOnSuccessListener(p0: Activity, p1: OnSuccessListener<in T>): Task<T> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isSuccessful(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getException(): Exception? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
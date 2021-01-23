package com.example.rxjava;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.rxjava.models.Task;
import com.example.rxjava.util.DataSource;
import com.jakewharton.rxbinding4.view.RxView;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.functions.Predicate;
import io.reactivex.rxjava3.schedulers.Schedulers;
import kotlin.Unit;

// About RxAndroid : This module adds the minimum classes to RxJava that make writing reactive components in Android applications easy and hassle-free.
// specifically, it provides a Scheduler that schedules on the main thread or any given Looper.

// Main things you need to know about the difference between RxJava and RxAndroid:
// 1) RxAndroid contains reactive components that make using RxJava in Android easier and hassle free. More specifically it provides a Scheduler that
// can schedule tasks on the main thread or any other looper - aka any thread. (It makes threading on Android very simple).
// 2) You could use just the RxAndroid dependency, but the releases of RxAndroid updates are less frequent. To make sure you're up-to-date with the newest
// RxJava components, use the newest RxJava dependency.

// I think the best way to grasp the concept of observables and observers is with an example :
// In the example I will:
    // 1) Create an Observable.
    // 2) Apply an operator to the Observable.
    // 3) Designate what thread to do the work on and what thread to emit the results to.
    // 4) Subscribe an Observer to the Observable and view the results.


                                                        // OPERATORS

// Purpose of an operator is to take a given data set and transform it into an observable data set. Some operators can also manipulate the data objects
// and transform them further.

            // TYPES OF OPERATORS

// Operators who's job is to create Observables : create(), just(), range(), repeat(). fromIterate().
// Operators for filtering and sorting : filter(), distinct, take(), takeList(), skip().
// Transform emitted data into other types : map(), flatMap(), switchMap(), buffer(), concatMap();


// In RxJava, Observables are the source which emits items to the Observers. For Observers to listen to the Observables, they need to subscribe first.
// The instance created after subscribing in RxJava is called Disposable. An Observable is like a speaker that emits the value. It does some work and
// emits some values. An Operator is like a translator which translates/modifies data from one form to another form. An Observer gets those values.

// Observer : Any object that wishes to be notified when the state of another object changes. Observable : Any object whose state may be of interest,
// and in whom another object may register an interest. For Observers to listen to the Observables, they need to subscribe first.

// We use RxJava for multithreading, managing background tasks, and removing callback hells. We can solve so many complex use-cases with the help of
// the RxJava. It enables us to do complex things very simple. It provides us the power.

// Schedulers.io() : Generally used for IO related stuff such as network requests, file system operations, etc. IO Scheduler is backed by a thread-pool.
// AndroidSchedulers.mainThread() : This scheduler is used to bring back the execution to the main thread so that UI updates can be done when required.
// This is usually used with observeOn() as shown below.

// subscribeOn(): This is the method that tells the Observables on which thread they should run.
// observeOn() : This is the method that tells the Observers on which thread they should consume the emitted values by Observable. If we have defined
// subscribeOn() on an observable and not specified observeOn() then the Observer will consume the items on that particular thread only. This method
// tells on which thread all subsequent (all the operators that come after subscribeOn()) operators will execute (until another observeOn is encountered),
// it may appear multiple times in the chain, changing execution thread of different code pieces.

// For This error visit : https://stackoverflow.com/questions/58482498/java-lang-bootstrapmethoderror-exception-from-call-site-2-bootstrap-method-on

public class MainActivity extends AppCompatActivity
{
    private static final String TAG = "MainActivity";

    private TextView textView;

    // What happens to all the observers when they are no longer needed or say when the activity to which observers are observing is destroyed. So
    // CompositeDisposable is something which keeps track of all the observers you are using and then how to clean them up when they are no longer needed.
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.text);

        // There are couples of ways to create the the observables like using these operators : create(), just(), range(), repeat(). fromIterate().

        // usingJust();
        // usingCreate();
        // usingFromIterable();
        // usingRange();

        (findViewById(R.id.button)).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                startActivity(new Intent(MainActivity.this, OperatorsActivity.class));
            }
        });
    }

    private void usingJust()
    {
        final Task task = new Task("Emilia Loves Gunjan", true, 10);

        Observable<Task> taskObservable = Observable
            .just(task)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());

        taskObservable.subscribe(new Observer<Task>()
        {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull Task task)
            {
                Log.d(TAG, "onNext: " + task.getDescription());
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void usingCreate()
    {
        Observable<Task> taskObservable = Observable
            .create(new ObservableOnSubscribe<Task>()
            {
                @Override
                public void subscribe(@NonNull ObservableEmitter<Task> emitter) throws Throwable
                {
                    for(Task task: DataSource.createTasksList())
                    {
                        if(!emitter.isDisposed())
                        {
                            emitter.onNext(task);
                        }
                    }

                    if(!emitter.isDisposed())
                    {
                        emitter.onComplete();
                    }
                }
            })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());

        taskObservable.subscribe(new Observer<Task>()
        {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull Task task)
            {
                Log.d(TAG, "onNext: " + task.getDescription());
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void usingFromIterable()
    {
        // It's seems weired as Observable is a list of Tasks but seems single object.
        // filter() : Every task in the list will pass through this filter() method and if task is complete then method will return true else false. After
        // passing through this method then those tasks will pass to the observers.
        Observable<Task> taskObservable = Observable
            .fromIterable(DataSource.createTasksList())         // fromIterable will take a list of objects and turns them into an observable.
            .subscribeOn(Schedulers.io())       // Tells where the work is going to be done (worker thread or background thread).
            .filter(new Predicate<Task>()       // This whole filter is doing the work on the background thread as it is called after subscribeOn().
            {
                @Override
                public boolean test(Task task) throws Throwable
                {
                    // Log.d(TAG, "onNext: " + Thread.currentThread().getName());

                    // We are freezing but on the background thread. This is where the work is being done, work is done on the background thread.
                    // Our work is not being done in the main thread. Hence Thread.sleep() won't freeze our UI.
                    Thread.sleep(1000);

                    return task.isComplete();
                }
            })
            // Observer is observing the observable on the main thread. Designate what thread you want to post the result to.
            .observeOn(AndroidSchedulers.mainThread());


        // subscribe() will return a void as there is a onSubscribe() method. Our UI does not freeze here. The work is being done in a background
        // thread, and we’re observing on the main thread, where the .subscribe() is working and posting updates on the main thread.
        taskObservable.subscribe(new Observer<Task>()
        {
            @Override
            public void onSubscribe(@NonNull Disposable d)
            {
                // This method will be called as soon as the observer subscribes to an Observable.
                // Disposable 'd' will be added to the list of the disposable (compositeDisposable) which are used in this activity.
                compositeDisposable.add(d);
            }

            @Override
            public void onNext(@NonNull Task task)
            {
                // This method will be called when the observer iterates through the observables. See above (fromIterable(DataSource.createTasksList())),
                // we have passed a list of tasks so this function will be called bunch of times.

                // When you log the name of the thread you will see "main" which means onNext() method is called on the main thread (observer is observing
                // on the main thread).
                Log.d(TAG, "onNext: " + Thread.currentThread().getName());
                Log.d(TAG, "onNext: " + task.getDescription());
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });

        // There are couple of other ways to create observers and subscribe to the observable. One of them is listed above. Another example :

        // subscribe() method here returns a disposable object as there is no onSubscribe() method unlike above example. So in this case we can add it
        // to the list of disposables (compositeDisposable).
        compositeDisposable.add(taskObservable.subscribe(new Consumer<Task>()
        {
            @Override
            public void accept(Task task) throws Throwable {

            }
        }));
    }

    private void usingRange()
    {
        Observable<Task> observable = Observable
            .range(50, 12)
            .subscribeOn(Schedulers.io())
            .map(new Function<Integer, Task>()
            {
                @Override
                public Task apply(Integer integer) throws Throwable
                {
                    Log.d(TAG, "apply: " + Thread.currentThread().getName());

                    return new Task("This is it", false, integer);
                }
            })
            .takeWhile(new Predicate<Task>()
            {
                @Override
                public boolean test(Task task) throws Throwable
                {
                    return task.getPriority()<57;
                }
            })
            .observeOn(AndroidSchedulers.mainThread());

        observable.subscribe(new Observer<Task>()
        {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull Task task)
            {
                Log.d(TAG, "onNext: " + task.getPriority());
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });

        Observable<Integer> observable1 = Observable
            .range(50, 4)
            .subscribeOn(Schedulers.io())
            .repeat(3)
            .observeOn(AndroidSchedulers.mainThread());

        observable1.subscribe(new Observer<Integer>()
        {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull Integer integer)
            {
                Log.d(TAG, "onNext: " + integer);
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    // Make sure to clear disposables when the activity is destroyed.
    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        // clear() will remove all the subscribers, all of the observers without disabling the observable. Remember if you are using the MVVM,
        // then in the ViewModel clear it inside the onCleared() method (override it).
        compositeDisposable.clear();

        // dispose() : same as clear but it will no longer allow anything to subscribe to the observable that was previously being observed.
        // compositeDisposable.dispose();
    }
}
package com.example.rxjava;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.example.rxjava.models.Task;
import com.example.rxjava.util.DataSource;
import com.jakewharton.rxbinding4.view.RxView;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;
import kotlin.Unit;

public class OperatorsActivity extends AppCompatActivity
{
    private static final String TAG = "OperatorsActivity";

    // What happens to all the observers when they are no longer needed or say when the activity to which observers are observing is destroyed. So
    // CompositeDisposable is something which keeps track of all the observers you are using and then how to clean them up when they are no longer needed.
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private androidx.appcompat.widget.SearchView searchView;
    private long timeSinceLastRequest;  // for log printouts only. Not part of logic.
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operators);

        timeSinceLastRequest = System.currentTimeMillis();
        searchView = findViewById(R.id.search_view);
        button = findViewById(R.id.button1);

        // Use cases of some other operators.

        // useBuffer();
        usingDebounce();
        throttleFirst();
    }

    private void useBuffer()
    {
        // Buffer : Periodically gather items from an Observable into bundles and emit the bundles rather than emitting items one at a time.

        // Create an Observable.
        Observable<Task> taskObservable = Observable
            .fromIterable(DataSource.createTasksList())
            .subscribeOn(Schedulers.io());

        taskObservable
            .buffer(2)     // Apply the Buffer() operator (emit in the group of 2).
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Observer<List<Task>>()
            {
                // Subscribe and view the emitted results.
                @Override
                public void onSubscribe(@NotNull Disposable d) {

                }
                @Override
                public void onNext(@NotNull List<Task> tasks)
                {
                    // Log.d(TAG, "onNext: bundle results: -------------------");

                    for(Task task: tasks)
                    {
                        // Log.d(TAG, "onNext: " + task.getDescription());
                    }
                }

                @Override
                public void onError(Throwable e) {
                }
                @Override
                public void onComplete() {
                }
            });

        // detect clicks to a button
        RxView.clicks(findViewById(R.id.button))
            .map(new Function<Unit, Integer>()
            {
                // Convert the detected clicks to an integer. Whenever there is a click, it will return 1 sso that we can have total number of clicks.
                @Override
                public Integer apply(Unit unit) throws Exception
                {
                    return 1;
                }
            })
            .buffer(4, TimeUnit.SECONDS)        // Capture all the clicks during a 4 second interval (basically the sum of all).
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Observer<List<Integer>>() {
                @Override
                public void onSubscribe(@NotNull Disposable d)
                {
                    compositeDisposable.add(d); // add to disposables to you can clear in onDestroy
                }
                @Override
                public void onNext(@NotNull List<Integer> integers)
                {
                    Log.d(TAG, "onNext: You clicked " + integers.size() + " times in 4 seconds!");
                }
                @Override
                public void onError(Throwable e) {

                }
                @Override
                public void onComplete() {

                }
            });
    }

    private void usingDebounce()
    {
        // The Debounce operator filters out items emitted by the source Observable that are rapidly followed by another emitted item.

        // Create the Observable.
        Observable<String> observableQueryText = Observable
            .create(new ObservableOnSubscribe<String>()
            {
                @Override
                public void subscribe(@NotNull final ObservableEmitter<String> emitter) throws Exception
                {
                    // Listen for text input into the SearchView.
                    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
                    {
                        @Override
                        public boolean onQueryTextSubmit(String query)
                        {
                            return false;
                        }

                        @Override
                        public boolean onQueryTextChange(String newText)
                        {
                            if(!emitter.isDisposed())
                            {
                                emitter.onNext(newText);  // Pass the query to the emitter.
                            }

                            return false;
                        }
                    });
                }
            })
            // Apply Debounce() operator to limit requests. Request will be sent to search for the query after 0.5 second we stop typing.
            .debounce(500, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());

        // Subscribe an Observer.
        observableQueryText.subscribe(new Observer<String>()
        {
            @Override
            public void onSubscribe(@NotNull Disposable d)
            {
                compositeDisposable.add(d);
            }

            @Override
            public void onNext(@NotNull String s)
            {
                Log.d(TAG, "onNext: time  since last request: " + (System.currentTimeMillis() - timeSinceLastRequest));
                Log.d(TAG, "onNext: search query: " + s);

                timeSinceLastRequest = System.currentTimeMillis();

                // Method for sending a request to the server.
                sendRequestToServer(s);
            }
            @Override
            public void onError(@NotNull Throwable e) {
            }
            @Override
            public void onComplete() {
            }
        });
    }

    private void throttleFirst()
    {
        // The ThrottleFirst() operator filters out items emitted by the source Observable that are within a time span. The ThrottleFirst() operator
        // is very useful in Android development. For example: If a user is spamming a button, You don't want to register every click. You can use the
        // ThrottleFirst() operator to only register new click events every time interval.

        // Set a click listener to the button with RxBinding Library. They basically attaches or interprets onClick event to views (in this case button)
        // and transform into an observable form (making button click observable) so that we can apply operators to them. Either we have to do everything
        // by our self like we din while using debounce operator (we didn't use the RxView library because RxView has old SearchView i.e. not from the
        // androidX). So there, we created an observable using create() and register onText change event inside that create().
        RxView.clicks(button)
            .throttleFirst(500, TimeUnit.MILLISECONDS) // Throttle the clicks so 500 ms must pass before registering a new click
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Observer<Unit>() {
                @Override
                public void onSubscribe(@NotNull Disposable d)
                {
                    compositeDisposable.add(d);
                }
                @Override
                public void onNext(@NotNull Unit unit)
                {
                    Log.d(TAG, "onNext: time since last clicked: " + (System.currentTimeMillis() - timeSinceLastRequest));

                    sendRequestToServer("Hello");   // Execute some method when a click is registered.
                }
                @Override
                public void onError(@NotNull Throwable e) {
                }
                @Override
                public void onComplete() {
                }
            });
    }

    // Fake method for sending a request to the server.
    private void sendRequestToServer(String query)
    {
        Log.d(TAG, "sendRequestToServer: Clicked button");
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
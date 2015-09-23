package miccab.currencyConverter.dao.impl;

import rx.Observer;

/**
 * Created by michal on 23.09.15.
 */
public abstract class BaseCommand<T> implements Runnable {

    protected final Observer<T> observer;

    public BaseCommand(Observer<T> observer) {
        this.observer = observer;
    }

    @Override
    public void run() {
        try {
            doRun();
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(e);
        }
    }

    protected abstract void doRun();
}

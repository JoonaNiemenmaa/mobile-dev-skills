package com.example.emailclient;

interface NetworkCallback<T> {
    void onComplete(T result);
}

package com.example.pharmacie;

public class OdooResponse<T> {
    public String jsonrpc;
    public Object id;
    public T result;
}
